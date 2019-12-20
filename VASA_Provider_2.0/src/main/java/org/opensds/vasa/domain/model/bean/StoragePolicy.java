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

package org.opensds.vasa.domain.model.bean;

import java.util.Map;

import org.opensds.vasa.base.common.VASAArrayUtil;
import org.opensds.vasa.vasa.db.model.NStorageCapabilityQos;
import org.opensds.vasa.vasa.db.model.NStorageProfile;

import java.util.HashMap;

public class StoragePolicy {
    public static Map<String, String> smartTierValueMap;

    private String name;

    private String containerId;

    private String containerName;

    private String type; // "Thin" or "Thick"

    private String qosControllerType; //IOType

    private String smartTier;

    private String diskType;

    private String raidLevel;

    private String qosPolicy;

    private Long qosMaxBandwidth;

    private Long qosMinBandwidth;

    private Long qosMaxIOPS;

    private Long qosMinIOPS;

    private String qosLatency;

    private boolean closeQos = false;

    private String controlType;
    private String controlTypeId;

    /**
     *  修改findbugs问题：ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD    Start
     */
    static {
        smartTierValueMap = new HashMap<String, String>();
        smartTierValueMap.put("No relocation", "0");
        smartTierValueMap.put("Auto relocation", "1");
        smartTierValueMap.put("Relocation to high-performance", "2");
        smartTierValueMap.put("Relocation to low-performance", "3");

    }

    @Override
    public String toString() {
        return "StoragePolicy [name=" + name + ", containerId=" + containerId + ", containerName=" + containerName
                + ", type=" + type + ", qosControllerType=" + qosControllerType + ", smartTier=" + smartTier + ", diskType=" + diskType
                + ", raidLevel=" + raidLevel + ", qosPolicy=" + qosPolicy + ", qosMaxBandwidth=" + qosMaxBandwidth + ", qosMinBandwidth="
                + qosMinBandwidth + ", qosMaxIOPS=" + qosMaxIOPS + ", qosMinIOPS=" + qosMinIOPS + ", qosLatency="
                + qosLatency + ", controlType=" + controlType + ", controlTypeId=" + controlTypeId + "]";
    }

    public static void CreateStoragePolicy(NStorageCapabilityQos storageCapabilityQos, NStorageProfile storageProfile, StoragePolicy policy) {

        policy.setContainerId(storageProfile.getContainerId());
        policy.setControlType(storageProfile.getControlType());
        policy.setControlTypeId(storageProfile.getControlTypeId());

        policy.setQosControllerPolicy(storageCapabilityQos.getQosControlPolicy());
        policy.setQosControllerType(storageCapabilityQos.getQosControlType());
        if (storageCapabilityQos.getQosControlPolicy().equalsIgnoreCase(VASAArrayUtil.ControlPolicy.isLowerBound)) {
            policy.setQosControllerObjectLatency(String.valueOf(storageCapabilityQos.getLatency()));
            policy.setQosControllerObjectMinBandwidth(storageCapabilityQos.getBandwidth());
            policy.setQosControllerObjectMinIOPS(storageCapabilityQos.getIops());
        } else if (storageCapabilityQos.getQosControlPolicy().equalsIgnoreCase(VASAArrayUtil.ControlPolicy.isUpperBound)) {
            policy.setQosControllerObjectMaxBandwidth(storageCapabilityQos.getBandwidth());
            policy.setQosControllerObjectMaxIOPS(storageCapabilityQos.getIops());
        }
    }

