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
package org.opensds.vasa.vasa.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.security.auth.x500.X500Principal;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.mbeans.MBeanUtils;
import org.opensds.vasa.common.MagicNumber;
import org.opensds.vasa.vasa.service.SecureConnectionService;

import org.opensds.platform.common.MessageContext;
import org.opensds.platform.common.ThreadLocalHolder;
import org.opensds.platform.common.config.ConfigManager;
import org.opensds.platform.common.utils.Base64Utils;
import org.opensds.platform.common.utils.StringUtils;

import com.vmware.vim.vasa.v20.InvalidArgument;
import com.vmware.vim.vasa.v20.InvalidCertificate;
import com.vmware.vim.vasa.v20.InvalidLogin;
import com.vmware.vim.vasa.v20.InvalidSession;
import com.vmware.vim.vasa.v20.StorageFault;

/**
 * Helper functions for handling SSL certificates.
 *
 * @author V1R10
 * @version V001R001C10
 */
public class SSLUtil {

    private static org.apache.logging.log4j.Logger LOGGER = org.apache.logging.log4j.LogManager
            .getLogger(SSLUtil.class);

    /**
     * 公共属性HASH_LENGTH
     */
    public static final int HASH_LENGTH = 20;

    /**
     * 公共属性VASA_SESSIONID_STR
     */
    public static final String VASA_SESSIONID_STR = "VASASESSIONID";

    public static final String DEFAULT_CSR_FILENAME = FileManager.getBasePath() + "conf" + File.separator
            + "certreq.csr";

    public static final String DEFAULT_CASIGNED_FILENAME = FileManager.getBasePath() + "conf" + File.separator
            + "caSigned.cert";

    public static final String DEFAULT_CRL_FILENAME = FileManager.getBasePath() + "conf" + File.separator + "caCRL.crl";

    private String trustStoreFileName;

    private String trustStorePassword;

    private boolean mustUseSSL;

    private String trustStoreType;

    private DataUtil dataUtil = DataUtil.getInstance();
    ;

    //private VasaInfoService vasaInfoService = ApplicationContextUtil.getBean("vasaInfoService");

    /**
     * Constructor
     *
     * @param fileName       方法参数：fileName
     * @param password       方法参数：password
     * @param sslOnlyIn      方法参数：sslOnlyIn
     * @param truststoreType truststoreType
     */
    public SSLUtil(String fileName, String password, boolean sslOnlyIn, String truststoreType) {
        trustStoreFileName = FileManager.getBasePath() + "conf" + File.separator + fileName;
        trustStorePassword = password;
        mustUseSSL = sslOnlyIn;
        trustStoreType = truststoreType;
    }

    /**
     * return the value of the given HTTP cookie
     *
     * @param cookieName 方法参数：cookieName
     * @return String 返回结果
     * @throws InvalidSession 异常：InvalidSession
     */
    public String getCookie(String cookieName) throws InvalidSession {
        MessageContext mc = ThreadLocalHolder.get();
        if (null == mc) {
            LOGGER.warn("MessageContext is null!");
            return null;
        }
        HttpServletRequest localHttpServletRequest = (HttpServletRequest) mc.getEntities().get("HTTP.REQUEST");

        if (localHttpServletRequest == null) {
            throw FaultUtil.invalidSession("No HTTP Servlet Request");
        }

        Cookie[] cookies = localHttpServletRequest.getCookies();
        if (cookies == null) {
            LOGGER.warn("cookies is null!");
            return null;
        }

        for (int i = 0; i < cookies.length; i++) {
            if (cookies[i].getName().equals(cookieName)) {
                return cookies[i].getValue();
            }
        }
        return null;
    }

    private String getLocalIpAddress() throws InvalidSession {
        MessageContext mc = ThreadLocalHolder.get();
        if (null == mc) {
            LOGGER.warn("MessageContext is null!");
            return null;
        }
        HttpServletRequest localHttpServletRequest = (HttpServletRequest) mc.getEntities().get("HTTP.REQUEST");

        if (localHttpServletRequest == null) {
            LOGGER.error("No HTTP Servlet Request");
            throw FaultUtil.invalidSession("No HTTP Servlet Request");
        }

        String localIp = localHttpServletRequest.getLocalAddr();
        return localIp;
    }

    /**
     * set the given HTTP cookie
     *
     * @param cookieName  方法参数：cookieName
     * @param cookieValue 方法参数：cookieValue
     * @throws InvalidSession 异常：InvalidSession
     */
    public void setCookie(String cookieName, String cookieValue) throws InvalidSession {
        MessageContext mc = ThreadLocalHolder.get();
        if (mc == null) {
            throw FaultUtil.invalidSession("No current message context");
        } else {
            HttpServletResponse localHttpServletResponse = (HttpServletResponse) mc.getEntities().get("HTTP.RESPONSE");
            if (localHttpServletResponse == null) {
                throw FaultUtil.invalidSession("No HTTP Servlet Response");
            } else {
                Cookie cookie = new Cookie(cookieName, cookieValue);
                /**
                 * CodeDEX Errors [Cookie Security:Cookie not Sent Over
                 * SSL,Cookie_Security--Cookie_not_Sent_Over_SSL] start
                 */
                cookie.setHttpOnly(true);
                cookie.setSecure(true);
                /**
                 * CodeDEX Errors [Cookie Security:Cookie not Sent Over
                 * SSL,Cookie_Security--Cookie_not_Sent_Over_SSL] end
                 */
                localHttpServletResponse.addCookie(cookie);
            }
        }
    }

