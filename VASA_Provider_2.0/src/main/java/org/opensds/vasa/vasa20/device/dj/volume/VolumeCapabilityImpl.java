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

package org.opensds.vasa.vasa20.device.dj.volume;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensds.vasa.interfaces.device.volume.IVolumeCapability;
import org.opensds.vasa.vasa20.device.dj.convert.VolumeCapabilityConvert;

import org.opensds.vasa.base.bean.terminal.ResBean;
import org.opensds.vasa.domain.model.bean.S2DVolume;

import org.opensds.platform.common.SDKErrorCode;
import org.opensds.platform.common.SDKResult;
import org.opensds.platform.common.bean.commu.RestReqMessage;
import org.opensds.platform.commu.itf.ISDKProtocolAdapter;
import org.opensds.platform.exception.ProtocolAdapterException;
import org.opensds.vasa.vasa20.device.AbstractVASACapability;
import org.opensds.vasa.vasa20.device.VASARestReqMessage;
import org.opensds.vasa.vasa20.device.dj.bean.BatchVolumeResBean;
import org.opensds.vasa.vasa20.device.dj.bean.CreateVolumeReqBean;
import org.opensds.vasa.vasa20.device.dj.bean.SVolume;
import org.opensds.vasa.vasa20.device.dj.bean.SVolumeMetaData;
import org.opensds.vasa.vasa20.device.dj.bean.VolumeResBean;

public class VolumeCapabilityImpl extends AbstractVASACapability implements IVolumeCapability {
    private static final Logger LOGGER = LogManager.getLogger(VolumeCapabilityImpl.class);

    private VolumeCapabilityConvert volumeCapabilityconvert = new VolumeCapabilityConvert();

    private static int BATCH_SIZE = 1000;

    public VolumeCapabilityImpl(ISDKProtocolAdapter protocolAdapter) {
        super(protocolAdapter);
    }

    //目前采用分页查询，marker每批次查询1000条（经测试1000条在系统空闲时时间为1s以内，系统繁忙时(load值为3)花费时间在3s内）
    @Override
    public SDKResult<List<S2DVolume>> getAllVolume() {
        LOGGER.debug("getAllVolume() start");
        SDKResult<List<S2DVolume>> sdkResult = new SDKResult<List<S2DVolume>>();
        List<S2DVolume> allVolumes = new ArrayList<S2DVolume>();

        VASARestReqMessage req = new VASARestReqMessage();
        req.setHttpMethod("GET");
        req.setMediaType("json");

        try {
            ResBean res = (ResBean) protocolAdapter.syncSendMessage(req, "/volumes/detail?limit=" + BATCH_SIZE,
                    "org.opensds.vasa.vasa20.device.dj.bean.BatchVolumeResBean");

            if (0 != res.getErrorCode()) {
                sdkResult.setErrCode(res.getErrorCode());
                sdkResult.setDescription(res.getDescription());
                return sdkResult;
            }

            BatchVolumeResBean batchArr = (BatchVolumeResBean) res.getResData();
            if (null == batchArr.getVolumes() || batchArr.getVolumes().size() <= 0) {
                sdkResult.setErrCode(res.getErrorCode());
                sdkResult.setDescription(res.getDescription());
                sdkResult.setResult(allVolumes);
                return sdkResult;
            }

            allVolumes.addAll(volumeCapabilityconvert.convertSouth2Model(batchArr.getVolumes()));
            String marker = batchArr.getVolumes().get(batchArr.getVolumes().size() - 1).getId();

            while (true) {
                ResBean res1 = (ResBean) protocolAdapter.syncSendMessage(req, "/volumes/detail?marker=" + marker + "&limit=" + BATCH_SIZE,
                        "org.opensds.vasa.vasa20.device.dj.bean.BatchVolumeResBean");

                if (0 != res1.getErrorCode()) {
                    sdkResult.setErrCode(res1.getErrorCode());
                    sdkResult.setDescription(res1.getDescription());
                    return sdkResult;
                }

                BatchVolumeResBean batchArr1 = (BatchVolumeResBean) res1.getResData();
                if (null == batchArr1.getVolumes() || batchArr1.getVolumes().size() <= 0) {
                    sdkResult.setErrCode(res1.getErrorCode());
                    sdkResult.setDescription(res1.getDescription());
                    sdkResult.setResult(allVolumes);
                    return sdkResult;
                }

                allVolumes.addAll(volumeCapabilityconvert.convertSouth2Model(batchArr1.getVolumes()));
                marker = batchArr1.getVolumes().get(batchArr1.getVolumes().size() - 1).getId();
            }

        } catch (ProtocolAdapterException e) {
            sdkResult.setErrCode(e.getErrorCode());
            sdkResult.setDescription(e.getMessage());
            LOGGER.error("getAllVolume() error", e);
        }

        LOGGER.debug("getAllVolume() end");
        return sdkResult;

    }

