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

package org.opensds.vasa.vasa20.device.dj.oeminfo;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensds.vasa.interfaces.device.oeminfo.IOEMInfoCapability;

import org.opensds.vasa.base.bean.terminal.ResBean;
import org.opensds.vasa.domain.model.bean.S2DOemInfo;

import org.opensds.platform.common.SDKResult;
import org.opensds.platform.commu.itf.ISDKProtocolAdapter;
import org.opensds.platform.exception.ProtocolAdapterException;
import org.opensds.vasa.vasa20.device.AbstractVASACapability;
import org.opensds.vasa.vasa20.device.VASARestReqMessage;
import org.opensds.vasa.vasa20.device.dj.bean.SOemInfo;
import org.opensds.vasa.vasa20.device.dj.convert.OEMInfoCapabilityConvert;

public class OEMInfoCapabilityImpl extends AbstractVASACapability implements IOEMInfoCapability {
    private static final Logger LOGGER = LogManager.getLogger(OEMInfoCapabilityImpl.class);

    private OEMInfoCapabilityConvert oeminfoCapabilityconvert = new OEMInfoCapabilityConvert();

    public OEMInfoCapabilityImpl(ISDKProtocolAdapter protocolAdapter) {
        super(protocolAdapter);
    }

    @Override
    public SDKResult<List<S2DOemInfo>> getAllOemInfo(String arrayId) {
        LOGGER.debug("getAllOemInfo() start");
        SDKResult<List<S2DOemInfo>> sdkResult = new SDKResult<List<S2DOemInfo>>();
        List<S2DOemInfo> result = new ArrayList<S2DOemInfo>();

        VASARestReqMessage req = new VASARestReqMessage();
        req.setHttpMethod("GET");
        req.setMediaType("json");
        req.setPaging(true);
        req.setArrayId(arrayId);
        try {
            ResBean res = (ResBean) protocolAdapter.syncSendMessage(req, "/oem_info/manufactory", "org.opensds.vasa.vasa20.device.dj.bean.SOemInfo");
            sdkResult.setErrCode(res.getErrorCode());
            sdkResult.setDescription(res.getDescription());
            if (0 == res.getErrorCode()) {
                @SuppressWarnings("unchecked")
                List<SOemInfo> list = (List<SOemInfo>) res.getResData();
                for (int i = 0; i < list.size(); i++) {
                    result.add(oeminfoCapabilityconvert.convertSouth2Model(list.get(i)));
                }
            }
            sdkResult.setResult(result);
        } catch (ProtocolAdapterException e) {
            sdkResult.setErrCode(e.getErrorCode());
            sdkResult.setDescription(e.getMessage());
            LOGGER.error("getAllOemInfo() error", e);
        }

        LOGGER.debug("getAllOemInfo() end");

        return sdkResult;
    }

}
