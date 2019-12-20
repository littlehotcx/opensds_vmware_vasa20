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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Stack;


import org.opensds.vasa.base.common.VasaConstant;
import org.opensds.vasa.base.common.VasaSrcTypeConstant;
import org.opensds.vasa.common.MagicNumber;
import org.opensds.vasa.domain.model.StorageModel;
import org.opensds.vasa.domain.model.bean.S2DLun;
import org.opensds.platform.common.SDKErrorCode;
import org.opensds.platform.common.SDKResult;
import org.opensds.platform.common.exception.SDKException;
import org.opensds.platform.common.utils.ApplicationContextUtil;
import org.opensds.platform.nemgr.itf.IDeviceManager;
import org.opensds.vasa.vasa.VasaNasArrayService;
import org.opensds.vasa.vasa.convert.VASAUtilDJConvert;
import org.opensds.vasa.vasa.db.model.EsxHostIp;
import org.opensds.vasa.vasa.db.model.FileSystemTable;
import org.opensds.vasa.vasa.db.model.FilesystemShareTable;
import org.opensds.vasa.vasa.db.model.NVirtualVolume;
import org.opensds.vasa.vasa.db.model.NVvolMetadata;
import org.opensds.vasa.vasa.db.model.ShareClientTable;
import org.opensds.vasa.vasa.db.model.StorageInfo;
import org.opensds.vasa.vasa.db.model.VvolPath;
import org.opensds.vasa.vasa.db.service.EsxHostIpService;
import org.opensds.vasa.vasa.db.service.SnapshotCloneRecordService;
import org.opensds.vasa.vasa.db.service.SnapshotCloneRecordService.Result;
import org.opensds.vasa.vasa.db.service.StorageManagerService;
import org.opensds.vasa.vasa.db.service.VirtualVolumeService;
import org.opensds.vasa.vasa.db.service.VvolMetadataService;
import org.opensds.vasa.vasa.db.service.VvolPathService;
import org.opensds.vasa.vasa.rest.bean.DeviceTypeMapper;
import org.opensds.vasa.vasa.util.DataUtil;
import org.opensds.vasa.vasa.util.FaultUtil;
import org.opensds.vasa.vasa.util.JsonUtil;
import org.opensds.vasa.vasa.util.VASAUtil;
import org.opensds.vasa.vasa20.device.array.NFSshare.AddAuthClientResBean;
import org.opensds.vasa.vasa20.device.array.NFSshare.INFSshareService;
import org.opensds.vasa.vasa20.device.array.NFSshare.NFSshareCreateResBean;
import org.opensds.vasa.vasa20.device.array.NFSvvol.INFSvvolService;
import org.opensds.vasa.vasa20.device.array.NFSvvol.NFSvvolCreateResBean;
import org.opensds.vasa.vasa20.device.array.fileSystem.FileSystemCloneCreateResBean;
import org.opensds.vasa.vasa20.device.array.fileSystem.FileSystemCreateResBean;
import org.opensds.vasa.vasa20.device.array.fileSystem.FileSystemMigrationResBean;
import org.opensds.vasa.vasa20.device.array.fileSystem.FileSystemMigrationUpdateResBean;
import org.opensds.vasa.vasa20.device.array.fileSystem.FileSystemSnapshotCreateResBean;
import org.opensds.vasa.vasa20.device.array.fileSystem.IFileSystemService;
import org.opensds.vasa.vasa20.device.array.logicalPort.LogicPortQueryResBean;

import com.vmware.vim.vasa.v20.StorageFault;
import com.vmware.vim.vasa.v20.data.vvol.xsd.BatchVirtualVolumeHandleResult;
import com.vmware.vim.vasa.v20.data.vvol.xsd.ProtocolEndpoint;
import com.vmware.vim.vasa.v20.data.vvol.xsd.ProtocolEndpointInbandId;
import com.vmware.vim.vasa.v20.data.vvol.xsd.ProtocolEndpointTypeEnum;
import com.vmware.vim.vasa.v20.data.vvol.xsd.VirtualVolumeHandle;
import com.vmware.vim.vasa.v20.data.vvol.xsd.VirtualVolumeInfo;
import com.vmware.vim.vasa.v20.data.xsd.NameValuePair;

import static org.opensds.vasa.base.common.VasaConstant.FILE_SYSTEM_TEMP;

public class VasaNasArrayServiceImpl implements VasaNasArrayService {

    interface FinalHandler {
        void onFilan();
    }

    class ShareInfo {
        private String shareId;
        private String sharePath;
        private List<String> clients;

        public ShareInfo(String shareId, String sharePath, List<String> clients) {
            super();
            this.shareId = shareId;
            this.sharePath = sharePath;
            this.clients = clients;
        }

        public List<String> getClients() {
            return clients;
        }

        public void setClients(List<String> clients) {
            this.clients = clients;
        }

        public String getShareId() {
            return shareId;
        }

        public void setShareId(String shareId) {
            this.shareId = shareId;
        }

        public String getSharePath() {
            return sharePath;
        }

        public void setSharePath(String sharePath) {
            this.sharePath = sharePath;
        }
    }

    class FileSystemInfo {
        private String filesystemId;
        private String filesystemName;
        private ShareInfo shareInfo;


        public String getFilesystemId() {
            return filesystemId;
        }

        public void setFilesystemId(String filesystemId) {
            this.filesystemId = filesystemId;
        }

        public String getFilesystemName() {
            return filesystemName;
        }

        public void setFilesystemName(String filesystemName) {
            this.filesystemName = filesystemName;
        }

        public ShareInfo getShareInfo() {
            return shareInfo;
        }

        public void setShareInfo(ShareInfo shareInfo) {
            this.shareInfo = shareInfo;
        }

        public FileSystemInfo(String filesystemId, String filesystemName, ShareInfo shareInfo) {
            this.filesystemId = filesystemId;
            this.filesystemName = filesystemName;
            this.shareInfo = shareInfo;
        }

    }

    private VvolPathService vvolPathDBService = (VvolPathService) ApplicationContextUtil
            .getBean("vvolPathService");
    private static org.apache.logging.log4j.Logger LOGGER = org.apache.logging.log4j.LogManager
            .getLogger(VasaNasArrayServiceImpl.class);
    private IDeviceManager deviceManager = (IDeviceManager) ApplicationContextUtil
            .getBean("deviceManager");
    private VirtualVolumeService virtualVolumeService = (VirtualVolumeService) ApplicationContextUtil
            .getBean("virtualVolumeService");
    private StorageManagerService storageManagerService = (StorageManagerService) ApplicationContextUtil
            .getBean("storageManagerService");
    private DataUtil dataManager = DataUtil.getInstance();

    private VvolMetadataService vvolMetadataService = (VvolMetadataService) ApplicationContextUtil
            .getBean("vvolMetadataService");

    private SnapshotCloneRecordService countServiceForSnapAndClone = (SnapshotCloneRecordService) ApplicationContextUtil
            .getBean("snapshotCloneRecordService");


    private EsxHostIpService esxHostIpService = (EsxHostIpService) ApplicationContextUtil
            .getBean("esxHostIpService");

    private StorageModel storageModel = new StorageModel();

    private static final Long COMMONMAXAUTOSIZ = 2147483648L;  //common maxsize is 1T

    private static final Long AUTOSIZEINCREMENT = 4194304L;  // increment is 2G  with sector

    private static final Long COMMONFSINITSIZE = 51200L;//config init size is 50G

    private static final int COMMONSECTORSIZE = 16384;  //bytes

    private static final int DATASECTORSIZE = 8192;  //bytes

    private static final String SPACESELFADJUSTINGMODE = "2";//auto shrink and auto grow

    private static final int SNAPSHOTRESERVEPER = 0;  // no reserve

    private static final String OWNINGCONTROLLER = ""; // no owning controller

    private static final boolean ISCLONEFS = false;

    private static final int AUTOSHRINKTHRESHOLDPERCENT = 50;

    private static final int AUTOGROWTHRESHOLDPERCENT = 95;

    // private static int RETRYTIMES = 0;
    private static ThreadLocal<Integer> RETRYTIMES = new ThreadLocal<Integer>();

    private String createDstFilesystenName(String srcName) {
        return VasaConstant.FILE_SYSTEM_PREFIX + srcName;
    }

    private SDKResult<FileSystemCloneCreateResBean> cloneFS(String dstFsName, String parentFilesystemid, String parentSnapshotId, String alloctype
            , Stack<FinalHandler> finalHandlers) throws StorageFault {
        IFileSystemService deviceLunService;
        try {
            String arrayId = VASAUtil.getArrayId();
            StorageInfo queryInfoByArrayId = storageManagerService.queryInfoByArrayId(arrayId);
            String thinThickSupport = DeviceTypeMapper.thinThickSupport(queryInfoByArrayId.getModel());
            if (null != thinThickSupport) {
                LOGGER.info("Current productmode have special thin/thick. Mode=" + queryInfoByArrayId.getModel() + ",thinThickSupport=" + thinThickSupport);
            }
            LOGGER.info("beginning cloneFS.");
            deviceLunService = deviceManager.getDeviceServiceProxy(arrayId,
                    IFileSystemService.class);

            LOGGER.info("cloneFileSystem = " + deviceLunService);
            LOGGER.info("arrayId = " + arrayId +
                    ", dstFsName = " + dstFsName +
                    ", alloctype = " + alloctype);
            LOGGER.info("end cloneFS.");

            SDKResult<FileSystemCloneCreateResBean> ret = deviceLunService.CloneFileSystem(arrayId, dstFsName, alloctype, parentFilesystemid, parentSnapshotId, null);
            if (ret.getErrCode() == 0) {
                finalHandlers.add(() -> {
                    try {
                        deleteFileSystem(ret.getResult().getID());
                    } catch (Exception e) {
                        LOGGER.error("delete fileSystem fail !!");
                    }
                });
            }
            return ret;

        } catch (SDKException e) {
            LOGGER.error("cloneFileSystem error, SDKException !", e);
            throw FaultUtil.storageFault();
        }
    }

    private void convertCloneRsp2CreteResult(FileSystemCreateResBean createResult, FileSystemCloneCreateResBean cloneRsp) {
        createResult.setID(cloneRsp.getID());
        createResult.setNAME(cloneRsp.getNAME());
        createResult.setALLOCTYPE(cloneRsp.getALLOCTYPE());
        createResult.setAVAILABLECAPCITY(cloneRsp.getAVAILABLECAPCITY());
    }

    private FileSystemInfo cloneFSAndShare(String vvolType, String vvolId, String poolId, long capacity, String parentFilesystemid,
                                           String parentSnapshotId, String dstFsName, Stack<FinalHandler> finalHandlers) throws StorageFault {
        SDKResult<FileSystemCloneCreateResBean> ret = cloneFS(dstFsName, parentFilesystemid, parentSnapshotId,
                VasaConstant.FILE_SYSTEM_THIN, finalHandlers);

        if (ret.getErrCode() != 0) {
            LOGGER.error("cloneFS for full clone fail !! vvol id is " + vvolId);
            throw new UnsupportedOperationException("cloneFS for full clone fail.");
        }

        FileSystemCreateResBean sysInfo = new FileSystemCreateResBean();
        convertCloneRsp2CreteResult(sysInfo, ret.getResult());

        FileSystemTable systable = new FileSystemTable(dstFsName, id2DBPrimaryKey(sysInfo.getID()),
                FileSystemTable.MAX_CAPACITY, String.valueOf(capacity), "0");

        try {
            Thread.sleep(3000);
            LOGGER.info("file cloned successfully,waiting to add client");
        } catch (InterruptedException e) {
            LOGGER.error(e);
        }
        ShareInfo shareInfo = queryNfsShareFromArray(vvolType, vvolId, dstFsName, sysInfo.getID(), dstFsName, finalHandlers);
        insertFilesystemInfo(systable, finalHandlers);
        return new FileSystemInfo(sysInfo.getID(), sysInfo.getNAME(), shareInfo);
    }

    private boolean hasCountRecord(String vmId, String opt) throws StorageFault {
        return countServiceForSnapAndClone.checkIfExist(vmId, opt);
    }

    private void initCountRecord(String vmId, String operationType, int diskCount, String inputName, Stack<FinalHandler> finalHandlers) throws StorageFault {
        LOGGER.info("init record.");
        countServiceForSnapAndClone.initRecord(vmId, operationType, diskCount, inputName);
        finalHandlers.add(() -> {
            try {
                LOGGER.info("clear for record[init].");
                countServiceForSnapAndClone.deleteRecord(vmId, operationType, inputName);
            } catch (StorageFault e) {
                LOGGER.error("clear: delete  snapshotclone record.");
            }
        });
    }

    private int getDiskNumByVmId(String vmId) throws StorageFault {
        //认证时，参数不会提供vmId,为了和当前的处理逻辑一致（一个虚拟机多个盘，只克隆一次），这里默认只有一个盘
        if (vmId == null || "".equals(vmId))
            return 1;
        return virtualVolumeService.getDiskCountByVmId(vmId);
    }

    private Result addRecord(String vmId, String opt, Stack<FinalHandler> finalHandlers) throws StorageFault {
        LOGGER.info("incr record num.");
        Result ret = countServiceForSnapAndClone.addRecord(vmId, opt);
        finalHandlers.add(() -> {
            LOGGER.info("clear for record.");
        });
        return ret;
    }

    private FileSystemInfo createFileSystemInfoByFSName(String fsName) {
        List<VvolPath> vvolPaths = vvolPathDBService.queryAllVvolPathByFileSystem(fsName);
        if (vvolPaths == null || vvolPaths.isEmpty()) {
            return null;
        }

        return new FileSystemInfo(vvolPaths.get(0).getFileSystemId(), vvolPaths.get(0).getFileSystemName()
                , new ShareInfo(vvolPaths.get(0).getShareId(), vvolPaths.get(0).getSharePath(), null));
    }


    private synchronized FileSystemInfo initCountRecordForFullClone(int count, VvolPath srcVvolPath, String configId
            , NVirtualVolume srcVvol, String poolId, String newUuId, String dstFsName, Stack<FinalHandler> finalHandlers) throws SDKException, StorageFault {
        FileSystemInfo fsInfo = null;
        if (!hasCountRecord(configId, SnapshotCloneRecordService.CLONE)) {
            String snapDisplayName = VASAUtil.buildDisplayName(VasaSrcTypeConstant.SNAPSHOT);
            String fileSystemId = srcVvolPath.getFileSystemId();
            SDKResult<FileSystemSnapshotCreateResBean> snap = createSnapShot(fileSystemId, snapDisplayName);
            if (snap.getErrCode() != 0) {
                LOGGER.error("create snapshot for offline migrate fail !!");
                throw new UnsupportedOperationException("create snapshot for offline migrate fail.");
            }

            finalHandlers.add(() ->
            {
                try {
                    deleteSnapBySnapshotId(snap.getResult().getID());
                } catch (Exception e) {
                    LOGGER.error("clear: delete snap for migrate of offline.");
                }
            });

            initCountRecord(configId, SnapshotCloneRecordService.CLONE, count, snap.getResult().getID(), finalHandlers);
            String srcFsId = srcVvolPath.getFileSystemId();
            FileSystemTable sysTable = vvolPathDBService.queryFileSystemTableByName(srcVvolPath.getFileSystemName());
            fsInfo = cloneFSAndShare(srcVvol.getVvolType(), newUuId, poolId,
                    Long.valueOf(sysTable.getCurrentCapacity()), srcFsId, snap.getResult().getID(), dstFsName, finalHandlers);
        } else {
            fsInfo = createFileSystemInfoByFSName(dstFsName);
        }

        return fsInfo;
    }


