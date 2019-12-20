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

package org.opensds.platform.log;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensds.platform.common.bean.log.OperationLogBean;
import org.opensds.platform.log.itf.IOperationLog;

public class OperationLog implements IOperationLog
{
    private static final Logger LOGGER = LogManager.getLogger(OperationLog.class);
    
    private static OperationLog instance = new OperationLog();
    
    private OperationLog()
    {
        
    }
    
    public static OperationLog getInstance()
    {
        return instance;
    }
    
    @Override
    public void debug(OperationLogBean logBean)
    {
        if (LOGGER.isDebugEnabled())
        {
            LOGGER.debug(buildLogMsg(logBean));
        }
    }

    @Override
    public void info(OperationLogBean logBean)
    {
        if (LOGGER.isInfoEnabled())
        {
            LOGGER.info(buildLogMsg(logBean));
        }
        
    }

    @Override
    public void warn(OperationLogBean logBean)
    {
        LOGGER.warn(buildLogMsg(logBean));
    }

    @Override
    public void error(OperationLogBean logBean)
    {
        LOGGER.debug(buildLogMsg(logBean));
        
    }
    
    private String buildLogMsg(OperationLogBean logBean)
    {
        StringBuilder sb = new StringBuilder();
        if (null != logBean)
        {
            sb.append(logBean.getModuleName()).append("|");
            sb.append(logBean.getUserId()).append("|");
            sb.append(logBean.getClientFlag()).append("|");
            sb.append(logBean.getResultCode()).append("|");
            sb.append(logBean.getKeyInfo());
        }
        
        return sb.toString();
    }
}
