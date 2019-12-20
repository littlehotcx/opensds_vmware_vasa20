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

package org.opensds.vasa.interfaces.device.lun;

import java.util.List;

import org.opensds.vasa.domain.model.bean.S2DLun;
import org.opensds.vasa.domain.model.bean.S2DLunCopyBean;
import org.opensds.platform.common.SDKResult;

public interface ILunCapability {
    SDKResult<List<S2DLun>> getLunByHostID(String arrayId, String hostId);

    SDKResult<List<S2DLun>> getLunByHostAndPort(String arrayId, String hostId, String metadata);

    SDKResult<List<S2DLun>> getThinLun(String arrayId);

    SDKResult<List<S2DLun>> getPELun(String arrayId);

    SDKResult<S2DLun> getLun(String arrayId, String lunId);

    SDKResult<S2DLunCopyBean> getLunCopy(String arrayId, String lunId);
}
