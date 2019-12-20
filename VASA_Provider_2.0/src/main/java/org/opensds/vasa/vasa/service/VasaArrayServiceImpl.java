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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensds.vasa.common.MagicNumber;
import org.opensds.vasa.domain.model.bean.S2DStoragePool;
import org.opensds.vasa.domain.model.bean.StoragePolicy;
import org.opensds.vasa.interfaces.device.storagepool.IStoragePoolCapability;
import org.opensds.vasa.vasa20.device.array.lun.IDeviceLunService;
import org.opensds.vasa.vasa20.device.array.lun.LunCopyCreateResBean;
import org.opensds.vasa.vasa20.device.array.lun.LunCreateResBean;
import org.opensds.vasa.vasa20.device.array.qos.IDeviceQosService;
import org.opensds.vasa.vasa20.device.array.qos.QosCreateResBean;
import org.opensds.vasa.vasa20.device.array.snapshot.IDeviceSnapshotService;
import org.opensds.vasa.vasa20.device.array.snapshot.SnapshotCreateResBean;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.opensds.vasa.base.bean.terminal.ResBean;
import org.opensds.vasa.base.common.VASAArrayUtil;
import org.opensds.vasa.base.common.VASAArrayUtil.QosStatus;

import org.opensds.platform.common.SDKErrorCode;
import org.opensds.platform.common.SDKResult;
import org.opensds.platform.common.exception.SDKException;
import org.opensds.platform.common.utils.ApplicationContextUtil;
import org.opensds.platform.nemgr.itf.IDeviceManager;
import org.opensds.vasa.vasa.VasaArrayService;
import org.opensds.vasa.vasa.db.model.NStorageProfile;
import org.opensds.vasa.vasa.db.model.NStorageQos;
import org.opensds.vasa.vasa.db.model.NTaskInfo;
import org.opensds.vasa.vasa.db.model.StorageInfo;
import org.opensds.vasa.vasa.db.service.StorageManagerService;
import org.opensds.vasa.vasa.db.service.StorageProfileService;
import org.opensds.vasa.vasa.db.service.StorageQosService;
import org.opensds.vasa.vasa.db.service.VirtualVolumeService;
import org.opensds.vasa.vasa.rest.bean.DeviceTypeMapper;
import org.opensds.vasa.vasa.util.DateUtil;
import org.opensds.vasa.vasa.util.FaultUtil;
import org.opensds.vasa.vasa.util.VASAUtil;

import com.vmware.vim.vasa.v20.StorageFault;

public class VasaArrayServiceImpl implements VasaArrayService {

    private static Logger LOGGER = LogManager.getLogger(VasaArrayServiceImpl.class);
    private StorageQosService storageQosService;
    private StorageProfileService storageProfileService;
    private IDeviceManager deviceManager;
    private static StorageManagerService storageManagerService = ApplicationContextUtil.getBean("storageManagerService");
    private VirtualVolumeService virtualVolumeService = (VirtualVolumeService) ApplicationContextUtil.getBean("virtualVolumeService");

    public static void main(String[] args) {
        String a = "1000";
        long v = 0;
        if (null != a && !a.equals("")) {
            v = Long.valueOf(a);
        }
        System.out.println(v);
    }

