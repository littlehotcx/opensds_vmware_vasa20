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

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;

import org.opensds.platform.common.config.ConfigManager;

public class ApplictionDataSourceFactory {
    private static org.apache.logging.log4j.Logger LOGGER = org.apache.logging.log4j.LogManager
            .getLogger(ApplictionDataSourceFactory.class);

    public DataSource buildLocalDataSource() {
        LOGGER.info("In buildLocalDataSource function.");
        //读配置文件统一使用PropertiesUtils工具类，代替自己写，防止classloader获空，FORTIFY.Missing_Check_against_Null

        BasicDataSource basic = new BasicDataSource();
        // remove Encryption
//        String key = ConfigManager.getInstance().getValue("vasa.cert.encryption.key");
//        Encryption encrytion = EncryptionFactory.getEncyption();
//        String encryptionKey = encrytion.encode(key, "").getEncryptedKey();
//        String gaussdbPassword = encrytion.decode(encryptionKey, ConfigManager.getInstance().getValue("dataBase_password"));
//        System.out.println("Get gaussdb password = "+gaussdbPassword);
//        basic.setPassword(gaussdbPassword);
        basic.setPassword(ConfigManager.getInstance().getValue("dataBase_password"));
        return basic;
    }
}
