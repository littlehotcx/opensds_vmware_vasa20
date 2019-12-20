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

package org.opensds.vasa.vasa20.device.dj.sysdstconfig;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensds.vasa.interfaces.device.sysdstconfig.ISystemDSTConfigCapability;

import org.opensds.vasa.base.bean.terminal.ResBean;
import org.opensds.vasa.domain.model.bean.S2DSystemDSTConfig;

import org.opensds.platform.common.SDKResult;
import org.opensds.platform.commu.itf.ISDKProtocolAdapter;
import org.opensds.platform.exception.ProtocolAdapterException;
import org.opensds.vasa.vasa20.device.AbstractVASACapability;
import org.opensds.vasa.vasa20.device.VASARestReqMessage;
import org.opensds.vasa.vasa20.device.dj.bean.SSystemDSTConfig;
import org.opensds.vasa.vasa20.device.dj.convert.SystemDSTConfigCapabilityConvert;

public class SystemDSTConfigCapabilityImpl extends AbstractVASACapability implements ISystemDSTConfigCapability {
    private static final Logger LOGGER = LogManager.getLogger(SystemDSTConfigCapabilityImpl.class);

    private SystemDSTConfigCapabilityConvert sysdstconfCapabilityconvert = new SystemDSTConfigCapabilityConvert();

    public SystemDSTConfigCapabilityImpl(ISDKProtocolAdapter protocolAdapter) {
        super(protocolAdapter);
    }

    @Override
    public SDKResult<S2DSystemDSTConfig> getTimeZoneDST(String arrayId,
                                                        String timeZoneName) {
        LOGGER.debug("getTimeZoneDST() start");
        SDKResult<S2DSystemDSTConfig> sdkResult = new SDKResult<S2DSystemDSTConfig>();
        VASARestReqMessage req = new VASARestReqMessage();
        req.setHttpMethod("GET");
        req.setMediaType("json");
        req.setArrayId(arrayId);

        try {
            ResBean res = (ResBean) protocolAdapter.syncSendMessage(req, "/system_dst_config?CMO_SYS_DST_CONF_TIME_ZONE_NAME=" + timeZoneName, "org.opensds.vasa.vasa20.device.dj.bean.SSystemDSTConfig");

            sdkResult.setErrCode(res.getErrorCode());
            sdkResult.setDescription(res.getDescription());

            if (0 == res.getErrorCode()) {
                SSystemDSTConfig conf = (SSystemDSTConfig) (res.getResData());
                sdkResult.setResult(sysdstconfCapabilityconvert.convertSouth2Model(conf));
            }
        } catch (ProtocolAdapterException e) {
            sdkResult.setErrCode(e.getErrorCode());
            sdkResult.setDescription(e.getMessage());
            LOGGER.error("getTimeZoneDST() error", e);
        }
        LOGGER.debug("getTimeZoneDST() end");

        return sdkResult;

    }

}
