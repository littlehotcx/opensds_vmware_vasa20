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

import java.util.Date;

import org.opensds.platform.common.exception.SDKException;

public interface IDeviceConnection
{
    /**
     * 获取连接附加信息
     * 
     * @param key
     * @return
     */
    Object getAdditionalData(String key);
    
    /**
     * 设置连接附加信息
     * 
     * @param key
     * @param data
     */
    void setAdditionalData(String key, Object data);
    
    /**
     * 获取设备服务代理的访问类
     * @param itfs 需要获取的接口类
     * 
     * @return
     * @throws SDKException 
     */
    Object getServiceProxy(Class<?>[] itfs)
        throws SDKException;
    
    /**
     * 进行设备心跳连接
     * 
     * @return 心跳调用失败则返回false
     */
    boolean doHeartbeat(String connId);
    
    /**
     * 初始化设备连接
     * @return 
     */
    boolean initConn(String connId);
    
    /**
     * 断开设备连接
     * @throws SDKException 
     */
    void destroyConn(String connId);
    
    /**
    * 保活次数
    * 
    * @return
    */
    int getKeepAliveTimes();
    
    /**
     * 保活需要的时间间隔
     * 
     * @return
     */
    int getKeepAlivePeriod();
    
    
     /** 
     * 保活定时任务开始时间
     * 
     * 由业务自行定义开始时间。默认为new Date()
     * @return
     */
    Date getStartTime();
    
    boolean isLocalAuth();
}
