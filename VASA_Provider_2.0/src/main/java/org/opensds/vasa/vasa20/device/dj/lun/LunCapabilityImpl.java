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

package org.opensds.vasa.vasa20.device.dj.lun;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.opensds.vasa.interfaces.device.lun.ILunCapability;
import org.opensds.vasa.vasa20.device.dj.bean.SLun;
import org.opensds.vasa.vasa20.device.dj.bean.SLunCopyBean;
import org.opensds.vasa.vasa20.device.dj.convert.LunCapabilityConvert;
import org.opensds.vasa.vasa20.device.dj.convert.LunCopyBeanCapabilityConvert;

import org.opensds.vasa.base.bean.terminal.ResBean;
import org.opensds.vasa.domain.model.bean.S2DLun;
import org.opensds.vasa.domain.model.bean.S2DLunCopyBean;

import org.opensds.platform.common.SDKResult;
import org.opensds.platform.common.utils.JsonUtils;
import org.opensds.platform.commu.itf.ISDKProtocolAdapter;
import org.opensds.platform.exception.ProtocolAdapterException;
import org.opensds.vasa.vasa20.device.AbstractVASACapability;
import org.opensds.vasa.vasa20.device.VASARestReqMessage;

public class LunCapabilityImpl extends AbstractVASACapability implements ILunCapability {
    private static final Logger LOGGER = LogManager.getLogger(LunCapabilityImpl.class);

    private LunCapabilityConvert lunCapabilityconvert = new LunCapabilityConvert();
    private LunCopyBeanCapabilityConvert lunCopyCapabilityconvert = new LunCopyBeanCapabilityConvert();

    private static final int NUM_PER_FETCH = 100;

    public LunCapabilityImpl(ISDKProtocolAdapter protocolAdapter) {
        super(protocolAdapter);
    }

    @Override
    public SDKResult<List<S2DLun>> getLunByHostID(String arrayId, String hostId) {
        SDKResult<List<S2DLun>> sdkResult = new SDKResult<List<S2DLun>>();
        List<S2DLun> result = new ArrayList<S2DLun>();

        VASARestReqMessage req = new VASARestReqMessage();
        req.setHttpMethod("GET");
        req.setMediaType("json");
        req.setPaging(true);
        req.setHasRange(true);
        req.setArrayId(arrayId);
        req.setPageSize(NUM_PER_FETCH);

        try {
            ResBean res = (ResBean) protocolAdapter.syncSendMessage(req, "/lun/associate?TYPE=11&ASSOCIATEOBJTYPE=21&ASSOCIATEOBJID=" + hostId, "org.opensds.vasa.vasa20.device.dj.bean.SLun");
            sdkResult.setErrCode(res.getErrorCode());
            sdkResult.setDescription(res.getDescription());

            if (0 == res.getErrorCode()) {
                @SuppressWarnings("unchecked")
                List<SLun> luns = (List<SLun>) res.getResData();
                for (SLun sLun : luns) {
                    result.add(lunCapabilityconvert.convertSouth2Model(sLun));
                }
                sdkResult.setResult(result);
            }
        } catch (ProtocolAdapterException e) {
            sdkResult.setErrCode(e.getErrorCode());
            sdkResult.setDescription(e.getMessage());
            LOGGER.error("getLunByHostID() error");
        }

        LOGGER.debug("getLunByHostID() end");

        return sdkResult;
    }

