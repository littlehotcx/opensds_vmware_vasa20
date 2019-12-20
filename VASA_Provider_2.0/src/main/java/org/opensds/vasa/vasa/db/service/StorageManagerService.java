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

import org.opensds.vasa.vasa.db.model.StorageInfo;
import org.opensds.vasa.vasa.db.model.VvolType;

public interface StorageManagerService {
    long getStorageArrayCount();

    void setInfo(StorageInfo storageInfo);

    List<StorageInfo> queryInfo();

    List<StorageInfo> queryStorageArray(String pageSize, String pageIndex);

    void deleteInfo(StorageInfo storageInfo);

    void modifyInfo(StorageInfo storageInfo);

    void addExistDevice(StorageInfo storageInfo);

    void updateExistDeviceInfo(StorageInfo storageInfo);

    List<VvolType> queryVvolType();

    StorageInfo queryInfoByArrayId(String arrayId);

    void syncInfo(StorageInfo storageInfo);

    void updateStatus(StorageInfo storageInfo);

    StorageInfo getStorageBySn(String sn);

    List<StorageInfo> needSyncStorageInfo(String syncIp);
}
