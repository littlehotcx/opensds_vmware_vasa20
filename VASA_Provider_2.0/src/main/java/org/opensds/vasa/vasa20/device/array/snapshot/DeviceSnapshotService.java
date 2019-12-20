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

package org.opensds.vasa.vasa20.device.array.snapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.opensds.vasa.base.bean.terminal.ResBean;
import org.opensds.platform.common.SDKErrorCode;
import org.opensds.platform.common.SDKResult;
import org.opensds.platform.commu.itf.ISDKProtocolAdapter;
import org.opensds.platform.exception.ProtocolAdapterException;
import org.opensds.vasa.vasa20.device.AbstractVASACapability;
import org.opensds.vasa.vasa20.device.VASARestReqMessage;

public class DeviceSnapshotService extends AbstractVASACapability implements IDeviceSnapshotService {

    public DeviceSnapshotService(ISDKProtocolAdapter protocolAdapter) {
        super(protocolAdapter);
        // TODO Auto-generated constructor stub
    }

    private static Logger LOGGER = LogManager.getLogger(DeviceSnapshotService.class);
    private String createSnap_url = "snapshot";
    private String activevvolSnap_url = "/snapshot/activate_vvol";
    private String deleteSnap_url = "snapshot/";
    private String deactivateSnapshot = "snapshot/stop";
    private String querySnapshot = "snapshot/";

    @Override
    public SDKResult<SnapshotCreateResBean> createSnapshot(String arrayId, String name, String parentId,
                                                           String description, String subType, String parentType) {
        // TODO Auto-generated method stub
        VASARestReqMessage req = new VASARestReqMessage();
        req.setHttpMethod("POST");
        req.setMediaType("json");
        req.setArrayId(arrayId);
        SnapshotCreateReqBean snapshotCreateReqBean = new SnapshotCreateReqBean();
        snapshotCreateReqBean.setDESCRIPTION(description);
        snapshotCreateReqBean.setNAME(name);
        snapshotCreateReqBean.setPARENTID(parentId);
        snapshotCreateReqBean.setPARENTTYPE(null);
        snapshotCreateReqBean.setSUBTYPE(subType);
        req.setPayload(snapshotCreateReqBean);
        LOGGER.debug("begin createSnapshot, " + snapshotCreateReqBean);
        SDKResult<SnapshotCreateResBean> sdkResult = new SDKResult<>();
        try {
            ResBean response = (ResBean) protocolAdapter.syncSendMessage(req, createSnap_url,
                    "org.opensds.vasa.vasa20.device.array.snapshot.SnapshotCreateResBean");
            sdkResult.setErrCode(response.getErrorCode());
            sdkResult.setDescription(response.getDescription());
            if (0 == response.getErrorCode()) {
                SnapshotCreateResBean result = (SnapshotCreateResBean) response.getResData();
                sdkResult.setResult(result);
            }
            LOGGER.debug("end createSnapshot, " + response);
        } catch (ProtocolAdapterException e) {
            LOGGER.error("createSnapshot fail ! snapshotCreateReqBean=" + snapshotCreateReqBean, e);
            sdkResult.setErrCode(e.getErrorCode());
            sdkResult.setDescription("createSnapshot fail ! snapshotCreateReqBean=" + snapshotCreateReqBean);
        }
        return sdkResult;
    }

    @Override
    public SDKErrorCode activeVvolLunSnapshot(String arrayId, List<String> snapshotlist) {
        // TODO Auto-generated method stub

        VASARestReqMessage req = new VASARestReqMessage();
        req.setHttpMethod("POST");
        req.setMediaType("json");
        req.setArrayId(arrayId);
        SnapshotActiveReqBean payload = new SnapshotActiveReqBean();
        payload.setSNAPSHOTLIST(snapshotlist);
        req.setPayload(payload);
        LOGGER.debug("begin activeVvolLunSnapshot, " + snapshotlist);
        SDKErrorCode sdkResult = new SDKErrorCode();
        try {
            ResBean response = (ResBean) protocolAdapter.syncSendMessage(req, activevvolSnap_url,
                    null);
            sdkResult.setErrCode(response.getErrorCode());
            sdkResult.setDescription(response.getDescription());
            LOGGER.debug("end activeVvolLunSnapshot, " + response);
        } catch (ProtocolAdapterException e) {
            LOGGER.error("activeVvolLunSnapshot fail ! snapshotlist=" + snapshotlist, e);
            sdkResult.setErrCode(e.getErrorCode());
            sdkResult.setDescription("activeVvolLunSnapshot fail ! snapshotlist=" + snapshotlist);
        }
        return sdkResult;
    }

