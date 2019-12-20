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

public class S2DExtraSpecsInfo {
    private String thin;

    private String vvol_creation;

    private String virtual_pool_id;

    private String IOType;

    private boolean SmartTier;

    private boolean QosSupport;

    private long minIOPS;

    private long maxIOPS;

    private long minBandWidth;

    private long maxBandWidth;

    private long minLatency;

    private long maxLatency;


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        return sb.append("[thin:").append(thin).append(", vvol_creation:").append(vvol_creation).append(", virtual_pool_id:").append(virtual_pool_id)
                .append(", IOType:").append(IOType).append(", minIOPS:").append(minIOPS).append(", maxIOPS:").append(maxIOPS).append(", minBandWidth:")
                .append(minBandWidth).append(", maxBandWidth:").append(maxBandWidth).append(", minLatency:").append(minLatency)
                .append(", maxLatency:").append(maxLatency).append(", SmartTierSupport:").append(SmartTier).append(", QosSupport:").append(QosSupport)
                .append("]").toString();
    }

    public String getThin() {
        return thin;
    }

    public void setThin(String thin) {
        this.thin = thin;
    }

    public String getVvol_creation() {
        return vvol_creation;
    }

    public void setVvol_creation(String vvol_creation) {
        this.vvol_creation = vvol_creation;
    }

    public String getVirtual_pool_id() {
        return virtual_pool_id;
    }

    public void setVirtual_pool_id(String virtual_pool_id) {
        this.virtual_pool_id = virtual_pool_id;
    }

    public String getIOType() {
        return IOType;
    }

    public void setIOType(String iOType) {
        IOType = iOType;
    }

    public boolean isSmartTierSupport() {
        return SmartTier;
    }

    public void setSmartTierSupport(boolean smartTier) {
        this.SmartTier = smartTier;
    }

    public boolean isQosSupport() {
        return QosSupport;
    }

    public void setQosSupport(boolean qosSupport) {
        this.QosSupport = qosSupport;
    }

    public long getMinIOPS() {
        return minIOPS;
    }

    public void setMinIOPS(long minIOPS) {
        this.minIOPS = minIOPS;
    }

    public long getMaxIOPS() {
        return maxIOPS;
    }

    public void setMaxIOPS(long maxIOPS) {
        this.maxIOPS = maxIOPS;
    }

    public long getMinBandWidth() {
        return minBandWidth;
    }

    public void setMinBandWidth(long minBandWidth) {
        this.minBandWidth = minBandWidth;
    }

    public long getMaxBandWidth() {
        return maxBandWidth;
    }

    public void setMaxBandWidth(long maxBandWidth) {
        this.maxBandWidth = maxBandWidth;
    }

    public long getMinLatency() {
        return minLatency;
    }

    public void setMinLatency(long minLatency) {
        this.minLatency = minLatency;
    }

    public long getMaxLatency() {
        return maxLatency;
    }

    public void setMaxLatency(long maxLatency) {
        this.maxLatency = maxLatency;
    }

}
