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

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.opensds.vasa.base.common.VASAArrayUtil;
import org.opensds.vasa.base.common.VasaConstant;
import org.opensds.vasa.base.common.VasaSrcTypeConstant;
import org.opensds.vasa.common.DeviceType;
import org.opensds.vasa.common.MagicNumber;
import org.opensds.vasa.domain.model.VVolModel;
import org.opensds.vasa.domain.model.bean.DArray;
import org.opensds.vasa.domain.model.bean.DArrayFlowControl;
import org.opensds.vasa.domain.model.bean.DArrayInfo;
import org.opensds.vasa.domain.model.bean.DArrayIsLock;
import org.opensds.vasa.domain.model.bean.DFileSystem;
import org.opensds.vasa.domain.model.bean.DLun;
import org.opensds.vasa.domain.model.bean.DPort;
import org.opensds.vasa.domain.model.bean.DProcessor;
import org.opensds.vasa.domain.model.bean.DStorageCapability;
import org.opensds.vasa.domain.model.bean.IsmStoageLun;
import org.opensds.vasa.domain.model.bean.IsmStorageCapability;
import org.opensds.vasa.domain.model.bean.IsmStorageFileSystem;
import org.opensds.vasa.domain.model.bean.IsmStoragePort;
import org.opensds.vasa.domain.model.bean.S2DLun;
import org.opensds.vasa.domain.model.bean.S2DStoragePool;
import org.opensds.vasa.domain.model.bean.S2DVolumeType;
import org.opensds.vasa.domain.model.bean.StoragePolicy;
import org.opensds.platform.common.SDKResult;
import org.opensds.platform.common.config.ConfigManager;
import org.opensds.platform.common.exception.SDKException;
import org.opensds.platform.common.utils.ApplicationContextUtil;
import org.opensds.platform.common.utils.ArrayPwdAES128Util;
import org.opensds.platform.common.utils.StringUtils;
import org.opensds.platform.nemgr.itf.IDeviceManager;
import org.opensds.vasa.vasa.VasaArrayService;
import org.opensds.vasa.vasa.convert.VASAUtilDJConvert;
import org.opensds.vasa.vasa.db.model.NCapabilityMetadata;
import org.opensds.vasa.vasa.db.model.NDefaultProfile;
import org.opensds.vasa.vasa.db.model.NStorageProfile;
import org.opensds.vasa.vasa.db.model.NTaskInfo;
import org.opensds.vasa.vasa.db.model.NVirtualMachine;
import org.opensds.vasa.vasa.db.model.NVirtualVolume;
import org.opensds.vasa.vasa.db.model.NVvolMetadata;
import org.opensds.vasa.vasa.db.model.NVvolProfile;
import org.opensds.vasa.vasa.db.model.StorageInfo;
import org.opensds.vasa.vasa.db.model.VvolPath;
import org.opensds.vasa.vasa.db.service.CapabilityMetadataService;
import org.opensds.vasa.vasa.db.service.DefaultProfileService;
import org.opensds.vasa.vasa.db.service.StorageManagerService;
import org.opensds.vasa.vasa.db.service.StorageProfileService;
import org.opensds.vasa.vasa.db.service.TaskInfoService;
import org.opensds.vasa.vasa.db.service.VasaEventService;
import org.opensds.vasa.vasa.db.service.VirtualMachineService;
import org.opensds.vasa.vasa.db.service.VirtualVolumeService;
import org.opensds.vasa.vasa.db.service.VvolMetadataService;
import org.opensds.vasa.vasa.db.service.VvolPathService;
import org.opensds.vasa.vasa.db.service.VvolProfileService;
import org.opensds.vasa.vasa.internal.Event;
import org.opensds.vasa.vasa.internal.Event.Identifier;
import org.opensds.vasa.vasa.internal.Event.Level;
import org.opensds.vasa.vasa.rest.bean.DeviceTypeMapper;
import org.opensds.vasa.vasa.util.DataUtil;
import org.opensds.vasa.vasa.util.DateUtil;
import org.opensds.vasa.vasa.util.FaultUtil;
import org.opensds.vasa.vasa.util.ListUtil;
import org.opensds.vasa.vasa.util.SessionContext;
import org.opensds.vasa.vasa.util.Util;
import org.opensds.vasa.vasa.util.VASAUtil;
import org.opensds.vasa.vasa20.device.RestQosControl;
import org.opensds.vasa.vasa20.device.array.lun.LunCreateResBean;

import com.vmware.vim.vasa.v20.IncompatibleVolume;
import com.vmware.vim.vasa.v20.InvalidArgument;
import com.vmware.vim.vasa.v20.InvalidProfile;
import com.vmware.vim.vasa.v20.NotCancellable;
import com.vmware.vim.vasa.v20.NotFound;
import com.vmware.vim.vasa.v20.NotImplemented;
import com.vmware.vim.vasa.v20.NotSupported;
import com.vmware.vim.vasa.v20.OutOfResource;
import com.vmware.vim.vasa.v20.ResourceInUse;
import com.vmware.vim.vasa.v20.SnapshotTooMany;
import com.vmware.vim.vasa.v20.StorageFault;
import com.vmware.vim.vasa.v20.VasaProviderBusy;
import com.vmware.vim.vasa.v20.data.policy.capability.provider.xsd.CapabilityMetadataPerCategory;
import com.vmware.vim.vasa.v20.data.policy.capability.provider.xsd.CapabilitySchema;
import com.vmware.vim.vasa.v20.data.policy.capability.provider.xsd.NamespaceInfo;
import com.vmware.vim.vasa.v20.data.policy.capability.provider.xsd.VendorInfo;
import com.vmware.vim.vasa.v20.data.policy.capability.types.xsd.DiscreteSet;
import com.vmware.vim.vasa.v20.data.policy.capability.xsd.CapabilityId;
import com.vmware.vim.vasa.v20.data.policy.capability.xsd.CapabilityInstance;
import com.vmware.vim.vasa.v20.data.policy.capability.xsd.CapabilityMetadata;
import com.vmware.vim.vasa.v20.data.policy.capability.xsd.ConstraintInstance;
import com.vmware.vim.vasa.v20.data.policy.capability.xsd.PropertyInstance;
import com.vmware.vim.vasa.v20.data.policy.capability.xsd.PropertyMetadata;
import com.vmware.vim.vasa.v20.data.policy.compliance.xsd.ComplianceResult;
import com.vmware.vim.vasa.v20.data.policy.compliance.xsd.ComplianceStatusEnum;
import com.vmware.vim.vasa.v20.data.policy.compliance.xsd.ComplianceSubject;
import com.vmware.vim.vasa.v20.data.policy.profile.xsd.CapabilityConstraints;
import com.vmware.vim.vasa.v20.data.policy.profile.xsd.DefaultProfile;
import com.vmware.vim.vasa.v20.data.policy.profile.xsd.StorageProfile;
import com.vmware.vim.vasa.v20.data.policy.profile.xsd.SubProfile;
import com.vmware.vim.vasa.v20.data.policy.xsd.ExtendedElementDescription;
import com.vmware.vim.vasa.v20.data.policy.xsd.ResourceAssociation;
import com.vmware.vim.vasa.v20.data.vvol.xsd.BatchErrorResult;
import com.vmware.vim.vasa.v20.data.vvol.xsd.BatchReturnStatus;
import com.vmware.vim.vasa.v20.data.vvol.xsd.BatchVirtualVolumeHandleResult;
import com.vmware.vim.vasa.v20.data.vvol.xsd.ContainerSpaceStats;
import com.vmware.vim.vasa.v20.data.vvol.xsd.ProtocolEndpoint;
import com.vmware.vim.vasa.v20.data.vvol.xsd.QueryConstraint;
import com.vmware.vim.vasa.v20.data.vvol.xsd.SpaceStats;
import com.vmware.vim.vasa.v20.data.vvol.xsd.TaskInfo;
import com.vmware.vim.vasa.v20.data.vvol.xsd.TaskStateEnum;
import com.vmware.vim.vasa.v20.data.vvol.xsd.VirtualVolumeBitmapResult;
import com.vmware.vim.vasa.v20.data.vvol.xsd.VirtualVolumeHandle;
import com.vmware.vim.vasa.v20.data.vvol.xsd.VirtualVolumeInfo;
import com.vmware.vim.vasa.v20.data.vvol.xsd.VirtualVolumeUnsharedChunksResult;
import com.vmware.vim.vasa.v20.data.xsd.BaseStorageEntity;
import com.vmware.vim.vasa.v20.data.xsd.EntityTypeEnum;
import com.vmware.vim.vasa.v20.data.xsd.EventConfigTypeEnum;
import com.vmware.vim.vasa.v20.data.xsd.EventTypeEnum;
import com.vmware.vim.vasa.v20.data.xsd.HostInitiatorInfo;
import com.vmware.vim.vasa.v20.data.xsd.MountInfo;
import com.vmware.vim.vasa.v20.data.xsd.NameValuePair;
import com.vmware.vim.vasa.v20.data.xsd.StorageContainer;
import com.vmware.vim.vasa.v20.data.xsd.StorageEvent;
import com.vmware.vim.vasa.v20.data.xsd.UsageContext;
import com.vmware.vim.vasa.v20.data.xsd.VasaAssociationObject;

public class DiscoverService {

    private static DiscoverService instance = null;

    private static org.apache.logging.log4j.Logger LOGGER = org.apache.logging.log4j.LogManager
            .getLogger(DiscoverService.class);

    private DataUtil dataManager = DataUtil.getInstance();

    protected IDeviceManager deviceManager = (IDeviceManager) ApplicationContextUtil.getBean("deviceManager");

    private DiscoverServiceImpl discoverServiceImpl = DiscoverServiceImpl.getInstance();

    // private FusionStorageInfoImpl fusionStorageInfoImpl = FusionStorageInfoImpl.getInstance();

    private CapabilityMetadataService capabilityMetadataService = (CapabilityMetadataService) ApplicationContextUtil.getBean("capabilityMetadataService");

    private DefaultProfileService defaultProfileService = (DefaultProfileService) ApplicationContextUtil.getBean("defaultProfileService");

    private VirtualVolumeService virtualVolumeService = (VirtualVolumeService) ApplicationContextUtil.getBean("virtualVolumeService");

    private VvolMetadataService vvolMetadataService = (VvolMetadataService) ApplicationContextUtil.getBean("vvolMetadataService");

    private VvolProfileService vvolProfileService = (VvolProfileService) ApplicationContextUtil.getBean("vvolProfileService");

    private VasaEventService vasaEventService = (VasaEventService) ApplicationContextUtil.getBean("vasaEventService");

    private StorageManagerService storageManagerService = (StorageManagerService) ApplicationContextUtil.getBean("storageManagerService");

    private StorageProfileService storageProfileService = (StorageProfileService) ApplicationContextUtil.getBean("storageProfileService");

    private VasaArrayService vasaArrayService = (VasaArrayService) ApplicationContextUtil.getBean("vasaArrayService");

    private VirtualMachineService virtualMachineService = (VirtualMachineService) ApplicationContextUtil.getBean("virtualMachineService");

    private TaskInfoService taskInfoService = ApplicationContextUtil.getBean("taskInfoService");

    private VvolPathService vvolPathDBService = (VvolPathService) ApplicationContextUtil
            .getBean("vvolPathService");
    private VVolModel vvolModel = new VVolModel();

    private static Object lock = new Object();

    public CapabilityMetadataService getCapabilityMetadataService() {
        return capabilityMetadataService;
    }

    public void setCapabilityMetadataService(
            CapabilityMetadataService capabilityMetadataService) {
        this.capabilityMetadataService = capabilityMetadataService;
    }

    public DefaultProfileService getDefaultProfileService() {
        return defaultProfileService;
    }

    public void setDefaultProfileService(DefaultProfileService defaultProfileService) {
        this.defaultProfileService = defaultProfileService;
    }

    public VirtualVolumeService getVirtualVolumeService() {
        return virtualVolumeService;
    }

    public void setVirtualVolumeService(VirtualVolumeService virtualVolumeService) {
        this.virtualVolumeService = virtualVolumeService;
    }

    public VvolMetadataService getVvolMetadataService() {
        return vvolMetadataService;
    }

    public void setVvolMetadataService(VvolMetadataService vvolMetadataService) {
        this.vvolMetadataService = vvolMetadataService;
    }

    public VvolProfileService getVvolProfileService() {
        return vvolProfileService;
    }

    public void setVvolProfileService(VvolProfileService vvolProfileService) {
        this.vvolProfileService = vvolProfileService;
    }

    private DiscoverService() {

    }

    /**
     * 单列
     *
     * @return DiscoverManager 返回结果
     */
    public static DiscoverService getInstance() {
        if (null == instance) {
            instance = new DiscoverService();
        }

        return instance;
    }

    public void init() {

    }

    /**
     * <获取DArray>
     *
     * @return List<DArray> [返回类型说明]
     * @throws StorageFault if has error
     */
    public List<DArray> queryStorageArrays() throws StorageFault {
        List<StorageInfo> arrs = storageManagerService.queryInfo();
        List<DArray> arrays = new ArrayList<DArray>(0);
        int countOfFailedDevice = 0;
        int countOfAllDevice = 0;
        Set<String> arrIds = new HashSet<String>();
        DArray storageArray = null;
        for (StorageInfo arr : arrs) {
            LOGGER.info("queryStorageArrays arr: " + arr);
            if (1 == arr.getDeleted()) {
                continue;
            }
            if (arr.getDevicestatus().equalsIgnoreCase("ONLINE")) {
                arrIds.add(arr.getId());
            }
            try {
                countOfAllDevice++;
                //****
                if (1 == arr.getDeviceType()) {
                    //storageArray = fusionStorageInfoImpl.getFSArrayFromOpenAPI(arr.getId());
                    LOGGER.info("queryStorageArrays FS The arrayId = " + arr.getId() + "  the storageArray : " + storageArray.toString());
                } else {
                    storageArray = discoverServiceImpl.getStorageArrayByArrayID(arr.getId());
                    LOGGER.info("The arrayId = " + arr.getId() + "  the storageArray : " + storageArray.toString());
                }
                //****
                if (null != storageArray && storageArray.getUniqueIdentifier() != null) {
                    arrays.add(storageArray);
                    TimeZone timeZone = getDeviceTimeZone(arr.getId());
                    dataManager.setTimeZone(storageArray.getUniqueIdentifier(), timeZone);
                    dataManager.addArray(arr.getId(), storageArray);
                }
                // 修改coverity
                if (null != storageArray && storageArray.getUniqueIdentifier() == null) {
                    countOfFailedDevice++;
                }
            } catch (Exception e) {
                LOGGER.error("queryStorageArrays falied,array id:" + arr.getId(), e);
                countOfFailedDevice++;
            }
        }
        DataUtil.getInstance().setArrayId(arrIds);
        // 所有的设备都失败了
        if (countOfFailedDevice != 0 && countOfFailedDevice >= countOfAllDevice) {
            LOGGER.error("StorageFault/queryStorageArrays failed,all devices failed.");
            throw FaultUtil.storageFault("queryStorageArrays failed,all devices failed.");
        }

        return arrays;
    }

    public void updateVasaEventTable(List<String> currArrays) {
        try {
            List<String> dbArrayIds = vasaEventService.getAllArrayIds();
            if (dbArrayIds == null || dbArrayIds.size() == 0) {
                return;
            }

            for (String dbArray : dbArrayIds) {
                if (!currArrays.contains(dbArray)) {
                    vasaEventService.deleteVasaEventByArrayId(dbArray);
                    LOGGER.info("deleteVasaEventByArrayId success! arrayId:" + dbArray);
                }
            }
        } catch (StorageFault e) {
            LOGGER.error("updateVasaEventTable SQLException.");
        }
    }


    public void addStorageArrayUpdateEvent(List<String> arrayIds) {
        //产生阵列添加删除事件,返回给所有订阅了Config事件的vcenter server
        LOGGER.info("In addStorageArrayUpdateEvent function.");
        List<String> ucUUIDs = VASAUtil.getConfigEventUcUUIDs();
        appendConfigStorageArrayUpdateEvent(ucUUIDs, arrayIds);
    }

    public void appendConfigStorageArrayEvent(List<String> ucUUIDs, List<String> cachedArrays, List<String> currArrays) {
        if (ucUUIDs == null || ucUUIDs.size() == 0) {
            LOGGER.warn("ucUUIDs isnull or empty.");
            return;
        }

        LOGGER.info("cachedArrays:" + VASAUtil.convertArrayToStr(cachedArrays));
        LOGGER.info("currArrays:" + VASAUtil.convertArrayToStr(currArrays));

        List<StorageEvent> storageEvent = new ArrayList<StorageEvent>();

        storageEvent.addAll(generateStorageArrayAddEvent(cachedArrays, currArrays));
        storageEvent.addAll(generateStorageArrayDeleteEvent(cachedArrays, currArrays));

        for (String ucUUID : ucUUIDs) {
            EventService.getInstance().appendStorageEvent(ucUUID, storageEvent);
        }
    }

    public void appendConfigStorageArrayUpdateEvent(List<String> ucUUIDs, List<String> arrayIds) {
        LOGGER.info("In appendConfigStorageArrayUpdateEvent funciton, the ucUUIDs" + ucUUIDs.toString());
        if (ucUUIDs == null || ucUUIDs.size() == 0) {
            LOGGER.warn("ucUUIDs isnull or empty.");
            return;
        }
        List<StorageEvent> storageEvent = new ArrayList<StorageEvent>();
        storageEvent.addAll(generateStorageArrayUpdateEvent(arrayIds));
        for (String ucUUID : ucUUIDs) {
            EventService.getInstance().appendConfigStorageEvent(ucUUID, storageEvent);
        }
    }

    public List<StorageEvent> generateStorageArrayUpdateEvent(List<String> arrayIds) {
        LOGGER.info("Start generate storage array update event, the arrayIds = " + arrayIds.toString());
        if (arrayIds == null || arrayIds.size() == 0) {
            LOGGER.error("generateStorageArrayUpdateEvent fail, the arrayIds is empty.");
            return new ArrayList<StorageEvent>();
        }
        return converStorageArrayUpdateEvent(arrayIds);
    }

    public List<StorageEvent> generateStorageArrayAddEvent(List<String> cachedArrays, List<String> currArrays) {
        if (null == currArrays || currArrays.size() == 0) {
            return new ArrayList<StorageEvent>();
        }

        List<String> newArrays = new ArrayList<String>();

        for (String currArray : currArrays) {
            Boolean isFound = false;
            for (String cachedArray : cachedArrays) {
                if (currArray.equals(cachedArray)) {
                    isFound = true;
                    break;
                }
            }

            if (!isFound) {
                newArrays.add(currArray);
            }
        }

        if (newArrays.size() == 0) {
            return new ArrayList<StorageEvent>();
        }

        //将StorageArray 添加事件放入缓存
        return convertStorageArrayAddEvent(newArrays);
    }

    public List<StorageEvent> generateStorageArrayDeleteEvent(List<String> cachedArrays, List<String> currArrays) {
        if (null == currArrays || currArrays.size() == 0) {
            //缓存的StorageArray都被删除了
            return convertStorageArrayDeleteEvent(cachedArrays);
        }

        List<String> deletedArrays = new ArrayList<String>();

        for (String cachedArray : cachedArrays) {
            Boolean isFound = false;
            for (String currArray : currArrays) {
                if (cachedArray.equals(currArray)) {
                    isFound = true;
                    break;
                }
            }

            if (!isFound) {
                deletedArrays.add(cachedArray);
            }
        }

        if (deletedArrays.size() == 0) {
            return new ArrayList<StorageEvent>();
        }

        //将StorageArray 删除事件放入缓存
        return convertStorageArrayDeleteEvent(deletedArrays);
    }

    private List<StorageEvent> converStorageArrayUpdateEvent(List<String> arrayIds) {
        List<StorageEvent> returnEvents = new ArrayList<StorageEvent>();
        for (String arrayId : arrayIds) {
            LOGGER.info("convertStorageArrayUpdateEvent StorageArray identifier:" + VASAUtil.getStorageArrayUUID(arrayId)
                    + " the event is storage array config update event.");
            //生成Storage Array update事件
            StorageEvent event = new StorageEvent();
            event.setEventType(EventTypeEnum.CONFIG.value());
            event.setEventObjType(EntityTypeEnum.STORAGE_ARRAY.value());
            event.setEventConfigType(EventConfigTypeEnum.UPDATE.value());
            event.setObjectId(VASAUtil.getStorageArrayUUID(arrayId));
            Calendar cal = Calendar.getInstance();
            cal.setTime(DateUtil.getUTCDate());
            event.setEventTimeStamp(cal);
            returnEvents.add(event);
        }
        return returnEvents;
    }

    private List<StorageEvent> convertStorageArrayAddEvent(List<String> newArrays) {
        List<StorageEvent> returnEvents = new ArrayList<StorageEvent>();
        for (String newArray : newArrays) {
            LOGGER.info("convertStorageArrayAddEvent StorageArray identifier:" + VASAUtil.getStorageArrayUUID(newArray));

            //生成StorageArray添加事件
            StorageEvent event = new StorageEvent();
            event.setEventType(EventTypeEnum.CONFIG.value());
            event.setEventObjType(EntityTypeEnum.STORAGE_ARRAY.value());
            event.setEventConfigType(EventConfigTypeEnum.NEW.value());
            event.setObjectId(VASAUtil.getStorageArrayUUID(newArray));
            Calendar cal = Calendar.getInstance();
            cal.setTime(DateUtil.getUTCDate());
            event.setEventTimeStamp(cal);

            returnEvents.add(event);
        }

        return returnEvents;
    }

    private List<StorageEvent> convertStorageArrayDeleteEvent(List<String> deletedArrays) {
        List<StorageEvent> returnEvents = new ArrayList<StorageEvent>();
        for (String deletedArray : deletedArrays) {
            LOGGER.info("convertStorageArrayDeleteEvent StorageArray identifier:" + VASAUtil.getStorageArrayUUID(deletedArray));

            //生成StorageArray删除事件
            StorageEvent event = new StorageEvent();
            event.setEventType(EventTypeEnum.CONFIG.value());
            event.setEventObjType(EntityTypeEnum.STORAGE_ARRAY.value());
            event.setEventConfigType(EventConfigTypeEnum.DELETE.value());
            event.setObjectId(VASAUtil.getStorageArrayUUID(deletedArray));
            Calendar cal = Calendar.getInstance();
            cal.setTime(DateUtil.getUTCDate());
            event.setEventTimeStamp(cal);
            returnEvents.add(event);
        }

        return returnEvents;
    }

    /**
     * <查询指定的DLUN>
     *
     * @param hostInitiatorIds 方法参数：hostInitiatorIds
     * @param requestedLunIds  方法参数：requestedLunIds
     * @return DLUN[] [返回类型说明]
     * @throws InvalidArgument [参数说明]
     * @throws StorageFault    异常：StorageFault
     */
    public List<DLun> queryStorageLuns(String[] hostInitiatorIds, String[] requestedLunIds) throws InvalidArgument,
            StorageFault {
        List<DLun> returnStorageLuns = new ArrayList<DLun>();
        List<DLun> queryStorageLuns = null;

        try {
            queryStorageLuns = queryStorageLunsForList(hostInitiatorIds);
        } catch (Exception e) {
            LOGGER.error("StorageFault/Query storageLun error.");
            throw FaultUtil.storageFault("Query storageLun error.");
        }

        for (String requestLunId : requestedLunIds) {
            for (DLun storageLun : queryStorageLuns) {
                if (requestLunId.equalsIgnoreCase(storageLun.getUniqueIdentifier())) {
                    returnStorageLuns.add(storageLun);
                }
            }
        }

        return returnStorageLuns;
    }

