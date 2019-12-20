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

package org.opensds.vasa.vasa20.device.array.login;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.opensds.vasa.base.bean.terminal.ResBean;
import org.opensds.platform.common.constants.ESDKErrorCodeConstant;
import org.opensds.platform.commu.itf.AbstractProtocolAdatperCustProvider;
import org.opensds.platform.exception.ProtocolAdapterException;

public class DeviceSwitchProvider extends AbstractProtocolAdatperCustProvider {

    private static final Logger LOGGER = LogManager.getLogger(DeviceSwitchProvider.class);

    private DeviceSwitchSession session;

    private final static String DEVICE_REST_PREFIX = "/deviceManager/rest/";

    public DeviceSwitchProvider(DeviceSwitchSession session) {

        this.session = session;
    }

    @Override
    public String getContent4Sending(Object reqMessage) {
        if (reqMessage instanceof String) {
            return (String) reqMessage;
        }

        Gson gson = new Gson();
        String reqPayloadInJSON = gson.toJson(reqMessage);

        return reqPayloadInJSON;
    }

    @Override
    public Map<String, String> getRequestHeaders() {
        Map<String, String> headers = new HashMap<String, String>();

        if (session.getToken() != null) {
            headers.put("iBaseToken", session.getToken());
        }

        headers.put("Content-type", "application/json");

        return headers;
    }

    @Override
    public Object postBuildRes(Object resMessage, String resObjClass) throws ProtocolAdapterException {

        Object resObj = null;
        Gson gson = new Gson();
        ResBean resBean = new ResBean();

        try {
            JSONObject resJson = new JSONObject(String.valueOf(resMessage));

            JSONObject errorObj = resJson.getJSONObject("error");

            if (errorObj.getLong("code") != 0L) {
                resBean.setErrorCode(errorObj.getLong("code"));
                resBean.setDescription(errorObj.getString("description"));
                resBean.setErrData(errorObj);
            } else {
                resBean.setErrorCode(0);
                resBean.setDescription("0");

                if (null != resObjClass) {
                    JSONObject dataObj = resJson.getJSONObject("data");
                    resObj = gson.fromJson(dataObj.toString(), Class.forName(resObjClass));

                    resBean.setResData(resObj);
                } else {
                    resBean.setResData(resJson);
                }
            }
        } catch (JsonSyntaxException e) {
            LOGGER.error("", e);
            throw new ProtocolAdapterException("syncSendMessage error", e, ESDKErrorCodeConstant.ERROR_CODE_SYS_ERROR);
        } catch (JSONException e) {
            LOGGER.error("", e);
            throw new ProtocolAdapterException("syncSendMessage error", e, ESDKErrorCodeConstant.ERROR_CODE_SYS_ERROR);
        } catch (ClassNotFoundException e) {
            LOGGER.error("", e);
            throw new ProtocolAdapterException("syncSendMessage error", e, ESDKErrorCodeConstant.ERROR_CODE_SYS_ERROR);
        }

        return resBean;

    }

    @Override
    public Object postSend(Object obj) throws ProtocolAdapterException {

        return obj;
    }

    @Override
    public Object preProcessReq(Object obj) {

        return obj;
    }

    @Override
    public Object preSend(Object obj) {

        return obj;
    }

    @Override
    public String reBuildNewUrl(String url, String interfaceName) {
        url = url.endsWith("/") ? url.substring(0, url.length() - 1) : url;

        if ("xxxxx/sessions".equals(interfaceName)) {
            return url + DEVICE_REST_PREFIX + interfaceName;
        }

        return url + DEVICE_REST_PREFIX + session.getDeviceId() + "/" + interfaceName;
    }

    public String getDevId() {
        return session.getDeviceId();
    }
}
