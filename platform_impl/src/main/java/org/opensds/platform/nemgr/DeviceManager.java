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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensds.platform.config.service.itf.IConfigObserver;
import org.opensds.platform.config.service.itf.IDeviceConfigService;
import org.opensds.platform.nemgr.base.NotifyCollector;
import org.opensds.platform.nemgr.callback.NemgrCallback;
import org.opensds.platform.nemgr.itf.IDeviceConnection;
import org.opensds.platform.nemgr.itf.IDeviceManager;
import org.opensds.platform.util.RedisUtil;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;

import org.opensds.platform.abnormalevent.itf.IAbnormalevent;
import org.opensds.platform.common.bean.config.DeviceConfig;
import org.opensds.platform.common.config.ConfigManager;
import org.opensds.platform.common.constants.ESDKErrorCodeConstant;
import org.opensds.platform.common.exception.SDKException;
import org.opensds.platform.common.utils.NumberUtils;
import org.opensds.platform.common.utils.VasaRedisKey;
import org.opensds.platform.config.SystemConfig;
import org.opensds.platform.jobs.RefreshDeviceConfigJob;
import org.opensds.platform.nemgr.conn.DeviceReconnector;
import org.opensds.platform.nemgr.itf.IDevice;

/**
 * 设备管理的实现类
 * 
 * @author j00160659
 * 
 */
public class DeviceManager implements IDeviceManager
{
    private static final Logger LOGGER = LogManager.getLogger(DeviceManager.class);
    
    private volatile String DEVICE_VERSION = "0";
    
    /**
     * 保存设备ID和设备信息的映射
     */
    private ConcurrentHashMap<String, DeviceInstance> mapId2Dev = new ConcurrentHashMap<String, DeviceInstance>();
    
    private DeviceInstance defaultDevice = null;
    
    private IAbnormalevent abnormaleventManager;
    
    private DeviceConnManager connMgr;
    
    private DeviceFactory deviceFactory;
    
    private DeviceReconnector reconnector;
    
    // quartz定时器
    private Scheduler scheduler;
    
    private NotifyCollector collector;
    
    private IDeviceConfigService deviceConfigService;
    
    private static Object refresh_device_lock = new Object();
    
    public DeviceConnManager getConnMgr()
    {
        return connMgr;
    }
    
    public void setConnMgr(DeviceConnManager connMgr)
    {
        this.connMgr = connMgr;
    }
    
    public DeviceFactory getDeviceFactory()
    {
        return deviceFactory;
    }
    
    public void setDeviceFactory(DeviceFactory deviceFactory)
    {
        this.deviceFactory = deviceFactory;
    }
    
    public DeviceReconnector getReconnector()
    {
        return reconnector;
    }
    
    public void setReconnector(DeviceReconnector reconnector)
    {
        this.reconnector = reconnector;
    }
    
    public IAbnormalevent getAbnormaleventManager()
    {
        return abnormaleventManager;
    }
    
    public void setAbnormaleventManager(IAbnormalevent abnormaleventManager)
    {
        this.abnormaleventManager = abnormaleventManager;
    }
    
    public NotifyCollector getCollector()
    {
        return collector;
    }
    
    public void setCollector(NotifyCollector collector)
    {
        this.collector = collector;
    }
    
    public IDeviceConfigService getDeviceConfigService()
    {
        return deviceConfigService;
    }
    
    public void setDeviceConfigService(IDeviceConfigService deviceConfigService)
    {
        this.deviceConfigService = deviceConfigService;
    }
    
    public Scheduler getScheduler()
    {
        return scheduler;
    }
    
    public void setScheduler(Scheduler scheduler)
    {
        this.scheduler = scheduler;
    }
    
    @Override
    public boolean addDevice(String devID, String devName, String devType, String devVer, String sap, String user,
        String pwd, String connMode, String reserver1, String reserver2, boolean isAsDefault)
    {
        DeviceInstance dev = mapId2Dev.get(devID);
        if (null == dev)
        {
            dev =
                new DeviceInstance(devID, devName, devType, devVer, sap, user, pwd, connMode, reserver1, reserver2,
                    isAsDefault);
            mapId2Dev.put(devID, dev);
            return true;
        }
        return false;
    }
    
    private boolean removeDevice(String devId)
    {
        DeviceInstance dev = mapId2Dev.get(devId);
        if (null != dev)
        {
            dev.destory();
            mapId2Dev.remove(devId);
        }
        return false;
    }
    
    public boolean closeConn(String devId)
    {
        DeviceInstance dev = getDeviceFromMap(devId);
        if (null == dev)
        {
            return false;
        }
        
        synchronized (dev)
        {
            dev.doDisconnect(connMgr, reconnector);
        }
        return true;
    }
    
    public boolean connect(String devId)
        throws SDKException
    {
        DeviceInstance dev = getDeviceFromMap(devId);
        if (null == dev)
        {
            return false;
        }
        
        synchronized (dev)
        {
            dev.getConnection(connMgr, reconnector);
        }
        return true;
    }
    
