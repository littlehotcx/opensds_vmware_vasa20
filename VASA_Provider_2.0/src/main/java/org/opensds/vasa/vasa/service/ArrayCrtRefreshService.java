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

package org.opensds.vasa.vasa.service;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStore.PrivateKeyEntry;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensds.vasa.common.MagicNumber;

import org.opensds.platform.common.config.ConfigManager;
import org.opensds.platform.common.config.SSLConfigManager;
import org.opensds.platform.common.utils.AES128System;
import org.opensds.platform.common.utils.VasaRedisKey;
import org.opensds.vasa.vasa.db.model.NArrayCertificate;
import org.opensds.vasa.vasa.db.model.NMultiVcCertificate;
import org.opensds.vasa.vasa.db.model.VasaOperation;
import org.opensds.vasa.vasa.db.model.VasaProperty;
import org.opensds.vasa.vasa.db.service.ArrayCertificateService;
import org.opensds.vasa.vasa.db.service.VasaOperationService;
import org.opensds.vasa.vasa.db.service.VasaPropertyService;
import org.opensds.vasa.vasa.util.ByteConvertUtil;
import org.opensds.vasa.vasa.util.DateUtil;
import org.opensds.vasa.vasa.util.IPUtil;
import org.opensds.vasa.vasa.util.RedisUtil;
import org.opensds.vasa.vasa.util.SSLUtil;

import com.vmware.vim.vasa.v20.StorageFault;

import sun.security.x509.CertificateExtensions;
import sun.security.x509.CertificateIssuerName;
import sun.security.x509.CertificateValidity;
import sun.security.x509.GeneralName;
import sun.security.x509.GeneralNames;
import sun.security.x509.IPAddressName;
import sun.security.x509.KeyIdentifier;
import sun.security.x509.SubjectAlternativeNameExtension;
import sun.security.x509.SubjectKeyIdentifierExtension;
import sun.security.x509.X500Name;
import sun.security.x509.X509CertImpl;
import sun.security.x509.X509CertInfo;

public class ArrayCrtRefreshService {
    private static String array_keystore_path = "/opt/Huawei/eSDK/esdk/conf/array.keystore";
    private static String vasa_keystore_path = "/opt/Huawei/eSDK/esdk/conf/server.keystore";
    private ArrayCertificateService arrayCertificateService;
    private VasaOperationService vasaOperationService;
    private VasaPropertyService vasaPropertyService;
    private static Logger logger = LogManager.getLogger(ArrayCrtRefreshService.class);

    private static String server_userDN = "CN=CN, OU=Opensds, O=Opensds, L=SuZhou, ST=JiangSu, C=CN";
    private static String server_alias = "server";

    public void startAction() throws Exception {
        refreshArrayCrt();
        loadVasaPropertyIntoRedis();
        checkRestoreServerKeystore();
        String installType = ConfigManager.getInstance().getValue("vasa.install.type");
        if (installType != null && installType.equalsIgnoreCase("staas")) {
            checkMultiVcCertificate();
        }
    }

