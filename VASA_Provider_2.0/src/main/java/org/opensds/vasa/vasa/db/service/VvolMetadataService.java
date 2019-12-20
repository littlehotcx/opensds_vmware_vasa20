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

import org.opensds.vasa.vasa.db.model.NVvolMetadata;

import com.vmware.vim.vasa.v20.StorageFault;

public interface VvolMetadataService {
    List<NVvolMetadata> getAllReportVvolMetadata() throws StorageFault;

    List<NVvolMetadata> getVvolMetadataByVvolId(String vvolid) throws StorageFault;

    int getCountByVvolIdAndKey(String vvolid, String key) throws StorageFault;

    void addVvolMetadata(NVvolMetadata vvolMetadata) throws StorageFault;

    void deleteVvolMetadataByVvolIdAndKey(String vvolid, String key) throws StorageFault;

    void deleteVvolMetadataByVvolId(String vvolid) throws StorageFault;

    String getvmNameByVvolId(String vvolid) throws StorageFault;

    void updateVvolMetadataByVvolIdAndKey(String vvolid, String key, String value) throws StorageFault;
}
