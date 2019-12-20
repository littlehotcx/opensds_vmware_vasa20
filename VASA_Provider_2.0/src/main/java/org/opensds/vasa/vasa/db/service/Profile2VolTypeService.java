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

import java.util.Date;
import java.util.List;

import org.opensds.vasa.vasa.db.model.NProfile2VolType;

import com.vmware.vim.vasa.v20.StorageFault;

public interface Profile2VolTypeService {
    NProfile2VolType getProfile2VolTypeByProfileId(String profileId, String containerId, String thinThick, long generationId, String deprecated);

    List<NProfile2VolType> getAllProfile2VolType() throws StorageFault;

    void delProfile2VolType(String profileId, long generationId, String containerId, String thinThick) throws StorageFault;

    void updateLastUseTime(String profileId, Date date, long generationId) throws StorageFault;

    void insertProfile2VolType(NProfile2VolType profile2VolType) throws StorageFault;

    void updateDeprecated(String containerId, String Deprecated) throws StorageFault;
}
