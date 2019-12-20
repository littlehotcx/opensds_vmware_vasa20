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

package org.opensds.vasa.vasa20.device.array.fileSystem;

import org.opensds.vasa.base.bean.terminal.ResBean;
import org.opensds.platform.common.SDKErrorCode;
import org.opensds.platform.common.SDKResult;
import org.opensds.platform.common.utils.JsonUtils;
import org.opensds.platform.commu.itf.ISDKProtocolAdapter;
import org.opensds.platform.exception.ProtocolAdapterException;
import org.opensds.vasa.vasa20.device.AbstractVASACapability;
import org.opensds.vasa.vasa20.device.VASARestReqMessage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 功能描述
 *
 * @author h00451513
 * @since 2019-02-28
 */
public class FileSystemService extends AbstractVASACapability implements IFileSystemService {

    public FileSystemService(ISDKProtocolAdapter protocolAdapter) {
        super(protocolAdapter);
    }

    private static Logger LOGGER = LogManager.getLogger(FileSystemService.class);
    private String fileSyste_url = "fileSystem";
    private String deleteFileSystem_url = "filesystem/";
    private String queryFileSystem_url = "filesystem?filter=NAME::";
    //
    private String modifyFileSystem_url = "filesystem/";
    private String createFssnapshot_url = "fssnapshot";
    private String deleteFssnapshot_url = "fssnapshot/";
    private String rollbackFssnapshot_url = "fssnapshot/rollback_fssnapshot";
    private String migrationFileSystem_url = "FS_MIGRATION";
    private String migrationFileSystemUpdate_url = "FS_MIGRATION?sourceFsId=";

    @Override
    public SDKResult<FileSystemCreateResBean> createFileSystem(String arrayId, String name, String parentId, long capacity, String alloctype,
                                                               int sectorSize, String spaceSelfadjustingMode, int snapshotReserveper, String owningController, boolean isCloneFS, String vstoreId,
                                                               int autoShringkThresholdPercent, int autoGrowThresholdPercent, Long maxAutoSize, Long autoSizeIncrement) {
        LOGGER.debug("creating FileSystem");
        VASARestReqMessage req = new VASARestReqMessage();
        req.setHttpMethod("POST");
        req.setMediaType("json");
        req.setArrayId(arrayId);
        FileSystemCreateReqBean reqPayload = new FileSystemCreateReqBean();
        SDKResult<FileSystemCreateResBean> sdkResult = new SDKResult<>();
        createFileSystemReqBean(reqPayload, name, parentId, capacity, alloctype, sectorSize, spaceSelfadjustingMode, snapshotReserveper, owningController,
                isCloneFS, vstoreId, autoShringkThresholdPercent, autoGrowThresholdPercent, maxAutoSize, autoSizeIncrement);
        req.setPayload(reqPayload);
        LOGGER.debug("req data:" + reqPayload.toString());

        try {
            ResBean response = (ResBean) protocolAdapter.syncSendMessage(req, fileSyste_url, "org.opensds.vasa.vasa20.device.array.fileSystem.FileSystemCreateResBean");

            sdkResult.setErrCode(response.getErrorCode());
            sdkResult.setDescription(response.getDescription());
            if (response.getErrorCode() == 0) {
                FileSystemCreateResBean result = (FileSystemCreateResBean) response.getResData();
                LOGGER.debug("create FileSystem success " + result);
                sdkResult.setResult(result);
            } else {
                LOGGER.error("create FileSystem fail! FileSystemCreateReqBean=" + response.getDescription());
                sdkResult.setErrCode(response.getErrorCode());
                sdkResult.setDescription("creat FileSystem fail FileSystemCreateReqBean=" + response.getDescription());
            }
        } catch (ProtocolAdapterException e) {
            LOGGER.error("create FileSystem fail! FileSystemCreateReqBean=" + reqPayload, e);
            sdkResult.setErrCode(e.getErrorCode());
            sdkResult.setDescription("creat FileSystem fail FileSystemCreateReqBean=" + reqPayload);
            e.printStackTrace();
        }

        return sdkResult;
    }