    /**
     * setHttpResponse 设置session头
     *
     * @param sc 方法参数：sc
     * @throws InvalidSession 异常：InvalidSession
     */
    public void setHttpResponse(SessionContext sc) throws InvalidSession {
        if (sc != null) {
            setCookie(VASA_SESSIONID_STR, sc.getSessionId());
        }
    }

    private void checkHttpForValidVASASession() throws InvalidSession {
        /*
         * Check for a valid VASA Session.
         */
        String sessionId = getCookie(VASA_SESSIONID_STR);
        if (sessionId == null) {
            LOGGER.error("InvalidSession/No valid VASA SessionId in HTTP header");
            throw FaultUtil.invalidSession("No valid VASA SessionId in HTTP header");
        }
        try {
            SessionContext sc = SessionContext.lookupSessionContextBySessionId(sessionId);
            if (sc == null) {
                LOGGER.error("InvalidSession/Invalid VASA SessionId " + VASAUtil.replaceSessionId(sessionId)
                        + " in HTTP header");
                throw FaultUtil.invalidSession(
                        "Invalid VASA SessionId " + VASAUtil.replaceSessionId(sessionId) + " in HTTP header");
            }
        } catch (InvalidSession e) {
            LOGGER.error("InvalidSession/Could not find session context ");
            throw FaultUtil.invalidSession("Could not find session context " + e);
        } catch (StorageFault e) {
            LOGGER.error("InvalidSession/Could not find session context ");
            throw FaultUtil.invalidSession("Could not find session context " + e);
        }
    }

    private void checkHttpForValidSSLSession(HttpServletRequest req) throws InvalidSession, InvalidCertificate {
        /*
         * Check for a valid SSL Session.
         */
        X509Certificate[] sslCerts = (X509Certificate[]) req.getAttribute("javax.servlet.request.X509Certificate");
        if ((sslCerts == null) || (sslCerts.length == 0)) {
            // 规避掉添加失败问题 g00250185 DTS2013092309414
            // return;
            LOGGER.error("InvalidSession/No SSL Client Certificate attached to HTTPS session");
            throw FaultUtil.invalidSession("No SSL Client Certificate attached to HTTPS session");
            // DTS2013092309414
        }

        if (!certificateIsTrusted(sslCerts[0])) {
            String str1 = null;
            String str2 = req.getRemoteAddr();
            try {
                addCertificateToTrustStore(str1, str2, sslCerts[0]);
                refreshTrustStore();

                //add certificate to db
                LOGGER.debug("checkHttpForValidSSLSession add certificate to db and do sync.");
                SecureConnectionService.getInstance().refreshAndNotifyLeaderToBroadcast(IPUtil.getLocalIP());

            } catch (Exception e) {
                /**
                 * 修改CodeDEX问题：FORTIFY.Poor_Error_Handling--Empty_Catch_Block By
                 * wWX315527 2016/11/17
                 */
                LOGGER.error("Execution during checkHttpForValidSSLSession: " + e.getMessage());
            }
        }
        if (certificateIsTrusted(sslCerts[0])) {
            return;
        }
        LOGGER.error("InvalidSession/No Trusted SSL Client Certificate attached to HTTPS session");
        throw FaultUtil.invalidSession("No Trusted SSL Client Certificate attached to HTTPS session");

        /**
         * Note that a certificate that is trusted by this server, but one that
         * has not necessarily been registered via a call to
         * registerVASACertficate() will be accepted as valid.
         */
    }


    public String checkHttpRequestThrowInvalidCertificate(boolean validSSLSessionNeeded, boolean validVASASessionNeeded)
            throws InvalidSession, InvalidCertificate {
        try {
            /*
             * Check for a valid context.
             */
            MessageContext mc = ThreadLocalHolder.get();
            if (mc == null) {
                LOGGER.error("InvalidSession/No current message context");
                throw FaultUtil.invalidSession("No current message context");
            }

            HttpServletRequest req = (HttpServletRequest) mc.getEntities().get("HTTP.REQUEST");

            if (req == null) {
                LOGGER.error("InvalidSession/No HTTP Servlet Request");
                throw FaultUtil.invalidSession("No HTTP Servlet Request");
            }
            String clientAddress = req.getRemoteAddr();
            /**
             * Get SSL data
             */
            String sslSessionId = (String) req.getAttribute("javax.servlet.request.ssl_session");
            if (sslSessionId == null) {
                /**
                 * This is not an SSL connection. If the service is not allowing
                 * none-SSL connections, throw an exception. Otherwise check for
                 * a valid VASA session if necessary.
                 */
                if (!mustUseSSL) {
                    if (validVASASessionNeeded) {
                        checkHttpForValidVASASession();
                    }
                    return clientAddress;
                } else {
                    LOGGER.error("InvalidSession/Must use SSL connection");
                    throw FaultUtil.invalidSession("Must use SSL connection");
                }
            }

            /*
             * At this point, it is known that there is a well formed HTTPS
             * session.
             */
            if (validSSLSessionNeeded) {
                checkHttpForValidSSLSession(req);
            }

            if (validVASASessionNeeded) {
                checkHttpForValidVASASession();
            }
            return clientAddress;
        } catch (InvalidCertificate ic) {
            LOGGER.error("InvalidSession/Non trusted certificate.");
            throw FaultUtil.invalidCertificate("Non trusted certificate.", ic);
            //throw FaultUtil.invalidSession("Non trusted certificate.", ic);
        } catch (InvalidSession is) {
            throw is;
        } catch (Exception e) {
            LOGGER.error("InvalidSession/checkHttpSession unexpected exception. Convert to InvalidSession.", e);
            throw FaultUtil.invalidSession("checkHttpSession unexpected exception. Convert to InvalidSession.", e);
        }
    }


