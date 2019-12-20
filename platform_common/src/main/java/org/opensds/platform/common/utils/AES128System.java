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
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensds.platform.common.config.ConfigManagerNoDecrypt;
import org.opensds.platform.common.exception.SDKException;

/**
 * 系统配置文件的加密
 * 用于加密系统配置文件的加密算法
 *
 */
public final class AES128System
{
    private static final Logger LOGGER = LogManager.getLogger(AES128System.class);
    
    private static final int AES_128_KEY_LEN = 16; // 128 bit
    
    private static byte[] BT_KEY;
    
    private static byte[] BT_KEY_OLD;
    
    private static byte[] BT_KEY_OLD_SAVE;
    
    static
    {
        BT_KEY_OLD = getKeyFromFile();
        
        // eSDK，控制台，OM。只有eSDK启动时更新系统密钥，控制台和OM启动时不更新。
        if ("Y".equalsIgnoreCase(ConfigManagerNoDecrypt.getInstance().getValue("esdk.platform.web", "N"))
            && "N".equalsIgnoreCase(ConfigManagerNoDecrypt.getInstance().getValue("platform.config.tool", "N"))
            && "N".equalsIgnoreCase(ConfigManagerNoDecrypt.getInstance().getValue("platform.mgmt.srv", "N")))
        {
            LOGGER.info("generate SYSTEMKEY.");
            BT_KEY = SecureRandom.getSeed(AES_128_KEY_LEN * 8);
            
            // 生成的SYSTEMKEY保存到文件
            saveKey();
            
            // 更新保存旧密钥的配置文件
            saveOldKey();

        }
        else
        {
            BT_KEY = getKeyFromFile();
        }
    }
    
    public static void init()
    {
    }
    
    public static byte[] getOldKey()
    {
        return BT_KEY_OLD;
    }
    
    public static void setOldKey(byte[] oldKey)
    {
        BT_KEY_OLD = oldKey;
    }
    
    public static void balanceKey()
    {
        BT_KEY_OLD = BT_KEY;
        
        // 将旧的key文件名改为finish
        chengeOldKeyFile();
    }
    
    /**
     * 将旧的key文件名由SYSTEMKEY_OLD.encrypt改为SYSTEMKEY_OLD_FINISHE.encrypt
     */
    private static void chengeOldKeyFile()
    {
    	// 先判断文件是否存在       
        try
        {
            String oldKeyFilePath = getPath() + "/SYSTEMKEY_OLD.encrypt";
            LOGGER.info("get old SYSTEMKEY from fileStr=" + oldKeyFilePath);
            File oldKeyFile = new File(oldKeyFilePath);
            
        	String oldKeyFinisheFilePath = getPath() + "/SYSTEMKEY_OLD_FINISHE.encrypt";
        	File oldKeyFinisheFile = new File(oldKeyFinisheFilePath);
            
            // 判断是否是完成的
            if (!oldKeyFile.exists())
            {            	
            	// 若不存在，新建文件SYSTEMKEY_OLD.encrypt，将旧的key保存到文件中
            	if (oldKeyFinisheFile.exists())
            	{
            		// 若是完成的，将旧的key更新到文件中, 并更改文件名为SYSTEMKEY_OLD_FINISHE.encrypt
                    try
                    {
                        boolean flag = saveKey(BT_KEY_OLD, oldKeyFinisheFile);
                        if (!flag)
                        {
                            LOGGER.info("save old SYSTEMKEY failed!");
                        }
                    }
                    catch (Exception e)
                    {
                        LOGGER.error("save old SYSTEMKEY error", e);
                    }
            	}
            }
            else
            {
            	// 若不是完成的，将旧的key更新到文件中, 并更改文件名为SYSTEMKEY_OLD_FINISHE.encrypt
                try
                {
                    boolean flag = saveKey(BT_KEY_OLD, oldKeyFile);
                    if (!flag)
                    {
                        LOGGER.info("save old SYSTEMKEY failed!");
                    }
                }
                catch (Exception e)
                {
                    LOGGER.error("save old SYSTEMKEY error", e);
                }

                oldKeyFile.renameTo(oldKeyFinisheFile);
        	}
        }
        catch (Exception e)
        {
            LOGGER.error("get old SYSTEMKEY file path failed");
        }
    }

