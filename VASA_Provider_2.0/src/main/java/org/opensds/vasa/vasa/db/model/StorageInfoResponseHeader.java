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

public class StorageInfoResponseHeader {
    private String id;

    private String name;

    private String vendor;

    private String sn;

    private String model;

    private String product_version;

    private String ip;

    private String ips;

    private String conn_ip;

    private int port;

    private String device_status;

    private String username;

    private String registered;

    private String supported_vasa_profiles;

    private int deviceType;

    public int getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }

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

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getProduct_version() {
        return product_version;
    }

    public void setProduct_version(String product_version) {
        this.product_version = product_version;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getIps() {
        return ips;
    }

    public void setIps(String ips) {
        this.ips = ips;
    }

    public String getConn_ip() {
        return conn_ip;
    }

    public void setConn_ip(String conn_ip) {
        this.conn_ip = conn_ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getDevice_status() {
        return device_status;
    }

    public void setDevice_status(String device_status) {
        this.device_status = device_status;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRegistered() {
        return registered;
    }

    public void setRegistered(String registered) {
        this.registered = registered;
    }

    public String getSupported_vasa_profiles() {
        return supported_vasa_profiles;
    }

    public void setSupported_vasa_profiles(String supported_vasa_profiles) {
        this.supported_vasa_profiles = supported_vasa_profiles;
    }

}
