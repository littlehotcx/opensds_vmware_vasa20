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

import java.util.Date;

public class NVirtualVolume {
    private String vvolid;

    private long size;

    private String createdBy;

    private Date creationTime;

    private String containerId;

    private String vvolType;

    private String sourceType;

    private String parentId;

    private String arrayId;

    private String rawId;

    private String status;

    private String lunId;

    private String wwn;

    private String rawPoolId;

    private String vmId;

    private String vmName;

    private Date deletedTime;

    @Override
    public String toString() {
        return "NVirtualVolume [vvolid=" + vvolid + ", size=" + size + ", createdBy=" + createdBy + ", creationTime="
                + creationTime + ", containerId=" + containerId + ", vvolType=" + vvolType + ", sourceType="
                + sourceType + ", parentId=" + parentId + ", arrayId=" + arrayId + ", rawId=" + rawId + ", status="
                + status + ", lunId=" + lunId + ", wwn=" + wwn + ", rawPoolId=" + rawPoolId + ", vmId=" + vmId
                + ", vmName=" + vmName + ", deletedTime=" + deletedTime + "]";
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getVvolid() {
        return vvolid;
    }

    public void setVvolid(String vvolid) {
        this.vvolid = vvolid;
    }

    public String getContainerId() {
        return containerId;
    }

    public void setContainerId(String containerId) {
        this.containerId = containerId;
    }

    public String getArrayId() {
        return arrayId;
    }

    public void setArrayId(String arrayId) {
        this.arrayId = arrayId;
    }

    public String getRawId() {
        return rawId;
    }

    public void setRawId(String rawId) {
        this.rawId = rawId;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
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

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getLunId() {
        return lunId;
    }

    public void setLunId(String lunId) {
        this.lunId = lunId;
    }

    public String getWwn() {
        return wwn;
    }

    public void setWwn(String wwn) {
        this.wwn = wwn;
    }

    public String getRawPoolId() {
        return rawPoolId;
    }

    public void setRawPoolId(String rawPoolId) {
        this.rawPoolId = rawPoolId;
    }

    public Date getDeletedTime() {
        return deletedTime;
    }

    public void setDeletedTime(Date deletedTime) {
        this.deletedTime = deletedTime;
    }

    public String getVmId() {
        return vmId;
    }

    public void setVmId(String vmId) {
        this.vmId = vmId;
    }

    public String getVmName() {
        return vmName;
    }

    public void setVmName(String vmName) {
        this.vmName = vmName;
    }

}
