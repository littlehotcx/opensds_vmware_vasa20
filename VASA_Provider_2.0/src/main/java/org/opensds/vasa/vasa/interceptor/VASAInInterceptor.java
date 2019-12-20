/*
 * // Copyright 2019 The OpenSDS Authors.
 * //
 * // Licensed under the Apache License, Version 2.0 (the "License"); you may
 * // not use this file except in compliance with the License. You may obtain
 * // a copy of the License at
 * //
 * //     http://www.apache.org/licenses/LICENSE-2.0
 * //
 * // Unless required by applicable law or agreed to in writing, software
 * // distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * // WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * // License for the specific language governing permissions and limitations
 * // under the License.
 *
 */

package org.opensds.vasa.vasa.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;

import org.opensds.platform.common.MessageContext;
import org.opensds.platform.common.ThreadLocalHolder;
import org.opensds.platform.common.constants.ESDKConstant;
import org.opensds.platform.common.constants.ESDKErrorCodeConstant;
import org.opensds.platform.common.utils.ApplicationContextUtil;
import org.opensds.platform.flowcontrol.itf.IMonitor;
import org.opensds.platform.flowcontrol.itf.IPerformer;

public class VASAInInterceptor extends AbstractPhaseInterceptor<Message> {
    private static Logger LOGGER = LogManager
            .getLogger(VASAInInterceptor.class);

    public VASAInInterceptor() {
        super(Phase.RECEIVE);
    }

    @Override
    public void handleMessage(Message message)
            throws Fault {
        // 通知流量监控模块
        IMonitor monitor = ApplicationContextUtil.getBean("northFCMonitor");
        if (monitor != null) {
            monitor.reportStatus(1); // 增加一次SOAP调用
            IPerformer performer = ApplicationContextUtil.getBean("northFCPerformer");
            if (performer != null) {
                if (performer.doFilter(message)) {
                    monitor.reportStatus(-1); // 被流控消息不计入监控数据
                    LOGGER.info("*****北向消息，线程id=*****" + Thread.currentThread().getId() + "被流控");
                    SOAPException soapExc = new SOAPException("");
                    Fault fault = new Fault(soapExc);
                    fault.setFaultCode(new QName(Integer.toString(ESDKErrorCodeConstant.ERROR_CODE_SDK_SYSBUSY)));
                    throw fault;
                }
            }
        }

        HttpServletRequest req = (HttpServletRequest) message.get("HTTP.REQUEST");
        HttpServletResponse resp = (HttpServletResponse) message.get("HTTP.RESPONSE");
        String ip = req.getRemoteAddr();
        MessageContext mc = ThreadLocalHolder.get();
        if (null == mc) {
            mc = new MessageContext();
            ThreadLocalHolder.set(mc);
        }
        mc.getEntities().put(ESDKConstant.ESDK_CLIENT_IP, ip);
        mc.getEntities().put("HTTP.REQUEST", req);
        mc.getEntities().put("HTTP.RESPONSE", resp);
        /*
        String soapAction = req.getHeader("SOAPAction");
        if (null != soapAction)
        {
            String interfaceName = soapAction.substring(1, soapAction.length() - 1);
            // 记录调用日志
            String messageId = (String)message.getExchange().get(LoggingMessage.ID_KEY);
            if (messageId == null)
            {
                messageId = LoggingMessage.nextId();
                message.getExchange().put(LoggingMessage.ID_KEY, messageId);
            }
            
            LogBean bean = new LogBean();
            bean.setActionName(interfaceName);
            bean.setRequestTime(new Date());
            bean.setIp(req.getRemoteHost());
            bean.setPort(req.getRemotePort() + "");
            LogInterface log = ApplicationContextUtil.getBean("logManager");
            log.saveRequestLog(messageId, bean);
        }
        */
    }
}
