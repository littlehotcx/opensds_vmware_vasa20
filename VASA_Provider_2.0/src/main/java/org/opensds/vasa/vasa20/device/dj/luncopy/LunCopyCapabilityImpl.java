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

package org.opensds.vasa.vasa20.device.dj.luncopy;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensds.vasa.interfaces.device.luncopy.ILunCopyCapability;

import org.opensds.vasa.base.bean.terminal.ResBean;
import org.opensds.vasa.domain.model.bean.S2DLunCopy;

import org.opensds.platform.common.SDKErrorCode;
import org.opensds.platform.common.SDKResult;
import org.opensds.platform.commu.itf.ISDKProtocolAdapter;
import org.opensds.platform.exception.ProtocolAdapterException;
import org.opensds.vasa.vasa20.device.AbstractVASACapability;
import org.opensds.vasa.vasa20.device.VASARestReqMessage;
import org.opensds.vasa.vasa20.device.dj.bean.SLunCopy;
import org.opensds.vasa.vasa20.device.dj.convert.LunCopyCapabilityConvert;

public class LunCopyCapabilityImpl extends AbstractVASACapability implements ILunCopyCapability {
    private static final Logger LOGGER = LogManager.getLogger(LunCopyCapabilityImpl.class);
    private LunCopyCapabilityConvert luncopyCapabilityconvert = new LunCopyCapabilityConvert();

    public LunCopyCapabilityImpl(ISDKProtocolAdapter protocolAdapter) {
        super(protocolAdapter);
    }

    @Override
    public SDKResult<S2DLunCopy> createLuncopy(String arrayId, String name, String description, String sourceLun,
                                               String targetLun, String baseLun, boolean isDiffsLunCopy) {
        LOGGER.debug("createLuncopy() start");
        SDKResult<S2DLunCopy> sdkResult = new SDKResult<S2DLunCopy>();

        VASARestReqMessage req = new VASARestReqMessage();
        req.setHttpMethod("POST");
        req.setMediaType("json");
        req.setArrayId(arrayId);

        SLunCopy luncopyBody = new SLunCopy();
        luncopyBody.setNAME(name);
        luncopyBody.setDESCRIPTION(description);
        if (isDiffsLunCopy) {
            luncopyBody.setSUBTYPE((long) 2);
            luncopyBody.setBASELUN(baseLun);
        } else {
            luncopyBody.setSUBTYPE((long) 1);
        }

        // DTS2016021704638,单个虚拟机克隆速率小于100MB/s，速率改为最快
        luncopyBody.setCOPYSPEED("4");
        luncopyBody.setLUNCOPYTYPE("1");
        luncopyBody.setSOURCELUN("INVALID;" + sourceLun + ";INVALID;INVALID;INVALID");
        luncopyBody.setTARGETLUN("INVALID;" + targetLun + ";INVALID;INVALID;INVALID");

        req.setPayload(luncopyBody);

        try {
            ResBean res = (ResBean) protocolAdapter.syncSendMessage(req, "/luncopy",
                    "org.opensds.vasa.vasa20.device.dj.bean.SLunCopy");

            sdkResult.setErrCode(res.getErrorCode());
            sdkResult.setDescription(res.getDescription());

            if (0 == res.getErrorCode()) {
                SLunCopy sLunCopy = (SLunCopy) (res.getResData());

                sdkResult.setResult(luncopyCapabilityconvert.convertSouth2Model(sLunCopy));
            }
        } catch (ProtocolAdapterException e) {
            sdkResult.setErrCode(e.getErrorCode());
            sdkResult.setDescription(e.getMessage());
            LOGGER.error("createLuncopy() error", e);
        }
        LOGGER.debug("createLuncopy() end");

        return sdkResult;
    }

    @Override
    public SDKResult<S2DLunCopy> getLuncopyById(String arrayId, String luncopyId) {
        LOGGER.debug("getLuncopyById() start");
        SDKResult<S2DLunCopy> sdkResult = new SDKResult<S2DLunCopy>();

        VASARestReqMessage req = new VASARestReqMessage();
        req.setHttpMethod("GET");
        req.setMediaType("json");
        req.setArrayId(arrayId);

        try {
            ResBean res = (ResBean) protocolAdapter.syncSendMessage(req, "/luncopy/" + luncopyId,
                    "org.opensds.vasa.vasa20.device.dj.bean.SLunCopy");

            sdkResult.setErrCode(res.getErrorCode());
            sdkResult.setDescription(res.getDescription());

            if (0 == res.getErrorCode()) {
                SLunCopy sLunCopy = (SLunCopy) (res.getResData());

                sdkResult.setResult(luncopyCapabilityconvert.convertSouth2Model(sLunCopy));
            }
        } catch (ProtocolAdapterException e) {
            sdkResult.setErrCode(e.getErrorCode());
            sdkResult.setDescription(e.getMessage());
            LOGGER.error("getLuncopyById() error", e);
        }
        LOGGER.debug("getLuncopyById() end");

        return sdkResult;
    }

