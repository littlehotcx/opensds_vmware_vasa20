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

package org.opensds.vasa.interfaces.device.luncopy;

import org.opensds.vasa.domain.model.bean.S2DLunCopy;
import org.opensds.platform.common.SDKErrorCode;
import org.opensds.platform.common.SDKResult;

public interface ILunCopyCapability {
    SDKResult<S2DLunCopy> createLuncopy(String arrayId, String name, String description, String sourceLun, String targetLun, String baseLun, boolean isDiffsLunCopy);

    SDKResult<S2DLunCopy> getLuncopyById(String arrayId, String luncopyId);

    SDKErrorCode startLuncopy(String arrayId, String luncopyId, boolean isDiffsLunCopy);

    SDKErrorCode stopLuncopy(String arrayId, String luncopyId, boolean isDiffsLunCopy);

    SDKErrorCode deleteLuncopy(String arrayId, String luncopyId, boolean isDiffsLunCopy);
}
