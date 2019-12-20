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

package org.opensds.vasa.vasa20.device.dj.host;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensds.vasa.interfaces.device.host.IHostCapability;
import org.opensds.vasa.vasa20.device.dj.convert.HostCapabilityConvert;

import org.opensds.vasa.base.bean.terminal.ResBean;
import org.opensds.vasa.domain.model.bean.S2DHost;

import org.opensds.platform.common.SDKResult;
import org.opensds.platform.commu.itf.ISDKProtocolAdapter;
import org.opensds.platform.exception.ProtocolAdapterException;
import org.opensds.vasa.vasa20.device.AbstractVASACapability;
import org.opensds.vasa.vasa20.device.VASARestReqMessage;
import org.opensds.vasa.vasa20.device.dj.bean.SHost;

public class HostCapabilityImpl extends AbstractVASACapability implements IHostCapability {
    private static final Logger LOGGER = LogManager.getLogger(HostCapabilityImpl.class);

    private HostCapabilityConvert hostCapabilityconvert = new HostCapabilityConvert();

    public HostCapabilityImpl(ISDKProtocolAdapter protocolAdapter) {
        super(protocolAdapter);
    }

    @Override
    public SDKResult<S2DHost> getHostByIBInitiator(String arrayId, String id) {
        LOGGER.debug("getHostByIBInitiator() start");
        SDKResult<S2DHost> sdkResult = new SDKResult<S2DHost>();

        VASARestReqMessage req = new VASARestReqMessage();
        req.setHttpMethod("GET");
        req.setMediaType("json");
        req.setArrayId(arrayId);

        try {
            ResBean res = (ResBean) protocolAdapter.syncSendMessage(req, "/host/associate?ASSOCIATEOBJTYPE=16499&ASSOCIATEOBJID=" + id, "org.opensds.vasa.vasa20.device.dj.bean.SHost");

            sdkResult.setErrCode(res.getErrorCode());
            sdkResult.setDescription(res.getDescription());

            if (0 == res.getErrorCode()) {
                SHost host = (SHost) (res.getResData());
                sdkResult.setResult(hostCapabilityconvert.convertSouth2Model(host));
            }
        } catch (ProtocolAdapterException e) {
            sdkResult.setErrCode(e.getErrorCode());
            sdkResult.setDescription(e.getMessage());
            LOGGER.error("getHostByIBInitiator() error");
        }
        LOGGER.debug("getHostByIBInitiator() end");

        return sdkResult;
    }

}
