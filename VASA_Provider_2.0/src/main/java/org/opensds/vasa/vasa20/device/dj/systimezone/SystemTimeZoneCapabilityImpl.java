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

package org.opensds.vasa.vasa20.device.dj.systimezone;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.opensds.vasa.interfaces.device.systimezone.ISystemTimeZoneCapability;

import org.opensds.vasa.base.bean.terminal.ResBean;
import org.opensds.vasa.domain.model.bean.S2DSystemTimeZone;

import org.opensds.platform.common.SDKResult;
import org.opensds.platform.common.utils.JsonUtils;
import org.opensds.platform.commu.itf.ISDKProtocolAdapter;
import org.opensds.platform.exception.ProtocolAdapterException;
import org.opensds.vasa.vasa20.device.AbstractVASACapability;
import org.opensds.vasa.vasa20.device.VASARestReqMessage;
import org.opensds.vasa.vasa20.device.dj.bean.SSystemTimeZone;
import org.opensds.vasa.vasa20.device.dj.convert.SystemTimeZoneCapabilityConvert;

public class SystemTimeZoneCapabilityImpl extends AbstractVASACapability implements ISystemTimeZoneCapability {
    private static final Logger LOGGER = LogManager.getLogger(SystemTimeZoneCapabilityImpl.class);

    private SystemTimeZoneCapabilityConvert systimezoneCapabilityconvert = new SystemTimeZoneCapabilityConvert();

    public SystemTimeZoneCapabilityImpl(ISDKProtocolAdapter protocolAdapter) {
        super(protocolAdapter);
    }

    @Override
    public SDKResult<List<S2DSystemTimeZone>> queryDeviceTimeZone(
            String arrayId) {
        LOGGER.debug("queryDeviceTimeZone() start");
        SDKResult<List<S2DSystemTimeZone>> sdkResult = new SDKResult<List<S2DSystemTimeZone>>();
        List<S2DSystemTimeZone> result = new ArrayList<S2DSystemTimeZone>();
        VASARestReqMessage req = new VASARestReqMessage();
        req.setHttpMethod("GET");
        req.setMediaType("json");
        req.setArrayId(arrayId);

        try {
            ResBean res = (ResBean) protocolAdapter.syncSendMessage(req, "/system_timezone", null);

            sdkResult.setErrCode(res.getErrorCode());
            sdkResult.setDescription(res.getDescription());

            if (0 == res.getErrorCode()) {
	        	 /*SSystemTimeZone timezone = (SSystemTimeZone)(res.getResData());
	        	 result.add(systimezoneCapabilityconvert.convertSouth2Model(timezone));
	        	 sdkResult.setResult(result);*/

                JSONObject jsonObj = (JSONObject) (res.getResData());
                if (jsonObj.has("data")) {
                    JSONArray arrObj = jsonObj.getJSONArray("data");
                    for (int i = 0; i < arrObj.length(); ++i) {

                        /**
                         * CodeDEX modified by twx381974 2017/02/15 START
                         * FORTIFY.JSON_Injection
                         */

//	        			 Gson gson = new Gson();
//	        			 SOemInfo oem = (SOemInfo)gson.fromJson(arrObj.getString(i) ,SOemInfo.class);

                        SSystemTimeZone systemTimeZone = JsonUtils.fromJson(arrObj.get(i).toString(), SSystemTimeZone.class);

                        /**
                         * CodeDEX modified by twx381974 2017/02/15 END
                         * FORTIFY.JSON_Injection
                         */

                        result.add(systimezoneCapabilityconvert.convertSouth2Model(systemTimeZone));
                    }
                }
                sdkResult.setResult(result);
            }

        } catch (ProtocolAdapterException e) {
            sdkResult.setErrCode(e.getErrorCode());
            sdkResult.setDescription(e.getMessage());
            LOGGER.error("queryDeviceTimeZone() error", e);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            sdkResult.setErrCode(-1);
            LOGGER.error("queryDeviceTimeZone() error", e);
        }
        LOGGER.debug("queryDeviceTimeZone() end");

        return sdkResult;
    }

}