    @Override
    public SDKResult<List<S2DLun>> getLunByHostAndPort(String arrayId, String hostId, String metadata) {
        LOGGER.debug("getLunByHostAndPort() start,arrayId=" + arrayId + ",hostId=" + hostId + ",metadata=" + metadata);
        SDKResult<List<S2DLun>> sdkResult = new SDKResult<List<S2DLun>>();
        List<S2DLun> result = new ArrayList<S2DLun>();

        VASARestReqMessage req = new VASARestReqMessage();
        req.setHttpMethod("GET");
        req.setMediaType("json");
        req.setPaging(true);
        req.setArrayId(arrayId);

        try {
            ResBean res = (ResBean) protocolAdapter.syncSendMessage(req, "/lun/associate?TYPE=11&ASSOCIATEOBJTYPE=21&ASSOCIATEOBJID=" + hostId + "&ASSOCIATEMETADATA=" + metadata, "org.opensds.vasa.vasa20.device.dj.bean.SLun");

            sdkResult.setErrCode(res.getErrorCode());
            sdkResult.setDescription(res.getDescription());

            if (0 == res.getErrorCode()) {
                List<SLun> lun = (List<SLun>) res.getResData();
                for (SLun sLun : lun) {
                    result.add(lunCapabilityconvert.convertSouth2Model(sLun));
                }
                sdkResult.setResult(result);
            }
        } catch (ProtocolAdapterException e) {
            sdkResult.setErrCode(e.getErrorCode());
            sdkResult.setDescription(e.getMessage());
            LOGGER.error("getLunByHostAndPort() error");
        }
        LOGGER.debug("getLunByHostAndPort() end");

        return sdkResult;
    }

    @Override
    public SDKResult<List<S2DLun>> getThinLun(String arrayId) {
        LOGGER.debug("getThinLun() start");
        SDKResult<List<S2DLun>> sdkResult = new SDKResult<List<S2DLun>>();
        List<S2DLun> result = new ArrayList<S2DLun>();

        VASARestReqMessage req = new VASARestReqMessage();
        req.setHttpMethod("GET");
        req.setMediaType("json");
        req.setArrayId(arrayId);

        try {
            ResBean res = (ResBean) protocolAdapter.syncSendMessage(req, "lun?filter=ALLOCTYPE::1%20and%20SUBTYPE::0", null);
            sdkResult.setErrCode(res.getErrorCode());
            sdkResult.setDescription(res.getDescription());

            if (0 == res.getErrorCode()) {
/*				@SuppressWarnings("unchecked")
				List<SLun> list = (List<SLun>) res.getResData();
				for(int i=0;i<list.size();i++)
				{
					result.add(lunCapabilityconvert.convertSouth2Model(list.get(i)));
				}
	            */
                JSONObject jsonObj = (JSONObject) (res.getResData());
                if (jsonObj.has("data")) {
                    JSONArray arrObj = jsonObj.getJSONArray("data");
                    for (int i = 0; i < arrObj.length(); ++i) {

                        /**
                         * CodeDEX modified by twx381974 2017/02/15 START
                         * FORTIFY.JSON_Injection
                         */

//		       			 Gson gson = new Gson();
//		       			 SOemInfo oem = (SOemInfo)gson.fromJson(arrObj.getString(i) ,SOemInfo.class);

                        SLun slun = JsonUtils.fromJson(arrObj.get(i).toString(), SLun.class);

                        /**
                         * CodeDEX modified by twx381974 2017/02/15 END
                         * FORTIFY.JSON_Injection
                         */

                        result.add(lunCapabilityconvert.convertSouth2Model(slun));
                    }
                }

                sdkResult.setResult(result);
            }


        } catch (ProtocolAdapterException e) {
            sdkResult.setErrCode(e.getErrorCode());
            sdkResult.setDescription(e.getMessage());
            LOGGER.error("getThinLun() error " + e);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            sdkResult.setErrCode(-1);
            sdkResult.setDescription(e.getMessage());
            LOGGER.error("getThinLun() error " + e);
        }

        LOGGER.debug("getThinLun() end");

        return sdkResult;
    }