    @Override
    public void createStorageProfile(StoragePolicy profile, String policyId, String profileId,
                                     String profileName, String lunId, long generationId, String vvolId, String vvolType) throws StorageFault {
        // TODO Auto-generated method stub
        //创建StorageQos在阵列上并且获取QosId

        try {
            LOGGER.debug("createStorageProfile , profile= " + profile + ",profileId=" + policyId + ",profileName=" + profileName + ",generationId=" + generationId);
            String arrayId = VASAUtil.getArrayId();
            IDeviceQosService deviceQosService =
                    deviceManager.getDeviceServiceProxy(arrayId,
                            IDeviceQosService.class);
            Long qosLatency = null;
            String qosControllerObjectLatency = profile.getQosControllerObjectLatency();
            if (null != qosControllerObjectLatency && !qosControllerObjectLatency.equals("")) {
                qosLatency = Long.valueOf(qosControllerObjectLatency);
            }
            saveProfile(profile, profileId, policyId, profileName, generationId, null);

            if (VASAUtil.getPolicyNoQos() || !vvolType.equalsIgnoreCase("Data") || profile.isCloseQos() || null == profile.getQosControllerType()) {
                return;
            }

            if (profile.getControlType().equalsIgnoreCase(NStorageProfile.ControlType.level_control)) {
                StorageInfo queryInfoByArrayId = storageManagerService.queryInfoByArrayId(arrayId);
                boolean supportQosLower = DeviceTypeMapper.isSupportQosLower(queryInfoByArrayId.getModel());
                if (!supportQosLower && profile.getQosControllerPolicy().equals(VASAArrayUtil.ControlPolicy.isLowerBound)) {
                    LOGGER.info("Current array can not support Control lower bound.Update to Control upper bound");
                    profile.setQosControllerPolicy(VASAArrayUtil.ControlPolicy.isUpperBound);
                    profile.setQosMaxBandwidth(profile.getQosMinBandwidth());
                    profile.setQosMaxIOPS(profile.getQosMinIOPS());
                    profile.setQosMinBandwidth(null);
                    profile.setQosMinIOPS(null);
                }
            }
            String qosName = "VASA_" + String.valueOf(new Random().nextInt(999999999)) + "_" + System.currentTimeMillis();
            SDKResult<QosCreateResBean> qosCreateRes = deviceQosService.createQos(arrayId, qosName, null, profile.getQosControllerType(), profile.getQosControllerObjectMaxBandwidth(), profile.getQosControllerObjectMinBandwidth(),
                    profile.getQosControllerObjectMaxIOPS(), profile.getQosControllerObjectMinIOPS(), qosLatency, profile.getQosControllerPolicy(), lunId);
            if (qosCreateRes.getErrCode() != 0) {
                LOGGER.error("createQos error! profile=" + profile + ",errorCode=" + qosCreateRes.getErrCode() + ",msg=" + qosCreateRes.getDescription());
                if (null == vvolId) {
                    virtualVolumeService.updateStatusByVvolId(vvolId, VASAArrayUtil.VVOLSTATUS.error_creating);
                }
                throw FaultUtil.storageFault("createQos error! errMsg=" + qosCreateRes.getDescription());
            }

            LOGGER.debug("createQos result = " + qosCreateRes);
            String rawQosId = qosCreateRes.getResult().getID();
            //保存创建的StorageQos在数据库中
            String qosId = UUID.randomUUID().toString();
            saveCreateQos(profile, qosName, rawQosId, qosId);
            //激活StorageQos在阵列上
            SDKErrorCode activeQosRes = deviceQosService.activeQos(arrayId, rawQosId);
            if (activeQosRes.getErrCode() != 0) {
                LOGGER.error("activeQos error! rawQosId=" + rawQosId + ",msg=" + activeQosRes.getDescription());
                if (null == vvolId) {
                    virtualVolumeService.updateStatusByVvolId(vvolId, VASAArrayUtil.VVOLSTATUS.error_creating);
                }
                throw FaultUtil.storageFault("activeQos error ! errMsg=" + activeQosRes.getDescription());
            }
            //更新激活状态
            updateQosStatus(qosId);
            updateQosIdInProfile(profileId, qosId);
            //保存policy数据到数据库中
        } catch (SDKException e) {
            // TODO Auto-generated catch block
            LOGGER.error("createStorageProfile error, SDKException !", e);
            throw FaultUtil.storageFault();
        }
    }


    private void updateQosIdInProfile(String profileId, String qosId) {
        // TODO Auto-generated method stub
        NStorageProfile nStorageProfile = new NStorageProfile();
        nStorageProfile.setProfileId(profileId);
        nStorageProfile.setSmartQosId(qosId);
        storageProfileService.updateData(nStorageProfile);
    }


