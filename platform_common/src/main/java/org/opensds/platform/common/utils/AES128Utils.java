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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.wcc.crypt.Crypter;
import org.wcc.crypt.CrypterFactory;

import org.opensds.platform.common.config.ConfigManagerNoDecrypt;

/**
 * 根密钥
 * 用于加密eSDK系统的密钥的加密算法。
 *
 * @author sWX198756
 */
public final class AES128Utils {
    private static final Logger LOGGER = LogManager.getLogger(AES128Utils.class);

    private static final int AES_128_KEY_LEN = 16; // 128 bit

    private static byte[] BT_KEY = null;
    
    
    private static Crypter crypter =  CrypterFactory.getCrypter(CrypterFactory.AES_CBC);
    
    static
    {
        BT_KEY = getBTKeyFromFile();
        if (0 == BT_KEY.length) {
            LOGGER.info("generate ROOTKEY.");
            BT_KEY = SecureRandom.getSeed(AES_128_KEY_LEN * 8);
            saveKey(BT_KEY);
        }
        LOGGER.info("ROOTKEY length: " + BT_KEY.length);
    }

    private static byte[] getBTKeyFromFile() {
        byte[] btKey = null;

        String fileStr = getPath() + "/KEY.encrypt";
        LOGGER.info("get ROOTKEY from fileStr=" + fileStr);

        btKey = readKey(fileStr);

        if (null == btKey) {
            LOGGER.info("ROOTKEY is null!");
        } else {
            LOGGER.info("ROOTKEY length: " + btKey.length);
        }

        return btKey;
    }

    private static String getPath() {
        String result = ConfigManagerNoDecrypt.getInstance().getPureValue("platform.key.path");
        if (null == result || result.contains("@{INSTALLROOT}")) {
            try {
                result = PathUtil.getAppPath(AES128Utils.class);
            } catch (Exception e) {
                LOGGER.debug("platform.key.path error!");
            }
        }

        return result;
    }

    public static void updateKey() {
        BT_KEY = SecureRandom.getSeed(AES_128_KEY_LEN * 8);
    }

    public static byte[] getKey() {
        return BT_KEY;
    }

    /**
     * @param key
     * @param file
     * @return
     */
    private static byte[] readKey(String fileStr) {
        FileInputStream fis = null;
        try {
            File file = new File(fileStr);
            if (file.exists()) {
                LOGGER.debug("file exists");
                fis = new FileInputStream(file);
                List<String> list = IOUtils.readLines(fis);
                for (String line : list)
                {
                    return crypter.decryptByRootKey(line).getBytes("UTF-8");
                }
            }
        } catch (Exception e) {
            LOGGER.error("", e);
        } finally {
            ESDKIOUtils.closeInputStream(fis);
        }
        return new byte[]{};
    }

    public static boolean saveKey(byte[] key) {
        String path = getPath();
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        LOGGER.debug("saveKey path=" + path);
        file = new File(path + "/KEY.encrypt");
        return saveKey(key, file);
    }

    /**
     * save encode key to file
     * @param key
     * @param file
     * @return
     */
    /**
     * codedex
     * FORTIFY.HW_-_Create_files_with_appropriate_access_permissions_in_multiuser_system
     * nwx356892
     */
    public static boolean saveKey(byte[] key, File file) {
        boolean write;
        OutputStream fos = null;
        try {
            fos = FileAttributeUtility.getSafeOutputStream(file.getAbsolutePath(), false);
            
            fos.write(crypter.encryptByRootKey(new String(key, "UTF-8")).getBytes("UTF-8"));
            write = true;
        } catch (NullPointerException e) {
            write = false;
        } catch (FileNotFoundException e) {
            write = false;
        } catch (UnsupportedEncodingException e) {
            write = false;
        } catch (IOException e) {
            write = false;
        } finally {
            try {
                if (null != fos) {
                    fos.close();
                }
            } catch (IOException e) {
                LOGGER.debug("code key by base64 IOException");
            }
        }
        return write;
    }

    /**
     * @param btPlain
     * @return
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     * @throws InvalidAlgorithmParameterException
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     */
    public static byte[] encode(byte[] btPlain)
            throws BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException, InvalidKeyException,
            NoSuchAlgorithmException, NoSuchPaddingException {
        byte[] btKey = getBTKeyFromFile();
        byte[] iv = SecureRandom.getSeed(AES_128_KEY_LEN);
        byte[] pwd = encodeDecode(btPlain, btKey, iv, 0);
        return getMergedArray(iv, pwd);
    }

    public static byte[] encode(byte[] btPlain, byte[] key)
            throws BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException, InvalidKeyException,
            NoSuchAlgorithmException, NoSuchPaddingException {
        byte[] iv = SecureRandom.getSeed(AES_128_KEY_LEN);
        byte[] pwd = encodeDecode(btPlain, key, iv, 0);
        return getMergedArray(iv, pwd);
    }

    /**
     * @param btPlain
     * @param btKey
     * @param btIV
     * @return
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     * @throws InvalidAlgorithmParameterException
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     */
    public static byte[] encode(byte[] btPlain, byte[] btKey, byte[] btIV)
            throws BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException, InvalidKeyException,
            NoSuchAlgorithmException, NoSuchPaddingException {
        return encodeDecode(btPlain, btKey, btIV, 0);
    }

