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

import org.opensds.platform.common.bean.config.DeviceConfig;
import org.opensds.platform.common.utils.ArrayPwdAES128Util;
import org.opensds.platform.common.utils.Base64Utils;
import org.opensds.platform.common.utils.StringUtils;
import org.opensds.platform.config.service.itf.IDeviceConfigService;
import org.opensds.platform.config.dao.DeviceFileDAO;
import org.opensds.platform.config.dao.itf.IDeviceDAO;

public final class DeviceConfigService extends BaseService implements IDeviceConfigService
{
    private static final Logger LOGGER = LogManager.getLogger(DeviceConfigService.class);
    
    private static IDeviceConfigService instance = new DeviceConfigService();
    
    private IDeviceDAO deviceDao;
    
    public static IDeviceConfigService getInstance()
    {
        return instance;
    }
    
    public DeviceConfigService()
    {
        try
        {
            String file = getFilePath("devices.xml");
            
            deviceDao = new DeviceFileDAO(file);
        }
        catch (Exception e)
        {
            LOGGER.error("", e);
        }
    }
    
    @Override
    public boolean addDevice(DeviceConfig device)
    {
        try
        {
            device.setLoginPwd(Base64Utils.encode(ArrayPwdAES128Util.encryptPwd((device.getLoginUser() + device.getLoginPwd()).getBytes("UTF-8"))));
//        	device.setLoginPwd(device.getLoginPwd());
            boolean result = deviceDao.addDevice(device);
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
    public boolean updateDevice(DeviceConfig device)
    {
        try
        {
            device.setLoginPwd(Base64Utils.encode(ArrayPwdAES128Util.encryptPwd((device.getLoginUser() + device.getLoginPwd()).getBytes("UTF-8"))));
//            device.setLoginPwd(device.getLoginPwd());
            boolean result = deviceDao.updateDevice(device);
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
    public boolean deleteDevice(String deviceId)
    {
        boolean result = deviceDao.deleteDevice(deviceId);
        if (result)
        {
            notifyObservers();
        }
        
        return result;
    }
    
    @Override
    public DeviceConfig getDeviceById(String deviceId)
    {
        return deviceDao.getDeviceById(deviceId);
    }
    
    @Override
    public List<DeviceConfig> getAllDevices()
    {
        List<DeviceConfig> result = deviceDao.getAllDevices();
        
        if (null != result && !result.isEmpty())
        {
            for (DeviceConfig deviceConfig : result)
            {
                if (null != deviceConfig && !StringUtils.isEmpty(deviceConfig.getLoginPwd()))
                {

 //               	LOGGER.info("decryptPwdByOldKey loginUser="+deviceConfig.getLoginUser()+",loginPwd="+deviceConfig.getLoginPwd());
                    String decryptPwdByOldKey = ArrayPwdAES128Util.decryptPwd(deviceConfig.getLoginUser(), deviceConfig.getLoginPwd());
 //                   LOGGER.info("decryptPwdByOldKey decryptPwdByOldKey="+decryptPwdByOldKey);

					deviceConfig.setLoginPwd(decryptPwdByOldKey);
                }
            }
        }
        LOGGER.info("In getAllDevices function, the result is " + result.toString());
        
        return result;
    }

}
