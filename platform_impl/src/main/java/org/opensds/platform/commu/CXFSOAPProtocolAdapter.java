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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import javax.xml.ws.soap.SOAPFaultException;

import org.apache.cxf.common.util.StringUtils;
import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.headers.Header;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.jaxb.JAXBDataBinding;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.message.Message;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensds.platform.common.config.ConfigManager;
import org.opensds.platform.common.constants.ESDKConstant;
import org.opensds.platform.common.constants.ESDKErrorCodeConstant;
import org.opensds.platform.commu.itf.ICXFSOAPCustProvider;
import org.opensds.platform.commu.itf.ICXFSOAPProtocolAdapter;
import org.opensds.platform.commu.itf.ISDKProtocolAdatperCustProvider;
import org.opensds.platform.exception.ProtocolAdapterException;

public class CXFSOAPProtocolAdapter implements ICXFSOAPProtocolAdapter
{
    private static final Logger LOGGER = LogManager
            .getLogger(CXFSOAPProtocolAdapter.class);

    /*
     * 设备访问点地址
     */
    private String serviceAccessPoint;
    
    /*
     * CXF协议定制化需求提供器
     */
    private ICXFSOAPCustProvider cxfSOAPCustProvider;
    
    /*
     * 接口调用对象Map
     */
    private static final Map<String, CachedObjectsBean> CLIENT_MAP = new HashMap<String, CachedObjectsBean>();

    public CXFSOAPProtocolAdapter(String serviceAccessPoint)
    {
        this.serviceAccessPoint = serviceAccessPoint;
    }

