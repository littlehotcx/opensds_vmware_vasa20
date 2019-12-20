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

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensds.vasa.interfaces.device.snapshot.ISnapshotCapability;

import org.opensds.vasa.base.bean.terminal.ResBean;
import org.opensds.vasa.domain.model.bean.S2DSnapshot;

import org.opensds.platform.common.SDKErrorCode;
import org.opensds.platform.common.SDKResult;
import org.opensds.platform.common.bean.commu.RestReqMessage;
import org.opensds.platform.commu.itf.ISDKProtocolAdapter;
import org.opensds.platform.exception.ProtocolAdapterException;
import org.opensds.vasa.vasa20.device.AbstractVASACapability;
import org.opensds.vasa.vasa20.device.dj.bean.BatchSnapshotResBean;
import org.opensds.vasa.vasa20.device.dj.bean.CreateSnapshotReqBean;
import org.opensds.vasa.vasa20.device.dj.bean.SSnapshot;
import org.opensds.vasa.vasa20.device.dj.bean.SnapshotResBean;
import org.opensds.vasa.vasa20.device.dj.convert.SnapshotCapabilityConvert;

public class SnapshotCapabilityImpl extends AbstractVASACapability implements ISnapshotCapability {
    private static final Logger LOGGER = LogManager.getLogger(SnapshotCapabilityImpl.class);

    private SnapshotCapabilityConvert snapshotCapabilityconvert = new SnapshotCapabilityConvert();

    private static final int NUM_PER_FETCH = 100;

    public SnapshotCapabilityImpl(ISDKProtocolAdapter protocolAdapter) {
        super(protocolAdapter);
    }


    /**
     * 按照指定状态从Dj返回所有的卷
     *
     * @param status
     * @return
     */
    private SDKResult<List<S2DSnapshot>> getAllStatusSnapshot(String status) {
        LOGGER.debug("getAllSnapshot(String) start. status is: " + status);
        SDKResult<List<S2DSnapshot>> sdkResult = new SDKResult<List<S2DSnapshot>>();
        List<S2DSnapshot> allSnapshots = new ArrayList<S2DSnapshot>();

        RestReqMessage req = new RestReqMessage();
        req.setHttpMethod("GET");
        req.setMediaType("json");

        int offset = 0;
        int limit = NUM_PER_FETCH;
        try {
            while (true) {
                ResBean res = (ResBean) protocolAdapter.syncSendMessage(req, "/snapshots?status=" + status + "&limit=" + limit + "&offset=" + offset, "org.opensds.vasa.vasa20.device.dj.bean.BatchSnapshotResBean");
                if (0 != res.getErrorCode()) {
                    sdkResult.setErrCode(res.getErrorCode());
                    sdkResult.setDescription(res.getDescription());
                    return sdkResult;
                }

                BatchSnapshotResBean batchArr = (BatchSnapshotResBean) res.getResData();
                if (null == batchArr.getSnapshots() || batchArr.getSnapshots().size() <= 0) {
                    sdkResult.setErrCode(res.getErrorCode());
                    sdkResult.setDescription(res.getDescription());
                    sdkResult.setResult(allSnapshots);
                    return sdkResult;
                }

                int len = batchArr.getSnapshots().size();
                allSnapshots.addAll(snapshotCapabilityconvert.convertSouth2Model(batchArr.getSnapshots()));
                if (len < NUM_PER_FETCH) {
                    sdkResult.setErrCode(res.getErrorCode());
                    sdkResult.setDescription(res.getDescription());
                    sdkResult.setResult(allSnapshots);
                    return sdkResult;
                } else {
                    offset = offset + NUM_PER_FETCH;
                }
            }
        } catch (ProtocolAdapterException e) {
            sdkResult.setErrCode(e.getErrorCode());
            sdkResult.setDescription(e.getMessage());
            LOGGER.error("getAllSnapshot(String) error. status is: " + status, e);
        }

        LOGGER.debug("getAllSnapshot(String) end. status is: " + status);
        return sdkResult;
    }


