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

package org.opensds.vasa.vasa.util;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.DigestScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;

import org.opensds.vasa.vasa.rest.bean.ResponseHeader;
import org.opensds.vasa.vasa.rest.bean.RestLoginResponse;

public class RestUtilsOfOM {
    private static final Logger LOGGER = LogManager.getLogger(RestUtilsOfOM.class);

    private static final String REST_URL_MAPPING = "/esdkom/rest";

    private static final Gson GSON = new Gson();

    private static String scheme;

    private static String hostname;

    private static int port;

    private DefaultHttpClient httpClient;

    private BasicHttpContext localContext;

    private HttpHost target;

    private int serverNounceCount;

    private String userName;

    private String password;

    private String token;

    private static final String TOKEN_TAG = "OMToken";

    private static RestUtilsOfOM instance;

    private RestUtilsOfOM(String restURL, String userName, String password) {
        LOGGER.debug("restURL=" + restURL + ",userName=" + userName + ",password=******");
        RestUtilsOfOM.scheme = restURL.substring(0, restURL.indexOf("://"));
        RestUtilsOfOM.port = Integer.parseInt(restURL.substring(restURL.lastIndexOf(":") + 1));
        RestUtilsOfOM.hostname = restURL.substring(restURL.indexOf("://") + 3, restURL.lastIndexOf(":"));

        this.userName = userName;
        this.password = password;
    }

    public synchronized static RestUtilsOfOM getInstance(String restURL, String userName, String password) {
        if (null == instance) {
            instance = new RestUtilsOfOM(restURL, userName, password);
        }
        return instance;
    }

    public String sendMessage(RestRequestMessage message, String resourcePath) {
        HttpRequestBase request = null;
        HttpResponse response = null;

        try {
            adapterScheme();

            buildBasicHttpContext();

            request = buildRequestMessage(message, resourcePath);

            httpClient.getCredentialsProvider().setCredentials(new AuthScope(target.getHostName(), target.getPort()),
                    new UsernamePasswordCredentials(this.userName, this.password));

            response = httpClient.execute(target, request, localContext);
            LOGGER.info("The response code is : " + response.getStatusLine().getStatusCode());
            if (response != null) {
                if (200 == response.getStatusLine().getStatusCode()) {
                    HttpEntity entity = response.getEntity();
                    String responsePayload = EntityUtils.toString(entity);
                    LOGGER.info("The response body is : " + responsePayload);

                    if (responsePayload != null && responsePayload.length() > 0
                            && responsePayload.contains("resultCode")) {
                        ResponseHeader responseHeaderBean = GSON.fromJson(responsePayload,
                                ResponseHeader.class);
                        if (responseHeaderBean.getResultCode() == 12000401L) {
                            LOGGER.warn("The response resultCode is 12000401, login and try again.");
                            if (login()) {
                                return sendMessage(message, resourcePath);
                            }
                        }
                    }
                    return responsePayload;
                } else if (401 == response.getStatusLine().getStatusCode()) {
                    return "401";
                }
            }

            return null;

        } catch (Exception e) {
            LOGGER.error("httpclient error", e);
            return null;
        } finally {
            if (null != request) {
                request.releaseConnection();
            }
        }

    }

    private boolean login() {
        RestRequestMessage req = new RestRequestMessage();

        req.setHttpMethod(RestConstant.HTTP_METHOD_POST);
        Map<String, String> parameters = new HashMap<>();
        parameters.put("username", this.userName);
        parameters.put("password", this.password);
        req.setPayload(parameters);

        String response = sendMessage(req, "/tokens");

        if (response == null || response.length() == 0) {
            return false;
        }

        RestLoginResponse restLoginResponse = GSON.fromJson(response, RestLoginResponse.class);

        if (restLoginResponse.getResultCode() == 0L) {
            this.token = restLoginResponse.getData().getSessionId();
            return true;
        }

        return false;
    }

