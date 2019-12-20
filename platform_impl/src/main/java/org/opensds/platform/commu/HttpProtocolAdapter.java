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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensds.platform.common.constants.ESDKConstant;
import org.opensds.platform.common.constants.ESDKErrorCodeConstant;
import org.opensds.platform.commu.itf.ISDKProtocolAdapter;
import org.opensds.platform.commu.itf.ISDKProtocolAdatperCustProvider;
import org.opensds.platform.exception.ProtocolAdapterException;
import org.opensds.platform.util.PathUtils;

public class HttpProtocolAdapter implements ISDKProtocolAdapter
{
    private static Logger LOGGER = LogManager.getLogger(HttpProtocolAdapter.class);
    
    private ISDKProtocolAdatperCustProvider sdkProtocolAdatperCustProvider;
    
    private HttpClient httpClient;
    
    private String serverUrl;
    
    public HttpProtocolAdapter(String serverUrl)
    {
        this.serverUrl = serverUrl;
        
        ClientConnectionManager conMgr = new PoolingClientConnectionManager();
        httpClient = new DefaultHttpClient(conMgr);
        
        //修改问题单DTS2015012906716 ， start
        if (serverUrl.toLowerCase(Locale.getDefault()).startsWith("https"))
        {
            try
            {
                configHttps(httpClient, conMgr);
            }
            catch (KeyManagementException e)
            {
                LOGGER.error("HttpProtocolAdapter config https failed!", e);
            }
            catch (NoSuchAlgorithmException e)
            {
                LOGGER.error("HttpProtocolAdapter config https failed!", e);
            }
            catch (MalformedURLException e)
            {
                LOGGER.error("HttpProtocolAdapter config https failed!", e);
            }
        }
        //end
    }
    
    private void configHttps(HttpClient httpclient, ClientConnectionManager conMgr)
        throws NoSuchAlgorithmException, KeyManagementException, MalformedURLException
    {
        SSLContext ctx = SSLContext.getInstance(ESDKConstant.SSL_SECURE_SOCKET_PROTOCOL);
        X509TrustManager tm = new X509TrustManager()
        {
            public void checkClientTrusted(X509Certificate[] xcs, String string)
                throws CertificateException
            {
                
            }
            
            public void checkServerTrusted(X509Certificate[] xcs, String string)
                throws CertificateException
            {
            }
            
            public X509Certificate[] getAcceptedIssuers()
            {
                return new X509Certificate[] {};
            }
        };
        
        ctx.init(null, new TrustManager[] {tm}, null);
        SSLSocketFactory ssf = new SSLSocketFactory(ctx, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        SchemeRegistry registry = conMgr.getSchemeRegistry();
        
        //https默认端口443
        int port = 443;
        /*
         * CodeDex:  FORTIFY.Resource_Injection      by nWX285177
         */
        URL url = new URL(PathUtils.UrlPathFormat(serverUrl));
        if (-1 != url.getPort())
        {
            port = url.getPort();
        }
        
        registry.register(new Scheme(ESDKConstant.PROTOCOL_ADAPTER_TYPE_HTTPS, port, ssf));
    }
    
    protected String getServerUrl()
    {
        return serverUrl;
    }
    
    protected void setServerUrl(String serverUrl)
    {
        this.serverUrl = serverUrl;
    }
    
    public String getServiceAccessPoint()
    {
        return this.serverUrl;
    }
    
    @Override
    public Object syncSendMessage(Object reqMessage, String serviceApiName, String resObjClass)
        throws ProtocolAdapterException
    {
        sdkProtocolAdatperCustProvider.preProcessReq(reqMessage);
        
        String res = syncSendMessage(sdkProtocolAdatperCustProvider.getContent4Sending(reqMessage), serviceApiName);
        
        return sdkProtocolAdatperCustProvider.postBuildRes(res, resObjClass);
    }
    
    @Override
    public String syncSendMessage(String messageContent, String serviceApiName)
        throws ProtocolAdapterException
    {
        HttpPost httpPost = null;
        try
        {
            httpPost = buildHttpPost(messageContent, serviceApiName);
            
            preSend(messageContent);
            
            // Send the http request
            HttpResponse response = httpClient.execute(httpPost);
            
            // Process the response header
            postSend(response);
            
            // Process the response body
            HttpEntity entity = response.getEntity();
            String responsePayload = EntityUtils.toString(entity);
            
            // replace session before log
            String logMessage = response.toString();
            if (-1 < logMessage.indexOf("SessionID="))
            {
                int begin = logMessage.indexOf("SessionID=") + 10;
                int end = logMessage.indexOf(",", begin);
                if (end > begin)
                {
                    int length = (end - begin) / 2;
                    String temp = logMessage.substring(begin, end);
                    StringBuffer rep = new StringBuffer();
                    for (int i = 0; i < length; i++)
                    {
                        rep.append("*");
                    }
                    rep.append(logMessage.substring(begin + length, end));
                    logMessage = logMessage.replace(temp, rep);
                }
            }
            LOGGER.debug("The response content is:" + logMessage);
            return responsePayload;
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
        finally
        {
            if (null != httpPost)
            {
                httpPost.releaseConnection();
            }
        }
    }
    
    protected HttpPost buildHttpPost(String messageContent, String serviceApiName)
        throws UnsupportedEncodingException
    {
        HttpPost httpPost = new HttpPost(getUrl(serviceApiName));
        StringEntity entity = new StringEntity(messageContent, "UTF-8");
        httpPost.setEntity(entity);
        
        Map<String, String> headers = getRequestHeaders();
        if (headers != null)
        {
            Set<Map.Entry<String, String>> es = headers.entrySet();
            for (Map.Entry<String, String> item : es)
            {
                httpPost.addHeader(item.getKey(), item.getValue());
            }
        }
        
        return httpPost;
    }
    
    protected String getUrl(String serviceApiName)
    {
        return sdkProtocolAdatperCustProvider.reBuildNewUrl(serverUrl, serviceApiName);
    }
    
    protected Map<String, String> getRequestHeaders()
    {
        // For override by derived class
        return sdkProtocolAdatperCustProvider.getRequestHeaders();
    }
    
    protected void preSend(Object request)
    {
        sdkProtocolAdatperCustProvider.preSend(request);
    }
    
    protected void postSend(Object response)
        throws ProtocolAdapterException
    {
        // For override by derived class
        sdkProtocolAdatperCustProvider.postSend(response);
    }
    
    @Override
    public boolean heartBeat()
        throws ProtocolAdapterException
    {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public int login(String userName, String pwd)
        throws ProtocolAdapterException
    {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public int logout()
        throws ProtocolAdapterException
    {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public ISDKProtocolAdatperCustProvider getSdkProtocolAdatperCustProvider()
    {
        return this.sdkProtocolAdatperCustProvider;
    }
    
    @Override
    public void setSdkProtocolAdatperCustProvider(ISDKProtocolAdatperCustProvider sdkProtocolAdatperCustProvider)
    {
        this.sdkProtocolAdatperCustProvider = sdkProtocolAdatperCustProvider;
    }
}
