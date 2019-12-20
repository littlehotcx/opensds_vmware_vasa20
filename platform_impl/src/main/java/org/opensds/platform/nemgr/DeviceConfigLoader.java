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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensds.platform.nemgr.itf.IDeviceManager;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import org.opensds.platform.common.bean.config.DeviceConfig;
import org.opensds.platform.common.bean.nemgr.DeviceProxy;
import org.opensds.platform.common.bean.nemgr.Proxys;
import org.opensds.platform.nemgr.itf.IDevice;


public class DeviceConfigLoader
{
    private static final Logger LOGGER = LogManager.getLogger(DeviceConfigLoader.class);

    private static List<Proxys> proxys = new ArrayList<Proxys>();

    @SuppressWarnings("unchecked")
    public static void loadDeviceProxys(DeviceFactory deviceFactory)
    {
        try {
            JAXBContext jc = JAXBContext.newInstance(Proxys.class);
            Unmarshaller u = jc.createUnmarshaller();
            ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources("classpath*:META-INF/device-proxy-config/device_proxy*.xml");
            LOGGER.debug("##########################################" + resources.length);
            for (Resource resource : resources) {
                proxys.add((Proxys) u.unmarshal(resource.getInputStream()));
            }
        }
        catch (IOException e)
        {
            LOGGER.error("", e);
        } catch (JAXBException e)
        {
            LOGGER.error("", e);
        }

        if (!proxys.isEmpty() && !proxys.get(0).getList().isEmpty())
        {
            // 遍历proxys，注册
            for (DeviceProxy proxy : proxys.get(0).getList())
            {
                if (LOGGER.isDebugEnabled())
                {
                    LOGGER.debug(new StringBuilder("Load DeviceProxy:").append(" ")
                        .append(proxy.getDeviceType())
                        .append("|")
                        .append(proxy.getClassName())
                        .toString());
                }
                if (null != proxy.getClassName() && null != proxy.getDeviceType())
                {
                    try
                    {
                        boolean b =
                            deviceFactory.registerDeviceClass(proxy.getDeviceType(),
                                (Class<? extends IDevice>)Class.forName(proxy.getClassName()),
                                proxy.getStartVersion(),
                                proxy.getEndVersion());
                        if (LOGGER.isDebugEnabled())
                        {
                            LOGGER.debug(new StringBuffer("register to ").append(deviceFactory)
                                .append(" with type=")
                                .append(proxy.getDeviceType())
                                .append(" return ")
                                .append(b)
                                .toString());
                        }
                    }
                    catch (ClassNotFoundException e)
                    {
                        LOGGER.error(e.getMessage() + " not founded!");
                    }
                }
            }
        }
        else
        {
            LOGGER.warn("No deviceProxy found!");
        }
    }
    
    public static DeviceProxy getDeviceProxyConfig(String devType)
    {
        if (!proxys.isEmpty() && !proxys.get(0).getList().isEmpty())
        {
            for (DeviceProxy item : proxys.get(0).getList()) {
                if (item.getDeviceType().equals(devType)) {
                    return item;
                }
            }
        }
        LOGGER.warn("The devType[" + devType + "] cannot be found from configuration file");
        throw new IllegalArgumentException("The devType " + devType + " is invalid");
    }
    
    public static DeviceConfig deviceInstance2Device(DeviceInstance deviceInfo)
    {
        DeviceConfig device = new DeviceConfig();
        device.setDeviceId(deviceInfo.getDeviceId());
        device.setDeviceName(deviceInfo.getDeviceName());
        device.setDeviceType(deviceInfo.getDeviceType());
        device.setDeviceVersion(deviceInfo.getDeviceVersion());
        device.setLoginPwd(deviceInfo.getLoginPwd());
        device.setLoginUser(deviceInfo.getLoginUser());
        device.setServiceAccessPoint(deviceInfo.getServiceAccessPoint());
        IDeviceManager.DEV_CONN_MODE_TYPE type = deviceInfo.getConnMode();
        if (type.equals(IDeviceManager.DEV_CONN_MODE_TYPE.CONNECT_AUTOMATIC))
        {
            device.setConnMode("0");
        }
        else
        {
            device.setConnMode("1");
        }
        device.setReserver1(deviceInfo.getReserver1());
        device.setReserver2(deviceInfo.getReserver2());
        device.setAsDefault(deviceInfo.isAsDefault());
        return device;
    }
    
    public static Map<String, Object> deviceInstance2Map(DeviceInstance deviceInfo)
    {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("deviceId", deviceInfo.getDeviceId());
        map.put("deviceType", deviceInfo.getDeviceType());
        map.put("deviceName", deviceInfo.getDeviceName());
        map.put("deviceVersion", deviceInfo.getDeviceVersion());
        map.put("password", deviceInfo.getLoginPwd());
        map.put("username", deviceInfo.getLoginUser());
        map.put("sap", deviceInfo.getServiceAccessPoint());
        IDeviceManager.DEV_CONN_MODE_TYPE type = deviceInfo.getConnMode();
        if (type.equals(IDeviceManager.DEV_CONN_MODE_TYPE.CONNECT_AUTOMATIC))
        {
            map.put("connMode", "auto_connect");
        }
        else
        {
            map.put("connMode", "not_auto_connect");
        }
        map.put("reserver1", deviceInfo.getReserver1());
        map.put("reserver2", deviceInfo.getReserver2());
        
        return map;
    }
}
