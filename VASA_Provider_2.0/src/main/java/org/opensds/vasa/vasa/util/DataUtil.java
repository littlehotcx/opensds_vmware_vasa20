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
package org.opensds.vasa.vasa.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Semaphore;
import java.util.Set;
import java.util.TimeZone;

import org.opensds.vasa.common.MagicNumber;
import org.opensds.vasa.common.ProductSpeciality;
import org.opensds.vasa.common.PropertiesManager;
import org.opensds.vasa.domain.model.bean.DArray;

import org.opensds.vasa.domain.model.bean.DArrayFlowControl;
import org.opensds.vasa.domain.model.bean.DArrayIsLock;

import org.opensds.vasa.domain.model.bean.DArrayInfo;

import org.opensds.vasa.domain.model.bean.DFileSystem;
import org.opensds.vasa.domain.model.bean.DLun;
import org.opensds.vasa.domain.model.bean.DPort;
import org.opensds.vasa.domain.model.bean.DProcessor;
import org.opensds.vasa.domain.model.bean.DStorageCapability;
import org.opensds.vasa.domain.model.bean.IsmStoragePort;
import org.opensds.vasa.vasa.convert.VASAUtilDJConvert;
import org.opensds.vasa.vasa.internal.Event;
import org.opensds.vasa.vasa.internal.EventParamEntity;

import com.vmware.vim.vasa.v20.data.vvol.xsd.ProtocolEndpoint;
import com.vmware.vim.vasa.v20.data.vvol.xsd.RebindEvent;
import com.vmware.vim.vasa.v20.data.xsd.StorageEvent;
import com.vmware.vim.vasa.v20.data.xsd.UsageContext;

