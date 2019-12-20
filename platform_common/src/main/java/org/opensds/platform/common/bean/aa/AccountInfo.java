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

package org.opensds.platform.common.bean.aa;

public class AccountInfo
{
    private String devId;
    
    private String userId;
    
    private String password;
    
    private String reserved1;
    
    private String reserved2;
    
    public String getDevId()
    {
        return devId;
    }
    
    public void setDevId(String devId)
    {
        this.devId = devId;
    }
    
    public String getUserId()
    {
        return userId;
    }
    
    public void setUserId(String userId)
    {
        this.userId = userId;
    }
    
    public String getPassword()
    {
        return password;
    }
    
    public void setPassword(String password)
    {
        this.password = password;
    }
    
    public String getReserved1()
    {
        return reserved1;
    }
    
    public void setReserved1(String reserved1)
    {
        this.reserved1 = reserved1;
    }
    
    public String getReserved2()
    {
        return reserved2;
    }
    
    public void setReserved2(String reserved2)
    {
        this.reserved2 = reserved2;
    }
}