    @Override
    public SDKResult<S2DVolume> getVolumeById(String volumeId) {
        LOGGER.debug("getVolumeById() start");
        SDKResult<S2DVolume> sdkResult = new SDKResult<S2DVolume>();

        RestReqMessage req = new RestReqMessage();
        req.setHttpMethod("GET");
        req.setMediaType("json");

        try {
            ResBean res = (ResBean) protocolAdapter.syncSendMessage(req, "/volumes/" + volumeId, "org.opensds.vasa.vasa20.device.dj.bean.VolumeResBean");

            sdkResult.setErrCode(res.getErrorCode());
            sdkResult.setDescription(res.getDescription());

            if (0 == res.getErrorCode()) {
                VolumeResBean vol = (VolumeResBean) res.getResData();
                sdkResult.setResult(volumeCapabilityconvert.convertSouth2Model(vol.getVolume()));
            }
        } catch (ProtocolAdapterException e) {
            sdkResult.setErrCode(e.getErrorCode());
            sdkResult.setDescription(e.getMessage());
            LOGGER.warn("getVolumeById() error.");
        }

        LOGGER.debug("getVolumeById() end");

        return sdkResult;
    }

    @Override
    public SDKResult<S2DVolume> createVolume(String name, String description, int sizeInGB, long sizeInMB, String volumeType, String vmName) {
        LOGGER.debug("createVolume() start");
        SDKResult<S2DVolume> sdkResult = new SDKResult<S2DVolume>();

        RestReqMessage req = new RestReqMessage();
        req.setHttpMethod("POST");
        req.setMediaType("json");

        SVolume svol = new SVolume();
        svol.setName(name);
        svol.setDescription(description);
        svol.setSize(sizeInGB);
        svol.setVolume_type(volumeType);

        SVolumeMetaData metadata = new SVolumeMetaData();
        metadata.setSizeInMegaBytes(String.valueOf(sizeInMB));
        if (null != name) {
            metadata.setDisplayName(name);
        }

        if (null != vmName && !vmName.equalsIgnoreCase("unknown")) {
            metadata.setVmName(vmName);
            LOGGER.info("create volume createVolume vmName is: " + vmName);
        }
        svol.setMetadata(metadata);

        CreateVolumeReqBean reqBody = new CreateVolumeReqBean();
        reqBody.setVolume(svol);

        req.setPayload(reqBody);
        try {
            //第三个参数是response 类Bean
            ResBean res = (ResBean) protocolAdapter.syncSendMessage(req, "/volumes", "org.opensds.vasa.vasa20.device.dj.bean.VolumeResBean");

            sdkResult.setErrCode(res.getErrorCode());
            sdkResult.setDescription(res.getDescription());

            if (0 == res.getErrorCode()) {
                VolumeResBean vol = (VolumeResBean) res.getResData();
                sdkResult.setResult(volumeCapabilityconvert.convertSouth2Model(vol.getVolume()));
            }
        } catch (ProtocolAdapterException e) {
            sdkResult.setErrCode(e.getErrorCode());
            sdkResult.setDescription(e.getMessage());
            LOGGER.error("createVolume() error", e);
        }

        LOGGER.debug("createVolume() end");

        return sdkResult;
    }

