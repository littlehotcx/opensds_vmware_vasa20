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

package org.opensds.vasa.vasa20.device.dj.enuminfo;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensds.vasa.interfaces.device.enuminfo.IEnumInfoCapability;

import org.opensds.vasa.base.bean.terminal.ResBean;
import org.opensds.vasa.domain.model.bean.S2DEnumInfo;

import org.opensds.platform.common.SDKResult;
import org.opensds.platform.commu.itf.ISDKProtocolAdapter;
import org.opensds.platform.exception.ProtocolAdapterException;
import org.opensds.vasa.vasa20.device.AbstractVASACapability;
import org.opensds.vasa.vasa20.device.VASARestReqMessage;
import org.opensds.vasa.vasa20.device.dj.bean.SEnumInfo;
import org.opensds.vasa.vasa20.device.dj.convert.EnumInfoCapabilityConvert;

public class EnumInfoCapabilityImpl extends AbstractVASACapability implements IEnumInfoCapability {
    private static final Logger LOGGER = LogManager.getLogger(EnumInfoCapabilityImpl.class);

    private EnumInfoCapabilityConvert controllerCapabilityconvert = new EnumInfoCapabilityConvert();

    public EnumInfoCapabilityImpl(ISDKProtocolAdapter protocolAdapter) {
        super(protocolAdapter);
    }

    @Override
    public SDKResult<List<S2DEnumInfo>> getAllStorageCapabilities(String arrayId) {
        LOGGER.debug("getAllStorageCapabilities() start");
        SDKResult<List<S2DEnumInfo>> sdkResult = new SDKResult<List<S2DEnumInfo>>();
        List<S2DEnumInfo> result = new ArrayList<S2DEnumInfo>();

        VASARestReqMessage req = new VASARestReqMessage();
        req.setHttpMethod("GET");
        req.setMediaType("json");
        req.setPaging(true);
        req.setArrayId(arrayId);

        try {
            ResBean res = (ResBean) protocolAdapter.syncSendMessage(req, "/enum_info?ENUM_NAME=STORAGE_CAPABILITY", "org.opensds.vasa.vasa20.device.dj.bean.SEnumInfo");

            sdkResult.setErrCode(res.getErrorCode());
            sdkResult.setDescription(res.getDescription());

            if (0 == res.getErrorCode()) {
                @SuppressWarnings("unchecked")
                List<SEnumInfo> list = (List<SEnumInfo>) res.getResData();
                for (int i = 0; i < list.size(); i++) {
                    result.add(controllerCapabilityconvert.convertSouth2Model(list.get(i)));
                }
            }
            sdkResult.setResult(result);
        } catch (ProtocolAdapterException e) {
            sdkResult.setErrCode(e.getErrorCode());
            sdkResult.setDescription(e.getMessage());
            LOGGER.error("getAllStorageCapabilities() error");
        }

        LOGGER.debug("getAllStorageCapabilities() end");

        return sdkResult;
    }

}
