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

package org.opensds.vasa.vasa20.device;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.net.ssl.SSLContext;

import org.apache.http.Header;
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
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.DigestScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensds.vasa.vasa20.device.array.bean.DeviceUserAuthReqBean;
import org.opensds.vasa.vasa20.device.array.bean.DeviceUserAuthResBean;
import org.opensds.vasa.vasa20.device.array.login.DeviceSwitchService;

import com.google.gson.Gson;

import org.opensds.vasa.base.bean.terminal.ResBean;
import org.opensds.vasa.base.common.VASAArrayUtil;
import org.opensds.vasa.base.common.VasaConstant;
import org.opensds.vasa.domain.model.StorageModel;
import org.opensds.vasa.domain.model.bean.DArrayFlowControl;
import org.opensds.vasa.domain.model.bean.DArrayIsLock;
import org.opensds.vasa.domain.model.bean.S2DArray;
import org.opensds.platform.common.MessageContext;
import org.opensds.platform.common.SDKResult;
import org.opensds.platform.common.ThreadLocalHolder;
import org.opensds.platform.common.bean.aa.AccountInfo;
import org.opensds.platform.common.bean.commu.RestReqMessage;
import org.opensds.platform.common.bean.log.InterfaceLogBean;
import org.opensds.platform.common.config.SSLConfigManager;
import org.opensds.platform.common.constants.ESDKConstant;
import org.opensds.platform.common.constants.ESDKErrorCodeConstant;
import org.opensds.platform.common.exception.SDKException;
import org.opensds.platform.common.utils.ApplicationContextUtil;
import org.opensds.platform.common.utils.StringUtils;
import org.opensds.platform.commu.itf.ISDKProtocolAdapter;
import org.opensds.platform.commu.itf.ISDKProtocolAdatperCustProvider;
import org.opensds.platform.exception.ProtocolAdapterException;
import org.opensds.platform.log.itf.IInterfaceLog;
import org.opensds.vasa.vasa.db.model.StorageInfo;
import org.opensds.vasa.vasa.rest.bean.QueryDBResponse;
import org.opensds.vasa.vasa.rest.resource.StorageManagerResource;
import org.opensds.vasa.vasa.service.DiscoverService;
import org.opensds.vasa.vasa.util.DataUtil;
import org.opensds.vasa.vasa.util.FaultUtil;

public class RestfulAdapterImplForVasa implements ISDKProtocolAdapter {
    private static Logger LOGGER = LogManager.getLogger(RestfulAdapterImplForVasa.class);

    private int serverNounceCount;

    private ISDKProtocolAdatperCustProvider sdkProtocolAdatperCustProvider;

    private String serverUrl;

    // volatile 保证每次登录后 其他线程使用最新的session，避免线程副本导致session与token不一致
    private volatile DefaultHttpClient httpClient;

    private String user;

    private String pwd;

    private HttpHost target;

    volatile boolean switchControllerIsRunning = false;

    private static Object switch_control_lock = new Object();

    long switchControllerFinishMils = 0;

    private ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock(true);

    private DataUtil dataUtil = DataUtil.getInstance();

    private DiscoverService discoverService = DiscoverService.getInstance();

    // private BasicHttpContext localContext;
    // 添加登录间隔时间 当前是 2s间隔
    private long lastLoginTime = 0;

