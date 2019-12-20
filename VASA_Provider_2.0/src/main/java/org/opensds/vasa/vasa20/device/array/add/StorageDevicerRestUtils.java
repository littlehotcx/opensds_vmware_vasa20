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

package org.opensds.vasa.vasa20.device.array.add;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.ContentType;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.opensds.vasa.base.bean.terminal.ResBean;
import org.opensds.platform.common.config.SSLConfigManager;
import org.opensds.platform.common.constants.ESDKConstant;
import org.opensds.platform.common.constants.ESDKErrorCodeConstant;
import org.opensds.platform.common.utils.JsonUtils;
import org.opensds.platform.common.utils.PathUtils;
import org.opensds.platform.common.utils.StringUtils;
import org.opensds.platform.exception.ProtocolAdapterException;
import org.opensds.vasa.vasa.util.RestRequestMessage;
import org.opensds.vasa.vasa.util.VASAResponseCode;


public class StorageDevicerRestUtils {
    private static final Logger LOGGER = LogManager.getLogger(StorageDevicerRestUtils.class);
    private String token;
    private String ip;
    private int port;
    private String username;
    private String password;
    private HttpHost target;
    private static String scheme = "https";
    private BasicHttpContext localContext;
    private int serverNounceCount;
    private String deviceId;
    private static final Gson GSON = new Gson();

