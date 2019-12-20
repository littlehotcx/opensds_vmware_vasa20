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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensds.platform.authorize.itf.IAuthorize;
import org.opensds.platform.authorize.itf.IAuthorizePolicy;
import org.opensds.platform.common.MessageContext;
import org.opensds.platform.common.ThreadLocalHolder;
import org.opensds.platform.common.bean.aa.AccountInfo;
import org.opensds.platform.common.constants.ESDKConstant;
import org.opensds.platform.common.utils.ApplicationContextUtil;

public class AccountMappingPolicy implements IAuthorizePolicy
{
    private static Logger LOGGER = LogManager.getLogger(AccountMappingPolicy.class);
    
    private static IAuthorize authorize = ApplicationContextUtil.getBean("authorize");
    
    private String deviceId;    
    
    public AccountMappingPolicy(String deviceId)
    {
        this.deviceId = deviceId;
    }
    
    @Override
    public AccountInfo getDeviceAccountInfo(String userId, String pwd)
    {
        MessageContext mc = ThreadLocalHolder.get();
        AccountInfo esdkAcctInfo = null;
        if (null != mc)
        {
            esdkAcctInfo = (AccountInfo)mc.getEntities().get(ESDKConstant.ACCT_INFO_ESDK);
        }
        
        return getAccountFromMapping(deviceId, esdkAcctInfo);
    }
    
    private AccountInfo getAccountFromMapping(String devId, AccountInfo esdkAcctInfo)
    {
        if (null == esdkAcctInfo)
        {
            return null;
        }

        AccountInfo result = authorize.getDevAccountInfo(devId, esdkAcctInfo.getUserId());
        LOGGER.debug("result=" + result);
        return result;
    }

    @Override
    public String getAuthPolicy()
    {
        return ESDKConstant.AUTH_POLICY_ACCT_MAPPING;
    }
}
