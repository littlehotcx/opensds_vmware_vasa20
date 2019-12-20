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

package org.opensds.vasa.vasa20.device.dj.vvolbind;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.opensds.vasa.interfaces.device.vvolbind.IVvolBindCapability;

import org.opensds.vasa.base.bean.terminal.ResBean;
import org.opensds.vasa.domain.model.bean.S2DVvolBind;

import org.opensds.platform.common.SDKErrorCode;
import org.opensds.platform.common.SDKResult;
import org.opensds.platform.common.utils.JsonUtils;
import org.opensds.platform.commu.itf.ISDKProtocolAdapter;
import org.opensds.platform.exception.ProtocolAdapterException;
import org.opensds.vasa.vasa20.device.AbstractVASACapability;
import org.opensds.vasa.vasa20.device.VASARestReqMessage;
import org.opensds.vasa.vasa20.device.dj.bean.SVvolBind;
import org.opensds.vasa.vasa20.device.dj.convert.VvolBindCapabilityConvert;

public class VvolBindCapabilityImpl extends AbstractVASACapability implements IVvolBindCapability {
    private static final Logger LOGGER = LogManager.getLogger(VvolBindCapabilityImpl.class);
    private VvolBindCapabilityConvert vvolbindCapabilityconvert = new VvolBindCapabilityConvert();

    public VvolBindCapabilityImpl(ISDKProtocolAdapter protocolAdapter) {
        super(protocolAdapter);
    }

