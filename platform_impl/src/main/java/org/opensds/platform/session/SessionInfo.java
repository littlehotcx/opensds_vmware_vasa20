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

package org.opensds.platform.session;

public class SessionInfo
{
    private String userName;
    
    private byte[] secretKey;
    
    private byte[] iv;
    
    public String getUserName()
    {
        return userName;
    }
    
    public void setUserName(String userName)
    {
        this.userName = userName;
    }
    
    public byte[] getSecretKey()
    {
        return secretKey;
    }
    
    public void setSecretKey(byte[] secretKey)
    {
        this.secretKey = secretKey;
    }
    
    public byte[] getIv()
    {
        return iv;
    }
    
    public void setIv(byte[] iv)
    {
        this.iv = iv;
    }
    
}
