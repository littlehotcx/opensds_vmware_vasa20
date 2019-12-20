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

import java.util.List;

public class FaultData implements Comparable<FaultData> {
    private String vmName;

    private String vmId;

    private String vvolId;

    private String vvolName;

    private String vvolType;

    private String vvolSize;

    private String sourceType;

    private String vvolCreateTime;

    private String vvolCreateBy;

    private String vvolWWN;

    private String vvolContainerId;

    private String containerName;

    private String vvolProfileId;

    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    private List<FaultDataMetaData> metaDatas;


    public String getVvolSize() {
        return vvolSize;
    }

    public void setVvolSize(String vvolSize) {
        this.vvolSize = vvolSize;
    }

    public String getVvolCreateTime() {
        return vvolCreateTime;
    }

    public void setVvolCreateTime(String vvolCreateTime) {
        this.vvolCreateTime = vvolCreateTime;
    }

    public String getVvolCreateBy() {
        return vvolCreateBy;
    }

    public void setVvolCreateBy(String vvolCreateBy) {
        this.vvolCreateBy = vvolCreateBy;
    }

    public String getVvolWWN() {
        return vvolWWN;
    }

    public void setVvolWWN(String vvolWWN) {
        this.vvolWWN = vvolWWN;
    }

    public String getVvolId() {
        return vvolId;
    }

    public void setVvolId(String vvolId) {
        this.vvolId = vvolId;
    }

    public String getVvolContainerId() {
        return vvolContainerId;
    }

    public void setVvolContainerId(String vvolContainerId) {
        this.vvolContainerId = vvolContainerId;
    }

    public String getVvolProfileId() {
        return vvolProfileId;
    }

    public void setVvolProfileId(String vvolProfileId) {
        this.vvolProfileId = vvolProfileId;
    }

    public List<FaultDataMetaData> getMetaDatas() {
        return metaDatas;
    }

    public void setMetaDatas(List<FaultDataMetaData> metaDatas) {
        this.metaDatas = metaDatas;
    }

    public String getVvolType() {
        return vvolType;
    }

    public void setVvolType(String vvolType) {
        this.vvolType = vvolType;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getVmName() {
        return vmName;
    }

    public void setVmName(String vmName) {
        this.vmName = vmName;
    }

    public String getVmId() {
        return vmId;
    }

    public void setVmId(String vmId) {
        this.vmId = vmId;
    }

    public String getVvolName() {
        return vvolName;
    }

    public void setVvolName(String vvolName) {
        this.vvolName = vvolName;
    }

    public String getContainerName() {
        return containerName;
    }

    public void setContainerName(String containerName) {
        this.containerName = containerName;
    }

    @Override
    public int compareTo(FaultData faultData) {
        if (this.vmId != null && faultData.getVmId() != null) {
            return this.vmId.compareTo(faultData.getVmId());
        } else if (this.vmId != null && faultData.getVmId() == null) {
            return 1;
        } else {
            return -1;
        }
    }

    @Override
    public String toString() {
        return "FaultData [vmName=" + vmName + ", vmId=" + vmId + ", vvolId=" + vvolId + ", vvolName=" + vvolName
                + ", vvolType=" + vvolType + ", vvolSize=" + vvolSize + ", sourceType=" + sourceType
                + ", vvolCreateTime=" + vvolCreateTime + ", vvolCreateBy=" + vvolCreateBy + ", vvolWWN=" + vvolWWN
                + ", vvolContainerId=" + vvolContainerId + ", containerName=" + containerName + ", vvolProfileId="
                + vvolProfileId + ", status=" + status + ", metaDatas=" + metaDatas + "]";
    }

}
