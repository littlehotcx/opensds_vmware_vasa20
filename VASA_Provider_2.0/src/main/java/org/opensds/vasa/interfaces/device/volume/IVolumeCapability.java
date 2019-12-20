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

package org.opensds.vasa.interfaces.device.volume;

import java.util.List;

import org.opensds.vasa.domain.model.bean.S2DVolume;
import org.opensds.platform.common.SDKErrorCode;
import org.opensds.platform.common.SDKResult;

public interface IVolumeCapability {
    SDKResult<List<S2DVolume>> getAllVolume();

    SDKResult<S2DVolume> getVolumeById(String volumeId);

    SDKResult<S2DVolume> createVolume(String name, String description, int sizeInGB, long sizeInMB, String volumeType, String vmName);

    SDKResult<S2DVolume> createVolumeFromSrcVolume(String name, String description, int sizeInGB, long sizeInMB, String volumeType, String volumeId
            , String vmName);

    SDKErrorCode resizeVolume(String id, int newSize);

    SDKErrorCode deleteVolume(String id);

    SDKErrorCode deleteVolumeForcely(String id);

    SDKResult<S2DVolume> cloneVolumeFromRawVvol(String name, String description, String sourceVvolId, long sizeInMB);

    SDKResult<S2DVolume> cloneVolumeFromSnapshotVvol(String name, String description, String snapshotId, long sizeInMB);

    SDKResult<S2DVolume> fastCloneVolumeFromRawVvol(String name, String description, String sourceVvolId, String vmName);

    SDKResult<S2DVolume> fastCloneVolumeFromSnapshotVvol(String name, String description, String snapshotId, String vmName);
}
