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

package org.opensds.vasa.vasa.db.dao;

import java.util.List;
import java.util.Map;

import org.opensds.vasa.vasa.db.model.NStoragePool;

public interface StoragePoolDao extends BaseDao<NStoragePool> {
    List<NStoragePool> queryStoragePoolByContainerId(String containerId);

    List<NStoragePool> queryStoragePoolByArrayId(String arrayId);

    List<NStoragePool> getUnbindStoragePoolPageByArrayId(Map map);

    List<NStoragePool> getAllBindStoragePoolByArrayId(String arrayId);

    int getUnbindStoragePoolSizeByArrayId(String arrayId);

    //void bindStoragePools(Map<String, Object> map);
    //void unbindStoragePools(Map<String, Object> map);
    List<NStoragePool> queryStoragePoolByArrayIdAndPageSize(Map map);

    List<NStoragePool> queryStoragePoolByContainerIdAndPageSize(Map map);

    void deleteStoragePoolsByPoolIds(Map<String, Object> map);

    void setStoragePoolsLost(Map<String, Object> map);
}