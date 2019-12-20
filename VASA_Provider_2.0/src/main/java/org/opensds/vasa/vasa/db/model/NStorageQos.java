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

public class NStorageQos extends BaseData {

    private String id;
    private String rawQosId;
    private String name;
    private String description;
    private String status;
    private String controlType;
    private String controlPolicy;
    private Long bandWidth;
    private Long iops;
    private Long latency;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getControlType() {
        return controlType;
    }

    public void setControlType(String controlType) {
        this.controlType = controlType;
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

    public String getRawQosId() {
        return rawQosId;
    }

    public void setRawQosId(String rawQosId) {
        this.rawQosId = rawQosId;
    }

    @Override
    public String toString() {
        return "NStorageQos [id=" + id + ", rawQosId=" + rawQosId + ", name=" + name + ", description=" + description
                + ", status=" + status + ", controlType=" + controlType + ", controlPolicy=" + controlPolicy
                + ", bandWidth=" + bandWidth + ", iops=" + iops + ", latency=" + latency + "]";
    }
}