    public synchronized void rebuildRestfulAdapter(String avilableServerUrl) {
        this.serverUrl = avilableServerUrl;
        PoolingClientConnectionManager conMgr = new PoolingClientConnectionManager();
        conMgr.setDefaultMaxPerRoute(200); // 每个主机的最大并行链接数
        conMgr.setMaxTotal(800);
        httpClient = new DefaultHttpClient(conMgr);
        HttpParams params = httpClient.getParams();
        setHttpTimeout(params, 52000, 60000, 30000);

        LOGGER.info("rebuild vasa httpclient for device manager . serverUrl is: " + serverUrl);

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

    public RestfulAdapterImplForVasa(String serverUrl) {
        this.serverUrl = serverUrl;
        // ClientConnectionManager conMgr = new
        // PoolingClientConnectionManager();
        // httpClient = new DefaultHttpClient(conMgr);
        PoolingClientConnectionManager conMgr = new PoolingClientConnectionManager();
        conMgr.setDefaultMaxPerRoute(200); // 每个主机的最大并行链接数
        conMgr.setMaxTotal(800);

        httpClient = new DefaultHttpClient(conMgr);
        HttpParams params = httpClient.getParams();
        setHttpTimeout(params, 52000, 60000, 30000);
        // httpClient.getParams().setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,
        // 60000);
        // httpClient.getParams().setIntParameter(CoreConnectionPNames.SO_TIMEOUT,
        // 60000);

        /*
         * ConnectionSocketFactory plainsf = PlainConnectionSocketFactory
         * .getSocketFactory(); LayeredConnectionSocketFactory sslsf =
         * SSLConnectionSocketFactory .getSocketFactory();
         * Registry<ConnectionSocketFactory> registry1 = RegistryBuilder
         * .<ConnectionSocketFactory> create().register("http", plainsf)
         * .register("https", sslsf).build();
         *
         * PoolingHttpClientConnectionManager cm = new
         * PoolingHttpClientConnectionManager( registry1);
         *
         * // 将最大连接数增加 cm.setMaxTotal(800); // 将每个路由基础的连接增加
         * cm.setDefaultMaxPerRoute(200);
         */

        LOGGER.info("Construct thread from VASA to DeviceManager connectionPool. serverUrl is: " + serverUrl);

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
            //result = new HttpHost(server.split(":")[0], Integer.valueOf(server.split(":")[1]), scheme);
            //支持获取ipv4和ipv6类型的ip
            result = new HttpHost(server.substring(0, server.lastIndexOf(":")), Integer.parseInt(server.substring(server.lastIndexOf(":") + 1)), scheme);
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
            BasicHttpContext localContext = new BasicHttpContext();
            localContext.setAttribute(ClientContext.AUTH_CACHE, authCache);

            /**
             * 修改CodeCC问题：Category：Concurrent data access violations
             * Type：Unguarded read Start 将localcontext放入线程变量
             */
            ThreadLocalHolder.get().getEntities().put("localContext", localContext);
            /**
             * 修改CodeCC问题：Category：Concurrent data access violations
             * Type：Unguarded read End
             */
        }
    }

