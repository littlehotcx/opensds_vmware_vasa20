/*
 * // Copyright 2019 The OpenSDS Authors.
 * //
 * // Licensed under the Apache License, Version 2.0 (the "License"); you may
 * // not use this file except in compliance with the License. You may obtain
 * // a copy of the License at
 * //
 * //     http://www.apache.org/licenses/LICENSE-2.0
 * //
 * // Unless required by applicable law or agreed to in writing, software
 * // distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * // WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * // License for the specific language governing permissions and limitations
 * // under the License.
 *
 */

package org.opensds.vasa.vasa.runnable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.opensds.vasa.base.common.VasaSrcTypeConstant;
import org.opensds.vasa.common.MagicNumber;
import org.opensds.vasa.domain.model.StorageModel;
import org.opensds.vasa.domain.model.VVolModel;
import org.opensds.vasa.domain.model.bean.S2DLunCopyBean;
import org.opensds.vasa.domain.model.bean.S2DVvolBind;
import org.opensds.platform.common.SDKErrorCode;
import org.opensds.platform.common.SDKResult;
import org.opensds.platform.common.exception.SDKException;
import org.opensds.platform.common.utils.ApplicationContextUtil;
import org.opensds.platform.nemgr.itf.IDeviceManager;
import org.opensds.vasa.vasa.VasaArrayService;
import org.opensds.vasa.vasa.common.VvolConstant;
import org.opensds.vasa.vasa.db.model.NVirtualVolume;
import org.opensds.vasa.vasa.db.service.SnapshotCloneRecordService;
import org.opensds.vasa.vasa.db.service.StorageProfileService;
import org.opensds.vasa.vasa.db.service.StorageQosService;
import org.opensds.vasa.vasa.db.service.VirtualVolumeService;
import org.opensds.vasa.vasa.db.service.VvolProfileService;
import org.opensds.vasa.vasa.service.DiscoverServiceImpl;
import org.opensds.vasa.vasa.util.DateUtil;
import org.opensds.vasa.vasa.util.VASAUtil;
import org.opensds.vasa.vasa20.device.array.lun.IDeviceLunService;
import org.opensds.vasa.vasa20.device.array.snapshot.IDeviceSnapshotService;

import com.vmware.vim.vasa.v20.StorageFault;

/**
 * 该类完成两个功能：
 * 1、清理残留vvool
 * 2、控制发往DJ的delete和create的任务数
 * 3、清理长期不用的volume type
 * <p>
 * 清理满足以下条件的vvol卷
 * 1: vasa数据库处于deleteing状态并且是快照或fast-clone的原卷和有lun拷贝增值业务的卷
 * 2： status为error_creating的卷
 * 3： 创建了但是50分钟还没激活的卷
 */
public class VvolControlRunable implements Runnable {
    private static org.apache.logging.log4j.Logger LOGGER = org.apache.logging.log4j.LogManager
            .getLogger(VvolControlRunable.class);

    private VasaArrayService vasaArrayService = (VasaArrayService) ApplicationContextUtil.getBean("vasaArrayService");
    private VvolProfileService vvolProfileService = (VvolProfileService) ApplicationContextUtil.getBean("vvolProfileService");
    private StorageProfileService storageProfileService = (StorageProfileService) ApplicationContextUtil.getBean("storageProfileService");
    private StorageQosService storageQosService = (StorageQosService) ApplicationContextUtil.getBean("storageQosService");
    private SnapshotCloneRecordService snapshotCloneRecordService = (SnapshotCloneRecordService) ApplicationContextUtil.getBean("snapshotCloneRecordService");

    private VVolModel vvolModel;
    private VirtualVolumeService virtualVolumeService;
    private StorageModel storageModel;
    private static int SUSTAINED_MAX_HOUR = 24;
    private static int CREATING_MAX_HOUR = 1;
    private static final long INACTIVE_TIME = 3000L;
    private static final long STAY_TIME_24H = 59103811L;
    private static int SLEEP_TIME = 5000;
    private static int INTERVAL_TIME = 600000;
    //用于清理在DJ数据库中不在VASA数据库中的记录
    private static String DJ_VOLUME_SUCCESS_STATUS = "available";
    private long lastClearTime = 0;
    private long lastClearVVolTime = 0;
    private int PAGE_SIZE = 1000;
    //一个运行实例(单例)
    private static VvolControlRunable instance = null;
    //vvol在流量控制统计中驻留的时间
    private Map<String, Long> resideTimeMap;
    //进行压力计算的状态列表
    private List<String> pressStatusList;
    //当前DJ处的creting和deleting任务数
    private long tasksNumberOfDj = 0;
    private static IDeviceManager deviceManager = (IDeviceManager) ApplicationContextUtil.getBean("deviceManager");

