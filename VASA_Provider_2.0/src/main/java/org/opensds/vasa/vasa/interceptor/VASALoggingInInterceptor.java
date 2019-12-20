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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.helpers.IOUtils;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingMessage;
import org.apache.cxf.io.CachedOutputStream;
import org.apache.cxf.message.Message;

import org.opensds.platform.common.bean.log.InterfaceLogBean;
import org.opensds.platform.common.config.ConfigManager;
import org.opensds.platform.common.utils.ApplicationContextUtil;
import org.opensds.platform.common.utils.MaskUtils;
import org.opensds.platform.common.utils.StringUtils;
import org.opensds.platform.log.itf.IInterfaceLog;

public class VASALoggingInInterceptor extends LoggingInInterceptor {
    private static org.apache.logging.log4j.Logger LOGGER = LogManager
            .getLogger(VASALoggingInInterceptor.class);
    private static String SENSITIVE_WORDS = "";

    static {
        SENSITIVE_WORDS = ConfigManager.getInstance().getValue("sensitive.words");
    }

    @Override
    protected void logging(Logger logger, Message message) throws Fault {
        HttpServletRequest req = (HttpServletRequest) message.get("HTTP.REQUEST");
//         HttpServletResponse resp = (HttpServletResponse)message.get("HTTP.RESPONSE");
//         MessageContext mc = ThreadLocalHolder.get();
//         if (null == mc)
//         {
//             mc = new MessageContext();
//             ThreadLocalHolder.set(mc);
//         }
//         mc.getEntities().put("HTTP.REQUEST", req);
//         mc.getEntities().put("HTTP.RESPONSE", resp);

        if (message.containsKey(LoggingMessage.ID_KEY)) {
            return;
        }
        String id = (String) message.getExchange().get(LoggingMessage.ID_KEY);
        if (id == null) {
            id = LoggingMessage.nextId();
            message.getExchange().put(LoggingMessage.ID_KEY, id);
        }
        message.put(LoggingMessage.ID_KEY, id);
        final LoggingMessage buffer = new LoggingMessage(
                "Inbound Message\n----------------------------", id);

        Integer responseCode = (Integer) message.get(Message.RESPONSE_CODE);
        if (responseCode != null) {
            buffer.getResponseCode().append(responseCode);
        }

        String encoding = (String) message.get(Message.ENCODING);

        if (encoding != null) {
            buffer.getEncoding().append(encoding);
        }
        String httpMethod = (String) message.get(Message.HTTP_REQUEST_METHOD);
        if (httpMethod != null) {
            buffer.getHttpMethod().append(httpMethod);
        }
        String ct = (String) message.get(Message.CONTENT_TYPE);
        if (ct != null) {
            buffer.getContentType().append(ct);
        }
        @SuppressWarnings("unchecked")
        Map<String, List<String>> headers = (Map<String, List<String>>) message
                .get(Message.PROTOCOL_HEADERS);

        if (headers != null) {
            buffer.getHeader().append(headers);
        }
        String uri = (String) message.get(Message.REQUEST_URL);
        if (uri != null) {
            buffer.getAddress().append(uri);
            String query = (String) message.get(Message.QUERY_STRING);
            if (query != null) {
                buffer.getAddress().append("?").append(query);
            }
        }

        InputStream is = message.getContent(InputStream.class);
        if (is != null) {
            CachedOutputStream bos = new CachedOutputStream();
            try {
                IOUtils.copy(is, bos);

                bos.flush();

                message.setContent(InputStream.class, bos.getInputStream());
                File tempFile = bos.getTempFile();
                if (tempFile != null) {
                    // large thing on disk...
                    buffer.getMessage().append(
                            "\nMessage (saved to tmp file):\n");
                    buffer.getMessage().append(
                            "Filename: " + tempFile.getAbsolutePath()
                                    + "\n");
                }
                if (bos.size() > limit) {
                    buffer.getMessage().append(
                            "(message truncated to " + limit + " bytes)\n");
                }

                // FORTIFY.Unreleased_Resource--StreamsFORTIFY.Unreleased_Resource--Streams
                StringBuilder builder = buffer.getPayload();
                writePayload(builder, bos, encoding, ct, false);
                //writePayload(buffer.getPayload(), bos, encoding, ct);

                bos.close();
            } catch (Exception e) {
                throw new Fault(e);
            } finally {
                try {
                    if (null != bos) {
                        bos.close();
                    }
                } catch (IOException e) {
                    LOGGER.error("handleMessage bos close error", e);
                }
                try {
                    if (null != is) {
                        is.close();
                    }
                } catch (IOException e) {
                    LOGGER.error("handleMessage os close error", e);
                }

            }
        }
        String strBuffer = buffer.toString();
        strBuffer = replaceSession(strBuffer);
        log(logger, MaskUtils.mask(strBuffer, SENSITIVE_WORDS));


        SoapMessage soapMessage = (SoapMessage) message;
        String soapAction = req.getHeader("SOAPAction");
        logOperation(soapMessage, req, soapAction);
    }


    private void logOperation(SoapMessage soapMessage, HttpServletRequest req, String soapAction) {
        if (!StringUtils.isEmpty(soapAction)) {
            String interfaceName = soapAction.substring(soapAction.lastIndexOf("/") + 1, soapAction.length() - 1);
            String messageId = (String) soapMessage.getExchange().get(LoggingMessage.ID_KEY);
            if (messageId == null) {
                messageId = LoggingMessage.nextId();
                soapMessage.getExchange().put(LoggingMessage.ID_KEY, messageId);
            }


            InterfaceLogBean bean = new InterfaceLogBean();
            bean.setTransactionId(messageId);
            bean.setProduct("VASA");
            bean.setInterfaceType("1");
            bean.setProtocolType("SOAP");
            bean.setReq(true);
            bean.setName(interfaceName);
            bean.setSourceAddr(req.getRemoteHost());
            bean.setTargetAddr(req.getLocalAddr());
            bean.setReqTime(new Date());

            IInterfaceLog logger = ApplicationContextUtil.getBean("interfaceLogger");
            logger.info(bean);
        }
    }

    private String replaceSession(String logMessage) {
        int begin = 0;
        if (-1 < logMessage.indexOf("VASASESSIONID=")) {
            begin = logMessage.indexOf("VASASESSIONID=") + 14;
        } else if (-1 < logMessage.indexOf("VASASESSIONID:")) {
            begin = logMessage.indexOf("VASASESSIONID:") + 14;
        } else {
            return logMessage;
        }

        int end = -1;
        int end1 = logMessage.indexOf("]", begin);
        int end2 = logMessage.indexOf(";", begin);
        if (-1 == end1) {
            end = end2;
        } else if (-1 == end2) {
            end = end1;
        } else {
            end = end1 < end2 ? end1 : end2;
        }

        if (end > begin) {
            int length = (end - begin) / 2;
            String temp = logMessage.substring(begin, end);

            StringBuffer rep = new StringBuffer();
            for (int i = 0; i < length; i++) {
                rep.append("*");
            }
            rep.append(logMessage.substring(begin + length, end));
            logMessage = logMessage.replace(temp, rep);
        }
        return logMessage;
    }

}