    public void cloneFileSystem(String poolId, NVirtualVolume srcVvol, String newUuId) throws StorageFault, SDKException {
        //

        Stack<FinalHandler> finalHandlers = new Stack<FinalHandler>();

        boolean shouldClear = true;
        try {
            LOGGER.info("beginning cloneFileSystem file!!");
            VvolPath vvolPath = new VvolPath();

            VvolPath srcVvolPath = vvolPathDBService.getVvolPathByVvolId(srcVvol.getVvolid());
            String configId = getFSNameOfDataByNameSpace(VASAUtil.getVmwNamespace());
            String newConfigId = updateVmId(configId, newUuId);
            String dstFsName = createDstFilesystenName(VASAUtil.UUId2FileSystemName(newConfigId));
            FileSystemInfo fsInfo = null;
            String vmId = srcVvol.getVmId();

            fsInfo = initCountRecordForFullClone(getDiskNumByVmId(vmId), srcVvolPath, newConfigId, srcVvol, poolId, newUuId, dstFsName, finalHandlers);
            Result ret = addRecord(newConfigId, SnapshotCloneRecordService.CLONE, finalHandlers);
            if (ret.result == SnapshotCloneRecordService.FINISHED) {
                LOGGER.info("delete snapshot " + ret.inputName + "!!");
                deleteSnapBySnapshotId(ret.inputName);
            } else {
                LOGGER.info("clone ......!!");
            }

            String path = srcVvolPath.getPath();

            if (srcVvol.getSourceType().equals(VasaConstant.OPT_SNAPSHOT)) {
                path = srcVvolPath.getPath().split("/")[2];
            }
            fullVvolPath(newUuId, fsInfo, path, vvolPath);
            vvolPathDBService.insertRecord(vvolPath);
            shouldClear = false;
            LOGGER.info("end cloneFileSystem file!!");
        } catch (Exception e) {
            LOGGER.error("cloneFileSystem fail, exec clear func!!" + e);
            throw e;
        } finally {
            if (shouldClear) {
                while (!finalHandlers.isEmpty()) {
                    LOGGER.info("do clear......");
                    FinalHandler finalHander = finalHandlers.pop();
                    finalHander.onFilan();
                }
                deleteVirtualVolumeFromDatabase(VasaConstant.VVOL_PREFIX + newUuId);
            }
        }
    }


    private SDKResult<FileSystemMigrationResBean> migrateFS(String poolId, String dstFsName, String parentFilesystemid, String parentSnapshotName, String alloctype
            , Stack<FinalHandler> finalHandlers) throws StorageFault {
        IFileSystemService deviceLunService;
        try {
            String arrayId = VASAUtil.getArrayId();
            StorageInfo queryInfoByArrayId = storageManagerService.queryInfoByArrayId(arrayId);
            String thinThickSupport = DeviceTypeMapper.thinThickSupport(queryInfoByArrayId.getModel());
            if (null != thinThickSupport) {
                LOGGER.info("Current productmode have special thin/thick. Mode=" + queryInfoByArrayId.getModel() + ",thinThickSupport=" + thinThickSupport);
            }
            LOGGER.info("beginning migrateFS.");
            deviceLunService = deviceManager.getDeviceServiceProxy(arrayId,
                    IFileSystemService.class);

            LOGGER.info("migrateFS = " + deviceLunService);
            LOGGER.info("arrayId = " + arrayId +
                    ", dstFsName = " + dstFsName +
                    ", alloctype = " + alloctype);

            SDKResult<FileSystemMigrationResBean> migRet = deviceLunService.MigrateFileSystem(arrayId, Integer.valueOf(parentFilesystemid),
                    dstFsName, parentSnapshotName, Integer.valueOf(poolId));
            if (migRet.getErrCode() == 0) {
                finalHandlers.add(() -> {
                    try {
                        deleteFileSystem(migRet.getResult().getMigrateTmpFsID());
                    } catch (Exception e) {
                        LOGGER.error("delete fileSystem fail for migrate!!");
                    }
                });
            }
            LOGGER.info("end migrateFS.");
            return migRet;

        } catch (SDKException e) {
            LOGGER.error("migrateFileSystem error, SDKException !", e);
            throw FaultUtil.storageFault();
        }
    }

    private SDKResult<FileSystemMigrationUpdateResBean> updateMigrateProgress(String sourceFsId) throws StorageFault {
        IFileSystemService deviceLunService;
        try {
            String arrayId = VASAUtil.getArrayId();
            StorageInfo queryInfoByArrayId = storageManagerService.queryInfoByArrayId(arrayId);
            String thinThickSupport = DeviceTypeMapper.thinThickSupport(queryInfoByArrayId.getModel());
            if (null != thinThickSupport) {
                LOGGER.info("Current productmode have special thin/thick. Mode=" + queryInfoByArrayId.getModel() + ",thinThickSupport=" + thinThickSupport);
            }
            LOGGER.info("beginning updateMigrateProgress.");
            deviceLunService = deviceManager.getDeviceServiceProxy(arrayId,
                    IFileSystemService.class);

            LOGGER.info("updateMigrateFS = " + deviceLunService);
            LOGGER.info("arrayId = " + arrayId +
                    ", sourceFsId = " + sourceFsId);

            SDKResult<FileSystemMigrationUpdateResBean> migRet = deviceLunService.updateFileSystemMigration(arrayId, Integer.valueOf(sourceFsId));
            LOGGER.info("end updateMigrateProgress.");
            return migRet;
        } catch (SDKException e) {
            LOGGER.error("migrateFileSystem error, SDKException !", e);
            throw FaultUtil.storageFault();
        }
    }

    private boolean shouldNextMigrateForFs(String uniqueFsId) throws StorageFault {
        return !countServiceForSnapAndClone.checkIfExist(uniqueFsId, SnapshotCloneRecordService.MIGRATE_ONE_BY_ONE);
    }


    private void nextMigrate(String poolId, String fsName, String sourceFsId, String snapName, String snapshotId,
                             String uniqueFsId, QueryMigrateResult ret, Stack<FinalHandler> finalHandlers) throws StorageFault {
        LOGGER.info("beginning nextMigrate.");
        SDKResult<FileSystemMigrationResBean> migRet = migrateFS(poolId, fsName, sourceFsId, snapName,
                VasaConstant.FILE_SYSTEM_THIN, finalHandlers);
        if (migRet.getErrCode() != 0) {
            if (migRet.getErrCode() != IFileSystemService.FILE_MIGRATE_BUSY) {
                LOGGER.error("nextMigrate for migrateOneByOne[create] fail !!" + migRet.getDescription());
                ret.result = QueryMigrateResult.MIGRATE_FAIL;
            } else {
                ret.result = QueryMigrateResult.MIGRATE_RUNNING;
                LOGGER.info("nextMigrate for migrateOneByOne[create] busy !!");
                return;
            }
        } else {
            ret.result = QueryMigrateResult.MIGRATE_RUNNING;
            LOGGER.info("nextMigrate for migrateOneByOne[create] running !!");
        }
        String tmpFsId = migRet.getResult().getMigrateTmpFsID();
        ret.targetFsId = tmpFsId;
        initCountRecord(uniqueFsId, SnapshotCloneRecordService.MIGRATE_ONE_BY_ONE,
                1, tmpFsId + VasaConstant.SEPARATOR + snapshotId + VasaConstant.SEPARATOR + tmpFsId + VasaConstant.SEPARATOR + fsName, finalHandlers);
        LOGGER.info("end nextMigrate.");
    }


    private QueryMigrateResult migrateOneByOne(String poolId, String fsName, String sourceFsId, String snapshotId
            , Stack<FinalHandler> finalHandlers) throws StorageFault {
        LOGGER.info("beginning migrateOneByOne.");
        String snapName = snapshotId.replaceAll(sourceFsId + "@", "");
        String uniqueFsId = sourceFsId + VasaConstant.SEPARATOR + VASAUtil.getArrayId();
        QueryMigrateResult ret = new QueryMigrateResult();
        ret.isSameTask = true;

        if (shouldNextMigrateForFs(uniqueFsId)) {
            nextMigrate(poolId, fsName, sourceFsId, snapName, snapshotId, uniqueFsId, ret, finalHandlers);
        } else {
            SDKResult<FileSystemMigrationUpdateResBean> rsp = updateMigrateProgress(sourceFsId);
            if ("100".equals(rsp.getResult().getMigrateProgress())) {
                LOGGER.info("migrateOneByOne[create] one succ !!");
                Result inputName = addRecord(uniqueFsId, SnapshotCloneRecordService.MIGRATE_ONE_BY_ONE, finalHandlers);
                ret.targetFsId = inputName.inputName.split(VasaConstant.SEPARATOR)[0];
                countServiceForSnapAndClone.deleteRecord(uniqueFsId, SnapshotCloneRecordService.MIGRATE_ONE_BY_ONE, inputName.inputName);
                if (!inputName.inputName.split(VasaConstant.SEPARATOR)[1].equals(snapshotId)) {
                    String otherFsName = inputName.inputName.split(VasaConstant.SEPARATOR)[3];
                    FileSystemTable fsTable = vvolPathDBService.queryFileSystemTableByName(otherFsName);
                    if (fsTable != null && fsTable.getId() == null) {
                        vvolPathDBService.updateFilesystemTableFsID(otherFsName,
                                id2DBPrimaryKey(inputName.inputName.split(VasaConstant.SEPARATOR)[2]));
                    }
                    ret.result = QueryMigrateResult.MIGRATE_RUNNING;

                    nextMigrate(poolId, fsName, sourceFsId, snapName, snapshotId, uniqueFsId, ret, finalHandlers);
                    LOGGER.info("updateMigrate [create] previous succ and start self migrate task !!");
                } else {
                    ret.result = QueryMigrateResult.MIGRATE_SUCC;
                    LOGGER.info("updateMigrate [create] self succ.");
                }
            } else {
                ret.result = QueryMigrateResult.MIGRATE_RUNNING;
                LOGGER.info("migrateOneByOne[create] running !!");
            }
        }
        LOGGER.info("end migrateOneByOne.");
        return ret;
    }

    private void createShareForUpdateMigrate(NVirtualVolume vvol, String vvolId, String fsName, String fsId,
                                             VvolPath vvolPath, Stack<FinalHandler> finalHandlers) throws StorageFault {
        ShareInfo shareInfo = createNfsShare(vvol.getVvolType(), vvolId, fsName, fsId, fsName, finalHandlers);
        vvolPath.setShareId(shareInfo.getShareId());
        vvolPath.setSharePath(shareInfo.getSharePath());
        vvolPath.setFileSystemId(fsId);
        vvolPathDBService.insertRecord(vvolPath);
        vvolPathDBService.setBindState(vvolId, false);
        finalHandlers.add(() -> {
            vvolPathDBService.deleteRecordByVvolId(vvolPath.getVvolid());
        });
    }

    private boolean hasUpdateSuccByOtherTaskOfCreate(NVirtualVolume vvol, String vvolId, String fsName,
                                                     String snapshotId, VvolPath vvolPath, String rawSnapId, Stack<FinalHandler> finalHandlers) throws StorageFault {
        FileSystemTable sysTable = vvolPathDBService.queryFileSystemTableByName(fsName);
        String fsId = sysTable.getId();
        if (fsId != null) {
            createShareForUpdateMigrate(vvol, vvolId, fsName, dbPrimaryKey2id(fsId), vvolPath, finalHandlers);
            //ret.result = QueryMigrateResult.MIGRATE_SUCC;
            LOGGER.info("updateMigrateTask by other task [create] succ !!");
            try {
                if (!rawSnapId.startsWith(MigrateResult.MIGRATE_ONLINE)) {
                    deleteSnapBySnapshotId(snapshotId);
                    LOGGER.info("updateMigrateTask[create] for offline migrate succ by other task !!");
                } else {
                    LOGGER.info("updateMigrateTask[create] for online migrate succ by other task !!");
                }
            } catch (SDKException e) {
                LOGGER.error("delete snapshot error:" + e);
            }
            return true;
        }
        return false;
    }

    private void updateTaskSuccBySelfTaskOfCreate(NVirtualVolume vvol, String vvolId, String fsName, String targetFsId
            , String snapshotId, VvolPath vvolPath, String rawSnapId, Stack<FinalHandler> finalHandlers) throws StorageFault {
        createShareForUpdateMigrate(vvol, vvolId, fsName, targetFsId, vvolPath, finalHandlers);
        try {
            if (!rawSnapId.startsWith(MigrateResult.MIGRATE_ONLINE)) {
                deleteSnapBySnapshotId(snapshotId);
                LOGGER.info("updateMigrateTask[create] for offline migrate !!");
            } else {
                LOGGER.info("updateMigrateTask[create] for online migrate !!");
            }
        } catch (SDKException e) {
            LOGGER.error("delete snapshot error:" + e);
        }
        vvolPathDBService.updateFilesystemTableFsID(fsName, id2DBPrimaryKey(targetFsId));
        //ret.result = QueryMigrateResult.MIGRATE_SUCC;
        LOGGER.info("updateMigrateTask by self task [create] succ !!");
    }

    private void updateTaskSuccOfQuery(NVirtualVolume vvol, String vvolId, String fsName, VvolPath vvolPath,
                                       QueryMigrateResult ret, Stack<FinalHandler> finalHandlers) throws StorageFault {
        FileSystemTable sysTable = vvolPathDBService.queryFileSystemTableByName(fsName);
        String fsId = sysTable.getId();
        if (fsId != null) {
            createShareForUpdateMigrate(vvol, vvolId, fsName, dbPrimaryKey2id(fsId), vvolPath, finalHandlers);
            ret.result = QueryMigrateResult.MIGRATE_SUCC;
            LOGGER.info("updateMigrateTask[query] succ !!");
        } else {
            ret.result = QueryMigrateResult.MIGRATE_RUNNING;
            LOGGER.info("updateMigrateTask[query] running !!");
        }
    }


    /**
     * (non-Javadoc)
     *
     * @see VasaNasArrayService#updateMigrateTask(java.lang.String[], NVirtualVolume)
     * 瀛樺偍瀵瑰悓涓�鏂囦欢绯荤粺鐨勪笉鍚屽揩鐓э紙snapt0,snapt1,...锛変笉鏀寔骞跺彂杩佺Щ锛岄渶瑕佹彃浠跺仛鎺掗槦
     */
    @Override
    public synchronized QueryMigrateResult updateMigrateTask(String[] paras, NVirtualVolume vvol) throws StorageFault {
        LOGGER.info("beginning updateMigrateTask !!");
        //paras
        //taskname + vvolId + snapshotId + poolId + targetfsname + opt(create|query) + srcfsId + sourcePath
        boolean shouldClear = true;
        Stack<FinalHandler> finalHandlers = new Stack<FinalHandler>();
        try {
            if (paras.length != 8) {
                LOGGER.error("error paras:" + paras.toString() + " !!");
                throw new UnsupportedOperationException("error paras.");
            }
            String vvolId = paras[1];
            String snapshotId = paras[2];
            String poolId = paras[3];
            String fsName = paras[4];
            String opt = paras[5];
            String sourceFsId = paras[6];
            String sourcePath = paras[7];
            VvolPath vvolPath = new VvolPath();
            String rawSnapShotId = snapshotId;
            if (snapshotId.startsWith(MigrateResult.MIGRATE_ONLINE)) {
                LOGGER.info("migrate online: " + snapshotId + " !!");
                snapshotId = snapshotId.replaceAll(MigrateResult.MIGRATE_ONLINE, "");
                LOGGER.info("old snapshotId: " + rawSnapShotId);
                LOGGER.info("new snapshotId: " + snapshotId);
            }

            vvolPath.setFileSystemName(fsName);
            vvolPath.setPath(sourcePath);
            vvolPath.setVvolid(vvolId);
            QueryMigrateResult ret = new QueryMigrateResult();
            if (MigrateResult.MIGRATE_CREATE.equals(opt)) {
                if (hasUpdateSuccByOtherTaskOfCreate(vvol, vvolId, fsName, snapshotId, vvolPath, rawSnapShotId, finalHandlers)) {
                    ret.result = QueryMigrateResult.MIGRATE_SUCC;
                    shouldClear = false;
                    return ret;
                }

                QueryMigrateResult migRsp = migrateOneByOne(poolId, fsName, sourceFsId, snapshotId, finalHandlers);
                if (QueryMigrateResult.MIGRATE_SUCC.equals(migRsp.result)) {
                    ret.result = QueryMigrateResult.MIGRATE_SUCC;
                    updateTaskSuccBySelfTaskOfCreate(vvol, vvolId, fsName, migRsp.targetFsId, snapshotId, vvolPath, rawSnapShotId, finalHandlers);
                } else if (QueryMigrateResult.MIGRATE_RUNNING.equals(migRsp.result)) {
                    ret.result = QueryMigrateResult.MIGRATE_RUNNING;
                    LOGGER.info("updateMigrateTask[create] running !!");
                } else {
                    ret.result = QueryMigrateResult.MIGRATE_FAIL;
                    LOGGER.info("updateMigrateTask[create] fail !!" + ret.result);
                }
            } else if (MigrateResult.MIGRATE_QUERY.equals(opt)) {
                updateTaskSuccOfQuery(vvol, vvolId, fsName, vvolPath, ret, finalHandlers);
            } else {
                LOGGER.error("error opt:" + opt + " !!");
                throw new UnsupportedOperationException("error opt.");
            }
            LOGGER.info("end updateMigrateTask !!");
            shouldClear = false;
            return ret;
        } catch (Exception e) {
            LOGGER.error("catch error !! " + e);
            throw e;
        } finally {
            if (shouldClear) {
                while (!finalHandlers.isEmpty()) {
                    LOGGER.info("do clear......");
                    FinalHandler finalHander = finalHandlers.pop();
                    finalHander.onFilan();
                }
            }
        }
    }

