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

package org.opensds.vasa.vasa20.device.dj.snapshot;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensds.vasa.interfaces.device.snapshot.ISnapshotextendCapability;

import org.opensds.vasa.base.bean.terminal.ResBean;
import org.opensds.vasa.domain.model.bean.S2DPassThroughSnapshot;

import org.opensds.platform.common.SDKErrorCode;
import org.opensds.platform.common.SDKResult;
import org.opensds.platform.commu.itf.ISDKProtocolAdapter;
import org.opensds.platform.exception.ProtocolAdapterException;
import org.opensds.vasa.vasa20.device.AbstractVASACapability;
import org.opensds.vasa.vasa20.device.VASARestReqMessage;
import org.opensds.vasa.vasa20.device.dj.bean.PassThroughSnapshot;
import org.opensds.vasa.vasa20.device.dj.bean.SSnapshotBatchOperation;
import org.opensds.vasa.vasa20.device.dj.convert.SnapshotCapabilityConvert;

public class SnapshotextendCapabilityImpl extends AbstractVASACapability implements ISnapshotextendCapability {
    private static final Logger LOGGER = LogManager.getLogger(SnapshotextendCapabilityImpl.class);
    private SnapshotCapabilityConvert snapshotCapabilityconvert = new SnapshotCapabilityConvert();

    public SnapshotextendCapabilityImpl(ISDKProtocolAdapter protocolAdapter) {
        super(protocolAdapter);
    }

    @Override
    public SDKResult<S2DPassThroughSnapshot> getSnapshotById(String arrayId, String rawId) {
        LOGGER.debug("getSnapshotById() start");
        SDKResult<S2DPassThroughSnapshot> sdkResult = new SDKResult<S2DPassThroughSnapshot>();

        VASARestReqMessage req = new VASARestReqMessage();
        req.setHttpMethod("GET");
        req.setMediaType("json");
        req.setArrayId(arrayId);

        try {
            ResBean res = (ResBean) protocolAdapter.syncSendMessage(req, "/snapshot" + rawId,
                    "org.opensds.vasa.vasa20.device.dj.bean.PassThroughSnapshot");

            sdkResult.setErrCode(res.getErrorCode());
            sdkResult.setDescription(res.getDescription());

            if (0 == res.getErrorCode()) {
                PassThroughSnapshot passThroughSnapshot = (PassThroughSnapshot) (res.getResData());

                sdkResult.setResult(snapshotCapabilityconvert.convertSouth2Model(passThroughSnapshot));
            }
        } catch (ProtocolAdapterException e) {
            sdkResult.setErrCode(e.getErrorCode());
            sdkResult.setDescription(e.getMessage());
            LOGGER.error("getSnapshotById() error", e);
        }
        LOGGER.debug("getSnapshotById() end");

        return sdkResult;
    }

    @Override
    public SDKErrorCode activateSnapshot(String arrayId, List<String> snapshotIds) {
        LOGGER.debug("activateSnapshot() start");
        SDKErrorCode sdkErrorCode = new SDKErrorCode();

        VASARestReqMessage req = new VASARestReqMessage();
        req.setHttpMethod("POST");
        req.setMediaType("json");
        req.setArrayId(arrayId);

        SSnapshotBatchOperation batchOperation = new SSnapshotBatchOperation();
        batchOperation.setSNAPSHOTLIST(snapshotIds);

        req.setPayload(batchOperation);

        try {
            ResBean res = (ResBean) protocolAdapter.syncSendMessage(req, "/snapshot/activate_vvol",
                    "org.opensds.vasa.vasa20.device.dj.bean.SSnapshotBatchOperation");

            sdkErrorCode.setErrCode(res.getErrorCode());
            sdkErrorCode.setDescription(res.getDescription());
        } catch (ProtocolAdapterException e) {
            sdkErrorCode.setErrCode(e.getErrorCode());
            sdkErrorCode.setDescription(e.getMessage());
            LOGGER.error("activateSnapshot() error", e);
        }
        LOGGER.debug("activateSnapshot() end");

        return sdkErrorCode;
    }

    @Override
    public SDKErrorCode rollbackSnapshot(String arrayId, String rawId) {
        LOGGER.debug("rollbackSnapshot() start");
        SDKErrorCode sdkErrorCode = new SDKErrorCode();

        VASARestReqMessage req = new VASARestReqMessage();
        req.setHttpMethod("PUT");
        req.setMediaType("json");
        req.setArrayId(arrayId);

        PassThroughSnapshot passThroughSnapshot = new PassThroughSnapshot();
        passThroughSnapshot.setID(rawId);
        passThroughSnapshot.setSUBTYPE((long) 1);
        passThroughSnapshot.setROLLBACKSPEED("4");

        req.setPayload(passThroughSnapshot);

        try {
            ResBean res = (ResBean) protocolAdapter.syncSendMessage(req, "/snapshot/rollback",
                    "org.opensds.vasa.vasa20.device.dj.bean.PassThroughSnapshot");

            sdkErrorCode.setErrCode(res.getErrorCode());
            sdkErrorCode.setDescription(res.getDescription());
        } catch (ProtocolAdapterException e) {
            sdkErrorCode.setErrCode(e.getErrorCode());
            sdkErrorCode.setDescription(e.getMessage());
            LOGGER.error("rollbackSnapshot() error", e);
        }
        LOGGER.debug("rollbackSnapshot() end");

        return sdkErrorCode;
    }
}
