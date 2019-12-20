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

package org.opensds.platform.nemgr.base;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.opensds.platform.callback.itf.ICallback;
import org.opensds.platform.common.bean.callback.CallbackMessage;
import org.opensds.platform.common.constants.ESDKConstant;

public class NotifyCollector
{
    private static final Map<String, ICallback> CALLBACK_MAPPING = new ConcurrentHashMap<String, ICallback>();
    
    public void publishNotify(CallbackMessage callbackMessage)
    {
        String key = null;
        if (ESDKConstant.NOTIFY_MSG_TYPE_ESDK_EVENT.equalsIgnoreCase(callbackMessage.getCallbackItfInfo()
            .getNotifyMsgType()))
        {
            key = callbackMessage.getCallbackItfInfo().getProcessorId();
        }
        else
        {
            key =
                callbackMessage.getCallbackItfInfo().getDevId()
                    + callbackMessage.getCallbackItfInfo().getConnectionId();
        }
        
        ICallback callbackImpl = CALLBACK_MAPPING.get(key);
        if (null != callbackImpl)
        {
            callbackImpl.onNotifyMsg(callbackMessage);
        }
    }
    
    public boolean subscribeNotify(String id, ICallback callback)
    {
        CALLBACK_MAPPING.put(id, callback);
        return true;
    }
    
    public boolean subscribeNotify(String neId, String connectionId, ICallback callback)
    {
        CALLBACK_MAPPING.put(neId + connectionId, callback);
        return true;
    }
    
    public void unsubscribeNotify(String neId, String connectionId)
    {
        CALLBACK_MAPPING.remove(neId + connectionId);
    }
    
    public void unsubscribeNotify(String id)
    {
        CALLBACK_MAPPING.remove(id);
    }
}
