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

package org.opensds.vasa.vasa.fusionstorageservice.storagemodel;

import java.util.List;

/**
 * Created by d00405853 on 2017/6/19.
 */
public class StorageFileSystem implements Comparable<StorageFileSystem> {

    public static final String UNKNOW = "";


    // lun标识
    private String name;// canonicalName

    // 设备上的lunId
    private String id;

    // lunHealthStatus
    private String healthStatus;

    // lunRunningStatus
    private String runningStatus;

    // 所属存储池
    private String storagePoolName;

    private String storagePoolId;

    // 容量
    private String capacity;

    private String allocCapacity;

    // 设备名称
    private String deviceName;

    // 所属设备序列号，页面的deviceId
    private String serialNumber;

    // lun类型，0为thicklun,1为thinlun
    private String allocType;

    // 设备上file System的名称
    private String deviceFsName;

    // 设备IP
    private String deviceIp;

    // 设备Id
    private String deviceId;

    // fs的UUID
    private String uuid;


    // ESXi主机上的fs的显示名称
    private String displayName;

    // lun对应的UsedBy名称
    private String usedBy;

    // lun对应UsedBy的类型
    private String usedByType;

    // lun对应UsedBy的状态
    private Boolean usedByStatus;

    private List<String> deviceSrvIpList;

    //for datastore unmount
    private String datastoreId;

    //for datastore mount
    private String localPath;
    private String remoteHost;
    private String remotePath;

    private String description;


    public StorageFileSystem() {
        // 所有成员默认为UNKNOW
        this.name = UNKNOW;
        this.id = UNKNOW;
        this.healthStatus = UNKNOW;
        this.runningStatus = UNKNOW;
        this.storagePoolName = UNKNOW;
        this.capacity = UNKNOW;
        this.deviceName = UNKNOW;
        this.serialNumber = UNKNOW;
        this.allocType = UNKNOW;
        this.deviceFsName = UNKNOW;
        this.deviceIp = UNKNOW;
        this.uuid = UNKNOW;
        this.datastoreId = UNKNOW;
        this.localPath = UNKNOW;
        this.remoteHost = UNKNOW;
        this.remotePath = UNKNOW;
    }

    public String getName() {
        return name;
    }

    public StorageFileSystem setName(String name) {
        this.name = name;
        return this;
    }

    public String getId() {
        return id;
    }