    private void createFileSystemReqBean(FileSystemCreateReqBean reqPayload, String name, String parentId, long capacity,
                                         String alloctype, int sectorSize, String spaceSelfadjustingMode, int snapshotReserveper, String owningController,
                                         boolean isCloneFS, String vstoreId, int autoShringkThresholdPercent, int autoGrowThresholdPercent, Long maxAutoSize, Long autoSizeIncrement) {
        reqPayload.setNAME(name);
        reqPayload.setPARENTID(parentId);
        reqPayload.setALLOCTYPE(alloctype);
        reqPayload.setCAPACITY(capacity * 2048);
        reqPayload.setSECTORSIZE(sectorSize);
        reqPayload.setSPACESELFADJUSTINGMODE(spaceSelfadjustingMode);
        reqPayload.setSNAPSHOTRESERVEPER(snapshotReserveper);
        reqPayload.setISCLONEFS(isCloneFS);
        reqPayload.setMAXAUTOSIZE(maxAutoSize);
        reqPayload.setAUTOSIZEINCREMENT(autoSizeIncrement);
        reqPayload.setAUTOGROWTHRESHOLDPERCENT(autoGrowThresholdPercent);
        reqPayload.setAUTOSHRINKTHRESHOLDPERCENT(autoShringkThresholdPercent);
//        if(owningController != null  &&  owningController.length()>0)
//        {
//            reqPayload.setOWNINGCONTROLLER(owningController);
//        }
//        if(vstoreId != null  && vstoreId.length()>0)
//        {
//            reqPayload.setVSTOREID(vstoreId);
//        }
    }

    @Override
    public SDKResult<FileSystemCreateResBean> queryFileSystemById(String arrayId, String fileSystemId) {
        return null;
    }

    @Override
    public SDKResult<FileSystemCreateResBean> queryFileSystemByName(String arrayId, String fileSystemName) {
        LOGGER.debug("query FileSystem by name:" + fileSystemName);
        VASARestReqMessage req = new VASARestReqMessage();
        req.setHttpMethod("GET");
        req.setArrayId(arrayId);
        req.setPaging(true);
        SDKResult<FileSystemCreateResBean> sdkResult = new SDKResult<FileSystemCreateResBean>();
        List<FileSystemCreateResBean> rsp = new ArrayList<FileSystemCreateResBean>();
        try {
            ResBean response = (ResBean) protocolAdapter.syncSendMessage(req, queryFileSystem_url + fileSystemName, "org.opensds.vasa.vasa20.device.array.fileSystem.FileSystemCreateResBean");
            sdkResult.setErrCode(response.getErrorCode());
            sdkResult.setDescription(response.getDescription());

            if (response.getErrorCode() == 0) {
                rsp = (List<FileSystemCreateResBean>) response.getResData();
                sdkResult.setResult(rsp.get(0));
            } else {
                LOGGER.error("query filesystem by name error" + response.getDescription());
            }
        } catch (ProtocolAdapterException e) {
            LOGGER.error("query filesystem by name error" + e);
            sdkResult.setErrCode(e.getErrorCode());
            sdkResult.setDescription(e.getMessage());
        }
        return sdkResult;
    }