    @Override
    public SDKResult<S2DVolume> createVolumeFromSrcVolume(String name, String description, int sizeInGB, long sizeInMB,
                                                          String volumeType, String volumeId, String vmName) {
        LOGGER.debug("createVolumeFromSrcVolume() start");
        SDKResult<S2DVolume> sdkResult = new SDKResult<S2DVolume>();

        RestReqMessage req = new RestReqMessage();
        req.setHttpMethod("POST");
        req.setMediaType("json");
        String reqBody = null;
        if (name == null) {
            reqBody = "{\"volume\":{\"description\":\"" + description + "\",\"size\":" + sizeInGB
                    + ",\"volume_type\":\"" + volumeType + "\",\"metadata\":{\"size_in_megabytes\":\"" + sizeInMB +
                    "\"}},\"OS-SCH-HNT:scheduler_hints\":{\"affinity_flag\":1," +
                    "\"affinity_vols\":[\"" + volumeId + "\"]}}";
        } else if (null != vmName && !vmName.equalsIgnoreCase("unknown")) {
            reqBody = "{\"volume\":{\"name\":\"" + name + "\",\"description\":\"" + description + "\",\"size\":" + sizeInGB
                    + ",\"volume_type\":\"" + volumeType + "\",\"metadata\":{\"size_in_megabytes\":\"" + sizeInMB
                    + "\",\"vm_name\":\"" + vmName + "\",\"display_name\":\"" + name + "\"}},\"OS-SCH-HNT:scheduler_hints\":{\"affinity_flag\":1," +
                    "\"affinity_vols\":[\"" + volumeId + "\"]}}";

            LOGGER.info("clone create VolumeFromSrcVolume vmName is: " + vmName);
        } else {
            reqBody = "{\"volume\":{\"name\":\"" + name + "\",\"description\":\"" + description + "\",\"size\":" + sizeInGB
                    + ",\"volume_type\":\"" + volumeType + "\",\"metadata\":{\"size_in_megabytes\":\"" + sizeInMB
                    + "\",\"display_name\":\"" + name + "\"}},\"OS-SCH-HNT:scheduler_hints\":{\"affinity_flag\":1," +
                    "\"affinity_vols\":[\"" + volumeId + "\"]}}";
        }


        req.setPayload(reqBody);
        try {
            ResBean res = (ResBean) protocolAdapter.syncSendMessage(req, "/volumes", "org.opensds.vasa.vasa20.device.dj.bean.VolumeResBean");

            sdkResult.setErrCode(res.getErrorCode());
            sdkResult.setDescription(res.getDescription());

            if (0 == res.getErrorCode()) {
                VolumeResBean vol = (VolumeResBean) res.getResData();
                sdkResult.setResult(volumeCapabilityconvert.convertSouth2Model(vol.getVolume()));
            }
        } catch (ProtocolAdapterException e) {
            sdkResult.setErrCode(e.getErrorCode());
            sdkResult.setDescription(e.getMessage());
            LOGGER.error("createVolumeFromSrcVolume() error", e);
        }

        LOGGER.debug("createVolumeFromSrcVolume() end");

        return sdkResult;
    }

    @Override
    public SDKErrorCode resizeVolume(String id, int newSize) {
        LOGGER.debug("resizeVolume() start");
        SDKErrorCode sdkErrorCode = new SDKErrorCode();

        RestReqMessage req = new RestReqMessage();
        req.setHttpMethod("POST");
        req.setMediaType("json");

        String reqBody = "{\"os-extend_vvol_in_mb\":{\"new_size\":" + newSize + "}}";

        req.setPayload(reqBody);
        try {
            ResBean res = (ResBean) protocolAdapter.syncSendMessage(req, "/volumes/" + id + "/action", "org.opensds.vasa.vasa20.device.dj.bean.VolumeResBean");

            sdkErrorCode.setErrCode(res.getErrorCode());
            sdkErrorCode.setDescription(res.getDescription());
        } catch (ProtocolAdapterException e) {
            sdkErrorCode.setErrCode(e.getErrorCode());
            sdkErrorCode.setDescription(e.getMessage());
            LOGGER.error("resizeVolume() error", e);
        }

        LOGGER.debug("resizeVolume() end");

        return sdkErrorCode;
    }

    @Override
    public SDKErrorCode deleteVolume(String id) {
        LOGGER.debug("deleteVolume() start");
        SDKErrorCode sdkErrorCode = new SDKErrorCode();

        RestReqMessage req = new RestReqMessage();
        req.setHttpMethod("DELETE");
        req.setMediaType("json");

        try {
            ResBean res = (ResBean) protocolAdapter.syncSendMessage(req, "/volumes/" + id, "org.opensds.vasa.vasa20.device.dj.bean.VolumeResBean");

            sdkErrorCode.setErrCode(res.getErrorCode());
            sdkErrorCode.setDescription(res.getDescription());
        } catch (ProtocolAdapterException e) {
            sdkErrorCode.setErrCode(e.getErrorCode());
            sdkErrorCode.setDescription(e.getMessage());
            LOGGER.error("deleteVolume() error", e);
        }

        LOGGER.debug("deleteVolume() end");

        return sdkErrorCode;
    }

