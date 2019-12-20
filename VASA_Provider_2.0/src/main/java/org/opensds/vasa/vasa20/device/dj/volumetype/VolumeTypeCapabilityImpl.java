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

package org.opensds.vasa.vasa20.device.dj.volumetype;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.opensds.vasa.interfaces.device.volumetype.IVolumeTypeCapability;

import org.opensds.vasa.base.bean.terminal.ResBean;
import org.opensds.vasa.domain.model.bean.S2DExtraSpecsInfo;
import org.opensds.vasa.domain.model.bean.S2DVolumeType;
import org.opensds.vasa.domain.model.bean.StoragePolicy;

import org.opensds.platform.common.SDKResult;
import org.opensds.platform.common.bean.commu.RestReqMessage;
import org.opensds.platform.commu.itf.ISDKProtocolAdapter;
import org.opensds.platform.exception.ProtocolAdapterException;
import org.opensds.vasa.vasa20.device.AbstractVASACapability;
import org.opensds.vasa.vasa20.device.dj.bean.SQos;

public class VolumeTypeCapabilityImpl extends AbstractVASACapability implements IVolumeTypeCapability {
    private static final Logger LOGGER = LogManager.getLogger(VolumeTypeCapabilityImpl.class);

    /**
     * 构造ID的原子自增变量
     */
    private static long indx = 0;

    private static final long MIN_QOS_VALUE = 0l;

    private static final long MAX_QOS_VALUE = Long.MAX_VALUE;

    private static final long INVALID_QOS_VALUE = 0;

    public VolumeTypeCapabilityImpl(ISDKProtocolAdapter protocolAdapter) {
        super(protocolAdapter);
    }

    public void convertQos2ExtraSpecs(SQos qosInfo, S2DExtraSpecsInfo specsInfo) {
        //设置volumetype的qos属性
        if (qosInfo.getQos_specs() != null && qosInfo.getQos_specs().getSpecs() != null) {
            specsInfo.setIOType(convert2IOType(qosInfo.getQos_specs().getSpecs().getIOType()));

            if (qosInfo.getQos_specs().getSpecs().getMinIOPS() == null && qosInfo.getQos_specs().getSpecs().getMaxIOPS() == null) {
                specsInfo.setMinIOPS(INVALID_QOS_VALUE);
                specsInfo.setMaxIOPS(INVALID_QOS_VALUE);
            } else {
                if (qosInfo.getQos_specs().getSpecs().getMinIOPS() == null) {
                    specsInfo.setMinIOPS(MIN_QOS_VALUE);
                    specsInfo.setMaxIOPS(Long.valueOf(qosInfo.getQos_specs().getSpecs().getMaxIOPS()));
                } else {
                    specsInfo.setMinIOPS(Long.valueOf(qosInfo.getQos_specs().getSpecs().getMinIOPS()));
                    specsInfo.setMaxIOPS(MAX_QOS_VALUE);
                }
            }

            if (qosInfo.getQos_specs().getSpecs().getMinBandWidth() == null && qosInfo.getQos_specs().getSpecs().getMaxBandWidth() == null) {
                specsInfo.setMinBandWidth(INVALID_QOS_VALUE);
                specsInfo.setMaxBandWidth(INVALID_QOS_VALUE);
            } else {
                if (qosInfo.getQos_specs().getSpecs().getMinBandWidth() == null) {
                    specsInfo.setMinBandWidth(MIN_QOS_VALUE);
                    specsInfo.setMaxBandWidth(Long.valueOf(qosInfo.getQos_specs().getSpecs().getMaxBandWidth()));
                } else {
                    specsInfo.setMinBandWidth(Long.valueOf(qosInfo.getQos_specs().getSpecs().getMinBandWidth()));
                    specsInfo.setMaxBandWidth(MAX_QOS_VALUE);
                }
            }

            if (qosInfo.getQos_specs().getSpecs().getLatency() == null) {
                specsInfo.setMinLatency(INVALID_QOS_VALUE);
                specsInfo.setMaxLatency(INVALID_QOS_VALUE);
            } else {
                specsInfo.setMinLatency(MIN_QOS_VALUE);
                specsInfo.setMaxLatency(Long.valueOf(qosInfo.getQos_specs().getSpecs().getLatency()));
            }
        } else {
            specsInfo.setIOType(null);
            specsInfo.setMinIOPS(INVALID_QOS_VALUE);
            specsInfo.setMaxIOPS(INVALID_QOS_VALUE);
            specsInfo.setMinBandWidth(INVALID_QOS_VALUE);
            specsInfo.setMaxBandWidth(INVALID_QOS_VALUE);
            specsInfo.setMinLatency(INVALID_QOS_VALUE);
            specsInfo.setMaxLatency(INVALID_QOS_VALUE);
        }
    }

