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

package org.opensds.platform.config;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.opensds.platform.common.bean.config.AppMappingConfig;
import org.opensds.platform.common.bean.config.UserConfig;
import org.opensds.platform.common.config.ConfigManagerNoDecrypt;
import org.opensds.platform.common.config.ConfigManagerUpdate;
import org.opensds.platform.common.utils.AES128System;
import org.opensds.platform.common.utils.ArrayPwdAES128Util;
import org.opensds.platform.config.service.AppMappingConfigService;
import org.opensds.platform.config.service.UserConfigService;
import org.opensds.platform.config.service.itf.IAppMappingConfigService;
import org.opensds.platform.config.service.itf.IUserConfigService;

public class SystemConfig
{
    private static final Logger LOGGER = LogManager.getLogger(SystemConfig.class);
    
    static
    {
        // 加载系统密钥，更新系统密钥，更新xml配置文件
        LOGGER.info("Load SystemConfig");
        AES128System.init();
        if ("Y".equalsIgnoreCase(ConfigManagerNoDecrypt.getInstance().getValue("esdk.platform.web", "N"))
            && "N".equalsIgnoreCase(ConfigManagerNoDecrypt.getInstance().getValue("platform.config.tool", "N"))
            && "N".equalsIgnoreCase(ConfigManagerNoDecrypt.getInstance().getValue("platform.mgmt.srv", "N")))
        {
            
            // 更新users.xml
            LOGGER.info("update users.xml--->start");
            IUserConfigService userConfigService = UserConfigService.getInstance();
            List<UserConfig> users = userConfigService.getAllUsers();
            if (null != users)
            {
                LOGGER.info("update users.xml; sizes: " + users.size());
                for (UserConfig item : users)
                {
                    userConfigService.updateUser(item);
                }
            }
            LOGGER.info("update users.xml--->end");
            
            // 更新app_device_app_mapping.xml
            LOGGER.info("update app_device_app_mapping.xml--->start");
            IAppMappingConfigService appMappingService = AppMappingConfigService.getInstance();
            List<AppMappingConfig> mappings = appMappingService.getAllAppMappings();
            if (null != mappings)
            {
                LOGGER.info("update app_device_app_mapping.xml; size: " + mappings.size());
                for (AppMappingConfig item : mappings)
                {
                    appMappingService.updateAppMapping(item);
                }
            }
            LOGGER.info("update app_device_app_mapping.xml--->end");
            
            // 更新properties文件
            LOGGER.info("update *.properties--->start");
            updateProperties();
            LOGGER.info("update *.properties--->end");
            // 配置文件更新完后，覆盖旧key
            AES128System.balanceKey();
            ArrayPwdAES128Util.init();
            
            // 更新devices.xml,不用每次重启都修改数据表密码
            /*LOGGER.info("update devices.xml--->start");
            IDeviceConfigService deviceConfigService = DeviceConfigService.getInstance();
            List<DeviceConfig> devices = deviceConfigService.getAllDevices();
            if (null != devices)
            {
            	LOGGER.info("update devices.xml; sizes: " + devices.size());
            	for (DeviceConfig item : devices)
            	{
            		deviceConfigService.updateDevice(item);
            	}
            }
            LOGGER.info("update devices.xml--->end");*/
        }
    }
    
    public static void init()
    {
    }
    
    private static void updateProperties()
    {
        ConfigManagerUpdate.init();
    }
    
}