    @Override
    public SDKErrorCode deleteFileSystem(String arrayId, String fileSystemId, String vstoreId) {
        LOGGER.debug("delete FileSystem:" + fileSystemId);
        VASARestReqMessage req = new VASARestReqMessage();
        req.setHttpMethod("DELETE");
        req.setMediaType("json");
        req.setArrayId(arrayId);

        SDKErrorCode sdkResult = new SDKErrorCode();

        try {
            ResBean response;
            if (vstoreId == null) {
                response = (ResBean) protocolAdapter.syncSendMessage(req, deleteFileSystem_url + fileSystemId, null);
            } else {
                response = (ResBean) protocolAdapter.syncSendMessage(req, deleteFileSystem_url + fileSystemId + "?vstoreId=" + vstoreId, null);
            }
            if (response.getErrorCode() == 0) {
                sdkResult.setErrCode(response.getErrorCode());
                sdkResult.setDescription(response.getDescription());
                LOGGER.debug("success delete FileSystem,response=" + response);
            } else {
                LOGGER.error("deleteFileSystem error" + response.getDescription());
                sdkResult.setErrCode(response.getErrorCode());
                sdkResult.setDescription(response.getDescription());
            }


        } catch (ProtocolAdapterException e) {
            LOGGER.error("delete FileSystem failed! FileSystem ID =" + fileSystemId, e);
            sdkResult.setErrCode(e.getErrorCode());
            sdkResult.setDescription("delete FileSystem fail! FileSystem ID = " + fileSystemId);
            e.printStackTrace();
        }
        return sdkResult;

    }

    @Override
    public void ModifyFileSystem(String arrayId, String fileSystemId, String capacity) {
        LOGGER.debug("modify FileSystem:" + fileSystemId);
        VASARestReqMessage req = new VASARestReqMessage();
        req.setHttpMethod("PUT");
        req.setMediaType("json");
        req.setArrayId(arrayId);
        FileSystemModifyReqBean reqPayload = new FileSystemModifyReqBean();
        reqPayload.setCAPACITY(capacity);
        req.setPayload(reqPayload);
        LOGGER.info("req data:" + reqPayload.toString());
        try {
            ResBean response = (ResBean) protocolAdapter.syncSendMessage(req, modifyFileSystem_url + fileSystemId, "org.opensds.vasa.vasa20.device.array.fileSystem.FileSystemModifyResBean");
            if (response.getErrorCode() == 0) {
                FileSystemModifyResBean result = (FileSystemModifyResBean) response.getResData();
                LOGGER.info("modify FileSystem success: " + result);
            } else {
                LOGGER.error("modify FileSystem fail! FileSystemModifyReqBean=" + response.getDescription());
            }
        } catch (ProtocolAdapterException e) {
            LOGGER.error("modify FileSystem fail! FileSystemModifyReqBean=" + reqPayload, e);
        }
    }

    @Override
    public SDKErrorCode ModifyFileSystemMaxAutosize(String arrayId, String fileSystemId, Long maxAutosize) {
        LOGGER.debug("modify FileSystem maxAutosize:" + fileSystemId);
        VASARestReqMessage req = new VASARestReqMessage();
        req.setHttpMethod("PUT");
        req.setMediaType("json");
        req.setArrayId(arrayId);
        FileSystemMaxAutosizeModifyReqBean reqPayload = new FileSystemMaxAutosizeModifyReqBean();
        reqPayload.setMAXAUTOSIZE(maxAutosize);
        req.setPayload(reqPayload);

        SDKErrorCode sdkErrorCode = new SDKErrorCode();

        LOGGER.debug("req data:" + reqPayload.toString());
        try {
            ResBean response = (ResBean) protocolAdapter.syncSendMessage(req, modifyFileSystem_url + fileSystemId, "org.opensds.vasa.vasa20.device.array.fileSystem.FileSystemModifyResBean");

            sdkErrorCode.setErrCode(response.getErrorCode());
            sdkErrorCode.setDescription(response.getDescription());

            if (response.getErrorCode() == 0) {
                FileSystemModifyResBean result = (FileSystemModifyResBean) response.getResData();
                LOGGER.debug("modify FileSystem success: " + result);
            } else {
                LOGGER.error("modify FileSystem fail! FileSystemMaxAutosizeModifyReqBean=" + response.getDescription());
            }
        } catch (ProtocolAdapterException e) {
            sdkErrorCode.setErrCode(e.getErrorCode());
            sdkErrorCode.setDescription(e.toString());

            LOGGER.error("modify FileSystem fail! FileSystemMaxAutosizeModifyReqBean=" + reqPayload, e);

        }
        return sdkErrorCode;
    }

