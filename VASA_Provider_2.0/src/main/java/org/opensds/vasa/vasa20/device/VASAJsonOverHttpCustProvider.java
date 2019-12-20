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

package org.opensds.vasa.vasa20.device;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.opensds.vasa.base.bean.terminal.ResBean;
import org.opensds.platform.common.MessageContext;
import org.opensds.platform.common.ThreadLocalHolder;
import org.opensds.platform.common.bean.aa.AccountInfo;
import org.opensds.platform.common.bean.log.InterfaceLogBean;
import org.opensds.platform.common.constants.ESDKConstant;
import org.opensds.platform.common.constants.ESDKErrorCodeConstant;
import org.opensds.platform.common.utils.ApplicationContextUtil;
import org.opensds.platform.common.utils.OSUtils;
import org.opensds.platform.commu.itf.AbstractProtocolAdatperCustProvider;
import org.opensds.platform.exception.ProtocolAdapterException;
import org.opensds.platform.log.itf.IInterfaceLog;

public class VASAJsonOverHttpCustProvider extends AbstractProtocolAdatperCustProvider {
    private static final Logger LOGGER = LogManager.getLogger(VASAJsonOverHttpCustProvider.class);

    private AccountInfo accountInfo = new AccountInfo();

    @Override
    public Object preProcessReq(Object reqMessage) {
        return reqMessage;
    }

    @Override
    public Map<String, String> getRequestHeaders() {
        Map<String, String> headers = new HashMap<String, String>();
        // Prepare the session ID for adding into HTTP header

        if (null != VASASession.getToken(getAccountInfo().getDevId())) {
            headers.put("iBaseToken", VASASession.getToken(getAccountInfo().getDevId()));
            //LOGGER.warn("request iBaseToken = "+ VASASession.getToken(getAccountInfo().getDevId()));
        }

        headers.put("Content-type", "application/json");

        return headers;
    }

    @Override
    public synchronized String getContent4Sending(Object reqMessage) {
        if (reqMessage instanceof String) {
//    		LOGGER.info("-----------------------------requestBody:" + reqMessage);
            return (String) reqMessage;
        }

        Gson gson = new Gson();
        String reqPayloadInJSON = gson.toJson(reqMessage);
//        LOGGER.info("-----------------------------requestBody:" + reqPayloadInJSON);

        return reqPayloadInJSON;
    }

    @Override
    public Object preSend(Object reqMessage) {
        return reqMessage;
    }

    @Override
    public String reBuildNewUrl(String url, String interfaceName) {
        String newUrl = "";
        url = url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
        interfaceName = interfaceName.startsWith("/") ? interfaceName.substring(1) : interfaceName;
        MessageContext mc = ThreadLocalHolder.get();
        if (!interfaceName.equals("xxxxx/sessions")) {
            newUrl = url + "/deviceManager/rest/" + getAccountInfo().getDevId() + "/" + interfaceName;
        } else {
            newUrl = url + "/deviceManager/rest/" + interfaceName;
        }
        String uuid = UUID.randomUUID().toString();


        if (null != mc) {
            mc.getEntities().put("interfaceLogger-TransactionId", uuid);
            mc.getEntities().put(ESDKConstant.ESDK_USER_ID, getAccountInfo().getUserId());
            mc.getEntities().put(ESDKConstant.ESDK_PLAIN_PWD, getAccountInfo().getPassword());
        }

        String targetIP = url;
        try {
            URI uri = new URI(url);
            targetIP = uri.getHost();
        } catch (URISyntaxException e) {
            LOGGER.warn("uri syntax error, uri is : " + url);
        }

        InterfaceLogBean bean = new InterfaceLogBean();
        bean.setTransactionId(uuid);
        bean.setProduct("VASA");
        bean.setInterfaceType("2");
        bean.setProtocolType("HTTP");
        bean.setReq(true);
        bean.setName(newUrl);
        bean.setSourceAddr(OSUtils.getLocalIP());
        bean.setTargetAddr(targetIP);
        bean.setReqTime(new Date());

        IInterfaceLog logger = ApplicationContextUtil.getBean("interfaceLogger");
        logger.info(bean);

        return newUrl;

    }

    @Override
    public Object postSend(Object resMessage) {
        try {
            HttpResponse response = (HttpResponse) resMessage;
            String sessionId = null;
            Header[] hs = response.getAllHeaders();
            for (Header header : hs) {
                if ("Set-Cookie".equals(header.getName())) {
                    sessionId = header.getValue().split("=")[1];
                    LOGGER.info("response sessionId = " + sessionId);
                    break;
                }
            }
        } catch (Exception e) {

        }

        return resMessage;
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
/*					if(!resJson.has("data")){
						resJson.put("data", "");
					}*/
                    if (resJson.has("data")) {
                        JSONObject dataObj = resJson.getJSONObject("data");
                        resObj = gson.fromJson(dataObj.toString(), Class.forName(resObjClass));
                        resBean.setResData(resObj);
                    }
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

    public List<Object> postBuildResList(Object resMessage, String resObjClass) throws ProtocolAdapterException {
        Object resObj = null;
        Gson gson = new Gson();
        List<Object> list = new ArrayList<>();

        try {
            JSONObject resJson = new JSONObject(String.valueOf(resMessage));
/*			if(!resJson.has("data")){
				resJson.put("data", new ArrayList<>());
			}*/

            if (resJson.has("data")) {
                JSONArray jsonArray = resJson.getJSONArray("data");

                for (int i = 0; i < jsonArray.length(); i++) {
                    Object object = jsonArray.get(i);
                    resObj = gson.fromJson(object.toString(), Class.forName(resObjClass));
                    list.add(resObj);
                }
            }

        } catch (JsonSyntaxException e) {
            LOGGER.error("", e);
            throw new ProtocolAdapterException("postBuildResList error", e, ESDKErrorCodeConstant.ERROR_CODE_SYS_ERROR);
        } catch (JSONException e) {
            LOGGER.error("", e);
            throw new ProtocolAdapterException("postBuildResList error", e, ESDKErrorCodeConstant.ERROR_CODE_SYS_ERROR);
        } catch (ClassNotFoundException e) {
            LOGGER.error("", e);
            throw new ProtocolAdapterException("postBuildResList error", e, ESDKErrorCodeConstant.ERROR_CODE_SYS_ERROR);

        }

        return list;
    }

    public String getDevId() {
        return getAccountInfo().getDevId();
    }

    public AccountInfo getAccountInfo() {
        return accountInfo;
    }

    public void setAccountInfo(AccountInfo accountInfo) {
        this.accountInfo = accountInfo;
    }

}
