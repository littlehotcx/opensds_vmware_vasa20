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

package org.opensds.platform.common;

import java.io.Serializable;

public class SDKErrorCode implements Serializable
{
    /**
     * UID
     */
    private static final long serialVersionUID = 1L;
    
    private String errCodeAsString = "0";

    private String subSystem;

    private String description;
    
    public long getErrCode()
    {
        try
        {
            return Long.parseLong(errCodeAsString);
        }
        catch(NumberFormatException ex)
        {
            throw new IllegalArgumentException(errCodeAsString + " is invalid number.");
        }
    }
    
    public String getErrCodeStr()
    {
        return errCodeAsString;
    }
    
    public void setErrCode(String errCodeAsString)
    {
        this.errCodeAsString = errCodeAsString;
    }
    
    public void setErrCode(long errCode)
    {
        this.errCodeAsString = String.valueOf(errCode);
    }

    public void setErrCode(Integer errCode)
    {
        long lErrCode = (null == errCode ? -1 : Long.valueOf(errCode));
        this.errCodeAsString = String.valueOf(lErrCode);
    }

    public String getSubSystem()
    {
        return subSystem;
    }

    public void setSubSystem(String subSystem)
    {
        this.subSystem = subSystem;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }
}