    @Override
    public SDKResult<S2DVvolBind> bind(String arrayId, String hostId, String vvolId, int bindType) {
        LOGGER.debug("bind() start");
        SDKResult<S2DVvolBind> sdkResult = new SDKResult<S2DVvolBind>();

        VASARestReqMessage req = new VASARestReqMessage();
        req.setHttpMethod("POST");
        req.setMediaType("json");
        req.setArrayId(arrayId);

        SVvolBind vvolbind = new SVvolBind();
        vvolbind.setHOSTID(hostId);
        vvolbind.setVVOLID(vvolId);
        vvolbind.setBINDTYPE(bindType);

        req.setPayload(vvolbind);

        try {
            ResBean res = (ResBean) protocolAdapter.syncSendMessage(req, "/vvol_binding",
                    "org.opensds.vasa.vasa20.device.dj.bean.SVvolBind");

            sdkResult.setErrCode(res.getErrorCode());
            sdkResult.setDescription(res.getDescription());

            if (0 == res.getErrorCode()) {
                SVvolBind sVvolBind = (SVvolBind) (res.getResData());

                sdkResult.setResult(vvolbindCapabilityconvert.convertSouth2Model(sVvolBind));
            }
        } catch (ProtocolAdapterException e) {
            sdkResult.setErrCode(e.getErrorCode());
            sdkResult.setDescription(e.getMessage());
            LOGGER.error("bind() error", e);
        }

        //These code should be removed when the storage array fixed it's bug.
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            LOGGER.error("Current Thread was interrupted. Thread name is: " + Thread.currentThread().getName());
        }
        LOGGER.debug("bind() end");
        return sdkResult;
    }

    @Override
    public SDKErrorCode unbindVvolFromAllHost(String arrayId, String vvolId) {
        LOGGER.debug("unbindVvolFromAllHost() start");
        SDKErrorCode sdkErrorCode = new SDKErrorCode();

        VASARestReqMessage req = new VASARestReqMessage();
        req.setHttpMethod("DELETE");
        req.setMediaType("json");
        req.setArrayId(arrayId);
/*		SVvolBind vvolbind = new SVvolBind();
		vvolbind.setVVOLID(vvolId);
		req.setPayload(vvolbind);*/
        Map<String, String> parameter = new HashMap<>();
        parameter.put("VVOLID", vvolId);
        req.setParameters(parameter);

        try {
            ResBean res = (ResBean) protocolAdapter.syncSendMessage(req, "/vvol_binding",
                    "org.opensds.vasa.vasa20.device.dj.bean.SVvolBind");
            sdkErrorCode.setErrCode(res.getErrorCode());
            sdkErrorCode.setDescription(res.getDescription());
        } catch (ProtocolAdapterException e) {
            sdkErrorCode.setErrCode(e.getErrorCode());
            sdkErrorCode.setDescription(e.getMessage());
            LOGGER.error("unbindVvolFromAllHost() error", e);
        }
        LOGGER.debug("unbindVvolFromAllHost() end");

        return sdkErrorCode;
    }

    @Override
    public SDKErrorCode unbindAllVvolFromHost(String arrayId, String hostId) {
        LOGGER.debug("unbindAllVvolFromHost() start");
        SDKErrorCode sdkErrorCode = new SDKErrorCode();

        VASARestReqMessage req = new VASARestReqMessage();
        req.setHttpMethod("DELETE");
        req.setMediaType("json");
        req.setArrayId(arrayId);

		/*SVvolBind vvolbind = new SVvolBind();
		vvolbind.setHOSTID(hostId);
		req.setPayload(vvolbind);*/

        Map<String, String> parameter = new HashMap<>();
        parameter.put("HOSTID", hostId);
        req.setParameters(parameter);

        try {
            ResBean res = (ResBean) protocolAdapter.syncSendMessage(req, "/vvol_binding",
                    "org.opensds.vasa.vasa20.device.dj.bean.SVvolBind");

            sdkErrorCode.setErrCode(res.getErrorCode());
            sdkErrorCode.setDescription(res.getDescription());
        } catch (ProtocolAdapterException e) {
            sdkErrorCode.setErrCode(e.getErrorCode());
            sdkErrorCode.setDescription(e.getMessage());
            LOGGER.error("unbindAllVvolFromHost() error", e);
        }
        LOGGER.debug("unbindAllVvolFromHost() end");

        return sdkErrorCode;
    }

    @Override
    public SDKErrorCode unbindVvolFromPELun(String arrayId, String vvolSecondaryId, String PELunId, int bindType) {
        LOGGER.debug("unbindVvolFromPELun() start");
        SDKErrorCode sdkErrorCode = new SDKErrorCode();

        VASARestReqMessage req = new VASARestReqMessage();
        req.setHttpMethod("DELETE");
        req.setMediaType("json");
        req.setArrayId(arrayId);

/*		SVvolBind vvolbind = new SVvolBind();
		vvolbind.setPELUNID(PELunId);
		vvolbind.setVVOLSECONDARYID(vvolSecondaryId);
		vvolbind.setBINDTYPE(bindType);
		req.setPayload(vvolbind);*/

        Map<String, String> parameters = new HashMap<>();
        parameters.put("PELUNID", PELunId);
        parameters.put("VVOLSECONDARYID", vvolSecondaryId);
        parameters.put("BINDTYPE", String.valueOf(bindType));
        req.setParameters(parameters);

        try {
            ResBean res = (ResBean) protocolAdapter.syncSendMessage(req, "/vvol_binding",
                    "org.opensds.vasa.vasa20.device.dj.bean.SVvolBind");

            sdkErrorCode.setErrCode(res.getErrorCode());
            sdkErrorCode.setDescription(res.getDescription());
        } catch (ProtocolAdapterException e) {
            sdkErrorCode.setErrCode(e.getErrorCode());
            sdkErrorCode.setDescription(e.getMessage());
            LOGGER.error("unbindVvolFromPELun() error", e);
        }
        LOGGER.debug("unbindVvolFromPELun() end");

        return sdkErrorCode;
    }

    @Override
    public SDKErrorCode unbindVvolFromPELunAndHost(String arrayId, String hostId, String vvolSecondaryId,
                                                   String PELunId, int bindType) {
        LOGGER.debug("unbindVvolFromPELunAndHost() start");
        SDKErrorCode sdkErrorCode = new SDKErrorCode();

        VASARestReqMessage req = new VASARestReqMessage();
        req.setHttpMethod("DELETE");
        req.setMediaType("json");
        req.setArrayId(arrayId);
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("HOSTID", hostId);
        parameters.put("PELUNID", PELunId);
        parameters.put("VVOLSECONDARYID", vvolSecondaryId);
        //parameters.put("BINDTYPE", bindType);
		/*SVvolBind vvolbind = new SVvolBind();
		vvolbind.setHOSTID(hostId);
		vvolbind.setPELUNID(PELunId);
		vvolbind.setVVOLSECONDARYID(vvolSecondaryId);
		vvolbind.setBINDTYPE(bindType);
		LOGGER.debug("unbind " + vvolbind);
		req.setPayload(vvolbind);*/
        req.setParameters(parameters);
        try {
            ResBean res = (ResBean) protocolAdapter.syncSendMessage(req, "/vvol_binding",
                    "org.opensds.vasa.vasa20.device.dj.bean.SVvolBind");

            sdkErrorCode.setErrCode(res.getErrorCode());
            sdkErrorCode.setDescription(res.getDescription());
        } catch (ProtocolAdapterException e) {
            sdkErrorCode.setErrCode(e.getErrorCode());
            sdkErrorCode.setDescription(e.getMessage());
            LOGGER.error("unbindVvolFromPELunAndHost() error", e);
        }
        LOGGER.debug("unbindVvolFromPELunAndHost() end");

        return sdkErrorCode;
    }

    @Override
    public SDKResult<List<S2DVvolBind>> getVVOLBind(String arrayId, String vvolId) {
        SDKResult<List<S2DVvolBind>> sdkResult = new SDKResult<List<S2DVvolBind>>();
        List<S2DVvolBind> allBindInfo = new ArrayList<S2DVvolBind>();
        try {
            VASARestReqMessage req = new VASARestReqMessage();
            req.setHttpMethod("GET");
            req.setMediaType("json");
            req.setArrayId(arrayId);
            long currStart = 0l;
            while (true) {
                long end = currStart + 100l;
                String range = "range=[" + currStart + "-" + end + "]";

                /* 发送查询条件，并处理结果 */
                ResBean res = (ResBean) protocolAdapter.syncSendMessage(req, "/vvol_binding?filter=VVOLID::" + vvolId + "&" + range,
                        null);

                if (0 != res.getErrorCode()) {
                    sdkResult.setErrCode(res.getErrorCode());
                    sdkResult.setDescription(res.getDescription());
                    return sdkResult;
                }

                JSONObject jsonObj = (JSONObject) (res.getResData());
                if (!jsonObj.has("data")) {
                    sdkResult.setErrCode(res.getErrorCode());
                    sdkResult.setDescription(res.getDescription());
                    sdkResult.setResult(allBindInfo);
                    return sdkResult;
                }

                JSONArray arrObj = jsonObj.getJSONArray("data");
                for (int i = 0; i < arrObj.length(); i++) {
                    SVvolBind bindInfo = JsonUtils.fromJson(arrObj.get(i).toString(), SVvolBind.class);
                    allBindInfo.add(vvolbindCapabilityconvert.convertSouth2Model(bindInfo));
                }
                if (arrObj.length() < 100) {
                    sdkResult.setErrCode(res.getErrorCode());
                    sdkResult.setDescription(res.getDescription());
                    sdkResult.setResult(allBindInfo);
                    return sdkResult;
                } else {
                    currStart = currStart + 100l;
                }

//				@SuppressWarnings("unchecked")
//				List<SVvolBind> sVvolBinds = (List<SVvolBind>) res.getResData();
//				
//				for (SVvolBind sVvolBind : sVvolBinds) {
//					allBindInfo.add(vvolbindCapabilityconvert.convertSouth2Model(sVvolBind));
//				}
//				
//				sdkResult.setResult(allBindInfo);
            }

        } catch (ProtocolAdapterException e) {
            sdkResult.setErrCode(e.getErrorCode());
            sdkResult.setDescription(e.getMessage());
            LOGGER.error("getVVOLBind() error", e);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            sdkResult.setErrCode(-1);
            sdkResult.setDescription(e.getMessage());
            LOGGER.error("getVVOLBind() error", e);
        }
        return sdkResult;
    }

}
