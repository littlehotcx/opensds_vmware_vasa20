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

import java.sql.Timestamp;

public class StorageInfo {

    private String id;
    private String ip;
    private String ips;
    private int port;
    private String username;
    private String password;
    private String devicestatus;
    private String model;
    private int deviceType;
    private String storagename;
    private String productversion;
    private String registered;
    private String sn;
    private String vendor;
    private String supportvvol;
    private String arrayUTCTime;
    private Timestamp createtime;
    private Timestamp updatetime;
    private Timestamp deletetime;
    private int priority;
    private int deleted;
    private String vvolsupportProfile;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDevicestatus() {
        return devicestatus;
    }

    public void setDevicestatus(String devicestatus) {
        this.devicestatus = devicestatus;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getStoragename() {
        return storagename;
    }

    public void setStoragename(String storagename) {
        this.storagename = storagename;
    }

    public String getProductversion() {
        return productversion;
    }

    public void setProductversion(String productversion) {
        this.productversion = productversion;
    }

    public String getRegistered() {
        return registered;
    }

    public void setRegistered(String registered) {
        this.registered = registered;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getSupportvvol() {
        return supportvvol;
    }

    public void setSupportvvol(String supportvvol) {
        this.supportvvol = supportvvol;
    }

    public Timestamp getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Timestamp createtime) {
        this.createtime = createtime;
    }

    public Timestamp getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(Timestamp updatetime) {
        this.updatetime = updatetime;
    }

    public Timestamp getDeletetime() {
        return deletetime;
    }

    public void setDeletetime(Timestamp deletetime) {
        this.deletetime = deletetime;
    }

    public int getDeleted() {
        return deleted;
    }

    public void setDeleted(int deleted) {
        this.deleted = deleted;
    }

    public String getVvolsupportProfile() {
        return vvolsupportProfile;
    }

    public void setVvolsupportProfile(String vvolsupportProfile) {
        this.vvolsupportProfile = vvolsupportProfile;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Override
    public String toString() {
        return "StorageInfo [id=" + id + ", ip=" + ip + ", ips=" + ips + ", port=" + port + ", username=" + username
                + ", password=*******" + ", devicestatus=" + devicestatus + ", model=" + model + ", storagename="
                + storagename + ", productversion=" + productversion + ", registered=" + registered + ", sn=" + sn
                + ", vendor=" + vendor + ", supportvvol=" + supportvvol + ", arrayUTCTime=" + arrayUTCTime
                + ", createtime=" + createtime + ", updatetime=" + updatetime + ", deletetime=" + deletetime
                + ", priority=" + priority + ", deleted=" + deleted + ", vvolsupportProfile=" + vvolsupportProfile
                + "]";
    }

    public String getArrayUTCTime() {
        return arrayUTCTime;
    }

    public void setArrayUTCTime(String arrayUTCTime) {
        this.arrayUTCTime = arrayUTCTime;
    }

    public int getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }

}
