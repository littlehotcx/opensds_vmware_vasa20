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

package org.opensds.vasa.vasa20.device.dj.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensds.vasa.interfaces.device.controller.IControllerCapability;

import org.opensds.vasa.base.bean.terminal.ResBean;
import org.opensds.vasa.domain.model.bean.S2DController;

import org.opensds.platform.common.SDKResult;
import org.opensds.platform.commu.itf.ISDKProtocolAdapter;
import org.opensds.platform.exception.ProtocolAdapterException;
import org.opensds.vasa.vasa20.device.AbstractVASACapability;
import org.opensds.vasa.vasa20.device.VASARestReqMessage;
import org.opensds.vasa.vasa20.device.dj.bean.SController;
import org.opensds.vasa.vasa20.device.dj.convert.ControllerCapabilityConvert;


public class ControllerCapabilityImpl extends AbstractVASACapability implements IControllerCapability {
    private static final Logger LOGGER = LogManager.getLogger(ControllerCapabilityImpl.class);

    private ControllerCapabilityConvert controllerCapabilityconvert = new ControllerCapabilityConvert();

    public ControllerCapabilityImpl(ISDKProtocolAdapter protocolAdapter) {
        super(protocolAdapter);
    }

    @Override
    public SDKResult<List<S2DController>> getAllController(String arrayId) {
        LOGGER.debug("getAllController() start");
        SDKResult<List<S2DController>> sdkResult = new SDKResult<List<S2DController>>();
        List<S2DController> result = new ArrayList<S2DController>();

        VASARestReqMessage req = new VASARestReqMessage();
        req.setHttpMethod("GET");
        req.setMediaType("json");
        req.setArrayId(arrayId);
        req.setPaging(true);

        try {
            ResBean res = (ResBean) protocolAdapter.syncSendMessage(req, "controller",
                    "org.opensds.vasa.vasa20.device.dj.bean.SController");

            sdkResult.setErrCode(res.getErrorCode());
            sdkResult.setDescription(res.getDescription());

            if (0 == res.getErrorCode()) {
                @SuppressWarnings("unchecked")
                List<SController> controllers = (List<SController>) res.getResData();
                for (int i = 0; i < controllers.size(); i++) {
                    result.add(controllerCapabilityconvert.convertSouth2Model(controllers.get(i)));
                }
                sdkResult.setResult(result);
            }
        } catch (ProtocolAdapterException e) {
            sdkResult.setErrCode(e.getErrorCode());
            sdkResult.setDescription(e.getMessage());
            LOGGER.error("getAllController() error");
        }

        LOGGER.debug("getAllController() end");

        return sdkResult;

    }

}
