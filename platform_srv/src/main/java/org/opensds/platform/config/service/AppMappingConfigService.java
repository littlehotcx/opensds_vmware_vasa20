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

package org.opensds.platform.config.service;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.opensds.platform.common.bean.config.AppMappingConfig;
import org.opensds.platform.common.utils.AES128System;
import org.opensds.platform.common.utils.Base64Utils;
import org.opensds.platform.common.utils.StringUtils;
import org.opensds.platform.config.dao.AppMappingDAO;
import org.opensds.platform.config.dao.itf.IAppMappingDAO;
import org.opensds.platform.config.service.itf.IAppMappingConfigService;

/**
 * 设备和eSDK用户的一一映射
 * @author sWX198756
 * @since  eSDK Platform V100R003C10
 */
public final class AppMappingConfigService extends BaseService implements IAppMappingConfigService
{
    private static final Logger LOGGER = LogManager.getLogger(AppMappingConfigService.class);
    
    private static IAppMappingConfigService instance = new AppMappingConfigService();
    
    private IAppMappingDAO appMappingDAO;
    
    public static IAppMappingConfigService getInstance()
    {
        return instance;
    }
    
    public AppMappingConfigService()
    {
        try
        {
            String file = getFilePath("app_device_app_mapping.xml");
            
            appMappingDAO = new AppMappingDAO(file);
        }
        catch (Exception e)
        {
            LOGGER.error("", e);
        }
    }
    
    @Override
    public boolean addAppMapping(AppMappingConfig appMapping)
    {
        try
        {
            appMapping.setDeviceAppPwd(Base64Utils.encode(AES128System.encryptPwdByNewKey((appMapping.getDeviceApp() + appMapping.getDeviceAppPwd()).getBytes("UTF-8"))));
            boolean result = appMappingDAO.addAppMapping(appMapping);
            if (result)
            {
                notifyObservers();
            }
            
            return result;
        }
        catch (Exception e)
        {
            LOGGER.error("", e);
            return false;
        }
    }
    
    @Override
    public boolean updateAppMapping(AppMappingConfig appMapping)
    {
        try
        {
            appMapping.setDeviceAppPwd(Base64Utils.encode(AES128System.encryptPwdByNewKey((appMapping.getDeviceApp() + appMapping.getDeviceAppPwd()).getBytes("UTF-8"))));
            boolean result = appMappingDAO.updateAppMapping(appMapping);
            if (result)
            {
                notifyObservers();
            }
            
            return result;
        }
        catch (Exception e)
        {
            LOGGER.error("", e);
            return false;
        }
    }
    
    @Override
    public boolean deleteAppMapping(String deviceId, String esdkApp)
    {
        boolean result = appMappingDAO.deleteAppMapping(deviceId, esdkApp);
        if (result)
        {
            notifyObservers();
        }
        
        return result;
    }
    
    @Override
    public AppMappingConfig getAppMappingByESDKApp(String esdkApp)
    {
        return appMappingDAO.getAppMappingByESDKApp(esdkApp);
    }
    
    @Override
    public List<AppMappingConfig> getAllAppMappings()
    {
        List<AppMappingConfig> result = appMappingDAO.getAllAppMappings();
        if (null != result && !result.isEmpty())
        {
            for (AppMappingConfig item : result)
            {
                if (null != item && !StringUtils.isEmpty(item.getDeviceAppPwd()))
                {
                    item.setDeviceAppPwd(AES128System.decryptPwdByOldKey(item.getDeviceApp(), item.getDeviceAppPwd()));
                }
            }
        }
        return result;
    }
}
