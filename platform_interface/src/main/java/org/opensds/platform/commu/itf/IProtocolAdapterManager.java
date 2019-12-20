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

package org.opensds.platform.commu.itf;

public interface IProtocolAdapterManager
{
    /**
     * 根据适配的协议类型及服务地址获取协议实例
     * 
     * @param protocolAdapterType 协议类型
     * @param sap 协议访问的服务地址
     * @return 协议实例
     * 
     */
    ISDKProtocolAdapter getProtocolInstanceByType(
            String protocolAdapterType, String sap);

}
