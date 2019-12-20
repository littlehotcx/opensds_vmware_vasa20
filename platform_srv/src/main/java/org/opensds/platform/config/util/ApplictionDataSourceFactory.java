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

package org.opensds.platform.config.util;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;

import org.opensds.platform.common.config.ConfigManager;
//import com.huawei.openas.commons.encryption.Encryption;
//import com.huawei.openas.commons.encryption.EncryptionFactory;

public class ApplictionDataSourceFactory
{
    public DataSource buildLocalDataSource()
    {
        BasicDataSource basic = new BasicDataSource();
        try {
//        	String key = ConfigManager.getInstance().getValue("vasa.cert.encryption.key");
//            Encryption encrytion = EncryptionFactory.getEncyption();
//            String encryptionKey = encrytion.encode(key, "").getEncryptedKey();
//            String gaussdbPassword = encrytion.decode(encryptionKey, ConfigManager.getInstance().getValue("dataBase_password"));
//            basic.setPassword(gaussdbPassword);
		} catch (Exception e) {
			e.printStackTrace();
		}
        
        return basic;
    }
}
