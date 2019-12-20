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
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.logging.log4j.LogManager;
import org.apache.cxf.common.logging.LogUtils;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.interceptor.LoggingMessage;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.io.CacheAndWriteOutputStream;
import org.apache.cxf.io.CachedOutputStream;
import org.apache.cxf.io.CachedOutputStreamCallback;
import org.apache.cxf.message.Message;
import org.apache.cxf.service.model.EndpointInfo;
import org.apache.cxf.service.model.InterfaceInfo;

import org.opensds.platform.common.bean.log.InterfaceLogBean;
import org.opensds.platform.common.config.ConfigManager;
import org.opensds.platform.common.utils.ApplicationContextUtil;
import org.opensds.platform.common.utils.MaskUtils;
import org.opensds.platform.log.itf.IInterfaceLog;

public class VASALoggingOutInterceptor extends LoggingOutInterceptor {
    private static final String LOG_SETUP = LoggingOutInterceptor.class
            .getName() + ".log-setup";

    protected static final org.apache.logging.log4j.Logger LOGGER = LogManager
            .getLogger(VASALoggingOutInterceptor.class);

    private static String SENSITIVE_WORDS = "";

    static {
        SENSITIVE_WORDS = ConfigManager.getInstance().getValue("sensitive.words");
    }


    private Logger getMessageLogger(Message message) {
        EndpointInfo endpoint = message.getExchange().getEndpoint()
                .getEndpointInfo();
        if (endpoint.getService() == null) {
            return getLogger();
        }
        Logger logger = endpoint.getProperty("MessageLogger", Logger.class);
        if (logger == null) {
            String serviceName = endpoint.getService().getName().getLocalPart();
            InterfaceInfo iface = endpoint.getService().getInterface();
            String portName = endpoint.getName().getLocalPart();
            String portTypeName = iface.getName().getLocalPart();
            String logName = "org.apache.cxf.services." + serviceName + "."
                    + portName + "." + portTypeName;
            logger = LogUtils.getL7dLogger(this.getClass(), null, logName);
            endpoint.setProperty("MessageLogger", logger);
        }
        return logger;
    }

    @Override
    public void handleMessage(Message message) throws Fault {
        final OutputStream os = message.getContent(OutputStream.class);
        if (os == null) {
            return;
        }
        Logger logger = getMessageLogger(message);

        if (logger.isLoggable(Level.INFO) || writer != null) {
            // Write the output while caching it for the log message
            boolean hasLogged = message.containsKey(LOG_SETUP);
            if (!hasLogged) {
                message.put(LOG_SETUP, Boolean.TRUE);
                final CacheAndWriteOutputStream newOut = new CacheAndWriteOutputStream(
                        os);
                message.setContent(OutputStream.class, newOut);
                newOut.registerCallback(new LoggingCallback(logger, message, os));
            }
        }
    }

    class LoggingCallback implements CachedOutputStreamCallback {

        private final Message message;

        private final OutputStream origStream;

        private final Logger logger; // NOPMD

        public LoggingCallback(final Logger logger, final Message msg,
                               final OutputStream os) {
            this.logger = logger;
            this.message = msg;
            this.origStream = os;
        }

        public void onFlush(CachedOutputStream cos) {

        }

        public void onClose(CachedOutputStream cos) {
            String id = (String) message.getExchange().get(
                    LoggingMessage.ID_KEY);
            if (id == null) {
                id = LoggingMessage.nextId();
                message.getExchange().put(LoggingMessage.ID_KEY, id);
            }
            final LoggingMessage buffer = new LoggingMessage(
                    "Outbound Message\n---------------------------", id);

            Integer responseCode = (Integer) message.get(Message.RESPONSE_CODE);
            if (responseCode != null) {
                buffer.getResponseCode().append(responseCode);
            }

            String encoding = (String) message.get(Message.ENCODING);

            if (encoding != null) {
                buffer.getEncoding().append(encoding);
            }
            String httpMethod = (String) message
                    .get(Message.HTTP_REQUEST_METHOD);
            if (httpMethod != null) {
                buffer.getHttpMethod().append(httpMethod);
            }
            String address = (String) message.get(Message.ENDPOINT_ADDRESS);
            if (address != null) {
                buffer.getAddress().append(address);
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
            File tempFile = cos.getTempFile();
            if (tempFile == null) {
                // buffer.append("Outbound Message:\n");
                if (cos.size() > limit) {
                    buffer.getMessage().append(
                            "(message truncated to " + limit + " bytes)\n");
                }
            } else {
                buffer.getMessage().append(
                        "Outbound Message (saved to tmp file):\n");
                buffer.getMessage().append(
                        "Filename: " + tempFile.getAbsolutePath() + "\n");
                if (cos.size() > limit) {
                    buffer.getMessage().append(
                            "(message truncated to " + limit + " bytes)\n");
                }
            }
            try {
                writePayload(buffer.getPayload(), cos, encoding, ct, false);
            } catch (Exception ex) {
                LOGGER.error(ex.getMessage());
            }

            String strBuffer = buffer.toString();
            log(logger, MaskUtils.mask(strBuffer, SENSITIVE_WORDS));

            try {
                // empty out the cache
                cos.lockOutputStream();
                cos.resetOut(null, false);
            } catch (Exception ex) {
                LOGGER.error(ex.getMessage());
            }
            message.setContent(OutputStream.class, origStream);

            InterfaceLogBean interfaceLogBean = new InterfaceLogBean();
            interfaceLogBean.setTransactionId(id);
            interfaceLogBean.setReq(false);
            interfaceLogBean.setRespTime(new Date());
            if (strBuffer.contains("<soapenv:Fault>")) {
                interfaceLogBean.setResultCode("-1");
            } else {
                interfaceLogBean.setResultCode("0");
            }

            IInterfaceLog logger = ApplicationContextUtil.getBean("interfaceLogger");
            logger.info(interfaceLogBean);
        }
    }
}
