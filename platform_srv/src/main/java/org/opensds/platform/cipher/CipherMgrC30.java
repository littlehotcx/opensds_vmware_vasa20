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

package org.opensds.platform.cipher;

import java.io.File;
import java.security.interfaces.RSAPublicKey;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.opensds.platform.cipher.itf.CipherMgr;
import org.opensds.platform.common.config.ConfigManagerNoDecrypt;
import org.opensds.platform.common.utils.AES128System;
import org.opensds.platform.common.utils.AES128Utils;
import org.opensds.platform.common.utils.PathUtil;
import org.opensds.platform.common.utils.RSA2048Utils;

public class CipherMgrC30 implements CipherMgr
{
    private static Logger LOGGER = LogManager.getLogger(CipherMgrC30.class);
    
    /* 
     * 供eSDK使用
     */
    @Override
    public synchronized RSAPublicKey getPublicKey()
    {
        LOGGER.debug("getPublicKey method start");
        
        RSAPublicKey key = null;
        try
        {
            key = RSA2048Utils.getPublicKey(getPath() + "/public.encrypt");
        }
        catch (Exception e)
        {
            LOGGER.error("", e);
        }
        
        LOGGER.debug("getPublicKey method end");
        return key;
    }
    
    /* 
     * 供 OM 和 mgmt 使用
     */
    @Override
    public synchronized void updatePrivateKey()
        throws Exception
    {
        LOGGER.debug("updatePrivateKey method start");
        
        String path = getPath();
        LOGGER.debug("CipherMgrC30 path:" + path);
        File file = new File(path);
        if(!file.exists())
        {
            file.mkdirs();
        }
        
        // 将旧的RSA的公钥和私钥保存到文件
        //        file = new File(path + "/private.encrypt.backup");
        file = new File(path + "/private.encrypt.backup");
        LOGGER.debug("CipherMgrC30 path2:" + file.getAbsolutePath());
        RSA2048Utils.savePrivateKey(file);
        
        file = new File(path + "/public.encrypt.backup");
        RSA2048Utils.savePublicKey(file);
        
        // 生成RSA的公钥和私钥,并保存到文件
        RSA2048Utils.initKey();
        
        file = new File(path + "/private.encrypt");
        RSA2048Utils.savePrivateKey(file);
        
        file = new File(path + "/public.encrypt");
        RSA2048Utils.savePublicKey(file);
        
        LOGGER.debug("updatePrivateKey method end");
    }
    
    /* 
     * 供 OM 和 mgmt 使用
     * 必须保证文件系统中的“根密钥”和“加密‘系统密钥’的根密钥”相同
     */
    @Override
    public synchronized void updateSystemKey()
        throws Exception
    {
        LOGGER.debug("updateSystemKey method start");
        
        String path = getPath();
        File file = new File(path);
        if(!file.exists())
        {
            file.mkdirs();
        }
        
        // 生成密钥因子,并保存到文件
        file = new File(path + "/KEY.encrypt.backup");
        
        // 保存旧的密钥因子
        // byte[] BT_KEY = AES128Utils.getKey();
        AES128Utils.saveKey(AES128Utils.getKey(), file);
        
        // 更新根密钥
        file = new File(path + "/KEY.encrypt");
        AES128Utils.updateKey();
        
        // 使用旧key解密系统密钥(文件中),并使用新key加密系统密钥后保存到文件。
        byte[] systemKey = AES128System.getKeyFromFile();
        
        // 保存根密钥到文件
        AES128Utils.saveKey(AES128Utils.getKey(), file);
        
        // 使用新key加密系统密钥后保存到文件
        AES128System.saveKey(systemKey);
        
        // 保存RSA的公钥和私钥到文件
        file = new File(path + "/private.encrypt");
        RSA2048Utils.savePrivateKey(file);
        
        file = new File(path + "/public.encrypt");
        RSA2048Utils.savePublicKey(file);
        LOGGER.debug("updateSystemKey method end");
    }
    
    /**
     * 1.eSDK系统启动时，调用密钥管理模块的接口，启动密钥因子生成流程。
     * 
     * 由安全随机函数生成，支持可配置，长度必须在128位及以上，
     * 在初次安装的时候，调用安全随机数接口生成128位安全随机数，并采用BASE64编码后保存到配置文件中
     * 
     * 2.生成RSA公钥和私钥，并保存到文件。
     * @throws Exception 
     * 
     */
    @Override
    public synchronized void initSystemKey()
        throws Exception
    {
        if (!"Y".equalsIgnoreCase(ConfigManagerNoDecrypt.getInstance().getValue("platform.mgmt.srv")))
        {
            LOGGER.debug("initSystemKey method start");
            
            String path = getPath();
            LOGGER.debug("path=" + path);
            File file = new File(path);
            if(!file.exists())
            {
                file.mkdirs();
            }
            
            // 生成根密钥因子,并保存到文件
            file = new File(path + "/KEY.encrypt");
            AES128Utils.saveKey(AES128Utils.getKey(), file);
            
            // 生成RSA的公钥和私钥,并保存到文件
            file = new File(path + "/private.encrypt");
            RSA2048Utils.savePrivateKey(file);
            
            file = new File(path + "/public.encrypt");
            RSA2048Utils.savePublicKey(file);
            
            LOGGER.debug("initSystemKey method end");
        }
    }
    
    private String getPath() throws Exception
    {
        String result = ConfigManagerNoDecrypt.getInstance().getPureValue("platform.key.path");
        if (null == result || result.contains("@{INSTALLROOT}"))
        {
            result = PathUtil.getAppPath(CipherMgrC30.class);
        }
        
        return result;
    }
}