    private synchronized void initCountRecordFormigrate(int count, String configId, VvolPath srcVvolPath, NVirtualVolume srcVvol
            , String poolId, String dstFsName, MigrateResult result, Stack<FinalHandler> finalHandlers) throws SDKException, StorageFault {
        if (!hasCountRecord(configId, SnapshotCloneRecordService.MIGRATE)) {
            String fileSystemId = srcVvolPath.getFileSystemId();
            String snapDisplayName = null;
            String snapId = null;
            String snapIdOpt = null;
            if (VasaConstant.OPT_SNAPSHOT.equalsIgnoreCase(srcVvol.getSourceType())) {
                snapId = srcVvolPath.getSnapshotId();
                if (snapId == null || "".equals(snapId)) {
                    LOGGER.error("snapid error srcVvolPath for snapshot:" + srcVvolPath.toString());
                    throw new IllegalArgumentException("error parameter snapid:" + snapId);
                }
                snapIdOpt = MigrateResult.MIGRATE_ONLINE + snapId;
                snapDisplayName = snapId.replaceAll(fileSystemId + "@", "");
            } else {
                snapDisplayName = VASAUtil.buildDisplayName(VasaSrcTypeConstant.SNAPSHOT);
                SDKResult<FileSystemSnapshotCreateResBean> snap = createSnapShot(fileSystemId, snapDisplayName);

                if (snap.getErrCode() != 0) {
                    LOGGER.error("create snapshot for offline migrate fail !!" + snap.getDescription());
                    throw new UnsupportedOperationException("create snapshot for offline migrate fail.");
                }
                snapId = snap.getResult().getID();
                snapIdOpt = snapId;

                finalHandlers.add(() ->
                {
                    try {
                        deleteSnapBySnapshotId(snap.getResult().getID());
                    } catch (Exception e) {
                        LOGGER.error("clear: delete snap for migrate of offline.");
                    }
                });
            }
//            SDKResult<FileSystemMigrationResBean> migRet = migrateFS(poolId, dstFsName, fileSystemId, snapDisplayName,
//                    VasaConstant.FILE_SYSTEM_THIN, finalHandlers);
//
//            if(migRet.getErrCode() != 0)
//            {
//                LOGGER.error("migrateFS for offline migrate fail !!");
//                throw new UnsupportedOperationException("migrateFS for offline migrate fail.");
//            }
//
//            String tgtFsId = migRet.getResult().getMigrateTmpFsID();
            initCountRecord(configId, SnapshotCloneRecordService.MIGRATE, count,
                    //snapId targetId srcId
                    snapIdOpt + VasaConstant.SEPARATOR + poolId + VasaConstant.SEPARATOR + fileSystemId
                    , finalHandlers);
            FileSystemTable sysTable = vvolPathDBService.queryFileSystemTableByName(srcVvolPath.getFileSystemName());
            result.migrateOpt = MigrateResult.MIGRATE_CREATE;

            vvolPathDBService.insertFilesystemTable(new FileSystemTable(dstFsName, null, sysTable.getMaxCapacity(),
                    sysTable.getCurrentCapacity(), "0"));

            finalHandlers.add(() ->
            {
                try {
                    vvolPathDBService.deleteFilesystemTableByName(dstFsName);
                } catch (Exception e) {
                    LOGGER.error("clear: filesystem for migrate of offline.");
                }
            });

        } else {
            result.migrateOpt = MigrateResult.MIGRATE_QUERY;
        }
    }

    private String updateVmId(String vmId, String uuId) {
        //认证时，参数不会提供vmId,为了和当前的处理逻辑一致（一个虚拟机多个盘，只克隆一次），这里默认只有一个盘
        if (vmId == null || "".equals(vmId))
            return uuId;
        return vmId;
    }


    /**
     * 鍏嬮殕铏氭嫙鏈烘椂锛屽鏋滆櫄鎷熸満瀛樺湪澶氫釜纾佺洏锛屽垯鍙绗竴娆℃敹鍒癱lone璇锋眰鐨勭鐩樺搴旂殑鏂囦欢绯荤粺杩涜clone锛屽苟鏍囪涓篶reate,鍏朵粬鐨勭鐩樻爣璁颁负query
     * clone task鐨刬d璁剧疆濡備笅锛�
     * taskname + vvolId + snapshotId + poolId + targetfsname + opt(create|query) + srcfsId + sourcePath  -----vasa task鏁版嵁搴撳taskId闀垮害鐨勯檺鍒舵槸255
     * updateTask鏃舵寜浠ヤ笅鏂瑰紡浠巘askId涓彇鍑�
     * String vvolId = paras[1];
     * String snapshotId = paras[2];
     * String poolId = paras[3];
     * String fsName = paras[4];
     * String opt = paras[5];
     * String sourceFsId = paras[6];
     * String sourcePath = paras[7];
     */
    @Override
    public MigrateResult migrateFilesystem(String arrayId, String poolId, long sizeInMB, List<NameValuePair> metadata, String newContainerId,
                                           String srcVvolType, NVirtualVolume srcVvol, String newUuId) throws StorageFault, SDKException {
        Stack<FinalHandler> finalHandlers = new Stack<FinalHandler>();
        MigrateResult result = new MigrateResult();
        boolean shouldClear = true;
        try {
            LOGGER.info("beginning migrateFileSystem file!!");

            VvolPath srcVvolPath = vvolPathDBService.getVvolPathByVvolId(srcVvol.getVvolid());
            String configId = getFSNameOfDataByNameSpace(VASAUtil.getVmwNamespace());
            String newConfigId = updateVmId(configId, newUuId);
            String dstFsName = createDstFilesystenName(VASAUtil.UUId2FileSystemName(newConfigId));
            String vmId = null;
            String resultPath = null;
            LOGGER.info("===============vvolInfo=================");
            LOGGER.info(srcVvol.toString());
            LOGGER.info("===============end=================");

            if (VasaConstant.OPT_SNAPSHOT.equalsIgnoreCase(srcVvol.getSourceType())) {
                String parentId = srcVvol.getParentId();
                NVirtualVolume vvol = virtualVolumeService.getVirtualVolumeByVvolId(parentId);
                VvolPath path = vvolPathDBService.getVvolPathByVvolId(parentId);
                vmId = vvol.getVmId();
                resultPath = path.getPath();
                LOGGER.info("migrate for online,vmId:" + vvol.getVmId() + " parentid " + parentId);
            } else {
                vmId = srcVvol.getVmId();
                resultPath = srcVvolPath.getPath();
                LOGGER.info("migrate for offline,vmId:" + srcVvol.getVmId());
            }


            initCountRecordFormigrate(getDiskNumByVmId(vmId), newConfigId, srcVvolPath, srcVvol, poolId, dstFsName, result, finalHandlers);
            Result ret = addRecord(newConfigId, SnapshotCloneRecordService.MIGRATE, finalHandlers);
            String[] ids = ret.inputName.split(VasaConstant.SEPARATOR);
            if (ids.length != 3) {
                LOGGER.error("error input:" + ret.inputName);
                throw new UnsupportedOperationException("error inputName.");
            }
            result.snapshotId = ids[0];
            result.poolId = ids[1];
            result.sourceFsId = ids[2];
            result.fsName = dstFsName;
            result.sourcePath = resultPath;

            if (ret.result == SnapshotCloneRecordService.FINISHED) {
                LOGGER.info("all disk clone ok," + ret.inputName + "!!");
            } else {
                LOGGER.info("clone ......!!");
            }

            shouldClear = false;
            vvolPathDBService.updateFileCount(dstFsName, 1);
            LOGGER.info("end migrateFileSystem file!!");
            return result;
        } catch (Exception e) {
            LOGGER.error("migrateFileSystem fail, exec clear func!!" + e);
            throw e;
        } finally {
            if (shouldClear) {
                while (!finalHandlers.isEmpty()) {
                    LOGGER.info("do clear......");
                    FinalHandler finalHander = finalHandlers.pop();
                    finalHander.onFilan();
                }
                deleteVirtualVolumeFromDatabase(VasaConstant.VVOL_PREFIX + newUuId);
            }
        }
    }

    private boolean isExistShare(String shareName) throws StorageFault {
        if (shareName == null) {
            LOGGER.error("the parameter fileSystem is null!!");
            throw FaultUtil.storageFault();
        }
        return (vvolPathDBService.queryShareByShareName(shareName) != null);//nd_todo 浼樺寲
    }

    private List<String> filterShuoldCreateAuthList(List<String> ips, String shareId) throws StorageFault {
        List<ShareClientTable> clients = vvolPathDBService.queryShareClientByShareId(id2DBPrimaryKey(shareId));
        LOGGER.info("query all client by [" + shareId + "]:" + clients.toString());
        if (clients == null || clients.isEmpty()) {
            return ips;
        }
        List<String> newIps = new ArrayList<String>();
        for (String ip : ips) {
            boolean isFind = false;
            for (ShareClientTable cli : clients) {
                if (cli.getShareProperty() != null && cli.getShareProperty().startsWith(ip))//ip + ; + clientid + arrayid
                {
                    isFind = true;
                    break;
                }
            }
            if (!isFind) {
                newIps.add(ip);
            }
        }
        return newIps;
    }

    public SDKResult<AddAuthClientResBean> createClient(String arrayId, String ip, String shareId) {
        SDKResult<AddAuthClientResBean> clientRsp = new SDKResult<AddAuthClientResBean>();
        try {
            INFSshareService deviceNfsSharedService;
            LOGGER.info("beginning create client ! ");
            deviceNfsSharedService = deviceManager.getDeviceServiceProxy(arrayId,
                    INFSshareService.class);
            clientRsp = deviceNfsSharedService.addAuthClient(arrayId, ip,
                    shareId, INFSshareService.ACCESSVAL_READ_WRITE, INFSshareService.SYNC
                    , INFSshareService.ALLSQUASH_NO_ALL, INFSshareService.ROOTSQUASH_NO_ROOT);
            LOGGER.info("end create client ! ");
            return clientRsp;
        } catch (Exception e) {
            clientRsp.setErrCode(-1);
            return clientRsp;
        }
    }

    public SDKErrorCode deleteClient(String arrayId, String clientId) {
        SDKErrorCode ret = new SDKErrorCode();
        try {
            INFSshareService deviceNfsSharedService;
            LOGGER.info("beginning deleteClient client ! ");
            deviceNfsSharedService = deviceManager.getDeviceServiceProxy(arrayId,
                    INFSshareService.class);
            LOGGER.info("end deleteClient client ! ");
            ret = deviceNfsSharedService.deleteAuthClient(clientId);
        } catch (Exception e) {
            ret.setErrCode(-1);
        }
        return ret;
    }

    private SDKErrorCode createClients(String vvolType, List<String> hostIps,
                                       String accessval, String sync, String allSquash, String rootSquash,
                                       SDKResult<NFSshareCreateResBean> share, List<ShareClientTable> clients, Stack<FinalHandler> finalHandlers) throws StorageFault {
        try {
            String arrayId = VASAUtil.getArrayId();
            INFSshareService deviceNfsSharedService;
            LOGGER.info("beginning create clients ! ");
            deviceNfsSharedService = deviceManager.getDeviceServiceProxy(arrayId,
                    INFSshareService.class);
            String shareId = share.getResult().getID();
            List<String> newIps = filterShuoldCreateAuthList(hostIps, shareId);
            boolean isSuccess = false;
            for (String ip : newIps) {
                SDKResult<AddAuthClientResBean> clientRsp = deviceNfsSharedService.addAuthClient(arrayId, ip,
                        shareId, accessval, sync, allSquash, rootSquash);
                if (clientRsp.getErrCode() != 0) {
                    if (clientRsp.getErrCode() == INFSshareService.CLIENT_EXIST) {
                        isSuccess = true;
                        LOGGER.warn("client exist[" + clientRsp.getErrCode() + "]!");
                        SDKResult<List<AddAuthClientResBean>> clientRsps = new SDKResult<>();
                        clientRsps = deviceNfsSharedService.queryAuthClient(arrayId, null, share.getResult().getID(), ip);
                        AddAuthClientResBean temp = new AddAuthClientResBean();
                        for (AddAuthClientResBean cli : clientRsps.getResult()) {
                            if (shareId.equals(cli.getPARENTID())) {
                                temp = cli;
                                break;
                            }
                        }
                        clients.add(new ShareClientTable(id2DBPrimaryKey(ip + VasaConstant.SEPARATOR + temp.getID()), id2DBPrimaryKey(shareId)));
//                        clientRsp = deviceNfsSharedService.queryAuthClient(arrayId, null, share.getResult().getID(), ip);
//                        clientIds.add(clientRsp.getResult().getCLIENTID());
                    } else {
                        LOGGER.error("create client error[" + clientRsp.getErrCode() + "]!");
                    }
                    continue;
                }
                clients.add(new ShareClientTable(id2DBPrimaryKey(ip + VasaConstant.SEPARATOR + clientRsp.getResult().getID())
                        , id2DBPrimaryKey(shareId)));
                isSuccess = true;
                finalHandlers.add(() -> {
                    LOGGER.info("clear client IP[ " + ip + "] success!");
                });
            }
            SDKErrorCode ret = new SDKErrorCode();

            if (isSuccess || newIps.isEmpty()) {
                ret.setErrCode(0);
            } else {
                ret.setErrCode(-1);
            }

            LOGGER.info("end create clients ! ");
            return ret;
        } catch (SDKException e) {
            LOGGER.error("create client error, SDKException !", e);
            throw FaultUtil.storageFault();
        }
    }

