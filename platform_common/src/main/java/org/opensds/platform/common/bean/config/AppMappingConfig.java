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

/**
 * 设备和eSDK用户的一一映射
 * @author sWX198756
 * @since  eSDK Platform V100R003C10
 */
public class AppMappingConfig
{
    /**
     * 设备ID
     */
    private String deviceId;
    
    /**
     * eSDK用户ID
     */
    private String esdkApp;
    
    /**
     * 向device鉴权的应用标识
     */
    private String deviceApp;
    
    /**
     * 向device鉴权的应用标识密码
     */
    private String deviceAppPwd;
    
    public String getDeviceId()
    {
        return deviceId;
    }
    
    public void setDeviceId(String deviceId)
    {
        this.deviceId = deviceId;
    }
    
    public String getEsdkApp()
    {
        return esdkApp;
    }
    
    public void setEsdkApp(String esdkApp)
    {
        this.esdkApp = esdkApp;
    }
    
    public String getDeviceApp()
    {
        return deviceApp;
    }
    
    public void setDeviceApp(String deviceApp)
    {
        this.deviceApp = deviceApp;
    }
    
    public String getDeviceAppPwd()
    {
        return deviceAppPwd;
    }
    
    public void setDeviceAppPwd(String deviceAppPwd)
    {
        this.deviceAppPwd = deviceAppPwd;
    }
    
    @Override
    public String toString()
    {
        return "AppMapping [deviceId=" + deviceId + ", esdkApp=" + esdkApp + ", deviceApp=" + deviceApp
            + ", deviceAppPwd=" + deviceAppPwd + "]";
    }
}
