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

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SimpleTimeZone;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.UUID;

import com.google.gson.Gson;

import org.opensds.vasa.base.common.VASAArrayUtil;
import org.opensds.vasa.base.common.VasaConstant;
import org.opensds.vasa.base.common.VasaSrcTypeConstant;
import org.opensds.vasa.common.ArrayErrCodeEnum;
import org.opensds.vasa.common.DeviceType;
import org.opensds.vasa.common.MOType;
import org.opensds.vasa.common.MagicNumber;
import org.opensds.vasa.domain.model.StorageModel;
import org.opensds.vasa.domain.model.VVolModel;
import org.opensds.vasa.domain.model.bean.DArray;
import org.opensds.vasa.domain.model.bean.DArrayInfo;
import org.opensds.vasa.domain.model.bean.DFileSystem;
import org.opensds.vasa.domain.model.bean.DLun;
import org.opensds.vasa.domain.model.bean.DPort;
import org.opensds.vasa.domain.model.bean.DProcessor;
import org.opensds.vasa.domain.model.bean.DStorageCapability;
import org.opensds.vasa.domain.model.bean.S2DAlarm;
import org.opensds.vasa.domain.model.bean.S2DArray;
import org.opensds.vasa.domain.model.bean.S2DBitmap;
import org.opensds.vasa.domain.model.bean.S2DController;
import org.opensds.vasa.domain.model.bean.S2DEnumInfo;
import org.opensds.vasa.domain.model.bean.S2DFCInitiator;
import org.opensds.vasa.domain.model.bean.S2DFileSystem;
import org.opensds.vasa.domain.model.bean.S2DHost;
import org.opensds.vasa.domain.model.bean.S2DHostLink;
import org.opensds.vasa.domain.model.bean.S2DISCSIInitiator;
import org.opensds.vasa.domain.model.bean.S2DLIT;
import org.opensds.vasa.domain.model.bean.S2DLun;
import org.opensds.vasa.domain.model.bean.S2DLunCopy;
import org.opensds.vasa.domain.model.bean.S2DLunCopyBean;
import org.opensds.vasa.domain.model.bean.S2DOemInfo;
import org.opensds.vasa.domain.model.bean.S2DPassThroughSnapshot;
import org.opensds.vasa.domain.model.bean.S2DStoragePool;
import org.opensds.vasa.domain.model.bean.S2DSystem;
import org.opensds.vasa.domain.model.bean.S2DSystemDSTConfig;
import org.opensds.vasa.domain.model.bean.S2DSystemTimeZone;
import org.opensds.vasa.domain.model.bean.S2DVirtualPoolSpaceStats;
import org.opensds.vasa.domain.model.bean.S2DVvolBind;
import org.opensds.vasa.domain.model.bean.StoragePolicy;
import org.opensds.vasa.domain.model.bean.StorageQosCreateBean;
import org.opensds.platform.common.SDKErrorCode;
import org.opensds.platform.common.SDKResult;
import org.opensds.platform.common.config.ConfigManager;
import org.opensds.platform.common.exception.SDKException;
import org.opensds.platform.common.utils.ApplicationContextUtil;
import org.opensds.platform.common.utils.Base64Utils;
import org.opensds.platform.common.utils.BytesUtils;
import org.opensds.platform.common.utils.StringUtils;
import org.opensds.platform.nemgr.itf.IDeviceManager;
import org.opensds.vasa.vasa.VasaArrayService;
import org.opensds.vasa.vasa.VasaNasArrayService;
import org.opensds.vasa.vasa.VasaNasArrayService.MigrateResult;
import org.opensds.vasa.vasa.VasaNasArrayService.QueryMigrateResult;
import org.opensds.vasa.vasa.common.VvolConstant;
import org.opensds.vasa.vasa.convert.VASAUtilDJConvert;
import org.opensds.vasa.vasa.db.model.NArrayCertificateSync;
import org.opensds.vasa.vasa.db.model.NProfileLevel;
import org.opensds.vasa.vasa.db.model.NStorageCapabilityQos;
import org.opensds.vasa.vasa.db.model.NStorageContainer;
import org.opensds.vasa.vasa.db.model.NStoragePool;
import org.opensds.vasa.vasa.db.model.NStorageProfile;
import org.opensds.vasa.vasa.db.model.NStorageQos;
import org.opensds.vasa.vasa.db.model.NTaskInfo;
import org.opensds.vasa.vasa.db.model.NUser;
import org.opensds.vasa.vasa.db.model.NVasaServiceCenter;
import org.opensds.vasa.vasa.db.model.NVirtualVolume;
import org.opensds.vasa.vasa.db.model.NVvolMetadata;
import org.opensds.vasa.vasa.db.model.NVvolProfile;
import org.opensds.vasa.vasa.db.model.StorageInfo;
import org.opensds.vasa.vasa.db.model.VvolPath;
import org.opensds.vasa.vasa.db.service.ArrayCertificateSyncService;
import org.opensds.vasa.vasa.db.service.ProfileLevelService;
import org.opensds.vasa.vasa.db.service.StorageCapabilityQosService;
import org.opensds.vasa.vasa.db.service.StorageContainerService;
import org.opensds.vasa.vasa.db.service.StorageManagerService;
import org.opensds.vasa.vasa.db.service.StoragePoolService;
import org.opensds.vasa.vasa.db.service.StorageProfileService;
import org.opensds.vasa.vasa.db.service.StorageQosService;
import org.opensds.vasa.vasa.db.service.TaskInfoService;
import org.opensds.vasa.vasa.db.service.UserManagerService;
import org.opensds.vasa.vasa.db.service.VasaEventService;
import org.opensds.vasa.vasa.db.service.VasaServiceCenterService;
import org.opensds.vasa.vasa.db.service.VirtualVolumeService;
import org.opensds.vasa.vasa.db.service.VvolMetadataService;
import org.opensds.vasa.vasa.db.service.VvolPathService;
import org.opensds.vasa.vasa.db.service.VvolProfileService;
import org.opensds.vasa.vasa.internal.Event;
import org.opensds.vasa.vasa.pool.service.ISelectPoolService;
import org.opensds.vasa.vasa.rest.bean.DeviceTypeMapper;
import org.opensds.vasa.vasa.rest.bean.QueryDBResponse;
import org.opensds.vasa.vasa.rest.bean.ResponseHeader;
import org.opensds.vasa.vasa.rest.resource.StorageManagerResource;
import org.opensds.vasa.vasa.runnable.VvolControlRunable;
import org.opensds.vasa.vasa.util.CopyDiffsTask;
import org.opensds.vasa.vasa.util.DataUtil;
import org.opensds.vasa.vasa.util.DateUtil;
import org.opensds.vasa.vasa.util.FaultUtil;
import org.opensds.vasa.vasa.util.IPUtil;
import org.opensds.vasa.vasa.util.JsonUtil;
import org.opensds.vasa.vasa.util.ListUtil;
import org.opensds.vasa.vasa.util.RestConstant;
import org.opensds.vasa.vasa.util.RestRequestMessage;
import org.opensds.vasa.vasa.util.RestUtilsOfOM;
import org.opensds.vasa.vasa.util.VASAUtil;
import org.opensds.vasa.vasa.util.VasaTask;
import org.opensds.vasa.vasa20.device.array.NFSvvol.INFSvvolService;
import org.opensds.vasa.vasa20.device.array.NFSvvol.NFSvvolCreateResBean;
import org.opensds.vasa.vasa20.device.array.NFSvvol.NFSvvolQueryResBean;
import org.opensds.vasa.vasa20.device.array.db.model.NStorageArrayBean;
import org.opensds.vasa.vasa20.device.array.db.service.StorageArrayService;
import org.opensds.vasa.vasa20.device.array.fileSystem.FileSystemSnapshotCreateResBean;
import org.opensds.vasa.vasa20.device.array.fileSystem.IFileSystemService;
import org.opensds.vasa.vasa20.device.array.logicalPort.LogicPortQueryResBean;
import org.opensds.vasa.vasa20.device.array.lun.LunCreateResBean;
import org.opensds.vasa.vasa20.device.array.qos.IDeviceQosService;
import org.opensds.vasa.vasa20.device.array.snapshot.IDeviceSnapshotService;
import org.opensds.vasa.vasa20.device.array.snapshot.SnapshotCreateResBean;
import org.opensds.vasa.common.EnumDefine;

import com.vmware.vim.vasa.v20.IncompatibleVolume;
import com.vmware.vim.vasa.v20.InvalidArgument;
import com.vmware.vim.vasa.v20.NotCancellable;
import com.vmware.vim.vasa.v20.NotFound;
import com.vmware.vim.vasa.v20.NotImplemented;
import com.vmware.vim.vasa.v20.OutOfResource;
import com.vmware.vim.vasa.v20.ResourceInUse;
import com.vmware.vim.vasa.v20.SnapshotTooMany;
import com.vmware.vim.vasa.v20.StorageFault;
import com.vmware.vim.vasa.v20.VasaProviderBusy;
import com.vmware.vim.vasa.v20.data.policy.capability.xsd.CapabilityInstance;
import com.vmware.vim.vasa.v20.data.policy.capability.xsd.PropertyInstance;
import com.vmware.vim.vasa.v20.data.policy.profile.xsd.StorageProfile;
import com.vmware.vim.vasa.v20.data.vvol.xsd.BatchErrorResult;
import com.vmware.vim.vasa.v20.data.vvol.xsd.BatchReturnStatus;
import com.vmware.vim.vasa.v20.data.vvol.xsd.BatchVirtualVolumeHandleResult;
import com.vmware.vim.vasa.v20.data.vvol.xsd.ContainerSpaceStats;
import com.vmware.vim.vasa.v20.data.vvol.xsd.PrepareSnapshotResult;
import com.vmware.vim.vasa.v20.data.vvol.xsd.ProtocolEndpoint;
import com.vmware.vim.vasa.v20.data.vvol.xsd.ProtocolEndpointInbandId;
import com.vmware.vim.vasa.v20.data.vvol.xsd.ProtocolEndpointTypeEnum;
import com.vmware.vim.vasa.v20.data.vvol.xsd.SpaceStats;
import com.vmware.vim.vasa.v20.data.vvol.xsd.TaskInfo;
import com.vmware.vim.vasa.v20.data.vvol.xsd.TaskStateEnum;
import com.vmware.vim.vasa.v20.data.vvol.xsd.VirtualVolumeBitmapResult;
import com.vmware.vim.vasa.v20.data.vvol.xsd.VirtualVolumeHandle;
import com.vmware.vim.vasa.v20.data.vvol.xsd.VirtualVolumeInfo;
import com.vmware.vim.vasa.v20.data.vvol.xsd.VirtualVolumeUnsharedChunksResult;
import com.vmware.vim.vasa.v20.data.xsd.EntityTypeEnum;
import com.vmware.vim.vasa.v20.data.xsd.HostInitiatorInfo;
import com.vmware.vim.vasa.v20.data.xsd.MountInfo;
import com.vmware.vim.vasa.v20.data.xsd.NameValuePair;
import com.vmware.vim.vasa.v20.data.xsd.StorageContainer;


interface FinalHander {
    void onFail(Object para);

    void onSucc(Object para);
}


public class DiscoverServiceImpl {
    private static DiscoverServiceImpl instance;

    private static org.apache.logging.log4j.Logger LOGGER = org.apache.logging.log4j.LogManager
            .getLogger(DiscoverServiceImpl.class);

    private StorageModel storageModel = new StorageModel();

    private DataUtil dataUtil = DataUtil.getInstance();
    private IDeviceManager deviceManager = (IDeviceManager) ApplicationContextUtil
            .getBean("deviceManager");
    private VVolModel vvolModel = new VVolModel();
    private VirtualVolumeService virtualVolumeService = (VirtualVolumeService) ApplicationContextUtil
            .getBean("virtualVolumeService");
    private VvolPathService vvolPathDBService = (VvolPathService) ApplicationContextUtil
            .getBean("vvolPathService");

    // private Profile2VolTypeService profile2VolTypeService =
    // (Profile2VolTypeService) ApplicationContextUtil
    // .getBean("profile2VolTypeService");
    private VasaNasArrayService vasaNasArrayService = (VasaNasArrayService) ApplicationContextUtil
            .getBean("vasaNasArrayService");
    private DataUtil dataManager = DataUtil.getInstance();

    private VvolMetadataService vvolMetadataService = (VvolMetadataService) ApplicationContextUtil
            .getBean("vvolMetadataService");

    private VvolProfileService vvolProfileService = (VvolProfileService) ApplicationContextUtil
            .getBean("vvolProfileService");

    private StoragePoolService storagePoolService = (StoragePoolService) ApplicationContextUtil
            .getBean("storagePoolService");

    private VasaEventService vasaEventService = (VasaEventService) ApplicationContextUtil.getBean("vasaEventService");

    private ISelectPoolService selectPoolService = (ISelectPoolService) ApplicationContextUtil
            .getBean("selectPoolService");

    private VasaArrayService vasaArrayService = (VasaArrayService) ApplicationContextUtil.getBean("vasaArrayService");

    private StorageProfileService storageProfileService = (StorageProfileService) ApplicationContextUtil
            .getBean("storageProfileService");

    private StorageManagerService storageManagerService = (StorageManagerService) ApplicationContextUtil
            .getBean("storageManagerService");

    private StorageQosService storageQosService = (StorageQosService) ApplicationContextUtil
            .getBean("storageQosService");

    private StorageCapabilityQosService storageCapabilityQosService = ApplicationContextUtil
            .getBean("storageCapabilityQosService");

    private StorageContainerService storageContainerService = (StorageContainerService) ApplicationContextUtil
            .getBean("storageContainerService");

    private TaskInfoService taskInfoService = ApplicationContextUtil.getBean("taskInfoService");

    private ProfileLevelService profileLevelService = ApplicationContextUtil.getBean("profileLevelService");

    private ArrayCertificateSyncService arrayCertificateSyncService = ApplicationContextUtil
            .getBean("arrayCertificateSyncService");

    private VasaServiceCenterService vasaServiceCenterService = ApplicationContextUtil
            .getBean("vasaServiceCenterService");

    // 用户管理
    private UserManagerService userManagerService = ApplicationContextUtil.getBean("userManagerService");

    // private FusionStorageInfoImpl fusionStorageInfoImpl = FusionStorageInfoImpl.getInstance();

    // private DiscoverService discoverService = DiscoverService.getInstance();
    // 用于拆分字符串的符号如VASA_SupportedFileSystem
    private static final String REGEX1 = ",";

//	private static final String VVOL_PREFIX = "rfc4122.";

//	private static final String VVOL_DATA_REAR = ".vmdk";
//
//	private static final String VVOL_SWAP_REAR = ".vswp";
//
//	private static final String VVOL_MEM_REAR = ".vmem";

    private static final String UTF8 = "utf-8";

//	private static final String SPACESELFADJUSTINGMODE = "2";//auto shrink and auto grow
//
//    private static final int  SNAPSHOTRESERVEPER = 0;  // no reserve
//
//    private static final String OWNINGCONTROLLER = ""; // no owning controller
//
//    private static final boolean ISCLONEFS = false;
//
//    private static final int AUTOSHRINKTHRESHOLDPERCENT = 50;
//
//    private static final int AUTOGROWTHRESHOLDPERCENT = 95;
//
//    //private static final Long MINAUTOSIZE = 0L;
//
//    private static final Long COMMONMAXAUTOSIZ =  2147483648L;  //common maxsize is 1T
//
//    private static final Long AUTOSIZEINCREMENT = 4194304L;  // increment is 2G  with sector
//
//    private static final Long COMMONFSINITSIZE = 51200L;//config init size is 50G
//
//    private static final int COMMONSECTORSIZE = 16384;  //bytes
//
//    private static final int DATASECTORSIZE = 8192;  //bytes

    /**
     * 每次请求最多获取多少条告警
     */
    private static final int EVENT_NUM_PER_FETCH = 100;

