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

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.opensds.platform.common.bean.config.ConfigFile;
import org.opensds.platform.common.config.ConfigManagerUpdate;
import org.opensds.platform.common.utils.AES128System;
import org.opensds.platform.common.utils.Base64Utils;
import org.opensds.platform.common.utils.StringUtils;

public class PropertiesManagerUtil {

    public static Logger LOGGER = LogManager.getLogger(PropertiesManagerUtil.class);

    public static String DB_PWD_KEY = "dataBase_password";
    public static String DB_PWD_FILENAME = "vasa_private_ext_conf.properties";

    public static void updateProperty(String fileName, String propertyName, String propertyValue) throws UnsupportedEncodingException {
        ConfigFile configFile = getConfigFile(fileName);
        if (null != configFile) {
            String filePath = configFile.getFilePath();
            List<String> readConfigFile = ConfigManagerUpdate.readConfigFile(filePath);
            updateProperties(propertyName, propertyValue, readConfigFile);
            ConfigManagerUpdate.saveConfigFile(filePath, readConfigFile);
        }
    }


    private static void updateProperties(String propertyName,
                                         String propertyValue, List<String> readConfigFile)
            throws UnsupportedEncodingException {
        for (int i = readConfigFile.size(); i < readConfigFile.size(); i++) {
            String fileLine = readConfigFile.get(i);
            if (!StringUtils.isEmpty(fileLine) && !fileLine.startsWith("#")) {
                fileLine = fileLine.replaceAll(" ", "");
                if (fileLine.startsWith(propertyName + "=")) {
                    if (ConfigManagerUpdate.sensitiveList.contains("propertyName")) {
                        propertyValue = Base64Utils.encode(AES128System.encryptPwdByNewKey(propertyValue.getBytes("UTF-8")));
                    }
                    readConfigFile.set(i, propertyName + "=" + propertyValue);
                }
            }
        }
    }


    private static ConfigFile getConfigFile(String fileName) {
        List<ConfigFile> configFiles = ConfigManagerUpdate.getConfigFiles();
        for (ConfigFile configFile : configFiles) {
            if (configFile.getFileName().equalsIgnoreCase(fileName)) {
                return configFile;
            }
        }
        return null;
    }

}