    public StoragePolicy() {
        /*smartTier = "";
        qosLatency = "";
        
        qosMinIOPS = 0;
        qosMaxIOPS=0;
        qosMinBandwidth=0;
        qosMaxBandwidth=0;*/
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContainerId() {
        return containerId;
    }

    public void setContainerId(String containerId) {
        this.containerId = containerId;
    }

    public String getContainerName() {
        return containerName;
    }

    public void setContainerName(String containerName) {
        this.containerName = containerName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    //直接返回0,1,2,3格式
    public String getSmartTier() {
        return smartTier;
    }

    //入参为英文字符串，转为0,1,2,3格式后记录。
    public void setSmartTier(String smartTier) {
        this.smartTier = smartTierValueMap.get(smartTier);
    }

    public String getDiskType() {
        return diskType;
    }

    public void setDiskType(String diskType) {
        this.diskType = diskType;
    }

    public String getRaidLevel() {
        return raidLevel;
    }

    public void setRaidLevel(String raidLevel) {
        this.raidLevel = raidLevel;
    }

    public String getQosControllerType() {
        return qosControllerType;
    }

    public void setQosControllerType(String qosControllerType) {
        this.qosControllerType = qosControllerType;
    }

    public String getQosControllerPolicy() {
        return qosPolicy;
    }

    public void setQosControllerPolicy(String qosPolicy) {
        this.qosPolicy = qosPolicy;
    }

    public Long getQosControllerObjectMaxBandwidth() {
        return qosMaxBandwidth;
    }

    public void setQosControllerObjectMaxBandwidth(Long qosMaxBandwidth) {
        this.qosMaxBandwidth = qosMaxBandwidth;
    }

    public Long getQosControllerObjectMinBandwidth() {
        return qosMinBandwidth;
    }

    public void setQosControllerObjectMinBandwidth(Long qosMinBandwidth) {
        this.qosMinBandwidth = qosMinBandwidth;
    }

    public Long getQosControllerObjectMaxIOPS() {
        return qosMaxIOPS;
    }

    public void setQosControllerObjectMaxIOPS(Long qosMaxIOPS) {
        this.qosMaxIOPS = qosMaxIOPS;
    }

    public Long getQosControllerObjectMinIOPS() {
        return qosMinIOPS;
    }

    public void setQosControllerObjectMinIOPS(Long qosMinIOPS) {
        this.qosMinIOPS = qosMinIOPS;
    }

    public String getQosControllerObjectLatency() {
        return qosLatency;
    }

    public void setQosControllerObjectLatency(String qosLatency) {
        this.qosLatency = qosLatency;
    }

    public static Map<String, String> getSmartTierValueMap() {
        return smartTierValueMap;
    }

    public static void setSmartTierValueMap(Map<String, String> smartTierValueMap) {
        StoragePolicy.smartTierValueMap = smartTierValueMap;
    }

    public String getQosPolicy() {
        return qosPolicy;
    }

    public void setQosPolicy(String qosPolicy) {
        this.qosPolicy = qosPolicy;
    }

    public Long getQosMaxBandwidth() {
        return qosMaxBandwidth;
    }

    public void setQosMaxBandwidth(Long qosMaxBandwidth) {
        this.qosMaxBandwidth = qosMaxBandwidth;
    }

    public Long getQosMinBandwidth() {
        return qosMinBandwidth;
    }

    public void setQosMinBandwidth(Long qosMinBandwidth) {
        this.qosMinBandwidth = qosMinBandwidth;
    }

    public Long getQosMaxIOPS() {
        return qosMaxIOPS;
    }

    public void setQosMaxIOPS(Long qosMaxIOPS) {
        this.qosMaxIOPS = qosMaxIOPS;
    }

    public Long getQosMinIOPS() {
        return qosMinIOPS;
    }

    public void setQosMinIOPS(Long qosMinIOPS) {
        this.qosMinIOPS = qosMinIOPS;
    }

    public String getQosLatency() {
        return qosLatency;
    }

    public void setQosLatency(String qosLatency) {
        this.qosLatency = qosLatency;
    }

    public String getControlType() {
        return controlType;
    }

    public void setControlType(String controlType) {
        this.controlType = controlType;
    }

    public String getControlTypeId() {
        return controlTypeId;
    }

    public void setControlTypeId(String controlTypeId) {
        this.controlTypeId = controlTypeId;
    }

    public boolean isCloseQos() {
        return closeQos;
    }

    public void setCloseQos(boolean closeQos) {
        this.closeQos = closeQos;
    }


}