    @Override
    public SDKErrorCode deleteVolumeForcely(String id) {
        LOGGER.debug("deleteVolumeForcely() start");
        SDKErrorCode sdkErrorCode = new SDKErrorCode();

        RestReqMessage req = new RestReqMessage();
        req.setHttpMethod("POST");
        req.setMediaType("json");
        String reqPayload = "{\"os-force_delete\":{}}";
        req.setPayload(reqPayload);
        try {
            ResBean res = (ResBean) protocolAdapter.syncSendMessage(req, "/volumes/" + id + "/action", "org.opensds.vasa.vasa20.device.dj.bean.VolumeResBean");

            sdkErrorCode.setErrCode(res.getErrorCode());
            sdkErrorCode.setDescription(res.getDescription());
        } catch (ProtocolAdapterException e) {
            sdkErrorCode.setErrCode(e.getErrorCode());
            sdkErrorCode.setDescription(e.getMessage());
            if (404 != e.getErrorCode()) {
                LOGGER.error("deleteVolumeForcely() error", e);
            }
        }

        LOGGER.debug("deleteVolumeForcely() end");

        return sdkErrorCode;
    }

    @Override
    public SDKResult<S2DVolume> cloneVolumeFromRawVvol(String name, String description, String sourceVvolId, long sizeInMB) {
        LOGGER.debug("cloneVolumeFromRawVvol() start");
        SDKResult<S2DVolume> sdkResult = new SDKResult<S2DVolume>();

        RestReqMessage req = new RestReqMessage();
        req.setHttpMethod("POST");
        req.setMediaType("json");

        SVolume svol = new SVolume();
        svol.setName(name);
        svol.setDescription(description);
        svol.setSource_volid(sourceVvolId);

        SVolumeMetaData metadata = new SVolumeMetaData();
        metadata.setCreationWay("clone");
        metadata.setSizeInMegaBytes(String.valueOf(sizeInMB));
        if (null != name) {
            metadata.setDisplayName(name);
        }
        svol.setMetadata(metadata);

        CreateVolumeReqBean reqBody = new CreateVolumeReqBean();
        reqBody.setVolume(svol);

        req.setPayload(reqBody);
        try {
            ResBean res = (ResBean) protocolAdapter.syncSendMessage(req, "/volumes", "org.opensds.vasa.vasa20.device.dj.bean.VolumeResBean");

            sdkResult.setErrCode(res.getErrorCode());
            sdkResult.setDescription(res.getDescription());

            if (0 == res.getErrorCode()) {
                VolumeResBean vol = (VolumeResBean) res.getResData();
                sdkResult.setResult(volumeCapabilityconvert.convertSouth2Model(vol.getVolume()));
            }
        } catch (ProtocolAdapterException e) {
            sdkResult.setErrCode(e.getErrorCode());
            sdkResult.setDescription(e.getMessage());
            LOGGER.error("cloneVolumeFromRawVvol() error", e);
        }

        LOGGER.debug("cloneVolumeFromRawVvol() end");

        return sdkResult;
    }

    @Override
    public SDKResult<S2DVolume> cloneVolumeFromSnapshotVvol(String name, String description, String snapshotId, long sizeInMB) {
        LOGGER.debug("cloneVolumeFromSnapshotVvol() start");
        SDKResult<S2DVolume> sdkResult = new SDKResult<S2DVolume>();

        RestReqMessage req = new RestReqMessage();
        req.setHttpMethod("POST");
        req.setMediaType("json");

        SVolume svol = new SVolume();
        svol.setName(name);
        svol.setDescription(description);
        svol.setSnapshot_id(snapshotId);

        SVolumeMetaData metadata = new SVolumeMetaData();
        metadata.setCreationWay("clone");
        metadata.setSizeInMegaBytes(String.valueOf(sizeInMB));
        if (null != name) {
            metadata.setDisplayName(name);
        }
        svol.setMetadata(metadata);

        CreateVolumeReqBean reqBody = new CreateVolumeReqBean();
        reqBody.setVolume(svol);

        req.setPayload(reqBody);
        try {
            ResBean res = (ResBean) protocolAdapter.syncSendMessage(req, "/volumes", "org.opensds.vasa.vasa20.device.dj.bean.VolumeResBean");

            sdkResult.setErrCode(res.getErrorCode());
            sdkResult.setDescription(res.getDescription());

            if (0 == res.getErrorCode()) {
                VolumeResBean vol = (VolumeResBean) res.getResData();
                sdkResult.setResult(volumeCapabilityconvert.convertSouth2Model(vol.getVolume()));
            }
        } catch (ProtocolAdapterException e) {
            sdkResult.setErrCode(e.getErrorCode());
            sdkResult.setDescription(e.getMessage());
            LOGGER.error("cloneVolumeFromSnapshotVvol() error", e);
        }

        LOGGER.debug("cloneVolumeFromSnapshotVvol() end");

        return sdkResult;
    }