    public Object syncSendMessageCore(Object reqMessage, String serviceApiName, String resObjClass)
            throws ProtocolAdapterException {

        VASARestReqMessage restReq = (VASARestReqMessage) reqMessage;
        checkLocalContext();

        HttpRequestBase request = null;

        long errCode = -1;
        String errMessage = "syncSendMessage retry timeout";

        //Boolean returnVasaProviderBusy = false;

        try {
            preSend(restReq);

            request = buildHttpRequest(restReq, serviceApiName);

            String devId = getDevId();

            /**
             * 修改CodeCC问题：Category：Concurrent data access violations
             * Type：Unguarded read Start
             */
            BasicHttpContext localContext = (BasicHttpContext) ThreadLocalHolder.get().getEntities()
                    .get("localContext");
            /**
             * 修改CodeCC问题：Category：Concurrent data access violations
             * Type：Unguarded read End
             */

            DArrayFlowControl arrayFlowControl = null;
            Semaphore semaphore = null;
            RestQosControl restQosControl = null;

            if (null != devId) {
                arrayFlowControl = dataUtil.getArrayFlowControlByDevId(devId);
                if (arrayFlowControl == null) {
                    discoverService.addFlowControlDevice(devId, restReq.getArrayId());
                    arrayFlowControl = dataUtil.getArrayFlowControlByDevId(devId);
                }
                semaphore = arrayFlowControl.getSemaphore();
                restQosControl = arrayFlowControl.getRestQosControl();
            }

            int retryTimes = 0;

            while (retryTimes <= 12) {
                //returnVasaProviderBusy = false;
                HttpResponse response = null;
                boolean readLock = false;
                if (!readWriteLock.isWriteLockedByCurrentThread()) {
                    readWriteLock.readLock().lock();
                    readLock = true;
                }
                try {
                    if (null == devId) {
                        setHttpHeaders(request, restReq.getHttpHeaders(), serviceApiName);
                        setTimeout(serviceApiName);
                        LOGGER.debug("request url = " + request.getURI());
                        response = httpClient.execute(target, request, localContext);
                    } else {
                        LOGGER.debug("the semaphore intavailable permits=" + semaphore.availablePermits() + " wait permits length = " + semaphore.getQueueLength());
                        if (semaphore.tryAcquire(5, TimeUnit.SECONDS)) {
                            try {
                                if (restQosControl.isPass()) {
                                    setHttpHeaders(request, restReq.getHttpHeaders(), serviceApiName);
                                    setTimeout(serviceApiName);
                                    LOGGER.debug("request url = " + request.getURI());
                                    response = httpClient.execute(target, request, localContext);
                                } else {
                                    LOGGER.warn("The array's request Concurrency for seconds have reach max request concurrency number. retryTimes=" + retryTimes);
                                    //returnVasaProviderBusy = true;
                                    if (retryTimes == 12) {
                                        LOGGER.error("Throw error,The array's request Concurrency for seconds have reach max request concurrency number. retryTimes=" + retryTimes);
                                        FaultUtil.vasaProviderBusy("The array's request Concurrency for seconds have reach max request concurrency number");
                                    }
                                    readLock = releaseReadLock(readLock);
                                    Thread.sleep(5000);
                                    retryTimes++;
                                    continue;
                                }

                            } catch (Exception e) {
                                LOGGER.error("send request error. ", e);
                                throw e;
                            } finally {
                                semaphore.release();
                            }
                        } else {
                            LOGGER.warn("The array's request Concurrency has reach max request concurrency number, retryTimes=" + retryTimes);
                            //returnVasaProviderBusy = true;
                            if (retryTimes == 12) {
                                LOGGER.error("Throw error,The array's request Concurrency has reach max request concurrency number, retryTimes=" + retryTimes);
                                FaultUtil.vasaProviderBusy("The array's request Concurrency has reach max request concurrency number");
                            }
                            readLock = releaseReadLock(readLock);
                            Thread.sleep(5000);
                            retryTimes++;
                            continue;
                        }
                    }
                } catch (Exception exception) {
                    LOGGER.info("begining switchControl ,exception=", exception);
                    if (!checkArrayIsNull(restReq) && checkSwitchCount(restReq)) {
                        readLock = releaseReadLock(readLock);
                        return switchControl(reqMessage, serviceApiName, resObjClass, restReq);
                    }
                } finally {
                    readLock = releaseReadLock(readLock);
                }

                if (null != response) {
                    StatusLine respStatusLine = response.getStatusLine();
                    ThreadLocalHolder.get().getEntities().put("HTTP_RES_CODE",
                            String.valueOf(respStatusLine.getStatusCode()));

                    if (!String.valueOf(response.getStatusLine().getStatusCode()).startsWith("2")) {
                        String responseBody = EntityUtils.toString(response.getEntity());
                        // 资源不存在，则不重试
                        if (404 == response.getStatusLine().getStatusCode()) {
                            LOGGER.warn("HTTP status code is " + response.getStatusLine().getStatusCode());
                            LOGGER.warn("HTTP response body: " + responseBody);
                            errCode = 404;
                            errMessage = responseBody;
                        }
                        // 内部错误，判断阵列是否离线，如果阵列离线，再次重试，直到尝试12次为止
                        else if (500 == response.getStatusLine().getStatusCode()) {
                            LOGGER.warn("retryTimes:" + retryTimes + ", HTTP response body: " + responseBody);
                            errCode = 500;
                            errMessage = responseBody;
                            // 如果阵列已离线，则不再continue，直接失败退出，不处于离线状态才continue。
                            if (checkArrayOnline(restReq.getArrayId())) {
                                Thread.sleep(5000);
                                retryTimes++;
                                continue;
                            }
                        }
                        //系统繁忙，一般是阵列处理请求过多，重试
                        else if (503 == response.getStatusLine().getStatusCode()) {
                            LOGGER.warn("retryTimes:" + retryTimes + ", HTTP response body: " + responseBody);
                            errCode = 503;
                            errMessage = responseBody;
                            Thread.sleep(5000);
                            retryTimes++;
                            continue;
                            /**
                             * 问题单号：DTS2015111401544,DTS2016042609503
                             * 时间2016.05.20 End
                             */
                        } else {
                            LOGGER.error("HTTP status code is " + response.getStatusLine().getStatusCode());
                            LOGGER.error("HTTP response body: " + responseBody);
                        }

                        throw new ProtocolAdapterException("Status code is not 200",
                                response.getStatusLine().getStatusCode());
                    }

                    // Process the response header
                    ResBean resBean = (ResBean) postSend(response, resObjClass);
                    errCode = resBean.getErrorCode();
                    errMessage = resBean.getDescription();

                    // 阵列侧繁忙，再次尝试
                    if (1077949006 == resBean.getErrorCode()) {
                        /**
                         * 问题单号：DTS2016051308665 时间2016.05.20 Start 【eSDK
                         * Storage
                         * V100R005C60B036+VASA+18500V3+4h】高端18000阵列掉电再上电，VVOL
                         * DATASTORE状态正常，下电的虚拟机为不可访问状态
                         * 18000阵列掉电重启，阵列可能会返-401错误码，针对此错误码做重试
                         *
                         * DTS2016051911617 【eSDK Storage
                         * V100R005C60B037】克隆vvol的过程中注入源vvol的工作控制器异常复位，克隆终止
                         * 阵列单控复位，DJ切控完成后去阵列查询lun报系统繁忙，对系统繁忙错误码1077949006做重试
                         */
                        LOGGER.warn("retryTimes:" + retryTimes + ", errCode:" + resBean.getErrorCode()
                                + ", description:" + resBean.getDescription());
                        errCode = resBean.getErrorCode();
                        errMessage = resBean.getDescription();
                        Thread.sleep(5000);
                        retryTimes++;
                        continue;
                        /**
                         * 问题单号：DTS2016051308665 时间2016.05.20 End
                         * errCode:1077949069, description:The user is offline
                         */
                    } else if (-401 == resBean.getErrorCode() || 1077949069 == resBean.getErrorCode()) {
                        // 加锁，登陆成功后重试，更新DB状态
                        if (serviceApiName.equalsIgnoreCase("sessions")) {
                            LOGGER.info("Current rest api is login out. No need to relogin!!");
                            throw new ProtocolAdapterException("receive no auth", -401);
                        }
                        VASAJsonOverHttpCustProvider vasaJsonOverHttpCustProvider = (VASAJsonOverHttpCustProvider) this.sdkProtocolAdatperCustProvider;
                        LOGGER.warn("auth fail, login at once and try again. update deviceId null,deviceId = " + vasaJsonOverHttpCustProvider.getAccountInfo().getDevId());
                        String currentToken = VASASession.getToken(vasaJsonOverHttpCustProvider.getAccountInfo().getDevId());
                        readWriteLock.writeLock().lock();
                        try {
                            String newToken = VASASession.getToken(vasaJsonOverHttpCustProvider.getAccountInfo().getDevId());
                            if (currentToken.equals(newToken)) {
                                MessageContext mContext = ThreadLocalHolder.get();
                                if (0 == login((String) mContext.getEntities().get(ESDKConstant.ESDK_USER_ID), (String) mContext.getEntities().get(ESDKConstant.ESDK_PLAIN_PWD))) {
                                    retryTimes = 12;
                                    // 401之后，登陆成功，需要重置request，如果不重置，会携带上一次的残留token,会陷入401死循环
                                    request = buildHttpRequest(restReq, serviceApiName);
                                    continue;
                                }
                            } else {
                                LOGGER.info("Device " + devId + "already login.");
                                retryTimes = 12;
                                // 401之后，登陆成功（其他线程登陆成功），需要重置request，如果不重置，会携带上一次的残留token,会陷入401死循环
                                request = buildHttpRequest(restReq, serviceApiName);
                                continue;
                            }
                        } finally {
                            readWriteLock.writeLock().unlock();
                            //避免多线程同时发送请求
                            Thread.sleep(new Random().nextInt(2000));
                        }
                    } else if (VASAArrayUtil.SwitchControlErroCodes.contains(resBean.getErrorCode()) && !checkArrayIsNull(restReq) && checkSwitchCount(restReq)) {
                        LOGGER.info("beging switchControl ,errCode=" + resBean.getErrorCode());
                        return switchControl(reqMessage, serviceApiName, resObjClass, restReq);
                    }
                    return resBean;
                }

                throw new ProtocolAdapterException("receive null response", ESDKErrorCodeConstant.ERROR_CODE_SYS_ERROR);
            }

            throw new ProtocolAdapterException(errMessage, (int) errCode);
        } catch (ProtocolAdapterException ex) {
            // 用于记录接口日志
            errCode = ex.getErrorCode();
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
        } catch (InterruptedException e) {
            LOGGER.error("", e);
            throw new ProtocolAdapterException("", ESDKErrorCodeConstant.ERROR_CODE_SYS_ERROR);
        } catch (Exception e) {
            LOGGER.error("", e);
            throw new ProtocolAdapterException("", ESDKErrorCodeConstant.ERROR_CODE_SYS_ERROR);
        } finally {
            MessageContext mc = ThreadLocalHolder.get();
            if (null != mc) {
                String uuid = (String) mc.getEntities().get("interfaceLogger-TransactionId");
                InterfaceLogBean bean = new InterfaceLogBean();
                bean.setTransactionId(uuid);
                bean.setReq(false);
                bean.setRespTime(new Date());
                bean.setResultCode(String.valueOf(errCode));

                IInterfaceLog interfaceLogger = ApplicationContextUtil.getBean("interfaceLogger");
                interfaceLogger.info(bean);
            }

            if (null != request) {
                request.releaseConnection();
            }
            //if(returnVasaProviderBusy){
            //	LOGGER.warn("The array's request Concurrency has reach max request concurrency number, throw vasa provaer busy.");
            //	FaultUtil.vasaProviderBusy("The array's request Concurrency has reach max request concurrency number" );
            //}
        }
    }