    /**
     * checkHttpRequest
     * <p>
     * The term "Session" is overloaded. A Session can refer to either a SSL
     * session or it can refer to a VASA session.
     * <p>
     * If there is an error in either of the Session configurations, then this
     * routine will throw the InvalidSession expection.
     *
     * @param validSSLSessionNeeded  方法参数：validSSLSessionNeeded
     * @param validVASASessionNeeded 方法参数：validVASASessionNeeded
     * @return String 返回结果
     * @throws InvalidSession     异常：InvalidSession
     * @throws InvalidCertificate
     */
    public String checkHttpRequest(boolean validSSLSessionNeeded, boolean validVASASessionNeeded)
            throws InvalidSession {
        try {
            /*
             * Check for a valid context.
             */
            MessageContext mc = ThreadLocalHolder.get();
            if (mc == null) {
                LOGGER.error("InvalidSession/No current message context");
                throw FaultUtil.invalidSession("No current message context");
            }

            HttpServletRequest req = (HttpServletRequest) mc.getEntities().get("HTTP.REQUEST");

            if (req == null) {
                LOGGER.error("InvalidSession/No HTTP Servlet Request");
                throw FaultUtil.invalidSession("No HTTP Servlet Request");
            }
            String clientAddress = req.getRemoteAddr();
            VASAUtil.saveCurrEsxIp(clientAddress);
            /**
             * Get SSL data
             */
            String sslSessionId = (String) req.getAttribute("javax.servlet.request.ssl_session");
            if (sslSessionId == null) {
                /**
                 * This is not an SSL connection. If the service is not allowing
                 * none-SSL connections, throw an exception. Otherwise check for
                 * a valid VASA session if necessary.
                 */
                if (!mustUseSSL) {
                    if (validVASASessionNeeded) {
                        checkHttpForValidVASASession();
                    }
                    return clientAddress;
                } else {
                    LOGGER.error("InvalidSession/Must use SSL connection");
                    throw FaultUtil.invalidSession("Must use SSL connection");
                }
            }

            /*
             * At this point, it is known that there is a well formed HTTPS
             * session.
             */
            if (validSSLSessionNeeded) {
                checkHttpForValidSSLSession(req);
            }

            if (validVASASessionNeeded) {
                checkHttpForValidVASASession();
            }
            return clientAddress;
        } catch (InvalidCertificate ic) {
            LOGGER.error("InvalidSession/Non trusted certificate.");
            //throw FaultUtil.invalidCertificate("Non trusted certificate.", ic);
            throw FaultUtil.invalidSession("Non trusted certificate.", ic);
        } catch (InvalidSession is) {
            throw is;
        } catch (Exception e) {
            LOGGER.error("InvalidSession/checkHttpSession unexpected exception. Convert to InvalidSession.", e);
            throw FaultUtil.invalidSession("checkHttpSession unexpected exception. Convert to InvalidSession.", e);
        }
    }

    /**
     * getCertificateThumbprint
     *
     * @param cert 方法参数：cert
     * @return String 返回结果
     * @throws InvalidArgument 异常：InvalidArgument
     */
    public String getCertificateThumbprint(Certificate cert) throws InvalidArgument {

        // Compute the SHA-1 hash of the certificate.
        try {
            byte[] encoded;
            try {
                encoded = cert.getEncoded();
            } catch (CertificateEncodingException cee) {
                throw FaultUtil.invalidArgument("Error reading certificate encoding: " + cee.getMessage(), cee);
            }

            MessageDigest sha1;
            try {
                sha1 = MessageDigest.getInstance("SHA-1");
            } catch (NoSuchAlgorithmException e) {
                throw FaultUtil.invalidArgument("Could not instantiate SHA-1 hash algorithm", e);
            }
            sha1.update(encoded);
            byte[] hash = sha1.digest();

            if (hash.length != HASH_LENGTH) {
                throw FaultUtil.invalidArgument(
                        "Computed thumbprint is " + hash.length + " bytes long, expected " + HASH_LENGTH);
            }

            StringBuilder thumbprintString = new StringBuilder(hash.length * MagicNumber.INT3);
            for (int i = 0; i < hash.length; i++) {
                if (i > 0) {
                    thumbprintString.append(":");
                }
                String hexByte = Integer.toHexString(MagicNumber.INT0XFF & (int) hash[i]);
                if (hexByte.length() == 1) {
                    thumbprintString.append("0");
                }
                thumbprintString.append(hexByte);
            }

            return thumbprintString.toString().toUpperCase();
        } catch (InvalidArgument ia) {
            throw ia;
        } catch (Exception e) {
            throw FaultUtil.invalidArgument("Exception: " + e);
        }
    }