    public DeviceInstance getDeviceFromMap(String devID)
    {
    	checkDeviceUpdate();
        // 获取设备数据
        DeviceInstance result =  mapId2Dev.get(devID);
        if (null == result)
        {
            LOGGER.warn("devID=" + devID + " is not found");
        }
        return result;
    }
    
    public void checkDeviceUpdate(){
    	String deviceVersion = RedisUtil.getStringKeyValue(VasaRedisKey.device_refresh_verion_key);
    	if(null != deviceVersion && !deviceVersion.equals(DEVICE_VERSION)){
    		synchronized (refresh_device_lock) {
    			deviceVersion = RedisUtil.getStringKeyValue(VasaRedisKey.device_refresh_verion_key);
    			if(null != deviceVersion && deviceVersion.equals(DEVICE_VERSION)){
    				return;
    			}
    			refreshDevices();
    			DEVICE_VERSION = deviceVersion;
			}
    	}
    }
    
    @Override
	public IDevice getDeviceByDeviceId(String devID) 
    {
		DeviceInstance ins = getDeviceFromMap(devID);
		return ins.getDeviceItf();
	}

	@Override
    public boolean modifyDeviceUserInfo(String devId, String user, String pwd)
    {
        DeviceInstance dev = getDeviceFromMap(devId);
        if (null == dev)
        {
            LOGGER.warn("device does not exist");
            return false;
        }
        synchronized (dev)
        {
            dev.setUserInfo(user, pwd);
        }
        return true;
    }
    
    @Override
    public Object getDeviceServiceProxy(String devID, @SuppressWarnings("rawtypes") Class[] itfs)
        throws SDKException
    {
        Object serviceProxy = getServiceProxyFromDevice(itfs, getDeviceFromMap(devID));
        if (null == serviceProxy)
        {
            LOGGER.debug("code api not support, devId=" + devID);
            SDKException exception = new SDKException("code api not support");
            exception.setSdkErrCode(ESDKErrorCodeConstant.ERROR_CODE_API_NOT_SUPPORT);
            throw exception;
        }
        
        return serviceProxy;
    }
    
    @SuppressWarnings("rawtypes")
    private Object getServiceProxyFromDevice(Class[] itfs, DeviceInstance dev)
        throws SDKException
    {
        if (null == dev)
        {
            LOGGER.debug("device does not exist");
            SDKException exception = new SDKException("device does not exist");
            exception.setSdkErrCode(ESDKErrorCodeConstant.ERROR_CODE_DEVICE_DOES_NOT_EXIST);
            throw exception;
        }
        
        // 获取设备通讯信息
        return dev.getServiceProxy(itfs, connMgr, reconnector);
    }
    
    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public <T> T getDeviceServiceProxy(String devID, Class<T> itfCls)
        throws SDKException
    {
        Class[] itfs = new Class[] {itfCls};
        return (T)getDeviceServiceProxy(devID, itfs);
    }
    
    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public <T> T getDefaultDeviceServiceProxy(Class<T> itfCls)
        throws SDKException
    {
        Class[] itfs = new Class[] {itfCls};
        return (T)getDefaultDeviceServiceProxy(itfs);
    }
    
    @SuppressWarnings("rawtypes")
    @Override
    public Object getDefaultDeviceServiceProxy(Class[] itfCls)
        throws SDKException
    {
        return getServiceProxyFromDevice(itfCls, defaultDevice);
    }
    
    @Override
    public void setAsDefaultDevice(String devID)
    {
        defaultDevice = getDeviceFromMap(devID);
    }
    
    /**
     * 查询设备信息
     */
    @Override
    public List<Map<String, Object>> queryDeviceInfo(String deviceId)
    {
        DeviceInstance deviceInfo = getDeviceFromMap(deviceId);
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        if (null != deviceInfo)
        {
            list.add(DeviceConfigLoader.deviceInstance2Map(deviceInfo));
        }
        return list;
    }
    
    /**
     * 查询所有的接口信息
     */
    @Override
    public List<DeviceConfig> queryAllDeviceInfo()
    {
        DeviceInstance deviceInfo = null;
        List<DeviceConfig> devices = new ArrayList<DeviceConfig>();
        
        for (String key : mapId2Dev.keySet())
        {
            deviceInfo = mapId2Dev.get(key);
            devices.add(DeviceConfigLoader.deviceInstance2Device(deviceInfo));
        }
        return devices;
    }
    
    /**
     * 根据设备类型查询设备信息
     * @param devType
     * @return
     */
    @Override
    public List<DeviceConfig> queryDeviceInfosByType(String devType)
    {
        DeviceInstance deviceInfo = null;
        List<DeviceConfig> devices = new ArrayList<DeviceConfig>();
        
        for (String key : mapId2Dev.keySet())
        {
            deviceInfo = mapId2Dev.get(key);
            if (deviceInfo.getDeviceType().equals(devType))
            {
                devices.add(DeviceConfigLoader.deviceInstance2Device(deviceInfo));
            }
        }
        return devices;
    }
    
