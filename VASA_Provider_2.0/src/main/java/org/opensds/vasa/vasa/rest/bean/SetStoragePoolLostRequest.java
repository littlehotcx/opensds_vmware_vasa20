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

package org.opensds.vasa.vasa.rest.bean;

public class SetStoragePoolLostRequest {
    private String arrayId;
    private String containerId;
    private String poolIds;

    public String getArrayId() {
        return arrayId;
    }

    public void setArrayId(String arrayId) {
        this.arrayId = arrayId;
    }

    public String getContainerId() {
        return containerId;
    }

    public void setContainerId(String containerId) {
        this.containerId = containerId;
    }

    public String getPoolIds() {
        return poolIds;
    }

    public void setPoolIds(String poolIds) {
        this.poolIds = poolIds;
    }

    @Override
    public String toString() {
        return "SetStoragePoolLostReqBean [arrayId=" + arrayId + ", containerId="
                + containerId + ", poolIds=" + poolIds + "]";
    }
}