    private void saveProfile(StoragePolicy profile,
                             String profileId, String policyId, String profileName,
                             long generationId, String qosId) {
        if (VASAUtil.getPolicyNoQos() && VASAUtil.checkProfileIdInStorageProfile(profileId)) {
            return;
        }
        NStorageProfile nStorageProfile = new NStorageProfile();
        nStorageProfile.setProfileId(profileId);
        nStorageProfile.setPolicyId(policyId);
        nStorageProfile.setProfileName(profileName);
        if (profile.getSmartTier() != null) {
            nStorageProfile.setIsSmartTier(true);
            nStorageProfile.setSmartTierValue(profile.getSmartTier());
        } else {
            nStorageProfile.setIsSmartTier(false);
        }
        if (profile.getDiskType() != null || profile.getRaidLevel() != null) {
            nStorageProfile.setIsStorageMedium(true);
            nStorageProfile.setDiskTypeValue(profile.getDiskType());
            nStorageProfile.setRaidLevelValue(profile.getRaidLevel());
        } else {
            nStorageProfile.setIsStorageMedium(false);
        }
		/*if(null != profile.getUserLevel()) {
			nStorageProfile.setControlType(NStorageProfile.ControlType.level_control);
			nStorageProfile.setControlTypeId(profile.getProfileLevelId());
		}else {
			nStorageProfile.setControlType(NStorageProfile.ControlType.precision_control);
		}*/
        nStorageProfile.setControlType(profile.getControlType());
        nStorageProfile.setControlTypeId(profile.getControlTypeId());

        nStorageProfile.setSmartQosId(qosId);
        nStorageProfile.setOmCreated(false);
        nStorageProfile.setDeleted(false);
        nStorageProfile.setThinThick(profile.getType());
        nStorageProfile.setGenerationId(generationId);
        nStorageProfile.setContainerId(profile.getContainerId());
        nStorageProfile.setDeprecated("false");
        storageProfileService.save(nStorageProfile);
    }


    private void updateQosStatus(String qosId) {
        NStorageQos updateStorageQos = new NStorageQos();
        updateStorageQos.setId(qosId);
        updateStorageQos.setStatus(QosStatus.ACTIVE);
        storageQosService.updateData(updateStorageQos);
    }


    private NStorageQos saveCreateQos(StoragePolicy profile, String qosName, String rawQosId, String qosId) {
        NStorageQos nStorageQos = new NStorageQos();
        nStorageQos.setId(qosId);
        nStorageQos.setRawQosId(rawQosId);
        nStorageQos.setName(qosName);
        nStorageQos.setDescription(null);
        nStorageQos.setStatus(QosStatus.CREATED);
        nStorageQos.setControlType(profile.getQosControllerType());
        nStorageQos.setControlPolicy(profile.getQosControllerPolicy());
        if (null != profile.getQosControllerObjectLatency()) {
            nStorageQos.setLatency(Long.valueOf(profile.getQosControllerObjectLatency()));
        }
        if (profile.getQosControllerPolicy().endsWith(VASAArrayUtil.ControlPolicy.isLowerBound)) {
            nStorageQos.setIops(profile.getQosControllerObjectMinIOPS());
            nStorageQos.setBandWidth(profile.getQosControllerObjectMinBandwidth());
        } else if (profile.getQosControllerPolicy().endsWith(VASAArrayUtil.ControlPolicy.isUpperBound)) {
            nStorageQos.setIops(profile.getQosControllerObjectMaxIOPS());
            nStorageQos.setBandWidth(profile.getQosControllerObjectMaxBandwidth());
        }
        nStorageQos.setCreatedTime(DateUtil.getUTCDate());
        nStorageQos.setDeleted(false);
        LOGGER.debug("save storageQos = " + nStorageQos);
        storageQosService.save(nStorageQos);
        return nStorageQos;
    }


    @Override
    public SDKResult<LunCreateResBean> createLun(String name, String description, int sizeInGB, long sizeInMB, String vmName, String parentId, String allocType, String dataTransferPolicy, int ioProperty) throws StorageFault {
        // TODO Auto-generated method stub

        IDeviceLunService deviceLunService;
        try {
            String arrayId = VASAUtil.getArrayId();
            StorageInfo queryInfoByArrayId = storageManagerService.queryInfoByArrayId(arrayId);
            String thinThickSupport = DeviceTypeMapper.thinThickSupport(queryInfoByArrayId.getModel());
            if (null != thinThickSupport) {
                LOGGER.info("Current productmode have special thin/thick. Mode=" + queryInfoByArrayId.getModel() + ",thinThickSupport=" + thinThickSupport);
                allocType = thinThickSupport;
            }
            LOGGER.debug("begin createLun ! ");
            deviceLunService = deviceManager.getDeviceServiceProxy(arrayId,
                    IDeviceLunService.class);
            //首先创建卷
            LOGGER.debug("deviceLunService = " + deviceLunService);
            SDKResult<LunCreateResBean> createLunRes = deviceLunService.createLun(arrayId, name, description, parentId, VASAArrayUtil.SUBTYPE.vvolLUN,
                    allocType, sizeInMB * MagicNumber.LONG1024 * 2l, dataTransferPolicy, VASAArrayUtil.USAGETYPE.VVOLLUN.getValue(), ioProperty);
            LOGGER.debug("end createLun ! createLunRes=" + createLunRes);
            return createLunRes;
        } catch (SDKException e) {
            // TODO Auto-generated catch block
            LOGGER.error("createLun error, SDKException !", e);
            throw FaultUtil.storageFault();
        }
    }

