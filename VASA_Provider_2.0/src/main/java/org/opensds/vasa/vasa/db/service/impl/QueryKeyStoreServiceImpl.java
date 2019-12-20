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

package org.opensds.vasa.vasa.db.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensds.vasa.vasa.util.FileManager;

import org.opensds.platform.common.config.ConfigManager;
import org.opensds.platform.common.utils.AES128System;
import org.opensds.vasa.vasa.db.service.QueryKeyStoreService;

public class QueryKeyStoreServiceImpl implements QueryKeyStoreService {
    private static Logger LOGGER = LogManager
            .getLogger(QueryKeyStoreServiceImpl.class);

    @Override
    public String queryKeyStoreExpireDate() {

        FileInputStream localFileInputStream = null;
        try {
            String trustPwd = "";

            String trustPassFromXml = ConfigManager.getInstance().getValue("vasa.ssl.truststorePass");
            if (trustPassFromXml != null && !trustPassFromXml.equals("")) {
                trustPwd = AES128System.decryptPwd(trustPassFromXml);
            }

            String trustPassFileName = ConfigManager.getInstance().getValue("vasa.ssl.truststoreFile");

            String trustStoreType = ConfigManager.getInstance().getValue("vasa.ssl.truststoreType");
            if (trustStoreType == null || trustStoreType.equals("")) {
                trustStoreType = "JKS";
            }

            String trustPassFilePath = FileManager.getBasePath() + "conf" + File.separator + trustPassFileName;


            KeyStore localKeyStore = KeyStore.getInstance(trustStoreType);
            localFileInputStream = new FileInputStream(trustPassFilePath);
            localKeyStore.load(localFileInputStream, trustPwd.toCharArray());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            X509Certificate localX509Certificate = (X509Certificate) localKeyStore.getCertificate("server");

            if (localX509Certificate != null) {
                return sdf.format(localX509Certificate.getNotAfter());
            }
        } catch (KeyStoreException e) {
            LOGGER.error("queryKeyStoreExpireDate error:KeyStoreException.");
        } catch (FileNotFoundException e) {
            LOGGER.error("queryKeyStoreExpireDate error:FileNotFoundException.");
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error("queryKeyStoreExpireDate error:NoSuchAlgorithmException.");
        } catch (CertificateException e) {
            LOGGER.error("queryKeyStoreExpireDate error:CertificateException.");
        } catch (IOException e) {
            LOGGER.error("queryKeyStoreExpireDate error:IOException.");
        } finally {
            if (localFileInputStream != null) {
                try {
                    localFileInputStream.close();
                } catch (IOException e) {
                    LOGGER.error("queryKeyStoreExpireDate close FileInputStream error:IOException.");
                }
            }
        }
        return null;

    }

}
