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

package org.opensds.vasa.vasa.service.model;

import org.opensds.vasa.vasa.db.model.NProfileLevel;
import org.opensds.vasa.vasa.db.model.NStorageCapabilityQos;
import org.opensds.vasa.vasa.db.model.NStorageProfile;
import org.opensds.vasa.vasa.db.model.NStorageQos;

public class StorageProfileData extends NStorageProfile {

    private String id;
    private String rawQosId;
    private String name;
    private String description;
    private String status;
    private String flowControlType;
    private String controlPolicy;
    private Long bandWidth;
    private Long iops;
    private Long latency;
    private String userLevel;
    private String serviceType;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "StorageProfileData [id=" + id + ", rawQosId=" + rawQosId + ", name=" + name + ", description="
                + description + ", status=" + status + ", controlType=" + flowControlType + ", controlPolicy="
                + controlPolicy + ", bandWidth=" + bandWidth + ", iops=" + iops + ", latency=" + latency
                + ", userLevel=" + userLevel + ", serviceType=" + serviceType + ", " + "]";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public String getFlowControlType() {
        return flowControlType;
    }

    public void setFlowControlType(String flowControlType) {
        this.flowControlType = flowControlType;
    }

    public String getControlPolicy() {
        return controlPolicy;
    }

    public void setControlPolicy(String controlPolicy) {
        this.controlPolicy = controlPolicy;
    }


    public Long getBandWidth() {
        return bandWidth;
    }

    public void setBandWidth(Long bandWidth) {
        this.bandWidth = bandWidth;
    }

    public Long getIops() {
        return iops;
    }

    public void setIops(Long iops) {
        this.iops = iops;
    }

    public Long getLatency() {
        return latency;
    }

    public void setLatency(Long latency) {
        this.latency = latency;
    }

    public static void combineProfileData(NStorageProfile storageProfile, NStorageQos storageQos, NProfileLevel profileLevel, NStorageCapabilityQos storageCapabilityQos, StorageProfileData profileData) {

        if (null != storageQos) {
            profileData.setBandWidth(storageQos.getBandWidth());
            profileData.setControlPolicy(storageQos.getControlPolicy());
            profileData.setDescription(storageQos.getDescription());
            profileData.setId(storageQos.getId());
            profileData.setIops(storageQos.getIops());
            profileData.setLatency(storageQos.getLatency());
            profileData.setName(storageQos.getName());
            profileData.setRawQosId(storageQos.getRawQosId());
            profileData.setFlowControlType(storageQos.getControlType());
        }

        if (null != storageCapabilityQos) {
            profileData.setBandWidth(storageCapabilityQos.getBandwidth());
            profileData.setControlPolicy(storageCapabilityQos.getQosControlPolicy());
            profileData.setId(storageCapabilityQos.getId());
            profileData.setIops(storageCapabilityQos.getIops());
            profileData.setLatency(storageCapabilityQos.getLatency());
            profileData.setName(storageCapabilityQos.getName());
            profileData.setFlowControlType(storageCapabilityQos.getQosControlType());
        }

        if (null != profileLevel) {
            profileData.setUserLevel(profileLevel.getUserLevel());
            profileData.setServiceType(profileLevel.getServiceType());
        }
        profileData.setContainerId(storageProfile.getContainerId());
        profileData.setControlTypeId(storageProfile.getControlTypeId());
        profileData.setControlType(storageProfile.getControlType());
        profileData.setSmartQosId(storageProfile.getSmartQosId());
        profileData.setIsSmartTier(storageProfile.getIsSmartTier());
        profileData.setSmartTierValue(storageProfile.getSmartTierValue());
        if (storageProfile.getIsStorageMedium()) {
            profileData.setIsStorageMedium(true);
            profileData.setDiskTypeValue(storageProfile.getDiskTypeValue());
            profileData.setRaidLevelValue(storageProfile.getRaidLevelValue());
        } else {
            profileData.setIsStorageMedium(false);
        }
        profileData.setGenerationId(storageProfile.getGenerationId());
        profileData.setDeprecated(storageProfile.getDeprecated());
        profileData.setOmCreated(storageProfile.getOmCreated());
        profileData.setProfileId(storageProfile.getProfileId());
        profileData.setProfileName(storageProfile.getProfileName());
        profileData.setThinThick(storageProfile.getThinThick());
    }

    public String getRawQosId() {
        return rawQosId;
    }

    public void setRawQosId(String rawQosId) {
        this.rawQosId = rawQosId;
    }

    public String getUserLevel() {
        return userLevel;
    }

    public void setUserLevel(String userLevel) {
        this.userLevel = userLevel;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }


}
