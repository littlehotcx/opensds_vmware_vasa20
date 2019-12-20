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

/**
 * Please modify this class to meet your needs
 * This class is not complete
 */

package org.opensds.vasa.vasa;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensds.vasa.common.MagicNumber;
import org.opensds.vasa.domain.model.VVolModel;
import org.opensds.vasa.domain.model.bean.DArray;
import org.opensds.vasa.domain.model.bean.DArrayInfo;
import org.opensds.vasa.domain.model.bean.DArrayIsLock;
import org.opensds.vasa.domain.model.bean.DFileSystem;
import org.opensds.vasa.domain.model.bean.DLun;
import org.opensds.vasa.domain.model.bean.DPort;
import org.opensds.vasa.domain.model.bean.DProcessor;
import org.opensds.vasa.domain.model.bean.DStorageCapability;
import org.opensds.vasa.domain.model.bean.S2DStoragePool;
import org.opensds.vasa.vasa.convert.VasaServicePortTypeConvert;
import org.opensds.vasa.vasa.db.model.NStorageContainer;
import org.opensds.vasa.vasa.db.model.NStoragePool;
import org.opensds.vasa.vasa.db.model.StorageInfo;
import org.opensds.vasa.vasa.db.model.VasaProperty;
import org.opensds.vasa.vasa.db.service.StorageContainerService;
import org.opensds.vasa.vasa.db.service.StorageManagerService;
import org.opensds.vasa.vasa.db.service.StoragePoolService;
import org.opensds.vasa.vasa.db.service.VasaPropertyService;
import org.opensds.vasa.vasa.db.service.VirtualVolumeService;
import org.opensds.vasa.vasa.runnable.VvolControlRunable;
import org.opensds.vasa.vasa.service.AlarmService;
import org.opensds.vasa.vasa.service.DiscoverService;
import org.opensds.vasa.vasa.service.DiscoverServiceImpl;
import org.opensds.vasa.vasa.service.EventService;
import org.opensds.vasa.vasa.service.SPBMService;
import org.opensds.vasa.vasa.service.SecureConnectionService;
import org.opensds.vasa.vasa.service.StorageService;
import org.opensds.vasa.vasa.service.VolumeService;
import org.opensds.vasa.vasa.util.DataUtil;
import org.opensds.vasa.vasa.util.DateUtil;
import org.opensds.vasa.vasa.util.FaultUtil;
import org.opensds.vasa.vasa.util.FileManager;
import org.opensds.vasa.vasa.util.JaxbUtil;
import org.opensds.vasa.vasa.util.ListUtil;
import org.opensds.vasa.vasa.util.SSLUtil;
import org.opensds.vasa.vasa.util.SessionContext;
import org.opensds.vasa.vasa.util.Util;
import org.opensds.vasa.vasa.util.VASAUtil;
import org.opensds.vasa.vasa20.device.VASAConnection;
import org.opensds.vasa.vasa20.device.array.db.service.StorageArrayService;
import org.opensds.vasa.vasa20.device.array.login.DeviceSwitchMetaData;
import org.opensds.vasa.vasa20.device.array.login.DeviceSwitchUtil;

import org.opensds.vasa.base.bean.terminal.ResBean;

import org.opensds.platform.common.SDKResult;
import org.opensds.platform.common.config.ConfigManager;
import org.opensds.platform.common.exception.SDKException;
import org.opensds.platform.common.utils.AES128System;
import org.opensds.platform.common.utils.ApplicationContextUtil;
import org.opensds.platform.nemgr.DeviceInstance;
import org.opensds.platform.nemgr.DeviceManager;
import org.opensds.platform.nemgr.itf.IDeviceConnection;

import com.vmware.vim.vasa.v20.ActivateProviderFailed;
import com.vmware.vim.vasa.v20.InactiveProvider;
import com.vmware.vim.vasa.v20.IncompatibleVolume;
import com.vmware.vim.vasa.v20.InvalidArgument;
import com.vmware.vim.vasa.v20.InvalidCertificate;
import com.vmware.vim.vasa.v20.InvalidLogin;
import com.vmware.vim.vasa.v20.InvalidProfile;
import com.vmware.vim.vasa.v20.InvalidSession;
import com.vmware.vim.vasa.v20.InvalidStatisticsContext;
import com.vmware.vim.vasa.v20.LostAlarm;
import com.vmware.vim.vasa.v20.LostEvent;
import com.vmware.vim.vasa.v20.NotCancellable;
import com.vmware.vim.vasa.v20.NotFound;
import com.vmware.vim.vasa.v20.NotImplemented;
import com.vmware.vim.vasa.v20.NotSupported;
import com.vmware.vim.vasa.v20.OutOfResource;
import com.vmware.vim.vasa.v20.PermissionDenied;
import com.vmware.vim.vasa.v20.ResourceInUse;
import com.vmware.vim.vasa.v20.SnapshotTooMany;
import com.vmware.vim.vasa.v20.StorageFault;
import com.vmware.vim.vasa.v20.Timeout;
import com.vmware.vim.vasa.v20.TooMany;
import com.vmware.vim.vasa.v20.VasaProviderBusy;
import com.vmware.vim.vasa.v20.VasaServicePortType;
import com.vmware.vim.vasa.v20.data.policy.capability.provider.xsd.CapabilitySchema;
import com.vmware.vim.vasa.v20.data.policy.capability.xsd.CapabilityInstance;
import com.vmware.vim.vasa.v20.data.policy.compliance.xsd.ComplianceResult;
import com.vmware.vim.vasa.v20.data.policy.compliance.xsd.ComplianceSubject;
import com.vmware.vim.vasa.v20.data.policy.profile.xsd.StorageProfile;
import com.vmware.vim.vasa.v20.data.policy.profile.xsd.SubProfile;
import com.vmware.vim.vasa.v20.data.policy.xsd.ResourceAssociation;
import com.vmware.vim.vasa.v20.data.vvol.xsd.BatchReturnStatus;
import com.vmware.vim.vasa.v20.data.vvol.xsd.BatchVirtualVolumeHandleResult;
import com.vmware.vim.vasa.v20.data.vvol.xsd.ContainerSpaceStats;
import com.vmware.vim.vasa.v20.data.vvol.xsd.ProtocolEndpoint;
import com.vmware.vim.vasa.v20.data.vvol.xsd.SpaceStats;
import com.vmware.vim.vasa.v20.data.vvol.xsd.TaskInfo;
import com.vmware.vim.vasa.v20.data.vvol.xsd.TaskStateEnum;
import com.vmware.vim.vasa.v20.data.vvol.xsd.VirtualVolumeBitmapResult;
import com.vmware.vim.vasa.v20.data.vvol.xsd.VirtualVolumeInfo;
import com.vmware.vim.vasa.v20.data.vvol.xsd.VirtualVolumeUnsharedChunksResult;
import com.vmware.vim.vasa.v20.data.xsd.BaseStorageEntity;
import com.vmware.vim.vasa.v20.data.xsd.StorageAlarm;
import com.vmware.vim.vasa.v20.data.xsd.StorageArray;
import com.vmware.vim.vasa.v20.data.xsd.StorageContainer;
import com.vmware.vim.vasa.v20.data.xsd.StorageEvent;
import com.vmware.vim.vasa.v20.data.xsd.StorageLun;
import com.vmware.vim.vasa.v20.data.xsd.UsageContext;
import com.vmware.vim.vasa.v20.data.xsd.VasaAssociationObject;
import com.vmware.vim.vasa.v20.data.xsd.VasaProviderInfo;
import com.vmware.vim.vasa.v20.xsd.ActivateProviderExResponse;
import com.vmware.vim.vasa.v20.xsd.AllocatedBitmapVirtualVolumeResponse;
import com.vmware.vim.vasa.v20.xsd.BindVirtualVolumeResponse;
import com.vmware.vim.vasa.v20.xsd.CloneVirtualVolumeResponse;
import com.vmware.vim.vasa.v20.xsd.CopyDiffsToVirtualVolumeResponse;
import com.vmware.vim.vasa.v20.xsd.CreateVirtualVolumeResponse;
import com.vmware.vim.vasa.v20.xsd.FastCloneVirtualVolumeResponse;
import com.vmware.vim.vasa.v20.xsd.GetAlarmsResponse;
import com.vmware.vim.vasa.v20.xsd.GetCurrentTaskResponse;
import com.vmware.vim.vasa.v20.xsd.GetEventsResponse;
import com.vmware.vim.vasa.v20.xsd.GetNumberOfEntitiesResponse;
import com.vmware.vim.vasa.v20.xsd.GetTaskUpdateResponse;
import com.vmware.vim.vasa.v20.xsd.PrepareToSnapshotVirtualVolumeResponse;
import com.vmware.vim.vasa.v20.xsd.QueryArraysResponse;
import com.vmware.vim.vasa.v20.xsd.QueryAssociatedCapabilityForFileSystemResponse;
import com.vmware.vim.vasa.v20.xsd.QueryAssociatedCapabilityForLunResponse;
import com.vmware.vim.vasa.v20.xsd.QueryAssociatedLunsForPortResponse;
import com.vmware.vim.vasa.v20.xsd.QueryAssociatedPortsForProcessorResponse;
import com.vmware.vim.vasa.v20.xsd.QueryAssociatedProcessorsForArrayResponse;
import com.vmware.vim.vasa.v20.xsd.QueryCACertificateRevocationListsResponse;
import com.vmware.vim.vasa.v20.xsd.QueryCACertificatesResponse;
import com.vmware.vim.vasa.v20.xsd.QueryCapabilityMetadataResponse;
import com.vmware.vim.vasa.v20.xsd.QueryCapabilityProfileForResourceResponse;
import com.vmware.vim.vasa.v20.xsd.QueryCapabilityProfileResponse;
import com.vmware.vim.vasa.v20.xsd.QueryCatalogResponse;
import com.vmware.vim.vasa.v20.xsd.QueryComplianceResultResponse;
import com.vmware.vim.vasa.v20.xsd.QueryDRSMigrationCapabilityForPerformanceResponse;
import com.vmware.vim.vasa.v20.xsd.QueryDefaultProfileForStorageContainerResponse;
import com.vmware.vim.vasa.v20.xsd.QueryProtocolEndpointForArrayResponse;
import com.vmware.vim.vasa.v20.xsd.QueryProtocolEndpointResponse;
import com.vmware.vim.vasa.v20.xsd.QueryStorageCapabilitiesResponse;
import com.vmware.vim.vasa.v20.xsd.QueryStorageContainerForArrayResponse;
import com.vmware.vim.vasa.v20.xsd.QueryStorageContainerResponse;
import com.vmware.vim.vasa.v20.xsd.QueryStorageFileSystemsResponse;
import com.vmware.vim.vasa.v20.xsd.QueryStorageLunsResponse;
import com.vmware.vim.vasa.v20.xsd.QueryStoragePortsResponse;
import com.vmware.vim.vasa.v20.xsd.QueryStorageProcessorsResponse;
import com.vmware.vim.vasa.v20.xsd.QueryUniqueIdentifiersForEntityResponse;
import com.vmware.vim.vasa.v20.xsd.QueryUniqueIdentifiersForFileSystemsResponse;
import com.vmware.vim.vasa.v20.xsd.QueryUniqueIdentifiersForLunsResponse;
import com.vmware.vim.vasa.v20.xsd.QueryVirtualVolumeInfoResponse;
import com.vmware.vim.vasa.v20.xsd.QueryVirtualVolumeResponse;
import com.vmware.vim.vasa.v20.xsd.RegisterCACertificatesAndCRLsResponse;
import com.vmware.vim.vasa.v20.xsd.RegisterVASACertificateResponse;
import com.vmware.vim.vasa.v20.xsd.ResizeVirtualVolumeResponse;
import com.vmware.vim.vasa.v20.xsd.RevertVirtualVolumeResponse;
import com.vmware.vim.vasa.v20.xsd.SetContextResponse;
import com.vmware.vim.vasa.v20.xsd.SnapshotVirtualVolumeResponse;
import com.vmware.vim.vasa.v20.xsd.SpaceStatsForStorageContainerResponse;
import com.vmware.vim.vasa.v20.xsd.SpaceStatsForVirtualVolumeResponse;
import com.vmware.vim.vasa.v20.xsd.UnbindVirtualVolumeFromAllHostResponse;
import com.vmware.vim.vasa.v20.xsd.UnbindVirtualVolumeResponse;
import com.vmware.vim.vasa.v20.xsd.UnsharedBitmapVirtualVolumeResponse;
import com.vmware.vim.vasa.v20.xsd.UnsharedChunksVirtualVolumeResponse;
import com.vmware.vim.vasa.v20.xsd.UpdateStorageProfileForVirtualVolumeResponse;

/**
 * This class was generated by Apache CXF 2.6.9 2014-06-16T15:35:44.936+08:00
 * Generated source version: 2.6.9
 */
// @WebService(targetNamespace = "http://com.vmware.vim.vasa/2.0/", name =
// "vasaServicePortType")
// @javax.jws.WebService(
// serviceName = "vasaService",
// portName = "vasaServiceHttpSoap12Endpoint",
// targetNamespace = "http://com.vmware.vim.vasa/2.0/",
// endpointInterface = "com.vmware.vim.vasa.v20.VasaServicePortType")
// @SOAPBinding(parameterStyle = ParameterStyle.BARE)
// @BindingType(value = javax.xml.ws.soap.SOAPBinding.SOAP12HTTP_BINDING)
public class VasaServicePortTypeImpl implements VasaServicePortType {

    private static Logger LOGGER = LogManager.getLogger(VasaServicePortTypeImpl.class);

    private static final String FILE_SEPARATOR = System.getProperty("file.separator");

    private String trustStoreFileName = FILE_SEPARATOR + "conf" + FILE_SEPARATOR + "jssecacerts";

    private static final String RETAIN_VP_CERTIFICATE_FLAG_CONF = FileManager.getBasePath() + "webcontent/conf"
            + File.separator + "retain_vp_certificate_flag.conf";

    /**
     * 公共属性VASA_SESSIONID_STR
     */
    public static final String VASA_SESSIONID_STR = "VASASESSIONID";

    /**
     * 未激活的快照保留时间，单位为秒
     */
    // private static final long INACTIVE_TIME = 3000L;

    private StorageService storageService;

    // private FusionStorageInfoImpl fusionStorageInfoImpl = FusionStorageInfoImpl.getInstance();

    public StorageService getStorageService() {
        return storageService;
    }

    public void setStorageService(StorageService storageService) {
        this.storageService = storageService;
    }

    private SPBMService sPBMService;

    private VolumeService volumeService;

    private EventService eventService;

    private AlarmService alarmService;

    private SecureConnectionService secureConnectionService;

    private DiscoverService discoverService;

    private DiscoverServiceImpl discoverServiceImpl;

    private VirtualVolumeService virtualVolumeService = (VirtualVolumeService) ApplicationContextUtil
            .getBean("virtualVolumeService");

    private StorageManagerService storageManagerService = ApplicationContextUtil.getBean("storageManagerService");
    private StorageArrayService storageArrayService = ApplicationContextUtil.getBean("storageArrayService");
    private StorageContainerService storageContainerService = ApplicationContextUtil.getBean("storageContainerService");
    private StoragePoolService storagePoolService = ApplicationContextUtil.getBean("storagePoolService");
    private DeviceManager deviceManager = ApplicationContextUtil.getBean("deviceManager");
    private VasaPropertyService vasaPropertyService = ApplicationContextUtil.getBean("vasaPropertyService");

    /**
     * 此处有线程安全问题，不再使用
     */
    // @Resource
    // private WebServiceContext wsContext;

    private VVolModel model = new VVolModel();

    private boolean isSupportNFS = false;

    private String trustPwd = null;

    private String trustStoreType = null;

    private SSLUtil sslUtil;

    private DataUtil dataUtil;

    private Timer timer;

    public VirtualVolumeService getVirtualVolumeService() {
        return virtualVolumeService;
    }

    public void setVirtualVolumeService(VirtualVolumeService virtualVolumeService) {
        this.virtualVolumeService = virtualVolumeService;
    }

    public SecureConnectionService getSecureConnectionService() {
        return secureConnectionService;
    }

    public void setSecureConnectionService(SecureConnectionService secureConnectionService) {
        this.secureConnectionService = secureConnectionService;
    }

