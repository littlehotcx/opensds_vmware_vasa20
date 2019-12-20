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

package org.opensds.platform.commu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.Authenticator;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URL;
import java.util.Locale;

import org.apache.http.client.ClientProtocolException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.opensds.platform.common.constants.ESDKErrorCodeConstant;
import org.opensds.platform.common.utils.ESDKIOUtils;
import org.opensds.platform.common.utils.StringUtils;
import org.opensds.platform.exception.ProtocolAdapterException;

public class HttpProtocolJDKAdapter extends HttpProtocolAdapter
{
    private static final Logger LOGGER = LogManager.getLogger(HttpProtocolJDKAdapter.class);
    
    public HttpProtocolJDKAdapter(String sap)
    {
        super(sap);
    }
    
    @Override
    public Object syncSendMessage(Object reqMessage, String serviceApiName, String resObjClass)
        throws ProtocolAdapterException
    {
        getSdkProtocolAdatperCustProvider().preProcessReq(reqMessage);
        
        String res =
            syncSendMessage(getSdkProtocolAdatperCustProvider().getContent4Sending(reqMessage), serviceApiName);
        
        return getSdkProtocolAdatperCustProvider().postBuildRes(res, resObjClass);
    }
    
    @Override
    public String syncSendMessage(String messageContent, String serviceApiName)
        throws ProtocolAdapterException
    {
        try
        {
            if ("GET".equals(serviceApiName))
            {
                messageContent = messageContent.replaceAll(" ", "%20");
                if (StringUtils.isNotEmpty(messageContent))
                {
                    return doGet(getServerUrl() + "?" + messageContent);
                }
                else
                {
                    return doGet(getServerUrl());
                }
            }
            else
            {
                throw new UnsupportedOperationException();
            }
        }
        catch (UnsupportedEncodingException e)
        {
            LOGGER.error("", e);
            throw new ProtocolAdapterException("syncSendMessage error", e, ESDKErrorCodeConstant.ERROR_CODE_SYS_ERROR);
        }
        catch (ClientProtocolException e)
        {
            LOGGER.error("", e);
            throw new ProtocolAdapterException("syncSendMessage error", e,
                ESDKErrorCodeConstant.ERROR_CODE_NETWORK_ERROR);
        }
        catch (IOException e)
        {
            LOGGER.error("", e);
            throw new ProtocolAdapterException("syncSendMessage error", e,
                ESDKErrorCodeConstant.ERROR_CODE_NETWORK_ERROR);
        }
    }
    
    private String doGet(String urlAsString)
        throws IOException
    {
        InputStream in = null;
        BufferedReader reader = null;
        try
        {
            String[] tempArray = urlAsString.split("msg");
            LOGGER.debug("Sending a HTTP GET request:" + tempArray[0].substring(0, tempArray[0].length() - 1));
            
            CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
            
            Authenticator.setDefault(new ESDKAuthenticator());
            
            URL url = new URL(urlAsString);
            if (urlAsString.toLowerCase(Locale.getDefault()).startsWith("https"))
            {
//                prepareHttpsProperties();
            }
            
            in = url.openConnection().getInputStream();
            
            reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            
//            StringBuilder sb = new StringBuilder();
            StringBuffer buffer = new StringBuffer();
//            String str = reader.readLine();
//            while (null != str)
//            {
//                sb.append(str);
//                str = reader.readLine();
//            }
            
        	int line = 0;
        	do{
        		StringBuffer lineBuffer = new StringBuffer();
        		line = ESDKIOUtils.readLine(reader, lineBuffer);
        		buffer.append(lineBuffer);
        	}
        	while(line==-1);
            
            LOGGER.debug("The response is " + buffer.toString());
            
            return buffer.toString();
        }
        finally
        {
            if (null != reader)
            {
                try
                {
                    reader.close();
                }
                catch (Exception e)
                {
                    LOGGER.error("", e);
                }
            }
            
            if (null != in)
            {
                try
                {
                    in.close();
                }
                catch (Exception e)
                {
                    LOGGER.error("", e);
                }
            }
        }
    }
    
    
    /*
     * CodeDex:  FORTIFY.Setting_Manipulation      by nWX285177
     * Comunicate with zhangzhili and do it
     */
//    private void prepareHttpsProperties()
//    {
//        String temp = ConfigManager.getInstance().getValue("sms.ssl.trustStore");
//        if (!StringUtils.isEmpty(temp))
//        {
//            System.setProperty("javax.net.ssl.trustStore", temp);
//        }
//        
//        temp = ConfigManager.getInstance().getValue("sms.ssl.trustStorePassword");
//        if (!StringUtils.isEmpty(temp))
//        {
//        	 /*
//             * CodeDex:  FORTIFY.Setting_Manipulation      by nWX285177
//             */
//            System.setProperty("javax.net.ssl.trustStorePassword", AES128System.decryptPwdByOldKey("", PathUtils.PswdFormat(temp)));
//        }
//        
//        temp = ConfigManager.getInstance().getValue("sms.ssl.keyStore");
//        if (!StringUtils.isEmpty(temp))
//        {
//            System.setProperty("javax.net.ssl.keyStore", temp);
//        }
//        
//        temp = ConfigManager.getInstance().getValue("sms.ssl.keyStorePassword");
//        if (!StringUtils.isEmpty(temp))
//        {
//            temp = AES128System.decryptPwdByOldKey("", temp);
//            /*
//             * CodeDex:  FORTIFY.Setting_Manipulation      by nWX285177
//             */
//            System.setProperty("javax.net.ssl.keyStorePassword", PathUtils.PswdFormat(temp));
//        }
//        
//        temp = ConfigManager.getInstance().getValue("sms.ssl.keyStoreType");
//        if (!StringUtils.isEmpty(temp))
//        {
//            System.setProperty("javax.net.ssl.keyStoreType", temp);
//        }
//    }
}
