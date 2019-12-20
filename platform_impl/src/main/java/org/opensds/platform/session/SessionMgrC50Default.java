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

package org.opensds.platform.session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.cxf.common.util.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensds.platform.session.itf.ISessionMgr;

public class SessionMgrC50Default implements ISessionMgr
{
    private static final Logger LOGGER = LogManager.getLogger(SessionMgrC50Default.class);
    
    private static Map<String, SessionInfo> sessionMap = new ConcurrentHashMap<String, SessionInfo>();
    
    private static SessionMgrC50Default sessionManager;
    
    private SessionMgrC50Default()
    {
    }
    
    public static synchronized SessionMgrC50Default getInstance()
    {
        if (null == sessionManager)
        {
            sessionManager = new SessionMgrC50Default();
        }
        return sessionManager;
    }
    
    @Override
    public synchronized boolean saveSDKSession(String sdkSession)
    {
        if (StringUtils.isEmpty(sdkSession))
        {
            return false;
        }
        SessionInfo ds = sessionMap.get(sdkSession);
        if (null != ds)
        {
            return false;
        }
        
        ds = new SessionInfo();
        try
        {
            sessionMap.put(sdkSession, ds);
        }
        catch (Exception e)
        {
            LOGGER.error("", e);
            return false;
        }
        return true;
    }
    
    public synchronized boolean saveSecretKey(String sdkSession, byte[] secretKey)
    {
        return saveSecretKey(sdkSession, secretKey, null);
    }
    
    public synchronized boolean saveSecretKey(String sdkSession, byte[] secretKey, byte[] iv)
    {
        if (StringUtils.isEmpty(sdkSession))
        {
            return false;
        }
        
        SessionInfo info = sessionMap.get(sdkSession);
        if (null == info)
        {
            info = new SessionInfo();
        }
        info.setSecretKey(secretKey);
        info.setIv(iv);
        
        sessionMap.put(sdkSession, info);
        return true;
    }
    
    @Override
    public synchronized boolean isSDKSessionExists(String sdkSession)
    {
        return null == sdkSession ? false : sessionMap.containsKey(sdkSession);
    }
    
    @Override
    public synchronized void removeSDKSession(String sdkSession)
    {
        if (StringUtils.isEmpty(sdkSession))
        {
            return;
        }
        try
        {
            if (!sessionMap.containsKey(sdkSession))
                return;
            sessionMap.remove(sdkSession);
        }
        catch (Exception e)
        {
            LOGGER.error("", e);
        }
    }
    
    @Override
    public synchronized byte[] getSecretKey(String sdkSession)
    {
        if (StringUtils.isEmpty(sdkSession))
        {
            return null;
        }
        SessionInfo ds = sessionMap.get(sdkSession);
        
        if (null != ds)
        {
            return ds.getSecretKey();
        }
        return null;
    }
    
    @Override
    public synchronized byte[] getIv(String sdkSession)
    {
        if (StringUtils.isEmpty(sdkSession))
        {
            return null;
        }
        SessionInfo ds = sessionMap.get(sdkSession);
        
        if (null != ds)
        {
            return ds.getIv();
        }
        return null;
    }
    
}
