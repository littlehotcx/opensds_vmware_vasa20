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

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 工作密钥
 * 用于加密网络交互的敏感数据。
 * 密钥和初始向量都为密钥协商的结果。
 * 
 * @author sWX198756
 *
 */
public final class AES128App
{
    private static final Logger LOGGER = LogManager.getLogger(AES128App.class);
    
    // 128 bit
    private static final int AES_128_KEY_LEN = 16;
    
    /**
     * 密钥算法
     */
    private static final String KEY_ALGORITHM = "AES";
    
    /**
     * 加解密算法/工作模式/填充方式
     */
    private static String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";
    
    private byte[] BT_KEY;
    
    private byte[] BT_IV;
    
    public byte[] getBytes(String str)
    {
        try
        {
            return str.getBytes("UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
            return new byte[] {};
        }
    }
    
    /**
     * @param btPlain 明文
     * @return 密文
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     * @throws InvalidAlgorithmParameterException
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     */
    public byte[] encode(byte[] btPlain)
        throws BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException, InvalidKeyException,
        NoSuchAlgorithmException, NoSuchPaddingException
    {
        return encodeDecode(btPlain, BT_KEY, BT_IV, 0);
    }
    
    /**
     * @param btPlain 明文
     * @param btKey 密钥
     * @param btIV 初始向量
     * @return 密文
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     * @throws InvalidAlgorithmParameterException
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     */
    public byte[] encode(byte[] btPlain, byte[] btKey, byte[] btIV)
        throws BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException, InvalidKeyException,
        NoSuchAlgorithmException, NoSuchPaddingException
    {
        return encodeDecode(btPlain, btKey, btIV, 0);
    }
    
    /**
     * @param btCipher 密文
     * @return 明文
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     * @throws InvalidAlgorithmParameterException
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     */
    public byte[] decode(byte[] btCipher)
        throws BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException, InvalidKeyException,
        NoSuchAlgorithmException, NoSuchPaddingException
    {
        return encodeDecode(btCipher, BT_KEY, BT_IV, 1);
    }
    
    /**
     * @param btCipher 密文
     * @param btKey 密钥
     * @param btIV 初始向量
     * @return 明文
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     * @throws InvalidAlgorithmParameterException
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     */
    public byte[] decode(byte[] btCipher, byte[] btKey, byte[] btIV)
        throws BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException, InvalidKeyException,
        NoSuchAlgorithmException, NoSuchPaddingException
    {
        return encodeDecode(btCipher, btKey, btIV, 1);
    }
    
    /**
     * @param btData 数据
     * @param btKey 密钥
     * @param btIV 初始向量
     * @param iFlag 0 - 加密 else 解密
     * @return 处理后的数据
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     * @throws InvalidAlgorithmParameterException
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     */
    public byte[] encodeDecode(byte[] btData, byte[] btKey, byte[] btIV, int iFlag)
        throws BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException, InvalidKeyException,
        NoSuchAlgorithmException, NoSuchPaddingException
    {
        int ii;
        int l_iMode;
        byte[] l_btKey = null;
        Cipher l_oCipher = null;
        
        if ((btData == null) || (btKey == null))
        {
            return new byte[] {};
        }
        
        int iLen = btData.length;
        int iKeyLen = btKey.length;
        int iIVLen = btIV == null ? 0 : btIV.length;
        
        if (iKeyLen > AES_128_KEY_LEN) // 16 Bytes
        {
            iKeyLen = AES_128_KEY_LEN; // 16 Bytes
        }
        
        l_btKey = new byte[AES_128_KEY_LEN]; // 16 Bytes
        
        for (ii = 0; ii < AES_128_KEY_LEN; ii++)
        {
            l_btKey[ii] = (byte)0x00;
        }
        
        for (ii = 0; ii < iKeyLen; ii++)
        {
            l_btKey[ii] = btKey[ii];
        }
        
        l_oCipher = Cipher.getInstance(CIPHER_ALGORITHM);
        
        if (iFlag == 0)
        {
            l_iMode = Cipher.ENCRYPT_MODE;
        }
        else
        {
            l_iMode = Cipher.DECRYPT_MODE;
        }
        
        if (btIV == null)
        {
            l_oCipher.init(l_iMode, new SecretKeySpec(l_btKey, 0, AES_128_KEY_LEN, KEY_ALGORITHM));
        }
        else
        {
            l_oCipher.init(l_iMode, new SecretKeySpec(l_btKey, 0, AES_128_KEY_LEN, KEY_ALGORITHM), new IvParameterSpec(
                btIV, 0, iIVLen));
        }
        return l_oCipher.doFinal(btData, 0, iLen);
    }
    
    public String encryptPwd(String plainPwd)
    {
        try
        {
            return Base64Utils.encode(encode(BytesUtils.getBytes(plainPwd)));
        }
        catch (InvalidKeyException e)
        {
            LOGGER.error("Encrypt password error", e);
            return plainPwd;
        }
        catch (BadPaddingException e)
        {
            LOGGER.error("Encrypt password error", e);
            return plainPwd;
        }
        catch (IllegalBlockSizeException e)
        {
            LOGGER.error("Encrypt password error", e);
            return plainPwd;
        }
        catch (InvalidAlgorithmParameterException e)
        {
            LOGGER.error("Encrypt password error", e);
            return plainPwd;
        }
        catch (NoSuchAlgorithmException e)
        {
            LOGGER.error("Encrypt password error", e);
            return plainPwd;
        }
        catch (NoSuchPaddingException e)
        {
            LOGGER.error("Encrypt password error", e);
            return plainPwd;
        }
    }
    
    public String decryptPwd(String pwd)
    {
        if (StringUtils.isEmpty(pwd))
        {
            return "";
        }
        
        try
        {
            byte[] temp = Base64Utils.getFromBASE64(pwd);
            if (0 == temp.length)
            {
                return pwd;
            }
            
            String result = new String(decode(temp), "UTF-8");
            
            return result;
        }
        catch (InvalidKeyException e)
        {
            LOGGER.error("Decrypt password error", e);
            return pwd;
        }
        catch (UnsupportedEncodingException e)
        {
            LOGGER.error("Decrypt password error", e);
            return pwd;
        }
        catch (BadPaddingException e)
        {
            LOGGER.error("Decrypt password error", e);
            return pwd;
        }
        catch (IllegalBlockSizeException e)
        {
            LOGGER.error("Decrypt password error", e);
            return pwd;
        }
        catch (InvalidAlgorithmParameterException e)
        {
            LOGGER.error("Decrypt password error", e);
            return pwd;
        }
        catch (NoSuchAlgorithmException e)
        {
            LOGGER.error("Decrypt password error", e);
            return pwd;
        }
        catch (NoSuchPaddingException e)
        {
            LOGGER.error("Decrypt password error", e);
            return pwd;
        }
    }
    
    /**将二进制转换成16进制
     * @param buf
     * @return
     */
    public String byte2Hex(byte buf[])
    {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buf.length; i++)
        {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1)
            {
                hex = '0' + hex;
            }
            sb.append(hex);
        }
        return sb.toString();
    }
    
    /**将16进制转换为二进制
     * @param hexStr
     * @return
     */
    public byte[] hex2Byte(String hexStr)
    {
        if (hexStr.length() < 1)
            return null;
        byte[] result = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++)
        {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
            result[i] = (byte)(high * 16 + low);
        }
        return result;
    }
    
    public byte[] getBT_KEY()
    {
        return BT_KEY;
    }
    
    public void setBT_KEY(byte[] bT_KEY)
    {
        BT_KEY = bT_KEY;
    }
    
    public byte[] getBT_IV()
    {
        return BT_IV;
    }
    
    public void setBT_IV(byte[] bT_IV)
    {
        BT_IV = bT_IV;
    }
    
}