    /**
     * buildCertificate Build a certificate from a Base64 formatted, PKCS#7
     * encoding of the certificate
     *
     * @param certString 方法参数：certString
     * @return Certificate 返回结果
     * @throws InvalidCertificate 异常：InvalidCertificate
     * @throws InvalidLogin
     */
    public Certificate buildCertificate(String certString) throws InvalidCertificate {
        try {
            String base64Cert = formatCertificate(certString);
            InputStream inBytes = new ByteArrayInputStream(base64Cert.getBytes("utf-8"));
            CertificateFactory cf = CertificateFactory.getInstance("X.509");

            assert inBytes.available() > 0;
            Certificate certificate = cf.generateCertificate(inBytes);
            inBytes.close();
            return certificate;
        } catch (FileNotFoundException e) {
            LOGGER.error("buildCertificate FileNotFoundException error ", e);
            throw FaultUtil.invalidCertificate("Could not build certificate", e);
        } catch (CertificateException e) {
            LOGGER.error("buildCertificate CertificateException error ", e);
            throw FaultUtil.invalidCertificate("Could not build certificate", e);
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("buildCertificate UnsupportedEncodingException error ", e);
            throw FaultUtil.invalidCertificate("Could not build certificate", e);
        } catch (IOException e) {
            LOGGER.error("buildCertificate IOException error ", e);
            throw FaultUtil.invalidCertificate("Could not build certificate", e);
        }
    }

    public String formatCertificate(String cert) throws InvalidCertificate {
        if (null == cert) {
            throw FaultUtil.invalidCertificate();
        }
        final String headerTemp = "-----BEGIN CERTIFICATE-----";
        final String footerTemp = "-----END CERTIFICATE-----";

        if (cert.trim().startsWith(headerTemp)) {
            return cert;
        }

        StringBuffer sb = new StringBuffer();
        sb.append(headerTemp);
        sb.append('\n');
        sb.append(cert.trim());
        sb.append('\n');
        sb.append(footerTemp);

        return sb.toString();
    }

    /**
     * Format of the alias is: "vpc-<integer>" For example, "vpc-3"
     */
    private String getAlias(String clientAddress) throws InvalidCertificate {
        int count = 0;
        String certAliasBase = "vpc-";

        String certAlias = certAliasBase.concat(Integer.toString(count));
        while (getCertificateFromAlias(certAlias) != null) {
            /**
             * Need to make sure that certAlias is not already in the
             * trustStore. If it is, create a different alias so as not to
             * overwrite an existing certificate.
             */
            count++;
            certAlias = certAliasBase.concat(Integer.toString(count));
        }

        LOGGER.debug("getCertificateFromAlias() " + certAlias + " for certificate from " + clientAddress);
        return certAlias;
    }

    /**
     * addCertifcateToTrustStore
     *
     * @param certToAdd    方法参数：certToAdd
     * @param certNameRoot 方法参数：certNameRoot
     * @throws InvalidArgument 异常：InvalidArgument
     */
    public void addCertificateToTrustStore(String alias, String clientAddress, Certificate paramCertificate)
            throws InvalidArgument {
        FileInputStream fileInputStream = null;
        FileOutputStream out = null;

        KeyStore localKeyStore;
        try {
            localKeyStore = KeyStore.getInstance(trustStoreType);
        } catch (Exception e) {
            throw FaultUtil.invalidArgument("Exception " + e);
        }

        try {
            fileInputStream = new FileInputStream(trustStoreFileName);
            localKeyStore.load(fileInputStream, trustStorePassword.toCharArray());
        } catch (Exception e) {
            throw FaultUtil.invalidArgument("Exception " + e);
        } finally {
            closeFileInputStream(fileInputStream);
        }

        try {
            if (alias == null) {
                alias = getAlias(clientAddress);
                localKeyStore.setCertificateEntry(alias, paramCertificate);
            } else {
                Key localObject = localKeyStore.getKey(alias, trustStorePassword.toCharArray());

                Certificate[] arrayOfCertificate = new Certificate[1];
                arrayOfCertificate[0] = paramCertificate;
                localKeyStore.setKeyEntry(alias, (Key) localObject, trustStorePassword.toCharArray(),
                        arrayOfCertificate);
            }

            out = new FileOutputStream(trustStoreFileName);
            localKeyStore.store(out, trustStorePassword.toCharArray());
        } catch (Exception e) {
            throw FaultUtil.invalidArgument("Exception " + e);
        } finally {
            closeFileOutPutStream(out);
        }
    }

    public void saveCRL(String strCRL) throws StorageFault {
        OutputStreamWriter osw = null;
        OutputStream os = null;
        try {
            os = new FileOutputStream(new File(DEFAULT_CRL_FILENAME));
            // @SuppressWarnings("resource")
            osw = new OutputStreamWriter(os, "UTF-8");

            osw.write(strCRL);

        } catch (FileNotFoundException e) {
            LOGGER.error("not found signedCert:" + DEFAULT_CRL_FILENAME);
            throw FaultUtil.storageFault("Exception ", e);
        } catch (IOException e) {
            LOGGER.error("write to signedCert IO exception!");
            throw FaultUtil.storageFault("Exception ", e);
        } finally {
            try {
                // CodeDEX问题修改 ： FORTIFY.Unreleased_Resource--Streams
                // wwX315527 2016/11/17
                if (os != null) {
                    os.close();
                }
                if (osw != null) {
                    osw.close();
                }
                // CodeDEX问题修改 ： FORTIFY.Unreleased_Resource--Streams
                // wwX315527 2016/11/19
            } catch (IOException e) {
                LOGGER.error("failed to close isr");
            }
        }
    }

