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

package org.opensds.vasa.interfaces.device.vvolbind;

import java.util.List;

import org.opensds.vasa.domain.model.bean.S2DVvolBind;
import org.opensds.platform.common.SDKErrorCode;
import org.opensds.platform.common.SDKResult;

public interface IVvolBindCapability {
    SDKResult<S2DVvolBind> bind(String arrayId, String hostId, String vvolId, int bindType);

    SDKErrorCode unbindVvolFromAllHost(String arrayId, String vvolId);

    SDKErrorCode unbindAllVvolFromHost(String arrayId, String hostId);

    SDKErrorCode unbindVvolFromPELun(String arrayId, String vvolSecondaryId, String PELunId, int bindType);

    SDKErrorCode unbindVvolFromPELunAndHost(String arrayId, String hostId, String vvolSecondaryId, String PELunId, int bindType);

    /**
     * 查询绑定关系<br/>
     * 目前支持的查询方式：
     * <ul>
     *  <li>url?range:[0-100]&hostId=xxx</li>
     *  <li>url?range:[0-100]&peLUNId=xxx</li>
     *  <li>url?filter=VVOLID::1&range=[0-100]</li>
     *  <li>url?range:[0-100]&hostId=xxx&peLUNId=xxx</li>
     *  <li>url?range:[0-100]&vvolId=xxx&peLUNId=xxx</li>
     *  <li>url?range:[0-100]&vvolSecondaryId=xxx&peLUNId=xxx</li>
     * </ul>
     * 以下参数都不能为空，但是queryCondition的size可以为0
     *
     * @param start          每次都是100的整数倍
     * @param end            每次都是100的整数倍
     * @param queryCondition 是上面的查询条件中的键值对
     * @param deviceId
     * @param vvolId
     */
    public SDKResult<List<S2DVvolBind>> getVVOLBind(String arrayId, String vvolId);
}