    public StorageQosService getStorageQosService() {
        return storageQosService;
    }

    public void setStorageQosService(StorageQosService storageQosService) {
        this.storageQosService = storageQosService;
    }

    public StorageProfileService getStorageProfileService() {
        return storageProfileService;
    }

    public void setStorageProfileService(StorageProfileService storageProfileService) {
        this.storageProfileService = storageProfileService;
    }


    @Override
    public void addLunToQos(String qosId, String lunId) throws StorageFault, SDKException {
        // TODO Auto-generated method stub
        //将卷加入到Qos组里面
        String arrayId = VASAUtil.getArrayId();
        IDeviceQosService deviceQosService =
                deviceManager.getDeviceServiceProxy(arrayId,
                        IDeviceQosService.class);
        SDKResult<QosCreateResBean> searchQosRes = deviceQosService.queryQos(arrayId, qosId);
        checkError(searchQosRes);
        QosCreateResBean resData = searchQosRes.getResult();
        String lunList = resData.getLUNLIST();
        List<String> asList = new ArrayList<String>();
        if (null != lunList) {
            JsonParser parser = new JsonParser();
            JsonElement el = parser.parse(lunList);
            while (el.isJsonArray()) {
                JsonArray jsonArray = el.getAsJsonArray();
                Iterator<JsonElement> iterator = jsonArray.iterator();
                if (iterator.hasNext()) {
                    JsonElement next = iterator.next();
                    String lunS = next.getAsString();
                    asList.add(lunS);
                }
            }
        } else {
            asList.add(lunId);
        }
        //更新Qos
        SDKErrorCode updateQos = deviceQosService.updateQos(arrayId, qosId, asList);
        checkError(updateQos);
    }


    private void checkError(SDKResult sdkResult) throws StorageFault {
        if (sdkResult.getErrCode() != 0) {
            throw FaultUtil.storageFault("errMsg=" + sdkResult.getDescription());
        }
    }

    private void checkError(SDKErrorCode sdkResult) throws StorageFault {
        if (sdkResult.getErrCode() != 0) {
            throw FaultUtil.storageFault("errMsg=" + sdkResult.getDescription());
        }
    }

    @Override
    public void delLunToQos(String qosId, String lunId) throws StorageFault, SDKException {
        // TODO Auto-generated method stub
        //将卷加入到Qos组里面
        String arrayId = VASAUtil.getArrayId();
        IDeviceQosService deviceQosService =
                deviceManager.getDeviceServiceProxy(arrayId,
                        IDeviceQosService.class);
        SDKResult<QosCreateResBean> searchQosRes = deviceQosService.queryQos(arrayId, qosId);
        checkError(searchQosRes);
        QosCreateResBean resData = searchQosRes.getResult();
        String lunList = resData.getLUNLIST();
        List<String> lastLunList = new ArrayList<>();
        boolean checkLunInQos = false;
        if (null != lunList) {
            JsonParser parser = new JsonParser();
            JsonElement el = parser.parse(lunList);
            if (el.isJsonArray()) {
                JsonArray jsonArray = el.getAsJsonArray();
                Iterator<JsonElement> iterator = jsonArray.iterator();
                while (iterator.hasNext()) {
                    JsonElement next = iterator.next();
                    String lunS = next.getAsString();
                    LOGGER.debug("lunS = " + lunS);
                    if (lunS.equalsIgnoreCase(lunId)) {
                        checkLunInQos = true;
                        continue;
                    }
                    lastLunList.add(lunS);
                }
            }
        }
        if (checkLunInQos) {
            LOGGER.debug("delLunToQos lastLunList = " + lastLunList);
            //更新Qos
            SDKErrorCode updateQos = deviceQosService.updateQos(arrayId, qosId, lastLunList);
            checkError(updateQos);
        } else {
            LOGGER.info("the qos is not contain the lun! lunId=" + lunId + ",rawQosId=" + qosId);
        }
    }


