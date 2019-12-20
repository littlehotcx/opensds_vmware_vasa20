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

package org.opensds.vasa.vasa20.device.array.logicalPort;

import org.opensds.vasa.base.bean.terminal.ResBean;
import org.opensds.platform.common.SDKResult;
import org.opensds.platform.common.utils.JsonUtils;
import org.opensds.platform.commu.itf.ISDKProtocolAdapter;
import org.opensds.platform.exception.ProtocolAdapterException;
import org.opensds.vasa.vasa20.device.AbstractVASACapability;
import org.opensds.vasa.vasa20.device.VASARestReqMessage;

import org.apache.logging.log4j.LogManager;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * 功能描述
 *
 * @author h00451513
 * @since 2019-03-02
 */
public class LogicPortService extends AbstractVASACapability implements IlogicPortService {


    public LogicPortService(ISDKProtocolAdapter protocolAdapter) {
        super(protocolAdapter);
    }

    private static org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger(LogicPortService.class);

    private String logicPort_url = "/lif?filter=IPV4ADDR:";

    /*
     * @Description
     * @Param range is the number of logicPort ,filter is IPV4 or IPV6
     * @return
     * @date 2019/3/2
     * @author h00451513
     *
     */
    @Override
    public SDKResult<List<LogicPortQueryResBean>> queryAllLogicPort(String arrayId, String vStoreId, String range, String filter) {
        LOGGER.debug("query logicPort");
        VASARestReqMessage req = new VASARestReqMessage();

        req.setHttpMethod("GET");
        req.setMediaType("json");
        req.setArrayId(arrayId);

        LogicPortQueryReqBean reqPayload = new LogicPortQueryReqBean();
        SDKResult<List<LogicPortQueryResBean>> sdkResult = new SDKResult<List<LogicPortQueryResBean>>();
        List<LogicPortQueryResBean> result = new ArrayList<LogicPortQueryResBean>();

        createLogicPortReqBean(vStoreId, range, filter, reqPayload);
        req.setPayload(reqPayload);

        try {
            ResBean response = (ResBean) protocolAdapter.syncSendMessage(req, logicPort_url, null);

            if (response.getErrorCode() == 0) {
                LOGGER.debug("query logicPort from array success");
            } else {
                LOGGER.error("query logicPort from array failed:" + sdkResult.getErrCode());
                return sdkResult;
            }

            sdkResult.setErrCode(response.getErrorCode());
            sdkResult.setDescription(response.getDescription());

            // LOGGER.error("error code:"+sdkResult.getErrCode());


            JSONObject jsonObject = (JSONObject) response.getResData();
            if (jsonObject.has("data")) {
                JSONArray array = jsonObject.getJSONArray("data");
                for (int i = 0; i < array.length(); ++i) {
                    LogicPortQueryResBean logicPortQueryResBean = JsonUtils.fromJson(array.get(i).toString(), LogicPortQueryResBean.class);
                    result.add(logicPortQueryResBean);
                }
            }
            sdkResult.setResult(result);
        } catch (ProtocolAdapterException e) {
            LOGGER.error("query logicPort failed " + e);
            sdkResult.setErrCode(404);
            sdkResult.setDescription(e.toString());
            return sdkResult;
        }
        return sdkResult;
    }

    private void createLogicPortReqBean(String vStoreId, String range, String filter, LogicPortQueryReqBean reqPayload) {

        reqPayload.setvStoreId(vStoreId);
        reqPayload.setFilter(filter);
        reqPayload.setRange(range);
    }

    @Override
    public void queryLogicPortCount() {

    }
}
