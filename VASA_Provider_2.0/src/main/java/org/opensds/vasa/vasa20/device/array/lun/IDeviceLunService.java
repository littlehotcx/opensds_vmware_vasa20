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

package org.opensds.vasa.vasa20.device.array.lun;

import org.opensds.platform.common.SDKErrorCode;
import org.opensds.platform.common.SDKResult;

public interface IDeviceLunService {

    public SDKResult<LunCreateResBean> createLun(String arrayId, String name, String description, String parentId,
                                                 int subType, String allocType, long capacity,
                                                 String dataTransferPolicy, int usageType, int ioProperty);

    public SDKErrorCode deleteLun(String arrayId, String lunId);

    public SDKResult<LunCreateResBean> queryLunInfo(String arrayId, String lunId);

    public SDKResult<LunCopyCreateResBean> createLunCopy(String arrayId, String name, String description, String sourceLunId, String targetLunId);

    public SDKErrorCode startLunCopy(String arrayId, String lunCopyId);

    public SDKResult<LunCopyCreateResBean> queryLunCopyInfo(String arrayId, String lunCopyId);

    public SDKErrorCode expandLun(String arrayId, String lun, long sizeInMb);

    public SDKErrorCode updateLun(String arrayId, String lunId, int ioProperty, String smartTier);

}
