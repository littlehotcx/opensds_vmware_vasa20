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

package org.opensds.vasa.vasa20.device.dj.system;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensds.vasa.interfaces.device.system.ISystemCapability;
import org.opensds.vasa.vasa20.device.dj.convert.SystemCapabilityConvert;

import org.opensds.vasa.base.bean.terminal.ResBean;
import org.opensds.vasa.domain.model.bean.S2DSystem;

import org.opensds.platform.common.SDKResult;
import org.opensds.platform.commu.itf.ISDKProtocolAdapter;
import org.opensds.platform.exception.ProtocolAdapterException;
import org.opensds.vasa.vasa20.device.AbstractVASACapability;
import org.opensds.vasa.vasa20.device.VASARestReqMessage;
import org.opensds.vasa.vasa20.device.dj.bean.SSystem;

public class SystemCapabilityImpl extends AbstractVASACapability implements ISystemCapability {
    private static final Logger LOGGER = LogManager.getLogger(SystemCapabilityImpl.class);

    private SystemCapabilityConvert sysCapabilityconvert = new SystemCapabilityConvert();

    public SystemCapabilityImpl(ISDKProtocolAdapter protocolAdapter) {
        super(protocolAdapter);
    }

    @Override
    public SDKResult<S2DSystem> getSystemInfo(String arrayId) {
        LOGGER.debug("getSystemInfo() start");
        SDKResult<S2DSystem> sdkResult = new SDKResult<S2DSystem>();

        VASARestReqMessage req = new VASARestReqMessage();
        req.setHttpMethod("GET");
        req.setMediaType("json");
        req.setArrayId(arrayId);

        try {
            ResBean res = (ResBean) protocolAdapter.syncSendMessage(req, "/system/", "org.opensds.vasa.vasa20.device.dj.bean.SSystem");

            sdkResult.setErrCode(res.getErrorCode());
            sdkResult.setDescription(res.getDescription());

            if (0 == res.getErrorCode()) {
                SSystem ss = (SSystem) (res.getResData());
                sdkResult.setResult(sysCapabilityconvert.convertSouth2Model(ss));
            }
        } catch (ProtocolAdapterException e) {
            sdkResult.setErrCode(e.getErrorCode());
            sdkResult.setDescription(e.getMessage());
            LOGGER.error("getSystemInfo() error");
        }
        LOGGER.debug("getSystemInfo() end");

        return sdkResult;
    }


}
