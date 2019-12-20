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

package org.opensds.platform.nemgr.itf;

import org.opensds.platform.authorize.itf.IAuthorizePolicy;

/**
 * 设备管理接口类型
 * 
 * @author j00160659
 * 
 */
public interface IDevice
{
    /**
     * 初始化设备连接
     * 
     * @param connId
     *            连接标识
     * @param sap
     *            设备访问点
     * @param loginUser
     *            登录用户名
     * @param loginPwd
     *            登录密码
     * @return
     */
    IDeviceConnection createConnection(String connId, String sap, String loginUser, String loginPwd);
    
    /**
     * 通过connId获取connection
     * 
     * @param key
     * @return
     */
    IDeviceConnection getConnById(String key);
    
    /**
     * 获取connid
     * 
     * @return
     */
    String getConnIdFromContext();
    
    /**
     * 将connection放入内存
     * 
     * @param key
     * @param value
     */
    void addId2ConnMap(String key, IDeviceConnection value);
    
    /**
     * 移除connection
     * 
     * @param key
     * @return
     */
    boolean removeConnId(String key);
    
    /**
     * 释放所有连接
     * * @return
     */
    Boolean releaseConns();
    
    /**
     * 设置鉴权策略
     * @param authorizePolicy 鉴权策略
     * @since eSDK Platform V001R003C10
     */
    void setAccountPolicy(IAuthorizePolicy authorizePolicy);
    
    void prepareAuthInfo(String user, String pwd);
    
    void setDeviceId(String deviceId);
}