    //获取单例
    public static synchronized VvolControlRunable getInstance(VirtualVolumeService virtualVolumeService) {
        if (instance == null) {
            instance = new VvolControlRunable(virtualVolumeService);
        }
        return instance;
    }

    private VvolControlRunable(VirtualVolumeService virtualVolumeService) {
        this.virtualVolumeService = virtualVolumeService;
        storageModel = new StorageModel();
        tasksNumberOfDj = 0;
        List<String> midStatusList = new ArrayList<String>();
        midStatusList.add("deleting");
        midStatusList.add("creating");
        pressStatusList = midStatusList;
        resideTimeMap = new HashMap<String, Long>();
        vvolModel = new VVolModel();
        lastClearTime = 0;
        lastClearVVolTime = 0;
    }

    public boolean checkDeleteTimes(String vvolId, Map<String, Integer> deletingCountMap, Map<String, Long> deletingTimeMap) throws StorageFault {
        long curTime = DateUtil.getUTCDate().getTime();
        //记录删卷本次删卷时间
        deletingTimeMap.put(vvolId, curTime);
        //记录删卷次数，第一次删除记录次数为1，后面每次删除记录删卷次数+1
        if (!deletingCountMap.containsKey(vvolId)) {
            deletingCountMap.put(vvolId, 1);
        } else {
            deletingCountMap.put(vvolId, deletingCountMap.get(vvolId) + 1);
        }
        if (deletingCountMap.get(vvolId) > SUSTAINED_MAX_HOUR) {
            virtualVolumeService.updateStatusByVvolId(vvolId, "error_deleting");
            LOGGER.error("Deleting status is too long. More than one day. vvolId is: " + vvolId);
            deletingCountMap.remove(vvolId);
            deletingTimeMap.remove(vvolId);
            return false;
        }
        return true;
    }


    /**
     * 清理状态为deleting的vvol lun
     *
     * @param deletingCountMap 记录vvol lun 删除次数
     * @param deletingTimeMap  记录vvol lun 删除时间
     */
    public void clearDeletingVirtualVolumeData(Map<String, Integer> deletingCountMap, Map<String, Long> deletingTimeMap) {
        LOGGER.debug("In clearDeletingVirtualVolumeData function.");
        List<NVirtualVolume> deletingList = new ArrayList<NVirtualVolume>();
        try {
            deletingList = virtualVolumeService.getDeletingVirtualVolumeOrderByDeletedTime();
        } catch (Exception e1) {
            LOGGER.error("Get deleting virtual volume list fail. Exception ", e1);
            return;
        }

        LOGGER.debug("The deletingList size is : " + deletingList.size());
        for (NVirtualVolume deletingVvol : deletingList) {
            LOGGER.info("Delete the virtual volume, the vvolId = " + deletingVvol.getVvolid());
            try {
                String vvolId = deletingVvol.getVvolid();
                String arrayId = deletingVvol.getArrayId();
                String rawId = deletingVvol.getRawId();
                if (vvolId == null || arrayId == null) {
                    LOGGER.error("Can't Delete vvol LUN, the vvolId=" + vvolId + " arrayId=" + arrayId + " rawId=" + rawId);
                }
                if ("NA".equalsIgnoreCase(rawId)) {
                    //getRawId().equalsIgnoreCase("NA")

                    if (!checkDeleteTimes(vvolId, deletingCountMap, deletingTimeMap)) {
                        LOGGER.warn("The vvolId= " + vvolId + "delete too many times, do not need to delete this time.");
                        continue;
                    }

                    if (DiscoverServiceImpl.getInstance().isNasVvol(vvolId)) {
                        LOGGER.info("delete nas vvol by auto clear, status is deleting ");
                        int result = DiscoverServiceImpl.getInstance().deleteVirtualVolume(deletingVvol, true);
                        if (result == 0) {
                            LOGGER.info("delete nas vvol by auto clear successfully ");
                        } else {
                            LOGGER.info("delete nas vvol by auto clear failed ");
                        }

                        //待验证
                        continue;
                    } else {
                        DiscoverServiceImpl.getInstance().deleteVirtualVolumeFromDatabase(vvolId);
                        LOGGER.info("The vvolId=" + vvolId + " do not have lunId, delete from DB successfully.");
                        continue;
                    }

                }
                //判断删除次数是否超过阀值
                if (!checkDeleteTimes(vvolId, deletingCountMap, deletingTimeMap)) {
                    LOGGER.warn("The vvolId= " + vvolId + "delete too many times, do not need to delete this time.");
                    continue;
                }
                VASAUtil.saveArrayId(arrayId);
                //判断是否有依赖关系不可以删除
                if (virtualVolumeService.checkDependencies(vvolId)) {
                    LOGGER.warn("The vvol vvolid:" + vvolId + " has snapshot or fast-clone in use");
                    continue;
                }
                //判断是否存在deleting状态的依赖卷，是的话暂时不删
                if (virtualVolumeService.checkDeletingDependencies(vvolId)) {
                    LOGGER.warn("The vvol vvolid:" + vvolId + " has snapshot or fast-clone status in deleting status, delet this vvol lun later.");
                }
                //删除卷
                boolean deleteFlag = false;
                if (VasaSrcTypeConstant.SNAPSHOT.equalsIgnoreCase(deletingVvol.getSourceType()) ||
                        VasaSrcTypeConstant.FAST_CLONE.equalsIgnoreCase(deletingVvol.getSourceType())) {
                    deleteFlag = DiscoverServiceImpl.getInstance().deleteSnapShotFormArray(vvolId);
                } else {
                    deleteFlag = DiscoverServiceImpl.getInstance().deleteVirtualVolumeFormArray(vvolId);
                }
                if (!deleteFlag) {
                    LOGGER.error("Deleting virtual volume vvolId=" + vvolId + " fail.");
                    //如果删除失败，则判断是否存在增值服务：如lunCopy，如果存在则删除
                    List<String> lunCopyTaskIds = DiscoverServiceImpl.getInstance().getLunCopyTaskId(arrayId, rawId);
                    if (lunCopyTaskIds.size() > 0) {
                        LOGGER.info("The vvol vvolid :" + vvolId + " exist Value-added services, the size is " + lunCopyTaskIds.size());
                        for (String luncopyId : lunCopyTaskIds) {
                            //遍历删除所有luncopy，能删则删，删除不了等下一周期
                            DiscoverServiceImpl.getInstance().deleteLunCopyTaskByLunCopyId(arrayId, luncopyId);
                        }
                    }
                }
            } catch (Exception e) {
                LOGGER.error("Deleting virtual volume vvolId=" + deletingVvol.getVvolid() + " fail. Exception ", e);
                continue;
            }
        }
        LOGGER.debug("End clearDeletingVirtualVolumeData, the deletingList size is : " + deletingList.size());
    }

