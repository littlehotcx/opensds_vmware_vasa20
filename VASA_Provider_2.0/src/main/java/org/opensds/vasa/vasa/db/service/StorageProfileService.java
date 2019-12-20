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

import org.opensds.vasa.vasa.db.model.NStorageProfile;
import org.opensds.vasa.vasa.service.model.StorageProfileData;

import com.vmware.vim.vasa.v20.StorageFault;

public interface StorageProfileService extends BaseService<NStorageProfile> {
    Long getStorageProfileCount() throws StorageFault;

    //	public StorageProfileData getProfileByProfileId(String profileId, String containerId, String thinThick,long generationId, String deprecated) throws StorageFault;
    public List<StorageProfileData> getStorageProfileByContainerId(String containerId) throws StorageFault;

    public String getCurrentProfileRawQosIdByVvolId(String vvolId) throws StorageFault;

    NStorageProfile getStorageProfileByProfileId(String profileId) throws StorageFault;

    int getStorageProfileByProfileName(String profileName) throws StorageFault;

    void updateStorageProfileByProfileId(NStorageProfile storageProfile) throws StorageFault;

    void deleteStorageProfileByProfileId(NStorageProfile storageProfile) throws StorageFault;

    public List<NStorageProfile> queryOmCreateStorageProfileByContainerId(String containerId);

    public List<NStorageProfile> queryOmCreateStorageProfileByPage(String pageIndex, String pageSize) throws StorageFault;

    NStorageProfile getCurrentProfileByVvolid(String vvolId) throws StorageFault;
}