    /**
     * 单列
     *
     * @return DiscoverManager 返回结果
     */
    public static DiscoverServiceImpl getInstance() {
        if (null == instance) {
            instance = new DiscoverServiceImpl();
        }

        return instance;
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

    public VasaEventService getVasaEventService() {
        return vasaEventService;
    }

    public void setVasaEventService(VasaEventService vasaEventService) {
        this.vasaEventService = vasaEventService;
    }

    public List<DFileSystem> getStorageFileSystems(String arrayId, List<MountInfo> infos) throws StorageFault {
        List<Map<S2DFileSystem, MountInfo>> fileSystemS2DObjs = getFileSystemBySharePath(arrayId, infos);

        // 封装DFileSystem对象
        List<DFileSystem> storageFileSystems = new ArrayList<DFileSystem>(0);
        for (Map<S2DFileSystem, MountInfo> obj : fileSystemS2DObjs) {
            storageFileSystems.add(VASAUtilDJConvert.convert2DFileSystem(arrayId, obj));
        }
        return storageFileSystems;
    }

    private List<Map<S2DFileSystem, MountInfo>> getFileSystemBySharePath(String arrayId, List<MountInfo> infos)
            throws StorageFault {
        List<Map<S2DFileSystem, MountInfo>> res = new ArrayList<Map<S2DFileSystem, MountInfo>>();

        try {
            List<String> litipv4addrs = getLITIPV4ADDR(arrayId);
            for (MountInfo info : infos) {
                LOGGER.debug("getFileSystemBySharePath info:" + info.getFilePath() + "    " + info.getServerName());
                //通过共享名称查找共享路径  阵列上无法查询到该文件系统信息提示并跳过
                if (storageModel.getNFSSharePathByShareName(arrayId, info.getFilePath()).getResult() == null) {
                    LOGGER.error("can not find NFS sharePath by sharename: " + info.getFilePath());
                    continue;
                }
                String sharePath = storageModel.getNFSSharePathByShareName(arrayId, info.getFilePath()).getResult().replaceAll("/", "");

                SDKResult<String> count = storageModel.getFileSystemBySharePathCount(arrayId,
                        sharePath);
                if (0 != count.getErrCode()) {
                    LOGGER.error("StorageFault/getFileSystemBySharePathCount error. params[" + arrayId + ","
                            + info.getFilePath().replace("/", "") + "] errCode:" + count.getErrCode() + " description:"
                            + count.getDescription());
                    throw FaultUtil.storageFault("getFileSystemBySharePathCount error. params[" + arrayId + ","
                            + info.getFilePath().replace("/", "") + "] errCode:" + count.getErrCode() + " description:"
                            + count.getDescription());
                }
                if (!info.getFilePath().contains(".snapshot") && litipv4addrs.contains(info.getServerName())) {
                    SDKResult<List<S2DFileSystem>> s2dfs = storageModel.getFileSystemBySharePath(arrayId,
                            sharePath, count.getResult());
                    if (0 != s2dfs.getErrCode()) {
                        LOGGER.error("StorageFault/getFileSystemBySharePath error. params[" + arrayId + ","
                                + info.getFilePath().replace("/", "") + "," + count.getResult() + "] errCode:"
                                + s2dfs.getErrCode() + " description:" + s2dfs.getDescription());
                        throw FaultUtil.storageFault("getFileSystemBySharePath error. params[" + arrayId + ","
                                + info.getFilePath().replace("/", "") + "," + count.getResult() + "] errCode:"
                                + s2dfs.getErrCode() + " description:" + s2dfs.getDescription());
                    }
                    int size = s2dfs.getResult().size();
                    for (int i = 0; i < size; ++i) {
                        String name = s2dfs.getResult().get(i).getNAME();
                        if (name.equals(sharePath)) {
                            Map<S2DFileSystem, MountInfo> map = new HashMap<S2DFileSystem, MountInfo>();
                            map.put(s2dfs.getResult().get(i), info);
                            res.add(map);
                        }
                    }

                }
            }
        } catch (SDKException e) {
            LOGGER.error("StorageFault/getFileSystemBySharePath SDKException");
            throw FaultUtil.storageFault("getFileSystemBySharePath SDKException", e);
        }

        return res;
    }

    public List<String> getLITIPV4ADDR(String arrayId) throws StorageFault {
        List<String> litList = new ArrayList<String>();
        try {
            SDKResult<String> count = storageModel.getLITCount(arrayId);
            if (0 != count.getErrCode()) {
                LOGGER.error("StorageFault/getLITCount error. params[" + arrayId + "] errCode:" + count.getErrCode()
                        + " description:" + count.getDescription());
                throw FaultUtil.storageFault("getLITCount error. params[" + arrayId + "] errCode:" + count.getErrCode()
                        + " description:" + count.getDescription());
            }
            SDKResult<List<S2DLIT>> lits = storageModel.getLIT(arrayId, count.getResult());

            if (0 != lits.getErrCode()) {
                LOGGER.error("StorageFault/getLIT error. params[" + arrayId + "," + count.getResult() + "] errCode:"
                        + lits.getErrCode() + " description:" + lits.getDescription());
                throw FaultUtil.storageFault("getLIT error. params[" + arrayId + "," + count.getResult() + "] errCode:"
                        + lits.getErrCode() + " description:" + lits.getDescription());
            }
            List<S2DLIT> ls = lits.getResult();

            int len = ls.size();
            for (int i = 0; i < len; i++) {
                litList.add(ls.get(i).getIPV4ADDR());
            }
        } catch (SDKException e) {
            LOGGER.error("StorageFault/getLITCount error", e);
            throw FaultUtil.storageFault("getLITCount error", e);
        }
        return litList;
    }

    public void updateArrayIds() {
        try {
            List<StorageInfo> arrs = storageManagerService.queryInfo();

            Set<String> arrIds = new HashSet<String>();
            for (StorageInfo arr : arrs) {
                if (1 == arr.getDeleted()) {
                    continue;
                }
                if (arr.getDevicestatus().equalsIgnoreCase("ONLINE")) {
                    arrIds.add(arr.getId());
                }
            }
            DataUtil.getInstance().setArrayId(arrIds);

        } catch (Exception e) {
            LOGGER.error("getAllArray error!", e);
        }

    }

    /**
     * 查询oem信息 NOT TEST
     *
     * @param String arrayId
     * @return DArray
     */
    public DArray getStorageArrayByArrayID(String arrayId) {
        DArray storageArray = new DArray();

        // 查询StorageArray需要的数据，分别在SYSTEM和CONTROLLER中
        try {
            // 查询System信息
            SDKResult<S2DSystem> sysResult = storageModel.getSystemInfo(arrayId);
            if (0 != sysResult.getErrCode()) {
                LOGGER.error("getSystemInfo error. params[" + arrayId + "] errCode:" + sysResult.getErrCode()
                        + " description:" + sysResult.getDescription());
                return storageArray;
            }

            S2DSystem sysObj = sysResult.getResult();
            // 取到阵列的SN作为UUID的一部分
            String arrayName = sysObj.getNAME() + "(SN:" + sysObj.getID() + ")";
            String supportedBlock = sysObj.getVASA_SUPPORT_BLOCK();
            String supportedFileSystem = sysObj.getVASA_SUPPORT_FILESYSTEM();
            // String supportedFileSystem = "NFS";
            String supportedProfile = sysObj.getVASA_SUPPORT_PROFILE();

            // dorado 不包含vvolprofile 添加
            if (!supportedProfile.contains("VirtualVolumeProfile")) {
                supportedProfile = supportedProfile + ",VirtualVolumeProfile";
            }

            // String supportedProfile =
            // "FileSystemProfile,BlockDeviceProfile,CapabilityProfile";
            String alternatename = sysObj.getVASA_ALTERNATE_NAME();

            // 查询OEM厂商信息
            SDKResult<List<S2DOemInfo>> oemResult = storageModel.getAllOemInfo(arrayId);
            if (0 != oemResult.getErrCode()) {
                LOGGER.error("getAllOemInfo error. params[" + arrayId + "] errCode:" + oemResult.getErrCode()
                        + " description:" + oemResult.getDescription());
                return storageArray;
            }

            String vendorId = oemResult.getResult().get(0).getCMO_OEM_MANUFACTORY_INFO();

            // 封装StorageArray
            storageArray.setVendorId(vendorId);
            String modelId = DeviceTypeMapper.getProfuctModeName(sysObj.getPRODUCTMODE());
            StorageArrayService storageArrayService = ApplicationContextUtil.getBean("storageArrayService");
            List<NStorageArrayBean> arrays = storageArrayService.getAllStorageArray();
            for (int i = 0; i < arrays.size(); i++) {
                if (arrays.get(i).getArrayId().equals(arrayId)) {
                    modelId = arrays.get(i).getModel();
                    LOGGER.debug("set modelId:" + modelId);
                    break;
                }
            }

            storageArray.setModelId(modelId);
            storageArray.setUniqueIdentifier(VASAUtil.getStorageArrayUUID(arrayId));
            storageArray.setArrayName(arrayName);
            ListUtil.clearAndAdd(storageArray.getSupportedBlock(),
                    VASAUtil.convertToStringArray(supportedBlock, REGEX1));
            ListUtil.clearAndAdd(storageArray.getSupportedFileSystem(),
                    VASAUtil.convertToStringArray(supportedFileSystem, REGEX1));
            ListUtil.clearAndAdd(storageArray.getSupportedProfile(),
                    VASAUtil.convertToStringArray(supportedProfile, REGEX1));
            if (null == alternatename || "".equals(alternatename.trim())) {
                ListUtil.clearAndAdd(storageArray.getAlternateName(), new String[]{arrayName});
            } else {
                ListUtil.clearAndAdd(storageArray.getAlternateName(),
                        VASAUtil.convertToStringArrayWithSuffix(alternatename, REGEX1, sysObj.getID()));
            }
            if (dataUtil.getArrayInfoMap().containsKey(arrayId)) {
                storageArray.setPriority(dataUtil.getArrayInfoMap().get(arrayId).getPriority());
                storageArray.setFirmware(sysObj.getPRODUCTVERSION());
            } else {
                Map<String, DArrayInfo> arrayInfoMap = dataUtil.getArrayInfoMap();

                DArrayInfo dArrayInfo = new DArrayInfo();
                dArrayInfo.setPriority(100);
                dArrayInfo.setStatus("ONLINE");
                arrayInfoMap.put(arrayId, dArrayInfo);
                dataUtil.setArrayInfoMap(arrayInfoMap);
                storageArray.setPriority(dataUtil.getArrayInfoMap().get(arrayId).getPriority());
                storageArray.setFirmware(sysObj.getPRODUCTVERSION());
            }

        } catch (SDKException e) {
            LOGGER.error("Query storage Array Error.", e);
        }
        return storageArray;
    }

    /**
     * queryDeviceTimeZone 获取设备时区
     *
     * @param String arrayId
     * @return TimeZone
     */
    public TimeZone queryDeviceTimeZone(String arrayId) {

        TimeZone timeZone = TimeZone.getDefault();
        if (arrayId == null) {
            return timeZone;
        }

        S2DSystemTimeZone dataObj = null;
        SDKResult<List<S2DSystemTimeZone>> result = null;
        try {
            if (DeviceTypeMapper.getDeviceType(arrayId).equals(DeviceType.FusionStorage.toString())) {
                //result = fusionStorageInfoImpl.queryDeviceTimeZone(arrayId);
            } else {
                result = storageModel.queryDeviceTimeZone(arrayId);
            }
            if (null == result) {
                return timeZone;
            }
            if (0 != result.getErrCode()) {
                LOGGER.error("queryDeviceTimeZone error. params[" + arrayId + "] errCode:" + result.getErrCode()
                        + " description:" + result.getDescription());
                return timeZone;
            }
            dataObj = result.getResult().get(0);
        } catch (Exception e) {
            LOGGER.warn("queryDeviceTimeZone array id " + arrayId + " failed!", e);
            return timeZone;
        }

        try {
            return jsonObject2TimeZone(arrayId, dataObj);
        } catch (Exception e) {
            LOGGER.warn("jsonObject2TimeZone; array id " + arrayId + " failed!", e);
            return timeZone;
        }
    }

    public long getAddedTimeByArrayTimeZone(String arrayId, Date utcDate) {
        Date localDate = DateUtil.getLocalTimeFromUTC(utcDate);
        try {
            SDKResult<List<S2DSystemTimeZone>> result = storageModel.queryDeviceTimeZone(arrayId);
            if (0 != result.getErrCode()) {
                LOGGER.error("queryDeviceTimeZone error. params[" + arrayId + "] errCode:" + result.getErrCode()
                        + " description:" + result.getDescription());
                return localDate.getTime();
            }
            S2DSystemTimeZone dataObj = result.getResult().get(0);
            TimeZone timeZone = TimeZone.getTimeZone("GMT" + dataObj.getCMO_SYS_TIME_ZONE());
            return localDate.getTime() + timeZone.getRawOffset();
        } catch (SDKException e) {
            LOGGER.warn("queryDeviceTimeZone array id " + arrayId + " failed!", e);
            return localDate.getTime();
        }
    }

    /**
     * JSONObject2TimeZone
     *
     * @param arrayId
     * @param obj     ->S2DSystemTimeZone
     * @return
     * @throws SDKException
     */
    private TimeZone jsonObject2TimeZone(String arrayId, S2DSystemTimeZone obj) throws SDKException {

        // 时区，格式为UTC{+|-}hh:mm
        // String timeZone = obj.getCMO_SYS_TIME_ZONE();
        // 时区名称（时区ID）例如：America/Los_Angeles
        String timeZoneName = obj.getCMO_SYS_TIME_ZONE_NAME();
        // 时区名称的显示风格 0-短名称（三字母形式）1-长名称
        // int timeZoneNameStyle =
        // Integer.valueOf(obj.getCMO_SYS_TIME_ZONE_NAME_STYLE());
        // 此时区是否使用了夏令时 0-未使用 1-使用
        int useDST = Integer.valueOf(obj.getCMO_SYS_TIME_ZONE_USE_DST());
        TimeZone ttc = TimeZone.getTimeZone(timeZoneName);
        TimeZone tc = new SimpleTimeZone(ttc.getRawOffset(), timeZoneName);

        if (useDST == 1) {
            SDKResult<S2DSystemDSTConfig> dstResult = storageModel.getTimeZoneDST(arrayId, timeZoneName);
            if (0 != dstResult.getErrCode()) {
                LOGGER.error("getTimeZoneDST error. params[" + arrayId + "," + timeZoneName + "] errCode:"
                        + dstResult.getErrCode() + ", description:" + dstResult.getDescription());
            }
            S2DSystemDSTConfig dstObj = dstResult.getResult();
            // {"error":{"code":0},"data":
            // {"CMO_SYS_DST_CONF_MODE":"0",
            // "CMO_SYS_DST_CONF_TIME_ZONE_NAME":"America/New_York",
            // "CMO_SYS_DST_CONF_ADJUST_TIME":"60",
            // "CMO_SYS_DST_CONF_DATE_TIME_BEGIN":"03-13 02:00:00",
            // "CMO_SYS_DST_CONF_DATE_TIME_END":"11-06 02:00:00"}
            // }
            if (dstObj != null) {
                // 系统当前时区夏令时起始日期时间
                // String timeBegin =
                // dstObj.getCMO_SYS_DST_CONF_DATE_TIME_BEGIN();

                // 系统当前时区夏令时结束日期时间
                // String timeEnd = dstObj.getCMO_SYS_DST_CONF_DATE_TIME_END();
                // 夏令时调整时间 单位：分钟 范围：0-120分
                int adjustTime = Integer.valueOf(dstObj.getCMO_SYS_DST_CONF_ADJUST_TIME());

                ((SimpleTimeZone) tc).setDSTSavings(adjustTime * MagicNumber.INT60 * MagicNumber.INT1000);
            }
        } else {
            ((SimpleTimeZone) tc).setDSTSavings(1);
        }
        return tc;
    }

    /**
     * getStorageLuns
     *
     * @param context          context
     * @param hostInitiatorIds hostInitiatorIds
     * @return List<StorageLun>
     * @throws StorageFault if has error
     */
    public List<DLun> getStorageLuns(String arrayId, String[] hostInitiatorIds) throws StorageFault {
        // 根据主机启动器的ID找到主机ID
        String[] hostIDs = getHostIDByInitiatorID(arrayId, hostInitiatorIds);

        LOGGER.info("getStorageLuns arrayId:" + arrayId + ", hostIds:" + VASAUtil.convertArrayToStr(hostIDs));

        Set<S2DLun> lunJSONObjs = getLunByHostID(arrayId, hostIDs);

        // 封装StorageLun对象
        List<DLun> storageLuns = new ArrayList<DLun>(0);

        for (S2DLun obj : lunJSONObjs) {
            storageLuns.add(VASAUtilDJConvert.convert2DLun(arrayId, obj));
        }

        return storageLuns;
    }

    /**
     * getHostIDByInitiatorID
     *
     * @param deviceContext    deviceContext
     * @param hostInitiatorIds hostInitiatorIds
     * @return String[] hostids
     */
    public String[] getHostIDByInitiatorID(String arrayId, String[] hostInitiatorIds) {
        List<String> hostIDs = new ArrayList<String>(0);

        if (StringUtils.isEmpty(arrayId)) {
            return hostIDs.toArray(new String[hostIDs.size()]);
        }
        for (String decimalInitiatorId : hostInitiatorIds) {
            String hostInitiatorId = VASAUtil.portWWNDecimal2Hex(decimalInitiatorId);
            // iscsi
            // HVSC00需要转换 无法获取到主机ID
            try {
                SDKResult<S2DISCSIInitiator> iscsiResult = storageModel.getISCSIInitiator(arrayId, hostInitiatorId);
                if (0 == iscsiResult.getErrCode()) {
                    String hostId = iscsiResult.getResult().getPARENTID();
                    if (null != hostId && !hostIDs.contains(hostId)) {
                        hostIDs.add(hostId);
                    }
                } else {
                    LOGGER.debug("getISCSIInitiator error. params[" + arrayId + "," + hostInitiatorId + "] errCode:"
                            + iscsiResult.getErrCode() + ", description:" + iscsiResult.getDescription());
                }
            } catch (SDKException e) {
                LOGGER.warn("queryed iscsi initiator not found, initiator is " + hostInitiatorId);
            }
            // fc
            // HVSC00需要转换
            try {
                SDKResult<S2DFCInitiator> fcResult = storageModel.getFCInitiator(arrayId, hostInitiatorId);
                if (0 == fcResult.getErrCode()) {
                    String hostId = fcResult.getResult().getPARENTID();
                    if (null != hostId && !hostIDs.contains(hostId)) {
                        hostIDs.add(hostId);
                    }
                } else {
                    LOGGER.debug("getFCInitiator error. params[" + arrayId + "," + hostInitiatorId + "] errCode:"
                            + fcResult.getErrCode() + ", description:" + fcResult.getDescription());
                }
            } catch (SDKException e) {
                LOGGER.warn("queryed fc initiator not found, initiator is " + hostInitiatorId);
            }

            // ib
            // HVSC00需要转换
            try {
                SDKResult<S2DHost> ibResult = storageModel.getHostByIBInitiator(arrayId, hostInitiatorId);
                if (0 == ibResult.getErrCode()) {
                    String hostId = ibResult.getResult().getID();
                    if (null != hostId && !hostIDs.contains(hostId)) {
                        hostIDs.add(hostId);
                    }
                } else {
                    LOGGER.debug("getHostByIBInitiator error. params[" + arrayId + "," + hostInitiatorId + "] errCode:"
                            + ibResult.getErrCode() + ", description:" + ibResult.getDescription());
                }
            } catch (SDKException e) {
                LOGGER.warn("queryed host by ib initiator not found, initiator is " + hostInitiatorId);
            }

        }
        return hostIDs.toArray(new String[hostIDs.size()]);
    }

    /**
     * getLunByHostID
     *
     * @param deviceContext deviceContext
     * @param hostIds       hostIds
     * @return Set<JSONObject>
     */
    private Set<S2DLun> getLunByHostID(String arrayId, String[] hostIds) {
        Set<S2DLun> res = new TreeSet<S2DLun>(new LUNSorter());

        int size = 0;
        for (String hostId : hostIds) {
            SDKResult<List<S2DLun>> lunResult;
            try {
                lunResult = storageModel.getLunByHostID(arrayId, hostId);

                if (0 != lunResult.getErrCode()) {
                    LOGGER.error("getLunByHostID error. params[" + arrayId + "," + hostId + "] errCode:"
                            + lunResult.getErrCode() + " description:" + lunResult.getDescription());
                    continue;
                }
                size = lunResult.getResult().size();
                for (int i = 0; i < size; i++) {
                    res.add(lunResult.getResult().get(i));
                }
            } catch (SDKException e) {
                LOGGER.error("getLunByHostID error, array id is:" + arrayId);
            }
        }

        return res;
    }

    /**
     * 用于LUN排序的Comparator
     *
     * @author g00250185
     */
    private static class LUNSorter implements Comparator<S2DLun>, Serializable {

        /**
         * serialVersionUID
         */
        private static final long serialVersionUID = 1L;

        /**
         * 覆写compare方法
         *
         * @param o1
         * @param o2
         * @return
         */
        @Override
        public int compare(S2DLun o1, S2DLun o2) {

            String id1 = null;
            String id2 = null;
            id1 = o1.getID();
            id2 = o2.getID();
            return id1.compareTo(id2);

        }
    }

    /**
     * getStoragePortsByArrayId
     *
     * @param String             arrayId
     * @param hostInitiatorInfos hostInitiatorInfos
     * @return List<DPort>
     */
    public List<DPort> getStoragePortsByArrayId(String arrayId, HostInitiatorInfo[] hostInitiatorInfos) {
        List<DPort> storagePorts = new ArrayList<DPort>(0);
        if (StringUtils.isEmpty(arrayId)) {
            return storagePorts;
        }

        for (HostInitiatorInfo hostInitator : hostInitiatorInfos) {
            try {
                String[] params = parseSendParams(hostInitator);
                SDKResult<List<S2DHostLink>> result = storageModel.getHostLinkByInitiator(arrayId, params[0], params[1],
                        params[2]);
                if (0 != result.getErrCode()) {
                    LOGGER.error("getHostLinkByInitiator error. params[" + params[0] + "," + params[1] + "," + params[2]
                            + "] errCode:" + result.getErrCode() + " description:" + result.getDescription());
                    continue;
                }

                int len = result.getResult().size();
                for (int i = 0; i < len; i++) {
                    S2DHostLink obj = result.getResult().get(i);
                    storagePorts.add(VASAUtilDJConvert.convert2DPort(arrayId, obj));
                }
            } catch (StorageFault e) {
                LOGGER.error("getHostLinkByInitiator error," + hostInitator.getIscsiIdentifier() + ' '
                        + hostInitator.getNodeWwn() + ' ' + hostInitator.getPortWwn(), e);
            } catch (SDKException e) {
                LOGGER.error("getHostLinkByInitiator error," + hostInitator.getIscsiIdentifier() + ' '
                        + hostInitator.getNodeWwn() + ' ' + hostInitator.getPortWwn(), e);
            }
        }
        return storagePorts;
    }

    public Set<String> getHostIdByInitiator(String arrayId, List<HostInitiatorInfo> hostInitiatorInfos)
            throws StorageFault {
        String hostId = null;
        String hostName = null;
        Set<String> hostIdSet = new HashSet<String>();

        for (HostInitiatorInfo hostInitator : hostInitiatorInfos) {
            try {
                String[] params = parseSendParams(hostInitator);
                SDKResult<List<S2DHostLink>> result = storageModel.getHostLinkByInitiator(arrayId, params[0], params[1],
                        params[2]);
                if (0 != result.getErrCode()) {
                    LOGGER.debug("getHostLinkByInitiator error. params[" + arrayId + "," + params[0] + "," + params[1]
                            + "," + params[2] + "] errCode:" + result.getErrCode() + " description:"
                            + result.getDescription());
                    continue;
                }

                int len = result.getResult().size();
                if (0 == len) {
                    continue;
                }
                hostId = result.getResult().get(0).getPARENTID();
                hostName = result.getResult().get(0).getPARENTNAME();
                if (null != hostId && !StringUtils.isEmpty(hostName)) {
                    hostIdSet.add(hostId);
                } else {
                    List<HostInitiatorInfo> emptyHostInitiatorList = new ArrayList<HostInitiatorInfo>();
                    emptyHostInitiatorList.add(hostInitator);
                    LOGGER.error("hostId is null. hostInitator is: "
                            + VASAUtil.convertHostInitiators(emptyHostInitiatorList));
                }

                if (hostIdSet.size() > 1) {
                    StringBuffer errorInfo = new StringBuffer(
                            "multiple initiators of one ESX host are added onto multiple logic hosts.hostInitiatorInfo: "
                                    + VASAUtil.convertArrayToStr(VASAUtil.convertHostInitiators(hostInitiatorInfos)));
                    errorInfo.append(" hostIdSet size is: " + hostIdSet.size());
                    errorInfo.append(" hostId is: ");
                    for (String logHost : hostIdSet) {
                        errorInfo.append(logHost + ",");
                    }
                    errorInfo.deleteCharAt(errorInfo.length() - 1);
                    LOGGER.error(errorInfo.toString());
                    throw FaultUtil
                            .storageFault("multiple initiators of one ESX host are added onto multiple logic hosts");
                }

                if (hostId == null) {
                    LOGGER.warn("getHostLinkByInitiator parentId null. params[" + arrayId + "," + params[0] + ","
                            + params[1] + "," + params[2] + "]");
                    continue;
                }
                // break;
            } /*
             * catch (StorageFault e) { LOGGER.error(
             * "parseSendParams error," + hostInitator.getIscsiIdentifier()
             * + ' ' + hostInitator.getNodeWwn() + ' ' +
             * hostInitator.getPortWwn(), e); }
             */ catch (SDKException e) {
                LOGGER.error("getHostLinkByInitiator sdkException error," + hostInitator.getIscsiIdentifier() + ' '
                        + hostInitator.getNodeWwn() + ' ' + hostInitator.getPortWwn(), e);
            }

        }
        return hostIdSet;
    }

    /**
     * 返回3个参数依次是：启动器类型，0->启动器类型 ；1->启动器wwn ；2->启动器node_wwn
     *
     * @param hostIni
     * @return
     * @throws StorageFault
     */
    private static String[] parseSendParams(HostInitiatorInfo hostIni) throws StorageFault {
        List<String> params = new ArrayList<String>(MagicNumber.INT3);

        int portType = VASAUtil.convertVasaPortToIsmData(hostIni, true);
        params.add(portType == MOType.ETH_PORT.getValue() ? String.valueOf(MOType.ISCSI_INITIATOR.getValue())
                : String.valueOf(MOType.FC_INITIATOR.getValue()));

        // iscsi启动器
        if (MOType.ETH_PORT.getValue() == portType) {
            params.add(null == hostIni.getIscsiIdentifier() ? "" : hostIni.getIscsiIdentifier());
        }
        // FC类型的启动器
        else if (MOType.FC_PORT.getValue() == portType) {
            params.add(null == hostIni.getPortWwn() ? "" : VASAUtil.portWWNDecimal2Hex(hostIni.getPortWwn()));
        }

        params.add(null == hostIni.getNodeWwn() ? "" : VASAUtil.portWWNDecimal2Hex(hostIni.getNodeWwn()));
        return params.toArray(new String[params.size()]);
    }

    /**
     * getStorageProcessorByArrayID
     *
     * @param String deviceContext
     * @return List<DProcessor>
     */
    public List<DProcessor> getStorageProcessorByArrayID(String arrayId) {
        List<DProcessor> storageProcessors = new ArrayList<DProcessor>(0);
        DProcessor storageProcessor = null;
        if (StringUtils.isEmpty(arrayId)) {
            return storageProcessors;
        }

        try {
            SDKResult<List<S2DController>> result = storageModel.getAllController(arrayId);
            if (0 != result.getErrCode()) {
                LOGGER.error("getAllController error. params[" + arrayId + "] errCode:" + result.getErrCode()
                        + ", description:" + result.getDescription());
                return storageProcessors;
            }

            String controllerID = null;
            S2DController processorObj = null;
            String uuid = null;
            int len = result.getResult().size();
            for (int i = 0; i < len; i++) {
                storageProcessor = new DProcessor();
                processorObj = result.getResult().get(i);
                controllerID = processorObj.getID();
                uuid = VASAUtil.getUUID(arrayId, EntityTypeEnum.STORAGE_PROCESSOR.value(), controllerID);
                storageProcessor.setUniqueIdentifier(uuid);
                if (null != controllerID) {
                    ListUtil.clearAndAdd(storageProcessor.getSpIdentifier(),
                            new String[]{String.valueOf(controllerID)});
                    storageProcessors.add(storageProcessor);
                }
            }
        } catch (SDKException e) {
            LOGGER.error("Error occured when Query storage Processor.", e);
        }

        return storageProcessors;
    }

    /**
     * getStorageCapabilitiesByArrayID
     *
     * @param arrayId arrayId
     * @return List<DStorageCapability>
     */
    public List<DStorageCapability> getStorageCapabilitiesByArrayID(String arrayId) {
        List<DStorageCapability> caps = new ArrayList<DStorageCapability>(0);
        if (StringUtils.isEmpty(arrayId)) {
            return caps;
        }

        try {
            SDKResult<List<S2DEnumInfo>> result = storageModel.getAllStorageCapabilities(arrayId);
            if (0 != result.getErrCode()) {
                LOGGER.error("getAllStorageCapabilities error. params[" + arrayId + "] errCode:" + result.getErrCode()
                        + ", description:" + result.getDescription());
                return caps;
            }

            S2DEnumInfo capObj = null;
            int len = result.getResult().size();
            for (int i = 0; i < len; i++) {
                capObj = result.getResult().get(i);
                caps.add(VASAUtilDJConvert.convert2DStorageCapability(arrayId, capObj));
            }
        } catch (Exception e) {
            LOGGER.warn("query capbilities error.", e);
        }
        return caps;
    }

    /**
     * queryAssociatedLunsForPort
     *
     * @param String             arrayId
     * @param hostInitiatorInfos hostInitiatorInfos
     * @param portIds            portIds
     * @return StoragePort, List<StorageLun>
     * @throws StorageFault if has error
     */
    public Map<String, List<String>> queryAssociatedLunsForPort(String arrayId, HostInitiatorInfo[] hostInitiatorInfos,
                                                                String[] portIds) throws StorageFault {

        Map<String, List<String>> returnValues = new HashMap<String, List<String>>(0);

        if (StringUtils.isEmpty(arrayId)) {
            return returnValues;
        }

        // get host id by ini
        List<String> hostInitiatorIds = VASAUtil.convertHostInitiators(hostInitiatorInfos);
        String[] iniIds = hostInitiatorIds.toArray(new String[hostInitiatorIds.size()]);
        String[] hostIDs = getHostIDByInitiatorID(arrayId, iniIds);

        Map<String, Integer> portids = new TreeMap<String, Integer>();
        String[] params = null;
        for (HostInitiatorInfo hostInitator : hostInitiatorInfos) {
            params = parseSendParams(hostInitator);

            try {
                SDKResult<List<S2DHostLink>> result = storageModel.getHostLinkByInitiator(arrayId, params[0], params[1],
                        params[2]);
                if (0 != result.getErrCode()) {
                    LOGGER.error("getHostLinkByInitiator error. params[" + arrayId + "," + params[0] + "," + params[1]
                            + "," + params[2] + "] errCode:" + result.getErrCode() + ", description:"
                            + result.getDescription());
                    continue;
                }

                S2DHostLink portObj = null;
                int len = result.getResult().size();
                for (int i = 0; i < len; i++) {
                    portObj = result.getResult().get(i);
                    // portids.put(VASAUtilDJConvert.getPortId(portObj.getID()),Integer.valueOf(portObj.getTARGET_TYPE()));
                    portids.put(VASAUtilDJConvert.getPortId(portObj.getID(), portObj.getTARGET_ID(),
                            portObj.getTARGET_PORT_WWN()), Integer.valueOf(portObj.getTARGET_TYPE()));
                }
            } catch (SDKException e) {
                LOGGER.error("error occured when querying host link", e);
            }
        }

        for (String hostID : hostIDs) {
            getLunByHostAndPort(arrayId, hostID, portids, returnValues);
        }

        return returnValues;
    }

    /**
     * getLunByHostAndPort
     *
     * @param String       arrayId
     * @param hostid       hostid
     * @param portids      portids
     * @param returnValues returnValues
     */
    private void getLunByHostAndPort(String arrayId, String hostid, Map<String, Integer> portids,
                                     Map<String, List<String>> returnValues) {
        if (StringUtils.isEmpty(arrayId)) {
            return;
        }
        // key is portid ,value is porttype
        Iterator<Map.Entry<String, Integer>> iter = portids.entrySet().iterator();
        Map.Entry<String, Integer> entry = null;
        String portid = null;
        int porttype = 0;
        String strPorttype = null;
        StringBuilder sb = null;
        String metadata = null;
        SDKResult<List<S2DLun>> result = null;
        List<S2DLun> dataArray = null;
        List<String> lunuids = null;
        int len = 0;
        String lunid = null;
        String portuid = null;
        while (iter.hasNext()) {
            entry = iter.next();
            portid = entry.getKey();
            porttype = entry.getValue();
            try {
                strPorttype = VASAUtilDJConvert.getPortType(porttype);
            } catch (SDKException e2) {
                LOGGER.info("getPortType failed : porttype: " + porttype);
            }
            sb = new StringBuilder();
            // {\"ports\":{\"{1}\":[\"{2}\"]}}
            sb.append("{\"ports\":{\"").append(strPorttype).append("\":[\"").append(portid).append("\"]}}");
            metadata = sb.toString();

            try {
                metadata = URLEncoder.encode(metadata, UTF8);
            } catch (UnsupportedEncodingException e1) {
                LOGGER.debug("Unknown encoding " + UTF8, e1);
                continue;
            }

            try {
                result = storageModel.getLunByHostAndPort(arrayId, hostid, metadata);
            } catch (SDKException e) {
                LOGGER.warn("getLunByHostAndPort failed!", e);
                continue;
            }
            if (0 != result.getErrCode()) {
                LOGGER.warn("getLunByHostAndPort error. params[" + arrayId + "," + hostid + "," + metadata
                        + "] errCode:" + result.getErrCode() + ", description:" + result.getDescription());
                continue;
            }

            dataArray = result.getResult();

            lunuids = new ArrayList<String>(0);
            len = dataArray.size();

            for (int i = 0; i < len; i++) {
                lunid = dataArray.get(i).getID();

                lunuids.add(VASAUtil.getUUID(arrayId, EntityTypeEnum.STORAGE_LUN.value(), lunid));
            }
            portuid = VASAUtil.getUUID(arrayId, EntityTypeEnum.STORAGE_PORT.value(), portid);
            addToResult(portuid, lunuids, returnValues);
        }
    }

    /**
     * addToResult
     *
     * @param portuid      portuid
     * @param lunuids      lunuids
     * @param returnValues returnValues
     */
    private void addToResult(String portuid, List<String> lunuids, Map<String, List<String>> returnValues) {
        List<String> existlunids = returnValues.get(portuid);
        if (existlunids == null) {
            returnValues.put(portuid, lunuids);
        } else {
            for (String nlun : lunuids) {
                if (!existlunids.contains(nlun)) {
                    existlunids.add(nlun);
                }
            }
        }
    }

    public String queryArraySnByArrayId1(String arrayId) {
        try {
            SDKResult<S2DArray> result = storageModel.getArrayById(arrayId);
            if (0 != result.getErrCode()) {
                LOGGER.error("getArrayById error. params[" + arrayId + "] errCode:" + result.getErrCode()
                        + ", description:" + result.getDescription());
                return null;
            }

            return result.getResult().getSn();
        } catch (SDKException e) {
            LOGGER.error("getArrayById SDKException.");
        }

        return null;
    }

    public String queryArraySnByArrayId(String arrayId) {
        QueryDBResponse response = new QueryDBResponse();
        List<StorageInfo> list = new ArrayList<StorageInfo>();
        StorageManagerResource storageManagerResource = new StorageManagerResource();
        response = storageManagerResource.queryData();
        list = response.getAllStorageInfo();
        for (int i = 0; i < list.size(); i++) {
            if (arrayId.equals(list.get(i).getId().trim())) {
                return list.get(i).getSn();
            }
        }
        return null;
    }

    public S2DArray queryArrayById(String arrayId) {
        try {
            SDKResult<S2DArray> arrayResult = storageModel.getArrayById(arrayId);
            if (0 != arrayResult.getErrCode()) {
                LOGGER.error("getArrayById error. params[" + arrayId + "], errCode:" + arrayResult.getErrCode()
                        + ", description:" + arrayResult.getDescription());
                return null;
            }

            return arrayResult.getResult();
        } catch (SDKException e) {
            LOGGER.error("queryEventsAfterCreatedTime getArrayById error. arrayId:" + arrayId);
            return null;
        }
    }

    public List<Event> queryEventsAfterSpecificTime(String arrayId, long creationTime, int maxNum) {
        List<Event> allEvents = new ArrayList<Event>(0);
        if (StringUtils.isEmpty(arrayId)) {
            return allEvents;
        }

        // LOGGER.info("arrayId:" + arrayId + ", query events after specific
        // time:" + creationTime);
        long curstart = 0; // 告警
        String[] inputParams = new String[MagicNumber.INT2];
        S2DAlarm oneEventObj = null;
        Event event = null;
        int len = 0;
        while (true) {
            inputParams[0] = String.valueOf(curstart);
            if (maxNum >= EVENT_NUM_PER_FETCH) {
                inputParams[1] = String.valueOf(curstart + EVENT_NUM_PER_FETCH);
                maxNum -= EVENT_NUM_PER_FETCH;
            } else {
                inputParams[1] = String.valueOf(curstart + maxNum);
                maxNum = 0;
            }

            SDKResult<List<S2DAlarm>> result = null;
            try {
                LOGGER.info("getRecentEvent range=[" + inputParams[0] + "-" + inputParams[1] + "]");
                result = storageModel.getRecentEvent(arrayId, inputParams[0], inputParams[1]);

                if (0 != result.getErrCode()) {
                    LOGGER.warn("getRecentEvent error. params[" + arrayId + "," + inputParams[0] + "," + inputParams[1]
                            + "] errCode:" + result.getErrCode() + ", description:" + result.getDescription());
                    // return allEvents;
                    break;
                }

            } catch (Exception e) {
                LOGGER.error("query all events or alarms failed.", e);
                break;
            }

            boolean isQueryEnd = false;
            len = result.getResult().size();
            for (int i = 0; i < len; i++) {
                oneEventObj = result.getResult().get(i);
                if ((oneEventObj.getStartTime()) * MagicNumber.LONG1000 > creationTime) {
                    event = VASAUtilDJConvert.convert2Event(arrayId, oneEventObj);
                    allEvents.add(event);
                } else {
                    isQueryEnd = true;
                    break;
                }

            }
            if (isQueryEnd || 0 == maxNum) {
                break;
            }
            if (len < EVENT_NUM_PER_FETCH) {
                // 此次数据没有EVENT_NUM_PER_FETCH条
                break;
            } else {
                curstart = curstart + EVENT_NUM_PER_FETCH;
            }
        }
        // result = storageModel.getRecentEvent(arrayId);
        return allEvents;
    }

    /**
     * queryAllEvents
     *
     * @param arrayid arrayid
     * @return List<Event>
     */
    public List<Event> queryAllEvents(String arrayid) {
        List<Event> events = queryAllEventsOrAlarmsHelper(arrayid, false);
        LOGGER.debug("got events size is " + events.size());
        return events;
    }

    /**
     * 查询告警或者事件的辅助函数
     *
     * @param deviceContext
     * @param isQueryAlarms 是否是查询告警
     * @return list<Event>
     */
    private List<Event> queryAllEventsOrAlarmsHelper(String arrayid, boolean isQueryAlarms) {
        List<Event> allEvents = new ArrayList<Event>(0);
        if (StringUtils.isEmpty(arrayid)) {
            return allEvents;
        }
        long curMaxSN = 0;
        long curstart = 0; // 告警
        String[] inputParams = new String[MagicNumber.INT2];
        S2DAlarm oneEventObj = null;
        Event event = null;
        int len = 0;
        while (true) {
            inputParams[0] = String.valueOf(curstart);
            inputParams[1] = String.valueOf(curstart + EVENT_NUM_PER_FETCH);

            SDKResult<List<S2DAlarm>> result = null;
            try {
                if (isQueryAlarms) {
                    result = storageModel.getAllAlarm(arrayid, inputParams[0], inputParams[1]);
                } else {
                    result = storageModel.getAllEvent(arrayid, inputParams[0], inputParams[1]);
                }

                if (0 != result.getErrCode()) {
                    LOGGER.warn("getAllAlarm or getAllEvent error. params[" + arrayid + "," + inputParams[0] + ","
                            + inputParams[1] + "] errCode:" + result.getErrCode() + ", description:"
                            + result.getDescription());
                    // return allEvents;
                    break;
                }

            } catch (Exception e) {
                LOGGER.debug("query all events or alarms failed.", e);
                break;
            }
            len = result.getResult().size();
            for (int i = 0; i < len; i++) {
                oneEventObj = result.getResult().get(i);
                event = VASAUtilDJConvert.convert2Event(arrayid, oneEventObj);
                allEvents.add(event);
            }
            if (len < EVENT_NUM_PER_FETCH) {
                // 此次数据没有EVENT_NUM_PER_FETCH条
                break;
            } else {
                curstart = curstart + EVENT_NUM_PER_FETCH;
            }
        }
        // storageModel.getAllAlarm(arrayid,
        curMaxSN = VASAUtil.findMaxEventSN(allEvents, arrayid);
        // 如果是查询告警 则添加thinlun告警
        if (isQueryAlarms) {
            LOGGER.debug("Start query thin lun alarms for HVS/TV2R2,original events size is " + allEvents.size());
            allEvents.addAll(queryThinLUNEvents(arrayid, curMaxSN));
            LOGGER.debug("End of query thin lun alarms,and size is " + allEvents.size());
        }

        return allEvents;
    }

    /**
     * queryThinLUNEvents
     *
     * @param arrayid  arrayid
     * @param curMaxSN 当前最大流水号
     * @return List<Event> list
     */
    private List<Event> queryThinLUNEvents(String arrayid, long curMaxSN) {
        List<Event> events = new ArrayList<Event>(0);
        if (StringUtils.isEmpty(arrayid)) {
            return events;
        }

        try {
            SDKResult<List<S2DLun>> result = storageModel.getThinLun(arrayid);

            if (0 != result.getErrCode()) {
                LOGGER.error("getThinLun error. params[" + arrayid + "] errCode:" + result.getErrCode()
                        + ", description:" + result.getDescription());
                return events;
            }
            int len = result.getResult().size();
            Event event = null;
            Event.Identifier identifier = null;
            Event.Level level = null;
            int lunId = 0;
            int useRate = 0;
            StringBuilder paras = null;
            S2DLun lunObj = null;
            for (int i = 0; i < len; i++) {
                lunObj = result.getResult().get(i);
                // Thin ID
                lunId = Integer.parseInt(lunObj.getID());

                level = Event.Level.valueOf(Integer.valueOf(lunObj.getCAPACITYALARMLEVEL()));
                // Thin LUN 使用率
                useRate = Integer.valueOf(lunObj.getTHINCAPACITYUSAGE());

                paras = new StringBuilder();
                paras.append(lunId);
                paras.append(",");
                paras.append(useRate);
                identifier = new Event.Identifier(arrayid, ++curMaxSN);
                event = new Event(identifier, level, VASAUtil.THIN_LUN_ALARM_ID, paras.toString());

                // for json serialize
                event.setDeviceId(identifier.getDeviceID());
                event.setDeviceSN(identifier.getEventSN());

                LOGGER.debug("Got a thin lun,lunid is " + lunId + ",alarm level is " + useRate + " alarmlevel is "
                        + level.getValue());
                events.add(event);
            }
        } catch (Exception e) {
            LOGGER.error("query thinLunAlarm error.", e);
        }
        LOGGER.debug("query thin lun events called,curmax sn is " + curMaxSN);
        return events;
    }

    /**
     * queryAllAlarms
     *
     * @param arrayid
     * @return List<Event>
     * @see
     */
    public List<Event> queryAllAlarms(String arrayid) {
        List<Event> eventsAndAlarms = queryAllEventsOrAlarmsHelper(arrayid, true);
        LOGGER.debug("got events And alarms size is " + eventsAndAlarms.size());
        return eventsAndAlarms;
    }

    public List<StorageProfile> getAllCapabilityProfile() throws StorageFault {
        List<StorageProfile> storageProfiles = new ArrayList<StorageProfile>();

        try {
            // result中不会包含policy_开头的volume type, getAllVolumeType返回前会进行过滤
            // 新版本使用omCreated字段过滤
            NStorageProfile profile = new NStorageProfile();
            profile.setOmCreated(true);
            profile.setDeleted(false);
            List<NStorageProfile> nStorageProfiles = storageProfileService.search(profile);
            for (NStorageProfile nStorageProfile : nStorageProfiles) {
                String profileName = nStorageProfile.getProfileName();
                if (!profileName.startsWith("policy_")) {
                    NStorageQos storageQos = new NStorageQos();
                    NProfileLevel profileLevel = new NProfileLevel();
                    if (nStorageProfile.getControlType().equals(NStorageProfile.ControlType.level_control)) {
                        String profileLevelId = nStorageProfile.getControlTypeId();
                        if (null != profileLevelId && !profileLevelId.equals("")) {
                            profileLevel = profileLevelService.getById(profileLevelId);
                        }
                    } else if (nStorageProfile.getControlType().equals(NStorageProfile.ControlType.precision_control)) {
                        String smartQosId = nStorageProfile.getControlTypeId();
                        if (null != smartQosId && !smartQosId.equals("")) {
                            storageQos = storageQosService.queryQosById(smartQosId);
                        }

                    }
                    StorageProfile storageProfile = VASAUtilDJConvert.convert2StorageProfile(nStorageProfile,
                            storageQos, profileLevel);
                    if (null != storageProfile) {
                        storageProfiles.add(storageProfile);
                    }
                }
            }
            return storageProfiles;
        } catch (Exception e) {
            LOGGER.error("StorageFault/getAllCapabilityProfile error.", e);
            throw FaultUtil.storageFault("getAllCapabilityProfile error.");
        }

    }

    public List<StorageContainer> getAllStorageContainer() throws StorageFault {
        LOGGER.debug("In getAllStorageContainer function");
        List<StorageContainer> storageContainers = new ArrayList<StorageContainer>();
        try {
            List<NStorageContainer> containers = storageContainerService.getAll();
            LOGGER.debug("containers :" + containers);
            for (NStorageContainer nStorageContainer : containers) {
                storageContainers.add(VASAUtilDJConvert.convert2StorageContainer(nStorageContainer));
            }
        } catch (Exception e) {
            LOGGER.error("StorageFault/getAllVirtualPool error.");
            throw FaultUtil.storageFault("getAllVirtualPool error.");
        }
        LOGGER.debug("End getAllStorageContainer function, the storageContainers :" + storageContainers);
        return storageContainers;
    }

    public List<String> getUniqueIdentifiersForStorageContainer(String arrayId) throws StorageFault {
        LOGGER.debug("In getUniqueIdentifiersForStorageContainer function, the arrayId = " + arrayId);
        List<String> containerIds = new ArrayList<String>();
        try {
            List<NStorageContainer> containers = storageContainerService.getAll();
            for (NStorageContainer nStorageContainer : containers) {
                List<NStoragePool> storagePools = storagePoolService
                        .getPoolListByContainerId(nStorageContainer.getContainerId());
                if (storagePools.size() != 0) {

                    int lostNum = 0;
                    for (int i = 0; i < storagePools.size(); i++) {
                        LOGGER.debug("toragePool:" + storagePools.get(i).toString());
                        if ("Lost".equalsIgnoreCase(storagePools.get(i).getDeviceStatus())) {
                            lostNum++;
                        }
                    }
                    LOGGER.debug(" lost storage pool nameber is:" + lostNum);
                    if (storagePools.get(0).getArrayId().equals(arrayId) && storagePools.size() > lostNum) {
                        containerIds.add(nStorageContainer.getContainerId());
                    }
                }
            }

        } catch (Exception e) {
            LOGGER.error("StorageFault/getVirtualPoolByArrayId error.");
            throw FaultUtil.storageFault("getVirtualPoolByArrayId error.");
        }
        LOGGER.debug("End getUniqueIdentifiersForStorageContainer function, the containerIds = " + containerIds);
        return containerIds;
    }

    public void setVolumeRetype(String volId, StoragePolicy storagePolicy, StorageProfile newProfile,
                                String uniProfileId, String migrationPolicy) throws StorageFault {
        try {
            // 1、根据volId查询到对应的QosId
            String oldProfileRawQosId = storageProfileService.getCurrentProfileRawQosIdByVvolId(VasaConstant.VVOL_PREFIX + volId);
            LOGGER.debug("getProfileByVvolId volId=" + volId + ",oldProfileRawQosId=" + oldProfileRawQosId);
            NVirtualVolume virtualVolumeByVvolId = virtualVolumeService.getVirtualVolumeByVvolId(VasaConstant.VVOL_PREFIX + volId);
            LOGGER.debug("setVolumeRetype vvol=" + virtualVolumeByVvolId);
            // 2、查询到老的QosId,并且将LunId剔除掉
            if (null != oldProfileRawQosId) {
                delOldQos(oldProfileRawQosId, virtualVolumeByVvolId);
            }
            int ioProperty = getIOPropertyByQos(storagePolicy, virtualVolumeByVvolId.getVvolType());
            SDKErrorCode updateLun = vasaArrayService.updateLun(virtualVolumeByVvolId.getLunId(), ioProperty,
                    storagePolicy.getSmartTier());
            if (updateLun.getErrCode() != 0) {
                LOGGER.error("Update Lun err.lunId = " + virtualVolumeByVvolId.getLunId() + ",errCode="
                        + updateLun.getErrCode() + ",errMsg=" + updateLun.getDescription());
                throw FaultUtil.storageFault("updateLun error! errMsg=" + updateLun.getDescription());
            }
            // 3.将lunId与新Qos组做绑定
            vasaArrayService.createStorageProfile(storagePolicy, newProfile.getProfileId(), uniProfileId,
                    newProfile.getName(), virtualVolumeByVvolId.getLunId(), newProfile.getGenerationId(), null,
                    virtualVolumeByVvolId.getVvolType());
            // return result.getResult();
        } catch (Exception e) {
            LOGGER.error("StorageFault/setVolumeRetype error.", e);
            throw FaultUtil.storageFault("setVolumeRetype error.");
        }
    }

    private void delOldQos(String oldProfileRawQosId, NVirtualVolume virtualVolumeByVvolId)
            throws StorageFault, SDKException {
        // 1、将LUNID从QOS组中删除
        vasaArrayService.delLunToQos(oldProfileRawQosId, virtualVolumeByVvolId.getLunId());
        // 2、将QOS组置为非激活状态
        IDeviceQosService deviceQosService = VVolModel.getDeviceManager().getDeviceServiceProxy(VASAUtil.getArrayId(),
                IDeviceQosService.class);
        SDKErrorCode deactiveQos = deviceQosService.deactiveQos(VASAUtil.getArrayId(), oldProfileRawQosId);
        if (0 != deactiveQos.getErrCode()) {
            LOGGER.error("deactiveQos err,please check it on array. rawQosId=" + oldProfileRawQosId + ",msg="
                    + deactiveQos.getDescription());
        } else {
            SDKErrorCode delQos = deviceQosService.delQos(VASAUtil.getArrayId(), oldProfileRawQosId);
            if (0 != delQos.getErrCode()) {
                LOGGER.error("delQos err,please check it on array. rawQosId=" + oldProfileRawQosId + ",msg="
                        + delQos.getDescription());
            }
        }
    }

    // profile_to_insert表示上层传下来了完整的profile
    public TaskInfo createVirtualVolume(long sizeInMB, StoragePolicy storagePolicy, StorageProfile storageProfile,
                                        List<NameValuePair> metadata, String containerId, String vvolType, Boolean removeThin,
                                        StorageProfile profile_to_insert, String thinValue) throws StorageFault, OutOfResource {
        // 1、获取当前container下有效的storagePool通过 containerId
        List<S2DStoragePool> containerPools = getStoragePoolByContainerId(sizeInMB, containerId, thinValue);

        // 2、通过数据查询对应符合的storagePool
        S2DStoragePool nStoragePool = selectPoolService.selectPool(containerPools, storagePolicy, sizeInMB);
        if (nStoragePool == null) {
            throw FaultUtil.storageFault("select storagepool  error ! containerId=" + containerId);
        }

        LOGGER.info("createVirtualVolume, select storagepool rawId = " + nStoragePool.getID());
        TaskInfo taskInfo = new TaskInfo();
        String creationTime = null;
        try {
            int sizeInGB = (sizeInMB % 1024 == 0) ? (int) sizeInMB / 1024 : ((int) sizeInMB / 1024 + 1);
            String vvolDisplayName = VASAUtil.buildVvolDisplayName(vvolType, metadata);

            VvolControlRunable.getInstance(virtualVolumeService).incrDjTask();
            LOGGER.info("traffic control,create vvol task increase once. current is: "
                    + VvolControlRunable.getInstance(virtualVolumeService).getDjTask() + "/"
                    + VvolConstant.MAX_SEND_DJ);
            // 评估description内容

            String vmName = VASAUtil.getRegularVMName(metadata);
            String vvolId = UUID.randomUUID().toString();

            //add description for ngc
            String description = VasaConstant.VVOL_PREFIX + vvolId;

            // 保存基本数据，包含vvolProfile,vvolMetadata,VirtualVolume(状态为creating)
            String profileId = storageProfile.getProfileId();
            String profileName = storageProfile.getName();
            long generationId = storageProfile.getGenerationId();
            if (profileId.equalsIgnoreCase("5200177B-98B7-4A0D-BFB5-11C02CE223E6") && (null != profile_to_insert)) {
                // This profile is the defaultProfile in vasa db, not the
                // profile from vcenter.
                profileId = profile_to_insert.getProfileId();
                profileName = profile_to_insert.getName();
                generationId = profile_to_insert.getGenerationId();
            }
            String uniProfileId = getUniProfileId(profileId);
            insertCreateVirtualVolumeData2DataBase(vvolId, sizeInMB, nStoragePool.getID(), storageProfile, uniProfileId,
                    metadata, containerId, vvolType, removeThin, profile_to_insert);
            int ioProperty = getIOPropertyByQos(storagePolicy, vvolType);

            String lunId = null;
            String wwn = null;
            if (VASAUtil.isNasContainer()) {
                SDKResult<NFSvvolCreateResBean> fileRsp = vasaNasArrayService.createFile(vvolType, vvolId, nStoragePool.getID(), sizeInMB);

                if (fileRsp.getErrCode() != 0) {
                    LOGGER.error("createfile error.");
                    virtualVolumeService.updateStatusByVvolId(VasaConstant.VVOL_PREFIX + vvolId,
                            VASAArrayUtil.VVOLSTATUS.error_creating);
                    throw FaultUtil.storageFault("createfile error.");
                }
                // nd_todo wwn和lunId

                taskInfo.setTaskState(TaskStateEnum.SUCCESS.value());
                updateVirtualVolume(vvolId, "NA", "NA", VASAArrayUtil.VVOLSTATUS.available);
            } else {
                LunCreateResBean resData = createLun(vvolDisplayName, vvolId, description, sizeInGB,
                        sizeInMB, vmName, nStoragePool, thinValue, storagePolicy, ioProperty);
                lunId = resData.getID();
                wwn = resData.getWWN();
                taskInfo.setTaskState(TaskStateEnum.RUNNING.value());
                updateVirtualVolume(vvolId, lunId, wwn, VASAArrayUtil.VVOLSTATUS.initing);
            }
            // 更改卷状态为creating,并且将获取的lunid 与 wwn存入数据库中

            //LunCreateResBean resData = createLun.getResult();
            Map<String, String> taskProperties = new HashMap<>();
            createTaskPolicy(storagePolicy, profileName, generationId, profileId, uniProfileId, taskProperties);

            taskInfo.setName("createVirtualVolume");
            taskInfo.setCancelable(false);
            taskInfo.setCancelled(false);
            Date date = getCurrentTimeFormat();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            taskInfo.setStartTime(calendar);
            taskInfo.setTaskId("createVirtualVolume:" + VasaConstant.VVOL_PREFIX + vvolId);
            // 任务成功之后再返回,表示已经执行完成
            taskInfo.setResult(VasaConstant.VVOL_PREFIX + vvolId);
            taskInfoService.saveTaskInfo(taskInfo, taskProperties);
            return taskInfo;
        } catch (ParseException e) {
            LOGGER.error("StorageFault/createVirtualVolume parse time error. time is : " + creationTime);
            throw FaultUtil.storageFault("createVirtualVolume parse time error. time is : " + creationTime);
        }
    }

    private LunCreateResBean createLun(String vvolDisplayName, String vvolId, String description, int sizeInGB, long sizeInMB, String vmName,
                                       S2DStoragePool nStoragePool, String thinValue, StoragePolicy storagePolicy, int ioProperty) throws StorageFault {
        SDKResult<LunCreateResBean> createLun = vasaArrayService.createLun(vvolDisplayName, description, sizeInGB,
                sizeInMB, vmName, nStoragePool.getID(), thinValue, storagePolicy.getSmartTier(), ioProperty);
        if (0 != createLun.getErrCode()) {
            LOGGER.error("createLun error ! errMsg=" + createLun.getDescription());
            virtualVolumeService.updateStatusByVvolId(VasaConstant.VVOL_PREFIX + vvolId,
                    VASAArrayUtil.VVOLSTATUS.error_creating);
            throw FaultUtil.storageFault("createLun error ! errMsg=" + createLun.getDescription());
        }

        return createLun.getResult();
    }

    private void createTaskPolicy(StoragePolicy storagePolicy, String profileName, long generationId, String profileId,
                                  String uniProfileId, Map<String, String> taskProperties) {
        StorageQosCreateBean qosCreateBean = new StorageQosCreateBean();
        qosCreateBean.setGenerationId(generationId);
        qosCreateBean.setProfileId(profileId);
        qosCreateBean.setProfileName(profileName);
        qosCreateBean.setStoragePolicy(storagePolicy);
        qosCreateBean.setUniProfileId(uniProfileId);
        taskProperties.put(NTaskInfo.STORAGE_PROFILE, JsonUtil.parse2JsonString(qosCreateBean));
        taskProperties.put(NTaskInfo.STORAGE_NO_QOS, String.valueOf(VASAUtil.getPolicyNoQos()));
    }

    private List<S2DStoragePool> getStoragePoolByContainerId(long sizeInMB, String containerId, String thinValue)
            throws StorageFault, OutOfResource {
        List<S2DStoragePool> arrayPools = refreshStoragePool();
        List<NStoragePool> pools = storagePoolService.getPoolListByContainerId(containerId);
        List<S2DStoragePool> containerPools = filterContainerPools(arrayPools, pools);
        LOGGER.debug("getPoolListByContainerId ! pools=" + pools);
        if (thinValue != null && thinValue.equalsIgnoreCase("Thick")) {
            checkContainerSpaceOutOfResource(pools, sizeInMB);
        }
        return containerPools;
    }

    private int getIOPropertyByQos(StoragePolicy storagePolicy, String vvolType) {
        // TODO Auto-generated method stub
        int result = 1;
        if (!vvolType.equalsIgnoreCase("Data")) {
            return result;
        }
        if (null != storagePolicy.getQosControllerObjectLatency() || (null != storagePolicy.getQosControllerPolicy()
                && storagePolicy.getQosControllerPolicy().equalsIgnoreCase(VASAArrayUtil.ControlPolicy.isLowerBound))) {
            result = 3;
        }
        LOGGER.info("create lun ioProperty = " + result + ",storagePolicy=" + storagePolicy);
        return result;
    }

    private List<S2DStoragePool> filterContainerPools(List<S2DStoragePool> arrayPools, List<NStoragePool> pools) {
        // TODO Auto-generated method stub
        updateStoragePoolStatus(pools, arrayPools);
        List<S2DStoragePool> result = new ArrayList<>();
        for (S2DStoragePool arrayPool : arrayPools) {
            for (NStoragePool nStoragePool : pools) {
                if (!"Lost".equalsIgnoreCase(nStoragePool.getDeviceStatus())
                        && arrayPool.getID().equalsIgnoreCase(nStoragePool.getRawPoolId())) {
                    result.add(arrayPool);
                    break;
                }
            }
        }
        return result;
    }

    private List<S2DStoragePool> refreshStoragePool() throws StorageFault {
        try {
            SDKResult<List<S2DStoragePool>> result = VASAUtil
                    .queryAndUpdateStoragePoolInfoByArrayId(VASAUtil.getArrayId());
            if (0 != result.getErrCode()) {
                throw FaultUtil.storageFault("getAllStoragePool err.");
            } else {
                return result.getResult();
            }
        } catch (SDKException e) {
            LOGGER.error("queryAndUpdateStoragePoolInfoByArrayId err.", e);
            throw FaultUtil.storageFault("queryAndUpdateStoragePoolInfoByArrayId err.");
        } catch (StorageFault e) {
            LOGGER.error("queryAndUpdateStoragePoolInfoByArrayId err.", e);
            throw FaultUtil.storageFault("queryAndUpdateStoragePoolInfoByArrayId err.");
        }
    }

    private Date getCurrentTimeFormat() throws ParseException {
        SimpleDateFormat foo = new SimpleDateFormat(VASAUtil.PATTEN_FORMAT);
        String format = foo.format(new Date());
        Date date = foo.parse(format);
        return date;
    }

    private void updateVirtualVolume(String vvolId, String lunId, String wwn, String status) throws StorageFault {
        Map<String, String> map = new HashMap<>();
        map.put("vvolid", VasaConstant.VVOL_PREFIX + vvolId);
        map.put("lunId", lunId);
        map.put("wwn", wwn);
        if (null != status) {
            map.put("status", status);
        }
        virtualVolumeService.updateDataByVvolId(map);
    }

    private void checkContainerSpaceOutOfResource(List<NStoragePool> pools, long sizeInMB)
            throws StorageFault, OutOfResource {
        List<String> luns = new ArrayList<>();
        for (NStoragePool nStoragePool : pools) {
            luns.add(nStoragePool.getRawPoolId());
        }
        long containerCap = 0l;
        try {
            List<S2DStoragePool> queryPoolsInfo = vasaArrayService.queryPoolsInfo();
            LOGGER.debug("queryPoolsInfo = " + queryPoolsInfo);
            for (S2DStoragePool poolInfoRes : queryPoolsInfo) {
                for (NStoragePool nStoragePool : pools) {
                    LOGGER.debug("RawPoolId = " + nStoragePool.getRawPoolId() + "poolId = " + poolInfoRes.getID());
                    if (nStoragePool.getRawPoolId().equals(poolInfoRes.getID())) {
                        long totalSizeInByte = Long.valueOf(poolInfoRes.getTotalCapacity()) * MagicNumber.LONG512;
                        long lunFreeSizeInByte = Long.valueOf(poolInfoRes.getFreeCapacity()) * MagicNumber.LONG512;
                        containerCap = containerCap + lunFreeSizeInByte;
                        updatePoolCapacity(poolInfoRes.getID(), totalSizeInByte, lunFreeSizeInByte);
                    }
                }
            }
            long sizeInByte = sizeInMB * MagicNumber.INT1024 * MagicNumber.INT1024;
            if (containerCap < sizeInByte) {
                LOGGER.error("OutOfResource/container space not enough, avaliable size:" + containerCap
                        + " ,created size:" + sizeInByte);
                throw FaultUtil.outOfResource();
            }
        } catch (SDKException e) {
            // TODO Auto-generated catch block
            LOGGER.error("queryPoolsInfo error!", e);
            throw FaultUtil.storageFault();
        }
    }

    private void updatePoolCapacity(String poolId, long totalSizeInByte, long lunFreeSizeInByte) {
        NStoragePool t = new NStoragePool();
        t.setRawPoolId(poolId);
        t.setTotalCapacity(totalSizeInByte);
        t.setFreeCapacity(lunFreeSizeInByte);
        storagePoolService.updateData(t);
    }

    private void insertCreateVirtualVolumeData2DataBase(String vvolId, long sizeInMB, String rawPoolId,
                                                        StorageProfile storageProfile, String uniProfileId, List<NameValuePair> metadata, String containerId,
                                                        String vvolType, Boolean removeThin, StorageProfile profile_to_insert) throws StorageFault {
        LOGGER.info("begin insertCreateVirtualVolumeData2DataBase...");
        String creationTime = null;
        String vmId = "";
        String vmName = "";
        try {
            List<NVvolProfile> listVvolProfile = new ArrayList<NVvolProfile>();
            List<NVvolMetadata> listVvolMetadata = new ArrayList<NVvolMetadata>();
            // 将storageProfile插入vvolProfile数据库
            if (null == profile_to_insert) {
                // profile_to_insert表示上层传下来了完整的profile，没有使用DB中的default profile.
                for (CapabilityInstance capaInstance : storageProfile.getConstraints().getSubProfiles().get(0)
                        .getCapability()) {
                    PropertyInstance proInstance = capaInstance.getConstraint().get(0).getPropertyInstance().get(0);
                    if (removeThin && proInstance.getId().equalsIgnoreCase(VASAUtil.VMW_STD_CAPABILITY)) {
                        LOGGER.warn("thin value:" + proInstance.getValue() + " removed, not inserted into database.");
                        continue;
                    }
                    NVvolProfile vvolProfile = new NVvolProfile();
                    vvolProfile.setVvolid(VasaConstant.VVOL_PREFIX + vvolId);
                    vvolProfile.setProfileName(storageProfile.getName());
                    vvolProfile.setProfileId(uniProfileId);
                    vvolProfile.setCreatedBy(storageProfile.getCreatedBy());
                    vvolProfile.setCreationTime(storageProfile.getCreationTime().getTime());
                    vvolProfile.setGenerationId(storageProfile.getGenerationId());
                    vvolProfile.setCapability(proInstance.getId());
                    vvolProfile.setType(VASAUtil.getPropertyValueType(proInstance.getValue()));
                    vvolProfile.setValue(VASAUtil.convertPropertyValue(proInstance.getValue()));

                    listVvolProfile.add(vvolProfile);
                }
            } else {
                NVvolProfile vvolProfile = new NVvolProfile();
                vvolProfile.setVvolid(VasaConstant.VVOL_PREFIX + vvolId);
                vvolProfile.setProfileName(profile_to_insert.getName());
                vvolProfile.setProfileId(uniProfileId);
                vvolProfile.setCreatedBy(profile_to_insert.getCreatedBy());
                vvolProfile.setCreationTime(profile_to_insert.getCreationTime().getTime());
                vvolProfile.setGenerationId(profile_to_insert.getGenerationId());

                listVvolProfile.add(vvolProfile);
            }

            // 将metadata插入数据库
            for (NameValuePair pair : metadata) {
                NVvolMetadata vvolMetadata = new NVvolMetadata();
                vvolMetadata.setVvolid(VasaConstant.VVOL_PREFIX + vvolId);
                vvolMetadata.setKey(pair.getParameterName());
                vvolMetadata.setValue(pair.getParameterValue());
                LOGGER.debug("vvolMetadata = " + vvolMetadata);
                listVvolMetadata.add(vvolMetadata);
                if ("VMW_VmID".equalsIgnoreCase(pair.getParameterName())) {
                    vmId = pair.getParameterValue();
                }
            }
            Date date = getCurrentTimeFormat();

            vmName = VASAUtil.getRegularVMName(metadata);

            NVirtualVolume volume = new NVirtualVolume();
            volume.setVvolid(VasaConstant.VVOL_PREFIX + vvolId);
            volume.setSize(sizeInMB);
            volume.setCreatedBy("Opensds");
            volume.setCreationTime(date);
            volume.setContainerId(containerId);
            volume.setVvolType(vvolType);
            volume.setSourceType(VasaSrcTypeConstant.RAW);
            volume.setParentId("NA");
            volume.setArrayId(VASAUtil.getArrayId());
            volume.setRawId("NA");
            volume.setRawPoolId(rawPoolId);
            if (vmId != null && !"".equals(vmId)) {
                volume.setVmId(vmId);
            }
            if (vmName != null && !"".equals(vmName)) {
                volume.setVmName(vmName);
            }
            // volume.setStatus("NA");
            // 创建的时候将卷的状态在vasa数据库中置为creating
            LOGGER.info("In insertCreateVirtualVolumeData2DataBase, update volume status creating. vvolId is: "
                    + VasaConstant.VVOL_PREFIX + vvolId);
            volume.setStatus(VASAArrayUtil.VVOLSTATUS.creating);

            virtualVolumeService.addCreateDataIntoDatabase(volume, listVvolProfile, listVvolMetadata);
        } catch (Exception e) {
            LOGGER.error("insertCreateVirtualVolumeData2DataBase parse time error, time is:" + creationTime, e);
            throw FaultUtil
                    .storageFault("insertCreateVirtualVolumeData2DataBase parse time error, time is:" + creationTime);
        }

        LOGGER.info("end insertCreateVirtualVolumeData2DataBase");
    }

    public List<S2DLun> getPELunsByArrayId(String arrayId) throws StorageFault {
        try {
            LOGGER.debug("Execute getPELunsByArrayId arrayId: " + arrayId);
            SDKResult<List<S2DLun>> result = null;
            result = storageModel.getPELun(arrayId);

            SDKResult<List<LogicPortQueryResBean>> NasPEResult = null;

            NasPEResult = storageModel.getNasPE(arrayId);

            if (NasPEResult.getErrCode() != 0) {
                LOGGER.error("query nas PE error:" + NasPEResult.getDescription());
            } else {
                for (int i = 0; i < NasPEResult.getResult().size(); i++) {
                    S2DLun s2DLun = new S2DLun();
                    s2DLun.setID(NasPEResult.getResult().get(i).getID());
                    s2DLun.setNAME(NasPEResult.getResult().get(i).getNAME());
                    s2DLun.setRUNNINGSTATUS(NasPEResult.getResult().get(i).getRUNNINGSTATUS());
                    s2DLun.setIPV4ADDR(NasPEResult.getResult().get(i).getIPV4ADDR());
                    s2DLun.setIPV6ADDR(NasPEResult.getResult().get(i).getIPV6ADDR());
                    s2DLun.setROLE(NasPEResult.getResult().get(i).getROLE());
                    s2DLun.setROLE(NasPEResult.getResult().get(i).getVSTOREID());
                    result.getResult().add(s2DLun);
                }
            }


            if (0 != result.getErrCode()) {
                LOGGER.warn("getPELun error. params[" + arrayId + "] errCode:" + result.getErrCode() + ", description:"
                        + result.getDescription());
                return new ArrayList<S2DLun>();
            }

            return result.getResult();
        } catch (SDKException e) {
            LOGGER.error("StorageFault/getPELun SDKException.");
            throw FaultUtil.storageFault("getPELun SDKException.");
        }
    }

    public List<ProtocolEndpoint> queryProtocolEndpoint() {
        List<ProtocolEndpoint> pes = new ArrayList<ProtocolEndpoint>();
        Set<String> arrayIds = dataManager.getArrayId();
        for (String arrayId : arrayIds) {
            try {
                List<S2DLun> s2dLuns = getPELunsByArrayId(arrayId);
                pes.addAll(VASAUtilDJConvert.convert2ProtocolEndpoint(arrayId, s2dLuns));
            } catch (StorageFault e) {
                LOGGER.warn("getPELunsByArrayId error.");
                continue;
            }

        }

        return pes;
    }

    // 删除创建成功的卷
    public int deleteVirtualVolume(NVirtualVolume vvol, Boolean forceDelete)
            throws StorageFault, VasaProviderBusy, ResourceInUse {
        VASAUtil.saveArrayId(vvol.getArrayId());
        String sourceType = vvol.getSourceType();
        int result = 1;
        VvolPath vvolpath = vvolPathDBService.getVvolPathByVvolId(vvol.getVvolid());

        if (isNasVvol(vvol.getVvolid()) && vvolpath == null) {
            LOGGER.info("no vvolpath record, delete db");
            virtualVolumeService.deleteVirtualVolumeByVvolId(vvol.getVvolid());
            result = 0;
            return result;
        }

        if (sourceType.equalsIgnoreCase(VasaSrcTypeConstant.SNAPSHOT)) {
            result = deleteSnapshot(vvol.getVvolid(), vvol.getArrayId(), vvol.getRawId(), forceDelete);
        } else if (sourceType.equalsIgnoreCase(VasaSrcTypeConstant.CLONE)) {
            result = deleteVirtualVolume(vvol.getVvolid(), vvol.getArrayId(), vvol.getRawId(), forceDelete);
        } else {
            result = deleteVirtualVolume(vvol.getVvolid(), vvol.getArrayId(), vvol.getRawId(), forceDelete);
        }
        return result;
    }

    public boolean checkAndDelProfile(NVirtualVolume virtualVolume) throws StorageFault, SDKException {
        // TODO Auto-generated method stub
        VASAUtil.saveArrayId(virtualVolume.getArrayId());
        // 获取当前卷使用的profile
        List<NVvolProfile> vvolProfileByVvolId = vvolProfileService.getVvolProfileByVvolId(virtualVolume.getVvolid());
        if (null == vvolProfileByVvolId || vvolProfileByVvolId.size() == 0) {
            LOGGER.error("The vvoldId=" + virtualVolume.getVvolid() + " do not have vvolProfile");
            return false;
        }
        NStorageProfile storageProfile = storageProfileService
                .getStorageProfileByProfileId(vvolProfileByVvolId.get(0).getProfileId());
        String smartQosId = storageProfile.getSmartQosId();
        if (null == smartQosId || "".equals(smartQosId)) {
            // 未使用Qos组不需要删除vvolProfile，因为没有Qos组的所有vvolLun共用一个profile
            LOGGER.info("The vvolId=" + virtualVolume.getVvolid() + " do not use Qos.");
            return true;
        }
        // 存在Qos组：
        // 第一步将VVOL LUN从Qos组中移除
        NStorageQos storageQos = storageQosService.getStorageQosByQosId(smartQosId);

        // 表示使用的om界面创建的qos
        if (null != storageQos && storageQos.getName().startsWith("_om_")) {
            LOGGER.info("The vvolId=" + virtualVolume.getVvolid() + " do not use Qos. Using om create qos.");
            return true;
        }

        if (null == storageQos || "".equals(storageQos.getRawQosId())) {
            LOGGER.error("The vvolId=" + virtualVolume.getVvolid() + "Can't find storageQos.");
            return false;
        }

        // Qos组是否已经删除，已经删除的Qos组直接返回成功
        if (storageQos.getDeleted()) {
            LOGGER.warn("The storageQos id=" + storageQos.getId() + " rawQosId=" + storageQos.getRawQosId()
                    + " have already deleted.");
            return true;
        }

        vasaArrayService.delLunToQos(storageQos.getRawQosId(), virtualVolume.getRawId());

        // 第二步将Qos组设置为未激活状态
        IDeviceQosService deviceQosService = VVolModel.getDeviceManager().getDeviceServiceProxy(VASAUtil.getArrayId(),
                IDeviceQosService.class);
        SDKErrorCode deactiveQos = deviceQosService.deactiveQos(VASAUtil.getArrayId(), storageQos.getRawQosId());
        if (0 != deactiveQos.getErrCode()) {
            LOGGER.error("deactiveQos err,please check it on array. rawQosId=" + storageQos.getRawQosId());
            return false;
        }

        // 第三步删除使用到的Qos组
        SDKErrorCode delQos = deviceQosService.delQos(VASAUtil.getArrayId(), storageQos.getRawQosId());
        if (0 != delQos.getErrCode()) {
            LOGGER.error("delQos err,please check it on array. rawQosId=" + storageQos.getRawQosId());
            return false;
        }

        NStorageQos qos = new NStorageQos();
        qos.setId(storageQos.getId());
        storageQosService.deleteStorageQosByQosId(qos);
        LOGGER.info("The vvolId=" + virtualVolume.getVvolid() + " rm Qos.");
        return true;
    }

    public boolean deleteSnapShotFormArray(String vvolId) {
        try {
            NVirtualVolume virtualVolumeByVvolId = virtualVolumeService.getVirtualVolumeByVvolId(vvolId);
            String arrayId = virtualVolumeByVvolId.getArrayId();
            String lunId = virtualVolumeByVvolId.getRawId();
            LOGGER.debug("In deleteSnapShotFormArray function, the delete VirtualVolume info is "
                    + virtualVolumeByVvolId.toString());
            // 第一步，查询snapshot信息，如果当前要删除的LUN已在阵列上删除，则直接返回成功
            SDKResult<SnapshotCreateResBean> snapshotInfo = vasaArrayService.querySnapshotInfo(lunId);
            if (1077937880 == snapshotInfo.getErrCode()) {
                LOGGER.warn("The Snapshot deleted id: " + vvolId.substring(vvolId.indexOf('.') + 1)
                        + " is not exsit in Array.");
                deleteVirtualVolumeFromDatabase(vvolId);
                return true;
            } else if (0 != snapshotInfo.getErrCode()) {
                LOGGER.error("StorageFault/deleteSnapshot getSnapshotById error(check error_deleting status). params["
                        + vvolId.substring(vvolId.indexOf('.') + 1) + "] errCode:" + snapshotInfo.getErrCode()
                        + ", description:" + snapshotInfo.getDescription());
                return false;
            }
            // 快照不能删除qos组
            // 第二步，查询vvol lun使用到的profile，如果使用了Qos组，则需要删除对应的Qos组
            /*
             * if(!checkAndDelProfile(virtualVolumeByVvolId)){ LOGGER.error(
             * "Delete virtualVolume vvolId="+vvolId+
             * " fail in check and delete profile."); return false; }
             */

            // 第三步，inactive快照
            if (snapshotInfo.getResult().getRUNNINGSTATUS().equalsIgnoreCase(VASAArrayUtil.SnapStatus.active)) {
                IDeviceSnapshotService deviceServiceProxy = VVolModel.getDeviceManager().getDeviceServiceProxy(arrayId,
                        IDeviceSnapshotService.class);
                SDKErrorCode deactivateSnapshot = deviceServiceProxy.deactivateSnapshot(arrayId, lunId);
                if (0 != deactivateSnapshot.getErrCode()) {
                    LOGGER.error("DeactivateSnapshot error. params[" + vvolId.substring(vvolId.indexOf('.') + 1)
                            + "] errCode:" + deactivateSnapshot.getErrCode() + ", description:"
                            + deactivateSnapshot.getDescription());
                    throw FaultUtil.storageFault("DeactivateSnapshot error. errCode:" + deactivateSnapshot.getErrCode()
                            + ", description:" + deactivateSnapshot.getDescription());
                }
            }
            // 第四步，删除快照
            SDKErrorCode delVvolLunSnapshot = vasaArrayService.delVvolLunSnapshot(lunId);
            if (0 != delVvolLunSnapshot.getErrCode()) {
                LOGGER.error("ResourceInUse/deleteSnapshot error. params[" + vvolId.substring(vvolId.indexOf('.') + 1)
                        + "] errCode:" + delVvolLunSnapshot.getErrCode() + ", description:"
                        + delVvolLunSnapshot.getDescription());
                return false;
            }
            // 第五步，删除DB中的记录
            deleteVirtualVolumeFromDatabase(vvolId);
            return true;
        } catch (Exception e) {
            LOGGER.error("deleteVirtualVolumeFormArray fail, Exception : ", e);
        }
        return false;
    }

    public boolean deleteVirtualVolumeFormArray(String vvolId) {
        try {
            NVirtualVolume virtualVolumeByVvolId = virtualVolumeService.getVirtualVolumeByVvolId(vvolId);
            LOGGER.debug("In deleteVirtualVolumeFormArray function, the delete VirtualVolume info is "
                    + virtualVolumeByVvolId.toString());
            // 第一步，查询Lun信息，如果当前要删除的LUN已在阵列上删除，则直接返回成功
            SDKResult<LunCreateResBean> result = vasaArrayService.queryLunInfo(virtualVolumeByVvolId.getLunId());
            if (1077936859 == result.getErrCode()) {
                // 当前要删除的LUN已在阵列上删除，则直接返回成功
                LOGGER.info("The deleted id: " + vvolId.substring(vvolId.indexOf('.') + 1)
                        + " is not exsit in Array, Deleted successfully.");
                // 从数据库中删除该数据
                deleteVirtualVolumeFromDatabase(vvolId);
                return true;
            } else if (0 != result.getErrCode()) {
                // 查询异常，则返回删卷失败
                LOGGER.error("StorageFault/deleteVirtualVolume getVolumeById error(check if error_deleting). params["
                        + virtualVolumeByVvolId.getLunId() + "] errCode:" + result.getErrCode() + ", description:"
                        + result.getDescription());
                return false;
            }
            // 第二步，查询vvol lun使用到的profile，如果使用了Qos组，则需要删除对应的Qos组fast-clone不用删除QOS
            if (!virtualVolumeByVvolId.getSourceType().equalsIgnoreCase(VasaSrcTypeConstant.FAST_CLONE)
                    && !checkAndDelProfile(virtualVolumeByVvolId)) {
                LOGGER.error("Delete virtualVolume vvolId=" + vvolId + " fail in check and delete profile.");
                return false;
            }
            LOGGER.info("Delete virtualVolume generally, the vvolId = " + vvolId);

            // 第三步，删除vvol Lun
            SDKErrorCode delResult = vasaArrayService.deleteLun(virtualVolumeByVvolId.getRawId());
            if (0 != delResult.getErrCode()) {
                LOGGER.error("resourceInUse/deleteVolume error. params[" + vvolId + "] errCode:"
                        + delResult.getErrCode() + ", description:" + delResult.getDescription());
                return false;
            }
            // 第四步，删除DB中的记录
            deleteVirtualVolumeFromDatabase(vvolId);
            return true;
        } catch (Exception e) {
            LOGGER.error("deleteVirtualVolumeFormArray fail, Exception : ", e);
        }
        return false;
    }

    public void deleteVirtualVolumeFromDatabase(String vvolId) throws StorageFault {
        virtualVolumeService.deleteVirtualVolumeInfo(vvolId);
    }

    /**
     * 判断vasa在60s内是否一直busy
     *
     * @return
     */
    public boolean isVasaProviderBusy(long sleepTime, long startTime) {
        while (DateUtil.getUTCDate().getTime() - startTime < 60000) {
            if (!VvolControlRunable.getInstance(virtualVolumeService).testDjOverloadIncr()) {
                return false;
            }
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                LOGGER.error("traffic control receives a InterruptedException.");
            }
        }
        return true;
    }


