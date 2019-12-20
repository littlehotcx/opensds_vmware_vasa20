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

public class CreateStorageProfileBean {

    private String id;
    private String name;
    private String storageContainerId;
    private String profileType;
    private String qosControl;
    private String qosControlType;
    private String qosControlPolicy;
    private String qosControlObjectiveBandwidth;
    private String qosControlObjectiveIOPS;
    private String qosControlObjectiveLatency;
    private String qosSmartTier;
    private String qosSmartTierValue;
    private String isStorageMedium;
    private String diskTypeValue;
    private String raidLevelValue;
    private String qosUserLevel;
    private String qosServiceType;
    private String controlType;


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

    public String getProfileType() {
        return profileType;
    }

    public void setProfileType(String profileType) {
        this.profileType = profileType;
    }

    public String getQosControl() {
        return qosControl;
    }

    public void setQosControl(String qosControl) {
        this.qosControl = qosControl;
    }

    public String getQosControlType() {
        return qosControlType;
    }

    public void setQosControlType(String qosControlType) {
        this.qosControlType = qosControlType;
    }

    public String getQosControlPolicy() {
        return qosControlPolicy;
    }

    public void setQosControlPolicy(String qosControlPolicy) {
        this.qosControlPolicy = qosControlPolicy;
    }

    public String getQosControlObjectiveBandwidth() {
        return qosControlObjectiveBandwidth;
    }

    public void setQosControlObjectiveBandwidth(String qosControlObjectiveBandwidth) {
        this.qosControlObjectiveBandwidth = qosControlObjectiveBandwidth;
    }

    public String getQosControlObjectiveIOPS() {
        return qosControlObjectiveIOPS;
    }

    public void setQosControlObjectiveIOPS(String qosControlObjectiveIOPS) {
        this.qosControlObjectiveIOPS = qosControlObjectiveIOPS;
    }

    public String getQosControlObjectiveLatency() {
        return qosControlObjectiveLatency;
    }

    public void setQosControlObjectiveLatency(String qosControlObjectiveLatency) {
        this.qosControlObjectiveLatency = qosControlObjectiveLatency;
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
        return "CreateStorageProfileBean [id=" + id + ", name=" + name + ", storageContainerId=" + storageContainerId
                + ", profileType=" + profileType + ", qosControl=" + qosControl + ", qosControlType=" + qosControlType
                + ", qosControlPolicy=" + qosControlPolicy + ", qosControlObjectiveBandwidth="
                + qosControlObjectiveBandwidth + ", qosControlObjectiveIOPS=" + qosControlObjectiveIOPS
                + ", qosControlObjectiveLatency=" + qosControlObjectiveLatency + ", qosSmartTier=" + qosSmartTier
                + ", qosSmartTierValue=" + qosSmartTierValue + ", isStorageMedium=" + isStorageMedium + ", dikTypeValue=" + diskTypeValue + ", raidLevelValue=" + raidLevelValue + ", qosUserLevel=" + qosUserLevel + ", qosServiceType="
                + qosServiceType + ", controlType=" + controlType + "]";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getControlType() {
        return controlType;
    }

    public void setControlType(String controlType) {
        this.controlType = controlType;
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

}