    /**
     * 清理状态为Creaing且超過一天的vvol lun
     *
     * @param None
     */
    public void clearCreatingVirtualVolumeDataADayBefore() {
        LOGGER.debug("In clearCreatingVirtualVolumeDataADayBefore function.");
        List<NVirtualVolume> creatingList = new ArrayList<NVirtualVolume>();
        try {
            //获取数据库中creating状态超过一天的vvol lun
            creatingList = virtualVolumeService.getAllCreatingDataADayBeforeCreateTime();
            LOGGER.debug("The need to delete creatingList size is " + creatingList.size());
            for (NVirtualVolume deletingVvol : creatingList) {
                String vvolId = deletingVvol.getVvolid();
                //如果当前卷rawId 为空，则直接从db中删除
                if (null == deletingVvol.getRawId() || "".equals(deletingVvol.getRawId())) {
                    LOGGER.info("The vvolId=" + vvolId + " do not hava rawId, delete it form database");
                    virtualVolumeService.deleteVirtualVolumeByVvolId(vvolId);
                    continue;
                }
                //更新状态，交给清理deleting状态的方法清理
                virtualVolumeService.updateStatusAndDeletedTimeByVvolId(vvolId, "deleting");
            }
        } catch (Exception e) {
            LOGGER.error("delete creatingList fail. Exception : ", e);
        }
        LOGGER.debug("End clearCreatingVirtualVolumeDataADayBefore function.");
    }

    /**
     * 清理状态为ErrorCreating且超過50分钟的vvol lun
     *
     * @param None
     */
    public void clearErrorCreatingVirtualVolume() {
        LOGGER.debug("In clearErrorCreatingVirtualVolume function.");
        List<NVirtualVolume> errorCreatingList = new ArrayList<NVirtualVolume>();
        try {
            //获取数据库中状态为errorCreating的数据
            errorCreatingList = virtualVolumeService.getAllSpecifiedStatusVvols("error_creating");
            LOGGER.debug("The errorCreatingList size is : " + errorCreatingList.size());
            for (NVirtualVolume errorCreatingVvol : errorCreatingList) {
                LOGGER.info("Prepare to delete error_creating virtual volume, vvolId = " + errorCreatingVvol.getVvolid());
                //更新状态，交给清理deleting状态的方法清理
                virtualVolumeService.updateStatusAndDeletedTimeByVvolId(errorCreatingVvol.getVvolid(), "deleting");
            }
        } catch (Exception e) {
            LOGGER.error("clearErrorCreatingVirtualVolume fail. Exceptoion : ", e);
        }
        LOGGER.debug("End clearErrorCreatingVirtualVolume function.");
    }