    private boolean releaseReadLock(boolean readLock) {
        if (!readWriteLock.isWriteLockedByCurrentThread() && readLock) {
            readWriteLock.readLock().unlock();
            readLock = false;
        }
        return readLock;
    }


    private void printTokens(HttpRequestBase request) {
        // TODO Auto-generated method stub
        Header[] allHeaders = request.getAllHeaders();
        for (Header header : allHeaders) {
            LOGGER.debug("request header_name=" + header.getName() + "header_Value=" + header.getValue());
        }
    }


    private void updateCookies() {
        List<Cookie> cookies = httpClient.getCookieStore().getCookies();
        String session = VASASession.getSession(((VASAJsonOverHttpCustProvider) this.sdkProtocolAdatperCustProvider).getAccountInfo().getDevId());
        int i = 0;
        if (null != session) {
            if (null != cookies && cookies.size() != 0) {
                ListIterator<Cookie> listIterator = cookies.listIterator();
                while (listIterator.hasNext()) {
                    Cookie next = listIterator.next();
                    if (next.getName().equals("session")) {
                        LOGGER.info("req session" + i + ":" + next.getValue());
                        listIterator.remove();
                        i = i + 1;
                    }
                }

                cookies.add(new BasicClientCookie("session", session));
                LOGGER.info("-----------------------------HttpHeaders:[" +
                        "session" + ":" + session + "]");
            }
        }
    }

