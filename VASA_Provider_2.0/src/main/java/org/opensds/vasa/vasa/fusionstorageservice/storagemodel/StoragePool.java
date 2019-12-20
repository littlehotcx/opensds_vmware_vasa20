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

/**
 * StoragePool模型
 *
 * @author XXX
 * @see [相关类/方法]
 */
public class StoragePool implements Comparable<StoragePool> {

    /**
     * UNKNOW
     */
    public static final String UNKNOW = "";

    /**
     * 0
     */
    public static final String ZERO = "0";

    /**
     * 生成HashCode的奇数
     */
    private static final int HASH_CODE_PRI = 31;

    private String id;

    private String name;

    private String parentType;

    private String parentId;

    private String parentName;

    private String healthStatus;

    private String runningStatus;

    private String description;

    private String workNodeId;

    private String totalCapacity;

    private String freeCapacity;

    private String availableCapacity;

    private String consumedCapacity;

    private String consumedCapacityPercentage;

    private String consumedCapacityThreshold;

    private String hotspareTotalCapacity;

    private String hotspareConsumedCapacity;

    private String hotspareConsumedCapacityPercentage;

    private String rawCapacity;

    private String replicationCapacity;

    private String sectorSize;


    private String usageType;

    public StoragePool() {
        super();
        this.id = UNKNOW;
        this.name = UNKNOW;
        this.parentType = UNKNOW;
        this.parentId = UNKNOW;
        this.parentName = UNKNOW;
        this.healthStatus = UNKNOW;
        this.runningStatus = UNKNOW;
        this.description = UNKNOW;
        this.workNodeId = UNKNOW;
        this.totalCapacity = ZERO;
        this.freeCapacity = ZERO;
        this.availableCapacity = ZERO;
        this.consumedCapacity = ZERO;
        this.consumedCapacityPercentage = ZERO;
        this.consumedCapacityThreshold = ZERO;
        this.hotspareTotalCapacity = ZERO;
        this.hotspareConsumedCapacity = ZERO;
        this.hotspareConsumedCapacityPercentage = ZERO;
        this.rawCapacity = ZERO;
        this.replicationCapacity = ZERO;
        this.sectorSize = ZERO;
    }


    public String getUsageType() {
        return usageType;
    }

    public StoragePool setUsageType(String usageType) {
        this.usageType = usageType;
        return this;
    }

    public String getId() {
        return id;
    }

    public StoragePool setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public StoragePool setName(String name) {
        this.name = name;
        return this;
    }

    public String getParentType() {
        return parentType;
    }

    public StoragePool setParentType(String parentType) {
        this.parentType = parentType;
        return this;
    }

    public String getParentId() {
        return parentId;
    }

    public StoragePool setParentId(String parentId) {
        this.parentId = parentId;
        return this;
    }

    public String getParentName() {
        return parentName;
    }

    public StoragePool setParentName(String parentName) {
        this.parentName = parentName;
        return this;
    }

    public String getHealthStatus() {
        return healthStatus;
    }

    public StoragePool setHealthStatus(String healthStatus) {
        this.healthStatus = healthStatus;
        return this;
    }

    public String getRunningStatus() {
        return runningStatus;
    }

