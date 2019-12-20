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

package org.opensds.vasa.vasa.service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.opensds.vasa.common.DeviceType;
import org.opensds.vasa.common.MagicNumber;
import org.opensds.vasa.domain.model.bean.DArray;
import org.opensds.platform.common.utils.ApplicationContextUtil;
import org.opensds.vasa.vasa.db.model.StorageInfo;
import org.opensds.vasa.vasa.db.service.StorageManagerService;
import org.opensds.vasa.vasa.internal.Event;
import org.opensds.vasa.vasa.rest.bean.DeviceTypeMapper;
import org.opensds.vasa.vasa.util.DataUtil;
import org.opensds.vasa.vasa.util.FaultUtil;
import org.opensds.vasa.vasa.util.ListUtil;
import org.opensds.vasa.vasa.util.SessionContext;
import org.opensds.vasa.vasa.util.VASAUtil;

import com.vmware.vim.vasa.v20.InvalidArgument;
import com.vmware.vim.vasa.v20.LostAlarm;
import com.vmware.vim.vasa.v20.data.xsd.AlarmStatusEnum;
import com.vmware.vim.vasa.v20.data.xsd.AlarmTypeEnum;
import com.vmware.vim.vasa.v20.data.xsd.EntityTypeEnum;
import com.vmware.vim.vasa.v20.data.xsd.StorageAlarm;
import com.vmware.vim.vasa.v20.data.xsd.UsageContext;

public class AlarmService {
    // 单例
    private static AlarmService instance;

    // 日志
    private static org.apache.logging.log4j.Logger LOGGER = org.apache.logging.log4j.LogManager
            .getLogger(AlarmService.class);
    /***
     * S5500T P11G-5527 【R2&R5归一版本+ V100R005C00SPC003B015+VASA】VASA环境下，VASA
     * PROVIDER一直频繁的登录阵列，导致阵列屏蔽登录事件 begin
     */
    // 时间间隔
    // private static final long DEFAULT_ALARM_RETRY_INTERVAL = 20 * 60;

    /* 保存数据的对象 */
    private static DataUtil dataManager = DataUtil.getInstance();

    private DiscoverServiceImpl discoverServiceImpl = DiscoverServiceImpl.getInstance();

    private StorageManagerService storageManagerService = (StorageManagerService) ApplicationContextUtil.getBean("storageManagerService");

    // event事件查询的LOCK
    private final Object lockOfAlarm = new Object();

    /***
     * S5500T P11G-5527 【R2&R5归一版本+ V100R005C00SPC003B015+VASA】VASA环境下，VASA
     * PROVIDER一直频繁的登录阵列，导致阵列屏蔽登录事件 end
     */

    /* 定时器 */
    private Timer alarmTimer = new Timer();

    // 定时获取告警线程
    private AlarmTask alarmTask = null;

    // 时间间隔
    private int timeInterval = VASAUtil.getDefaultRetryTimeInSeconds();

    //获取最大告警数量
    private int maxAlarmNum = timeInterval / 30 * 100;

    /*
     * 构造方法
     */
    private AlarmService() {
    }

    /**
     * 获取单例对象 Accessor method for singleton instance
     *
     * @return AlarmManagerImpl 返回结果
     */
    public static synchronized AlarmService getInstance() {
        if (null == instance) {
            instance = new AlarmService();
        }
        return instance;
    }

    /**
     * 初始化
     */
    public void init() {

    }

    /**
     * 定时获取告警
     */
    public void scheduleAlarms() {
        if (null == alarmTask) {
            /** Schedule periodic service cache sync */
            int alarmInterval = getReScheduleInterval();

            LOGGER.debug("Scheduling alarms in " + alarmInterval + " seconds");
            alarmTask = new AlarmTask();
            alarmTimer.schedule(alarmTask, alarmInterval * MagicNumber.INT1000);
        }
    }