    private int deleteVirtualVolume(String vvolId, String arrayId, String rawId, Boolean forceDelete)
            throws ResourceInUse, StorageFault, VasaProviderBusy {
        LOGGER.info("In deleteVirtualVolume function, the vvolId=" + vvolId + " arrayId=" + arrayId + " rawId=" + rawId
                + " forceDelete=" + forceDelete);
        VASAUtil.saveArrayId(arrayId);
        try {
            if (isNasVvol(vvolId)) {
                // nd_todo
                // 判断vvol是否处于绑定状态
                // 判断是否有依赖关系
                // 删除NAS vvol
                if (vvolPathDBService.isBindState(vvolId)) {
                    LOGGER.error("vvol :" + vvolId + " is in bind state, can not be delete");
                    throw FaultUtil.resourceInUse("vvol " + vvolId + " is in bind state");
                }
                virtualVolumeService.updateStatusAndDeletedTimeByVvolId(vvolId, "deleting");
                LOGGER.info("vvol :" + vvolId + " set status to deleting");
                NVirtualVolume vvol = virtualVolumeService.getVirtualVolumeByVvolId(vvolId);
                VvolPath vvolPath = vvolPathDBService.getVvolPathByVvolId(vvolId);
                //vasaNasArrayService.setVvolMetadata(vvolId);

                if (vvol == null) {
                    throw FaultUtil.notFound("not found vvolId:" + vvolId);
                }

                String vvolType = vvol.getVvolType();
                SDKErrorCode ret = vasaNasArrayService.deleteFile(vvolId, vvolType, arrayId);
                if (ret.getErrCode() == 0 || ret.getErrCode() == INFSvvolService.ERROR_OBJ_NOT_EXIST) {
                    deleteVirtualVolumeFromDatabase(vvolId);
                    vvolPathDBService.deleteRecordByVvolId(vvolId);
                }

                if (VasaConstant.VVOL_TYPE_DATA.equals(vvolType)
                        && vvolPathDBService.getCountRecordByFileSystemName(vvolPath.getFileSystemName()) > 0) {
                    LOGGER.info("modify fileSystem when delete virtualvolume");
                    vasaNasArrayService.modifyFIleSystem(arrayId, vvol.getVmId(), vvolPath.getFileSystemId(), -99L);
                }
            } else {
                // 删除SAN vvol
                // 判断vvol是否处于绑定状态
                int bindResult = virtualVolumeService.checkInBindStatus(vvolId, arrayId, rawId);
                if (bindResult == 2) {
                    LOGGER.error("the vvol vvolid:" + vvolId + " is in bound state.");
                    throw FaultUtil.resourceInUse("the vvol vvolid:" + vvolId + " is in bound state.");
                } else if (bindResult == 1) {
                    LOGGER.error("vasa get vvolid:" + vvolId + " bound state error.");
                    throw FaultUtil.storageFault("vasa get vvolid:" + vvolId + " bound state error.");
                }
                // 判断是否有依赖关系
                if (virtualVolumeService.checkDependencies(vvolId)) {
                    throw FaultUtil.resourceInUse("the vvol vvolid:" + vvolId + " has snapshot or fast-clone in use");
                }
                // 判断当前卷可删除，标记卷状态为deleting 且 更新 deletedTime字段,
                // 用作后续删除队列任务清理deleting状态的卷
                virtualVolumeService.updateStatusAndDeletedTimeByVvolId(vvolId, "deleting");
                LOGGER.debug(
                        "DataBase VVOL Info2 : " + virtualVolumeService.getAllSpecifiedStatusVvols("deleting").toString());
            }
        } catch (Exception e) {
            LOGGER.error("deleteVirtualVolume fail, the vvolid=" + vvolId + " Exception ", e);
            throw FaultUtil.resourceInUse("deleteVirtualVolume fail, the vvolid=" + vvolId);
        }
        LOGGER.info("In deleteVirtualVolume, update volume status is deleting. return esx host that the vvolId = "
                + vvolId + " delete successfully.");
        return 0;
    }

