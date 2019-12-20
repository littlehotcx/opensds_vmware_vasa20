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

import java.util.List;
import java.util.Map;

import org.opensds.platform.common.bean.config.DeviceConfig;
import org.opensds.platform.common.exception.SDKException;

/**
 * 进行设备管理和设备连接
 * 
 * @author j00160659
 * 
 */
public interface IDeviceManager
{
    /**
     * 设备连接方式
     * 
     * @author j00160659
     * 
     */
    public enum DEV_CONN_MODE_TYPE
    {
        NOT_CONNECT_AUTOMATIC, // 不自动连接设备
        CONNECT_AUTOMATIC
        // 自动连接设备
    }
    
    public enum DEV_STATE_TYPE
    {
        DST_CONNECTING, // device is connecting
        DST_CONNECTED, // device has connected
        DST_DISCONNECTING, // device is disconnecting
        DST_DISCONNECTED
        // device hasn't connected
    }
    
    /**
     * 在系统中增加一个受管理的设备
     * 
     * @param devID
     *            设备ID，仅用于唯一标识设备
     * @param devType
     *            设备类型
     * @param devVer
     *            设备版本以V100R001[C01]方式表示
     * @param sap
     *            业务设备的实际访问点
     * @param user
     *            访问业务设备时需要使用的用户名
     * @param pwd
     *            访问设备时需要使用的密码
     * @param connMode
     *            设备的连接类型
     * @return 如果devID已经存在、devID/devType/sap没有提供则返回失败；否则返回成功
     */
    boolean addDevice(String devID, String devName, String devType, String devVer, String sap, String user, String pwd,
        String connMode, String reserver1, String reserver2, boolean isAsDefault);
    
    /**
     * 获取设备服务访问接口，返回支持多个接口的代理类
     * 
     * @param devID
     *            设备ID
     * @param itfs
     *            要求提供的接口列表
     * @return
     * @throws SDKException 
     */
    Object getDeviceServiceProxy(String devID, @SuppressWarnings("rawtypes") Class[] itfs)
        throws SDKException;
    
    /**
     * 获取设备服务访问接口，返回支持该接口的代理类
     * 
     * @param devID
     *            设备ID
     * @param itf
     *            要求提供的接口
     * @return 指定类型的接口的代理
     * @throws SDKException 
     */
    <T> T getDeviceServiceProxy(String devID, Class<T> itf)
        throws SDKException;
    
    <T> T getDefaultDeviceServiceProxy(Class<T> itf)
        throws SDKException;
    
    Object getDefaultDeviceServiceProxy(@SuppressWarnings("rawtypes") Class[] itfs)
        throws SDKException;
    
    void setAsDefaultDevice(String devID);
    
    /**
     * 修改设备用户信息
     * 
     * @param devID
     *            设备ID
     * @param user
     *            访问业务设备时需要使用的用户名
     * @param pwd
     *            访问设备时需要使用的密码
     * @return
     */
    boolean modifyDeviceUserInfo(String devId, String user, String pwd);
    
    /**
     * 
     * @param deviceId
     *            设备ID
     * @return
     */
    List<Map<String, Object>> queryDeviceInfo(String deviceId);
    
    boolean closeConn(String devId);
    
    boolean connect(String devId)
        throws SDKException;
    
    List<DeviceConfig> queryAllDeviceInfo();
    
    List<DeviceConfig> queryDeviceInfosByType(String devType);
    
    void refreshDevices();
    
    void releaseConn(IDeviceConnection conn);
    
    IDevice getDeviceByDeviceId(String devID);
}