    @Override
    public SDKResult<List<S2DSnapshot>> getAllSnapshot() {
        LOGGER.debug("getAllSnapshot() start");
		/*SDKResult<List<S2DSnapshot>> sdkResult = new SDKResult<List<S2DSnapshot>>();
		List<S2DSnapshot> allSnapshots = new ArrayList<S2DSnapshot>();
		sdkResult.setResult(allSnapshots);
		
		//快照在Dj处的状态有: available error(对应VP的error_creating) creating deleting error_deleting deleted
		//其中 deleted的状态无需查询
		List<String> allGetStatus =  new ArrayList<String>();
		allGetStatus.add("available");
		allGetStatus.add("error");
		allGetStatus.add("creating");
		allGetStatus.add("deleting");
		allGetStatus.add("error_deleting");
		
		//遍历所有的状态从Dj获取此种状态的卷，只要有其中一个出错，整体就返回错误
		for(String status : allGetStatus)
		{
		    SDKResult<List<S2DSnapshot>> sdkStatusResult = getAllStatusSnapshot(status);
		    if(0 != sdkStatusResult.getErrCode())
		    {
		        return sdkStatusResult;
		    }
		    else
		    {
		        sdkResult.setErrCode(sdkStatusResult.getErrCode());
		        sdkResult.setDescription(sdkStatusResult.getDescription());
		        sdkResult.getResult().addAll(sdkStatusResult.getResult());
		    }
		}
		
		
		 LOGGER.debug("getAllSnapshot() end. all status size is: " + sdkResult.getResult().size());
		 return sdkResult;*/


        //LOGGER.debug("getAllSnapshot(String) start. status is: " + status);
        SDKResult<List<S2DSnapshot>> sdkResult = new SDKResult<List<S2DSnapshot>>();
        List<S2DSnapshot> allSnapshots = new ArrayList<S2DSnapshot>();

        RestReqMessage req = new RestReqMessage();
        req.setHttpMethod("GET");
        req.setMediaType("json");

        int offset = 0;
        int limit = NUM_PER_FETCH;
        try {
            while (true) {
                ResBean res = (ResBean) protocolAdapter.syncSendMessage(req, "/snapshots?&limit=" + limit + "&offset=" + offset, "org.opensds.vasa.vasa20.device.dj.bean.BatchSnapshotResBean");


                if (0 != res.getErrorCode()) {
                    sdkResult.setErrCode(res.getErrorCode());
                    sdkResult.setDescription(res.getDescription());
                    return sdkResult;
                }

                BatchSnapshotResBean batchArr = (BatchSnapshotResBean) res.getResData();
                if (null == batchArr.getSnapshots() || batchArr.getSnapshots().size() <= 0) {
                    sdkResult.setErrCode(res.getErrorCode());
                    sdkResult.setDescription(res.getDescription());
                    sdkResult.setResult(allSnapshots);
                    return sdkResult;
                }

                int len = batchArr.getSnapshots().size();
                allSnapshots.addAll(snapshotCapabilityconvert.convertSouth2Model(batchArr.getSnapshots()));
                if (len < NUM_PER_FETCH) {
                    sdkResult.setErrCode(res.getErrorCode());
                    sdkResult.setDescription(res.getDescription());
                    sdkResult.setResult(allSnapshots);
                    return sdkResult;
                } else {
                    offset = offset + NUM_PER_FETCH;
                }
            }
        } catch (ProtocolAdapterException e) {
            sdkResult.setErrCode(e.getErrorCode());
            sdkResult.setDescription(e.getMessage());
            LOGGER.error("getAllSnapshot() error.", e);
        }

        LOGGER.debug("getAllSnapshot()  end.");
        return sdkResult;
    }

    @Override
    public SDKResult<S2DSnapshot> getSnapshotById(String snapshotId) {
        LOGGER.debug("getSnapshotById() start");
        SDKResult<S2DSnapshot> sdkResult = new SDKResult<S2DSnapshot>();

        RestReqMessage req = new RestReqMessage();
        req.setHttpMethod("GET");
        req.setMediaType("json");

        try {
            ResBean res = (ResBean) protocolAdapter.syncSendMessage(req, "/snapshots/" + snapshotId, "org.opensds.vasa.vasa20.device.dj.bean.SnapshotResBean");

            sdkResult.setErrCode(res.getErrorCode());
            sdkResult.setDescription(res.getDescription());

            if (0 == res.getErrorCode()) {
                SnapshotResBean snapshot = (SnapshotResBean) res.getResData();
                sdkResult.setResult(snapshotCapabilityconvert.convertSouth2Model(snapshot.getSnapshot()));
            }
        } catch (ProtocolAdapterException e) {
            sdkResult.setErrCode(e.getErrorCode());
            sdkResult.setDescription(e.getMessage());
            LOGGER.warn("getSnapshotById() error");
        }

        LOGGER.debug("getSnapshotById() end");

        return sdkResult;
    }

