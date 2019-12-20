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

public class FaultDataDTO {
    private String vvolSize;

    private String vvolCreateTime;

    private String vvolCreateBy;

    private String vvolWWN;

    private String vvolId;

    private String vvolContainerId;

    private String vvolProfileId;

    private String key;

    private String value;


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

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
