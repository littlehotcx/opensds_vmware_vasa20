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

import org.opensds.platform.common.SDKErrorCode;
import org.opensds.platform.common.SDKResult;

/**
 * 功能描述
 *
 * @author h00451513
 * @since 2019-02-27
 */
public interface IFileSystemService {

    long FILE_SYS_EXIST = 1077948993;

    long FILE_SYS_NOT_EXITS = 1073752065;

    long FILE_SYS_SNAP_NOT_EXIST = 1073754118;

    long FILE_SYS_SNAP_EXIST = 1077948993;

    long FILE_MIGRATE_BUSY = 1088539173;

    int type = 57802;

    int breakAfterMigrate = 1;// 0 stand for delete reletionship

    int createSnapAfterMove = 0; //1 stand for no snapshot after move

    int migrateType = 2;//2 stand for migrate snap only

    int migrateSpeedMid = 2;
    int migrateSpeedFST = 3;
    int migrateSpeedMST = 4;


    public SDKResult<FileSystemCreateResBean> createFileSystem(String arrayId, String name, String parentId, long capacity, String alloctype, int sectorSize, String spaceSelfadjustingMode, int snapshotReserveper, String owningController, boolean isCloneFS, String vstoreId, int autoShringkThresholdPercent, int autoGrowThresholdPercent, Long maxAutoSize, Long autoSizeIncrement);

    public SDKResult<FileSystemCreateResBean> queryFileSystemById(String arrayId, String fileSystemId);

    public SDKResult<FileSystemCreateResBean> queryFileSystemByName(String arrayId, String fileSystemName);

    public SDKErrorCode deleteFileSystem(String arrayId, String fileSystemId, String vStoreId);

    public void ModifyFileSystem(String arrayId, String fileSystemId, String Capacity);

    public SDKErrorCode ModifyFileSystemMaxAutosize(String arrayId, String fileSystemId, Long maxAutosize);

    public SDKResult<FileSystemSnapshotCreateResBean> createFSSnapshot(String arrayId, String fsSnapshotName, String parentId, String parentType);

    public SDKErrorCode deleteFSSnapshot(String arrayId, String fsSnapshotId, String vstoreId);

    public SDKErrorCode RollbackFSSnapshot(String arrayId, String fsSnapshotId, String vstoreId);

    public SDKResult<FileSystemCloneCreateResBean> CloneFileSystem(String arrayId, String name, String alloctype, String parentFilesystemid, String parentSnapshotId, String vstoreId);

    public SDKResult<FileSystemMigrationResBean> MigrateFileSystem(String arrayId, int sourceFsId, String targetFsName, String snapshotName, int targetStoragePoolId);

    public SDKResult<FileSystemMigrationUpdateResBean> updateFileSystemMigration(String arrayId, int sourceFsId);

}
