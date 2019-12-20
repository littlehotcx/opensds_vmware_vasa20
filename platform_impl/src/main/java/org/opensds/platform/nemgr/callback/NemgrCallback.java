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

package org.opensds.platform.nemgr.callback;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensds.platform.callback.itf.ICallback;
import org.opensds.platform.common.bean.callback.CallbackMessage;
import org.opensds.platform.common.constants.ESDKConstant;
import org.opensds.platform.common.exception.SDKException;
import org.opensds.platform.common.utils.ApplicationContextUtil;
import org.opensds.platform.nemgr.itf.IDeviceManager;

public class NemgrCallback implements ICallback
{
    private static final Logger LOGGER = LogManager.getLogger(NemgrCallback.class);
    
    @Override
    public Object onNotifyMsg(CallbackMessage callbackMessage)
    {
        IDeviceManager deviceManager = ApplicationContextUtil.getBean("deviceManager");
        String itfName = callbackMessage.getCallbackItfInfo().getItfName();
        String devId = callbackMessage.getCallbackItfInfo().getDevId();
        
        if (ESDKConstant.NOTIFY_ITFNAME_CONNECT.equals(itfName))
        {
            try
            {
                deviceManager.connect(devId);
            }
            catch (SDKException e)
            {
                LOGGER.error("", e);
            }
        }
        else if (ESDKConstant.NOTIFY_ITFNAME_DISCONNECT.equals(itfName))
        {
            deviceManager.closeConn(devId);
        }
        return null;
    }
}