    /**
     * 查询所有StorageLUN
     *
     * @param hostInitiatorIds 方法参数：hostInitiatorIds
     * @return List<StorageLun> 返回结果
     */
    public List<DLun> queryStorageLunsForList(String[] hostInitiatorIds) {
        List<DLun> storageLuns = new ArrayList<DLun>(0);
        Set<String> arrayIds = dataManager.getArrayId();
        for (String arrayId : arrayIds) {
            try {
                storageLuns.addAll(getStorageLuns(arrayId, hostInitiatorIds));
            } catch (Exception exception) {
                LOGGER.warn("query lun error,array id is " + arrayId);
            }
        }

        // 添加到缓存中
        for (DLun lun : storageLuns) {
            dataManager.addLun(lun.getUniqueIdentifier(), lun);
        }

        return storageLuns;
    }

    /**
     * <获取阵列的DLun>
     *
     * @param context          方法参数：context
     * @param hostInitiatorIds 方法参数：hostInitiatorIds
     * @return List<DLun> [返回类型说明]
     * @throws StorageFault [参数说明]
     * @throws throws       [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    public List<DLun> getStorageLuns(String arrayId, String[] hostInitiatorIds) throws StorageFault {
        List<DLun> storageLuns = null;
        //判断设备类型为Fusionstorage
        if (DeviceTypeMapper.getDeviceType(arrayId).equals(equals(DeviceType.FusionStorage))) {
            //storageLuns = fusionStorageInfoImpl.getStorageLuns(arrayId, hostInitiatorIds);
        } else {

            storageLuns = discoverServiceImpl.getStorageLuns(arrayId, hostInitiatorIds);
        }
        /*
         * try {
         * dataManager.setStorageLuns(VASAUtil.getUcUUID(ContextManagerImpl
         * .getInstance() .getUsageContext()), storageLuns); } catch
         * (InvalidSession e) { LogManager.error( "get UsageContext error."); }
         */