    private void checkMultiVcCertificate() throws Exception {

        try {
            VasaProperty propertyByName = vasaPropertyService.getPropertyByName(VasaProperty.MULTI_VC_KEYSTORE_KEY + "_" + IPUtil.getLocalIP());
            if (null == propertyByName) {
                NMultiVcCertificate multiVcCertificate = arrayCertificateService.getMultiVcCertificate();
                if (null == multiVcCertificate) {
                    reCreateServerKeystore(ConfigManager.getInstance().getValue("vasa.staas.floatIp"));
                    byte[] bytes = ByteConvertUtil.file2byte(vasa_keystore_path);
                    NMultiVcCertificate certificate = new NMultiVcCertificate();
                    certificate.setCacontent(bytes);
                    certificate.setCreateTime(DateUtil.getUTCDate());
                    arrayCertificateService.saveMultiVcCertificate(certificate);
                } else {
                    ByteConvertUtil.byte2file(multiVcCertificate.getCacontent(), vasa_keystore_path);
                }
                saveMultVcProperty();
                new Timer("refreshTrustStore-Timer", true).schedule(new TimerTask() {
                    @Override
                    public void run() {
                        try {
                            SSLUtil.refreshTrustStore();
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }, MagicNumber.INT30 * MagicNumber.INT1000);
            }
        } catch (StorageFault e) {
            // TODO Auto-generated catch block
            logger.error("IPUtil.getLocalIP error!!", e);
            throw e;
        }

    }

    private void saveMultVcProperty() throws StorageFault {
        VasaProperty property = new VasaProperty();
        property.setName(VasaProperty.MULTI_VC_KEYSTORE_KEY + "_" + IPUtil.getLocalIP());
        property.setType("boolean");
        property.setValue("true");
        vasaPropertyService.save(property);
    }

    private void loadVasaPropertyIntoRedis() {
        // TODO Auto-generated method stub
        VasaProperty propertyByName = vasaPropertyService.getPropertyByName(VasaProperty.RETAIN_VP_CERTIFICATE_KEY);
        if (null != propertyByName) {
            if (!RedisUtil.checkExist(VasaRedisKey.multi_vc_config_value_key)) {
                RedisUtil.setStringKeyValue(VasaRedisKey.multi_vc_config_value_key, propertyByName.getValue());
            }
        }
    }

    public void refreshArrayCrt() {
        logger.info("Begin init array keystore.");
        List<NArrayCertificate> getall = arrayCertificateService.getall();
        logger.info("current arraycrt size = " + getall.size());
        char[] pwdCharArray = null;
        KeyStore keystore = null;
        try {
            keystore = KeyStore.getInstance(KeyStore.getDefaultType());
            String pwd = SSLConfigManager.getProperty("vasa.ssl.array.keystorePass");
            pwdCharArray = pwd.toCharArray();

            //修改为从array.keystore中读取
            InputStream inputStream = new FileInputStream(array_keystore_path);
            keystore.load(inputStream, pwdCharArray);


            //keystore.load(null, pwdCharArray);
        } catch (Exception e) {
            logger.error("load storage array keystore fail :", e);
        }
        boolean needRefresh = false;
        for (NArrayCertificate nArrayCertificate : getall) {
            try {
                CertificateFactory cf = CertificateFactory.getInstance("X.509");
                InputStream inStream = new ByteArrayInputStream(nArrayCertificate.getCacontent());
                X509Certificate arrayX509Certificate = (X509Certificate) cf.generateCertificate(inStream);
                inStream.close();
                keystore.setCertificateEntry(nArrayCertificate.getId() + "_crt", arrayX509Certificate);
                needRefresh = true;
            } catch (Exception e) {
                logger.error("store cert fail :", e);
            }

        }
        if (needRefresh) {
            try {
                FileOutputStream outputStream = new FileOutputStream(array_keystore_path);
                keystore.store(outputStream, pwdCharArray);
                outputStream.close();
            } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
			/*File keyStoreBack = new File(array_keystore_back_path);
			File keyStore = new File(array_keystore_path);
			if(keyStore.exists()){
				keyStore.delete();
			}
			keyStoreBack.renameTo(keyStore);*/
        }
        logger.info("End init array keystore.");
    }

    public void checkRestoreServerKeystore() {
        try {
            String ip = IPUtil.getLocalIP();
            VasaOperation t = new VasaOperation();
            t.setKey(VasaOperation.RELOAD_SERVER_KEYSTORE_KEY);
            t.setIp(ip);
            VasaOperation dataByKey = vasaOperationService.getDataByKey(t);
            if (null == dataByKey) {
                logger.info("ArrayCrtRefreshService/checkRestoreServerKeystore current VasaOperation is null. Insert into, ip=" + ip);
                reCreateServerKeystore(IPUtil.getLocalIP());
                t.setValue("false");
                t.setCreatedTime(new Date());
                vasaOperationService.save(t);
            } else {
                logger.info("ArrayCrtRefreshService/checkRestoreServerKeystore current VasaOperation =" + dataByKey);
                if (dataByKey.getValue().equalsIgnoreCase("true")) {
                    reCreateServerKeystore(IPUtil.getLocalIP());
                    dataByKey.setUpdatedTime(new Date());
                    dataByKey.setValue("false");
                    vasaOperationService.updateData(dataByKey);
                }
            }
        } catch (StorageFault e) {
            // TODO Auto-generated catch block
            logger.info("ArrayCrtRefreshService/checkRestoreServerKeystore error.", e);
        }


    }

    public void reCreateServerKeystore(String currentIp) {
        logger.info("ArrayCrtRefreshService/reCreateServerKeystore begin.");
        KeyStore keystore = null;
        FileInputStream localFileInputStream = null;
        FileOutputStream localObjectFileOut = null;
        try {
            localFileInputStream = new FileInputStream(vasa_keystore_path);
            keystore = KeyStore.getInstance("JKS");
			/*String key = ConfigManager.getInstance().getValue("vasa.cert.encryption.key");
			Encryption encrytion = EncryptionFactory.getEncyption();
			String encryptionKey = encrytion.encode(key, "").getEncryptedKey();
			String serverKeystorePass = encrytion.decode(encryptionKey, ConfigManager.getInstance().getValue("vasa.ssl.keystorePass"));*/

            String trustPassFromXml = ConfigManager.getInstance().getValue("vasa.ssl.truststorePass");
            if (trustPassFromXml != null && !trustPassFromXml.equals("")) {
                String serverKeystorePass = AES128System.decryptPwd(trustPassFromXml);
                keystore.load(localFileInputStream, serverKeystorePass.toCharArray());
                @SuppressWarnings("rawtypes")
                Enumeration localEnumeration = keystore.aliases();
                while (localEnumeration.hasMoreElements()) {
                    String str = (String) localEnumeration.nextElement();
                    logger.info("ArrayCrtRefreshService/reCreateServerKeystore serverkeystore alias=" + str);
                    if (str.equalsIgnoreCase(server_alias)) {
                        KeyStore.PrivateKeyEntry pkEntry = (PrivateKeyEntry) keystore.getEntry(str, new KeyStore.PasswordProtection(serverKeystorePass.toCharArray()));
                        X509Certificate certificate = (X509Certificate) pkEntry.getCertificate();
                        byte[] encod1 = certificate.getEncoded();
                        X509CertImpl x509CertImpl = new X509CertImpl(encod1);
                        //X509CertInfo x509CertInfo = new X509CertInfo()
                        X509CertInfo x509CertInfo = (X509CertInfo) x509CertImpl.get(X509CertImpl.NAME + "." + X509CertImpl.INFO);
                        x509CertInfo.set(X509CertInfo.ISSUER + "." + CertificateIssuerName.DN_NAME, new X500Name(server_userDN));
                        x509CertInfo.set(X509CertInfo.SUBJECT + "." + CertificateIssuerName.DN_NAME, new X500Name(server_userDN));
                        SubjectAlternativeNameExtension alternativeNameExtension = new SubjectAlternativeNameExtension();
                        IPAddressName addressName = new IPAddressName(currentIp);
                        GeneralNames generalNames = new GeneralNames();
                        GeneralName generalName = new GeneralName(addressName);
                        generalNames.add(generalName);
                        alternativeNameExtension.set(SubjectAlternativeNameExtension.SUBJECT_NAME, generalNames);

                        Date begindate = new Date();
                        Date enddate = new Date(begindate.getTime() + 3650 * 24 * 60 * 60 * 1000L);
                        CertificateValidity cv = new CertificateValidity(begindate, enddate);

                        PrivateKey caprk = (PrivateKey) keystore.getKey(str, serverKeystorePass.toCharArray());
                        PublicKey publicKey = certificate.getPublicKey();
                        KeyPair kp = new KeyPair(publicKey, caprk);
                        CertificateExtensions exts = new CertificateExtensions();
                        exts.set(SubjectAlternativeNameExtension.NAME, alternativeNameExtension);
                        exts.set(SubjectKeyIdentifierExtension.NAME, new SubjectKeyIdentifierExtension((new KeyIdentifier(kp.getPublic())).getIdentifier()));
                        x509CertInfo.set(X509CertInfo.EXTENSIONS, exts);
                        x509CertInfo.set(X509CertInfo.VALIDITY, cv);

                        X509CertImpl newcert = new X509CertImpl(x509CertInfo);
                        newcert.sign(caprk, "SHA256withRSA");
                        Certificate[] chain = {newcert};
                        KeyStore.PrivateKeyEntry privateKeyEntry = new KeyStore.PrivateKeyEntry(caprk, chain);
                        keystore.deleteEntry(str);
                        keystore.setEntry(str, privateKeyEntry, new KeyStore.PasswordProtection(serverKeystorePass.toCharArray()));
                    } else {
                        logger.info("ArrayCrtRefreshService/reCreateServerKeystore del server.keystore alias=" + str);
                        keystore.deleteEntry(str);
                    }
                }
                localObjectFileOut = new FileOutputStream(vasa_keystore_path);
                keystore.store(localObjectFileOut, serverKeystorePass.toCharArray());
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            logger.error("ArrayCrtRefreshService/reCreateServerKeystore error.", e);
        } finally {
            try {
                if (null != localObjectFileOut) {
                    localObjectFileOut.close();
                }
            } catch (IOException e) {
                logger.error("failed to close localObjectFileOut" + e);
            }
            try {
                if (null != localFileInputStream) {
                    localFileInputStream.close();
                }
            } catch (IOException e) {
                logger.error("failed to close localFileInputStream" + e);
            }

            /**
             * 修改CodeDEX问题：FORTIFY.Unreleased_Resource--Streams Modified by
             * wWX315527 2016/11/19
             */
        }

    }

    public ArrayCertificateService getArrayCertificateService() {
        return arrayCertificateService;
    }

    public void setArrayCertificateService(ArrayCertificateService arrayCertificateService) {
        this.arrayCertificateService = arrayCertificateService;
    }

    public VasaOperationService getVasaOperationService() {
        return vasaOperationService;
    }

    public void setVasaOperationService(VasaOperationService vasaOperationService) {
        this.vasaOperationService = vasaOperationService;
    }

    public VasaPropertyService getVasaPropertyService() {
        return vasaPropertyService;
    }

    public void setVasaPropertyService(VasaPropertyService vasaPropertyService) {
        this.vasaPropertyService = vasaPropertyService;
    }


}