    public void init() {
        try {
            String trustPassFromXml = ConfigManager.getInstance().getValue("vasa.ssl.truststorePass");
            if (trustPassFromXml != null && !trustPassFromXml.equals("")) {
                trustPwd = AES128System.decryptPwd(trustPassFromXml);
            }
            String trustPassFilePath = ConfigManager.getInstance().getValue("vasa.ssl.truststoreFile");
            if (trustPassFilePath != null && !trustPassFilePath.equals("")) {
                trustStoreFileName = trustPassFilePath;
            }

            String path = trustStoreFileName;

            trustStoreType = ConfigManager.getInstance().getValue("vasa.ssl.truststoreType");
            if (trustStoreType == null || trustStoreType.equals("")) {
                // not found use the default
                trustStoreType = "JKS";
            }

            dataUtil = DataUtil.getInstance();
            dataUtil.init();

            discoverService = DiscoverService.getInstance();
            discoverService.init();

            discoverServiceImpl = DiscoverServiceImpl.getInstance();

            String vasaInstallType = ConfigManager.getInstance().getValue("vasa.install.type");
            LOGGER.info("Vasa install Type=" + vasaInstallType);
            if (vasaInstallType != null && !vasaInstallType.equals("")) {
                Map<String, String> vasaInfoMap = dataUtil.getVasaInfoMap();
                if (vasaInfoMap == null) {
                    vasaInfoMap = new HashMap<String, String>();
                }
                vasaInfoMap.put("InstallType", vasaInstallType);
                dataUtil.setVasaInfoMap(vasaInfoMap);
            }

            try {
                List<StorageInfo> arrs = discoverService.queryAllArrays();
                Set<String> arrIds = new HashSet<String>();

                Map<String, DArrayIsLock> arrayIsLockInfo = dataUtil.getArrayIsLock();
                Map<String, DArrayInfo> arrayInfoMap = new HashMap<String, DArrayInfo>();

                for (StorageInfo arr : arrs) {
                    arrIds.add(arr.getId());
                    DArrayIsLock arrayislock = new DArrayIsLock();
                    arrayislock.setDeviceId(arr.getSn());
                    arrayislock.setIsLock(false);
                    arrayIsLockInfo.put(arr.getSn(), arrayislock);
                    dataUtil.setArrayIsLock(arrayIsLockInfo);
                    discoverService.addFlowControlDevice(arr.getSn(), arr.getId());
                    LOGGER.info("------------------------------------arrayId:[" + arr.getId()
                            + "]--------------------------------------");
                    DArrayInfo dArrayInfo = new DArrayInfo();
                    dArrayInfo.setPriority(100);
                    dArrayInfo.setStatus("ONLINE");
                    arrayInfoMap.put(arr.getId(), dArrayInfo);
                }
                dataUtil.setArrayId(arrIds);
                dataUtil.setArrayInfoMap(arrayInfoMap);

                timer = new Timer(true);
                timer.schedule(new RefreshArrayTask(), MagicNumber.INT2 * MagicNumber.INT60 * MagicNumber.INT1000);
            } catch (Exception e) {
                LOGGER.error("getAllArray error!", e);
            }

            secureConnectionService = SecureConnectionService.getInstance();
            storageService = StorageService.getInstance();
            sPBMService = SPBMService.getInstance();
            volumeService = VolumeService.getInstance();
            eventService = EventService.getInstance();
            alarmService = AlarmService.getInstance();

            storageService.init();

            // 初始连接和VASA信息
            sslUtil = new SSLUtil(path, trustPwd, false, trustStoreType);

            secureConnectionService.init(sslUtil);
            secureConnectionService.initializeVasaProviderInfo();

            LOGGER.debug("init eventService");
            eventService.scheduleEvents();

            LOGGER.debug("init alarmService");
            alarmService.scheduleAlarms();

            discoverServiceImpl.initVasaServiceCenter();

            // 传入线程名方便使用jstack定位问题
            LOGGER.info("add clear thread");
            new Thread(VvolControlRunable.getInstance(virtualVolumeService), "Clear-Remain-Vvol-Thread").start();

            LOGGER.info("init timer todo sync certificate , task delay 60 sec .");

            new Timer("SyncCertificate-Timer", true).schedule(new TimerTask() {
                @Override
                public void run() {
                    // todo sync certs
                    LOGGER.info("begin to sync cert .");
                    String installer = dataUtil.getVasaInfoMapByKey("InstallType");
                    if ("Staas".equalsIgnoreCase(installer)) {
                        LOGGER.info("The Staas environment need to sync certificate.");
                        secureConnectionService.queryCertificateToDoSync(false);
                    } else {
                        LOGGER.info("The vasa environment no need to sync certificate.");
                    }
                }
            }, MagicNumber.INT60 * MagicNumber.INT1000);

            LOGGER.info("init timer compete master, delay 60 sec, repeat 10 min.");
            new Timer("CompeteMaster-Timer", true).scheduleAtFixedRate(new CompeteMasterTask(),
                    MagicNumber.INT60 * MagicNumber.INT1000,
                    MagicNumber.INT10 * MagicNumber.INT60 * MagicNumber.INT1000);

            new Timer("RefreshPool-Timer", true).schedule(new RefreshPoolTask(), MagicNumber.INT2 * MagicNumber.INT60 * MagicNumber.INT1000);
        } catch (Exception e) {
            // TODO: handle exception
            LOGGER.error("VasaServicePortTypeImpl init fail ", e);
        }
    }

    public void destroy() {

    }

    class CompeteMasterTask extends TimerTask {
        @Override
        public void run() {
            LOGGER.info("compete master task is run .");
            secureConnectionService.queryCurrentMasterOrCompeteIt();
        }

    }

    class RefreshArrayTask extends TimerTask {
        public void run() {
            while (true) {
                try {
                    // 从缓存中拿到阵列ID
                    Set<String> cachedArrays = dataUtil.getArrayId();
                    List<StorageInfo> arrs = discoverService.queryAllArrays();
                    Set<String> arrIds = new HashSet<String>();
                    refreshArray(arrs, arrIds);
                    dataUtil.setArrayId(arrIds);
                    List<String> ucUUIDs = VASAUtil.getConfigEventUcUUIDs();
                    discoverService.appendConfigStorageArrayAlarm(ucUUIDs, VASAUtil.convertSet2List(cachedArrays),
                            VASAUtil.convertSet2List(arrIds));

                    if (secureConnectionService.checkCurrentNodeIsMaster()) {
                        /*
                         * //定时任务业务逻辑中不上报阵列的新增和移除事件，而应该上报不能管理某台阵列的告警事件 //
                         * 产生阵列添加删除事件,返回给所有订阅了Config事件的vcenter server
                         * List<String> ucUUIDs =
                         * VASAUtil.getConfigEventUcUUIDs();
                         * discoverService.appendConfigStorageArrayEvent(
                         * ucUUIDs, VASAUtil.convertSet2List(cachedArrays),
                         * VASAUtil.convertSet2List(arrIds)); //
                         * 刷新完缓存后将VasaEvent表中不存在的记录删除
                         * discoverService.updateVasaEventTable(VASAUtil.
                         * convertSet2List(arrIds));
                         */
                    }
                    Thread.sleep(MagicNumber.INT2 * MagicNumber.INT60 * MagicNumber.INT1000);
                } catch (Exception e) {
                    LOGGER.error("getAllArray error!", e);
                    try {
                        Thread.sleep(MagicNumber.INT60 * MagicNumber.INT1000);
                    } catch (InterruptedException e1) {
                        LOGGER.error("InterruptedException error " + e1.getMessage());
                    }
                }
            }
        }
    }

    class RefreshPoolTask extends TimerTask {
        public void run() {
            while (true) {
                try {
                    List<NStorageContainer> totalStorageContainers = storageContainerService.getAll();
                    List<NStorageContainer> containers = new ArrayList<NStorageContainer>();
                    for (NStorageContainer nStorageContainer : totalStorageContainers) {
                        if (null == nStorageContainer.getContainerId()
                                || "".equalsIgnoreCase(nStorageContainer.getContainerId())) {
                            continue;
                        }
                        containers.add(nStorageContainer);
                    }
                    if (containers.isEmpty()) {
                        return;
                    }
                    for (NStorageContainer nStorageContainer : containers) {
                        refreshPool(nStorageContainer);
                    }
                    Thread.sleep(MagicNumber.INT2 * MagicNumber.INT60 * MagicNumber.INT1000);
                } catch (Exception e) {
                    LOGGER.error("Refresh Pool error!", e);
                    try {
                        Thread.sleep(MagicNumber.INT60 * MagicNumber.INT1000);
                    } catch (InterruptedException e1) {
                        LOGGER.error("InterruptedException error " + e1.getMessage());
                    }
                }
            }
        }

    }

    private void refreshPool(NStorageContainer nStorageContainer) throws SDKException {

        List<NStoragePool> storagePools = storagePoolService
                .queryStoragePoolByContainerId(nStorageContainer.getContainerId());
        if (storagePools.isEmpty()) {
            return;
        }
        String arrayId = storagePools.get(0).getArrayId();
        SDKResult<List<S2DStoragePool>> storagePoolsFormDevice = model.getAllStoragePool(arrayId);
        List<S2DStoragePool> s2dStoragePool = storagePoolsFormDevice.getResult();

        List<String> s2dStoragePoolIds = new ArrayList<String>();
        for (S2DStoragePool pool : s2dStoragePool) {
            s2dStoragePoolIds.add(pool.getID());
        }
        List<String> poolName = new ArrayList<>();

        for (NStoragePool nStoragePool : storagePools) {
            if ("Lost".equalsIgnoreCase(nStoragePool.getDeviceStatus())) {
                continue;
            }
            if (!s2dStoragePoolIds.contains(nStoragePool.getRawPoolId())) {
                String[] pools = new String[1];
                pools[0] = nStoragePool.getRawPoolId();
                storagePoolService.setStoragePoolsLost(arrayId, nStoragePool.getContainerId(), pools);
                poolName.add(nStoragePool.getName());
            }
        }
        if (!poolName.isEmpty()) {
            LOGGER.info("set container " + nStorageContainer.getContainerName() + " pools " + poolName + " Lost.");
        }

    }

    private void refreshArray(List<StorageInfo> arrs, Set<String> arrIds) throws SDKException {
        for (StorageInfo arr : arrs) {
            // 校验阵列是否在线
            // 1.begin
            // 判断阵列是否已被删除
            if (1 == arr.getDeleted()) {
                continue;
            }
            if (null == arr.getIps()) {
                LOGGER.error("the array ips is null. arrayId=" + arr.getId());
                continue;
            }
            String arrayStatus = arr.getDevicestatus();
            boolean arrayOnline = false;
            /*
             * FusionStorage类型通过OpenApi的keepAlive方法保持连接
             */
            LOGGER.info("DeviceType:   " + arr.getDeviceType());
            /*
             * OceanStor保活
             */
            // 判断当前阵列是否有连接存在
            DeviceInstance deviceFromMap = deviceManager.getDeviceFromMap(arr.getId());
            LOGGER.info("DeviceInstance deviceFromMap: " + deviceFromMap);

            if (null != deviceFromMap) {
                IDeviceConnection connection = deviceFromMap.getConnection(deviceManager.getConnMgr(),
                        deviceManager.getReconnector());
                VASAConnection vasaConnection = (VASAConnection) connection;

                if (null == vasaConnection) {
                    LOGGER.error("get vasaConnection is null, update arrayId = " + arr.getId() + "to offline.");
                    arrayOnline = false;
                    updateArrayStatus(arrIds, arr.getId(), arrayStatus, arrayOnline);
                    continue;
                }

                ResBean doHeartbeat = vasaConnection.doHeartbeat();
                if (0 == doHeartbeat.getErrorCode()) {
                    arrayOnline = true;
                } else {
                    arrayOnline = false;
                }
            } else {
                LOGGER.debug("check array is online or offline, arrayId = " + arr.getId());
                String[] ips = arr.getIps().split(",");
                for (String ip : ips) {
                    DeviceSwitchMetaData deviceSwitchMetaData = new DeviceSwitchMetaData(ip, arr.getPort(),
                            arr.getUsername(), arr.getPassword());
                    DeviceSwitchUtil deviceSwitchUtil = new DeviceSwitchUtil(deviceSwitchMetaData.getServerUrl());
                    if (deviceSwitchUtil.loginDevice(deviceSwitchMetaData.getUname(),
                            deviceSwitchMetaData.getUpass())) {
                        deviceSwitchUtil.logoutDevice();
                        arrIds.add(arr.getId());
                        arrayOnline = true;
                        break;
                    }
                }
            }
            updateArrayStatus(arrIds, arr.getId(), arrayStatus, arrayOnline);
        }
    }

    private void updateArrayStatus(Set<String> arrIds, String arrayId, String arrayStatus, boolean online)
            throws SDKException {
        if (!online) {
            if ("ONLINE".equalsIgnoreCase(arrayStatus)) {
                LOGGER.info("array is offline, arrayId = " + arrayId);
                storageArrayService.updateArrayControllerIP(arrayId, "", "OFFLINE");
            }
        } else {
            if ("OFFLINE".equalsIgnoreCase(arrayStatus)) {
                LOGGER.info("array is online, arrayId = " + arrayId);
                storageArrayService.updateArrayControllerIP(arrayId, "", "ONLINE");
                // 上报添加PE LUN事件
                List<ProtocolEndpoint> arrayProtocolEndpoint = discoverService.queryProtocolEndpointByArrayId(arrayId);
                Map<String, List<ProtocolEndpoint>> session2pEs = DataUtil.getInstance().getSession2PEs();
                Set<String> keySet = session2pEs.keySet();
                for (String session : keySet) {
                    try {
                        SessionContext sc = SessionContext.lookupSessionContextBySessionId(session);
                        if (null == sc) {
                            // this should "never happen" if
                            // checkHttpRequest does not
                            // throw an exception
                            LOGGER.warn("get SessionContext is null. sessionId=" + session);
                            continue;
                        }
                        UsageContext uc = sc.getUsageContext();
                        String vcGuid = uc.getHostGuid();
                        if (null != vcGuid) {
                            LOGGER.info("Append PeConfigEvent. session=" + session + ",vcGuid=" + vcGuid);
                            List<ProtocolEndpoint> sessionPEs = session2pEs.get(session);
                            List<StorageEvent> convertPEAddEvent = discoverService.convertPEAddEvent(sessionPEs,
                                    arrayProtocolEndpoint);
                            EventService.getInstance().appendPeConfigEvent(vcGuid, convertPEAddEvent);
                        }
                    } catch (StorageFault e) {
                        // TODO Auto-generated catch block
                        LOGGER.error("SessionContext.lookupSessionContextBySessionId error. session=" + session, e);
                    }
                }

            }
            arrIds.add(arrayId);
        }
    }



    /*
     * class DelSnapshotThread extends Thread {
     *
     * @Override public void run() { while(true) { List<NVirtualVolume>
     * deletedSnapshotVvols = new ArrayList<NVirtualVolume>(); try {
     * List<NVirtualVolume> vvols =
     * virtualVolumeService.getAllInactiveSnapshots(); if(vvols != null &&
     * vvols.size() != 0) { for (NVirtualVolume vvol : vvols) { Date
     * creationTime = vvol.getCreationTime(); Date currDate =
     * DateUtil.getUTCDate();
     *
     * if (currDate.getTime() - creationTime.getTime() > INACTIVE_TIME *
     * MagicNumber.LONG1000) { deletedSnapshotVvols.add(vvol); } } } } catch
     * (StorageFault e1) { LOGGER.warn("getAllInactiveSnapshots error."); }
     *
     * for(NVirtualVolume snapVvol : deletedSnapshotVvols) { try {
     * DiscoverServiceImpl.getInstance().deleteSnapshot(snapVvol.getVvolid(),
     * snapVvol.getArrayId(), snapVvol.getRawId());
     * DiscoverServiceImpl.getInstance().deleteVirtualVolumeFromDatabase(
     * snapVvol.getVvolid()); LOGGER.info("success deleting inactive snapshot:"
     * + snapVvol.getVvolid()); } catch (Exception e) { LOGGER.warn(
     * "error deleting inactive snapshot:" + snapVvol.getVvolid()); continue; }
     * }
     *
     * try { Thread.sleep(180000L); } catch (InterruptedException e) {
     * LOGGER.error("delete inactive snapshot InterruptedException."); } } } }
     */

