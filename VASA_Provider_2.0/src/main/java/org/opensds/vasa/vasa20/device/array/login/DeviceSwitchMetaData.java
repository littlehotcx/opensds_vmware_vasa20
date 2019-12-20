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

package org.opensds.vasa.vasa20.device.array.login;

public class DeviceSwitchMetaData {

    private String ip;
    private int port;
    private String uname;
    private String upass;
    private boolean avilable;

    public DeviceSwitchMetaData(String ip, int port, String uname, String upass) {
        this.ip = ip;
        this.port = port;
        this.uname = uname;
        this.upass = upass;
        this.avilable = false;
    }

    public DeviceSwitchMetaData(String ip, String uname, String upass) {
        this.ip = ip;
        this.port = 8088;
        this.uname = uname;
        this.upass = upass;
        this.avilable = false;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean isAvilable() {
        return avilable;
    }

    public void setAvilable(boolean avilable) {
        this.avilable = avilable;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getUpass() {
        return upass;
    }

    public void setUpass(String upass) {
        this.upass = upass;
    }

    // rebuild new url
    public String getServerUrl() {
        return "https://" + getIp() + ":" + getPort() + "/";
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ConnectMetaData [ip=").append(ip).append(", port=").append(port).append(", uname=")
                .append(uname).append(", upass=******").append(", avilable=").append(avilable).append("]");
        return builder.toString();
    }


}