    /**
     *  更新保存旧密钥的配置文件SYSTEMKEY_OLD.encrypt
     */
    private static void saveOldKey()
    {
    	BT_KEY_OLD_SAVE = BT_KEY_OLD;
	
    	// 先判断文件是否存在       
        try
        {
            String oldKeyFilePath = getPath() + "/SYSTEMKEY_OLD.encrypt";
            LOGGER.info("get old SYSTEMKEY from fileStr=" + oldKeyFilePath);
            File oldKeyFile = new File(oldKeyFilePath);
            
            // 判断是否是完成的
            if (!oldKeyFile.exists())
            {
            	String oldKeyFinisheFilePath = getPath() + "/SYSTEMKEY_OLD_FINISHE.encrypt";
            	File oldKeyFinisheFile = new File(oldKeyFinisheFilePath);
            	
            	// 若不存在，新建文件SYSTEMKEY_OLD.encrypt，将旧的key保存到文件中
            	if (!oldKeyFinisheFile.exists())
            	{
                    try
                    {
                        boolean flag = saveKey(BT_KEY_OLD, oldKeyFile);
                        if (!flag)
                        {
                            LOGGER.info("save old SYSTEMKEY failed!");
                        }
                    }
                    catch (Exception e)
                    {
                        LOGGER.error("save old SYSTEMKEY error", e);
                    }
            	}
            	else
            	{
            		// 若是完成的，将旧的key更新到文件中, 并更改文件名为SYSTEMKEY_OLD.encrypt
                    try
                    {
                        boolean flag = saveKey(BT_KEY_OLD, oldKeyFinisheFile);
                        if (!flag)
                        {
                            LOGGER.info("save old SYSTEMKEY failed!");
                        }
                    }
                    catch (Exception e)
                    {
                        LOGGER.error("save old SYSTEMKEY error", e);
                    }
                    
                    
                    oldKeyFinisheFile.renameTo(oldKeyFile);
            	}
            }
            else
            {
            	// 若不是完成的，则不更新
            	BT_KEY_OLD_SAVE = readKey(oldKeyFile);
            }
        }
        catch (Exception e)
        {
            LOGGER.error("get old SYSTEMKEY file path failed");
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
    public static byte[] encode(byte[] btPlain)
        throws BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException, InvalidKeyException,
        NoSuchAlgorithmException, NoSuchPaddingException
    {
        byte[] iv = SecureRandom.getSeed(AES_128_KEY_LEN);
        byte[] psw = encodeDecode(btPlain, BT_KEY, iv, 0);
        return getMergedArray(iv, psw);
    }
    
    public static byte[] encode(byte[] pwd, byte[] key)
        throws BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException, InvalidKeyException,
        NoSuchAlgorithmException, NoSuchPaddingException
    {
        byte[] iv = SecureRandom.getSeed(AES_128_KEY_LEN);
        byte[] psw = encodeDecode(pwd, key, iv, 0);
        return getMergedArray(iv, psw);
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
    public static byte[] encode(byte[] btPlain, byte[] btKey, byte[] btIV)
        throws BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException, InvalidKeyException,
        NoSuchAlgorithmException, NoSuchPaddingException
    {
        byte[] psw = encodeDecode(btPlain, btKey, btIV, 0);
        if (AES_128_KEY_LEN == btIV.length)
        {
            return getMergedArray(btIV, psw);
        }
        return psw;
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
    public static byte[] decode(byte[] btCipher)
        throws BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException, InvalidKeyException,
        NoSuchAlgorithmException, NoSuchPaddingException
    {
        byte[] iv = Arrays.copyOfRange(btCipher, 0, AES_128_KEY_LEN);
        byte[] pwd = Arrays.copyOfRange(btCipher, AES_128_KEY_LEN, btCipher.length);
        return encodeDecode(pwd, BT_KEY, iv, 1);
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
    public static byte[] decode(byte[] btCipher, byte[] btKey, byte[] btIV)
        throws BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException, InvalidKeyException,
        NoSuchAlgorithmException, NoSuchPaddingException
    {
        return encodeDecode(btCipher, btKey, btIV, 1);
    }
    
    /**
     * @param ivPwd iv+密文
     * @param key 密钥
     * @param btIV 初始向量
     * @return 明文
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     * @throws InvalidAlgorithmParameterException
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     */
    public static byte[] decode(byte[] ivPwd, byte[] key)
        throws BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException, InvalidKeyException,
        NoSuchAlgorithmException, NoSuchPaddingException
    {
        byte[] iv = Arrays.copyOfRange(ivPwd, 0, AES_128_KEY_LEN);
        byte[] pwd = Arrays.copyOfRange(ivPwd, AES_128_KEY_LEN, ivPwd.length);
        return encodeDecode(pwd, key, iv, 1);
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
    public static byte[] encodeDecode(byte[] btData, byte[] btKey, byte[] btIV, int iFlag)
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
        
        l_oCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        
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
            l_oCipher.init(l_iMode, new SecretKeySpec(l_btKey, 0, AES_128_KEY_LEN, "AES"));
        }
        else
        {
            l_oCipher.init(l_iMode, new SecretKeySpec(l_btKey, 0, AES_128_KEY_LEN, "AES"), new IvParameterSpec(btIV, 0,
                iIVLen));
        }
        return l_oCipher.doFinal(btData, 0, iLen);
    }
    
    public static String encryptPwd(String plainPwd)
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
    
    public static byte[] encryptPwdByNewKey(byte[] plainPwd)
    {
        LOGGER.info("AES128System encryptPwdByNewKey()");
        try
        {
            byte[] key = getNewKeyByApp();
            if (null == key || 0 == key.length)
            {
                throw new SDKException("can not encrypt by system key");
            }
            return encode(plainPwd, key);
        }
        catch (Exception e)
        {
            try
            {
                LOGGER.error("Encrypt password by new key error");
                return AESCbc128Utils.encode(plainPwd);
            }
            catch (Exception e1)
            {
                LOGGER.error("Encrypt password by fixed key error");
                return plainPwd;
            }
        }
    }
    
    public static String decryptPwd(String pwd)
    {
        return decryptPwd("", pwd);
    }
    
    public static String decryptPwd(String userId, String pwd)
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
            if (result.startsWith(userId) && !result.equals(userId))
            {
                result = result.substring(userId.length());
            }
            
            return result;
        }
        catch (InvalidKeyException e)
        {
            LOGGER.error("Decrypt password error[userId=]" + userId);
            return pwd;
        }
        catch (UnsupportedEncodingException e)
        {
            LOGGER.error("Decrypt password error[userId=]" + userId);
            return pwd;
        }
        catch (BadPaddingException e)
        {
            LOGGER.error("Decrypt password error[userId=]" + userId);
            return pwd;
        }
        catch (IllegalBlockSizeException e)
        {
            LOGGER.error("Decrypt password error[userId=]" + userId);
            return pwd;
        }
        catch (InvalidAlgorithmParameterException e)
        {
            LOGGER.error("Decrypt password error[userId=]" + userId);
            return pwd;
        }
        catch (NoSuchAlgorithmException e)
        {
            LOGGER.error("Decrypt password error[userId=]" + userId);
            return pwd;
        }
        catch (NoSuchPaddingException e)
        {
            LOGGER.error("Decrypt password error[userId=]" + userId);
            return pwd;
        }
    }
    
    public static String decryptPwdByOldKey(String id, String pwd)
    {
        LOGGER.info("AES128System decryptPwdByOldKey()");
        if (StringUtils.isEmpty(pwd))
        {
            return "";
        }
        
        String result = null;
        try
        {
            byte[] key = getOldKeyByApp();
            if (null == key || 0 == key.length)
            {
                throw new SDKException("can not decrypt by system key, system key is null");
            }
            
            byte[] temp = Base64Utils.getFromBASE64(pwd);
            if (0 == temp.length)
            {
                return pwd;
            }
            
            result = new String(decode(temp, key), "UTF-8");
            
            if (StringUtils.isEmpty(result))
            {
                throw new SDKException("can not decrypt by system key");
            }
            
            if (result.startsWith(id) && !result.equals(id))
            {
                result = result.substring(id.length());
            }
            
            return result;
        }
        catch (Exception e)
        {
            result = AESCbc128Utils.decryptPwd(id, pwd);
			try 
			{
				// 尝试用保存的旧key解码
				if (BT_KEY_OLD_SAVE != null && !BT_KEY_OLD_SAVE.equals(BT_KEY_OLD)) 
				{
					LOGGER.info("Decrypt password by save old key start");

					byte[] temp = Base64Utils.getFromBASE64(pwd);
					if (0 == temp.length) 
					{
						return pwd;
					}

					result = new String(decode(temp, BT_KEY_OLD_SAVE), "UTF-8");

					if (StringUtils.isEmpty(result)) 
					{
						throw new SDKException("can not decrypt by old system key");
					}

					if (result.startsWith(id) && !result.equals(id)) 
					{
						result = result.substring(id.length());
					}

					return result;

				}
			} 
			catch (Exception e1) 
			{
				LOGGER.error("Decrypt password by save old key failed");
			}
            
            LOGGER.error("Decrypt password by old key error[userId=]" + id);
            return result;
        }
        
    }
    
    /**
     * 供eSDK使用
     * 
     * 将Key文件保持到文件中
     * @param key
     * @param file
     * @return
     */
    /**
	 *codedex 	
	 *FORTIFY.HW_-_Create_files_with_appropriate_access_permissions_in_multiuser_system
	 *FORTIFY.Unreleased_Resource--Streams    
	 *nwx356892 
	 */
    public static boolean saveKey(File file)
    {
        boolean write;
        OutputStream fos = null;
        ObjectOutputStream oos = null;
        try
        {
            fos = FileAttributeUtility.getSafeOutputStream(file.getAbsolutePath(), false);
            oos = new ObjectOutputStream(fos);
            
            byte[] key = Base64Utils.encode(AES128Utils.encode(BT_KEY)).getBytes("UTF-8");
            
            // 注意，此处采用writeObject方法，读取时也要采用readObject方法  
            oos.writeObject(key);
            write = true;
        }
        catch (FileNotFoundException e)
        {
            LOGGER.error("", e);
            write = false;
        }
        catch (IOException e)
        {
            LOGGER.error("", e);
            write = false;
        }
        catch (InvalidKeyException e)
        {
            LOGGER.error("", e);
            write = false;
        }
        catch (BadPaddingException e)
        {
            LOGGER.error("", e);
            write = false;
        }
        catch (IllegalBlockSizeException e)
        {
            LOGGER.error("", e);
            write = false;
        }
        catch (InvalidAlgorithmParameterException e)
        {
            LOGGER.error("", e);
            write = false;
        }
        catch (NoSuchAlgorithmException e)
        {
            LOGGER.error("", e);
            write = false;
        }
        catch (NoSuchPaddingException e)
        {
            LOGGER.error("", e);
            write = false;
        }
        finally
        {
            ESDKIOUtils.closeFileStreamNotThrow(oos);
            ESDKIOUtils.closeFileStreamNotThrow(fos);
        }
        return write;
    }
    
    /**
     * 将新生成的系统密钥保存到文件
     * 
     * 供eSDK使用
     */
    private static void saveKey()
    {
        try
        {
            String path = getPath();
            File file = new File(path);
            if(!file.exists())
            {
                file.mkdirs();
            }
            String fileStr = path + "/SYSTEMKEY.encrypt";
            LOGGER.info("save SYSTEMKEY fileStr=" + fileStr);
            file = new File(fileStr);
            boolean flag = saveKey(file);
            if (!flag)
            {
                LOGGER.info("save SYSTEMKEY failed!");
            }
        }
        catch (Exception e)
        {
            LOGGER.error("save SYSTEMKEY error", e);
        }
    }
    
    /**
     * 供 OM 和 mgmt 使用
     * 
     * 将Key文件保持到文件中
     * @param key
     * @param file
     * @return
     */
    /**
	 *codedex 	
	 *FORTIFY.HW_-_Create_files_with_appropriate_access_permissions_in_multiuser_system
	 *FORTIFY.Unreleased_Resource--Streams    
	 *nwx356892 
	 */
    public static boolean saveKey(byte[] key, File file)
    {
        boolean write;
        OutputStream fos = null;
        ObjectOutputStream oos = null;
        try
        {
            fos = FileAttributeUtility.getSafeOutputStream(file.getAbsolutePath(), false);
            oos = new ObjectOutputStream(fos);
            
            // 使用内存里的根密钥来加密系统密钥。（供 OM 和 mgmt 使用）
            key = Base64Utils.encode(AES128Utils.encode(key)).getBytes("UTF-8");
            
            // 注意，此处采用writeObject方法，读取时也要采用readObject方法  
            oos.writeObject(key);
            write = true;
        }
        catch (FileNotFoundException e)
        {
            LOGGER.error("", e);
            write = false;
        }
        catch (IOException e)
        {
            LOGGER.error("", e);
            write = false;
        }
        catch (InvalidKeyException e)
        {
            LOGGER.error("", e);
            write = false;
        }
        catch (BadPaddingException e)
        {
            LOGGER.error("", e);
            write = false;
        }
        catch (IllegalBlockSizeException e)
        {
            LOGGER.error("", e);
            write = false;
        }
        catch (InvalidAlgorithmParameterException e)
        {
            LOGGER.error("", e);
            write = false;
        }
        catch (NoSuchAlgorithmException e)
        {
            LOGGER.error("", e);
            write = false;
        }
        catch (NoSuchPaddingException e)
        {
            LOGGER.error("", e);
            write = false;
        }
        finally
        {
            ESDKIOUtils.closeFileStreamNotThrow(oos);
            ESDKIOUtils.closeFileStreamNotThrow(fos);
        }
        return write;
    }
    
    /**
     * 供 OM 和 mgmt 使用
     */
    public static void saveKey(byte[] key)
    {
        try
        {
            String path = getPath();
            File file = new File(path);
            if(!file.exists())
            {
                file.mkdirs();
            }
            String fileStr = path + "/SYSTEMKEY.encrypt";
            LOGGER.info("save SYSTEMKEY fileStr=" + fileStr);
            file = new File(fileStr);
            saveKey(key, file);
        }
        catch (Exception e)
        {
            LOGGER.error("get SYSTEMKEY file path failed");
        }
    }
    
    /**
     * 从文件中读取私钥
     * @param file
     */
    public static byte[] readKey(File file)
    {
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        byte[] keyByte = null;
        try
        {
            //读取数据  
            fis = new FileInputStream(file);
            ois = new ObjectInputStream(fis);
            keyByte = (byte[])ois.readObject();
            
            keyByte = AES128Utils.decode(Base64Utils.getFromBASE64(new String(keyByte, "UTF-8")));
            
        }
        catch (IOException e)
        {
            LOGGER.error("", e);
        }
        catch (NoSuchAlgorithmException e)
        {
            LOGGER.error("", e);
        }
        catch (ClassNotFoundException e)
        {
            LOGGER.error("", e);
        }
        catch (InvalidKeyException e)
        {
            LOGGER.error("", e);
        }
        catch (BadPaddingException e)
        {
            LOGGER.error("", e);
        }
        catch (IllegalBlockSizeException e)
        {
            LOGGER.error("", e);
        }
        catch (InvalidAlgorithmParameterException e)
        {
            LOGGER.error("", e);
        }
        catch (NoSuchPaddingException e)
        {
            LOGGER.error("", e);
        }
        finally
        {
        	ESDKIOUtils.closeFileStreamNotThrow(ois);
        	ESDKIOUtils.closeFileStreamNotThrow(fis);
        }
        return keyByte;
    }
    
    private static String getPath()
        throws Exception
    {
        String result = ConfigManagerNoDecrypt.getInstance().getPureValue("platform.key.path");
        if (null == result || result.contains("@{INSTALLROOT}"))
        {
            result = PathUtil.getAppPath(AES128System.class);
        }
        
        return result;
    }
    
    public static byte[] getKeyFromFile()
    {
        byte[] btKey = null;
        
        try
        {
            String fileStr = getPath() + "/SYSTEMKEY.encrypt";
            LOGGER.info("get SYSTEMKEY from fileStr=" + fileStr);
            File file = new File(fileStr);
            if (!file.exists())
            {
                LOGGER.info("SYSTEMKEY is null");
                return null;
            }
            btKey = readKey(file);
        }
        catch (Exception e)
        {
            LOGGER.error("get SYSTEMKEY file path failed");
        }
        
        return btKey;
    }
    
    public static byte[] getMergedArray(byte[] iv, byte[] psw)
    {
        byte[] ivPsw = new byte[iv.length + psw.length];
        System.arraycopy(iv, 0, ivPsw, 0, iv.length);
        System.arraycopy(psw, 0, ivPsw, iv.length, psw.length);
        return ivPsw;
    }
    
    public static byte[] getOldKeyByApp()
    {
        if ("Y".equalsIgnoreCase(ConfigManagerNoDecrypt.getInstance().getValue("esdk.platform.web", "N"))
            && "N".equalsIgnoreCase(ConfigManagerNoDecrypt.getInstance().getValue("platform.config.tool", "N"))
            && "N".equalsIgnoreCase(ConfigManagerNoDecrypt.getInstance().getValue("platform.mgmt.srv", "N")))
        {
            // eSDK
            return BT_KEY_OLD;
        }
        else
        {
            // OM , cmd
            return getKeyFromFile();
        }
    }
    
    public static byte[] getNewKeyByApp()
    {
        if ("Y".equalsIgnoreCase(ConfigManagerNoDecrypt.getInstance().getValue("esdk.platform.web", "N"))
            && "N".equalsIgnoreCase(ConfigManagerNoDecrypt.getInstance().getValue("platform.config.tool", "N"))
            && "N".equalsIgnoreCase(ConfigManagerNoDecrypt.getInstance().getValue("platform.mgmt.srv", "N")))
        {
            // eSDK
            return BT_KEY;
        }
        else
        {
            // OM , cmd
            return getKeyFromFile();
        }
    }
    
}