    @Override
    public SDKResult<LunCreateResBean> createVolumeFromSrcVolume(String name, String description, String parentId,
                                                                 int sizeInGB, long sizeInMB, String lunId,
                                                                 String vmName, String createVvolId, Map<String, String> taskProperties, String allocType, String dataTransferPolicy, int ioProperty) throws StorageFault, SDKException {
        // TODO Auto-generated method stub
        String arrayId = VASAUtil.getArrayId();
        final IDeviceLunService deviceLunService =
                deviceManager.getDeviceServiceProxy(arrayId,
                        IDeviceLunService.class);
        IDeviceSnapshotService deviceSnapshotService =
                deviceManager.getDeviceServiceProxy(arrayId,
                        IDeviceSnapshotService.class);
        String createLunId = null;
        String createsnapshotId = null;

        //1、查询LUN信息 用于校验lun是否存在
        SDKResult<LunCreateResBean> lunInfo = deviceLunService.queryLunInfo(arrayId, lunId);
        if (lunInfo.getErrCode() != 0) {
            LOGGER.error("queryLunInfo error,lunId=" + lunId + lunInfo.getDescription());
            if (null == createVvolId) {
                virtualVolumeService.updateStatusByVvolId(createVvolId, VASAArrayUtil.VVOLSTATUS.error_creating);
            }
            throw FaultUtil.storageFault("queryLunInfo error,lunId=" + lunId + lunInfo.getDescription());
        }
        //LunCreateResBean lunInfoBean = lunInfo.getResult();
        //2、创建LUN
        SDKResult<LunCreateResBean> createLun = createLun(name, description, sizeInGB, sizeInMB, vmName, parentId, allocType, dataTransferPolicy, ioProperty);
        if (createLun.getErrCode() != 0) {
            LOGGER.error("createLun error,name=" + name + createLun.getDescription());
//			storagePoolService.incStoragePoolSize(parentId, sizeInMB*MagicNumber.LONG1024*MagicNumber.LONG1024);
            if (null == createVvolId) {
                virtualVolumeService.updateStatusByVvolId(createVvolId, VASAArrayUtil.VVOLSTATUS.error_creating);
            }
            throw FaultUtil.storageFault("createLun error,name=" + name + createLun.getDescription());
        }
        LunCreateResBean lunCreateResBean = createLun.getResult();
        createLunId = lunCreateResBean.getID();
        try {
            //创建LunCopy
            String luncopyName = VASAUtil.buildDisplayName("luncopy");
            SDKResult<LunCopyCreateResBean> createLunCopy = deviceLunService.createLunCopy(arrayId, luncopyName, description, lunId, createLunId);
            if (createLunCopy.getErrCode() != 0) {
                LOGGER.error("createLunCopy error,lunId=" + lunId + createLunCopy.getDescription());
                if (null == createVvolId) {
                    virtualVolumeService.updateStatusByVvolId(createVvolId, VASAArrayUtil.VVOLSTATUS.error_creating);
                }
                throw FaultUtil.storageFault("createLunCopy error,lunId=" + lunId + createLunCopy.getDescription());
            }
            LunCopyCreateResBean copyCreateResBean = createLunCopy.getResult();

            //开始LunCopy
            final String lunCopyId = copyCreateResBean.getID();
            taskProperties.put(NTaskInfo.LUN_COPY_ID, lunCopyId);
            taskProperties.put(NTaskInfo.DET_RAW_ID, createLunId);
            //task.getExtraProperties().put(CloneVvolTask.LUN_COPY_SNAPSHOT_ID, createsnapshotId);
            SDKErrorCode startLunCopy = deviceLunService.startLunCopy(arrayId, lunCopyId);
            if (startLunCopy.getErrCode() != 0) {
                LOGGER.error("startLunCopy error,lunCopyId=" + lunCopyId + startLunCopy.getDescription());
                if (null == createVvolId) {
                    virtualVolumeService.updateStatusByVvolId(createVvolId, VASAArrayUtil.VVOLSTATUS.error_creating);
                }
                throw FaultUtil.storageFault("createLunCopy error,lunCopyId=" + lunCopyId + startLunCopy.getDescription());
            }
        } catch (StorageFault storageFault) {
            //删除创建的lun 与删除创建的snapshot
            SDKErrorCode deleteLun = deleteLun(createLunId);
            checkError(deleteLun);
            throw storageFault;
        }

        return createLun;
    }


