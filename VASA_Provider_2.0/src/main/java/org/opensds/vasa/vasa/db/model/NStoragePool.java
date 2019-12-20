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

public class NStoragePool extends BaseData {
    private String id;
    private String arrayId;
    private String rawPoolId;
    private String name;
    private String description;
    private Long freeCapacity;
    private Long totalCapacity;
    private String raidLevel;
    private String deviceStatus;
    private String storageType;
    private Long capacityThreshold;
    private String parentId;
    private String parentName;
    private String diskType;
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    private String containerId;


    @Override
    public String toString() {
        return "NStoragePool [id=" + id + ", arrayId=" + arrayId
                + ", rawPoolId=" + rawPoolId + ", name=" + name
                + ", description=" + description + ", freeCapacity="
                + freeCapacity + ", totalCapacity=" + totalCapacity
                + ", raidLevel=" + raidLevel + ", deviceStatus=" + deviceStatus
                + ", storageType=" + storageType + ", capacityThreshold="
                + capacityThreshold + ", parentId=" + parentId
                + ", parentName=" + parentName + ", diskType=" + diskType
                + ", status=" + status + ", containerId=" + containerId
                + ", getCreatedTime()=" + getCreatedTime()
                + ", getUpdatedTime()=" + getUpdatedTime()
                + ", getDeletedTime()=" + getDeletedTime() + ", getDeleted()="
                + getDeleted() + "]";
    }

    public String getArrayId() {
        return arrayId;
    }

    public void setArrayId(String arrayId) {
        this.arrayId = arrayId;
    }

    public String getRawPoolId() {
        return rawPoolId;
    }

    public void setRawPoolId(String rawPoolId) {
        this.rawPoolId = rawPoolId;
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

    public Long getFreeCapacity() {
        return freeCapacity;
    }

    public void setFreeCapacity(Long freeCapacity) {
        this.freeCapacity = freeCapacity;
    }

    public Long getTotalCapacity() {
        return totalCapacity;
    }

    public void setTotalCapacity(Long totalCapacity) {
        this.totalCapacity = totalCapacity;
    }

    public String getRaidLevel() {
        return raidLevel;
    }

    public void setRaidLevel(String raidLevel) {
        this.raidLevel = raidLevel;
    }

    public String getDeviceStatus() {
        return deviceStatus;
    }

    public void setDeviceStatus(String deviceStatus) {
        this.deviceStatus = deviceStatus;
    }

    public String getStorageType() {
        return storageType;
    }

    public void setStorageType(String storageType) {
        this.storageType = storageType;
    }

    public Long getCapacityThreshold() {
        return capacityThreshold;
    }

    public void setCapacityThreshold(Long capacityThreshold) {
        this.capacityThreshold = capacityThreshold;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getDiskType() {
        return diskType;
    }

    public void setDiskType(String diskType) {
        this.diskType = diskType;
    }

    public String getContainerId() {
        return containerId;
    }

    public void setContainerId(String containerId) {
        this.containerId = containerId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

}