    public int deleteSnapshot(String vvolId, String arrayId, String rawId, Boolean forceDelete)
            throws StorageFault, VasaProviderBusy, ResourceInUse {
        LOGGER.info("In deleteSnapshot function, the vvolId=" + vvolId + " arrayid=" + arrayId + " rawId=" + rawId);
        try {
            if (isNasVvol(vvolId)) {
                //TODO(huyang)
                // 判断vvol是否处于绑定状态
                // 判断是否有依赖关系
                // 删除NAS snapshot vvol

                //在线克隆场景下，删除快照没有解绑，就调用删除接口
//				if(vvolPathDBService.isBindState(vvolId))
//				{
//					LOGGER.error("vvol :"+vvolId+" is in bind state, can not be delete");
//					throw FaultUtil.resourceInUse("vvol "+vvolId+" is in bind state");
//				}
                virtualVolumeService.updateStatusAndDeletedTimeByVvolId(vvolId, "deleting");

                NVirtualVolume vvol = virtualVolumeService.getVirtualVolumeByVvolId(vvolId);
                if (vvol == null) {
                    throw FaultUtil.notFound("not found vvolId:" + vvolId);
                }

                SDKErrorCode ret = vasaNasArrayService.deleteFSSnapshot(vvolId);
                if (ret.getErrCode() == 0 || ret.getErrCode() == IFileSystemService.FILE_SYS_SNAP_NOT_EXIST) {
                    deleteVirtualVolumeFromDatabase(vvolId);
                    vvolPathDBService.deleteRecordByVvolId(vvolId);
                }
                return 0;
            }
            // 判断vvol是否处于绑定状态
            int bindResult = virtualVolumeService.checkInBindStatus(vvolId, arrayId, rawId);
            if (bindResult == 2) {
                LOGGER.error("the vvol vvolid:" + vvolId + " is in bound state.");
                throw FaultUtil.resourceInUse("the vvol vvolid:" + vvolId + " is in bound state.");
            } else if (bindResult == 1) {
                LOGGER.error("vasa get vvolid:" + vvolId + " bound state error.");
                throw FaultUtil.storageFault("vasa get vvolid:" + vvolId + " bound state error.");
            }
            // 判断是否有依赖关系
            if (virtualVolumeService.checkDependencies(vvolId)) {
                throw FaultUtil.resourceInUse("the vvol vvolid:" + vvolId + " has snapshot or fast-clone in use");
            }
            // record delete operator into DB
            LOGGER.info("In deleteSnapshot(String,String,String,Boolean),update volume status is deleting. vvolId is: "
                    + vvolId);
            virtualVolumeService.updateStatusAndDeletedTimeByVvolId(vvolId, "deleting");
        } catch (Exception e) {
            LOGGER.info("deleteVirtualVolume error. Exception :", e);
            throw FaultUtil.storageFault("deleteVirtualVolume error.");
        }
        return 0;
    }

    public TaskInfo resizeVirtualVolume(NVirtualVolume vvol, long newSize) throws StorageFault, OutOfResource {
        String vvolId = vvol.getVvolid();
        String arrayId = vvol.getArrayId();
        VASAUtil.saveArrayId(arrayId);
        List<NStoragePool> pools = storagePoolService.getPoolListByContainerId(vvol.getContainerId());
        if (isNasVvol(vvolId)) {
            Long freeSizeInByte = pools.get(0).getFreeCapacity();
            Long expandSizeInByte = (newSize - vvol.getSize()) * MagicNumber.LONG1024 * MagicNumber.LONG1024;
            if (freeSizeInByte < expandSizeInByte) {
                LOGGER.error("OutOfResource/container space not enough, avaliable size:" + freeSizeInByte
                        + " ,created size:" + expandSizeInByte);
                throw FaultUtil.outOfResource();
            }
        } else {
            checkContainerSpaceOutOfResource(pools, newSize - vvol.getSize());
        }

        try {
            // int sizeInGB = (newSize%1024 == 0)? (int)newSize/1024 :
            // ((int)newSize/1024 + 1);
            SDKErrorCode expandLun;
            TaskInfo taskInfo = new TaskInfo();
            StringBuilder srcPath = new StringBuilder();
            boolean isNas = isNasVvol(vvolId);
            String fsId = null;
            if (isNas) {
                VvolPath vvolPath = vvolPathDBService.getVvolPathByVvolId(vvolId);
                String fileSystemName = vvolPath.getFileSystemName();
                fsId = vvolPath.getFileSystemId();
                vasaNasArrayService.modifyFIleSystem(arrayId, vvolId, fsId, newSize);
                expandLun = vasaNasArrayService.operateFile(vvolId, vvol.getVvolType(), newSize, INFSvvolService.operate_setattr, fileSystemName, srcPath);
            } else {
                expandLun = vasaArrayService.expandLun(vvol.getRawId(), newSize);
            }
            // SDKErrorCode result =
            // vvolModel.resizeVolume(vvolId.substring(vvolId.indexOf('.') + 1),
            // (int) newSize);
            if (0 != expandLun.getErrCode()) {
                LOGGER.error("StorageFault/resizeVolume error. params[" + vvolId.substring(vvolId.indexOf('.') + 1)
                        + newSize + "] errCode:" + expandLun.getErrCode() + ", description:"
                        + expandLun.getDescription());
                throw FaultUtil.storageFault("resizeVolume error. errCode:" + expandLun.getErrCode());
            }

            NTaskInfo nTaskInfo = taskInfoService.getTaskInfoByTaskId("resizeVirtualVolume:" + vvolId);
            if (null != nTaskInfo && !nTaskInfo.getTaskState().equalsIgnoreCase("RUNNING")) {
                taskInfoService.delete(nTaskInfo);
            }
            taskInfo.setName("resizeVirtualVolume");
            taskInfo.setCancelable(false);
            taskInfo.setCancelled(false);

            taskInfo.setStartTime(DateUtil.getUTCCalendar());
            taskInfo.setArrayId(VASAUtil.getStorageArrayUUID(vvol.getArrayId()));
            taskInfo.setTaskId("resizeVirtualVolume:" + vvolId);
            if (isNas) {
                taskInfo.setTaskState(TaskStateEnum.SUCCESS.value());
                virtualVolumeService.updateSizeByVvolId(vvolId, newSize);
            } else {
                taskInfo.setTaskState(TaskStateEnum.RUNNING.value());
            }

            // 将进行中的任务加入任务列表
            // VasaTask task = new GeneralVvolTask(taskInfo);
            taskInfoService.saveTaskInfo(taskInfo, null);
            LOGGER.debug(taskInfo);
            return taskInfo;
        } catch (SDKException e) {
            LOGGER.error("StorageFault/resizeVolume sdkexception. errCode:" + e.getSdkErrCode());
            throw FaultUtil.storageFault("resizeVolume sdkexception. errCode:" + e.getSdkErrCode());
        }
    }

    public boolean isNasVvol(String vvolId) throws StorageFault {
        if (virtualVolumeService.getVirtualVolumeByVvolId(vvolId) == null) {
            LOGGER.error("unknow vvolId = " + vvolId);
            throw FaultUtil.storageFault("unknow vvolId = " + vvolId);
        }
        try {
            NVirtualVolume vvol = virtualVolumeService.getVirtualVolumeByVvolId(vvolId);
            String containerId = vvol.getContainerId();
            String containerType = storageContainerService.getStorageContainerByContainerId(containerId).getContainerType();
            if (containerType.equals("SAN")) {
                return false;
            } else if (containerType.equals("NAS")) {
                return true;
            } else {
                LOGGER.error("unknow container type:" + containerType);
                throw FaultUtil.storageFault("unknow container type:" + containerType);
            }
//	        return vvolPathDBService.getVvolPathByVvolId(vvolId) == null ? false:true;
        } catch (Exception e) {
            LOGGER.error("unknow vvolPath ,vvolId =  " + vvolId);
            throw FaultUtil.storageFault("unknow vvolPath ,vvolId =  " + vvolId);
        }
    }

    public List<BatchVirtualVolumeHandleResult> bindSanVirtualVolume(String arrayId, String vvolId, String rawId,
                                                                     String sourceType, String hostId, int bindType) throws StorageFault {
        VASAUtil.saveArrayId(arrayId);
        List<BatchVirtualVolumeHandleResult> bindResults = new ArrayList<BatchVirtualVolumeHandleResult>();
        S2DVvolBind bindresult = null;
        try {
            SDKResult<S2DVvolBind> result = vvolModel.bind(arrayId, hostId, rawId, bindType);
            if (0 != result.getErrCode()) {
                if (1073747207 == result.getErrCode()) {
                    LOGGER.warn("already bind. params[" + arrayId + "," + hostId + "," + rawId + "," + bindType
                            + "] errCode:" + result.getErrCode() + ", description:" + result.getDescription());
                    bindresult = getBindInfo(arrayId, rawId, hostId);
                    if (null == bindresult) {
                        LOGGER.error("BindInfo error. Info is null.");
                        throw new SDKException("BindInfo error, not bind host");
                    }
                } else {
                    LOGGER.error("bind error. params[" + arrayId + "," + hostId + "," + rawId + "," + bindType
                            + "] errCode:" + result.getErrCode() + ", description:" + result.getDescription());
                    BatchVirtualVolumeHandleResult handleResult = new BatchVirtualVolumeHandleResult();
                    handleResult.setVvolId(vvolId);
                    handleResult.setFault(new com.vmware.vim.vasa.v20.fault.xsd.StorageFault());
                    bindResults.add(handleResult);
                    return bindResults;
                }
            } else {
                bindresult = result.getResult();
            }
            SDKResult<S2DLun> peLunResult = storageModel.getLun(arrayId, bindresult.getPELUNID());
            if (0 != peLunResult.getErrCode()) {
                LOGGER.error("StorageFault/getLun error. params[" + arrayId + "," + bindresult.getPELUNID()
                        + "] errCode:" + peLunResult.getErrCode() + ", description:" + peLunResult.getDescription());
                throw FaultUtil
                        .storageFault("getLun error. arrayId:" + arrayId + ", peLunId:" + bindresult.getPELUNID());
            }
            BatchVirtualVolumeHandleResult handleResult1 = new BatchVirtualVolumeHandleResult();
            handleResult1.setVvolId(vvolId);

            ProtocolEndpointInbandId peInBandId = new ProtocolEndpointInbandId();
            peInBandId.setProtocolEndpointType(ProtocolEndpointTypeEnum.SCSI.value());
            peInBandId.setLunId("naa." + peLunResult.getResult().getWWN());

            VirtualVolumeHandle vvolHandle = new VirtualVolumeHandle();
            vvolHandle.setPeInBandId(peInBandId);
            // vvolHandle.setVvolSecondaryId(result.getResult().getVVOLSECONDARYID());
            vvolHandle.setVvolSecondaryId("0x" + bindresult.getVVOLSECONDARYID());
            // vvolHandle.setUniqueIdentifier(arrayId + ":VirtualVolumeHandle:"
            // + "0x" + result.getResult().getVVOLSECONDARYID()
            // + ":" + peInBandId.getLunId());
            vvolHandle.setUniqueIdentifier(vvolId);
            handleResult1.setVvolHandle(vvolHandle);

            handleResult1.setVvolInfo(queryVirtualVolumeInfo(vvolId));
            // LOGGER.info("In getLun storageModel.getLun");
            if ("snapshot".equalsIgnoreCase(sourceType)) {
                LOGGER.info("In bindVirtualVolume the vvol lun sourceType is " + sourceType);
                SDKResult<SnapshotCreateResBean> snapshotInfo = vasaArrayService.querySnapshotInfo(rawId);
                if (0 == snapshotInfo.getErrCode()) {
                    // 与曾强z90004993 确认默认为512L
                    long sectorSize = 512L;
                    long capacity = Long.valueOf(snapshotInfo.getResult().getUSERCAPACITY());
                    handleResult1
                            .setVvolLogicalSize(capacity * sectorSize / MagicNumber.LONG1024 / MagicNumber.LONG1024);
                } else {
                    LOGGER.warn("getSnapshot error. params[" + arrayId + "," + rawId + "] errCode:"
                            + snapshotInfo.getErrCode() + ", description: " + snapshotInfo.getDescription());
                }
            } else {
                SDKResult<S2DLun> lunResult = storageModel.getLun(arrayId, rawId);
                // lunResult.getResult().getID();
                if (0 == lunResult.getErrCode()) {
                    long sectorSize = Long.valueOf(lunResult.getResult().getSECTORSIZE());
                    long capacity = Long.valueOf(lunResult.getResult().getCAPACITY());
                    handleResult1
                            .setVvolLogicalSize(capacity * sectorSize / MagicNumber.LONG1024 / MagicNumber.LONG1024);
                } else {
                    LOGGER.warn("getLun error. params[" + arrayId + "," + rawId + "] errCode:" + lunResult.getErrCode()
                            + ", description:" + lunResult.getDescription());
                }
            }
            bindResults.add(handleResult1);
        } catch (SDKException e) {
            LOGGER.error("StorageFault/bind error. arrayId:" + arrayId + ", hostId:" + hostId + ", vvolId:" + rawId
                    + ", errCode:" + e.getSdkErrCode());
            throw FaultUtil.storageFault("bind error. arrayId:" + arrayId + ", hostId:" + hostId + ", vvolId:" + rawId
                    + ", errCode:" + e.getSdkErrCode());
        }

        return bindResults;
    }


    public List<BatchVirtualVolumeHandleResult> bindNasVirtualVolume(String arrayId, String vvolId, String rawId,
                                                                     String sourceType, int bindType) throws StorageFault {
        VASAUtil.saveArrayId(arrayId);

        return vasaNasArrayService.bindNasVirtualVolume(arrayId, vvolId, rawId, sourceType, bindType);
    }

    /**
     * @param arrayId
     * @param vvolId
     * @param rawId
     * @param hostId
     * @param isRebind
     * @return
     * @throws StorageFault
     */
//	public List<BatchVirtualVolumeHandleResult> bindVirtualVolume(String arrayId, String vvolId, String rawId,
//			String sourceType, String hostId, int bindType) throws StorageFault {
//	    LOGGER.debug("bind step 5.");
//		if(isNasVvol(vvolId))
//		{
//		    LOGGER.info("exec bindNasVirtualVolume !!");
//		    return bindNasVirtualVolume(arrayId, vvolId, rawId, sourceType, hostId, bindType);
//		}
//		else
//		{
//		    LOGGER.info("exec bindSanVirtualVolume !!");
//		    return bindSanVirtualVolume(arrayId, vvolId, rawId, sourceType, hostId, bindType);
//		}
//
//	}
    private S2DVvolBind getBindInfo(String arrayId, String vvolId, String hostId) throws SDKException {
        SDKResult<List<S2DVvolBind>> vvolBindResult = vvolModel.getVVOLBind(arrayId, vvolId);
        if (vvolBindResult.getErrCode() == 0) {
            if (0 == vvolBindResult.getErrCode()) {
                List<S2DVvolBind> s2dVvolBinds = vvolBindResult.getResult();
                for (S2DVvolBind s2dVvolBind : s2dVvolBinds) {
                    if (s2dVvolBind.getHOSTID().equals(hostId)) {
                        return s2dVvolBind;
                    }
                }
            }
        } else {
            LOGGER.error("get vvolBindResult error, errorCode=" + vvolBindResult.getErrCode() + ",msg="
                    + vvolBindResult.getDescription());
            throw new SDKException("get vvolBindResult error, errorCode=" + vvolBindResult.getErrCode() + ",msg="
                    + vvolBindResult.getDescription());
        }
        return null;
    }

    public VirtualVolumeInfo queryVirtualVolumeInfo(String vvolId) throws StorageFault {
        VirtualVolumeInfo vvolInfo = new VirtualVolumeInfo();

        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
        // 从数据库中将vvol的metadata取出
        List<NVvolMetadata> listVvolMetadata = vvolMetadataService.getVvolMetadataByVvolId(vvolId);

        if (listVvolMetadata == null || listVvolMetadata.size() == 0) {
            return null;
        }

        for (NVvolMetadata result : listVvolMetadata) {
            NameValuePair pair = new NameValuePair();
            pair.setParameterName(result.getKey());
            pair.setParameterValue(result.getValue());

            pairs.add(pair);
        }

        vvolInfo.setUniqueIdentifier(vvolId);
        vvolInfo.setVvolId(vvolId);
        vvolInfo.getMetadata().addAll(pairs);
        return vvolInfo;
    }

    public BatchReturnStatus unbindVirtualVolumeFromAllHost(NVirtualVolume vvol) throws StorageFault {
        try {
            SDKErrorCode sdkErrorCode = vvolModel.unbindVvolFromAllHost(vvol.getArrayId(), vvol.getRawId());
            if (0 != sdkErrorCode.getErrCode()) {
                LOGGER.error("unbindVvolFromAllHost error. params[" + vvol.getArrayId() + "," + vvol.getRawId()
                        + "] errCode:" + sdkErrorCode.getErrCode() + ", description:" + sdkErrorCode.getDescription());

                BatchReturnStatus returnStatus = new BatchReturnStatus();
                BatchErrorResult errorResult = new BatchErrorResult();
                errorResult.getError().add(new com.vmware.vim.vasa.v20.fault.xsd.StorageFault());
                returnStatus.setErrorResult(errorResult);
                // returnStatus.setUniqueId("unbindVirtualVolumeFromAllHost:" +
                // vvol.getVvolid());
                returnStatus.setUniqueId(vvol.getVvolid());
                return returnStatus;
            }
            BatchReturnStatus returnStatus1 = new BatchReturnStatus();
            returnStatus1.setUniqueId(vvol.getVvolid());

            return returnStatus1;
        } catch (SDKException e) {
            LOGGER.error("StorageFault/unbindVvolFromAllHost error. arrayId:" + vvol.getArrayId() + ", vvol raw_id:"
                    + vvol.getRawId() + ", errCode:" + e.getSdkErrCode());
            throw FaultUtil.storageFault("unbindVvolFromAllHost error. arrayId:" + vvol.getArrayId() + ", vvol raw_id:"
                    + vvol.getRawId() + ", errCode:" + e.getSdkErrCode());
        }
    }

    public void unbindAllVirtualVolumesFromHost(List<HostInitiatorInfo> hostInitiatorInfos) throws StorageFault {
        Map<String, String> arrayHostMap = new HashMap<String, String>();
        Set<String> arrayIds = DataUtil.getInstance().getArrayId();
        for (String arrayId : arrayIds) {
            Set<String> hostIds = getHostIdByInitiator(arrayId, hostInitiatorInfos);
            if (null == hostIds) {
                LOGGER.warn("no host found on array:" + arrayId);
                continue;
            }

            // arrayHostMap.put(arrayId, hostId);
            for (String hostId : hostIds) {
                try {
                    LOGGER.info("vvolModel.unbindAllVvolFromHost called. arrayId:" + arrayId + ", hostId:" + hostId);
                    SDKErrorCode sdkErrorCode = vvolModel.unbindAllVvolFromHost(arrayId, hostId);
                    if (0 != sdkErrorCode.getErrCode()) {
                        LOGGER.error("unbindAllVvolFromHost error. params[" + arrayId + "," + hostId + "] errCode:"
                                + sdkErrorCode.getErrCode() + ", description:" + sdkErrorCode.getDescription());
                    }
                } catch (SDKException e) {
                    LOGGER.error("StorageFault/unbindAllVvolFromHost error. errCode:" + e.getSdkErrCode());
                    throw FaultUtil.storageFault("unbindAllVvolFromHost error. errCode:" + e.getSdkErrCode());
                }
            }
        }

        /*
         * for (Map.Entry<String, String> entry : arrayHostMap.entrySet()) {
         *
         * }
         */

    }

    public void updateShareForUnbind(String arrayId, String vvolType, VvolPath vvolPath) throws StorageFault {
//	    vasaNasArrayService.updateShare(arrayId, vvolType, vvolPath,false);
    }

