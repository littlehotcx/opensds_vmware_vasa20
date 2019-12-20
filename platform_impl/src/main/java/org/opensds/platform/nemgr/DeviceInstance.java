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

package org.opensds.platform.nemgr;

import java.util.Date;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensds.platform.abnormalevent.itf.IAbnormalevent;
import org.opensds.platform.authorize.itf.IAuthorize;
import org.opensds.platform.authorize.itf.IAuthorizePolicy;
import org.opensds.platform.common.ThreadLocalHolder;
import org.opensds.platform.common.bean.abnormalevent.AbnormaleventBean;
import org.opensds.platform.common.config.ConfigManager;
import org.opensds.platform.common.constants.ESDKConstant;
import org.opensds.platform.common.constants.ESDKErrorCodeConstant;
import org.opensds.platform.common.exception.SDKException;
import org.opensds.platform.common.utils.ApplicationContextUtil;
import org.opensds.platform.common.utils.StringUtils;
import org.opensds.platform.nemgr.itf.IDeviceManager;
import org.opensds.platform.nemgr.conn.DeviceReconnector;
import org.opensds.platform.nemgr.itf.IDevice;
import org.opensds.platform.nemgr.itf.IDeviceConnection;

enum DevConnStatus
{
    DCS_NORMAL, DCS_ABANDON, DCS_BREAK
}

/**
 * 设备实体类
 * 
 * @author t00212088
 * 
 */
public class DeviceInstance
{
    private static final Logger LOGGER = LogManager.getLogger(DeviceInstance.class);
    
    private String deviceId;
    
    private String deviceName;
    
    private IDeviceManager.DEV_CONN_MODE_TYPE connMode;
    
    private String serviceAccessPoint;
    
    private String loginUser;
    
    private String loginPwd;
    
    private String deviceType;
    
    private String deviceVersion;
    
    private IDevice deviceItf;
    
    private String reserver1;
    
    private String reserver2;
    
    private boolean isAsDefault;
    
    private IAuthorizePolicy authorizePolicy;
    
    private IAuthorize authorize = ApplicationContextUtil.getBean("authorize");
    
    private Map<String, String> productAuthPolicyMappings;
    
    private Object create_connection_lock = new Object();
    
    public DeviceInstance()
    {
        this.connMode = IDeviceManager.DEV_CONN_MODE_TYPE.NOT_CONNECT_AUTOMATIC;
        this.deviceItf = null;
        loadConfig();
    }
    
    private void loadConfig()
    {
        String productAuthPolicyMappingConfig = ConfigManager.getInstance().getValue("platform.product.authpolicy.mapping");
        productAuthPolicyMappings = StringUtils.parseString(productAuthPolicyMappingConfig, ",", ":");
    }
    
    public DeviceInstance(String deviceId, String deviceName, String deviceType, String deviceVersion, String sap,
        String loginUser, String loginPwd, String connMode, String reserver1, String reserver2, boolean isAsDefault)
    {
        this.deviceId = deviceId;
        this.deviceName = deviceName;
        this.deviceType = deviceType;
        this.deviceVersion = deviceVersion;
        this.serviceAccessPoint = sap;
        this.loginUser = loginUser;
        this.loginPwd = loginPwd;
        this.connMode =
            "0".equals(connMode) ? IDeviceManager.DEV_CONN_MODE_TYPE.NOT_CONNECT_AUTOMATIC : IDeviceManager.DEV_CONN_MODE_TYPE.CONNECT_AUTOMATIC;
        this.deviceItf = null;
        this.reserver1 = reserver1;
        this.reserver2 = reserver2;
        this.isAsDefault = isAsDefault;
        
        loadConfig();
    }
    
    public String getDeviceId()
    {
        return deviceId;
    }
    
    public void setDeviceId(String deviceId)
    {
        this.deviceId = deviceId;
    }
    
    public IDeviceManager.DEV_CONN_MODE_TYPE getConnMode()
    {
        return connMode;
    }
    
    public void setConnMode(IDeviceManager.DEV_CONN_MODE_TYPE connType)
    {
        this.connMode = connType;
    }
    
    public String getServiceAccessPoint()
    {
        return serviceAccessPoint;
    }
    
    public void setServiceAccessPoint(String serviceAccessPoint)
    {
        this.serviceAccessPoint = serviceAccessPoint;
    }
    