    /**
     * 注销告警的方法
     */
    public void destroy() {
        LOGGER.info("AlarmManagerImpl destroy");
        if (null != this.alarmTask) {
            this.alarmTask.cancel();
            LOGGER.debug("alarmTask canceld");
        }
        if (this.alarmTimer != null) {
            this.alarmTimer.cancel();
            LOGGER.debug("alarmTimer canceld");
        }
    }

    /**
     * 获取告警的定时间隔
     *
     * @return int [返回类型说明]
     * @see [类、类#方法、类#成员]
     */
    private int getReScheduleInterval() {
        return timeInterval;
    }

    /*
     * 获取所有设备的告警
     */
    private void populateAlarms() {
        //Set<String> arrayIds = dataManager.getArrayId();
        Set<String> arrayIds = new HashSet<String>();
        List<StorageInfo> storageInfos = storageManagerService.queryInfo();
        for (StorageInfo storageInfo : storageInfos) {
            if (storageInfo.getDeleted() == 0 && storageInfo.getDevicestatus().equalsIgnoreCase("ONLINE")) {
                arrayIds.add(storageInfo.getId());
            }
        }

        synchronized (lockOfAlarm) {
            for (String arrayid : arrayIds) {
                queryAlarms(arrayid, maxAlarmNum);
                if (!DeviceTypeMapper.getDeviceType(arrayid).equals(DeviceType.FusionStorage.toString())) {
                    VASAUtil.saveStorageLuns(arrayid);
                }
            }
        }
    }

    // /**
    // * 方法 ： main
    // *
    // * @param args 方法参数：args
    // */
    /*
     * public static void main(String[] args) {
     */
    // dataManager.init();
    // DeviceContext deviceContext = new DeviceContext("admin",
    // "Admin@storage", new String[] { "129.61.41.81" });
    // deviceContext.setDeviceType(DeviceType.C3);
    // AlarmManagerImpl alarm = AlarmManagerImpl.getInstance();
    // while (true)
    // {
    // // alarm.scheduleAlarms();
    // alarm.queryAlarms(deviceContext);
    // try
    // {
    // Thread.sleep(30000);
    // }
    // catch (InterruptedException e)
    // {
    // e.printStackTrace();
    // }
    // }
    /* } */

    /*
     * 打印
     */
    private void print(String sessionId, String uc, List<StorageAlarm> stoargeAlarms, long lastAlarmId) {
        LOGGER.info("sessionId:" + VASAUtil.replaceSessionId(sessionId) + " VcGuid is " + uc + ". get new alarm Data :"
                + stoargeAlarms.size() + " LastAlarmID is " + lastAlarmId);
    }