    @Override
    public SDKResult<List<S2DLun>> getPELun(String arrayId) {
        LOGGER.debug("getPELun() start");
        SDKResult<List<S2DLun>> sdkResult = new SDKResult<List<S2DLun>>();
        List<S2DLun> result = new ArrayList<S2DLun>();

        VASARestReqMessage req = new VASARestReqMessage();
        req.setHttpMethod("GET");
        req.setMediaType("json");
        req.setArrayId(arrayId);
        req.setPaging(true);
        req.setHasRange(true);
        req.setPageSize(100);
        try {
            // add paging to solve Dorado V3R2C20 query PE timeout problem
            if (req.isPaging()) {
                ResBean res = (ResBean) protocolAdapter.syncSendMessage(req, "lun?filter=USAGETYPE::3", "org.opensds.vasa.vasa20.device.dj.bean.SLun");

                sdkResult.setErrCode(res.getErrorCode());
                sdkResult.setDescription(res.getDescription());
                if (0 != res.getErrorCode()) {
                    sdkResult.setErrCode(res.getErrorCode());
                    sdkResult.setDescription(res.getDescription());
                    return sdkResult;
                }
                List<SLun> sluns = (List<SLun>) res.getResData();
                result.addAll(lunCapabilityconvert.convertSouth2Model(sluns));
                sdkResult.setResult(result);
            } else {
                ResBean res = (ResBean) protocolAdapter.syncSendMessage(req, "lun?filter=USAGETYPE::3", null);

                if (0 != res.getErrorCode()) {
                    sdkResult.setErrCode(res.getErrorCode());
                    sdkResult.setDescription(res.getDescription());
                    return sdkResult;
                }

                JSONObject jsonObj = (JSONObject) (res.getResData());
                if (jsonObj.has("data")) {
                    JSONArray arrObj = jsonObj.getJSONArray("data");
                    for (int i = 0; i < arrObj.length(); ++i) {
                        SLun sLun = JsonUtils.fromJson(arrObj.get(i).toString(), SLun.class);
                        result.add(lunCapabilityconvert.convertSouth2Model(sLun));
                    }
                }
                sdkResult.setResult(result);
            }
            return sdkResult;

        } catch (ProtocolAdapterException e) {
            sdkResult.setErrCode(e.getErrorCode());
            sdkResult.setDescription(e.getMessage());
            LOGGER.error("getPELun() error");
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            sdkResult.setErrCode(-1);
            sdkResult.setDescription("getPELun() error");
            LOGGER.error("getPELun() error", e);
        }

        LOGGER.debug("getPELun() end");

        return sdkResult;
    }

    @Override
    public SDKResult<S2DLun> getLun(String arrayId, String lunId) {
        LOGGER.debug("getLun() start");
        SDKResult<S2DLun> sdkResult = new SDKResult<S2DLun>();

        VASARestReqMessage req = new VASARestReqMessage();
        req.setHttpMethod("GET");
        req.setMediaType("json");
        req.setArrayId(arrayId);

        try {
            ResBean res = (ResBean) protocolAdapter.syncSendMessage(req, "lun/" + lunId, "org.opensds.vasa.vasa20.device.dj.bean.SLun");

            sdkResult.setErrCode(res.getErrorCode());
            sdkResult.setDescription(res.getDescription());

            if (0 != res.getErrorCode()) {
                return sdkResult;
            }

            SLun lun = (SLun) res.getResData();
            sdkResult.setResult(lunCapabilityconvert.convertSouth2Model(lun));

            return sdkResult;
        } catch (ProtocolAdapterException e) {
            sdkResult.setErrCode(e.getErrorCode());
            sdkResult.setDescription(e.getMessage());
            LOGGER.error("getLun() error");
        }
        LOGGER.debug("getLun() end");

        return sdkResult;
    }

    @Override
    public SDKResult<S2DLunCopyBean> getLunCopy(String arrayId, String lunId) {
        LOGGER.info("getLunCopy() start");
        SDKResult<S2DLunCopyBean> sdkResult = new SDKResult<S2DLunCopyBean>();

        VASARestReqMessage req = new VASARestReqMessage();
        req.setHttpMethod("GET");
        req.setMediaType("json");
        req.setArrayId(arrayId);
        try {
            ResBean res = (ResBean) protocolAdapter.syncSendMessage(req, "/lun/" + lunId, "org.opensds.vasa.vasa20.device.dj.bean.SLunCopyBean");

            sdkResult.setErrCode(res.getErrorCode());
            sdkResult.setDescription(res.getDescription());

            if (0 == res.getErrorCode()) {
                SLunCopyBean sLunCopyBean = (SLunCopyBean) res.getResData();
                sdkResult.setResult(lunCopyCapabilityconvert.convertSouth2Model(sLunCopyBean));
            }
        } catch (ProtocolAdapterException e) {
            sdkResult.setErrCode(e.getErrorCode());
            sdkResult.setDescription(e.getMessage());
            LOGGER.error("getLunCopy() error");
        }
        LOGGER.info("getLunCopy() end");

        return sdkResult;
    }
}