    private boolean checkSwitchCount(VASARestReqMessage restReq) {
        LOGGER.info("checkSwitchCount count = " + restReq.getSwitchControlCount());
        return (null == restReq.getSwitchControlCount() || restReq.getSwitchControlCount() > 0);
    }

    private boolean checkArrayIsNull(VASARestReqMessage restReq) {
        // TODO Auto-generated method stub
        LOGGER.debug("checkArrayIsNull arrayId = " + restReq.getArrayId());
        if (null == restReq.getArrayId() || restReq.getArrayId().equals("")) {
            return true;
        }

        return false;
    }

    private Object switchControl(Object reqMessage, String serviceApiName,
                                 String resObjClass, VASARestReqMessage restReq)
            throws ProtocolAdapterException {
        //在切控完成之前发出的切控请求，在切控完成之后还没有返回timeout异常之前，再次重新发一次业务请求
        synchronized (switch_control_lock) {
            if (switchControllerFinishMils != 0 && switchControllerFinishMils >= restReq.getRequestStartTime()) {
                LOGGER.warn("request start time :" + restReq.getRequestStartTime() + ",swith finish time :" + switchControllerFinishMils);

                return syncSendMessageCore(reqMessage, serviceApiName, resObjClass);
            }

            LOGGER.warn("now switch controller running state is :" + switchControllerIsRunning);

            if (!switchControllerIsRunning) {
                switchControllerIsRunning = true;
                LOGGER.warn("http client error , begin to switch controller ");
                try {
                    DeviceSwitchService service = ApplicationContextUtil.getBean("deviceSwitchService");
                    String newUrl = service.doCheck2Switch(restReq);
                    if (newUrl != null) {
                        // rebuild httpclient and try again
                        LOGGER.info("rebuild restful adater " + newUrl);
                        rebuildRestfulAdapter(newUrl);

                        LOGGER.info("send message try again .");
                        Object rt = syncSendMessageCore(reqMessage, serviceApiName, resObjClass);
                        switchControllerFinishMils = System.currentTimeMillis();
                        return rt;
                    } else {
                        LOGGER.error("all the controller not reachable .");
                        throw new ProtocolAdapterException("", ESDKErrorCodeConstant.ERROR_CODE_SYS_ERROR);
                    }
                } finally {
                    switchControllerIsRunning = false;
                }
            } else {
                LOGGER.warn("controller is switching , please hold on .");
                throw new ProtocolAdapterException("", ESDKErrorCodeConstant.ERROR_CODE_SYS_ERROR);
            }
        }
    }

    private void setTimeout(String serviceApiName) {
        // TODO Auto-generated method stub
        HttpParams params = httpClient.getParams();
        if (serviceApiName.equalsIgnoreCase("xxxxx/sessions")) {
            setHttpTimeout(params, 32000, 60000, 30000);
        } else {
            setHttpTimeout(params, 52000, 60000, 30000);
        }
    }

    public void setHttpTimeout(HttpParams params, Integer connectionTimeout, Integer soTimeout, Integer managerTimeout) {
        ConnManagerParams.setTimeout(params, managerTimeout);//该值就是连接不够用的时候等待超时时间，一定要设置，而且不能太大
        HttpConnectionParams.setSoTimeout(params, soTimeout);//设置等待数据超时时间  根据业务调整
        HttpConnectionParams.setConnectionTimeout(params, connectionTimeout);//设置请求超时 根据业务调整
    }