    /**
     * 初始化方法，解析配置文件
     */
    public void init()
    {
        // 加载系统密钥，更新系统密钥，更新xml配置文件
        LOGGER.info("DeviceManager init()");
        SystemConfig.init();
        DeviceConfigLoader.loadDeviceProxys(deviceFactory);
        LOGGER.info("begin get allDevices");
        List<DeviceConfig> devices = deviceConfigService.getAllDevices();
        LOGGER.info("end get allDevices, devices="+devices);
        if (null != devices)
        {
            // 遍历设备信息
            for (DeviceConfig device : devices)
            {
            	LOGGER.info("DeviceManager init(), device="+device);
                // 将设备添加到内存中
                addDevice(device.getDeviceId(),
                    device.getDeviceName(),
                    device.getDeviceType(),
                    device.getDeviceVersion(),
                    device.getServiceAccessPoint(),
                    device.getLoginUser(),
                    device.getLoginPwd(),
                    device.getConnMode(),
                    device.getReserver1(),
                    device.getReserver2(),
                    device.isAsDefault());
                if (device.isAsDefault())
                {
                    setAsDefaultDevice(device.getDeviceId());
                }
            }
        }
        
        //Following two ways will be picked one later
        //Way 1
        deviceConfigService.registerObserver(new IConfigObserver()
        {
            @Override
            public void doAction()
            {
                LOGGER.info("Devices configuration changed");
                refreshDevices();
            }
        });
        //Way 2
        startRefreshJob();
        
        registerCallback();
    }
    
    private void startRefreshJob()
    {
        try
        {
            JobDetail jobDetail = JobBuilder.newJob(RefreshDeviceConfigJob.class).withIdentity("file_monitor").build();
//            jobDetail.setName("file_monitor");
            jobDetail.getJobDataMap().put("deviceManager", this);
//            jobDetail.setJobClass(RefreshDeviceConfigJob.class);
            
            Trigger trigger =
                TriggerBuilder.newTrigger()
                    .withIdentity("monitor", "eSDK_Jobs")
                    .startNow()
                    .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .repeatMinutelyForever(NumberUtils.parseIntValue(ConfigManager.getInstance()
                            .getValue("fileMonitorInterval", "30")))
                        .repeatForever())
                    .build();
//                new SimpleTrigger("monitor", "eSDK_Jobs", new Date(), null, SimpleTrigger.REPEAT_INDEFINITELY,
//                    1000L * 60 * NumberUtils.parseIntValue(ConfigManager.getInstance().getValue("fileMonitorInterval",
//                        "30")));
            scheduler.scheduleJob(jobDetail, trigger);
        }
        catch (SchedulerException e)
        {
            LOGGER.error("", e);
        }
    }
    
    @Override
    public void refreshDevices()
    {
        List<DeviceConfig> devices = deviceConfigService.getAllDevices();
        LOGGER.info("refreshDevices devices="+devices);
        if (null == devices)
        {
            return;
        }
        
        ConcurrentHashMap<String, DeviceInstance> newMap = new ConcurrentHashMap<String, DeviceInstance>();
        DeviceInstance instance = null;
        for (DeviceConfig device : devices)
        {
            instance =
                new DeviceInstance(device.getDeviceId(), device.getDeviceName(), device.getDeviceType(),
                    device.getDeviceVersion(), device.getServiceAccessPoint(), device.getLoginUser(),
                    device.getLoginPwd(), device.getConnMode(), device.getReserver1(), device.getReserver2(),
                    device.isAsDefault());
            LOGGER.info("refreshDevices deviceId="+instance.getDeviceId()+",instance="+instance);
            newMap.put(instance.getDeviceId(), instance);
        }
        
        List<String> keys = new ArrayList<String>();
        
        Set<Entry<String, DeviceInstance>> removeSet = mapId2Dev.entrySet();
        for (Entry<String, DeviceInstance> es : removeSet)
        {
            if (newMap.containsValue(mapId2Dev.get(es.getKey())))
            {
                newMap.remove(es.getKey());
            }
            else
            {
                keys.add(es.getKey());
            }
        }
        
        for (String key : keys)
        {
            removeDevice(key);
            //将注册的通知去掉
            collector.unsubscribeNotify(key);
        }
        
        Set<Entry<String, DeviceInstance>> putSet = newMap.entrySet();
        for (Entry<String, DeviceInstance> es : putSet)
        {
            mapId2Dev.put(es.getKey(), newMap.get(es.getKey()));
            //新增设备要注册通知
            collector.subscribeNotify(es.getKey(), new NemgrCallback());
        }
    }
    
    public void registerCallback()
    {
        Set<Entry<String, DeviceInstance>> putSet = mapId2Dev.entrySet();
        for (Entry<String, DeviceInstance> es : putSet)
        {
            String id = es.getKey();
            collector.subscribeNotify(id, new NemgrCallback());
        }
    }
    
    @Override
    public void releaseConn(IDeviceConnection conn)
    {
        reconnector.removeDevice(conn);
        connMgr.removeFromKeepAliveSchedule(conn);
    }

}