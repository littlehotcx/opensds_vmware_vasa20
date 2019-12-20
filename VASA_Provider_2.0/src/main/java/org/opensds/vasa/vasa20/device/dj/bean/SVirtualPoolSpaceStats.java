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

public class SVirtualPoolSpaceStats {
    private String id;

    private long total_capacity;

    private long free_capacity;

    private long available_capacity;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getTotal_capacity() {
        return total_capacity;
    }

    public void setTotal_capacity(long total_capacity) {
        this.total_capacity = total_capacity;
    }

    public long getFree_capacity() {
        return free_capacity;
    }

    public void setFree_capacity(long free_capacity) {
        this.free_capacity = free_capacity;
    }

    public long getAvailable_capacity() {
        return available_capacity;
    }

    public void setAvailable_capacity(long available_capacity) {
        this.available_capacity = available_capacity;
    }

}