        return storageLuns;
    }

    public List<DFileSystem> queryStorageFileSystems(List<MountInfo> infos) throws InvalidArgument {
        List<DFileSystem> returnStorageFileSystems = queryStorageFileSystemsForList(infos);
        return returnStorageFileSystems;
    }

    public List<DFileSystem> queryStorageFileSystems(List<MountInfo> infos, String[] fsIds) throws InvalidArgument,
            StorageFault {
        List<DFileSystem> returnStorageFileSystems = new ArrayList<DFileSystem>();
        List<DFileSystem> queryStorageFileSystems = null;
        try {
            queryStorageFileSystems = queryStorageFileSystemsForList(infos);
        } catch (Exception e) {
            LOGGER.error("StorageFault/Query queryStorageFileSystems error.");
            throw FaultUtil.storageFault("Query queryStorageFileSystems error.");
        }

        for (String requestfsId : fsIds) {
            for (DFileSystem fileSystem : queryStorageFileSystems) {
                if (requestfsId.equalsIgnoreCase(fileSystem.getUniqueIdentifier())) {
                    returnStorageFileSystems.add(fileSystem);
                }
            }
        }

        return returnStorageFileSystems;
    }

    private List<DFileSystem> queryStorageFileSystemsForList(List<MountInfo> infos) throws InvalidArgument {
        List<DFileSystem> storageFileSystems = null;
        Set<String> arrayIds = dataManager.getArrayId();
        LOGGER.info("arrayIds size:" + arrayIds.size());
        for (String arrayId : arrayIds) {
            LOGGER.info("arrayId:" + arrayId);
            String deviceType = DeviceTypeMapper.getDeviceType(arrayId);
            if (deviceType.startsWith("dorado_productmode")) {
                throw FaultUtil.invalidArgument("Dorado is not support fileSystem");
            }

            //FusionStorage不支持filesystem
            if (deviceType.equals(DeviceType.FusionStorage.toString())) {
                throw FaultUtil.invalidArgument("FusionStorage is not support fileSystem");
            }

            try {
                storageFileSystems = getStorageFileSystems(arrayId, infos);
            } catch (Exception exception) {
                LOGGER.error("query filesystem error,arrayId is " + arrayId + ",exception" + exception);
            }
        }

        if (null != storageFileSystems) {
            // 添加到缓存中
            for (DFileSystem fileSystem : storageFileSystems) {
                dataManager.addFileSystems(fileSystem.getUniqueIdentifier(), fileSystem);
            }
        } else {
            storageFileSystems = new ArrayList<DFileSystem>();
        }

        return storageFileSystems;
    }

    private List<DFileSystem> getStorageFileSystems(String arrayId, List<MountInfo> infos)
            throws StorageFault {
        List<DFileSystem> fileSystems = discoverServiceImpl.getStorageFileSystems(arrayId, infos);

        return fileSystems;
    }

    /**
     * <查询指定的控制器>
     *
     * @param processorIds 方法参数：processorIds
     * @return StorageProcessor[] [返回类型说明]
     * @throws InvalidArgument 异常：InvalidArgument
     * @throws StorageFault    异常：StorageFault
     */
    public List<DProcessor> getStorageProcessorByIds(String[] processorIds) throws InvalidArgument, StorageFault {
        List<String> processorUniqueId = null;
        List<DProcessor> returnValues = new ArrayList<DProcessor>();
        try {
            processorUniqueId = queryUniqueIdentifiersForStorageProcessor();
        } catch (Exception e) {
            LOGGER.error("StorageFault/Query StorageProcessor error.");
            throw FaultUtil.storageFault("Query StorageProcessor error.");
        }

        // 如果id为空，则查询全部的
        if (Util.isEmpty(processorIds)) {
            for (String processorId : processorUniqueId) {
                DProcessor processor = dataManager.getProcesor(processorId);
                if (null != processor) {
                    returnValues.add(processor);
                }
            }

            return returnValues;
        }

        for (String processorId : processorIds) {
            DProcessor processor = dataManager.getProcesor(processorId);
            if (null != processor) {
                returnValues.add(processor);
            }
        }

        return returnValues;
    }

    /**
     * <获取控制器唯一标示>
     *
     * @return List<String> [返回类型说明]
     * @throws StorageFault if has error
     */
    public List<String> queryUniqueIdentifiersForStorageProcessor() throws StorageFault {
        List<String> uniques = new ArrayList<String>(0);
        Set<String> arrayIds = dataManager.getArrayId();
        List<DProcessor> storageProcessors = null;
        int countOfFailedDevice = 0;
        for (String arrayId : arrayIds) {
            try {
                storageProcessors = discoverServiceImpl.getStorageProcessorByArrayID(arrayId);
                for (DProcessor storageProcessor : storageProcessors) {
                    uniques.add(storageProcessor.getUniqueIdentifier());
                    dataManager.addProcesor(storageProcessor.getUniqueIdentifier(), storageProcessor);
                }
            } catch (Exception e) {
                LOGGER.debug("queryUniqueIdentifiersForStorageProcessor failed,array id is " + arrayId, e);
                countOfFailedDevice++;
            }
        }

        // 所有的设备都失败了
        if (countOfFailedDevice == arrayIds.size() && countOfFailedDevice != 0) {
            LOGGER.error("StorageFault/queryUniqueIdentifiersForStorageProcessor failed,all devices failed.");
            throw FaultUtil.storageFault("queryUniqueIdentifiersForStorageProcessor failed,all devices failed.");
        }

        LOGGER.debug("Return storageProcessor size is:" + uniques.size());
        LOGGER.debug("Return storageProcessor ids are:"
                + VASAUtil.convertArrayToStr(uniques.toArray(new String[uniques.size()])));

        return uniques;
    }

    /**
     * <查询StoragePort>
     *
     * @param portIds            方法参数：portIds
     * @param hostInitiatorInfos 方法参数：hostInitiatorInfos
     * @return StoragePort[] [返回类型说明]
     * @throws InvalidArgument 异常：InvalidArgument
     * @throws StorageFault    异常：StorageFault
     */
    public List<DPort> queryStoragePortByPortIds(String[] portIds, HostInitiatorInfo[] hostInitiatorInfos)
            throws InvalidArgument, StorageFault {
        List<DPort> returnStoragePorts = new ArrayList<DPort>(0);
        List<String> portUniqueIDs = null;
        try {
            portUniqueIDs = queryUniqueIdentifiersForStoragePort(hostInitiatorInfos);
        } catch (Exception e) {
            LOGGER.error("StorageFault/Query storagePort error.");
            throw FaultUtil.storageFault("Query storagePort error.");
        }

        // 如果查询的StoragePortID为空，则返回所有的
        if (Util.isEmpty(portIds)) {
            for (String portUniqueID : portUniqueIDs) {
                if (dataManager.getPort(portUniqueID) != null) {
                    returnStoragePorts.add(dataManager.getPort(portUniqueID));
                }
            }

            return returnStoragePorts;
        }

        for (String portId : portIds) {
            DPort port = dataManager.getPort(portId);
            if (null != port) {
                returnStoragePorts.add(port);
            }
        }

        return returnStoragePorts;
    }

    /**
     * <获取Port唯一标示>
     *
     * @param hostInitiatorInfos 方法参数：hostInitiatorInfos
     * @return String[] [返回类型说明]
     * @throws StorageFault
     */
    public List<String> queryUniqueIdentifiersForStoragePort(HostInitiatorInfo[] hostInitiatorInfos) throws StorageFault {
        List<String> uniquesList = new ArrayList<String>(0);
        Set<String> uniquesSet = new HashSet<String>();
        Set<String> arrayIds = dataManager.getArrayId();
        List<DPort> ports = null;
        int countOfFailedDevice = 0;
        for (String arrayId : arrayIds) {
            try {
                ports = discoverServiceImpl.getStoragePortsByArrayId(arrayId, hostInitiatorInfos);
                LOGGER.info("the ports is : " + ports.toString());
                for (DPort port : ports) {
                    uniquesSet.add(port.getUniqueIdentifier());
                    dataManager.addPort(port.getUniqueIdentifier(), port);
                }
            } catch (Exception e) {
                LOGGER.error("queryUniqueIdentifiersForStoragePort falied,array id is:" + arrayId, e);
                countOfFailedDevice++;
            }
        }
        // 所有的设备都失败了
        if (countOfFailedDevice == arrayIds.size() && countOfFailedDevice != 0) {
            LOGGER.error("StorageFault/queryUniqueIdentifiersForStoragePort failed,all devices failed.");
            throw FaultUtil.storageFault("queryUniqueIdentifiersForStoragePort failed,all devices failed.");
        }

        LOGGER.debug("Return storagePort size is:" + uniquesSet.size());
        LOGGER.debug("Return storagePort ids are:"
                + VASAUtil.convertArrayToStr(uniquesSet.toArray(new String[uniquesSet.size()])));
        LOGGER.info("The uniques is : " + uniquesSet.toString());
        uniquesList.addAll(uniquesSet);
        return uniquesList;
    }

    public String[] queryUniqueIdentifiersForFileSystems(String arrayUniqueID, List<MountInfo> mountInfos)
            throws StorageFault, NotFound {
        List<String> uniques = new ArrayList<String>(0);
        if (mountInfos.isEmpty() || 0 == mountInfos.size()) {
            LOGGER.info("queryUniqueIdentifiersForFileSystems mountInfos is null");
            return uniques.toArray(new String[uniques.size()]);
        }

        String arrayId = VASAUtil.getArrayID(arrayUniqueID);
        List<DFileSystem> fileSystems;
        if (!StringUtils.isEmpty(arrayId)) {
            try {
                fileSystems = getStorageFileSystems(arrayId, mountInfos);
            } catch (Exception e) {
                LOGGER.error("StorageFault/query fileSystem error, " + arrayUniqueID, e);
                throw FaultUtil.storageFault("query fileSystem error, " + arrayUniqueID, e);
            }
        } else {
            queryUniqueIdentifiersForStorageArray();
            arrayId = VASAUtil.getArrayID(arrayUniqueID);
            if (StringUtils.isEmpty(arrayId)) {
                LOGGER.error("NotFound/array ID " + arrayUniqueID + " not found");
                throw FaultUtil.notFound("array ID " + arrayUniqueID + " not found");
            }
            try {
                fileSystems = getStorageFileSystems(arrayId, mountInfos);
            } catch (Exception e) {
                LOGGER.error("StorageFault/query fileSystem error, " + arrayUniqueID);
                throw FaultUtil.storageFault("query fileSystem error, " + arrayUniqueID);
            }
        }

        for (DFileSystem fileSystem : fileSystems) {
            uniques.add(fileSystem.getUniqueIdentifier());
        }

        return uniques.toArray(new String[uniques.size()]);
    }

    /**
     * <查询Storage Array唯一标示>
     *
     * @return List<String> [返回类型说明]
     * @throws StorageFault if has an error
     */
    public List<String> queryUniqueIdentifiersForStorageArray() throws StorageFault {
        List<String> uniques = new ArrayList<String>(0);
        Set<String> arrayIds = dataManager.getArrayId();
        DArray storageArray = null;
        int countOfFailedDevice = 0;
        for (String arrayId : arrayIds) {
            try {
                //****
                if (DeviceTypeMapper.getDeviceType(arrayId).equals(DeviceType.FusionStorage)) {
                    //storageArray = fusionStorageInfoImpl.getFSArrayFromOpenAPI(arrayId);
                    uniques.add(storageArray.getUniqueIdentifier());
                    LOGGER.info("queryUniqueIdentifiersForStorageArray fs from openApi: " + storageArray);

                }
                //****
                storageArray = discoverServiceImpl.getStorageArrayByArrayID(arrayId);
                if (null != storageArray && storageArray.getUniqueIdentifier() != null) {
                    uniques.add(storageArray.getUniqueIdentifier());
                    dataManager.addArray(storageArray.getUniqueIdentifier(), storageArray);
                    dataManager.addArray(arrayId, storageArray);
//                    dataManager.addDevice(storageArray.getUniqueIdentifier(), context);
//                    dataManager.addDevice(String.valueOf(VASAUtil.getIpsHashCode(context)), context);
                }

                // 修改coverity
                if (null != storageArray && storageArray.getUniqueIdentifier() == null) {
                    countOfFailedDevice++;
                }
            } catch (Exception e) {
                LOGGER.error("queryUniqueIdentifiersForStorageArray failed,array id is " + arrayId, e);
                countOfFailedDevice++;
            }

        }
        // 所有的设备都失败了
        if (countOfFailedDevice >= arrayIds.size() && countOfFailedDevice != 0) {
            LOGGER.error("StorageFault/queryUniqueIdentifiersForStorageArray failed,all devices failed.");
            throw FaultUtil.storageFault("queryUniqueIdentifiersForStorageArray failed,all devices failed.");
        }

        LOGGER.debug("Return Storage array size is:" + uniques.size());
        LOGGER.debug("Return Storage arary ids is:"
                + VASAUtil.convertArrayToStr(uniques.toArray(new String[uniques.size()])));
        return uniques;
    }

    /**
     * <获取阵列的时区>
     *
     * @param arrayId
     * @return TimeZone [返回类型说明]
     */
    private TimeZone getDeviceTimeZone(String arrayId) {
        try {
            TimeZone timeZone = discoverServiceImpl.queryDeviceTimeZone(arrayId);
            return timeZone;
        } catch (Exception e) {
            LOGGER.error("Get device Timezone error。");
        }

        return TimeZone.getDefault();
    }

    /**
     * <查询LUN的identifer>
     *
     * @param arrayUniqueID    方法参数：arrayUniqueID
     * @param hostInitiatorIds 方法参数：hostInitiatorIds
     * @return List<String> [返回类型说明]
     * @throws NotFound     异常：NotFound
     * @throws StorageFault 异常：StorageFault
     */
    public List<String> queryUniqueIdentifiersForLuns(UsageContext uc, String arrayUniqueID, String[] hostInitiatorIds) throws NotFound,
            StorageFault {
        List<String> uniques = new ArrayList<String>(0);
        if (Util.isEmpty(hostInitiatorIds)) {
            LOGGER.info("queryUniqueIdentifiersForLuns.hostInitiatorIds is null");
            return uniques;
        }

        String arrayId = VASAUtil.getArrayID(arrayUniqueID);
        if (!dataManager.getArrayId().contains(arrayId)) {
            LOGGER.error("NotFound/not existed arrayId:" + arrayId);
            throw FaultUtil.notFound("not existed arrayId:" + arrayId);
        }
        List<DLun> storageLuns;
        if (!StringUtils.isEmpty(arrayId)) {
            try {
                storageLuns = getStorageLuns(arrayId, hostInitiatorIds);
            } catch (Exception e) {
                LOGGER.error("StorageFault/query array lun error, " + arrayUniqueID, e);
                throw FaultUtil.storageFault("query array lun error, " + arrayUniqueID, e);
            }
        } else {
            queryUniqueIdentifiersForStorageArray();
            arrayId = VASAUtil.getArrayID(arrayUniqueID);
            if (StringUtils.isEmpty(arrayId)) {
                LOGGER.error("NotFound/array ID " + arrayUniqueID + " not found");
                throw FaultUtil.notFound("array ID " + arrayUniqueID + " not found");
            }
            try {
                storageLuns = getStorageLuns(arrayId, hostInitiatorIds);
            } catch (Exception e) {
                LOGGER.error("StorageFault/query array lun error, " + arrayUniqueID);
                throw FaultUtil.storageFault("query array lun error, " + arrayUniqueID);
            }
        }

        String usageContextUUID = uc.getVcGuid();
        for (DLun storageLun : storageLuns) {
            uniques.add(storageLun.getUniqueIdentifier());
            //VASA 2.0非vvol认证 , Pos061: Verify Alarm Filtering on Common LUN/Filesystem is visible to both clients
            dataManager.addLunToUsageContext(storageLun, usageContextUUID);
        }

        return uniques;
    }

    /**
     * <获取StorageCapability唯一标示>
     *
     * @return String[] [返回类型说明]
     * @throws StorageFault if has error
     */
    public List<String> queryUniqueIdentifiersForStorageCapability() throws StorageFault {
        List<String> uniques = new ArrayList<String>(0);
        Set<String> arrayIds = dataManager.getArrayId();
        List<DStorageCapability> capabilities = null;
        int countOfFailedDevice = 0;
        for (String arrayId : arrayIds) {
            try {
                capabilities = discoverServiceImpl.getStorageCapabilitiesByArrayID(arrayId);
                for (DStorageCapability capability : capabilities) {
                    // 去除重复的存储能力
                    if (!uniques.contains(capability.getUniqueIdentifier())) {
                        uniques.add(capability.getUniqueIdentifier());
                        dataManager.setCapablity(capability.getUniqueIdentifier(), capability);
                    }
                }

            } catch (Exception e) {
                LOGGER.error("queryUniqueIdentifiersForStorageCapability falied,array id is:" + arrayId, e);
                countOfFailedDevice++;
            }
        }
        // 所有的设备都失败了
        if (countOfFailedDevice == arrayIds.size() && countOfFailedDevice != 0) {
            LOGGER.error("StorageFault/queryUniqueIdentifiersForStorageCapability failed,all devices failed.");
            throw FaultUtil.storageFault("queryUniqueIdentifiersForStorageCapability failed,all devices failed.");
        }

        LOGGER.debug("Return StorageCapability size is:" + uniques.size());
        LOGGER.debug("Return StorageCapability ids is:"
                + VASAUtil.convertArrayToStr(uniques.toArray(new String[uniques.size()])));

        return uniques;
    }

    /**
     * <查询所有capability对象>
     *
     * @param capabilityIds 方法参数：capabilityIds
     * @return StorageCapability[] [返回类型说明]
     * @throws InvalidArgument 异常：InvalidArgument
     * @throws StorageFault    异常：StorageFault
     */
    public List<DStorageCapability> getStorageCapabilityByIds(String[] capabilityIds) throws InvalidArgument, StorageFault {
        List<DStorageCapability> returnValues = new ArrayList<DStorageCapability>(0);
        List<String> storageCapacityIds = null;
        try {
            storageCapacityIds = queryUniqueIdentifiersForStorageCapability();
        } catch (Exception e) {
            LOGGER.error("StorageFault/Query StorageCapablity Error.");
            throw FaultUtil.storageFault("Query StorageCapablity Error.");
        }

        // 为空时，返回所有的capability对象
        if (Util.isEmpty(capabilityIds)) {
            DStorageCapability storageCapacity = null;
            for (String id : storageCapacityIds) {
                storageCapacity = dataManager.getCapablity(id);
                if (null != storageCapacity) {
                    returnValues.add(storageCapacity);
                }
            }

            return returnValues;
        }

        DStorageCapability storageCapacity = null;
        for (String capacityId : capabilityIds) {
            storageCapacity = dataManager.getCapablity(capacityId);
            if (null != storageCapacity) {
                returnValues.add(storageCapacity);
            }

        }

        return returnValues;
    }

    /**
     * <查询所有storageLUN>
     *
     * @param hostInitiatorIds 方法参数：hostInitiatorIds
     * @return StorageLun[] [返回类型说明]
     */
    public List<DLun> queryStorageLuns(String[] hostInitiatorIds) {
        List<DLun> storageLuns = queryStorageLunsForList(hostInitiatorIds);
        return storageLuns;
    }

    /**
     * <查询端口与StorageLUN的对应关系>
     *
     * @param hostInitiatorIds 方法参数：hostInitiatorIds
     * @param portIds          方法参数：portIds
     * @return VasaAssociationObject[] [返回类型说明]
     * @throws InvalidArgument 异常：InvalidArgument
     * @throws StorageFault    异常：StorageFault
     */
    public VasaAssociationObject[] queryAssociatedLunsForPort(HostInitiatorInfo[] hostInitiatorIds, String[] portIds)
            throws InvalidArgument, StorageFault {
        List<VasaAssociationObject> returnValues = new ArrayList<VasaAssociationObject>();
        Set<String> arrayIds = dataManager.getArrayId();
        int countOfFailedDevice = 0;
        for (String arrayId : arrayIds) {
            try {
                Map<String, List<String>> port2luns = discoverServiceImpl.queryAssociatedLunsForPort(arrayId, hostInitiatorIds, portIds);
                LOGGER.info(port2luns.toString());
                convertAssociationLunForPort(returnValues, port2luns, portIds);
            } catch (Exception e) {
                LOGGER.error("queryAssociatedLunsForPort falied,array id is:" + arrayId, e);
                countOfFailedDevice++;
            }
        }
        // 所有的设备都失败了
        if (countOfFailedDevice == arrayIds.size() && countOfFailedDevice != 0) {
            LOGGER.error("StorageFault/queryAssociatedLunsForPort failed,all devices failed.");
            throw FaultUtil.storageFault("queryAssociatedLunsForPort failed,all devices failed.");
        }
        LOGGER.info(returnValues.toString());
        return returnValues.toArray(new VasaAssociationObject[returnValues.size()]);
    }

    /**
     * <功能详细描述>
     *
     * @param returnValues  返回结果
     * @param port2luns     port2luns
     * @param returnPortIds 要求返回的portids
     * @return void [返回类型说明]
     * @throws throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    private void convertAssociationLunForPort(List<VasaAssociationObject> returnValues,
                                              Map<String, List<String>> port2luns, String[] returnPortIds) {
        List<String> returnPortIdsList = null;
        if (returnPortIds != null) {
            returnPortIdsList = Arrays.asList(returnPortIds);
        } else {
            returnPortIdsList = new ArrayList<String>(0);
        }

        Iterator<Map.Entry<String, List<String>>> iter = port2luns.entrySet().iterator();
        Map.Entry<String, List<String>> entry = null;
        while (iter.hasNext()) {
            entry = iter.next();
            String portuid = entry.getKey();
            // 当需要返回的端口id为0 时返回所有的端口映射列表
            if ((!Util.isEmpty(returnPortIds)) && (!returnPortIdsList.contains(portuid))) {
                LOGGER.info("Portuid:" + portuid + " not in queryPortUids"
                        + VASAUtil.convertArrayToStr(returnPortIdsList));
                continue;
            }
            VasaAssociationObject associationObject = new VasaAssociationObject();
            BaseStorageEntity storagePortEntity = new BaseStorageEntity();
            storagePortEntity.setUniqueIdentifier(portuid);

            List<BaseStorageEntity> storageLunEntitys = new ArrayList<BaseStorageEntity>();
            String arrayId = getArrayIdInUniqueIdentifier(portuid);
            for (String lunuid : entry.getValue()) {
                LOGGER.debug("lunid:" + lunuid + ", arrayid:" + arrayId);
                if (arrayId.equalsIgnoreCase(getArrayIdInUniqueIdentifier(lunuid))) {
                    BaseStorageEntity storageLunEntity = new BaseStorageEntity();
                    storageLunEntity.setUniqueIdentifier(lunuid);
                    storageLunEntitys.add(storageLunEntity);
                }
            }
            ListUtil.clearAndAdd(associationObject.getEntityId(), new BaseStorageEntity[]
                    {storagePortEntity});
            ListUtil.clearAndAdd(associationObject.getAssociatedId(),
                    storageLunEntitys.toArray(new BaseStorageEntity[storageLunEntitys.size()]));

            returnValues.add(associationObject);
        }

    }

    /***
     *
     * <从实体uuid中获取阵列的uuid> <功能详细描述>
     *
     * @param uniqueIdentifier
     * @return [参数说明]
     * @return String [返回类型说明]
     * @see [类、类#方法、类#成员]
     */
    private String getArrayIdInUniqueIdentifier(String uniqueIdentifier) {

        return uniqueIdentifier.substring(0, uniqueIdentifier.indexOf(":"));
    }

    /**
     * <根据LUNID查询其存储能力>
     *
     * @param hostInitiatorIds 方法参数：hostInitiatorIds
     * @param lunIds           方法参数：lunIds
     * @return VasaAssociationObject[] [返回类型说明]
     * @throws throws          [违例类型] [违例说明]
     * @throws InvalidArgument 异常：InvalidArgument
     * @see [类、类#方法、类#成员]
     */
    public VasaAssociationObject[] queryAssociatedCapabilityForLunByLunIds(String[] hostInitiatorIds, String[] lunIds)
            throws InvalidArgument {
        List<VasaAssociationObject> associationObjects = new ArrayList<VasaAssociationObject>(0);
        List<DLun> storageLuns = queryStorageLunsForList(hostInitiatorIds);
        Set<DStorageCapability> capabilities = getAllStorageCapabilitys();

        // 如果没有指定查询的Lunid，则返回所有的
        if (Util.isEmpty(lunIds)) {
            for (DLun storageLun : storageLuns) {
                convertLunForCapabilitys(associationObjects, storageLun, capabilities);
            }

            return associationObjects.toArray(new VasaAssociationObject[associationObjects.size()]);
        }

        for (String lunId : lunIds) {
            for (DLun storageLun : storageLuns) {
                if (lunId.equalsIgnoreCase(storageLun.getUniqueIdentifier())) {
                    convertLunForCapabilitys(associationObjects, storageLun, capabilities);
                }
            }
        }

        return associationObjects.toArray(new VasaAssociationObject[associationObjects.size()]);
    }

    // 查询所有的存储能力
    private Set<DStorageCapability> getAllStorageCapabilitys() {
        Set<DStorageCapability> returnValue = new HashSet<DStorageCapability>(0);
        Set<String> arrayIds = dataManager.getArrayId();
        for (String arrayId : arrayIds) {
            returnValue.addAll(discoverServiceImpl.getStorageCapabilitiesByArrayID(arrayId));
        }
        return returnValue;
    }

    /**
     * <功能详细描述>
     *
     * @param associationObjects
     * @param storageLun         [参数说明]
     * @return void [返回类型说明]
     * @throws throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    private void convertLunForCapabilitys(List<VasaAssociationObject> associationObjects, DLun storageLun,
                                          Set<DStorageCapability> capabilities) {
        if (!(storageLun instanceof IsmStoageLun)) {
            return;
        }
        IsmStoageLun lun = (IsmStoageLun) storageLun;
        VasaAssociationObject vasaAssociationObject = null;
        BaseStorageEntity entityId = null;
        BaseStorageEntity associationId = null;
        IsmStorageCapability ismStorageCapability = null;
        int capablityTypeID = 0;
        for (DStorageCapability capability : capabilities) {
            if (!(capability instanceof IsmStorageCapability)) {
                return;
            }
            ismStorageCapability = (IsmStorageCapability) capability;
            capablityTypeID = ismStorageCapability.getStorageCapabilityTypeID();

            if (capablityTypeID == lun.getStorageLunCapabilityID()) {
                vasaAssociationObject = new VasaAssociationObject();
                entityId = new BaseStorageEntity();
                entityId.setUniqueIdentifier(storageLun.getUniqueIdentifier());
                ListUtil.clearAndAdd(vasaAssociationObject.getEntityId(), new BaseStorageEntity[]
                        {entityId});

                associationId = new BaseStorageEntity();
                associationId.setUniqueIdentifier(capability.getUniqueIdentifier());
                ListUtil.clearAndAdd(vasaAssociationObject.getAssociatedId(), new BaseStorageEntity[]
                        {associationId});
                break;
            }
        }

        associationObjects.add(vasaAssociationObject);
    }

    public VasaAssociationObject[] queryAssociatedCapabilityForFileSystem(List<MountInfo> infos, String[] fsIds) throws InvalidArgument {
        List<VasaAssociationObject> associationObjects = new ArrayList<VasaAssociationObject>(0);
        List<DFileSystem> fileSystems = queryStorageFileSystemsForList(infos);
        Set<DStorageCapability> capabilities = getAllStorageCapabilitys();

        if (Util.isEmpty(fsIds)) {
            for (DFileSystem fileSystem : fileSystems) {
                convertFileSystemForCapabilitys(associationObjects, fileSystem, capabilities);
            }

            return associationObjects.toArray(new VasaAssociationObject[associationObjects.size()]);
        }

        for (String fsId : fsIds) {
            for (DFileSystem fileSystem : fileSystems) {
                if (fsId.equalsIgnoreCase(fileSystem.getUniqueIdentifier())) {
                    convertFileSystemForCapabilitys(associationObjects, fileSystem, capabilities);
                }
            }
        }

        return associationObjects.toArray(new VasaAssociationObject[associationObjects.size()]);
    }

    private void convertFileSystemForCapabilitys(List<VasaAssociationObject> associationObjects,
                                                 DFileSystem storageFileSystem, Set<DStorageCapability> capabilities) {
        if (!(storageFileSystem instanceof IsmStorageFileSystem)) {
            return;
        }
        IsmStorageFileSystem fileSystem = (IsmStorageFileSystem) storageFileSystem;
        VasaAssociationObject vasaAssociationObject = null;
        BaseStorageEntity entityId = null;
        BaseStorageEntity associationId = null;
        IsmStorageCapability ismStorageCapability = null;
        int capablityTypeID = 0;
        for (DStorageCapability capability : capabilities) {
            if (!(capability instanceof IsmStorageCapability)) {
                return;
            }
            ismStorageCapability = (IsmStorageCapability) capability;
            capablityTypeID = ismStorageCapability.getStorageCapabilityTypeID();

            if (capablityTypeID == fileSystem.getStorageFileSystemCapabilityID()) {
                vasaAssociationObject = new VasaAssociationObject();
                entityId = new BaseStorageEntity();
                entityId.setUniqueIdentifier(fileSystem.getUniqueIdentifier());
                ListUtil.clearAndAdd(vasaAssociationObject.getEntityId(), new BaseStorageEntity[]
                        {entityId});

                associationId = new BaseStorageEntity();
                associationId.setUniqueIdentifier(capability.getUniqueIdentifier());
                ListUtil.clearAndAdd(vasaAssociationObject.getAssociatedId(), new BaseStorageEntity[]
                        {associationId});
                break;
            }
        }

        associationObjects.add(vasaAssociationObject);
    }


    /**
     * <查询是否支持DRS>
     *
     * @param srcID            方法参数：srcID
     * @param dstID            方法参数：dstID
     * @param hostInitiatorIds 方法参数：hostInitiatorIds
     * @param mii
     * @return boolean [返回类型说明]
     * @throws throws          [违例类型] [违例说明]
     * @throws NotFound        异常：NotFound
     * @throws InvalidArgument
     * @see [类、类#方法、类#成员]
     */
    public boolean queryDRSMigrationCapabilityForPerformance(String srcID, String dstID, String[] hostInitiatorIds,
                                                             List<MountInfo> mii) throws NotFound, InvalidArgument {
        List<DLun> storageLuns = queryStorageLunsForList(hostInitiatorIds);
        DLun srcStorageLun = null;
        DLun dstStorageLun = null;
        for (DLun storageLun : storageLuns) {
            if (storageLun.getUniqueIdentifier().equalsIgnoreCase(srcID)) {
                srcStorageLun = storageLun;
            }

            if (storageLun.getUniqueIdentifier().equalsIgnoreCase(dstID)) {
                dstStorageLun = storageLun;
            }
        }

        DFileSystem srcFileSystem = null;
        DFileSystem dstFileSystem = null;
        try {
            List<DFileSystem> storageFileSystems = queryStorageFileSystemsForList(mii);
            for (DFileSystem storageFileSystem : storageFileSystems) {
                if (storageFileSystem.getUniqueIdentifier().equalsIgnoreCase(srcID)) {
                    srcFileSystem = storageFileSystem;
                }

                if (storageFileSystem.getUniqueIdentifier().equalsIgnoreCase(dstID)) {
                    dstFileSystem = storageFileSystem;
                }
            }
        } catch (InvalidArgument e) {
            // TODO Auto-generated catch block
            LOGGER.warn("query filesystem error,arrayId is not support fileSystem,exception=" + e);
        }
        if ((null == srcStorageLun || null == dstStorageLun) && (null == srcFileSystem || null == dstFileSystem)) {
            LOGGER.error("NotFound/not found.");
            List<DFileSystem> storageFileSystems = queryStorageFileSystemsForList(mii);
            for (DFileSystem storageFileSystem : storageFileSystems) {
                if (storageFileSystem.getUniqueIdentifier().equalsIgnoreCase(srcID)) {
                    srcFileSystem = storageFileSystem;
                }

                if (storageFileSystem.getUniqueIdentifier().equalsIgnoreCase(dstID)) {
                    dstFileSystem = storageFileSystem;
                }
            }
        }
        if ((null == srcStorageLun || null == dstStorageLun) && (null == srcFileSystem || null == dstFileSystem)) {
            LOGGER.error("NotFound/not found.");
            throw FaultUtil.notFound();
        }
        // LogManager.debug("SrcStorageLun drsManagementPermitted is " +
        // srcStorageLun.isDrsManagementPermitted()
        // + ", DstStorageLun drsManagementPermitted is " +
        // dstStorageLun.isDrsManagementPermitted());
        if ((null != srcStorageLun && null != dstStorageLun)) {
            if (!srcStorageLun.isDrsManagementPermitted() || !dstStorageLun.isDrsManagementPermitted()) {
                return false;
            }
        }

        return true;
    }

    /**
     * <根据阵列ID查询关联的控制器>
     *
     * @param arrayIds 方法参数：arrayIds
     * @return VasaAssociationObject[] [返回类型说明]
     * @throws InvalidArgument 异常：InvalidArgument
     * @throws StorageFault    异常：StorageFault
     */
    public VasaAssociationObject[] queryAssociatedProcessorsForArrayByArrayIds(String[] arrayIds)
            throws InvalidArgument, StorageFault {
        List<VasaAssociationObject> associationObjects = new ArrayList<VasaAssociationObject>();
        List<String> arrayUniqueIds = null;
        try {
            arrayUniqueIds = queryUniqueIdentifiersForStorageArray();
        } catch (Exception e) {
            LOGGER.error("StorageFault/Query StorageArray error.");
            throw FaultUtil.storageFault("Query StorageArray error.");
        }

        // 如果arrayID为空，则返回所有的关联对象
        if (Util.isEmpty(arrayIds)) {
            for (String arrayUniqueId : arrayUniqueIds) {
                queryAssociationForArryAndProcessors(associationObjects, arrayUniqueId);
            }

            return associationObjects.toArray(new VasaAssociationObject[associationObjects.size()]);
        }

        for (String requestArrayID : arrayIds) {
            for (String arrayUniqueId : arrayUniqueIds) {
                if (requestArrayID.equalsIgnoreCase(arrayUniqueId)) {
                    queryAssociationForArryAndProcessors(associationObjects, arrayUniqueId);
                }
            }
        }

        return associationObjects.toArray(new VasaAssociationObject[associationObjects.size()]);
    }

    /**
     * <功能详细描述>
     *
     * @param associationObjects
     * @param arrayUniqueId      [参数说明]
     */
    private void queryAssociationForArryAndProcessors(List<VasaAssociationObject> associationObjects,
                                                      String arrayUniqueId) {
        VasaAssociationObject vasaAssociationObject = null;
        BaseStorageEntity storageEntity = null;
        String arrayId = VASAUtil.getArrayID(arrayUniqueId);
        if (!StringUtils.isEmpty(arrayId)) {
            vasaAssociationObject = new VasaAssociationObject();
            List<DProcessor> storageProcessors = discoverServiceImpl.getStorageProcessorByArrayID(arrayId);
            DArray storageArray = dataManager.getArray(arrayUniqueId);

            if (null == storageArray) {
                return;
            }

            // StorageArray
            BaseStorageEntity storageArrayEntity = new BaseStorageEntity();
            storageArrayEntity.setUniqueIdentifier(storageArray.getUniqueIdentifier());

            List<BaseStorageEntity> baseStorageProcessorEntitys = new ArrayList<BaseStorageEntity>();
            for (DProcessor storageProcessor : storageProcessors) {
                // StorageArray，这里是1对N的关系
                // storageProccor
                storageEntity = new BaseStorageEntity();
                storageEntity.setUniqueIdentifier(storageProcessor.getUniqueIdentifier());
                baseStorageProcessorEntitys.add(storageEntity);
            }

            ListUtil.clearAndAdd(vasaAssociationObject.getEntityId(), new BaseStorageEntity[]
                    {storageArrayEntity});
            ListUtil.clearAndAdd(vasaAssociationObject.getAssociatedId(),
                    baseStorageProcessorEntitys.toArray(new BaseStorageEntity[baseStorageProcessorEntitys.size()]));

            associationObjects.add(vasaAssociationObject);
        }
    }

    /**
     * <查询控制器上的port>
     *
     * @param hostInitiators 方法参数：hostInitiators
     * @param processorIds   方法参数：processorIds
     * @return List<VasaAssociationObject> 返回结果
     * @throws InvalidArgument 异常：InvalidArgument
     * @throws StorageFault    异常：StorageFault
     */
    public List<VasaAssociationObject> queryAssociatedPortsForProcessor(HostInitiatorInfo[] hostInitiators,
                                                                        String[] processorIds) throws InvalidArgument, StorageFault {
        List<VasaAssociationObject> returnValues = new ArrayList<VasaAssociationObject>();
        List<DPort> storagePorts = null;
        List<DProcessor> processors = queryAllStorageProcessors();
        try {
            storagePorts = queryStoragePortByPortIds(null, hostInitiators);
        } catch (InvalidArgument e) {
            throw e;
        } catch (StorageFault e) {
            throw e;
        }
        if (!Util.isEmpty(processorIds)) {
            for (String processorID : processorIds) {
                for (DProcessor processor : processors) {
                    if (processorID.equalsIgnoreCase(processor.getUniqueIdentifier())) {
                        convertPortsForProcessor(returnValues, storagePorts, processor);
                    }
                }
            }
        } else {
            for (DProcessor processor : processors) {
                convertPortsForProcessor(returnValues, storagePorts, processor);
            }
        }
        return returnValues;
    }


    /**
     * <查询所有控制器对象>
     *
     * @return List<StorageProcessor> [返回类型说明]
     */
    public List<DProcessor> queryAllStorageProcessors() {
        List<DProcessor> returnValue = new ArrayList<DProcessor>();
        Set<String> arrayIds = dataManager.getArrayId();
        for (String arrayId : arrayIds) {
            List<DProcessor> storageProcessors = discoverServiceImpl.getStorageProcessorByArrayID(arrayId);
            for (DProcessor storageProcessor : storageProcessors) {
                dataManager.addProcesor(storageProcessor.getUniqueIdentifier(), storageProcessor);
            }
            returnValue.addAll(storageProcessors);
        }
        return returnValue;
    }


    /**
     * <功能详细描述>
     *
     * @param returnValues
     * @param storagePorts
     * @param processor    [参数说明]
     * @return void [返回类型说明]
     * @throws throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    private void convertPortsForProcessor(List<VasaAssociationObject> returnValues, List<DPort> storagePorts,
                                          DProcessor processor) {
        VasaAssociationObject associationObject = new VasaAssociationObject();
        BaseStorageEntity processorEntity = new BaseStorageEntity();
        processorEntity.setUniqueIdentifier(processor.getUniqueIdentifier());

        List<BaseStorageEntity> storagePortEntitys = new ArrayList<BaseStorageEntity>();
        for (DPort storagePort : storagePorts) {
            IsmStoragePort ismStoragePort = (IsmStoragePort) storagePort;
            if (ismStoragePort.getProcessorUniquIdentifier().equalsIgnoreCase(processor.getUniqueIdentifier())) {
                BaseStorageEntity storagePortEntity = new BaseStorageEntity();
                storagePortEntity.setUniqueIdentifier(storagePort.getUniqueIdentifier());
                storagePortEntitys.add(storagePortEntity);
            }

        }

        ListUtil.clearAndAdd(associationObject.getEntityId(), new BaseStorageEntity[]
                {processorEntity});
        ListUtil.clearAndAdd(associationObject.getAssociatedId(),
                storagePortEntitys.toArray(new BaseStorageEntity[storagePortEntitys.size()]));
        if (storagePortEntitys.size() != 0) {
            returnValues.add(associationObject);
        }
    }

    /**
     * <查询所有profile对象>
     *
     * @param profileIds 方法参数：profileIds
     * @return List<StorageProfile> [返回类型说明]
     * @throws NotFound     异常：NotFound
     * @throws StorageFault 异常：StorageFault
     */
    public List<StorageProfile> getCapabilityProfileByIds(String[] profileIds) throws NotFound, StorageFault {
        List<StorageProfile> returnValues = new ArrayList<StorageProfile>(0);
        List<StorageProfile> profiles = null;
        try {
            profiles = getAllCapabilityProfile();
        } catch (Exception e) {
            LOGGER.error("StorageFault/getAllCapabilityProfile error.");
            throw FaultUtil.storageFault("getAllCapabilityProfile error.");
        }

        //为空时,返回所有的profile对象
        if (Util.isEmpty(profileIds)) {
            for (StorageProfile profile : profiles) {
                returnValues.add(profile);
            }

            return returnValues;
        }

        for (String profileId : profileIds) {
            for (StorageProfile profile : profiles) {
                if (profileId.equalsIgnoreCase(profile.getProfileId())) {
                    returnValues.add(profile);
                    break;
                }
            }
        }

        if (0 == returnValues.size()) {
            LOGGER.error("NotFound/return profile size is 0.");
            throw FaultUtil.notFound("return profile size is 0.");
        }

        return returnValues;
    }

    /**
     * <查询所有profile对象>
     *
     * @return List<StorageProfile> [返回类型说明]
     * @throws StorageFault 异常：StorageFault
     */
    public List<StorageProfile> getAllCapabilityProfile() throws StorageFault {
        List<StorageProfile> profiles = discoverServiceImpl.getAllCapabilityProfile();
        return profiles;
    }


    /**
     * <查询所有CapabilitySchema对象>
     *
     * @param schemaIds 方法参数：schemaIds
     * @return List<CapabilitySchema> [返回类型说明]
     * @throws NotFound     异常：NotFound
     * @throws StorageFault 异常：StorageFault
     */
    public List<CapabilitySchema> getCapabilityMetadataByIds(String[] schemaIds) throws NotFound, StorageFault {
        List<CapabilitySchema> returnValues = new ArrayList<CapabilitySchema>(0);
        List<CapabilitySchema> capabilities = null;
        try {
            capabilities = getAllCapabilityMetadata();
        } catch (Exception e) {
            LOGGER.error("StorageFault/getAllCapabilityMetadata error.", e);
            throw FaultUtil.storageFault("getAllCapabilityMetadata error.");
        }

        //为空时,返回所有的profile对象
        if (Util.isEmpty(schemaIds)) {
            for (CapabilitySchema capability : capabilities) {
                returnValues.add(capability);
            }

            return returnValues;
        }

        for (String schemaId : schemaIds) {
            Boolean isValid = false;
            for (CapabilitySchema capability : capabilities) {
                if (schemaId.equalsIgnoreCase(capability.getSchemaId())) {
                    returnValues.add(capability);
                    isValid = true;
                    break;
                }
            }

            if (!isValid) {
                LOGGER.error("NotFound/the schemaId is invalid:" + schemaId);
                throw FaultUtil.notFound("the schemaId is invalid:" + schemaId);
            }
        }
        return returnValues;
    }

    /**
     * <查询所有CapabilitySchema对象>
     *
     * @return List<CapabilitySchema> [返回类型说明]
     * @throws StorageFault 异常：StorageFault
     */
    public List<CapabilitySchema> getAllCapabilityMetadata() throws StorageFault {
        List<CapabilitySchema> capabilities = new ArrayList<CapabilitySchema>();

        List<String> categorys = capabilityMetadataService.getAllMetadataCategory();
        //目前categorys里面只有一个值： performance
        for (String category : categorys) {
            CapabilitySchema capaSchema = new CapabilitySchema();
            CapabilityMetadataPerCategory capaMetadataPerCategory1 = new CapabilityMetadataPerCategory();
            String namespace = "org.opensds.vasaprovider";
            List<NCapabilityMetadata> listCapaMetadata = capabilityMetadataService.getCapabilityMetadataByCategory(category);
            for (NCapabilityMetadata result : listCapaMetadata) {
                String capabilityId = result.getCapabilityId();
                namespace = result.getNamespace();
                CapabilityId capaId1 = new CapabilityId();
                capaId1.setId(capabilityId);
                capaId1.setNamespace(result.getNamespace());

                ExtendedElementDescription summary1 = new ExtendedElementDescription();
                summary1.setKey(result.getKey());
                summary1.setLabel(capabilityId);
                summary1.setMessageCatalogKeyPrefix(result.getKey());
                summary1.setSummary(result.getSummary());

                PropertyMetadata proMetadata1 = new PropertyMetadata();
                proMetadata1.setId(capabilityId);
                proMetadata1.setMandatory(result.getMandatory());
                proMetadata1.setSummary(summary1);


                //如果没有setType注册都会有问题
                proMetadata1.setType(VASAUtil.convert2VMwareType(result.getRequirementsTypeHint()));
                if (!StringUtils.isEmpty(result.getDefaultValue())) {
                    proMetadata1.setDefaultValue(result.getDefaultValue());
                }
    			/*if (capabilityId.equalsIgnoreCase("SmartTier") || capabilityId.equalsIgnoreCase("FlowControlPolicy")){
    			    proMetadata1.setDefaultValue(result.getDefaultValue());
    			}*/
                if (!StringUtils.isEmpty(result.getAllowedValues())) {
                    DiscreteSet allowedValue1 = new DiscreteSet();
                    String[] values = result.getAllowedValues().split(",");
                    for (String val : values) {
                        allowedValue1.getValues().add(val);
                    }
                    LOGGER.info("allowedValue: " + allowedValue1.getValues());
                    proMetadata1.getAllowedValue().add(allowedValue1);
                }

                CapabilityMetadata capaMetadata1 = new CapabilityMetadata();
                capaMetadata1.setCapabilityId(capaId1);
                capaMetadata1.getPropertyMetadata().add(proMetadata1);
                capaMetadata1.setSummary(summary1);
                capaMetadataPerCategory1.getCapabilityMetadata().add(capaMetadata1);
            }

            //
            capaMetadataPerCategory1.setCategory(category);
            capaSchema.getCapabilityMetadataPerCategory().add(capaMetadataPerCategory1);
            capaSchema.setNamespaceInfo(getNamespaceInfo(namespace));
            capaSchema.setVendorInfo(getVenderInfo(category));
            capaSchema.setSchemaId(getSchemaId(getCategoryId(category)));
            capabilities.add(capaSchema);
        }
        return capabilities;
    }

    private NamespaceInfo getNamespaceInfo(String namespace) {
        NamespaceInfo namespaceInfo = new NamespaceInfo();
        namespaceInfo.setNamespace(namespace);
        namespaceInfo.setVersion("1.0");
        return namespaceInfo;
    }

    private String getSchemaId(String categoryId) {
        return "org.opensds.storage.policy" + categoryId;
    }

    private String getCategoryId(String category) {
        if (category.equalsIgnoreCase("performance_level")) {
            return ".level";
        } else if (category.equalsIgnoreCase("performance_capability")) {
            return ".capability";
        }
        return "";
    }

    private VendorInfo getVenderInfo(String category) {
        VendorInfo vendorInfo = new VendorInfo();
        ExtendedElementDescription info = new ExtendedElementDescription();
        info.setKey("org.opensds.esdk");
        info.setLabel("VVOL Capabilities");
        info.setMessageCatalogKeyPrefix("org.opensds.esdk");
        info.setSummary("Capabilities");
        vendorInfo.setInfo(info);
        vendorInfo.setVendorUuid("136D90FA-DB20-4AE2-AA34-69D6755BFF6E");
//		}
        return vendorInfo;
    }

    public boolean isCapabilicyEmpty(S2DVolumeType voltype) {
        if (voltype.getExtra_specs().isSmartTierSupport() || voltype.getExtra_specs().isQosSupport()) {
            return false;
        } else {
            return true;
        }
    }

    public List<ResourceAssociation> queryCapabilityProfileForResource(String[] resourceIds) throws StorageFault, NotFound {
        if (resourceIds.length != 0) {
            /*检查是否所有ID都不存在，都不存在抛出notfound异常**/
            if (!VASAUtil.checkResourceIdExist(resourceIds)) {
                LOGGER.error("NotFound/none of the resourcesIds exist:" + VASAUtil.convertArrayToStr(resourceIds));
                throw FaultUtil.notFound("none of the resourcesIds exist:" + VASAUtil.convertArrayToStr(resourceIds));
            }
        }

        List<ResourceAssociation> returnValues = new ArrayList<ResourceAssociation>(0);
        List<StorageContainer> containers = null;
        if (Util.isEmpty(resourceIds)) {
            containers = getAllStorageContainer();
            for (StorageContainer container : containers) {
                //List<S2DVolumeType> volTypes = discoverServiceImpl.getVolumeTypeForContainer(container.getUniqueIdentifier());
                List<NStorageProfile> search = queryProfileByContainerId(container.getUniqueIdentifier());
                for (NStorageProfile storageProfile : search) {
                    ResourceAssociation asso = new ResourceAssociation();
                    asso.setProfileId(storageProfile.getProfileId());
                    asso.setResourceId(container.getUniqueIdentifier());
                    returnValues.add(asso);
                    LOGGER.info("Get a ResourceAssociation. ProfileId: " + asso.getProfileId() + ", ResourceId" + asso.getResourceId());
                }

            }

            return returnValues;
        }

        for (String resourceId : resourceIds) {
            try {
                //List<S2DVolumeType> volTypes = discoverServiceImpl.getVolumeTypeForContainer(resourceId);
                List<NStorageProfile> search = queryProfileByContainerId(resourceId);
                for (NStorageProfile storageProfile : search) {
                    ResourceAssociation asso = new ResourceAssociation();
                    asso.setProfileId(storageProfile.getProfileId());
                    asso.setResourceId(resourceId);
                    returnValues.add(asso);
                    LOGGER.info("Get a ResourceAssociation. ProfileId: " + asso.getProfileId() + ", ResourceId" + asso.getResourceId());
                }
            } catch (Exception e) {
                continue;
            }
        }

        /*如果container存在但是没有volumeType不应该抛出notFound异常，直接返回空的值**/
//    	if(0 == returnValues.size())
//    	{
//    		LOGGER.error("invalid resourcesIds:" + VASAUtil.convertArrayToStr(resourceIds));
//    		throw FaultUtil.notFound("invalid resourcesIds:" + VASAUtil.convertArrayToStr(resourceIds));
//    	}

        return returnValues;
    }

    private List<NStorageProfile> queryProfileByContainerId(String containerId) {
        NStorageProfile t = new NStorageProfile();
        t.setContainerId(containerId);
        t.setDeleted(false);
        List<NStorageProfile> search = storageProfileService.queryOmCreateStorageProfileByContainerId(containerId);
        return search;
    }

    /**
     * <查询所有StorageContainer对象>
     *
     * @param containerIds 方法参数：containerIds
     * @return List<StorageContainer> [返回类型说明]
     * @throws StorageFault 异常：StorageFault
     */
    public List<StorageContainer> queryStorageContainer(String[] containerIds) throws StorageFault {
        List<StorageContainer> returnValues = new ArrayList<StorageContainer>(0);
        List<StorageContainer> containers = null;
        try {
            containers = getAllStorageContainer();
        } catch (Exception e) {
            LOGGER.error("StorageFault/queryStorageContainer error.");
            throw FaultUtil.storageFault("queryStorageContainer error.");
        }

        //为空时,返回所有的container对象
        if (Util.isEmpty(containerIds)) {
            for (StorageContainer container : containers) {
                returnValues.add(container);
            }

            return returnValues;
        }

        for (String containerId : containerIds) {
            for (StorageContainer container : containers) {
                if (containerId.equalsIgnoreCase(container.getUniqueIdentifier())) {
                    returnValues.add(container);
                    break;
                }
            }
        }

        return returnValues;
    }

    /**
     * <查询所有StorageContainer对象>
     *
     * @return List<StorageContainer> [返回类型说明]
     * @throws StorageFault 异常：StorageFault
     */
    public List<StorageContainer> getAllStorageContainer() throws StorageFault {
        List<StorageContainer> containers = discoverServiceImpl.getAllStorageContainer();
        return containers;
    }

    /**
     * <查询StorageContainer>
     *
     * @param arrayId 方法参数：arrayId
     * @return List<String> [返回类型说明]
     * @throws StorageFault 异常：StorageFault
     */
    public List<String> queryStorageContainerForArray(String arrayId) throws StorageFault {
        List<String> containerIds = discoverServiceImpl.getUniqueIdentifiersForStorageContainer(arrayId);
        return containerIds;
    }

    /**
     * <查询DefaultProfile对象>
     *
     * @param containerId 方法参数：containerId
     * @param entityType  方法参数：entityType
     * @return List<DefaultProfile> [返回类型说明]
     * @throws StorageFault 异常：StorageFault
     */
    public List<DefaultProfile> queryDefaultProfileForStorageContainer(String containerId, List<String> entityType) throws StorageFault {
        List<DefaultProfile> returnValues = new ArrayList<DefaultProfile>();

        String createdAt = null;
        try {
            //目前DefaultProfile表只有一条记录，所以参数containerId没有用
            List<NDefaultProfile> listDefaultProfile = defaultProfileService.getAllDefaultProfile();

            DefaultProfile storageProfile = new DefaultProfile();
            String name = listDefaultProfile.get(0).getName();
            String profileId = listDefaultProfile.get(0).getProfileId();
            createdAt = listDefaultProfile.get(0).getCreatedAt();

            CapabilityConstraints constraints = new CapabilityConstraints();
            SubProfile subProfile = new SubProfile();
            subProfile.setName(name);
            constraints.getSubProfiles().add(subProfile);

            Date date = DateUtil.getFormateDate(createdAt, VASAUtil.PATTEN_FORMAT);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            storageProfile.setConstraints(constraints);
            storageProfile.setCreatedBy("Opensds");
            storageProfile.setCreationTime(calendar);//TODO
            storageProfile.setDescription("something about " + name + " profile");
            storageProfile.setGenerationId((long) 0);
            storageProfile.setLastUpdatedBy("Opensds");
            storageProfile.setLastUpdatedTime(calendar);//TODO
            storageProfile.setName(name);
            storageProfile.setProfileId(profileId);

            if (Util.isEmpty(ListUtil.list2ArrayString(entityType))) {
                storageProfile.getEntityType().add("Config");
                storageProfile.getEntityType().add("Data");
                storageProfile.getEntityType().add("Swap");
                storageProfile.getEntityType().add("Memory");
            } else {
                storageProfile.getEntityType().addAll(entityType);
            }
            LOGGER.debug("default storageProfile = " + storageProfile);
            returnValues.add(storageProfile);

            return returnValues;
        } catch (ParseException e) {
            LOGGER.error("StorageFault/parse createdAt time error. createdAt:" + createdAt);
            throw FaultUtil.storageFault("parse createdAt time error. createdAt:" + createdAt);
        }

    }

    /**
     * <查询ComplianceResult对象>
     *
     * @param subjects 方法参数：subjects
     * @return List<ComplianceResult> [返回类型说明]
     * @throws StorageFault 异常：StorageFault
     */
    public List<ComplianceResult> queryComplianceResult(List<ComplianceSubject> subjects) throws NotFound, StorageFault {
        List<ComplianceResult> returnValues = new ArrayList<ComplianceResult>();
//    	LOGGER.info("subjects size; " + subjects.size() + ", subjects: " + subjects);
        for (ComplianceSubject subject : subjects) {
            Long generationId = subject.getGenerationId() == null ? 0 : subject.getGenerationId();
            List<ComplianceResult> results = queryComplianceResult(subject.getProfileId(), generationId, subject.getObjectId());
            returnValues.addAll(results);
        }
        return returnValues;
    }

    public List<ComplianceResult> queryComplianceResult(String profileId, long generationId, List<String> objectIds) throws NotFound, StorageFault {
        if (null == objectIds || objectIds.size() == 0) {
            LOGGER.error("NotFound/specified vvol not found, vvolids is empty");
            throw FaultUtil.notFound("NotFound/specified vvol not found, vvolids is empty");
        }
        List<ComplianceResult> returnValues = new ArrayList<ComplianceResult>();
        for (String objectId : objectIds) {
            ComplianceResult complianceResult = new ComplianceResult();
            LOGGER.debug("queryComplianceResult/getVvolProfileByVvolId objectId=" + objectId);

            //NotFound Thrown if the specified object identifiers are not valid for this provider, or do not exist.
            NVirtualVolume virtualVolumeByVvolId = virtualVolumeService.getVirtualVolumeByVvolId(objectId);
            if (null == virtualVolumeByVvolId) {
                LOGGER.error("NotFound/specified vvol not found, vvolid is:" + objectId);
                throw FaultUtil.notFound("specified vvol not found, vvolid is:" + objectId);
            }

            List<NVvolProfile> listVvolProfile = vvolProfileService.getVvolProfileByVvolId(objectId);
            LOGGER.debug("queryComplianceResult/getVvolProfileByVvolId listVvolProfile=" + listVvolProfile + ",objectId=" + objectId);
            if (listVvolProfile == null || listVvolProfile.size() == 0) {
                //If the profileId or generationId is omitted as input in ComplianceSubject and the VVol has no associated
                //profile, the VASA Provider should return no result
                continue;
            }

            //May be no result if ComplianceSubject is empty and the VVol lacks a profile
			/*if(null == profileId) {
				return returnValues;
			}*/
            //vvol nas authentication begin
//			if(DiscoverServiceImpl.getInstance().isNasVvol(objectId))
//            {
//                LOGGER.info("construct nas complianceResult");
//                ComplianceResult nasComplianceResult = new ComplianceResult();
//                Calendar cal = Calendar.getInstance();
//                int zoneOffset = cal.get(java.util.Calendar.ZONE_OFFSET);
//                int dstOffset = cal.get(java.util.Calendar.DST_OFFSET);
//                cal.add(java.util.Calendar.MILLISECOND,-(zoneOffset + dstOffset));
//                nasComplianceResult.setCheckTime(cal);
//                nasComplianceResult.setObjectId(objectId);
//                nasComplianceResult.setProfileId(profileId);
//				nasComplianceResult.setProfileMismatch(false);
//				nasComplianceResult.setComplianceStatus(ComplianceStatusEnum.COMPLIANT.value());
//                returnValues.add(nasComplianceResult);
//				continue;
//            }
            //vvol nas authentication end
            NVirtualVolume virtualVolume = virtualVolumeByVvolId;


            Calendar cal = Calendar.getInstance();
            int zoneOffset = cal.get(java.util.Calendar.ZONE_OFFSET);
            int dstOffset = cal.get(java.util.Calendar.DST_OFFSET);
            cal.add(java.util.Calendar.MILLISECOND, -(zoneOffset + dstOffset));
            complianceResult.setCheckTime(cal);
            complianceResult.setObjectId(objectId);
            complianceResult.setProfileId(profileId);

            if (profileId == null) {
                complianceResult.setProfileMismatch(false);
                NStorageProfile storageProfile = storageProfileService.getStorageProfileByProfileId(listVvolProfile.get(0).getProfileId());
                complianceResult.setProfileId(storageProfile.getPolicyId());
                complianceResult.setComplianceStatus(ComplianceStatusEnum.COMPLIANT.value());
                returnValues.add(complianceResult);
                continue;
            }

            boolean isProfileValid = true;

            for (NVvolProfile rs : listVvolProfile) {

                NStorageProfile storageProfile = storageProfileService.getStorageProfileByProfileId(rs.getProfileId());
                if (null == storageProfile) {
                    isProfileValid = false;
                    break;
                } else {
                    String sqlProfileId = storageProfile.getPolicyId();
                    long sqlGenerationId = storageProfile.getGenerationId();
                    if (!sqlProfileId.equalsIgnoreCase(profileId) || generationId != sqlGenerationId) {
                        complianceResult.setProfileMismatch(true);
                        isProfileValid = false;
                        break;
                    }
                }
            }
            if (isProfileValid == true) {
                boolean isMatch = VASAUtil.queryComplianceResult(listVvolProfile, virtualVolume.getContainerId());
                complianceResult.setProfileMismatch(false);
                if (isMatch) {
                    complianceResult.setComplianceStatus(ComplianceStatusEnum.COMPLIANT.value());
                } else {
                    complianceResult.setComplianceStatus(ComplianceStatusEnum.NON_COMPLIANT.value());
                }
            } else {
                complianceResult.setComplianceStatus(ComplianceStatusEnum.UNKNOWN.value());
            }


            returnValues.add(complianceResult);
        }
        return returnValues;
    }

    /**
     * 更新虚拟卷的存储策略<br/>
     * 1.如果使用的策略未变，则直接返回成功<br/>
     * 2.如果使用的策略发生了变化：<br/>
     * 校验新策略是否合法，是否支持<br/>
     * 不支持则抛出异常<br/>
     * 支持则删除原有策略/插入新策略<br/>
     *
     * @param vvolId
     * @param newProfile
     * @return
     * @throws NotFound
     * @throws StorageFault
     * @throws NotSupported
     * @throws ResourceInUse
     * @throws InvalidProfile
     */
    public TaskInfo updateStorageProfileForVirtualVolume(String vvolId, StorageProfile newProfile)
            throws NotFound, StorageFault, NotSupported, ResourceInUse, InvalidProfile, OutOfResource {
        if (StringUtils.isEmpty(vvolId)) {
            LOGGER.error("NotFound/not found vvolid: " + vvolId);
            throw FaultUtil.notFound("not found vvolid: " + vvolId);
        }

        // 1.校验是否包含id是null或者""的capability,解决updStorProForVVol.Neg007, 应该把是否支持也校验到位。
        //   校验是否包含id是null或者""的namespace,解决updStorProForVVol.Neg008， 应该把是否支持也校验到位。

        if (!VASAUtil.checkProfileNull(newProfile)) {
            List<CapabilityInstance> capabilities = newProfile.getConstraints()
                    .getSubProfiles().get(0).getCapability();
            for (CapabilityInstance capabilityInstance : capabilities) {
                if (null == capabilityInstance.getCapabilityId()) {
                    LOGGER.error("InvalidProfile/CapabilityId is null");
                    throw FaultUtil.invalidProfile("CapabilityId is null");
                }
                if (StringUtils.isEmpty(capabilityInstance.getCapabilityId()
                        .getId())) {
                    LOGGER.error("InvalidProfile/id is null");
                    throw FaultUtil.invalidProfile("id is null");
                }
                if (StringUtils.isEmpty(capabilityInstance.getCapabilityId()
                        .getNamespace())) {
                    LOGGER.error("InvalidProfile/Namespace is null");
                    throw FaultUtil.invalidProfile("Namespace is null");
                }
            }
        }

        if (!existVirtualVolumeNotIncludeDeleting(vvolId))// 查询vvolid是否存在
        {
            LOGGER.error("NotFound/not found vvolid: " + vvolId);
            throw FaultUtil.notFound("not found vvolid: " + vvolId);
        }

        // 查询策略是否有变化
        List<NVvolProfile> listVvolProfile = vvolProfileService.getVvolProfileByVvolId(vvolId);

        if (listVvolProfile.size() != 0 && listVvolProfile.get(0) != null) {
            String sqlProfileId = listVvolProfile.get(0).getProfileId();
            NStorageProfile storageProfileByProfileId = storageProfileService.getStorageProfileByProfileId(sqlProfileId);
            if (null == storageProfileByProfileId) {
                //throw FaultUtil.notFound("not found storageProfile peofileId=: " + sqlProfileId);

                if (DiscoverServiceImpl.getInstance().isNasVvol(vvolId)) {
                    TaskInfo taskInfo = new TaskInfo();
                    taskInfo.setName("updateStorageProfileForVirtualVolume");
                    taskInfo.setCancelable(false);
                    taskInfo.setCancelled(false);

                    taskInfo.setStartTime(DateUtil.getUTCCalendar());

                    taskInfo.setTaskId("updateStorageProfileForVirtualVolume:"
                            + vvolId);
                    taskInfo.setTaskState(TaskStateEnum.SUCCESS.value());

                    return taskInfo;
                } else {
                    throw FaultUtil.notFound("not found storageProfile peofileId=: " + sqlProfileId);
                }
            }

            String policyId = storageProfileByProfileId.getPolicyId();
            long generationId = storageProfileByProfileId.getGenerationId();

//			long sqlGenerationId = listVvolProfile.get(0).getGenerationId();
            if (policyId.equalsIgnoreCase(newProfile.getProfileId())
                    && generationId == newProfile.getGenerationId()) {
                TaskInfo taskInfo = new TaskInfo();
                taskInfo.setName("updateStorageProfileForVirtualVolume");
                taskInfo.setCancelable(false);
                taskInfo.setCancelled(false);

                taskInfo.setStartTime(DateUtil.getUTCCalendar());

                taskInfo.setTaskId("updateStorageProfileForVirtualVolume:"
                        + vvolId);
                taskInfo.setTaskState(TaskStateEnum.SUCCESS.value());

                return taskInfo;
            }
        }

        List<SubProfile> subProfiles = newProfile.getConstraints().getSubProfiles();
        List<String> metadataIds = capabilityMetadataService.getAllMetadataCapabilityId();
        //check match new profile
        NVirtualVolume vvol = virtualVolumeService.getVirtualVolumeByVvolId(vvolId);
        NVvolProfile profile = vvolProfileService.getThinThickVvolProfile(vvolId);
        String containerId = vvol.getContainerId();
        VASAUtil.saveArrayId(vvol.getArrayId());
        String thinThickString = "";
        if (profile == null || profile.getValue() == null || "".equals(profile.getValue())) {
            thinThickString = VASAUtil.getPreferredThinValue(vvol.getVvolType());
        } else {
            thinThickString = profile.getValue();
        }

        //deal with update policy subprofile is null
        if (subProfiles.size() == 0) {

            SubProfile subProfile = new SubProfile();
            CapabilityInstance capabilityInstance = new CapabilityInstance();
            CapabilityId capabilityId = new CapabilityId();
            capabilityId.setId(VASAUtil.VMW_STD_CAPABILITY);
            capabilityId.setNamespace(VASAUtil.VMW_NAMESPACE);
            capabilityInstance.setCapabilityId(capabilityId);
            ConstraintInstance constraintInstance = new ConstraintInstance();
            PropertyInstance propertyInstance = new PropertyInstance();
            propertyInstance.setId(VASAUtil.VMW_STD_CAPABILITY);
            DiscreteSet discreteSet = new DiscreteSet();
            discreteSet.getValues().add(thinThickString);
            propertyInstance.setValue(discreteSet);
            constraintInstance.getPropertyInstance().add(propertyInstance);
            capabilityInstance.getConstraint().add(constraintInstance);
            subProfile.getCapability().add(capabilityInstance);
            subProfiles.add(subProfile);
        } else {
            /**
             *  修改CodeDEX问题：FORTIFY.Redundant_Null_Check
             *  Modified by wWX315527 2016/11/19
             */
            for (SubProfile subProfile : subProfiles) {
                List<CapabilityInstance> capbility = subProfile.getCapability();
                for (CapabilityInstance capabilityInstance : capbility) {
                    if (VASAUtil.checkCapabilityNull(capabilityInstance)
                            || !VASAUtil.checkVendorSpecificCapaId(capabilityInstance.getCapabilityId().getId(), metadataIds)) {
						/*updStorProForVvol.Pos002 Call	updateStorageProfileForVirtualVolume() for given VVols with a new
	                      profile having single sub profile contains only vendor specific namespace .**/
                        LOGGER.error("InvalidProfile/capability is null or not vendor specific.");
                        throw FaultUtil.invalidProfile("capability is null or not vendor specific.");
                    }
                }
            }
            /**
             *  修改CodeDEX问题：FORTIFY.Redundant_Null_Check
             *  Modified by wWX315527 2016/11/19
             */
        }

        StoragePolicy storagePolicy = VASAUtil.matchContainer(newProfile, containerId, thinThickString, null);
        VASAUtil.reCalculateRealQos(storagePolicy, vvol.getSize());
        VASAUtil.checkQosForArray(vvol.getArrayId(), storagePolicy);
        if (null == storagePolicy) {
            LOGGER.error("InvalidProfile/the new storageProfile:" + newProfile.getProfileId() + " doesn't match container:" + containerId);
            throw FaultUtil.invalidProfile("InvalidProfile/the new storageProfile:" + newProfile.getProfileId() + " doesn't match container:" + containerId);
        }
        try {
            SDKResult<S2DStoragePool> storagePoolsFromDevice = vvolModel.getStoragePoolByPoolId(vvol.getArrayId(), vvol.getRawPoolId());
            S2DStoragePool s2dStoragePool = storagePoolsFromDevice.getResult();
            if (null != storagePolicy.getDiskType()) {
                String diskTypeValue = getDiskTypeFromStoragePool(s2dStoragePool).get("DiskType");
                if (!diskTypeValue.equalsIgnoreCase(storagePolicy.getDiskType())) {
                    LOGGER.info("RawPoolId = " + s2dStoragePool.getID() + " do not match diskType.");
                    throw FaultUtil.invalidProfile("InvalidProfile/the new storageProfile:" + newProfile.getProfileId() + " doesn't match diskType");
                }
            }
            if (null != storagePolicy.getRaidLevel()) {
                String raidLevelValue = getDiskTypeFromStoragePool(s2dStoragePool).get("RaidLevel");
                if (!raidLevelValue.equalsIgnoreCase(storagePolicy.getRaidLevel())) {
                    LOGGER.info("RawPoolId = " + s2dStoragePool.getID() + " do not match raidLevel.");
                    throw FaultUtil.invalidProfile("InvalidProfile/the new storageProfile:" + newProfile.getProfileId() + " doesn't match raidLevel");
                }
            }
        } catch (SDKException e) {
            LOGGER.info("queryStoragePoolByPoolId fail , " + e, e);
        }

        //send request to DJ
        String uniProfileId = UUID.randomUUID().toString();
        String djVvolId = vvolId.substring(vvolId.indexOf('.') + 1);

        LOGGER.info("Retype volume:" + djVvolId + ", new type is:" + storagePolicy);
        discoverServiceImpl.setVolumeRetype(djVvolId, storagePolicy, newProfile, uniProfileId, "never");
        //update vvol profile and metadata
        updateVolumeProfile2Metadata(vvolId, newProfile, uniProfileId, thinThickString);
        TaskInfo taskInfo = new TaskInfo();
        taskInfo.setName("updateStorageProfileForVirtualVolume");
        taskInfo.setCancelable(false);
        taskInfo.setCancelled(false);
        taskInfo.setStartTime(DateUtil.getUTCCalendar());
        taskInfo.setTaskId("updateStorageProfileForVirtualVolume:" + vvolId);
        taskInfo.setTaskState(TaskStateEnum.SUCCESS.value());

        return taskInfo;
    }


    public void updateVolumeProfile2Metadata(String vvolId, StorageProfile newProfile, String uniProfileId, String thinThick) throws StorageFault {
        synchronized (lock) {
            //delete vvol profile
            vvolProfileService.deleteVvolProfileByVvolId(vvolId);

            //add spaceEfficiency
            NVvolProfile vvolProfile = new NVvolProfile();
            vvolProfile.setVvolid(vvolId);
            vvolProfile.setProfileName(newProfile.getName());
            vvolProfile.setProfileId(uniProfileId);
            vvolProfile.setCreatedBy(newProfile.getCreatedBy());
            vvolProfile.setCreationTime(newProfile.getCreationTime().getTime());
            vvolProfile.setGenerationId(newProfile.getGenerationId());
            vvolProfile.setCapability(VASAUtil.VMW_STD_CAPABILITY);
            vvolProfile.setType(4);
            vvolProfile.setValue(thinThick);
            vvolProfileService.addVvolProfile(vvolProfile);

            //add capability
            for (CapabilityInstance capaInstance : newProfile.getConstraints().getSubProfiles().get(0)
                    .getCapability()) {
                PropertyInstance proInstance = capaInstance.getConstraint().get(0).getPropertyInstance().get(0);
                //remove space efficiency
                if (VASAUtil.VMW_STD_CAPABILITY.equalsIgnoreCase(proInstance.getId())) {
                    continue;
                }
                vvolProfile = new NVvolProfile();
                vvolProfile.setVvolid(vvolId);
                vvolProfile.setProfileName(newProfile.getName());
                vvolProfile.setProfileId(uniProfileId);
                vvolProfile.setCreatedBy(newProfile.getCreatedBy());
                vvolProfile.setCreationTime(newProfile.getCreationTime().getTime());
                vvolProfile.setGenerationId(newProfile.getGenerationId());
                vvolProfile.setCapability(proInstance.getId());
                vvolProfile.setType(VASAUtil.getPropertyValueType(proInstance.getValue()));
                vvolProfile.setValue(VASAUtil.convertPropertyValue(proInstance.getValue()));
                vvolProfileService.addVvolProfile(vvolProfile);
            }
            //update metadata of profile id
            List<NVvolMetadata> vmList = vvolMetadataService.getVvolMetadataByVvolId(vvolId);
            for (NVvolMetadata metadata : vmList) {
                if (metadata.getKey().equals("VMW_VvolProfile")) {
                    vvolMetadataService.updateVvolMetadataByVvolIdAndKey(vvolId, "VMW_VvolProfile", newProfile.getProfileId());
                }
            }
        }
    }

    private boolean existVirtualVolume(String vvolId) throws StorageFault {
        // 查询vvol是否存在
        NVirtualVolume vvol = virtualVolumeService.getVirtualVolumeByVvolId(vvolId);

        if (vvol != null) {
            return true;
        }
        return false;
    }

    private boolean existVirtualVolumeNotIncludeDeleting(String vvolId) throws StorageFault {
        // 查询vvol是否存在
        NVirtualVolume vvol = virtualVolumeService.getVirtualVolumeByVvolId(vvolId);

        if (vvol != null && !vvol.getStatus().equalsIgnoreCase("deleting") && !vvol.getStatus().equalsIgnoreCase("error_deleting")) {
            return true;
        }
        return false;
    }

    public TaskInfo createVirtualVolumeWithDefaultProfile(String containerId, String vvolType,
                                                          long sizeInMB, List<NameValuePair> metadata, StorageProfile profile_to_insert) throws StorageFault, NotFound, OutOfResource, InvalidProfile {
        List<DefaultProfile> defaultProfiles = queryDefaultProfileForStorageContainer(containerId, null);
        if (null == defaultProfiles || null == defaultProfiles.get(0)) {
            LOGGER.error("StorageFault/queryDefaultProfileForStorageContainer error.");
            throw FaultUtil.storageFault("queryDefaultProfileForStorageContainer error.");
        }

        StorageProfile defaultStorageProfile = defaultProfiles.get(0);
        String thinValue = VASAUtil.getPreferredThinValue(vvolType);

        setStorageProfile(defaultStorageProfile, thinValue);
        VASAUtil.printVcenterProfile(defaultStorageProfile);

        return createVirtualVolume(containerId, vvolType, sizeInMB, metadata,
                profile_to_insert, defaultStorageProfile, thinValue);
    }

    private TaskInfo createVirtualVolume(String containerId, String vvolType,
                                         long sizeInMB, List<NameValuePair> metadata,
                                         StorageProfile profile_to_insert,
                                         StorageProfile defaultStorageProfile, String thinValue)
            throws NotFound, OutOfResource, StorageFault, InvalidProfile {
        StoragePolicy matchStoragePolicy = VASAUtil.matchContainer(defaultStorageProfile, containerId, profile_to_insert);

        if (null == matchStoragePolicy) {
            LOGGER.error("OutOfResource/the default profile with thin value:" + thinValue + " doesn't match container:" + containerId);
            throw FaultUtil.outOfResource("the default profile with thin value:" + thinValue + " doesn't match container:" + containerId);
        }
        VASAUtil.reCalculateRealQos(matchStoragePolicy, sizeInMB);
        VASAUtil.checkQosForArray(VASAUtil.getArrayId(), matchStoragePolicy);
        return discoverServiceImpl.createVirtualVolume(sizeInMB, matchStoragePolicy, defaultStorageProfile, metadata, containerId,
                vvolType, false, profile_to_insert, thinValue);
    }

    public TaskInfo createVirtualVolumeWithStorageProfile(String containerId, String vvolType, StorageProfile storageProfile,
                                                          long sizeInMB, List<NameValuePair> metadata, Boolean removeThin) throws OutOfResource, NotFound, StorageFault, InvalidProfile {
        String thinValue = VASAUtil.getThinValueFromStorageProfile(storageProfile);
    	/*StorageProfileData profileData = VASAUtil.matchContainer(storageProfile, containerId, null);

    	if(null == profileData)
    	{
    		LOGGER.error("OutOfResource/the storageProfile:" + storageProfile.getProfileId() + " doesn't match container:" + containerId);
    		throw FaultUtil.outOfResource("the storageProfile:" + storageProfile.getProfileId() + " doesn't match container:" + containerId);
    	}


    	return discoverServiceImpl.createVirtualVolume(sizeInMB, profileData, storageProfile, metadata, containerId,
    			vvolType, removeThin, null, thinValue);*/
        return createVirtualVolume(containerId, vvolType, sizeInMB, metadata,
                null, storageProfile, thinValue);
    }

    public TaskInfo cloneVirtualVolumeWithDefaultProfile(String containerId, String vvolType,
                                                         long sizeInMB, List<NameValuePair> metadata, StorageProfile profile_to_insert, NVirtualVolume vvol
    ) throws StorageFault, NotFound, OutOfResource, VasaProviderBusy, ResourceInUse, InvalidArgument, InvalidProfile {
        List<DefaultProfile> defaultProfiles = queryDefaultProfileForStorageContainer(containerId, null);
        if (null == defaultProfiles || null == defaultProfiles.get(0)) {
            LOGGER.error("StorageFault/queryDefaultProfileForStorageContainer error.");
            throw FaultUtil.storageFault();
        }

        StorageProfile storageProfile = defaultProfiles.get(0);
        String thinValue = VASAUtil.getPreferredThinValue(vvolType);

        setStorageProfile(storageProfile, thinValue);

    	/*StorageProfileData profileData = VASAUtil.matchContainer(storageProfile, containerId, profile_to_insert);

    	if(null == profileData)
    	{
    		LOGGER.error("OutOfResource/the default profile with thin value:" + thinValue + " doesn't match container:" + containerId);
    		throw FaultUtil.outOfResource("the default profile with thin value:" + thinValue + " doesn't match container:" + containerId);
    	}

    	return discoverServiceImpl.cloneVirtualVolume(sizeInMB, profileData, storageProfile, metadata, containerId,
    			vvolType, false, profile_to_insert, thinValue, vvol);*/
        return cloneVirtualVolume(containerId, vvolType, storageProfile,
                sizeInMB, metadata, profile_to_insert, false, vvol, thinValue);
    }

    private void setStorageProfile(StorageProfile storageProfile,
                                   String thinValue) {
        CapabilityInstance capability1 = new CapabilityInstance();
        CapabilityId capaId1 = new CapabilityId();
        capaId1.setId(VASAUtil.VMW_STD_CAPABILITY);
        capaId1.setNamespace(VASAUtil.VMW_NAMESPACE);
        ConstraintInstance constraint1 = new ConstraintInstance();
        PropertyInstance propertyInstance1 = new PropertyInstance();
        propertyInstance1.setId(VASAUtil.VMW_STD_CAPABILITY);
        DiscreteSet setValue = new DiscreteSet();
        setValue.getValues().add(thinValue);
        propertyInstance1.setValue(setValue);
        constraint1.getPropertyInstance().add(propertyInstance1);
        capability1.setCapabilityId(capaId1);
        capability1.getConstraint().add(constraint1);

        storageProfile.getConstraints().getSubProfiles().get(0).getCapability().add(capability1);
    }

    public TaskInfo cloneVirtualVolumeWithStorageProfile(String containerId, String vvolType, StorageProfile storageProfile,
                                                         long sizeInMB, List<NameValuePair> metadata, Boolean removeThin, NVirtualVolume vvol)
            throws OutOfResource, NotFound, StorageFault, VasaProviderBusy, ResourceInUse, InvalidArgument, InvalidProfile {
        String thinValue = VASAUtil.getThinValueFromStorageProfile(storageProfile);

        return cloneVirtualVolume(containerId, vvolType, storageProfile,
                sizeInMB, metadata, null, removeThin, vvol, thinValue);
    }

    private TaskInfo cloneVirtualVolume(String containerId, String vvolType,
                                        StorageProfile storageProfile, long sizeInMB,
                                        List<NameValuePair> metadata, StorageProfile profile_to_insert, Boolean removeThin,
                                        NVirtualVolume vvol, String thinValue) throws NotFound,
            OutOfResource, StorageFault, VasaProviderBusy, ResourceInUse,
            InvalidArgument, InvalidProfile {
        StoragePolicy matchStoragePolicy = VASAUtil.matchContainer(storageProfile, containerId, null);

        if (null == matchStoragePolicy) {
            LOGGER.error("OutOfResource/the storageProfile:" + storageProfile.getProfileId() + " doesn't match container:" + containerId);
            throw FaultUtil.outOfResource("the storageProfile:" + storageProfile.getProfileId() + " doesn't match container:" + containerId);
        }
        VASAUtil.reCalculateRealQos(matchStoragePolicy, sizeInMB);
        VASAUtil.checkQosForArray(vvol.getArrayId(), matchStoragePolicy);
        return discoverServiceImpl.cloneVirtualVolume(sizeInMB, matchStoragePolicy, storageProfile, metadata, containerId,
                vvolType, removeThin, profile_to_insert, thinValue, vvol);
    }

    public List<String> queryProtocolEndpointForArray(List<ProtocolEndpoint> listOfPE, String arrayId) throws StorageFault {
        List<String> result = new ArrayList<String>(0);
        List<S2DLun> pELuns = discoverServiceImpl.getPELunsByArrayId(arrayId);
        LOGGER.debug("all peLUN size:" + pELuns.size());
//    	if(null != listOfPE && listOfPE.size() != 0)
//    	{
//    		LOGGER.info("listOfPE is not null or empty!");
//    		for(ProtocolEndpoint pe : listOfPE)
//    		{
//    			String peLunID = pe.getInBandId().getLunId();
//    			String peLunWWN = peLunID.substring(peLunID.indexOf('.') + 1);
//    			for(S2DLun pelun : pELuns)
//    			{
//    				if(peLunWWN.equalsIgnoreCase(pelun.getWWN()))
//    				{
//    					String uuid = arrayId + ":" + EntityTypeEnum.PROTOCOL_ENDPOINT.value() + ":" + pelun.getID();
//    					result.add(uuid);
//    					pe.setUniqueIdentifier(uuid);
//    					pe.getInBandId().setProtocolEndpointType(ProtocolEndpointTypeEnum.SCSI.value());
//    					break;
//    				}
//    			}
//    		}
//    	}
//    	else
//    	{
        for (S2DLun pelun : pELuns) {
            String uuid = arrayId + ":" + EntityTypeEnum.PROTOCOL_ENDPOINT.value() + ":" + pelun.getID();
            result.add(uuid);
        }

//    	}

        return result;
    }

    public List<ProtocolEndpoint> queryProtocolEndpoint(List<String> peIds) throws StorageFault {
        List<ProtocolEndpoint> result = new ArrayList<ProtocolEndpoint>();
        if (null == peIds) {
            return result;
        }

        List<ProtocolEndpoint> pes = queryProtocolEndpoint();

        for (String peId : peIds) {
            if (!VASAUtil.isIdValid(peId, EntityTypeEnum.PROTOCOL_ENDPOINT.value())) {
                result.add(new ProtocolEndpoint());
                continue;
            }

            Boolean isFound = false;
            for (ProtocolEndpoint pelun : pes) {
                if (peId.equalsIgnoreCase(pelun.getUniqueIdentifier())) {
                    isFound = true;
                    result.add(pelun);
                    break;
                }
            }

            if (!isFound) {
                result.add(new ProtocolEndpoint());
            }

        }

        return result;
    }

    public List<ProtocolEndpoint> queryProtocolEndpoint() {
        List<ProtocolEndpoint> pes = new ArrayList<ProtocolEndpoint>();
        Set<String> arrayIds = dataManager.getArrayId();
        for (String arrayId : arrayIds) {
            try {
                List<S2DLun> s2dLuns = discoverServiceImpl.getPELunsByArrayId(arrayId);
                pes.addAll(VASAUtilDJConvert.convert2ProtocolEndpoint(arrayId, s2dLuns));
            } catch (StorageFault e) {
                LOGGER.warn("getPELunsByArrayId error.");
                continue;
            }

        }

        return pes;
    }


    public List<ProtocolEndpoint> queryProtocolEndpointByArrayId(String arrayId) {
        List<ProtocolEndpoint> pes = new ArrayList<ProtocolEndpoint>();
        try {
            List<S2DLun> s2dLuns = discoverServiceImpl.getPELunsByArrayId(arrayId);
            pes.addAll(VASAUtilDJConvert.convert2ProtocolEndpoint(arrayId, s2dLuns));
        } catch (StorageFault e) {
            LOGGER.warn("getPELunsByArrayId error.");
        }
        return pes;
    }

    public void updateInbandPE(List<ProtocolEndpoint> listOfHostPEs) {
        if (null == listOfHostPEs || listOfHostPEs.size() == 0) {
            return;
        }

        LOGGER.info("update inband pe list.");

        List<ProtocolEndpoint> outOfBandPes = queryProtocolEndpoint();
        for (ProtocolEndpoint inbandPe : listOfHostPEs) {
            for (ProtocolEndpoint outOfbandPe : outOfBandPes) {
                if (VASAUtil.isSamePe(inbandPe, outOfbandPe)) {
                    inbandPe.setUniqueIdentifier(outOfbandPe.getUniqueIdentifier());
                    break;
                }
            }
        }
    }

    public List<StorageEvent> generatePEAddEvent(List<ProtocolEndpoint> cachedPes, List<ProtocolEndpoint> listOfHostPEs) {
        if (null == listOfHostPEs || listOfHostPEs.size() == 0) {
            return new ArrayList<StorageEvent>();
        }

        List<ProtocolEndpoint> newPes = new ArrayList<ProtocolEndpoint>();

        if (cachedPes == null || cachedPes.size() == 0) {
            //如果缓存的pe是空的，那么setPeContext传入的都是新添加的PE
            newPes.addAll(listOfHostPEs);
        } else {
            for (ProtocolEndpoint listPe : listOfHostPEs) {
                Boolean isFound = false;
                for (ProtocolEndpoint cachedPe : cachedPes) {
                    if (VASAUtil.isSamePe(listPe, cachedPe)) {
                        isFound = true;
                        break;
                    }
                }

                if (!isFound) {
                    newPes.add(listPe);
                }
            }
        }


        if (newPes.size() == 0) {
            return new ArrayList<StorageEvent>();
        }

        List<ProtocolEndpoint> outOfBandPes = queryProtocolEndpoint();
        //将PE 添加事件放入缓存
        return convertPEAddEvent(newPes, outOfBandPes);
    }

    public List<StorageEvent> generatePEDeleteEvent(List<ProtocolEndpoint> cachedPes, List<ProtocolEndpoint> listOfHostPEs) {
        if (null == cachedPes || cachedPes.size() == 0) {
            return new ArrayList<StorageEvent>();
        }

        if (null == listOfHostPEs || listOfHostPEs.size() == 0) {
            //缓存的PE都被删除了
            return convertPEDeleteEvent(cachedPes);
        }

        List<ProtocolEndpoint> deletedPes = new ArrayList<ProtocolEndpoint>();

        for (ProtocolEndpoint cachedPe : cachedPes) {
            Boolean isFound = false;
            for (ProtocolEndpoint listPe : listOfHostPEs) {
                if (VASAUtil.isSamePe(listPe, cachedPe)) {
                    isFound = true;
                    break;
                }
            }

            if (!isFound) {
                deletedPes.add(cachedPe);
            }
        }

        if (deletedPes.size() == 0) {
            return new ArrayList<StorageEvent>();
        }

        //将PE 删除事件放入缓存
        return convertPEDeleteEvent(deletedPes);
    }

    public String getPeWWN(ProtocolEndpoint pe) {
        if (pe != null && pe.getInBandId() != null) {
            if (pe.getInBandId().getLunId() != null) {
                return pe.getInBandId().getLunId();
            } else if (pe.getInBandId().getIpAddress() != null) {
                return pe.getInBandId().getIpAddress();
            } else {
                return null;
            }
        }

        return null;
    }

    public List<StorageEvent> convertPEAddEvent(List<ProtocolEndpoint> newPes, List<ProtocolEndpoint> outOfBandPes) {
        List<StorageEvent> returnEvents = new ArrayList<StorageEvent>();
        for (ProtocolEndpoint newPe : newPes) {
            for (ProtocolEndpoint outOfBandPe : outOfBandPes) {
                if (VASAUtil.isSamePe(newPe, outOfBandPe)) {
                    LOGGER.info("convertPEAddEvent PE identifier:" + outOfBandPe.getUniqueIdentifier()
                            + ", WWN:" + getPeWWN(outOfBandPe));
                    //将带外PE uuid填到缓存
                    newPe.setUniqueIdentifier(outOfBandPe.getUniqueIdentifier());

                    //生成PE添加事件
                    StorageEvent event = new StorageEvent();
                    event.setEventType(EventTypeEnum.CONFIG_PROTOCOL_ENDPOINT.value());
                    event.setEventObjType(EntityTypeEnum.PROTOCOL_ENDPOINT.value());
                    event.setEventConfigType(EventConfigTypeEnum.NEW.value());
                    event.setObjectId(outOfBandPe.getUniqueIdentifier());
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(DateUtil.getUTCDate());
                    event.setEventTimeStamp(cal);
                    event.setArrayId(((outOfBandPe.getUniqueIdentifier() == null) ? null :
                            VASAUtil.getStorageArrayUUID(outOfBandPe.getUniqueIdentifier().split(":")[0])));

                    returnEvents.add(event);
                }
            }
        }

        return returnEvents;
    }

    private List<StorageEvent> convertPEDeleteEvent(List<ProtocolEndpoint> deletedPes) {
        List<StorageEvent> returnEvents = new ArrayList<StorageEvent>();
        if (deletedPes == null || deletedPes.size() == 0) {
            return returnEvents;
        }

        for (ProtocolEndpoint deletedPe : deletedPes) {
            LOGGER.info("convertPEDeleteEvent PE identifier:" + deletedPe.getUniqueIdentifier()
                    + ", WWN:" + getPeWWN(deletedPe));
            //生成PE删除事件
            StorageEvent event = new StorageEvent();
            event.setEventType(EventTypeEnum.CONFIG_PROTOCOL_ENDPOINT.value());
            event.setEventObjType(EntityTypeEnum.PROTOCOL_ENDPOINT.value());
            event.setEventConfigType(EventConfigTypeEnum.DELETE.value());
            event.setObjectId(deletedPe.getUniqueIdentifier());
            Calendar cal = Calendar.getInstance();
            cal.setTime(DateUtil.getUTCDate());
            event.setEventTimeStamp(cal);
            event.setArrayId(((deletedPe.getUniqueIdentifier() == null) ? null :
                    VASAUtil.getStorageArrayUUID(deletedPe.getUniqueIdentifier().split(":")[0])));

            returnEvents.add(event);
        }

        return returnEvents;
    }

    public List<String> queryVirtualVolume(List<QueryConstraint> constraints) throws StorageFault {
        List<String> matchedVvolIds = new ArrayList<String>(0);
        Map<String, Map<String, String>> comparedItems = getVirtualVolumeMetadata();
        LOGGER.debug("comparedItems =" + comparedItems);
        for (Map.Entry<String, Map<String, String>> entry : comparedItems.entrySet()) {
            Boolean isMatched = true;
            Map<String, String> metadata = entry.getValue();
            LOGGER.debug("compared metadata =" + metadata);
            for (QueryConstraint constraint : constraints) {
                if (constraint.getValue() == null) {
                    if (null == metadata.get(constraint.getKey())) {
                        isMatched = false;
                        break;
                    } else {
                        continue;
                    }
                } else {
                    if (constraint.getValue().equals(metadata.get(constraint.getKey()))) {
                        continue;
                    } else {
                        isMatched = false;
                        break;
                    }
                }

            }

            if (isMatched) {
                matchedVvolIds.add(entry.getKey());
            }
        }

        return matchedVvolIds;
    }

    public Map<String, Map<String, String>> getVirtualVolumeMetadata() throws StorageFault {
        Map<String, Map<String, String>> vvol2metadata = new HashMap<String, Map<String, String>>();
        List<NVvolMetadata> listVvolMetadata = vvolMetadataService.getAllReportVvolMetadata();
        LOGGER.debug("listVvolMetadata = " + listVvolMetadata);
        String vvolid = null;
        String key = null;
        String value = null;
        if (listVvolMetadata != null && listVvolMetadata.size() != 0) {
            for (NVvolMetadata result : listVvolMetadata) {
                vvolid = result.getVvolid();
                key = result.getKey();
                value = result.getValue();

                if (null == vvol2metadata.get(vvolid)) {
                    Map<String, String> pairs = new HashMap<String, String>();
                    vvol2metadata.put(vvolid, pairs);
                }

                vvol2metadata.get(vvolid).put(key, value);
            }
        }

        return vvol2metadata;
    }


    public List<VirtualVolumeInfo> queryVirtualVolumeInfo(List<String> vvolIds) throws StorageFault {
        List<VirtualVolumeInfo> results = new ArrayList<VirtualVolumeInfo>();
        for (String vvolId : vvolIds) {
            VirtualVolumeInfo vvolInfo = queryVirtualVolumeInfo(vvolId);
            if (null == vvolInfo) {
                continue;
            } else {
                results.add(vvolInfo);
            }
        }

        return results;
    }

    public VirtualVolumeInfo queryVirtualVolumeInfo(String vvolId) throws StorageFault {
        VirtualVolumeInfo vvolInfo = new VirtualVolumeInfo();

        NVirtualVolume virtualVolumeByVvolId = virtualVolumeService.getVirtualVolumeByVvolId(vvolId);
        if (null != virtualVolumeByVvolId) {
            String status = virtualVolumeByVvolId.getStatus();
            if (status.equalsIgnoreCase(VASAArrayUtil.VVOLSTATUS.deleting)) {
                return null;
            }
        }
        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
        //从数据库中将vvol的metadata取出
        List<NVvolMetadata> listVvolMetadata = vvolMetadataService.getVvolMetadataByVvolId(vvolId);
        int count = 0;
        String key = null;
        String value = null;
        for (NVvolMetadata result : listVvolMetadata) {
            count++;
            key = result.getKey();
            value = result.getValue();

            NameValuePair pair = new NameValuePair();
            pair.setParameterName(key);
            pair.setParameterValue(value);

            pairs.add(pair);
        }

        if (count <= 0) {
            return null;
        }

        vvolInfo.setUniqueIdentifier(vvolId);
        vvolInfo.setVvolId(vvolId);
        vvolInfo.getMetadata().addAll(pairs);
        return vvolInfo;
    }

    public void deleteVirtualVolume(String vvolId) throws StorageFault, VasaProviderBusy, NotFound, ResourceInUse {
        List<String> vvolIds = new ArrayList<String>();
        vvolIds.add(vvolId);
        List<NVirtualVolume> vvols = queryVirtualVolumeFromDataBaseNotIncludeDeleting(vvolIds);
        if (0 == vvols.size()) {
            //LOGGER.warn("NotFound/not found vvolId:" + vvolId);
            //return;
            throw FaultUtil.notFound("not found vvolId:" + vvolId);
        }
        VASAUtil.saveArrayId(vvols.get(0).getArrayId());
        discoverServiceImpl.deleteVirtualVolume(vvols.get(0), false);
    }

    private long getVVolSizeById(NVirtualVolume vvol) throws StorageFault {
        long newSize = 0;
        try {

            VASAUtil.saveArrayId(vvol.getArrayId());
            SDKResult<LunCreateResBean> queryLunInfo = vasaArrayService.queryLunInfo(vvol.getRawId());
            // SDKResult<S2DVolume> result = new VVolModel().getVolumeById(vvolId.substring(vvolId.indexOf('.') + 1));
            if (0 != queryLunInfo.getErrCode()) {
                LOGGER.error("StorageFault/getVolumeById error. params[" + vvol.getRawId()
                        + "] errCode:" + queryLunInfo.getErrCode() + ", description:" + queryLunInfo.getDescription());
                throw FaultUtil.storageFault("getVolumeById error. vvolId:" + vvol.getVvolid() + ", errCode:"
                        + queryLunInfo.getErrCode() + ", description:" + queryLunInfo.getDescription());
            }

            newSize = Long.valueOf(queryLunInfo.getResult().getCAPACITY()) * MagicNumber.LONG512 / MagicNumber.LONG1024 / MagicNumber.LONG1024;
        } catch (SDKException e) {
            LOGGER.error("StorageFault/getSnapshotById error. " + ", errCode:" + e.getSdkErrCode() + ", message:"
                    + e.getMessage());
            throw FaultUtil.storageFault(
                    "getSnapshotById error. " + ", errCode:" + e.getSdkErrCode() + ", message:" + e.getMessage());
        }

        return newSize;
    }

    public TaskInfo resizeVirtualVolume(String vvolId, long newSize) throws NotSupported, StorageFault, NotFound, OutOfResource {
        //从数据库中将vvol的metadata取出
        List<String> vvolIds = new ArrayList<String>();
        vvolIds.add(vvolId);
        List<NVirtualVolume> vvols = queryVirtualVolumeFromDataBaseNotIncludeDeleting(vvolIds);
        if (0 == vvols.size()) {
            LOGGER.error("NotFound/not found vvolId:" + vvolId);
            throw FaultUtil.notFound("not found vvolId:" + vvolId);
        }

        NVirtualVolume vvol = vvols.get(0);
        long oldSize = vvol.getSize();
        if (!discoverServiceImpl.isNasVvol(vvol.getVvolid())) {
            long djSize = getVVolSizeById(vvol);
            if (djSize > oldSize) {
                //将DJ的vvolsize更新到vasa数据库中
                LOGGER.info("last array resize successfully but insert vasa database failed. vvolId is: " + vvolId +
                        " djSize is: " + djSize + " vasa database size is: " + oldSize);
                oldSize = djSize;
                virtualVolumeService.updateSizeByVvolId(vvolId, newSize);
            }
        }

        String sourceType = vvol.getSourceType();
        if (sourceType == null || sourceType.equalsIgnoreCase(VasaSrcTypeConstant.SNAPSHOT) || sourceType.equalsIgnoreCase(VasaSrcTypeConstant.FAST_CLONE)) {
            LOGGER.error("NotSupported/the volume type is snapshot or fast-clone");
            throw FaultUtil.notSupported("the volume type is snapshot or fast-clone");
        }

        if (oldSize == newSize) {
            LOGGER.error("old size equals new size. oldSize: " + oldSize + ", newSize: " + newSize);
            TaskInfo taskInfo = new TaskInfo();
            taskInfo.setName("resizeVirtualVolume");
            taskInfo.setCancelable(false);
            taskInfo.setCancelled(false);

            taskInfo.setStartTime(DateUtil.getUTCCalendar());

            taskInfo.setTaskId("resizeVirtualVolume:" + vvolId);
            taskInfo.setTaskState(TaskStateEnum.SUCCESS.value());
            return taskInfo;
        }

        if (oldSize > newSize) {
            LOGGER.error("NotSupported/do not support shrink virtual volume");
            throw FaultUtil.notSupported("do not support shrink virtual volume");
        }

        return discoverServiceImpl.resizeVirtualVolume(vvol, newSize);
    }

    public List<BatchVirtualVolumeHandleResult> bindVirtualVolume(List<ProtocolEndpoint> PEs, UsageContext usageContext,
                                                                  List<String> vvolIds, int bindType) throws StorageFault, InvalidArgument {
        try {
            boolean needCheck = false;
            for (String vvolId : vvolIds) {
                if (!discoverServiceImpl.isNasVvol(vvolId)) {
                    needCheck = true;
                }
            }
            if (needCheck) {
                Iterator<ProtocolEndpoint> peIterator = PEs.iterator();
                while (peIterator.hasNext()) {
                    ProtocolEndpoint pe = peIterator.next();
                    if (pe.getInBandId() == null || pe.getInBandId().getLunId() == null) {
                        peIterator.remove();
                    }
                }

                checkPEexist(PEs);
            }

            List<BatchVirtualVolumeHandleResult> allResults = new ArrayList<BatchVirtualVolumeHandleResult>();
            String containerId = null;
            Map<String, BatchVirtualVolumeHandleResult> bindResult = new HashMap<>();
            for (String vvolId : vvolIds) {
                if (!VASAUtil.checkVvolIdValid(vvolId)) {
                    LOGGER.warn("invalid vvolId:" + vvolId);
                    BatchVirtualVolumeHandleResult handleResult = new BatchVirtualVolumeHandleResult();
                    handleResult.setVvolId(vvolId);
                    handleResult.setFault(new com.vmware.vim.vasa.v20.fault.xsd.InvalidArgument());
                    allResults.add(handleResult);
                    continue;
                }

                List<String> validVvolIds = new ArrayList<String>();
                validVvolIds.add(vvolId);
                List<NVirtualVolume> nVirtualVolumes = queryVirtualVolumeFromDataBase(validVvolIds);
                if (0 == nVirtualVolumes.size()) {
                    LOGGER.warn("not found vvolId:" + vvolId);
                    BatchVirtualVolumeHandleResult handleResult = new BatchVirtualVolumeHandleResult();
                    handleResult.setVvolId(vvolId);
                    handleResult.setFault(new com.vmware.vim.vasa.v20.fault.xsd.InvalidArgument());
                    allResults.add(handleResult);
                    continue;
                }

                //检查传入的有效vvol的containerID是否是同一个， see vmware documents(All virtual volumes passed to bindVirtualVolume must reside in the same storage container)
                NVirtualVolume nVirtualVolume = nVirtualVolumes.get(0);

                //如果一个vvol lun已经删除，则不允许主机再下发绑定卷操作
                if ("deleting".equalsIgnoreCase(nVirtualVolume.getStatus()) ||
                        "error_deleting".equalsIgnoreCase(nVirtualVolume.getStatus())) {

                    LOGGER.warn("The vvolId=" + vvolId + "has already deleted, can't bind to PE lun");
                    BatchVirtualVolumeHandleResult handleResult = new BatchVirtualVolumeHandleResult();
                    handleResult.setVvolId(vvolId);
                    handleResult.setFault(new com.vmware.vim.vasa.v20.fault.xsd.InvalidArgument());
                    allResults.add(handleResult);
                    continue;
                }

                if (null == containerId) {
                    containerId = nVirtualVolume.getContainerId();
                } else {
                    if (!containerId.equals(nVirtualVolume.getContainerId())) {
                        LOGGER.error("InvalidArgument/not all vvol reside in the same storage container");
                        throw FaultUtil.invalidArgument("not all vvol reside in the same storage container");
                    }
                }

                boolean isNasVvol = discoverServiceImpl.isNasVvol(nVirtualVolume.getVvolid());
                try {
                    int errCount = 0;
                    String normalKey = null;
                    String errKey = null;

                    if (isNasVvol) {
                        LOGGER.info("exec bindNasVirtualVolume !!");
                        List<BatchVirtualVolumeHandleResult> results = discoverServiceImpl.bindNasVirtualVolume(nVirtualVolume.getArrayId(),
                                nVirtualVolume.getVvolid(), nVirtualVolume.getRawId(), nVirtualVolume.getSourceType(), bindType);
                        BatchVirtualVolumeHandleResult batchVirtualVolumeHandleResult = results.get(0);
                        allResults.add(batchVirtualVolumeHandleResult);
                    } else {
                        Set<String> hostIds = discoverServiceImpl.getHostIdByInitiator(nVirtualVolume.getArrayId(), usageContext.getHostInitiator());
                        if (null == hostIds) {
                            LOGGER.error("no hostId found");
                            BatchVirtualVolumeHandleResult handleResult = new BatchVirtualVolumeHandleResult();
                            handleResult.setVvolId(nVirtualVolume.getVvolid());
                            handleResult.setFault(new com.vmware.vim.vasa.v20.fault.xsd.StorageFault());
                            allResults.add(handleResult);
                            continue;
                        }
                        LOGGER.info("exec bindSanVirtualVolume !!");
                        for (String hostId : hostIds) {
                            List<BatchVirtualVolumeHandleResult> results = discoverServiceImpl.bindSanVirtualVolume(nVirtualVolume.getArrayId(),
                                    nVirtualVolume.getVvolid(), nVirtualVolume.getRawId(), nVirtualVolume.getSourceType(), hostId, bindType);
                            BatchVirtualVolumeHandleResult batchVirtualVolumeHandleResult = results.get(0);
                            bindResult.put(hostId + "##" + vvolId, batchVirtualVolumeHandleResult);
                            if (null != batchVirtualVolumeHandleResult && null != batchVirtualVolumeHandleResult.getFault()) {
                                errCount++;
                                errKey = hostId + "##" + vvolId;
                            }
                            normalKey = hostId + "##" + vvolId;
                            allResults.add(batchVirtualVolumeHandleResult);
                        }
                        if (errCount == hostIds.size() || errCount == 0) {
                            allResults.add(bindResult.get(normalKey));
                        } else {
                            allResults.add(bindResult.get(errKey));
                            LOGGER.error("bind hosts some error. begin unbind already binded host.");
                            rollbackUnbind(bindType, bindResult, nVirtualVolume);
                        }
                    }
                } catch (StorageFault e) {
                    // TODO: handle exception
                    LOGGER.error("bind hosts error. begin unbind already binded host.");
                    if (!isNasVvol) {
                        rollbackUnbind(bindType, bindResult, nVirtualVolume);
                    }
                    throw e;
                }
            }

            return allResults;
        } catch (Exception e) {
            LOGGER.error("bind error !!" + e);
            throw e;
        }
    }

    private void rollbackUnbind(int bindType, Map<String, BatchVirtualVolumeHandleResult> bindResult,
                                NVirtualVolume nVirtualVolume) throws StorageFault {
        Set<String> hosts = bindResult.keySet();
        for (String bindhostStr : hosts) {
            BatchVirtualVolumeHandleResult result = bindResult.get(bindhostStr);
            if (null != result && null == result.getFault()) {
                VirtualVolumeHandle vvolHandle = result.getVvolHandle();
                String peLunId = vvolHandle.getPeInBandId().getLunId();
                ProtocolEndpoint pE = VASAUtil.getPEByWwn(vvolHandle.getPeInBandId().getLunId());
                String[] paras = pE.getUniqueIdentifier().split(":");
                discoverServiceImpl.unbindVirtualVolume(nVirtualVolume.getArrayId(), bindhostStr.split("##")[0], vvolHandle.getVvolSecondaryId(), peLunId, paras[2], bindType, vvolHandle.getUniqueIdentifier());
            }
        }
    }

    private void checkPEexist(List<ProtocolEndpoint> PEs) throws StorageFault {
        List<ProtocolEndpoint> outOfBandPEs = dataManager.getOutOfBandPes();
        if (outOfBandPEs == null || outOfBandPEs.size() == 0) {
            List<ProtocolEndpoint> newOutOfBandPEs = queryProtocolEndpoint();
            dataManager.setOutOfBandPes(newOutOfBandPEs);
            checkPEexist(PEs, newOutOfBandPEs);
            return;
        }

        //remove unusable nas PE
        Iterator<ProtocolEndpoint> peIterator = outOfBandPEs.iterator();
        while (peIterator.hasNext()) {
            ProtocolEndpoint pe = peIterator.next();
            if (pe.getInBandId() == null || pe.getInBandId().getLunId() == null) {
                peIterator.remove();
            }
        }
        Boolean needRefreshPE = false;
        for (ProtocolEndpoint checkedPe : PEs) {
            Boolean isfind = false;
            for (ProtocolEndpoint validPE : outOfBandPEs) {
                //pe.getInBandId().getLunId()为： naa. + PElun的wwn
                if (VASAUtil.isSamePe(checkedPe, validPE)) {
                    //校验PE成功后将PE的唯一标识符反填到缓存，避免解绑定的时候对应的PE缓存的唯一标识符为null，导致解绑失败
                    checkedPe.setUniqueIdentifier(validPE.getUniqueIdentifier());
                    isfind = true;
                    break;
                }
            }
            if (!isfind) {
                needRefreshPE = true;
                break;
            }
        }

        if (needRefreshPE) {
            List<ProtocolEndpoint> refreshedPEs = queryProtocolEndpoint();
            dataManager.setOutOfBandPes(refreshedPEs);
            checkPEexist(PEs, refreshedPEs);
        }
    }

    private void checkPEexist(List<ProtocolEndpoint> PEs, List<ProtocolEndpoint> outOfBandPEs) throws StorageFault {
        for (ProtocolEndpoint checkedPe : PEs) {
            Boolean isfind = false;
            for (ProtocolEndpoint validPE : outOfBandPEs) {
                if (checkedPe.getInBandId().getLunId() != null) {
                    if (checkedPe.getInBandId().getLunId().equalsIgnoreCase(validPE.getInBandId().getLunId())) {
                        //校验PE成功后将PE的唯一标识符反填到缓存，避免解绑定的时候对应的PE缓存的唯一标识符为null，导致解绑失败
                        checkedPe.setUniqueIdentifier(validPE.getUniqueIdentifier());
                        isfind = true;
                        break;
                    }
                }


                if (checkedPe.getInBandId().getIpAddress() != null) {
                    if (checkedPe.getInBandId().getIpAddress().equalsIgnoreCase(validPE.getInBandId().getIpAddress())) {
                        checkedPe.setUniqueIdentifier(validPE.getUniqueIdentifier());
                        isfind = true;
                        break;
                    }
                }
            }
            if (!isfind) {
                if ((null != checkedPe.getInBandId().getLunId()) && (-1 == checkedPe.getInBandId().getLunId().indexOf("!"))) {
                    LOGGER.warn("PE lun : " + checkedPe.getInBandId().getLunId() + " not found.");
                } else if ((null != checkedPe.getInBandId().getIpAddress())) {
                    LOGGER.warn("PE Address : " + checkedPe.getInBandId().getIpAddress() + " not found.");
                }
                //暂时让认证用例通过，认证用例中要求必须失败
                else {
                    throw FaultUtil.storageFault("PE lun : " + checkedPe.getInBandId().getLunId() + " not found.");
                }
            }
        }
    }

    public List<BatchReturnStatus> unbindVirtualVolumeFromAllHost(List<String> vvolIds) throws StorageFault {
        List<BatchReturnStatus> returnValues = new ArrayList<BatchReturnStatus>();

        for (String vvolId : vvolIds) {
            if (!VASAUtil.checkVvolIdValid(vvolId)) {
                BatchReturnStatus returnStatus = new BatchReturnStatus();
                BatchErrorResult errorResult = new BatchErrorResult();
                errorResult.getError().add(new com.vmware.vim.vasa.v20.fault.xsd.InvalidArgument());
                returnStatus.setErrorResult(errorResult);
                returnStatus.setUniqueId(vvolId);
                LOGGER.warn("vvolId is invalid: " + vvolId);
                returnValues.add(returnStatus);
                continue;
            }
            if (discoverServiceImpl.isNasVvol(vvolId)) {
                unbindNasVvol(vvolId);
                continue;
            }
            List<String> validVvolIds = new ArrayList<String>();
            validVvolIds.add(vvolId);
            List<NVirtualVolume> validVVolumes = queryVirtualVolumeFromDataBase(validVvolIds);
            if (0 == validVVolumes.size()) {
                BatchReturnStatus returnStatus = new BatchReturnStatus();
                BatchErrorResult errorResult = new BatchErrorResult();
                errorResult.getError().add(new com.vmware.vim.vasa.v20.fault.xsd.NotFound());
                returnStatus.setErrorResult(errorResult);
                returnStatus.setUniqueId(vvolId);
                LOGGER.warn("vvolId can't be found in database: " + vvolId);
                returnValues.add(returnStatus);
                continue;
            }

            BatchReturnStatus otherResult = discoverServiceImpl.unbindVirtualVolumeFromAllHost(validVVolumes.get(0));
            returnValues.add(otherResult);
        }
        return returnValues;
    }

    public void unbindAllVirtualVolumesFromHost(UsageContext usageContext) throws StorageFault {
        //nas vvol to do
        discoverServiceImpl.unbindAllVirtualVolumesFromHost(usageContext.getHostInitiator());
    }

    public void unbindNasVvol(String vvolid) {
        vvolPathDBService.setBindState(vvolid, false);
    }

    public List<BatchReturnStatus> unbindVirtualVolume(List<VirtualVolumeHandle> vvolHandles, int bindType,
                                                       UsageContext usageContext) throws StorageFault {
        List<BatchReturnStatus> allResults = new ArrayList<BatchReturnStatus>();
        for (VirtualVolumeHandle vvolHandle : vvolHandles) {
            String identifier = vvolHandle.getUniqueIdentifier();
            String secondaryId = vvolHandle.getVvolSecondaryId();
            LOGGER.info("identifier: " + identifier + ", secondaryId: " + secondaryId);

            List<String> identifiers = new ArrayList<String>();
            identifiers.add(identifier);
            List<NVirtualVolume> nVirtVolumes = queryVirtualVolumeFromDataBase(identifiers);
            if (null == nVirtVolumes || 0 == nVirtVolumes.size()) {
                LOGGER.warn("invalid identifiers: " + identifiers);
                BatchReturnStatus returnStatus = new BatchReturnStatus();
                BatchErrorResult errorResult = new BatchErrorResult();
                errorResult.getError().add(new com.vmware.vim.vasa.v20.fault.xsd.NotFound());
                returnStatus.setErrorResult(errorResult);
                returnStatus.setUniqueId(vvolHandle.getUniqueIdentifier());
                allResults.add(returnStatus);
                continue;
            }

            if (discoverServiceImpl.isNasVvol(identifier)) {
                LOGGER.info("nas identifiers: " + identifier);
                VvolPath vvolPath = vvolPathDBService.getVvolPathByVvolId(identifier);
                String temp = vvolPath.getFileSystemName() + "/" + vvolPath.getPath();
                if (nVirtVolumes.get(0).getVvolType().equals(VasaConstant.VVOL_TYPE_CONFIG)) {
                    temp = temp + "/";
                }

                BatchReturnStatus returnStatus = new BatchReturnStatus();
                if (!temp.equals(secondaryId)) {
                    BatchErrorResult errorResult = new BatchErrorResult();
                    errorResult.getError().add(new com.vmware.vim.vasa.v20.fault.xsd.NotFound());
                    returnStatus.setErrorResult(errorResult);
                }
                if (!VASAUtil.isIPV4(vvolHandle.getPeInBandId().getIpAddress())) {
                    BatchErrorResult errorResult = new BatchErrorResult();
                    errorResult.getError().add(new com.vmware.vim.vasa.v20.fault.xsd.InvalidArgument());
                    returnStatus.setErrorResult(errorResult);
                }

                returnStatus.setUniqueId(vvolHandle.getUniqueIdentifier());
                allResults.add(returnStatus);
                unbindNasVvol(identifier);
                discoverServiceImpl.updateShareForUnbind(nVirtVolumes.get(0).getArrayId(), nVirtVolumes.get(0).getVvolType(), vvolPath);
                continue;
            }

            String lunIdHex = secondaryId.substring(4, 10);
            NVirtualVolume nVirtualVolume = nVirtVolumes.get(0);
            int lunId = Integer.parseInt(lunIdHex, 16);
            int rowId = Integer.parseInt(nVirtVolumes.get(0).getRawId());
            if (rowId != lunId) {
                LOGGER.error("invalid secondaryId: " + secondaryId + ", rawId: " + rowId);
                BatchReturnStatus returnStatus = new BatchReturnStatus();
                BatchErrorResult errorResult = new BatchErrorResult();
                errorResult.getError().add(new com.vmware.vim.vasa.v20.fault.xsd.NotFound());
                returnStatus.setErrorResult(errorResult);
                returnStatus.setUniqueId(vvolHandle.getUniqueIdentifier());

                allResults.add(returnStatus);
                continue;
            }

            if (!vvolHandle.getPeInBandId().getProtocolEndpointType().equalsIgnoreCase("SCSI")) {
                LOGGER.warn("invalid protocolEndpointType:" + vvolHandle.getPeInBandId().getProtocolEndpointType());

                BatchReturnStatus returnStatus = new BatchReturnStatus();
                BatchErrorResult errorResult = new BatchErrorResult();
                errorResult.getError().add(new com.vmware.vim.vasa.v20.fault.xsd.InvalidArgument());
                returnStatus.setErrorResult(errorResult);
                returnStatus.setUniqueId(vvolHandle.getUniqueIdentifier());

                allResults.add(returnStatus);
                continue;
            }

            ProtocolEndpoint pE = VASAUtil.getPEByWwn(vvolHandle.getPeInBandId().getLunId());
            if (null == pE) {
                LOGGER.warn("invalid peInBandId:" + vvolHandle.getPeInBandId().getLunId());

                BatchReturnStatus returnStatus = new BatchReturnStatus();
                BatchErrorResult errorResult = new BatchErrorResult();
                errorResult.getError().add(new com.vmware.vim.vasa.v20.fault.xsd.InvalidArgument());
                returnStatus.setErrorResult(errorResult);
                returnStatus.setUniqueId(vvolHandle.getUniqueIdentifier());

                allResults.add(returnStatus);
                continue;
            }

            if (!VASAUtil.isIdValid(pE.getUniqueIdentifier(), EntityTypeEnum.PROTOCOL_ENDPOINT.value())) {
                LOGGER.warn("invalid PE identifier:" + pE.getUniqueIdentifier());

                BatchReturnStatus returnStatus = new BatchReturnStatus();
                BatchErrorResult errorResult = new BatchErrorResult();
                errorResult.getError().add(new com.vmware.vim.vasa.v20.fault.xsd.InvalidArgument());
                returnStatus.setErrorResult(errorResult);
                returnStatus.setUniqueId(vvolHandle.getUniqueIdentifier());

                allResults.add(returnStatus);
                continue;
            }

            String[] paras = pE.getUniqueIdentifier().split(":");

            Set<String> hostIds = null;
            hostIds = discoverServiceImpl.getHostIdByInitiator(nVirtualVolume.getArrayId(), usageContext.getHostInitiator());
            if (discoverServiceImpl.isNasVvol(nVirtualVolume.getVvolid())) {
                hostIds = discoverServiceImpl.getHostIdByInitiator(nVirtualVolume.getArrayId(), usageContext.getHostInitiator());
                if (null == hostIds) {
                    LOGGER.error("not found host id");

                    BatchReturnStatus returnStatus = new BatchReturnStatus();
                    BatchErrorResult errorResult = new BatchErrorResult();
                    errorResult.getError().add(new com.vmware.vim.vasa.v20.fault.xsd.StorageFault());
                    returnStatus.setErrorResult(errorResult);
                    returnStatus.setUniqueId(vvolHandle.getUniqueIdentifier());

                    allResults.add(returnStatus);
                    continue;
                }
            }
            int errCount = 0;
            BatchReturnStatus normalResult = null;
            BatchReturnStatus errorResult = null;
            for (String hostId : hostIds) {
                BatchReturnStatus result = discoverServiceImpl.unbindVirtualVolume(nVirtualVolume.getArrayId(), hostId, vvolHandle.getVvolSecondaryId(),
                        paras[2], vvolHandle.getPeInBandId().getLunId(), bindType, vvolHandle.getUniqueIdentifier());
//    	    	if(null != result.getErrorResult()) {
//    	    		errorResult = result;
//    	    		errCount++;
//    	    	}
//    	    	normalResult = result;
                allResults.add(result);
            }
//    	    if(errCount == 0 || errCount == hostIds.size()) {
//    	    	allResults.add(normalResult);
//    	    }else {
//    	    	allResults.add(errorResult);
//    	    }
        }
        return allResults;
    }

    /**
     * 被调用的条件:
     * ..外面需要校验vvolid是否null,是否有key为null:
     * if(true)
     * throw exception
     * else
     * continue;
     * 代码逻辑:
     * 1.查询vvolid是否存在:
     * if(false)
     * throw exception
     * else
     * continue.
     * 2.查询vvolid and key 是否存在:
     * if(false)
     * insert
     * else
     * if(value is empty)
     * delete
     * else
     * update
     *
     * @param vvolId
     * @param keyValuePairs
     * @throws StorageFault
     * @throws NotFound
     */
    public void updateVirtualVolumeMetaData(String vvolId, List<NameValuePair> keyValuePairs) throws StorageFault, NotFound {
        NVirtualVolume vvol = virtualVolumeService.getVirtualVolumeByVvolIdNotIncludeDeleting(vvolId);
        String vmId = "";
        String vmName = "";
        if (vvol == null) {
            LOGGER.error("NotFound/not found vvolId: " + vvolId);
            throw FaultUtil.notFound("not found vvolId: " + vvolId);
        }

        for (NameValuePair pair : keyValuePairs) {
            LOGGER.info("key=" + pair.getParameterName() + " value=" + pair.getParameterValue() + " vmId=" + vmId);
            if ("VMW_VmID".equalsIgnoreCase(pair.getParameterName())) {
                vmId = pair.getParameterValue();
                //记录卷归属于哪一个
                vmName = VASAUtil.getRegularVMName(keyValuePairs);
                if (vmId != null && !"".equals(vmId)) {
                    if (vmName != null && !"".equals(vmName)) {
                        virtualVolumeService.updateVmInfoByVvolId(vmId, vmName, vvolId);
                    } else {
                        virtualVolumeService.updateVmIdByVvolId(vmId, vvolId);
                    }
                }

                //记录虚拟机名称与vmId的关联关系
                saveVirtualMachineInfo(vvol, vmId);
            }
            int count = vvolMetadataService.getCountByVvolIdAndKey(vvolId, pair.getParameterName());
            if (0 == count) {
                //save vvol lun metadata
                NVvolMetadata vvolMetadata = new NVvolMetadata();
                vvolMetadata.setVvolid(vvolId);
                vvolMetadata.setKey(pair.getParameterName());
                vvolMetadata.setValue(pair.getParameterValue());
                vvolMetadataService.addVvolMetadata(vvolMetadata);
            }
            if (null == pair.getParameterValue()) {
                vvolMetadataService.deleteVvolMetadataByVvolIdAndKey(vvolId, pair.getParameterName());
            } else {
                vvolMetadataService.updateVvolMetadataByVvolIdAndKey(vvolId, pair.getParameterName(), pair.getParameterValue());
            }
        }

    }

    public List<SpaceStats> spaceStatsForVirtualVolume(List<String> vvolIds) throws StorageFault {
        List<NVirtualVolume> validVvolIds = queryVirtualVolumeFromDataBaseNotIncludeDeleting(vvolIds);

        return discoverServiceImpl.spaceStatsForVirtualVolume(validVvolIds);
    }

    public List<NVirtualVolume> getVirtualVolumeFromDataBaseByParentId(String parentid) throws StorageFault {
    	/*
    	if(!VASAUtil.checkVvolIdValid(parentid)){
    		LOGGER.warn("invalid vvolId:" + parentid);
    		return null;
    	}
    	*/
        return virtualVolumeService.getVirtualVolumeByParentId(parentid);
    }

    public List<NVirtualVolume> queryVirtualVolumeFromDataBase(List<String> vvolIds) throws StorageFault {
        List<NVirtualVolume> validVvolIds = new ArrayList<NVirtualVolume>();
        for (String vvolId : vvolIds) {
            if (!VASAUtil.checkVvolIdValid(vvolId)) {
                LOGGER.warn("invalid vvolId:" + vvolId);
                continue;
            }

            NVirtualVolume vvol = virtualVolumeService.getVirtualVolumeByVvolId(vvolId);
            if (vvol != null) {
                validVvolIds.add(vvol);
            } else {
                LOGGER.warn("not found vvolid : " + vvolId + " in database.");
            }
        }

        return validVvolIds;
    }

    public List<NVirtualVolume> queryVirtualVolumeFromDataBaseNotIncludeDeleting(List<String> vvolIds) throws StorageFault {
        List<NVirtualVolume> validVvolIds = new ArrayList<NVirtualVolume>();
        for (String vvolId : vvolIds) {
            if (!VASAUtil.checkVvolIdValid(vvolId)) {
                LOGGER.warn("invalid vvolId:" + vvolId);
                continue;
            }

            NVirtualVolume vvol = virtualVolumeService.getVirtualVolumeByVvolId(vvolId);
            if (vvol != null && !vvol.getStatus().equalsIgnoreCase("deleting") && !vvol.getStatus().equalsIgnoreCase("error_deleting")) {
                validVvolIds.add(vvol);
            } else {
                LOGGER.warn("not found vvolid : " + vvolId + " in database.");
            }
        }

        return validVvolIds;
    }

    public List<ContainerSpaceStats> spaceStatsForStorageContainer(String containerId, List<String> capabilityProfileIds)
            throws StorageFault, NotFound {
        if (capabilityProfileIds != null && capabilityProfileIds.size() != 0) {
            //List<S2DVolumeType> volumeTypes = discoverServiceImpl.getVolumeTypeForContainer(containerId);
            List<NStorageProfile> profileByContainerId = queryProfileByContainerId(containerId);
            LOGGER.info("spaceStatsForStorageContainer queryProfileByContainerId=" + profileByContainerId);
            for (String profileId : capabilityProfileIds) {
                boolean isFound = false;
                for (NStorageProfile nStorageProfile : profileByContainerId) {
                    if (profileId.equalsIgnoreCase(nStorageProfile.getProfileId())) {
                        isFound = true;
                        break;
                    }
                }
                if (!isFound) {
                    LOGGER.error("NotFound/not found profileId:" + profileId);
                    throw FaultUtil.notFound("not found profileId:" + profileId);
                }
            }
        }
        LOGGER.debug("begin spaceStatsForStorageContainer");
        return discoverServiceImpl.spaceStatsForStorageContainer(containerId, capabilityProfileIds);
    }

    public TaskInfo prepareToSnapshotVirtualVolume(String vvolId, StorageProfile storageProfile) throws StorageFault, NotFound {
        List<String> vvolIds = new ArrayList<String>();
        vvolIds.add(vvolId);
        List<NVirtualVolume> validVvols = queryVirtualVolumeFromDataBaseNotIncludeDeleting(vvolIds);
        if (0 == validVvols.size()) {
            LOGGER.error("NotFound/not found vvol:" + vvolId);
            throw FaultUtil.notFound("not found vvol:" + vvolId);
        }
        VASAUtil.updateArrayIdByVvolId(vvolId);
        return discoverServiceImpl.prepareToSnapshotVirtualVolume(validVvols.get(0), storageProfile);
    }

    public List<BatchReturnStatus> snapshotVirtualVolume(List<VirtualVolumeInfo> snapshotInfos, long timeoutMS)
            throws StorageFault {
        List<BatchReturnStatus> allResults = new ArrayList<BatchReturnStatus>();
        for (VirtualVolumeInfo snapshotInfo : snapshotInfos) {
            String vvolId = snapshotInfo.getVvolId();
            if (!VASAUtil.checkVvolIdValid(vvolId)) {
                LOGGER.warn("invalid vvolId:" + vvolId);

                BatchReturnStatus returnStatus = new BatchReturnStatus();
                BatchErrorResult errorResult = new BatchErrorResult();
                errorResult.getError().add(new com.vmware.vim.vasa.v20.fault.xsd.InvalidArgument());
                returnStatus.setErrorResult(errorResult);
                returnStatus.setUniqueId(vvolId);

                allResults.add(returnStatus);
                continue;
            }

            List<String> vvolIds = new ArrayList<String>();
            vvolIds.add(vvolId);
            List<NVirtualVolume> vvols = queryVirtualVolumeFromDataBaseNotIncludeDeleting(vvolIds);
            if (0 == vvols.size()) {
                LOGGER.warn("not found vvolId:" + vvolId);

                BatchReturnStatus returnStatus = new BatchReturnStatus();
                BatchErrorResult errorResult = new BatchErrorResult();
                errorResult.getError().add(new com.vmware.vim.vasa.v20.fault.xsd.NotFound());
                returnStatus.setErrorResult(errorResult);
                returnStatus.setUniqueId(vvolId);

                allResults.add(returnStatus);
                continue;
            }
            NVirtualVolume validVvol = vvols.get(0);

            BatchReturnStatus statusResult = discoverServiceImpl.snapshotVirtualVolume(validVvol, snapshotInfo.getMetadata(), timeoutMS);
            allResults.add(statusResult);
        }

        return allResults;
    }

    public TaskInfo revertVirtualVolume(String vvolId, String snapshotVvolId) throws StorageFault, NotFound, IncompatibleVolume {
        List<String> vvolIds = new ArrayList<String>();
        vvolIds.add(snapshotVvolId);
        vvolIds.add(vvolId);
        List<NVirtualVolume> snapshots = queryVirtualVolumeFromDataBaseNotIncludeDeleting(vvolIds);
        if (2 != snapshots.size()) {
            LOGGER.error("NotFound/not found snapshot:" + snapshotVvolId);
            throw FaultUtil.notFound("not found snapshot:" + snapshotVvolId);
        }

        NVirtualVolume snapshot = snapshots.get(0);
        if (!snapshot.getParentId().equalsIgnoreCase(vvolId)) {
            LOGGER.error("IncompatibleVolume/vvolId:" + vvolId + " and snapVvolId:" + snapshotVvolId + " are not in proper snapshot relationship");
            throw FaultUtil.incompatibleVolume("vvolId:" + vvolId + " and snapVvolId:" + snapshotVvolId + " are not in proper snapshot relationship");
        }

        return discoverServiceImpl.revertVirtualVolume(snapshot);
    }

    /*public TaskInfo cloneVirtualVolume(String vvolId, String newContainerId, StorageProfile storageProfile, List<NameValuePair> medatada)
    		throws NotFound, NotSupported, StorageFault
    {
    	List<String> vvolIds = new ArrayList<String>();
    	vvolIds.add(vvolId);
    	List<NVirtualVolume> vvols = queryVirtualVolumeFromDataBase(vvolIds);
    	if(0 == vvols.size())
    	{
    		LOGGER.error("not found vvolId:" + vvolId);
    		throw FaultUtil.notFound("not found vvolId:" + vvolId);
    	}

    	NVirtualVolume vvol = vvols.get(0);
    	if(null != newContainerId && !vvol.getContainerId().equalsIgnoreCase(newContainerId))
    	{
    		LOGGER.error("not support different container. containerId:" + vvol.getContainerId() + ", newContainerId:" + newContainerId);
    		throw FaultUtil.notSupported("not support different container. containerId:" + vvol.getContainerId() + ", newContainerId:" + newContainerId);
    	}

    	TaskInfo result = discoverServiceImpl.cloneVirtualVolume(vvol, storageProfile, medatada);
    	return result;
    }*/

    public TaskInfo fastCloneVirtualVolume(String vvolId, StorageProfile storageProfile, List<NameValuePair> metadata)
            throws NotFound, StorageFault, SnapshotTooMany {
        List<String> vvolIds = new ArrayList<String>();
        vvolIds.add(vvolId);
        List<NVirtualVolume> vvols = queryVirtualVolumeFromDataBaseNotIncludeDeleting(vvolIds);
        if (0 == vvols.size()) {
            LOGGER.error("NotFound/not found vvolId:" + vvolId);
            throw FaultUtil.notFound("not found vvolId:" + vvolId);
        }

        NVirtualVolume vvol = vvols.get(0);
        TaskInfo result = discoverServiceImpl.fastCloneVirtualVolume(vvol, storageProfile, metadata);
        return result;
    }

    public VirtualVolumeUnsharedChunksResult unsharedChunksVirtualVolume(String vvolId, String baseVvolId,
                                                                         long segmentStartOffsetBytes, long segmentLengthBytes) throws StorageFault, NotFound
            , NotImplemented, IncompatibleVolume, InvalidArgument {
        List<String> vvolIds = new ArrayList<String>();
        vvolIds.add(vvolId);
        List<NVirtualVolume> vvols = queryVirtualVolumeFromDataBaseNotIncludeDeleting(vvolIds);
        if (0 == vvols.size()) {
            LOGGER.error("NotFound/not found vvolId:" + vvolId);
            throw FaultUtil.notFound("not found vvolId:" + vvolId);
        }

        NVirtualVolume vvol = vvols.get(0);


//    	if( segmentStartOffsetBytes + segmentLengthBytes > vvol.getSize()*MagicNumber.LONG1024*MagicNumber.LONG1024)
//    	{
//    		LOGGER.error("out of range, the vvol size is:" + vvol.getSize()*MagicNumber.LONG1024*MagicNumber.LONG1024 + " Bytes");
//    		throw FaultUtil.invalidArgument("out of range, the vvol size is:" + vvol.getSize()*MagicNumber.LONG1024*MagicNumber.LONG1024 + " Bytes");
//    	}

        if (null == baseVvolId) {
            VirtualVolumeUnsharedChunksResult result = discoverServiceImpl.unsharedChunksVirtualVolume(vvol,
                    segmentStartOffsetBytes, segmentLengthBytes);
            return result;
        }

        vvolIds.clear();
        vvolIds.add(baseVvolId);
        List<NVirtualVolume> baseVvols = queryVirtualVolumeFromDataBaseNotIncludeDeleting(vvolIds);
        if (0 == baseVvols.size()) {
            LOGGER.error("NotFound/not found baseVvolId:" + baseVvolId);
            throw FaultUtil.notFound("not found baseVvolId:" + baseVvolId);
        }
        //getVvolParentList
        NVirtualVolume baseVvol = baseVvols.get(0);
        //unshChunksVVol.Neg005 认证用例  判断原卷和快照卷的关联关系，没有关联关系返回InCompatibleVolume
        List<String> srcparentVvolIdList = getVvolParentList(vvol);
        List<String> baseparentVvolIdList = getVvolParentList(baseVvol);
        LOGGER.info("The srcparentVvolIdList is " + srcparentVvolIdList.toString());
        LOGGER.info("The baseparentVvolIdList is " + baseparentVvolIdList.toString());
        if (srcparentVvolIdList != null && baseparentVvolIdList != null) {
            if (srcparentVvolIdList.contains(baseVvolId) || baseparentVvolIdList.contains(vvolId)) {
                LOGGER.info("vvolId:" + vvolId + " and baseVvolId:" + baseVvolId + " are in a snapshot relationship");
            } else {
                LOGGER.error("vvolId:" + vvolId + " and baseVvolId:" + baseVvolId + " are not in a snapshot relationship");
                throw FaultUtil.incompatibleVolume("vvolId:" + vvolId + " and baseVvolId:" + baseVvolId + " are not in a snapshot relationship");
            }
        }

        /**不是快照关系的异常到阵列侧去校验*/
//    	if(!baseVvol.getParentId().equalsIgnoreCase(vvolId) && !vvol.getParentId().equalsIgnoreCase(baseVvolId))
//    	{
//    		LOGGER.error("vvolId:" + vvolId + " and baseVvolId:" + baseVvolId + " are not in a snapshot relationship");
//    		throw FaultUtil.incompatibleVolume("vvolId:" + vvolId + " and baseVvolId:" + baseVvolId + " are not in a snapshot relationship");
//    	}

        if (!vvol.getArrayId().equalsIgnoreCase(baseVvol.getArrayId())) {
            LOGGER.error("StorageFault/the two vvols not in same array");
            throw FaultUtil.storageFault("the two vvols not in same array");
        }

        VirtualVolumeUnsharedChunksResult result = discoverServiceImpl.unsharedChunksVirtualVolume(vvol, baseVvol,
                segmentStartOffsetBytes, segmentLengthBytes);
        return result;
    }

    public VirtualVolumeBitmapResult unsharedBitmapVirtualVolume(String vvolId, String baseVvolId, long segmentStartOffsetBytes,
                                                                 long segmentLengthBytes, long chunkSizeBytes) throws NotFound, InvalidArgument, NotImplemented, StorageFault, IncompatibleVolume {
        List<String> vvolIds = new ArrayList<String>();
        vvolIds.add(vvolId);
        vvolIds.add(baseVvolId);
        List<NVirtualVolume> vvols = queryVirtualVolumeFromDataBaseNotIncludeDeleting(vvolIds);
        if (2 != vvols.size()) {
            LOGGER.error("NotFound/not found vvolId:" + vvolId + " or baseVvolId:" + baseVvolId);
            throw FaultUtil.notFound("not found vvolId:" + vvolId + " or baseVvolId:" + baseVvolId);
        }

        NVirtualVolume vvol = vvols.get(0);
        NVirtualVolume baseVvol = vvols.get(1);

        //unshBMapVVol.Neg0007  判断原卷和快照卷的关联关系，没有关联关系返回InCompatibleVolume
        List<String> srcparentVvolIdList = getVvolParentList(vvol);
        List<String> baseparentVvolIdList = getVvolParentList(baseVvol);
        LOGGER.info("The srcparentVvolIdList is " + srcparentVvolIdList.toString());
        LOGGER.info("The baseparentVvolIdList is " + baseparentVvolIdList.toString());
        if (srcparentVvolIdList != null && baseparentVvolIdList != null) {
            if (srcparentVvolIdList.contains(baseVvolId) || baseparentVvolIdList.contains(vvolId)) {
                LOGGER.info("vvolId:" + vvolId + " and baseVvolId:" + baseVvolId + " are in a snapshot relationship");
            } else {
                LOGGER.error("vvolId:" + vvolId + " and baseVvolId:" + baseVvolId + " are not in a snapshot relationship");
                throw FaultUtil.incompatibleVolume("vvolId:" + vvolId + " and baseVvolId:" + baseVvolId + " are not in a snapshot relationship");
            }
        }

        /** unshBMapVVol.Neg0007 判断vvol关系早于判断长度越界，放到阵列侧去校验*/
//    	if( segmentStartOffsetBytes + segmentLengthBytes > vvol.getSize()*MagicNumber.LONG1024*MagicNumber.LONG1024)
//    	{
//    		LOGGER.error("out of range, the vvol size is:" + vvol.getSize()*MagicNumber.LONG1024*MagicNumber.LONG1024 + " Bytes");
//    		throw FaultUtil.invalidArgument("out of range, the vvol size is:" + vvol.getSize()*MagicNumber.LONG1024*MagicNumber.LONG1024 + " Bytes");
//    	}

        /**fClnVVol.Pos006 baseVvolId为vvolId的快照的fastClone,并不是快照关系，这个异常放到阵列上去校验*/
//    	if(!baseVvol.getParentId().equalsIgnoreCase(vvolId) && !vvol.getParentId().equalsIgnoreCase(baseVvolId))
//    	{
//    		LOGGER.error("vvolId:" + vvolId + " and baseVvolId:" + baseVvolId + " are not in a snapshot relationship");
//    		throw FaultUtil.incompatibleVolume("vvolId:" + vvolId + " and baseVvolId:" + baseVvolId + " are not in a snapshot relationship");
//    	}

        if (!vvol.getArrayId().equalsIgnoreCase(baseVvol.getArrayId())) {
            LOGGER.error("StorageFault/the two vvols not in same array");
            throw FaultUtil.storageFault("the two vvols not in same array");
        }

        VirtualVolumeBitmapResult result = discoverServiceImpl.unsharedBitmapVirtualVolume(vvol, baseVvol, segmentStartOffsetBytes,
                segmentLengthBytes, chunkSizeBytes);
        return result;
    }

    public VirtualVolumeBitmapResult allocatedBitmapVirtualVolume(String vvolId, long segmentStartOffsetBytes,
                                                                  long segmentLengthBytes, long chunkSizeBytes) throws NotFound, NotImplemented, InvalidArgument, StorageFault {
        List<String> vvolIds = new ArrayList<String>();
        vvolIds.add(vvolId);
        List<NVirtualVolume> vvols = queryVirtualVolumeFromDataBaseNotIncludeDeleting(vvolIds);
        if (0 == vvols.size()) {
            LOGGER.error("NotFound/not found vvolId:" + vvolId);
            throw FaultUtil.notFound("not found vvolId:" + vvolId);
        }

        NVirtualVolume vvol = vvols.get(0);

        if (segmentStartOffsetBytes + segmentLengthBytes > vvol.getSize() * MagicNumber.LONG1024 * MagicNumber.LONG1024) {
            LOGGER.error("InvalidArgument/out of range, the vvol size is:" + vvol.getSize() * MagicNumber.LONG1024 * MagicNumber.LONG1024 + " Bytes");
            throw FaultUtil.invalidArgument("out of range, the vvol size is:" + vvol.getSize() * MagicNumber.LONG1024 * MagicNumber.LONG1024 + " Bytes");
        }

        VirtualVolumeBitmapResult result = discoverServiceImpl.allocatedBitmapVirtualVolume(vvol, segmentStartOffsetBytes, segmentLengthBytes,
                chunkSizeBytes);
        return result;
    }

    public TaskInfo copyDiffsToVirtualVolume(String srcVvolId, String srcBaseVvolId, String dstVvolId)
            throws NotFound, StorageFault, IncompatibleVolume, ResourceInUse, NotImplemented, InvalidArgument, VasaProviderBusy {
        List<String> vvolIds = new ArrayList<String>();
        vvolIds.add(srcVvolId);
        vvolIds.add(srcBaseVvolId);
        vvolIds.add(dstVvolId);
        List<NVirtualVolume> vvols = queryVirtualVolumeFromDataBaseNotIncludeDeleting(vvolIds);
        if (3 != vvols.size()) {
            LOGGER.error("NotFound/not found all vvols");
            throw FaultUtil.notFound("not found all vvols");
        }

        NVirtualVolume srcVvol = vvols.get(0);
        NVirtualVolume srcBaseVvol = vvols.get(1);
        NVirtualVolume dstVvol = vvols.get(2);

        //判断srcVvolId和srcBaseVvolId的关联关系(in fast clone or snapshot)，没有关联关系返回InCompatibleVolume,20170207
        //srcVVolId and baseVvolId must be in a fast clone or snapshot relationship, though not necessarily parent‐child.
        List<String> srcparentVvolIdList = getVvolParentList(srcVvol);
        List<String> baseparentVvolIdList = getVvolParentList(srcBaseVvol);
        LOGGER.info("The srcparentVvolIdList is " + srcparentVvolIdList.toString());
        LOGGER.info("The baseparentVvolIdList is " + baseparentVvolIdList.toString());
        if (srcparentVvolIdList != null && baseparentVvolIdList != null) {
            if (srcparentVvolIdList.contains(srcBaseVvolId) || baseparentVvolIdList.contains(srcVvolId)) {
                LOGGER.info("vvolId:" + srcVvolId + " and baseVvolId:" + srcBaseVvolId + " are in a snapshot relationship");
            } else {
                LOGGER.error("vvolId:" + srcVvolId + " and baseVvolId:" + srcBaseVvolId + " are not in a snapshot relationship");
                throw FaultUtil.incompatibleVolume("vvolId:" + srcVvolId + " and baseVvolId:" + srcBaseVvolId + " are not in a snapshot relationship");
            }
        }

        if (!srcVvol.getArrayId().equalsIgnoreCase(srcBaseVvol.getArrayId()) || !srcVvol.getArrayId().equalsIgnoreCase(dstVvol.getArrayId())) {
            LOGGER.error("StorageFault/the three vvols not in the same array");
            throw FaultUtil.storageFault("the three vvols not in the same array");
        }
        VASAUtil.saveArrayId(srcVvol.getArrayId());
        return discoverServiceImpl.copyDiffsToVirtualVolume(srcVvol, srcBaseVvol, dstVvol);
    }

    public void cancelTask(String taskId) throws InvalidArgument, NotFound, NotCancellable, StorageFault {

        if (taskId.startsWith("cloneVirtualVolume")) {
            discoverServiceImpl.cancelCloneTask(taskId);
        } else if (taskId.startsWith("copyDiffsToVirtualVolume")) {
            String[] paras = taskId.split(":");
            if (3 != paras.length) {
                LOGGER.error("InvalidArgument/invalid taskId:" + taskId);
                throw FaultUtil.invalidArgument("invalid taskId:" + taskId);
            }

            discoverServiceImpl.cancelCopyDiffsTask(paras[1], paras[2]);
        } else {
            LOGGER.error("NotCancellable/not cancellable taskId:" + taskId);
            throw FaultUtil.notCancellable("not cancellable taskId:" + taskId);
        }
    }

    public TaskInfo getTaskUpdate(String taskId) throws StorageFault, NotFound, VasaProviderBusy {
        LOGGER.info("query task, taskId = " + taskId);
        //copyDiffsToVirtualVolume同步接口
        if (taskId.startsWith("copyDiffsToVirtualVolume")) {
            return discoverServiceImpl.updateCopyDiffsTask(taskId);
        } else if (taskId.startsWith("prepareToSnapshotVirtualVolume")/* || taskId.startsWith("revertVirtualVolume")*/) {
            return discoverServiceImpl.updateSnapshotVvolTask(taskId);
        } else if (taskId.startsWith("cloneVirtualVolume")) {
            return discoverServiceImpl.updateCloneVvolTask(taskId);
        } else if (taskId.startsWith("fastCloneVirtualVolume")) {
            return discoverServiceImpl.updateFastCloneVirtualVolume(taskId);
        } else {
            return discoverServiceImpl.updateGeneralVvolTask(taskId);
        }
    }

    public List<StorageInfo> queryAllArrays() {
        List<StorageInfo> storageInfos = storageManagerService.queryInfo();
        for (StorageInfo storageInfo : storageInfos) {
            storageInfo.setPassword(ArrayPwdAES128Util.decryptPwd(storageInfo.getUsername(), storageInfo.getPassword()));
        }
        return storageInfos;
    }

    public List<NTaskInfo> getRunningTaskByArrayId(String arrayId) {
        NTaskInfo t = new NTaskInfo();
        t.setArrayid(arrayId);
        return taskInfoService.search(t);
    }

    public List<String> getVvolParentList(NVirtualVolume vvol) {
        List<String> parentVvolIdList = new ArrayList<String>();
        try {
            NVirtualVolume tempVvol = vvol;
            while (!tempVvol.getParentId().equalsIgnoreCase("NA")) {
                parentVvolIdList.add(tempVvol.getParentId());
                tempVvol = virtualVolumeService.getVirtualVolumeByVvolId(tempVvol.getParentId());
                if (tempVvol == null) {
                    break;
                }
            }
        } catch (StorageFault e) {
            // TODO Auto-generated catch block
            LOGGER.error("get VirtualVolumeByVvolID fail. Exception ", e);
        }
        return parentVvolIdList;
    }

    /**
     * 方法 ： saveVirtualMachineInfo
     *
     * @param parameters 方法参数：metadata
     * @return
     */
    public void saveVirtualMachineInfo(NVirtualVolume vvol, String vmId) {
        LOGGER.info("In save saveVirtualMachineInfo function.");
        if (vvol == null || vmId == null || "".equals(vmId)) {
            LOGGER.warn("The vmId = " + vmId + " not need add virtualMachine info");
            return;
        }
        try {
            //after update vvol metadata, the vvol has vmId, then update it to virtualVolumeInfo
            List<NVvolMetadata> vvolMetadataList = vvolMetadataService.getVvolMetadataByVvolId(vvol.getVvolid());
            String vmName = "";
            boolean isConfigFlag = false;
            for (NVvolMetadata vvolMetadata : vvolMetadataList) {
                if ("VMW_VVolType".equalsIgnoreCase(vvolMetadata.getKey()) &&
                        "Config".equalsIgnoreCase(vvolMetadata.getValue())) {
                    isConfigFlag = true;
                }
                if ("VMW_VVolName".equalsIgnoreCase(vvolMetadata.getKey())) {
                    vmName = vvolMetadata.getValue();
                }
            }

            if ("".equals(vmName) || "".equals(vmId) || vmName == null || vmId == null) {
                LOGGER.warn("The vmName=" + vmName + " vmId=" + vmId + " do not update vmMachineInfo");
                return;
            }

            if (isConfigFlag) {
                NVirtualMachine vmInfo = new NVirtualMachine();
                vmInfo.setVmName(vmName);
                vmInfo.setVmId(vmId);
                vmInfo.setDeleted(false);
                if (virtualMachineService.getVirtualMachineInfoByVmId(vmId).size() > 0) {
                    LOGGER.info("updateVirtualMachine");
                    vmInfo.setUpdatedTime(new Timestamp(System.currentTimeMillis()));
                    virtualMachineService.updateVirtualMachine(vmInfo);
                } else {
                    LOGGER.info("addVirtualMachine");
                    vmInfo.setCreatedTime(new Timestamp(System.currentTimeMillis()));
                    virtualMachineService.addVirtualMachine(vmInfo);
                }
            }
        } catch (Exception e) {
            LOGGER.error("SaveVirtualMachineInfo faile. Exception ", e);
        }
    }

    public void addFlowControlDevice(String devId, String arrayId) {
        Map<String, DArrayFlowControl> arrayFlowControls = dataManager.getArrayFlowControl();
        int MAX_ACCESS_COUNT = Integer.parseInt(ConfigManager.getInstance().getValue("vasa.rest.max.access.count"));
        int limit = 200;
        int period = 1;
        DArrayFlowControl arrayFlowControl = new DArrayFlowControl();
        arrayFlowControl.setArrayId(arrayId);
        arrayFlowControl.setMAX_ACCESS_COUNT(MAX_ACCESS_COUNT);
        arrayFlowControl.setSemaphore(new Semaphore(MAX_ACCESS_COUNT));
        arrayFlowControl.setRestQosControl(new RestQosControl(limit, period, TimeUnit.SECONDS));
        arrayFlowControls.put(devId, arrayFlowControl);
    }

    public void appendConfigStorageArrayAlarm(List<String> ucUUIDs, List<String> cachedArrays, List<String> currArrays) {
        if (ucUUIDs == null || ucUUIDs.size() == 0) {
            LOGGER.warn("ucUUIDs isnull or empty.");
            return;
        }

        LOGGER.info("cachedArrays:" + VASAUtil.convertArrayToStr(cachedArrays));
        LOGGER.info("currArrays:" + VASAUtil.convertArrayToStr(currArrays));

        List<String> newArrays = new ArrayList<String>();
        List<String> removeArrays = new ArrayList<String>();

        for (String currArray : currArrays) {
            if (!cachedArrays.contains(currArray)) {
                newArrays.add(currArray);
            }
        }
        for (String cacheArray : cachedArrays) {
            if (!currArrays.contains(cacheArray)) {
                removeArrays.add(cacheArray);
            }
        }
        if (newArrays != null && newArrays.size() != 0) {
            LOGGER.info("add Storage Array green alarm , the new online array is : " + newArrays.toString());
            addStorageArrayGreenAlarm(newArrays);

            for (String arrayId : newArrays) {
                updateArrayInfoMapDevicePriority(arrayId, 100);
            }
            addStorageArrayUpdateEvent(newArrays);
        }
        if (removeArrays != null && removeArrays.size() != 0) {
            LOGGER.info("add Storage Array red alarm , the new offline array is : " + removeArrays.toString());
            addStorageArrayRedAlarm(removeArrays);
            for (String arrayId : removeArrays) {
                updateArrayInfoMapDevicePriority(arrayId, 10);
            }
            addStorageArrayUpdateEvent(removeArrays);

        }
    }

    public void addStorageArrayGreenAlarm(List<String> arrayIds) {
        LOGGER.info("In addStorageArrayGreenAlarm function, the arrayIds = " + arrayIds.toString());
        List<SessionContext> scs = SessionContext.getSessionContextList();
        List<String> ucUUIDs = new ArrayList<String>();
        for (SessionContext sc : scs) {
            String sessionId = sc.getSessionId();
            ucUUIDs.add(sessionId);
        }
        appendStorageArrayStatusChangeAlarm(ucUUIDs, arrayIds, true);
    }

    public void addStorageArrayRedAlarm(List<String> arrayIds) {
        LOGGER.info("In addStorageArrayRedAlarm function, the arrayIds = " + arrayIds.toString());
        List<SessionContext> scs = SessionContext.getSessionContextList();
        List<String> ucUUIDs = new ArrayList<String>();
        for (SessionContext sc : scs) {
            String sessionId = sc.getSessionId();
            ucUUIDs.add(sessionId);
        }

        appendStorageArrayStatusChangeAlarm(ucUUIDs, arrayIds, false);
    }

    public void appendStorageArrayStatusChangeAlarm(List<String> ucUUIDs, List<String> arrayIds, boolean isGreen) {
        LOGGER.info("In appendStorageArrayStatusChangeAlarm function , the ucUUIDs is " + ucUUIDs.toString() +
                " arrayIds = " + arrayIds.toString() + "isGreen = " + isGreen);
        if (ucUUIDs == null || ucUUIDs.size() == 0) {
            LOGGER.warn("ucUUIDs isnull or empty.");
            return;
        }
        Map<String, List<Event>> sendToVcenterAlarms = dataManager.getAlarmForVcenter();
        List<Event> savedAlarms = new ArrayList<Event>(0);

        for (String ucUUID : ucUUIDs) {
            List<Event> toVcenterAlarms = sendToVcenterAlarms.get(ucUUID);

            if (toVcenterAlarms == null) {
                LOGGER.info("In appendStorageArrayStatusChangeAlarm function, the sessionId = " + ucUUID + " the toVcenterAlarms is NULL");
                toVcenterAlarms = new ArrayList<Event>(0);
                dataManager.setConfigAlarmForvCenter(ucUUID, toVcenterAlarms);
            }

            for (String arrayid : arrayIds) {
                Event event = null;
                Level level = null;
                level = Level.valueOf(6);
                long curMaxSN = VASAUtil.findMaxEventSN(toVcenterAlarms, arrayid);
                Identifier identifier = new Identifier(arrayid, ++curMaxSN);
                if (isGreen) {
                    event = new Event(identifier, level, VASAUtil.STORAGE_ONLINE_ALARM_ID);
                } else {
                    event = new Event(identifier, level, VASAUtil.STORAGE_OFFILINE_ALARM_ID);
                }
                //StorageArray:8a2bafe1-4c81-449e-bb81-83dc281317b3
                event.setDeviceId("StorageArray:" + identifier.getDeviceID());
                event.setDeviceSN(identifier.getEventSN());
                savedAlarms.add(event);
            }

            toVcenterAlarms.addAll(0, savedAlarms);
            dataManager.setConfigAlarmForvCenter(ucUUID, toVcenterAlarms);
        }
    }

    public void addArrayInfoMap(String arrayId) {
        //增加阵列状态
        Map<String, DArrayInfo> arrayInfoMap = dataManager.getArrayInfoMap();
        DArrayInfo dArrayInfo = arrayInfoMap.get(arrayId);
        if (dArrayInfo == null) {
            dArrayInfo = new DArrayInfo();
            dArrayInfo.setPriority(100);
            dArrayInfo.setStatus("ONLINE");
        } else {
            dArrayInfo.setStatus("ONLINE");
        }
        arrayInfoMap.put(arrayId, dArrayInfo);
        dataManager.setArrayInfoMap(arrayInfoMap);
    }

    public void removeArrayInfoMap(String arrayId) {
        //移除阵列状态
        Map<String, DArrayInfo> arrayInfoMap = dataManager.getArrayInfoMap();
        DArrayInfo dArrayInfo = arrayInfoMap.get(arrayId);
        if (null != dArrayInfo) {
            dArrayInfo.setStatus("OFFLINE");
            arrayInfoMap.remove(arrayId);
        }
    }

    public void updateArrayInfoMapDevicePriority(String arrayId, int priority) {
        //更新阵列优先级
        LOGGER.info("In updateArrayInfoMapDevicePriority function, the arrayId = " + arrayId + " priority = " + priority);
        Map<String, DArrayInfo> arrayInfoMap = dataManager.getArrayInfoMap();
        DArrayInfo dArrayInfo = arrayInfoMap.get(arrayId);
        if (dArrayInfo == null) {
            LOGGER.error("Invalid arrayId = " + arrayId);
            return;
        }
        dArrayInfo.setPriority(priority);
        dataManager.setArrayInfoMap(arrayInfoMap);
        LOGGER.info("End updateArrayInfoMapDevicePriority function.");
    }

    public void updateArrayInfoMapDeviceStatus(String arrayId, String status) {
        //更新阵列状态
        LOGGER.info("update storage status arrayId = " + arrayId + " status = " + status);
        if (!StringUtils.isEmpty(status)) {
            status = status.toUpperCase();
        }
        Map<String, DArrayInfo> arrayInfoMap = dataManager.getArrayInfoMap();
        DArrayInfo dArrayInfo = arrayInfoMap.get(arrayId);
        if (dArrayInfo == null) {
            LOGGER.warn("Invalid arrayId = " + arrayId);
            dArrayInfo = new DArrayInfo();
            dArrayInfo.setPriority(100);
            dArrayInfo.setStatus(status);
        } else {
            dArrayInfo.setStatus(status);
        }
        arrayInfoMap.put(arrayId, dArrayInfo);
        dataManager.setArrayInfoMap(arrayInfoMap);
    }

    public void addStorageArrayIsLockInfo(String devId, boolean isLock) {
        DArrayIsLock arrayislock = new DArrayIsLock();
        arrayislock.setDeviceId(devId);
        arrayislock.setIsLock(isLock);
        Map<String, DArrayIsLock> arrayIsLockInfo = dataManager.getArrayIsLock();
        arrayIsLockInfo.put(devId, arrayislock);
        dataManager.setArrayIsLock(arrayIsLockInfo);
    }

    private Map<String, String> getDiskTypeFromStoragePool(S2DStoragePool s2dStoragePool) {
        Map<String, String> resultMap = new HashMap<String, String>();

        StringBuilder diskTypeSB = new StringBuilder();
        Set<String> diskTypeSet = new HashSet<String>();

        StringBuilder raidLevelSB = new StringBuilder();
        List<String> raidLevelList = new ArrayList<String>();

        if (!"0".equals(s2dStoragePool.getTier0disktype())) {
            if ("3".equals(s2dStoragePool.getTier0disktype()) || "10".equals(s2dStoragePool.getTier0disktype())) {
                diskTypeSet.add("SSD");
            }
            setRaidLevel(raidLevelList, s2dStoragePool, 0);
        }
        if (!"0".equals(s2dStoragePool.getTier1disktype())) {
            if ("1".equals(s2dStoragePool.getTier1disktype()) || "8".equals(s2dStoragePool.getTier1disktype())) {
                diskTypeSet.add("SAS");
            }
            setRaidLevel(raidLevelList, s2dStoragePool, 1);
        }
        if (!"0".equals(s2dStoragePool.getTier2disktype())) {
            if ("2".equals(s2dStoragePool.getTier2disktype()) || "4".equals(s2dStoragePool.getTier2disktype()) || "11".equals(s2dStoragePool.getTier2disktype())) {
                diskTypeSet.add("NL_SAS");
            }
            setRaidLevel(raidLevelList, s2dStoragePool, 2);
        }
        Iterator<String> it = diskTypeSet.iterator();
        while (it.hasNext()) {
            String str = it.next();
            diskTypeSB.append(str);
            if (it.hasNext()) {
                diskTypeSB.append("/");
            }
        }
        Iterator<String> iterator = raidLevelList.iterator();
        while (iterator.hasNext()) {
            String str = iterator.next();
            raidLevelSB.append(str);
            if (iterator.hasNext()) {
                raidLevelSB.append("/");
            }
        }
        resultMap.put("DiskType", diskTypeSB.toString());
        resultMap.put("RaidLevel", raidLevelSB.toString());
        return resultMap;

    }

    private void setRaidLevel(List<String> raidLevelSet, S2DStoragePool s2dStoragePool, int choose) {
        switch (choose) {
            case 0:
                if (!"0".equals(s2dStoragePool.getTier0RaidLv())) {
                    LOGGER.debug("The raidLevel0 is " + s2dStoragePool.getTier0RaidLv());
                    if ("1".equals(s2dStoragePool.getTier0RaidLv())) {
                        raidLevelSet.add("RAID 10");
                    }
                    if ("2".equals(s2dStoragePool.getTier0RaidLv())) {
                        raidLevelSet.add("RAID 5");
                    }
                    if ("3".equals(s2dStoragePool.getTier0RaidLv())) {
                        raidLevelSet.add("RAID 0");
                    }
                    if ("4".equals(s2dStoragePool.getTier0RaidLv())) {
                        raidLevelSet.add("RAID 1");
                    }
                    if ("5".equals(s2dStoragePool.getTier0RaidLv())) {
                        raidLevelSet.add("RAID 6");
                    }
                    if ("6".equals(s2dStoragePool.getTier0RaidLv())) {
                        raidLevelSet.add("RAID 50");
                    }
                    if ("7".equals(s2dStoragePool.getTier0RaidLv())) {
                        raidLevelSet.add("RAID 3");
                    }
                }
                break;
            case 1:
                if (!"0".equals(s2dStoragePool.getTier1RaidLv())) {
                    LOGGER.debug("The raidLevel1 is " + s2dStoragePool.getTier1RaidLv());
                    if ("1".equals(s2dStoragePool.getTier1RaidLv())) {
                        raidLevelSet.add("RAID 10");
                    }
                    if ("2".equals(s2dStoragePool.getTier1RaidLv())) {
                        raidLevelSet.add("RAID 5");
                    }
                    if ("3".equals(s2dStoragePool.getTier1RaidLv())) {
                        raidLevelSet.add("RAID 0");
                    }
                    if ("4".equals(s2dStoragePool.getTier1RaidLv())) {
                        raidLevelSet.add("RAID 1");
                    }
                    if ("5".equals(s2dStoragePool.getTier1RaidLv())) {
                        raidLevelSet.add("RAID 6");
                    }
                    if ("6".equals(s2dStoragePool.getTier1RaidLv())) {
                        raidLevelSet.add("RAID 50");
                    }
                    if ("7".equals(s2dStoragePool.getTier1RaidLv())) {
                        raidLevelSet.add("RAID 3");
                    }
                }
                break;
            case 2:
                if (!"0".equals(s2dStoragePool.getTier2RaidLv())) {
                    LOGGER.debug("The raidLevel2 is " + s2dStoragePool.getTier2RaidLv());
                    if ("1".equals(s2dStoragePool.getTier2RaidLv())) {
                        raidLevelSet.add("RAID 10");
                    }
                    if ("2".equals(s2dStoragePool.getTier2RaidLv())) {
                        raidLevelSet.add("RAID 5");
                    }
                    if ("3".equals(s2dStoragePool.getTier2RaidLv())) {
                        raidLevelSet.add("RAID 0");
                    }
                    if ("4".equals(s2dStoragePool.getTier2RaidLv())) {
                        raidLevelSet.add("RAID 1");
                    }
                    if ("5".equals(s2dStoragePool.getTier2RaidLv())) {
                        raidLevelSet.add("RAID 6");
                    }
                    if ("6".equals(s2dStoragePool.getTier2RaidLv())) {
                        raidLevelSet.add("RAID 50");
                    }
                    if ("7".equals(s2dStoragePool.getTier2RaidLv())) {
                        raidLevelSet.add("RAID 3");
                    }
                }
            default:
                break;
        }

    }

}
