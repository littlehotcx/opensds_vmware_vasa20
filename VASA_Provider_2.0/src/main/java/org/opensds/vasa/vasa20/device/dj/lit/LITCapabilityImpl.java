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

package org.opensds.vasa.vasa20.device.dj.lit;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensds.vasa.interfaces.device.lit.ILITCapability;
import org.opensds.vasa.vasa20.device.dj.bean.SLIT;
import org.opensds.vasa.vasa20.device.dj.convert.LITCapabilityConvert;

import org.opensds.vasa.base.bean.terminal.ResBean;
import org.opensds.vasa.domain.model.bean.S2DLIT;

import org.opensds.platform.common.SDKResult;
import org.opensds.platform.commu.itf.ISDKProtocolAdapter;
import org.opensds.platform.exception.ProtocolAdapterException;
import org.opensds.vasa.vasa20.device.AbstractVASACapability;
import org.opensds.vasa.vasa20.device.VASARestReqMessage;

public class LITCapabilityImpl extends AbstractVASACapability implements ILITCapability {
    private static final Logger LOGGER = LogManager.getLogger(LITCapabilityImpl.class);

    private LITCapabilityConvert litCapabilityconvert = new LITCapabilityConvert();

    public LITCapabilityImpl(ISDKProtocolAdapter protocolAdapter) {
        super(protocolAdapter);
    }

    @Override
    public SDKResult<String> getLITCount(String arrayId) {
        LOGGER.debug("getLITCount() start");
        SDKResult<String> sdkResult = new SDKResult<String>();

        VASARestReqMessage req = new VASARestReqMessage();
        req.setHttpMethod("GET");
        req.setMediaType("json");
        req.setArrayId(arrayId);
        try {
            ResBean res = (ResBean) protocolAdapter.syncSendMessage(req, "/LIF/count", "org.opensds.vasa.vasa20.device.dj.bean.SLIT");

            sdkResult.setErrCode(res.getErrorCode());
            sdkResult.setDescription(res.getDescription());

            if (0 == res.getErrorCode()) {
                SLIT lit = (SLIT) (res.getResData());
                sdkResult.setResult(lit.getCOUNT());
            }
        } catch (ProtocolAdapterException e) {
            sdkResult.setErrCode(e.getErrorCode());
            sdkResult.setDescription(e.getMessage());
            LOGGER.error("getLITCount() error", e);
        }
        LOGGER.debug("getLITCount() end");

        return sdkResult;

    }

    @Override
    public SDKResult<List<S2DLIT>> getLIT(String arrayId, String count) {
        LOGGER.debug("getLITCount() start");
        SDKResult<List<S2DLIT>> sdkResult = new SDKResult<List<S2DLIT>>();
        List<S2DLIT> result = new ArrayList<S2DLIT>();

        VASARestReqMessage req = new VASARestReqMessage();
        req.setHttpMethod("GET");
        req.setMediaType("json");
        req.setPageSize(Integer.parseInt(count));
        req.setPaging(true);
        req.setArrayId(arrayId);

        try {
            ResBean res = (ResBean) protocolAdapter.syncSendMessage(req, "/LIF?", "org.opensds.vasa.vasa20.device.dj.bean.SLIT");

            sdkResult.setErrCode(res.getErrorCode());
            sdkResult.setDescription(res.getDescription());

            if (0 == res.getErrorCode()) {
                if (0 == res.getErrorCode()) {
                    @SuppressWarnings("unchecked")
                    List<SLIT> list = (List<SLIT>) res.getResData();
                    for (int i = 0; i < list.size(); i++) {
                        result.add(litCapabilityconvert.convertSouth2Model(list.get(i)));
                    }
                }
                sdkResult.setResult(result);
            }
        } catch (ProtocolAdapterException e) {
            sdkResult.setErrCode(e.getErrorCode());
            sdkResult.setDescription(e.getMessage());
            LOGGER.error("getLITCount() error", e);
        }

        LOGGER.debug("getLITCount() end");

        return sdkResult;
    }
}