    @Override
    public ResBean fastCloneVolumeFromSnapshotVvol(String name, String description, String parentId,
                                                   int sizeInGB, long sizeInMB, String qosId, String snapshotId,
                                                   String vmName) {
        // TODO Auto-generated method stub
        //deviceLunService.getLunInfo(lunId);
		/*final IDeviceLunService deviceLunService = 
				deviceManager.getDeviceServiceProxy(VASAUtil.getArrayId(),
						IDeviceLunService.class);
		IDeviceSnapshotService deviceSnapshotService = 
				deviceManager.getDeviceServiceProxy(VASAUtil.getArrayId(),
						IDeviceSnapshotService.class);
		String createLunId = null;
		String createsnapshotId = null;
		
		//1、查询LUN信息
		SDKResult<SnapshotCreateResBean> querySnapshotInfo = querySnapshotInfo(snapshotId);
		checkError(querySnapshotInfo);
		SnapshotCreateResBean snapshotInfo = querySnapshotInfo.getResult();
		//2、创建LUN
		SDKResult<LunCreateResBean> createLun = createLun(name, description, sizeInGB, sizeInMB, qosId, vmName, parentId, lunInfoBean.getALLOCTYPE(), lunInfoBean.getDATATRANSFERPOLICY());
		checkError(createLun);
		LunCreateResBean lunCreateResBean = createLun.getResult();
		createLunId = lunCreateResBean.getID();
		try{
			//创建LunCopy
			SDKResult<LunCopyCreateResBean> createLunCopy = deviceLunService.createLunCopy(vmName+"_"+System.currentTimeMillis(), description, snapshotId, createLunId);
			checkError(createLunCopy);
			LunCopyCreateResBean copyCreateResBean = createLunCopy.getResult();
			
			//开始LunCopy
			final String lunCopyId = copyCreateResBean.getID();
			task.getExtraProperties().put(CloneVvolTask.LUN_COPY_ID, lunCopyId);
			task.getExtraProperties().put(CloneVvolTask.LUN_COPY_SNAPSHOT_ID, createsnapshotId);
			SDKErrorCode startLunCopy = deviceLunService.startLunCopy(lunCopyId);
			checkError(startLunCopy);
		}catch(StorageFault storageFault){
			//删除创建的lun 与删除创建的snapshot
			SDKErrorCode deleteLun = deleteLun(createLunId);
			checkError(deleteLun);
			if(null != qosId){
				delLunToQos(qosId, createLunId);
			}
			if(null != createsnapshotId){
				SDKErrorCode deleteSnapshot = delVvolLunSnapshot(createsnapshotId);
				checkError(deleteSnapshot);
			}
			throw storageFault;
			
		}
		
		return createLun;*/


        return null;
    }


    @Override
    public SDKResult<QosCreateResBean> getQosInfo(String qosId) throws SDKException, StorageFault {
        // TODO Auto-generated method stub
        String arrayId = VASAUtil.getArrayId();
        IDeviceQosService deviceQosService =
                deviceManager.getDeviceServiceProxy(arrayId,
                        IDeviceQosService.class);
        SDKResult<QosCreateResBean> queryQos = deviceQosService.queryQos(arrayId, qosId);


        return null;
    }


    @Override
    public List<LunCreateResBean> queryLunsInfo(List<String> luns) throws StorageFault, SDKException {
        // TODO Auto-generated method stub
        String arrayId = VASAUtil.getArrayId();
        IDeviceLunService deviceLunService =
                deviceManager.getDeviceServiceProxy(arrayId,
                        IDeviceLunService.class);
        List<LunCreateResBean> result = new ArrayList<>();
        for (String lun : luns) {
            SDKResult<LunCreateResBean> queryLunInfo = deviceLunService.queryLunInfo(arrayId, lun);
            checkError(queryLunInfo);
            result.add(queryLunInfo.getResult());
        }
        return result;
    }


    @Override
    public List<S2DStoragePool> queryPoolsInfo() throws StorageFault, SDKException {
        // TODO Auto-generated method stub

        IStoragePoolCapability storagePoolCapability =
                getDeviceManager().getDeviceServiceProxy(VASAUtil.getArrayId(), IStoragePoolCapability.class);
        SDKResult<List<S2DStoragePool>> result = storagePoolCapability.getAllStoragePool();

        checkError(result);

        return result.getResult();
    }


    @Override
    public SDKErrorCode deleteLun(String lunId) throws StorageFault, SDKException {
        // TODO Auto-generated method stub
        String arrayId = VASAUtil.getArrayId();
        IDeviceLunService deviceLunService =
                deviceManager.getDeviceServiceProxy(arrayId,
                        IDeviceLunService.class);
        return deviceLunService.deleteLun(arrayId, lunId);
    }


    public IDeviceManager getDeviceManager() {
        return deviceManager;
    }


    public void setDeviceManager(IDeviceManager deviceManager) {
        this.deviceManager = deviceManager;
    }


