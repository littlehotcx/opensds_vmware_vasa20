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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensds.platform.common.config.ConfigManagerNoDecrypt;

public class RSA2048Utils
{
    private static final Logger LOGGER = LogManager.getLogger(RSA2048Utils.class);
    
    private static String RSA_TRANSFORMATION = "RSA/ECB/OAEPWithSHA-512AndMGF1Padding";
    
    /** 
     * 公钥 
     */
    private static RSAPublicKey publicKey;
    
    /** 
     * 私钥 
     */
    private static RSAPrivateKey privateKey;
    
    /** 
     * 用于加解密 
     */
    private static Cipher cipher;
    
    /** 
     * 明文块的长度 它必须小于密文块的长度 - 11 
     */
    private static final int originLength = 128;
    
    /** 
     * 密文块的长度 
     */
    private static final int encrytLength = 256;
    
    static
    {
        try
        {
            initKey();
        }
        catch (NoSuchAlgorithmException e)
        {
            LOGGER.error("", e);
        }
        catch (NoSuchPaddingException e)
        {
            LOGGER.error("", e);
        }
    }
    
    /** 
     * 得到初始化的公钥和私钥 
     * @throws NoSuchAlgorithmException  
     * @throws NoSuchPaddingException  
     */
    public static void initKey()
        throws NoSuchAlgorithmException, NoSuchPaddingException
    {
        //RSA加密算法：创建密钥对，长度采用2048  
        KeyPairGenerator kg = KeyPairGenerator.getInstance("RSA");
        kg.initialize(encrytLength * 8);
        KeyPair keypair = kg.generateKeyPair();
        
        //分别得到公钥和私钥  
        publicKey = (RSAPublicKey)keypair.getPublic();
        privateKey = (RSAPrivateKey)keypair.getPrivate();
    }
    
    public static RSAPublicKey getPublicKey(String file)
    {
        return readPublicKey(new File(file));
    }
    
    /** 
     * 将公钥保存至文件 
     * @param file 待写入的文件 
     * @return true 写入成功;false 写入失败 
     */
    public static boolean savePublicKey(File file)
    {
        return saveKey(publicKey, file);
    }
    
    /** 
     * 将私钥保持至文件 
     * @param file 待写入的文件 
     * @return true 写入成功;false 写入失败 
     */
    public static boolean savePrivateKey(File file)
    {
        return saveKey(privateKey, file);
    }
    
