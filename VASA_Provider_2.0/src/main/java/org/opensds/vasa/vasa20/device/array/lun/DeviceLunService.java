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

package org.opensds.vasa.vasa20.device.array.lun;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.opensds.vasa.base.bean.terminal.ResBean;
import org.opensds.vasa.base.common.VASAArrayUtil;
import org.opensds.vasa.common.MagicNumber;
import org.opensds.vasa.domain.model.bean.StoragePolicy;
import org.opensds.platform.common.SDKErrorCode;
import org.opensds.platform.common.SDKResult;
import org.opensds.platform.common.utils.StringUtils;
import org.opensds.platform.commu.itf.ISDKProtocolAdapter;
import org.opensds.platform.exception.ProtocolAdapterException;
import org.opensds.vasa.vasa20.device.AbstractVASACapability;
import org.opensds.vasa.vasa20.device.VASARestReqMessage;

public class DeviceLunService extends AbstractVASACapability implements IDeviceLunService {

    public DeviceLunService(ISDKProtocolAdapter protocolAdapter) {
        super(protocolAdapter);
        // TODO Auto-generated constructor stub
    }

    private static Logger LOGGER = LogManager.getLogger(DeviceLunService.class);
    private String createLun_url = "lun";
    private String getLun_url = "lun/";
    private String createLunCopy_url = "/luncopy";
    private String startLunCopy_url = "LUNCOPY/start";
    private String queryLunCopy_url = "luncopy/";
    private String deleteLun_url = "lun/";
    private String expandLun = "lun/expand";
    private String updateLun = "lun/";
    private Integer copySpeed = 4;
    private static String lunStrFormat = "INVALID;{lunId};INVALID;INVALID;INVALID";

    @Override
    public SDKResult<LunCreateResBean> createLun(String arrayId, String name, String description, String parentId,
                                                 int subType, String allocType, long capacity,
                                                 String dataTransferPolicy, int usageType, int ioProperty) {
        VASARestReqMessage req = new VASARestReqMessage();
        req.setHttpMethod("POST");
        req.setMediaType("json");
        req.setArrayId(arrayId);
        LunCreateReqBean reqPayload = new LunCreateReqBean();
        SDKResult<LunCreateResBean> sdkResult = new SDKResult<>();
        createLunReqBean(name, description, parentId, subType, allocType,
                capacity, dataTransferPolicy, usageType, reqPayload, ioProperty);
        LOGGER.debug("create lun LunReqBean=" + reqPayload);
        req.setPayload(reqPayload);
        try {
            ResBean response = (ResBean) protocolAdapter.syncSendMessage(req, createLun_url, "org.opensds.vasa.vasa20.device.array.lun.LunCreateResBean");
            sdkResult.setErrCode(response.getErrorCode());
            sdkResult.setDescription(response.getDescription());
            if (0 == response.getErrorCode()) {
                LunCreateResBean result = (LunCreateResBean) response.getResData();
                sdkResult.setResult(result);
            }
            LOGGER.debug("create lun response=" + response);
        } catch (ProtocolAdapterException e) {
            LOGGER.error("createLun fail ! LunCreateReqBean=" + reqPayload, e);
            sdkResult.setErrCode(e.getErrorCode());
            sdkResult.setDescription("createLun fail ! LunCreateReqBean=" + reqPayload);
        }
        return sdkResult;
    }

    private void createLunReqBean(String name, String description,
                                  String parentId, Integer subType, String allocType, long capacity,
                                  String dataTransferPolicy, Integer usageType,
                                  LunCreateReqBean reqPayload, int ioProperty) {
        if (null != allocType && allocType.equalsIgnoreCase("Thin")) {
            reqPayload.setALLOCTYPE(1);
        } else {
            reqPayload.setALLOCTYPE(0);
        }
        if (null != dataTransferPolicy && StoragePolicy.smartTierValueMap.containsValue(dataTransferPolicy)) {
            reqPayload.setDATATRANSFERPOLICY(Integer.valueOf(dataTransferPolicy));
        } else {
            reqPayload.setDATATRANSFERPOLICY(0);
        }
        reqPayload.setCAPACITY(capacity);
        reqPayload.setDESCRIPTION(description);
        reqPayload.setNAME(name);
        reqPayload.setPARENTID(parentId);
        reqPayload.setUSAGETYPE(usageType);
        reqPayload.setSUBTYPE(subType);
        reqPayload.setIOPRIORITY(ioProperty);
    }

    public String getCreateLun_url() {
        return createLun_url;
    }

    public void setCreateLun_url(String createLun_url) {
        this.createLun_url = createLun_url;
    }

