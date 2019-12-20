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

package org.opensds.vasa.vasa;

import java.util.List;

import org.opensds.platform.common.SDKErrorCode;
import org.opensds.platform.common.SDKResult;
import org.opensds.platform.common.exception.SDKException;
import org.opensds.vasa.vasa.db.model.NVirtualVolume;
import org.opensds.vasa.vasa.db.model.VvolPath;
import org.opensds.vasa.vasa20.device.array.NFSshare.AddAuthClientResBean;
import org.opensds.vasa.vasa20.device.array.NFSvvol.NFSvvolCreateResBean;
import org.opensds.vasa.vasa20.device.array.fileSystem.FileSystemCreateResBean;
import org.opensds.vasa.vasa20.device.array.fileSystem.FileSystemSnapshotCreateResBean;

import com.vmware.vim.vasa.v20.StorageFault;
import com.vmware.vim.vasa.v20.data.vvol.xsd.BatchVirtualVolumeHandleResult;
import com.vmware.vim.vasa.v20.data.xsd.NameValuePair;


public interface VasaNasArrayService {

    class MigrateResult {
        public static final String MIGRATE_CREATE = "create";
        public static final String MIGRATE_QUERY = "query";
        public static final String MIGRATE_ONLINE = "on_";
        public String poolId;
        public String sourceFsId;
        public String migrateOpt;
        public String snapshotId;
        public String sourcePath;
        public String fsName;
    }

    class QueryMigrateResult {
        public String result;
        public String targetFsId;
        public boolean isSameTask;
        public static final String MIGRATE_SUCC = "success";
        public static final String MIGRATE_FAIL = "fail";
        public static final String MIGRATE_RUNNING = "running";
    }

    SDKResult<NFSvvolCreateResBean> createFile(String vvolType, String vvolId, String poolId, long capacity) throws StorageFault;

    SDKErrorCode deleteFile(String vvolId, String vvolType, String arrayId) throws StorageFault;

    SDKResult<NFSvvolCreateResBean> operateFile(String vvolId, String vvolType, long capacity, String cmd,
                                                String fileSystemName, StringBuilder outSrcPath) throws StorageFault;

    List<BatchVirtualVolumeHandleResult> bindNasVirtualVolume(String arrayId, String vvolId, String rawId,
                                                              String sourceType, int bindType) throws StorageFault;

    void cloneFileSystem(String poolId, NVirtualVolume srcVvol, String uuId) throws StorageFault, SDKException;

    SDKResult<FileSystemSnapshotCreateResBean> createSnapshotFromSourceFS(String srcVvolId, String snapVvolId,
                                                                          String name, String desc) throws SDKException, StorageFault;

    SDKErrorCode deleteFSSnapshot(String snapVvolId) throws SDKException, StorageFault;

    SDKErrorCode rollbackFsSapshot(String snapVvolId) throws SDKException, StorageFault;

    MigrateResult migrateFilesystem(String arrayId, String poolId, long sizeInMB, List<NameValuePair> metadata, String newContainerId,
                                    String srcVvolType, NVirtualVolume srcVvol, String newUuId) throws StorageFault, SDKException;

    QueryMigrateResult updateMigrateTask(String[] paras, NVirtualVolume vvol) throws StorageFault;

    SDKResult<FileSystemCreateResBean> queryFileSystemByName(String name) throws StorageFault;

    SDKErrorCode modifyFIleSystem(String arrayId, String vvolId, String fsId, Long capacity) throws StorageFault;

    void clearMigrateDate(String[] paras) throws SDKException, StorageFault;

    void setVvolMetadata(String vvolId);

    int createGNSshare(String arrayId) throws StorageFault;

    int createTempShare(String arrayId, String poolId);

    int deleteTempShare(String arrayId);

    SDKResult<AddAuthClientResBean> createClient(String arrayId, String ip, String shareId);

    SDKErrorCode deleteClient(String arrayId, String clientId);

    void updateShare(String arrayId, String vvolType, VvolPath vvolPath, boolean isBind) throws StorageFault;
}
