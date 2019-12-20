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

public class StorageContainerInfo {

    float capacity_rate;

    long free_capacity;

    long total_capacity;

    String storage_array_name;

    String storage_array_ip;

    public float getCapacity_rate() {
        return capacity_rate;
    }

    public void setCapacity_rate(float capacity_rate) {
        this.capacity_rate = capacity_rate;
    }

    public long getFree_capacity() {
        return free_capacity;
    }

    public void setFree_capacity(long free_capacity) {
        this.free_capacity = free_capacity;
    }

    public long getTotal_capacity() {
        return total_capacity;
    }

    public void setTotal_capacity(long total_capacity) {
        this.total_capacity = total_capacity;
    }

    public String getStorage_array_name() {
        return storage_array_name;
    }

    public void setStorage_array_name(String storage_array_name) {
        this.storage_array_name = storage_array_name;
    }

    public String getStorage_array_ip() {
        return storage_array_ip;
    }

    public void setStorage_array_ip(String storage_array_ip) {
        this.storage_array_ip = storage_array_ip;
    }

    @Override
    public String toString() {
        return "StorageContainerInfo [capacity_rate=" + capacity_rate + ", free_capacity=" + free_capacity
                + ", total_capacity=" + total_capacity + ", storage_array_name=" + storage_array_name
                + ", storage_array_ip=" + storage_array_ip + "]";
    }

}