    public String getLoginUser()
    {
        return loginUser;
    }
    
    public void setLoginUser(String loginUser)
    {
        this.loginUser = loginUser;
    }
    
    public String getLoginPwd()
    {
        return loginPwd;
    }
    
    public void setLoginPwd(String loginPwd)
    {
        this.loginPwd = loginPwd;
    }
    
    public String getDeviceType()
    {
        return deviceType;
    }
    
    public void setDeviceType(String deviceType)
    {
        this.deviceType = deviceType;
    }
    
    public String getDeviceVersion()
    {
        return deviceVersion;
    }
    
    public void setDeviceVersion(String deviceVersion)
    {
        this.deviceVersion = deviceVersion;
    }
    
    public IDevice getDeviceItf()
    {
        return deviceItf;
    }
    
    public void setDeviceItf(IDevice connItf)
    {
        this.deviceItf = connItf;
    }
    
    public String getDeviceName()
    {
        return deviceName;
    }
    
    public void setDeviceName(String deviceName)
    {
        this.deviceName = deviceName;
    }
    
    public String getReserver1()
    {
        return reserver1;
    }
    
    public void setReserver1(String reserver1)
    {
        this.reserver1 = reserver1;
    }
    
    public String getReserver2()
    {
        return reserver2;
    }
    
    public void setReserver2(String reserver2)
    {
        this.reserver2 = reserver2;
    }
    
    public boolean isAsDefault()
    {
        return isAsDefault;
    }
    
    public void setAsDefault(boolean isAsDefault)
    {
        this.isAsDefault = isAsDefault;
    }
    
