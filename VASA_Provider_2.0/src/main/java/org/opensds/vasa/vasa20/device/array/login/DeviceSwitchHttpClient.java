/*
 * // Copyright 2019 The OpenSDS Authors.
 * //
 * // Licensed under the Apache License, Version 2.0 (the "License"); you may
 * // not use this file except in compliance with the License. You may obtain
 * // a copy of the License at
 * //
 * //     http://www.apache.org/licenses/LICENSE-2.0
 * //
 * // Unless required by applicable law or agreed to in writing, software
 * // distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * // WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * // License for the specific language governing permissions and limitations
 * // under the License.
 *
 */

package org.opensds.vasa.vasa20.device.array.login;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.StatusLine;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.DigestScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;

import org.opensds.platform.common.ThreadLocalHolder;
import org.opensds.platform.common.bean.aa.AccountInfo;
import org.opensds.platform.common.bean.commu.RestReqMessage;
import org.opensds.platform.common.config.SSLConfigManager;
import org.opensds.platform.common.constants.ESDKConstant;
import org.opensds.platform.common.constants.ESDKErrorCodeConstant;
import org.opensds.platform.common.utils.StringUtils;
import org.opensds.platform.commu.itf.ISDKProtocolAdapter;
import org.opensds.platform.commu.itf.ISDKProtocolAdatperCustProvider;
import org.opensds.platform.exception.ProtocolAdapterException;

public class DeviceSwitchHttpClient implements ISDKProtocolAdapter {

    private static Logger LOGGER = LogManager.getLogger(DeviceSwitchHttpClient.class);

    private String serverUrl;

    private int serverNounceCount;

    private ISDKProtocolAdatperCustProvider sdkProtocolAdatperCustProvider;

    private HttpHost target;

    private BasicHttpContext localContext;

    private DefaultHttpClient httpClient;

    private String user;

    private String pwd;

