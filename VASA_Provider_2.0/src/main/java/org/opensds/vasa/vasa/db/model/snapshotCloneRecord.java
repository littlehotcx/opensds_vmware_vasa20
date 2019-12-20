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

/**
 * 功能描述
 *
 * @author h00451513
 * @since 2019-03-23
 */
public class snapshotCloneRecord {

    private int id;

    private String vmId;

    private int diskCount;

    private int diskRemain;

    private String operationType;

    private Date startTime;

    private Date endTime;

    private String inputName;


    @Override
    public String toString() {
        return "snapshotCloneRecord{" +
                "id=" + id +
                ", vmId='" + vmId + '\'' +
                ", diskCount=" + diskCount +
                ", diskRemain=" + diskRemain +
                ", operationType='" + operationType + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", inputName='" + inputName + '\'' +
                '}';
    }

    public String getInputName() {
        return inputName;
    }

    public void setInputName(String inputName) {
        this.inputName = inputName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getVmId() {
        return vmId;
    }

    public void setVmId(String vmId) {
        this.vmId = vmId;
    }

    public int getDiskCount() {
        return diskCount;
    }

    public void setDiskCount(int diskCount) {
        this.diskCount = diskCount;
    }

    public int getDiskRemain() {
        return diskRemain;
    }

    public void setDiskRemain(int diskRemain) {
        this.diskRemain = diskRemain;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
}