    /**
     * @param btCipher
     * @return
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     * @throws InvalidAlgorithmParameterException
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     */
    public static byte[] decode(byte[] btCipher)
            throws BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException, InvalidKeyException,
            NoSuchAlgorithmException, NoSuchPaddingException {
        byte[] btKey = getBTKeyFromFile();

        byte[] iv = Arrays.copyOfRange(btCipher, 0, AES_128_KEY_LEN);
        byte[] pwd = Arrays.copyOfRange(btCipher, AES_128_KEY_LEN, btCipher.length);
        return encodeDecode(pwd, btKey, iv, 1);
    }

    /**
     * @param ivPwd
     * @param key
     * @param btIV
     * @return
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     * @throws InvalidAlgorithmParameterException
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     */
    public static byte[] decode(byte[] ivPwd, byte[] key)
            throws BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException, InvalidKeyException,
            NoSuchAlgorithmException, NoSuchPaddingException {
        byte[] iv = Arrays.copyOfRange(ivPwd, 0, AES_128_KEY_LEN);
        byte[] pwd = Arrays.copyOfRange(ivPwd, AES_128_KEY_LEN, ivPwd.length);
        return encodeDecode(pwd, key, iv, 1);
    }

    /**
     * @param btCipher
     * @param btKey
     * @param btIV
     * @return
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     * @throws InvalidAlgorithmParameterException
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     */
    public static byte[] decode(byte[] btCipher, byte[] btKey, byte[] btIV)
            throws BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException, InvalidKeyException,
            NoSuchAlgorithmException, NoSuchPaddingException {
        return encodeDecode(btCipher, btKey, btIV, 1);
    }

    /**
     * @param btData
     * @param btKey
     * @param btIV
     * @param iFlag
     * @return
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     * @throws InvalidAlgorithmParameterException
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     */
    public static byte[] encodeDecode(byte[] btData, byte[] btKey, byte[] btIV, int iFlag)
            throws BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException, InvalidKeyException,
            NoSuchAlgorithmException, NoSuchPaddingException {
        int ii;
        int l_iMode;
        byte[] l_btKey = null;
        Cipher l_oCipher = null;

        if ((btData == null) || (btKey == null)) {
            return new byte[]{};
        }

        int iLen = btData.length;
        int iKeyLen = btKey.length;
        int iIVLen = btIV == null ? 0 : btIV.length;

        if (iKeyLen > AES_128_KEY_LEN) // 16 Bytes
        {
            iKeyLen = AES_128_KEY_LEN; // 16 Bytes
        }

        l_btKey = new byte[AES_128_KEY_LEN]; // 16 Bytes

        for (ii = 0; ii < AES_128_KEY_LEN; ii++) {
            l_btKey[ii] = (byte) 0x00;
        }

        for (ii = 0; ii < iKeyLen; ii++) {
            l_btKey[ii] = btKey[ii];
        }

        l_oCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

        if (iFlag == 0) {
            l_iMode = Cipher.ENCRYPT_MODE;
        } else {
            l_iMode = Cipher.DECRYPT_MODE;
        }

        if (btIV == null) {
            l_oCipher.init(l_iMode, new SecretKeySpec(l_btKey, 0, AES_128_KEY_LEN, "AES"));
        } else {
            l_oCipher.init(l_iMode, new SecretKeySpec(l_btKey, 0, AES_128_KEY_LEN, "AES"), new IvParameterSpec(btIV, 0,
                    iIVLen));
        }

        return l_oCipher.doFinal(btData, 0, iLen);
    }

    public static String decryptPwd(String userId, String pwd) {
        try {
            byte[] temp = Base64Utils.getFromBASE64(pwd);
            if (0 == temp.length) {
                return pwd;
            }

            String result = new String(decode(temp), "UTF-8");
            if (result.startsWith(userId) && !result.equals(userId)) {
                result = result.substring(userId.length());
            }

            return result;
        } catch (InvalidKeyException e) {
            LOGGER.error("Decrypt password error[userId=]" + userId);
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("Decrypt password error[userId=]" + userId);
        } catch (BadPaddingException e) {
            LOGGER.error("Decrypt password error[userId=]" + userId);
        } catch (IllegalBlockSizeException e) {
            LOGGER.error("Decrypt password error[userId=]" + userId);
        } catch (InvalidAlgorithmParameterException e) {
            LOGGER.error("Decrypt password error[userId=]" + userId);
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error("Decrypt password error[userId=]" + userId);
        } catch (NoSuchPaddingException e) {
            LOGGER.error("Decrypt password error[userId=]" + userId);
        }
        return pwd;
    }

    public static byte[] getMergedArray(byte[] iv, byte[] pwd) {
        byte[] ivPsw = new byte[iv.length + pwd.length];
        System.arraycopy(iv, 0, ivPsw, 0, iv.length);
        System.arraycopy(pwd, 0, ivPsw, iv.length, pwd.length);
        return ivPsw;
    }
}