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

public class StorageContainerRestBean {
    private String name;
    private String id;
    private String description;
    private String container_type;
    private long available_capacity;
    private float capacity_rate;
    private long free_capacity;
    private long total_capacity;
    private String storage_array_name;
    private String storage_array_ip;

    public String getStorage_array_ip() {
        return storage_array_ip;
    }

    public void setStorage_array_ip(String storage_array_ip) {
        this.storage_array_ip = storage_array_ip;
    }

    public String getStorage_array_name() {
        return storage_array_name;
    }

    public void setStorage_array_name(String storage_array_name) {
        this.storage_array_name = storage_array_name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContainer_type() {
        return container_type;
    }

    public void setContainer_type(String container_type) {
        this.container_type = container_type;
    }

    public long getAvailable_capacity() {
        return available_capacity;
    }

    public void setAvailable_capacity(long available_capacity) {
        this.available_capacity = available_capacity;
    }

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

}