    /*
     * 查询指定设备的告警 这个是在后台任务当中
     */
    private void queryAlarms(String arrayid, int maxNum) {
        List<Event> alarms = null;
        LOGGER.info("queryAlarms" + arrayid + ";DeviceType:" + DeviceTypeMapper.getDeviceType(arrayid));
        if (DeviceTypeMapper.getDeviceType(arrayid).equals(DeviceType.FusionStorage.toString())) {
            try {
                // alarms = fusionStorageInfoImpl.getStorageAlarms(arrayid);
                LOGGER.info("get fusionStorage alarm" + alarms.size());
            } catch (Exception e) {
                LOGGER.info("get fusionStorage alarm error");
            }
        } else {
            alarms = discoverServiceImpl.queryAllAlarms(arrayid);
        }

        LOGGER.info("all alarm size is:" + arrayid + " ," + alarms.size());
        Map<String, Long> deviceAlarmSN = null;
        Map<String, List<Event>> sendToVcenterAlarms = dataManager.getAlarmForVcenter();
        Map<String, List<Event>> sendToVcenterConfigAlarms = dataManager.getConfigAlarmForvCenter();
        List<Event> savedAlarms = new ArrayList<Event>(0);
        List<SessionContext> scs = SessionContext.getSessionContextList();

        Long lastAlarmSN = null;
        int sessionContextCount = scs.size();
        SessionContext sc = null;
        // 获取告警只缓存vcenter server对应的UsageContext
        String sessionId = null;
        // long maxAlarmSN = -1;
        for (int i = 0; i < sessionContextCount; i++) {
            sc = scs.get(i);
            sessionId = sc.getSessionId();
            List<Event> toVcenterAlarms = sendToVcenterAlarms.get(sessionId);
            List<Event> toVcenterConfigAlarms = sendToVcenterConfigAlarms.get(sessionId);

            if (toVcenterAlarms == null) {
                toVcenterAlarms = new ArrayList<Event>(0);
            }

            if (toVcenterConfigAlarms == null) {
                toVcenterConfigAlarms = new ArrayList<Event>(0);
                dataManager.setConfigAlarmForvCenter(sessionId, toVcenterConfigAlarms);
            }

            //当前内存已存在最大告警数量，不用再添加告警
            if (toVcenterAlarms.size() >= maxNum) {
                return;
            }
            // 清空上一个设备的新增告警缓存
            savedAlarms.clear();
            // maxAlarmSN = -1;
            deviceAlarmSN = dataManager.getSavedVcenterForMaxAlarmID(sessionId);
            if (null == deviceAlarmSN) {
                deviceAlarmSN = new HashMap<String, Long>(0);
                dataManager.putVcenterForMaxAlarmID(sessionId, deviceAlarmSN);
            }
            // 上一次查询回来的最大告警流水号
            lastAlarmSN = deviceAlarmSN.get(arrayid);
            if (lastAlarmSN != null && 0 != lastAlarmSN.longValue()) {
                for (Event alarm : alarms) {
                    // 保存比之前流水号大的告警，表示是新增加的告警
                    if (alarm.getDeviceSN() > lastAlarmSN
                            || alarm.getEventID() == VASAUtil.THIN_LUN_ALARM_ID) {
                        savedAlarms.add(alarm);
                    }
                }
            } else {
                savedAlarms.addAll(alarms);
            }

            if (sc.getUsageContext().getHostGuid() != null && !sc.getUsageContext().getHostGuid().isEmpty()) {
                LOGGER.debug("clear last saved event, sessionId:" + VASAUtil.replaceSessionId(sessionId) + ", hsotGuid:"
                        + sc.getUsageContext().getHostGuid());
                if (null != toVcenterAlarms) {
                    toVcenterAlarms.clear();
                }
            }

            // 没有数据表示没有新的告警生成
            if (!savedAlarms.isEmpty()) {
                if (null == toVcenterAlarms) {
                    if (savedAlarms.size() > maxNum) {
                        savedAlarms = savedAlarms.subList(0, maxNum - 1);
                    }
                    toVcenterAlarms = savedAlarms;
                } else {
                    if ((toVcenterAlarms.size() + savedAlarms.size()) > maxNum) {
                        savedAlarms = savedAlarms.subList(0, maxNum - toVcenterAlarms.size());
                    }
                    toVcenterAlarms.addAll(0, savedAlarms);
                }
                long maxAlarmSN = VASAUtil.getMaxEventSN(savedAlarms);
                deviceAlarmSN.put(arrayid, maxAlarmSN);
                dataManager.putVcenterForMaxAlarmID(sessionId, deviceAlarmSN);
                dataManager.setAlarmForVcenter(sessionId, toVcenterAlarms);
            }

            LOGGER.debug("sessionId is :" + VASAUtil.replaceSessionId(sessionId) + ", deviceID is:" + arrayid
                    + ",maxAlarm ID is :" + deviceAlarmSN.get(arrayid).longValue() + ",newAlarm size is:"
                    + savedAlarms.size());
        }
    }

