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

package org.opensds.vasa.vasa20.device.dj.initiator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensds.vasa.interfaces.device.initiator.IISCSIInitiatorCapability;

import org.opensds.vasa.base.bean.terminal.ResBean;
import org.opensds.vasa.domain.model.bean.S2DISCSIInitiator;

import org.opensds.platform.common.SDKResult;
import org.opensds.platform.commu.itf.ISDKProtocolAdapter;
import org.opensds.platform.exception.ProtocolAdapterException;
import org.opensds.vasa.vasa20.device.AbstractVASACapability;
import org.opensds.vasa.vasa20.device.VASARestReqMessage;
import org.opensds.vasa.vasa20.device.dj.bean.SISCSIInitiator;
import org.opensds.vasa.vasa20.device.dj.convert.ISCSIInitiatorCapabilityConvert;

public class ISCSIInitiatorCapabilityImpl extends AbstractVASACapability implements IISCSIInitiatorCapability {
    private static final Logger LOGGER = LogManager.getLogger(ISCSIInitiatorCapabilityImpl.class);

    private ISCSIInitiatorCapabilityConvert iscsiCapabilityconvert = new ISCSIInitiatorCapabilityConvert();

    public ISCSIInitiatorCapabilityImpl(ISDKProtocolAdapter protocolAdapter) {
        super(protocolAdapter);
    }

    @Override
    public SDKResult<S2DISCSIInitiator> getISCSIInitiator(String arrayId, String id) {
        LOGGER.debug("getISCSIInitiator() start");
        SDKResult<S2DISCSIInitiator> sdkResult = new SDKResult<S2DISCSIInitiator>();

        VASARestReqMessage req = new VASARestReqMessage();
        req.setHttpMethod("GET");
        req.setMediaType("json");
        req.setArrayId(arrayId);
        LOGGER.debug("getISCSIInitiator id=" + id);
        try {
            ResBean res = (ResBean) protocolAdapter.syncSendMessage(req, "/iscsi_initiator/" + id, "org.opensds.vasa.vasa20.device.dj.bean.SISCSIInitiator");

            sdkResult.setErrCode(res.getErrorCode());
            sdkResult.setDescription(res.getDescription());

            if (0 == res.getErrorCode()) {
                SISCSIInitiator ini = (SISCSIInitiator) res.getResData();
                sdkResult.setResult(iscsiCapabilityconvert.convertSouth2Model(ini));
            }
        } catch (ProtocolAdapterException e) {
            sdkResult.setErrCode(e.getErrorCode());
            sdkResult.setDescription(e.getMessage());
            LOGGER.error("getISCSIInitiator() error");
        }
        LOGGER.debug("getISCSIInitiator() end");

        return sdkResult;
    }

}