    public BatchReturnStatus unbindVirtualVolume(String arrayId, String hostId, String vvolSecondaryId, String peLunId,
                                                 String peWwn, int bindType, String vvolUuid) throws StorageFault {
        try {

            LOGGER.debug("unbindVvolFromPELunAndHost. params[" + arrayId + "," + hostId + "," + vvolSecondaryId + ","
                    + peLunId + "," + bindType + "]");

            SDKErrorCode sdkErrorCode = vvolModel.unbindVvolFromPELunAndHost(arrayId, hostId,
                    vvolSecondaryId.substring(2), peLunId, bindType);
            // SDKErrorCode sdkErrorCode =
            // vvolModel.unbindVvolFromPELun(arrayId,
            // vvolSecondaryId.substring(2), peLunId, bindType);
            if (0 != sdkErrorCode.getErrCode()) {
                if (ArrayErrCodeEnum.RETURN_PARAM_ERROR.getValue() == sdkErrorCode.getErrCode()) {
                    LOGGER.error("unbindVvolFromPELunAndHost error. params[" + arrayId + "," + hostId + ","
                            + vvolSecondaryId.substring(2) + "," + peLunId + "," + bindType + "] errCode:"
                            + sdkErrorCode.getErrCode() + ", description:" + sdkErrorCode.getDescription());
                    BatchReturnStatus returnStatus = new BatchReturnStatus();
                    BatchErrorResult errorResult = new BatchErrorResult();
                    errorResult.getError().add(new com.vmware.vim.vasa.v20.fault.xsd.InvalidArgument());
                    returnStatus.setErrorResult(errorResult);
                    returnStatus.setUniqueId(vvolUuid);
                    return returnStatus;
                } else {
                    LOGGER.error("unbindVvolFromPELun error. params[" + arrayId + "," + hostId + ","
                            + vvolSecondaryId.substring(2) + "," + peLunId + "," + bindType + "] errCode:"
                            + sdkErrorCode.getErrCode() + ", description:" + sdkErrorCode.getDescription());
                    BatchReturnStatus returnStatus = new BatchReturnStatus();
                    BatchErrorResult errorResult = new BatchErrorResult();
                    errorResult.getError().add(new com.vmware.vim.vasa.v20.fault.xsd.StorageFault());
                    returnStatus.setErrorResult(errorResult);
                    returnStatus.setUniqueId(vvolUuid);

                    return returnStatus;
                }
            }
            BatchReturnStatus returnStatus1 = new BatchReturnStatus();
            returnStatus1.setUniqueId(vvolUuid);
            return returnStatus1;
        } catch (SDKException e) {
            LOGGER.error("StorageFault/unbindVvolFromPELun error. errCode:" + e.getSdkErrCode());
            throw FaultUtil.storageFault("unbindVvolFromPELun error. errCode:" + e.getSdkErrCode());
        }
    }

    public List<SpaceStats> spaceStatsForVirtualVolume(List<NVirtualVolume> ValidVvolIds) throws StorageFault {

        List<SpaceStats> returnValues = new ArrayList<SpaceStats>();
        for (NVirtualVolume volume : ValidVvolIds) {
            // 调用南向透传查询vvol接口
            try {
                if (isNasVvol(volume.getVvolid())) {
                    String arrayId = volume.getArrayId();

                    LOGGER.info("====>  query spaceStats for nas vvol ");
                    INFSvvolService infSvvolService;
                    try {
                        infSvvolService = deviceManager.getDeviceServiceProxy(arrayId, INFSvvolService.class);

                        VvolPath vvolPath = vvolPathDBService.getVvolPathByVvolId(volume.getVvolid());

                        SpaceStats spaceStats = new SpaceStats();
                        spaceStats.setObjectId(volume.getVvolid());
                        spaceStats.setUnsharedValid(false);
                        String srcPath = vvolPath.getSharePath() + vvolPath.getPath();
                        LOGGER.info("====>  query spaceStats for nas vvol <====  path is " + srcPath);

                        if (VasaConstant.VVOL_TYPE_CONFIG.equals(volume.getVvolType())) {
                            spaceStats.setLogical(volume.getSize());
                            spaceStats.setCommitted(volume.getSize());
                            LOGGER.info("====>  successfully query spaceStats for nas vvol <===" + volume.getSize());
                        } else {
                            SDKResult<NFSvvolQueryResBean> result = infSvvolService.queryNFSvvol(arrayId, null, srcPath);
                            if (0 != result.getErrCode()) {
                                LOGGER.error("queryNFSvvol error.");
                                continue;
                            }
                            spaceStats.setLogical(result.getResult().getFileSize() / MagicNumber.LONG1024 / MagicNumber.LONG1024);
                            spaceStats.setCommitted(result.getResult().getFileSize() / MagicNumber.LONG1024 / MagicNumber.LONG1024);
                            LOGGER.info("====>  successfully query spaceStats for nas vvol <===" + result.getResult().toString());
                        }

                        returnValues.add(spaceStats);
                    } catch (SDKException e) {
                        LOGGER.info("====> query spaceStats for nas vvol failed<===", e.getSdkErrDesc());
                        throw FaultUtil.storageFault();
                    }
                    continue;
                }

                String sourceType = volume.getSourceType();
                if (sourceType.equalsIgnoreCase(VasaSrcTypeConstant.SNAPSHOT)
                        || sourceType.equalsIgnoreCase(VasaSrcTypeConstant.FAST_CLONE)) {

                    SDKResult<S2DPassThroughSnapshot> result =
                            vvolModel.getSnapshotById(volume.getArrayId(),
                                    volume.getRawId());

                    VASAUtil.saveArrayId(volume.getArrayId());
                    SDKResult<SnapshotCreateResBean> snapshotInfo = vasaArrayService
                            .querySnapshotInfo(volume.getRawId());
                    if (0 != snapshotInfo.getErrCode()) {
                        LOGGER.error("getSnapshotById error. params[" + volume.getArrayId() + "," + volume.getRawId()
                                + "] errCode:" + snapshotInfo.getErrCode() + ", description:"
                                + snapshotInfo.getDescription());
                        continue;
                    }

                    SpaceStats spaceStats = new SpaceStats();
                    spaceStats.setObjectId(volume.getVvolid());
                    long userCapacity = Long.valueOf(snapshotInfo.getResult().getUSERCAPACITY());
                    long consumedCapacity = Long.valueOf(snapshotInfo.getResult().getCONSUMEDCAPACITY());
                    spaceStats.setLogical(
                            userCapacity * MagicNumber.LONG512 / MagicNumber.LONG1024 / MagicNumber.LONG1024);
                    if (consumedCapacity > userCapacity) {
                        consumedCapacity = userCapacity;
                    }
                    spaceStats.setCommitted(
                            consumedCapacity * MagicNumber.LONG512 / MagicNumber.LONG1024 / MagicNumber.LONG1024);
                    // spaceStats.setUnshared();TODO
                    spaceStats.setUnsharedValid(false);

                    returnValues.add(spaceStats);
                } else {
                    SDKResult<S2DLun> result = storageModel.getLun(volume.getArrayId(), volume.getRawId());
                    if (0 != result.getErrCode()) {
                        LOGGER.error("getLun error.params[" + volume.getArrayId() + "," + volume.getRawId()
                                + "] errCode:" + result.getErrCode() + ", description:" + result.getDescription());
                        continue;
                    }

                    SpaceStats spaceStats = new SpaceStats();
                    spaceStats.setObjectId(volume.getVvolid());
                    long sectorSize = Long.valueOf(result.getResult().getSECTORSIZE());
                    long capacity = Long.valueOf(result.getResult().getCAPACITY());
                    long allocCapacity = Long.valueOf(result.getResult().getALLOCCAPACITY());
                    spaceStats.setLogical(capacity * sectorSize / MagicNumber.LONG1024 / MagicNumber.LONG1024);
                    if (allocCapacity > capacity) {
                        allocCapacity = capacity;
                    }
                    spaceStats.setCommitted(allocCapacity * sectorSize / MagicNumber.LONG1024 / MagicNumber.LONG1024);
                    // spaceStats.setUnshared();TODO
                    spaceStats.setUnsharedValid(false);

                    returnValues.add(spaceStats);
                }
            } catch (SDKException e) {
                LOGGER.error("StorageFault/getLun error. errCode:" + e.getSdkErrCode());
                throw FaultUtil.storageFault("getLun error. errCode:" + e.getSdkErrCode());
            }
        }

        return returnValues;
    }

    SDKResult<NFSvvolQueryResBean> queryNFSvvol(String arrayId, String vstoreId, String srcPath) {


        return null;
    }

    SDKResult<S2DVirtualPoolSpaceStats> getVirtualPoolSpaceStatsById(String containerId) throws StorageFault {
        SDKResult<S2DVirtualPoolSpaceStats> result = new SDKResult<>();
        try {
            S2DVirtualPoolSpaceStats containerSpaceState = new S2DVirtualPoolSpaceStats();
            List<NStoragePool> poolListByContainerId = storagePoolService.getPoolListByContainerId(containerId);
            LOGGER.debug("getPoolListByContainerId poolListByContainerId=" + poolListByContainerId);
            if (null != poolListByContainerId && poolListByContainerId.size() != 0) {
                String arrayId = poolListByContainerId.get(0).getArrayId();
                SDKResult<List<S2DStoragePool>> storagePoolsResult = VASAUtil
                        .queryAndUpdateStoragePoolInfoByArrayId(arrayId);
                if (0 != storagePoolsResult.getErrCode()) {
                    LOGGER.error("StorageFault/getAllStoragePool error. arrayId = " + arrayId + ",errCode="
                            + storagePoolsResult.getErrCode() + ",description=" + storagePoolsResult.getDescription());
                    throw FaultUtil.storageFault("StorageFault/getAllStoragePool error. arrayId = " + arrayId
                            + ",errCode=" + storagePoolsResult.getErrCode() + ",description="
                            + storagePoolsResult.getDescription());
                }
                Long total_capacity = 0l;
                Long free_capacity = 0l;
                List<S2DStoragePool> storagePools = storagePoolsResult.getResult();
                for (S2DStoragePool s2dStoragePool : storagePools) {
                    for (NStoragePool storagePool : poolListByContainerId) {
                        if (s2dStoragePool.getID().equalsIgnoreCase(storagePool.getRawPoolId())) {
                            String totalCapacity = s2dStoragePool.getTotalCapacity();
                            String freeCapacity = s2dStoragePool.getFreeCapacity();
                            total_capacity += Long.valueOf(totalCapacity) / 2 * MagicNumber.LONG1024;
                            free_capacity += Long.valueOf(freeCapacity) / 2 * MagicNumber.LONG1024;
                        }
                    }
                }
                if (0l == total_capacity) {
                    result.setErrCode(1l);
                    result.setDescription("Get container's pool size error. total_capacity = " + total_capacity
                            + ",containerId=" + containerId);
                } else {
                    containerSpaceState.setFree_capacity(free_capacity);
                    containerSpaceState.setTotal_capacity(total_capacity);
                    containerSpaceState.setId(containerId);
                    result.setErrCode(0l);
                    result.setResult(containerSpaceState);
                }
            }

        } catch (SDKException e) {
            LOGGER.error("StorageFault/getAllStoragePool error. containerId = " + containerId, e);
            throw FaultUtil.storageFault("StorageFault/getAllStoragePool error. containerId = " + containerId);
        }

        return result;

    }

    public List<ContainerSpaceStats> spaceStatsForStorageContainer(String containerId,
                                                                   List<String> capabilityProfileIds) throws StorageFault {
        List<ContainerSpaceStats> returnValues = new ArrayList<ContainerSpaceStats>();
        SDKResult<S2DVirtualPoolSpaceStats> result = getVirtualPoolSpaceStatsById(containerId);
        if (0 != result.getErrCode()) {
            LOGGER.error("StorageFault/getVirtualPoolSpaceStatsById error. params[" + containerId + "] errCode:"
                    + result.getErrCode() + ", description:" + result.getDescription());
            throw FaultUtil.storageFault("getVirtualPoolSpaceStatsById error. containerId:" + containerId + ", errCode:"
                    + result.getErrCode() + ", description:" + result.getDescription());
        }
        if (null == capabilityProfileIds || 0 == capabilityProfileIds.size()) {
            ContainerSpaceStats spaceStats = new ContainerSpaceStats();
            spaceStats.setObjectId(containerId);
            long totalCapacity = result.getResult().getTotal_capacity() / MagicNumber.LONG1024 / MagicNumber.LONG1024;
            long freeCapacity = result.getResult().getFree_capacity() / MagicNumber.LONG1024 / MagicNumber.LONG1024;
            long usedCapacity = totalCapacity - freeCapacity;
            spaceStats.setPhysicalTotal(totalCapacity);
            spaceStats.setPhysicalFree(freeCapacity);
            spaceStats.setPhysicalUsed(usedCapacity);
            spaceStats.setLogicalFree(0);
            spaceStats.setLogicalLimit(0);
            spaceStats.setLogicalUsed(0);

            returnValues.add(spaceStats);
            return returnValues;
        }
        for (String capaProfileId : capabilityProfileIds) {
            ContainerSpaceStats spaceStats = new ContainerSpaceStats();
            spaceStats.setObjectId(capaProfileId);
            long totalCapacity = result.getResult().getTotal_capacity() / MagicNumber.LONG1024 / MagicNumber.LONG1024;
            long freeCapacity = result.getResult().getFree_capacity() / MagicNumber.LONG1024 / MagicNumber.LONG1024;
            long usedCapacity = totalCapacity - freeCapacity;
            spaceStats.setPhysicalTotal(totalCapacity);
            spaceStats.setPhysicalFree(freeCapacity);
            spaceStats.setPhysicalUsed(usedCapacity);
            spaceStats.setLogicalFree(0);
            spaceStats.setLogicalLimit(0);
            spaceStats.setLogicalUsed(0);

            returnValues.add(spaceStats);
        }
        LOGGER.debug("spaceStatsForStorageContainer returnValues=" + returnValues);
        return returnValues;
    }

    public TaskInfo prepareToSnapshotVirtualVolume(NVirtualVolume vvol, StorageProfile storageProfile)
            throws StorageFault {
        String vvolId = vvol.getVvolid();
        String creationTime = null;
        TaskInfo taskInfo = new TaskInfo();
        try {
            VvolControlRunable.getInstance(virtualVolumeService).incrDjTask();
            LOGGER.info("traffic control,create snapshot dj tasks increase once. current is: "
                    + VvolControlRunable.getInstance(virtualVolumeService).getDjTask() + "/"
                    + VvolConstant.MAX_SEND_DJ);
            String snapShotId = UUID.randomUUID().toString();
            String snapDisplayName = VASAUtil.buildDisplayName(VasaSrcTypeConstant.SNAPSHOT);
            VASAUtil.checkProfileNoQos(storageProfile);
            Date date = getCurrentTimeFormat();
            insertCreateSnapshotData2DataBase(vvol, snapShotId, date, storageProfile, vvol.getSize());

            if (isNasVvol(vvolId)) {
                SDKResult<FileSystemSnapshotCreateResBean> createSnapshot = vasaNasArrayService
                        .createSnapshotFromSourceFS(vvolId, snapShotId, snapDisplayName, "");
                if (0 != createSnapshot.getErrCode()) {
                    LOGGER.error("StorageFault/createSnapshot error. params[" + vvolId.substring(vvolId.indexOf('.') + 1)
                            + "," + snapDisplayName + "] errCode:" + createSnapshot.getErrCode() + ", description:"
                            + createSnapshot.getDescription());
                    throw FaultUtil.storageFault("createSnapshot error. vvolId:" + vvol.getVvolid() + ", errCode:"
                            + createSnapshot.getErrCode() + ", description:" + createSnapshot.getDescription());
                }

                updateVirtualVolume(snapShotId, "NA", "NA", VASAArrayUtil.VVOLSTATUS.active);
                taskInfo.setTaskState(TaskStateEnum.SUCCESS.value());

                List<String> vvolIds = new ArrayList<String>();
                vvolIds.clear();
                vvolIds.add(vvolId);
                List<NVirtualVolume> vvols = DiscoverService.getInstance().queryVirtualVolumeFromDataBase(vvolIds);
                if (1 != vvols.size()) {
                    LOGGER.warn("not found vvol in database:" + vvolId);
                    taskInfoService.updateTaskInfoByTaskId(taskInfo, null);
                    return taskInfo;
                }

                List<SpaceStats> spaceStatsResult = spaceStatsForVirtualVolume(vvols);
                if (0 == spaceStatsResult.size()) {
                    LOGGER.warn("spaceStatsForVirtualVolume error.vvolId:" + vvolId);
                    taskInfoService.updateTaskInfoByTaskId(taskInfo, null);
                    return taskInfo;
                }

                SpaceStats spaceStats = spaceStatsResult.get(0);
                VirtualVolumeInfo vvolInfo = queryVirtualVolumeInfo(vvolId);
                PrepareSnapshotResult snapshotResult = new PrepareSnapshotResult();
                snapshotResult.setParentInfo(vvolInfo);
                snapshotResult.setParentStats(spaceStats);
                snapshotResult.setSnapshotId(VasaConstant.VVOL_PREFIX + snapShotId);
                taskInfo.setResult(snapshotResult);
            } else {
                SDKResult<SnapshotCreateResBean> createSnapshot = vasaArrayService
                        .createSnapshotFromSourceVolume(vvol.getRawId(), snapDisplayName, "");
                if (0 != createSnapshot.getErrCode()) {
                    LOGGER.error("StorageFault/createSnapshot error. params[" + vvolId.substring(vvolId.indexOf('.') + 1)
                            + "," + snapDisplayName + "] errCode:" + createSnapshot.getErrCode() + ", description:"
                            + createSnapshot.getDescription());
                    throw FaultUtil.storageFault("createSnapshot error. vvolId:" + vvol.getVvolid() + ", errCode:"
                            + createSnapshot.getErrCode() + ", description:" + createSnapshot.getDescription());
                }

                virtualVolumeService.updateArrayIdAndRawIdByVvolId(VasaConstant.VVOL_PREFIX + snapShotId, VASAUtil.getArrayId(),
                        createSnapshot.getResult().getID());
                taskInfo.setTaskState(TaskStateEnum.RUNNING.value());
            }

            taskInfo.setName("prepareToSnapshotVirtualVolume");
            taskInfo.setCancelable(false);
            taskInfo.setCancelled(false);
            taskInfo.setProgressUpdateAvailable(true);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            taskInfo.setStartTime(calendar);

            taskInfo.setArrayId(VASAUtil.getStorageArrayUUID(vvol.getArrayId()));
            taskInfo.setTaskId("prepareToSnapshotVirtualVolume:" + VasaConstant.VVOL_PREFIX + snapShotId);
            taskInfoService.saveTaskInfo(taskInfo, null);
            LOGGER.debug(taskInfo);
            return taskInfo;
        } catch (SDKException e) {
            LOGGER.error("StorageFault/createSnapshot error. errCode:" + e.getSdkErrCode() + ", description:"
                    + e.getMessage());
            throw FaultUtil.storageFault(
                    "createSnapshot error. errCode:" + e.getSdkErrCode() + ", description:" + e.getMessage());
        } catch (ParseException e) {
            LOGGER.error("StorageFault/createVirtualVolume parse time error. time is : " + creationTime);
            throw FaultUtil.storageFault("createVirtualVolume parse time error. time is : " + creationTime);
        }

    }

    private void insertCreateSnapshotData2DataBase(NVirtualVolume vvol, String snapshotId, Date createDate,
                                                   StorageProfile storageProfile, long size) throws StorageFault {
        String createdTime = null;
        List<NVvolProfile> vvolProfiles = new ArrayList<NVvolProfile>();
        // 将storageProfile插入VvolProfile
        // 什么情况下storageProfile会为空？？？？？？？？？？？？？？？？？？？？？？？？？？？？？
        if (!VASAUtil.checkProfileNull(storageProfile)) {
            if (VASAUtil.getPolicyNoQos()) {
                if (!VASAUtil.checkProfileIdInStorageProfile(storageProfile.getProfileId())) {
                    String uniProfileId = UUID.randomUUID().toString();
                    NStorageProfile nStorageProfile = new NStorageProfile();
                    nStorageProfile.setProfileId(uniProfileId);
                    nStorageProfile.setPolicyId(storageProfile.getProfileId());
                    nStorageProfile.setProfileName(storageProfile.getName());
                    nStorageProfile.setGenerationId(storageProfile.getGenerationId());
                    nStorageProfile.setDeprecated("false");
                    nStorageProfile.setContainerId(vvol.getContainerId());
                    nStorageProfile.setCreatedTime(new Date());
                    storageProfileService.save(nStorageProfile);
                }
            }

            for (CapabilityInstance capaInstance : storageProfile.getConstraints().getSubProfiles().get(0)
                    .getCapability()) {
                PropertyInstance proInstance = capaInstance.getConstraint().get(0).getPropertyInstance().get(0);
                NVvolProfile vvolProfile = new NVvolProfile();
                vvolProfile.setVvolid(VasaConstant.VVOL_PREFIX + snapshotId);
                vvolProfile.setProfileName(storageProfile.getName());
                vvolProfile.setProfileId(storageProfile.getProfileId());
                vvolProfile.setCreatedBy(storageProfile.getCreatedBy());
                vvolProfile.setCreationTime(storageProfile.getCreationTime().getTime());
                vvolProfile.setGenerationId(storageProfile.getGenerationId());
                vvolProfile.setCapability(proInstance.getId());
                vvolProfile.setType(VASAUtil.getPropertyValueType(proInstance.getValue()));
                vvolProfile.setValue(VASAUtil.convertPropertyValue(proInstance.getValue()));
                vvolProfiles.add(vvolProfile);
            }
        }

        // 将Snapshot信息插入VirtualVolume表
        NVirtualVolume volume = new NVirtualVolume();
        volume.setVvolid(VasaConstant.VVOL_PREFIX + snapshotId);
        volume.setSize(size);
        volume.setCreatedBy("Opensds");
        volume.setCreationTime(createDate);
        volume.setContainerId(vvol.getContainerId());
        volume.setVvolType(vvol.getVvolType());
        volume.setSourceType(VasaSrcTypeConstant.SNAPSHOT);
        volume.setParentId(vvol.getVvolid());
        volume.setArrayId(vvol.getArrayId());
        volume.setRawPoolId(vvol.getRawPoolId());
        volume.setRawId("NA");
        LOGGER.info("In insertCreateSnapshotData2DataBase,update volume status is creating. vvolid is: " + VasaConstant.VVOL_PREFIX
                + snapshotId);
        volume.setStatus("creating");

        virtualVolumeService.addCreateDataIntoDatabase(volume, vvolProfiles, null);
    }

    public BatchReturnStatus snapshotVirtualVolume(NVirtualVolume snapVvol, List<NameValuePair> snapshotMetadata,
                                                   long timeoutMS) throws StorageFault {
        try {
            if (isNasVvol(snapVvol.getVvolid())) {
                BatchReturnStatus returnStatus = new BatchReturnStatus();
                returnStatus.setUniqueId(snapVvol.getVvolid());

                // 将snapshot的metadata存入数据库
                insertSnapshotMetadata2DataBase(snapVvol, snapshotMetadata);
                return returnStatus;
            }
            String snapshotId = snapVvol.getRawId();
            List<String> snapshotIds = new ArrayList<String>();
            snapshotIds.add(snapshotId);

            Date beginDate = new Date();
            long beginTimeMS = beginDate.getTime();
            SDKErrorCode sdkErrorCode = vvolModel.activateSnapshot(snapVvol.getArrayId(), snapshotIds);
            if (0 != sdkErrorCode.getErrCode()) {
                LOGGER.error("activateSnapshot error. params[" + snapVvol.getArrayId() + ","
                        + VASAUtil.convertArrayToStr(snapshotIds) + "] errCode:" + sdkErrorCode.getErrCode()
                        + ", description:" + sdkErrorCode.getDescription());
                virtualVolumeService.updateStatusByVvolId(snapVvol.getVvolid(),
                        VASAArrayUtil.VVOLSTATUS.error_creating);
                BatchReturnStatus returnStatus = new BatchReturnStatus();
                BatchErrorResult errorResult = new BatchErrorResult();
                errorResult.getError().add(new com.vmware.vim.vasa.v20.fault.xsd.StorageFault());
                returnStatus.setErrorResult(errorResult);
                returnStatus.setUniqueId(snapVvol.getVvolid());
                return returnStatus;
            }

            Date endDate = new Date();
            long endTimeMS = endDate.getTime();

            if ((endTimeMS - beginTimeMS) > timeoutMS) {
                LOGGER.error("activateSnapshot error. arrayId:" + snapVvol.getArrayId() + ", snapshotIds:"
                        + VASAUtil.convertArrayToStr(snapshotIds) + ", actualTime consumed:" + (endTimeMS - beginTimeMS)
                        + ", timeoutMS:" + timeoutMS);

                BatchReturnStatus returnStatus = new BatchReturnStatus();
                BatchErrorResult errorResult = new BatchErrorResult();
                errorResult.getError().add(new com.vmware.vim.vasa.v20.fault.xsd.Timeout());
                returnStatus.setErrorResult(errorResult);
                returnStatus.setUniqueId(snapVvol.getVvolid());
                return returnStatus;
            }

            BatchReturnStatus returnStatus1 = new BatchReturnStatus();
            returnStatus1.setUniqueId(snapVvol.getVvolid());

            // 将snapshot的metadata存入数据库
            insertSnapshotMetadata2DataBase(snapVvol, snapshotMetadata);
            // 存储虚拟机相关信息
            String vmId = "";
            for (NameValuePair pair : snapshotMetadata) {
                if ("VMW_VmID".equalsIgnoreCase(pair.getParameterName())) {
                    vmId = pair.getParameterValue();
                }
            }
            String vmName = VASAUtil.getRegularVMName(snapshotMetadata);
            LOGGER.info("The vvolId=" + snapVvol.getVvolid() + " vmName=" + vmName + " vmId=" + vmId);
            if (vmName != null && !"".equals(vmName) && vmId != null && !"".equals(vmId)) {
                virtualVolumeService.updateVmInfoByVvolId(vmId, vmName, snapVvol.getVvolid());
            }
            // 激活快照成功后将vvol快照的状态改为active写入数据库
            virtualVolumeService.updateStatusByVvolId(snapVvol.getVvolid(), "active");

            return returnStatus1;
        } catch (SDKException e) {
            LOGGER.error("StorageFault/activateSnapshot error. errCode:" + e.getSdkErrCode() + ", description:"
                    + e.getMessage());
            throw FaultUtil.storageFault(
                    "activateSnapshot error. errCode:" + e.getSdkErrCode() + ", description:" + e.getMessage());
        }
    }

    private void insertSnapshotMetadata2DataBase(NVirtualVolume snapVvol, List<NameValuePair> snapshotMetadata)
            throws StorageFault {
        // 将metadata插入数据库
        for (NameValuePair pair : snapshotMetadata) {
            NVvolMetadata vvolMetadata = new NVvolMetadata();
            vvolMetadata.setVvolid(snapVvol.getVvolid());
            vvolMetadata.setKey(pair.getParameterName());
            vvolMetadata.setValue(pair.getParameterValue());
            vvolMetadataService.addVvolMetadata(vvolMetadata);
        }
    }

    public TaskInfo revertVirtualVolume(NVirtualVolume snapshot) throws StorageFault {
        try {
            TaskInfo taskInfo = new TaskInfo();

            taskInfo.setName("revertVirtualVolume");
            taskInfo.setCancelable(false);
            taskInfo.setCancelled(false);
            taskInfo.setProgressUpdateAvailable(true);
            taskInfo.setProgress(0);

            taskInfo.setStartTime(DateUtil.getUTCCalendar());

            taskInfo.setArrayId(VASAUtil.getStorageArrayUUID(snapshot.getArrayId()));
            taskInfo.setTaskId("revertVirtualVolume:" + snapshot.getArrayId() + ":" + snapshot.getRawId());

            SDKErrorCode sdkErrorCode = new SDKErrorCode();
            if (isNasVvol(snapshot.getVvolid())) {
                sdkErrorCode = vasaNasArrayService.rollbackFsSapshot(snapshot.getVvolid());
            } else {
                sdkErrorCode = vvolModel.rollbackSnapshot(snapshot.getArrayId(), snapshot.getRawId());
            }
            if (0 != sdkErrorCode.getErrCode()) {
                LOGGER.error("StorageFault/rollbackSnapshot error. params[" + snapshot.getArrayId() + ","
                        + snapshot.getRawId() + "] errCode:" + sdkErrorCode.getErrCode() + ", description:"
                        + sdkErrorCode.getDescription());
                taskInfo.setTaskState(TaskStateEnum.ERROR.value());
            } else {
                taskInfo.setTaskState(TaskStateEnum.SUCCESS.value());
            }

            return taskInfo;
        } catch (SDKException e) {
            LOGGER.error("StorageFault/rollbackSnapshot error. errCode:" + e.getSdkErrCode() + ", description:"
                    + e.getMessage());
            throw FaultUtil.storageFault(
                    "rollbackSnapshot error. errCode:" + e.getSdkErrCode() + ", description:" + e.getMessage());
        }
    }

    private boolean isCloneForNas(String srcVvol, String newContainerId) throws StorageFault {
        NStorageContainer container = storageContainerService.getStorageContainerByContainerId(newContainerId);
        LOGGER.info("current container type is " + container.getContainerType());
        boolean isNasType = isNasVvol(srcVvol);
        boolean isNasContainer = VasaConstant.CONTAINER_TYPE_NAS.equals(container.getContainerType());
        if (isNasType && isNasContainer) {
            LOGGER.info("clone for nas.");
            return true;
        } else if (!isNasType && !isNasContainer) {
            LOGGER.info("clone for san.");
            return false;
        } else {
            String srcType = isNasType ? "NAS" : "SAN";
            String dstType = isNasContainer ? "NAS" : "SAN";
            LOGGER.error("clone para error srcContainerType[" + srcType + "],dstContainerType[" + dstType + "] !!");
            throw FaultUtil.storageFault("clone para error !!");
        }
    }