    /**
     * 定时获取告警的线程
     *
     * @author d69088
     * @version [版本号V001R010C00, 2011-12-14]
     * @see [相关类/方法]
     * @since [产品/模块版本]
     */
    private class AlarmTask extends TimerTask {
        /**
         * 方法 ： run
         */
        public void run() {
            int rescheduleInterval = getReScheduleInterval();

            try {

                if (SecureConnectionService.getInstance().checkCurrentNodeIsMaster()) {
                    populateAlarms();
                } else {
                    LOGGER.info("current node is not master node , no need reports alarm.");
                }

            } catch (Exception e) {
                LOGGER.debug("AlarmManager populate alarms error:", e);
                rescheduleInterval = timeInterval;
            }

            alarmTask = null;
            if (rescheduleInterval > 0) {
                scheduleAlarms();
            }
        }
    }

    // /**
    // * 方法 ： getAlarms
    // *
    // * @param uc 方法参数：uc
    // * @param lastReturnedAlarmId 方法参数：lastReturnedAlarmId
    // * @throws InvalidArgument 命令执行过程中的InvalidArgument
    // * @throws LostAlarm 异常：LostAlarm
    // * @return StorageAlarm[] 返回结果
    // */

    /**
     * override get Alarms
     *
     * @param uc                  uc
     * @param lastReturnedAlarmId lastReturnedAlarmId
     * @return StorageAlarm[]
     */
    public StorageAlarm[] getAlarms(String sessionId, UsageContext uc, long lastReturnedAlarmId)
            throws InvalidArgument, LostAlarm {
        LOGGER.info("getAlarms called, and lastReturnedAlarmId is:" + lastReturnedAlarmId + ", ucUUID:"
                + VASAUtil.getUcUUID(uc) + ", sessionId:" + VASAUtil.replaceSessionId(sessionId));

        if (lastReturnedAlarmId < -1) {
            LOGGER.error("InvalidArgument/lastAlarmId must be -1 or greater. (" + lastReturnedAlarmId + ')');
            throw FaultUtil.invalidArgument("lastAlarmId must be -1 or greater. (" + lastReturnedAlarmId + ')');
        }
        List<StorageAlarm> returnAlrams = new ArrayList<StorageAlarm>(MagicNumber.INT16);
        // 将主机信息保存
        VASAUtil.saveHostInitiatorIds(uc);
        synchronized (lockOfAlarm) {
            // 获取当前session的上次告警ID
            Long lastAlarmIDObj = dataManager.getSavedLastAlarmID(sessionId);
            if (null != lastAlarmIDObj) {
                if (lastReturnedAlarmId != -1 && lastReturnedAlarmId > lastAlarmIDObj) {
                    LOGGER.error("InvalidArgument/Last alarmID error, id is :" + lastReturnedAlarmId);
                    throw FaultUtil.invalidArgument("Last AlarmID is :" + lastReturnedAlarmId + " error.");
                }
            }
            Map<String, List<Event>> alarms = dataManager.getAlarmForVcenter();
            LOGGER.info("Start to get configAlarms ....");
            Map<String, List<Event>> configAlarms = dataManager.getConfigAlarmForvCenter();
            LOGGER.info("configAlarms is : " + configAlarms.toString());
            List<Event> ucAlarms = alarms.get(sessionId);
            List<Event> cfAlarms = new ArrayList<Event>();
            cfAlarms = configAlarms.get(sessionId);


            // 获取数据后，从列表中移除
            List<Event> removedAlarms = new ArrayList<Event>(MagicNumber.INT16);
            List<Event> removeConfigAlarms = new ArrayList<Event>(MagicNumber.INT16);
            if (null == ucAlarms || ucAlarms.isEmpty()) {
                ucAlarms = new ArrayList<Event>(0);
            }
            if (null == cfAlarms || cfAlarms.isEmpty()) {
                cfAlarms = new ArrayList<Event>(0);
            }

            LOGGER.debug("ucAlarms size is " + ucAlarms.size() + " ucAlarms : " + ucAlarms.toString());
            LOGGER.debug("cfAlarms size is " + cfAlarms.size() + " cfAlarms : " + cfAlarms.toString());

            LOGGER.debug("old returnAlamrs size is ======== " + ucAlarms.size() + ", ucUUID is "
                    + VASAUtil.getUcUUID(uc) + ", sessionId:" + VASAUtil.replaceSessionId(sessionId));
            getAlarmsByUC(sessionId, uc, lastReturnedAlarmId, returnAlrams, ucAlarms, removedAlarms);
            getAlarmsByUC(sessionId, uc, lastReturnedAlarmId, returnAlrams, cfAlarms, removeConfigAlarms);
            return returnAlrams.toArray(new StorageAlarm[returnAlrams.size()]);
        }
    }

