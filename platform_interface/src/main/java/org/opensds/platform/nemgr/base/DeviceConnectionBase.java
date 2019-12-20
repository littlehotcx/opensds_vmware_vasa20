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

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

import org.opensds.platform.authorize.itf.IAuthorizePolicy;
import org.opensds.platform.common.MessageContext;
import org.opensds.platform.common.ThreadLocalHolder;
import org.opensds.platform.common.bean.aa.AccountInfo;
import org.opensds.platform.common.constants.ESDKConstant;
import org.opensds.platform.nemgr.itf.IDeviceConnection;

public abstract class DeviceConnectionBase implements IDeviceConnection
{
    protected ConcurrentHashMap<String, Object> mapData = new ConcurrentHashMap<String, Object>();
    
    protected String loginUser;
    
    protected String loginPassword;
    
    protected String session;
    
    protected DeviceConnectionBase(String user, String pwd)
    {
        loginUser = user;
        loginPassword = pwd;
    }
    
    protected AccountInfo getDevAcctInfo(IAuthorizePolicy authorizePolicy)
    {
        MessageContext mc = ThreadLocalHolder.get();
        AccountInfo devAcctInfo = authorizePolicy.getDeviceAccountInfo(getLoginUser(), getLoginPassword());
        if (null == devAcctInfo)
        {
            //For reconnection case
            AccountInfo esdkAcct = (AccountInfo)getAdditionalData(ESDKConstant.ACCT_INFO_ESDK);
            if (null != esdkAcct)
            {
                mc.getEntities().put(ESDKConstant.ACCT_INFO_ESDK, esdkAcct);
                devAcctInfo = authorizePolicy.getDeviceAccountInfo(esdkAcct.getUserId(), esdkAcct.getPassword());
            }
        }
        
        return devAcctInfo;
    }
    
    @Override
    public Object getAdditionalData(String key)
    {
        return mapData.get(key);
    }
    
    @Override
    public void setAdditionalData(String key, Object data)
    {
        mapData.put(key, data);
    }
    
    @Override
    public int getKeepAliveTimes()
    {
        return 1;
    }
    
    @Override
    public int getKeepAlivePeriod()
    {
        return 10000;
    }
    
    @Override
    public Date getStartTime()
    {
        return new Date();
    }
    
    public String getLoginUser()
    {
        return loginUser;
    }
    
    public void setLoginUser(String loginUser)
    {
        this.loginUser = loginUser;
    }
    
    public String getLoginPassword()
    {
        return loginPassword;
    }
    
    public void setLoginPassword(String loginPassword)
    {
        this.loginPassword = loginPassword;
    }
    
    public String getSession()
    {
        return session;
    }
    
    public void setSession(String session)
    {
        this.session = session;
    }

    @Override
    public boolean isLocalAuth()
    {
        // TODO Auto-generated method stub
        return true;
    }
    
    
}
