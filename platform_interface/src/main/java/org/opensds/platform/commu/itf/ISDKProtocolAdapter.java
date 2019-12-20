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

import org.opensds.platform.exception.ProtocolAdapterException;

public interface ISDKProtocolAdapter
{
    String getServiceAccessPoint();
    
    Object syncSendMessage(Object reqMessage, String serviceApiName,
            String resObjClass) throws ProtocolAdapterException;

    String syncSendMessage(String messageContent, String serviceApiName)
            throws ProtocolAdapterException;

    boolean heartBeat() throws ProtocolAdapterException;

    int login(String userName, String pwd) throws ProtocolAdapterException;

    int logout() throws ProtocolAdapterException;
    
    ISDKProtocolAdatperCustProvider getSdkProtocolAdatperCustProvider();

    void setSdkProtocolAdatperCustProvider(
            ISDKProtocolAdatperCustProvider sdkProtocolAdatperCustProvider);
}