    public List<StorageAlarm> getAlarmsByUC(String sessionId, UsageContext uc, long lastReturnedAlarmId, List<StorageAlarm> returnAlrams,
                                            List<Event> alarms, List<Event> removedAlarms) throws InvalidArgument, LostAlarm {
        Long lastAlarmIDObj = dataManager.getSavedLastAlarmID(sessionId);

        if (null != lastAlarmIDObj) {
            // -1时，上报所有没有上报过的告警
            if (lastAlarmIDObj.longValue() != lastReturnedAlarmId && -1 != lastReturnedAlarmId) {
                LOGGER.error("LostAlarm/Last alarmID error, id is :" + lastReturnedAlarmId);
                throw FaultUtil.lostAlarm("Last AlarmID is :" + lastReturnedAlarmId + " error.");
            }

            // 表示vCenter重新请求告警,将最后的alarmID置为-1；
            if (lastReturnedAlarmId == -1 && lastAlarmIDObj.longValue() >= -1) {
                dataManager.setSavedLastAlarmID(sessionId, -1);
            }
        } else {
            // lastAlarmIDObj = new Long(-1);
            lastAlarmIDObj = Long.valueOf(-1);
        }
        return retriveAlarms(sessionId, lastAlarmIDObj.longValue(), uc, returnAlrams, alarms, removedAlarms);
    }


    /**
     * <转换告警>
     *
     * @param lastReturnedAlarmId
     * @param ucUUID
     * @param returnAlrams
     * @param ucAlarms
     * @param removedAlarms
     * @return StorageAlarm[] [返回类型说明]
     * @throws throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    private List<StorageAlarm> retriveAlarms(String sessionId, long lastReturnedAlarmId, UsageContext ucUUID,
                                             List<StorageAlarm> returnAlrams, List<Event> ucAlarms, List<Event> removedAlarms) {
        LOGGER.debug("retrive StorageAlarm begin.");
        LOGGER.debug("ucAlarms ===================== " + ucAlarms.size());
        DiscoverService nexManager = DiscoverService.getInstance();
        Date date = null;
        StorageAlarm storageAlarm = null;
        String param = null;
        String lunId = null;
        String objID = null;
        DArray storageArray = null;
        Calendar calendar = null;
        String arrayID = null;
        /**
         * 是否把thinLUN告警加到最后面
         */
        boolean addThinLunAlarmLast = false;
        boolean addStorageArrayOnlineOfflineLast = false;