    @Override
    public Object syncSendMessageWithCxf(Object reqMessage,
            String itfClassName, String serviceApiName)
            throws ProtocolAdapterException
    {
        try
        {
        	//tls协商
            Object proxy11 = getClient(getURL(itfClassName),
                    Class.forName(itfClassName), ESDKConstant.SSL_SECURE_SOCKET_PROTOCOL_TLS11);
            Object proxy12 = getClient(getURL(itfClassName),
                    Class.forName(itfClassName), ESDKConstant.SSL_SECURE_SOCKET_PROTOCOL_TLSv1_2);
            Object proxy10 = getClient(getURL(itfClassName),
                    Class.forName(itfClassName), ESDKConstant.SSL_SECURE_SOCKET_PROTOCOL_TLS);
            
            if (null == proxy11 && null == proxy12 && null == proxy10)
            {
                throw new ProtocolAdapterException(
                        "syncSendMessageWithCxf error: proxy is null",
                        ESDKErrorCodeConstant.ERROR_CODE_SYS_ERROR);
            }

            Method m = null;
            Object res = null;
            try
            {
            	/**
            	 *  CodeDEX:FORTIFY.Redundant_Null_Check start
            	 *  Modified by wWX315527 2017/02/09
            	 */
            	if(proxy11 != null){
            		m = proxy11.getClass().getMethod(serviceApiName, reqMessage.getClass());
            		res = m.invoke(proxy11, reqMessage);
            	}
            	/**
            	 *  CodeDEX:FORTIFY.Redundant_Null_Check end
            	 *  Modified by wWX315527 2017/02/09
            	 */
            }
            catch(Exception e11)
            {
            	try
            	{
                	/**
                	 *  CodeDEX:FORTIFY.Redundant_Null_Check start
                	 *  Modified by wWX315527 2017/02/09
                	 */
            		if(proxy12 != null){
            			m = proxy12.getClass().getMethod(serviceApiName, reqMessage.getClass());
            			res = m.invoke(proxy12, reqMessage);
            		}
                	/**
                	 *  CodeDEX:FORTIFY.Redundant_Null_Check end
                	 *  Modified by wWX315527 2017/02/09
                	 */
            	}
            	catch(Exception e12)
            	{
					m = proxy10.getClass().getMethod(serviceApiName, reqMessage.getClass());
					res = m.invoke(proxy10, reqMessage);
            	}
            }
            return res;
        }
        catch (JAXBException e)
        {
            LOGGER.error("", e);
            throw new ProtocolAdapterException("syncSendMessageWithCxf error",
                    e, ESDKErrorCodeConstant.ERROR_CODE_SYS_ERROR);
        }
        catch (ClassNotFoundException e)
        {
            LOGGER.error("", e);
            throw new ProtocolAdapterException("syncSendMessageWithCxf error",
                    e, ESDKErrorCodeConstant.ERROR_CODE_SYS_ERROR);
        }
        catch (SecurityException e)
        {
            LOGGER.error("", e);
            throw new ProtocolAdapterException("syncSendMessageWithCxf error",
                    e, ESDKErrorCodeConstant.ERROR_CODE_SYS_ERROR);
        }
        catch (NoSuchMethodException e)
        {
            LOGGER.error("", e);
            throw new ProtocolAdapterException("syncSendMessageWithCxf error",
                    e, ESDKErrorCodeConstant.ERROR_CODE_SYS_ERROR);
        }
        catch (IllegalArgumentException e)
        {
            LOGGER.error("", e);
            throw new ProtocolAdapterException("syncSendMessageWithCxf error",
                    e, ESDKErrorCodeConstant.ERROR_CODE_SYS_ERROR);
        }
        catch (IllegalAccessException e)
        {
            LOGGER.error("", e);
            throw new ProtocolAdapterException("syncSendMessageWithCxf error",
                    e, ESDKErrorCodeConstant.ERROR_CODE_SYS_ERROR);
        }
        catch (InvocationTargetException e)
        {
            LOGGER.error("", e);
            Throwable t = e.getTargetException();
            if (null != t && t instanceof SOAPFaultException)
            {
                SOAPFaultException sfe = (SOAPFaultException) t;
                LOGGER.error("fault code = " + sfe.getFault().getFaultCode());
                if (null != sfe.getFault().getDetail()
                        && null != sfe.getFault().getDetail().getFirstChild())
                {
                    LOGGER.error("detail = "
                            + sfe.getFault().getDetail().getFirstChild()
                                    .getTextContent());
                }
                LOGGER.error("faultactor = " + sfe.getFault().getFaultActor());
                LOGGER.error("fault string = "
                        + sfe.getFault().getFaultString());
                
                if (null != t.getMessage()
                    && (t.getMessage().contains("Connection timed out") || t.getMessage().contains("连接超时")))
                {
                    throw new ProtocolAdapterException("syncSendMessageWithCxf error", e,
                        ESDKErrorCodeConstant.ERROR_CODE_NETWORK_ERROR);
                }
                
                throw new ProtocolAdapterException(
                        "syncSendMessageWithCxf error",
                        e,
                        ESDKErrorCodeConstant.ERROR_CODE_DEVICE_SERVICE_EXCEPTION);
            }
            throw new ProtocolAdapterException("syncSendMessageWithCxf error",
                    e, ESDKErrorCodeConstant.ERROR_CODE_NETWORK_ERROR);
        }
        catch (NoSuchAlgorithmException e)
        {
            LOGGER.error("No Such Algorithm Exception", e);
            throw new ProtocolAdapterException("syncSendMessageWithCxf error",
                    e, ESDKErrorCodeConstant.ERROR_CODE_SYS_ERROR);
        }
        catch (KeyManagementException e)
        {
            LOGGER.error("Key Management Exception", e);
            throw new ProtocolAdapterException("syncSendMessageWithCxf error",
                    e, ESDKErrorCodeConstant.ERROR_CODE_SYS_ERROR);
        }
    }

    private String getURL(String itfClassName)
    {
        String resultURI = null;
        String resultURL = null;
        if (null != cxfSOAPCustProvider && null != cxfSOAPCustProvider.getSerivceURIMapping() && null != itfClassName)
        {
            String key = itfClassName;
            int index = itfClassName.lastIndexOf(".");
            if (index > -1)
            {
                key = itfClassName.substring(index + 1);
            }
            resultURI = cxfSOAPCustProvider.getSerivceURIMapping().get(key);
        }
        
        if (StringUtils.isEmpty(resultURI))
        {
            resultURL = this.serviceAccessPoint;
        }
        else
        {
            resultURL = this.serviceAccessPoint + resultURI;
        }
        
        if (null != cxfSOAPCustProvider)
        {
            resultURL = cxfSOAPCustProvider.reBuildNewUrl(resultURL, itfClassName);
        }
        
        return resultURL;
    }
  
