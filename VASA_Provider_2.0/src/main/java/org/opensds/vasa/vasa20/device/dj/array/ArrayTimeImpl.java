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

package org.opensds.vasa.vasa20.device.dj.array;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.opensds.vasa.interfaces.device.array.IArrayTime;

import org.opensds.vasa.base.bean.terminal.ResBean;

import org.opensds.platform.common.SDKResult;
import org.opensds.platform.commu.itf.ISDKProtocolAdapter;
import org.opensds.platform.exception.ProtocolAdapterException;
import org.opensds.vasa.vasa20.device.AbstractVASACapability;
import org.opensds.vasa.vasa20.device.VASARestReqMessage;

public class ArrayTimeImpl extends AbstractVASACapability implements IArrayTime {


    private static final Logger LOGGER = LogManager.getLogger(ArrayTimeImpl.class);

    public ArrayTimeImpl(ISDKProtocolAdapter protocolAdapter) {
        super(protocolAdapter);
    }

    @Override
    public SDKResult<String> getArrayUTCTime(String arrayId) {
        // TODO Auto-generated method stub

        LOGGER.debug("getArrayUTCTime() start");
        SDKResult<String> sdkResult = new SDKResult<String>();

        VASARestReqMessage req = new VASARestReqMessage();
        req.setArrayId(arrayId);
        req.setHttpMethod("GET");
        req.setMediaType("json");

        try {
            ResBean res = (ResBean) protocolAdapter.syncSendMessage(req, "/system_utc_time", null);

            sdkResult.setErrCode(res.getErrorCode());
            sdkResult.setDescription(res.getDescription());

            if (0 == res.getErrorCode()) {
                JSONObject jsonObj = (JSONObject) (res.getResData());
                String utc_time = jsonObj.getString("CMO_SYS_UTC_TIME");
                LOGGER.info("getArrayUTCTime arrayId=" + arrayId + ",utc_time=" + utc_time);
                sdkResult.setResult(utc_time);
            }
        } catch (ProtocolAdapterException e) {
            sdkResult.setErrCode(e.getErrorCode());
            sdkResult.setDescription(e.getMessage());
            LOGGER.error("getArrayUTCTime() error", e);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            sdkResult.setErrCode(-1);
            sdkResult.setDescription("getArrayUTCTime() error");
            LOGGER.error("getArrayUTCTime() error", e);
        }
        LOGGER.debug("getArrayUTCTime() end");

        return sdkResult;
    }

}
