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

import java.util.Date;

public class NVasaServiceCenter {

    private int id;

    private String serviceIp;

    private int servicePort;

    private String serviceIsMaster;

    private Date lastModifiedTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getServiceIp() {
        return serviceIp;
    }

    public void setServiceIp(String serviceIp) {
        this.serviceIp = serviceIp;
    }

    public int getServicePort() {
        return servicePort;
    }

    public void setServicePort(int servicePort) {
        this.servicePort = servicePort;
    }

    public String getServiceIsMaster() {
        return serviceIsMaster;
    }

    public void setServiceIsMaster(String serviceIsMaster) {
        this.serviceIsMaster = serviceIsMaster;
    }

    public Date getLastModifiedTime() {
        return lastModifiedTime;
    }

    public void setLastModifiedTime(Date lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("NVasaServiceCenter [id=").append(id).append(", serviceIp=").append(serviceIp)
                .append(", servicePort=").append(servicePort).append(", serviceIsMaster=").append(serviceIsMaster)
                .append(", lastModifiedTime=").append(lastModifiedTime).append("]");
        return builder.toString();
    }

}
