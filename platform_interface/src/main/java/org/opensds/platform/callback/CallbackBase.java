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

package org.opensds.platform.callback;

import org.opensds.platform.common.bean.callback.CallbackMessage;
import org.opensds.platform.common.notification.NotifyDispatcher;

public class CallbackBase
{
    protected NotifyDispatcher<?> notifyDispatcher;

    public Object onNotifyMsg(CallbackMessage callbackMessage)
    {
        if (null != notifyDispatcher)
        {
            return notifyDispatcher.fireNotify(callbackMessage.getCallbackItfInfo().getItfName(),
                    callbackMessage.getPayload(),
                    callbackMessage.getExtendedInfo());
        }
        else
        {
            return new Object();
        }
    }
    
    public NotifyDispatcher<?> getNotifyDispatcher()
    {
        return notifyDispatcher;
    }

    public void setNotifyDispatcher(NotifyDispatcher<?> notifyDispatcher)
    {
        this.notifyDispatcher = notifyDispatcher;
    }
}