    public String getToken() {
        return token;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        if (null != ip && !isIPV4(ip)) {
            ip = "[" + ip + "]";
        }
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    private static ClientConnectionManager conMgr;
    private static DefaultHttpClient httpClient;

    public StorageDevicerRestUtils() {
        conMgr = new PoolingClientConnectionManager();
        httpClient = new DefaultHttpClient(conMgr);
    }

    public ResBean init_conn(String username, String password, String ip, int port) {
        setUsername(username);
        setPassword(password);
        setIp(ip);
        setPort(port);
        ResBean resBean = new ResBean();
        try {
            RestRequestMessage request = new RestRequestMessage();
            request.setHttpMethod("POST");
            StorageDeviceAuthBean reqBody = new StorageDeviceAuthBean();
            reqBody.setUsername(username);
            reqBody.setPassword(password);
            reqBody.setScope("0");
            request.setPayload(reqBody);

            resBean = (ResBean) this.sendMessage(request, "/deviceManager/rest/xxxxx/sessions", "org.opensds.vasa.vasa20.device.array.add.StorageResponseData");
            StorageResponseData userAuthRes = (StorageResponseData) resBean.getResData();
            if (0 == resBean.getErrorCode()) {
                deviceId = userAuthRes.getDeviceid();
                setToken(userAuthRes.getiBaseToken());
            }
            return resBean;
        } catch (Exception e) {
            // TODO: handle exception
            resBean.setErrorCode(-1);
            resBean.setDescription(e.getMessage());
            LOGGER.error("login in array error.ip = " + ip);
        }
        return resBean;
    }


    public ResBean getSysInfo() {
        ResBean resBean = new ResBean();
        try {
            LOGGER.info("Get getSysInfo");
            RestRequestMessage request = new RestRequestMessage();
            request.setHttpMethod(ESDKConstant.HTTP_METHOD_GET);

            resBean = (ResBean) this.sendMessage(request, "/deviceManager/rest/" + getDeviceId() + "/system/", "org.opensds.vasa.vasa20.device.array.add.SystemResponseData");
            return resBean;
        } catch (Exception e) {
            // TODO: handle exception
            resBean.setErrorCode(-1);
            resBean.setDescription(e.getMessage());
            LOGGER.error("getSysInfo error deviceId = " + getDeviceId());
        }
        return resBean;
    }

    public ResBean getEthportInfo() {
        ResBean resBean = new ResBean();
        List<EthPortResponseData> result = new ArrayList<>();
        try {
            LOGGER.info("Get getEthportInfo");
            RestRequestMessage request = new RestRequestMessage();
            request.setHttpMethod(ESDKConstant.HTTP_METHOD_GET);
            resBean = (ResBean) this.sendMessage(request, "/deviceManager/rest/" + getDeviceId() + "/eth_port", null);
            if (0 == resBean.getErrorCode()) {
                JSONObject jsonObj = (JSONObject) (resBean.getResData());
                if (jsonObj.has("data")) {
                    JSONArray arrObj = jsonObj.getJSONArray("data");
                    for (int i = 0; i < arrObj.length(); ++i) {
                        Object object = arrObj.get(i);
                        EthPortResponseData ethPortResponseData = JsonUtils.fromJson(object.toString(), EthPortResponseData.class);
                        result.add(ethPortResponseData);
                    }
                }
                resBean.setResData(result);
            }
            return resBean;
        } catch (Exception e) {
            // TODO: handle exception
            resBean.setErrorCode(-1);
            resBean.setDescription(e.getMessage());
            LOGGER.error("getEthportInfo error deviceId = " + getDeviceId());
        }
        return resBean;
    }


    public ResBean getUTCTime() {
        ResBean resBean = new ResBean();
        List<EthPortResponseData> result = new ArrayList<>();
        try {
            LOGGER.info("Get getUTCTime");
            RestRequestMessage request = new RestRequestMessage();
            request.setHttpMethod(ESDKConstant.HTTP_METHOD_GET);
            resBean = (ResBean) this.sendMessage(request, "/deviceManager/rest/" + getDeviceId() + "/system_utc_time", null);
            if (0 == resBean.getErrorCode()) {
                JSONObject jsonObj = (JSONObject) (resBean.getResData());
                if (jsonObj.has("data")) {
                    JSONObject dataObject = jsonObj.getJSONObject("data");
                    String utc_time = dataObject.getString("CMO_SYS_UTC_TIME");
                    resBean.setResData(utc_time);
                }
            }
            return resBean;
        } catch (Exception e) {
            // TODO: handle exception
            resBean.setErrorCode(-1);
            resBean.setDescription(e.getMessage());
            LOGGER.error("getUTCTime error deviceId = " + getDeviceId());
        }
        return resBean;
    }

    public void logout() {
        RestRequestMessage req = new RestRequestMessage();
        req.setHttpMethod("DELETE");

        try {
            ResBean response = (ResBean) this.sendMessage(req, "/deviceManager/rest/" + getDeviceId() + "/sessions", null);
            LOGGER.info("logout result = " + response);
        } catch (Exception e) {
            LOGGER.warn("logout device manager fail. err :" + e.getMessage());
        }
        setUsername(null);
        setPassword(null);
        setIp(null);
        setPort(0);
        setToken(null);

    }

    //第一个参数是要拆分的参数，第二个参数是要要组合出来的类名
    private Object postBuildRes(Object resMessage, String resObjClass) throws ProtocolAdapterException {
        Object resObj = null;
        Gson gson = new Gson();
        ResBean resBean = new ResBean();

        try {
            JSONObject resJson = new JSONObject(String.valueOf(resMessage));

            JSONObject errorObj = resJson.getJSONObject("error");

            if (errorObj.getLong("code") != 0L) {
                resBean.setErrorCode(errorObj.getLong("code"));
                resBean.setDescription(errorObj.getString("description"));
                resBean.setErrData(errorObj);
            } else {
                resBean.setErrorCode(0);
                resBean.setDescription("0");
                if (null != resObjClass) {
/*					if(!resJson.has("data")){
						resJson.put("data", "");
					}*/
                    if (resJson.has("data")) {
                        JSONObject dataObj = resJson.getJSONObject("data");
                        resObj = gson.fromJson(dataObj.toString(), Class.forName(resObjClass));
                        resBean.setResData(resObj);
                    }
                } else {
                    resBean.setResData(resJson);
                }
            }
        } catch (JsonSyntaxException e) {
            LOGGER.error("", e);
            throw new ProtocolAdapterException("syncSendMessage error", e, ESDKErrorCodeConstant.ERROR_CODE_SYS_ERROR);
        } catch (JSONException e) {
            LOGGER.error("", e);
            throw new ProtocolAdapterException("syncSendMessage error", e, ESDKErrorCodeConstant.ERROR_CODE_SYS_ERROR);
        } catch (ClassNotFoundException e) {
            LOGGER.error("", e);
            throw new ProtocolAdapterException("syncSendMessage error", e, ESDKErrorCodeConstant.ERROR_CODE_SYS_ERROR);
        }
        return resBean;

    }

    public Object sendMessage(RestRequestMessage message, String resourcePath, String resObjClass) throws URISyntaxException {
        return sendMessageCore(message, resourcePath, resObjClass, 1);
    }


    /**
     * @param message
     * @param resourcePath
     * @param resObjClass
     * @param retryCount   当发送请求返回401授权失败后，重新获取token后，再次尝试发送请求的次数
     * @return
     * @throws ClientProtocolException
     * @throws URISyntaxException
     * @throws IOException
     */
    public Object sendMessageCore(RestRequestMessage message, String resourcePath, String resObjClass, int retryCount)
            throws URISyntaxException {
        adapterScheme(resourcePath);

        buildBasicHttpContext();
        LOGGER.info("The request URL is :" + resourcePath);
        HttpRequestBase request = buildRequestMessage(message, resourcePath);

        httpClient.getCredentialsProvider().setCredentials(new AuthScope(target.getHostName(), target.getPort()),
                new UsernamePasswordCredentials("", ""));

        HttpResponse response = null;

        try {
            /**
             * 问题单号：DTS2016042803673  时间2016.05.21  Start
             * 【VASA-ALL-可靠性: eSDK Storage V100R005C60B023】DJ的rabbitmq进程挂死后，
             *  在esdk管理平台接入阵列，2个小时页面都不跳转，既不提示成功也不提示失败
             * 	Modified By wwx315527
             */
//        	httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 60000);
//        	httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 60000);
            /**
             * 问题单号：DTS2016042803673  时间2016.05.21  End
             */
            response = httpClient.execute(target, request, localContext);


            if (response != null) {
                if (String.valueOf(response.getStatusLine().getStatusCode()).startsWith("2")) {
                    HttpEntity entity = response.getEntity();
                    String responsePayload = EntityUtils.toString(entity);
                    //LOGGER.info("The real responsePayload is:"+responsePayload);
                    return postBuildRes(responsePayload, resObjClass);
                } else {
                    HttpEntity entity = response.getEntity();
                    String responsePayload = EntityUtils.toString(entity);

                    ResBean resBean = new ResBean();
                    resBean.setErrorCode(response.getStatusLine().getStatusCode());
                    resBean.setDescription(responsePayload);

                    if ("401".equals(String.valueOf(response.getStatusLine().getStatusCode()))) {
                        if (retryCount > 0) {
                            LOGGER.warn("DJ return 401,try more times:" + retryCount);
                            retryCount--;
                            getToken();

                            return sendMessageCore(message, resourcePath, resObjClass, retryCount);
                        }
                    }

                    return resBean;
                }
            }

            ResBean resBean = new ResBean();
            resBean.setErrorCode(-1);
            resBean.setDescription("unknown");
            return resBean;

        } catch (SSLHandshakeException e) {
            LOGGER.error("create SSL handshake exception.", e);
            ResBean resBean = new ResBean();
            resBean.setErrorCode(Long.valueOf(VASAResponseCode.storageManagerService.STORAGE_ARRAY_SSL_ERROR));
            resBean.setDescription(VASAResponseCode.storageManagerService.STORAGE_ARRAY_SSL_ERROR_DESCRIPTION);
            return resBean;
        } catch (SSLException e) {
            // TODO: handle exception
            LOGGER.error("create SSL exception.", e);
            ResBean resBean = new ResBean();
            resBean.setErrorCode(Long.valueOf(VASAResponseCode.storageManagerService.STORAGE_ARRAY_SSL_ERROR));
            resBean.setDescription(VASAResponseCode.storageManagerService.STORAGE_ARRAY_SSL_ERROR_DESCRIPTION);
            return resBean;
        } catch (Exception e) {
            LOGGER.error("httpclient error", e);
            ResBean resBean = new ResBean();
            resBean.setErrorCode(-1);
            resBean.setDescription(e.getMessage());
            return resBean;
        } finally {
            if (null != request) {
                request.releaseConnection();
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


    private void adapterScheme(String resourcePath) {

        target = buildHttpHost(resourcePath);

        if (ESDKConstant.PROTOCOL_ADAPTER_TYPE_HTTPS.equalsIgnoreCase(scheme)) {
            try {
                SSLContext ctx = SSLConfigManager.createSSLContext();
                SSLSocketFactory sslSocketFactory =
                        new SSLSocketFactory(ctx, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
                SchemeRegistry registry = conMgr.getSchemeRegistry();
                registry.register(new Scheme(scheme, port, sslSocketFactory));
            } catch (Exception ex) {
                LOGGER.error("https error", ex);
            }
        }
        setTimeout(resourcePath);
    }

    private HttpHost buildHttpHost(String resourcePath) {
        if (StringUtils.isEmpty(this.ip)) {
            return null;
        }
        return new HttpHost(this.ip, this.port, this.scheme);
    }

    private String getServerURL(String resourcePath) {
        LOGGER.info("The real URL is : " + this.target + resourcePath);
        return this.target + resourcePath;
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

    private HttpRequestBase buildRequestMessage(RestRequestMessage message, String reourecePath) throws URISyntaxException {
        /**
         * codeDEX[Server-Side Request Forgery] start
         */
        String requestUrl = PathUtils.UrlPathFormat(getServerURL(reourecePath));
        /**
         * codeDEX[Server-Side Request Forgery] end
         */

        HttpRequestBase request;

        if (ESDKConstant.HTTP_METHOD_GET.equalsIgnoreCase(message.getHttpMethod())) {
            HttpGet httpGet = new HttpGet(requestUrl);
            setParameters(httpGet, message.getParameters());
            request = httpGet;
        } else if (ESDKConstant.HTTP_METHOD_POST.equalsIgnoreCase(message.getHttpMethod())) {
            HttpPost httpPost = new HttpPost(requestUrl);
            httpPost.setEntity(new StringEntity(rebuildPayLoad(message.getPayload()), ContentType.APPLICATION_JSON));
            request = httpPost;
        } else if (ESDKConstant.HTTP_METHOD_PUT.equalsIgnoreCase(message.getHttpMethod())) {
            HttpPut httpPut = new HttpPut(requestUrl);
            httpPut.setEntity(new StringEntity(rebuildPayLoad(message.getPayload()), ContentType.APPLICATION_JSON));
            request = httpPut;
        } else if (ESDKConstant.HTTP_METHOD_DELETE.equalsIgnoreCase(message.getHttpMethod())) {
            HttpDelete httpDelete = new HttpDelete(requestUrl);
            setParameters(httpDelete, message.getParameters());
            request = httpDelete;
        } else if ("PATCH".equalsIgnoreCase(message.getHttpMethod())) {
            HttpPatch httpPatch = new HttpPatch(requestUrl);
            httpPatch.setEntity(new StringEntity(rebuildPayLoad(message.getPayload()), ContentType.APPLICATION_JSON));
            request = httpPatch;
        } else {
            String msg = message.getHttpMethod() + " is not a valid HTTP method";
            LOGGER.error(msg);
            throw new IllegalArgumentException(msg);
        }

        setHttpHeaders(request, reourecePath);

        return request;
    }

    private String rebuildPayLoad(Object obj) {
        if (obj instanceof String) {
            return (String) obj;
        } else {
            return GSON.toJson(obj);
        }
    }

    private void setHttpHeaders(HttpRequestBase request, String resourcePath) {
        if (!resourcePath.equals("/deviceManager/rest/xxxxx/sessions")) {
            //鑾峰彇token鐨勬帴鍙ｄ笉闇�鍔爐oken锛岄槻姝㈣甯﹀叆鑰佺殑token瀵艰嚧DJ鎶�01閴存潈澶辫触
            if (null != token) {
                request.addHeader("iBaseToken", token);
            }
            LOGGER.info("token=" + token);
        }

        request.addHeader("Content-type", "application/json");
    }

    private void setTimeout(String serviceApiName) {
        // TODO Auto-generated method stub
        HttpParams params = httpClient.getParams();
        if (serviceApiName.equalsIgnoreCase("/deviceManager/rest/xxxxx/sessions")) {
            setHttpTimeout(params, 32000, 60000, 30000);
        } else {
            setHttpTimeout(params, 52000, 60000, 30000);
        }
    }

    private void setHttpTimeout(HttpParams params, Integer connectionTimeout, Integer soTimeout, Integer managerTimeout) {
        ConnManagerParams.setTimeout(params, managerTimeout);//该值就是连接不够用的时候等待超时时间，一定要设置，而且不能太大
        HttpConnectionParams.setSoTimeout(params, soTimeout);//设置等待数据超时时间  根据业务调整
        HttpConnectionParams.setConnectionTimeout(params, connectionTimeout);//设置请求超时 根据业务调整
    }

    private boolean isIPV4(String addr) {
        if (addr.length() < 7 || addr.length() > 15 || "".equals(addr)) {
            return false;
        }

        // 判断IP格式和范围
        String rexp = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";

        Pattern pat = Pattern.compile(rexp);
        Matcher mat = pat.matcher(addr);
        boolean ipAddress = mat.find();

        return ipAddress;
    }

}
