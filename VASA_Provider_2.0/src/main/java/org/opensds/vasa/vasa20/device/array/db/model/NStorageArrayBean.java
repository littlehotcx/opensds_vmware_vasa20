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

package org.opensds.vasa.vasa20.device.array.db.model;

import java.util.Date;

public class NStorageArrayBean {
    private String arrayId;

    private String ip;

    private String ips;

    private int port;

    private String uname;

    private String upass;

    private String deviceStatus;

    private String model;

    private String storageName;

    private String productVersion;

    private String registered;

    private String sn;

    private String vendor;

    private String supportVvol;

    private Date createTime;

    private Date updateTime;

    private Date deleteTime;

    private boolean deleted;

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("NStorageArrayBean [arrayId=").append(arrayId).append(", ip=").append(ip).append(", ips=")
                .append(ips).append(", port=").append(port).append(", uname=").append(uname).append(", upass=")
                .append(upass).append(", deviceStatus=").append(deviceStatus).append(", model=").append(model)
                .append(", storageName=").append(storageName).append(", productVersion=").append(productVersion)
                .append(", registered=").append(registered).append(", sn=").append(sn).append(", vendor=")
                .append(vendor).append(", supportVvol=").append(supportVvol).append(", createTime=").append(createTime)
                .append(", updateTime=").append(updateTime).append(", deleteTime=").append(deleteTime)
                .append(", deleted=").append(deleted).append("]");
        return builder.toString();
    }

    public String getArrayId() {
        return arrayId;
    }

    public void setArrayId(String arrayId) {
        this.arrayId = arrayId;
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

    public String getDeviceStatus() {
        return deviceStatus;
    }

    public void setDeviceStatus(String deviceStatus) {
        this.deviceStatus = deviceStatus;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getStorageName() {
        return storageName;
    }

    public void setStorageName(String storageName) {
        this.storageName = storageName;
    }

    public String getProductVersion() {
        return productVersion;
    }

    public void setProductVersion(String productVersion) {
        this.productVersion = productVersion;
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

    public String getSupportVvol() {
        return supportVvol;
    }

    public void setSupportVvol(String supportVvol) {
        this.supportVvol = supportVvol;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Date getDeleteTime() {
        return deleteTime;
    }

    public void setDeleteTime(Date deleteTime) {
        this.deleteTime = deleteTime;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }


}
