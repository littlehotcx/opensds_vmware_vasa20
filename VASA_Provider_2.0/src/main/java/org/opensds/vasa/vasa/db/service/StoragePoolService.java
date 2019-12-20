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

import org.opensds.vasa.domain.model.bean.S2DStoragePool;
import org.opensds.vasa.vasa.db.model.NStoragePool;

import com.vmware.vim.vasa.v20.StorageFault;

public interface StoragePoolService extends BaseService<NStoragePool> {

    public List<NStoragePool> getPoolListByContainerId(String containerId) throws StorageFault;

    List<NStoragePool> queryStoragePoolByContainerId(String containerId);

    List<NStoragePool> queryStoragePoolByArrayId(String arrayId);

    List<NStoragePool> getUnbindStoragePoolPageByArrayId(String arrayId, String pageIndex, String pageSize);

    List<NStoragePool> getAllBindStoragePoolByArrayId(String arrayId);

    int getUnbindStoragePoolSizeByArrayId(String arrayId);

    void updatePool(S2DStoragePool s2dStoragePool, String arrayId);

    void saveStorageData(S2DStoragePool s2dStoragePool, String arrayId);

    //void bindStoragePools(String arrayId, String containerId, String[] pools);
    void bindStoragePools(NStoragePool storagePool);

    void unbindStoragePools(String arrayId, String containerId, String[] pools);

    void setStoragePoolsLost(String arrayId, String containerId, String[] pools);

    boolean descStoragePoolSize(String rawPoolId, long freeCapacity);

    boolean incStoragePoolSize(String rawPoolId, long freeCapacity);

    List<NStoragePool> queryStoragePoolByArrayIdAndPageSize(String arrayId, String pageIndex, String pageSize);

    List<NStoragePool> queryStoragePoolByContainerIdAndPageSize(String containerId, String pageIndex, String pageSize);
}
