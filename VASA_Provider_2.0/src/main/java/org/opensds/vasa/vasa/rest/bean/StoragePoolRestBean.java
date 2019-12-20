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

public class StoragePoolRestBean {
    private String id;

    private String name;

    private String storage_array_id;

    private String storage_array_name;

    private String storage_array_ip;

    private String disk_type;

    private String raid_level;

    private Boolean tiering;

    private long total_capacity;

    private long free_capacity;

    private String status;

    private String device_status;

    private String storage_container_name;

    private int capacity_rate;

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

    public String getStorage_array_id() {
        return storage_array_id;
    }

    public void setStorage_array_id(String storage_array_id) {
        this.storage_array_id = storage_array_id;
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

    public String getDisk_type() {
        return disk_type;
    }

    public void setDisk_type(String disk_type) {
        this.disk_type = disk_type;
    }

    public String getRaid_level() {
        return raid_level;
    }

    public void setRaid_level(String raid_level) {
        this.raid_level = raid_level;
    }

    public Boolean getTiering() {
        return tiering;
    }

    public void setTiering(Boolean tiering) {
        this.tiering = tiering;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDevice_status() {
        return device_status;
    }

    public void setDevice_status(String device_status) {
        this.device_status = device_status;
    }

    public String getStorage_container_name() {
        return storage_container_name;
    }

    public void setStorage_container_name(String storage_container_name) {
        this.storage_container_name = storage_container_name;
    }

    public int getCapacity_rate() {
        return capacity_rate;
    }

    public void setCapacity_rate(int capacity_rate) {
        this.capacity_rate = capacity_rate;
    }

    @Override
    public String toString() {
        return "StoragePoolRestBean [id=" + id + ", name=" + name + ", storage_array_id=" + storage_array_id
                + ", storage_array_name=" + storage_array_name + ", storage_array_ip=" + storage_array_ip
                + ", disk_type=" + disk_type + ", raid_level="
                + raid_level + ", tiering=" + tiering + ", total_capacity=" + total_capacity + ", free_capacity="
                + free_capacity + ", status=" + status + ", device_status=" + device_status
                + ", storage_container_name=" + storage_container_name + ", capacity_rate=" + capacity_rate + "]";
    }
}