        for (Event alarm : ucAlarms) {
            addThinLunAlarmLast = false;
            addStorageArrayOnlineOfflineLast = false;
            // 过滤SVP告警
            if (alarm.getDeviceSN() >= VASAUtil.HVS_SVP_ALARM_SN) {
                removedAlarms.add(alarm);
                continue;
            }

            // 每次查询的告警不能超过100条
            if (returnAlrams.size() >= VASAUtil.MAX_STORAGE_ALARM_EVENT_NUM) {
                break;
            }

            date = new Date(alarm.getStartTime());
            calendar = Calendar.getInstance();
            calendar.setTime(date);

            storageArray = dataManager.getArray(alarm.getDeviceId());
            if (null == storageArray) {
                try {
                    nexManager.queryStorageArrays();
                    storageArray = dataManager.getArray(alarm.getDeviceId());
                } catch (Exception e) {
                    LOGGER.debug("retriveAlarms cannot the storage array.");
                }
            }

            // 查询时还是没有找到，则跳过此循环
            if (null == storageArray) {
                removedAlarms.add(alarm);
                continue;
            }

            if (null != storageArray) {
                arrayID = VASAUtil.getArraySnFromUniqueId(storageArray.getUniqueIdentifier());
                calendar.setTimeZone(dataManager.getTimeZone(storageArray.getUniqueIdentifier()));
            }

            storageAlarm = new StorageAlarm();
            storageAlarm.setAlarmId(++lastReturnedAlarmId);
            storageAlarm.setAlarmTimeStamp(calendar);
            storageAlarm.setAlarmType(AlarmTypeEnum.OBJECT.value());
            storageAlarm.setMessageId(String.valueOf(alarm.getEventID()));
            // 直接界面显示内容
            storageAlarm.setObjectId(storageArray.getUniqueIdentifier());
            storageAlarm.setObjectType(EntityTypeEnum.STORAGE_ARRAY.value());
            ListUtil.clearAndAdd(storageAlarm.getParameterList(),
                    VASAUtil.convertNameValuePair(alarm.getEventParserdParam(), arrayID));
            storageAlarm.setStatus(convertAlarmStatus(alarm));
            // alarm.
            LOGGER.info("ucAlarms getEventID-----:" + alarm.getEventID());

            if (alarm.getEventID() == VASAUtil.THIN_LUN_ALARM_ID) {
                LOGGER.info("ucAlarms getEventID-----:0000000000");
                param = alarm.getEventParam();
                storageAlarm.setMessageId("Alarm.ThinProvisioned");
                ListUtil.clearAndAdd(storageAlarm.getParameterList(), VASAUtil.convertNameValuePair(param, arrayID));
                // 参数构成为THIN LUN ID,使用率
                lunId = param.substring(0, param.indexOf(","));
                objID = VASAUtil.getStorageEntityID(VASAUtil.getDeviceID(alarm, storageArray),
                        EntityTypeEnum.STORAGE_LUN.value(), lunId);

                LOGGER.debug("Get thin alarm:" + objID + ", UCUUID = " + VASAUtil.getUcUUID(ucUUID));
                // boolean isStorageLunExistInUC = true;
                if (!VASAUtil.isStorageLunExistInUC(ucUUID, objID, alarm)) {
                    LOGGER.debug("The thin alarm is not in this UCUUID:" + objID);
                    // isStorageLunExistInUC = false;
                    lastReturnedAlarmId--;
                    removedAlarms.add(alarm);
                    continue;
                }
                storageAlarm.setObjectId(objID);
                storageAlarm.setAlarmType(AlarmTypeEnum.SPACE_CAPACITY.value());
                storageAlarm.setObjectType(EntityTypeEnum.STORAGE_LUN.value());
                LOGGER.debug("Get a thin alarm:" + storageAlarm.getObjectId());
                addThinLunAlarmLast = true;
            } else if (alarm.getEventID() == VASAUtil.STORAGE_ONLINE_ALARM_ID) {
                LOGGER.info("generate storage array green alarm.");
                storageAlarm.setMessageId("Storage Array status change online ");
                storageAlarm.setObjectId(storageArray.getUniqueIdentifier());
                storageAlarm.setObjectType(EntityTypeEnum.STORAGE_ARRAY.value());
                storageAlarm.setAlarmType(AlarmTypeEnum.MANAGEABILITY.value());
                storageAlarm.setStatus(AlarmStatusEnum.GREEN.value());
                addStorageArrayOnlineOfflineLast = true;
            } else if (alarm.getEventID() == VASAUtil.STORAGE_OFFILINE_ALARM_ID) {
                LOGGER.info("generate storage array Red alarm.");
                storageAlarm.setMessageId("Storage Array status change offline ");
                storageAlarm.setObjectId(storageArray.getUniqueIdentifier());
                storageAlarm.setObjectType(EntityTypeEnum.STORAGE_ARRAY.value());
                storageAlarm.setAlarmType(AlarmTypeEnum.MANAGEABILITY.value());
                storageAlarm.setStatus(AlarmStatusEnum.RED.value());
                addStorageArrayOnlineOfflineLast = true;
            }
            if (addThinLunAlarmLast || addStorageArrayOnlineOfflineLast) {
                returnAlrams.add(storageAlarm);
            } else {
                returnAlrams.add(0, storageAlarm);
            }

            removedAlarms.add(alarm);
        }