    @Override
    public SDKResult<S2DSnapshot> createSnapshot(String vvolId, String name, String description) {
        LOGGER.debug("createSnapshot() start");
        SDKResult<S2DSnapshot> sdkResult = new SDKResult<S2DSnapshot>();

        RestReqMessage req = new RestReqMessage();
        req.setHttpMethod("POST");
        req.setMediaType("json");

        SSnapshot snapshot = new SSnapshot();
        snapshot.setName(name);
        snapshot.setDescription(description);
        snapshot.setVolume_id(vvolId);

        CreateSnapshotReqBean reqBody = new CreateSnapshotReqBean();
        reqBody.setSnapshot(snapshot);

        req.setPayload(reqBody);
        try {
            ResBean res = (ResBean) protocolAdapter.syncSendMessage(req, "/snapshots", "org.opensds.vasa.vasa20.device.dj.bean.SnapshotResBean");

            sdkResult.setErrCode(res.getErrorCode());
            sdkResult.setDescription(res.getDescription());

            if (0 == res.getErrorCode()) {
                SnapshotResBean snapVol = (SnapshotResBean) res.getResData();
                sdkResult.setResult(snapshotCapabilityconvert.convertSouth2Model(snapVol.getSnapshot()));
            }
        } catch (ProtocolAdapterException e) {
            sdkResult.setErrCode(e.getErrorCode());
            sdkResult.setDescription(e.getMessage());
            LOGGER.error("createSnapshot() error", e);
        }
        LOGGER.debug("createSnapshot() end");
        return sdkResult;
    }


    @Override
    public SDKErrorCode deleteSnapshot(String snapshotId) {
        LOGGER.debug("deleteSnapshot() start");
        SDKErrorCode sdkErrorCode = new SDKErrorCode();

        RestReqMessage req = new RestReqMessage();
        req.setHttpMethod("DELETE");
        req.setMediaType("json");

        try {
            ResBean res = (ResBean) protocolAdapter.syncSendMessage(req, "/snapshots/" + snapshotId, "org.opensds.vasa.vasa20.device.dj.bean.SnapshotResBean");

            sdkErrorCode.setErrCode(res.getErrorCode());
            sdkErrorCode.setDescription(res.getDescription());
        } catch (ProtocolAdapterException e) {
            sdkErrorCode.setErrCode(e.getErrorCode());
            sdkErrorCode.setDescription(e.getMessage());
            LOGGER.error("deleteSnapshot() error", e);
        }

        LOGGER.debug("deleteSnapshot() end");

        return sdkErrorCode;
    }

    @Override
    public SDKErrorCode deleteSnapshotForcely(String snapshotId) {
        LOGGER.debug("deleteSnapshotForcely() start");
        SDKErrorCode sdkErrorCode = new SDKErrorCode();

        RestReqMessage req = new RestReqMessage();
        req.setHttpMethod("POST");
        req.setMediaType("json");
        String reqPayload = "{\"os-force_delete\":{}}";
        req.setPayload(reqPayload);

        try {
            ResBean res = (ResBean) protocolAdapter.syncSendMessage(req, "/snapshots/" + snapshotId + "/action", "org.opensds.vasa.vasa20.device.dj.bean.SnapshotResBean");

            sdkErrorCode.setErrCode(res.getErrorCode());
            sdkErrorCode.setDescription(res.getDescription());
        } catch (ProtocolAdapterException e) {
            sdkErrorCode.setErrCode(e.getErrorCode());
            sdkErrorCode.setDescription(e.getMessage());
            LOGGER.error("deleteSnapshotForcely() error", e);
        }

        LOGGER.debug("deleteSnapshotForcely() end");

        return sdkErrorCode;
    }


}