    /**
     * 获取连接
     * 
     * @param connMgr 设备连接管理
     * @param reconnector 设备重连管理
     * @return
     * @throws SDKException
     */
    public IDeviceConnection getConnection(DeviceConnManager connMgr, DeviceReconnector reconnector)
        throws SDKException
    {
        IAbnormalevent abnormaleventManager = ApplicationContextUtil.getBean("abnormaleventManager");
        
        IDeviceConnection conn = null;
        // 将IDevice实现类加入DeviceInstance中
        prepareForConnect((DeviceFactory)ApplicationContextUtil.getBean("deviceFactory"));
        if (null == getDeviceItf())
        {
            SDKException ex = new SDKException("fail to login, the device itf dose not exist");
            ex.setSdkErrCode(ESDKErrorCodeConstant.ERROR_CODE_DEVICEITF_NOT_EXIST);
            throw ex;
        }
        String connIdFromContext = getDeviceItf().getConnIdFromContext();
        String connId = deviceId + "_" + connIdFromContext;
        
        // replace session before log
        String logMessage = connId;
        String exp = logMessage.substring(0, logMessage.length() / 2);
        StringBuffer rep = new StringBuffer();
        for (int i = 0; i < exp.length(); i++)
        {
            rep.append("*");
        }
        logMessage = logMessage.replace(exp, rep);

        
        LOGGER.debug("connId=" + logMessage);
        conn = getDeviceItf().getConnById(connId);
        LOGGER.debug("1 conn = " + conn);
        if (null == conn)
        {
        		//加锁，给每个DeviceInstance添加，避免并发场景下，发生多个注册请求，导致阵列session占满
	        	synchronized (create_connection_lock) {
	        		conn = getDeviceItf().getConnById(connId);
	        		if(null == conn){
	        			if (IDeviceManager.DEV_CONN_MODE_TYPE.CONNECT_AUTOMATIC == getConnMode())
	        			{
	        				// 长连接进行第一次连接
	        				conn =
	        						getDeviceItf().createConnection(connIdFromContext,
	        								getServiceAccessPoint(),
	        								getLoginUser(),
	        								getLoginPwd());
	        				LOGGER.debug("2 conn = " + conn);
	        				// 将device id放入参数map中
	        				conn.setAdditionalData("deviceId", getDeviceId());
	        				// 将deviceName放入参数map中
	        				conn.setAdditionalData("deviceName", getDeviceName());
	        				
	        				// 得到connection实例后进行连接
	        				if (conn.initConn(connIdFromContext))
	        				{
	        					conn.setAdditionalData("connId", connIdFromContext);
	        					
	        					//连接成功后如果有异常就清除
	        					AbnormaleventBean ebean = new AbnormaleventBean();
	        					ebean.setEndTime(new Date());
	        					abnormaleventManager.endException(deviceName + "_" + IAbnormalevent.FAIL_TO_CONNECT, ebean);
	        					
	        					conn.setAdditionalData("failTime", Integer.valueOf(0));
	        					conn.setAdditionalData("connStatus", DevConnStatus.DCS_NORMAL);
	        					// 连接成功，将conn放入保活队列中
	        					connMgr.addToKeepAliveSchedule(conn);
	        					
	        					//将连接存入设备的map中
	        					getDeviceItf().addId2ConnMap(deviceId + "_" + connIdFromContext, conn);
	        				}else{
	        					//连接失败需要记录异常
	        					AbnormaleventBean ebean = new AbnormaleventBean();
	        					ebean.setObjName(deviceName);
	        					ebean.setOccurrence(IAbnormalevent.FAIL_TO_CONNECT);
	        					ebean.setOccurTime(new Date());
	        					ebean.setExceptionMessage("");
	        					abnormaleventManager.occurException(ebean.getObjName() + "_" + ebean.getOccurrence(), ebean);
	        					// 连接失败，则将conn放入重连列表
	        					// 且是网络错误重连才有意义
	        					if (authorize.isLocalAuth(getProductFromDevType())
	        							&& "Y".equalsIgnoreCase((String)conn.getAdditionalData("networkErrorFlag")))
	        					{
	        						if (conn.isLocalAuth())
	        						{
	        							reconnector.addDevice(conn);
	        						}
	        						//将连接存入设备的map中
	        						getDeviceItf().addId2ConnMap(deviceId + "_" + connIdFromContext, conn);
	        					}
	        					else
	        					{
	        						conn = null;
	        					}
	        				}
	        				
	        			}else if (IDeviceManager.DEV_CONN_MODE_TYPE.NOT_CONNECT_AUTOMATIC == getConnMode()){
	        				// 开始连接非长连接
	        				conn = deviceItf.createConnection(connIdFromContext, serviceAccessPoint, getLoginUser(), getLoginPwd());
	        				LOGGER.debug("3 conn = " + conn);
	        				// 连接成功放入pool，失败丢弃
	        				if (conn.initConn(connIdFromContext))
	        				{
	        					LOGGER.debug("initConn suc");
	        					//连接成功后如果有异常就清除
	        					AbnormaleventBean ebean = new AbnormaleventBean();
	        					ebean.setEndTime(new Date());
	        					abnormaleventManager.endException(deviceName + "_" + IAbnormalevent.FAIL_TO_CONNECT, ebean);
	        					
	        					conn.setAdditionalData("deviceId", getDeviceId());
	        					conn.setAdditionalData("connId", connIdFromContext);
	        					conn.setAdditionalData("deviceName", getDeviceName());
	        					
	        					getDeviceItf().addId2ConnMap(deviceId + "_" + connIdFromContext, conn);
	        					// 若是新建的连接，则放入保活列表
	        					connMgr.addToKeepAliveSchedule(conn);
	        				}else{
	        					//连接失败需要记录异常
	        					AbnormaleventBean ebean = new AbnormaleventBean();
	        					ebean.setObjName(deviceName);
	        					ebean.setOccurrence(IAbnormalevent.FAIL_TO_CONNECT);
	        					ebean.setOccurTime(new Date());
	        					ebean.setExceptionMessage("");
	        					abnormaleventManager.occurException(ebean.getObjName() + "_" + ebean.getOccurrence(), ebean);
	        					
	        					conn = null;
	        					SDKException ex =
	        							new SDKException("fail to login Terminal, please check username and password,or your network");
	        					ex.setSdkErrCode(ESDKErrorCodeConstant.ERROR_CODE_DEVICE_CONN_ERROR);
	        					throw ex;
	        				}
	        				conn.setAdditionalData("lastTime", new Date());
	        			}
	        		}
	        	}
			}
        	
        
        if (null != conn)
        {
            conn.setAdditionalData("connId", connIdFromContext);
            Object acctInfo = ThreadLocalHolder.get().getEntities().get(ESDKConstant.ACCT_INFO_ESDK);
            if (null != acctInfo)
            {
                conn.setAdditionalData(ESDKConstant.ACCT_INFO_ESDK, acctInfo);
            }
        }
        LOGGER.debug("conn = " + conn);
        return conn;
    }
    