/**
 * <一些数据保存的类>
 *
 * @author xKF20991
 * @version V001R010C00
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public final class DataUtil {
    private static org.apache.logging.log4j.Logger LOGGER = org.apache.logging.log4j.LogManager
            .getLogger(DataUtil.class);

    private static final String ARRAY_ID_SET_KEY = "DATAUTIL_ARRAYID_KEY";

    private static final String ARRAY_MAP_KEY = "DATAUTIL_ARRAY_MAP_KEY";

    private static final String PROCESSOR_MAP_KEY = "DATAUTIL_PROCESSOR_MAP_KEY";

    private static final String PORT_MAP_KEY = "DATAUTIL_PORT_MAP_KEY";

    private static final String LUN_MAP_KEY = "DATAUTIL_LUN_MAP_KEY";

    private static final String FILESYSTEM_MAP_KEY = "DATAUTIL_FILESYSTEM_MAP_KEY";

    private static final String CAPABLITY_MAP_KEY = "DATAUTIL_CAPABLITY_MAP_KEY";

    private static final String EVENTFORVCENTER_MAP_KEY = "DATAUTIL_EVENTFORVCENTER_MAP_KEY";

    private static final String ALARMFORVCENTER_MAP_KEY = "DATAUTIL_ALARMFORVCENTER_MAP_KEY";

    private static final String STORAGEEVENTFORVCENTER_MAP_KEY = "DATAUTIL_STORAGEEVENTFORVCENTER_MAP_KEY";

    private static final String PECONFIGEVENTFORVCENTER_MAP_KEY = "DATAUTIL_PECONFIGEVENTFORVCENTER_MAP_KEY";

    private static final String TIMEZONES_MAP_KEY = "DATAUTIL_TIMEZONES_MAP_KEY";

    private static final String USAGECONTEXTS_LIST_ID_KEY = "DATAUTIL_USAGECONTEXTS_LIST_ID_KEY";

    private static final String USAGECONTEXTS_LIST_KEY = "DATAUTIL_USAGECONTEXTS_LIST_KEY";

    private static final String SAVEDLASTALARMID_MAP_KEY = "DATAUTIL_SAVEDLASTALARMID_MAP_KEY";

    private static final String SAVEDLASTEVENTID_MAP_KEY = "DATAUTIL_SAVEDLASTEVENTID_MAP_KEY";

    private static final String INTERNALLASTEVENTID_MAP_LEY = "DATAUTIL_INTERNALLASTEVENTID_MAP_KEY";

    private static final String SAVEDVCENTERFORMAXALARMID_MAP_KEY = "DATAUTIL_SAVEDVCENTERFORMAXALARMID_MAP_KEY";

    private static final String STORAGELUNS_MAP_KEY = "DATAUTIL_STORAGELUNS_MAP_KEY";

    private static final String HOSTINITIATORIDS_MAP_KEY = "DATAUTIL_HOSTINITIATORIDS_MAP_KEY";

    private static final String EVENTPARAMMAP_MAP_KEY = "DATAUTIL_EVENTPARAMMAP_MAP_KEY";

    private static final String USAGECONTEXT2LUNS_MAP_KEY = "DATAUTIL_USAGECONTEXT2LUNS_MAP_KEY";

    private static final String USAGECONTEXT2FILESYSTEMS_MAP_KEY = "DATAUTIL_USAGECONTEXT2FILESYSTEMS_MAP_KEY";

    private static final String SESSION2PES_MAP_KEY = "DATAUTIL_SESSION2PES_MAP_KEY";

    private static final String SESSION2HOSTIPS_MAP_KEY = "DATAUTIL_SESSION2HOSTIPS_MAP_KEY";

    private static final String OUTOFBANDPES_LIST_KEY = "DATAUTIL_OUTOFBANDPES_LIST_KEY";

    private static final String DEVICEPRODUCTSPECIALITY_MAP_KEY = "DATAUTIL_DEVICEPRODUCTSPECIALITY_MAP_KEY";

    private Map<String, DArrayInfo> arrayInfoMap;

    private Map<String, String> vasaInfoMap;

    private Map<String, List<StorageEvent>> configEventForvCenter;

    //PE配置事件单独用一个队列缓存
    private Map<String, List<StorageEvent>> peConfigEventForVenter;

    //ConfigAlarm事件
    private Map<String, List<Event>> configAlarmForvCenter;

    private static final String DEVICECONCURRENTLOCK_MAP_KEY = "DATAUTIL_DEVICECONCURRENTLOCK_MAP_KEY";

    //private static Integer MAX_ACCESS_COUNT = 64;

    private static Semaphore semaphore = null;

    private Map<String, DArrayIsLock> arrayIsLock;

    private Map<String, DArrayFlowControl> arrayFlowControl;

    private DataUtil() {

        arrayIsLock = new HashMap<String, DArrayIsLock>();
        setArrayIsLock(arrayIsLock);
        vasaInfoMap = new HashMap<String, String>();
        //MAX_ACCESS_COUNT = Integer.parseInt(ConfigManager.getInstance().getValue("vasa.rest.max.access.count"));
        //semaphore = new Semaphore(MAX_ACCESS_COUNT);
        //LOGGER.info("first the semaphore available permits : " + semaphore.availablePermits());
        arrayFlowControl = new HashMap<String, DArrayFlowControl>();

        setArrayInfoMap(new HashMap<String, DArrayInfo>());
        arrayInfoMap = new HashMap<String, DArrayInfo>();
        configEventForvCenter = new HashMap<String, List<StorageEvent>>();
        configAlarmForvCenter = new HashMap<String, List<Event>>();
        peConfigEventForVenter = new HashMap<String, List<StorageEvent>>();
    }

    private static class singletonHolder {
        private static final DataUtil instance = new DataUtil();
    }

    public DArrayFlowControl getArrayFlowControlByDevId(String devId) {
        return arrayFlowControl.get(devId);
    }

    public Map<String, DArrayFlowControl> getArrayFlowControl() {
        return arrayFlowControl;
    }

    public void setArrayFlowControl(Map<String, DArrayFlowControl> arrayFlowControl) {
        this.arrayFlowControl = arrayFlowControl;
    }

    /**
     * 方法 ： getInstance
     *
     * @return DataUtil 返回结果
     */
    public static DataUtil getInstance() {
        return singletonHolder.instance;
    }

    /**
     * 方法 ： destroyAllData
     */
    public synchronized void destroyAllData() {
        this.arrayIsLock.clear();
        this.arrayInfoMap.clear();
        this.configEventForvCenter.clear();
        this.peConfigEventForVenter.clear();
        RedisUtil.clearByKey(ARRAY_MAP_KEY);
        RedisUtil.clearByKey(PROCESSOR_MAP_KEY);
        RedisUtil.clearByKey(PORT_MAP_KEY);
        RedisUtil.clearByKey(LUN_MAP_KEY);
        RedisUtil.clearByKey(FILESYSTEM_MAP_KEY);
        RedisUtil.clearByKey(CAPABLITY_MAP_KEY);
        RedisUtil.clearByKey(PROCESSOR_MAP_KEY);
        RedisUtil.clearKeyAndValues(EVENTFORVCENTER_MAP_KEY);
        RedisUtil.clearKeyAndValues(ALARMFORVCENTER_MAP_KEY);
        RedisUtil.clearKeyAndValues(STORAGEEVENTFORVCENTER_MAP_KEY);
        RedisUtil.clearKeyAndValues(PECONFIGEVENTFORVCENTER_MAP_KEY);
        RedisUtil.clearByKey(USAGECONTEXTS_LIST_ID_KEY);
        RedisUtil.clearByKey(USAGECONTEXTS_LIST_KEY);
        RedisUtil.clearByKey(SAVEDLASTALARMID_MAP_KEY);
        RedisUtil.clearByKey(SAVEDLASTEVENTID_MAP_KEY);
        RedisUtil.clearByKey(INTERNALLASTEVENTID_MAP_LEY);
        RedisUtil.clearKeyAndValues(SAVEDVCENTERFORMAXALARMID_MAP_KEY);
        RedisUtil.clearKeyAndValues(STORAGELUNS_MAP_KEY);
        RedisUtil.clearByKey(INTERNALLASTEVENTID_MAP_LEY);
        RedisUtil.clearByKey(EVENTPARAMMAP_MAP_KEY);
        RedisUtil.clearByKey(USAGECONTEXT2LUNS_MAP_KEY);
        RedisUtil.clearByKey(FILESYSTEM_MAP_KEY);
        RedisUtil.clearByKey(SESSION2PES_MAP_KEY);
        RedisUtil.clearByKey(OUTOFBANDPES_LIST_KEY);
    }

    public Map<String, List<Event>> getConfigAlarmForvCenter() {
        return configAlarmForvCenter;
    }

    public void setConfigAlarmForvCenter(String vc, List<Event> configAlarmEvents) {
        this.configAlarmForvCenter.put(vc, configAlarmEvents);
    }

    public Map<String, List<StorageEvent>> getConfigEventForvCenter() {
        return configEventForvCenter;
    }

    public void setConfigEventForvCenter(String vc, List<StorageEvent> configEventForvCenter) {
        this.configEventForvCenter.put(vc, configEventForvCenter);
    }

    public Semaphore getSemaphore() {
        return semaphore;
    }

    /**
     * 方法 ： getCapablityM
     *
     * @param id 方法参数：id
     * @return StorageCapability 返回结果
     */
    public DStorageCapability getCapablity(String id) {
        return RedisApi.getValue2RedisMap(CAPABLITY_MAP_KEY, id, DStorageCapability.class);
    }

    /**
     * 方法 ： setCapablity
     *
     * @param id              方法参数：id
     * @param storageCapacity 方法参数：storageCapacity
     */
    public void setCapablity(String id, DStorageCapability storageCapacity) {
        /*
         * DStorageCapability capacity = this.capablityMap.get(id); if (null !=
         * capacity) { this.capablityMap.remove(id); }
         */
        RedisApi.putValue2RedisMap(CAPABLITY_MAP_KEY, id, storageCapacity);
    }

    /**
     * 方法 ： addArray
     *
     * @param paramString       方法参数：paramString
     * @param paramStorageArray 方法参数：paramStorageArray
     */
    public void addArray(String paramString, DArray paramStorageArray) {
        RedisApi.putValue2RedisMap(ARRAY_MAP_KEY, paramString, paramStorageArray);
    }

    /**
     * 方法 ： getArray
     *
     * @param paramString 方法参数：paramString
     * @return StorageArray 返回结果
     */
    public DArray getArray(String paramString) {
        return RedisApi.getValue2RedisMap(ARRAY_MAP_KEY, paramString, DArray.class);
    }

    /**
     * 方法 ： addProcesor
     *
     * @param paramString           方法参数：paramString
     * @param paramStorageProcessor 方法参数：paramStorageProcessor
     */
    public void addProcesor(String paramString, DProcessor paramStorageProcessor) {
        RedisApi.putValue2RedisMap(PROCESSOR_MAP_KEY, paramString, paramStorageProcessor);
    }

    /**
     * 方法 ： getProcesor
     *
     * @param paramString 方法参数：paramString
     * @return StorageProcessor 返回结果
     */
    public DProcessor getProcesor(String paramString) {
        return RedisApi.getValue2RedisMap(PROCESSOR_MAP_KEY, paramString, DProcessor.class);
    }

    /**
     * 方法 ： addPort
     *
     * @param paramString      方法参数：paramString port uuid
     * @param paramStoragePort 方法参数：paramStoragePort
     */
    public void addPort(String paramString, DPort paramStoragePort) {
        synchronized (LockConstant.LOCK_DATAUTIL_PORT_MAP) {
            RedisApi.putValue2RedisMap(PORT_MAP_KEY, paramString, paramStoragePort);
        }
    }

    /**
     * 方法 ： getPort
     *
     * @param paramString 方法参数：paramString
     * @return StoragePort 返回结果
     */
    public DPort getPort(String paramString) {
        synchronized (LockConstant.LOCK_DATAUTIL_PORT_MAP) {
            return RedisApi.getValue2RedisMap(PORT_MAP_KEY, paramString, IsmStoragePort.class);
        }
    }

    /**
     * getPortByEngAndCard 根据引擎和板载卡id获取端口
     *
     * @param arrayid      array uuid StorageArray:XXXXX
     * @param controllerid controllerid format 0A
     * @param cardid       caredid format 1
     * @param portid       portid format 3
     * @return DPort
     */
    public DPort getPortByEngAndCard(String arrayid, String controllerid, String cardid, String portid) {
        LOGGER.debug("arrayid:" + arrayid + " controllerid:" + controllerid + " cardid" + cardid + " portid" + portid);
        synchronized (LockConstant.LOCK_DATAUTIL_PORT_MAP) {

            Map<String, IsmStoragePort> portMap = RedisApi.getValueAsMapWithBean2RedisMap(PORT_MAP_KEY,
                    IsmStoragePort.class);

            DPort resultPort = null;
            Iterator<Entry<String, IsmStoragePort>> it = portMap.entrySet().iterator();
            String key;
            DPort value;
            Entry<String, IsmStoragePort> entry;
            String portValue;
            String controllerID;
            int cardIdCaled;
            int portidCaled;
            while (it.hasNext()) {
                entry = it.next();
                key = entry.getKey();
                LOGGER.debug("port key is " + key);
                if (!arrayid.contains(key.substring(0, key.indexOf(":")))) {
                    continue;
                }
                // 查看portid 是否一致
                value = (DPort) entry.getValue();
                portValue = value.getUniqueIdentifier();
                portValue = portValue.substring(portValue.lastIndexOf(":") + 1);

                // check processor
                if (value instanceof IsmStoragePort) {
                    controllerID = ((IsmStoragePort) value).getProcessorUniquIdentifier();
                    LOGGER.debug("Contoller id from cache is " + controllerID);
                    if (!controllerID.substring(controllerID.length() - MagicNumber.INT2)
                            .equalsIgnoreCase(controllerid)) {
                        continue;
                    }
                }
                // check card
                cardIdCaled = VASAUtilDJConvert.getCardIdByPortId(portValue);
                LOGGER.debug("Card id caled is :" + cardIdCaled);
                if (!cardid.equals("" + cardIdCaled)) {
                    continue;
                }
                // check portid
                portidCaled = VASAUtilDJConvert.getPortNo(portValue);
                LOGGER.debug("Prot id from cache is :" + portidCaled);
                if (!portid.equals("" + portidCaled)) {
                    continue;
                } else {
                    resultPort = value;
                }
            }
            return null;
        }
    }

    /**
     * 方法 ： addLun
     *
     * @param paramString     方法参数：paramString
     * @param paramStorageLun 方法参数：paramStorageLun
     */
    public void addLun(String paramString, DLun paramStorageLun) {
        RedisApi.putValue2RedisMap(LUN_MAP_KEY, paramString, paramStorageLun);
    }

    /**
     * 方法 ： getLun
     *
     * @param paramString 方法参数：paramString
     * @return StorageLun 返回结果
     */
    public DLun getLun(String paramString) {
        return RedisApi.getValue2RedisMap(LUN_MAP_KEY, paramString, DLun.class);
    }

    /**
     * 方法 ： getEventForVcenter
     *
     * @return Map<String, List < Event>> 返回结果
     */
    public Map<String, List<Event>> getEventForVcenter() {
        return RedisApi.getValueAsMapWithList2RedisMap(EVENTFORVCENTER_MAP_KEY, Event.class);
    }

    /**
     * 方法 ： setEventForVcenter
     *
     * @param vc     方法参数：vc
     * @param events 方法参数：events
     */
    public void setEventForVcenter(String vc, List<Event> events) {
        RedisApi.putValue2RedisMap(EVENTFORVCENTER_MAP_KEY, vc, events);
    }

    /**
     * 方法 ： getStorageEventForVcenter
     *
     * @return Map<String, List < StorageEvent>> 返回结果
     */
    public Map<String, List<StorageEvent>> getStorageEventForVcenter() {
        Map<String, List<StorageEvent>> result = new HashMap<>();
        Map<String, List<RebindEvent>> rebindEventMap = RedisApi.getValueAsMapWithList2RedisMap(STORAGEEVENTFORVCENTER_MAP_KEY, RebindEvent.class);
        Set<String> keySet = rebindEventMap.keySet();
        for (String key : keySet) {
            List<StorageEvent> events = new ArrayList<>();
            List<RebindEvent> list = rebindEventMap.get(key);
            for (RebindEvent rebindEvent : list) {
                if (rebindEvent.getVvolId().size() != 0) {
                    events.add(rebindEvent);
                } else {
                    events.add(JsonUtil.parseJson2Bean(JsonUtil.parse2JsonString(rebindEvent), StorageEvent.class));
                }
            }
            result.put(key, events);
        }
        return result;
    }


    /**
     * 方法 ： setStorageEventForVcenter
     *
     * @param vc     方法参数：vc
     * @param events 方法参数：events
     */
    public void setStorageEventForVcenter(String vc, List<StorageEvent> events) {
        RedisApi.putValue2RedisMap(STORAGEEVENTFORVCENTER_MAP_KEY, vc, events);
    }

    /**
     * 方法 ： getPeConfigEventForVcenter
     *
     * @return Map<String, List < StorageEvent>> 返回结果
     */
	/*
	public Map<String, List<StorageEvent>> getPeConfigEventForVcenter() {
		return RedisApi.getValueAsMapWithList2RedisMap(PECONFIGEVENTFORVCENTER_MAP_KEY, StorageEvent.class);
	}
	*/
    public Map<String, List<StorageEvent>> getPeConfigEventForVcenter() {
        return peConfigEventForVenter;
    }

    /**
     * 方法 ： setPeConfigEventForVcenter
     *
     * @param vc     方法参数：vc
     * @param events 方法参数：events
     */
	/*
	public void setPeConfigEventForVcenter(String vc, List<StorageEvent> events) {
		RedisApi.putValue2RedisMap(PECONFIGEVENTFORVCENTER_MAP_KEY, vc, events);
	}
	*/
    public void setPeConfigEventForVcenter(String vc, List<StorageEvent> events) {
        this.peConfigEventForVenter.put(vc, events);
    }

    // /**
    // * 方法 ： getSubscribeEventForVcenter
    // *
    // * @return Map<String, List<Event>> 返回结果
    // */
    // public synchronized Map<String, List<String>>
    // getSubscribeEventForVcenter()
    // {
    // return subscribeEventForVcenter;
    // }
    //
    // /**
    // * 方法 ： setSubscribeEventForVcenter
    // *
    // * @param vc 方法参数：vc
    // * @param events 方法参数：events
    // */
    // public synchronized void setSubscribeEventForVcenter(String vc,
    // List<String> subscribeEvents)
    // {
    // this.subscribeEventForVcenter.put(vc, subscribeEvents);
    // }

    /**
     * 方法 ： getAlarmForVcenter
     *
     * @return Map<String, List < Event>> 返回结果
     */
    public Map<String, List<Event>> getAlarmForVcenter() {
        return RedisApi.getValueAsMapWithList2RedisMap(ALARMFORVCENTER_MAP_KEY, Event.class);
    }

    // public Map<String, List<?>> convert(Map<String, List<Object>>
    // target,String clzName)
    // {
    // Map<String, List<?>> result = new HashMap<>();
    // for(Map.Entry<String, List<Object>> entry : target.entrySet())
    // {
    // List<?> list = new ArrayList<>();
    // for(Object object : entry.getValue())
    // {
    // list.add(Class.forName(clzName).cast(object));
    // }
    //
    // result.put(entry.getKey(), list);
    // }
    //
    // return result;
    // }

    /**
     * 方法 ： setAlarmForVcenter
     *
     * @param vcId   方法参数：vcId
     * @param alrams 方法参数：alrams
     */
    public void setAlarmForVcenter(String vcId, List<Event> alrams) {
        RedisApi.putValue2RedisMap(ALARMFORVCENTER_MAP_KEY, vcId, alrams);
    }

    public void removeAlarmForVcenter(String vcId) {
        RedisUtil.RedisMap.removeKey(ALARMFORVCENTER_MAP_KEY, vcId);
    }

    /**
     * 方法： setDeviceTrafficControl
     *
     * @param arrayId              方法参数： arrayId
     * @param deviceTrafficControl 方法参数： alarms
     */
    public synchronized void setDeviceTrafficControl(String devId, NDeviceTrafficControl deviceTrafficControl) {
        RedisApi.putValue2RedisMap(DEVICECONCURRENTLOCK_MAP_KEY, devId, deviceTrafficControl);
    }

    /**
     * 方法： getDeviceTrafficControl
     *
     * @param arrayId 方法参数： arrayId
     */
    public synchronized NDeviceTrafficControl getDeviceTrafficControl(String devId) {
        return RedisApi.getValue2RedisMap(DEVICECONCURRENTLOCK_MAP_KEY, devId, NDeviceTrafficControl.class);
    }


    /**
     * 方法 ： getUsageContextUUIDs
     *
     * @return List<String> 返回结果
     */
    public synchronized List<String> getUsageContextUUIDs() {

        return RedisApi.getListOfBean2RedisList(USAGECONTEXTS_LIST_ID_KEY, String.class);
    }

    /**
     * 方法 ： addUsageContextUUID
     *
     * @param uc 方法参数：uc
     */
    public synchronized void addUsageContextUUID(String uc, UsageContext usageContext) {
        if (getUsageContextUUIDs().contains(uc)) {
            updateUsageContext(usageContext);
            return;
        }

        RedisApi.putValue2RedisList(USAGECONTEXTS_LIST_ID_KEY, uc);
        RedisApi.putValue2RedisList(USAGECONTEXTS_LIST_KEY, usageContext);
    }

    private void updateUsageContext(UsageContext usageContext) {
        for (UsageContext cachedUc : getUsageContexts()) {
            if (VASAUtil.getUcUUID(cachedUc).equalsIgnoreCase(VASAUtil.getUcUUID(usageContext))) {
                if (usageContext.getSubscribeEvent() != null && usageContext.getSubscribeEvent().size() != 0) {
                    List<String> subscribeEvents = cachedUc.getSubscribeEvent();
                    subscribeEvents.clear();
                    subscribeEvents.addAll(usageContext.getSubscribeEvent());
                }
                break;
            }
        }
    }

    public synchronized List<UsageContext> getUsageContexts() {
        return RedisApi.getListOfBean2RedisList(USAGECONTEXTS_LIST_KEY, UsageContext.class);
    }

    /**
     * 方法 ： getSavedLastAlarmID
     *
     * @param ucUUID 方法参数：ucUUID
     * @return Long 返回结果
     */
    public Long getSavedLastAlarmID(String ucUUID) {
        return RedisApi.getValue2RedisMap(SAVEDLASTALARMID_MAP_KEY, ucUUID, Long.class);
    }

    /**
     * 方法 ： getSavedLastAlarmID
     *
     * @return Map<String, Long> 返回结果
     */
    public Map<String, Long> getSavedLastAlarmID() {

        return RedisApi.getValueAsMapWithBean2RedisMap(SAVEDLASTALARMID_MAP_KEY, Long.class);
    }

    /**
     * 方法 ： setSavedLastAlarmID
     *
     * @param uc          方法参数：uc
     * @param lastAlarmID 方法参数：lastAlarmID
     */
    public void setSavedLastAlarmID(String uc, long lastAlarmID) {
        RedisApi.putValue2RedisMap(SAVEDLASTALARMID_MAP_KEY, uc, lastAlarmID);
    }

    public void removeSavedLastAlramId(String uc) {
        RedisUtil.RedisMap.removeKey(SAVEDLASTALARMID_MAP_KEY, uc);
    }

    /**
     * 方法 ： getSavedLastEventID
     *
     * @return Map<String, Long> 返回结果
     */
    public Map<String, Long> getSavedLastEventID() {

        return RedisApi.getValueAsMapWithBean2RedisMap(SAVEDLASTEVENTID_MAP_KEY, Long.class);
    }

    /**
     * 方法 ： setSavedLastEventID
     *
     * @param uc          方法参数：uc
     * @param lastEventId 方法参数：lastEventId
     */
    public void setSavedLastEventID(String uc, long lastEventId) {
        RedisApi.putValue2RedisMap(SAVEDLASTEVENTID_MAP_KEY, uc, lastEventId);
    }

    /**
     * 方法 ： getInternalLastEventID
     *
     * @return Map<String, Long> 返回结果
     */
    public Map<String, Long> getInternalLastEventID() {

        return RedisApi.getValueAsMapWithBean2RedisMap(INTERNALLASTEVENTID_MAP_LEY, Long.class);
    }

    /**
     * 方法 ： setInternalLastEventID
     *
     * @param uc          方法参数：uc
     * @param lastEventId 方法参数：lastEventId
     */
    public void setInternalLastEventID(String uc, long lastEventId) {
        RedisApi.putValue2RedisMap(INTERNALLASTEVENTID_MAP_LEY, uc, lastEventId);
    }

    /**
     * 方法 ： getTimeZone
     *
     * @param deviceID 方法参数：deviceID
     * @return TimeZone 返回结果
     */
    public TimeZone getTimeZone(String deviceID) {
        TimeZone tZone = RedisApi.getValue2RedisMap(TIMEZONES_MAP_KEY, deviceID, TimeZone.class);
        if (null == tZone) {
            return TimeZone.getDefault();
        }

        return tZone;
    }

    /**
     * 方法 ： setTimeZone
     *
     * @param deviceID 方法参数：deviceID
     * @param timeZone 方法参数：timeZone
     */
    public void setTimeZone(String deviceID, TimeZone timeZone) {
        RedisApi.putValue2RedisMap(TIMEZONES_MAP_KEY, deviceID, timeZone);
    }

    /**
     * 方法 ： getSession2PEs
     *
     * @return Map<String, List < ProtocolEndpoint>> 返回结果
     */
    public Map<String, List<ProtocolEndpoint>> getSession2PEs() {

        return RedisApi.getValueAsMapWithList2RedisMap(SESSION2PES_MAP_KEY, ProtocolEndpoint.class);
    }

    /**
     * 方法 ： removePEsBySessionId
     *
     * @return void 返回结果
     */
    public void removePEsBySessionId(String sessionId) {
        if (null != sessionId) {
            RedisUtil.RedisMap.removeKey(SESSION2PES_MAP_KEY, sessionId);
        }
    }

    public List<ProtocolEndpoint> getOutOfBandPes() {
        synchronized (LockConstant.LOCK_DATAUTIL_OUTOFBANDPES) {

            return RedisApi.getListOfBean2RedisList(OUTOFBANDPES_LIST_KEY, ProtocolEndpoint.class);
        }
    }

    public void setOutOfBandPes(List<ProtocolEndpoint> outOfBandPes) {
        synchronized (LockConstant.LOCK_DATAUTIL_OUTOFBANDPES) {
            RedisApi.setValue2RedisList(OUTOFBANDPES_LIST_KEY, outOfBandPes);
        }
    }

    /**
     * 方法 ： getPEsBySessionId
     *
     * @param sessionid 方法参数：sessionid
     * @return List<ProtocolEndpoint> 返回结果
     */
    public List<ProtocolEndpoint> getPEsBySessionId(String sessionid) {

        return getSession2PEs().get(sessionid);
    }

    /**
     * 方法 ： setPEsForSessionId
     *
     * @param sessionid     方法参数：sessionid
     * @param listOfHostPEs 方法参数：listOfHostPEs
     */
    public void setPEsForSessionId(String sessionid, List<ProtocolEndpoint> listOfHostPEs) {
        RedisApi.putValue2RedisMap(SESSION2PES_MAP_KEY, sessionid, listOfHostPEs);
    }

    public void removeSavedVcenterForMaxAlarmId(String vcId) {
        RedisUtil.RedisMap.removeKey(SAVEDVCENTERFORMAXALARMID_MAP_KEY, vcId);
    }

