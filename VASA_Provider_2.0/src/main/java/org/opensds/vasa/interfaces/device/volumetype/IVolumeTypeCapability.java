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

package org.opensds.vasa.interfaces.device.volumetype;

import java.util.List;

import org.json.JSONObject;

import org.opensds.vasa.domain.model.bean.S2DExtraSpecsInfo;
import org.opensds.vasa.domain.model.bean.S2DVolumeType;
import org.opensds.vasa.domain.model.bean.StoragePolicy;
import org.opensds.platform.common.SDKResult;
import org.opensds.platform.exception.ProtocolAdapterException;
import org.opensds.vasa.vasa20.device.dj.bean.SQos;

public interface IVolumeTypeCapability {
    SDKResult<List<S2DVolumeType>> getAllVolumeType();

    SDKResult<List<S2DVolumeType>> getVolumeTypeByVirtualPool(String poolId);

    SDKResult<JSONObject> createVolumeType(StoragePolicy profile);

    SDKResult<SQos> createQoS(JSONObject qos_specs);

    SDKResult<String> associateQoS(String qosId, String volTypeId);

    void convertQos2ExtraSpecs(SQos qosInfo, S2DExtraSpecsInfo specsInfo);

    SDKResult<SQos> getQosByVolumeType(String volTypeId);

    SDKResult<Object> setVolumeRetype(String volId, String newType, String migrationPolicy);

    void delVolumeType(String volTypeId) throws ProtocolAdapterException;

    void delQos(String qosId) throws ProtocolAdapterException;

    void disassociateQoS(String qosId, String volTypeId) throws ProtocolAdapterException;
}