    @Override
    public SDKResult<LunCreateResBean> queryLunInfo(String lunId) throws
            SDKException, StorageFault {
        // TODO Auto-generated method stub
        String arrayId = VASAUtil.getArrayId();
        IDeviceLunService deviceLunService = deviceManager.getDeviceServiceProxy(arrayId, IDeviceLunService.class);
        SDKResult<LunCreateResBean> queryLunInfo = deviceLunService.queryLunInfo(arrayId, lunId);
        return queryLunInfo;
    }


    @Override
    public SDKResult<SnapshotCreateResBean> createSnapshotFromSourceVolume(
            String parentId, String name, String desc) throws SDKException, StorageFault {
        // TODO Auto-generated method stub
        //1、为源lun创建快照
        String arrayId = VASAUtil.getArrayId();
        IDeviceSnapshotService deviceSnapshotService =
                deviceManager.getDeviceServiceProxy(arrayId,
                        IDeviceSnapshotService.class);
        SDKResult<SnapshotCreateResBean> createSnapshot = deviceSnapshotService.createSnapshot(arrayId, name, parentId, desc, "1", "11");
        //checkError(createSnapshot);
        return createSnapshot;
    }


    @Override
    public SDKErrorCode activeVvolLunSnapshot(
            String snapShotId) throws SDKException, StorageFault {
        // TODO Auto-generated method stub
        String arrayId = VASAUtil.getArrayId();
        IDeviceSnapshotService deviceSnapshotService =
                deviceManager.getDeviceServiceProxy(arrayId,
                        IDeviceSnapshotService.class);
        List<String> snapshotlist = new ArrayList<>();
        snapshotlist.add(snapShotId);
        SDKErrorCode activeVvolLunSnapshot = deviceSnapshotService.activeVvolLunSnapshot(arrayId, snapshotlist);
        return activeVvolLunSnapshot;
    }


    @Override
    public SDKErrorCode delVvolLunSnapshot(String snapShotId)
            throws SDKException, StorageFault {
        // TODO Auto-generated method stub
        String arrayId = VASAUtil.getArrayId();
        IDeviceSnapshotService deviceSnapshotService =
                deviceManager.getDeviceServiceProxy(arrayId,
                        IDeviceSnapshotService.class);

        SDKErrorCode deleteSnapshot = deviceSnapshotService.deleteSnapshot(arrayId, snapShotId);
        return deleteSnapshot;
    }


    @Override
    public SDKResult<SnapshotCreateResBean> querySnapshotInfo(String snapShotId)
            throws SDKException, StorageFault {
        // TODO Auto-generated method stub
        String arrayId = VASAUtil.getArrayId();
        IDeviceSnapshotService deviceSnapshotService =
                deviceManager.getDeviceServiceProxy(arrayId,
                        IDeviceSnapshotService.class);

        SDKResult<SnapshotCreateResBean> querySnapshotInfo = deviceSnapshotService.querySnapshotInfo(arrayId, snapShotId);
        return querySnapshotInfo;
    }


