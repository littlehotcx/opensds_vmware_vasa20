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

public class NStorageCapabilityQos extends BaseData {

    private String id;
    private String name;
    private String qosControlType;
    private String qosControlPolicy;
    private Long bandwidth;
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

    public String getQosControlType() {
        return qosControlType;
    }

    public void setQosControlType(String qosControlType) {
        this.qosControlType = qosControlType;
    }

    public String getQosControlPolicy() {
        return qosControlPolicy;
    }

    public void setQosControlPolicy(String qosControlPolicy) {
        this.qosControlPolicy = qosControlPolicy;
    }

    public Long getBandwidth() {
        return bandwidth;
    }

    public void setBandwidth(Long bandwidth) {
        this.bandwidth = bandwidth;
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

    @Override
    public String toString() {
        return "NStorageCapabilityQos [id=" + id + ", name=" + name + ", qosControlType=" + qosControlType
                + ", qosControlPolicy=" + qosControlPolicy + ", bandwidth=" + bandwidth + ", iops=" + iops
                + ", latency=" + latency + "]";
    }


}
