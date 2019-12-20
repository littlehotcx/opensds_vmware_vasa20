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

package org.opensds.platform.common.bean.log;

public class OperationLogBean
{
    /*
     * 内部模块名称，暂时分为：login、config、log、version
     */
    private String moduleName;
    
    /*
     * 操作用户
     */
    private String userId;
    
    /*
     * 操作客户端标识，一般为客户端IP
     */
    private String clientFlag;
    
    /*
     * resultCode:指操作成功还是失败，用"successful"、"failed"标识
     */
    private String resultCode;
    
    /*
     * 关键描述信息
     */
    private String keyInfo;

    public String getModuleName()
    {
        return moduleName;
    }

    public void setModuleName(String moduleName)
    {
        this.moduleName = moduleName;
    }

    public String getUserId()
    {
        return userId;
    }

    public void setUserId(String userId)
    {
        this.userId = userId;
    }

    public String getClientFlag()
    {
        return clientFlag;
    }

    public void setClientFlag(String clientFlag)
    {
        this.clientFlag = clientFlag;
    }

    public String getResultCode()
    {
        return resultCode;
    }

    public void setResultCode(String resultCode)
    {
        this.resultCode = resultCode;
    }

    public String getKeyInfo()
    {
        return keyInfo;
    }

    public void setKeyInfo(String keyInfo)
    {
        this.keyInfo = keyInfo;
    }
}