    @Override
    public SDKResult<FileSystemSnapshotCreateResBean> createFSSnapshot(String arrayId, String fsSnapshotName, String parentId, String parentType) {
        LOGGER.debug("create FileSystem Snapshot:" + parentId + "snapshot name:" + fsSnapshotName);
        VASARestReqMessage req = new VASARestReqMessage();
        req.setHttpMethod("POST");
        req.setMediaType("json");
        req.setArrayId(arrayId);
        FileSystemSnapshotCreateReqBean reqPayload = new FileSystemSnapshotCreateReqBean();
        reqPayload.setNAME(fsSnapshotName);
        reqPayload.setPARENTID(parentId);
        reqPayload.setPARENTTYPE(parentType);
        req.setPayload(reqPayload);
        LOGGER.debug("req data:" + reqPayload.toString());

        SDKResult<FileSystemSnapshotCreateResBean> sdkResult = new SDKResult<FileSystemSnapshotCreateResBean>();
        try {
            ResBean response = (ResBean) protocolAdapter.syncSendMessage(req, createFssnapshot_url, "org.opensds.vasa.vasa20.device.array.fileSystem.FileSystemSnapshotCreateResBean");
            sdkResult.setErrCode(response.getErrorCode());
            sdkResult.setDescription(response.getDescription());

            if (response.getErrorCode() == 0) {
                FileSystemSnapshotCreateResBean result = (FileSystemSnapshotCreateResBean) response.getResData();
                sdkResult.setResult(result);
                LOGGER.debug("create FileSystem Snapshot success: " + result);
            } else {
                LOGGER.error("create FileSystem Snapshot fail! FileSystemSnapshotCreateResBean = " + response.getDescription());
            }
        } catch (ProtocolAdapterException e) {
            LOGGER.error("create FileSystem Snapshot fail! FileSystemSnapshotCreateReqBean = " + reqPayload, e);
            e.printStackTrace();
            sdkResult.setErrCode(e.getErrorCode());
            sdkResult.setDescription(e.getMessage());
        }

        return sdkResult;
    }

    @Override
    public SDKErrorCode deleteFSSnapshot(String arrayId, String fsSnapshotId, String vstoreId) {
        LOGGER.debug("delete FileSystem Snapshot");
        LOGGER.debug("fsSnapshotId: " + fsSnapshotId);
        VASARestReqMessage req = new VASARestReqMessage();
        req.setHttpMethod("DELETE");
        req.setMediaType("json");
        req.setArrayId(arrayId);

        SDKErrorCode sdkResult = new SDKErrorCode();
        try {
            ResBean response;
            if (vstoreId == null) {
                response = (ResBean) protocolAdapter.syncSendMessage(req, deleteFssnapshot_url + fsSnapshotId, null);
            } else {
                response = (ResBean) protocolAdapter.syncSendMessage(req, deleteFssnapshot_url + fsSnapshotId + "?vstoreId=" + vstoreId, null);
            }
            LOGGER.debug("success delete FileSystem Snapshot,response=" + response);
            sdkResult.setErrCode(response.getErrorCode());
            sdkResult.setDescription(response.getDescription());

        } catch (ProtocolAdapterException e) {
            LOGGER.error("delete FileSystem Snapshot failed! FileSystem ID = " + fsSnapshotId, e);
            e.printStackTrace();
            sdkResult.setErrCode(e.getErrorCode());
            sdkResult.setDescription("delete FileSystem Snapshot failed! FileSystem ID = " + fsSnapshotId);
        }

        return sdkResult;
    }

