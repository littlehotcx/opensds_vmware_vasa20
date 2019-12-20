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

package org.opensds.vasa.interfaces.device.snapshot;

import java.util.List;

import org.opensds.vasa.domain.model.bean.S2DSnapshot;
import org.opensds.platform.common.SDKErrorCode;
import org.opensds.platform.common.SDKResult;

public interface ISnapshotCapability {
    SDKResult<List<S2DSnapshot>> getAllSnapshot();

    SDKResult<S2DSnapshot> getSnapshotById(String snapshotId);

    SDKResult<S2DSnapshot> createSnapshot(String vvolId, String name, String description);

    SDKErrorCode deleteSnapshot(String snapshotId);

    SDKErrorCode deleteSnapshotForcely(String snapshotId);


}
