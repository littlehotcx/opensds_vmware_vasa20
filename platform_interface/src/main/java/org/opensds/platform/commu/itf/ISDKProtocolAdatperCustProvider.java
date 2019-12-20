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

import java.util.Map;

import org.opensds.platform.common.bean.aa.AccountInfo;
import org.opensds.platform.exception.ProtocolAdapterException;

public interface ISDKProtocolAdatperCustProvider
{
    Object preProcessReq(Object reqMessage);

    Map<String, String> getRequestHeaders();
    
    String getContent4Sending(Object reqMessage);
    
    Object preSend(Object reqMessage);
    
    String reBuildNewUrl(String url, String interfaceName);
    
    Object postSend(Object resMessage) throws ProtocolAdapterException;
    
    Object postBuildRes(Object resMessage, String resObjClass) throws ProtocolAdapterException;
    
    AccountInfo getProtocolAuthInfo(); 
    
    String getDevId();
}
