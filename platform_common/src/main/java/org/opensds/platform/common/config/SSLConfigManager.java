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

package org.opensds.platform.common.config;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import org.opensds.platform.common.constants.ESDKConstant;
import org.opensds.platform.common.utils.AES128System;
import org.opensds.platform.common.utils.StringUtils;

public class SSLConfigManager{

	private static Properties properties  = new Properties();
	
	public static String SSL_CONFIG_FILE = "vasa_private_ext_conf.properties";
	
	private static Boolean SSLCertVerify = null;
	
	private static String confPath = null;
	
	static{
		String classPath = SSLConfigManager.class.getResource("/").getPath();
		confPath = classPath.substring(0, classPath.length()-27)+"conf/";
	}
	
	private static org.apache.logging.log4j.Logger LOGGER = org.apache.logging.log4j.LogManager.getLogger(SSLConfigManager.class);
	
	public static void loadConfigs(String absolutePath) throws Exception{
		properties.load(new BufferedInputStream(new FileInputStream( new File(absolutePath))));
	}

	public static String getProperty(String key){
		String propertyValue = properties.getProperty(key, null);
		if (!StringUtils.isEmpty(propertyValue) && ConfigManagerUpdate.sensitiveList.contains(key))
        {
			propertyValue = AES128System.decryptPwdByOldKey("", propertyValue);
        }
		LOGGER.debug("get property key="+key+",value="+propertyValue);
		return propertyValue;
	}
	
	
	public static SSLContext createSSLContext() throws KeyManagementException, NoSuchAlgorithmException{
		if(null == SSLCertVerify) {
			SSLCertVerify = Boolean.valueOf(ConfigManager.getInstance().getValue("vasa.agent.ssl.verify"));
		}
		LOGGER.info("create createSSLContext boolean = "+SSLCertVerify);
		if(null != SSLCertVerify && !SSLCertVerify){
			return createNoCertSSLContext();
		}else{
			try {
				return getSSLContext();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				LOGGER.error("create ssl context fail.",e);
				return null;
			}
		}
	}
	
	
	//生成SSLContext 读取证书使用
		public static SSLContext getSSLContext() throws Exception { 
			LOGGER.info("create sslcontext with cert-----------begin");
	        // 实例化密钥库   KeyManager选择证书证明自己的身份
	        KeyManagerFactory keyManagerFactory = KeyManagerFactory  
	                .getInstance(KeyManagerFactory.getDefaultAlgorithm());  
	        // 获得密钥库  
	        KeyStore keyStore = getKeyStore(SSLConfigManager.getProperty("vasa.ssl.array.keystorePass"), confPath+SSLConfigManager.getProperty("vasa.ssl.array.keystoreFile"));  
	        // 初始化密钥工厂  
	        keyManagerFactory.init(keyStore, SSLConfigManager.getProperty("vasa.ssl.array.keystorePass").toCharArray());  
	  
	        // 实例化信任库    TrustManager决定是否信任对方的证书
	        TrustManagerFactory trustManagerFactory = TrustManagerFactory  
	                .getInstance(TrustManagerFactory.getDefaultAlgorithm());  
	        // 获得信任库  
	        KeyStore trustStore = getKeyStore(SSLConfigManager.getProperty("vasa.ssl.array.truststorePass"), confPath+SSLConfigManager.getProperty("vasa.ssl.array.truststoreFile"));  
	        // 初始化信任库  
	        trustManagerFactory.init(trustStore);  
	        // 实例化SSL上下文  
	        SSLContext ctx = SSLContext.getInstance("TLS");  
	        // 初始化SSL上下文  
	        ctx.init(keyManagerFactory.getKeyManagers(),  
	                trustManagerFactory.getTrustManagers(), null);  
	        // 获得SSLSocketFactory  
	        LOGGER.info("create sslcontext with cert-----------end");
	        return ctx;  
	    }  
	    
	    public static KeyStore getKeyStore(String password, String keyStorePath)  
	            throws Exception {  
	        // 实例化密钥库 KeyStore用于存放证书，创建对象时 指定交换数字证书的加密标准 
	        //指定交换数字证书的加密标准 
	        KeyStore ks = KeyStore.getInstance("JKS");  
	        // 获得密钥库文件流  
	        FileInputStream is = new FileInputStream(keyStorePath);  
	        // 加载密钥库  
	        ks.load(is, password.toCharArray());  
	        // 关闭密钥库文件流  
	        is.close();  
	        return ks;  
	    } 
		
	    private static SSLContext createNoCertSSLContext()
				throws NoSuchAlgorithmException, KeyManagementException {
			SSLContext ctx = SSLContext.getInstance(ESDKConstant.SSL_SECURE_SOCKET_PROTOCOL_TLSv1_2);
			X509TrustManager tm = new X509TrustManager() {
				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					return new java.security.cert.X509Certificate[] {};
				}

				@Override
				public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType)
						throws java.security.cert.CertificateException {
				}

				@Override
				public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType)
						throws java.security.cert.CertificateException {
				}

			};
			ctx.init(null, new TrustManager[] { tm }, null);
			return ctx;
		}
	
}
