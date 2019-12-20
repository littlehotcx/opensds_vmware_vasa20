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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.opensds.vasa.base.bean.terminal.ResBean;
import org.opensds.platform.common.bean.commu.RestReqMessage;
import org.opensds.platform.commu.itf.ISDKProtocolAdapter;
import org.opensds.platform.exception.ProtocolAdapterException;
import org.opensds.vasa.vasa20.device.array.bean.DeviceUserAuthReqBean;
import org.opensds.vasa.vasa20.device.array.bean.DeviceUserAuthResBean;

public class DeviceSwitchUtil {

    private static final Logger LOGGER = LogManager.getLogger(DeviceSwitchUtil.class);

    private final static String LOGIN_URL = "xxxxx/sessions";

    private final static String LOGOUT_URL = "sessions";

    private ISDKProtocolAdapter httpClientAdapter;

    private String sap;

    private DeviceSwitchSession session = new DeviceSwitchSession();

    public DeviceSwitchUtil(String sap) {
        this.sap = sap;

        this.httpClientAdapter = new DeviceSwitchHttpClient(sap);

        this.httpClientAdapter.setSdkProtocolAdatperCustProvider(new DeviceSwitchProvider(session));

    }

    public boolean loginDevice(String uname, String upass) {
        RestReqMessage req = new RestReqMessage();
        req.setHttpMethod("POST");
        req.setMediaType("json");

        DeviceUserAuthReqBean reqPayload = new DeviceUserAuthReqBean();
        reqPayload.setUsername(uname);
        reqPayload.setPassword(upass);
        reqPayload.setScope("0");

        req.setPayload(reqPayload);

        try {
            ResBean response = (ResBean) httpClientAdapter.syncSendMessage(req, LOGIN_URL,
                    "org.opensds.vasa.vasa20.device.array.bean.DeviceUserAuthResBean");

            if (response.getErrorCode() != 0) {
                LOGGER.info("login device manager fail. message=" + response.getDescription());
                return false;
            }
            LOGGER.debug("loginDevice response=" + response);
            DeviceUserAuthResBean res = (DeviceUserAuthResBean) response.getResData();

            session.setDeviceId(res.getDeviceid());
            session.setToken(res.getiBaseToken());

            return true;
        } catch (Exception e) {
            LOGGER.info("login device manager fail " + sap + ", err :", e);
        }

        return false;
    }

    public boolean logoutDevice() {
        RestReqMessage req = new RestReqMessage();
        req.setHttpMethod("DELETE");
        req.setMediaType("json");

        try {
            ResBean response = (ResBean) httpClientAdapter.syncSendMessage(req, LOGOUT_URL,
                    "org.opensds.vasa.vasa20.device.array.bean.DeviceUserAuthResBean");

            return true;
        } catch (ProtocolAdapterException e) {
            LOGGER.info("logout device manager fail , " + sap + ", err :" + e.getMessage());
        }

        return false;
    }

}