    //加入tsl协议入参(tlsv1.1 tlsv1.2 tlsv1)
    protected Object getClient(String url,
            Class<? extends Object> clz, String sslProtocol) throws JAXBException, NoSuchAlgorithmException, KeyManagementException
    {
    	CachedObjectsBean item = null;
    	synchronized(this)
    	{
    		item = CLIENT_MAP.get(url + clz.getName());
    	}
    	
        if (null != item)
        {
            Client client = item.getClient();
            client.getRequestContext().put(Header.HEADER_LIST, getHeaderList());
            return item.getService();
        }
        else
        {
            JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
            
            boolean cxfValidationFlag =Boolean.parseBoolean(ConfigManager.getInstance()
                .getValue("cxfValidationFlag", "true"));
            
            if(!cxfValidationFlag)
            {
                Map<String, Object> properties = new HashMap<String, Object>();
                properties.put("set-jaxb-validation-event-handler", false);
                factory.setProperties(properties);
            }
            
            factory.setAddress(url.split("\\|")[0]);

            Object service = factory.create(clz);
            Client client = ClientProxy.getClient(service);

            // Add the SOAP HEADER info
            client.getRequestContext().put(Header.HEADER_LIST, getHeaderList());

            client.getOutInterceptors().addAll(getOutInterceptors());
            client.getInInterceptors().addAll(getInInterceptors());

            // Setting HTTP Related information
            HTTPConduit http = (HTTPConduit) client.getConduit();
            if (null == http)
            {
                return null;
            }
            
            if(url.startsWith(ESDKConstant.PROTOCOL_ADAPTER_TYPE_HTTPS))
            {
                TLSClientParameters tlsParams = http.getTlsClientParameters();
                
                if (null == tlsParams)
                {
                    tlsParams = new TLSClientParameters();
//                    tlsParams.setSecureSocketProtocol(ESDKConstant.SSL_SECURE_SOCKET_PROTOCOL_TLS11);
                    tlsParams.setSecureSocketProtocol(sslProtocol);
                }
                
                X509TrustManager tm = new X509TrustManager() {
                    public void checkClientTrusted(X509Certificate[] xcs, String string) throws CertificateException { }

                    public void checkServerTrusted(X509Certificate[] xcs, String string) throws CertificateException { }

                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[]{};
                    }
                };
//                SSLContext sslContext = SSLContext.getInstance(ESDKConstant.SSL_SECURE_SOCKET_PROTOCOL_TLS11);
                SSLContext sslContext = SSLContext.getInstance(sslProtocol);
                
                sslContext.init(null, new TrustManager[]{tm}, null);
                SSLSocketFactory ssf = sslContext.getSocketFactory();
                tlsParams.setSSLSocketFactory(ssf);
                tlsParams.setUseHttpsURLConnectionDefaultHostnameVerifier(false);
                tlsParams.setDisableCNCheck(true);
                
                http.setTlsClientParameters(tlsParams);
            }
            
            HTTPClientPolicy httpClientPolicy = new HTTPClientPolicy();
            httpClientPolicy.setConnectionTimeout(36000);
            httpClientPolicy.setAllowChunking(false);
            httpClientPolicy.setReceiveTimeout(32000);
            http.setClient(httpClientPolicy);

            item = new CachedObjectsBean();
            item.setService(service);
            item.setClient(client);            
            synchronized(this)
            {
            	CLIENT_MAP.put(url + clz.getName(), item);
            }
            return service;
        }
    }
    
    private List<Header> getHeaderList()
    {
        List<Header> result = new ArrayList<Header>();
        if (null == cxfSOAPCustProvider)
        {
            return result;
        }
        
        Map<String, String> headers = cxfSOAPCustProvider.getSoapHeaders();
        if (null != headers)
        {
            Header header;
            Set<Entry<String, String>> entries = headers.entrySet();
            for (Entry<String, String> entry : entries)
            {
                try
                {
                    header = new Header(new QName("", entry.getKey()),
                            entry.getValue(), new JAXBDataBinding(String.class));
                    result.add(header);
                }
                catch (JAXBException e)
                {
                    LOGGER.error("Prepare header error of " + entry.getKey() + ":"
                            + entry.getValue(), e);
                }
            }
        }
        
        return result;
    }
    
    @SuppressWarnings("unchecked")
    private List<Interceptor<Message>> getInInterceptors()
    {
        List<Interceptor<Message>> inInterceptors = new ArrayList<Interceptor<Message>>();        
        if (null != cxfSOAPCustProvider && null != cxfSOAPCustProvider.getInInterceptors())
        {
            List<Object> pInInterceptors = cxfSOAPCustProvider.getInInterceptors();
            for (Object obj : pInInterceptors)
            {
                if (obj instanceof Interceptor<?>)
                {
                    inInterceptors.add((Interceptor<Message>)obj);
                }
            }
        }
        
        return inInterceptors;
    }
    
    @SuppressWarnings("unchecked")
    private List<Interceptor<Message>> getOutInterceptors()
    {
        List<Interceptor<Message>> outInterceptors = new ArrayList<Interceptor<Message>>();    
        if (null != cxfSOAPCustProvider && null != cxfSOAPCustProvider.getOutInterceptors())
        {
            List<Object> pOutInterceptors = cxfSOAPCustProvider.getOutInterceptors();
            for (Object obj : pOutInterceptors)
            {
                if (obj instanceof Interceptor<?>)
                {
                    outInterceptors.add((Interceptor<Message>)obj);
                }
            }
        }
        
        return outInterceptors;
    }
    
    @Override
    public Object syncSendMessage(Object reqMessage, String serviceApiName,
            String resObjClass) throws ProtocolAdapterException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public String syncSendMessage(String messageContent, String serviceApiName)
            throws ProtocolAdapterException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean heartBeat() throws ProtocolAdapterException
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
    public int logout() throws ProtocolAdapterException
    {
        throw new UnsupportedOperationException();
    }

    public String getServiceAccessPoint()
    {
        return serviceAccessPoint;
    }

    public void setServiceAccessPoint(String serviceAccessPoint)
    {
        this.serviceAccessPoint = serviceAccessPoint;
    }
    
    public ICXFSOAPCustProvider getCXFSOAPCustProvider()
    {
        return cxfSOAPCustProvider;
    }

    public void setCXFSOAPCustProvider(ICXFSOAPCustProvider cxfSOAPCustProvider)
    {
        this.cxfSOAPCustProvider = cxfSOAPCustProvider;
    }

    @Override
    public ISDKProtocolAdatperCustProvider getSdkProtocolAdatperCustProvider()
    {
        return null;
    }

    @Override
    public void setSdkProtocolAdatperCustProvider(
            ISDKProtocolAdatperCustProvider sdkProtocolAdatperCustProvider)
    {
    }

    @Override
    public Object syncSendMessageWithCxf(Class<?> itfClass, String serviceApi, Object... reqMessage) throws ProtocolAdapterException
    {
    	return syncSendMessageWithCxf(itfClass, serviceApi, null, reqMessage);
    }
    
    @Override
    public Object syncSendMessageWithCxf(Class<?> itfClass, String serviceApi, Class[] clsz, Object... reqMessage) throws ProtocolAdapterException
    {
    	try
    	{
    		//tls协商
    		Object proxy11 = getClient(getURL(itfClass.getName()), itfClass, ESDKConstant.SSL_SECURE_SOCKET_PROTOCOL_TLS11);
    		Object proxy12 = getClient(getURL(itfClass.getName()), itfClass, ESDKConstant.SSL_SECURE_SOCKET_PROTOCOL_TLSv1_2);
    		Object proxy10 = getClient(getURL(itfClass.getName()), itfClass, ESDKConstant.SSL_SECURE_SOCKET_PROTOCOL_TLS);
    		
    		if (null == proxy11 && null == proxy12 && null == proxy10)
    		{
    			throw new ProtocolAdapterException(
    					"syncSendMessageWithCxf error: proxy is null",
    					ESDKErrorCodeConstant.ERROR_CODE_SYS_ERROR);
    		}
    		
    		if (null == clsz)
    		{
    			clsz = new Class[]{};
    		}
    		
    		Method m = null;
    		Object res = null;
    		Object proxy = null;
    		
    		proxy = proxy11 != null ? proxy11 : (proxy12 != null ? proxy12 : proxy10);
            m = proxy.getClass().getMethod(serviceApi, clsz);
    		res = m.invoke(proxy, reqMessage);
    		
/*    		try
    		{
        		m = proxy11.getClass().getMethod(serviceApi, clsz);
        		res = m.invoke(proxy11, reqMessage);
    		}
    		catch(Exception e11)
    		{
    			try
    			{
            		m = proxy12.getClass().getMethod(serviceApi, clsz);
            		res = m.invoke(proxy12, reqMessage);
    			}
    			catch(Exception e12)
    			{
            		m = proxy10.getClass().getMethod(serviceApi, clsz);
            		res = m.invoke(proxy10, reqMessage);
    			}
    		}*/
    		
    		return res;
    	}
    	catch (JAXBException e)
    	{
    		LOGGER.error("", e);
    		throw new ProtocolAdapterException("syncSendMessageWithCxf error",
    				e, ESDKErrorCodeConstant.ERROR_CODE_SYS_ERROR);
    	}
    	catch (SecurityException e)
    	{
    		LOGGER.error("", e);
    		throw new ProtocolAdapterException("syncSendMessageWithCxf error",
    				e, ESDKErrorCodeConstant.ERROR_CODE_SYS_ERROR);
    	}
    	catch (NoSuchMethodException e)
    	{
    		LOGGER.error("", e);
    		throw new ProtocolAdapterException("syncSendMessageWithCxf error",
    				e, ESDKErrorCodeConstant.ERROR_CODE_SYS_ERROR);
    	}
    	catch (IllegalArgumentException e)
    	{
    		LOGGER.error("", e);
    		throw new ProtocolAdapterException("syncSendMessageWithCxf error",
    				e, ESDKErrorCodeConstant.ERROR_CODE_SYS_ERROR);
    	}
    	catch (IllegalAccessException e)
    	{
    		LOGGER.error("", e);
    		throw new ProtocolAdapterException("syncSendMessageWithCxf error",
    				e, ESDKErrorCodeConstant.ERROR_CODE_SYS_ERROR);
    	}
    	catch (InvocationTargetException e)
    	{
    		LOGGER.error("", e);
    		Throwable t = e.getTargetException();
    		if (null != t && t instanceof SOAPFaultException)
    		{
    			SOAPFaultException sfe = (SOAPFaultException) t;
    			LOGGER.error("fault code = " + sfe.getFault().getFaultCode());
    			if (null != sfe.getFault().getDetail()
    					&& null != sfe.getFault().getDetail().getFirstChild())
    			{
    				LOGGER.error("detail = "
    						+ sfe.getFault().getDetail().getFirstChild()
    						.getTextContent());
    			}
    			LOGGER.error("faultactor = " + sfe.getFault().getFaultActor());
    			LOGGER.error("fault string = "
    					+ sfe.getFault().getFaultString());
    			
    			if (null != t.getMessage()
    					&& (t.getMessage().contains("Connection timed out") || t.getMessage().contains("连接超时")))
    			{
    				throw new ProtocolAdapterException("syncSendMessageWithCxf error", e,
    						ESDKErrorCodeConstant.ERROR_CODE_NETWORK_ERROR);
    			}
    			
    			throw new ProtocolAdapterException(
    					"syncSendMessageWithCxf error",
    					e,
    					ESDKErrorCodeConstant.ERROR_CODE_DEVICE_SERVICE_EXCEPTION);
    		}
    		throw new ProtocolAdapterException("syncSendMessageWithCxf error",
    				e, ESDKErrorCodeConstant.ERROR_CODE_NETWORK_ERROR);
    	}
    	catch (NoSuchAlgorithmException e)
    	{
    		LOGGER.error("No Such Algorithm Exception", e);
    		throw new ProtocolAdapterException("syncSendMessageWithCxf error",
    				e, ESDKErrorCodeConstant.ERROR_CODE_SYS_ERROR);
    	}
    	catch (KeyManagementException e)
    	{
    		LOGGER.error("Key Management Exception", e);
    		throw new ProtocolAdapterException("syncSendMessageWithCxf error",
    				e, ESDKErrorCodeConstant.ERROR_CODE_SYS_ERROR);
    	}
    }
    
}
