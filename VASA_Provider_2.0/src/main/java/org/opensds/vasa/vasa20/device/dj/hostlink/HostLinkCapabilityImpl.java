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

package org.opensds.vasa.vasa20.device.dj.hostlink;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.opensds.vasa.interfaces.device.hostlink.IHostLinkCapability;

import org.opensds.vasa.base.bean.terminal.PassThroughReqBean;
import org.opensds.vasa.base.bean.terminal.ResBean;
import org.opensds.vasa.domain.model.bean.S2DHostLink;

import org.opensds.platform.common.SDKResult;
import org.opensds.platform.common.utils.JsonUtils;
import org.opensds.platform.commu.itf.ISDKProtocolAdapter;
import org.opensds.platform.exception.ProtocolAdapterException;
import org.opensds.vasa.vasa20.device.AbstractVASACapability;
import org.opensds.vasa.vasa20.device.VASARestReqMessage;
import org.opensds.vasa.vasa20.device.dj.bean.SHostLink;
import org.opensds.vasa.vasa20.device.dj.convert.HostLinkCapabilityConvert;

public class HostLinkCapabilityImpl extends AbstractVASACapability implements IHostLinkCapability {
    private static final Logger LOGGER = LogManager.getLogger(HostLinkCapabilityImpl.class);

    private HostLinkCapabilityConvert hostlinkCapabilityconvert = new HostLinkCapabilityConvert();

    public HostLinkCapabilityImpl(ISDKProtocolAdapter protocolAdapter) {
        super(protocolAdapter);
    }

    @Override
    public SDKResult<List<S2DHostLink>> getHostLinkByInitiator(String arrayId,
                                                               String iniType, String portWwn, String nodeWwn) {
        LOGGER.debug("getHostLinkByInitiator() start,the request parameter is : iniType=" + iniType + " portWwn="
                + portWwn + " nodeWwn" + nodeWwn);
        SDKResult<List<S2DHostLink>> sdkResult = new SDKResult<List<S2DHostLink>>();
        List<S2DHostLink> result = new ArrayList<S2DHostLink>();
        VASARestReqMessage req = new VASARestReqMessage();
        req.setHttpMethod("GET");
        req.setMediaType("json");
        req.setArrayId(arrayId);
        PassThroughReqBean reqPayload = new PassThroughReqBean();
        reqPayload.setArray_id(arrayId);
        //reqPayload.setRaw_method("GET");
        String raw_uri = "/host_link?TYPE=255&INITIATOR_TYPE=" + iniType +
                "&INITIATOR_PORT_WWN=" + portWwn + "&INITIATOR_NODE_WWN=" + nodeWwn;
        //reqPayload.setRaw_uri(raw_uri);

        req.setPayload(reqPayload);

        try {
            ResBean res = (ResBean) protocolAdapter.syncSendMessage(req, raw_uri, null);
            //LOGGER.info("getHostLinkByInitiator response is :"+res.toString());
            sdkResult.setErrCode(res.getErrorCode());
            sdkResult.setDescription(res.getDescription());

            if (0 == res.getErrorCode()) {
                JSONObject jsonObj = (JSONObject) (res.getResData());
                if (jsonObj.has("data")) {
                    JSONArray arrObj = jsonObj.getJSONArray("data");
                    for (int i = 0; i < arrObj.length(); ++i) {
                        /**
                         * CodeDEX modified by twx381974 2017/02/15 START
                         * FORTIFY.JSON_Injection
                         */

//	        			 Gson gson = new Gson();
//	        			 SHostLink hostlink = (SHostLink)gson.fromJson(arrObj.getString(i) ,SHostLink.class);
                        Object object = arrObj.get(i);
                        SHostLink hostlink = JsonUtils.fromJson(object.toString(), SHostLink.class);

                        /**
                         * CodeDEX modified by twx381974 2017/02/15 END
                         * FORTIFY.JSON_Injection
                         */

                        result.add(hostlinkCapabilityconvert.convertSouth2Model(hostlink));
                    }
                }
                sdkResult.setResult(result);
            }
        } catch (ProtocolAdapterException e) {
            sdkResult.setErrCode(e.getErrorCode());
            sdkResult.setDescription(e.getMessage());
            LOGGER.error("getHostLinkByInitiator() error");
        } catch (JSONException e) {
            sdkResult.setErrCode(-1);
            LOGGER.error("getHostLinkByInitiator() error", e);
        }

        LOGGER.debug("getHostLinkByInitiator() end");

        return sdkResult;
    }

}