    /*
     * Note: Some volume types will be filtered in this method!!
     * */
    @Override
    public SDKResult<List<S2DVolumeType>> getAllVolumeType() {
        LOGGER.info("getAllVolumeType() start");
        SDKResult<List<S2DVolumeType>> sdkResult = new SDKResult<List<S2DVolumeType>>();
        List<S2DVolumeType> allVolumeTypes = new ArrayList<S2DVolumeType>();

        RestReqMessage req = new RestReqMessage();
        req.setHttpMethod("GET");
        req.setMediaType("json");

        try {
            ResBean res = (ResBean) protocolAdapter.syncSendMessage(req, "/types", null);

            if (0 != res.getErrorCode()) {
                sdkResult.setErrCode(res.getErrorCode());
                sdkResult.setDescription(res.getDescription());
                return sdkResult;
            }

            JSONObject resObj = new JSONObject((String) res.getResData());
            LOGGER.info("Queried volume types from DJ: " + resObj);
            JSONArray batchArr = resObj.getJSONArray("volume_types");

            int len = batchArr.length();

            for (int i = 0; i < len; i++) {
                S2DVolumeType volType = new S2DVolumeType();
                JSONObject newTypeObj = batchArr.getJSONObject(i);

                //过滤掉非vvol的volumeType
                String isVvol = newTypeObj.getJSONObject("extra_specs").getString("is_vvol");
                if (isVvol.equalsIgnoreCase("false")) {
                    LOGGER.warn("the volumetype does not support vvol");
                    continue;
                }
                //过滤掉policy_开头的volType
                if (newTypeObj.getString("name").startsWith("policy_")) {
                    continue;
                }
                volType.setId(newTypeObj.getString("id"));
                volType.setName(newTypeObj.getString("name"));
                volType.setCreated_at(newTypeObj.getString("created_at"));//TODO

                S2DExtraSpecsInfo specsInfo = new S2DExtraSpecsInfo();
                specsInfo.setVvol_creation(newTypeObj.getJSONObject("extra_specs").getString("is_vvol"));
                specsInfo.setVirtual_pool_id(newTypeObj.getJSONObject("extra_specs").getString("virtual_pool_id"));

                //Parse thin or thick
                if (newTypeObj.getJSONObject("extra_specs").has("capabilities:thin_provisioning_support")
                        || newTypeObj.getJSONObject("extra_specs").has("capabilities:thick_provisioning_support")) {
                    if (newTypeObj.getJSONObject("extra_specs").has("capabilities:thin_provisioning_support")
                            && newTypeObj.getJSONObject("extra_specs").getString("capabilities:thin_provisioning_support").equalsIgnoreCase("<is> True")) {
                        specsInfo.setThin("thin");
                    } else {
                        specsInfo.setThin("thick");
                    }
                } else {
                    specsInfo.setThin("");
                }
                //Parse SmartTier		
                if (newTypeObj.getJSONObject("extra_specs").has("capabilities:smarttier")
                        && newTypeObj.getJSONObject("extra_specs").getString("capabilities:smarttier").equalsIgnoreCase("<is> True")) {
                    specsInfo.setSmartTierSupport(true);
                } else {
                    specsInfo.setSmartTierSupport(false);
                }

                //Get and parse QoS
                ResBean res1 = (ResBean) protocolAdapter.syncSendMessage(req, "/types/" + volType.getId() + "/types_qos_specs",
                        "org.opensds.vasa.vasa20.device.dj.bean.SQos");
                if (0 != res1.getErrorCode()) {
                    sdkResult.setErrCode(res1.getErrorCode());
                    sdkResult.setDescription(res1.getDescription());
                    return sdkResult;
                }
                SQos qosInfo = (SQos) res1.getResData();
                if (null != qosInfo.getQos_specs().getSpecs()) {
                    LOGGER.info("Queried qosInfo from DJ: " + qosInfo);
                    specsInfo.setQosSupport(true);
                    convertQos2ExtraSpecs(qosInfo, specsInfo);
                } else {
                    specsInfo.setQosSupport(false);
                }

                LOGGER.info("Parsed specs from DJ: " + specsInfo);
                volType.setExtra_specs(specsInfo);

                allVolumeTypes.add(volType);
            }
            LOGGER.info("getAllVolumeType success. size:" + allVolumeTypes.size());
            sdkResult.setErrCode(res.getErrorCode());
            sdkResult.setDescription(res.getDescription());
            sdkResult.setResult(allVolumeTypes);
            return sdkResult;
        } catch (ProtocolAdapterException e) {
            sdkResult.setErrCode(e.getErrorCode());
            sdkResult.setDescription(e.getMessage());
            LOGGER.error("getAllVolumeType() error", e);
        } catch (JSONException e) {
            sdkResult.setErrCode("-1");
            sdkResult.setDescription("json exception!");
            LOGGER.error("getAllVolumeType() error", e);
        }

        LOGGER.debug("getAllVolumeType() end");
        return sdkResult;
    }