    //
    public com.vmware.vim.vasa.v20.xsd.QueryVirtualVolumeResponse queryVirtualVolume(
            com.vmware.vim.vasa.v20.xsd.QueryVirtualVolume parameters)
            throws InactiveProvider, InvalidArgument, VasaProviderBusy, InvalidSession, NotImplemented, StorageFault {
        // Mandatory function
        // verify valid SSL and VASA Sessions.
        String clientAddr = sslUtil.checkHttpRequest(true, true);
        LOGGER.info(clientAddr + ": Executing operation queryVirtualVolume");

        List<String> checkedKeys = VASAUtil.convertSearchConstraint2KeyArray(parameters.getSearchConstraint());
        VASAUtil.checkSearchConstraintKeys(checkedKeys);

        QueryVirtualVolumeResponse response = new QueryVirtualVolumeResponse();
        List<String> result = volumeService.queryVirtualVolume(parameters.getSearchConstraint());
        response.getReturn().addAll(result);
        LOGGER.info("End executing operation queryVirtualVolume");
        return response;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vmware.vim.vasa.v20.VasaServicePortType#getEvents(com.vmware.vim
     * .vasa.v20.xsd.GetEvents parameters )*
     */
    public com.vmware.vim.vasa.v20.xsd.GetEventsResponse getEvents(com.vmware.vim.vasa.v20.xsd.GetEvents parameters)
            throws InvalidSession, InvalidArgument, StorageFault, LostEvent {
        // Mandatory function
        // verify valid SSL and VASA Sessions.
        String clientAddr = sslUtil.checkHttpRequest(true, true);
        LOGGER.info(clientAddr + ": Executing operation getEvents");

        // verify argument
        long lastEventId = parameters.getLastEventId();
        Util.eventOrAlarmIdIsValid(lastEventId);
        // run function
        List<StorageEvent> events = eventService.getEvents(secureConnectionService.getUsageContext(), lastEventId);

        for (StorageEvent event : events) {
            LOGGER.debug("**********eventId:" + event.getEventId() + ", eventType:" + event.getEventType()
                    + ", eventObjType:" + event.getEventObjType() + ", messageId:" + event.getMessageId() + ", arrayId:"
                    + event.getArrayId());
        }

        // reset event params
        VASAUtil.resetEventParams(events);
        GetEventsResponse response = new GetEventsResponse();
        response.getReturn().addAll(events);
        LOGGER.info("End executing operation getEvents");
        return response;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vmware.vim.vasa.v20.VasaServicePortType#
     * queryUniqueIdentifiersForEntity
     * (com.vmware.vim.vasa.v20.xsd.QueryUniqueIdentifiersForEntity parameters
     * )*
     */
    public com.vmware.vim.vasa.v20.xsd.QueryUniqueIdentifiersForEntityResponse queryUniqueIdentifiersForEntity(
            com.vmware.vim.vasa.v20.xsd.QueryUniqueIdentifiersForEntity parameters)
            throws InvalidSession, InvalidArgument, StorageFault {
        // Mandatory function
        // verify valid SSL and VASA Sessions.
        String clientAddr = sslUtil.checkHttpRequest(true, true);
        LOGGER.info(clientAddr + ": Executing operation queryUniqueIdentifiersForEntity");

        UsageContext uc = secureConnectionService.getUsageContext();
        QueryUniqueIdentifiersForEntityResponse response = new QueryUniqueIdentifiersForEntityResponse();
        if (Util.isEmpty(uc.getHostInitiator())) {
            LOGGER.error("usageContext is null.");
            return response;
        }

        // run function
        List<String> uuids = storageService.queryUniqueIdentifiersForEntity(parameters.getEntityType(),
                secureConnectionService.getUsageContext());
        response.getReturn().addAll(uuids);
        LOGGER.info("End executing operation queryUniqueIdentifiersForEntity, the uuids size is " + uuids.size());
        return response;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vmware.vim.vasa.v20.VasaServicePortType#
     * queryCACertificateRevocationLists(*
     */
    public com.vmware.vim.vasa.v20.xsd.QueryCACertificateRevocationListsResponse queryCACertificateRevocationLists()
            throws InvalidSession, VasaProviderBusy, StorageFault {
        // verify valid SSL and VASA Sessions.
        String clientAddr = sslUtil.checkHttpRequest(true, true);
        LOGGER.info(clientAddr + ": Executing operation queryCACertificateRevocationLists");

        QueryCACertificateRevocationListsResponse response = secureConnectionService
                .queryCACertificateRevocationLists();
        LOGGER.info("End executing operation queryCACertificateRevocationLists");
        return response;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.vmware.vim.vasa.v20.VasaServicePortType#queryStorageProcessors(com
     * .vmware.vim.vasa.v20.xsd.QueryStorageProcessors parameters )*
     */
    public com.vmware.vim.vasa.v20.xsd.QueryStorageProcessorsResponse queryStorageProcessors(
            com.vmware.vim.vasa.v20.xsd.QueryStorageProcessors parameters)
            throws InvalidSession, InvalidArgument, NotImplemented, StorageFault {
        // verify valid SSL and VASA Sessions.
        String clientAddr = sslUtil.checkHttpRequest(true, true);
        LOGGER.info(clientAddr + ": Executing operation queryStorageProcessors");

        // verify arguments
        // Util.allUniqueIdsAreValid(processorId, true);
        String[] processorId = ListUtil.list2ArrayString(parameters.getSpUniqueId());
        VASAUtil.checkIsProcessorIdValide(processorId);
        QueryStorageProcessorsResponse response = new QueryStorageProcessorsResponse();
        // run function
        List<DProcessor> pros = storageService.queryStorageProcessors(processorId);
        response.getReturn().addAll(VasaServicePortTypeConvert.queryStorageProcessorsModal2Soap(pros));
        LOGGER.info("End executing operation queryStorageProcessors");
        return response;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vmware.vim.vasa.v20.VasaServicePortType#prepareForBindingChange(
     * com.vmware.vim.vasa.v20.xsd.PrepareForBindingChange parameters )*
     */
    public com.vmware.vim.vasa.v20.xsd.PrepareForBindingChangeResponse prepareForBindingChange(
            com.vmware.vim.vasa.v20.xsd.PrepareForBindingChange parameters) throws InactiveProvider, NotFound,
            InvalidArgument, VasaProviderBusy, InvalidSession, NotImplemented, StorageFault {
        LOGGER.info("Executing operation prepareForBindingChange");
        throw FaultUtil.notImplemented("VASA NOT SUPPORT THIS FUNCTION.");
        /*
         * try { com.vmware.vim.vasa.v20.xsd.PrepareForBindingChangeResponse
         * _return = null; return _return; } catch (java.lang.Exception ex) {
         * LOGGER.error(ex); //throw new RuntimeException(ex); throw
         * FaultUtil.notImplemented("VASA NOT SUPPORT THIS FUNCTION."); }
         */
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.vmware.vim.vasa.v20.VasaServicePortType#unsharedChunksVirtualVolume
     * (com.vmware.vim.vasa.v20.xsd.UnsharedChunksVirtualVolume parameters )*
     */
    public com.vmware.vim.vasa.v20.xsd.UnsharedChunksVirtualVolumeResponse unsharedChunksVirtualVolume(
            com.vmware.vim.vasa.v20.xsd.UnsharedChunksVirtualVolume parameters)
            throws InactiveProvider, NotFound, InvalidArgument, VasaProviderBusy, NotSupported, InvalidSession,
            IncompatibleVolume, NotImplemented, StorageFault {
        // Mandatory function
        // verify valid SSL and VASA Sessions.
        String clientAddr = sslUtil.checkHttpRequest(true, true);
        LOGGER.info(clientAddr + ": Executing operation unsharedChunksVirtualVolume");

        if (!VASAUtil.checkVvolIdValid(parameters.getVvolId())) {
            LOGGER.error("InvalidArgument/invalid vvolId:" + parameters.getVvolId());
            throw FaultUtil.invalidArgument("invalid vvolId:" + parameters.getVvolId());
        }

        if (null != parameters.getBaseVvolId() && VASAUtil.checkVvolIdValid(parameters.getBaseVvolId()) == false) {
            LOGGER.error("InvalidArgument/invalid baseVvolId:" + parameters.getBaseVvolId());
            throw FaultUtil.invalidArgument("invalid baseVvolId:" + parameters.getBaseVvolId());
        }

        if (parameters.getSegmentStartOffsetBytes() < 0 || parameters.getSegmentLengthBytes() < 0) {
            LOGGER.error("InvalidArgument/invalid segmentStartOffsetBytes:" + parameters.getSegmentStartOffsetBytes()
                    + " or invalid segmentLengthBytes:" + parameters.getSegmentLengthBytes());
            throw FaultUtil.invalidArgument("invalid segmentStartOffsetBytes:" + parameters.getSegmentStartOffsetBytes()
                    + " or invalid segmentLengthBytes:" + parameters.getSegmentLengthBytes());
        }

        UnsharedChunksVirtualVolumeResponse response = new UnsharedChunksVirtualVolumeResponse();
        VirtualVolumeUnsharedChunksResult result = volumeService.unsharedChunksVirtualVolume(parameters.getVvolId(),
                parameters.getBaseVvolId(), parameters.getSegmentStartOffsetBytes(),
                parameters.getSegmentLengthBytes());
        response.setReturn(result);
        LOGGER.info("End executing operation unsharedChunksVirtualVolume");
        return response;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vmware.vim.vasa.v20.VasaServicePortType#
     * queryAssociatedStatisticsManifestForArray
     * (com.vmware.vim.vasa.v20.xsd.QueryAssociatedStatisticsManifestForArray
     * parameters )*
     */
    public com.vmware.vim.vasa.v20.xsd.QueryAssociatedStatisticsManifestForArrayResponse queryAssociatedStatisticsManifestForArray(
            com.vmware.vim.vasa.v20.xsd.QueryAssociatedStatisticsManifestForArray parameters)
            throws InactiveProvider, InvalidArgument, VasaProviderBusy, InvalidSession, NotImplemented, StorageFault {
        LOGGER.info("Executing operation queryAssociatedStatisticsManifestForArray");
        try {
            com.vmware.vim.vasa.v20.xsd.QueryAssociatedStatisticsManifestForArrayResponse _return = null;
            return _return;
        } catch (java.lang.Exception ex) {
            // CodeDEX问题修改 ：FORTIFY.System_Information_Leak
            // wwX315527 2016/11/17
            // ex.printStackTrace();
            LOGGER.error("Exception during queryAssociatedStatisticsManifestForArray:" + ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vmware.vim.vasa.v20.VasaServicePortType#resizeVirtualVolume(com.
     * vmware.vim.vasa.v20.xsd.ResizeVirtualVolume parameters )*
     */
    public com.vmware.vim.vasa.v20.xsd.ResizeVirtualVolumeResponse resizeVirtualVolume(
            com.vmware.vim.vasa.v20.xsd.ResizeVirtualVolume parameters)
            throws InactiveProvider, NotFound, InvalidArgument, VasaProviderBusy, NotSupported, InvalidSession,
            PermissionDenied, NotImplemented, ResourceInUse, OutOfResource, StorageFault {
        // verify valid SSL and VASA Sessions.
        String clientAddr = sslUtil.checkHttpRequest(true, true);
        LOGGER.info(clientAddr + ": Executing operation resizeVirtualVolume");

        if (!VASAUtil.checkVvolIdValid(parameters.getVvolId())) {
            LOGGER.error("InvalidArgument/invalid vvolid:" + parameters.getVvolId());
            throw FaultUtil.invalidArgument("invalid vvolid:" + parameters.getVvolId());
        }

        if (parameters.getSizeInMB() <= 0) {
            LOGGER.error("InvalidArgument/invalid sizeInMB:" + parameters.getSizeInMB());
            throw FaultUtil.invalidArgument("invalid sizeInMB:" + parameters.getSizeInMB());
        }

        ResizeVirtualVolumeResponse response = new ResizeVirtualVolumeResponse();
        TaskInfo result = volumeService.resizeVirtualVolume(parameters.getVvolId(), parameters.getSizeInMB());
        response.setReturn(result);
        LOGGER.info("End executing operation resizeVirtualVolume");
        return response;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.vmware.vim.vasa.v20.VasaServicePortType#queryProtocolEndpointForArray
     * (com.vmware.vim.vasa.v20.xsd.QueryProtocolEndpointForArray parameters )*
     */
    public com.vmware.vim.vasa.v20.xsd.QueryProtocolEndpointForArrayResponse queryProtocolEndpointForArray(
            com.vmware.vim.vasa.v20.xsd.QueryProtocolEndpointForArray parameters) throws InactiveProvider, NotFound,
            InvalidArgument, VasaProviderBusy, InvalidSession, NotImplemented, StorageFault {
        // verify valid SSL and VASA Sessions.
        String clientAddr = sslUtil.checkHttpRequest(true, true);
        LOGGER.info(clientAddr + ": Executing operation queryProtocolEndpointForArray");
        VASAUtil.checkArrayIdValidAndExist(parameters.getArrayId());
        String arrayId = parameters.getArrayId().split(":")[1];

        String sessionid = sslUtil.getCookie(VASA_SESSIONID_STR);
        QueryProtocolEndpointForArrayResponse response = new QueryProtocolEndpointForArrayResponse();

        List<String> result = volumeService
                .queryProtocolEndpointForArray(DataUtil.getInstance().getPEsBySessionId(sessionid), arrayId);
        response.getReturn().addAll(result);
        LOGGER.info("End executing operation queryProtocolEndpointForArray");
        return response;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.vmware.vim.vasa.v20.VasaServicePortType#cancelTask(com.vmware.vim
     * .vasa.v20.xsd.CancelTask parameters )*
     */
    public void cancelTask(com.vmware.vim.vasa.v20.xsd.CancelTask parameters) throws NotCancellable, InactiveProvider,
            NotFound, InvalidArgument, VasaProviderBusy, InvalidSession, NotImplemented, StorageFault {
        // verify valid SSL and VASA Sessions.
        String clientAddr = sslUtil.checkHttpRequest(true, true);
        LOGGER.info(clientAddr + ": Executing operation cancelTask");

        volumeService.cancelTask(parameters.getTaskId());

        LOGGER.info("End executing operation cancelTask");
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.vmware.vim.vasa.v20.VasaServicePortType#queryCapabilityProfile(com
     * .vmware.vim.vasa.v20.xsd.QueryCapabilityProfile parameters )*
     */
    public com.vmware.vim.vasa.v20.xsd.QueryCapabilityProfileResponse queryCapabilityProfile(
            com.vmware.vim.vasa.v20.xsd.QueryCapabilityProfile parameters)
            throws InactiveProvider, NotFound, VasaProviderBusy, InvalidSession, StorageFault {
        // verify valid SSL and VASA Sessions.
        String clientAddr = sslUtil.checkHttpRequest(true, true);
        LOGGER.info(clientAddr + ": Executing operation queryCapabilityProfile");

        String[] profileIds = ListUtil.list2ArrayString(parameters.getProfileId());
        QueryCapabilityProfileResponse response = new QueryCapabilityProfileResponse();

        List<StorageProfile> sps = sPBMService.queryCapabilityProfile(profileIds);
        response.getReturn().addAll(sps);

        LOGGER.info("End Executing operation queryCapabilityProfile");
        return response;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vmware.vim.vasa.v20.VasaServicePortType#queryCapabilityMetadata(
     * com.vmware.vim.vasa.v20.xsd.QueryCapabilityMetadata parameters )*
     */
    public com.vmware.vim.vasa.v20.xsd.QueryCapabilityMetadataResponse queryCapabilityMetadata(
            com.vmware.vim.vasa.v20.xsd.QueryCapabilityMetadata parameters)
            throws InactiveProvider, NotFound, VasaProviderBusy, InvalidSession, StorageFault {
        // verify valid SSL and VASA Sessions.
        String clientAddr = sslUtil.checkHttpRequest(true, true);
        LOGGER.info(clientAddr + ": Executing operation queryCapabilityMetadata");

        String[] schemaIds = ListUtil.list2ArrayString(parameters.getSchemaId());
        QueryCapabilityMetadataResponse response = new QueryCapabilityMetadataResponse();

        List<CapabilitySchema> sps = sPBMService.queryCapabilityMetadata(schemaIds);
        response.getReturn().addAll(sps);

        LOGGER.info("End Executing operation queryCapabilityMetadata");
        return response;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vmware.vim.vasa.v20.VasaServicePortType#
     * queryCapabilityProfileForResource
     * (com.vmware.vim.vasa.v20.xsd.QueryCapabilityProfileForResource parameters
     * )*
     */
    public com.vmware.vim.vasa.v20.xsd.QueryCapabilityProfileForResourceResponse queryCapabilityProfileForResource(
            com.vmware.vim.vasa.v20.xsd.QueryCapabilityProfileForResource parameters)
            throws InactiveProvider, NotFound, InvalidArgument, VasaProviderBusy, InvalidSession, StorageFault {
        // verify valid SSL and VASA Sessions.
        String clientAddr = sslUtil.checkHttpRequest(true, true);
        LOGGER.info(clientAddr + ": Executing operation queryCapabilityProfileForResource");

        String[] resourceIds = ListUtil.list2ArrayString(parameters.getResourceId());
        QueryCapabilityProfileForResourceResponse response = new QueryCapabilityProfileForResourceResponse();

        List<ResourceAssociation> assos = sPBMService.queryCapabilityProfileForResource(resourceIds);
        response.getReturn().addAll(assos);

        LOGGER.info("End Executing operation queryCapabilityProfileForResource");
        return response;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.vmware.vim.vasa.v20.VasaServicePortType#queryAssociatedLunsForPort
     * (com.vmware.vim.vasa.v20.xsd.QueryAssociatedLunsForPort parameters )*
     */
    public com.vmware.vim.vasa.v20.xsd.QueryAssociatedLunsForPortResponse queryAssociatedLunsForPort(
            com.vmware.vim.vasa.v20.xsd.QueryAssociatedLunsForPort parameters)
            throws InvalidSession, InvalidArgument, NotImplemented, StorageFault {
        // verify valid SSL and VASA Sessions.
        String clientAddr = sslUtil.checkHttpRequest(true, true);
        LOGGER.info(clientAddr + ": Executing operation queryAssociatedLunsForPort");
        // verify arguments
        // Util.allUniqueIdsAreValid(portId, true);
        String[] portId = ListUtil.list2ArrayString(parameters.getPortUniqueId());
        VASAUtil.checkIsPortIdValid(portId);
        QueryAssociatedLunsForPortResponse response = new QueryAssociatedLunsForPortResponse();
        // run function
        VasaAssociationObject[] assos = storageService.queryAssociatedLunsForPort(
                secureConnectionService.getHostInitiatorIds(), portId, secureConnectionService.getUsageContext());

        String contextUUID = secureConnectionService.getUsageContext().getVcGuid();
        // 将查询到的LUN加入到ussgecontext
        if (assos.length != 0) {
            for (VasaAssociationObject o : assos) {
                List<BaseStorageEntity> lunids = o.getAssociatedId();
                if (lunids == null || lunids.isEmpty()) {
                    continue;
                }
                for (BaseStorageEntity en : lunids) {
                    LOGGER.debug("addLunToUsageContext UniqueIdentifier=" + en.getUniqueIdentifier());
                    DataUtil.getInstance().addLunToUsageContext(en.getUniqueIdentifier(), contextUUID);
                }
            }
        }

        response.getReturn().addAll(ListUtil.array2List(assos));
        LOGGER.info("End executing operation queryAssociatedLunsForPort, the response size is "
                + ListUtil.array2List(assos).size());
        return response;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.vmware.vim.vasa.v20.VasaServicePortType#queryProtocolEndpoint(com
     * .vmware.vim.vasa.v20.xsd.QueryProtocolEndpoint parameters )*
     */
    public com.vmware.vim.vasa.v20.xsd.QueryProtocolEndpointResponse queryProtocolEndpoint(
            com.vmware.vim.vasa.v20.xsd.QueryProtocolEndpoint parameters)
            throws InvalidSession, InactiveProvider, VasaProviderBusy, NotImplemented, StorageFault {
        // verify valid SSL and VASA Sessions.
        String clientAddr = sslUtil.checkHttpRequest(true, true);
        LOGGER.info(clientAddr + ": Executing operation queryProtocolEndpoint");

        String sessionid = sslUtil.getCookie(VASA_SESSIONID_STR);
        QueryProtocolEndpointResponse response = new QueryProtocolEndpointResponse();
        List<ProtocolEndpoint> result = volumeService.queryProtocolEndpoint(dataUtil.getPEsBySessionId(sessionid),
                parameters.getPeId());
        response.getReturn().addAll(result);
        LOGGER.info("End executing operation queryProtocolEndpoint");
        return response;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vmware.vim.vasa.v20.VasaServicePortType#queryBackingStoragePools
     * (com.vmware.vim.vasa.v20.xsd.QueryBackingStoragePools parameters )*
     */
    public com.vmware.vim.vasa.v20.xsd.QueryBackingStoragePoolsResponse queryBackingStoragePools(
            com.vmware.vim.vasa.v20.xsd.QueryBackingStoragePools parameters)
            throws InactiveProvider, InvalidArgument, VasaProviderBusy, InvalidSession, NotImplemented, StorageFault {
        LOGGER.info("Executing operation queryBackingStoragePools");
        try {
            com.vmware.vim.vasa.v20.xsd.QueryBackingStoragePoolsResponse _return = null;
            return _return;
        } catch (java.lang.Exception ex) {
            // CodeDEX问题修改 ：FORTIFY.System_Information_Leak
            // wwX315527 2016/11/17
            // ex.printStackTrace();
            LOGGER.error("Exception during queryBackingStoragePools:" + ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.vmware.vim.vasa.v20.VasaServicePortType#bindingChangeComplete(com
     * .vmware.vim.vasa.v20.xsd.BindingChangeComplete parameters )*
     */
    public com.vmware.vim.vasa.v20.xsd.BindingChangeCompleteResponse bindingChangeComplete(
            com.vmware.vim.vasa.v20.xsd.BindingChangeComplete parameters) throws InactiveProvider, NotFound,
            InvalidArgument, VasaProviderBusy, InvalidSession, NotImplemented, StorageFault {
        LOGGER.info("Executing operation bindingChangeComplete");
        throw FaultUtil.notImplemented("VASA NOT SUPPORT THIS FUNCTION.");
        /*
         * try { com.vmware.vim.vasa.v20.xsd.BindingChangeCompleteResponse
         * _return = null; return _return; } catch (java.lang.Exception ex) { //
         * CodeDEX问题修改 ：FORTIFY.System_Information_Leak // wwX315527 2016/11/17
         * // ex.printStackTrace(); LOGGER.error(
         * "Exception during bindingChangeComplete:" + ex.getMessage()); throw
         * new RuntimeException(ex); }
         */
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.vmware.vim.vasa.v20.VasaServicePortType#unregisterVASACertificate
     * (com.vmware.vim.vasa.v20.xsd.UnregisterVASACertificate parameters )*
     */
    public void unregisterVASACertificate(com.vmware.vim.vasa.v20.xsd.UnregisterVASACertificate parameters)
            throws InvalidSession, InvalidCertificate, StorageFault {
        // verify valid SSL and VASA Sessions.
        LOGGER.info("Executing operation unregisterVASACertificate function.");
        String clientAddr = sslUtil.checkHttpRequestThrowInvalidCertificate(false, false);
        LOGGER.info(clientAddr + ": Executing operation unregisterVASACertificate");
        secureConnectionService.unregisterVASACertificate(parameters.getExistingCertificate());
        LOGGER.info("End Executing operation unregisterVASACertificate");
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.vmware.vim.vasa.v20.VasaServicePortType#refreshCACertificatesAndCRLs
     * (com.vmware.vim.vasa.v20.xsd.RefreshCACertificatesAndCRLs parameters )*
     */
    public void refreshCACertificatesAndCRLs(com.vmware.vim.vasa.v20.xsd.RefreshCACertificatesAndCRLs parameters)
            throws VasaProviderBusy, InvalidSession, PermissionDenied, InvalidCertificate, StorageFault {
        LOGGER.info("Executing operation refreshCACertificatesAndCRLs function.");
        String clientAddr = this.sslUtil.checkHttpRequestThrowInvalidCertificate(true, true);
        LOGGER.info(clientAddr + ": Executing operation refreshCACertificatesAndCRLs");
        LOGGER.info("End Executing operation refreshCACertificatesAndCRLs");
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.vmware.vim.vasa.v20.VasaServicePortType#activateProviderEx(com.vmware
     * .vim.vasa.v20.xsd.ActivateProviderEx parameters )*
     */
    public com.vmware.vim.vasa.v20.xsd.ActivateProviderExResponse activateProviderEx(
            com.vmware.vim.vasa.v20.xsd.ActivateProviderEx parameters)
            throws InvalidSession, InvalidArgument, VasaProviderBusy, ActivateProviderFailed, StorageFault {
        String clientAddr = sslUtil.checkHttpRequest(true, true);
        LOGGER.info(clientAddr + ": Executing operation activateProviderEx");
        try {
            TaskInfo taskInfo = new TaskInfo();
            taskInfo.setName("activateProviderEx");
            taskInfo.setCancelable(false);
            taskInfo.setCancelled(false);

            taskInfo.setStartTime(DateUtil.getUTCCalendar());

            taskInfo.setTaskId("activateProviderEx:" + parameters.getActivationSpec().getEntityId());
            taskInfo.setProgress(100);
            taskInfo.setProgressUpdateAvailable(false);
            taskInfo.setTaskState(TaskStateEnum.SUCCESS.value());

            ActivateProviderExResponse response = new ActivateProviderExResponse();
            response.setReturn(taskInfo);
            LOGGER.info("End Executing operation activateProviderEx");
            return response;
        } catch (java.lang.Exception ex) {
            LOGGER.error("StorageFault/activateProviderEx error.");
            throw FaultUtil.storageFault("activateProviderEx error.");
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vmware.vim.vasa.v20.VasaServicePortType#setPEContext(com.vmware.
     * vim.vasa.v20.xsd.SetPEContext parameters )*
     */
    public void setPEContext(com.vmware.vim.vasa.v20.xsd.SetPEContext parameters)
            throws InvalidSession, VasaProviderBusy, NotImplemented, StorageFault {
        // verify valid SSL and VASA Sessions.
        String clientAddr = sslUtil.checkHttpRequest(true, true);
        LOGGER.info(clientAddr + ": Executing operation setPEContext");

        String sessionId = sslUtil.getCookie(VASA_SESSIONID_STR);
        if (sessionId == null) {
            LOGGER.error("InvalidSession/No valid VASA SessionId in HTTP header");
            throw FaultUtil.invalidSession("No valid VASA SessionId in HTTP header");
        }

        volumeService.setPEContext(sessionId, secureConnectionService.getUsageContext(), parameters.getListOfHostPE());
        LOGGER.info("End executing operation setPEContext");
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vmware.vim.vasa.v20.VasaServicePortType#copyDiffsToVirtualVolume
     * (com.vmware.vim.vasa.v20.xsd.CopyDiffsToVirtualVolume parameters )*
     */
    public com.vmware.vim.vasa.v20.xsd.CopyDiffsToVirtualVolumeResponse copyDiffsToVirtualVolume(
            com.vmware.vim.vasa.v20.xsd.CopyDiffsToVirtualVolume parameters)
            throws InactiveProvider, NotFound, InvalidArgument, VasaProviderBusy, NotSupported, InvalidSession,
            IncompatibleVolume, PermissionDenied, NotImplemented, ResourceInUse, StorageFault {
        // verify valid SSL and VASA Sessions.
        String clientAddr = sslUtil.checkHttpRequest(true, true);
        LOGGER.info(clientAddr + ": Executing operation copyDiffsToVirtualVolume");

        CopyDiffsToVirtualVolumeResponse response = new CopyDiffsToVirtualVolumeResponse();
        TaskInfo result = volumeService.copyDiffsToVirtualVolume(parameters.getSrcVVolId(),
                parameters.getSrcBaseVVolId(), parameters.getDstVVolId());
        response.setReturn(result);
        LOGGER.info("End executing operation copyDiffsToVirtualVolume");
        return response;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vmware.vim.vasa.v20.VasaServicePortType#
     * prepareToSnapshotVirtualVolume
     * (com.vmware.vim.vasa.v20.xsd.PrepareToSnapshotVirtualVolume parameters )*
     */
    public com.vmware.vim.vasa.v20.xsd.PrepareToSnapshotVirtualVolumeResponse prepareToSnapshotVirtualVolume(
            com.vmware.vim.vasa.v20.xsd.PrepareToSnapshotVirtualVolume parameters) throws InactiveProvider, NotFound,
            InvalidArgument, VasaProviderBusy, NotSupported, InvalidSession, SnapshotTooMany, PermissionDenied,
            NotImplemented, ResourceInUse, OutOfResource, StorageFault, InvalidProfile {
        // verify valid SSL and VASA Sessions.
        String clientAddr = sslUtil.checkHttpRequest(true, true);
        LOGGER.info(clientAddr + ": Executing operation prepareToSnapshotVirtualVolume");

        PrepareToSnapshotVirtualVolumeResponse response = new PrepareToSnapshotVirtualVolumeResponse();
        TaskInfo result = volumeService.prepareToSnapshotVirtualVolume(parameters.getVvolId(),
                parameters.getStorageProfile());
        response.setReturn(result);

        LOGGER.info("End executing operation prepareToSnapshotVirtualVolume");
        return response;
    }


    /*
     * (non-Javadoc)
     *
     * @see com.vmware.vim.vasa.v20.VasaServicePortType#
     * queryAssociatedCapabilityForFileSystem
     * (com.vmware.vim.vasa.v20.xsd.QueryAssociatedCapabilityForFileSystem
     * parameters )*
     */
    public com.vmware.vim.vasa.v20.xsd.QueryAssociatedCapabilityForFileSystemResponse queryAssociatedCapabilityForFileSystem(
            com.vmware.vim.vasa.v20.xsd.QueryAssociatedCapabilityForFileSystem parameters)
            throws InvalidSession, InvalidArgument, NotImplemented, StorageFault {
        // verify valid SSL and VASA Sessions.
        String clientAddr = sslUtil.checkHttpRequest(true, true);
        LOGGER.info(clientAddr + ": Executing operation queryAssociatedCapabilityForFileSystem. isSupportNFS"
                + isSupportNFS);

        // verify arguments
        // Util.allUniqueIdsAreValid(lunId, true);
        if (isSupportNFS) {
            String[] fsId = ListUtil.list2ArrayString(parameters.getFsUniqueId());
            VASAUtil.checkIsFileSystemIdInvalid(fsId, true);
            QueryAssociatedCapabilityForFileSystemResponse response = new QueryAssociatedCapabilityForFileSystemResponse();
            // run function
            VasaAssociationObject[] assos = storageService.queryAssociatedCapabilityForFileSystem(
                    secureConnectionService.getUsageContext().getMountPoint(), fsId);

            response.getReturn().addAll(ListUtil.array2List(assos));
            LOGGER.info("End executing operation queryAssociatedCapabilityForFileSystem");
            return response;
        } else {
            isSupportNFS = false;
            LOGGER.error("NotImplemented/This device not implemented FileSystemProfile");
            throw FaultUtil.notImplemented("This device not implemented FileSystemProfile");
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vmware.vim.vasa.v20.VasaServicePortType#setStatisticsContext(com
     * .vmware.vim.vasa.v20.xsd.SetStatisticsContext parameters )*
     */
    public void setStatisticsContext(com.vmware.vim.vasa.v20.xsd.SetStatisticsContext parameters)
            throws InactiveProvider, InvalidArgument, InvalidStatisticsContext, VasaProviderBusy, InvalidSession,
            NotImplemented, StorageFault {
        LOGGER.info("Executing operation setStatisticsContext");
        System.out.println(parameters);
        try {
        } catch (java.lang.Exception ex) {
            // CodeDEX问题修改 ：FORTIFY.System_Information_Leak
            // wwX315527 2016/11/17
            // ex.printStackTrace();
            LOGGER.error("Exception during setStatisticsContext:" + ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vmware.vim.vasa.v20.VasaServicePortType#
     * queryAssociatedProcessorsForArray
     * (com.vmware.vim.vasa.v20.xsd.QueryAssociatedProcessorsForArray parameters
     * )*
     */
    public com.vmware.vim.vasa.v20.xsd.QueryAssociatedProcessorsForArrayResponse queryAssociatedProcessorsForArray(
            com.vmware.vim.vasa.v20.xsd.QueryAssociatedProcessorsForArray parameters)
            throws InvalidSession, InvalidArgument, NotImplemented, StorageFault {
        // verify valid SSL and VASA Sessions.
        String clientAddr = sslUtil.checkHttpRequest(true, true);
        LOGGER.info(clientAddr + ": Executing operation queryAssociatedProcessorsForArray");

        // verify arguments
        // Util.allUniqueIdsAreValid(arrayId, true);
        String[] arrayId = ListUtil.list2ArrayString(parameters.getArrayUniqueId());
        VASAUtil.checkIsArrayIdValid(arrayId);
        QueryAssociatedProcessorsForArrayResponse response = new QueryAssociatedProcessorsForArrayResponse();
        UsageContext uc = secureConnectionService.getUsageContext();
        if (Util.isEmpty(uc.getHostInitiator())) {
            LOGGER.error("queryAssociatedProcessorsForArray,usageContext is null.");

            return response;
        }

        // run function
        VasaAssociationObject[] assos = storageService.queryAssociatedProcessorsForArray(arrayId);

        response.getReturn().addAll(ListUtil.array2List(assos));
        LOGGER.info("End executing operation queryAssociatedProcessorsForArray");
        return response;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vmware.vim.vasa.v20.VasaServicePortType#
     * queryDRSMigrationCapabilityForPerformance
     * (com.vmware.vim.vasa.v20.xsd.QueryDRSMigrationCapabilityForPerformance
     * parameters )*
     */
    public com.vmware.vim.vasa.v20.xsd.QueryDRSMigrationCapabilityForPerformanceResponse queryDRSMigrationCapabilityForPerformance(
            com.vmware.vim.vasa.v20.xsd.QueryDRSMigrationCapabilityForPerformance parameters)
            throws InvalidSession, NotFound, InvalidArgument, StorageFault {
        // verify valid SSL and VASA Sessions.
        String clientAddr = sslUtil.checkHttpRequest(true, true);
        LOGGER.info(clientAddr + ": Executing operation queryDRSMigrationCapabilityForPerformance");

        // verify arguments
        // Util.uniqueIdIsValid(srcUniqueId);
        // Util.uniqueIdIsValid(dstUniqueId);
        String srcUniqueId = parameters.getSrcUniqueId();
        String dstUniqueId = parameters.getDstUniqueId();
        String entityType = parameters.getEntityType();
        VASAUtil.checkIsUniqueIdInvalid(new String[]{srcUniqueId}, true);
        VASAUtil.checkIsUniqueIdInvalid(new String[]{dstUniqueId}, true);

        // run function
        boolean result = storageService.queryDRSMigrationCapabilityForPerformance(
                secureConnectionService.getHostInitiatorIds(),
                secureConnectionService.getUsageContext().getMountPoint(), srcUniqueId, dstUniqueId, entityType);

        QueryDRSMigrationCapabilityForPerformanceResponse response = new QueryDRSMigrationCapabilityForPerformanceResponse();
        response.setReturn(result);
        LOGGER.info("End executing operation queryDRSMigrationCapabilityForPerformance");
        return response;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vmware.vim.vasa.v20.VasaServicePortType#
     * queryDefaultProfileForStorageContainer
     * (com.vmware.vim.vasa.v20.xsd.QueryDefaultProfileForStorageContainer
     * parameters )*
     */
    public com.vmware.vim.vasa.v20.xsd.QueryDefaultProfileForStorageContainerResponse queryDefaultProfileForStorageContainer(
            com.vmware.vim.vasa.v20.xsd.QueryDefaultProfileForStorageContainer parameters)
            throws InactiveProvider, NotFound, InvalidArgument, VasaProviderBusy, InvalidSession, StorageFault {
        // verify valid SSL and VASA Sessions.
        String clientAddr = sslUtil.checkHttpRequest(true, true);
        LOGGER.info(clientAddr + ": Executing operation queryDefaultProfileForStorageContainer");

        QueryDefaultProfileForStorageContainerResponse response = new QueryDefaultProfileForStorageContainerResponse();

        VASAUtil.checkEntityType(parameters.getEntityType());
        if (!VASAUtil.checkContainerIdValid(parameters.getContainerId())) {
            LOGGER.error("NotFound/containerId not valid:" + parameters.getContainerId());
            throw FaultUtil.notFound("containerId not valid:" + parameters.getContainerId());
        }
        VASAUtil.checkContainerIdExist(parameters.getContainerId());

        //// List<DefaultProfile> result =
        //// sPBMService.queryDefaultProfileForStorageContainer(parameters.getContainerId(),
        //// parameters.getEntityType());
        // response.getReturn().addAll(result);
        LOGGER.info("End Executing operation queryDefaultProfileForStorageContainer");
        return response;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vmware.vim.vasa.v20.VasaServicePortType#
     * queryUniqueIdentifiersForFileSystems
     * (com.vmware.vim.vasa.v20.xsd.QueryUniqueIdentifiersForFileSystems
     * parameters )*
     */
    public com.vmware.vim.vasa.v20.xsd.QueryUniqueIdentifiersForFileSystemsResponse queryUniqueIdentifiersForFileSystems(
            com.vmware.vim.vasa.v20.xsd.QueryUniqueIdentifiersForFileSystems parameters)
            throws InvalidSession, NotFound, InvalidArgument, NotImplemented, StorageFault {
        // verify valid SSL and VASA Sessions.
        String clientAddr = sslUtil.checkHttpRequest(true, true);
        LOGGER.info(clientAddr + ": Executing operation queryUniqueIdentifiersForFileSystems:"
                + parameters.getFsUniqueId());

        // verify arguments
        // Util.uniqueIdIsValid(arrayId);
        String arrayUniqueId = parameters.getFsUniqueId();
        VASAUtil.checkIsArrayIdValid(new String[]{arrayUniqueId});
        if (null == arrayUniqueId || "".equals(arrayUniqueId)) {
            LOGGER.error(
                    "InvalidArgument/queryUniqueIdentifiersForFileSystems: NULL or emputy not allowed as parameter");
            throw FaultUtil
                    .invalidArgument("queryUniqueIdentifiersForFileSystems: NULL or emputy not allowed as parameter");
        }
        DArray array = dataUtil.getArray(arrayUniqueId.split(":")[1]);
        if (null == array) {
            LOGGER.error("NotFound/array ID: " + arrayUniqueId + " not found");
            throw FaultUtil.notFound("array ID: " + arrayUniqueId + " not found");
        }
        if (array.getSupportedFileSystem().contains("NFS")) {
            isSupportNFS = true;
            QueryUniqueIdentifiersForFileSystemsResponse response = new QueryUniqueIdentifiersForFileSystemsResponse();
            UsageContext uc = secureConnectionService.getUsageContext();
            if (Util.isEmpty(uc.getHostInitiator())) {
                LOGGER.error("queryUniqueIdentifiersForFileSystems,usageContext is null.");
                return response;
            }

            // run function
            String[] uuids = storageService.queryUniqueIdentifiersForFileSystems(uc.getMountPoint(), arrayUniqueId);
            // String[] uuids =
            // {"210235G6EH10E8000001:StorageFileSystem:5"};
            response.getReturn().addAll(ListUtil.array2List(uuids));
            LOGGER.info("End executing operation queryUniqueIdentifiersForFileSystems");
            return response;
        } else {
            isSupportNFS = false;
            LOGGER.error("NotImplemented/This device not implemented FileSystemProfile");
            throw FaultUtil.notImplemented("This device not implemented FileSystemProfile");
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vmware.vim.vasa.v20.VasaServicePortType#
     * queryAssociatedCapabilityForLun
     * (com.vmware.vim.vasa.v20.xsd.QueryAssociatedCapabilityForLun parameters
     * )*
     */
    public com.vmware.vim.vasa.v20.xsd.QueryAssociatedCapabilityForLunResponse queryAssociatedCapabilityForLun(
            com.vmware.vim.vasa.v20.xsd.QueryAssociatedCapabilityForLun parameters)
            throws InvalidSession, InvalidArgument, NotImplemented, StorageFault {
        // verify valid SSL and VASA Sessions.
        String clientAddr = sslUtil.checkHttpRequest(true, true);
        LOGGER.info(clientAddr + ": Executing operation queryAssociatedCapabilityForLun");

        // verify arguments
        // Util.allUniqueIdsAreValid(lunId, true);
        String[] lunId = ListUtil.list2ArrayString(parameters.getLunUniqueId());
        VASAUtil.checkIsLunIdInvalid(lunId, true);

        QueryAssociatedCapabilityForLunResponse response = new QueryAssociatedCapabilityForLunResponse();
        // run function
        VasaAssociationObject[] assos = storageService
                .queryAssociatedCapabilityForLun(secureConnectionService.getHostInitiatorIds(), lunId);

        // VasaAssociationObject vasaAssociationObject = new
        // VasaAssociationObject();
        // BaseStorageEntity entityId = new BaseStorageEntity();
        // entityId.setUniqueIdentifier("210235G6EH10E8000001:StorageLun:10");
        // ListUtil.clearAndAdd(vasaAssociationObject.getEntityId(), new
        // BaseStorageEntity[]
        // {entityId});
        //
        // BaseStorageEntity associationId = new BaseStorageEntity();
        // ListUtil.clearAndAdd(vasaAssociationObject.getAssociatedId(), new
        // BaseStorageEntity[]
        // {associationId});
        // VasaAssociationObject[] assos = new VasaAssociationObject[]
        // {vasaAssociationObject};
        response.getReturn().addAll(ListUtil.array2List(assos));
        LOGGER.info("End executing operation queryAssociatedCapabilityForLun");
        return response;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vmware.vim.vasa.v20.VasaServicePortType#
     * queryAssociatedPortsForProcessor
     * (com.vmware.vim.vasa.v20.xsd.QueryAssociatedPortsForProcessor parameters
     * )*
     */
    public com.vmware.vim.vasa.v20.xsd.QueryAssociatedPortsForProcessorResponse queryAssociatedPortsForProcessor(
            com.vmware.vim.vasa.v20.xsd.QueryAssociatedPortsForProcessor parameters)
            throws InvalidSession, InvalidArgument, NotImplemented, StorageFault {
        // verify valid SSL and VASA Sessions.
        String clientAddr = sslUtil.checkHttpRequest(true, true);
        LOGGER.info(clientAddr + ": Executing operation queryAssociatedPortsForProcessor");

        // verify arguments
        // Util.allUniqueIdsAreValid(processorId, true);
        String[] processorId = ListUtil.list2ArrayString(parameters.getSpUniqueId());
        VASAUtil.checkIsProcessorIdValide(processorId);

        QueryAssociatedPortsForProcessorResponse response = new QueryAssociatedPortsForProcessorResponse();
        // run function
        VasaAssociationObject[] assos = storageService
                .queryAssociatedPortsForProcessor(secureConnectionService.getHostInitiatorIds(), processorId);
        response.getReturn().addAll(ListUtil.array2List(assos));
        LOGGER.info("End executing operation queryAssociatedPortsForProcessor");
        return response;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.vmware.vim.vasa.v20.VasaServicePortType#queryUniqueIdentifiersForLuns
     * (com.vmware.vim.vasa.v20.xsd.QueryUniqueIdentifiersForLuns parameters )*
     */
    public com.vmware.vim.vasa.v20.xsd.QueryUniqueIdentifiersForLunsResponse queryUniqueIdentifiersForLuns(
            com.vmware.vim.vasa.v20.xsd.QueryUniqueIdentifiersForLuns parameters)
            throws InvalidSession, NotFound, InvalidArgument, NotImplemented, StorageFault {
        // verify valid SSL and VASA Sessions.
        String clientAddr = sslUtil.checkHttpRequest(true, true);
        LOGGER.info(clientAddr + ": Executing operation queryUniqueIdentifiersForLuns");

        // verify arguments
        // Util.uniqueIdIsValid(arrayUniqueId);
        String arrayUniqueId = parameters.getArrayUniqueId();
        VASAUtil.checkIsArrayIdValid(new String[]{arrayUniqueId});
        QueryUniqueIdentifiersForLunsResponse response = new QueryUniqueIdentifiersForLunsResponse();
        UsageContext uc = secureConnectionService.getUsageContext();
        if (Util.isEmpty(uc.getHostInitiator())) {
            LOGGER.error("queryUniqueIdentifiersForLuns,usageContext is null.");
            return response;
        }

        // run function
        List<String> uuids = storageService.queryUniqueIdentifiersForLuns(uc,
                secureConnectionService.getHostInitiatorIds(), arrayUniqueId);
        // String[] uuids = new String[]
        // {"210235G6EH10E8000001:StorageLun:10"};

        response.getReturn().addAll(uuids);
        LOGGER.info("End executing operation queryUniqueIdentifiersForLuns");
        return response;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vmware.vim.vasa.v20.VasaServicePortType#registerVASACertificate(
     * com.vmware.vim.vasa.v20.xsd.RegisterVASACertificate parameters )*
     */
    public com.vmware.vim.vasa.v20.xsd.RegisterVASACertificateResponse registerVASACertificate(
            com.vmware.vim.vasa.v20.xsd.RegisterVASACertificate parameters)
            throws InvalidSession, InvalidLogin, InvalidCertificate, StorageFault {
        LOGGER.info("Executing operation registerVASACertificate function.");
        // Mandatory function
        String clientAddr = this.sslUtil.checkHttpRequestThrowInvalidCertificate(false, false);
        LOGGER.info(clientAddr + ": Executing operation registerVASACertificate");

        // run function
        VasaProviderInfo vpInfo = secureConnectionService.registerVASACertificate(parameters.getUserName(),
                parameters.getPassword(), parameters.getNewCertificate());
        com.vmware.vim.vasa.v20.xsd.RegisterVASACertificateResponse response = new RegisterVASACertificateResponse();
        response.setReturn(vpInfo);
        LOGGER.info("End executing operation registerVASACertificate");
        return response;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vmware.vim.vasa.v20.VasaServicePortType#revertVirtualVolume(com.
     * vmware.vim.vasa.v20.xsd.RevertVirtualVolume parameters )*
     */
    public com.vmware.vim.vasa.v20.xsd.RevertVirtualVolumeResponse revertVirtualVolume(
            com.vmware.vim.vasa.v20.xsd.RevertVirtualVolume parameters)
            throws InactiveProvider, NotFound, InvalidArgument, VasaProviderBusy, NotSupported, InvalidSession,
            IncompatibleVolume, PermissionDenied, NotImplemented, ResourceInUse, StorageFault {
        String clientAddr = sslUtil.checkHttpRequest(true, true);
        LOGGER.info(clientAddr + ": Executing operation revertVirtualVolume");

        if (!VASAUtil.checkVvolIdValid(parameters.getVvolId())
                || !VASAUtil.checkVvolIdValid(parameters.getSnapshotVvolId())) {
            LOGGER.error("InvalidArgument/invalid argument. vvolId:" + parameters.getVvolId() + ", snapshotVvolId:"
                    + parameters.getSnapshotVvolId());
            throw FaultUtil.invalidArgument("invalid argument. vvolId:" + parameters.getVvolId() + ", snapshotVvolId:"
                    + parameters.getSnapshotVvolId());
        }

        RevertVirtualVolumeResponse response = new RevertVirtualVolumeResponse();
        TaskInfo taskInfo = volumeService.revertVirtualVolume(parameters.getVvolId(), parameters.getSnapshotVvolId());
        response.setReturn(taskInfo);
        LOGGER.info("End executing operation revertVirtualVolume");
        return response;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vmware.vim.vasa.v20.VasaServicePortType#queryCACertificates(*
     */
    public com.vmware.vim.vasa.v20.xsd.QueryCACertificatesResponse queryCACertificates()
            throws InvalidSession, VasaProviderBusy, StorageFault {
        String clientAddr = sslUtil.checkHttpRequest(true, true);
        LOGGER.info(clientAddr + ": Executing operation queryCACertificates");

        List<String> certs = secureConnectionService.queryCACertificates();

        com.vmware.vim.vasa.v20.xsd.QueryCACertificatesResponse response = new QueryCACertificatesResponse();
        response.getReturn().addAll(certs);

        LOGGER.info("End Executing operation queryCACertificates");
        return response;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vmware.vim.vasa.v20.VasaServicePortType#createVirtualVolume(com.
     * vmware.vim.vasa.v20.xsd.CreateVirtualVolume parameters )*
     */
    public com.vmware.vim.vasa.v20.xsd.CreateVirtualVolumeResponse createVirtualVolume(
            com.vmware.vim.vasa.v20.xsd.CreateVirtualVolume parameters)
            throws InactiveProvider, NotFound, InvalidArgument, VasaProviderBusy, InvalidSession, PermissionDenied,
            NotImplemented, OutOfResource, StorageFault, InvalidProfile {
        String clientAddr = sslUtil.checkHttpRequest(true, true);
        LOGGER.info(clientAddr + ": Executing operation createVirtualVolume");

        CreateVirtualVolumeResponse response = new CreateVirtualVolumeResponse();

        String sessionId = sslUtil.getCookie(VASA_SESSIONID_STR);
        VASAUtil.setSessionId(sessionId);
        TaskInfo result = volumeService.createVirtualVolume(parameters.getContainerId(), parameters.getVvolType(),
                parameters.getStorageProfile(), parameters.getSizeInMB(), parameters.getMetadata());
        response.setReturn(result);
        LOGGER.info("End Executing operation createVirtualVolume");
        return response;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vmware.vim.vasa.v20.VasaServicePortType#
     * unbindVirtualVolumeFromAllHost
     * (com.vmware.vim.vasa.v20.xsd.UnbindVirtualVolumeFromAllHost parameters )*
     */
    public com.vmware.vim.vasa.v20.xsd.UnbindVirtualVolumeFromAllHostResponse unbindVirtualVolumeFromAllHost(
            com.vmware.vim.vasa.v20.xsd.UnbindVirtualVolumeFromAllHost parameters) throws InactiveProvider, NotFound,
            InvalidArgument, VasaProviderBusy, InvalidSession, TooMany, NotImplemented, StorageFault {
        String clientAddr = sslUtil.checkHttpRequest(true, true);
        LOGGER.info(clientAddr + ": Executing operation unbindVirtualVolumeFromAllHost");

        if (null == parameters.getVvolId() || 0 == parameters.getVvolId().size()) {
            LOGGER.error("InvalidArgument/the vvolId is null or empty");
            throw FaultUtil.invalidArgument("the vvolId is null or empty");
        }

        UnbindVirtualVolumeFromAllHostResponse response = new UnbindVirtualVolumeFromAllHostResponse();
        List<BatchReturnStatus> result = volumeService.unbindVirtualVolumeFromAllHost(parameters.getVvolId());
        response.getReturn().addAll(result);
        LOGGER.info("End executing operation unbindVirtualVolumeFromAllHost");
        return response;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.vmware.vim.vasa.v20.VasaServicePortType#spaceStatsForStorageContainer
     * (com.vmware.vim.vasa.v20.xsd.SpaceStatsForStorageContainer parameters )*
     */
    public com.vmware.vim.vasa.v20.xsd.SpaceStatsForStorageContainerResponse spaceStatsForStorageContainer(
            com.vmware.vim.vasa.v20.xsd.SpaceStatsForStorageContainer parameters) throws InactiveProvider, NotFound,
            InvalidArgument, VasaProviderBusy, InvalidSession, NotImplemented, StorageFault {
        // verify valid SSL and VASA Sessions.
        String clientAddr = sslUtil.checkHttpRequest(true, true);
        LOGGER.info(clientAddr + ": Executing operation spaceStatsForStorageContainer");

        SpaceStatsForStorageContainerResponse response = new SpaceStatsForStorageContainerResponse();
        List<ContainerSpaceStats> result = volumeService.spaceStatsForStorageContainer(parameters.getContainerId(),
                parameters.getCapabilityProfileId());
        response.getReturn().addAll(result);
        LOGGER.info("End executing operation spaceStatsForStorageContainer");
        return response;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.vmware.vim.vasa.v20.VasaServicePortType#bindVirtualVolume(com.vmware
     * .vim.vasa.v20.xsd.BindVirtualVolume parameters )*
     */
    public com.vmware.vim.vasa.v20.xsd.BindVirtualVolumeResponse bindVirtualVolume(
            com.vmware.vim.vasa.v20.xsd.BindVirtualVolume parameters) throws InactiveProvider, InvalidArgument,
            VasaProviderBusy, InvalidSession, TooMany, PermissionDenied, NotImplemented, StorageFault {
        // verify valid SSL and VASA Sessions.
        String clientAddr = sslUtil.checkHttpRequest(true, true);
        LOGGER.info(clientAddr + ": Executing operation bindVirtualVolume");

        if (null == parameters.getVvolId() || 0 == parameters.getVvolId().size()) {
            LOGGER.error("InvalidArgument/invalid vvolId:" + VASAUtil.convertArrayToStr(parameters.getVvolId()));
            throw FaultUtil.invalidArgument("invalid vvolId:" + VASAUtil.convertArrayToStr(parameters.getVvolId()));
        }

        String sessionid = sslUtil.getCookie(VASA_SESSIONID_STR);
        VASAUtil.setSessionId(sessionid);
        BindVirtualVolumeResponse response = new BindVirtualVolumeResponse();
        List<BatchVirtualVolumeHandleResult> result = volumeService.bindVirtualVolume(sessionid,
                secureConnectionService.getUsageContext(), parameters.getVvolId(), parameters.getBindContext());
        response.getReturn().addAll(result);
        LOGGER.info("End executing operation bindVirtualVolume");
        return response;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vmware.vim.vasa.v20.VasaServicePortType#deleteVirtualVolume(com.
     * vmware.vim.vasa.v20.xsd.DeleteVirtualVolume parameters )*
     */
    public void deleteVirtualVolume(com.vmware.vim.vasa.v20.xsd.DeleteVirtualVolume parameters)
            throws InactiveProvider, NotFound, InvalidArgument, VasaProviderBusy, InvalidSession, PermissionDenied,
            NotImplemented, ResourceInUse, StorageFault {
        Date startDate = new Date();
        // verify valid SSL and VASA Sessions.
        String clientAddr = sslUtil.checkHttpRequest(true, true);
        LOGGER.info(clientAddr + ": Executing operation deleteVirtualVolume");

        if (!VASAUtil.checkVvolIdValid(parameters.getVvolId())) {
            LOGGER.error("InvalidArgument/Invalid vvol id:" + parameters.getVvolId());
            throw FaultUtil.invalidArgument("Invalid vvol id:" + parameters.getVvolId());
        }

        volumeService.deleteVirtualVolume(parameters.getVvolId());
        LOGGER.info("End executing operation deleteVirtualVolume");
        Date endDate = new Date();

        long timeTaken = endDate.getTime() - startDate.getTime();
        LOGGER.info("time_taken: |" + timeTaken + "|ms.");
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vmware.vim.vasa.v20.VasaServicePortType#queryCatalog(*
     */
    public com.vmware.vim.vasa.v20.xsd.QueryCatalogResponse queryCatalog() throws InvalidSession, StorageFault {
        // verify valid SSL and VASA Sessions.
        String clientAddr = sslUtil.checkHttpRequest(true, true);
        LOGGER.info(clientAddr + ": Executing operation queryCatalog");
        QueryCatalogResponse response = new QueryCatalogResponse();
        // run function
        response.getReturn().addAll(secureConnectionService.queryCatalog());
        LOGGER.info("End executing operation queryCatalog");
        return response;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vmware.vim.vasa.v20.VasaServicePortType#getNumberOfEntities(com.
     * vmware.vim.vasa.v20.xsd.GetNumberOfEntities parameters )*
     */
    public com.vmware.vim.vasa.v20.xsd.GetNumberOfEntitiesResponse getNumberOfEntities(
            com.vmware.vim.vasa.v20.xsd.GetNumberOfEntities parameters)
            throws InvalidSession, InvalidArgument, StorageFault {
        // verify valid SSL and VASA Sessions.
        String clientAddr = sslUtil.checkHttpRequest(true, true);
        LOGGER.info(clientAddr + ": Executing operation getNumberOfEntities");

        UsageContext uc = secureConnectionService.getUsageContext();
        GetNumberOfEntitiesResponse response = new GetNumberOfEntitiesResponse();
        if (Util.isEmpty(uc.getHostInitiator())) {
            LOGGER.error("usageContext is null.");
            response.setReturn(0);
            return response;
        }

        // run function
        int result = storageService.getNumberOfEntities(secureConnectionService.getHostInitiatorIds(),
                parameters.getEntityType(), secureConnectionService.getUsageContext());
        response.setReturn(result);
        LOGGER.info("End executing operation getNumberOfEntities");
        return response;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.vmware.vim.vasa.v20.VasaServicePortType#activateProvider(com.vmware
     * .vim.vasa.v20.xsd.ActivateProvider parameters )*
     */
    public void activateProvider(com.vmware.vim.vasa.v20.xsd.ActivateProvider parameters)
            throws InvalidSession, InvalidArgument, VasaProviderBusy, StorageFault {
        LOGGER.info("Executing operation activateProvider");
        try {
        } catch (java.lang.Exception ex) {
            // CodeDEX问题修改 ：FORTIFY.System_Information_Leak
            // wwX315527 2016/11/17
            // ex.printStackTrace();
            LOGGER.error("Exception during activateProvider:" + ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    /*
     * (non-Javadoc) 创建成功调用
     *
     * @see
     * com.vmware.vim.vasa.v20.VasaServicePortType#updateVirtualVolumeMetaData
     * (com.vmware.vim.vasa.v20.xsd.UpdateVirtualVolumeMetaData parameters )*
     */
    public void updateVirtualVolumeMetaData(com.vmware.vim.vasa.v20.xsd.UpdateVirtualVolumeMetaData parameters)
            throws InactiveProvider, NotFound, InvalidArgument, VasaProviderBusy, InvalidSession, PermissionDenied,
            NotImplemented, StorageFault {
        // verify valid SSL and VASA Sessions.
        String clientAddr = sslUtil.checkHttpRequest(true, true);
        LOGGER.info(clientAddr + ": Executing operation updateVirtualVolumeMetaData");

        if (!VASAUtil.checkVvolIdValid(parameters.getVvolId())) {
            LOGGER.error("InvalidArgument/invalid vvolId:" + parameters.getVvolId());
            throw FaultUtil.invalidArgument("invalid vvolId:" + parameters.getVvolId());
        }

        if (null == parameters.getKeyValuePair() || 0 == parameters.getKeyValuePair().size()) {
            LOGGER.error("InvalidArgument/invalid keyValuePair: null or empty");
            throw FaultUtil.invalidArgument("invalid keyValuePair: null or empty");
        }

        volumeService.updateVirtualVolumeMetaData(parameters.getVvolId(), parameters.getKeyValuePair());
        LOGGER.info("End executing operation updateVirtualVolumeMetaData");
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.vmware.vim.vasa.v20.VasaServicePortType#snapshotVirtualVolume(com
     * .vmware.vim.vasa.v20.xsd.SnapshotVirtualVolume parameters )*
     */
    public com.vmware.vim.vasa.v20.xsd.SnapshotVirtualVolumeResponse snapshotVirtualVolume(
            com.vmware.vim.vasa.v20.xsd.SnapshotVirtualVolume parameters)
            throws InactiveProvider, NotFound, InvalidArgument, VasaProviderBusy, NotSupported, InvalidSession, TooMany,
            PermissionDenied, Timeout, NotImplemented, ResourceInUse, StorageFault {
        // verify valid SSL and VASA Sessions.
        String clientAddr = sslUtil.checkHttpRequest(true, true);
        LOGGER.info(clientAddr + ": Executing operation snapshotVirtualVolume");

        if (parameters.getTimeoutMS() <= 0) {
            LOGGER.error("InvalidArgument/invalid timeoutMS:" + parameters.getTimeoutMS());
            throw FaultUtil.invalidArgument("invalid timeoutMS:" + parameters.getTimeoutMS());
        }

        if (null == parameters.getSnapshotInfo() || 0 == parameters.getSnapshotInfo().size()) {
            LOGGER.error("InvalidArgument/snapshotInfo is null or empty");
            throw FaultUtil.invalidArgument("snapshotInfo is null or empty");
        }

        SnapshotVirtualVolumeResponse response = new SnapshotVirtualVolumeResponse();
        List<BatchReturnStatus> result = volumeService.snapshotVirtualVolume(parameters.getSnapshotInfo(),
                parameters.getTimeoutMS());
        response.getReturn().addAll(result);
        LOGGER.info("End executing operation snapshotVirtualVolume");
        return response;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vmware.vim.vasa.v20.VasaServicePortType#cancelBindingChange(com.
     * vmware.vim.vasa.v20.xsd.CancelBindingChange parameters )*
     */
    public com.vmware.vim.vasa.v20.xsd.CancelBindingChangeResponse cancelBindingChange(
            com.vmware.vim.vasa.v20.xsd.CancelBindingChange parameters) throws InactiveProvider, NotFound,
            InvalidArgument, VasaProviderBusy, InvalidSession, NotImplemented, StorageFault {
        LOGGER.info("Executing operation cancelBindingChange");
        throw FaultUtil.notImplemented("VASA NOT SUPPORT THIS FUNCTION.	");
        /*
         * try { com.vmware.vim.vasa.v20.xsd.CancelBindingChangeResponse _return
         * = new CancelBindingChangeResponse(); return _return; } catch
         * (java.lang.Exception ex) { // CodeDEX问题修改
         * ：FORTIFY.System_Information_Leak // wwX315527 2016/11/17 //
         * ex.printStackTrace(); LOGGER.error(
         * "Exception during cancelBindingChange:" + ex.getMessage()); throw new
         * RuntimeException(ex); }
         */
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.vmware.vim.vasa.v20.VasaServicePortType#queryVirtualVolumeInfo(com
     * .vmware.vim.vasa.v20.xsd.QueryVirtualVolumeInfo parameters )*
     */
    public com.vmware.vim.vasa.v20.xsd.QueryVirtualVolumeInfoResponse queryVirtualVolumeInfo(
            com.vmware.vim.vasa.v20.xsd.QueryVirtualVolumeInfo parameters)
            throws InactiveProvider, VasaProviderBusy, InvalidSession, TooMany, NotImplemented, StorageFault {
        // verify valid SSL and VASA Sessions.
        String clientAddr = sslUtil.checkHttpRequest(true, true);
        LOGGER.info(clientAddr + ": Executing operation queryVirtualVolumeInfo");

        QueryVirtualVolumeInfoResponse response = new QueryVirtualVolumeInfoResponse();
        List<VirtualVolumeInfo> result = volumeService.queryVirtualVolumeInfo(parameters.getVvolId());
        response.getReturn().addAll(result);

        LOGGER.info("End executing operation queryVirtualVolumeInfo");
        return response;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vmware.vim.vasa.v20.VasaServicePortType#
     * queryDrsMigrationCapabilityForPerformanceEx
     * (com.vmware.vim.vasa.v20.xsd.QueryDrsMigrationCapabilityForPerformanceEx
     * parameters )*
     */
    public com.vmware.vim.vasa.v20.xsd.QueryDrsMigrationCapabilityForPerformanceExResponse queryDrsMigrationCapabilityForPerformanceEx(
            com.vmware.vim.vasa.v20.xsd.QueryDrsMigrationCapabilityForPerformanceEx parameters)
            throws InactiveProvider, InvalidArgument, VasaProviderBusy, InvalidSession, NotImplemented, StorageFault {
        String clientAddr = sslUtil.checkHttpRequest(true, true);
        LOGGER.info(clientAddr + ": Executing operation queryDrsMigrationCapabilityForPerformanceEx");
        return null;

    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.vmware.vim.vasa.v20.VasaServicePortType#unsharedBitmapVirtualVolume
     * (com.vmware.vim.vasa.v20.xsd.UnsharedBitmapVirtualVolume parameters )*
     */
    public com.vmware.vim.vasa.v20.xsd.UnsharedBitmapVirtualVolumeResponse unsharedBitmapVirtualVolume(
            com.vmware.vim.vasa.v20.xsd.UnsharedBitmapVirtualVolume parameters)
            throws InactiveProvider, NotFound, InvalidArgument, VasaProviderBusy, NotSupported, InvalidSession,
            IncompatibleVolume, NotImplemented, StorageFault {
        // verify valid SSL and VASA Sessions.
        String clientAddr = sslUtil.checkHttpRequest(true, true);
        LOGGER.info(clientAddr + ": Executing operation unsharedBitmapVirtualVolume");

        if (!VASAUtil.checkVvolIdValid(parameters.getVvolId())
                || !VASAUtil.checkVvolIdValid(parameters.getBaseVvolId())) {
            LOGGER.error("InvalidArgument/invalid vvolId:" + parameters.getVvolId() + " or invalid baseVvolId:"
                    + parameters.getBaseVvolId());
            throw FaultUtil.invalidArgument("invalid vvolId:" + parameters.getVvolId() + " or invalid baseVvolId:"
                    + parameters.getBaseVvolId());
        }

        if (parameters.getSegmentStartOffsetBytes() < 0 || parameters.getSegmentLengthBytes() < 0) {
            LOGGER.error("InvalidArgument/invalid segmentStartOffsetBytes:" + parameters.getSegmentStartOffsetBytes()
                    + " or invalid segmentLengthBytes:" + parameters.getSegmentLengthBytes());
            throw FaultUtil.invalidArgument("invalid segmentStartOffsetBytes:" + parameters.getSegmentStartOffsetBytes()
                    + " or invalid segmentLengthBytes:" + parameters.getSegmentLengthBytes());
        }

        if (parameters.getChunkSizeBytes() <= 0) {
            LOGGER.error("InvalidArgument/invalid chunkSizeBytes:" + parameters.getChunkSizeBytes());
            throw FaultUtil.invalidArgument("invalid chunkSizeBytes:" + parameters.getChunkSizeBytes());
        }

        UnsharedBitmapVirtualVolumeResponse response = new UnsharedBitmapVirtualVolumeResponse();
        VirtualVolumeBitmapResult result = volumeService.unsharedBitmapVirtualVolume(parameters.getVvolId(),
                parameters.getBaseVvolId(), parameters.getSegmentStartOffsetBytes(), parameters.getSegmentLengthBytes(),
                parameters.getChunkSizeBytes());
        response.setReturn(result);
        LOGGER.info("End executing operation unsharedBitmapVirtualVolume");
        return response;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vmware.vim.vasa.v20.VasaServicePortType#
     * queryAssociatedStatisticsForEntity
     * (com.vmware.vim.vasa.v20.xsd.QueryAssociatedStatisticsForEntity
     * parameters )*
     */
    public com.vmware.vim.vasa.v20.xsd.QueryAssociatedStatisticsForEntityResponse queryAssociatedStatisticsForEntity(
            com.vmware.vim.vasa.v20.xsd.QueryAssociatedStatisticsForEntity parameters)
            throws InactiveProvider, InvalidArgument, InvalidStatisticsContext, VasaProviderBusy, InvalidSession,
            TooMany, NotImplemented, StorageFault {
        String clientAddr = sslUtil.checkHttpRequest(true, true);
        LOGGER.info(clientAddr + ": Executing operation queryAssociatedStatisticsForEntity");
        try {
            com.vmware.vim.vasa.v20.xsd.QueryAssociatedStatisticsForEntityResponse _return = null;
            return _return;
        } catch (java.lang.Exception ex) {
            // CodeDEX问题修改 ：FORTIFY.System_Information_Leak
            // wwX315527 2016/11/17
            // ex.printStackTrace();
            LOGGER.error("Exception during queryAssociatedStatisticsForEntity:" + ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.vmware.vim.vasa.v20.VasaServicePortType#getCurrentTask(com.vmware
     * .vim.vasa.v20.xsd.GetCurrentTask parameters )*
     */
    public com.vmware.vim.vasa.v20.xsd.GetCurrentTaskResponse getCurrentTask(
            com.vmware.vim.vasa.v20.xsd.GetCurrentTask parameters) throws InactiveProvider, NotFound, InvalidArgument,
            VasaProviderBusy, InvalidSession, NotImplemented, StorageFault {
        // verify valid SSL and VASA Sessions.
        String clientAddr = sslUtil.checkHttpRequest(true, true);
        LOGGER.info(clientAddr + ": Executing operation getCurrentTask");

        VASAUtil.checkArrayIdValidAndExist(parameters.getArrayId());

        GetCurrentTaskResponse response = new GetCurrentTaskResponse();
        List<TaskInfo> result = volumeService.getCurrentTask(parameters.getArrayId());
        response.getReturn().addAll(result);
        LOGGER.info("End executing operation getCurrentTask");
        return response;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vmware.vim.vasa.v20.VasaServicePortType#queryStorageCapabilities
     * (com.vmware.vim.vasa.v20.xsd.QueryStorageCapabilities parameters )*
     */
    public com.vmware.vim.vasa.v20.xsd.QueryStorageCapabilitiesResponse queryStorageCapabilities(
            com.vmware.vim.vasa.v20.xsd.QueryStorageCapabilities parameters)
            throws InvalidSession, InvalidArgument, NotImplemented, StorageFault {
        // verify valid SSL and VASA Sessions.
        String clientAddr = sslUtil.checkHttpRequest(true, true);
        LOGGER.info(clientAddr + ": Executing operation queryStorageCapabilities");

        // verify arguments
        // Util.allUniqueIdsAreValid(capId, true);
        String[] capId = ListUtil.list2ArrayString(parameters.getCapabilityUniqueId());
        VASAUtil.checkIsCapabilityIdInvalid(capId);
        QueryStorageCapabilitiesResponse response = new QueryStorageCapabilitiesResponse();
        // run function
        List<DStorageCapability> caps = storageService.queryStorageCapabilities(capId);
        response.getReturn().addAll(VasaServicePortTypeConvert.queryStorageCapabilitiesModal2Soap(caps));
        LOGGER.info("End executing operation queryStorageCapabilities");
        return response;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.vmware.vim.vasa.v20.VasaServicePortType#queryArrays(com.vmware.vim
     * .vasa.v20.xsd.QueryArrays parameters )*
     */
    public com.vmware.vim.vasa.v20.xsd.QueryArraysResponse queryArrays(
            com.vmware.vim.vasa.v20.xsd.QueryArrays parameters) throws InvalidSession, InvalidArgument, StorageFault {
        // verify valid SSL and VASA Sessions.
        String clientAddr = sslUtil.checkHttpRequest(true, true);
        LOGGER.info(clientAddr + ": Executing operation queryArrays");

        // run function
        QueryArraysResponse response = new QueryArraysResponse();

        List<DArray> result = storageService.queryArrays(secureConnectionService.getUsageContext(),
                ListUtil.list2ArrayString(parameters.getArrayUniqueId()));

        List<StorageArray> storageArrays = response.getReturn();

        for (DArray dArray : result) {
            storageArrays.add(VasaServicePortTypeConvert.queryArraysModal2Soap(dArray));
        }
        LOGGER.info("End executing operation queryArrays");
        return response;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vmware.vim.vasa.v20.VasaServicePortType#
     * registerCASignedProviderCertificate
     * (com.vmware.vim.vasa.v20.xsd.RegisterCASignedProviderCertificate
     * parameters )*
     */
    public void registerCASignedProviderCertificate(
            com.vmware.vim.vasa.v20.xsd.RegisterCASignedProviderCertificate parameters)
            throws VasaProviderBusy, InvalidSession, PermissionDenied, InvalidCertificate, StorageFault {
        // Mandatory function
        LOGGER.info("Executing operation registerCASignedProviderCertificate function.");
        String clientAddr = this.sslUtil.checkHttpRequestThrowInvalidCertificate(false, false);
        LOGGER.info(clientAddr + ": Executing operation registerCASignedProviderCertificate");

        try {
            secureConnectionService.registerVASACertificate(null, null, parameters.getProviderCert());
        } catch (InvalidLogin e) {
            throw FaultUtil.invalidCertificate();
        }
        LOGGER.info("End executing operation registerCASignedProviderCertificate");
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vmware.vim.vasa.v20.VasaServicePortType#requestCSR(*
     */
    public com.vmware.vim.vasa.v20.xsd.RequestCSRResponse requestCSR()
            throws InvalidSession, PermissionDenied, VasaProviderBusy, StorageFault {
        String clientAddr = sslUtil.checkHttpRequest(false, false);
        LOGGER.info(clientAddr + ": Executing operation requestCSR");
        LOGGER.info("End executing operation requestCSR");
        return secureConnectionService.requestCSR();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.vmware.vim.vasa.v20.VasaServicePortType#cloneVirtualVolume(com.vmware
     * .vim.vasa.v20.xsd.CloneVirtualVolume parameters )*
     */
    public com.vmware.vim.vasa.v20.xsd.CloneVirtualVolumeResponse cloneVirtualVolume(
            com.vmware.vim.vasa.v20.xsd.CloneVirtualVolume parameters)
            throws InactiveProvider, NotFound, InvalidArgument, VasaProviderBusy, NotSupported, InvalidSession,
            PermissionDenied, NotImplemented, ResourceInUse, OutOfResource, StorageFault, InvalidProfile {
        // verify valid SSL and VASA Sessions.
        String clientAddr = sslUtil.checkHttpRequest(true, true);
        LOGGER.info(clientAddr + ": Executing operation cloneVirtualVolume");

        if (!VASAUtil.checkVvolIdValid(parameters.getVvolId())) {
            LOGGER.error("InvalidArgument/invalid vvolId:" + parameters.getVvolId());
            throw FaultUtil.invalidArgument("invalid vvolId:" + parameters.getVvolId());
        }

        if (null != parameters.getNewContainerId() && !VASAUtil.checkContainerIdValid(parameters.getNewContainerId())) {
            LOGGER.error("InvalidArgument/invalid containerId:" + parameters.getNewContainerId());
            throw FaultUtil.invalidArgument("invalid containerId:" + parameters.getNewContainerId());
        }

        if (null != parameters.getNewContainerId()) {
            VASAUtil.checkContainerIdExist(parameters.getNewContainerId());
        }

        if (!VASAUtil.checkProfileNull(parameters.getNewProfile())) {
            // 校验是否包含id是null或者""的capability,解决updStorProForVVol.Neg007,
            // 应该把是否支持也校验到位。
            // 校验是否包含id是null或者""的namespace,解决updStorProForVVol.Neg008，
            // 应该把是否支持也校验到位。
            List<SubProfile> subProfiles = parameters.getNewProfile().getConstraints().getSubProfiles();
            for (SubProfile subProfile : subProfiles) {
                List<CapabilityInstance> capbility = subProfile.getCapability();
                for (CapabilityInstance capabilityInstance : capbility) {
                    if (VASAUtil.checkCapabilityNull(capabilityInstance)) {
                        LOGGER.error("InvalidProfile/invalid profile");
                        throw FaultUtil.invalidProfile();
                    }
                }
            }
        }

        CloneVirtualVolumeResponse response = new CloneVirtualVolumeResponse();
        TaskInfo result = volumeService.cloneVirtualVolume(parameters.getVvolId(), parameters.getNewContainerId(),
                parameters.getNewProfile(), parameters.getMetadata());
        response.setReturn(result);
        LOGGER.info("End executing operation cloneVirtualVolume");
        return response;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.vmware.vim.vasa.v20.VasaServicePortType#fastCloneVirtualVolume(com
     * .vmware.vim.vasa.v20.xsd.FastCloneVirtualVolume parameters )*
     */
    public com.vmware.vim.vasa.v20.xsd.FastCloneVirtualVolumeResponse fastCloneVirtualVolume(
            com.vmware.vim.vasa.v20.xsd.FastCloneVirtualVolume parameters) throws InactiveProvider, NotFound,
            InvalidArgument, VasaProviderBusy, NotSupported, InvalidSession, SnapshotTooMany, PermissionDenied,
            NotImplemented, ResourceInUse, OutOfResource, StorageFault, InvalidProfile {
        // verify valid SSL and VASA Sessions.
        String clientAddr = sslUtil.checkHttpRequest(true, true);
        LOGGER.info(clientAddr + ": Executing operation fastCloneVirtualVolume");

        if (!VASAUtil.checkVvolIdValid(parameters.getVvolId())) {
            LOGGER.error("InvalidArgument/invalid vvolId:" + parameters.getVvolId());
            throw FaultUtil.invalidArgument("invalid vvolId:" + parameters.getVvolId());
        }

        FastCloneVirtualVolumeResponse response = new FastCloneVirtualVolumeResponse();
        TaskInfo result = volumeService.fastCloneVirtualVolume(parameters.getVvolId(), parameters.getNewProfile(),
                parameters.getMetadata());
        response.setReturn(result);
        LOGGER.info("End executing operation fastCloneVirtualVolume");
        return response;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.vmware.vim.vasa.v20.VasaServicePortType#queryStorageContainer(com
     * .vmware.vim.vasa.v20.xsd.QueryStorageContainer parameters )*
     */
    public com.vmware.vim.vasa.v20.xsd.QueryStorageContainerResponse queryStorageContainer(
            com.vmware.vim.vasa.v20.xsd.QueryStorageContainer parameters)
            throws InvalidSession, VasaProviderBusy, NotImplemented, StorageFault {
        // verify valid SSL and VASA Sessions.
        String clientAddr = sslUtil.checkHttpRequest(true, true);
        LOGGER.info(clientAddr + ": Executing operation queryStorageContainer");

        String[] containerIds = ListUtil.list2ArrayString(parameters.getContainerId());
        QueryStorageContainerResponse response = new QueryStorageContainerResponse();

        List<StorageContainer> containers = volumeService.queryStorageContainer(containerIds);
        response.getReturn().addAll(containers);

        LOGGER.info("End Executing operation queryStorageContainer");
        return response;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.vmware.vim.vasa.v20.VasaServicePortType#setStorageContainerContext
     * (com.vmware.vim.vasa.v20.xsd.SetStorageContainerContext parameters )*
     */
    public com.vmware.vim.vasa.v20.xsd.SetStorageContainerContextResponse setStorageContainerContext(
            com.vmware.vim.vasa.v20.xsd.SetStorageContainerContext parameters) throws InactiveProvider, NotFound,
            InvalidArgument, VasaProviderBusy, InvalidSession, NotImplemented, StorageFault {
        String clientAddr = sslUtil.checkHttpRequest(true, true);
        LOGGER.info(clientAddr + ": Executing operation setStorageContainerContext");

        throw FaultUtil.notImplemented("not implemented");
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.vmware.vim.vasa.v20.VasaServicePortType#spaceStatsForVirtualVolume
     * (com.vmware.vim.vasa.v20.xsd.SpaceStatsForVirtualVolume parameters )*
     */
    public com.vmware.vim.vasa.v20.xsd.SpaceStatsForVirtualVolumeResponse spaceStatsForVirtualVolume(
            com.vmware.vim.vasa.v20.xsd.SpaceStatsForVirtualVolume parameters)
            throws InactiveProvider, VasaProviderBusy, InvalidSession, TooMany, NotImplemented, StorageFault {
        // verify valid SSL and VASA Sessions.
        String clientAddr = sslUtil.checkHttpRequest(true, true);
        LOGGER.info(clientAddr + ": Executing operation spaceStatsForVirtualVolume");

        SpaceStatsForVirtualVolumeResponse response = new SpaceStatsForVirtualVolumeResponse();
        List<SpaceStats> result = volumeService.spaceStatsForVirtualVolume(parameters.getVvolId());
        response.getReturn().addAll(result);
        LOGGER.info("End executing operation spaceStatsForVirtualVolume");
        return response;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.vmware.vim.vasa.v20.VasaServicePortType#registerCACertificatesAndCRLs
     * (com.vmware.vim.vasa.v20.xsd.RegisterCACertificatesAndCRLs parameters )*
     */
    public com.vmware.vim.vasa.v20.xsd.RegisterCACertificatesAndCRLsResponse registerCACertificatesAndCRLs(
            com.vmware.vim.vasa.v20.xsd.RegisterCACertificatesAndCRLs parameters)
            throws VasaProviderBusy, InvalidSession, InvalidLogin, InvalidCertificate, StorageFault {
        // Mandatory function
        LOGGER.info("Executing operation registerCACertificatesAndCRLs function.");
        String clientAddr = this.sslUtil.checkHttpRequestThrowInvalidCertificate(false, false);
        LOGGER.info(clientAddr + ": Executing operation registerCACertificatesAndCRLs");
        // run function
        VasaProviderInfo vpInfo = null;
        List<String> caRootCerts = parameters.getCaRootCert();
        for (int i = 0; i < caRootCerts.size(); ++i) {
            String caRoot = caRootCerts.get(i);
            if (caRoot == null) {
                continue;
            }
            vpInfo = secureConnectionService.registerVASACertificate(parameters.getProviderUserName(),
                    parameters.getProviderPassword(), caRoot);
        }

        VasaProperty propertyByName = vasaPropertyService.getPropertyByName(VasaProperty.RETAIN_VP_CERTIFICATE_KEY);
        LOGGER.info("registerCACertificatesAndCRLs/propertyByName=" + propertyByName);
        if (null != propertyByName) {
            vpInfo.setRetainVasaProviderCertificate(Boolean.valueOf(propertyByName.getValue()));
        }

        // String configItem =
        // secureConnectionService.loadConfigFile(RETAIN_VP_CERTIFICATE_FLAG_CONF).trim();
        // boolean vpflag = Boolean.valueOf(configItem.split("=")[1]);
        // if (vpInfo != null) {
        // vpInfo.setRetainVasaProviderCertificate(vpflag);
        // }

        com.vmware.vim.vasa.v20.xsd.RegisterCACertificatesAndCRLsResponse response = new RegisterCACertificatesAndCRLsResponse();
        response.setReturn(vpInfo);
        LOGGER.info("End executing operation registerCACertificatesAndCRLs");
        return response;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.vmware.vim.vasa.v20.VasaServicePortType#queryStoragePorts(com.vmware
     * .vim.vasa.v20.xsd.QueryStoragePorts parameters )*
     */
    public com.vmware.vim.vasa.v20.xsd.QueryStoragePortsResponse queryStoragePorts(
            com.vmware.vim.vasa.v20.xsd.QueryStoragePorts parameters)
            throws InvalidSession, InvalidArgument, NotImplemented, StorageFault {
        // verify valid SSL and VASA Sessions.
        String clientAddr = sslUtil.checkHttpRequest(true, true);
        LOGGER.info(clientAddr + ": Executing operation queryStoragePorts");

        // verify arguments
        // Util.allUniqueIdsAreValid(processorId, true);
        String[] portIds = ListUtil.list2ArrayString(parameters.getPortUniqueId());
        VASAUtil.checkIsPortIdValid(portIds);
        QueryStoragePortsResponse response = new QueryStoragePortsResponse();
        // run function
        List<DPort> ports = storageService.queryStoragePorts(secureConnectionService.getHostInitiatorIds(), portIds,
                secureConnectionService.getUsageContext());

        response.getReturn().addAll(VasaServicePortTypeConvert.queryStoragePortsModal2Soap(ports));
        LOGGER.info("End executing operation queryStoragePorts");
        return response;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vmware.vim.vasa.v20.VasaServicePortType#unbindVirtualVolume(com.
     * vmware.vim.vasa.v20.xsd.UnbindVirtualVolume parameters )*
     */
    public com.vmware.vim.vasa.v20.xsd.UnbindVirtualVolumeResponse unbindVirtualVolume(
            com.vmware.vim.vasa.v20.xsd.UnbindVirtualVolume parameters) throws InactiveProvider, NotFound,
            InvalidArgument, VasaProviderBusy, InvalidSession, TooMany, NotImplemented, StorageFault {
        // verify valid SSL and VASA Sessions.
        String clientAddr = sslUtil.checkHttpRequest(true, true);
        LOGGER.info(clientAddr + ": Executing operation unbindVirtualVolume");

        if (null == parameters.getVvolHandle() || 0 == parameters.getVvolHandle().size()) {
            LOGGER.error("InvalidArgument/invalid vvolHandle: null or empty");
            throw FaultUtil.invalidArgument("invalid vvolHandle: null or empty");
        }

        UnbindVirtualVolumeResponse response = new UnbindVirtualVolumeResponse();
        List<BatchReturnStatus> result = volumeService.unbindVirtualVolume(parameters.getVvolHandle(),
                parameters.getUnbindContext(), secureConnectionService.getUsageContext());

        response.getReturn().addAll(result);
        LOGGER.info("End executing operation unbindVirtualVolume");
        return response;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.vmware.vim.vasa.v20.VasaServicePortType#queryStorageLuns(com.vmware
     * .vim.vasa.v20.xsd.QueryStorageLuns parameters )*
     */
    public com.vmware.vim.vasa.v20.xsd.QueryStorageLunsResponse queryStorageLuns(
            com.vmware.vim.vasa.v20.xsd.QueryStorageLuns parameters)
            throws InvalidSession, InvalidArgument, NotImplemented, StorageFault {
        // verify valid SSL and VASA Sessions.
        String clientAddr = sslUtil.checkHttpRequest(true, true);
        LOGGER.info(clientAddr + ": Executing operation queryStorageLuns");

        // verify arguments
        // Util.allUniqueIdsAreValid(lunId, false);
        String[] lunId = ListUtil.list2ArrayString(parameters.getLunUniqueId());
        VASAUtil.checkIsLunIdInvalid(lunId, false);

        QueryStorageLunsResponse response = new QueryStorageLunsResponse();

        List<DLun> dluns = storageService.queryStorageLuns(secureConnectionService.getHostInitiatorIds(), lunId);

        DataUtil.getInstance().addLunsToUsageContext(dluns, secureConnectionService.getUsageContext().getVcGuid());

        List<StorageLun> storageluns = response.getReturn();
        storageluns.addAll(VasaServicePortTypeConvert.queryStorageLunsModal2Soap(dluns));
        LOGGER.info("End executing operation queryStorageLuns");
        return response;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vmware.vim.vasa.v20.VasaServicePortType#
     * unbindAllVirtualVolumesFromHost
     * (com.vmware.vim.vasa.v20.xsd.UnbindAllVirtualVolumesFromHost parameters
     * )*
     */
    public void unbindAllVirtualVolumesFromHost(com.vmware.vim.vasa.v20.xsd.UnbindAllVirtualVolumesFromHost parameters)
            throws InactiveProvider, NotFound, InvalidArgument, VasaProviderBusy, InvalidSession, NotImplemented,
            StorageFault {
        // verify valid SSL and VASA Sessions.
        String clientAddr = sslUtil.checkHttpRequest(true, true);
        LOGGER.info(clientAddr + ": Executing operation unbindAllVirtualVolumesFromHost");

        volumeService.unbindAllVirtualVolumesFromHost(secureConnectionService.getUsageContext());
        LOGGER.info("End executing operation unbindAllVirtualVolumesFromHost");
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vmware.vim.vasa.v20.VasaServicePortType#getAlarms(com.vmware.vim
     * .vasa.v20.xsd.GetAlarms parameters )*
     */
    public com.vmware.vim.vasa.v20.xsd.GetAlarmsResponse getAlarms(com.vmware.vim.vasa.v20.xsd.GetAlarms parameters)
            throws InvalidSession, InvalidArgument, StorageFault, LostAlarm {
        // verify valid SSL and VASA Sessions.
        String clientAddr = sslUtil.checkHttpRequest(true, true);
        LOGGER.info(clientAddr + ": Executing operation getAlarms");
        long lastAlarmId = parameters.getLastAlarmId();
        // verify argument
        Util.eventOrAlarmIdIsValid(lastAlarmId);

        String sessionId = sslUtil.getCookie(VASA_SESSIONID_STR);
        // run function

        StorageAlarm[] alarms = alarmService.getAlarms(sessionId, secureConnectionService.getUsageContext(),
                lastAlarmId);
        LOGGER.info(": getAlarms :" + alarms.length);

        for (StorageAlarm event : alarms) {
            LOGGER.info("**********eventId:" + event.getAlarmId() + ", eventType:" + event.getAlarmType()
                    + ", eventObjType:" + event.getObjectType() + ", messageId:" + event.getMessageId() + ", arrayId:"
                    + event.getObjectId());
        }

        VASAUtil.resetAlarmParams(alarms);
        GetAlarmsResponse response = new GetAlarmsResponse();
        response.getReturn().addAll(ListUtil.array2List(alarms));
        LOGGER.info("End executing operation getAlarms");
        return response;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.vmware.vim.vasa.v20.VasaServicePortType#queryStorageContainerForArray
     * (com.vmware.vim.vasa.v20.xsd.QueryStorageContainerForArray parameters )*
     */
    public com.vmware.vim.vasa.v20.xsd.QueryStorageContainerForArrayResponse queryStorageContainerForArray(
            com.vmware.vim.vasa.v20.xsd.QueryStorageContainerForArray parameters) throws InactiveProvider, NotFound,
            InvalidArgument, VasaProviderBusy, InvalidSession, NotImplemented, StorageFault {
        // verify valid SSL and VASA Sessions.
        String clientAddr = sslUtil.checkHttpRequest(true, true);
        LOGGER.info(clientAddr + ": Executing operation queryStorageContainerForArray");

        VASAUtil.checkArrayIdValidAndExist(parameters.getArrayId());
        String arrayId = parameters.getArrayId().split(":")[1];

        QueryStorageContainerForArrayResponse response = new QueryStorageContainerForArrayResponse();

        List<String> containerIds = volumeService.queryStorageContainerForArray(arrayId);

        response.getReturn().addAll(containerIds);
        LOGGER.info("End executing operation queryStorageContainerForArray");
        return response;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.vmware.vim.vasa.v20.VasaServicePortType#setContext(com.vmware.vim
     * .vasa.v20.xsd.SetContext parameters )*
     */
    public com.vmware.vim.vasa.v20.xsd.SetContextResponse setContext(com.vmware.vim.vasa.v20.xsd.SetContext parameters)
            throws InvalidSession, InvalidArgument, StorageFault {
        String clientAddr = sslUtil.checkHttpRequest(false, false);
        LOGGER.info(clientAddr + ": Executing operation setContext");
        LOGGER.info("SetContext request:\n" + JaxbUtil.convertToXml(parameters));
        // run function
        /** neg018 修改 begin **/
        UsageContext usageContext = parameters.getUsageContext();
        if (null == usageContext) {
            LOGGER.error("InvalidArgument/usageContext is null");
            throw FaultUtil.invalidArgument("usageContext is null");
        }
        /** neg018 修改 end **/

        VasaProviderInfo vpInfo = secureConnectionService.setContext(usageContext);
        com.vmware.vim.vasa.v20.xsd.SetContextResponse response = new SetContextResponse();
        response.setReturn(vpInfo);

        LOGGER.info("SetContextResponse:\n" + JaxbUtil.convertToXml(response));
        LOGGER.info("End executing operation setContext");
        return response;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vmware.vim.vasa.v20.VasaServicePortType#queryStorageFileSystems(
     * com.vmware.vim.vasa.v20.xsd.QueryStorageFileSystems parameters )*
     */
    public com.vmware.vim.vasa.v20.xsd.QueryStorageFileSystemsResponse queryStorageFileSystems(
            com.vmware.vim.vasa.v20.xsd.QueryStorageFileSystems parameters)
            throws InvalidSession, InvalidArgument, NotImplemented, StorageFault {
        // verify valid SSL and VASA Sessions.
        String clientAddr = sslUtil.checkHttpRequest(true, true);
        LOGGER.info(clientAddr + ": Executing operation queryStorageFileSystems. isSupportNFS" + isSupportNFS);
        if (isSupportNFS) {
            String[] fsId = ListUtil.list2ArrayString(parameters.getFsUniqueId());
            VASAUtil.checkIsFileSystemIdInvalid(fsId, false);
            // run function
            QueryStorageFileSystemsResponse response = new QueryStorageFileSystemsResponse();

            List<DFileSystem> fileSystems = storageService
                    .queryStorageFileSystems(secureConnectionService.getUsageContext().getMountPoint(), fsId);

            DataUtil.getInstance().addFileSystemsToUsageContext(
                    fileSystems.toArray(new DFileSystem[fileSystems.size()]),
                    VASAUtil.getUcUUID(secureConnectionService.getUsageContext()));

            response.getReturn().addAll(VasaServicePortTypeConvert.queryStorageFileSystemsModal2Soap1(fileSystems));
            LOGGER.info("End executing operation queryStorageFileSystems");
            return response;
        } else {
            isSupportNFS = false;
            LOGGER.error("NotImplemented/This device not implemented FileSystemProfile");
            throw FaultUtil.notImplemented("This device not implemented FileSystemProfile");
        }

    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.vmware.vim.vasa.v20.VasaServicePortType#queryComplianceResult(com
     * .vmware.vim.vasa.v20.xsd.QueryComplianceResult parameters )*
     */
    public com.vmware.vim.vasa.v20.xsd.QueryComplianceResultResponse queryComplianceResult(
            com.vmware.vim.vasa.v20.xsd.QueryComplianceResult parameters)
            throws InactiveProvider, NotFound, InvalidArgument, VasaProviderBusy, InvalidSession, StorageFault {
        // verify valid SSL and VASA Sessions.
        String clientAddr = sslUtil.checkHttpRequest(true, true);
        LOGGER.info(clientAddr + ": Executing operation QueryComplianceResult");

        List<ComplianceSubject> subjects = parameters.getCompliance();

        QueryComplianceResultResponse response = new QueryComplianceResultResponse();

        List<ComplianceResult> result = sPBMService.queryComplianceResult(subjects);
        response.getReturn().addAll(result);
        LOGGER.info("End executing operation QueryComplianceResult");
        return response;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vmware.vim.vasa.v20.VasaServicePortType#
     * updateStorageProfileForVirtualVolume
     * (com.vmware.vim.vasa.v20.xsd.UpdateStorageProfileForVirtualVolume
     * parameters )*
     */
    public com.vmware.vim.vasa.v20.xsd.UpdateStorageProfileForVirtualVolumeResponse updateStorageProfileForVirtualVolume(
            com.vmware.vim.vasa.v20.xsd.UpdateStorageProfileForVirtualVolume parameters)
            throws InactiveProvider, NotFound, VasaProviderBusy, NotSupported, InvalidSession, PermissionDenied,
            ResourceInUse, OutOfResource, StorageFault, InvalidProfile {
        // verify valid SSL and VASA Sessions.
        String clientAddr = sslUtil.checkHttpRequest(true, true);
        LOGGER.info(clientAddr + ": Executing operation updateStorageProfileForVirtualVolume");

        UpdateStorageProfileForVirtualVolumeResponse response = new UpdateStorageProfileForVirtualVolumeResponse();

        TaskInfo result = sPBMService.updateStorageProfileForVirtualVolume(parameters.getVvolId(),
                parameters.getNewStorageProfile());
        response.setReturn(result);
        LOGGER.info("End executing operation updateStorageProfileForVirtualVolume");
        return response;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.vmware.vim.vasa.v20.VasaServicePortType#allocatedBitmapVirtualVolume
     * (com.vmware.vim.vasa.v20.xsd.AllocatedBitmapVirtualVolume parameters )*
     */
    public com.vmware.vim.vasa.v20.xsd.AllocatedBitmapVirtualVolumeResponse allocatedBitmapVirtualVolume(
            com.vmware.vim.vasa.v20.xsd.AllocatedBitmapVirtualVolume parameters) throws InactiveProvider, NotFound,
            InvalidArgument, VasaProviderBusy, NotSupported, InvalidSession, NotImplemented, StorageFault {
        // verify valid SSL and VASA Sessions.
        String clientAddr = sslUtil.checkHttpRequest(true, true);
        LOGGER.info(clientAddr + ": Executing operation allocatedBitmapVirtualVolume");

        if (!VASAUtil.checkVvolIdValid(parameters.getVvolId())) {
            LOGGER.error("InvalidArgument/invalid vvolId:" + parameters.getVvolId());
            throw FaultUtil.invalidArgument("invalid vvolId:" + parameters.getVvolId());

        }

        if (parameters.getSegmentStartOffsetBytes() < 0 || parameters.getSegmentLengthBytes() < 0) {
            LOGGER.error("InvalidArgument/invalid segmentStartOffsetBytes:" + parameters.getSegmentStartOffsetBytes()
                    + " or invalid segmentLengthBytes:" + parameters.getSegmentLengthBytes());
            throw FaultUtil.invalidArgument("invalid segmentStartOffsetBytes:" + parameters.getSegmentStartOffsetBytes()
                    + " or invalid segmentLengthBytes:" + parameters.getSegmentLengthBytes());
        }

        if (parameters.getChunkSizeBytes() <= 0) {
            LOGGER.error("InvalidArgument/invalid chunkSizeBytes:" + parameters.getChunkSizeBytes());
            throw FaultUtil.invalidArgument("invalid chunkSizeBytes:" + parameters.getChunkSizeBytes());
        }

        AllocatedBitmapVirtualVolumeResponse response = new AllocatedBitmapVirtualVolumeResponse();
        VirtualVolumeBitmapResult result = volumeService.allocatedBitmapVirtualVolume(parameters.getVvolId(),
                parameters.getSegmentStartOffsetBytes(), parameters.getSegmentLengthBytes(),
                parameters.getChunkSizeBytes());
        response.setReturn(result);
        LOGGER.info("End executing operation allocatedBitmapVirtualVolume");
        return response;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vmware.vim.vasa.v20.VasaServicePortType#getTaskUpdate(com.vmware
     * .vim.vasa.v20.xsd.GetTaskUpdate parameters )*
     */
    public com.vmware.vim.vasa.v20.xsd.GetTaskUpdateResponse getTaskUpdate(
            com.vmware.vim.vasa.v20.xsd.GetTaskUpdate parameters) throws InactiveProvider, NotFound, InvalidArgument,
            VasaProviderBusy, InvalidSession, NotImplemented, StorageFault {
        // verify valid SSL and VASA Sessions.
        String clientAddr = sslUtil.checkHttpRequest(true, true);
        LOGGER.info(clientAddr + ": Executing operation getTaskUpdate");

        GetTaskUpdateResponse response = new GetTaskUpdateResponse();
        TaskInfo result = volumeService.getTaskUpdate(parameters.getTaskId());
        response.setReturn(result);
        LOGGER.info("End executing operation getTaskUpdate");
        return response;
    }

}