    public StorageFileSystem setId(String id) {
        this.id = id;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public StorageFileSystem setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getAllocCapacity() {
        return allocCapacity;
    }

    public StorageFileSystem setAllocCapacity(String allocCapacity) {
        this.allocCapacity = allocCapacity;
        return this;
    }

    public List<String> getDeviceSrvIpList() {
        return deviceSrvIpList;
    }

    public StorageFileSystem setDeviceSrvIpList(List<String> deviceSrvIpList) {
        this.deviceSrvIpList = deviceSrvIpList;
        return this;
    }

    public String getHealthStatus() {
        return healthStatus;
    }

    public StorageFileSystem setHealthStatus(String healthStatus) {
        this.healthStatus = healthStatus;
        return this;
    }

    public String getRunningStatus() {
        return runningStatus;
    }

    public StorageFileSystem setRunningStatus(String runningStatus) {
        this.runningStatus = runningStatus;
        return this;
    }

    public String getStoragePoolName() {
        return storagePoolName;
    }

    public StorageFileSystem setStoragePoolName(String storagePoolName) {
        this.storagePoolName = storagePoolName;
        return this;
    }

    public String getStoragePoolId() {
        return storagePoolId;
    }

    public StorageFileSystem setStoragePoolId(String storagePoolId) {
        this.storagePoolId = storagePoolId;
        return this;
    }

    public String getCapacity() {
        return capacity;
    }

    public StorageFileSystem setCapacity(String capacity) {
        this.capacity = capacity;
        return this;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public StorageFileSystem setDeviceName(String deviceName) {
        this.deviceName = deviceName;
        return this;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public StorageFileSystem setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
        return this;
    }

    public String getAllocType() {
        return allocType;
    }

    public StorageFileSystem setAllocType(String allocType) {
        this.allocType = allocType;
        return this;
    }

    public String getDeviceFsName() {
        return deviceFsName;
    }

    public StorageFileSystem setDeviceFsName(String deviceFsName) {
        this.deviceFsName = deviceFsName;
        return this;
    }

    public String getDeviceIp() {
        return deviceIp;
    }

    public StorageFileSystem setDeviceIp(String deviceIp) {
        this.deviceIp = deviceIp;
        return this;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public StorageFileSystem setDeviceId(String deviceId) {
        this.deviceId = deviceId;
        return this;
    }

    public String getUuid() {
        return uuid;
    }

    public StorageFileSystem setUuid(String uuid) {
        this.uuid = uuid;
        return this;
    }

    public String getDisplayName() {
        return displayName;
    }

    public StorageFileSystem setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public String getUsedBy() {
        return usedBy;
    }

    public StorageFileSystem setUsedBy(String usedBy) {
        this.usedBy = usedBy;
        return this;
    }

    public String getUsedByType() {
        return usedByType;
    }

    public StorageFileSystem setUsedByType(String usedByType) {
        this.usedByType = usedByType;
        return this;
    }

    public Boolean getUsedByStatus() {
        return usedByStatus;
    }

    public StorageFileSystem setUsedByStatus(Boolean usedByStatus) {
        this.usedByStatus = usedByStatus;
        return this;
    }

    public String getDatastoreId() {
        return datastoreId;
    }

    public StorageFileSystem setDatastoreId(String datastoreId) {
        this.datastoreId = datastoreId;
        return this;
    }

    public String getLocalPath() {
        return localPath;
    }

    public StorageFileSystem setLocalPath(String localPath) {
        this.localPath = localPath;
        return this;
    }

    public String getRemoteHost() {
        return remoteHost;
    }

    public StorageFileSystem setRemoteHost(String remoteHost) {
        this.remoteHost = remoteHost;
        return this;
    }

    public String getRemotePath() {
        return remotePath;
    }

    public StorageFileSystem setRemotePath(String remotePath) {
        this.remotePath = remotePath;
        return this;
    }

    @Override
    public int compareTo(StorageFileSystem other) {
        if (null == other) {
            return 1;
        }
        if (this.getDeviceIp().compareTo(other.getDeviceIp()) > 0) {
            return -1;
        }
        if (this.getDeviceIp().compareTo(other.getDeviceIp()) < 0) {
            return 1;
        }
        if (this.getDeviceIp().compareTo(other.getDeviceIp()) == 0) {
            long thisLunId = 0L;
            long otherLunId = 0L;
            try {
                thisLunId = Long.parseLong(this.getId());
                otherLunId = Long.parseLong(other.getId());
            } catch (NumberFormatException e) {

            }
            if (thisLunId > otherLunId) {
                return 1;
            } else if (thisLunId < otherLunId) {
                return -1;
            } else {
                return 0;
            }
        }
        return 1;
    }

    @Override
    public String toString() {
        return "StorageFileSystem{" +
                "name='" + name + '\'' +
                ", id='" + id + '\'' +
                ", healthStatus='" + healthStatus + '\'' +
                ", runningStatus='" + runningStatus + '\'' +
                ", storagePoolName='" + storagePoolName + '\'' +
                ", capacity='" + capacity + '\'' +
                ", deviceName='" + deviceName + '\'' +
                ", serialNumber='" + serialNumber + '\'' +
                ", allocType='" + allocType + '\'' +
                ", deviceFsName='" + deviceFsName + '\'' +
                ", deviceIp='" + deviceIp + '\'' +
                ", deviceId='" + deviceId + '\'' +
                ", uuid='" + uuid + '\'' +
                ", displayName='" + displayName + '\'' +
                ", usedBy='" + usedBy + '\'' +
                ", usedByType='" + usedByType + '\'' +
                ", usedByStatus=" + usedByStatus +
                ", deviceSrvIpList=" + deviceSrvIpList +
                ", datastoreId=" + datastoreId +
                ", localPath='" + localPath + '\'' +
                ", remoteHost='" + remoteHost + '\'' +
                ", remotePath='" + remotePath + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StorageFileSystem that = (StorageFileSystem) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        return deviceId != null ? deviceId.equals(that.deviceId) : that.deviceId == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (deviceId != null ? deviceId.hashCode() : 0);
        return result;
    }
}
