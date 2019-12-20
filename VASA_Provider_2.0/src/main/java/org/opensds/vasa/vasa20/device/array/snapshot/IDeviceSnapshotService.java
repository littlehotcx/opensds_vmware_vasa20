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

package org.opensds.vasa.vasa20.device.array.snapshot;

import java.util.List;

import org.opensds.platform.common.SDKErrorCode;
import org.opensds.platform.common.SDKResult;

/**
 * @author j00405142
 */
public interface IDeviceSnapshotService {

    /**
     * @param @param  name
     * @param @param  parentId  	lunID or SnapshotId
     * @param @param  description
     * @param @param  subType   	0:common LUN or 1:vvol LUN
     * @param @param  parentType 	11:LUN or 27:snapshot
     * @param @return
     * @return ResBean
     * @throws
     * @Title: createSnapshot
     * @Description: TODO(这里用一句话描述这个方法的作用)
     */
    public SDKResult<SnapshotCreateResBean> createSnapshot(String arrayId, String name, String parentId, String description, String subType, String parentType);


    public SDKErrorCode activeVvolLunSnapshot(String arrayId, List<String> snapshotlist);


    public SDKErrorCode deleteSnapshot(String arrayId, String snapshotId);

    public SDKErrorCode deactivateSnapshot(String arrayId, String snapshotId);

    public SDKResult<SnapshotCreateResBean> querySnapshotInfo(String arrayId, String snapshotId);
}