    @Override
    public SDKErrorCode RollbackFSSnapshot(String arrayId, String fsSnapshotId, String vstoreId) {
        LOGGER.debug("rollback FileSystem Snapshot:" + fsSnapshotId);
        VASARestReqMessage req = new VASARestReqMessage();
        req.setHttpMethod("PUT");
        req.setMediaType("json");
        req.setArrayId(arrayId);
        FileSystemSnapshotRollbackReqBean reqPayload = new FileSystemSnapshotRollbackReqBean();
        reqPayload.setID(fsSnapshotId);
        //reqPayload.setVstoreId(vstoreId);
        req.setPayload(reqPayload);
        LOGGER.info("req data:" + reqPayload.toString());

        SDKErrorCode sdkResult = new SDKErrorCode();
        try {
            ResBean response;
            if (vstoreId == null) {
                response = (ResBean) protocolAdapter.syncSendMessage(req, rollbackFssnapshot_url, null);
            } else {
                response = (ResBean) protocolAdapter.syncSendMessage(req, rollbackFssnapshot_url, null);
            }
            sdkResult.setErrCode(response.getErrorCode());
            sdkResult.setDescription(response.getDescription());
            LOGGER.debug("rollback FileSystem Snapshot success , response = " + response);
        } catch (ProtocolAdapterException e) {
            LOGGER.error("rollback FileSystem Snapshot failed ! FileSystem Snapshot ID =" + fsSnapshotId, e);
            e.printStackTrace();
            sdkResult.setErrCode(e.getErrorCode());
            sdkResult.setDescription("rollback FileSystem Snapshot failed ! FileSystem Snapshot ID = " + fsSnapshotId);
        }

        return sdkResult;
    }

    @Override
    public SDKResult<FileSystemCloneCreateResBean> CloneFileSystem(String arrayId, String name, String alloctype, String parentFilesystemid, String parentSnapshotId, String vstoreId) {
        LOGGER.debug("clone file system: " + parentFilesystemid);
        VASARestReqMessage req = new VASARestReqMessage();
        req.setHttpMethod("POST");
        req.setMediaType("json");
        req.setArrayId(arrayId);
        FileSystemCloneCreateReqBean reqPayload = new FileSystemCloneCreateReqBean();
        reqPayload.setNAME(name);
        reqPayload.setALLOCTYPE(alloctype);
        reqPayload.setPARENTFILESYSTEMID(parentFilesystemid);
        reqPayload.setPARENTSNAPSHOTID(parentSnapshotId);
        reqPayload.setVstoreId(vstoreId);
        req.setPayload(reqPayload);
        SDKResult<FileSystemCloneCreateResBean> sdkResult = new SDKResult<>();
        LOGGER.info("req data:" + reqPayload.toString());
        try {
            ResBean response = (ResBean) protocolAdapter.syncSendMessage(req, fileSyste_url, "org.opensds.vasa.vasa20.device.array.fileSystem.FileSystemCloneCreateResBean");
            if (response.getErrorCode() == 0) {
                FileSystemCloneCreateResBean result = (FileSystemCloneCreateResBean) response.getResData();
                LOGGER.debug("clone FileSystem  success " + result);
                sdkResult.setResult(result);
            } else {
                LOGGER.error("clone FileSystem  fail! FileSystemCloneCreateResBean = " + response.getDescription());
                sdkResult.setErrCode(response.getErrorCode());
                sdkResult.setDescription(response.getDescription());
            }
        } catch (ProtocolAdapterException e) {
            LOGGER.error("clone FileSystem  fail! FileSystemCloneCreateReqBean = " + reqPayload, e);
            e.printStackTrace();
        }

        return sdkResult;
    }