    private void adapterScheme() {
        ClientConnectionManager conMgr = new PoolingClientConnectionManager();
        httpClient = new DefaultHttpClient(conMgr);
        target = new HttpHost(hostname, port, scheme);

        if (RestConstant.HTTPS_SCHEME.equalsIgnoreCase(scheme)) {
            try {
                SSLContext ctx = SSLContext.getInstance(RestConstant.SSL_SECURE_SOCKET_PROTOCOL);
                X509TrustManager tm = new X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return new java.security.cert.X509Certificate[]{};
                    }

                    public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType)
                            throws java.security.cert.CertificateException {
                    }

                    public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType)
                            throws java.security.cert.CertificateException {
                    }

                };
                ctx.init(null, new TrustManager[]{tm}, null);
                SSLSocketFactory sslSocketFactory = new SSLSocketFactory(ctx,
                        SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
                SchemeRegistry registry = conMgr.getSchemeRegistry();
                registry.register(new Scheme(scheme, port, sslSocketFactory));
            } catch (Exception ex) {
                LOGGER.error("https error", ex);
            }
        }
    }

    private void buildBasicHttpContext() {
        AuthCache authCache = new BasicAuthCache();
        DigestScheme digestScheme = new DigestScheme();
        digestScheme.overrideParamter("nc", String.valueOf(serverNounceCount++));
        digestScheme.overrideParamter("cnonce", UUID.randomUUID().toString().replaceAll("-", ""));
        digestScheme.overrideParamter("qop", "auth");
        // DTS2016121407363 Redundant error message start
        digestScheme.overrideParamter("realm", "My realm");
        digestScheme.overrideParamter("nonce", "whatever");
        // DTS2016121407363 Redundant error message end

        authCache.put(target, digestScheme);

        localContext = new BasicHttpContext();
        localContext.setAttribute(ClientContext.AUTH_CACHE, authCache);
    }

    private HttpRequestBase buildRequestMessage(RestRequestMessage message, String reourecePath)
            throws URISyntaxException, UnsupportedEncodingException {
        /**
         * codeDEX[Server-Side Request Forgery] start
         */
        String requestUrl = PathUtils.UrlPathFormat(getServerURL(reourecePath));
        /**
         * codeDEX[Server-Side Request Forgery] end
         */

        HttpRequestBase request;

        if (RestConstant.HTTP_METHOD_GET.equalsIgnoreCase(message.getHttpMethod())) {
            HttpGet httpGet = new HttpGet(requestUrl);
            setParameters(httpGet, message.getParameters());
            request = httpGet;
        } else if (RestConstant.HTTP_METHOD_POST.equalsIgnoreCase(message.getHttpMethod())) {
            HttpPost httpPost = new HttpPost(requestUrl);
            httpPost.setEntity(new StringEntity(GSON.toJson(message.getPayload()), ContentType.APPLICATION_JSON));
            request = httpPost;
        } else if (RestConstant.HTTP_METHOD_PUT.equalsIgnoreCase(message.getHttpMethod())) {
            HttpPut httpPut = new HttpPut(requestUrl);
            httpPut.setEntity(new StringEntity(GSON.toJson(message.getPayload()), ContentType.APPLICATION_JSON));
            request = httpPut;
        } else if (RestConstant.HTTP_METHOD_DELETE.equalsIgnoreCase(message.getHttpMethod())) {
            HttpDelete httpDelete = new HttpDelete(requestUrl);
            setParameters(httpDelete, message.getParameters());
            request = httpDelete;
        } else {
            String msg = message.getHttpMethod() + " is not a valid HTTP method";
            LOGGER.error(msg);
            throw new IllegalArgumentException(msg);
        }

        addHttpHeader(request);

        return request;
    }

    private void addHttpHeader(HttpRequestBase httpRequestBase) {
        if (token != null && token.length() > 0) {
            httpRequestBase.addHeader(TOKEN_TAG, this.token);
        }
    }

    private void setParameters(HttpRequestBase httpRequest, Map<String, String> parameters) throws URISyntaxException {
        if (!parameters.isEmpty()) {
            URIBuilder uriBuilder = new URIBuilder(httpRequest.getURI());

            for (Map.Entry<String, String> entry : parameters.entrySet()) {
                uriBuilder.addParameter(entry.getKey(), entry.getValue());
            }

            httpRequest.setURI(uriBuilder.build());
        }
    }

    private String getServerURL(String resourcePath) {
        return REST_URL_MAPPING + resourcePath;
    }
}
