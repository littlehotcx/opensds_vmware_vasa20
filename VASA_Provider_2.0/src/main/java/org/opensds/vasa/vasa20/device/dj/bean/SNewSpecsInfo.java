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

package org.opensds.vasa.vasa20.device.dj.bean;

public class SNewSpecsInfo {
    private String IOType;

    private String minIOPS;

    private String maxIOPS;

    private String minBandWidth;

    private String maxBandWidth;

    private String latency;


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        return sb.append("[IOType:").append(IOType).append(", minIOPS:").append(minIOPS).append(", maxIOPS:").append(maxIOPS)
                .append(", minBandWidth:").append(minBandWidth).append(", maxBandWidth:").append(maxBandWidth)
                .append(", latency:").append(latency).append("]").toString();
    }


    public String getIOType() {
        return IOType;
    }

    public void setIOType(String iOType) {
        IOType = iOType;
    }

    public String getMinBandWidth() {
        return minBandWidth;
    }

    public void setMinBandWidth(String minBandWidth) {
        this.minBandWidth = minBandWidth;
    }

    public String getMaxBandWidth() {
        return maxBandWidth;
    }

    public void setMaxBandWidth(String maxBandWidth) {
        this.maxBandWidth = maxBandWidth;
    }

    public String getLatency() {
        return latency;
    }

    public void setLatency(String latency) {
        this.latency = latency;
    }

    public String getMinIOPS() {
        return minIOPS;
    }

    public void setMinIOPS(String minIOPS) {
        this.minIOPS = minIOPS;
    }

    public String getMaxIOPS() {
        return maxIOPS;
    }

    public void setMaxIOPS(String maxIOPS) {
        this.maxIOPS = maxIOPS;
    }

}
