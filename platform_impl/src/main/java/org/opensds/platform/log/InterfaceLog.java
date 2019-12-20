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

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensds.platform.common.bean.log.InterfaceLogBean;
import org.opensds.platform.common.utils.StringUtils;
import org.opensds.platform.log.itf.IInterfaceLog;

public class InterfaceLog implements IInterfaceLog
{
    private static final Logger LOGGER = LogManager.getLogger(InterfaceLog.class);
    
    private Map<String, InterfaceLogBean> logMap = new HashMap<String, InterfaceLogBean>();
    
    @Override
    public void info(String log)
    {
        if (LOGGER.isInfoEnabled())
        {
            LOGGER.info(log);
        }
    }
    
    @Override
    public void info(InterfaceLogBean logBean)
    {
        logBean = preProcessLogBean(logBean);
        if (null == logBean)
        {
            return;
        }
        
        if (LOGGER.isInfoEnabled())
        {
            LOGGER.info(buildLogMsg(logBean));
        }
    }

    private InterfaceLogBean preProcessLogBean(InterfaceLogBean logBean)
    {
        if (logBean.isReq())
        {
            logMap.put(logBean.getTransactionId(), logBean);
            return null;
        }
        else
        {
            InterfaceLogBean req = logMap.remove(logBean.getTransactionId());
            if (null == req)
            {
                return null;
            }            
            req.setRespTime(logBean.getRespTime());
            req.setRespParams(logBean.getRespParams());
            req.setResultCode(logBean.getResultCode());
            
            return req;
        }
    }

    @Override
    public void error(InterfaceLogBean logBean)
    {
        logBean = preProcessLogBean(logBean);
        if (null == logBean)
        {
            return;
        }
        LOGGER.error(buildLogMsg(logBean));
    }
    
    private String buildLogMsg(InterfaceLogBean logBean)
    {
        //接口日志设计格式
        //日期时间|级别|请求响应标识|接口所属业务|接口类型|接口名称|源端设备|宿端设备|会话标识|请求响应参数。
        //日期时间|级别 用Log4j组件输出
        StringBuilder sb = new StringBuilder();
        if (null != logBean)
        {
            sb.append(logBean.getProduct()).append("|");
            sb.append(logBean.getInterfaceType()).append("|");
            sb.append(logBean.getProtocolType()).append("|");
            sb.append(logBean.getName()).append("|");
            sb.append(logBean.getSourceAddr()).append("|");
            sb.append(logBean.getTargetAddr()).append("|");
            sb.append(StringUtils.avoidNull(logBean.getTransactionId())).append("|");
            sb.append(logBean.getReqTimeAsString()).append("|");
            sb.append(logBean.getRespTimeAsString()).append("|");
            sb.append(StringUtils.avoidNull(logBean.getReqParams())).append("|");
            sb.append(logBean.getResultCode()).append("|");
            sb.append(StringUtils.avoidNull(logBean.getRespParams()));
        }
        
        return sb.toString();
    }
}
