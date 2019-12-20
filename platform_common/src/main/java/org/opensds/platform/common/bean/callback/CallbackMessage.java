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

package org.opensds.platform.common.bean.callback;

/**
 * 回调信息Bean
 * 
 * @author z00209306
 * @since V001V002C01
 */
public class CallbackMessage
{
    /*
     * 该回调消息所属的接口信息
     */
    private CallbackItfInfo callbackItfInfo;
    
    private Object payload;
    
    private Object extendedInfo;
    
    public Object getPayload()
    {
        return payload;
    }
    
    public void setPayload(Object payload)
    {
        this.payload = payload;
    }
    
    public Object getExtendedInfo()
    {
        return extendedInfo;
    }
    
    public void setExtendedInfo(Object extendedInfo)
    {
        this.extendedInfo = extendedInfo;
    }
    
    public CallbackItfInfo getCallbackItfInfo()
    {
        return callbackItfInfo;
    }
    
    public void setCallbackItfInfo(CallbackItfInfo callbackItfInfo)
    {
        this.callbackItfInfo = callbackItfInfo;
    }
}
