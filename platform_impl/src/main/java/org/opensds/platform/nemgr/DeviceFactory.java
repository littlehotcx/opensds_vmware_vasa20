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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensds.platform.nemgr.itf.IDevice;
import org.opensds.platform.nemgr.itf.IDeviceClassRegister;

public class DeviceFactory implements IDeviceClassRegister
{
    private static final Logger LOGGER = LogManager.getLogger(DeviceFactory.class);
    
    private Map<String, List<DeviceClassInfo>> mapType2Class = new HashMap<String, List<DeviceClassInfo>>();
    
    @Override
    public boolean registerDeviceClass(String devType, Class<? extends IDevice> inst)
    {
        return registerDeviceClass(devType, inst, "", "");
    }
    
    @Override
    public boolean registerDeviceClass(String devType, Class<? extends IDevice> inst, String verStart, String verEnd)
    {
        List<DeviceClassInfo> clsInfos = mapType2Class.get(devType);
        if (null == clsInfos)
        {
            clsInfos = new LinkedList<DeviceClassInfo>();
            mapType2Class.put(devType, clsInfos);
        }
        
        int index = 0;
        for (DeviceClassInfo deviceClassInfo : clsInfos)
        {
            if (deviceClassInfo.getVerStart().compareToIgnoreCase(verStart) > 0)
            {
                index = clsInfos.indexOf(deviceClassInfo);
                break;
            }
        }
        DeviceClassInfo newClsInfo = new DeviceClassInfo();
        newClsInfo.setDevType(devType);
        newClsInfo.setVerStart(verStart);
        newClsInfo.setVerEnd(verEnd);
        newClsInfo.setInst(inst);
        clsInfos.add(index, newClsInfo);
        
        if (LOGGER.isDebugEnabled())
        {
            LOGGER.debug(new StringBuilder("add deviceClass ").append(devType)
                .append("|")
                .append(verStart)
                .append("|")
                .append(verEnd)
                .append(" to map"));
        }
        return true;
    }
    
    public IDevice createInstance(DeviceInstance devInfo)
    {
        List<DeviceClassInfo> clsInfos = mapType2Class.get(devInfo.getDeviceType());
        if (null == clsInfos)
        {
            LOGGER.debug("mapType2Class size=" + mapType2Class.size());
            LOGGER.warn("mapType2Class mgrId=" + this + " for " + devInfo.getDeviceType() + " is empty.");
            return null;
        }
        
        for (DeviceClassInfo clsInfo : clsInfos)
        {
            if (clsInfo.isVersionSupported(devInfo))
            {
                try
                {
                    Object[] params = new Object[1];
                    params[0] = devInfo.getServiceAccessPoint();
                    
                    @SuppressWarnings("unchecked")
                    Constructor<IDevice> con = (Constructor<IDevice>)clsInfo.getInst().getConstructor(String.class);
                    IDevice dev = con.newInstance(params);
                    return dev;
                }
                catch (InstantiationException e)
                {
                    LOGGER.error("", e);
                }
                catch (IllegalAccessException e)
                {
                    LOGGER.error("", e);
                }
                catch (NoSuchMethodException e)
                {
                    LOGGER.error("", e);
                }
                catch (IllegalArgumentException e)
                {
                    LOGGER.error("", e);
                }
                catch (InvocationTargetException e)
                {
                    LOGGER.error("", e);
                }
            }
        }
        
        LOGGER.warn(new StringBuilder("Find dev class in mapType2Class for ").append(devInfo.getDeviceType())
            .append("|")
            .append(devInfo.getDeviceName())
            .append(" failed"));
        return null;
    }
    
    private DeviceFactory inst;
    
    public DeviceFactory()
    {
        inst = this;
    }
    
    public  DeviceFactory getInstance()
    {
        return inst;
    }
}
