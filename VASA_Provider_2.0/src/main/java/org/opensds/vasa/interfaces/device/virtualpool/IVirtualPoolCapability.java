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

package org.opensds.vasa.interfaces.device.virtualpool;

import java.util.List;

import org.opensds.vasa.domain.model.bean.S2DVirtualPool;
import org.opensds.vasa.domain.model.bean.S2DVirtualPoolSpaceStats;
import org.opensds.platform.common.SDKResult;

public interface IVirtualPoolCapability {
    SDKResult<List<S2DVirtualPool>> getAllVirtualPool();

    SDKResult<S2DVirtualPool> getVirtualPoolById(String poolId);

    SDKResult<S2DVirtualPoolSpaceStats> getVirtualPoolSpaceStatsById(String poolId);

    SDKResult<List<S2DVirtualPool>> getVirtualPoolByArrayId(String arrayId);

}
