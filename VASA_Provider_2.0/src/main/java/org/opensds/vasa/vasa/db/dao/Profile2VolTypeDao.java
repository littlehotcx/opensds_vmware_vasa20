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

import org.opensds.vasa.vasa.db.model.NProfile2VolType;

public interface Profile2VolTypeDao {
    NProfile2VolType getProfile2VolTypeByProfileId(Map<String, Object> profile2VolType);

    List<NProfile2VolType> getAllProfile2VolType();

    void delProfile2VolType(Map<String, Object> delParamMap);

    void updateLastUseTime(NProfile2VolType profile2VolType);

    void insertProfile2VolType(NProfile2VolType profile2VolType);

    void updateDeprecated(Map<String, Object> paramMap);
}
