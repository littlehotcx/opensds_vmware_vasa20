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

package org.opensds.vasa.vasa.rest.bean;

public class StorageProfileRestBean {
    String id;

    String name;

    String storageContainerId;

    String storageContainerName;

    String profileType;

    String QoSControl;

    String QoSControlType;

    String QoSControlPolicy;

    String QosControlObjectiveBandwidth;

    String QosControlObjectiveIOPS;

    String QosControlObjectiveLatency;

    String qosSmartTier;

    String qosSmartTierValue;

    String isStorageMedium;

    String diskTypeValue;

    String raidLevelValue;

    String qosUserLevel;

    String qosServiceType;

    String controlType;


    public String getControlType() {
        return controlType;
    }

    public void setControlType(String controlType) {
        this.controlType = controlType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStorageContainerId() {
        return storageContainerId;
    }

    public void setStorageContainerId(String storageContainerId) {
        this.storageContainerId = storageContainerId;
    }

    public String getStorageContainerName() {
        return storageContainerName;
    }

    public void setStorageContainerName(String storageContainerName) {
        this.storageContainerName = storageContainerName;
    }

    public String getProfileType() {
        return profileType;
    }

    public void setProfileType(String profileType) {
        this.profileType = profileType;
    }

    public String getQoSControl() {
        return QoSControl;
    }

    public void setQoSControl(String qoSControl) {
        QoSControl = qoSControl;
    }

    public String getQoSControlType() {
        return QoSControlType;
    }

    public void setQoSControlType(String qoSControlType) {
        QoSControlType = qoSControlType;
    }

    public String getQoSControlPolicy() {
        return QoSControlPolicy;
    }

    public void setQoSControlPolicy(String qoSControlPolicy) {
        QoSControlPolicy = qoSControlPolicy;
    }

    public String getQosControlObjectiveBandwidth() {
        return QosControlObjectiveBandwidth;
    }

    public void setQosControlObjectiveBandwidth(String qosControlObjectiveBandwidth) {
        QosControlObjectiveBandwidth = qosControlObjectiveBandwidth;
    }

    public String getQosControlObjectiveIOPS() {
        return QosControlObjectiveIOPS;
    }

    public void setQosControlObjectiveIOPS(String qosControlObjectiveIOPS) {
        QosControlObjectiveIOPS = qosControlObjectiveIOPS;
    }

    public String getQosControlObjectiveLatency() {
        return QosControlObjectiveLatency;
    }

    public void setQosControlObjectiveLatency(String qosControlObjectiveLatency) {
        QosControlObjectiveLatency = qosControlObjectiveLatency;
    }

    public String getQosSmartTier() {
        return qosSmartTier;
    }

    public void setQosSmartTier(String qosSmartTier) {
        this.qosSmartTier = qosSmartTier;
    }

    public String getQosSmartTierValue() {
        return qosSmartTierValue;
    }

    public void setQosSmartTierValue(String qosSmartTierValue) {
        this.qosSmartTierValue = qosSmartTierValue;
    }

    public String getIsStorageMedium() {
        return isStorageMedium;
    }

    public void setIsStorageMedium(String isStorageMedium) {
        this.isStorageMedium = isStorageMedium;
    }

    public String getDiskTypeValue() {
        return diskTypeValue;
    }

    public void setDiskTypeValue(String diskTypeValue) {
        this.diskTypeValue = diskTypeValue;
    }

    public String getRaidLevelValue() {
        return raidLevelValue;
    }

    public void setRaidLevelValue(String raidLevelValue) {
        this.raidLevelValue = raidLevelValue;
    }

    public String getQosUserLevel() {
        return qosUserLevel;
    }

    public void setQosUserLevel(String qosUserLevel) {
        this.qosUserLevel = qosUserLevel;
    }

    public String getQosServiceType() {
        return qosServiceType;
    }

    public void setQosServiceType(String qosServiceType) {
        this.qosServiceType = qosServiceType;
    }

    @Override
    public String toString() {
        return "StorageProfileRestBean [id=" + id + ", name=" + name + ", storageContainerId=" + storageContainerId
                + ", storageContainerName=" + storageContainerName + ", profileType=" + profileType + ", QoSControl="
                + QoSControl + ", QoSControlType=" + QoSControlType + ", QoSControlPolicy=" + QoSControlPolicy
                + ", QosControlObjectiveBandwidth=" + QosControlObjectiveBandwidth + ", QosControlObjectiveIOPS="
                + QosControlObjectiveIOPS + ", QosControlObjectiveLatency=" + QosControlObjectiveLatency
                + ", qosSmartTier=" + qosSmartTier + ", qosSmartTierValue=" + qosSmartTierValue + ", isStorageMedium=" + isStorageMedium + ", diskTypeValue=" + diskTypeValue + ", raidLevelValue=" + raidLevelValue + ", qosUserLevel="
                + qosUserLevel + ", qosServiceType=" + qosServiceType + "]";
    }
}
