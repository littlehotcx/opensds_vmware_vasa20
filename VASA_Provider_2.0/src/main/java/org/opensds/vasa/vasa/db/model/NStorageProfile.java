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

package org.opensds.vasa.vasa.db.model;

public class NStorageProfile extends BaseData {

    public class ControlType {
        public static final String precision_control = "precision_control";
        public static final String level_control = "level_control";
        public static final String capability_control = "capability_control";
    }

    private String profileId;
    private String policyId;
    private String profileName;
    private Boolean isSmartTier;
    private String smartTierValue;
    private Boolean isStorageMedium;
    private String diskTypeValue;
    private String raidLevelValue;
    private String controlType;
    private String controlTypeId;
    private String smartQosId;
    private String containerId;
    private String thinThick;
    private Long generationId;
    private String deprecated;
    private Boolean omCreated;


    @Override
    public String toString() {
        return "NStorageProfile [profileId=" + profileId + ", policyId=" + policyId + ", profileName=" + profileName
                + ", isSmartTier=" + isSmartTier + ", smartTierValue=" + smartTierValue + ", isStorageMedium=" + isStorageMedium + ", diskTypeValue=" + diskTypeValue
                + ", raidLevelValue=" + raidLevelValue + ", controlType=" + controlType + ", controlTypeId=" + controlTypeId
                + ", smartQosId=" + smartQosId + ", containerId=" + containerId
                + ", thinThick=" + thinThick + ", generationId=" + generationId + ", deprecated=" + deprecated
                + ", omCreated=" + omCreated + "]";
    }


    public String getProfileId() {
        return profileId;
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

    public String getContainerId() {
        return containerId;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public Boolean getIsSmartTier() {
        return isSmartTier;
    }

    public void setIsSmartTier(Boolean isSmartTier) {
        this.isSmartTier = isSmartTier;
    }

    public String getSmartTierValue() {
        return smartTierValue;
    }

    public void setSmartTierValue(String smartTierValue) {
        this.smartTierValue = smartTierValue;
    }

    public void setContainerId(String containerId) {
        this.containerId = containerId;
    }


    public String getThinThick() {
        return thinThick;
    }


    public void setThinThick(String thinThick) {
        this.thinThick = thinThick;
    }


    public Long getGenerationId() {
        return generationId;
    }


    public void setGenerationId(Long generationId) {
        this.generationId = generationId;
    }


    public String getDeprecated() {
        return deprecated;
    }


    public void setDeprecated(String deprecated) {
        this.deprecated = deprecated;
    }

    public Boolean getOmCreated() {
        return omCreated;
    }

    public void setOmCreated(Boolean omCreated) {
        this.omCreated = omCreated;
    }

    public String getPolicyId() {
        return policyId;
    }

    public void setPolicyId(String policyId) {
        this.policyId = policyId;
    }

    public String getSmartQosId() {
        return smartQosId;
    }

    public void setSmartQosId(String smartQosId) {
        this.smartQosId = smartQosId;
    }

    public Boolean getIsStorageMedium() {
        return isStorageMedium;
    }

    public void setIsStorageMedium(Boolean isStorageMedium) {
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
