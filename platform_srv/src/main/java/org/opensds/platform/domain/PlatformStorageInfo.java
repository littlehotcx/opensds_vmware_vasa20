/*
 *
 *  * // Copyright 2019 The OpenSDS Authors.
 *  * //
 *  * // Licensed under the Apache License, Version 2.0 (the "License"); you may
 *  * // not use this file except in compliance with the License. You may obtain
 *  * // a copy of the License at
 *  * //
 *  * //     http://www.apache.org/licenses/LICENSE-2.0
 *  * //
 *  * // Unless required by applicable law or agreed to in writing, software
 *  * // distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *  * // WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *  * // License for the specific language governing permissions and limitations
 *  * // under the License.
 *  *
 *
 */

package org.opensds.platform.domain;

public class PlatformStorageInfo {
	
		private String id;
		private String ip;
	    private String ips;
		private String port;
		private String username;
		private String password;
		private String devicestatus;
	    private String model;
	    private String storagename;
	    private String productversion;
	    private String registered;
	    private String sn;
	    private String vendor;
	    private String supportvvol;
	    private String createtime;
	    private String updatetime;
	    private String deletetime;
	    private String deleted;
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
		public String getPort() {
			return port;
		}
		public void setPort(String port) {
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
		public String getCreatetime() {
			return createtime;
		}
		public void setCreatetime(String createtime) {
			this.createtime = createtime;
		}
		public String getUpdatetime() {
			return updatetime;
		}
		public void setUpdatetime(String updatetime) {
			this.updatetime = updatetime;
		}
		public String getDeletetime() {
			return deletetime;
		}
		public void setDeletetime(String deletetime) {
			this.deletetime = deletetime;
		}
		public String getDeleted() {
			return deleted;
		}
		public void setDeleted(String deleted) {
			this.deleted = deleted;
		}
		@Override
		public String toString() {
			return "StorageInfo [id=" + id + ", ip=" + ip + ", ips=" + ips + ", port=" + port + ", username=" + username
					+ ", password=******" + ", devicestatus=" + devicestatus + ", model=" + model + ", storagename="
					+ storagename + ", productversion=" + productversion + ", registered=" + registered + ", sn=" + sn
					+ ", vendor=" + vendor + ", supportvvol=" + supportvvol + ", createtime=" + createtime + ", updatetime="
					+ updatetime + ", deletetime=" + deletetime + ", deleted=" + deleted + "]";
		}

}