    public TaskInfo cloneVirtualVolume(long sizeInMB, StoragePolicy storagePolicy, StorageProfile storageProfile,
                                       List<NameValuePair> metadata, String newContainerId, String srcVvolType, Boolean removeThin,
                                       StorageProfile profile_to_insert, String thinValue, NVirtualVolume srcVvol)
            throws StorageFault, OutOfResource, VasaProviderBusy, ResourceInUse, InvalidArgument, NotFound {
        String vmName = VASAUtil.getRegularVMName(metadata);
        String uuid = UUID.randomUUID().toString();
        String createVvolId = VasaConstant.VVOL_PREFIX + uuid;
        List<NStoragePool> pools = storagePoolService.getPoolListByContainerId(newContainerId);
        if (null == pools || pools.size() == 0) {
            LOGGER.error("Container have no storagePools. containerId = " + newContainerId);
            throw FaultUtil.invalidArgument();
        }

        if (!pools.get(0).getArrayId().equalsIgnoreCase(srcVvol.getArrayId())) {
            LOGGER.warn("clone error.srcArrayId=" + srcVvol.getArrayId() + ",dstArrayId=" + pools.get(0).getArrayId());
            return createCloneTask(srcVvol.getArrayId(), createVvolId, TaskStateEnum.ERROR.value());
        }

        // 1、获取storagePool通过 containerId
        List<S2DStoragePool> arrayPools = refreshStoragePool();
        List<S2DStoragePool> containerPools = filterContainerPools(arrayPools, pools);

        if (thinValue != null && thinValue.equalsIgnoreCase("Thick")) {
            checkContainerSpaceOutOfResource(pools, sizeInMB);
        }
        // 2、通过数据查询对应符合的storagePool
        S2DStoragePool storagePool = selectPoolService.selectPool(containerPools, storagePolicy, sizeInMB);
        LOGGER.info("cloneVirtualVolume, select storagepool rawId = " + storagePool.getID());
        try {
            int sizeInGB = (sizeInMB % 1024 == 0) ? (int) sizeInMB / 1024 : ((int) sizeInMB / 1024 + 1);
            String vvolDisplayName = VASAUtil.buildVvolDisplayName(srcVvolType, metadata);

            VvolControlRunable.getInstance(virtualVolumeService).incrDjTask();
            LOGGER.info("traffic control, clone vvol dj tasks increase once. current is: "
                    + VvolControlRunable.getInstance(virtualVolumeService).getDjTask() + "/"
                    + VvolConstant.MAX_SEND_DJ);

            String lunId = srcVvol.getLunId();
            TaskInfo taskInfo = null;//createCloneTask(srcVvol.getArrayId(), createVvolId, TaskStateEnum.RUNNING.value());
            // CloneVvolTask task = new CloneVvolTask(taskInfo);

            LOGGER.debug("the source vvol  = " + srcVvol);

            String profileId = storageProfile.getProfileId();
            String profileName = storageProfile.getName();
            long generationId = storageProfile.getGenerationId();
            if (profileId.equalsIgnoreCase("5200177B-98B7-4A0D-BFB5-11C02CE223E6") && (null != profile_to_insert)) {
                // This profile is the defaultProfile in vasa db, not the
                // profile from vcenter.
                profileId = profile_to_insert.getProfileId();
                profileName = profile_to_insert.getName();
                generationId = profile_to_insert.getGenerationId();
            }
            String uniProfileId = getUniProfileId(profileId);
            int ioProperty = getIOPropertyByQos(storagePolicy, srcVvolType);
            Map<String, String> taskProperties = new HashMap<>();
            String dstLunId = null;
            String dstWwn = null;
            insertCloneVirtualVolumeData2DataBase(srcVvol, createVvolId, storagePool.getID(), uniProfileId, sizeInMB,
                    storageProfile, metadata, newContainerId, srcVvolType, removeThin, profile_to_insert);
            if (isCloneForNas(srcVvol.getVvolid(), newContainerId)) {
                MigrateResult migRsp = vasaNasArrayService.migrateFilesystem(VASAUtil.getArrayId(), storagePool.getID(), sizeInMB, metadata,
                        newContainerId, srcVvolType, srcVvol, uuid);
                //VasaConstant.VVOL_PREFIX + uuid + snapshotId + poolId + targetfsname + opt + srcfsId + sourcePath
                String taskResult = createVvolId;
                createVvolId = createVvolId + VasaConstant.TASK_SEPARATOR + migRsp.snapshotId + VasaConstant.TASK_SEPARATOR + migRsp.poolId
                        + VasaConstant.TASK_SEPARATOR + migRsp.fsName + VasaConstant.TASK_SEPARATOR + migRsp.migrateOpt
                        + VasaConstant.TASK_SEPARATOR + migRsp.sourceFsId + VasaConstant.TASK_SEPARATOR + migRsp.sourcePath;
                taskInfo = createCloneTask(srcVvol.getArrayId(), createVvolId, TaskStateEnum.RUNNING.value());
                taskInfo.setResult(taskResult);
            } else {
                SDKResult<LunCreateResBean> createLun = vasaArrayService.createLun(vvolDisplayName, "", sizeInGB, sizeInMB,
                        vmName, storagePool.getID(), thinValue, storagePolicy.getSmartTier(), ioProperty);
                if (0 != createLun.getErrCode()) {
                    LOGGER.error("createLun error ! errMsg=" + createLun.getDescription());
                    virtualVolumeService.updateStatusByVvolId(createVvolId, VASAArrayUtil.VVOLSTATUS.error_creating);
                    throw FaultUtil.storageFault("createLun error ! errMsg=" + createLun.getDescription());
                }
                LunCreateResBean lunCreateResBean = createLun.getResult();
                dstLunId = lunCreateResBean.getID();
                dstWwn = lunCreateResBean.getWWN();
                taskInfo = createCloneTask(srcVvol.getArrayId(), createVvolId, TaskStateEnum.RUNNING.value());
            }
            updateVirtualVolume(uuid, dstLunId, dstWwn,
                    VASAArrayUtil.VVOLSTATUS.initing);
            createTaskPolicy(storagePolicy, profileName, generationId, profileId, uniProfileId, taskProperties);
            // vasaArrayService.createStorageProfile(storagePolicy,
            // profileId,uniProfileId, profileName, lunCreateResBean.getID(),
            // generationId, createVvolId, vvolType);
            // 更改卷状态为初始化中,并且将获取的lunid 与 wwn存入数据库中
//			updateVirtualVolume(uuid, dstLunId, dstWwn,
//					VASAArrayUtil.VVOLSTATUS.initing);
            // vasaArrayService.createStorageProfile(storagePolicy,
            // profileId,uniProfileId, profileName, lunCreateResBean.getID(),
            // generationId, createVvolId, vvolType);
            taskProperties.put(NTaskInfo.SRC_RAW_ID, lunId);
            taskInfoService.saveTaskInfo(taskInfo, taskProperties);
            return taskInfo;
        } catch (Exception e) {
            LOGGER.error("StorageFault/cloneVirtualVolume error.", e);
            throw FaultUtil.storageFault("cloneVirtualVolume error.");
        }
    }

    private String getUniProfileId(String profileId) {
        String uniProfileId = null;
        if (VASAUtil.getPolicyNoQos()) {
            uniProfileId = profileId;
        } else {
            uniProfileId = UUID.randomUUID().toString();
        }
        return uniProfileId;
    }

    private TaskInfo createCloneTask(String arrayId, String createVvolId, String taskState) {
        String taskId = "cloneVirtualVolume" + VasaConstant.TASK_SEPARATOR + createVvolId;
        TaskInfo taskInfo = new TaskInfo();
        taskInfo.setName("cloneVirtualVolume");
        taskInfo.setCancelable(true);
        taskInfo.setCancelled(false);
        taskInfo.setProgressUpdateAvailable(true);
        taskInfo.setStartTime(DateUtil.getUTCCalendar());
        taskInfo.setProgress(0);
        taskInfo.setArrayId(VASAUtil.getStorageArrayUUID(arrayId));
        taskInfo.setTaskId(taskId);
        taskInfo.setTaskState(taskState);
        return taskInfo;
    }

    private void insertCloneVirtualVolumeData2DataBase(NVirtualVolume srcVvol, String vvolId, String rawPoolId,
                                                       String uniProfileId, long sizeInMB, StorageProfile storageProfile, List<NameValuePair> metadata,
                                                       String containerId, String vvolType, Boolean removeThin, StorageProfile profile_to_insert)
            throws StorageFault {
        LOGGER.info("begin insertCloneVirtualVolumeData2DataBase...");
        String creationTime = null;
        try {
            List<NVvolProfile> listVvolProfile = new ArrayList<NVvolProfile>();
            List<NVvolMetadata> listVvolMetadata = new ArrayList<NVvolMetadata>();
            // 将storageProfile插入数据库
            if (null == profile_to_insert) {
                for (CapabilityInstance capaInstance : storageProfile.getConstraints().getSubProfiles().get(0)
                        .getCapability()) {
                    PropertyInstance proInstance = capaInstance.getConstraint().get(0).getPropertyInstance().get(0);
                    if (removeThin && proInstance.getId().equalsIgnoreCase(VASAUtil.VMW_STD_CAPABILITY)) {
                        LOGGER.warn("thin value:" + proInstance.getValue() + " removed, not inserted into database.");
                        continue;
                    }
                    NVvolProfile vvolProfile = new NVvolProfile();
                    vvolProfile.setVvolid(vvolId);
                    vvolProfile.setProfileName(storageProfile.getName());
                    vvolProfile.setProfileId(uniProfileId);
                    vvolProfile.setCreatedBy(storageProfile.getCreatedBy());
                    vvolProfile.setCreationTime(storageProfile.getCreationTime().getTime());
                    vvolProfile.setGenerationId(storageProfile.getGenerationId());
                    vvolProfile.setCapability(proInstance.getId());
                    vvolProfile.setType(VASAUtil.getPropertyValueType(proInstance.getValue()));
                    vvolProfile.setValue(VASAUtil.convertPropertyValue(proInstance.getValue()));

                    listVvolProfile.add(vvolProfile);
                }
            } else {
                NVvolProfile vvolProfile = new NVvolProfile();
                vvolProfile.setVvolid(vvolId);
                vvolProfile.setProfileName(profile_to_insert.getName());
                vvolProfile.setProfileId(uniProfileId);
                vvolProfile.setCreatedBy(profile_to_insert.getCreatedBy());
                vvolProfile.setCreationTime(profile_to_insert.getCreationTime().getTime());
                vvolProfile.setGenerationId(profile_to_insert.getGenerationId());

                listVvolProfile.add(vvolProfile);
            }
            String vmId = "";
            // 将metadata插入数据库
            for (NameValuePair pair : metadata) {
                NVvolMetadata vvolMetadata = new NVvolMetadata();
                vvolMetadata.setVvolid(vvolId);
                vvolMetadata.setKey(pair.getParameterName());
                vvolMetadata.setValue(pair.getParameterValue());
                if ("VMW_VmID".equalsIgnoreCase(pair.getParameterName())) {
                    vmId = pair.getParameterValue();
                }
                listVvolMetadata.add(vvolMetadata);
            }
            String vmName = VASAUtil.getRegularVMName(metadata);
            // 将创建成功的vvol信息插入VirtualVolume Table
            // creationTime = DateUtil.getUTCDate().toString();
            Date date = getCurrentTimeFormat();

            NVirtualVolume volume = new NVirtualVolume();
            volume.setVvolid(vvolId);
            volume.setSize(sizeInMB);
            volume.setCreatedBy("Opensds");
            volume.setCreationTime(date);
            volume.setContainerId(containerId);
            volume.setVvolType(vvolType);
            volume.setSourceType(VasaSrcTypeConstant.CLONE);
            volume.setParentId(srcVvol.getVvolid());
            volume.setArrayId(srcVvol.getArrayId());
            volume.setRawPoolId(rawPoolId);
            volume.setRawId("NA");
            if (vmId != null && !"".equals(vmId)) {
                volume.setVmId(vmId);
            }
            if (vmName != null && !"".equals(vmName)) {
                volume.setVmName(vmName);
            }

            // 克隆的时候将卷的状态在vasa数据库中置为creating
            LOGGER.info(
                    "In insertCloneVirtualVolumeData2DataBase, update volume status creating. vvolId is: " + vvolId);
            volume.setStatus(VASAArrayUtil.VVOLSTATUS.creating);

            virtualVolumeService.addCreateDataIntoDatabase(volume, listVvolProfile, listVvolMetadata);
        } catch (ParseException e) {
            LOGGER.error("insertCloneVirtualVolumeData2DataBase parse time error, time is:" + creationTime);
            throw FaultUtil
                    .storageFault("insertCreateVirtualVolumeData2DataBase parse time error, time is:" + creationTime);
        }

        LOGGER.info("end insertCloneVirtualVolumeData2DataBase");
    }

    private void insertFastCloneVirtualVolumeData2DataBase(NVirtualVolume vvol, String vvolId, Date createDate,
                                                           StorageProfile storageProfile, List<NameValuePair> metadata) throws StorageFault, ParseException {
        List<NVvolProfile> vvolProfiles = new ArrayList<NVvolProfile>();
        String vmId = "";
        String vmName = "";
        // 将storageProfile插入VvolProfile
        if (!VASAUtil.checkProfileNull(storageProfile)) {
            String uniProfileId = UUID.randomUUID().toString();
            if (VASAUtil.getPolicyNoQos()) {
                if (!VASAUtil.checkProfileIdInStorageProfile(storageProfile.getProfileId())) {
                    NStorageProfile nStorageProfile = new NStorageProfile();
                    nStorageProfile.setProfileId(uniProfileId);
                    nStorageProfile.setPolicyId(storageProfile.getProfileId());
                    nStorageProfile.setProfileName(storageProfile.getName());
                    nStorageProfile.setGenerationId(storageProfile.getGenerationId());
                    nStorageProfile.setDeprecated("false");
                    nStorageProfile.setContainerId(vvol.getContainerId());
                    nStorageProfile.setCreatedTime(new Date());
                    storageProfileService.save(nStorageProfile);
                }
            }

            for (CapabilityInstance capaInstance : storageProfile.getConstraints().getSubProfiles().get(0)
                    .getCapability()) {
                PropertyInstance proInstance = capaInstance.getConstraint().get(0).getPropertyInstance().get(0);
                NVvolProfile vvolProfile = new NVvolProfile();
                vvolProfile.setVvolid(VasaConstant.VVOL_PREFIX + vvolId);
                vvolProfile.setProfileName(storageProfile.getName());
                vvolProfile.setProfileId(uniProfileId);
                vvolProfile.setCreatedBy(storageProfile.getCreatedBy());
                vvolProfile.setCreationTime(storageProfile.getCreationTime().getTime());
                vvolProfile.setGenerationId(storageProfile.getGenerationId());
                vvolProfile.setCapability(proInstance.getId());
                vvolProfile.setType(VASAUtil.getPropertyValueType(proInstance.getValue()));
                vvolProfile.setValue(VASAUtil.convertPropertyValue(proInstance.getValue()));
                vvolProfiles.add(vvolProfile);
            }
        }

        List<NVvolMetadata> listVvolMetadata = new ArrayList<NVvolMetadata>();
        // 将metadata插入数据库
        for (NameValuePair pair : metadata) {
            NVvolMetadata vvolMetadata = new NVvolMetadata();
            vvolMetadata.setVvolid(VasaConstant.VVOL_PREFIX + vvolId);
            vvolMetadata.setKey(pair.getParameterName());
            vvolMetadata.setValue(pair.getParameterValue());
            if ("VMW_VmID".equalsIgnoreCase(pair.getParameterName())) {
                vmId = pair.getParameterValue();
            }
            listVvolMetadata.add(vvolMetadata);
        }

        // 将volume信息插入VirtualVolume表
        // createdTime = newVvol.getCreated_at();
        // Date date = DateUtil.getFormateDate(createdTime,
        // VASAUtil.PATTEN_FORMAT);

        vmName = VASAUtil.getRegularVMName(metadata);

        NVirtualVolume volume = new NVirtualVolume();
        volume.setVvolid(VasaConstant.VVOL_PREFIX + vvolId);
        volume.setSize(vvol.getSize());
        volume.setCreatedBy("Opensds");
        volume.setCreationTime(createDate);
        volume.setContainerId(vvol.getContainerId());
        volume.setVvolType(vvol.getVvolType());
        volume.setSourceType(VasaSrcTypeConstant.FAST_CLONE);
        volume.setParentId(vvol.getVvolid());
        volume.setArrayId(vvol.getArrayId());
        volume.setRawPoolId(vvol.getRawPoolId());
        volume.setRawId("NA");
        if (vmId != null && !"".equals(vmId)) {
            volume.setVmId(vmId);
        }
        if (vmName != null && !"".equals(vmName)) {
            volume.setVmName(vmName);
        }
        LOGGER.info("In insertCloneVirtualVolumeData2DataBase,update volume status is creating. vvolid is: "
                + VasaConstant.VVOL_PREFIX + vvolId);
        volume.setStatus("creating");

        virtualVolumeService.addCreateDataIntoDatabase(volume, vvolProfiles, listVvolMetadata);
    }

    public TaskInfo fastCloneVirtualVolume(NVirtualVolume vvol, StorageProfile storageProfile,
                                           List<NameValuePair> metadata) throws StorageFault, SnapshotTooMany {
        String vvolId = vvol.getVvolid();
        String creationTime = null;
        try {
            // SDKResult<S2DVolume> result = null;
            VvolControlRunable.getInstance(virtualVolumeService).incrDjTask();
            LOGGER.info("traffic control,fast clone vvol tasks increase once."
                    + VvolControlRunable.getInstance(virtualVolumeService).getDjTask() + "/"
                    + VvolConstant.MAX_SEND_DJ);

            String vvolDisplayName = VASAUtil.buildVvolDisplayName(vvol.getVvolType(), metadata);
            String newVVolId = UUID.randomUUID().toString();
            String vmName = VASAUtil.getRegularVMName(metadata);
            Date createDate = getCurrentTimeFormat();
            LOGGER.info("Begin fastCloneVirtualVolume. params[" + vvolDisplayName + ","
                    + vvolId.substring(vvolId.indexOf('.') + 1) + "]");
            VASAUtil.checkProfileNoQos(storageProfile);
            TaskInfo taskInfo = new TaskInfo();
            VASAUtil.saveArrayId(vvol.getArrayId());
            insertFastCloneVirtualVolumeData2DataBase(vvol, newVVolId, createDate, storageProfile, metadata);
            if (isNasVvol(vvol.getVvolid())) {
                vasaNasArrayService.cloneFileSystem(vvol.getRawPoolId(), vvol, newVVolId);
                updateVirtualVolume(newVVolId, "NA", "NA", VASAArrayUtil.VVOLSTATUS.available);
                taskInfo.setResult(VasaConstant.VVOL_PREFIX + newVVolId);
                taskInfo.setTaskState(TaskStateEnum.SUCCESS.value());
            } else {
                SDKResult<SnapshotCreateResBean> createSnapshot = vasaArrayService
                        .createSnapshotFromSourceVolume(vvol.getRawId(), vvolDisplayName, "");

                if (0 != createSnapshot.getErrCode()) {
                    LOGGER.error("fasrCloneFromSourceVolume/createSnapshot error! ErrCode=" + createSnapshot.getErrCode()
                            + ",ErrMsg=" + createSnapshot.getDescription() + ",parentId=" + vvol.getRawId());
                    virtualVolumeService.updateStatusByVvolId(VasaConstant.VVOL_PREFIX + vvolId,
                            VASAArrayUtil.VVOLSTATUS.error_creating);
                    if (createSnapshot.getErrCode() == 1077948994 || createSnapshot.getErrCode() == 1077937859) {
                        throw FaultUtil.snapshotTooMany(
                                "fasrCloneFromSourceVolume/createSnapshot error! ErrCode=" + createSnapshot.getErrCode()
                                        + ",ErrMsg=" + createSnapshot.getDescription() + ",parentId=" + vvol.getRawId());
                    } else {
                        throw FaultUtil.storageFault(
                                "fasrCloneFromSourceVolume/createSnapshot error! ErrCode=" + createSnapshot.getErrCode()
                                        + ",ErrMsg=" + createSnapshot.getDescription() + ",parentId=" + vvol.getRawId());
                    }
                }

                updateVirtualVolume(newVVolId, createSnapshot.getResult().getID(), createSnapshot.getResult().getWWN(),
                        VASAArrayUtil.VVOLSTATUS.creating);
                taskInfo.setTaskState(TaskStateEnum.RUNNING.value());
            }
            taskInfo.setName("fastCloneVirtualVolume");
            taskInfo.setCancelable(false);
            taskInfo.setCancelled(false);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(createDate);
            taskInfo.setStartTime(calendar);
            taskInfo.setArrayId(VASAUtil.getStorageArrayUUID(vvol.getArrayId()));
            taskInfo.setTaskId("fastCloneVirtualVolume:" + VasaConstant.VVOL_PREFIX + newVVolId);
            return taskInfo;
        } catch (SDKException e) {
            LOGGER.error("StorageFault/fastCloneVirtualVolume error. errCode:" + e.getSdkErrCode() + ", description:"
                    + e.getMessage());
            throw FaultUtil.storageFault(
                    "fastCloneVirtualVolume error. errCode:" + e.getSdkErrCode() + ", description:" + e.getMessage());
        } catch (ParseException e) {
            LOGGER.error("StorageFault/fastCloneVirtualVolume parse time error. time is : " + creationTime);
            throw FaultUtil.storageFault("fastCloneVirtualVolume parse time error. time is : " + creationTime);
        } catch (Exception e) {
            if (e instanceof SnapshotTooMany) {
                throw e;
            } else {
                LOGGER.error("StorageFault/fastCloneVirtualVolume error.", e);
                throw FaultUtil.storageFault("fastCloneVirtualVolume parse time error. time is : " + creationTime);
            }
        }
    }

    public VirtualVolumeUnsharedChunksResult unsharedChunksVirtualVolume(NVirtualVolume vvol,
                                                                         long segmentStartOffsetBytes, long segmentLengthBytes)
            throws StorageFault, IncompatibleVolume, NotImplemented, InvalidArgument {
        return unsharedChunksVirtualVolume(vvol, null, segmentStartOffsetBytes, segmentLengthBytes);
    }

    public VirtualVolumeUnsharedChunksResult unsharedChunksVirtualVolume(NVirtualVolume vvol, NVirtualVolume baseVvol,
                                                                         long segmentStartOffsetBytes, long segmentLengthBytes)
            throws StorageFault, IncompatibleVolume, NotImplemented, InvalidArgument {
        try {
//		    if(isNasVvol(vvol.getVvolid()))
//            {
//                throw new NotImplemented("NotSupported for nas.");
//            }
            SDKResult<S2DBitmap> result = null;
            result = vvolModel.getUnsharedChunks(vvol.getArrayId(), vvol.getRawId(),
                    baseVvol == null ? null : baseVvol.getRawId(), segmentStartOffsetBytes, segmentLengthBytes);

            if (0 != result.getErrCode()) {
                if (ArrayErrCodeEnum.RETURN_SNAP_OBJECTS_NOT_IN_SNAPSHOT_RALATIONSHIP.getValue() == result
                        .getErrCode()) {
                    LOGGER.error("IncompatibleVolume/incompatibleVolume vvol:" + vvol.getVvolid()
                            + (baseVvol == null ? null : (", baseVvol:" + baseVvol.getVvolid())));
                    throw FaultUtil.incompatibleVolume();
                } else if (ArrayErrCodeEnum.RETURN_PARAM_ERROR.getValue() == result.getErrCode()) {
                    LOGGER.error("InvalidArgument/invalid argument");
                    throw FaultUtil.invalidArgument();
                } else {
                    LOGGER.error("StorageFault/getUnsharedChunks error. params[" + vvol.getArrayId() + ","
                            + vvol.getRawId() + "," + (baseVvol == null ? null : baseVvol.getRawId()) + ","
                            + segmentStartOffsetBytes + "," + segmentLengthBytes + "] errCode:" + result.getErrCode()
                            + ", description:" + result.getDescription());
                    throw FaultUtil.storageFault("getUnsharedChunks error. " + ", errCode:" + result.getErrCode()
                            + ", description:" + result.getDescription());
                }
            }

            VirtualVolumeUnsharedChunksResult returnResult = new VirtualVolumeUnsharedChunksResult();
            returnResult.setChunkSizeBytes(result.getResult().getCHUNKSIZEBYTES());
            returnResult.setUnsharedChunks(result.getResult().getUNSHAREDCHUNKS());
            returnResult.setScannedChunks(result.getResult().getSCANNEDCHUNKS());
            return returnResult;
        } catch (SDKException e) {
            LOGGER.error("StorageFault/unsharedChunksVirtualVolume error. errCode:" + e.getSdkErrCode()
                    + ", description:" + e.getMessage());
            throw FaultUtil.storageFault("unsharedChunksVirtualVolume error. errCode:" + e.getSdkErrCode()
                    + ", description:" + e.getMessage());
        }
    }

    public VirtualVolumeBitmapResult unsharedBitmapVirtualVolume(NVirtualVolume vvol, NVirtualVolume baseVvol,
                                                                 long segmentStartOffsetBytes, long segmentLengthBytes, long chunkSizeBytes)
            throws StorageFault, IncompatibleVolume, NotImplemented, InvalidArgument {
        try {
//		    if(isNasVvol(vvol.getVvolid()))
//		    {
//		        throw new NotImplemented("NotSupported for nas.");
//		    }
            SDKResult<S2DBitmap> result = vvolModel.getUnsharedBitmap(vvol.getArrayId(), vvol.getRawId(),
                    baseVvol.getRawId(), segmentStartOffsetBytes, segmentLengthBytes, chunkSizeBytes);
            if (0 != result.getErrCode()) {
                if (ArrayErrCodeEnum.RETURN_SNAP_OBJECTS_NOT_IN_SNAPSHOT_RALATIONSHIP.getValue() == result
                        .getErrCode()) {
                    LOGGER.error("IncompatibleVolume/incompatibleVolume vvol:" + vvol.getVvolid() + ", baseVvol:"
                            + baseVvol.getVvolid());
                    throw FaultUtil.incompatibleVolume(
                            "incompatibleVolume vvol:" + vvol.getVvolid() + ", baseVvol:" + baseVvol.getVvolid());
                } else if (ArrayErrCodeEnum.RETURN_PARAM_ERROR.getValue() == result.getErrCode()) {
                    LOGGER.error("InvalidArgument/invalid argument");
                    throw FaultUtil.invalidArgument();
                } else {
                    LOGGER.error("StorageFault/getUnsharedBitmap error. params[" + vvol.getArrayId() + ","
                            + vvol.getRawId() + "," + baseVvol.getRawId() + "," + segmentStartOffsetBytes + ","
                            + segmentLengthBytes + "," + chunkSizeBytes + "] errCode:" + result.getErrCode()
                            + ", description:" + result.getDescription());
                    throw FaultUtil.storageFault("getUnsharedBitmap error. " + ", errCode:" + result.getErrCode()
                            + ", description:" + result.getDescription());
                }
            }

            String chunkBitmap = result.getResult().getCHUNKBITMAP();
            byte[] bitmapArray = BytesUtils.hexStringToBytes(chunkBitmap);

            VirtualVolumeBitmapResult bitmapResult = new VirtualVolumeBitmapResult();
            bitmapResult.setChunkBitmap(Base64Utils.encode(bitmapArray));

            return bitmapResult;
        } catch (SDKException e) {
            LOGGER.error("StorageFault/unsharedBitmapVirtualVolume error. errCode:" + e.getSdkErrCode()
                    + ", description:" + e.getMessage());
            throw FaultUtil.storageFault("unsharedBitmapVirtualVolume error. errCode:" + e.getSdkErrCode()
                    + ", description:" + e.getMessage());
        }
    }

    public VirtualVolumeBitmapResult allocatedBitmapVirtualVolume(NVirtualVolume vvol, long segmentStartOffsetBytes,
                                                                  long segmentLengthBytes, long chunkSizeBytes) throws NotImplemented, StorageFault {
        try {
            if (isNasVvol(vvol.getVvolid())) {
                throw new NotImplemented("NotSupported for nas.");
            }
            SDKResult<S2DBitmap> result = vvolModel.getAllocatedBitmap(vvol.getArrayId(), vvol.getRawId(),
                    segmentStartOffsetBytes, segmentLengthBytes, chunkSizeBytes);
            if (0 != result.getErrCode()) {
                LOGGER.error(
                        "StorageFault/getAllocatedBitmap error. params[" + vvol.getArrayId() + "," + vvol.getRawId()
                                + "," + segmentStartOffsetBytes + "," + segmentLengthBytes + "," + chunkSizeBytes
                                + "] errCode:" + result.getErrCode() + ", description:" + result.getDescription());
                throw FaultUtil.storageFault("getAllocatedBitmap error. " + ", errCode:" + result.getErrCode()
                        + ", description:" + result.getDescription());
            }

            String chunkBitmap = result.getResult().getCHUNKBITMAP();
            byte[] bitmapArray = BytesUtils.hexStringToBytes(chunkBitmap);

            VirtualVolumeBitmapResult bitmapResult = new VirtualVolumeBitmapResult();
            bitmapResult.setChunkBitmap(Base64Utils.encode(bitmapArray));

            return bitmapResult;
        } catch (SDKException e) {
            LOGGER.error("StorageFault/allocatedBitmapVirtualVolume error. errCode:" + e.getSdkErrCode()
                    + ", description:" + e.getMessage());
            throw FaultUtil.storageFault("allocatedBitmapVirtualVolume error. errCode:" + e.getSdkErrCode()
                    + ", description:" + e.getMessage());
        }
    }