    @Override
    public ResBean fastCloneVolumeFromVvol(String name, String description,
                                           String parentId, int sizeInGB, long sizeInMB, String qosId,
                                           String snapshotId, String vmName) {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public SDKResult<SnapshotCreateResBean> fastCloneFromSourceVolume(
            String parentId, String name, String desc, String createVvolId) throws SDKException,
            StorageFault {
        // TODO Auto-generated method stub
        //1、为源lun创建快照
        LOGGER.debug("fasrCloneFromSourceVolume parentId=" + parentId + ",name=" + name + ",desc" + desc);
        String arrayId = VASAUtil.getArrayId();
        IDeviceSnapshotService deviceSnapshotService =
                deviceManager.getDeviceServiceProxy(arrayId,
                        IDeviceSnapshotService.class);
        SDKResult<SnapshotCreateResBean> createSnapshot = deviceSnapshotService.createSnapshot(arrayId, name, parentId, desc, "1", "11");
        if (0 != createSnapshot.getErrCode()) {
            LOGGER.error("fasrCloneFromSourceVolume/createSnapshot error! ErrCode=" + createSnapshot.getErrCode() + ",ErrMsg=" + createSnapshot.getDescription() + ",parentId=" + parentId);
            if (null == createVvolId) {
                virtualVolumeService.updateStatusByVvolId(createVvolId, VASAArrayUtil.VVOLSTATUS.error_creating);
            }
            throw FaultUtil.storageFault("fasrCloneFromSourceVolume/createSnapshot error! ErrCode=" + createSnapshot.getErrCode() + ",ErrMsg=" + createSnapshot.getDescription() + ",parentId=" + parentId);
        }
        SDKErrorCode activeVvolLunSnapshot = activeVvolLunSnapshot(createSnapshot.getResult().getID());
        if (0 != activeVvolLunSnapshot.getErrCode()) {
            LOGGER.error("fasrCloneFromSourceVolume/activeVvolLunSnapshot error! ErrCode=" + activeVvolLunSnapshot.getErrCode() + ",ErrMsg=" + activeVvolLunSnapshot.getDescription());
            if (null == createVvolId) {
                virtualVolumeService.updateStatusByVvolId(createVvolId, VASAArrayUtil.VVOLSTATUS.error_creating);
            }
            SDKErrorCode delVvolLunSnapshot = delVvolLunSnapshot(createSnapshot.getResult().getID());
            if (0 != delVvolLunSnapshot.getErrCode()) {
                LOGGER.error("fasrCloneFromSourceVolume/delVvolLunSnapshot error! ErrCode=" + delVvolLunSnapshot.getErrCode() + ",ErrMsg=" + delVvolLunSnapshot.getDescription());
            }
            throw FaultUtil.storageFault("fasrCloneFromSourceVolume/activeVvolLunSnapshot error! ErrCode=" + activeVvolLunSnapshot.getErrCode() + ",ErrMsg=" + activeVvolLunSnapshot.getDescription());
        }
        return createSnapshot;
    }


    @Override
    public SDKErrorCode expandLun(String rawId, long sizeInMb) throws StorageFault, SDKException {
        // TODO Auto-generated method stub
        IDeviceLunService deviceLunService;
        LOGGER.debug("begin expandLun ! ");
        String arrayId = VASAUtil.getArrayId();
        deviceLunService = deviceManager.getDeviceServiceProxy(arrayId,
                IDeviceLunService.class);
        //首先创建卷
        SDKErrorCode expandLun = deviceLunService.expandLun(arrayId, rawId, sizeInMb);
        LOGGER.debug("end expandLun ! createLunRes=" + expandLun);
        return expandLun;
    }


    @Override
    public SDKErrorCode updateLun(String rawId, int ioProperty, String smartTier)
            throws StorageFault, SDKException {
        // TODO Auto-generated method stub
        IDeviceLunService deviceLunService;
        LOGGER.debug("begin updateLunIOProperty ! ");
        String arrayId = VASAUtil.getArrayId();
        deviceLunService = deviceManager.getDeviceServiceProxy(arrayId,
                IDeviceLunService.class);
        //首先创建卷
        SDKErrorCode errorCode = deviceLunService.updateLun(arrayId, rawId, ioProperty, smartTier);
        LOGGER.debug("end updateLunIOProperty ! result=" + errorCode);
        return errorCode;
    }


    @Override
    public String createLuncopyAndStart(String arrayId,
                                        String description, String lunId, String createLunId) throws StorageFault, SDKException {
        // TODO Auto-generated method stub
        IDeviceLunService deviceLunService;
        deviceLunService = deviceManager.getDeviceServiceProxy(arrayId, IDeviceLunService.class);
        String luncopyName = VASAUtil.buildDisplayName("luncopy");
        SDKResult<LunCopyCreateResBean> createLunCopy = deviceLunService.createLunCopy(arrayId, luncopyName, description, lunId, createLunId);
        if (createLunCopy.getErrCode() != 0) {
            LOGGER.error("createLunCopy error,lunId=" + lunId + createLunCopy.getDescription());
            throw FaultUtil.storageFault("createLunCopy error,lunId=" + lunId + createLunCopy.getDescription());
        }
        LunCopyCreateResBean copyCreateResBean = createLunCopy.getResult();

        //开始LunCopy
        final String lunCopyId = copyCreateResBean.getID();
        //task.getExtraProperties().put(CloneVvolTask.LUN_COPY_SNAPSHOT_ID, createsnapshotId);
        SDKErrorCode startLunCopy = deviceLunService.startLunCopy(arrayId, lunCopyId);
        if (startLunCopy.getErrCode() != 0) {
            LOGGER.error("startLunCopy error,lunCopyId=" + lunCopyId + startLunCopy.getDescription());
            throw FaultUtil.storageFault("createLunCopy error,lunCopyId=" + lunCopyId + startLunCopy.getDescription());
        }
        return lunCopyId;
    }

}
