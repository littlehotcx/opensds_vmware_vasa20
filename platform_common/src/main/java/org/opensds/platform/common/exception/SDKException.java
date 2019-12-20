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

package org.opensds.platform.common.exception;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.opensds.platform.common.SDKErrorCode;

public class SDKException extends Exception
{
    protected static final Logger LOGGER = LogManager.getLogger(SDKException.class);

    private static final long serialVersionUID = 1L;

    private SDKErrorCode errInfo;

    public SDKException(String msg)
    {
        super(msg);
        errInfo = new SDKErrorCode();
        LOGGER.error("Error Message:" + msg);
    }

    public SDKException(String msg, Throwable t)
    {
        super(msg, t);
        errInfo = new SDKErrorCode();
        LOGGER.error("Error Message:" + msg, t);
    }

    public SDKException(Throwable t)
    {
        super(t);
        errInfo = new SDKErrorCode();
        LOGGER.error(t);
    }

    public String getSdkErrDesc()
    {
        return errInfo.getDescription();
    }

    public void setSdkErrDesc(String sdkErrDesc)
    {
        errInfo.setDescription(sdkErrDesc);
    }

    public long getSdkErrCode()
    {
        return errInfo.getErrCode();
    }

    public void setSdkErrCode(int sdkErrCode)
    {
        errInfo.setErrCode(sdkErrCode);
    }

    public void setSdkErrCode(Integer sdkErrCode)
    {
        errInfo.setErrCode(sdkErrCode);
    }

}