    @Override
    public SDKResult<S2DVolume> fastCloneVolumeFromRawVvol(String name, String description, String sourceVvolId, String vmName) {
        LOGGER.debug("fastCloneVolumeFromRawVvol() start");
        SDKResult<S2DVolume> sdkResult = new SDKResult<S2DVolume>();

        RestReqMessage req = new RestReqMessage();
        req.setHttpMethod("POST");
        req.setMediaType("json");

        SVolume svol = new SVolume();
        svol.setName(name);
        svol.setDescription(description);
        svol.setSource_volid(sourceVvolId);

        SVolumeMetaData metadata = new SVolumeMetaData();
        metadata.setCreationWay("fast-clone");
        if (null != name) {
            metadata.setDisplayName(name);
        }
        if (null != vmName && !vmName.equalsIgnoreCase("unknown")) {
            metadata.setVmName(vmName);
            LOGGER.info("fast clone VolumeFromRawVvol vmName is: " + vmName);
        }
        svol.setMetadata(metadata);

        CreateVolumeReqBean reqBody = new CreateVolumeReqBean();
        reqBody.setVolume(svol);

        req.setPayload(reqBody);
        try {
            ResBean res = (ResBean) protocolAdapter.syncSendMessage(req, "/volumes", "org.opensds.vasa.vasa20.device.dj.bean.VolumeResBean");

            sdkResult.setErrCode(res.getErrorCode());
            sdkResult.setDescription(res.getDescription());

            if (0 == res.getErrorCode()) {
                VolumeResBean vol = (VolumeResBean) res.getResData();
                sdkResult.setResult(volumeCapabilityconvert.convertSouth2Model(vol.getVolume()));
            }
        } catch (ProtocolAdapterException e) {
            sdkResult.setErrCode(e.getErrorCode());
            sdkResult.setDescription(e.getMessage());
            LOGGER.error("fastCloneVolumeFromRawVvol() error", e);
        }

        LOGGER.debug("fastCloneVolumeFromRawVvol() end");

        return sdkResult;
    }

    @Override
    public SDKResult<S2DVolume> fastCloneVolumeFromSnapshotVvol(String name, String description, String snapshotId, String vmName) {
        LOGGER.debug("fastCloneVolumeFromSnapshotVvol() start");
        SDKResult<S2DVolume> sdkResult = new SDKResult<S2DVolume>();

        RestReqMessage req = new RestReqMessage();
        req.setHttpMethod("POST");
        req.setMediaType("json");

        SVolume svol = new SVolume();
        svol.setName(name);
        svol.setDescription(description);
        svol.setSnapshot_id(snapshotId);

        SVolumeMetaData metadata = new SVolumeMetaData();
        metadata.setCreationWay("fast-clone");
        if (null != name) {
            metadata.setDisplayName(name);
        }
        if (null != vmName && !vmName.equalsIgnoreCase("unknown")) {
            metadata.setVmName(vmName);
            LOGGER.info("fast clone VolumeFromSnapshotVvol vmName is: " + vmName);
        }
        svol.setMetadata(metadata);

        CreateVolumeReqBean reqBody = new CreateVolumeReqBean();
        reqBody.setVolume(svol);

        req.setPayload(reqBody);
        try {
            ResBean res = (ResBean) protocolAdapter.syncSendMessage(req, "/volumes", "org.opensds.vasa.vasa20.device.dj.bean.VolumeResBean");

            sdkResult.setErrCode(res.getErrorCode());
            sdkResult.setDescription(res.getDescription());

            if (0 == res.getErrorCode()) {
                VolumeResBean vol = (VolumeResBean) res.getResData();
                sdkResult.setResult(volumeCapabilityconvert.convertSouth2Model(vol.getVolume()));
            }
        } catch (ProtocolAdapterException e) {
            sdkResult.setErrCode(e.getErrorCode());
            sdkResult.setDescription(e.getMessage());
            LOGGER.error("fastCloneVolumeFromSnapshotVvol() error", e);
        }

        LOGGER.debug("fastCloneVolumeFromSnapshotVvol() end");

        return sdkResult;
    }


}