    public TaskInfo copyDiffsToVirtualVolume(NVirtualVolume srcVvol, NVirtualVolume srcBaseVvol, NVirtualVolume dstVvol)
            throws StorageFault, IncompatibleVolume, ResourceInUse, InvalidArgument, NotImplemented, VasaProviderBusy, NotFound {
        try {
            String luncopyDisplayName = VASAUtil.buildDisplayName("luncopy");
            SDKResult<S2DLunCopy> createResult = vvolModel.createLuncopy(srcVvol.getArrayId(), luncopyDisplayName, "",
                    srcVvol.getRawId(), dstVvol.getRawId(), srcBaseVvol.getRawId(), true);
            if (0 != createResult.getErrCode()) {
                LOGGER.error("createLuncopy error. params[" + srcVvol.getArrayId() + "," + luncopyDisplayName + ","
                        + srcVvol.getRawId() + "," + dstVvol.getRawId() + "," + srcBaseVvol.getRawId()
                        + ",true] errCode:" + createResult.getErrCode() + ", description:"
                        + createResult.getDescription());
                VASAUtil.throwCopyDiffException(createResult.getErrCode());
            }

            if (isNasVvol(srcVvol.getVvolid())) {
                throw new NotImplemented("NotSupported for nas.");
            }
            SDKErrorCode startResult = vvolModel.startLuncopy(srcVvol.getArrayId(), createResult.getResult().getID(),
                    true);
            if (0 != startResult.getErrCode()) {
                LOGGER.error("startLuncopy error. params[" + srcVvol.getArrayId() + ","
                        + createResult.getResult().getID() + ",true] errCode:" + startResult.getErrCode()
                        + ", description:" + startResult.getDescription());
                // 从阵列侧删除任务
                SDKErrorCode sdkErrorCode = new VVolModel().deleteLuncopy(srcVvol.getArrayId(),
                        createResult.getResult().getID(), true);
                if (0 != sdkErrorCode.getErrCode()) {
                    LOGGER.error("deleteLuncopy error. params[" + srcVvol.getArrayId() + ","
                            + createResult.getResult().getID() + ",true] errCode:" + sdkErrorCode.getErrCode()
                            + ", description:" + sdkErrorCode.getDescription());
                }
                VASAUtil.throwCopyDiffException(startResult.getErrCode());
            }

            SDKResult<S2DLunCopy> queryResult = vvolModel.getLuncopyById(srcVvol.getArrayId(),
                    createResult.getResult().getID());
            if (0 != queryResult.getErrCode()) {
                LOGGER.error("StorageFault/getLuncopyById error. params[" + srcVvol.getArrayId() + ","
                        + createResult.getResult().getID() + "] errCode:" + queryResult.getErrCode() + ", description:"
                        + queryResult.getDescription());
                throw FaultUtil.storageFault("getLuncopyById error. " + ", errCode:" + queryResult.getErrCode()
                        + ", description:" + queryResult.getDescription());
            }

            int rs = Integer.valueOf(queryResult.getResult().getRUNNINGSTATUS());
            int hs = Integer.valueOf(queryResult.getResult().getHEALTHSTATUS());

            TaskInfo taskInfo = new TaskInfo();

            taskInfo.setName("copyDiffsToVirtualVolume");
            taskInfo.setCancelable(true);
            taskInfo.setCancelled(false);
            taskInfo.setProgressUpdateAvailable(true);

            long startTimeMS = Long.valueOf(queryResult.getResult().getCOPYSTARTTIME()) * 1000;
            Date date = new Date(startTimeMS);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            taskInfo.setStartTime(calendar);

            String taskId = "copyDiffsToVirtualVolume:" + srcVvol.getArrayId() + ":" + createResult.getResult().getID();
            taskInfo.setArrayId(VASAUtil.getStorageArrayUUID(srcVvol.getArrayId()));
            taskInfo.setTaskId(taskId);
            // taskInfo.setResult(vvolId);成功之后再返回

            if (hs != EnumDefine.HEALTH_STATUS_E.NORMAL.getValue()) {
                SDKErrorCode sdkErrorCode = new VVolModel().stopLuncopy(srcVvol.getArrayId(),
                        createResult.getResult().getID(), true);
                if (0 != sdkErrorCode.getErrCode()) {
                    LOGGER.error("stopLuncopy error. params[" + srcVvol.getArrayId() + ","
                            + createResult.getResult().getID() + ",true] errCode:" + sdkErrorCode.getErrCode()
                            + ", description:" + sdkErrorCode.getDescription());
                }
                // 从阵列侧删除任务
                sdkErrorCode = new VVolModel().deleteLuncopy(srcVvol.getArrayId(), createResult.getResult().getID(),
                        true);
                if (0 != sdkErrorCode.getErrCode()) {
                    LOGGER.error("deleteLuncopy error. params[" + srcVvol.getArrayId() + ","
                            + createResult.getResult().getID() + ",true] errCode:" + sdkErrorCode.getErrCode()
                            + ", description:" + sdkErrorCode.getDescription());
                }
                taskInfo.setTaskState(TaskStateEnum.ERROR.value());
                return taskInfo;
            }

            if (rs == EnumDefine.RUNNING_STATUS_E.COPYING.getValue() || rs == EnumDefine.RUNNING_STATUS_E.QUEUING.getValue()) {
                taskInfo.setTaskState(TaskStateEnum.RUNNING.value());
                taskInfo.setProgress(Integer.valueOf(queryResult.getResult().getCOPYPROGRESS()));

                VasaTask task = new CopyDiffsTask(taskInfo);
                LOGGER.debug(task);
            } else if (rs == EnumDefine.RUNNING_STATUS_E.COMPLETED.getValue()) {
                // 从阵列侧删除任务
                SDKErrorCode sdkErrorCode = new VVolModel().deleteLuncopy(srcVvol.getArrayId(),
                        createResult.getResult().getID(), true);
                if (0 != sdkErrorCode.getErrCode()) {
                    LOGGER.error("deleteLuncopy error. params[" + srcVvol.getArrayId() + ","
                            + createResult.getResult().getID() + ",true] errCode:" + sdkErrorCode.getErrCode()
                            + ", description:" + sdkErrorCode.getDescription());
                }

                taskInfo.setTaskState(TaskStateEnum.SUCCESS.value());
                taskInfo.setProgress(100);
            } else {
                // 从阵列侧删除任务
                SDKErrorCode sdkErrorCode = new VVolModel().deleteLuncopy(srcVvol.getArrayId(),
                        createResult.getResult().getID(), true);
                if (0 != sdkErrorCode.getErrCode()) {
                    LOGGER.error("deleteLuncopy error. params[" + srcVvol.getArrayId() + ","
                            + createResult.getResult().getID() + ",true] errCode:" + sdkErrorCode.getErrCode()
                            + ", description:" + sdkErrorCode.getDescription());
                }
                taskInfo.setTaskState(TaskStateEnum.ERROR.value());
            }

            return taskInfo;
        } catch (SDKException e) {
            LOGGER.error("StorageFault/copyDiffsToVirtualVolume error. errCode:" + e.getSdkErrCode() + ", description:"
                    + e.getMessage());
            throw FaultUtil.storageFault(
                    "copyDiffsToVirtualVolume error. errCode:" + e.getSdkErrCode() + ", description:" + e.getMessage());
        }
    }

    public void cancelCopyDiffsTask(String arrayId, String rawId) throws NotCancellable, StorageFault {
        try {
            SDKErrorCode sdkErrorCode = vvolModel.stopLuncopy(arrayId, rawId, true);
            if (0 != sdkErrorCode.getErrCode()) {
                LOGGER.error("NotCancellable/stopLuncopy error. params[" + arrayId + "," + rawId + ",true] errCode:"
                        + sdkErrorCode.getErrCode() + ", description:" + sdkErrorCode.getDescription());
                throw FaultUtil.notCancellable("stopLuncopy error. arrayId:" + arrayId + ", rawId:" + rawId
                        + ", errCode:" + sdkErrorCode.getErrCode() + ", description:" + sdkErrorCode.getDescription());
            }

            sdkErrorCode = vvolModel.deleteLuncopy(arrayId, rawId, true);
            if (0 != sdkErrorCode.getErrCode()) {
                LOGGER.error("NotCancellable/deleteLuncopy error. params[" + arrayId + "," + rawId + ",true] errCode:"
                        + sdkErrorCode.getErrCode() + ", description:" + sdkErrorCode.getDescription());
                throw FaultUtil.notCancellable("deleteLuncopy error. arrayId:" + arrayId + ", rawId:" + rawId
                        + ", errCode:" + sdkErrorCode.getErrCode() + ", description:" + sdkErrorCode.getDescription());
            }

            LOGGER.info("successful in cancelling the task:copyDiffsToVirtualVolume:" + arrayId + ":" + rawId);

            // 取消任务成功后清除缓存的任务
            VasaTask.removeFromList("copyDiffsToVirtualVolume:" + arrayId + ":" + rawId);
        } catch (SDKException e) {
            LOGGER.error(
                    "StorageFault/stopLuncopy error. errCode:" + e.getSdkErrCode() + ", description:" + e.getMessage());
            throw FaultUtil.storageFault(
                    "stopLuncopy error. errCode:" + e.getSdkErrCode() + ", description:" + e.getMessage());
        }
    }

    public void cancelCloneTask(String taskId) throws NotFound, NotCancellable, StorageFault {
        // CloneVvolTask task = (CloneVvolTask)
        // VasaTask.lookupTaskByTaskId(taskId);
        NTaskInfo taskInfoByTaskId = taskInfoService.getTaskInfoByTaskId(taskId);
        if (null == taskInfoByTaskId) {
            LOGGER.error("NotFound/not found task:" + taskId);
            throw FaultUtil.notFound("not found task:" + taskId);
        }

        /*
         * if (!task.isLuncopyAvaliable()) { LOGGER.error(
         * "NotCancellable/could not cancel task:" + taskId); throw
         * FaultUtil.notCancellable("could not cancel task:" + taskId); }
         */
        TaskInfo taskInfo = taskInfoByTaskId.getTaskInfo();
        String arrayId = taskInfoByTaskId.getArrayid().split(":")[1];
        String luncopyId = taskInfoByTaskId.getExtProperties(NTaskInfo.LUN_COPY_ID);
        String dstRawId = taskInfoByTaskId.getExtProperties(NTaskInfo.DET_RAW_ID);

        String vvolId = taskId.split(":")[1];
        try {
            SDKErrorCode sdkErrorCode = vvolModel.stopLuncopy(arrayId, luncopyId, false);
            if (0 != sdkErrorCode.getErrCode()) {
                LOGGER.error(
                        "NotCancellable/stopLuncopy error. params[" + arrayId + "," + luncopyId + ",false] errCode:"
                                + sdkErrorCode.getErrCode() + ", description:" + sdkErrorCode.getDescription());
                throw FaultUtil.notCancellable("stopLuncopy error. arrayId:" + arrayId + ", luncopyId:" + luncopyId
                        + ", errCode:" + sdkErrorCode.getErrCode() + ", description:" + sdkErrorCode.getDescription());
            }

            // 取消任务后删除luncopy
            sdkErrorCode = vvolModel.deleteLuncopy(arrayId, luncopyId, false);
            if (0 != sdkErrorCode.getErrCode()) {
                LOGGER.error(
                        "NotCancellable/deleteLuncopy error. params[" + arrayId + "," + luncopyId + ",false] errCode:"
                                + sdkErrorCode.getErrCode() + ", description:" + sdkErrorCode.getDescription());
                throw FaultUtil.notCancellable("deleteLuncopy error. arrayId:" + arrayId + ", luncopyId:" + luncopyId
                        + ", errCode:" + sdkErrorCode.getErrCode() + ", description:" + sdkErrorCode.getDescription());
            }

            LOGGER.info("successful in cancelling the task:" + taskId);

            // 取消任务成功后清除缓存的任务
            // VasaTask.removeFromList(taskId);

            // 不立即从队列中删除该任务，而是等48小时让其自动清理，或者下次调用getTaskUpdate（如果会调用）时立即删除。
            taskInfo.setCancelled(true);
            taskInfo.setTaskState(TaskStateEnum.SUCCESS.value());
            taskInfoService.updateTaskInfoByTaskId(taskInfo, null);
            if (null != vvolId) {
                // 取消任务后删除目标卷
                deleteVirtualVolume(vvolId, arrayId, dstRawId, false);
                // 主流程不删除卷
                // deleteVirtualVolumeFromDatabase(vvolId);
            }
        } catch (SDKException e) {
            LOGGER.error("NotCancellable/SDKException, task:" + taskId + " not cancellable.");
            throw FaultUtil.notCancellable("SDKException, task:" + taskId + " not cancellable.");
        } catch (VasaProviderBusy e) {
            LOGGER.warn("deleteVirtualVolume error. vvolId:" + vvolId);
        } catch (ResourceInUse e) {
            LOGGER.warn("deleteVirtualVolume error. vvolId:" + vvolId);
        }

    }

    public TaskInfo updateCopyDiffsTask(String taskId) throws StorageFault {
        TaskInfo taskInfo = new TaskInfo();

        VasaTask task = VasaTask.lookupTaskByTaskId(taskId);
        if (null == task) {
            taskInfo.setName(taskId.split(":")[0]);
            taskInfo.setCancelable(true);
            taskInfo.setCancelled(true);
            taskInfo.setTaskId(taskId);
            taskInfo.setProgressUpdateAvailable(true);
            taskInfo.setProgress(100);
            taskInfo.setTaskState(TaskStateEnum.SUCCESS.value());
            return taskInfo;
        }

        String arrayId = taskId.split(":")[1];
        String rawId = taskId.split(":")[2];
        Boolean deleteFlag = false;
        SDKResult<S2DLunCopy> queryResult = null;
        try {
            queryResult = new VVolModel().getLuncopyById(arrayId, rawId);
            if (0 != queryResult.getErrCode()) {
                VasaTask.removeFromList(taskId);// 失败任务从任务队列删除
                LOGGER.error("StorageFault/getLuncopyById error. params[" + arrayId + "," + rawId + "] errCode:"
                        + queryResult.getErrCode() + ", description:" + queryResult.getDescription());
                throw FaultUtil.storageFault("getLuncopyById error. arrayId:" + arrayId + ", rawId:" + rawId
                        + ", errCode:" + queryResult.getErrCode() + ", description:" + queryResult.getDescription());
            }

            taskInfo.setName("copyDiffsToVirtualVolume");
            taskInfo.setCancelable(true);
            taskInfo.setCancelled(false);
            taskInfo.setProgressUpdateAvailable(true);

            long startTimeMS = Long.valueOf(queryResult.getResult().getCOPYSTARTTIME()) * 1000;
            Date date = new Date(startTimeMS);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            taskInfo.setStartTime(calendar);

            taskInfo.setArrayId(VASAUtil.getStorageArrayUUID(arrayId));
            taskInfo.setTaskId(taskId);

            int rs = Integer.valueOf(queryResult.getResult().getRUNNINGSTATUS());
            int hs = Integer.valueOf(queryResult.getResult().getHEALTHSTATUS());

            if (hs != EnumDefine.HEALTH_STATUS_E.NORMAL.getValue()) {
                LOGGER.error("luncopy health status abnormal. arrayId:" + arrayId + ", luncopyId:" + rawId);
                taskInfo.setTaskState(TaskStateEnum.ERROR.value());
                taskInfo.setError("healthStatus:" + hs);

                VasaTask.removeFromList(taskId);// 失败任务从任务队列删除

                // 先停止luncopy
                SDKErrorCode sdkErrorCode = new VVolModel().stopLuncopy(arrayId, rawId, true);
                if (0 != sdkErrorCode.getErrCode()) {
                    LOGGER.error("stopLuncopy error. params[" + arrayId + "," + rawId + ",true] errCode:"
                            + sdkErrorCode.getErrCode() + ", description:" + sdkErrorCode.getDescription());
                }
                // 从阵列侧删除任务
                sdkErrorCode = new VVolModel().deleteLuncopy(arrayId, rawId, true);
                if (0 != sdkErrorCode.getErrCode()) {
                    LOGGER.error("deleteLuncopy error. params[" + arrayId + "," + rawId + ",true] errCode:"
                            + sdkErrorCode.getErrCode() + ", description:" + sdkErrorCode.getDescription());
                }
                return taskInfo;
            }

            if (rs == EnumDefine.RUNNING_STATUS_E.COPYING.getValue() || rs == EnumDefine.RUNNING_STATUS_E.QUEUING.getValue()) {
                taskInfo.setTaskState(TaskStateEnum.RUNNING.value());
                taskInfo.setProgress(Integer.valueOf(queryResult.getResult().getCOPYPROGRESS()));
            } else if (rs == EnumDefine.RUNNING_STATUS_E.COMPLETED.getValue()) {
                LOGGER.info("luncopy running status completed. arrayId:" + arrayId + ", luncopyId:" + rawId);
                taskInfo.setTaskState(TaskStateEnum.SUCCESS.value());
                taskInfo.setProgress(100);
                deleteFlag = true;
            } else {
                LOGGER.error("luncopy running status error. arrayId:" + arrayId + ", luncopyId:" + rawId);
                taskInfo.setTaskState(TaskStateEnum.ERROR.value());
                taskInfo.setError("runningStasus:" + rs);
                deleteFlag = true;
            }

            if (deleteFlag) {
                VasaTask.removeFromList(taskId);// 完成的任务从任务队列删除
                // 从阵列侧删除任务
                SDKErrorCode sdkErrorCode = new VVolModel().deleteLuncopy(arrayId, rawId, true);
                if (0 != sdkErrorCode.getErrCode()) {
                    LOGGER.error("deleteLuncopy error. params[" + arrayId + "," + rawId + ",true] errCode:"
                            + sdkErrorCode.getErrCode() + ", description:" + sdkErrorCode.getDescription());
                }
            }
        } catch (SDKException e) {
            LOGGER.error("StorageFault/getLuncopyById error. " + ", errCode:" + e.getSdkErrCode() + ", message:"
                    + e.getMessage());
            throw FaultUtil.storageFault(
                    "getLuncopyById error. " + ", errCode:" + e.getSdkErrCode() + ", message:" + e.getMessage());
        }

        return taskInfo;
    }

    public TaskInfo updateSnapshotVvolTask(String taskId) throws StorageFault {
        if (taskId.startsWith("prepareToSnapshotVirtualVolume")) {
            return updatePrepareToSnapshotVirtualVolume(taskId);
        } else {
            return updateRevertVirtualVolume(taskId);
        }
    }

    public TaskInfo updateFastCloneVirtualVolume(String taskId) throws StorageFault {
        // TODO Auto-generated method stub
        TaskInfo taskInfo = new TaskInfo();
        String snapshotId = taskId.split(":")[1];
        NVirtualVolume virtualVolumeByVvolId = virtualVolumeService.getVirtualVolumeByVvolId(snapshotId);
        if (null == virtualVolumeByVvolId) {
            LOGGER.error("getSnapshot from DB error. params[" + snapshotId + "]");
            throw FaultUtil.storageFault("getSnapshot from DB error. params[" + snapshotId + "]");
        }
        VASAUtil.saveArrayId(virtualVolumeByVvolId.getArrayId());
        try {
            taskInfo.setName("fastCloneVirtualVolume");
            taskInfo.setCancelable(false);
            taskInfo.setCancelled(false);
            Date date = virtualVolumeByVvolId.getCreationTime();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            taskInfo.setStartTime(calendar);
            taskInfo.setTaskId(taskId);

            SDKResult<SnapshotCreateResBean> querySnapshotInfo = vasaArrayService
                    .querySnapshotInfo(virtualVolumeByVvolId.getRawId());
            if (0 != querySnapshotInfo.getErrCode()) {
                LOGGER.error("StorageFault/getSnapshotById error. params["
                        + snapshotId.substring(snapshotId.indexOf('.') + 1) + "] errCode:"
                        + querySnapshotInfo.getErrCode() + ", description:" + querySnapshotInfo.getDescription());
                throw FaultUtil.storageFault("getSnapshotById error. vvolId:" + snapshotId + ", errCode:"
                        + querySnapshotInfo.getErrCode() + ", description:" + querySnapshotInfo.getDescription());
            }
            String status = querySnapshotInfo.getResult().getRUNNINGSTATUS();
            if (status.equalsIgnoreCase(VASAArrayUtil.SnapStatus.initing)) {
                taskInfo.setTaskState(TaskStateEnum.RUNNING.value());
            } else if (status.equalsIgnoreCase(VASAArrayUtil.SnapStatus.inactive)) {
                virtualVolumeService.updateStatusByVvolId(snapshotId, VASAArrayUtil.VVOLSTATUS.inactive);
                SDKErrorCode activeVvolLunSnapshot = vasaArrayService
                        .activeVvolLunSnapshot(virtualVolumeByVvolId.getRawId());
                if (0 != activeVvolLunSnapshot.getErrCode()) {
                    LOGGER.error("StorageFault/activeVvolLunSnapshot error. params["
                            + snapshotId.substring(snapshotId.indexOf('.') + 1) + "] errCode:"
                            + querySnapshotInfo.getErrCode() + ", description:" + querySnapshotInfo.getDescription());
                    throw FaultUtil.storageFault("activeVvolLunSnapshot error. vvolId:" + snapshotId + ", errCode:"
                            + querySnapshotInfo.getErrCode() + ", description:" + querySnapshotInfo.getDescription());
                }
                taskInfo.setTaskState(TaskStateEnum.RUNNING.value());
            } else if (status.equalsIgnoreCase(VASAArrayUtil.SnapStatus.active)) {
                virtualVolumeService.updateStatusByVvolId(snapshotId, VASAArrayUtil.VVOLSTATUS.available);
                taskInfo.setTaskState(TaskStateEnum.SUCCESS.value());
                taskInfo.setResult(snapshotId);
            } else {
                virtualVolumeService.updateStatusByVvolId(snapshotId, VASAArrayUtil.VVOLSTATUS.error_creating);
                taskInfo.setTaskState(TaskStateEnum.ERROR.value());
            }
        } catch (SDKException e) {
            // TODO Auto-generated catch block
            LOGGER.error("StorageFault/updateFastCloneVirtualVolume error. " + ", errCode:" + e.getSdkErrCode()
                    + ", message:" + e.getMessage());
            throw FaultUtil.storageFault("updateFastCloneVirtualVolume error. " + ", errCode:" + e.getSdkErrCode()
                    + ", message:" + e.getMessage());
        }
        taskInfoService.updateTaskInfoByTaskId(taskInfo, null);
        return taskInfo;
    }

    public TaskInfo updatePrepareToSnapshotVirtualVolume(String taskId) throws StorageFault {
        TaskInfo taskInfo = new TaskInfo();
        try {
            String snapshotId = taskId.split(":")[1];
            NVirtualVolume virtualVolumeByVvolId = virtualVolumeService.getVirtualVolumeByVvolId(snapshotId);
            if (null == virtualVolumeByVvolId) {
                LOGGER.error("getSnapshot from DB error. params[" + snapshotId + "]");
                throw FaultUtil.storageFault("getSnapshot from DB error. params[" + snapshotId + "]");
            }
            VASAUtil.saveArrayId(virtualVolumeByVvolId.getArrayId());
            SDKResult<SnapshotCreateResBean> querySnapshotInfo = vasaArrayService
                    .querySnapshotInfo(virtualVolumeByVvolId.getRawId());
            if (0 != querySnapshotInfo.getErrCode()) {
                LOGGER.error("StorageFault/getSnapshotById error. params["
                        + snapshotId.substring(snapshotId.indexOf('.') + 1) + "] errCode:"
                        + querySnapshotInfo.getErrCode() + ", description:" + querySnapshotInfo.getDescription());
                throw FaultUtil.storageFault("getSnapshotById error. vvolId:" + snapshotId + ", errCode:"
                        + querySnapshotInfo.getErrCode() + ", description:" + querySnapshotInfo.getDescription());
            }

            taskInfo.setName("prepareToSnapshotVirtualVolume");
            taskInfo.setCancelable(false);
            taskInfo.setCancelled(false);

            // creationTime = virtualVolumeByVvolId.getCreationTime();
            Date date = virtualVolumeByVvolId.getCreationTime();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            taskInfo.setStartTime(calendar);

            taskInfo.setTaskId(taskId);

            String status = querySnapshotInfo.getResult().getRUNNINGSTATUS();
            if (status.equalsIgnoreCase(VASAArrayUtil.SnapStatus.initing)) {
                taskInfo.setTaskState(TaskStateEnum.RUNNING.value());
            } else if (status.equalsIgnoreCase(VASAArrayUtil.SnapStatus.inactive)) {
                LOGGER.info("snapshot created successful. snapshotId:" + snapshotId);
                VasaTask.removeFromList(taskId);// 完成的任务从任务队列删除

                taskInfo.setTaskState(TaskStateEnum.SUCCESS.value());

                // 将创建成功的快照在VASA数据库中设置为inactive状态
                virtualVolumeService.updateStatusByVvolId(snapshotId, "inactive");

                VvolControlRunable.getInstance(virtualVolumeService).descDjTask();
                LOGGER.info(
                        "traffic control, In updatePrepareToSnapshotVirtualVolume, update volume status for inactive. snapshotId is: "
                                + snapshotId + " task decrease once. current is: "
                                + VvolControlRunable.getInstance(virtualVolumeService).getDjTask() + "/"
                                + VvolConstant.MAX_SEND_DJ);

                List<String> vvolIds = new ArrayList<String>();
                vvolIds.add(snapshotId);
                List<NVirtualVolume> snapvvols = DiscoverService.getInstance().queryVirtualVolumeFromDataBase(vvolIds);
                if (1 != snapvvols.size()) {
                    LOGGER.warn("not found snapshot in database:" + snapshotId);
                    taskInfoService.updateTaskInfoByTaskId(taskInfo, null);
                    return taskInfo;
                }

                String vvolId = snapvvols.get(0).getParentId();
                vvolIds.clear();
                vvolIds.add(vvolId);
                List<NVirtualVolume> vvols = DiscoverService.getInstance().queryVirtualVolumeFromDataBase(vvolIds);
                if (1 != vvols.size()) {
                    LOGGER.warn("not found vvol in database:" + vvolId);
                    taskInfoService.updateTaskInfoByTaskId(taskInfo, null);
                    return taskInfo;
                }

                List<SpaceStats> spaceStatsResult = spaceStatsForVirtualVolume(vvols);
                if (0 == spaceStatsResult.size()) {
                    LOGGER.warn("spaceStatsForVirtualVolume error.vvolId:" + vvolId);
                    taskInfoService.updateTaskInfoByTaskId(taskInfo, null);
                    return taskInfo;
                }

                SpaceStats spaceStats = spaceStatsResult.get(0);
                VirtualVolumeInfo vvolInfo = queryVirtualVolumeInfo(vvolId);
                PrepareSnapshotResult snapshotResult = new PrepareSnapshotResult();
                snapshotResult.setParentInfo(vvolInfo);
                snapshotResult.setParentStats(spaceStats);
                snapshotResult.setSnapshotId(snapshotId);// 成功之后再返回

                taskInfo.setResult(snapshotResult);
            } else {
                LOGGER.error("snapshot created error. snapshotId:" + snapshotId);
                VasaTask.removeFromList(taskId);// 完成的任务从任务队列删除
                taskInfo.setTaskState(TaskStateEnum.ERROR.value());
                taskInfo.setError(-1);

                // deleteVirtualVolume(snapshotId, true);//任务失败删除数据库中的垃圾数据

                // 创建失败不清除数据，只是在数据库中记录当前创建失败，然后在清理线程去清理
                virtualVolumeService.updateStatusByVvolId(snapshotId, "error_creating");
            }
        } catch (SDKException e) {
            LOGGER.error("StorageFault/getSnapshotById error. " + ", errCode:" + e.getSdkErrCode() + ", message:"
                    + e.getMessage());
            throw FaultUtil.storageFault(
                    "getSnapshotById error. " + ", errCode:" + e.getSdkErrCode() + ", message:" + e.getMessage());
        }

        taskInfoService.updateTaskInfoByTaskId(taskInfo, null);
        return taskInfo;
    }

    public TaskInfo updateRevertVirtualVolume(String taskId) throws StorageFault {
        TaskInfo taskInfo = new TaskInfo();
        try {
            String arrayId = taskId.split(":")[1];
            String rawId = taskId.split(":")[2];
            int attempts = 0;
            SDKResult<S2DPassThroughSnapshot> queryResult = null;
            while (true) {
                attempts++;
                queryResult = new VVolModel().getSnapshotById(arrayId, rawId);
                if (0 != queryResult.getErrCode()) {
                    if (attempts < 3) {
                        Thread.sleep(3000);
                        continue;
                    }
                    LOGGER.error("StorageFault/getSnapshotById error. params[" + arrayId + "," + rawId + "] errCode:"
                            + queryResult.getErrCode() + ", description:" + queryResult.getDescription());
                    throw FaultUtil.storageFault(
                            "getSnapshotById error. arrayId:" + arrayId + ", rawId:" + rawId + ", errCode:"
                                    + queryResult.getErrCode() + ", description:" + queryResult.getDescription());
                }
                break;
            }

            taskInfo.setName("revertVirtualVolume");
            taskInfo.setCancelable(false);
            taskInfo.setCancelled(false);
            taskInfo.setProgressUpdateAvailable(true);
            taskInfo.setProgress(0);

            Long startTimeMS = Long.valueOf(queryResult.getResult().getROLLBACKSTARTTIME()) * 1000;
            Date date = new Date(startTimeMS);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            taskInfo.setStartTime(calendar);

            taskInfo.setArrayId(VASAUtil.getStorageArrayUUID(arrayId));
            taskInfo.setTaskId(taskId);

            int rs = Integer.valueOf(queryResult.getResult().getRUNNINGSTATUS());
            int hs = Integer.valueOf(queryResult.getResult().getHEALTHSTATUS());

            if (hs != EnumDefine.HEALTH_STATUS_E.NORMAL.getValue()) {
                LOGGER.error(
                        "snapshot revertding health status abnormal. arrayId:" + arrayId + ", snapshotId:" + rawId);
                taskInfo.setTaskState(TaskStateEnum.ERROR.value());
                taskInfo.setError("healthStatus:" + hs);
                return taskInfo;
            }

            if (rs == EnumDefine.RUNNING_STATUS_E.ROLLBACK.getValue()) {
                taskInfo.setTaskState(TaskStateEnum.RUNNING.value());
                taskInfo.setProgress(Integer.valueOf(queryResult.getResult().getROLLBACKRATE()));
            } else if (rs == EnumDefine.RUNNING_STATUS_E.ACTIVATED.getValue()) {
                LOGGER.info(
                        "snapshot revertding running status activated. arrayId:" + arrayId + ", snapshotId:" + rawId);
                taskInfo.setTaskState(TaskStateEnum.SUCCESS.value());
                taskInfo.setProgress(100);

                VasaTask.removeFromList(taskId);// 完成的任务从任务队列删除
            } else {
                LOGGER.error("snapshot revertding running status error. arrayId:" + arrayId + ", snapshotId:" + rawId);
                taskInfo.setTaskState(TaskStateEnum.ERROR.value());
                taskInfo.setError("runningStatus:" + rs);

                VasaTask.removeFromList(taskId);// 完成的任务从任务队列删除
            }
        } catch (SDKException e) {
            LOGGER.error("StorageFault/getSnapshotById error. " + ", errCode:" + e.getSdkErrCode() + ", message:"
                    + e.getMessage());
            throw FaultUtil.storageFault(
                    "getSnapshotById error. " + ", errCode:" + e.getSdkErrCode() + ", message:" + e.getMessage());
        } catch (InterruptedException e) {
            LOGGER.error("StorageFault/getSnapshotById error. Thread sleep InterruptedException!");
            throw FaultUtil.storageFault("getSnapshotById error. Thread sleep InterruptedException!");
        }

        return taskInfo;
    }