    /**
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
    public static boolean saveKey(Key key, File file)
    {
        boolean write;
        OutputStream fos = null;
        ObjectOutputStream oos = null;
        try
        {
            fos = FileAttributeUtility.getSafeOutputStream(file.getAbsolutePath(), false);
            oos = new ObjectOutputStream(fos);
            
            // System.out.println(key.getFormat());  
            // 公钥默认使用的是X.509编码，私钥默认采用的是PKCS #8编码  
            byte[] encode = key.getEncoded();
            encode = Base64Utils.encode(AES128Utils.encode(encode)).getBytes("UTF-8");
            
            // 注意，此处采用writeObject方法，读取时也要采用readObject方法  
            oos.writeObject(encode);
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
     * 从文件中读取公钥
     * @param file
     * @return
     */
    public static RSAPublicKey readPublicKey(File file)
    {
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        RSAPublicKey publicKey = null;
        try
        {
            //读取数据  
            fis = new FileInputStream(file);
            ois = new ObjectInputStream(fis);
            byte[] keybyte = (byte[])ois.readObject();
            keybyte = AES128Utils.decode(Base64Utils.getFromBASE64(new String(keybyte, "UTF-8")));
            
            //默认编码  
            KeyFactory keyfactory = KeyFactory.getInstance("RSA");
            
            //得到公钥或是私钥  
            X509EncodedKeySpec x509eks = new X509EncodedKeySpec(keybyte);
            publicKey = (RSAPublicKey)keyfactory.generatePublic(x509eks);
        }
        catch (IOException e)
        {
            LOGGER.error("", e);
        }
        catch (NoSuchAlgorithmException e)
        {
            LOGGER.error("", e);
        }
        catch (InvalidKeySpecException e)
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
            //关闭资源  
        	ESDKIOUtils.closeFileStreamNotThrow(ois);
        	ESDKIOUtils.closeFileStreamNotThrow(fis);
        }
        return publicKey;
    }
    
    /**
     * 从文件中读取私钥
     * @param file
     */
    public static RSAPrivateKey readPrivateKey(File file)
    {
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        RSAPrivateKey privateKey = null;
        try
        {
            //读取数据  
            fis = new FileInputStream(file);
            ois = new ObjectInputStream(fis);
            byte[] keybyte = (byte[])ois.readObject();
            
            keybyte = AES128Utils.decode(Base64Utils.getFromBASE64(new String(keybyte, "UTF-8")));
            
            //默认编码  
            KeyFactory keyfactory = KeyFactory.getInstance("RSA");
            
            //得到公钥或是私钥  
            PKCS8EncodedKeySpec pkcs8eks = new PKCS8EncodedKeySpec(keybyte);
            privateKey = (RSAPrivateKey)keyfactory.generatePrivate(pkcs8eks);
        }
        catch (IOException e)
        {
            LOGGER.error("", e);
        }
        catch (NoSuchAlgorithmException e)
        {
            LOGGER.error("", e);
        }
        catch (InvalidKeySpecException e)
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
            //关闭资源  
        	ESDKIOUtils.closeFileStreamNotThrow(ois);
        	ESDKIOUtils.closeFileStreamNotThrow(fis);
        }
        return privateKey;
    }
    
    /** 
     * 数据RSA加密 
     * @param origin 明文 
     * @return 密文 
     */
    public static byte[] encrypt(byte[] origin)
    {
        return origin;
    }
    
    
    /** 
     * 数据RSA解密 
     * @param enc 密文 
     * @return 明文 
     */
    public static byte[] decrypt(byte[] enc)
    {
        refreshKeys();
        if (null == enc || 0 == enc.length)
        {
            return BytesUtils.getBytes("");
        }
        byte[] origin = null;
        try
        {
            cipher = Cipher.getInstance(RSA_TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            origin = cipher.doFinal(enc);
        }
        catch (NoSuchAlgorithmException e)
        {
            LOGGER.error("", e);
        }
        catch (NoSuchPaddingException e)
        {
            LOGGER.error("", e);
        }
        catch (InvalidKeyException e)
        {
            LOGGER.error("", e);
        }
        catch (IllegalBlockSizeException e)
        {
            LOGGER.error("", e);
        }
        catch (BadPaddingException e)
        {
            LOGGER.error("", e);
        }
        return origin;
    }
    
    /** 
     * 数据RSA解密 
     * @param enc 密文 
     * @return 明文 
     */
    public static byte[] decrypt(byte[] enc, String algorithm)
    {
        refreshKeys();
        if (null == enc || 0 == enc.length)
        {
            return BytesUtils.getBytes("");
        }
        byte[] origin = null;
        try
        {
            cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            origin = cipher.doFinal(enc);
        }
        catch (NoSuchAlgorithmException e)
        {
            LOGGER.error("", e);
        }
        catch (NoSuchPaddingException e)
        {
            LOGGER.error("", e);
        }
        catch (InvalidKeyException e)
        {
            LOGGER.error("", e);
        }
        catch (IllegalBlockSizeException e)
        {
            LOGGER.error("", e);
        }
        catch (BadPaddingException e)
        {
            LOGGER.error("", e);
        }
        return origin;
    }
    
    
    /** 
     * 加密文件 
     * @param origin 明文件 
     * @throws IOException 
     */
    public static void encryptFile(File origin)
    {
        FileInputStream fis = null;
        OutputStream fos = null;
        BufferedOutputStream bos = null;
        BufferedInputStream bis = null;
        
        //读入  
        try
        {
            fis = new FileInputStream(origin);
            bis = new BufferedInputStream(fis);
            byte[] originbyte = new byte[originLength];
            
            //写出  
            /**
        	 *codedex 	
        	 *FORTIFY.HW_-_Create_files_with_appropriate_access_permissions_in_multiuser_system
        	 *nwx356892 
        	 */
            fos = FileAttributeUtility.getSafeOutputStream(origin + ".encrypt",false);
            bos = new BufferedOutputStream(fos);
            byte[] encryptbyte;
            
            while (bis.read(originbyte) > 0)
            {
                encryptbyte = encrypt(originbyte);
                if (null != encryptbyte)
                {
                    bos.write(encryptbyte);
                }
                originbyte = new byte[originLength];
            }
            bos.flush();
        }
        catch (FileNotFoundException e)
        {
            LOGGER.error("", e);
        }
        catch (IOException e)
        {
            LOGGER.error("", e);
        }
        finally
        {
            //关闭资源 
            ESDKIOUtils.closeFileStreamNotThrow(bos);
            ESDKIOUtils.closeFileStreamNotThrow(fos);
            ESDKIOUtils.closeFileStreamNotThrow(fis);
            ESDKIOUtils.closeFileStreamNotThrow(bis);
        }
    }
    
    /** 
     * 解密文件 
     * @param encrypt 密文件 
     * @throws IOException 
     */
    public static void decryptFile(File encrypt)
    {
        FileInputStream fis = null;
        OutputStream fos = null;
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        
        try
        {
            //读入  
            fis = new FileInputStream(encrypt);
            bis = new BufferedInputStream(fis);
            byte[] encryptbyte = new byte[encrytLength];
            
            //写出  
            fos = FileAttributeUtility.getSafeOutputStream(encrypt + ".decrypt",false);
            bos = new BufferedOutputStream(fos);
            byte[] originbyte;
            
            while (bis.read(encryptbyte) > 0)
            {
                originbyte = decrypt(encryptbyte);
                if (null != originbyte)
                {
                    bos.write(originbyte);
                }
                encryptbyte = new byte[encrytLength];
            }
            //压入  
            bos.flush();
        }
        catch (FileNotFoundException e)
        {
            LOGGER.error("", e);
        }
        catch (IOException e)
        {
            LOGGER.error("", e);
        }
        finally
        {
            //关闭资源
            ESDKIOUtils.closeFileStreamNotThrow(bos);
            ESDKIOUtils.closeFileStreamNotThrow(fos);
            ESDKIOUtils.closeFileStreamNotThrow(fis);
            ESDKIOUtils.closeFileStreamNotThrow(bis);
        }
    }
    
    public static String decodeFromBase64(String password)
    {
        try
        {
            return new String(RSA2048Utils.decrypt(Base64Utils.getFromBASE64(password)), "UTF-8");
        }
        catch (Exception e)
        {
            LOGGER.error("password decode error", e);
            return "";
        }
    }
    
    private static String getPath() throws Exception
    {
        String result = ConfigManagerNoDecrypt.getInstance().getPureValue("platform.key.path");
        if (null == result || result.contains("@{INSTALLROOT}"))
        {
            result = PathUtil.getAppPath(RSA2048Utils.class);
        }
        
        return result;
    }
    
    private static void refreshKeys()
    {
        String path;
        try
        {
            path = getPath();
            String publicFile = path + "/public.encrypt";
            String privateFile = path + "/private.encrypt";
            File file = new File(publicFile);
            if (file.exists())
            {
                publicKey = readPublicKey(file);
            }
            file = new File(privateFile);
            if (file.exists())
            {
                privateKey = readPrivateKey(file);
            }
        }
        catch (Exception e)
        {
            LOGGER.error("", e);
        }
    }
}