        LOGGER.debug("retrive StorageAlarm end.");
        // 移除已经转换的告警
        ucAlarms.removeAll(removedAlarms);
        dataManager.setAlarmForVcenter(sessionId, ucAlarms);

        // 保存lastalarmID
        dataManager.setSavedLastAlarmID(sessionId, lastReturnedAlarmId);

        print(sessionId, VASAUtil.getUcUUID(ucUUID), returnAlrams, lastReturnedAlarmId);
        LOGGER.info("Retrived alarm size is :" + returnAlrams.size());
        LOGGER.debug("saved vcenterID is:" + VASAUtil.getUcUUID(ucUUID) + ",lastAlarmID is :"
                + dataManager.getSavedLastAlarmID());
        removedAlarms.clear();

        // 重新排序
        // 将告警重新排序 前面已经将THIN LUN告警放在最后面了
        resortAlarms(returnAlrams, lastReturnedAlarmId);

        // 记录返回的告警
        for (StorageAlarm tempAlarm : returnAlrams) {
            printStorageAlarmMes(tempAlarm, VASAUtil.getUcUUID(ucUUID));
        }

        //return returnAlrams.toArray(new StorageAlarm[returnAlrams.size()]);
        return returnAlrams;
    }

    /**
     * 将thin lun告警放在最后面 要求具有最大的流水号（alarmid） 而且将冲排序 要求 顺序是连续的
     *
     * @param alarms
     * @param maxIndex
     */
    private void resortAlarms(List<StorageAlarm> alarms, long maxIndex) {
        int size = alarms.size();
        for (int i = 0; i < size; i++) {
            alarms.get(i).setAlarmId(maxIndex - size + 1 + i);
        }
    }

    /*
     * 打印告警信息
     */
    private void printStorageAlarmMes(StorageAlarm storageAlarm, String ucuuid) {
        LOGGER.debug(ucuuid + " GetAlarm info is : " + VASAUtil.LINE_SEPRATOR + "alarmID is :"
                + storageAlarm.getAlarmId() + VASAUtil.LINE_SEPRATOR + "alarmTimeStamp is :"
                + storageAlarm.getAlarmTimeStamp() + VASAUtil.LINE_SEPRATOR + "alarmType is :"
                + storageAlarm.getAlarmType() + VASAUtil.LINE_SEPRATOR + "alarmMessageID is :"
                + storageAlarm.getMessageId() + VASAUtil.LINE_SEPRATOR + "alarmObjectID is :"
                + storageAlarm.getObjectId() + VASAUtil.LINE_SEPRATOR + "alarmObjectType is :"
                + storageAlarm.getObjectType() + VASAUtil.LINE_SEPRATOR + "alarmParameter is :"
                + VASAUtil.convertNameValuePairToString(storageAlarm.getParameterList()) + VASAUtil.LINE_SEPRATOR
                + "alarmStatus is :" + storageAlarm.getStatus() + VASAUtil.LINE_SEPRATOR);
    }

    /*
     * 转换告警级别
     */
    private String convertAlarmStatus(Event event) {
        Event.Level eventLevel = event.getLevel();
        switch (eventLevel) {
            case Warning:
            case Major:
                return AlarmStatusEnum.YELLOW.value();
            case Critical:
                return AlarmStatusEnum.RED.value();
            case Info:
            default:
                return AlarmStatusEnum.GREEN.value();
        }
    }
}
