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

/**
 * Created by z00389905 on 2018/5/10 in vCenter-Web-Plugin-FS.
 */
public class StorageNasIP {
    private String name;
    private String address;

    public String getName() {
        return name;
    }

    public StorageNasIP setName(String name) {
        this.name = name;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public StorageNasIP setAddress(String address) {
        this.address = address;
        return this;
    }

}
