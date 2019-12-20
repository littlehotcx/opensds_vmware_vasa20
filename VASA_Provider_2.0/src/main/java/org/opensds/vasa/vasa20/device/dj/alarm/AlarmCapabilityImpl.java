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

package org.opensds.vasa.vasa20.device.dj.alarm;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.opensds.vasa.interfaces.device.alarm.IAlarmCapability;
import org.opensds.vasa.vasa20.device.dj.convert.AlarmCapabilityConvert;

import org.opensds.vasa.base.bean.terminal.ResBean;
import org.opensds.vasa.domain.model.bean.S2DAlarm;

import org.opensds.platform.common.SDKResult;
import org.opensds.platform.common.utils.JsonUtils;
import org.opensds.platform.commu.itf.ISDKProtocolAdapter;
import org.opensds.platform.exception.ProtocolAdapterException;
import org.opensds.vasa.vasa20.device.AbstractVASACapability;
import org.opensds.vasa.vasa20.device.VASARestReqMessage;
import org.opensds.vasa.vasa20.device.dj.bean.SAlarm;

public class AlarmCapabilityImpl extends AbstractVASACapability implements IAlarmCapability {
    private static final Logger LOGGER = LogManager.getLogger(AlarmCapabilityImpl.class);

    private AlarmCapabilityConvert alarmCapabilityconvert = new AlarmCapabilityConvert();

    public AlarmCapabilityImpl(ISDKProtocolAdapter protocolAdapter) {
        super(protocolAdapter);
    }

    @Override
    public SDKResult<List<S2DAlarm>> getAllEvent(String arrayid, String start, String end) {
        LOGGER.debug("getAllEvent() start");
        SDKResult<List<S2DAlarm>> sdkResult = new SDKResult<List<S2DAlarm>>();
        List<S2DAlarm> result = new ArrayList<S2DAlarm>();

        VASARestReqMessage req = new VASARestReqMessage();
        req.setHttpMethod("GET");
        req.setMediaType("json");
        req.setArrayId(arrayid);
        req.setHasRange(false);
        req.setPaging(true);

        try {
            ResBean res = (ResBean) protocolAdapter.syncSendMessage(req,
                    "/alarm/historyalarm?range=[" + start + "-" + end + "]",
                    "org.opensds.vasa.vasa20.device.dj.bean.SAlarm");

            sdkResult.setErrCode(res.getErrorCode());
            sdkResult.setDescription(res.getDescription());

            if (0 == res.getErrorCode()) {
                @SuppressWarnings("unchecked")
                List<SAlarm> list = (List<SAlarm>) res.getResData();
                for (int i = 0; i < list.size(); i++) {
                    result.add(alarmCapabilityconvert.convertSouth2Model(list.get(i)));
                }
                sdkResult.setResult(result);
            }
        } catch (ProtocolAdapterException e) {
            sdkResult.setErrCode(e.getErrorCode());
            sdkResult.setDescription(e.getMessage());
            LOGGER.error("getAllEvent() error", e);
        }

        LOGGER.debug("getAllEvent() end");

        return sdkResult;

    }

    @Override
    public SDKResult<List<S2DAlarm>> getAllAlarm(String arrayid, String start, String end) {
        LOGGER.debug("getAllAlarm() start");
        SDKResult<List<S2DAlarm>> sdkResult = new SDKResult<List<S2DAlarm>>();
        List<S2DAlarm> result = new ArrayList<S2DAlarm>();

        VASARestReqMessage req = new VASARestReqMessage();
        req.setHttpMethod("GET");
        req.setMediaType("json");
        req.setArrayId(arrayid);

        try {
            ResBean res = (ResBean) protocolAdapter.syncSendMessage(req,
                    "/alarm/currentalarm?range=[" + start + "-" + end + "]", null);

            sdkResult.setErrCode(res.getErrorCode());
            sdkResult.setDescription(res.getDescription());

            if (0 == res.getErrorCode()) {
                /*
                 * @SuppressWarnings("unchecked") List<SAlarm> list =
                 * (List<SAlarm>) res.getResData(); for(int
                 * i=0;i<list.size();i++){
                 * result.add(alarmCapabilityconvert.convertSouth2Model(list.get
                 * (i))); } sdkResult.setResult(result);
                 */

                JSONObject jsonObj = (JSONObject) (res.getResData());
                if (jsonObj.has("data")) {
                    JSONArray arrObj = jsonObj.getJSONArray("data");
                    for (int i = 0; i < arrObj.length(); ++i) {

                        /**
                         * CodeDEX modified by twx381974 2017/02/15 START
                         * FORTIFY.JSON_Injection
                         */

                        // Gson gson = new Gson();
                        // SOemInfo oem =
                        // (SOemInfo)gson.fromJson(arrObj.getString(i)
                        // ,SOemInfo.class);
                        Object object = arrObj.get(i);
                        SAlarm alarm = JsonUtils.fromJson(object.toString(), SAlarm.class);

                        /**
                         * CodeDEX modified by twx381974 2017/02/15 END
                         * FORTIFY.JSON_Injection
                         */

                        result.add(alarmCapabilityconvert.convertSouth2Model(alarm));
                    }
                }
                sdkResult.setResult(result);

            }
        } catch (ProtocolAdapterException e) {
            sdkResult.setErrCode(e.getErrorCode());
            sdkResult.setDescription(e.getMessage());
            LOGGER.error("getAllAlarm() error", e);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            sdkResult.setErrCode(-1);
            sdkResult.setDescription("getRecentEvent() error");
            LOGGER.error("getAllAlarm() error", e);
        }

        LOGGER.debug("getAllAlarm() end");

        return sdkResult;
    }

    @Override
    public SDKResult<List<S2DAlarm>> getRecentEvent(String arrayid, String start, String end) {
        LOGGER.debug("getRecentEvent() start");
        SDKResult<List<S2DAlarm>> sdkResult = new SDKResult<List<S2DAlarm>>();
        List<S2DAlarm> result = new ArrayList<S2DAlarm>();

        VASARestReqMessage req = new VASARestReqMessage();
        req.setHttpMethod("GET");
        req.setMediaType("json");
        req.setArrayId(arrayid);
        try {
            ResBean res = (ResBean) protocolAdapter.syncSendMessage(req,
                    "/alarm/historyalarm?sortby=startTime,d&range=[" + start + "-" + end + "]", null);
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

                        // Gson gson = new Gson();
                        // SOemInfo oem =
                        // (SOemInfo)gson.fromJson(arrObj.getString(i)
                        // ,SOemInfo.class);
                        Object object = arrObj.get(i);
                        SAlarm alarm = JsonUtils.fromJson(object.toString(), SAlarm.class);

                        /**
                         * CodeDEX modified by twx381974 2017/02/15 END
                         * FORTIFY.JSON_Injection
                         */

                        result.add(alarmCapabilityconvert.convertSouth2Model(alarm));
                    }
                }
                sdkResult.setResult(result);
            }
        } catch (ProtocolAdapterException e) {
            sdkResult.setErrCode(e.getErrorCode());
            sdkResult.setDescription(e.getMessage());
            LOGGER.error("getRecentEvent() error", e);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            sdkResult.setErrCode(-1);
            sdkResult.setDescription("getRecentEvent() error");
            LOGGER.error("getRecentEvent() error", e);
        }
        LOGGER.debug("getRecentEvent() end");

        return sdkResult;
    }

}
