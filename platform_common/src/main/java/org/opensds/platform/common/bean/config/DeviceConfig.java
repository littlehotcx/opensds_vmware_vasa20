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

package org.opensds.platform.common.bean.config;

public class DeviceConfig
{
    private boolean asDefault;
    
    private String deviceId;
    
    private String deviceName;
    
    private String connMode;
    
    private String curState;
    
    private String serviceAccessPoint;
    
    private String loginUser;
    
    private String loginPwd;
    
    private String deviceType;
    
    private String deviceVersion;
    
    private String reserver1;
    
    private String reserver2;
    
    public String getDeviceId()
    {
        return deviceId;
    }
    
    public void setDeviceId(String deviceId)
    {
        this.deviceId = deviceId;
    }
    
    public String getConnMode()
    {
        return connMode;
    }
    
    public void setConnMode(String connMode)
    {
        this.connMode = connMode;
    }
    
    public String getCurState()
    {
        return curState;
    }
    
    public void setCurState(String curState)
    {
        this.curState = curState;
    }
    
    public String getServiceAccessPoint()
    {
        return serviceAccessPoint;
    }
    
    public void setServiceAccessPoint(String serviceAccessPoint)
    {
        this.serviceAccessPoint = serviceAccessPoint;
    }
    
    public String getLoginUser()
    {
        return loginUser;
    }
    
    public void setLoginUser(String loginUser)
    {
        this.loginUser = loginUser;
    }
    
    public String getLoginPwd()
    {
        return loginPwd;
    }
    
    public void setLoginPwd(String loginPwd)
    {
        this.loginPwd = loginPwd;
    }
    
    public String getDeviceType()
    {
        return deviceType;
    }
    
    public void setDeviceType(String deviceType)
    {
        this.deviceType = deviceType;
    }
    
    public String getDeviceVersion()
    {
        return deviceVersion;
    }
    
    public void setDeviceVersion(String deviceVersion)
    {
        this.deviceVersion = deviceVersion;
    }
    
    public boolean isAsDefault()
    {
        return asDefault;
    }
    
    public void setAsDefault(boolean asDefault)
    {
        this.asDefault = asDefault;
    }
    
    public String getDeviceName()
    {
        return deviceName;
    }
    
    public void setDeviceName(String deviceName)
    {
        this.deviceName = deviceName;
    }
    
    public String getReserver1()
    {
        return reserver1;
    }
    
    public void setReserver1(String reserver1)
    {
        this.reserver1 = reserver1;
    }
    
    public String getReserver2()
    {
        return reserver2;
    }
    
    public void setReserver2(String reserver2)
    {
        this.reserver2 = reserver2;
    }
    
    @Override
    public String toString()
    {
        return "Device [asDefault=" + asDefault + ", deviceId=" + deviceId + ", deviceName=" + deviceName
            + ", connMode=" + connMode + ", curState=" + curState + ", serviceAccessPoint=" + serviceAccessPoint
            + ", loginUser=" + loginUser + ", loginPwd=******" + ", deviceType=" + deviceType + ", deviceVersion="
            + deviceVersion + ", reserver1=" + reserver1 + ", reserver2=" + reserver2 + "]";
    }
    
}