    @Override
    public SDKResult<List<S2DVolumeType>> getVolumeTypeByVirtualPool(String poolId) {
        LOGGER.info("getVolumeTypeByVirtualPool() start");
        SDKResult<List<S2DVolumeType>> sdkResult = new SDKResult<List<S2DVolumeType>>();
        List<S2DVolumeType> returnValues = new ArrayList<S2DVolumeType>();

        SDKResult<List<S2DVolumeType>> allVolumeTypeResult = getAllVolumeType();
        if (0 != allVolumeTypeResult.getErrCode()) {
            LOGGER.error("getVolumeTypeByVirtualPool() error");
            sdkResult.setErrCode(allVolumeTypeResult.getErrCode());
            sdkResult.setDescription(allVolumeTypeResult.getDescription());
            return sdkResult;
        }

        for (S2DVolumeType volType : allVolumeTypeResult.getResult()) {
            if (volType.getExtra_specs().getVirtual_pool_id().equalsIgnoreCase(poolId)) {
                returnValues.add(volType);
            }
        }

        sdkResult.setErrCode(allVolumeTypeResult.getErrCode());
        sdkResult.setDescription(allVolumeTypeResult.getDescription());
        sdkResult.setResult(returnValues);
        LOGGER.info("getVolumeTypeByVirtualPool-returnValues size: " + returnValues.size());
        LOGGER.info("getVolumeTypeByVirtualPool() end");
        return sdkResult;
    }

    @Override
    public SDKResult<JSONObject> createVolumeType(StoragePolicy profile) {
        // 创建VolumeType
        SDKResult<JSONObject> sdkResult = new SDKResult<JSONObject>();
        RestReqMessage req = new RestReqMessage();
        req.setHttpMethod("POST");
        req.setMediaType("json");

        String reqPayLoad = null;
        try {
            JSONObject extra_specs = new JSONObject();

            //Set thin or thick.
            if ("thin".equalsIgnoreCase(profile.getType())) {
                extra_specs.put("capabilities:thin_provisioning_support", "<is> True");
            } else {
                extra_specs.put("capabilities:thick_provisioning_support", "<is> True");
            }

            //Set vvol related.
            extra_specs.put("virtual_pool_id", profile.getContainerId());
            extra_specs.put("vvol_support", "<is> True");
            extra_specs.put("is_vvol", "true");

            //Set SmartTier if need.
            if (profile.getSmartTier() != null && !profile.getSmartTier().equalsIgnoreCase("")) {
                extra_specs.put("capabilities:smarttier", "<is> true");
                extra_specs.put("smarttier:policy", profile.getSmartTier());
            }
            JSONObject volume_type = new JSONObject();
            volume_type.put("name", profile.getName());
            volume_type.put("extra_specs", extra_specs);
            JSONObject object = new JSONObject();
            object.put("volume_type", volume_type);
            reqPayLoad = object.toString();
            req.setPayload(reqPayLoad);
            ResBean resBean = (ResBean) protocolAdapter.syncSendMessage(req, "/types", null);
            sdkResult.setErrCode(resBean.getErrorCode());
            sdkResult.setDescription(resBean.getDescription());

            if (0 != resBean.getErrorCode()) {
                LOGGER.error("createVolumeType error, ErrorCode=" + resBean.getErrorCode());
                return sdkResult;
            }
            JSONObject resObj = new JSONObject((String) resBean.getResData());
            JSONObject volTypeRes = resObj.getJSONObject("volume_type");
            sdkResult.setResult(volTypeRes);
            return sdkResult;
        } catch (ProtocolAdapterException e) {
            sdkResult.setErrCode(e.getErrorCode());
            sdkResult.setDescription(e.getMessage());
            LOGGER.error("createVolumeType() error", e);
        } catch (JSONException e) {
            sdkResult.setErrCode("-1");
            sdkResult.setDescription("json exception!");
            LOGGER.error("createVolumeType() error", e);
        }
        LOGGER.debug("createVolumeType() end");
        return sdkResult;
    }