    /**
     * 准备连接
     * 
     * @param factory 设备代理工厂
     * @return
     */
    public boolean prepareForConnect(DeviceFactory factory)
    {
        if (null == getDeviceItf())
        {
            IDevice device = factory.createInstance(this);
            authorizePolicy = authorize.getAuthPolicyImpl(authorize.getAuthPolicy(getProductFromDevType()), deviceId);
            if (null != device)
            {
                device.setAccountPolicy(authorizePolicy);
                device.setDeviceId(deviceId);
                device.prepareAuthInfo(getLoginUser(), getLoginPwd());
            }
            setDeviceItf(device);
        }
        else
        {
            getDeviceItf().prepareAuthInfo(getLoginUser(), getLoginPwd());
        }
        if (null != getDeviceItf())
        {
            return true;
        }
        return false;
    }
    
    /**
     * 连接设备
     * 
     * @param connMgr 设备连接管理
     * @param reconnector 设备重连管理
     */
    public void doDisconnect(DeviceConnManager connMgr, DeviceReconnector reconnector)
    {
        if (null == getDeviceItf())
        {
            return;
        }
        String connIdFromContext = getDeviceItf().getConnIdFromContext();
        IDeviceConnection conn = getDeviceItf().getConnById(deviceId + "_" + connIdFromContext);
        
        if (null != conn)
        {
            // 销毁conn的同时，将其从保活或者重连列表中剔除
            connMgr.removeFromKeepAliveSchedule(conn);
            reconnector.removeDevice(conn);
            conn.destroyConn((String)conn.getAdditionalData("connId"));
            getDeviceItf().removeConnId(deviceId + "_" + connIdFromContext);
        }
    }
    
    private String getProductFromDevType()
    {
        LOGGER.debug("deviceType=" + deviceType);
        String product = (String) ThreadLocalHolder.get().getEntities().get(ESDKConstant.CURRENT_PRODUCT);
        if (StringUtils.isNotEmpty(product))
        {
            LOGGER.debug("product from thread local is " + product);
            return product;
        }
        
        String className = DeviceConfigLoader.getDeviceProxyConfig(deviceType).getClassName();
        for (Map.Entry<String, String> entry : productAuthPolicyMappings.entrySet())
        {
            if (className.contains(entry.getKey()))
            {
                return entry.getValue();
            }
        }
        LOGGER.warn("The derived product is empty");
        return "";
    }
    
    public void destory()
    {
        if (null == getDeviceItf())
        {
            return;
        }
        getDeviceItf().releaseConns();
    }
    
    public void setUserInfo(String user, String pwd)
    {
        loginUser = user;
        loginPwd = pwd;
    }
    
    /**
     * 获取设备代理
     * 
     * @param itfs 代理接口类型
     * @param connMgr 设备连接管理
     * @param reconnector 设备重连管理
     * @return
     * @throws SDKException
     */
    @SuppressWarnings("rawtypes")
    public Object getServiceProxy(Class[] itfs, DeviceConnManager connMgr, DeviceReconnector reconnector)
        throws SDKException
    {
        IDeviceConnection conn = getConnection(connMgr, reconnector);
        LOGGER.debug("conn = " + conn);
        if (null != conn)
        {
            Object obj = conn.getServiceProxy(itfs);
            return obj;
        }
        else
        {
            LOGGER.debug("Device connection is null");
            SDKException exception = new SDKException("Device connection is null");
            exception.setSdkErrCode(ESDKErrorCodeConstant.ERROR_CODE_CONN_NULL);
            throw exception;
        }
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if (null == obj)
        {
            return false;
        }
        if (!(obj instanceof DeviceInstance))
        {
            return false;
        }
        DeviceInstance dev = (DeviceInstance)obj;
        if (dev.getDeviceId().equals(this.deviceId) && dev.getServiceAccessPoint().equals(this.serviceAccessPoint)
            && dev.getDeviceType().equals(this.deviceType) && dev.getDeviceVersion().equals(this.deviceVersion)
            && dev.getLoginUser().equals(this.loginUser) && dev.getLoginPwd().equals(this.loginPwd))
        {
            return true;
        }
        return false;
    }
    
    @Override
    public int hashCode()
    {
        int result =
            getDeviceId().hashCode() + getServiceAccessPoint().hashCode() + getDeviceType().hashCode()
                + getDeviceVersion().hashCode() + getLoginUser().hashCode() + getLoginPwd().hashCode();
        return result;
    }
}