    @Override
    public SDKResult<LunCreateResBean> queryLunInfo(String arrayId, String lunId) {
        // TODO Auto-generated method stub
        VASARestReqMessage req = new VASARestReqMessage();
        req.setHttpMethod("GET");
        req.setMediaType("json");
        req.setArrayId(arrayId);
        SDKResult<LunCreateResBean> sdkResult = new SDKResult<>();
        try {
            LOGGER.debug("queryLunInfo lunId=" + lunId);
            ResBean response = (ResBean) protocolAdapter.syncSendMessage(req, getLun_url + lunId, "org.opensds.vasa.vasa20.device.array.lun.LunCreateResBean");
            sdkResult.setErrCode(response.getErrorCode());
            sdkResult.setDescription(response.getDescription());
            LOGGER.debug("queryLunInfo " + response);
            if (0 == response.getErrorCode()) {
                LunCreateResBean result = (LunCreateResBean) response.getResData();
                sdkResult.setResult(result);
            }
        } catch (ProtocolAdapterException e) {
            LOGGER.error("getLunInfo fail ! lunId=" + lunId, e);
            sdkResult.setErrCode(e.getErrorCode());
            sdkResult.setDescription("getLunInfo fail ! lunId=" + lunId);
        }
        return sdkResult;
    }

    @Override
    public SDKResult<LunCopyCreateResBean> createLunCopy(String arrayId, String name, String description,
                                                         String sourceLunId, String targetLunId) {
        // TODO Auto-generated method stub
        VASARestReqMessage req = new VASARestReqMessage();
        req.setHttpMethod("POST");
        req.setArrayId(arrayId);
        req.setMediaType("json");
        LunCopyCreateReqBean lunCopyCreateReqBean = new LunCopyCreateReqBean();
        lunCopyCreateReqBean.setNAME(name);
        lunCopyCreateReqBean.setDESCRIPTION(description);
        lunCopyCreateReqBean.setSOURCELUN(lunStrFormat.replace("{lunId}", sourceLunId));
        lunCopyCreateReqBean.setTARGETLUN(lunStrFormat.replace("{lunId}", targetLunId));
        lunCopyCreateReqBean.setSUBTYPE("1");
        lunCopyCreateReqBean.setCOPYSPEED(copySpeed);
        req.setPayload(lunCopyCreateReqBean);
        LOGGER.debug("begin createLunCopy ," + lunCopyCreateReqBean);
        SDKResult<LunCopyCreateResBean> sdkResult = new SDKResult<>();
        ResBean response = new ResBean();
        try {
            response = (ResBean) protocolAdapter.syncSendMessage(req, createLunCopy_url, "org.opensds.vasa.vasa20.device.array.lun.LunCopyCreateResBean");
            sdkResult.setErrCode(response.getErrorCode());
            sdkResult.setDescription(response.getDescription());
            if (0 == response.getErrorCode()) {
                LunCopyCreateResBean result = (LunCopyCreateResBean) response.getResData();
                sdkResult.setResult(result);
            }
            LOGGER.debug("end createLunCopy ," + sdkResult);
        } catch (ProtocolAdapterException e) {
            LOGGER.info("createLunCopy fail ! lunCopyCreateReqBean=" + lunCopyCreateReqBean, e);
            sdkResult.setErrCode(e.getErrorCode());
            sdkResult.setErrCode("createLunCopy fail ! lunCopyCreateReqBean=" + lunCopyCreateReqBean);
        }
        return sdkResult;
    }

    @Override
    public SDKErrorCode startLunCopy(String arrayId, String lunCopyId) {
        // TODO Auto-generated method stub
        VASARestReqMessage req = new VASARestReqMessage();
        req.setHttpMethod("PUT");
        req.setMediaType("json");
        req.setArrayId(arrayId);
        LunCopyStartReqBean reqBean = new LunCopyStartReqBean();
        reqBean.setID(lunCopyId);
        req.setPayload(reqBean);
        LOGGER.debug("begin startLunCopy ,lunCopyId=" + lunCopyId);
        SDKErrorCode sdkResult = new SDKErrorCode();
        try {
            ResBean response = (ResBean) protocolAdapter.syncSendMessage(req, startLunCopy_url, null);
            sdkResult.setErrCode(response.getErrorCode());
            sdkResult.setDescription(response.getDescription());
            LOGGER.debug("end createLunCopy ," + sdkResult);
        } catch (ProtocolAdapterException e) {
            LOGGER.error("startLunCopy fail ! startLunCopy=" + lunCopyId, e);
            sdkResult.setErrCode(e.getErrorCode());
            sdkResult.setDescription("startLunCopy fail ! startLunCopy=" + lunCopyId);
        }
        return sdkResult;
    }