    @Override
    public SDKErrorCode startLuncopy(String arrayId, String luncopyId, boolean isDiffsLunCopy) {
        LOGGER.debug("startLuncopy() start");
        SDKErrorCode sdkErrorCode = new SDKErrorCode();

        VASARestReqMessage req = new VASARestReqMessage();
        req.setHttpMethod("PUT");
        req.setMediaType("json");
        req.setArrayId(arrayId);

        SLunCopy luncopyReq = new SLunCopy();
        luncopyReq.setID(luncopyId);
        if (isDiffsLunCopy) {
            luncopyReq.setSUBTYPE((long) 2);
        } else {
            luncopyReq.setSUBTYPE((long) 1);
        }

        req.setPayload(luncopyReq);

        try {
            ResBean res = (ResBean) protocolAdapter.syncSendMessage(req, "/LUNCOPY/start",
                    "org.opensds.vasa.vasa20.device.dj.bean.SLunCopy");

            sdkErrorCode.setErrCode(res.getErrorCode());
            sdkErrorCode.setDescription(res.getDescription());

        } catch (ProtocolAdapterException e) {
            sdkErrorCode.setErrCode(e.getErrorCode());
            sdkErrorCode.setDescription(e.getMessage());
            LOGGER.error("startLuncopy() error", e);
        }
        LOGGER.debug("startLuncopy() end");

        return sdkErrorCode;
    }

    @Override
    public SDKErrorCode stopLuncopy(String arrayId, String luncopyId, boolean isDiffsLunCopy) {
        LOGGER.debug("stopLuncopy() start");
        SDKErrorCode sdkErrorCode = new SDKErrorCode();

        VASARestReqMessage req = new VASARestReqMessage();
        req.setHttpMethod("PUT");
        req.setMediaType("json");
        req.setArrayId(arrayId);

        SLunCopy luncopyReq = new SLunCopy();
        luncopyReq.setID(luncopyId);
        if (isDiffsLunCopy) {
            luncopyReq.setSUBTYPE((long) 2);
        } else {
            luncopyReq.setSUBTYPE((long) 1);
        }

        req.setPayload(luncopyReq);

        try {
            ResBean res = (ResBean) protocolAdapter.syncSendMessage(req, "/LUNCOPY/stop",
                    "org.opensds.vasa.vasa20.device.dj.bean.SLunCopy");

            sdkErrorCode.setErrCode(res.getErrorCode());
            sdkErrorCode.setDescription(res.getDescription());

        } catch (ProtocolAdapterException e) {
            sdkErrorCode.setErrCode(e.getErrorCode());
            sdkErrorCode.setDescription(e.getMessage());
            LOGGER.error("stopLuncopy() error", e);
        }
        LOGGER.debug("stopLuncopy() end");

        return sdkErrorCode;
    }

    @Override
    public SDKErrorCode deleteLuncopy(String arrayId, String luncopyId, boolean isDiffsLunCopy) {
        LOGGER.debug("deleteLuncopy() start");
        SDKErrorCode sdkErrorCode = new SDKErrorCode();

        VASARestReqMessage req = new VASARestReqMessage();
        req.setHttpMethod("DELETE");
        req.setMediaType("json");
        req.setArrayId(arrayId);

//		SLunCopy luncopyReq = new SLunCopy();
//		luncopyReq.setID(luncopyId);
//		if (isDiffsLunCopy) {
//			luncopyReq.setSUBTYPE((long) 2);
//		} else {
//			luncopyReq.setSUBTYPE((long) 1);
//		}
//		
//
//		req.setPayload(luncopyReq);
        Map<String, String> parameters = new HashMap<String, String>();
        if (isDiffsLunCopy) {
            parameters.put("SUBTYPE", "2");
        } else {
            parameters.put("SUBTYPE", "1");
        }
        req.setParameters(parameters);
        try {
            ResBean res = (ResBean) protocolAdapter.syncSendMessage(req, "/luncopy/" + luncopyId,
                    "org.opensds.vasa.vasa20.device.dj.bean.SLunCopy");

            sdkErrorCode.setErrCode(res.getErrorCode());
            sdkErrorCode.setDescription(res.getDescription());

        } catch (ProtocolAdapterException e) {
            sdkErrorCode.setErrCode(e.getErrorCode());
            sdkErrorCode.setDescription(e.getMessage());
            LOGGER.error("deleteLuncopy() error", e);
        }
        LOGGER.debug("deleteLuncopy() end");

        return sdkErrorCode;
    }

}
