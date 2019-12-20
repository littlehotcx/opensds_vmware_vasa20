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

package org.opensds.vasa.vasa.db.service;

import java.util.List;

import org.opensds.vasa.vasa.db.model.FileSystemTable;
import org.opensds.vasa.vasa.db.model.FilesystemShareTable;
import org.opensds.vasa.vasa.db.model.ShareClientTable;
import org.opensds.vasa.vasa.db.model.VvolPath;

import com.vmware.vim.vasa.v20.StorageFault;

public interface VvolPathService {

    boolean isExistFileSystem(String vvolType, String fileSystemName);

    List<VvolPath> queryAllVvolPathByFileSystem(String fileSystemName);

    //插入一条新的记录
    void insertRecord(VvolPath record) throws StorageFault;

    //更新sharePath
    void updateSharePath(VvolPath newSharePath);

    VvolPath getVvolPathByVvolId(String vvolId);

    void deleteRecordByVvolId(String vvolId);

    int getCountRecordByFileSystemName(String fileSystemName);

    void updateVvolId(VvolPath vvolPath);

    void deleteVvolPathByVvolId(String vvolId);

    FileSystemTable queryFileSystemTableByName(String fileSystemName);

    List<FileSystemTable> queryFileSystemTableByFuzzySystemName(String fileSystemName);

    void insertFilesystemTable(FileSystemTable recode);

    void deleteFilesystemTableByName(String fileSystemName);

    void updateFileCount(String fileSystemName, int num);

    void updateFileCountAndCapacity(String fileSystemName, int num, long capacity);

    void updateCurrCapacity(String fileSystemName, long capacity);

    void updateFilesystemTableFsID(String fileSystemName, String id);

    void updateStatus(String fileSystemName, String status);

    FilesystemShareTable queryShareByShareName(String shareName);

    void insertShareRecord(FilesystemShareTable record);

    void deleteShareByShareName(String shareName);

    List<FilesystemShareTable> queryAllShare();

    void updateHostId(FilesystemShareTable record);

    List<ShareClientTable> queryShareClientByShareId(String shareId);

    void insertShareClientRecord(ShareClientTable record);

    void deleteShareClientByShareId(String shareId);

    void deleteRecordByProperty(String property);

    boolean isBindState(String vvolId);

    void setBindState(String vvolId, boolean isBind);
}