    public static String buildDisplayName(String prefix) {
        return prefix + "_" + buildObjectID();
    }

    private synchronized static String buildObjectID() {
        if (indx > 9999999999L) {
            indx = 0;
        }
        Date d = new Date();
        Long longtime = d.getTime();
        String objID = Long.toHexString(longtime) + "_" + indx++;
        return objID;
    }

    @Override
    public SDKResult<SQos> createQoS(JSONObject qos_specs) {
        // 创建QoS
        SDKResult<SQos> sdkResult = new SDKResult<SQos>();
        try {
            JSONObject qosReq = new JSONObject();
            qosReq.put("qos_specs", qos_specs);

            String reqPayLoad1 = null;
            reqPayLoad1 = qosReq.toString();

            RestReqMessage req = new RestReqMessage();
            req.setHttpMethod("POST");
            req.setMediaType("json");
            req.setPayload(reqPayLoad1);

            ResBean resBean = (ResBean) protocolAdapter.syncSendMessage(req, "/qos-specs",
                    "org.opensds.vasa.vasa20.device.dj.bean.SQos");

            sdkResult.setErrCode(resBean.getErrorCode());
            sdkResult.setDescription(resBean.getDescription());
            if (0 != resBean.getErrorCode()) {
                LOGGER.error("createQoS error, ErrorCode=" + resBean.getErrorCode());
                return sdkResult;
            }
            SQos qosInfo = (SQos) resBean.getResData();
            sdkResult.setResult(qosInfo);
            return sdkResult;
        } catch (ProtocolAdapterException e) {
            sdkResult.setErrCode(e.getErrorCode());
            sdkResult.setDescription(e.getMessage());
            LOGGER.error("getAllVolumeType() error", e);
        } catch (JSONException e) {
            sdkResult.setErrCode("-1");
            sdkResult.setDescription("json exception!");
            LOGGER.error("getAllVolumeType() error", e);
        }
        LOGGER.debug("createVolumeType() end");
        return sdkResult;
    }

    @Override
    public SDKResult<String> associateQoS(String qosId, String volTypeId) {
        SDKResult<String> sdkResult = new SDKResult<String>();
        RestReqMessage req = new RestReqMessage();
        req.setHttpMethod("GET");
        req.setMediaType("json");

        try {
            // 设置指定Qos与VolumeType的关联关系
            ResBean resBean = (ResBean) protocolAdapter.syncSendMessage(req,
                    "/qos-specs/" + qosId + "/associate?vol_type_id=" + volTypeId, null);

            sdkResult.setErrCode(resBean.getErrorCode());
            sdkResult.setDescription(resBean.getDescription());

            if (0 != resBean.getErrorCode()) {
                LOGGER.error("associateQoS error, ErrorCode=" + resBean.getErrorCode() + ", qosId="
                        + qosId + ", volumeTypeId" + volTypeId);
            }
        } catch (ProtocolAdapterException e) {
            sdkResult.setErrCode(e.getErrorCode());
            sdkResult.setDescription(e.getMessage());
            LOGGER.error("associateQoS() error", e);
        }
        return sdkResult;
    }