    @Override
    public SDKResult<FileSystemMigrationResBean> MigrateFileSystem(String arrayId, int sourceFsId, String targetFsName, String snapshotName, int targetStoragePoolId) {
        LOGGER.debug("migrate file system: " + sourceFsId);
        VASARestReqMessage req = new VASARestReqMessage();
        req.setHttpMethod("POST");
        req.setMediaType("json");
        req.setArrayId(arrayId);
        FileSystemMigrationReqBean reqPayload = new FileSystemMigrationReqBean();
        createFsMigrationReqBean(reqPayload, sourceFsId, targetFsName, snapshotName, targetStoragePoolId);
        req.setPayload(reqPayload);
        SDKResult<FileSystemMigrationResBean> sdkResult = new SDKResult<>();
        LOGGER.info("req data:" + reqPayload.toString());
        try {
            ResBean response = (ResBean) protocolAdapter.syncSendMessage(req, migrationFileSystem_url, "org.opensds.vasa.vasa20.device.array.fileSystem.FileSystemMigrationResBean");
            sdkResult.setErrCode(response.getErrorCode());
            sdkResult.setDescription(response.getDescription());
            if (response.getErrorCode() == 0) {
                FileSystemMigrationResBean result = (FileSystemMigrationResBean) response.getResData();
                sdkResult.setResult(result);
                LOGGER.debug(",migrate FileSystem  success " + result);
            } else {
                LOGGER.error("migrate FileSystem fail! FileSystemCloneCreateResBean = " + response.getDescription());
                sdkResult.setErrCode(response.getErrorCode());
                sdkResult.setDescription(response.getDescription());
            }

            LOGGER.debug("success migrate file system,response=" + response);
        } catch (ProtocolAdapterException e) {
            LOGGER.error("migrate file system fail! FileSystemMigrateResBean = " + sdkResult.getDescription());
        }

        return sdkResult;
    }

    @Override
    public SDKResult<FileSystemMigrationUpdateResBean> updateFileSystemMigration(String arrayId, int sourceFsId) {

        LOGGER.debug("update FileSystem Migration:" + sourceFsId);
        VASARestReqMessage req = new VASARestReqMessage();
        req.setHttpMethod("GET");
        req.setArrayId(arrayId);
//        req.setPaging(true);
        SDKResult<FileSystemMigrationUpdateResBean> sdkResult = new SDKResult<FileSystemMigrationUpdateResBean>();
        try {
            ResBean response = (ResBean) protocolAdapter.syncSendMessage(req, migrationFileSystemUpdate_url + sourceFsId, null);
            sdkResult.setErrCode(response.getErrorCode());
            sdkResult.setDescription(response.getDescription());

            if (response.getErrorCode() == 0) {
                JSONObject jsonObject = (JSONObject) response.getResData();
                if (jsonObject.has("data")) {
                    JSONArray array = jsonObject.getJSONArray("data");
                    if (array.length() > 0) {
                        for (int i = 0; i < array.length(); ++i) {
                            FileSystemMigrationUpdateResBean fileSystemMigrationUpdateResBean = JsonUtils.fromJson(array.get(i).toString(), FileSystemMigrationUpdateResBean.class);
                            sdkResult.setResult(fileSystemMigrationUpdateResBean);
                        }
                    }
                } else {
                    LOGGER.warn("query migrate filesystem without data" + response.getDescription());
                    FileSystemMigrationUpdateResBean fileSystemMigrationUpdateResBean = new FileSystemMigrationUpdateResBean();
                    fileSystemMigrationUpdateResBean.setMigrateProgress(String.valueOf(100));
                    sdkResult.setResult(fileSystemMigrationUpdateResBean);
                }
            } else {
                LOGGER.error("update migrate filesystem error" + response.getDescription());
            }
        } catch (ProtocolAdapterException e) {
            LOGGER.error("update migrate filesystem error" + e);
            sdkResult.setErrCode(e.getErrorCode());
            sdkResult.setDescription(e.getMessage());
        }
        return sdkResult;

    }

    private void createFsMigrationReqBean(FileSystemMigrationReqBean reqPayload, int sourceFsId, String targetFsName, String snapshotName, int targetStoragePoolId) {
        reqPayload.setType(type);
        reqPayload.setSourceFsID(sourceFsId);
        reqPayload.setTargetStoragePoolID(String.valueOf(targetStoragePoolId));
        reqPayload.setMigrateSpeed(String.valueOf(migrateSpeedMST));
        reqPayload.setMigrateType(migrateType);
        reqPayload.setPresentMigrateSnapname(snapshotName);
        reqPayload.setBreakAfterMigrate(breakAfterMigrate);
        reqPayload.setCreateSnapAfterMigrate(createSnapAfterMove);
        reqPayload.setMigrateTmpFsName(targetFsName);

    }
}