    public void clearFinishedRecord() {
        LOGGER.debug("In clear Finished record");
        snapshotCloneRecordService.deleteFinishedRecord();
    }

    public void clearTimeoutRecord() {
        LOGGER.debug("In clear Timeout record");
        snapshotCloneRecordService.deleteTimeoutRecord();
    }

    /**
     * 清理状态为Initing状态且超過一天的vvol lun
     *
     * @param None
     */
    public void clearInitingVirtualVolume() {
        LOGGER.debug("In clearInitingVirtualVolume function.");
        List<NVirtualVolume> initingList = new ArrayList<NVirtualVolume>();
        try {
            //获取数据库中Initing状态的所有vvol lun
            initingList = virtualVolumeService.getAllSpecifiedStatusVvols("initing");
            LOGGER.debug("The initingList size is : " + initingList.size());
            long curTime = DateUtil.getUTCDate().getTime();
            for (NVirtualVolume initingVirtualVolume : initingList) {
                Long t = curTime - initingVirtualVolume.getCreationTime().getTime();
                LOGGER.debug("the timetimetime " + t.toString());
                if (curTime - initingVirtualVolume.getCreationTime().getTime() > STAY_TIME_24H) {
                    LOGGER.info("Prepare to delete initing virtualVolume, vvolId is: " + initingVirtualVolume.getVvolid());
                    //更新状态，交给清理deleting状态的方法清理
                    virtualVolumeService.updateStatusAndDeletedTimeByVvolId(initingVirtualVolume.getVvolid(), "deleting");
                }
            }
        } catch (Exception e) {
            LOGGER.error("clearInitingVirtualVolume fail. Exceptoion : ", e);
        }
    }

    /**
     * 清理状态为Inactive状态且时间超过一小时的snapshot
     *
     * @param None
     */
    public void clearInactiveSnapshot() {
        LOGGER.debug("In clearInactiveSnapshot function.");
        List<NVirtualVolume> inactiveSanpshotList = new ArrayList<NVirtualVolume>();
        try {
            //获取数据库中获取所有inactive的snapshot
            inactiveSanpshotList = virtualVolumeService.getAllInactiveSnapshots();
            LOGGER.debug("The inactiveSanpshotList size is : " + inactiveSanpshotList.size());
            long curTime = System.currentTimeMillis();
            for (NVirtualVolume inactiveSnapshot : inactiveSanpshotList) {
                if (curTime - inactiveSnapshot.getCreationTime().getTime() > INACTIVE_TIME * MagicNumber.LONG1000) {
                    LOGGER.info("Prepare to delete inactive snapshot, vvolId is: " + inactiveSnapshot.getVvolid());
                    //更新状态，交给清理deleting状态的方法清理
                    virtualVolumeService.updateStatusAndDeletedTimeByVvolId(inactiveSnapshot.getVvolid(), "deleting");
                }
            }
        } catch (Exception e) {
            LOGGER.error("clearInactiveSnapshot fail. Exceptoion : ", e);
        }
    }


