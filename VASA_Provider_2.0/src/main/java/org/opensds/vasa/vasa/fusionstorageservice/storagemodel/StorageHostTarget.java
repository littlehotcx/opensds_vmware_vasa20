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
package org.opensds.vasa.vasa.fusionstorageservice.storagemodel;

public class StorageHostTarget {

    private String targetType;
    private String name;
    private String addr;
    private String port;
    private String fcfMac = "";
    private String vnMac = "";
    private String vlanId = "";

    public String getTargetType() {
        return targetType;
    }

    public StorageHostTarget setTargetType(String targetType) {
        this.targetType = targetType;
        return this;
    }

    public String getName() {
        return name;
    }

    public StorageHostTarget setName(String name) {
        this.name = name;
        return this;
    }

    public String getAddr() {
        return addr;
    }

    public StorageHostTarget setAddr(String addr) {
        this.addr = addr;
        return this;
    }

    public String getPort() {
        return port;
    }

    public StorageHostTarget setPort(String port) {
        this.port = port;
        return this;
    }

    public String getFcfMac() {
        return fcfMac;
    }

    public StorageHostTarget setFcfMac(String fcfMac) {
        this.fcfMac = fcfMac;
        return this;
    }

    public String getVnMac() {
        return vnMac;
    }

    public StorageHostTarget setVnMac(String vnMac) {
        this.vnMac = vnMac;
        return this;
    }

    public String getVlanId() {
        return vlanId;
    }

    public StorageHostTarget setVlanId(String vlanId) {
        this.vlanId = vlanId;
        return this;
    }
}
