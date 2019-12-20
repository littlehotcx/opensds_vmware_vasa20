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

package org.opensds.vasa.vasa20.device.array.add;

public class StorageResponseData {
    @Override
    public String toString() {
        return "StorageResponseData [accountstate=" + accountstate + ", deviceid=" + deviceid + ", iBaseToken="
                + iBaseToken + ", lastloginip=" + lastloginip + ", lastlogintime=" + lastlogintime + ", level=" + level
                + ", pwdchangetime=" + pwdchangetime + ", usergroup=" + usergroup + ", roleId=" + roleId + ", userid="
                + userid + ", username=" + username + ", userscope=" + userscope + "]";
    }

    private int accountstate;
    private String deviceid;
    private String iBaseToken;
    private String lastloginip;
    private String lastlogintime;
    private int level;
    private long pwdchangetime;
    private String usergroup;
    private String roleId;
    private String userid;
    private String username;
    private String userscope;

    public int getAccountstate() {
        return accountstate;
    }

    public void setAccountstate(int accountstate) {
        this.accountstate = accountstate;
    }

    public String getDeviceid() {
        return deviceid;
    }

    public void setDeviceid(String deviceid) {
        this.deviceid = deviceid;
    }

    public String getiBaseToken() {
        return iBaseToken;
    }

    public void setiBaseToken(String iBaseToken) {
        this.iBaseToken = iBaseToken;
    }

    public String getLastloginip() {
        return lastloginip;
    }

    public void setLastloginip(String lastloginip) {
        this.lastloginip = lastloginip;
    }

    public String getLastlogintime() {
        return lastlogintime;
    }

    public void setLastlogintime(String lastlogintime) {
        this.lastlogintime = lastlogintime;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public long getPwdchangetime() {
        return pwdchangetime;
    }

    public void setPwdchangetime(long pwdchangetime) {
        this.pwdchangetime = pwdchangetime;
    }

    public String getUsergroup() {
        return usergroup;
    }

    public void setUsergroup(String usergroup) {
        this.usergroup = usergroup;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserscope() {
        return userscope;
    }

    public void setUserscope(String userscope) {
        this.userscope = userscope;
    }

}
