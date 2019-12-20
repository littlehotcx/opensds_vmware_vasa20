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

/**
 * 设备处理类注册接口
 * 
 * @author j00160659
 * 
 */
public interface IDeviceClassRegister
{
    /**
     * 注册某种设备类型的设备处理类
     * 
     * @param devType
     *            设备类型
     * @param devCls
     *            设备处理类
     * @return
     */
    boolean registerDeviceClass(String devType, Class<? extends IDevice> devCls);
    
    /**
     * 注册某种设备类型的设备处理类
     * 
     * @param devType
     *            设备类型
     * @param devCls
     *            设备处理类
     * @param verStart
     *            匹配的设备版本下限
     * @param verEnd
     *            匹配的设备版本上限，实际匹配为[verStart,verEnd)半开半闭区间内的版本
     * @return
     */
    boolean registerDeviceClass(String devType, Class<? extends IDevice> devCls, String verStart, String verEnd);
}
