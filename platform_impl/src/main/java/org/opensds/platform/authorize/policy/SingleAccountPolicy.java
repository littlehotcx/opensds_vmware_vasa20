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

package org.opensds.platform.authorize.policy;

import org.opensds.platform.authorize.itf.IAuthorizePolicy;
import org.opensds.platform.common.bean.aa.AccountInfo;
import org.opensds.platform.common.constants.ESDKConstant;

public class SingleAccountPolicy implements IAuthorizePolicy
{
    private String deviceId;
    
    public SingleAccountPolicy(String deviceId)
    {
        this.deviceId = deviceId;
    }
    
    @Override
    public AccountInfo getDeviceAccountInfo(String userId, String pwd)
    {
        AccountInfo result = new AccountInfo();
        result.setDevId(deviceId);
        result.setUserId(userId);
        result.setPassword(pwd);
        
        return result;
    }
    
    @Override
    public String getAuthPolicy()
    {
        return ESDKConstant.AUTH_POLICY_SINGLE_ACCT;
    }
}