    @Override
    public void disassociateQoS(String qosId, String volumeTypeId) throws ProtocolAdapterException {
        RestReqMessage req = new RestReqMessage();
        req.setHttpMethod("GET");
        req.setMediaType("json");
        ResBean res1 = (ResBean) protocolAdapter.syncSendMessage(req, "/qos-specs/" + qosId + "/disassociate?vol_type_id=" + volumeTypeId, null);
        if (0 != res1.getErrorCode()) {
            LOGGER.error("disassociateQoS() error.");
        }
    }

    @Override
    public SDKResult<SQos> getQosByVolumeType(String volTypeId) {
        SDKResult<SQos> sdkResult = new SDKResult<SQos>();
        RestReqMessage req = new RestReqMessage();
        req.setHttpMethod("GET");
        req.setMediaType("json");
        try {
            ResBean res1 = (ResBean) protocolAdapter.syncSendMessage(req, "/types/" + volTypeId + "/types_qos_specs",
                    "org.opensds.vasa.vasa20.device.dj.bean.SQos");
            if (0 != res1.getErrorCode()) {
                sdkResult.setErrCode(res1.getErrorCode());
                sdkResult.setDescription(res1.getDescription());
                return sdkResult;
            }
            SQos qosInfo = (SQos) res1.getResData();
            sdkResult.setResult(qosInfo);
        } catch (ProtocolAdapterException e) {
            sdkResult.setErrCode(e.getErrorCode());
            sdkResult.setDescription(e.getMessage());
        }
        return sdkResult;
    }

    @Override
    public void delVolumeType(String volumeTypeId) throws ProtocolAdapterException {
        RestReqMessage req = new RestReqMessage();
        req.setHttpMethod("DELETE");
        req.setMediaType("json");
        ResBean res1 = (ResBean) protocolAdapter.syncSendMessage(req, "/types/" + volumeTypeId, null);
        if (0 != res1.getErrorCode()) {
            LOGGER.error("delVolumeType() error.");
        }
    }

    public void delQos(String qosId) throws ProtocolAdapterException {
        RestReqMessage req = new RestReqMessage();
        req.setHttpMethod("DELETE");
        req.setMediaType("json");
        ResBean res1 = (ResBean) protocolAdapter.syncSendMessage(req, "/qos-specs/" + qosId, null);
        if (0 != res1.getErrorCode()) {
            LOGGER.error("delQos() error.");
        }
    }

    private String convert2IOType(String ioType) {
        if (ioType == null) {
            return "Read/Write I/Os";
        }

        if (ioType.equalsIgnoreCase("0")) {
            return "Read I/O";
        } else if (ioType.equalsIgnoreCase("1")) {
            return "Write I/O";
        } else {
            return "Read/Write I/Os";
        }
    }

    @Override
    public SDKResult<Object> setVolumeRetype(String volId, String newType, String migrationPolicy) {
        // 创建VolumeType
        SDKResult<Object> sdkResult = new SDKResult<Object>();
        RestReqMessage req = new RestReqMessage();
        req.setHttpMethod("POST");
        req.setMediaType("json");

        String reqPayLoad = null;
        try {
            JSONObject retype = new JSONObject();
            retype.put("new_type", newType);
            retype.put("migration_policy", migrationPolicy);
            JSONObject object = new JSONObject();
            object.put("os-retype", retype);
            reqPayLoad = object.toString();
            req.setPayload(reqPayLoad);
            ResBean resBean = (ResBean) protocolAdapter.syncSendMessage(req, "/volumes/" + volId + "/action", null);
            sdkResult.setErrCode(resBean.getErrorCode());
            sdkResult.setDescription(resBean.getDescription());
            LOGGER.info("XXX set retype return :" + resBean.getResData());
            if (0 != resBean.getErrorCode()) {
                LOGGER.error("setVolumeRetype error, ErrorCode=" + resBean.getErrorCode());
                return sdkResult;
            }
            sdkResult.setResult(resBean.getResData());
            return sdkResult;
        } catch (ProtocolAdapterException e) {
            sdkResult.setErrCode(e.getErrorCode());
            sdkResult.setDescription(e.getMessage());
            LOGGER.error("setVolumeRetype() error", e);
        } catch (JSONException e) {
            sdkResult.setErrCode("-1");
            sdkResult.setDescription("json exception!");
            LOGGER.error("setVolumeRetype() error", e);
        }
        LOGGER.debug("setVolumeRetype() end");
        return sdkResult;
    }
}
