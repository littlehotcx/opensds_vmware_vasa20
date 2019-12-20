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
import org.opensds.platform.common.MessageContext;
import org.opensds.platform.common.ThreadLocalHolder;
import org.opensds.platform.common.bean.aa.AccountInfo;
import org.opensds.platform.common.constants.ESDKConstant;

public final class PassThroughPolicy implements IAuthorizePolicy
{
    private static PassThroughPolicy instance = new PassThroughPolicy();
    
    private PassThroughPolicy()
    {
        //Singleton
    }
    
    public static PassThroughPolicy getInstance()
    {
        return instance;
    }
    
    @Override
    public AccountInfo getDeviceAccountInfo(String userId, String pwd)
    {
        return getAccountInfoFromContext();
    }
    
    private AccountInfo getAccountInfoFromContext()
    {
        MessageContext mc = ThreadLocalHolder.get();
        if (null != mc)
        {
            return (AccountInfo) mc.getEntities().get(ESDKConstant.ACCT_INFO_ESDK);
        }
        
        return null;
    }

    @Override
    public String getAuthPolicy()
    {
        return ESDKConstant.AUTH_POLICY_PASS_THROUGH;
    }
}