    @Override
    public SDKErrorCode deleteSnapshot(String arrayId, String snapshotId) {
        // TODO Auto-generated method stub
        VASARestReqMessage req = new VASARestReqMessage();
        req.setHttpMethod("DELETE");
        req.setMediaType("json");
        req.setArrayId(arrayId);
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("SUBTYPE", "1");
        req.setParameters(parameters);
        SDKErrorCode sdkResult = new SDKErrorCode();
        LOGGER.debug("begin delateSnapshot, snapshotId=" + snapshotId);
        try {
            ResBean response = (ResBean) protocolAdapter.syncSendMessage(req, deleteSnap_url + snapshotId,
                    null);
            sdkResult.setErrCode(response.getErrorCode());
            sdkResult.setDescription(response.getDescription());
            LOGGER.debug("end delateSnapshot, response=" + response);
        } catch (ProtocolAdapterException e) {
            LOGGER.error("delateSnapshot fail ! snapshotId=" + snapshotId, e);
            sdkResult.setErrCode(e.getErrorCode());
            sdkResult.setDescription("delateSnapshot fail ! snapshotId=" + snapshotId);
        }
        return sdkResult;
    }

    @Override
    public SDKErrorCode deactivateSnapshot(String arrayId, String snapshotId) {
        // TODO Auto-generated method stub

        VASARestReqMessage req = new VASARestReqMessage();
        req.setHttpMethod("PUT");
        req.setMediaType("json");
        req.setArrayId(arrayId);
        DeactivateSnapshotReqBean payload = new DeactivateSnapshotReqBean();
        payload.setID(snapshotId);
        req.setPayload(payload);
        LOGGER.debug("deactivateSnapshot " + payload);
        SDKErrorCode sdkResult = new SDKErrorCode();
        try {
            ResBean response = (ResBean) protocolAdapter.syncSendMessage(req, deactivateSnapshot, null);
            sdkResult.setErrCode(response.getErrorCode());
            sdkResult.setDescription(response.getDescription());
        } catch (ProtocolAdapterException e) {
            sdkResult.setErrCode(e.getErrorCode());
            sdkResult.setDescription("deactivateSnapshot fail ! snapshotId=" + snapshotId);
        }
        return sdkResult;
    }

    @Override
    public SDKResult<SnapshotCreateResBean> querySnapshotInfo(String arrayId, String snapshotId) {
        // TODO Auto-generated method stub
        VASARestReqMessage req = new VASARestReqMessage();
        req.setHttpMethod("GET");
        req.setMediaType("json");
        req.setArrayId(arrayId);
        LOGGER.debug("querySnapshotInfo snapshotId = " + snapshotId);
        SDKResult<SnapshotCreateResBean> sdkResult = new SDKResult<>();
        try {
            ResBean response = (ResBean) protocolAdapter.syncSendMessage(req, querySnapshot + snapshotId, "org.opensds.vasa.vasa20.device.array.snapshot.SnapshotCreateResBean");
            sdkResult.setErrCode(response.getErrorCode());
            sdkResult.setDescription(response.getDescription());
            if (0 == response.getErrorCode()) {
                SnapshotCreateResBean result = (SnapshotCreateResBean) response.getResData();
                sdkResult.setResult(result);
            }
        } catch (ProtocolAdapterException e) {
            sdkResult.setErrCode(e.getErrorCode());
            sdkResult.setDescription("querySnapshotInfo fail ! snapshotId=" + snapshotId);
        }
        return sdkResult;
    }

}