    private SDKResult<NFSshareCreateResBean> doCreateNfsShare(String vvolType, String fsId,
                                                              String shareName, Stack<FinalHandler> finalHandlers) throws StorageFault {
        LOGGER.info("beginning doCreateNfsShare !!");
        if (fsId == null || shareName == null) {
            LOGGER.error("[doCreateNfsShare] the parameter shareName or fsId is null !!");
            throw FaultUtil.storageFault();
        }

        if (isExistShare("/" + shareName + "/")) {
            LOGGER.info("share is exist !!");
            SDKResult<NFSshareCreateResBean> ret = new SDKResult<NFSshareCreateResBean>();
            FilesystemShareTable share = vvolPathDBService.queryShareByShareName("/" + shareName + "/");// nd_todo浼樺寲
            if (share == null) {
                LOGGER.error("query share from db fail !");
                throw FaultUtil.storageFault("query share fail !");
            }
            LOGGER.info(share.toString());
            NFSshareCreateResBean retBean = new NFSshareCreateResBean();
            retBean.setSHAREPATH(share.getSharePath());
            retBean.setID(dbPrimaryKey2id(share.getShareId()));
            ret.setResult(retBean);
            ret.setErrCode(0);
            return ret;
        }
        LOGGER.info("create nfs share: " + ",filesystemName = " + shareName);
        INFSshareService deviceNfsSharedService;
        try {
            String arrayId = VASAUtil.getArrayId();
            deviceNfsSharedService = deviceManager.getDeviceServiceProxy(arrayId,
                    INFSshareService.class);

            SDKResult<NFSshareCreateResBean> shareRet = deviceNfsSharedService.createShare(arrayId, fsId, "/" + shareName + "/", "create share");
            if (shareRet.getErrCode() != 0) {
                if (INFSshareService.SHARE_EXIST != shareRet.getErrCode()) {
                    LOGGER.error("create nfsShare error[" + shareRet.getErrCode() + "]!");
                    throw FaultUtil.storageFault();
                } else {
                    shareRet = deviceNfsSharedService.queryShare(arrayId, "/" + shareName + "/", null);
                    return shareRet;
                }
            }

            String shareId = shareRet.getResult().getID();
            finalHandlers.add(() -> {
                deviceNfsSharedService.deleteShare(arrayId, shareId, null);
            });
            LOGGER.info("end doCreateNfsShare !!");
            return shareRet;
        } catch (SDKException e) {
            LOGGER.error("create nfsShare error, SDKException !", e);
            throw FaultUtil.storageFault();
        }
    }

    private boolean isExistFileSystem(String poolId, String fileSysName, String vvolType) {
        return vvolPathDBService.isExistFileSystem(vvolType, fileSysName);
    }

    private SDKResult<FileSystemCreateResBean> createFilesystem(String vvolType, String vvolId, String poolId,
                                                                long capacity, String fileSystemName, Stack<FinalHandler> finalHandlers) throws StorageFault {
        SDKResult<FileSystemCreateResBean> fileSysRsp = new SDKResult<FileSystemCreateResBean>();
        LOGGER.info("beginning createFilesystem file!!");
        String arrayId = VASAUtil.getArrayId();
        if (!isExistFileSystem(poolId, fileSystemName, vvolType)) {
            LOGGER.info("beginning create filesystem[ " + fileSystemName + "] !!");
            fileSysRsp = doCreateFileSystem(vvolId, fileSystemName, poolId, capacity, VasaConstant.FILE_SYSTEM_THIN, vvolType);
            long errcode = fileSysRsp.getErrCode();
            if (errcode != 0) {
                if (IFileSystemService.FILE_SYS_EXIST != errcode) {
                    LOGGER.error("create fileSystem fail !!");
                    return fileSysRsp;
                } else {
                    LOGGER.info("fileSystem[" + fileSystemName + "] has created,beginning query fileSystem info from array !!");

                    fileSysRsp = queryFileSystemByName(fileSystemName);

                    if (!VasaConstant.VVOL_TYPE_CONFIG.equals(vvolType)) {
                        modifyFIleSystem(arrayId, vvolId, fileSysRsp.getResult().getID(), capacity);
                    }
                }
            } else {
                String fsId = fileSysRsp.getResult().getID();

                LOGGER.info("fileSystem[" + fileSystemName + "]  created succ !!");


                finalHandlers.add(() -> {
                    try {
                        deleteFileSystem(fsId);
                    } catch (Exception e) {
                        LOGGER.error("delete fileSystem fail !!");
                    }
                });
            }
        } else {
            FileSystemTable sysTable = vvolPathDBService.queryFileSystemTableByName(fileSystemName);

            if (sysTable == null) {
                LOGGER.error("find fileSystem[" + fileSystemName + "] failed !!");
                throw FaultUtil.storageFault();
            }

            LOGGER.info("filesystem[ " + fileSystemName + "] exist !!");
            LOGGER.info(sysTable.toString());

            FileSystemCreateResBean filesys = new FileSystemCreateResBean();

            String sysId = dbPrimaryKey2id(sysTable.getId());

            if (!VasaConstant.VVOL_TYPE_CONFIG.equals(vvolType)) {
                modifyFIleSystem(arrayId, vvolId, sysId, capacity);
                LOGGER.info("filesystem[ " + fileSystemName + "] exist !!");
                LOGGER.info("===>   " + arrayId + " " + vvolId + " " + sysId + " " + capacity);

            }
            filesys.setID(sysId);
            filesys.setNAME(sysTable.getFileSystemName());
            fileSysRsp.setResult(filesys);
            fileSysRsp.setErrCode(0);
        }
        LOGGER.info("end createFilesystem file!!");
        return fileSysRsp;
    }


    public SDKErrorCode modifyFIleSystem(String arrayId, String vvolId, String fsId, Long capacity) throws StorageFault {
        LOGGER.info(" modify fileSystem[" + fsId + "] max auto expend size.");

        //Long newSize =Math.round(( calVMAllDataSizeByVvolId(vvolId) + capacity ) * 1024 * 1024 * 1.2) / 512;

        String vmId = null;
        // -99L means modify filesystem for deleting vvol
        if (capacity == -99L) {
            LOGGER.info("modify filesystem for deleting vvol");
            vmId = vvolId;
        } else {
            vmId = VASAUtil.getVmId();
        }

        if (vmId == null || vmId.length() < 4) {
            LOGGER.info("can not find vmId for create data vvol ,try to get vmId by nameSpace.");
            vmId = getVmIdByNameSpace(VASAUtil.getVmwNamespace());
        }

        Long vmAllDataSize;
        if (vmId == null) {
            //认证用例没有vmId和namespace
            vmAllDataSize = capacity;
        } else {
            vmAllDataSize = calVMAllDataSizeByVmId(vmId);
        }
        Long newSize = Math.round(vmAllDataSize * MagicNumber.LONG1024 * MagicNumber.LONG1024 * 1.2) / MagicNumber.LONG512;

        IFileSystemService deviceLunService;
        SDKErrorCode sdkErrorCode = null;
        try {
            deviceLunService = deviceManager.getDeviceServiceProxy(arrayId,
                    IFileSystemService.class);
            sdkErrorCode = deviceLunService.ModifyFileSystemMaxAutosize(arrayId, fsId, newSize);
            if (sdkErrorCode.getErrCode() == 0) {
                LOGGER.info("fileSystem[" + fsId + "] has changed max auto size.");
            } else {
                LOGGER.error("fileSystem[" + fsId + "] changed max auto size error: " + sdkErrorCode.getDescription());
            }
        } catch (SDKException e) {
            LOGGER.error("fileSystem[" + fsId + "] changed max auto size error:" + e);
            throw FaultUtil.storageFault();
        }
        return sdkErrorCode;

    }

    private String getEsxHostId(String hostIp) {
        Map<String, List<String>> tempAllIps = DataUtil.getInstance().getAllHostIps();
        for (Entry<String, List<String>> it : tempAllIps.entrySet()) {
            List<String> values = it.getValue();
            for (String li : values) {
                if (li.equals(hostIp)) {
                    return it.getKey();
                }
            }
        }
        LOGGER.warn("can not find hostId by hostIp[" + hostIp + "].");
        return VasaConstant.HOST_ID_CERTIFICATION;
    }


    private List<String> getHostIps(String vvolType) throws StorageFault {
        List<String> allIps = null;
        String ctlIp = VASAUtil.getCurrEsxIp();
        String hostId = getEsxHostId(ctlIp);
        if (VasaConstant.HOST_ID_CERTIFICATION.equals(hostId)) {
            Map<String, List<String>> tempAllIps = DataUtil.getInstance().getAllHostIps();
            List<String> temp = new ArrayList<String>();
            for (Entry<String, List<String>> it : tempAllIps.entrySet()) {
                temp.addAll(it.getValue());
            }
            allIps = temp;
        } else {
            allIps = DataUtil.getInstance().getHostIpsByEsxHostId(hostId);
        }
        List<String> ips = new ArrayList<String>();
        allIps = VASAUtil.removeDuplicate(allIps);
        for (String ip : allIps) {
            if (VASAUtil.isIPV4(ip)) {
                ips.add(ip);
            }
        }

        LOGGER.info("hostIps" + ips.toString());

        if (ips.size() == 0) {
            ips.add("*");
        }
        return ips;
    }