    public StoragePool setRunningStatus(String runningStatus) {
        this.runningStatus = runningStatus;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public StoragePool setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getWorkNodeId() {
        return workNodeId;
    }

    public StoragePool setWorkNodeId(String workNodeId) {
        this.workNodeId = workNodeId;
        return this;
    }

    public String getTotalCapacity() {
        return totalCapacity;
    }

    public StoragePool setTotalCapacity(String totalCapacity) {
        this.totalCapacity = totalCapacity;
        return this;
    }

    public String getFreeCapacity() {
        return freeCapacity;
    }

    public StoragePool setFreeCapacity(String freeCapacity) {
        this.freeCapacity = freeCapacity;
        return this;
    }

    public String getAvailableCapacity() {
        return availableCapacity;
    }

    public StoragePool setAvailableCapacity(String availableCapacity) {
        this.availableCapacity = availableCapacity;
        return this;
    }

    public String getConsumedCapacity() {
        return consumedCapacity;
    }

    public StoragePool setConsumedCapacity(String consumedCapacity) {
        this.consumedCapacity = consumedCapacity;
        return this;
    }

    public String getConsumedCapacityPercentage() {
        return consumedCapacityPercentage;
    }

    public StoragePool setConsumedCapacityPercentage(String consumedCapacityPercentage) {
        this.consumedCapacityPercentage = consumedCapacityPercentage;
        return this;
    }

    public String getConsumedCapacityThreshold() {
        return consumedCapacityThreshold;
    }

    public StoragePool setConsumedCapacityThreshold(String consumedCapacityThreshold) {
        this.consumedCapacityThreshold = consumedCapacityThreshold;
        return this;
    }

    public String getHotspareTotalCapacity() {
        return hotspareTotalCapacity;
    }

    public StoragePool setHotspareTotalCapacity(String hotspareTotalCapacity) {
        this.hotspareTotalCapacity = hotspareTotalCapacity;
        return this;
    }

    public String getHotspareConsumedCapacity() {
        return hotspareConsumedCapacity;
    }

    public StoragePool setHotspareConsumedCapacity(String hotspareConsumedCapacity) {
        this.hotspareConsumedCapacity = hotspareConsumedCapacity;
        return this;
    }

    public String getHotspareConsumedCapacityPercentage() {
        return hotspareConsumedCapacityPercentage;
    }

    public StoragePool setHotspareConsumedCapacityPercentage(String hotspareConsumedCapacityPercentage) {
        this.hotspareConsumedCapacityPercentage = hotspareConsumedCapacityPercentage;
        return this;
    }

    public String getRawCapacity() {
        return rawCapacity;
    }

    public StoragePool setRawCapacity(String rawCapacity) {
        this.rawCapacity = rawCapacity;
        return this;
    }

    public String getReplicationCapacity() {
        return replicationCapacity;
    }

    public StoragePool setReplicationCapacity(String replicationCapacity) {
        this.replicationCapacity = replicationCapacity;
        return this;
    }

    public String getSectorSize() {
        return sectorSize;
    }

    public StoragePool setSectorSize(String sectorSize) {
        this.sectorSize = sectorSize;
        return this;
    }

    /**
     * 重写compareTo
     *
     * @param other StoragePoolInterface
     * @return int 大于返回1，小于返回 -1，相等返回0
     */
    @Override
    public int compareTo(StoragePool other) {
        if (null == other) {
            return 1;
        }
        if (this.getName().compareTo(other.getName()) > 0) {
            return 1;
        }
        if (this.getName().compareTo(other.getName()) < 0) {
            return -1;
        }
        if (this.getName().compareTo(other.getName()) == 0) {
            String thisPoolId = "";
            String otherPoolId = "";
            try {
                thisPoolId = this.id;
                otherPoolId = other.id;
            } catch (NumberFormatException e) {
                return 0;
            }
            if (thisPoolId.equals(otherPoolId)) {
                return 0;
            } else {
                return 1;
            }
        }

        return 1;
    }

    /**
     * 重写HashCode
     *
     * @return hashCode
     */
    @Override
    public int hashCode() {
        final int prime = HASH_CODE_PRI;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((parentId == null) ? 0 : parentId.hashCode());
        return result;
    }

    /**
     * 重写equals方法
     *
     * @param obj 被比较的对象
     * @return true为想等, false为不等
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        StoragePool other = (StoragePool) obj;
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        if (parentId == null) {
            if (other.parentId != null) {
                return false;
            }
        } else if (!parentId.equals(other.parentId)) {
            return false;
        }
        return true;
    }

}
