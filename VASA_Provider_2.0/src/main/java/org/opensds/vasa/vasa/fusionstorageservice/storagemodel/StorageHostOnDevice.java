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
 * Created by z00389905 on 2018/5/12 in vCenter-Web-Plugin-FS.
 */
public class StorageHostOnDevice {
    private String hostName;
    private String hostId;
    private String lunAddingItemId;
    private String hostLunID;
    private String mappingViewID;

    public String getHostName() {
        return hostName;
    }

    public StorageHostOnDevice setHostName(String hostName) {
        this.hostName = hostName;
        return this;
    }

    public String getHostId() {
        return hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    public String getHostLunID() {
        return hostLunID;
    }

    public void setHostLunID(String hostLunID) {
        this.hostLunID = hostLunID;
    }

    public String getMappingViewID() {
        return mappingViewID;
    }

    public void setMappingViewID(String mappingViewID) {
        this.mappingViewID = mappingViewID;
    }


    public String getLunAddingItemId() {
        return lunAddingItemId;
    }

    public StorageHostOnDevice setLunAddingItemId(String lunAddingItemId) {
        this.lunAddingItemId = lunAddingItemId;
        return this;
    }

    @Override
    public String toString() {
        return "StorageHostOnDevice{" +
                "hostName='" + hostName + '\'' +
                ", hostId='" + hostId + '\'' +
                ", lunAddingItemId='" + lunAddingItemId + '\'' +
                ", hostLunID='" + hostLunID + '\'' +
                ", mappingViewID='" + mappingViewID + '\'' +
                '}';
    }
}
