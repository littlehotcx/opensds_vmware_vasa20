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

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.opensds.platform.base.BaseManager;
import org.opensds.platform.common.bean.log.LogBean;
import org.opensds.platform.log.itf.LogInterface;

public class LogManager extends BaseManager implements LogInterface
{
    private static final Logger LOGGER = org.apache.logging.log4j.LogManager.getLogger(LogManager.class);

    private Map<String, LogBean> logMap = new HashMap<String, LogBean>();

    private void writeLog(LogBean bean)
    {
        String format="yyyyMMddHHmmss";
        SimpleDateFormat sdf=new SimpleDateFormat(format); 
        String logStr = bean.getActionName() + "," + bean.getIp() + ","
                + bean.getPort() + ","
                + sdf.format(bean.getRequestTime()) + ","
                + sdf.format(bean.getResponseTime()) + ","
                + bean.getResultCode();
        LOGGER.info(logStr);
    }

    @Override
    public void saveRequestLog(String messageId, LogBean log)
    {
        synchronized (logMap)
        {
            logMap.put(messageId, log);
        }
    }

    @Override
    public void saveResponseLog(String messageId, LogBean log)
    {
        LogBean bean;
        synchronized (logMap)
        {
            bean = logMap.get(messageId);
            if (null == bean)
            {
                return;
            }
            bean.setResponseTime(log.getResponseTime());
            bean.setResultCode(log.getResultCode());
            logMap.remove(messageId);
        }
        writeLog(bean);
    }

}