    private String list2String(List<String> list, String separator) {
        if (list == null || list.size() == 0) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i)).append(separator);
        }
        return sb.toString().substring(0, sb.toString().length() - 1);
    }

    public SDKResult<NFSvvolCreateResBean> operateFile(String vvolId, String vvolType, long capacity, String cmd, String fileSystemName, StringBuilder outSrcPath)
            throws StorageFault {

        SDKResult<NFSvvolCreateResBean> vvolInfo = null;
        if (outSrcPath == null) {
            LOGGER.error("null para ! outSrcPath is null");
            throw FaultUtil.storageFault("null para !");
        }

        try {
            LOGGER.debug("begin operateFile");
            String arrayId = VASAUtil.getArrayId();

            INFSvvolService deviceNasService;
            deviceNasService = deviceManager.getDeviceServiceProxy(arrayId,
                    INFSvvolService.class);

            LOGGER.debug("deviceNasService = " + deviceNasService);

            String srcPathCommon;
            String srcPath;
            String pathCommon = null;
            String path = null;
            if (INFSvvolService.operate_create.equals(cmd) ||
                    INFSvvolService.operate_mkdir.equals(cmd)) {
                srcPathCommon = "/" + fileSystemName + "/" + VasaConstant.VVOL_PREFIX + vvolId;
                pathCommon = VasaConstant.VVOL_PREFIX;
            } else {
                srcPathCommon = "/" + fileSystemName + "/" + vvolId;
                pathCommon = "";
            }

            if (VasaConstant.VVOL_TYPE_CONFIG.equals(vvolType)) {
                srcPath = srcPathCommon;
                path = pathCommon + vvolId;
                vvolInfo = deviceNasService.operateNFSvvol(arrayId, cmd, srcPath, null, capacity, null);
            } else if (VasaConstant.VVOL_TYPE_DATA.equals(vvolType)) {
                srcPath = srcPathCommon + VasaConstant.VVOL_DATA_REAR;
                path = pathCommon + vvolId + VasaConstant.VVOL_DATA_REAR;
                vvolInfo = deviceNasService.operateNFSvvol(arrayId, cmd, srcPath, null, capacity, null);
            } else if (VasaConstant.VVOL_TYPE_SWAP.equals(vvolType)) {
                srcPath = srcPathCommon + VasaConstant.VVOL_SWAP_REAR;
                path = pathCommon + vvolId + VasaConstant.VVOL_SWAP_REAR;
                vvolInfo = deviceNasService.operateNFSvvol(arrayId, cmd, srcPath, null, capacity, null);
            } else if (VasaConstant.VVOL_TYPE_MEMORY.equals(vvolType)) {
                srcPath = srcPathCommon + VasaConstant.VVOL_MEM_REAR;
                path = pathCommon + vvolId + VasaConstant.VVOL_MEM_REAR;
                vvolInfo = deviceNasService.operateNFSvvol(arrayId, cmd, srcPath, null, capacity, null);
            } else if (VasaConstant.VVOL_TYPE_OTHER.equals(vvolType)) {
                srcPath = srcPathCommon + VasaConstant.VVOL_OTHER_REAR;
                path = pathCommon + vvolId + VasaConstant.VVOL_OTHER_REAR;
                vvolInfo = deviceNasService.operateNFSvvol(arrayId, cmd, srcPath, null, capacity, null);
            } else {
                LOGGER.error("unknown file type !" + vvolType);
                throw FaultUtil.storageFault("unknown file type !");
            }
            outSrcPath.append(path);
        } catch (SDKException e) {
            LOGGER.error("createFile error, SDKException !", e);
            throw FaultUtil.storageFault();
        }

        return vvolInfo;
    }


    private void insertFilesystemInfo(FileSystemTable sysInfo, Stack<FinalHandler> finalHandlers) {
        vvolPathDBService.insertFilesystemTable(sysInfo);
        finalHandlers.add(
                () -> {
                    vvolPathDBService.deleteFilesystemTableByName(sysInfo.getFileSystemName());
                }
        );
    }


    private void insertShareInfo(FilesystemShareTable shareInfo, List<ShareClientTable> clients
            , Stack<FinalHandler> finalHandlers) {
//        vvolPathDBService.insertFilesystemTable(sysInfo);
//        finalHandlers.add(
//                ()->{
//                    vvolPathDBService.deleteFilesystemTableByName(sysInfo.getFileSystemName());
//                }
//                );
        vvolPathDBService.insertShareRecord(shareInfo);
        finalHandlers.add(
                () -> {
                    vvolPathDBService.deleteShareByShareName(shareInfo.getSharePath());
                }
        );

        for (ShareClientTable cli : clients) {
            vvolPathDBService.insertShareClientRecord(cli);
            finalHandlers.add(
                    () -> {
                        vvolPathDBService.deleteShareClientByShareId(cli.getShareId());
                    }
            );
        }
    }

    private boolean needModifyFileSystemInfo(String fileSystemName) {

        FileSystemTable sys = vvolPathDBService.queryFileSystemTableByName(fileSystemName);
        if (sys != null) {
            FilesystemShareTable share = vvolPathDBService.queryShareByShareName("/" + fileSystemName + "/");
            if (share != null) {
                List<ShareClientTable> clients = vvolPathDBService.queryShareClientByShareId(share.getShareId());
                if (clients != null) {
                    return clients.isEmpty();
                }

            }
        }

        return true;
    }

    private String id2DBPrimaryKey(String id) throws StorageFault {
        String arrayId = VASAUtil.getArrayId();
        return id + VasaConstant.SEPARATOR + arrayId;
    }

    private String dbPrimaryKey2id(String id) throws StorageFault {
        String arrayId = VASAUtil.getArrayId();
        return id.replaceAll(VasaConstant.SEPARATOR + arrayId, "").trim();
    }


    private String getHostIdByVvolType(String vvolType) throws StorageFault {
//        if(vvolType.equals(VasaConstant.VVOL_TYPE_CONFIG) || vvolType.equals(VasaConstant.VVOL_TYPE_SWAP))
//        {
//            return "*";
//        }
        String ctlIp = VASAUtil.getCurrEsxIp();
        String hostId = getEsxHostId(ctlIp);
        return hostId;
    }

    private ShareInfo createNfsShare(String vvolType, String vvolId, String fileSystemName, String fsId, String shareName,
                                     Stack<FinalHandler> finalHandlers) throws StorageFault {

//        LOGGER.info("beginning createNfsShare file!!");
//        SDKResult<FileSystemCreateResBean> fileSys =  createFilesystem(vvolType,vvolId,poolId,capacity,fileSystemName,finalHandlers);
//        if(fileSys.getErrCode() != 0)
//        {
//            LOGGER.error("create filesystem fail !!");
//            throw FaultUtil.storageFault("create filesystem fail !!");
//        }
//        FileSystemTable sysInfo = new FileSystemTable(fileSystemName, id2DBPrimaryKey(sys.getID()),
//                FileSystemTable.MAX_CAPACITY, String.valueOf(capacity), "0");
        SDKResult<NFSshareCreateResBean> shareRet = doCreateNfsShare(vvolType, fsId, shareName, finalHandlers);
        if (shareRet.getErrCode() != 0) {
            LOGGER.error("create share fail !!" + shareRet.getDescription());
            throw FaultUtil.storageFault("create share fail !!");
        }

        String hostId = getHostIdByVvolType(vvolType);
        FilesystemShareTable shareInfo = new FilesystemShareTable(id2DBPrimaryKey(fsId),
                shareRet.getResult().getSHAREPATH(), id2DBPrimaryKey(shareRet.getResult().getID()), hostId);

        List<ShareClientTable> clients = new ArrayList<ShareClientTable>();
        SDKErrorCode clientRet = createClients(vvolType, getHostIps(vvolType), INFSshareService.ACCESSVAL_READ_WRITE, INFSshareService.SYNC,
                INFSshareService.ALLSQUASH_NO_ALL, INFSshareService.ROOTSQUASH_NO_ROOT, shareRet, clients, finalHandlers);
        if (clientRet.getErrCode() != 0) {
            LOGGER.error("create clients fail !!" + clientRet.getDescription());
            throw FaultUtil.storageFault("create clients fail !!");
        }

        //濉暟鎹簱
        insertShareInfo(shareInfo, clients, finalHandlers);
        LOGGER.info("end createNfsShare file!!");
        ShareInfo share = new ShareInfo(shareRet.getResult().getID(), shareRet.getResult().getSHAREPATH(), null);
        return share;
    }


    private SDKResult<NFSshareCreateResBean> doQueryNfsShare(String vvolType, String fsId,
                                                             String shareName, Stack<FinalHandler> finalHandlers) throws StorageFault {
        LOGGER.info("beginning doQueryNfsShare !!");
        if (fsId == null || shareName == null) {
            LOGGER.error("[doQueryNfsShare] the parameter shareName or fsId is null !!");
            throw FaultUtil.storageFault();
        }

        if (isExistShare("/" + shareName + "/")) {
            LOGGER.info("share is exist !!");
            SDKResult<NFSshareCreateResBean> ret = new SDKResult<NFSshareCreateResBean>();
            FilesystemShareTable share = vvolPathDBService.queryShareByShareName("/" + shareName + "/");// nd_todo优化
            if (share == null) {
                LOGGER.error("query share from db fail ! share is null");
                throw FaultUtil.storageFault("query share fail !");
            }
            LOGGER.info(share.toString());
            NFSshareCreateResBean retBean = new NFSshareCreateResBean();
            retBean.setSHAREPATH(share.getSharePath());
            retBean.setID(dbPrimaryKey2id(share.getShareId()));
            ret.setResult(retBean);
            ret.setErrCode(0);
            return ret;
        }
        LOGGER.info("query nfs share: " + ",filesystemName = " + shareName);
        INFSshareService deviceNfsSharedService;
        try {
            String arrayId = VASAUtil.getArrayId();
            deviceNfsSharedService = deviceManager.getDeviceServiceProxy(arrayId,
                    INFSshareService.class);

            SDKResult<NFSshareCreateResBean> shareRet = deviceNfsSharedService.queryShare(arrayId, "/" + shareName + "/", null);
            if (shareRet.getErrCode() != 0) {
                LOGGER.error("query nfsShare fail !" + shareRet.getDescription());
                throw FaultUtil.storageFault("query nfsShare fail.");
            }
            String shareId = shareRet.getResult().getID();
            finalHandlers.add(() -> {
                deviceNfsSharedService.deleteShare(arrayId, shareId, null);
            });
            LOGGER.info("end doQueryNfsShare !!");
            return shareRet;
        } catch (SDKException e) {
            LOGGER.error("query nfsShare error, SDKException !", e);
            throw FaultUtil.storageFault();
        }
    }


    private SDKErrorCode queryClients(String vvolType, List<String> hostIps,
                                      String accessval, String sync, String allSquash, String rootSquash,
                                      SDKResult<NFSshareCreateResBean> share, List<ShareClientTable> clients, Stack<FinalHandler> finalHandlers) throws StorageFault {
        try {
            String arrayId = VASAUtil.getArrayId();
            INFSshareService deviceNfsSharedService;
            LOGGER.info("beginning queryClients clients ! ");
            deviceNfsSharedService = deviceManager.getDeviceServiceProxy(arrayId,
                    INFSshareService.class);
            String shareId = share.getResult().getID();
            List<String> newIps = filterShuoldCreateAuthList(hostIps, shareId);
            boolean isSuccess = false;
            for (String ip : newIps) {

                SDKResult<List<AddAuthClientResBean>> clientRsps = new SDKResult<>();
                clientRsps = deviceNfsSharedService.queryAuthClient(arrayId, null, share.getResult().getID(), ip);
                AddAuthClientResBean temp = new AddAuthClientResBean();
                for (AddAuthClientResBean cli : clientRsps.getResult()) {
                    if (shareId.equals(cli.getPARENTID())) {
                        temp = cli;
                        isSuccess = true;
                        finalHandlers.add(() -> {
                            LOGGER.info("clear client IP[ " + ip + "] success!");
                        });
                        clients.add(new ShareClientTable(id2DBPrimaryKey(ip + VasaConstant.SEPARATOR + temp.getID()), id2DBPrimaryKey(shareId)));
                        break;
                    }
                }

            }
            SDKErrorCode ret = new SDKErrorCode();
            if (isSuccess || newIps.isEmpty()) {
                ret.setErrCode(0);
            } else {
                ret.setErrCode(-1);
            }

            LOGGER.info("end queryClients clients ! ");
            return ret;
        } catch (SDKException e) {
            LOGGER.error("create queryClients error, SDKException !", e);
            throw FaultUtil.storageFault();
        }
    }

    private ShareInfo queryNfsShareFromArray(String vvolType, String vvolId, String fileSystemName, String fsId, String shareName,
                                             Stack<FinalHandler> finalHandlers) throws StorageFault {
        LOGGER.info("beginning queryNfsShareFromArray file!!");
        SDKResult<NFSshareCreateResBean> shareRet = doQueryNfsShare(vvolType, fsId, shareName, finalHandlers);
        if (shareRet.getErrCode() != 0) {
            LOGGER.error("query share fail !!");
            throw FaultUtil.storageFault("query share fail !!");
        }

        String hostId = getHostIdByVvolType(vvolType);
        FilesystemShareTable shareInfo = new FilesystemShareTable(id2DBPrimaryKey(fsId),
                shareRet.getResult().getSHAREPATH(), id2DBPrimaryKey(shareRet.getResult().getID()), hostId);

        List<ShareClientTable> clients = new ArrayList<ShareClientTable>();
        SDKErrorCode clientRet = queryClients(vvolType, getHostIps(vvolType), INFSshareService.ACCESSVAL_READ_WRITE, INFSshareService.SYNC,
                INFSshareService.ALLSQUASH_NO_ALL, INFSshareService.ROOTSQUASH_NO_ROOT, shareRet, clients, finalHandlers);
        if (clientRet.getErrCode() != 0) {
            LOGGER.error("query clients fail !!");
            throw FaultUtil.storageFault("query clients fail !!");
        }

        //填数据库
        insertShareInfo(shareInfo, clients, finalHandlers);
        LOGGER.info("end queryNfsShareFromArray file!!");
        ShareInfo share = new ShareInfo(shareRet.getResult().getID(), shareRet.getResult().getSHAREPATH(), null);
        return share;
    }

    private SDKResult<NFSvvolCreateResBean> handleCreateFile(String vvolType, String vvolId, String poolId, long capacity,
                                                             String fileSystemName, StringBuilder srcPath, Stack<FinalHandler> finalHandlers) throws StorageFault {
        LOGGER.info("beginning handleCreateFile file!!");
        if (srcPath == null || finalHandlers == null) {
            LOGGER.error("null para !! srcPath is null");
            throw FaultUtil.storageFault("null para !!");
        }
        SDKResult<NFSvvolCreateResBean> vvolInfo = null;
        if (VasaConstant.VVOL_TYPE_CONFIG.equals(vvolType)) {
            vvolInfo = operateFile(vvolId, vvolType, capacity, INFSvvolService.operate_mkdir, fileSystemName, srcPath);
        } else {
            vvolInfo = operateFile(vvolId, vvolType, capacity, INFSvvolService.operate_create, fileSystemName, srcPath);
        }

        if (vvolInfo.getErrCode() == 0) {
            finalHandlers.add(() -> {
                try {
                    doDeleteFile(VasaConstant.VVOL_PREFIX + vvolId, vvolType, fileSystemName);
                } catch (Exception e) {
                    LOGGER.error("deleteFile error, SDKException !", e);
                }
            });
        }
        LOGGER.info("end handleCreateFile file!!");
        return vvolInfo;
    }

    private FileSystemInfo getFileSystemInfoByFSName(String fsName) throws StorageFault {
        FileSystemTable fsInfo = vvolPathDBService.queryFileSystemTableByName(fsName);
        FilesystemShareTable share = vvolPathDBService.queryShareByShareName("/" + fsName + "/");
        ShareInfo shareInfo = new ShareInfo(dbPrimaryKey2id(share.getShareId()), share.getSharePath(), null);
        return new FileSystemInfo(dbPrimaryKey2id(fsInfo.getId()), fsInfo.getFileSystemName(), shareInfo);
    }

    private synchronized FileSystemInfo initConfig(String vvolType, String vvolId, String poolId, long capacity,
                                                   Stack<FinalHandler> finalHandlers) throws StorageFault {
        String arrayId = VASAUtil.getArrayId();
        FileSystemInfo ret = null;
        String preName = VasaConstant.FILE_SYSTEM_TYPE_COMMON + VASAUtil.UUId2FileSystemName(arrayId);
        String fileSystemName = getCommonSystemName(preName, vvolType);
        LOGGER.info("beginning init file!!");

        if (fileSystemName != null) {
            LOGGER.info("find filesystem [" + fileSystemName + "] in db!!");

            if (needModifyFileSystemInfo(fileSystemName)) {
                ret = createFSAndShare(vvolType, vvolId, poolId, capacity, fileSystemName, finalHandlers);
            } else {
                ret = getFileSystemInfoByFSName(fileSystemName);
            }
            return ret;
        }

        if (fileSystemName == null) {
            for (int i = 0; i < VasaConstant.COMMON_FILE_SYSTEM_SIZE; i++) {
                fileSystemName = preName + "_" + i;
                ret = createFSAndShare(vvolType, vvolId, poolId, capacity, fileSystemName, finalHandlers);
            }
        }
        //deleteTempShare(arrayId);
        LOGGER.info("init filesystem [" + fileSystemName + "]!!");
        return ret;
    }


    private void fullVvolPath(String vvolId, FileSystemInfo fsInfo, String srcPath, VvolPath vvolPath) throws StorageFault {
        if (vvolPath == null || fsInfo == null || fsInfo.getShareInfo() == null) {
            LOGGER.error("get fullPath error,para is null");
            return;
        }
        vvolPath.setVvolid(VasaConstant.VVOL_PREFIX + vvolId);
        vvolPath.setPath(srcPath);
        vvolPath.setBind(false);
        vvolPath.setFileSystemName(fsInfo.getFilesystemName());
        vvolPath.setSharePath(fsInfo.getShareInfo().getSharePath());
        vvolPath.setFileSystemId(fsInfo.getFilesystemId());
        vvolPath.setShareId(fsInfo.getShareInfo().getShareId());
    }

    private SDKResult<NFSvvolCreateResBean> doCreateCommonFile(String vvolType, String vvolId, String poolId, long capacity) throws StorageFault {
        Stack<FinalHandler> finalHandlers = new Stack<FinalHandler>();
        boolean shouldClear = true;
        try {
            VvolPath vvolPath = new VvolPath();
            LOGGER.info("beginning doCreateCommonFile file!!");
            FileSystemInfo fsInfo = initConfig(vvolType, vvolId, poolId, capacity, finalHandlers);
            String fileSystemName = fsInfo.getFilesystemName();
            StringBuilder srcPath = new StringBuilder();
            if (fileSystemName == null) {
                LOGGER.error("get fileSystemName [" + vvolType + "] fail !!");
                throw FaultUtil.storageFault("get fileSystemName [" + vvolType + "] fail !!");
            }
            SDKResult<NFSvvolCreateResBean> vvolInfo = handleCreateFile(vvolType, vvolId, poolId, capacity,
                    fileSystemName, srcPath, finalHandlers);

            if (vvolInfo.getErrCode() != 0 && VasaConstant.VVOL_TYPE_CONFIG.equals(vvolType)) {
                return changeOtherFS(fileSystemName, vvolType, vvolId, poolId, capacity);
            }

            RETRYTIMES.set(0);
            fullVvolPath(vvolId, fsInfo, srcPath.toString(), vvolPath);
            vvolPathDBService.insertRecord(vvolPath);
//            vvolPathDBService.updateFileCount(fileSystemName,1);// nd_todo 寮傚父娓呯悊
//            vvolPathDBService.updateCurrCapacity(fileSystemName, capacity);
            vvolPathDBService.updateFileCountAndCapacity(fileSystemName, 1, capacity);
            LOGGER.info("insert vvol to datebase, id=" + fileSystemName);
            finalHandlers.add(() -> {
                vvolPathDBService.deleteRecordByVvolId(vvolPath.getVvolid());
                vvolPathDBService.updateFileCount(fileSystemName, -1);
                vvolPathDBService.updateCurrCapacity(fileSystemName, capacity * -1);
            });
            shouldClear = false;
            return vvolInfo;
        } catch (Exception e) {
            LOGGER.error("end doCreateCommonFile file fail, exec clear func!!" + e);
            throw e;
        } finally {
            if (shouldClear) {
                while (!finalHandlers.isEmpty()) {
                    LOGGER.info("do clear......");
                    FinalHandler finalHander = finalHandlers.pop();
                    finalHander.onFilan();
                }
                if (RETRYTIMES.get() != 0) {
                    deleteVirtualVolumeFromDatabase(VasaConstant.VVOL_PREFIX + vvolId);
                    RETRYTIMES.set(0);
                }

            }
        }
    }

    private synchronized SDKResult<NFSvvolCreateResBean> changeOtherFS(String fileSystemName, String vvolType, String vvolId, String poolId, long capacity) throws StorageFault {

        LOGGER.info("FS :" + fileSystemName + " is unusable");
        updateFSStatus(fileSystemName, FileSystemTable.OFFLINE);

        if (RETRYTIMES.get() < 4) {
            RETRYTIMES.set(RETRYTIMES.get() + 1);
            LOGGER.info("create config vvol failed, try to use another FS,retry times = " + RETRYTIMES.get());
            return doCreateCommonFile(vvolType, vvolId, poolId, capacity);
        } else {
            LOGGER.info("create config vvol failed, and retry times is 5");
            throw FaultUtil.storageFault("create config vvol failed, and retry times is 5");
        }
    }

    private void updateFSStatus(String fileSystemName, String status) {
        vvolPathDBService.updateStatus(fileSystemName, status);
    }

    private FileSystemInfo createFSAndShare(String vvolType, String vvolId, String poolId, long capacity,
                                            String fileSystemName, Stack<FinalHandler> finalHandlers) throws StorageFault {
        LOGGER.info("beginning createFSAndShare.");

        SDKResult<FileSystemCreateResBean> fileSys = createFilesystem(vvolType, vvolId, poolId, capacity, fileSystemName, finalHandlers);
        if (fileSys.getErrCode() != 0) {
            LOGGER.error("create filesystem fail !!" + fileSys.getDescription());
            throw FaultUtil.storageFault("create filesystem fail !!");
        }

        FileSystemTable systable = new FileSystemTable(fileSystemName, id2DBPrimaryKey(fileSys.getResult().getID()),
                FileSystemTable.MAX_CAPACITY, String.valueOf(capacity), "0");

        FileSystemCreateResBean sys = fileSys.getResult();

        ShareInfo share = createNfsShare(vvolType, vvolId, fileSystemName, sys.getID(), fileSystemName, finalHandlers);
        FileSystemInfo sysInfo = new FileSystemInfo(fileSys.getResult().getID(), fileSys.getResult().getNAME(), share);
        LOGGER.info("beginning createFSAndShare.");
        insertFilesystemInfo(systable, finalHandlers);
        return sysInfo;
    }

    private SDKResult<NFSvvolCreateResBean> doCreateFile(String vvolType, String vvolId, String poolId, long capacity, String fileSystemName) throws StorageFault {
        Stack<FinalHandler> finalHandlers = new Stack<FinalHandler>();
        boolean shouldClear = true;
        try {
            LOGGER.info("beginning doCreateFile file!!");
            VvolPath vvolPath = new VvolPath();

            FileSystemInfo fsInfo = createFSAndShare(vvolType, vvolId, poolId, capacity, fileSystemName, finalHandlers);

            StringBuilder srcPath = new StringBuilder();
            SDKResult<NFSvvolCreateResBean> vvolInfo = handleCreateFile(vvolType, vvolId, poolId, capacity,
                    fileSystemName, srcPath, finalHandlers);

            fullVvolPath(vvolId, fsInfo, srcPath.toString(), vvolPath);
            vvolPathDBService.insertRecord(vvolPath);
//            vvolPathDBService.updateFileCount(fileSystemName,1);// nd_todo 寮傚父娓呯悊
//            vvolPathDBService.updateCurrCapacity(fileSystemName, capacity);
            vvolPathDBService.updateFileCountAndCapacity(fileSystemName, 1, capacity);
            LOGGER.info("insert vvol to datebase, id=" + fileSystemName);
            finalHandlers.add(() -> {
                vvolPathDBService.deleteRecordByVvolId(vvolPath.getVvolid());
                vvolPathDBService.updateFileCount(fileSystemName, -1);
                vvolPathDBService.updateCurrCapacity(fileSystemName, capacity * -1);
            });
            shouldClear = false;
            LOGGER.info("end doCreateFile file!!");
            return vvolInfo;
        } catch (Exception e) {
            LOGGER.error("end doCreateFile file fail, exec clear func!!" + e);
            throw e;
        } finally {
            if (shouldClear) {
                while (!finalHandlers.isEmpty()) {
                    LOGGER.info("do clear......");
                    FinalHandler finalHander = finalHandlers.pop();
                    finalHander.onFilan();
                }
                deleteVirtualVolumeFromDatabase(VasaConstant.VVOL_PREFIX + vvolId);
            }
        }
    }

    private String getCommonSystemName(String preName, String vvolType) throws StorageFault {
        List<FileSystemTable> systems = vvolPathDBService.queryFileSystemTableByFuzzySystemName(preName);
        if (systems == null || systems.size() == 0) {
            return null;
        }
        boolean nullAvailable = true;
        for (int i = 0; i < systems.size(); i++) {
            if (systems.get(i).getStatus().equals(FileSystemTable.NORMAL)) {
                nullAvailable = false;
            }
        }
        if (nullAvailable) {
            LOGGER.error("null COMMON FS ONLINE");
            throw FaultUtil.storageFault("null COMMON FS ONLINE");
        }
        int minFileCount = Integer.MAX_VALUE;
        FileSystemTable temp = new FileSystemTable();
        for (int i = 0; i < systems.size(); i++) {
            if (minFileCount > Integer.valueOf(systems.get(i).getFileCount()) && systems.get(i).getStatus().equals(FileSystemTable.NORMAL)) {
                minFileCount = Integer.valueOf(systems.get(i).getFileCount());
                temp = systems.get(i);
            }
        }
        return temp.getFileSystemName();
    }

    public SDKResult<NFSvvolCreateResBean> createFile(String vvolType, String vvolId, String poolId, long capacity) throws StorageFault {
        SDKResult<NFSvvolCreateResBean> fileRsp = null;

        LOGGER.info("beginning create file.");
        LOGGER.info("create file: vvolType = " + vvolType + ",vvolId = " + vvolId +
                ",poolId = " + poolId + ",capacity = " + capacity);
        try {
            if (VasaConstant.VVOL_TYPE_CONFIG.equals(vvolType)) {
                LOGGER.info("create config file.");
                RETRYTIMES.set(0);
                fileRsp = doCreateCommonFile(vvolType, vvolId, poolId, capacity);
            } else if (VasaConstant.VVOL_TYPE_DATA.equals(vvolType)) {
                String fsNameOfData = getFSNameOfDataByNameSpace(VASAUtil.getVmwNamespace());
                String fileSystemName = VasaConstant.FILE_SYSTEM_PREFIX;
                if (fsNameOfData == null) {
                    fileSystemName += VASAUtil.UUId2FileSystemName(vvolId);
                } else {
                    fileSystemName += VASAUtil.UUId2FileSystemName(fsNameOfData);
                }
                LOGGER.info("create [" + vvolType + "] file.");
                fileRsp = doCreateFile(vvolType, vvolId, poolId, capacity, fileSystemName);
            } else if (VasaConstant.VVOL_TYPE_MEMORY.equals(vvolType)) {
                LOGGER.info("create memory file.");
                fileRsp = doCreateCommonFile(vvolType, vvolId, poolId, capacity);
            } else if (VasaConstant.VVOL_TYPE_SWAP.equals(vvolType)) {
                LOGGER.info("create swap file.");
                fileRsp = doCreateCommonFile(vvolType, vvolId, poolId, capacity);
            } else if (VasaConstant.VVOL_TYPE_OTHER.equals(vvolType)) {
                String fileSystemName = VasaConstant.FILE_SYSTEM_TYPE_OTHER + VASAUtil.UUId2FileSystemName(vvolId);
                LOGGER.info("create other file.");
                fileRsp = doCreateFile(vvolType, vvolId, poolId, capacity, fileSystemName);
            } else {
                LOGGER.error("error vvolType " + vvolType);
                throw FaultUtil.storageFault("error vvolType !!");
            }
            LOGGER.info("end create file.");
            vvolPathDBService.setBindState(VasaConstant.VVOL_PREFIX + vvolId, false);
            LOGGER.info("set bing state " + false);
            return fileRsp;
        } catch (Exception e) {
            LOGGER.error("create file fail !!" + e);
            throw FaultUtil.storageFault("create file fail !!");
        }
    }

    private String getFSNameOfDataByNameSpace(String nameSpace) {
        LOGGER.info("find FSNameOfData vvol nameSpace : " + nameSpace);
        if (nameSpace == null || nameSpace.length() < 44) {
            LOGGER.warn("can not find FSNameOfData vvol nameSpace : " + nameSpace);
            return null;
        }
        String configVvolID = nameSpace.substring(nameSpace.length() - 44, nameSpace.length());
        LOGGER.info("config id: " + configVvolID);

        return configVvolID;
    }

    private String getVmIdByNameSpace(String nameSpace) {
        LOGGER.info("find vmId vvol nameSpace : " + nameSpace);
        String vmId = null;
        LOGGER.info("find FSNameOfData vvol nameSpace : " + nameSpace);
        if (nameSpace == null || nameSpace.length() < 44) {
//            LOGGER.error("can not find FSNameOfData vvol nameSpace : "+nameSpace);
            //跑认证时，不会携带namespace,这里直接返回，外层处理
            LOGGER.warn("can not find vmId vvol nameSpace : " + nameSpace);
            return null;
        }
        String configVvolID = nameSpace.substring(nameSpace.length() - 44, nameSpace.length());
        LOGGER.info("config id: " + configVvolID);
        try {
            NVirtualVolume configVvol = virtualVolumeService.getVirtualVolumeByVvolId(configVvolID);
            //LOGGER.info("config vvol: "+configVvol.getVmName());
            if (configVvol != null) {
                vmId = configVvol.getVmId();
            }
            //LOGGER.info("config vmid: "+vmId);
            //LOGGER.info("get vmId by name Space configId : "+configVvolID +" VMid:"+vmId);
        } catch (StorageFault storageFault) {
            LOGGER.error("get vvolid from database error" + storageFault.toString());
        }
        return vmId;

    }

    private SDKResult<FileSystemCreateResBean> doCreateFileSystem(String vvolid, String name, String poolId,
                                                                  long capacity, String alloctype, String vvolType) throws StorageFault {
        IFileSystemService deviceLunService;
        try {
            String arrayId = VASAUtil.getArrayId();
            StorageInfo queryInfoByArrayId = storageManagerService.queryInfoByArrayId(arrayId);
            String thinThickSupport = DeviceTypeMapper.thinThickSupport(queryInfoByArrayId.getModel());
            if (null != thinThickSupport) {
                LOGGER.info("Current productmode have special thin/thick. Mode=" + queryInfoByArrayId.getModel() + ",thinThickSupport=" + thinThickSupport);
            }
            LOGGER.info("beginning doCreateFileSystem ! ");
            deviceLunService = deviceManager.getDeviceServiceProxy(arrayId,
                    IFileSystemService.class);
            //棣栧厛鏂囦欢绯荤粺
            LOGGER.info("createFileSystem = " + deviceLunService);
            LOGGER.info("arrayId = " + arrayId +
                    ", name = " + name +
                    ", poolId = " + poolId +
                    ", capacity = " + capacity +
                    ", alloctype = " + alloctype + ", sectorSize = " + 8192);
            LOGGER.info("end doCreateFileSystem ! ");

            if (VasaConstant.VVOL_TYPE_CONFIG.equals(vvolType) || VasaConstant.VVOL_TYPE_SWAP.equals(vvolType)) {
                return deviceLunService.createFileSystem(arrayId, name, poolId, COMMONFSINITSIZE,
                        alloctype, COMMONSECTORSIZE, SPACESELFADJUSTINGMODE, SNAPSHOTRESERVEPER, OWNINGCONTROLLER, ISCLONEFS, null,
                        AUTOSHRINKTHRESHOLDPERCENT, AUTOGROWTHRESHOLDPERCENT, COMMONMAXAUTOSIZ, AUTOSIZEINCREMENT);
            } else {
                // 鏂囦欢绯荤粺鍒涘缓鐨勬渶灏忓閲忓ぇ灏忎负1024MB銆�
                if (capacity < MagicNumber.LONG1024) {
                    LOGGER.info("vm data capacity is smaller than 1024MB, change it to 1024MB.");
                    capacity = MagicNumber.LONG1024;
                }

                // 鏂囦欢绯荤粺鑷姩鎵╁涓婇檺澶у皬涓烘枃浠剁郴缁熷ぇ灏忕殑1.2鍊嶃��
                Long newSize = Math.round((calVMAllDataSizeByVvolId(vvolid) + capacity) * MagicNumber.LONG1024 * MagicNumber.LONG1024 * 1.2) / MagicNumber.LONG512;

                return deviceLunService.createFileSystem(arrayId, name, poolId, capacity, alloctype, DATASECTORSIZE,
                        SPACESELFADJUSTINGMODE, SNAPSHOTRESERVEPER, OWNINGCONTROLLER, ISCLONEFS, null, AUTOSHRINKTHRESHOLDPERCENT,
                        AUTOGROWTHRESHOLDPERCENT, newSize, AUTOSIZEINCREMENT);
            }

        } catch (SDKException e) {
            LOGGER.error("createFileSystem error, SDKException !", e);
            throw FaultUtil.storageFault();
        }

    }

    public long calVMAllDataSizeByVvolId(String vvolId) {
        LOGGER.info("get all vm data size by vvolid " + vvolId);
        long currentSize = 0;
        try {
            currentSize = virtualVolumeService.getVMAllDataSizeByVvolID(vvolId);
            LOGGER.info("get all vm data size is " + currentSize);
        } catch (StorageFault storageFault) {
            LOGGER.info("get all vm data size error " + storageFault);
            storageFault.printStackTrace();
        }
        return currentSize;
    }

    public long calVMAllDataSizeByVmId(String vmId) {
        LOGGER.info("get all vm data size by vmId " + vmId);
        long currentSize = 0;
        if (vmId == null) {
            return currentSize;
        }
        try {
            currentSize = virtualVolumeService.getVMAllDataSizeByVmId(vmId);
            LOGGER.info("get all vm data size is " + currentSize);
        } catch (StorageFault storageFault) {
            LOGGER.error("get all vm data size error " + storageFault);
            storageFault.printStackTrace();
        }
        return currentSize;
    }

    public SDKResult<FileSystemCreateResBean> queryFileSystemByName(String name) throws StorageFault {
        IFileSystemService deviceLunService;
        try {
            String arrayId = VASAUtil.getArrayId();
            LOGGER.info("beginning queryFileSystem ! ");
            deviceLunService = deviceManager.getDeviceServiceProxy(arrayId,
                    IFileSystemService.class);
            //棣栧厛鏂囦欢绯荤粺
            LOGGER.info("queryFileSystem = " + deviceLunService);
            LOGGER.info("arrayId = " + arrayId +
                    ", filesystemName = " + name);
            LOGGER.info("end queryFileSystem ! ");
            return deviceLunService.queryFileSystemByName(arrayId, name);
        } catch (SDKException e) {
            LOGGER.error("queryFileSystem error, SDKException !", e);
            throw FaultUtil.storageFault();
        }

    }
    /*
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     * */

    private String path2vvolId(String path, String vvolType) {
        if (vvolType.equals(VasaConstant.VVOL_TYPE_DATA)) {
            return path.replaceAll(VasaConstant.VVOL_DATA_REAR, "");
        } else if (vvolType.equals(VasaConstant.VVOL_TYPE_MEMORY)) {
            return path.replaceAll(VasaConstant.VVOL_MEM_REAR, "");
        } else if (vvolType.equals(VasaConstant.VVOL_TYPE_SWAP)) {
            return path.replaceAll(VasaConstant.VVOL_SWAP_REAR, "");
        } else if (vvolType.equals(VasaConstant.VVOL_TYPE_OTHER)) {
            return path.replaceAll(VasaConstant.VVOL_OTHER_REAR, "");
        } else {
            return path;
        }
    }

    private SDKResult<NFSvvolCreateResBean> doDeleteFile(String vvolId, String vvolType, String fileSystemName) throws StorageFault {
        SDKResult<NFSvvolCreateResBean> vvolInfo = null;
        LOGGER.info("begin doDeleteFile.");
        StringBuilder outSrcPath = new StringBuilder();
        VvolPath vvolPath = vvolPathDBService.getVvolPathByVvolId(vvolId);
        LOGGER.info("delete vvolId:" + vvolId);
        vvolId = path2vvolId(vvolPath.getPath(), vvolType);
        LOGGER.info("real delete file or dirc:" + vvolId + ".");
        if (VasaConstant.VVOL_TYPE_CONFIG.equals(vvolType)) {
            vvolInfo = operateFile(vvolId, vvolType, 0, INFSvvolService.operate_rmdir, fileSystemName, outSrcPath);
        } else {
            vvolInfo = operateFile(vvolId, vvolType, 0, INFSvvolService.operate_remove, fileSystemName, outSrcPath);
        }

        return vvolInfo;

    }

    private synchronized SDKErrorCode deleteFileSystemOfData(String arrayId, VvolPath vvolPath) throws StorageFault {
        SDKErrorCode ret = new SDKErrorCode();
        ret.setErrCode(0);
        if (vvolPathDBService.getCountRecordByFileSystemName(vvolPath.getFileSystemName()) > 1) {
            return ret;
        }
        // 鍒犻櫎鍏变韩
        ret = deleteShare(arrayId, vvolPath.getShareId());
        if (ret.getErrCode() != 0 && ret.getErrCode() != INFSshareService.SHARE_NOT_EXIST) {
            LOGGER.error("delete share fail !!");
            throw FaultUtil.storageFault("delete share error.");
        }

        // 鍒犻櫎鏂囦欢绯荤粺
        ret = deleteFileSystem(vvolPath.getFileSystemId());
        if (ret.getErrCode() != 0 && ret.getErrCode() != IFileSystemService.FILE_SYS_NOT_EXITS) {
            LOGGER.error("delete filesystem fail !!");
            throw FaultUtil.storageFault("delete filesystem error.");
        }
        vvolPathDBService.deleteFilesystemTableByName(vvolPath.getFileSystemName());
        vvolPathDBService.deleteShareByShareName(vvolPath.getSharePath());
        vvolPathDBService.deleteShareClientByShareId(id2DBPrimaryKey(vvolPath.getShareId()));

        return ret;
    }

    private SDKErrorCode deleteFileSystem(String fileSystemId) throws StorageFault {
        IFileSystemService deviceLunService;
        try {
            String arrayId = VASAUtil.getArrayId();
            LOGGER.info("begin deleteFileSystem ! ");
            deviceLunService = deviceManager.getDeviceServiceProxy(arrayId,
                    IFileSystemService.class);
            SDKErrorCode ret = deviceLunService.deleteFileSystem(arrayId, fileSystemId, null);

            return ret;
        } catch (SDKException e) {
            LOGGER.error("deleteFileSystem error, SDKException !", e);
            throw FaultUtil.storageFault("deleteFileSystem error.");
        }
    }

    public SDKErrorCode deleteFile(String vvolId, String vvolType, String arrayId) throws StorageFault {
        VvolPath vvolPath = vvolPathDBService.getVvolPathByVvolId(vvolId);

        LOGGER.info("begin deleteFile.");
        if (vvolPath == null) {
            LOGGER.error("vasa get vvolid:" + vvolId + " fail.");
            throw FaultUtil.storageFault("vasa get vvolid:" + vvolId + " fail.");
        }
        SDKResult<NFSvvolCreateResBean> delFileRet = doDeleteFile(vvolId, vvolType, vvolPath.getFileSystemName());
        if (delFileRet.getErrCode() != 0 && delFileRet.getErrCode() != INFSvvolService.ERROR_OBJ_NOT_EXIST) {
            LOGGER.error("delete file fail !!" + delFileRet.getErrCode());
            throw FaultUtil.storageFault("delete file error.");
        }

        SDKErrorCode ret = new SDKErrorCode();

        if (VasaConstant.VVOL_TYPE_DATA.equals(vvolType) || VasaConstant.VVOL_TYPE_MEMORY.equals(vvolType)) {
            ret = deleteFileSystemOfData(arrayId, vvolPath);
            ret.setErrCode(0);
        } else {
            ret.setErrCode(0);
        }
        NVirtualVolume volume = virtualVolumeService.getVirtualVolumeByVvolId(vvolId);
//        vvolPathDBService.updateFileCount(vvolPath.getFileSystemName(), -1);
//        vvolPathDBService.updateCurrCapacity(vvolPath.getFileSystemName(), volume.getSize() * -1);
        vvolPathDBService.updateFileCountAndCapacity(vvolPath.getFileSystemName(), -1, volume.getSize() * -1);
        return ret;
    }

    private SDKErrorCode deleteShare(String arrayId, String shareId) throws StorageFault {
        LOGGER.info("begin deleteShare !");
        try {
            INFSshareService deviceNfsSharedService = deviceManager.getDeviceServiceProxy(arrayId,
                    INFSshareService.class);

            LOGGER.info("deviceNfsSharedService = " + deviceNfsSharedService);
            SDKErrorCode ret = deviceNfsSharedService.deleteShare(arrayId, shareId, null);// nd_todo vstoreid

            return ret;
        } catch (SDKException e) {
            LOGGER.error("delete share error: " + e);
            throw FaultUtil.storageFault("delete share error.");
        }

    }

    public void deleteVirtualVolumeFromDatabase(String vvolId) throws StorageFault {
        virtualVolumeService.deleteVirtualVolumeInfo(vvolId);
    }

    /**
     *
     **/

    public List<S2DLun> getPELunsByArrayId(String arrayId) throws StorageFault {
        try {
            LOGGER.debug("Execute getPELunsByArrayId arrayId: " + arrayId);
            SDKResult<List<S2DLun>> result = new SDKResult<List<S2DLun>>();
            List<S2DLun> res = new ArrayList<>();
            result.setResult(res);
            SDKResult<List<LogicPortQueryResBean>> NasPEResult = null;
            try {
                LOGGER.info("sleeping...");
                Thread.sleep(100);
            } catch (InterruptedException e) {
                LOGGER.error("sleep error" + e);
            }
            NasPEResult = storageModel.getNasPE(arrayId);
            if (0 != NasPEResult.getErrCode()) {
                LOGGER.error("getNas PE error. params[" + arrayId + "] errCode:" + NasPEResult.getErrCode() + ", description:"
                        + NasPEResult.getDescription());
                throw FaultUtil.storageFault("getPELun SDKException.");
            }
            for (int i = 0; i < NasPEResult.getResult().size(); i++) {
                S2DLun s2DLun = new S2DLun();
                s2DLun.setID(NasPEResult.getResult().get(i).getID());
                s2DLun.setNAME(NasPEResult.getResult().get(i).getNAME());
                s2DLun.setRUNNINGSTATUS(NasPEResult.getResult().get(i).getRUNNINGSTATUS());
                s2DLun.setIPV4ADDR(NasPEResult.getResult().get(i).getIPV4ADDR());
                //s2DLun.setIPV6ADDR(NasPEResult.getResult().get(i).getIPV6ADDR());
                s2DLun.setROLE(NasPEResult.getResult().get(i).getROLE());
                //s2DLun.setROLE(NasPEResult.getResult().get(i).getVSTOREID());
                result.getResult().add(s2DLun);
            }

            return result.getResult();
        } catch (SDKException e) {
            LOGGER.error("StorageFault/getPELun SDKException.");
            throw FaultUtil.storageFault("getPELun SDKException.");
        }
    }

    public List<ProtocolEndpoint> queryProtocolEndpoint(String arrayId) throws StorageFault {
        List<ProtocolEndpoint> pes = new ArrayList<ProtocolEndpoint>();
        try {
            List<S2DLun> s2dLuns = getPELunsByArrayId(arrayId);
            pes.addAll(VASAUtilDJConvert.convert2ProtocolEndpoint(arrayId, s2dLuns));
        } catch (StorageFault e) {
            LOGGER.warn("getPELunsByArrayId error " + e);
            throw FaultUtil.storageFault("query nas pe fail !!");
        }

        return pes;
    }


    private String selectNasPEIp(String arrayId) throws StorageFault {
        String sessionId = VASAUtil.getSessionId();
        LOGGER.debug("sessionId : " + sessionId);
        List<ProtocolEndpoint> pEs = DataUtil.getInstance().getPEsBySessionId(sessionId);
        List<ProtocolEndpoint> outOfBandPEs = queryProtocolEndpoint(arrayId);//dataManager.getOutOfBandPes();

        List<ProtocolEndpoint> alivePes = new ArrayList<ProtocolEndpoint>();

        if (pEs == null || pEs.isEmpty()) {
            LOGGER.error("pes is null.");
            throw FaultUtil.storageFault("pes is null.");
        }
        for (ProtocolEndpoint checkedPe : pEs) {
            for (ProtocolEndpoint validPE : outOfBandPEs) {
                if (VASAUtil.isSamePe(checkedPe, validPE) && validPE.getUniqueIdentifier().startsWith(arrayId)) {
                    checkedPe.setUniqueIdentifier(validPE.getUniqueIdentifier());
                    alivePes.add(checkedPe);
                }
            }
        }

        HashSet<ProtocolEndpoint> temp = new HashSet<ProtocolEndpoint>(alivePes);
        alivePes.clear();
        alivePes.addAll(temp);

        if (alivePes.isEmpty()) {
            LOGGER.error("get nas pe fail: alivePes is empty !!");
            throw FaultUtil.storageFault("get nas pe fail !!");
        } else {
            LOGGER.info("alivePes pEs: ");
            VASAUtil.printPeWWN(alivePes);
        }

        List<String> ips = new ArrayList<String>();
        for (ProtocolEndpoint pe : alivePes) {
            if (pe.getInBandId() != null && pe.getInBandId().getIpAddress() != null &&
                    VASAUtil.isIPV4(pe.getInBandId().getIpAddress())) {
                ips.add(pe.getInBandId().getIpAddress());
            }
        }

        if (ips.size() <= 0) {
            throw FaultUtil.storageFault("get nas pe fail !!");
        }

        Random r = new Random();
        int index = r.nextInt(ips.size());
        LOGGER.info("pE index[" + index + "] : " + ips.get(index));
        return ips.get(index);
    }

    public VirtualVolumeInfo queryVirtualVolumeInfo(String vvolId) throws StorageFault {
        VirtualVolumeInfo vvolInfo = new VirtualVolumeInfo();

        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
        // 浠庢暟鎹簱涓皢vvol鐨刴etadata鍙栧嚭
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

    public synchronized void updateShare(String arrayId, String vvolType, VvolPath vvolPath, boolean isBind) throws StorageFault {
//        if(vvolType.equals(VasaConstant.VVOL_TYPE_CONFIG)
//                || vvolType.equals(VasaConstant.VVOL_TYPE_SWAP))
//        {
//            return;
//        }
        String shareName = vvolPath.getSharePath();
        String hostId = getHostIdByVvolType(vvolType);
        FilesystemShareTable shareDB = vvolPathDBService.queryShareByShareName(shareName);
        if (shareDB.getHostId().indexOf(hostId) != -1) {
            return;
        } else {
            if (hostId.equals(VasaConstant.HOST_ID_CERTIFICATION)) {
                return;
            }
            List<EsxHostIp> hostIps = esxHostIpService.queryEsxHostIpByHostId(hostId);
            List<String> ipsOfCurrHost = DataUtil.getInstance().getHostIpsByEsxHostId(hostId);
            List<String> ipsOfHost = new ArrayList<String>();
            if (hostIps != null) {
                for (EsxHostIp ip : hostIps) {
                    ipsOfHost.add(ip.getId());
                }
            }
            List<String> newIps = VASAUtil.getUpdateIps(ipsOfCurrHost, ipsOfHost);
            List<String> delIps = VASAUtil.getUpdateIps(ipsOfHost, ipsOfCurrHost);

            List<EsxHostIp> tempHostIps = esxHostIpService.queryEsxHostIpByHostId(hostId);
            for (String ip : newIps) {
                boolean isNotFind = true;
                if (tempHostIps != null) {
                    for (EsxHostIp tempHost : tempHostIps) {
                        if (tempHost.getIp() != null && tempHost.getIp().equals(ip)) {
                            isNotFind = false;
                        }
                    }
                }
                if (isNotFind) {
                    esxHostIpService.insertRecord(new EsxHostIp(ip, hostId));
                }
            }

            for (String ip : delIps) {
                esxHostIpService.deleteRecord(new EsxHostIp(ip, hostId));
            }

            List<ShareClientTable> clients = vvolPathDBService.queryShareClientByShareId(shareDB.getShareId());

            String shareId = shareDB.getShareId().split(VasaConstant.SEPARATOR)[0];
            if (isBind) {
                for (String ip : ipsOfCurrHost) {
                    boolean isExist = false;
                    ShareClientTable curCli = null;
                    for (ShareClientTable cli : clients) {
                        if (cli.getShareProperty().startsWith(ip)) {
                            isExist = true;
                            curCli = cli;
                            break;
                        }
                    }

                    if (!isExist) {
                        SDKResult<AddAuthClientResBean> ret = createClient(arrayId, ip, shareId);
                        if (ret.getErrCode() == 0) {
                            String clientId = ret.getResult().getID();
                            String para = ip + VasaConstant.SEPARATOR + clientId + VasaConstant.SEPARATOR + arrayId;
                            vvolPathDBService.insertShareClientRecord(new ShareClientTable(para, shareId + VasaConstant.SEPARATOR + arrayId));
                        }
                    }
                }
                shareDB.setHostId(shareDB.getHostId() + VasaConstant.SEPARATOR + hostId);
                vvolPathDBService.updateHostId(shareDB);
            }
        }
    }


    public List<BatchVirtualVolumeHandleResult> bindNasVirtualVolume(String arrayId, String vvolId, String rawId,
                                                                     String sourceType, int bindType) throws StorageFault {
        VASAUtil.saveArrayId(arrayId);

        List<BatchVirtualVolumeHandleResult> bindResults = new ArrayList<BatchVirtualVolumeHandleResult>();
        try {

            BatchVirtualVolumeHandleResult handleResult = new BatchVirtualVolumeHandleResult();
            handleResult.setVvolId(vvolId);
            VirtualVolumeHandle vvolHandle = new VirtualVolumeHandle();

            ProtocolEndpointInbandId nasInbandId = new ProtocolEndpointInbandId();
            nasInbandId.setProtocolEndpointType(ProtocolEndpointTypeEnum.NFS.value());
            String peIp = selectNasPEIp(arrayId);
            LOGGER.info("nas pe address: " + peIp);
            nasInbandId.setIpAddress(peIp);
            nasInbandId.setServerMount("/");

            vvolHandle.setPeInBandId(nasInbandId);
            vvolHandle.setUniqueIdentifier(vvolId);
            handleResult.setVvolHandle(vvolHandle);
            //缁勮铏氭嫙鏈轰俊鎭�
            VirtualVolumeInfo virtualvolumeinfo = queryVirtualVolumeInfo(vvolId);

            String virtualinfo = JsonUtil.parse2JsonString(virtualvolumeinfo);
            LOGGER.info("VM metadata:" + virtualinfo);
            handleResult.setVvolInfo(virtualvolumeinfo);

            /*
            add vvol size from database
             */
            NVirtualVolume vvol = virtualVolumeService.getVirtualVolumeByVvolId(vvolId);
            Long vvolSize = vvol.getSize();

            handleResult.setVvolLogicalSize(vvolSize);

            VvolPath vvolPath = vvolPathDBService.getVvolPathByVvolId(vvolId);
            List<NameValuePair> metadataList = virtualvolumeinfo.getMetadata();

            for (NameValuePair pair : metadataList) {
                String key = pair.getParameterName();
                String value = pair.getParameterValue();


                if (VasaConstant.VMW_VVolType.equals(key)) {
                    if (VasaConstant.OPT_SNAPSHOT.equalsIgnoreCase(sourceType)) {
                        LOGGER.info("In bindVirtualVolume the vvol lun sourceType is " + sourceType);
                        vvolHandle.setVvolSecondaryId(vvolPath.getFileSystemName() + "/" + vvolPath.getPath());
                    } else {
                        if (VasaConstant.VVOL_TYPE_CONFIG.equals(value)) {
                            vvolHandle.setVvolSecondaryId(vvolPath.getFileSystemName() + "/" + vvolPath.getPath() + "/");
                        } else if (VasaConstant.VVOL_TYPE_SWAP.equals(value) || VasaConstant.VVOL_TYPE_DATA.equals(value)
                                || VasaConstant.VVOL_TYPE_MEMORY.equals(value)) {
                            vvolHandle.setVvolSecondaryId(vvolPath.getFileSystemName() + "/" + vvolPath.getPath());
                        } else {
                            LOGGER.error("unknown  vvolType: " + value);
                            throw FaultUtil.storageFault("unknown  vvolType: " + value);
                        }
                    }
                    updateShare(arrayId, value, vvolPath, true);
                }
            }
            bindResults.add(handleResult);
        } catch (Exception e) {
            LOGGER.error("bind vvol error: " + e);
            throw FaultUtil.storageFault("unknown  bind vvol error.");
        }
        LOGGER.info("end bind vvol");
        vvolPathDBService.setBindState(vvolId, true);
        return bindResults;
    }

    private SDKResult<FileSystemSnapshotCreateResBean> createSnapShot(String fileSystemId, String name) throws SDKException, StorageFault {
        String arrayId = VASAUtil.getArrayId();
        IFileSystemService fileSystemService =
                deviceManager.getDeviceServiceProxy(arrayId, IFileSystemService.class);
        SDKResult<FileSystemSnapshotCreateResBean> createSnapshot =
                fileSystemService.createFSSnapshot(arrayId, name, fileSystemId, "40");
        return createSnapshot;
    }

    private synchronized SDKResult<FileSystemSnapshotCreateResBean> initCountRecordForSnap(String vmId, VvolPath srcVvolPath, String name,
                                                                                           int count, Stack<FinalHandler> finalHandlers) throws SDKException, StorageFault {
        if (!hasCountRecord(vmId, SnapshotCloneRecordService.SNAPSHOT)) {
            SDKResult<FileSystemSnapshotCreateResBean> createSnapshot = createSnapShot(srcVvolPath.getFileSystemId(), name);
            String snapshotId = createSnapshot.getResult().getID();
            initCountRecord(vmId, SnapshotCloneRecordService.SNAPSHOT, count,
                    snapshotId + VasaConstant.SEPARATOR + name, finalHandlers);
            if (createSnapshot.getErrCode() == 0) {
                finalHandlers.add(() -> {
                    try {
                        deleteSnapBySnapshotId(createSnapshot.getResult().getID());
                    } catch (Exception e) {
                        LOGGER.info("clear: delete snahshot for create snap.");
                    }
                });
            }
            return createSnapshot;
        }
        SDKResult<FileSystemSnapshotCreateResBean> createSnapshot = new SDKResult<FileSystemSnapshotCreateResBean>();
        createSnapshot.setErrCode(0);
        return createSnapshot;
    }

    public SDKResult<FileSystemSnapshotCreateResBean> createSnapshotFromSourceFS(String srcVvolId, String snapVvolId,
                                                                                 String name, String desc) throws SDKException, StorageFault {
        VvolPath srcVvolPath = vvolPathDBService.getVvolPathByVvolId(srcVvolId);
        SDKResult<FileSystemSnapshotCreateResBean> createSnapshot = new SDKResult<FileSystemSnapshotCreateResBean>();
        Stack<FinalHandler> finalHandlers = new Stack<FinalHandler>();
        String vmId = virtualVolumeService.getVirtualVolumeByVvolId(srcVvolId).getVmId();

        String newVmId = updateVmId(vmId, snapVvolId);
        String snapshotId = null;
        String snapshotName = name;
//        if(!hasCountRecord(vmId,SnapshotCloneRecordService.SNAPSHOT))
//        {
//            createSnapshot = createSnapShot(srcVvolPath.getFileSystemId(),name);
//            snapshotId = createSnapshot.getResult().getID();
//            initCountRecord(vmId, SnapshotCloneRecordService.SNAPSHOT, getDiskNumByVmId(vmId),
//                    snapshotId + VasaConstant.SEPARATOR + name, finalHandlers);
//        }
        boolean shouldClear = true;
        try {
            createSnapshot = initCountRecordForSnap(newVmId, srcVvolPath, snapshotName, getDiskNumByVmId(vmId), finalHandlers);
            if (createSnapshot.getErrCode() != 0) {
                LOGGER.error("create snapshot fail !!");
                return createSnapshot;
            }
            Result ret = addRecord(newVmId, SnapshotCloneRecordService.SNAPSHOT, finalHandlers);
            String[] snapPara = ret.inputName.split(VasaConstant.SEPARATOR);
            if (snapPara.length != 2) {
                throw new IllegalArgumentException("snapPara size error.");
            }
            snapshotId = snapPara[0];
            snapshotName = snapPara[1];
            if (ret.result == SnapshotCloneRecordService.FINISHED) {
                LOGGER.info("snapshotId = " + ret.inputName + ",ok !!");
            } else {
                LOGGER.info("snapshot ......!!");
            }
            VvolPath snapVvolPath = new VvolPath();
            snapVvolPath.setVvolid(VasaConstant.VVOL_PREFIX + snapVvolId);
            snapVvolPath.setFileSystemId(srcVvolPath.getFileSystemId());
            snapVvolPath.setShareId(srcVvolPath.getShareId());
            snapVvolPath.setSnapshotId(snapshotId);
            snapVvolPath.setFileSystemName(srcVvolPath.getFileSystemName());
            snapVvolPath.setSharePath(srcVvolPath.getSharePath());

            String snapPath = VasaConstant.VVOL_SNAPSHOT_FLODER + "/" + snapshotName + "/" + srcVvolPath.getPath();
            snapVvolPath.setPath(snapPath);
            vvolPathDBService.insertRecord(snapVvolPath);
            finalHandlers.add(() -> {
                vvolPathDBService.deleteRecordByVvolId(snapVvolPath.getVvolid());
            });
            shouldClear = false;
        } catch (Exception e) {
            LOGGER.error("createSnapshotFromSourceFS fail, exec clear func!!" + e);
            throw e;
        } finally {
            if (shouldClear) {
                while (!finalHandlers.isEmpty()) {
                    LOGGER.info("do clear......");
                    FinalHandler finalHander = finalHandlers.pop();
                    finalHander.onFilan();
                }
                deleteVirtualVolumeFromDatabase(VasaConstant.VVOL_PREFIX + snapVvolId);
            }
        }
        return createSnapshot;
    }


    private SDKErrorCode deleteSnapBySnapshotId(String fsSnapshotId) throws SDKException, StorageFault {
        String arrayId = VASAUtil.getArrayId();
        IFileSystemService fileSystemService =
                deviceManager.getDeviceServiceProxy(arrayId, IFileSystemService.class);
        return fileSystemService.deleteFSSnapshot(arrayId, fsSnapshotId, null);
    }

    public SDKErrorCode deleteFSSnapshot(String snapVvolId) throws SDKException, StorageFault {
        String arrayId = VASAUtil.getArrayId();
        VvolPath vvolPath = vvolPathDBService.getVvolPathByVvolId(snapVvolId);
        String fsSnapshotId = vvolPath.getSnapshotId();
        IFileSystemService fileSystemService =
                deviceManager.getDeviceServiceProxy(arrayId, IFileSystemService.class);
        return fileSystemService.deleteFSSnapshot(arrayId, fsSnapshotId, null);
    }

    public SDKErrorCode rollbackFsSapshot(String snapVvolId) throws SDKException, StorageFault {
        String arrayId = VASAUtil.getArrayId();
        VvolPath vvolPath = vvolPathDBService.getVvolPathByVvolId(snapVvolId);
        String fsSnapshotId = vvolPath.getSnapshotId();
        IFileSystemService fileSystemService =
                deviceManager.getDeviceServiceProxy(arrayId, IFileSystemService.class);
        return fileSystemService.RollbackFSSnapshot(arrayId, fsSnapshotId, null);
    }

    public void clearMigrateDate(String[] paras) throws SDKException, StorageFault {
        LOGGER.info("Migrate FS error,clear data .......");
        if (paras.length != 8) {
            LOGGER.error("error paras:" + paras.toString() + " !!");
            throw new UnsupportedOperationException("error paras.");
        }
        String vvolId = paras[1];
        String snapshotId = paras[2];
        String poolId = paras[3];
        String fsName = paras[4];
        String opt = paras[5];
        String sourceFsId = paras[6];
        String sourcePath = paras[7];

        if (MigrateResult.MIGRATE_CREATE.equals(opt)) {
            try {
                //删除克隆失败的卷记录
                virtualVolumeService.deleteVirtualVolumeByVvolId(vvolId);

                //删快照
                if (!snapshotId.startsWith(MigrateResult.MIGRATE_ONLINE)) {
                    deleteSnapBySnapshotId(snapshotId);
                }

                //删文件系统记录
                vvolPathDBService.deleteFilesystemTableByName(fsName);
            } catch (StorageFault storageFault) {
                LOGGER.error("clear data exception: " + storageFault);
                throw storageFault;
            }
        }
        LOGGER.info("Migrate FS error,clear data successfully!");

    }

    public void setVvolMetadata(String vvolId) {
        LOGGER.info("set vvol metadata");
        List<NVvolMetadata> metadatas = null;
        try {
            metadatas = vvolMetadataService.getVvolMetadataByVvolId(vvolId);
        } catch (StorageFault storageFault) {
            storageFault.printStackTrace();
        }
        List<NameValuePair> metadata = new ArrayList<>();
        for (int i = 0; i < metadatas.size(); i++) {
            NameValuePair nameValuePair = new NameValuePair();
            nameValuePair.setParameterName(metadatas.get(i).getKey());
            nameValuePair.setParameterValue(metadatas.get(i).getValue());
            metadata.add(nameValuePair);
        }
        VASAUtil.saveNameSpace(metadata);
    }

    public int deleteTempShare(String arrayId) {
        if (arrayId == null) {
            LOGGER.error("poolid is null");
            return -1;
        }
        IFileSystemService deviceLunService = null;
        INFSshareService deviceNfsSharedService = null;
        LOGGER.info("delete temp share");
        String name = null;
        try {
            name = FILE_SYSTEM_TEMP + VASAUtil.UUId2FileSystemName(arrayId);
        } catch (StorageFault storageFault) {
            storageFault.printStackTrace();
        }
        FileSystemTable fileSystemTable = vvolPathDBService.queryFileSystemTableByName(name);
        FilesystemShareTable shareTable = vvolPathDBService.queryShareByShareName("/" + name + "/");
        if (fileSystemTable == null || fileSystemTable.getFileSystemName() == null) {
            return -1;
        }
        String fsName = fileSystemTable.getFileSystemName();
        String fsId = fileSystemTable.getId();
        String shareId = shareTable.getShareId();
        try {
            deviceLunService
                    = deviceManager.getDeviceServiceProxy(arrayId,
                    IFileSystemService.class);
            deviceNfsSharedService = deviceManager.getDeviceServiceProxy(arrayId,
                    INFSshareService.class);
        } catch (SDKException e) {
            e.printStackTrace();
        }

        SDKErrorCode sdkErrorCode = deviceNfsSharedService.deleteShare(arrayId, shareId, null);
        if (sdkErrorCode.getErrCode() == 0 || sdkErrorCode.getErrCode() == INFSshareService.SHARE_NOT_EXIST) {
            vvolPathDBService.deleteShareByShareName("/" + fsName + "/");
        }
        SDKErrorCode res = deviceLunService.deleteFileSystem(arrayId, fsId, null);
        if (res.getErrCode() == 0 || res.getErrCode() == IFileSystemService.FILE_SYS_NOT_EXITS) {
            vvolPathDBService.deleteFilesystemTableByName(fsName);
        }
        LOGGER.info("delete Temp successfully");
        return 0;
    }

    @Override
    public int createGNSshare(String arrayid) {
        LOGGER.info("create GNS share");
        int result = 0;
        INFSshareService deviceNfsSharedService;

        FilesystemShareTable filesystemShareTable = vvolPathDBService.queryShareByShareName("/");
        if (filesystemShareTable != null) {
            LOGGER.info(" GNS share already exists");
            return 1;
        }

        try {
            String arrayId = arrayid;
            deviceNfsSharedService = deviceManager.getDeviceServiceProxy(arrayId,
                    INFSshareService.class);

            SDKResult<NFSshareCreateResBean> shareRet = deviceNfsSharedService.createShare(arrayId, null, "/", "vvol create share");
            if (shareRet.getErrCode() != 0) {
                if (INFSshareService.SHARE_EXIST != shareRet.getErrCode()) {
                    LOGGER.error("create nfsShare error[" + shareRet.getErrCode() + "]!");
                    result = -1;
                    throw FaultUtil.storageFault();
                } else {
                    shareRet = deviceNfsSharedService.queryShare(arrayId, "/", null);
                }
            }

            String shareId = shareRet.getResult().getID();
            FilesystemShareTable record = new FilesystemShareTable();
            record.setFilesystemId("--");
            record.setShareId(shareId);
            record.setSharePath("/");
            record.setHostId("*");
            vvolPathDBService.insertShareRecord(record);

        } catch (SDKException e) {
            LOGGER.error("create nfsShare error[" + e + "]!");
            result = -1;
        } catch (StorageFault storageFault) {
            LOGGER.error("create nfsShare error[" + storageFault + "]!");
            result = -1;
        }
        LOGGER.info("create GNS successfully");
        return result;
    }

    public int createTempShare(String arrayid, String poolId) {
        LOGGER.info("create Temp share");
        if (arrayid == null || poolId == null) {
            LOGGER.error("arrayid or poolid is null");
            return -1;
        }

        int result = 0;
        String arrayId = arrayid;
        String name = null;
        long capacity = 1024L;
        long maxAutoSize = 2 * 1024 * 1024L;
        INFSshareService deviceNfsSharedService;
        IFileSystemService deviceLunService;
        try {
            name = "VASA_SYSTEM_TEMP_" + VASAUtil.UUId2FileSystemName(arrayId);
        } catch (StorageFault storageFault) {
            storageFault.printStackTrace();
        }
        FilesystemShareTable filesystemShareTable = vvolPathDBService.queryShareByShareName("/" + name + "/");
        if (filesystemShareTable != null) {
            LOGGER.info(" Temp share already exists");
            return 0;
        }

        try {

            deviceLunService
                    = deviceManager.getDeviceServiceProxy(arrayId,
                    IFileSystemService.class);
            SDKResult<FileSystemCreateResBean> res = deviceLunService.createFileSystem(arrayId, name, poolId, capacity, VasaConstant.FILE_SYSTEM_THIN, DATASECTORSIZE,
                    SPACESELFADJUSTINGMODE, SNAPSHOTRESERVEPER, OWNINGCONTROLLER, ISCLONEFS, null, AUTOSHRINKTHRESHOLDPERCENT,
                    AUTOGROWTHRESHOLDPERCENT, maxAutoSize, AUTOSIZEINCREMENT);
            if (res.getErrCode() != 0) {
                if (res.getErrCode() == IFileSystemService.FILE_SYS_EXIST) {
                    res = deviceLunService.queryFileSystemByName(arrayId, name);
                } else {
                    LOGGER.error("createFileSystem error[" + res.getErrCode() + "]!");
                    result = -1;
                    return result;
                }
            }
            String fsId = res.getResult().getID();
            FileSystemTable record = new FileSystemTable();
            record.setId(fsId);
            record.setFileSystemName(name);
            record.setCurrentCapacity("");
            record.setFileCount("");
            record.setMaxCapacity("");
            vvolPathDBService.insertFilesystemTable(record);

            deviceNfsSharedService = deviceManager.getDeviceServiceProxy(arrayId,
                    INFSshareService.class);

            SDKResult<NFSshareCreateResBean> shareRet = deviceNfsSharedService.createShare(arrayId, fsId, "/" + name + "/", "vvol create share");
            if (shareRet.getErrCode() != 0) {
                if (INFSshareService.SHARE_EXIST != shareRet.getErrCode()) {
                    LOGGER.error("create nfsShare error[" + shareRet.getErrCode() + "]!");
                    result = -1;
                    throw FaultUtil.storageFault();
                } else {
                    shareRet = deviceNfsSharedService.queryShare(arrayId, "/" + name + "/", null);
                }
            }
            String shareId = shareRet.getResult().getID();
            FilesystemShareTable shareRecord = new FilesystemShareTable();
            shareRecord.setFilesystemId(fsId);
            shareRecord.setShareId(shareId);
            shareRecord.setSharePath("/" + name + "/");
            vvolPathDBService.insertShareRecord(shareRecord);

            SDKResult<AddAuthClientResBean> clientRsp = deviceNfsSharedService.addAuthClient(arrayId, "*",
                    shareId, INFSshareService.ACCESSVAL_READ_ONLY, INFSshareService.SYNC,
                    INFSshareService.ALLSQUASH_NO_ALL, INFSshareService.ROOTSQUASH_NO_ROOT);

            ShareClientTable shareClientTable = new ShareClientTable(shareId, fsId + ";" + arrayId);


        } catch (StorageFault storageFault) {
            storageFault.printStackTrace();
        } catch (SDKException e) {
            e.printStackTrace();
        }
        LOGGER.info("create Temp successfully");
        return result;
    }
}