    /**
     * <ICP修改> 关闭文件输出流
     *
     * @param out
     * @return void [返回类型说明]
     * @throws InvalidArgument [参数说明]
     * @throws throws          [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    private void closeFileOutPutStream(FileOutputStream out) throws InvalidArgument {
        if (null != out) {
            try {
                out.close();
            } catch (IOException e) {
                throw FaultUtil.invalidArgument("Exception " + e);
            }
        }
    }

    /**
     * <ICP修改> 关闭文件输入流
     *
     * @param fileInputStream
     * @return void [返回类型说明]
     * @throws InvalidArgument [参数说明]
     * @throws throws          [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    private void closeFileInputStream(FileInputStream fileInputStream) throws InvalidArgument {
        if (null != fileInputStream) {
            try {
                fileInputStream.close();
            } catch (IOException e) {
                throw FaultUtil.invalidArgument("Exception " + e);
            }
        }
    }

    /**
     * removeCertifcateFromTrustStore
     *
     * @param certToRemove 方法参数：certToRemove
     * @throws InvalidArgument 异常：InvalidArgument
     */
    public void removeCertificateFromTrustStore(Certificate paramCertificate) throws InvalidArgument {
        FileInputStream localFileInputStream = null;
        // Object localObjectFileOut = null;
        FileOutputStream localObjectFileOut = null;
        try {
            KeyStore localKeyStore = KeyStore.getInstance("JKS");
            localFileInputStream = new FileInputStream(this.trustStoreFileName);
            localKeyStore.load(localFileInputStream, this.trustStorePassword.toCharArray());
            // localFileInputStream.close();
            Enumeration localEnumeration = localKeyStore.aliases();
            while (localEnumeration.hasMoreElements()) {
                String localObject = (String) localEnumeration.nextElement();
                if (localKeyStore.isCertificateEntry((String) localObject)) {
                    X509Certificate localX509Certificate = (X509Certificate) localKeyStore
                            .getCertificate((String) localObject);
                    if (localX509Certificate.equals(paramCertificate))
                        localKeyStore.deleteEntry((String) localObject);
                }
            }
            localObjectFileOut = new FileOutputStream(this.trustStoreFileName);
            localKeyStore.store((OutputStream) localObjectFileOut, this.trustStorePassword.toCharArray());
            // ((FileOutputStream)localObjectFileOut).close();
        } catch (Exception localException) {
            LOGGER.error(new StringBuilder().append("Exception ").append(localException).toString());
            throw FaultUtil.invalidArgument(new StringBuilder().append("Exception ").append(localException).toString());
        } finally {

            /**
             * 修改CodeDEX问题：FORTIFY.Unreleased_Resource--Streams Modified by
             * wWX315527 2016/11/19
             */
            try {
                if (null != localObjectFileOut) {
                    localObjectFileOut.close();
                }
            } catch (IOException e) {
                LOGGER.error("failed to close localObjectFileOut" + e);
            }
            try {
                if (null != localFileInputStream) {
                    localFileInputStream.close();
                }
            } catch (IOException e) {
                LOGGER.error("failed to close localFileInputStream" + e);
            }

            /**
             * 修改CodeDEX问题：FORTIFY.Unreleased_Resource--Streams Modified by
             * wWX315527 2016/11/19
             */
        }
    }

    /**
     * certificateIsTrusted
     *
     * @param certToCheck 方法参数：certToCheck
     * @return boolean 返回结果
     * @throws InvalidCertificate 异常：InvalidCertificate
     */
    public boolean certificateIsTrusted(Certificate paramCertificate) throws InvalidCertificate {
        FileInputStream localFileInputStream = null;
        try {
            KeyStore localKeyStore = KeyStore.getInstance("JKS");
            localFileInputStream = new FileInputStream(this.trustStoreFileName);
            localKeyStore.load(localFileInputStream, this.trustStorePassword.toCharArray());
            // localFileInputStream.close();
            Enumeration localEnumeration = localKeyStore.aliases();
            while (localEnumeration.hasMoreElements()) {
                String str = (String) localEnumeration.nextElement();
                if (localKeyStore.isCertificateEntry(str)) {
                    X509Certificate localX509Certificate = (X509Certificate) localKeyStore.getCertificate(str);
                    try {
                        localX509Certificate.checkValidity();
                        if (localX509Certificate.equals(paramCertificate))
                            return true;
                    } catch (Exception localException2) {
                        LOGGER.error(new StringBuilder().append("Certificate ").append(str).append(" is not valid.")
                                .toString());
                    }
                }
            }
            LOGGER.info("certificateIsTrusted: no certificate matches");
            return false;
        } catch (Exception localException1) {
            throw FaultUtil
                    .invalidCertificate(new StringBuilder().append("Exception: ").append(localException1).toString());
        } finally {
            /**
             * 修改CodeDEX问题：FORTIFY.Unreleased_Resource--Streams Modified by
             * wWX315527 2016/11/19
             */
            try {
                if (localFileInputStream != null) {
                    localFileInputStream.close();
                }
            } catch (IOException e) {
                LOGGER.error("failed to close localFileInputStream");
            }
            /**
             * 修改CodeDEX问题：FORTIFY.Unreleased_Resource--Streams Modified by
             * wWX315527 2016/11/19
             */
        }
    }