    @Override
    public SDKResult<LunCopyCreateResBean> queryLunCopyInfo(String arrayId, String lunCopyId) {
        // TODO Auto-generated method stub
        VASARestReqMessage req = new VASARestReqMessage();
        req.setHttpMethod("GET");
        req.setArrayId(arrayId);
        LOGGER.debug("begin queryLunCopyInfo ,lunCopyId=" + lunCopyId);
        SDKResult<LunCopyCreateResBean> sdkResult = new SDKResult<>();
        try {
            ResBean response = (ResBean) protocolAdapter.syncSendMessage(req, queryLunCopy_url + lunCopyId,
                    "org.opensds.vasa.vasa20.device.array.lun.LunCopyCreateResBean");
            sdkResult.setErrCode(response.getErrorCode());
            sdkResult.setDescription(response.getDescription());
            if (0 == response.getErrorCode()) {
                LunCopyCreateResBean result = (LunCopyCreateResBean) response.getResData();
                sdkResult.setResult(result);
            }
            LOGGER.debug("end queryLunCopyInfo ," + sdkResult);
        } catch (ProtocolAdapterException e) {
            LOGGER.error("startLunCopy fail ! startLunCopy=" + lunCopyId, e);
            sdkResult.setErrCode(e.getErrorCode());
            sdkResult.setDescription("startLunCopy fail ! startLunCopy=" + lunCopyId);
        }
        return sdkResult;
    }

    @Override
    public SDKErrorCode deleteLun(String arrayId, String lunId) {
        // TODO Auto-generated method stub
        VASARestReqMessage req = new VASARestReqMessage();
        req.setHttpMethod("DELETE");
        req.setArrayId(arrayId);
        Map<String, String> map = new HashMap<>();
        map.put("SUBTYPE", String.valueOf(VASAArrayUtil.SUBTYPE.vvolLUN));
        req.setParameters(map);
        LOGGER.debug("begin deleteLun ,lunId=" + lunId);
        SDKErrorCode sdkResult = new SDKErrorCode();
        try {
            ResBean response = (ResBean) protocolAdapter.syncSendMessage(req, deleteLun_url + lunId, null);
            sdkResult.setErrCode(response.getErrorCode());
            sdkResult.setDescription(response.getDescription());
            LOGGER.debug("end deleteLun ,response=" + response);
        } catch (ProtocolAdapterException e) {
            LOGGER.error("deleteLun fail ! lunId=" + lunId, e);
            sdkResult.setErrCode(e.getErrorCode());
            sdkResult.setDescription("deleteLun fail ! lunId=" + lunId);
        }
        return sdkResult;

    }

    @Override
    public SDKErrorCode expandLun(String arrayId, String lun, long sizeInMb) {
        // TODO Auto-generated method stub
        VASARestReqMessage req = new VASARestReqMessage();
        req.setHttpMethod("PUT");
        req.setMediaType("json");
        req.setArrayId(arrayId);
        LunExpandReqBean reqBean = new LunExpandReqBean();
        reqBean.setID(lun);
        //转换为sectors
        reqBean.setCAPACITY(sizeInMb * MagicNumber.LONG1024 * MagicNumber.LONG1024 / MagicNumber.LONG512);
        req.setPayload(reqBean);
        LOGGER.debug("begin expandLun ,lunId=" + lun + ",sizeInMb=" + sizeInMb);
        SDKErrorCode sdkResult = new SDKErrorCode();
        try {
            ResBean response = (ResBean) protocolAdapter.syncSendMessage(req, expandLun, null);
            sdkResult.setErrCode(response.getErrorCode());
            sdkResult.setDescription(response.getDescription());
            LOGGER.debug("end expandLun ," + sdkResult);
        } catch (ProtocolAdapterException e) {
            LOGGER.error("expandLun fail ! lunId=" + lun, e);
            sdkResult.setErrCode(e.getErrorCode());
            sdkResult.setDescription("expandLun fail ! lunId=" + lun);
        }
        return sdkResult;
    }

    @Override
    public SDKErrorCode updateLun(String arrayId, String lunId, int ioProperty, String smartTier) {
        // TODO Auto-generated method stub
        VASARestReqMessage req = new VASARestReqMessage();
        req.setHttpMethod("PUT");
        req.setMediaType("json");
        req.setArrayId(arrayId);
        LunUpdateReqBean reqBean = new LunUpdateReqBean();
        reqBean.setIOPRIORITY(ioProperty);
        if (StringUtils.isEmpty(smartTier)) {
            reqBean.setDATATRANSFERPOLICY(0);
        } else {
            reqBean.setDATATRANSFERPOLICY(Integer.valueOf(smartTier));
        }
        req.setPayload(reqBean);
        SDKErrorCode sdkResult = new SDKErrorCode();
        try {
            ResBean response = (ResBean) protocolAdapter.syncSendMessage(req, updateLun + lunId, null);
            sdkResult.setErrCode(response.getErrorCode());
            sdkResult.setDescription(response.getDescription());
            LOGGER.debug("end updateLunIOProperty ," + sdkResult);
        } catch (ProtocolAdapterException e) {
            LOGGER.error("updateLunIOProperty fail ! lunId=" + lunId, e);
            sdkResult.setErrCode(e.getErrorCode());
            sdkResult.setDescription("updateLunIOProperty fail ! lunId=" + lunId);
        }
        return sdkResult;
    }

}