    public DeviceSwitchHttpClient(String serverUrl) {

        this.serverUrl = serverUrl;
        // ClientConnectionManager conMgr = new
        // PoolingClientConnectionManager();
        // httpClient = new DefaultHttpClient(conMgr);
        PoolingClientConnectionManager conMgr = new PoolingClientConnectionManager();
        conMgr.setDefaultMaxPerRoute(200); // 每个主机的最大并行链接数
        conMgr.setMaxTotal(800);

        httpClient = new DefaultHttpClient(conMgr);
        HttpParams params = httpClient.getParams();
        HttpConnectionParams.setSoTimeout(params, 60000);
        HttpConnectionParams.setConnectionTimeout(params, 60000);
        // httpClient.getParams().setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,
        // 60000);
        // httpClient.getParams().setIntParameter(CoreConnectionPNames.SO_TIMEOUT,
        // 60000);

        target = buildHttpHost();

        if (serverUrl.startsWith(ESDKConstant.PROTOCOL_ADAPTER_TYPE_HTTPS)) {
            try {
                SSLContext ctx = SSLConfigManager.createSSLContext();
                SSLSocketFactory ssf = new SSLSocketFactory(ctx, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
                SchemeRegistry registry = conMgr.getSchemeRegistry();
                registry.register(
                        new Scheme(ESDKConstant.PROTOCOL_ADAPTER_TYPE_HTTPS,
                                Integer.parseInt(
                                        serverUrl.substring(serverUrl.lastIndexOf(":") + 1, serverUrl.indexOf("/", 8))),
                                ssf));
            } catch (KeyManagementException e) {
                LOGGER.error("https error", e);
            } catch (NoSuchAlgorithmException e) {
                LOGGER.error("https error", e);
            }
        }

    }

    private HttpHost buildHttpHost() {
        if (StringUtils.isEmpty(serverUrl)) {
            return null;
        }

        HttpHost result;

        String scheme = serverUrl.substring(0, serverUrl.indexOf("://"));
        String server = serverUrl.substring(serverUrl.indexOf("://") + 3, serverUrl.indexOf("/", scheme.length() + 3));
        if (server.contains(":")) {
            /**
             * FindBugs:DM_BOXED_PRIMITIVE_FOR_PARSING start Modified by
             * wWX315527 2017/02/06
             */
            //result = new HttpHost(server.split(":")[0], Integer.parseInt(server.split(":")[1]), scheme);
            result = new HttpHost(server.substring(0, server.lastIndexOf(":")), Integer.parseInt(server.substring(server.lastIndexOf(":") + 1)), scheme);
            /**
             * FindBugs:DM_BOXED_PRIMITIVE_FOR_PARSING start Modified by
             * wWX315527 2017/02/06
             */
        } else {
            result = new HttpHost(server, 80, scheme);
        }

        return result;
    }

    @Override
    public String getServiceAccessPoint() {

        return this.serverUrl;
    }

    private synchronized void checkLocalContext() {
        if (null != sdkProtocolAdatperCustProvider && null != target) {
            // Create AuthCache instance
            AuthCache authCache = new BasicAuthCache();
            // Generate DIGEST scheme object, initialize it and add it to the
            // local auth cache
            String authType = (String) ThreadLocalHolder.get().getEntities().get("AuthType");
            if ("Basic".equals(authType)) {
                LOGGER.debug("authentication type: basic");
            } else {
                DigestScheme digestAuth = new DigestScheme();
                digestAuth.overrideParamter("nc", String.valueOf(serverNounceCount++));
                digestAuth.overrideParamter("cnonce", UUID.randomUUID().toString().replaceAll("-", ""));
                digestAuth.overrideParamter("qop", "auth");
                authCache.put(target, digestAuth);
            }

            // Add AuthCache to the execution context
            localContext = new BasicHttpContext();
            localContext.setAttribute(ClientContext.AUTH_CACHE, authCache);
        }
    }

    private void preSend(RestReqMessage restReq) {
        if (null != sdkProtocolAdatperCustProvider) {
            AccountInfo accountInfo = sdkProtocolAdatperCustProvider.getProtocolAuthInfo();
            if (null != accountInfo && null != accountInfo.getUserId()) {
                if (!StringUtils.strsEquals(accountInfo.getUserId(), user)
                        || !StringUtils.strsEquals(accountInfo.getPassword(), pwd)) {
                    httpClient.getCredentialsProvider().setCredentials(
                            new AuthScope(target.getHostName(), target.getPort()),
                            new UsernamePasswordCredentials(accountInfo.getUserId(), accountInfo.getPassword()));
                    this.user = accountInfo.getUserId();
                    this.pwd = accountInfo.getPassword();
                }
            }

            sdkProtocolAdatperCustProvider.preProcessReq(restReq);
            sdkProtocolAdatperCustProvider.preSend(restReq);
        }
    }

    private HttpRequestBase buildHttpRequest(RestReqMessage restReq, String serviceApiName)
            throws URISyntaxException, UnsupportedEncodingException {
        HttpRequestBase request;
        if (ESDKConstant.HTTP_METHOD_GET.equalsIgnoreCase(restReq.getHttpMethod())) {
            HttpGet httpGet = new HttpGet(getURL(serviceApiName));
            setParameters(httpGet, restReq.getParameters());
            request = httpGet;
        } else if (ESDKConstant.HTTP_METHOD_POST.equalsIgnoreCase(restReq.getHttpMethod())) {
            HttpPost httpPost = new HttpPost(getURL(serviceApiName));
            setParameters(httpPost, restReq.getParameters());
            httpPost.setEntity(new StringEntity(getPayloadAsString(restReq), "UTF-8"));
            request = httpPost;
        } else if (ESDKConstant.HTTP_METHOD_PUT.equalsIgnoreCase(restReq.getHttpMethod())) {
            HttpPut httpPut = new HttpPut(getURL(serviceApiName));
            httpPut.setEntity(new StringEntity(getPayloadAsString(restReq), "UTF-8"));
            request = httpPut;
        } else if (ESDKConstant.HTTP_METHOD_DELETE.equalsIgnoreCase(restReq.getHttpMethod())) {
            HttpDelete httpDelete = new HttpDelete(getURL(serviceApiName));
            setParameters(httpDelete, restReq.getParameters());
            request = httpDelete;
        } else {
            String msg = restReq.getHttpMethod() + " is not a valid HTTP method";
            LOGGER.error(msg);
            throw new IllegalArgumentException(msg);
        }

        setHttpHeaders(request, restReq.getHttpHeaders());

        return request;
    }

    private String getURL(String serviceApiName) {
        if (null != sdkProtocolAdatperCustProvider) {
            return sdkProtocolAdatperCustProvider.reBuildNewUrl(serverUrl, serviceApiName);
        } else {
            return serverUrl;
        }
    }

    private void setParameters(HttpRequestBase httpRequest, Map<String, String> parameters) throws URISyntaxException {
        if (null != parameters && !parameters.isEmpty()) {
            URIBuilder uriBuilder = new URIBuilder(httpRequest.getURI());

            for (Map.Entry<String, String> entry : parameters.entrySet()) {
                uriBuilder.addParameter(entry.getKey(), entry.getValue());
            }

            httpRequest.setURI(uriBuilder.build());
        }
    }

    private String getPayloadAsString(RestReqMessage restReq) {
        String mediaType = restReq.getMediaType();
        mediaType = (mediaType == null ? "" : mediaType.toLowerCase(Locale.ENGLISH));
        if (mediaType.contains("json")) {
            if (null != sdkProtocolAdatperCustProvider) {
                return sdkProtocolAdatperCustProvider.getContent4Sending(restReq.getPayload());
            } else {
                Gson gson = new Gson();
                String reqPayloadInJSON = gson.toJson(restReq.getPayload());
                return reqPayloadInJSON;
            }
        } else if (mediaType.contains("xml")) {
            if (null != sdkProtocolAdatperCustProvider) {
                return sdkProtocolAdatperCustProvider.getContent4Sending(restReq);
            } else {
                return restReq.getPayload().toString();
            }
        } else {
            return restReq.getPayload().toString();
        }
    }

    private void setHttpHeaders(HttpRequestBase request, Map<String, String> pHeaders) {
        for (Map.Entry<String, String> entry : pHeaders.entrySet()) {
            request.addHeader(entry.getKey(), entry.getValue());
        }

        Map<String, String> headers = getRequestHeaders();
        if (headers != null) {
            Set<Map.Entry<String, String>> es = headers.entrySet();
            for (Map.Entry<String, String> item : es) {
                request.addHeader(item.getKey(), item.getValue());
            }
        }
    }

    protected Map<String, String> getRequestHeaders() {
        // For override by derived class
        if (null != sdkProtocolAdatperCustProvider) {
            return sdkProtocolAdatperCustProvider.getRequestHeaders();
        }
        return null;
    }

    private Object postSend(HttpResponse response, String resObjClass)
            throws ProtocolAdapterException, ParseException, IOException {
        HttpEntity entity = response.getEntity();
        String responsePayload = EntityUtils.toString(entity);

        if (null != sdkProtocolAdatperCustProvider) {
            sdkProtocolAdatperCustProvider.postSend(responsePayload);
            return sdkProtocolAdatperCustProvider.postBuildRes(responsePayload, resObjClass);
        } else {
            // Process the response body
            LOGGER.debug("The response content is:" + response);
            return responsePayload;
        }
    }

    @Override
    public Object syncSendMessage(Object reqMessage, String serviceApiName, String responseClass)
            throws ProtocolAdapterException {
        if (!(reqMessage instanceof RestReqMessage)) {
            throw new IllegalArgumentException("reqMessage is not a instance of RestReqMessage");
        }
        RestReqMessage restReq = (RestReqMessage) reqMessage;

        checkLocalContext();

        HttpRequestBase request = null;
        try {
            preSend(restReq);

            request = buildHttpRequest(restReq, serviceApiName);
            // Send the http request
            HttpResponse response = httpClient.execute(target, request, localContext);
            StatusLine respStatusLine = response.getStatusLine();
            ThreadLocalHolder.get().getEntities().put("HTTP_RES_CODE", String.valueOf(respStatusLine.getStatusCode()));
            // LOGGER.info("-----------------------------StatusLine:" +
            // respStatusLine);

            if (!String.valueOf(response.getStatusLine().getStatusCode()).startsWith("2")) {
                LOGGER.error("HTTP status code is " + response.getStatusLine().getStatusCode());
                LOGGER.error("HTTP response body: " + EntityUtils.toString(response.getEntity()));
                throw new ProtocolAdapterException("Status code is not 200", response.getStatusLine().getStatusCode());
            }
            // Process the response header
            return postSend(response, responseClass);
        } catch (ProtocolAdapterException ex) {
//			 LOGGER.error("", ex);
            throw ex;
        } catch (ClientProtocolException e) {
            LOGGER.error("", e);
            throw new ProtocolAdapterException("", ESDKErrorCodeConstant.ERROR_CODE_SYS_ERROR);
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("", e);
            throw new ProtocolAdapterException("", ESDKErrorCodeConstant.ERROR_CODE_SYS_ERROR);
        } catch (IOException e) {
            LOGGER.error("", e);
            throw new ProtocolAdapterException("", ESDKErrorCodeConstant.ERROR_CODE_SYS_ERROR);
        } catch (URISyntaxException e) {
            LOGGER.error("", e);
            throw new ProtocolAdapterException("", ESDKErrorCodeConstant.ERROR_CODE_SYS_ERROR);
        } finally {
            if (null != request) {
                request.releaseConnection();
            }
        }

    }

    @Override
    public String syncSendMessage(String s, String s1) throws ProtocolAdapterException {
        throw new UnsupportedOperationException(
                "syncSendMessage(String messageContent, String serviceApiName) is not supported");
    }

    @Override
    public boolean heartBeat() throws ProtocolAdapterException {
        return false;
    }

    @Override
    public int login(String s, String s1) throws ProtocolAdapterException {
        return 0;
    }

    @Override
    public int logout() throws ProtocolAdapterException {
        return 0;
    }

    @Override
    public ISDKProtocolAdatperCustProvider getSdkProtocolAdatperCustProvider() {

        return this.sdkProtocolAdatperCustProvider;
    }

    @Override
    public void setSdkProtocolAdatperCustProvider(ISDKProtocolAdatperCustProvider isdkprotocoladatpercustprovider) {

        this.sdkProtocolAdatperCustProvider = isdkprotocoladatpercustprovider;
    }

}
