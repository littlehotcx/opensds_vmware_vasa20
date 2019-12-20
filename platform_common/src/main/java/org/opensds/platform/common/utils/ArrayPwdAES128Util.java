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

package org.opensds.platform.common.utils;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.opensds.platform.common.config.ConfigManager;
import org.opensds.platform.common.exception.SDKException;

public class ArrayPwdAES128Util {
    private static byte[] BT_ARRAY_PWD_KEY;

    private static Logger LOGGER = LogManager.getLogger(ArrayPwdAES128Util.class);

    public static void init() {
        String propertyValue = ConfigManager.getInstance().getValue("vasa.array.password.key", "BMEIMPL@YYYYMMDD");
        String pwd_key_str = AES128System.decryptPwdByOldKey("", propertyValue);

        try {
            KeyGenerator kg = KeyGenerator.getInstance("AES");
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            secureRandom.setSeed(pwd_key_str.getBytes());
            kg.init(128, secureRandom);
            SecretKey sk = kg.generateKey();
            BT_ARRAY_PWD_KEY = sk.getEncoded();
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error("general the array decrypt key error!", e);
        }
    }

    public static String decryptPwd(String userId, String pwd) {
        if (StringUtils.isEmpty(pwd)) {
            return "";
        }
        SecretKeySpec key = new SecretKeySpec(BT_ARRAY_PWD_KEY, "AES");
        Cipher cipher;
        try {
            cipher = Cipher.getInstance("AES");
            byte[] byteContent = Base64Utils.getFromBASE64(pwd);
            cipher.init(Cipher.DECRYPT_MODE, key);// 初始化
            byte[] resultByte = cipher.doFinal(byteContent);
            String result = new String(resultByte, "UTF-8");
            if (StringUtils.isEmpty(result)) {
                throw new SDKException("can not decrypt by system key");
            }

            if (result.startsWith(userId) && !result.equals(userId)) {
                result = result.substring(userId.length());
            }
            return result;
        } catch (Exception e) {
            LOGGER.error("decryptPwd err by key.", e);
        }
        return null;
    }

    public static byte[] encryptPwd(byte[] passwordByte) {
        SecretKeySpec key = new SecretKeySpec(BT_ARRAY_PWD_KEY, "AES");
        Cipher cipher;
        try {
            cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, key);// 初始化
            byte[] result = cipher.doFinal(passwordByte);
            return result;
        } catch (Exception e) {
            LOGGER.error("encryptPwd err by key.", e);
            return passwordByte;
        }
    }

}
