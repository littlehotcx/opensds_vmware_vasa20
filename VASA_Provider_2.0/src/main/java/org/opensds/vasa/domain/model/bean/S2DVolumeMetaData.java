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

public class S2DVolumeMetaData {
    private String isVvol;

    private String creationWay;

    private String virtualPoolId;

    private String storageArrayId;

    private String rawId;

    private String rawCopyId;

    private String sizeInMB;

    private String displayName;

    private String affinityEnabled;

    private String affinityParams;

    public String getAffinityEnabled() {
        return affinityEnabled;
    }

    public void setAffinityEnabled(String affinityEnabled) {
        this.affinityEnabled = affinityEnabled;
    }

    public String getAffinityParams() {
        return affinityParams;
    }

    public void setAffinityParams(String affinityParams) {
        this.affinityParams = affinityParams;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getSizeInMB() {
        return sizeInMB;
    }

    public void setSizeInMB(String sizeInMB) {
        this.sizeInMB = sizeInMB;
    }

    public String getRawCopyId() {
        return rawCopyId;
    }

    public void setRawCopyId(String rawCopyId) {
        this.rawCopyId = rawCopyId;
    }

    public String getIsVvol() {
        return isVvol;
    }

    public void setIsVvol(String isVvol) {
        this.isVvol = isVvol;
    }

    public String getCreationWay() {
        return creationWay;
    }

    public void setCreationWay(String creationWay) {
        this.creationWay = creationWay;
    }

    public String getVirtualPoolId() {
        return virtualPoolId;
    }

    public void setVirtualPoolId(String virtualPoolId) {
        this.virtualPoolId = virtualPoolId;
    }

    public String getStorageArrayId() {
        return storageArrayId;
    }

    public void setStorageArrayId(String storageArrayId) {
        this.storageArrayId = storageArrayId;
    }

    public String getRawId() {
        return rawId;
    }

    public void setRawId(String rawId) {
        this.rawId = rawId;
    }
}