    public boolean isProviderCertSelfSigned() throws InvalidCertificate {
        java.io.FileInputStream fis = null;
        try {
            KeyStore ks = KeyStore.getInstance(trustStoreType);

            fis = new java.io.FileInputStream(trustStoreFileName);
            ks.load(fis, trustStorePassword.toCharArray());

            // get my private key
            KeyStore.PrivateKeyEntry pkEntry = (KeyStore.PrivateKeyEntry) ks.getEntry("server",
                    new KeyStore.PasswordProtection(trustStorePassword.toCharArray()));
            String cn = null;
            if (pkEntry != null) {
                X509Certificate cert = (X509Certificate) pkEntry.getCertificate();
                X500Principal issuerPrincipal = cert.getIssuerX500Principal();
                String name = issuerPrincipal.getName();
                LOGGER.info("issuer:" + name);
                String[] items = name.split(",");
                for (String item : items) {
                    if (item.contains("CN=")) {
                        cn = item.split("=")[1];
                    }
                }
            }
            if (cn != null && cn.equalsIgnoreCase("CA")) {
                return false;
            } else {
                return true;
            }

        } catch (KeyStoreException e) {
            throw FaultUtil.invalidCertificate("Exception: " + e);
        } catch (FileNotFoundException e) {
            throw FaultUtil.invalidCertificate("Exception: " + e);
        } catch (NoSuchAlgorithmException e) {
            throw FaultUtil.invalidCertificate("Exception: " + e);
        } catch (CertificateException e) {
            throw FaultUtil.invalidCertificate("Exception: " + e);
        } catch (IOException e) {
            throw FaultUtil.invalidCertificate("Exception: " + e);
        } catch (UnrecoverableEntryException e) {
            throw FaultUtil.invalidCertificate("Exception: " + e);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    throw FaultUtil.invalidCertificate("Exception: " + e);
                }
            }
        }
    }

    /**
     * getCertificateAlias
     *
     * @param cert 方法参数：cert
     * @return String 返回结果
     * @throws InvalidCertificate 异常：InvalidCertificate
     */
    public String getCertificateAlias(Certificate paramCertificate) throws InvalidCertificate {
        FileInputStream localFileInputStream = null;
        try {
            KeyStore localKeyStore = KeyStore.getInstance("JKS");
            localFileInputStream = new FileInputStream(this.trustStoreFileName);
            localKeyStore.load(localFileInputStream, this.trustStorePassword.toCharArray());
            // localFileInputStream.close();
            return localKeyStore.getCertificateAlias(paramCertificate);
        } catch (Exception localException) {
            throw FaultUtil
                    .invalidCertificate(new StringBuilder().append("Exception: ").append(localException).toString());
        } finally {
            /**
             * 修改CodeDEX问题：FORTIFY.Unreleased_Resource--Streams Modified by
             * wWX315527 2016/11/19
             */
            try {
                if (localFileInputStream != null) {
                    localFileInputStream.close();
                }
            } catch (IOException e) {
                LOGGER.error("failed to close localFileInputStream");
            }
            /**
             * 修改CodeDEX问题：FORTIFY.Unreleased_Resource--Streams Modified by
             * wWX315527 2016/11/19
             */
        }
    }

    public List<String> getAllCertificatesFromKeystore() throws StorageFault {
        FileInputStream is = null;
        List<String> certs = new ArrayList<String>();

        try {
            KeyStore ts = KeyStore.getInstance(trustStoreType);
            is = new FileInputStream(trustStoreFileName);

            ts.load(is, trustStorePassword.toCharArray());

            Enumeration<String> aliases = ts.aliases();
            while (aliases.hasMoreElements()) {
                String alias = aliases.nextElement();
                if (ts.isCertificateEntry(alias)) {
                    /**
                     * add certificate
                     */
                    String cert = Base64Utils.encode(getCertificateFromAlias(alias).getEncoded());
                    certs.add(formatCertificate(cert));
                }
            }

            // get my private key
            KeyStore.PrivateKeyEntry pkEntry = (KeyStore.PrivateKeyEntry) ts.getEntry("server",
                    new KeyStore.PasswordProtection(trustStorePassword.toCharArray()));
            if (pkEntry != null) {
                X509Certificate cert = (X509Certificate) pkEntry.getCertificate();
                certs.add(formatCertificate(Base64Utils.encode(cert.getEncoded())));
            }
        } catch (KeyStoreException e) {
            throw FaultUtil.storageFault("Exception: " + e);
        } catch (NoSuchAlgorithmException e) {
            throw FaultUtil.storageFault("Exception: " + e);
        } catch (CertificateException e) {
            throw FaultUtil.storageFault("Exception: " + e);
        } catch (InvalidCertificate e) {
            throw FaultUtil.storageFault("Exception: " + e);
        } catch (IOException e) {
            throw FaultUtil.storageFault("Exception: " + e);
        } catch (UnrecoverableEntryException e) {
            throw FaultUtil.storageFault("Exception: " + e);
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    throw FaultUtil.storageFault("Exception: " + e);
                }
            }
        }

        return certs;
    }

    /**
     * getCertificateFromAlias return the certificate corresponding to this
     * alias
     *
     * @param certAlias 方法参数：certAlias
     * @return Certificate 返回结果
     * @throws InvalidCertificate 异常：InvalidCertificate
     */
    public Certificate getCertificateFromAlias(String paramString) throws InvalidCertificate {
        FileInputStream localFileInputStream = null;
        try {
            KeyStore localKeyStore = KeyStore.getInstance("JKS");
            localFileInputStream = new FileInputStream(this.trustStoreFileName);
            localKeyStore.load(localFileInputStream, this.trustStorePassword.toCharArray());
            localFileInputStream.close();
            return localKeyStore.getCertificate(paramString);
        } catch (Exception localException) {
            throw FaultUtil
                    .invalidCertificate(new StringBuilder().append("Exception: ").append(localException).toString());
        } finally {
            /**
             * 修改CodeDEX问题：FORTIFY.Unreleased_Resource--Streams Modified by
             * wWX315527 2016/11/19
             */
            try {
                if (localFileInputStream != null) {
                    localFileInputStream.close();
                }
            } catch (IOException e) {
                LOGGER.error("failed to close localFileInputStream");
            }
            /**
             * 修改CodeDEX问题：FORTIFY.Unreleased_Resource--Streams Modified by
             * wWX315527 2016/11/19
             */
        }
    }

    /**
     * thumbprintIsTrusted
     *
     * @param thumbprint 方法参数：thumbprint
     * @throws InvalidCertificate 异常：InvalidCertificate
     */
    public void thumbprintIsTrusted(String paramString) throws InvalidCertificate {
        FileInputStream localFileInputStream = null;
        try {
            KeyStore localKeyStore = KeyStore.getInstance("JKS");
            localFileInputStream = new FileInputStream(this.trustStoreFileName);
            localKeyStore.load(localFileInputStream, this.trustStorePassword.toCharArray());
            // localFileInputStream.close();
            Enumeration localEnumeration = localKeyStore.aliases();
            while (localEnumeration.hasMoreElements()) {
                String str = (String) localEnumeration.nextElement();
                if (localKeyStore.isCertificateEntry(str)) {
                    X509Certificate localX509Certificate = (X509Certificate) localKeyStore.getCertificate(str);
                    if (paramString.equals(getCertificateThumbprint(localKeyStore.getCertificate(str))))
                        try {
                            localX509Certificate.checkValidity();
                            return;
                        } catch (Exception localException2) {
                            throw FaultUtil.invalidCertificate("cert with thumprint is not valid", localException2);
                        }
                }
            }
            throw FaultUtil.invalidCertificate("could not find certifcate that matches thumbprint");
        } catch (InvalidCertificate localInvalidCertificate) {
            throw localInvalidCertificate;
        } catch (Exception localException1) {
            throw FaultUtil
                    .invalidCertificate(new StringBuilder().append("Exception: ").append(localException1).toString());
        } finally {
            /**
             * 修改CodeDEX问题：FORTIFY.Unreleased_Resource--Streams Modified by
             * wWX315527 2016/11/19
             */
            try {
                if (localFileInputStream != null) {
                    localFileInputStream.close();
                }
            } catch (IOException e) {
                LOGGER.error("failed to close localFileInputStream");
            }
            /**
             * 修改CodeDEX问题：FORTIFY.Unreleased_Resource--Streams Modified by
             * wWX315527 2016/11/19
             */
        }
    }

    /**
     * Stop and restart the SSL connection so that the tomcat server will
     * re-read the certificates from the truststore file.
     *
     * @throws MBeanException               异常：MBeanException
     * @throws ReflectionException          异常：ReflectionException
     * @throws AttributeNotFoundException   异常：AttributeNotFoundException
     * @throws IntrospectionException       异常：IntrospectionException
     * @throws InstanceNotFoundException    异常：InstanceNotFoundException
     * @throws MalformedObjectNameException 异常：MalformedObjectNameException
     */
    public static void refreshTrustStore() throws Exception {
        MBeanServer localMBeanServer = MBeanUtils.createServer();
        Set localSet = localMBeanServer.queryNames(new ObjectName("*:*"), null);
        Iterator localIterator = localSet.iterator();
        while (localIterator.hasNext()) {
            try {
                ObjectName localObjectName = (ObjectName) localIterator.next();
                MBeanInfo localMBeanInfo = localMBeanServer.getMBeanInfo(localObjectName);
                if (localMBeanInfo.getClassName().equals("org.apache.catalina.mbeans.ConnectorMBean")) {
                    String str = (String) localMBeanServer.getAttribute(localObjectName, "protocol");
                    LOGGER.debug("protocol:" + str);
                    if (str.toLowerCase().startsWith("http")) {
                        int i = ((localMBeanServer.getAttribute(localObjectName, "secure") != null) && (localMBeanServer
                                .getAttribute(localObjectName, "secure").toString().equalsIgnoreCase("true"))) ? 1 : 0;
                        int j = ((localMBeanServer.getAttribute(localObjectName, "scheme") != null) && (localMBeanServer
                                .getAttribute(localObjectName, "scheme").toString().equalsIgnoreCase("https"))) ? 1 : 0;
                        if ((i != 0) && (j != 0)) {
                            LOGGER.debug(new StringBuilder().append("Restarting SSL Connector on port ")
                                    .append(localMBeanServer.getAttribute(localObjectName, "port")).toString());
                            Object[] arrayOfObject = new Object[0];
                            String[] arrayOfString = new String[0];
                            localMBeanServer.invoke(localObjectName, "stop", arrayOfObject, arrayOfString);
                            localMBeanServer.invoke(localObjectName, "start", arrayOfObject, arrayOfString);
                        }
                    }
                }
            } catch (InstanceNotFoundException localException) {
                LOGGER.warn(new StringBuilder().append("Did not find mbean: ").append(localException).toString());
                continue;
            } catch (Exception localException) {
                LOGGER.error(new StringBuilder().append("Did not restart SSL Connector: ").append(localException)
                        .toString());
                throw localException;
            }
        }

    }

    public void generateCSR() throws StorageFault {
        // CodeDEX问题修改 ：FORTIFY.Missing_Check_against_Null
        // wwX315527 2016/11/17
        // String os = System.getProperty("os.name").toLowerCase();
        String os = "";
        String result = System.getProperty("os.name");
        if (result != null) {
            os = result.toLowerCase();
        }
        // CodeDEX问题修改 ：FORTIFY.Missing_Check_against_Null
        // wwX315527 2016/11/17

        LOGGER.debug("--------OS-----------" + os);
        LOGGER.debug("DEFAULT_CSR_FILENAME:" + DEFAULT_CSR_FILENAME);
        LOGGER.debug("trustStoreFileName:" + trustStoreFileName);
        try {
            if (os.contains("windows")) {
                /**
                 * codeDEX[Command Injection] start
                 */
                LOGGER.error("could not buid CSR on windows platform.");
                throw FaultUtil.storageFault("cound not build CSR");
                /**
                 * codeDEX[Command Injection] end
                 */
            } else if (os.contains("linux")) {
                String ipAddress = "";
                CommandsUtil.execCommand("dir").toString();

                String intaller = dataUtil.getVasaInfoMapByKey("InstallType");
                if (intaller.equalsIgnoreCase("staas")) {
                    String staasFloatIp = ConfigManager.getInstance().getValue("vasa.staas.floatIp");
                    if (!StringUtils.isEmpty(staasFloatIp)) {
                        ipAddress = staasFloatIp;
                    } else {
                        LOGGER.error("the staas float ip is null");
                    }
//            		String commands = "get_info.py --manage_float_ip";
//            		CommandResult commandResult = CommandsUtil.execCommand(commands);
//            		if(commandResult.result != 0){
//            			LOGGER.error("StorageFault/cound not build CSR");
//            			throw FaultUtil.storageFault("cound not build CSR");
//            		}
//            		ipAddress = commandResult.responseMsg;
                } else {
                    ipAddress = IPUtil.getLocalIP();
                }

                String[] cmds =
                        {
                                FileManager.getBasePath() + "../uninstall/jre/jre_linux/bin/keytool", "-certreq", "-alias", "server", "-sigalg", "MD5withRSA", "-file", DEFAULT_CSR_FILENAME,
                                "-keypass", trustStorePassword, "-keystore", trustStoreFileName, "-ext", "san=ip:" + ipAddress,
                                "-storepass", trustStorePassword
                                /*
                                FileManager.getBasePath() + "../uninstall/jre/jre_linux/bin/keytool", "-certreq", "-alias", "server", "-sigalg", "MD5withRSA", "-file", DEFAULT_CSR_FILENAME,
                                "-keypass", trustStorePassword, "-keystore", trustStoreFileName, "-ext","san=ip:"+ getLocalIpAddress(),
                                "-storepass", trustStorePassword */
                        };
                /**
                 * codeDEX[Command Injection] start
                 */
                cmds = VASAUtil.commandFormat(cmds);
                /**
                 * codeDEX[Command Injection] start
                 */
                Process process = Runtime.getRuntime().exec(cmds);
                process.waitFor();

            }
        } catch (Exception e) {
            LOGGER.error("StorageFault/cound not build CSR");
            throw FaultUtil.storageFault("cound not build CSR");
        }
    }

    public String loadCSR() throws StorageFault {
        InputStreamReader isr = null;
        InputStream is = null;
        try {
            is = new FileInputStream(new File(DEFAULT_CSR_FILENAME));
            // @SuppressWarnings("resource")
            isr = new InputStreamReader(is, "UTF-8");
            int ch = 0;

            StringBuilder sb = new StringBuilder();

            while ((ch = isr.read()) != -1) {
                String str = (char) ch + "";
                sb.append(str);
            }

            return sb.toString();

        } catch (FileNotFoundException e) {
            LOGGER.error("StorageFault/FileNotFoundException error.");
            throw FaultUtil.storageFault("Exception ", e);
        } catch (IOException e) {
            LOGGER.error("StorageFault/IOException error.");
            throw FaultUtil.storageFault("Exception ", e);
        } finally {
            try {
                /**
                 * 修改CodeDEX问题：FORTIFY.Unreleased_Resource start Modified by
                 * wWX315527 2016/11/19
                 */
                if (is != null) {
                    is.close();
                }
                if (isr != null) {
                    isr.close();
                }
                /**
                 * 修改CodeDEX问题：FORTIFY.Unreleased_Resource end Modified by
                 * wWX315527 2016/11/19
                 */
            } catch (IOException e) {
                LOGGER.error("failed to close isr");
            }
        }

    }

    public String loadCASignedCertificate() throws StorageFault {
        InputStreamReader isr = null;
        InputStream is = null;
        try {
            is = new FileInputStream(new File(DEFAULT_CASIGNED_FILENAME));
            // @SuppressWarnings("resource")
            isr = new InputStreamReader(is, "UTF-8");
            int ch = 0;

            StringBuilder sb = new StringBuilder();

            while ((ch = isr.read()) != -1) {
                // System.out.print((char)ch);
                String str = (char) ch + "";
                sb.append(str);
            }

            // return formatCSR(sb.toString());
            return sb.toString();

        } catch (FileNotFoundException e) {
            throw FaultUtil.storageFault("Exception ", e);
        } catch (IOException e) {
            throw FaultUtil.storageFault("Exception ", e);
        } finally {
            try {
                // CodeDEX问题修改 ：FORTIFY.Unreleased_Resource--Streams
                // wwX315527 2016/11/17
                if (is != null) {
                    is.close();
                }
                if (isr != null) {
                    isr.close();
                }
                // CodeDEX问题修改 ：FORTIFY.Unreleased_Resource--Streams
                // wwX315527 2016/11/19
            } catch (IOException e) {
                LOGGER.error("failed to close isr");
            }
        }

    }

}
