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

package org.opensds.platform.nemgr.base;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensds.platform.authorize.itf.IAuthorizePolicy;
import org.opensds.platform.common.constants.ESDKErrorCodeConstant;
import org.opensds.platform.common.exception.SDKException;
import org.opensds.platform.nemgr.itf.IDevice;
import org.opensds.platform.nemgr.itf.IDeviceConnection;

/**
 * 设备适配基础类，应用可从这个类继承以减少非业务部分的工作
 * 
 * @author j00160659
 * 
 */
public abstract class MultiConnDeviceBase implements IDevice
{
    private static final Logger LOGGER = LogManager.getLogger(MultiConnDeviceBase.class);
    
    protected ConcurrentHashMap<String, IDeviceConnection> id2Connection =
        new ConcurrentHashMap<String, IDeviceConnection>();
    
    protected HashMap<Class<?>, Object> mapFunc = new HashMap<Class<?>, Object>();
    
    protected String deviceId;
    
    protected IAuthorizePolicy authorizePolicy;
    
    @Override
    public IDeviceConnection getConnById(String key)
    {
        return id2Connection.get(key);
    }
    
    @Override
    public void addId2ConnMap(String key, IDeviceConnection value)
    {
        if (value == null)
        {
            id2Connection.remove(key);
        }
        else
        {
            id2Connection.put(key, value);
        }
    }
    
    @Override
    public boolean removeConnId(String key)
    {
        id2Connection.remove(key);
        return true;
    }
    
    protected void addServiceObjectMap(Class<?> itfClass, Object implObj)
    {
        recursiveInterface(itfClass, implObj);
        mapFunc.put(itfClass, implObj);
    }
    
    private void recursiveInterface(Class<?> itfClass, Object implObj)
    {
        for (Class<?> clazz : itfClass.getInterfaces())
        {
            mapFunc.put(clazz, implObj);
            recursiveInterface(clazz, implObj);
        }
    }
    
    public Object getService(Class<?> itfClass)
    {
        return mapFunc.get(itfClass);
    }
    
    public InvocationHandler getService(Class<?>[] itfs)
    {
        return new InvocationHandler()
        {
            public Object invoke(Object proxy, Method m, Object[] args)
                throws SDKException// 此方法在代理类中
            {
                Object obj = mapFunc.get(m.getDeclaringClass());
                if (null == obj)
                {
                    for (Class<?> clazz : m.getDeclaringClass().getInterfaces())
                    {
                        obj = mapFunc.get(clazz);
                        if (null != obj)
                        {
                            break;
                        }
                    }
                }
                try
                {
                    return m.invoke(obj, args);
                }
                catch (IllegalArgumentException e)
                {
                    LOGGER.error("", e);
                    SDKException sdkException = new SDKException(e.getMessage());
                    sdkException.setSdkErrCode(ESDKErrorCodeConstant.ERROR_CODE_API_NOT_SUPPORT);
                    throw sdkException;
                }
                catch (Exception e)
                {
                    LOGGER.error("", e);
                    SDKException sdkException = new SDKException(e.getCause());
                    sdkException.setSdkErrCode(ESDKErrorCodeConstant.ERROR_CODE_SYS_ERROR);
                    throw sdkException;
                }
            }
        };
    }
    
    @Override
    public void setAccountPolicy(IAuthorizePolicy authorizePolicy)
    {
        this.authorizePolicy = authorizePolicy;
    }
    
    @Override
    public void prepareAuthInfo(String user, String pwd)
    {
    }
    
    @Override
    public void setDeviceId(String deviceId)
    {
        this.deviceId = deviceId;
    }
}