//	public synchronized void setHostIps(List<String> HostIps) {
//        RedisApi.setValue2RedisList(SESSION2HOSTIPS_MAP_KEY, HostIps);
//    }


    //	public synchronized List<String> getHostIps() {
//       return RedisApi.getListOfBean2RedisList(SESSION2HOSTIPS_MAP_KEY, String.class);
//    }
//	
//	
    public synchronized void setHostIpsOfEsxHost(String hostId, List<String> HostIps) {
        RedisApi.putValue2RedisMap(SESSION2HOSTIPS_MAP_KEY, hostId, HostIps);
    }


    public synchronized List<String> getHostIpsByEsxHostId(String hostId) {
        return getAllHostIps().get(hostId);
    }

    public synchronized Map<String, List<String>> getAllHostIps() {
        return RedisApi.getValueAsMapWithList2RedisMap(SESSION2HOSTIPS_MAP_KEY, String.class);
    }


    public List<ProtocolEndpoint> getOutOfBandPes2() {
        synchronized (LockConstant.LOCK_DATAUTIL_OUTOFBANDPES) {

            return RedisApi.getListOfBean2RedisList(OUTOFBANDPES_LIST_KEY, ProtocolEndpoint.class);
        }
    }

    public void setOutOfBandPes2(List<ProtocolEndpoint> outOfBandPes) {
        synchronized (LockConstant.LOCK_DATAUTIL_OUTOFBANDPES) {
            RedisApi.setValue2RedisList(OUTOFBANDPES_LIST_KEY, outOfBandPes);
        }
    }


    /**
     * 方法 ： getSavedVcenterForMaxAlarmID
     *
     * @param vcId 方法参数：vcId
     * @return Map<String, Long> 返回结果
     */
    public Map<String, Long> getSavedVcenterForMaxAlarmID(String vcId) {
        return RedisApi.getValueAsMapWithMap2RedisMap(SAVEDVCENTERFORMAXALARMID_MAP_KEY, vcId, Long.class);
    }

    /**
     * 方法 ： putVcenterForMaxAlarmID
     *
     * @param vcenId              方法参数：vcenId
     * @param maxAlarmIdForDevice 方法参数：maxAlarmIdForDevice
     */
    public void putVcenterForMaxAlarmID(String vcenId, Map<String, Long> maxAlarmIdForDevice) {
        RedisApi.putValue2RedisMap(SAVEDVCENTERFORMAXALARMID_MAP_KEY, vcenId, maxAlarmIdForDevice);
    }

    // /**
    // * 方法 ： getAllEvents
    // *
    // * @return List<Event> 返回结果
    // */
    // public synchronized List<Event> getAllEvents()
    // {
    // return allEvents;
    // }
    //
    // /**
    // * 方法 ： setAllEvents
    // *
    // * @param allEvents 方法参数：allEvents
    // */
    // public synchronized void setAllEvents(List<Event> allEvents)
    // {
    // if (null != allEvents)
    // {
    // this.allEvents.clear();
    // this.allEvents.addAll(allEvents);
    // }
    // }

    /**
     * 方法 ： getStorageLuns
     *
     * @return Map<String, List < StorageLun>> 返回结果
     */
    public Map<String, List<DLun>> getStorageLuns() {

        return RedisApi.getValueAsMapWithList2RedisMap(STORAGELUNS_MAP_KEY, DLun.class);
    }

    /**
     * 方法 ： getStorageLunsByUsageContext
     *
     * @param deviceID 方法参数：usageContext
     * @return List<StorageLun> 返回结果
     */
    public List<DLun> getStorageLunsByDeviceID(String deviceID) {

        return getStorageLuns().get(deviceID);

    }

    /**
     * 方法 ： setStorageLuns
     *
     * @param deviceID      方法参数：usageContext
     * @param storageLunsIn 方法参数：storageLunsIn
     */
    public void setStorageLuns(String deviceID, List<DLun> storageLunsIn) {
        if (null == storageLunsIn || null == deviceID) {
            return;
        }

        RedisApi.putValue2RedisMap(STORAGELUNS_MAP_KEY, deviceID, storageLunsIn);
    }

    // /**
    // * 方法 ： getAllAlarms
    // *
    // * @return List<Event> 返回结果
    // */
    // public List<Event> getAllAlarms()
    // {
    // synchronized (LOCK)
    // {
    // return allAlarms;
    // }
    // }
    //
    // /**
    // * 方法 ： setAllAlarms
    // *
    // * @param allAlarms 方法参数：allAlarms
    // */
    // public void setAllAlarms(List<Event> allAlarms)
    // {
    // synchronized (LOCK)
    // {
    // this.allAlarms = allAlarms;
    // }
    // }

    /**
     * <设置本地IP>
     *
     * @param localIP
     *            [参数说明]
     *
     * @see [类、类#方法、类#成员]
     */
    /*
     * public synchronized void setLocalIP(String localIP) { this.localIP =
     * localIP; }
     */

    /**
     * <设置本地IP>
     *
     * @return String 返回结果
     * @see [类、类#方法、类#成员]
     */
    /*
     * public synchronized String getLocalIP() { return this.localIP; }
     */
    public String getDeviceProductSpeciality(String deviceID) {
        if (null != deviceID) {
            return RedisApi.getValue2RedisMap(DEVICEPRODUCTSPECIALITY_MAP_KEY, deviceID, String.class);
        }

        return null;
    }

    /**
     * 对deviceProductSpeciality进行赋值
     *
     * @param deviceID          设备ID
     * @param productSpeciality 对deviceProductSpeciality进行赋值
     */
    public void setDeviceProductSpeciality(String deviceID, String productSpeciality) {
        if (null != productSpeciality) {
            RedisApi.putValue2RedisMap(DEVICEPRODUCTSPECIALITY_MAP_KEY, deviceID, productSpeciality);
        }
    }

    public boolean isDeviceSupportSPeciality(String arrayid, ProductSpeciality speciality) {
        String productSpecialities = getDeviceProductSpeciality(arrayid);
        if (null != productSpecialities) {
            return productSpecialities.contains(speciality.name());
        }

        return false;
    }

    /**
     * <初始化全局数据和从配置中获取配置的阵列信息>
     *
     * @see [类、类#方法、类#成员]
     */
    public synchronized void init() {

        List<String> lines = PropertiesManager.getInstance().getLines();

        String[] split = null;
        EventParamEntity entity = null;
        for (String line : lines) {
            split = line.split("=");
            if (split.length == MagicNumber.INT2) {
                entity = VASAUtil.parseEventParamEntity(split[0], "en");
                RedisApi.putValue2RedisMap(EVENTPARAMMAP_MAP_KEY, RedisConvert.convertBean2JsonString(entity),
                        split[1]);
            }
        }
        LOGGER.info("Succ load count:" + RedisUtil.RedisMap.getSize(EVENTPARAMMAP_MAP_KEY));

    }

    // public static void main(String[] args)
    // {
    // DataUtil dataUtil = DataUtil.getInstance();
    // dataUtil.init();
    // }

    /**
     * 返回 hostInitiatorIds
     *
     * @return 返回 hostInitiatorIds
     */
    public Map<String, String[]> getHostInitiatorIds() {

        return RedisApi.getValueAsMapWithBean2RedisMap(HOSTINITIATORIDS_MAP_KEY, String[].class);
    }

    /**
     * 对hostInitiatorIds进行赋值
     *
     * @param hostInitiatorId 对hostInitiatorIds进行赋值
     * @param ucUUID          ucID，用于保存一些vcenter的信息
     */
    public void setHostInitiatorIds(String ucUUID, String[] hostInitiatorId) {
        if (null != ucUUID && null != hostInitiatorId) {
            RedisApi.putValue2RedisMap(HOSTINITIATORIDS_MAP_KEY, ucUUID, hostInitiatorId);
        }
    }

    /**
     * getParamKey
     *
     * @param e EventParamEntity
     * @return 对应的描述信息
     */
    public String getParamKey(EventParamEntity e) {

        return RedisApi.getValue2RedisMap(EVENTPARAMMAP_MAP_KEY, RedisConvert.convertBean2JsonString(e), String.class);
    }

    /**
     * 将LUNs 添加到指定的usagecontext
     *
     * @param luns             DLun数组
     * @param usageContextUUID usagecontext 的唯一标识
     */
    public void addLunsToUsageContext(List<DLun> luns, String usageContextUUID) {
        if (luns == null || luns.size() == 0 || usageContextUUID == null) {
            return;
        }
        for (DLun lun : luns) {
            addLunToUsageContext(lun, usageContextUUID);
        }
    }

    /**
     * 将lun添加到指定的usgaecontext
     *
     * @param lun              storageLun
     * @param usageContextUUID usageContextUUID
     */
    public void addLunToUsageContext(DLun lun, String usageContextUUID) {
        LOGGER.debug("addLunToUsageContext UniqueIdentifier=" + lun.getUniqueIdentifier());
        addLunToUsageContext(lun.getUniqueIdentifier(), usageContextUUID);
    }

    /**
     * 将lun添加到指定的usgaecontext
     *
     * @param lunid            lunuuid
     * @param usageContextUUID usageContextUUID
     */
    public void addLunToUsageContext(String lunid, String usageContextUUID) {
        synchronized (LockConstant.LOCK_DATAUTIL_USAGECONTEXT_LUNS) {
            Set<String> existLuns = RedisApi.getValueAsSetWithBean2RedisMap(USAGECONTEXT2LUNS_MAP_KEY, usageContextUUID,
                    String.class);
            if (existLuns == null) {
                existLuns = new HashSet<String>(0);
            }
            existLuns.add(lunid);
            RedisApi.putValue2RedisMap(USAGECONTEXT2LUNS_MAP_KEY, usageContextUUID, existLuns);
        }
    }

    public void addFileSystemsToUsageContext(DFileSystem[] fileSystems, String usageContextUUID) {
        if (fileSystems == null || fileSystems.length == 0 || usageContextUUID == null) {
            return;
        }
        for (DFileSystem fileSystem : fileSystems) {
            addFileSystemToUsageContext(fileSystem, usageContextUUID);
        }
    }

    public void addFileSystemToUsageContext(DFileSystem fileSystem, String usageContextUUID) {
        addFileSystemToUsageContext(fileSystem.getUniqueIdentifier(), usageContextUUID);
    }

    public void addFileSystemToUsageContext(String fsId, String usageContextUUID) {
        Set<String> existFileSystems = RedisApi.getValueAsSetWithBean2RedisMap(USAGECONTEXT2FILESYSTEMS_MAP_KEY,
                usageContextUUID, String.class);
        if (existFileSystems == null) {
            existFileSystems = new HashSet<String>(0);
        }
        existFileSystems.add(fsId);
        RedisApi.putValue2RedisMap(USAGECONTEXT2FILESYSTEMS_MAP_KEY, usageContextUUID, existFileSystems);
    }

    /**
     * 根据指定的usgaecontext uuid 获取属于该usagecontext的LUN的集合（LUN UUIDS）
     *
     * @param contextUUID context uuuid
     * @return 属于该usagecontext的LUN的集合
     */
    public Set<String> getLunByUsageContext(String contextUUID) {
        synchronized (LockConstant.LOCK_DATAUTIL_USAGECONTEXT_LUNS) {
            Set<String> existLuns = RedisApi.getValueAsSetWithBean2RedisMap(USAGECONTEXT2LUNS_MAP_KEY, contextUUID,
                    String.class);

            if (existLuns == null) {
                return new HashSet<String>();
            }

            return existLuns;
        }
    }

    public void addFileSystems(String paramString, DFileSystem storageFileSystem) {
        /*
         * synchronized (this.LOCK) { if (null !=
         * this.fileSystemMap.get(paramString)) {
         * this.fileSystemMap.remove(paramString); }
         */

        RedisApi.putValue2RedisMap(FILESYSTEM_MAP_KEY, paramString, storageFileSystem);
        // }
    }

    public DFileSystem getFileSystems(String paramString) {

        return RedisApi.getValue2RedisMap(FILESYSTEM_MAP_KEY, paramString, DFileSystem.class);
    }

    public Set<String> getArrayId() {
        synchronized (LockConstant.LOCK_DATAUTIL_ARRAYID) {
            return RedisUtil.RedisSet.getValue(ARRAY_ID_SET_KEY);
        }
    }

    public void setArrayId(Set<String> arrayId) {
        synchronized (LockConstant.LOCK_DATAUTIL_ARRAYID) {
            RedisUtil.RedisSet.setValue(ARRAY_ID_SET_KEY, arrayId);
        }
    }

    public Map<String, DArrayIsLock> getArrayIsLock() {
        return arrayIsLock;
    }

    public void setArrayIsLock(Map<String, DArrayIsLock> arrayIsLock) {
        this.arrayIsLock = arrayIsLock;
    }

    public Map<String, DArrayInfo> getArrayInfoMap() {
        return arrayInfoMap;
    }

    public void setArrayInfoMap(Map<String, DArrayInfo> arrayInfoMap) {
        this.arrayInfoMap = arrayInfoMap;
    }

    public Map<String, String> getVasaInfoMap() {
        return vasaInfoMap;
    }

    public void setVasaInfoMap(Map<String, String> vasaInfoMap) {
        this.vasaInfoMap = vasaInfoMap;
    }

    public String getVasaInfoMapByKey(String key) {
        return vasaInfoMap.get(key);
    }
}