    public TaskInfo updateGeneralVvolTask(String taskId) throws StorageFault, NotFound {
        TaskInfo taskInfo = new TaskInfo();
        String vvolId = taskId.split(":")[1];
        try {
            NVirtualVolume virtualVolumeByVvolId = virtualVolumeService.getVirtualVolumeByVvolId(vvolId);
            if (null == virtualVolumeByVvolId) {
                LOGGER.error("getVirtualVolumeByVvolId from DB error. params[" + vvolId + "]");
                throw FaultUtil.storageFault("getVirtualVolumeByVvolId from DB error. params[" + vvolId + "]");
            }
            VASAUtil.saveArrayId(virtualVolumeByVvolId.getArrayId());
            // SDKResult<S2DVolume> result = new
            // VVolModel().getVolumeById(vvolId.substring(vvolId.indexOf('.') +
            // 1));
            int retryTimes = 0;
            while (retryTimes < 20) {
                SDKResult<LunCreateResBean> queryLunInfo = vasaArrayService.queryLunInfo(virtualVolumeByVvolId.getRawId());
                if (0 != queryLunInfo.getErrCode()) {
                    LOGGER.error("StorageFault/getVolumeById error. params[" + vvolId.substring(vvolId.indexOf('.') + 1)
                            + "] errCode:" + queryLunInfo.getErrCode() + ", description:" + queryLunInfo.getDescription());
                    throw FaultUtil.storageFault("getVolumeById error. vvolId:" + vvolId + ", errCode:"
                            + queryLunInfo.getErrCode() + ", description:" + queryLunInfo.getDescription());
                }
                String status = queryLunInfo.getResult().getRUNNINGSTATUS();
                // taskInfo.setName("createVirtualVolume");
                taskInfo.setName(taskId.split(":")[0]);
                taskInfo.setCancelable(false);
                taskInfo.setCancelled(false);

                // creationTime = result.getResult().getCreated_at();
                Date date = virtualVolumeByVvolId.getCreationTime();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                taskInfo.setStartTime(calendar);

                taskInfo.setTaskId(taskId);

                if (status.equalsIgnoreCase(VASAArrayUtil.RUNNINGSTATUS.initing)) {
                    taskInfo.setTaskState(TaskStateEnum.RUNNING.value());
                    break;
                } else if (status.equalsIgnoreCase(VASAArrayUtil.RUNNINGSTATUS.online)) {
                    LOGGER.info("volume status available. vvolid:" + vvolId);
                    taskInfo.setTaskState(TaskStateEnum.SUCCESS.value());

                    // 将VASA数据库中卷的状态设置为success状态
                    virtualVolumeService.updateStatusByVvolId(vvolId, VASAArrayUtil.VVOLSTATUS.available);
                    VvolControlRunable.getInstance(virtualVolumeService).descDjTask();
                    LOGGER.info("traffic control, In updateGeneralVvolTask,update volume status for available. vvolId is: "
                            + vvolId + " task decrease once. current is: "
                            + VvolControlRunable.getInstance(virtualVolumeService).getDjTask() + "/"
                            + VvolConstant.MAX_SEND_DJ);
                    if (!taskId.startsWith("resizeVirtualVolume")) {
                        NTaskInfo taskInfoByTaskId = taskInfoService.getTaskInfoByTaskId(taskId);
                        VASAUtil.setPolicyNoQos(
                                Boolean.valueOf(taskInfoByTaskId.getExtProperties(NTaskInfo.STORAGE_NO_QOS)));
                        String storageProfileS = taskInfoByTaskId.getExtProperties(NTaskInfo.STORAGE_PROFILE);
                        StorageQosCreateBean storageProfile = JsonUtil.parseJson2Bean(storageProfileS,
                                StorageQosCreateBean.class);
                        vasaArrayService.createStorageProfile(storageProfile.getStoragePolicy(),
                                storageProfile.getProfileId(), storageProfile.getUniProfileId(),
                                storageProfile.getProfileName(), virtualVolumeByVvolId.getRawId(),
                                storageProfile.getGenerationId(), vvolId, virtualVolumeByVvolId.getVvolType());
                        taskInfo.setResult(vvolId);
                        break;
                    } else {
                        long newSize = 0;
                        newSize = Long.valueOf(queryLunInfo.getResult().getCAPACITY()) * MagicNumber.LONG512
                                / MagicNumber.LONG1024 / MagicNumber.LONG1024;

                        NStorageProfile nStorageProfile = storageProfileService.getCurrentProfileByVvolid(vvolId);
                        if (null != nStorageProfile.getControlType()
                                && nStorageProfile.getControlType().equals(NStorageProfile.ControlType.capability_control)
                                && StringUtils.isNotEmpty(nStorageProfile.getControlTypeId())) {
                            try {
                                if (StringUtils.isNotEmpty(nStorageProfile.getSmartQosId())) {
                                    // del old qos
                                    NStorageQos storageQosByQosId = storageQosService
                                            .getStorageQosByQosId(nStorageProfile.getSmartQosId());
                                    delOldQos(storageQosByQosId.getRawQosId(), virtualVolumeByVvolId);
                                    // create new qos
                                    StoragePolicy policy = new StoragePolicy();
                                    NStorageCapabilityQos storageCapabilityQos = storageCapabilityQosService
                                            .getStorageCapabilityQosById(nStorageProfile.getControlTypeId());
                                    StoragePolicy.CreateStoragePolicy(storageCapabilityQos, nStorageProfile, policy);
                                    VASAUtil.reCalculateRealQos(policy, newSize);
                                    storageProfileService.delete(nStorageProfile);
                                    vasaArrayService.createStorageProfile(policy, nStorageProfile.getPolicyId(),
                                            nStorageProfile.getProfileId(), nStorageProfile.getProfileName(),
                                            virtualVolumeByVvolId.getRawId(), nStorageProfile.getGenerationId(), vvolId,
                                            virtualVolumeByVvolId.getVvolType());
                                }
                            } catch (SDKException e) {
                                // TODO Auto-generated catch block
                                LOGGER.error("StorageFault/delOldQos sdkexception. errCode:" + e.getSdkErrCode());
                                throw FaultUtil.storageFault("resizeVolume sdkexception. errCode:" + e.getSdkErrCode());
                            }
                        }
                        // 扩容vvol成功后将新的size写入数据库
                        virtualVolumeService.updateSizeByVvolId(vvolId, newSize);
                        break;
                    }
                } else if (status.equalsIgnoreCase(VASAArrayUtil.RUNNINGSTATUS.offline)) {
                    LOGGER.error("volume status error. vvolid:" + vvolId);
                    VasaTask.removeFromList(taskId);// 完成的任务从任务队列删除
                    taskInfo.setTaskState(TaskStateEnum.ERROR.value());
                    taskInfo.setError(-1);
                    // 创建失败不清除数据，只是在数据库中记录当前创建失败，然后在清理线程去清理
                    LOGGER.info("In updateGeneralVvolTask,update volume status for error_creating. vvolId is: " + vvolId);
                    virtualVolumeService.updateStatusByVvolId(vvolId, VASAArrayUtil.VVOLSTATUS.error_creating);
                    break;
                } else {
                    retryTimes++;
                    Thread.sleep(1000);
                    LOGGER.info("In updateGeneralVvolTask,query lun info fail. vvolId is: " + vvolId + " operation increase once. current is: " + retryTimes + "/20");
                    if (20 == retryTimes) {
                        VasaTask.removeFromList(taskId);// 完成的任务从任务队列删除
                        taskInfo.setTaskState(TaskStateEnum.ERROR.value());
                        taskInfo.setError(-1);
                        LOGGER.info("In updateGeneralVvolTask,after 20 retries,update volume status for error_creating. vvolId is: " + vvolId);
                        virtualVolumeService.updateStatusByVvolId(vvolId, VASAArrayUtil.VVOLSTATUS.error_creating);
                    }
                }
            }
        } catch (SDKException e) {
            LOGGER.error("StorageFault/getVolumeById error. vvolId:" + vvolId + ", errCode:" + e.getSdkErrCode()
                    + ", message:" + e.getMessage());
            throw FaultUtil.storageFault("getVolumeById error. vvolId:" + vvolId + ", errCode:" + e.getSdkErrCode()
                    + ", message:" + e.getMessage());
        } catch (InterruptedException e) {
            LOGGER.error("StorageFault/getVolumeById error. Thread sleep InterruptedException!");
            throw FaultUtil.storageFault("getVolumeById error. Thread sleep InterruptedException!");
        }
        taskInfoService.updateTaskInfoByTaskId(taskInfo, null);
        return taskInfo;
    }

    public TaskInfo updateCloneVvolTask(String taskId) throws StorageFault, NotFound, VasaProviderBusy {
        TaskInfo taskInfo = new TaskInfo();
        NTaskInfo taskInfoByTaskId = taskInfoService.getTaskInfoByTaskId(taskId);
        if (null == taskInfoByTaskId) {
            // 如果检视不到该任务，说明针对该任务的getTaskUpdate一定被重入过（上一次还未返回下一次就进入了），且在被卡住的getTaskUpdate里面把该任务清除了
            // 此处不必清理残留，vmware或后台清理程序会自动清理残留。
            LOGGER.error("NotFound/not found task:" + taskId);
            throw FaultUtil.notFound("not found task:" + taskId);
        }
        TaskInfo dbTaskInfo = taskInfoByTaskId.getTaskInfo();
        // 该任务已经被取消
        if (dbTaskInfo.isCancelled()) {
            return cancelCloneTask(taskId, taskInfo);
        }

        taskInfo.setName("cloneVirtualVolume");
        taskInfo.setCancelable(true);
        taskInfo.setCancelled(false);
        taskInfo.setProgressUpdateAvailable(true);
        taskInfo.setStartTime(dbTaskInfo.getStartTime());
        taskInfo.setTaskId(taskId);
        taskInfo.setArrayId(dbTaskInfo.getArrayId());

        String[] paras = taskId.split(":");
        String vvolId = paras[1];
        Map<String, String> extMap = new HashMap<>();
        String parentId = "NA";
        NVirtualVolume virtualVolumeByVvolId = virtualVolumeService.getVirtualVolumeByVvolId(vvolId);
        if (null == virtualVolumeByVvolId) {
            LOGGER.error("getVirtualVolumeByVvolId from DB error. params[" + vvolId + "]");
            throw FaultUtil.storageFault("getVirtualVolumeByVvolId from DB error. params[" + vvolId + "]");
        }
        //多租户这里应该用原始卷（文件系统）所在的阵列ID nd_todo
        VASAUtil.saveArrayId(virtualVolumeByVvolId.getArrayId());
        try {
            if (paras.length != 2) {
                //nas
                LOGGER.info("update migrate[clone] task.");
                try {
                    QueryMigrateResult ret = vasaNasArrayService.updateMigrateTask(paras, virtualVolumeByVvolId);

                    if (ret.result.equals(QueryMigrateResult.MIGRATE_SUCC)) {
                        taskInfo.setResult(taskId.split(":")[1]);
                        taskInfo.setProgress(100);
                        taskInfo.setTaskState(TaskStateEnum.SUCCESS.value());
                        virtualVolumeService.updateStatusByVvolId(taskId.split(":")[1],
                                VASAArrayUtil.VVOLSTATUS.available);
                        parentId = paras[7].replaceAll(VASAUtil.getVvolRearByVvolType(virtualVolumeByVvolId.getVvolType()), "");
                    } else if (ret.result.equals(QueryMigrateResult.MIGRATE_FAIL)) {
                        virtualVolumeService.updateStatusByVvolId(vvolId, VASAArrayUtil.VVOLSTATUS.error_creating);
                        taskInfo.setTaskState(TaskStateEnum.ERROR.value());
                        taskInfo.setError(-1);
                        vasaNasArrayService.clearMigrateDate(paras);
                    } else if (ret.result.equals(QueryMigrateResult.MIGRATE_RUNNING)) {
                        taskInfo.setProgress(50);
                        taskInfo.setTaskState(TaskStateEnum.RUNNING.value());
                    } else {
                        virtualVolumeService.updateStatusByVvolId(vvolId, VASAArrayUtil.VVOLSTATUS.error_creating);
                        taskInfo.setTaskState(TaskStateEnum.ERROR.value());
                        taskInfo.setError(-1);
                        vasaNasArrayService.clearMigrateDate(paras);
                    }
                    //return taskInfo;
                } catch (Exception e) {
                    vasaNasArrayService.clearMigrateDate(paras);
                    LOGGER.error(e);
                    throw e;
                }

            } else {
                String luncopyId = taskInfoByTaskId.getExtProperties(NTaskInfo.LUN_COPY_ID);

                if (null == luncopyId) {
                    int retryTimes = 0;
                    while (retryTimes < 20) {
                        SDKResult<LunCreateResBean> queryLunInfo = vasaArrayService
                                .queryLunInfo(virtualVolumeByVvolId.getRawId());
                        if (0 != queryLunInfo.getErrCode()) {
                            LOGGER.error("StorageFault/getVolumeById error. params[" + vvolId.substring(vvolId.indexOf('.') + 1)
                                    + "] errCode:" + queryLunInfo.getErrCode() + ", description:"
                                    + queryLunInfo.getDescription());
                            throw FaultUtil.storageFault("getVolumeById error. vvolId:" + vvolId + ", errCode:"
                                    + queryLunInfo.getErrCode() + ", description:" + queryLunInfo.getDescription());
                        }
                        String status = queryLunInfo.getResult().getRUNNINGSTATUS();
                        if (status.equalsIgnoreCase(VASAArrayUtil.RUNNINGSTATUS.initing)) {
                            taskInfo.setProgress(dbTaskInfo.getProgress());
                            taskInfo.setTaskState(TaskStateEnum.RUNNING.value());
                            break;
                        } else if (status.equalsIgnoreCase(VASAArrayUtil.RUNNINGSTATUS.online)) {
                            try {

                                String storageProfileS = taskInfoByTaskId.getExtProperties(NTaskInfo.STORAGE_PROFILE);
                                VASAUtil.setPolicyNoQos(
                                        Boolean.valueOf(taskInfoByTaskId.getExtProperties(NTaskInfo.STORAGE_NO_QOS)));
                                StorageQosCreateBean storageProfile = JsonUtil.parseJson2Bean(storageProfileS,
                                        StorageQosCreateBean.class);
                                vasaArrayService.createStorageProfile(storageProfile.getStoragePolicy(),
                                        storageProfile.getProfileId(), storageProfile.getUniProfileId(),
                                        storageProfile.getProfileName(), virtualVolumeByVvolId.getRawId(),
                                        storageProfile.getGenerationId(), vvolId, virtualVolumeByVvolId.getVvolType());
                                String srcRawId = taskInfoByTaskId.getExtProperties(NTaskInfo.SRC_RAW_ID);
                                LOGGER.info("create vvolLunId = " + virtualVolumeByVvolId.getRawId()
                                        + "status = available,being create lun copy.");
                                String lunCopyId = vasaArrayService.createLuncopyAndStart(VASAUtil.getArrayId(), "", srcRawId,
                                        virtualVolumeByVvolId.getLunId());
                                LOGGER.info(
                                        "create lunCopyId=" + luncopyId + ",destRawId = " + virtualVolumeByVvolId.getRawId());
                                extMap.put(NTaskInfo.SRC_RAW_ID, srcRawId);
                                extMap.put(NTaskInfo.LUN_COPY_ID, lunCopyId);
                                virtualVolumeService.updateStatusByVvolId(taskId.split(":")[1],
                                        VASAArrayUtil.VVOLSTATUS.available);
                                taskInfo.setProgress(dbTaskInfo.getProgress() + 20);
                                taskInfo.setTaskState(TaskStateEnum.RUNNING.value());
                                break;
                            } catch (StorageFault storageFault) {
                                taskInfo.setTaskState(TaskStateEnum.ERROR.value());
                                break;
                            }
                        } else if (status.equalsIgnoreCase(VASAArrayUtil.RUNNINGSTATUS.offline)) {
                            VasaTask.removeFromList(taskId);// 完成的任务从任务队列删除
                            taskInfo.setTaskState(TaskStateEnum.ERROR.value());
                            taskInfo.setError(-1);
                            // 创建失败不清除数据，只是在数据库中记录当前创建失败，然后在清理线程去清理
                            LOGGER.info("In updateCloneVvolTask,update volume status for error_creating. vvolId is: " + vvolId);
                            virtualVolumeService.updateStatusByVvolId(vvolId, VASAArrayUtil.VVOLSTATUS.error_creating);
                            break;
                        } else {
                            retryTimes++;
                            Thread.sleep(1000);
                            LOGGER.info("In updateCloneVvolTask,query lun info fail. vvolId is: " + vvolId + " operation increase once. current is: " + retryTimes + "/20");
                            if (20 == retryTimes) {
                                VasaTask.removeFromList(taskId);// 完成的任务从任务队列删除
                                taskInfo.setTaskState(TaskStateEnum.ERROR.value());
                                taskInfo.setError(-1);
                                LOGGER.info("In updateCloneVvolTask,after 20 retries,update volume status for error_creating. vvolId is: " + vvolId);
                                virtualVolumeService.updateStatusByVvolId(vvolId, VASAArrayUtil.VVOLSTATUS.error_creating);
                            }
                        }
                    }
                } else {
                    String luncopyStatus = updateLuncopyStatus(taskInfo, luncopyId);
                    if (luncopyStatus.equalsIgnoreCase(TaskStateEnum.RUNNING.value())) {
                        taskInfo.setProgress(dbTaskInfo.getProgress());
                        taskInfo.setTaskState(TaskStateEnum.RUNNING.value());
                    } else if (luncopyStatus.equalsIgnoreCase(TaskStateEnum.ERROR.value())) {
                        taskInfo.setError(-1);
                        taskInfo.setTaskState(TaskStateEnum.ERROR.value());
                    } else {
                        taskInfo.setResult(taskId.split(":")[1]);
                        taskInfo.setProgress(100);
                        taskInfo.setTaskState(TaskStateEnum.SUCCESS.value());
                        // luncopy成功设置目标卷为available状态
                    }
                }
            }
            if (taskInfo.getTaskState().equals(TaskStateEnum.SUCCESS.value())) {
                LOGGER.info("Clone volume success, vvolid is : " + taskId.split(":")[1]
                        + " set parentId : NA and status available.");
                virtualVolumeService.updateParentIdByVvolId(taskId.split(":")[1], parentId);
                virtualVolumeService.updateStatusByVvolId(vvolId, VASAArrayUtil.VVOLSTATUS.available);
            } else if (taskInfo.getTaskState().equals(TaskStateEnum.ERROR.value())) {
                LOGGER.info("Clone volume error, vvolid is : " + taskId.split(":")[1]
                        + " set parentId : NA AND status=deleting");
                virtualVolumeService.updateStatusByVvolId(vvolId, VASAArrayUtil.VVOLSTATUS.deleting);
                virtualVolumeService.updateParentIdByVvolId(taskId.split(":")[1], parentId);
            }
            taskInfoService.updateTaskInfoByTaskId(taskInfo, extMap);
            return taskInfo;
        } catch (SDKException e) {
            LOGGER.error("StorageFault/getVolumeById error. vvolId:" + vvolId + ", errCode:" + e.getSdkErrCode()
                    + ", message:" + e.getMessage());
            throw FaultUtil.storageFault("getVolumeById error. vvolId:" + vvolId + ", errCode:" + e.getSdkErrCode()
                    + ", message:" + e.getMessage());
        } catch (InterruptedException e) {
            LOGGER.error("StorageFault/getVolumeById error. Thread sleep InterruptedException!");
            throw FaultUtil.storageFault("getVolumeById error. Thread sleep InterruptedException!");
        }
    }

    private TaskInfo cancelCloneTask(String taskId, TaskInfo taskInfo) {
        taskInfo.setName("cloneVirtualVolume");
        taskInfo.setCancelable(true);
        taskInfo.setCancelled(true);
        taskInfo.setTaskId(taskId);
        taskInfo.setProgressUpdateAvailable(true);
        taskInfo.setProgress(100);
        taskInfo.setTaskState(TaskStateEnum.SUCCESS.value());
        VasaTask.removeFromList(taskId);// 将该任务从队列中清除
        return taskInfo;
    }

    private String updateLuncopyStatus(TaskInfo taskInfo, String luncopyId) throws StorageFault, VasaProviderBusy {
        String arrayId = taskInfo.getArrayId().split(":")[1];
        SDKResult<S2DLunCopy> queryResult = null;
        try {
            queryResult = new VVolModel().getLuncopyById(arrayId, luncopyId);
            if (0 != queryResult.getErrCode()) {
                /**
                 * 问题单号：DTS2016051911617 时间2016.05.20 Start 【eSDK Storage
                 * V100R005C60B037】克隆vvol的过程中注入源vvol的工作控制器异常复位，克隆终止
                 * 阵列单控复位，DJ切控完成后去阵列查询lun报系统繁忙，对系统繁忙错误码1077949006做重试
                 */
                if (1077949006 == queryResult.getErrCode()) {
                    // 如果阵列报系统繁忙，维持上一次的进度不变，返回任务进行中
                    return TaskStateEnum.RUNNING.value();
                }
                /**
                 * 问题单号：DTS2016051911617 时间2016.05.20 Start
                 */
                LOGGER.error("StorageFault/getLuncopyById error. params[" + arrayId + "," + luncopyId + "] errCode:"
                        + queryResult.getErrCode() + ", description:" + queryResult.getDescription());
                VvolControlRunable.getInstance(virtualVolumeService).descDjTask();
                throw FaultUtil.storageFault("getLuncopyById error. arrayId:" + arrayId + ", luncopyId:" + luncopyId
                        + ", errCode:" + queryResult.getErrCode() + ", description:" + queryResult.getDescription());
            }

            int rs = Integer.valueOf(queryResult.getResult().getRUNNINGSTATUS());
            int hs = Integer.valueOf(queryResult.getResult().getHEALTHSTATUS());

            if (hs != EnumDefine.HEALTH_STATUS_E.NORMAL.getValue()) {
                LOGGER.error("luncopy health status abnormal. arrayId:" + arrayId + ", luncopyId:" + luncopyId);
                // 先停止luncopy
                SDKErrorCode sdkErrorCode = new VVolModel().stopLuncopy(arrayId, luncopyId, false);
                if (0 != sdkErrorCode.getErrCode()) {
                    LOGGER.error("stopLuncopy error. params[" + arrayId + "," + luncopyId + ",false] errCode:"
                            + sdkErrorCode.getErrCode() + ", description:" + sdkErrorCode.getDescription());
                }
                // 从阵列侧删除任务
                sdkErrorCode = new VVolModel().deleteLuncopy(arrayId, luncopyId, false);
                if (0 != sdkErrorCode.getErrCode()) {
                    LOGGER.error("deleteLuncopy error. params[" + arrayId + "," + luncopyId + ",false] errCode:"
                            + sdkErrorCode.getErrCode() + ", description:" + sdkErrorCode.getDescription());
                    VvolControlRunable.getInstance(virtualVolumeService).descDjTask();
                    LOGGER.info("traffic control,fast clone vvol dj tasks increase once."
                            + VvolControlRunable.getInstance(virtualVolumeService).getDjTask() + "/"
                            + VvolConstant.MAX_SEND_DJ);
                    return TaskStateEnum.ERROR.value();
                }
                VvolControlRunable.getInstance(virtualVolumeService).descDjTask();
                return TaskStateEnum.ERROR.value();
            }

            if (rs == EnumDefine.RUNNING_STATUS_E.COPYING.getValue() || rs == EnumDefine.RUNNING_STATUS_E.QUEUING.getValue()) {
                taskInfo.setProgress(Integer.valueOf(queryResult.getResult().getCOPYPROGRESS()));
                return TaskStateEnum.RUNNING.value();
            } else if (rs == EnumDefine.RUNNING_STATUS_E.COMPLETED.getValue()) {
                LOGGER.info("luncopy running status completed. arrayId:" + arrayId + ", luncopyId:" + luncopyId);
                // 从阵列侧删除任务
                SDKErrorCode sdkErrorCode = new VVolModel().deleteLuncopy(arrayId, luncopyId, false);
                if (0 != sdkErrorCode.getErrCode()) {
                    LOGGER.error("deleteLuncopy error. params[" + arrayId + "," + luncopyId + ",false] errCode:"
                            + sdkErrorCode.getErrCode() + ", description:" + sdkErrorCode.getDescription());
                }
                VvolControlRunable.getInstance(virtualVolumeService).descDjTask();
                return TaskStateEnum.SUCCESS.value();
            } else {
                LOGGER.error("luncopy running status error. arrayId:" + arrayId + ", luncopyId:" + luncopyId);
                // 从阵列侧删除任务
                SDKErrorCode sdkErrorCode = new VVolModel().deleteLuncopy(arrayId, luncopyId, false);
                if (0 != sdkErrorCode.getErrCode()) {
                    LOGGER.error("deleteLuncopy error. params[" + arrayId + "," + luncopyId + ",false] errCode:"
                            + sdkErrorCode.getErrCode() + ", description:" + sdkErrorCode.getDescription());
                }
                VvolControlRunable.getInstance(virtualVolumeService).descDjTask();
                return TaskStateEnum.ERROR.value();
            }

        } catch (SDKException e) {
            LOGGER.error("StorageFault/getLuncopyById error. " + ", errCode:" + e.getSdkErrCode() + ", message:"
                    + e.getMessage());
            throw FaultUtil.storageFault(
                    "getLuncopyById error. " + ", errCode:" + e.getSdkErrCode() + ", message:" + e.getMessage());
        }
    }

    public boolean stopLunCopyTaskByLunCopyId(String arrayId, String lunCopyId) throws SDKException {
        SDKErrorCode sdkErrorCode = vvolModel.stopLuncopy(arrayId, lunCopyId, false);
        if (0 != sdkErrorCode.getErrCode()) {
            // If errorCode is 1077950180, the luncopy is already completed.
            if (1077950180 != sdkErrorCode.getErrCode()) {
                LOGGER.error("In VvolControlRunable, stopLuncopy error. params[" + arrayId + "," + lunCopyId
                        + ",false] errCode:" + sdkErrorCode.getErrCode() + ", description:"
                        + sdkErrorCode.getDescription());
                return false;
            }
        }
        return true;
    }

    public boolean deleteLunCopyTaskByLunCopyId(String arrayId, String luncopyId) throws SDKException {
        if (!stopLunCopyTaskByLunCopyId(arrayId, luncopyId)) {
            LOGGER.error("In deleteLunCopyTaskByLunCopyId function, stop luncopyId=" + luncopyId + " fail.");
            return false;
        }
        // 从阵列侧删除luncopy任务
        SDKErrorCode sdkErrorCode = vvolModel.deleteLuncopy(arrayId, luncopyId, false);
        if (0 != sdkErrorCode.getErrCode()) {
            LOGGER.error("In VvolControlRunable, deleteLuncopy error. params[" + arrayId + "," + luncopyId
                    + ",false] errCode:" + sdkErrorCode.getErrCode() + ", description:"
                    + sdkErrorCode.getDescription());
            return false;
        }
        return true;
    }

    public List<String> getLunCopyTaskId(String arrayId, String lunId) throws SDKException {
        List<String> lunCopyTaskIds = new ArrayList<String>();
        String[] lunCopyIdArray = null;
        if (null == arrayId || null == lunId) {
            LOGGER.error("getLunCopyTaskId fail, the arrayId=" + arrayId + " lunId=" + lunId);
            return lunCopyTaskIds;
        }
        // 获取Lun信息，并查看Lun copy ID list.
        SDKResult<S2DLunCopyBean> lunCopyResult = storageModel.getLunCopy(arrayId, lunId);
        if (null == lunCopyResult.getResult()) {
            return lunCopyTaskIds;
        }
        String lunCopyIdStr = lunCopyResult.getResult().getLUNCOPYIDS();
        LOGGER.info("In getLunCopyTaskId, lunId is: " + lunId + " LUNCOPYIDS is: " + lunCopyIdStr);
        if (null == lunCopyIdStr) {
            return lunCopyTaskIds;
        }
        // 2是因为需要去掉首位的中括号
        if (lunCopyIdStr.length() > 2 && lunCopyIdStr.startsWith("[") && lunCopyIdStr.endsWith("]")) {
            lunCopyIdArray = lunCopyIdStr.substring(1, lunCopyIdStr.length() - 1).split(",");
        }
        if (null != lunCopyIdArray && lunCopyIdArray.length > 0) {
            for (String lunCopyId : lunCopyIdArray) {
                // 去除首位的引号
                if (lunCopyId.startsWith("\"") && lunCopyId.endsWith("\"")) {
                    lunCopyId = lunCopyId.substring(1, lunCopyId.length() - 1);
                    // LOGGER.info("In VvolControlRunable, delete luncopy.
                    // lunId: " + lunId + " arrayId: " + arrayId + " lunCopyId:
                    // " + lunCopyId);
                    lunCopyTaskIds.add(lunCopyId);
                }
            }
        }
        LOGGER.info("The lunCopyTaskIds is " + lunCopyTaskIds);
        return lunCopyTaskIds;
    }

    public void notifyLeaderToBroadcastRefresh(String currentNodeIp, String work, String syncid) {
        LOGGER.info("In notifyLeaderToBroadcastRefresh function, the currentNodeIp=" + currentNodeIp + "work= " + work
                + " syncid= " + syncid);
        try {
            if (work.equalsIgnoreCase("syncArrayCert")) {
                NArrayCertificateSync arrayCertificateSync = new NArrayCertificateSync();
                arrayCertificateSync.setCerId(syncid);
                arrayCertificateSync.setSyncIp(currentNodeIp);
                arrayCertificateSync.setSyncTime(DateUtil.getUTCDate());
                arrayCertificateSyncService.addArrayCertificateSync(arrayCertificateSync);
            }

            // notify leader
            String leaderServiceUrl = ConfigManager.getInstance().getValue("om.rest.url");

            List<NUser> omUsers = userManagerService.getUserInfoByUsername("admin");
            if (omUsers == null || omUsers.size() == 0) {
                LOGGER.error("get om user size is 0.");
                throw FaultUtil.storageFault("get om user size is 0.");
            }

            RestRequestMessage requestMessage = new RestRequestMessage();
            Map<String, String> parameters = new HashMap<String, String>();
            parameters.put("serviceIp", currentNodeIp);
            parameters.put("work", work);
            requestMessage.setHttpMethod(RestConstant.HTTP_METHOD_POST);
            requestMessage.setPayload(parameters);

            RestUtilsOfOM restClient = RestUtilsOfOM.getInstance(leaderServiceUrl, omUsers.get(0).getUsername(),
                    omUsers.get(0).getPassword());
            String result = restClient.sendMessage(requestMessage, "/notifySyncCert");
            if (result == null) {
                LOGGER.error("request notifySyncCert not available.");
                throw FaultUtil.storageFault("request notifySyncCert not available.");
            }
            if ("401".equals(result)) {
                LOGGER.error("request notifySyncCert return 401.");
                throw FaultUtil.storageFault("request notifySyncCert return 401.");
            }

            ResponseHeader response = new Gson().fromJson(result, ResponseHeader.class);
            if (response.getResultCode() != 0) {
                LOGGER.error("request notifySyncCert fail . err : " + response.getResultDescription());
                throw FaultUtil.storageFault("request notifySyncCert fail . err : " + response.getResultDescription());
            }
        } catch (Exception e) {
            LOGGER.error("notifyLeaderToBroadcastRefresh fail. ", e);
        }
    }

    public void initVasaServiceCenter() {
        LOGGER.info("In initVasaServiceCenter function");
        try {
            String localIpAddress = IPUtil.getLocalIP();
            List<NVasaServiceCenter> vasaServiceCenterList = vasaServiceCenterService
                    .getVasaServiceByServiceIp(localIpAddress);
            if (vasaServiceCenterList.size() == 0) {
                LOGGER.info("The vasaServiceCenterList size is 0,start save ip :" + localIpAddress
                        + "into vasaServiceCenter.");
                NVasaServiceCenter center = new NVasaServiceCenter();
                center.setServiceIp(localIpAddress);
                center.setServicePort(18543);
                center.setLastModifiedTime(DateUtil.getUTCDate());
                vasaServiceCenterService.addVasaService(center);
            }
        } catch (Exception e) {
            LOGGER.info("initVasaServiceCenter fail. ", e);
        }
    }

    private void updateStoragePoolStatus(List<NStoragePool> storagePools, List<S2DStoragePool> s2dStoragePool) {
        String arrayId = storagePools.get(0).getArrayId();
        List<String> s2dStoragePoolIds = new ArrayList<String>();
        for (S2DStoragePool pool : s2dStoragePool) {
            s2dStoragePoolIds.add(pool.getID());
        }
        for (NStoragePool nStoragePool : storagePools) {
            if ("Lost".equalsIgnoreCase(nStoragePool.getDeviceStatus())) {
                continue;
            }
            if (!s2dStoragePoolIds.contains(nStoragePool.getRawPoolId())) {
                String[] pools = new String[1];
                pools[0] = nStoragePool.getRawPoolId();
                storagePoolService.setStoragePoolsLost(arrayId, nStoragePool.getContainerId(), pools);
            }

        }
    }

    public TaskInfoService getTaskInfoService() {
        return taskInfoService;
    }

    public void setTaskInfoService(TaskInfoService taskInfoService) {
        this.taskInfoService = taskInfoService;
    }

}