    /*
     * VASA Provider 清理线程
     * 1、清理不受VASA Provider管理的VVOL卷  清理周期一周
     * 2、清理creating状态超过24H的数据           清理周期24H
     * 3、清理deleting状态的数据                        清理周期5s
     * 4、清理error_deleting状态的数据            清理周期24H
     * 5、清理残留Qos策略组                                  清理周期24H
     * 6、清理残留Policy                   清理周期24H
     * 7、清理超时未完成的异步任务                      清理周期24H
     * 8、清理未激活的快照                                    清理周期一小时
     * */
    @Override
    public void run() {
        // 记录删卷次数
        Map<String, Integer> deletingCountMap = new HashMap<String, Integer>();
        // 记录删卷时间
        Map<String, Long> deletingTimeMap = new HashMap<String, Long>();
        while (true) {
            try {
                LOGGER.debug("Enter vasa clear thread process.");

                //功能一 ：处理deleting状态的卷
                clearDeletingVirtualVolumeData(deletingCountMap, deletingTimeMap);
                //功能二 ：清理creating状态超过24H的数据
                clearCreatingVirtualVolumeDataADayBefore();
                //功能三 ：清理error_creating的数据
                clearErrorCreatingVirtualVolume();
                //功能四 : 清理incative snapshot的数据
                clearInactiveSnapshot();
                //功能五 : 清理initing状态超过24H的数据
                clearInitingVirtualVolume();

                //删除snapshotCloneRecord表中超过1天的无用数据
                clearFinishedRecord();
                //删除snapshotCloneRecord表中超过1天未完成的数据
                clearTimeoutRecord();


                Thread.sleep(5000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                LOGGER.error("clear thread error. Exception ", e);
            }
        }
    }

    /**
     * 当前VASA是否有卷任务在运行
     * 判断方法: 当前vasa 数据库中有deleting或者creating状态的卷
     *
     * @return
     */
    private boolean hasVolumeTask() {
        boolean hasTask = false;
        try {
            List<NVirtualVolume> deletings = virtualVolumeService.getAllSpecifiedStatusVvols("deleting");
            if (null != deletings && !deletings.isEmpty()) {
                return true;
            }
            List<NVirtualVolume> creatings = virtualVolumeService.getAllSpecifiedStatusVvols("creating");
            if (null != creatings && !creatings.isEmpty()) {
                return true;
            }
        } catch (Exception ex) {
            LOGGER.error("HasVolumeTask failed. Exception is: ", ex);
        }
        return hasTask;
    }

    /**
     * 如果有luncopy，先删除luncopy
     * 如果是快照,不做处理
     *
     * @param vvol:          vasa数据库中处于deleting状态的vvol
     * @param vvolId2Status: 从Dj处获取到的卷id和对应状态的map
     * @throws Exception
     */
    private void processAdditionalService(NVirtualVolume vvol, Map<String, String> vvolId2Status) throws Exception {
        //处理有增值业务的卷
        if (null != vvol) {
            String vvolId = vvol.getVvolid();
            //如果Dj侧有此卷，并且此卷在Dj侧的状态为error_deleting，才会判断是否有增值业务,否则会增大Dj侧的压力
            if (vvolId2Status.containsKey(vvolId) && vvolId2Status.get(vvolId).equalsIgnoreCase("error_deleting")) {
                String arrayId = vvol.getArrayId();

                //判断是否有luncopy
                SDKResult<S2DLunCopyBean> lunCopyResult = storageModel.getLunCopy(arrayId, vvol.getRawId());
                if (null != lunCopyResult.getResult()) {
                    String lunCopyIdStr = lunCopyResult.getResult().getLUNCOPYIDS();
                    LOGGER.info("In VvolControlRunable, vvolid is: " + vvolId + " LUNCOPYIDS is: " + lunCopyIdStr);
                    //2是因为需要去掉首位的中括号
                    if (lunCopyIdStr.length() > 2) {
                        //去除首位的中括号
                        if (lunCopyIdStr.startsWith("[") && lunCopyIdStr.endsWith("]")) {
                            String[] lunCopyIdArray = lunCopyIdStr.substring(1, lunCopyIdStr.length() - 1).split(",");
                            //卷上有luncopy增值业务
                            if ((null != lunCopyIdArray) && (lunCopyIdArray.length > 0)) {
                                //对于卷上面的每一个lunCopyId进行删除操作
                                for (String lunCopyId : lunCopyIdArray) {
                                    //去除首位的引号
                                    if (lunCopyId.startsWith("\"") && lunCopyId.endsWith("\"")) {
                                        lunCopyId = lunCopyId.substring(1, lunCopyId.length() - 1);
                                    }
                                    LOGGER.info("In VvolControlRunable, delete luncopy. vvolid: " + vvolId + " arrayId: " +
                                            arrayId + " lunCopyId: " + lunCopyId);
                                    //先停止luncopy
                                    SDKErrorCode sdkErrorCode = vvolModel.stopLuncopy(vvol.getArrayId(), lunCopyId, false);

                                    if (0 != sdkErrorCode.getErrCode()) {
                                        //If errorCode is 1077950180, the luncopy is already completed.
                                        if (1077950180 != sdkErrorCode.getErrCode()) {
                                            LOGGER.error("In VvolControlRunable, stopLuncopy error. params[" + vvol.getArrayId() + ","
                                                    + lunCopyId + ",false] errCode:" + sdkErrorCode.getErrCode()
                                                    + ", description:" + sdkErrorCode.getDescription());
                                        }
                                    }
                                    // 从阵列侧删除luncopy任务
                                    sdkErrorCode = vvolModel.deleteLuncopy(vvol.getArrayId(), lunCopyId, false);
                                    if (0 != sdkErrorCode.getErrCode()) {
                                        LOGGER.error("In VvolControlRunable, deleteLuncopy error. params[" + vvol.getArrayId() + ","
                                                + lunCopyId + ",false] errCode:" + sdkErrorCode.getErrCode()
                                                + ", description:" + sdkErrorCode.getDescription());
                                    }
                                }
                                //删除luncopy增值业务之后删除此卷
                                if (!testDjOverloadIncr()) {
                                    LOGGER.info("In VvolControlRunable, delete luncopy vvolid: " + vvolId);
                                    if (!vvol.getRawId().equals("NA") && !vvol.getArrayId().equals("NA")) {
                                        deleteForcely(vvol);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    /**
     * 处理第一次被第一次删除被流量控制或者依赖关系给挡住了
     *
     * @param deletingList
     * @param vvolId2Status
     * @throws Exception
     */
    private void processShoudDelete(List<NVirtualVolume> deletingList,
                                    Map<String, String> vvolId2Status) throws Exception {
        //遍历所有处于deleting状态的卷
        for (NVirtualVolume vvol : deletingList) {
            String vvolId = vvol.getVvolid();
            //如果vasa为deleting，Dj为available，那么可能是第一次删除被流量控制或者依赖关系给挡住了
            //此时因为VP没有发送过删卷命令，那么在Dj侧此卷一定处于available状态
            if (vvolId2Status.containsKey(vvolId) && vvolId2Status.get(vvolId).equalsIgnoreCase("available")) {
                if (shouldSendDeleteCmd(vvolId)) {
                    LOGGER.info("In VvolControlRunable, processShoudDelete delete vvolid: " + vvolId);
                    if (!vvol.getRawId().equals("NA") && !vvol.getArrayId().equals("NA")) {
                        deleteForcely(vvol);
                    }
                }
            }
        }
    }

    //获取DJ当前是否压力情况(单线程)
    private void getDjPressure(Map<String, String> vvolId2Status) {
        //清除resideTimeMap中的垃圾数据
        clearResideMapData(resideTimeMap, vvolId2Status);

        long djTaskNumber = 0L;
        try {
            for (Map.Entry<String, String> entry : vvolId2Status.entrySet()) {
                String vvolId = entry.getKey();
                String status = entry.getValue();

                long currentTime = DateUtil.getUTCDate().getTime();

                if (pressStatusList.contains(status)) {

                    /**
                     *  修改CodeDEX问题：OVERFLOW_BEFORE_WIDEN
                     *  By wWX315527 2016/11/17
                     *  新增Long.valueOf处理，避免溢出，该处为误报，修改不影响
                     */
                    if (!resideTimeMap.containsKey(vvolId)
                            || (currentTime - resideTimeMap.get(
                            vvolId) < Long.valueOf(CREATING_MAX_HOUR) * 3600 * 1000)) {
                        djTaskNumber = djTaskNumber + 1;
                        if (!resideTimeMap.containsKey(vvolId)) {
                            resideTimeMap.put(vvolId, currentTime);
                        }
                    }
                }
                /**
                 *  修改CodeDEX问题：OVERFLOW_BEFORE_WIDEN
                 *  By wWX315527 2016/11/17
                 *  新增Long.valueOf处理，避免溢出，该处为误报，修改不影响
                 */
                if (resideTimeMap.containsKey(vvolId)
                        && currentTime - resideTimeMap
                        .get(vvolId) >= Long.valueOf(SUSTAINED_MAX_HOUR) * 3600 * 1000) {
                    resideTimeMap.remove(vvolId);
                }

            }

            //将当前的任务设置为从Dj查询出来的任务数
            setDjTask(djTaskNumber);

        } catch (Exception e) {
            LOGGER.error("Get DJ press failed. Exception: ", e);
        } finally {
            try {
                Thread.sleep(SLEEP_TIME);
            } catch (InterruptedException e) {

            }
        }
    }

    /**
     * 当前是否应该向Dj发送删卷命令，能否下发必须满足以下两个条件:
     * 1、流量控制没有满；
     * 2、当前卷是否有依赖（快照或者fast-clone）
     *
     * @param vvolId
     * @return
     * @throws StorageFault
     */
    public boolean shouldSendDeleteCmd(String vvolId) throws StorageFault {
        if (!testDjOverloadIncr() && (virtualVolumeService.getSnapshotAndFastCloneCountByVvolId(vvolId) <= 0)) {
            return true;
        }
        return false;
    }

    /**
     *
     * 从Dj侧获取所有非deleted状态的卷和快照，并放入vvolId2Status中
     * 这个接口已经废除
     * @param vvolId2Status
     * @return false: 失败  true: 成功
     * @throws SDKException
     */
	/*private boolean getAllDjVvol2StatusMap(Map<String,String> vvolId2Status,Set<String> DJVolumesSet,
	        Set<String> DJSnapshotSet,long currentUTCTime) 
	        throws SDKException
	{
	    //因为是从Dj处获取所有的非deleted卷，那么就需要在获取之前先将map清空
	    vvolId2Status.clear();
	    DJVolumesSet.clear();
	    DJSnapshotSet.clear();
	    
	    //Get all volume from DJ
	    SDKResult<List<S2DVolume>> allVolumes =  vvolModel.getAllVolume();
        if(0 != allVolumes.getErrCode())
        {
            return false;
        }
        //Get all Snapshot from DJ
        SDKResult<List<S2DSnapshot>> allSnapshot = vvolModel.getAllSnapshot();
        if(0 != allSnapshot.getErrCode())
        {
            return false;
        }
        
        
        LOGGER.info("In VvolControlRunable getAllDjVvol2StatusMap, allVolumes size is: " + allVolumes.getResult().size()
                + " allSnapshot size is: " + allSnapshot.getResult().size());
        
        //traverse all vvol
        for(S2DVolume  arrayVvol : allVolumes.getResult())
        {
            String status = arrayVvol.getStatus();
            String createAt = arrayVvol.getCreated_at();
            vvolId2Status.put(VvolConstant.VVOL_PREFIX + arrayVvol.getId(), arrayVvol.getStatus());
            LOGGER.debug("Current vvolid: " + VvolConstant.VVOL_PREFIX + arrayVvol.getId() + " Current time is: " + currentUTCTime 
                    + " createAt: " + getUTCTimeStamp(createAt));
            //只统计DJ中状态为DJ_VOLUME_SUCCESS_STATUS并且创建时间超过1天的卷
            if(DJ_VOLUME_SUCCESS_STATUS.equalsIgnoreCase(status) && (currentUTCTime - getUTCTimeStamp(createAt) > VvolConstant.CLEAR_VVOL_INTERVAL_TIME))
            {
                DJVolumesSet.add(VvolConstant.VVOL_PREFIX + arrayVvol.getId());
            }
            LOGGER.debug("In VvolControlRunable getAllDjVvol2StatusMap, vvolid is: " + arrayVvol.getId() + " status is: " + arrayVvol.getStatus());
        }
        //traverse all Snapshot
        for(S2DSnapshot arraySnapshot : allSnapshot.getResult())
        {
            String status = arraySnapshot.getStatus();
            String createAt = arraySnapshot.getCreated_at();
            vvolId2Status.put(VvolConstant.VVOL_PREFIX + arraySnapshot.getId(), arraySnapshot.getStatus());
            LOGGER.debug("Current snapshotid: " + VvolConstant.VVOL_PREFIX + arraySnapshot.getId() + " Current time is: " + currentUTCTime 
                    + " createAt: " + getUTCTimeStamp(createAt));
            //只统计DJ中状态为DJ_VOLUME_SUCCESS_STATUS并且创建时间超过1天的快照
            if(DJ_VOLUME_SUCCESS_STATUS.equalsIgnoreCase(status) && (currentUTCTime - getUTCTimeStamp(createAt) > VvolConstant.CLEAR_VVOL_INTERVAL_TIME))
            {
                DJSnapshotSet.add(VvolConstant.VVOL_PREFIX + arraySnapshot.getId());
            }
            LOGGER.debug("In VvolControlRunable getAllDjVvol2StatusMap, snapshotId is: " + arraySnapshot.getId() + " status is: " + arraySnapshot.getStatus());
        }
        
        return true;
	}*/


    /**
     * 根据时间字符串获取时间的UTC时间戳
     *
     * @param createAt
     * @return
     */
    private long getUTCTimeStamp(String createAt) {
        long UTCTimeStamp = 0L;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date date;
        try {
            date = sdf.parse(createAt);
            UTCTimeStamp = date.getTime();
        } catch (ParseException e) {
            LOGGER.error("Parse utc time stamp failed. Exception is: ", e);
        }
        return UTCTimeStamp;
    }


    /**
     * 清除在resideTimeMap2中，但是不在vvolId2Status中的key的数据，出现这种情况的原因是:
     * resideTimeMap2中的key可能在之后的清理线程中被清理掉了
     *
     * @param resideTimeMap2 : 用于统计Dj压力的map对应数据
     * @param vvolId2Status  : 从Dj获取的关于卷和状态的对应关系
     */
    private void clearResideMapData(Map<String, Long> resideTimeMap2,
                                    Map<String, String> vvolId2Status) {

        resideTimeMap2.keySet().retainAll(vvolId2Status.keySet());
    }

    /**
     * 清除deletingTimeMap和deletingTimesMap中没有出现在deletingList中的vvolid的数据,可能出现场景是正常流程删除成功了
     * 下次轮训的时候deletingList中不存在此vvolid,所以需要清除deletingTimeMap和deletingTimesMap中的数据
     *
     * @param deletingTimeMap
     * @param deletingCountMap
     * @param deletingList
     */
    public synchronized void clearMapData(Map<String, Long> deletingTimeMap, Map<String, Integer> deletingCountMap, List<NVirtualVolume> deletingList) {
        Set<String> vvolidSet = new HashSet<String>();
        for (NVirtualVolume vvol : deletingList) {
            vvolidSet.add(vvol.getVvolid());
        }

        //从deletingTimeSet中移除不在vvolidSet的所有元素
        deletingTimeMap.keySet().retainAll(vvolidSet);
        deletingCountMap.keySet().retainAll(vvolidSet);
    }

    /**
     * 强制删除卷或快照
     *
     * @param vvol
     * @throws StorageFault
     * @throws SDKException
     */
    public void deleteForcely(NVirtualVolume vvol) throws StorageFault {
        String vvolId = vvol.getVvolid();

        try {
            // 判断vvol是否处于绑定状态
            SDKResult<List<S2DVvolBind>> bindResult = vvolModel.getVVOLBind(vvol.getArrayId(), vvol.getRawId());
            if (0 == bindResult.getErrCode() && bindResult.getResult() != null && bindResult.getResult().size() != 0) {
                LOGGER.error("ResourceInUse/the vvol vvolid:" + vvolId + " is in bound state.");
                return;
            }
            VASAUtil.saveArrayId(vvol.getArrayId());
            //TODO 删除卷,可以直接删掉的，有接口
            if (!isSnapshot(vvol)) {
                IDeviceLunService deviceLunService =
                        deviceManager.getDeviceServiceProxy(VASAUtil.getArrayId(),
                                IDeviceLunService.class);
                SDKErrorCode deleteLunResult = deviceLunService.deleteLun(vvol.getArrayId(), vvol.getRawId());
                if (deleteLunResult.getErrCode() != 0) {
                    LOGGER.error("delete lun error! msg=" + deleteLunResult.getDescription());
                }
            }
            //TODO 删除快照,有没有被激活，只能删除未激活的。
            //如果已经激活了，要先把快照取消激活
            else {
                IDeviceSnapshotService deviceSnapshotService =
                        deviceManager.getDeviceServiceProxy(VASAUtil.getArrayId(),
                                IDeviceSnapshotService.class);
                SDKErrorCode deactivateSnapshotResult = deviceSnapshotService.deactivateSnapshot(vvol.getArrayId(), vvol.getRawId());
                if (deactivateSnapshotResult.getErrCode() == 0) {
                    SDKErrorCode delSnapshotResult = deviceSnapshotService.deleteSnapshot(vvol.getArrayId(), vvol.getRawId());
                    if (delSnapshotResult.getErrCode() != 0) {
                        LOGGER.error("delsnapshot error !msg=" + delSnapshotResult.getDescription());
                    }
                } else {
                    LOGGER.error("deactivateSnapshotResult error !msg=" + deactivateSnapshotResult.getDescription());
                }
            }
        } catch (SDKException e) {
            LOGGER.error("Delete vvolId: " + vvolId + " failed. Exception is: ", e);
        }
    }

    /**
     * 判断卷是否是快照
     *
     * @param vvol
     * @return
     */
    public boolean isSnapshot(NVirtualVolume vvol) {
        if (VasaSrcTypeConstant.SNAPSHOT.equalsIgnoreCase(vvol.getSourceType())) {
            return true;
        }
        return false;
    }


    /**
     * 判断当前Dj是否压力过重
     *
     * @return
     */
    public synchronized boolean testDjOverloadIncr() {
        if (getDjTask() < VvolConstant.MAX_SEND_DJ) {
            //如果发现DJ不过载,增加任务数
            incrDjTask();
            return false;
        }
        return true;
    }

    /**
     * 获取当前DJ任务数
     *
     * @return
     */
    public synchronized long getDjTask() {
        return tasksNumberOfDj;
    }

    /**
     * @param
     */
    private synchronized void setDjTask(long taskNumber) {
        tasksNumberOfDj = taskNumber;
    }

    /**
     * @param
     */
    public synchronized void incrDjTask() {

        tasksNumberOfDj = tasksNumberOfDj + 1;
    }

    /**
     * @param
     */
    public synchronized void descDjTask() {
        tasksNumberOfDj = tasksNumberOfDj - 1;
        if (tasksNumberOfDj <= 0) {
            //LOGGER.warn("task number of Dj is smaller than 0. task number is: " + tasksNumberOfDj);
            tasksNumberOfDj = 0;
        }
    }
}