    @Override
    public Object syncSendMessage(Object reqMessage, String serviceApiName, String resObjClass)
            throws ProtocolAdapterException {
        if (!(reqMessage instanceof RestReqMessage)) {
            throw new IllegalArgumentException("reqMessage is not a instance of RestReqMessage");
        }
        VASARestReqMessage restReq = (VASARestReqMessage) reqMessage;

//		Gson gson = new Gson();
//        String reqPayloadInJSON = gson.toJson(restReq.getPayload());

        //LOGGER.info("req data: " + reqPayloadInJSON);

        if (restReq.isPaging()) {
            ResBean totalRes = new ResBean();
            List<Object> res = new ArrayList<>();

            if (!restReq.isHasRange()) {
                ResBean result = (ResBean) syncSendMessageCore(reqMessage, serviceApiName, null);

                totalRes.setErrorCode(result.getErrorCode());
                totalRes.setDescription(result.getDescription());

                if (0 != result.getErrorCode()) {
                    return totalRes;
                }

                List<Object> list = ((VASAJsonOverHttpCustProvider) sdkProtocolAdatperCustProvider)
                        .postBuildResList(result.getResData(), resObjClass);
                res.addAll(list);

                totalRes.setResData(res);

                return totalRes;
            } else {
                int start = 0;
                int end = start + restReq.getPageSize();
                while (true) {
                    String flag = serviceApiName.contains("?") ? "&" : "?";
                    String url = serviceApiName + flag + "range=[" + start + "-" + end + "]";
                    ;
                    ResBean result = (ResBean) syncSendMessageCore(reqMessage, url, null);

                    totalRes.setErrorCode(result.getErrorCode());
                    totalRes.setDescription(result.getDescription());

                    if (0 != result.getErrorCode()) {
                        return totalRes;
                    }

                    List<Object> list = ((VASAJsonOverHttpCustProvider) sdkProtocolAdatperCustProvider)
                            .postBuildResList(result.getResData(), resObjClass);
                    res.addAll(list);

                    if (list.size() < restReq.getPageSize()) {
                        totalRes.setResData(res);

                        return totalRes;
                    } else {
                        start = start + restReq.getPageSize();
                        end = start + restReq.getPageSize();
                        continue;
                    }
                }
            }
        } else {
            return syncSendMessageCore(reqMessage, serviceApiName, resObjClass);
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

    protected Object postSend(HttpResponse response, String resObjClass)
            throws ProtocolAdapterException, ParseException, IOException {
        HttpEntity entity = response.getEntity();
        String responsePayload = EntityUtils.toString(entity);

        LOGGER.info("responsePayload=" + responsePayload);
        if (null != sdkProtocolAdatperCustProvider) {
            sdkProtocolAdatperCustProvider.postSend(responsePayload);
            return sdkProtocolAdatperCustProvider.postBuildRes(responsePayload, resObjClass);
        } else {
            // Process the response body
            LOGGER.debug("The response content is:" + response);
            return responsePayload;
        }
    }

    protected HttpRequestBase buildHttpRequest(RestReqMessage restReq, String serviceApiName)
            throws URISyntaxException, UnsupportedEncodingException {
        HttpRequestBase request;
        if (ESDKConstant.HTTP_METHOD_GET.equalsIgnoreCase(restReq.getHttpMethod())) {
            HttpGet httpGet = new HttpGet(getURL(serviceApiName));
            setParameters(httpGet, restReq.getParameters());
            request = httpGet;
        } else if (ESDKConstant.HTTP_METHOD_POST.equalsIgnoreCase(restReq.getHttpMethod())) {
            HttpPost httpPost = new HttpPost(getURL(serviceApiName));
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

        return request;
    }

    protected String getURL(String serviceApiName) {
        if (null != sdkProtocolAdatperCustProvider) {
            return sdkProtocolAdatperCustProvider.reBuildNewUrl(serverUrl, serviceApiName);
        } else {
            return serverUrl;
        }
    }

    protected String getDevId() {
        if (null != sdkProtocolAdatperCustProvider) {
            return sdkProtocolAdatperCustProvider.getDevId();
        }
        return null;
    }

    protected String getPayloadAsString(RestReqMessage restReq) {
        String mediaType = restReq.getMediaType();
        mediaType = (mediaType == null ? "" : mediaType.toLowerCase(Locale.ENGLISH));
        if (mediaType.contains("json")) {
            if (null != sdkProtocolAdatperCustProvider) {
                return sdkProtocolAdatperCustProvider.getContent4Sending(restReq.getPayload());
            } else {
                Gson gson = new Gson();
                String reqPayloadInJSON = gson.toJson(restReq.getPayload());
                LOGGER.info("-----------------------------requestBody:" + reqPayloadInJSON);
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

    protected void setParameters(HttpRequestBase httpRequest, Map<String, String> parameters)
            throws URISyntaxException {
        if (!parameters.isEmpty()) {
            URIBuilder uriBuilder = new URIBuilder(httpRequest.getURI());

            for (Map.Entry<String, String> entry : parameters.entrySet()) {
                uriBuilder.addParameter(entry.getKey(), entry.getValue());
            }

            httpRequest.setURI(uriBuilder.build());
        }
    }

    protected void setHttpHeaders(HttpRequestBase request, Map<String, String> pHeaders, String url) {
        for (Map.Entry<String, String> entry : pHeaders.entrySet()) {
            request.addHeader(entry.getKey(), entry.getValue());
        }

        Map<String, String> headers = getRequestHeaders();
        if (headers != null && (url != null && url.indexOf("xxxxx/sessions") == -1))// 登陆请求不用带token
        {
            Set<Map.Entry<String, String>> es = headers.entrySet();
            for (Map.Entry<String, String> item : es) {
                request.addHeader(item.getKey(), item.getValue());
                LOGGER.info("-----------------------------HttpHeaders:[" +
                        item.getKey() + ":" + item.getValue() + "]");
            }
        }
        for (int i = 0; i < request.getAllHeaders().length; i++) {
            LOGGER.info(request.getAllHeaders()[i].getName() + ":" + request.getAllHeaders()[i].getValue());
        }
        // 不需要更新session,session保存在httpclient中，由它维护
//		updateCookies();

    }

    protected boolean checkArrayOnline1(String arrayId) {
        Set<String> offlineArrIds = new HashSet<String>();
        try {
            StorageModel storageModel = new StorageModel();
            SDKResult<List<S2DArray>> arrs = storageModel.getAllArray();

            if (0 != arrs.getErrCode()) {
                LOGGER.info("getAllArray error!");
            } else {
                List<S2DArray> listArr = arrs.getResult();
                for (S2DArray arr : listArr) {
                    if ("OFFLINE".equalsIgnoreCase(arr.getDevice_status())) {
                        offlineArrIds.add(arr.getId());
                    }
                }
            }

        } catch (SDKException e) {
            LOGGER.error("getAllArray error!", e);
        }
        return !(null != arrayId && offlineArrIds.contains(arrayId));
    }
	/*
	public boolean checkArrayIsLock(String arrayId){
		//true is lock
		//false is not lock
		if(StringUtils.isEmpty(arrayId)){
			LOGGER.error("check Array is lock, the arrayId null");
			return false;
		}
		QueryDBResponse response = new QueryDBResponse();
		List<StorageInfo> list= new ArrayList<StorageInfo>();
		StorageManagerResource storageManagerResource = new StorageManagerResource();
		response = storageManagerResource.queryData();
		list = response.getAllStorageInfo();
		String devId = null;
		for(StorageInfo storageInfo : list){
			if(arrayId.equalsIgnoreCase(storageInfo.getId().trim())){
				devId = storageInfo.getSn();
			}
		}
		if(StringUtils.isEmpty(devId)){
			LOGGER.error("In checkArrayIsLock function, the deviceId is null");
			return false;
		}
		Map<String,DArrayIsLock> arrayIsLockInfo = dataUtil.getArrayIsLock();
		DArrayIsLock arrayIsLock = arrayIsLockInfo.get(devId);
		if(arrayIsLock.getIsLock()){
			LOGGER.info("The arrayId = " + arrayId + " is lock");
			return true;
		}
		return false;
	}
	*/

    public boolean checkArrayOnline(String arrayId) {
        QueryDBResponse response = new QueryDBResponse();
        List<StorageInfo> list = new ArrayList<StorageInfo>();
        StorageManagerResource storageManagerResource = new StorageManagerResource();
        response = storageManagerResource.queryData();
        list = response.getAllStorageInfo();
        for (int i = 0; i < list.size(); i++) {
            if (arrayId.equals(list.get(i).getId().trim())) {
                if (list.get(i).getDevicestatus().trim().equals("ONLINE")) {
                    System.out.print("this array is online");
                    return true;
                }
                if (list.get(i).getDevicestatus().trim().equals("OFFLINE")) {
                    System.out.print("this array is offline");
                    return false;
                }
            }
        }
        return false;
    }

    @Override
    public boolean heartBeat() throws ProtocolAdapterException {
        return true;
    }

    @Override
    public int login(String userName, String pwd) {

        VASAJsonOverHttpCustProvider vasaJsonOverHttpCustProvider = (VASAJsonOverHttpCustProvider) this.sdkProtocolAdatperCustProvider;
        VASARestReqMessage req = new VASARestReqMessage();
        req.setHttpMethod("POST");
        req.setMediaType("json");
        //重新登录前，暂停3s保证其他请求已经处理完成
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            LOGGER.warn("Current login thread sleep error" + e);
        }

        MessageContext mc = ThreadLocalHolder.get();

        DeviceUserAuthReqBean reqPayload = new DeviceUserAuthReqBean();
        reqPayload.setUsername(userName);
        reqPayload.setPassword(pwd);
        reqPayload.setScope("0");

        req.setPayload(reqPayload);

        try {
            String devId = vasaJsonOverHttpCustProvider.getAccountInfo().getDevId();
            Map<String, DArrayIsLock> arrayIsLockInfo = dataUtil.getArrayIsLock();
            DArrayIsLock arrayIsLock = arrayIsLockInfo.get(devId);
            if (arrayIsLock.getIsLock()) {
                LOGGER.info("The storage Array devID = " + devId + " is lock, do not login again.");
                return -1;
            }
            ResBean res = (ResBean) syncSendMessage(req, "xxxxx/sessions",
                    "org.opensds.vasa.vasa20.device.array.bean.DeviceUserAuthResBean");

            if (1077949061 == res.getErrorCode() || -401 == res.getErrorCode()) {
                LOGGER.error("login failed! " + "description:" + res.getDescription() + " code:" + res.getErrorCode() + " start update strage lock status");
                arrayIsLock.setIsLock(true);
                arrayIsLockInfo.put(devId, arrayIsLock);
                dataUtil.setArrayIsLock(arrayIsLockInfo);
                mc.getEntities().put(VasaConstant.VASA_DEV_LOGIN_STATUS, "-1");
                return -1;
            }
            if (0 != res.getErrorCode()) {
                LOGGER.error("login failed! " + "description:" + res.getDescription() + " code:" + res.getErrorCode());
                mc.getEntities().put(VasaConstant.VASA_DEV_LOGIN_STATUS, "-1");
                return -1;
            }

            DeviceUserAuthResBean authRes = (DeviceUserAuthResBean) res.getResData();
            mc.getEntities().put(VasaConstant.VASA_DEV_LOGIN_STATUS, "0");
            List<Cookie> cookies = httpClient.getCookieStore().getCookies();
            if (null != cookies && cookies.size() != 0) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equalsIgnoreCase("session")) {
                        VASASession.setSession(authRes.getDeviceid(), cookie.getValue());
                        LOGGER.info("login cookie:" + cookie.getValue());
                    }
                }
            }

            VASASession.setToken(authRes.getDeviceid(), authRes.getiBaseToken());
            LOGGER.info("login token:" + authRes.getiBaseToken());
            // 更新Capability中的protocolAdapter
            VASAJsonOverHttpCustProvider provider = (VASAJsonOverHttpCustProvider) getSdkProtocolAdatperCustProvider();
            AccountInfo accountInfo = provider.getAccountInfo();
            accountInfo.setDevId(authRes.getDeviceid());
            accountInfo.setUserId(userName);
            accountInfo.setPassword(pwd);
            return 0;
        } catch (ProtocolAdapterException e) {
            LOGGER.error("", e);
            mc.getEntities().put(VasaConstant.VASA_DEV_LOGIN_STATUS, "-1");
            return -1;
        }

    }

    @Override
    public int logout() throws ProtocolAdapterException {
        return 0;
    }

    protected Map<String, String> getRequestHeaders() {
        // For override by derived class
        return sdkProtocolAdatperCustProvider.getRequestHeaders();
    }

    @Override
    public ISDKProtocolAdatperCustProvider getSdkProtocolAdatperCustProvider() {
        return sdkProtocolAdatperCustProvider;
    }

    @Override
    public void setSdkProtocolAdatperCustProvider(ISDKProtocolAdatperCustProvider sdkProtocolAdatperCustProvider) {
        this.sdkProtocolAdatperCustProvider = sdkProtocolAdatperCustProvider;
    }

    @Override
    public String syncSendMessage(String s, String s1) throws ProtocolAdapterException {
        return null;
    }

    public static void main(String[] args) {
        List<String> cookie = new ArrayList<>();

        cookie.add("ssss");
        cookie.add("test");
        System.out.println(cookie);
    }
}
