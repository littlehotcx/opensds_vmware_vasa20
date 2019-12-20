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
package org.opensds.vasa.vasa.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.opensds.vasa.common.DeviceType;
import org.opensds.vasa.common.MagicNumber;
import org.opensds.vasa.domain.model.bean.DArray;
import org.opensds.vasa.domain.model.bean.DLun;
import org.opensds.vasa.domain.model.bean.DProcessor;
import org.opensds.platform.common.utils.ApplicationContextUtil;
import org.opensds.platform.common.utils.StringUtils;
import org.opensds.vasa.vasa.convert.VASAUtilDJConvert;
import org.opensds.vasa.vasa.db.model.NVasaEvent;
import org.opensds.vasa.vasa.db.model.StorageInfo;
import org.opensds.vasa.vasa.db.service.StorageManagerService;
import org.opensds.vasa.vasa.db.service.VasaEventService;
import org.opensds.vasa.vasa.db.service.VirtualVolumeService;
import org.opensds.vasa.vasa.internal.Event;
import org.opensds.vasa.vasa.rest.bean.DeviceTypeMapper;
import org.opensds.vasa.vasa.util.DataUtil;
import org.opensds.vasa.vasa.util.FaultUtil;
import org.opensds.vasa.vasa.util.ListUtil;
import org.opensds.vasa.vasa.util.VASAEvent;
import org.opensds.vasa.vasa.util.VASAUtil;

import com.vmware.vim.vasa.v20.InvalidArgument;
import com.vmware.vim.vasa.v20.LostEvent;
import com.vmware.vim.vasa.v20.StorageFault;
import com.vmware.vim.vasa.v20.data.vvol.xsd.RebindEvent;
import com.vmware.vim.vasa.v20.data.xsd.EntityTypeEnum;
import com.vmware.vim.vasa.v20.data.xsd.EventConfigTypeEnum;
import com.vmware.vim.vasa.v20.data.xsd.EventTypeEnum;
import com.vmware.vim.vasa.v20.data.xsd.StorageEvent;
import com.vmware.vim.vasa.v20.data.xsd.UsageContext;

/**
 * 事件管理服务类
 *
 * @author d69088
 * @version [版本号V001R010C00, 2011-12-14]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public final class EventService {

    // 单例
    private static EventService instance;

    private static org.apache.logging.log4j.Logger LOGGER = org.apache.logging.log4j.LogManager
            .getLogger(EventService.class);

    private static final String FILE_SEPARATOR = System.getProperty("file.separator");

    /***
     * S5500T P11G-5527 【R2&R5归一版本+ V100R005C00SPC003B015+VASA】VASA环境下，VASA
     * PROVIDER一直频繁的登录阵列，导致阵列屏蔽登录事件 begin
     */
    // 间隔时间
    // private static final long DEFAULT_EVENT_RETRY_INTERVAL = 20 * 60;

    private static DataUtil dataManager = DataUtil.getInstance();

    private DiscoverServiceImpl discoverServiceImpl = DiscoverServiceImpl.getInstance();

    // 日志
    // event事件查询的LOCK
    private final Object lcokOfEvent = new Object();

    //北向getEvent时的LOCK
    private final Object lcokOfStorageEvent = new Object();
    // private FusionStorageInfoImpl fusionStorageInfoImpl = FusionStorageInfoImpl.getInstance();

    private VirtualVolumeService virtualVolumeService = (VirtualVolumeService) ApplicationContextUtil.getBean("virtualVolumeService");

    private StorageManagerService storageManagerService = (StorageManagerService) ApplicationContextUtil.getBean("storageManagerService");

    private VasaEventService vasaEventService = (VasaEventService) ApplicationContextUtil.getBean("vasaEventService");

    /***
     * S5500T P11G-5527 【R2&R5归一版本+ V100R005C00SPC003B015+VASA】VASA环境下，VASA
     * PROVIDER一直频繁的登录阵列，导致阵列屏蔽登录事件 end
     */

    // 定时器
    private Timer eventTimer = new Timer();

    // 告警任务
    private EventTask eventTask = null;

    // 间隔时间
    private int timeInterval = VASAUtil.getDefaultRetryTimeInSeconds();

    //获取最大事件数量
    private int maxEventNum = timeInterval / 30 * 100;


    /**
     * Constructor
     */

    private EventService() {
    }

    /**
     * Accessor method for singleton instance
     *
     * @return singleton list manager instance
     */

    public VirtualVolumeService getVirtualVolumeService() {
        return virtualVolumeService;
    }

    public void setVirtualVolumeService(VirtualVolumeService virtualVolumeService) {
        this.virtualVolumeService = virtualVolumeService;
    }

    public VasaEventService getVasaEventService() {
        return vasaEventService;
    }

    public void setVasaEventService(VasaEventService vasaEventService) {
        this.vasaEventService = vasaEventService;
    }

    /**
     * 方法 ： getInstance
     *
     * @return EventManagerImpl 返回结果
     */
    public static synchronized EventService getInstance() {
        if (instance == null) {
            instance = new EventService();
        }

        return instance;
    }

    /**
     * 方法 ： init
     */
    public void init() {

    }

    /**
     * 方法 ： scheduleEvents
     */
    public void scheduleEvents() {
        if (eventTask == null) {
            /** Schedule periodic service cache sync */
            int eventInterval = getReScheduleInterval();

            LOGGER.debug("Scheduling events in " + eventInterval + " seconds");
            eventTask = new EventTask();
            eventTimer.schedule(eventTask, eventInterval * MagicNumber.INT1000);
        }
    }

    /**
     * 方法 ： destroy
     */
    public void destroy() {
        LOGGER.debug("EventanagerImpl destroy");
        if (this.eventTask != null) {
            this.eventTask.cancel();
            LOGGER.debug("evetnTask canceld");
        }
        if (this.eventTimer != null) {
            this.eventTimer.cancel();
            LOGGER.debug("evetnTimer canceld");
        }
    }

    // 得到时间间隔
    private int getReScheduleInterval() {
        return timeInterval;
    }

    /**
     * <功能详细描述> [参数说明]
     *
     * @throws StorageFault
     * @see [类、类#方法、类#成员]
     */
    private void populateConfigEvents() throws StorageFault {

        //Set<String> arrayIds = dataManager.getArrayId();
        Set<String> arrayIds = new HashSet<String>();
        List<StorageInfo> storageInfos = storageManagerService.queryInfo();
        for (StorageInfo storageInfo : storageInfos) {
            if (storageInfo.getDeleted() == 0 && storageInfo.getDevicestatus().equalsIgnoreCase("ONLINE")) {
                arrayIds.add(storageInfo.getId());
            }
        }
        synchronized (lcokOfEvent) {
            LOGGER.debug("populateConfigEvents");
            for (String arrayid : arrayIds) {
                VASAUtil.saveStorageLuns(arrayid);
                queryEvents(arrayid);
            }
        }
    }

    /**
     * Scheduler for events
     *
     * @author V1R10
     * @version [版本号V001R010C00, 2011-12-14]
     */
    private class EventTask extends TimerTask {
        /**
         * 方法 ： run
         */
        public void run() {
            try {
                LOGGER.debug("EventTask");
                int rescheduleInterval = getReScheduleInterval();

                try {
                    if (SecureConnectionService.getInstance().checkCurrentNodeIsMaster()) {
                        populateConfigEvents();
                        getEventsPrecondition();
                    } else {
                        LOGGER.info("current node is not master node , no need reports event.");
                    }
                } catch (Exception e) {
                    LOGGER.error("execute EventTask error.", e);
                    rescheduleInterval = timeInterval;
                }

                eventTask = null;
                if (rescheduleInterval > 0) {
                    scheduleEvents();
                }
            } catch (Exception e) {
                LOGGER.error("close Event task error", e);
            }
        }
    }

    /*
     * 查询指定设备的告警
     */
    private void queryEvents(String arrayid) {
        LOGGER.debug("queryEvents");
        List<Event> events = null;
        if (DeviceTypeMapper.getDeviceType(arrayid).equals(DeviceType.FusionStorage.toString())) {
            try {
                // events = fusionStorageInfoImpl.getStorageEvents(arrayid);
                LOGGER.info("get fusionStorage events" + events.size());
            } catch (Exception e) {
                LOGGER.info("get fusionStorage Exception");
            }
        } else {
            events = queryUnhandledEvents(arrayid);
        }

        LOGGER.debug("unhandled event size is:" + arrayid + ',' + events.size());

        Map<String, List<Event>> sendToVcenterEvents = dataManager.getEventForVcenter();
        List<Event> savedEvents = new ArrayList<Event>(0);
        List<String> usageContexts = dataManager.getUsageContextUUIDs();

        removeLoginAndLogoutEvent(events);
        // 过滤重复的告警
        filterEvents(events);
//        dataManager.setAllEvents(events);

        List<Event> toVcenterAlarms = null;
        for (String usageContext : usageContexts) {
            // 清空上一个设备的历史事件缓存
            savedEvents.clear();

            savedEvents.addAll(events);

            // 没有数据表示没有新的告警生成
            if (!savedEvents.isEmpty()) {
                LOGGER.info("ucUUID:" + usageContext + ", arrayid:" + arrayid + ", filtered events size:" + events.size());
                toVcenterAlarms = sendToVcenterEvents.get(usageContext);
                if (null == toVcenterAlarms) {
                    toVcenterAlarms = savedEvents;
                } else {
                    toVcenterAlarms.addAll(0, savedEvents);
                }

                // 过滤重复的告警
                filterEvents(toVcenterAlarms);
                dataManager.setEventForVcenter(usageContext, toVcenterAlarms);
            }

            if (null != toVcenterAlarms) {
                LOGGER.debug("cur events is cache is device  is:" + getAllEventId(toVcenterAlarms));
            }
        }
    }

    private List<Event> queryUnhandledEvents(String arrayId) {
        List<Event> unhandledEvents = new ArrayList<Event>();

        String created_at = null;
        try {
            NVasaEvent dbEvent = vasaEventService.getEventByArrayId(arrayId);
            if (null == dbEvent) {
                //第一次去该阵列查事件
                StorageInfo queryInfoByArrayId = storageManagerService.queryInfoByArrayId(arrayId);
                //	S2DArray arr = discoverServiceImpl.queryArrayById(arrayId);
                if (queryInfoByArrayId == null) {
                    LOGGER.error("not found array:" + arrayId);
                    return unhandledEvents;
                }

                String arraySn = queryInfoByArrayId.getSn();
                created_at = queryInfoByArrayId.getArrayUTCTime();
				/*Date utcDate = DateUtil.getFormateDate2(created_at, VASAUtil.PATTEN_FORMAT);
				long creationTime = discoverServiceImpl.getAddedTimeByArrayTimeZone(arrayId, utcDate);*/
                long creationTime = 0;
                if (DeviceTypeMapper.getDeviceType(arrayId).equals(DeviceType.FusionStorage.toString())) {
                    String formatType = "yyyy-MM-dd HH:mm:ss";
                    SimpleDateFormat formatter = new SimpleDateFormat(formatType);
                    Date date = formatter.parse(created_at);
                    creationTime = date.getTime();
                } else {
                    creationTime = Long.valueOf(created_at);
                }
                LOGGER.info("first query event, arrayId:" + arrayId + " creation time:" + created_at);

                unhandledEvents = discoverServiceImpl.queryEventsAfterSpecificTime(arrayId, Long.valueOf(created_at) * MagicNumber.LONG1000, maxEventNum);

                if (!unhandledEvents.isEmpty()) {
                    Event maxEvent = VASAUtil.getMaxStartTimeEvent(unhandledEvents);

                    NVasaEvent addedEvent = new NVasaEvent();
                    addedEvent.setArrayId(arrayId);
                    addedEvent.setArraySn(arraySn);
                    addedEvent.setEventId(maxEvent.getEventID());
                    addedEvent.setEventSequence(maxEvent.getDeviceSN());
                    addedEvent.setStartTime(maxEvent.getStartTime());

                    LOGGER.info("max event added to db. arrayId:" + arrayId + ", arraySn:" + arraySn + ", eventId:" + maxEvent.getEventID()
                            + ", eventSequence:" + maxEvent.getDeviceSN() + ", startTime:" + maxEvent.getStartTime());
                    vasaEventService.addEvent(addedEvent);
                } else {
                    LOGGER.info("no event found!");
                }

            } else {
                LOGGER.info("update array event, arrayId:" + arrayId + " last event startTime:" + dbEvent.getStartTime());
                //查出比上次返回的最后一条事件更新的事件
                unhandledEvents = discoverServiceImpl.queryEventsAfterSpecificTime(arrayId, dbEvent.getStartTime(), maxEventNum);
                if (!unhandledEvents.isEmpty()) {
                    Event maxEvent = VASAUtil.getMaxStartTimeEvent(unhandledEvents);

                    NVasaEvent updatedEvent = new NVasaEvent();
                    updatedEvent.setArrayId(arrayId);
                    updatedEvent.setEventId(maxEvent.getEventID());
                    updatedEvent.setEventSequence(maxEvent.getDeviceSN());
                    updatedEvent.setStartTime(maxEvent.getStartTime());

                    LOGGER.info("max event updated to db. arrayId:" + arrayId + ", eventId:" + maxEvent.getEventID()
                            + ", eventSequence:" + maxEvent.getDeviceSN() + ", startTime:" + maxEvent.getStartTime());
                    vasaEventService.updateEventByArrayId(updatedEvent);
                } else {
                    LOGGER.info("no event found!");
                }
            }
        } catch (StorageFault e) {
            LOGGER.error("getEventByArrayId SQLException.", e);
        } catch (ParseException e) {
            LOGGER.error("queryUnhandledEvents ParseException. arrayId:" + arrayId + ", created_at:" + created_at, e);
        }

        return unhandledEvents;
    }

    /* 过滤告警，相同的告警只保留一条 * */
    private void filterEvents(List<Event> events) {
        Map<String, Event> eventForMap = new HashMap<String, Event>(0);
        List<Event> filterEvents = new ArrayList<Event>(0);
        Event compairedEvent = null;
        for (Event event : events) {
            compairedEvent = eventForMap.get(String.valueOf(event.getEventID()));

            if (null == compairedEvent) {
                eventForMap.put(String.valueOf(event.getEventID()), event);
            } else {
                // 保留时间最近的告警
                if (event.getEventID() == compairedEvent.getEventID()
                        && (null != event.getEventParam() && event.getEventParam().equals(compairedEvent.getEventParam()))
                        && event.getStartTime() > compairedEvent.getStartTime()) {
                    eventForMap.put(String.valueOf(event.getEventID()), event);
                    filterEvents.add(compairedEvent);
                }
            }
        }

        events.removeAll(filterEvents);
        LOGGER.debug("filter same event size : " + filterEvents.size() + ", remain event size : " + events.size());
        filterEvents.clear();
        eventForMap.clear();
    }

    /**
     * <去掉登录和退出的事件>
     *
     * @param savedEvents [参数说明]
     * @param
     * @return void [返回类型说明]
     * @throws throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    private void removeLoginAndLogoutEvent(List<Event> savedEvents) {
        List<Event> filterEvents = new ArrayList<Event>(0);
        long eventID = 0L;
        boolean tempBoolean = false;
        // 过滤掉退出和登录的事件
        for (Event event : savedEvents) {
            eventID = event.getEventID();
            tempBoolean = eventID == MagicNumber.LONG35244536365076 || eventID == MagicNumber.LONG35244536365077
                    || (eventID == VASAEvent.SFEVENT)
                    || eventID == VASAEvent.VIS_LOGIN;
            tempBoolean = tempBoolean || eventID == VASAEvent.XVE_LOGIN || eventID == VASAEvent.VIS_LOGIN_OUT
                    || eventID == VASAEvent.XVE_LOGOUT;// 添加XVE登录 退出事件过滤
            if (eventID == MagicNumber.LONG35244536365075 || eventID == MagicNumber.LONG35244536365072
                    || eventID == MagicNumber.LONG35244536365073 || eventID == MagicNumber.LONG35244536365074
                    || tempBoolean) {
                filterEvents.add(event);
            }

            // 过滤SVP告警
            if (event.getDeviceSN() >= VASAUtil.HVS_SVP_ALARM_SN) {
                filterEvents.add(event);
            }
        }
        savedEvents.removeAll(filterEvents);
        LOGGER.debug("remove login and logout event size : " + filterEvents.size() + ", remain event size : " + savedEvents.size());
        filterEvents.clear();
    }

    /*
     * 打印获取到的告警
     */
    private void logReturnedEvent(StorageEvent e) {
        LOGGER.debug("GetEvent info is : "
                + FILE_SEPARATOR
                + "eventID is :"
                + e.getEventId()
                + VASAUtil.LINE_SEPRATOR
                // + "eventTimeStamp is :"
                // + e.getEventTimeStamp()
                + VASAUtil.LINE_SEPRATOR + "eventType is :" + e.getEventType() + VASAUtil.LINE_SEPRATOR
                + "eventMessageID is :" + e.getMessageId() + VASAUtil.LINE_SEPRATOR + "eventObjectID is :"
                + e.getObjectId() + VASAUtil.LINE_SEPRATOR + "eventObjectType is :" + e.getEventObjType()
                + VASAUtil.LINE_SEPRATOR + "eventConfigType is :" + e.getEventConfigType() + VASAUtil.LINE_SEPRATOR
                + "params:" + VASAUtil.convertNameValuePairToString(e.getParameterList()));
    }

    /**
     * 方法 ： getEvents
     *
     * @param uc                  方法参数：uc
     * @param lastReturnedEventId 方法参数：lastReturnedEventId
     * @return StorageEvent[] 返回结果
     * @throws LostEvent       异常：LostEvent
     * @throws InvalidArgument 异常：InvalidArgument
     * @throws StorageFault    异常：StorageFault
     */
    public List<StorageEvent> getEvents(UsageContext uc, long lastReturnedEventId) throws LostEvent, InvalidArgument,
            StorageFault {
        LOGGER.info("getEvents called, And lastReturnedEventId is:" + lastReturnedEventId);

        if (lastReturnedEventId < -1) {
            LOGGER.error("InvalidArgument/lastEventId must be -1 or greater. (" + lastReturnedEventId + ')');
            throw FaultUtil.invalidArgument("lastEventId must be -1 or greater. (" + lastReturnedEventId + ')');
        }

        // 将主机信息保存
        VASAUtil.saveHostInitiatorIds(uc);

        // 这里需要锁住该变量
        synchronized (lcokOfStorageEvent) {
            String ucUUID = VASAUtil.getUcUUID(uc);
            Map<String, Long> savedLastEventID = dataManager.getSavedLastEventID();
            Long lastEventIDObj = savedLastEventID.get(ucUUID);
            if (null != lastEventIDObj) {
                if (lastReturnedEventId != -1 && lastReturnedEventId > lastEventIDObj) {
                    LOGGER.error("InvalidArgument/Last eventId error, id is :" + lastReturnedEventId);
                    throw FaultUtil.invalidArgument("Last eventId is :" + lastReturnedEventId + " error.");
                }
            }

            Map<String, List<StorageEvent>> storageEvents = dataManager.getStorageEventForVcenter();
            Map<String, List<StorageEvent>> peConfigEventsForVcenter = dataManager.getPeConfigEventForVcenter();
            Map<String, List<StorageEvent>> storageConfigEventsForVcenter = dataManager.getConfigEventForvCenter();
            LOGGER.debug("the storageConfigEventsForVcenter size is " + storageConfigEventsForVcenter.size() +
                    " the events : " + storageConfigEventsForVcenter.toString());
            List<StorageEvent> ucStorageEvents = storageEvents.get(ucUUID);
            List<StorageEvent> peConfigEvents = peConfigEventsForVcenter.get(ucUUID);
            List<StorageEvent> storageConfigEvents = storageConfigEventsForVcenter.get(ucUUID);

            if (null == ucStorageEvents || ucStorageEvents.isEmpty()) {
                ucStorageEvents = new ArrayList<StorageEvent>(0);
            }

            if (null == peConfigEvents || peConfigEvents.isEmpty()) {
                peConfigEvents = new ArrayList<StorageEvent>(0);
            }

            if (null == storageConfigEvents || storageConfigEvents.isEmpty()) {
                storageConfigEvents = new ArrayList<StorageEvent>(0);
            }

            if (null != lastEventIDObj) {
                // 最后的告警ID不是-1，并且与VASA保存的告警ID不一致，则抛出lostEvent异常
                if (lastEventIDObj.longValue() != lastReturnedEventId && -1 != lastReturnedEventId) {
                    LOGGER.error("LostEvent/Last eventID error =" + lastReturnedEventId);
                    throw FaultUtil.lostEvent("Last EventID is " + lastReturnedEventId + " error.");
                }

                // 表示vCenter重新请求告警,将最后的alarmID置为-1；返回所有告警
                if (lastReturnedEventId == -1 && lastEventIDObj.longValue() > 0) {
                    dataManager.setSavedLastEventID(ucUUID, -1L);
                }
            }

            List<StorageEvent> returnEvents = new ArrayList<StorageEvent>(0);
            List<StorageEvent> removedEvents = new ArrayList<StorageEvent>(0);
            List<StorageEvent> removedPeConfigEvents = new ArrayList<StorageEvent>(0);
            List<StorageEvent> removedStorageConfigEvents = new ArrayList<StorageEvent>(0);
            LOGGER.debug("all storageEvents size is:" + ucStorageEvents.size() + ", vcenterId:" + ucUUID);
            LOGGER.debug("all peConfigEvents size is:" + peConfigEvents.size() + ", vcenterId:" + ucUUID);
            LOGGER.debug("all storageConfigEvents size is:" + storageConfigEvents.size() + ", vcenterId:" + ucUUID);

            for (StorageEvent storageEvent : storageConfigEvents) {
                if (returnEvents.size() >= VASAUtil.MAX_STORAGE_ALARM_EVENT_NUM) {
                    break;
                }

                storageEvent.setEventId(++lastReturnedEventId);
                returnEvents.add(storageEvent);
                removedStorageConfigEvents.add(storageEvent);
            }

            for (StorageEvent storageEvent : peConfigEvents) {
                if (returnEvents.size() >= VASAUtil.MAX_STORAGE_ALARM_EVENT_NUM) {
                    break;
                }

                storageEvent.setEventId(++lastReturnedEventId);
                returnEvents.add(storageEvent);
                removedPeConfigEvents.add(storageEvent);
            }

            for (StorageEvent storageEvent : ucStorageEvents) {
                if (returnEvents.size() >= VASAUtil.MAX_STORAGE_ALARM_EVENT_NUM) {
                    break;
                }

                storageEvent.setEventId(++lastReturnedEventId);
                returnEvents.add(storageEvent);
                removedEvents.add(storageEvent);
            }

            //移除已经转化的事件
            ucStorageEvents.removeAll(removedEvents);
            peConfigEvents.removeAll(removedPeConfigEvents);
            storageConfigEvents.removeAll(removedStorageConfigEvents);
            dataManager.setStorageEventForVcenter(ucUUID, ucStorageEvents);
            dataManager.setPeConfigEventForVcenter(ucUUID, peConfigEvents);
            dataManager.setConfigEventForvCenter(ucUUID, storageConfigEvents);
            //保存lastEventId
            dataManager.setSavedLastEventID(ucUUID, lastReturnedEventId);
            LOGGER.info("Retrived event size is : " + returnEvents.size());
            LOGGER.debug("saved vcenterID is:" + ucUUID + ",lastEventId is :" + dataManager.getSavedLastEventID());
            removedEvents.clear();
            removedPeConfigEvents.clear();
            removedStorageConfigEvents.clear();
            return returnEvents;
        }
    }

    private void getEventsPrecondition() throws LostEvent, InvalidArgument,
            StorageFault {
        LOGGER.debug("getEventsPrecondition called.");

        List<UsageContext> usageContexts = dataManager.getUsageContexts();
        for (UsageContext uc : usageContexts) {
            List<StorageEvent> storageEventForVcenter = null;
            String ucUUID = null;
            // 这里需要锁住该变量
            synchronized (lcokOfEvent) {
                ucUUID = VASAUtil.getUcUUID(uc);
                Map<String, Long> internalLastEventID = dataManager.getInternalLastEventID();
                Long lastEventIDObj = internalLastEventID.get(ucUUID);
                List<StorageEvent> returnEvents = new ArrayList<StorageEvent>(0);
                // 获取数据后，从列表中移除
                List<Event> removedEvents = new ArrayList<Event>(0);
                Map<String, List<Event>> events = dataManager.getEventForVcenter();
                List<Event> ucEvents = events.get(ucUUID);

                List<String> ucIds = dataManager.getUsageContextUUIDs();

                if (null == ucEvents || ucEvents.isEmpty()) {
                    ucEvents = new ArrayList<Event>(0);
                }

                // 没有数据表示是第一次进来
                if (!ucIds.contains(ucUUID)) {
                    ucEvents.clear();
                    //第一次进来的只需要添加后来的事件
//                    ucEvents.addAll(dataManager.getAllEvents());
                }
                LOGGER.debug("Oraginal ucEvents.size:" + ucEvents.size());

                LOGGER.debug("event last eventIDOBJ is :" + lastEventIDObj);
//                LOGGER.info("after update ucEvents size is:" + ucEvents.size() + " All current event Id in Cache is:"
//                    + getAllEventId(ucEvents));
                if (null == lastEventIDObj) {
                    lastEventIDObj = -1L;
                }
                storageEventForVcenter = retriveEvents(lastEventIDObj, returnEvents, ucEvents, removedEvents, uc);
            }
            appendStorageEvent(ucUUID, storageEventForVcenter);
        }

    }

    public void appendConfigStorageEvent(String ucUUID, List<StorageEvent> storageConfigEventForVcenter) {
        if (storageConfigEventForVcenter == null || storageConfigEventForVcenter.size() == 0) {
            return;
        }

        synchronized (lcokOfStorageEvent) {
            Map<String, List<StorageEvent>> stoageEvents = dataManager.getConfigEventForvCenter();
            List<StorageEvent> ucStorageEvents = stoageEvents.get(ucUUID);
            if (null == ucStorageEvents) {
                ucStorageEvents = storageConfigEventForVcenter;
            } else {
                ucStorageEvents.addAll(storageConfigEventForVcenter);
            }

            LOGGER.info("appendConfigStorageEvent ucUUID=" + ucUUID + ",ucStorageEvents size = " + ucStorageEvents.size());
            dataManager.setConfigEventForvCenter(ucUUID, ucStorageEvents);
            LOGGER.debug("vcenterID:" + ucUUID + ", new cached storageEvents:" + storageConfigEventForVcenter.size());
        }
    }

    public void appendStorageEvent(String ucUUID, List<StorageEvent> storageEventForVcenter) {
        if (storageEventForVcenter == null || storageEventForVcenter.size() == 0) {
            return;
        }

        synchronized (lcokOfStorageEvent) {
            Map<String, List<StorageEvent>> stoageEvents = dataManager.getStorageEventForVcenter();
            List<StorageEvent> ucStorageEvents = stoageEvents.get(ucUUID);
            if (null == ucStorageEvents) {
                ucStorageEvents = storageEventForVcenter;
            } else {
                ucStorageEvents.addAll(storageEventForVcenter);
            }
            LOGGER.info("appendStorageEvent ucUUID=" + ucUUID + ",ucStorageEventsSize=" + ucStorageEvents.size());
            dataManager.setStorageEventForVcenter(ucUUID, ucStorageEvents);
            LOGGER.debug("vcenterID:" + ucUUID + ", new cached storageEvents:" + storageEventForVcenter.size());
        }
    }

    public void appendPeConfigEvent(String ucUUID, List<StorageEvent> peConfigEvents) {
        if (peConfigEvents == null || peConfigEvents.size() == 0) {
            return;
        }

        synchronized (lcokOfStorageEvent) {
            Map<String, List<StorageEvent>> stoageEvents = dataManager.getPeConfigEventForVcenter();
            List<StorageEvent> ucStorageEvents = stoageEvents.get(ucUUID);
            if (null == ucStorageEvents) {
                ucStorageEvents = peConfigEvents;
            } else {
                for (StorageEvent newEvent : peConfigEvents) {
                    if (newEvent.getObjectId() == null) {
                        continue;
                    }

                    for (int i = 0; i < ucStorageEvents.size(); i++) {
                        if (newEvent.getObjectId().equalsIgnoreCase(ucStorageEvents.get(i).getObjectId())) {
                            LOGGER.info("delete cached PE Config event, objectId:" + ucStorageEvents.get(i).getObjectId()
                                    + ", eventConfigType:" + ucStorageEvents.get(i).getEventConfigType());
                            ucStorageEvents.remove(i);
                            break;
                        }
                    }
                }

                ucStorageEvents.addAll(peConfigEvents);
            }

            dataManager.setPeConfigEventForVcenter(ucUUID, ucStorageEvents);
            LOGGER.debug("vcenterID:" + ucUUID + ", new cached peConfig event:" + peConfigEvents.size());
        }
    }

    private List<String> getAllEventId(List<Event> ucEvents) {
        List<String> eventIds = new ArrayList<String>(0);
        int i = 1;
        for (Event event : ucEvents) {
            if (i >= VASAUtil.MAX_EVENTID_TO_LOG) {
                eventIds.add(event.getEventID() + " seq:" + event.getDeviceSN());
            }
            i++;
        }

        return eventIds;
    }

    /**
     * <转换事件>
     *
     * @param lastReturnedEventId
     * @param returnEvents
     * @param ucEvents
     * @param removedEvents
     * @return StorageEvent[] [返回类型说明]
     * @throws throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    private List<StorageEvent> retriveEvents(long lastReturnedEventId, List<StorageEvent> returnEvents,
                                             List<Event> ucEvents, List<Event> removedEvents, UsageContext uuId) {
        DiscoverService nexManager = DiscoverService.getInstance();
        Date date = null;
        StorageEvent storageEvent = null;
        DArray storageArray = null;
        Calendar calendar = null;
        long eventIdTemp;
        String arrayID = null;

        /* 查询是否订阅事件，以及订阅了哪些事件begin *****/
//        List<String> subscribeEvent = dataManager.getSubscribeEventForVcenter().get(VASAUtil.getUcUUID(uuId));
        List<String> subscribeEvent = uuId.getSubscribeEvent();
        boolean isSubscribed = false;// 是否有订阅，默认假设未订阅所有事件
        LOGGER.debug("RetriveEvents method. SubscribeEvent: " + subscribeEvent);
        if (null != subscribeEvent && subscribeEvent.size() > 0) {
            isSubscribed = true;// 订阅了事件
        } else {
            isSubscribed = false;// 未订阅事件
        }
        /* 查询是否订阅事件，以及订阅了哪些事件end *****/
        logSouthCachedEvent(VASAUtil.getUcUUID(uuId), ucEvents);

        for (Event event : ucEvents) {
            /* 处理未包含在订阅内的事件begin *****/
            String vmwareEventType = VASAUtil.getVMwareEventType(String.valueOf(event.getEventID()));// 把阵列侧的事件类型转为vmware的事件类型
            if (vmwareEventType == null) {
                vmwareEventType = EventTypeEnum.SYSTEM.value();
            }
            // 如果定阅了事件，并且不包含该事件类型，则直接跳过这个事件
            if (isSubscribed && null != subscribeEvent && !subscribeEvent.contains(vmwareEventType)) {
                LOGGER.debug("event not in subscribedEvent list, subscribeEvent:" + VASAUtil.convertArrayToStr(subscribeEvent)
                        + ", vmwareEventType:" + vmwareEventType);
                removedEvents.add(event);
                continue;
            }
            /* 处理未包含在订阅内的事件end *****/

            LOGGER.debug(VASAUtil.getUcUUID(uuId) + " event ID is :" + event.getEventID() + " : " + event.getEventParam());
//            // 每次查询的告警不能超过100条
//            if (returnEvents.size() >= VASAUtil.MAX_STORAGE_ALARM_EVENT_NUM)
//            {
//                break;
//            }
            date = new Date(event.getStartTime());
            calendar = Calendar.getInstance();
            calendar.setTime(date);

            storageArray = dataManager.getArray(event.getDeviceId());
            if (null == storageArray) {
                try {
                    nexManager.queryStorageArrays();
                    storageArray = dataManager.getArray(event.getDeviceId());
                } catch (StorageFault e) {
                    LOGGER.debug("retriveEvents cannot the storage array.");
                }

            }

            // 查询时还是没有找到，则跳过此循环
            if (null == storageArray) {
                continue;
            }
            arrayID = VASAUtil.getArraySnFromUniqueId(storageArray.getUniqueIdentifier());
            calendar.setTimeZone(dataManager.getTimeZone(storageArray.getUniqueIdentifier()));
            if (VASAEvent.REBIND_EVENT == event.getEventID()
                    || VASAEvent.CHANGE_WORKING_CONTROLLER_EVENT == event.getEventID())// 如果是bind事件
            {
                storageEvent = new RebindEvent();
            } else// 否则
            {
                storageEvent = new StorageEvent();
            }
            storageEvent.setEventId(++lastReturnedEventId);
            storageEvent.setEventTimeStamp(calendar);
            storageEvent.setEventType(EventTypeEnum.SYSTEM.value());
            storageEvent.setObjectId(storageArray.getUniqueIdentifier());
            storageEvent.setEventConfigType(EventConfigTypeEnum.UPDATE.value());
            storageEvent.setEventObjType(EntityTypeEnum.STORAGE_ARRAY.value());
            // 直接显示时间内容
            storageEvent.setMessageId(String.valueOf(event.getEventID()));
            storageEvent.setArrayId(storageArray.getUniqueIdentifier());//TODO
            // storageEvent.setMessageId(storageArray.getArrayName() + ": "
            // + event.getDescription());
            ListUtil.clearAndAdd(storageEvent.getParameterList(),
                    VASAUtil.convertNameValuePair(event.getEventParserdParam(), arrayID));
            eventIdTemp = event.getEventID();
            // Processor update事件，需要报所有的Processor
            if (retrivEventsDegred(eventIdTemp)) {
                LOGGER.debug("Got processor update info ,event id " + eventIdTemp);
//                String arrayid = event.getIdentifier().getDeviceID();
//                // 可能是2条，所以这里之前告警大于98就返回,每次最多返回100条数据
//                if (returnEvents.size() > MagicNumber.INT48)
//                {
//                    LOGGER.info("returnEvents sise > 48");
//                    lastReturnedEventId--;
//                    break;
//                }
//             
                lastReturnedEventId = getProcessorUpdateEvent(event, lastReturnedEventId, returnEvents, calendar);
            } else if (VASAEvent.REBIND_EVENT == event.getEventID()
                    || VASAEvent.CHANGE_WORKING_CONTROLLER_EVENT == event.getEventID())// 如果是rebind事件:1.设置类型 2.设置VVolId
            {
                try {
                    // TODO 这个事件直接移除不太合适
                    LOGGER.info("get rebind event from storage.");
                    boolean isVvolEvent = convert2RebindEvent(storageEvent, event, arrayID);
                    if (!isVvolEvent)// 不是vvollun事件
                    {
                        lastReturnedEventId--;
                        removedEvents.add(event);
                        continue;
                    } else {
                        LOGGER.info("add Rebind event or change working controller event.");
                        returnEvents.add(storageEvent);
                    }
                } catch (StorageFault e) {
                    LOGGER.error("Convert rebind event error", e);
                }
            } else {
                // 处理特殊事件
                if (!doEventTask(event, storageArray, storageEvent, uuId)) {
                    LOGGER.debug("deal special Event Error.");
                    lastReturnedEventId--;
                    removedEvents.add(event);
                    continue;
                }
                returnEvents.add(storageEvent);
            }

            LOGGER.debug("alarm ID is :" + event.getEventID());
            logReturnedEvent(storageEvent);
            removedEvents.add(event);
        }
        LOGGER.debug("retrive EventAlarm end.");

        // 移除已经转换的告警
        ucEvents.removeAll(removedEvents);
        dataManager.setEventForVcenter(VASAUtil.getUcUUID(uuId), ucEvents);

        // 保存internalLastalarmID
        dataManager.setInternalLastEventID(VASAUtil.getUcUUID(uuId), lastReturnedEventId);

        logNorthCachedEvent(VASAUtil.getUcUUID(uuId), returnEvents);

        print(VASAUtil.getUcUUID(uuId), returnEvents, lastReturnedEventId);
        LOGGER.info("Retrived event size is :" + returnEvents.size());
        LOGGER.debug("saved vcenterID is:" + uuId + ",internalLastEventId is :" + lastReturnedEventId);
        removedEvents.clear();
        return returnEvents;
    }

    private void logSouthCachedEvent(String ucuuid, List<Event> ucEvents) {
        if (ucEvents == null || ucEvents.size() == 0) {
            LOGGER.debug("logSouthCachedEvent ucuuid:" + ucuuid + " no cached south event.");
        } else {
            LOGGER.debug("logSouthCachedEvent begin, ucuuid:" + ucuuid);
            for (Event event : ucEvents) {
                LOGGER.debug("arrayId:" + event.getDeviceId() + ", sequence:" + event.getDeviceSN()
                        + ", eventId:" + event.getEventID() + ", eventParam:" + event.getEventParam());
            }
            LOGGER.debug("logSouthCachedEvent end.");
        }
    }

    private void logNorthCachedEvent(String ucuuid, List<StorageEvent> storageEvents) {
        if (storageEvents == null || storageEvents.size() == 0) {
            LOGGER.debug("logNorthCachedEvent ucuuid:" + ucuuid + " no cached north event.");
        } else {
            LOGGER.debug("logNorthCachedEvent begin, ucuuid:" + ucuuid);
            for (StorageEvent event : storageEvents) {
                LOGGER.debug("eventType:" + event.getEventType() + ", eventObjType:" + event.getEventObjType()
                        + ", eventConfigType:" + event.getEventConfigType() + ", objectId:" + event.getObjectId()
                        + ", messageId:" + event.getMessageId());
            }
            LOGGER.debug("logNorthCachedEvent end.");
        }
    }

    /**
     * 将阵列事件转为VMware的Rebind事件
     *
     * @param storageEvent 最终转为的VMware的事件，不能为NULL
     * @param event        阵列事件，不能为NULL
     *                     TODO 是否要对事件做校验?
     * @throws StorageFault
     */
    private boolean convert2RebindEvent(StorageEvent storageEvent, Event event, String arrayid) throws StorageFault {
        LOGGER.info("Get rebind event: " + event.getEventID() + "， EventParam: " + event.getEventParam());
        String[] eventParam = event.getEventParam().split(",");
        String lunId = eventParam[2];
//        storageEvent.setEventType(EventTypeEnum.REBIND.value());
//        storageEvent.setArrayId(VASAUtil.getStorageArrayUUID(arrayid));

        storageEvent.setEventType(EventTypeEnum.REBIND.value());
        //storageEvent.setArrayId(VASAUtil.getStorageArrayUUID(arrayid));
        storageEvent.setEventObjType(null);
        storageEvent.setEventConfigType(null);
        storageEvent.setEventObjType(null);

        List<String> vvolIds = ((RebindEvent) storageEvent).getVvolId();
        List<String> vvolidList = virtualVolumeService.getVvolIdByArrayIdAndRawId(arrayid, lunId);
        LOGGER.info("Creat rebind Event, the arrayId is " + VASAUtil.getStorageArrayUUID(arrayid) + " the lunId is " + lunId);
        /*if(null != vvolidList)
        {
	        LOGGER.info("For test. In convert2RebindEvent. getVvolIdByArrayIdAndRawId return size is: " + vvolidList.size()
	        		+ " arrayid is: " + arrayid + " lunId is: " + lunId);
	        for(int index = 0;index < vvolidList.size();index++)
	        {
	        	LOGGER.info("For test. In convert2RebindEvent, index: " + index + " vvolidList: " +  vvolidList.get(index));
	        }
        }*/

        if (null == vvolidList || vvolidList.size() != 1)// 查不到vvolid说明不是vvol切控
        {
            LOGGER.error("the vvolid list size is 0");
            return false;
        }
        LOGGER.info("The vvolid is : " + vvolidList.toString());
        vvolIds.add(vvolidList.get(0));
        return true;
    }

    /*
     * 打印
     */
    private void print(String uc, List<StorageEvent> stoargeEvents, long lastEventId) {
        LOGGER.debug("VcGuid is " + uc + ". get new event Data :" + stoargeEvents.size() + " lastEventId is "
                + lastEventId);
    }

    /**
     * reteriveEvents 深度降低
     *
     * @param eventid
     * @return
     */
    private boolean retrivEventsDegred(long eventid) {
        boolean tempBoolean = eventid == MagicNumber.LONG35244534595609 || eventid == MagicNumber.LONG35248809771087
                || eventid == VASAEvent.MODCTLIPV4 || eventid == VASAEvent.MODCTLIPV6;
        tempBoolean = tempBoolean || eventid == VASAEvent.HVSC00_PROCESSOR_RECOVER;
        return tempBoolean;
    }

    /* 得到Processor改变事件 * */
    private long getProcessorUpdateEvent(Event event, long lastReturnedEventId, List<StorageEvent> returnEvents,
                                         Calendar calendar) {
        String arrayid = event.getDeviceId();
        if (StringUtils.isEmpty(arrayid)) {
            return lastReturnedEventId;
        }

        List<DProcessor> processors = discoverServiceImpl.getStorageProcessorByArrayID(arrayid);
        StorageEvent processorUpdateEvent = null;
        if (processors.size() > 0) {
            --lastReturnedEventId;// 避免下面循环第一次++lastReturnedEventId和外面重复
        }
        for (DProcessor processor : processors) {
            processorUpdateEvent = new StorageEvent();
            processorUpdateEvent.setEventId(++lastReturnedEventId);
            processorUpdateEvent.setEventTimeStamp(calendar);
            processorUpdateEvent.setEventType(EventTypeEnum.CONFIG.value());
            processorUpdateEvent.setEventConfigType(EventConfigTypeEnum.UPDATE.value());
            processorUpdateEvent.setEventObjType(EntityTypeEnum.STORAGE_PROCESSOR.value());
            processorUpdateEvent.setObjectId(processor.getUniqueIdentifier());
            processorUpdateEvent.setMessageId(String.valueOf(event.getEventID()));
            ListUtil.clearAndAdd(
                    processorUpdateEvent.getParameterList(),
                    VASAUtil.convertNameValuePair(event.getEventParserdParam(),
                            VASAUtil.getArraySnFromUniqueId(processor.getUniqueIdentifier())));

            logReturnedEvent(processorUpdateEvent);
            returnEvents.add(processorUpdateEvent);

            LOGGER.debug("Recive update Processor event:" + processor.getUniqueIdentifier());
        }

        return lastReturnedEventId;
    }

    /**
     * <处理特殊事件告警>
     *
     * @param event
     * @param storageArray
     * @param storageEvent [参数说明]
     * @return void [返回类型说明]
     */
    private boolean doEventTask(Event event, DArray storageArray, StorageEvent storageEvent, UsageContext uc) {
        // 删除LUN
        if (isLunDelete(event)) {
            synchronized (lcokOfEvent) {
                return displayDeleteLun(event, storageArray, storageEvent);
            }

        }
        // 添加LUN映射
        else if (event.getEventID() == MagicNumber.LONG35244536037395
                || event.getEventID() == MagicNumber.LONG35248812654624 || event.getEventID() == VASAEvent.ADDMAP
                || event.getEventID() == VASAEvent.HVSC00_ADD_LUN) {
            synchronized (lcokOfEvent) {
                return dispalyAddLunMapping(event, storageArray, storageEvent, uc);
            }
        }
        // 创建普通LUN
        // 35248797319196 （TV2未完成）找不到LUNID
        else if (event.getEventID() == MagicNumber.LONG77343883390 || event.getEventID() == VASAEvent.ADDVOL) {
            synchronized (lcokOfEvent) {
                return displayAddLun(event, storageArray, storageEvent, uc);
            }

        }

        return doEventTaskDigread(event, storageArray, storageEvent, uc);
    }

    /**
     * 降低复杂度
     *
     * @param event        事件
     * @param storageArray storageArray对象
     * @param storageEvent envent对象
     * @param uc           uc对象
     * @return boolean 操作是否成功
     */
    private boolean doEventTaskDigread(Event event, DArray storageArray, StorageEvent storageEvent,
                                       UsageContext uc) {
        // 删除映射
        if (event.getEventID() == MagicNumber.LONG35244536037398 || event.getEventID() == VASAEvent.DELMAP) {
            storageEvent.setEventType(EventTypeEnum.CONFIG.value());
        }
        // 控制器增加和修改
        else if (isAddOrDeleteProcessor(event)) {
            displayProcessorAddAndDelete(event, storageArray, storageEvent);
        }
        // 修改LUN名称
        else if (event.getEventID() == MagicNumber.LONG35244534595624
                || event.getEventID() == MagicNumber.LONG35248797319202
                || event.getEventID() == VASAEvent.MODVOL
                || (event.getEventID() == VASAEvent.VIS_RESIZE_LUN && event.getEventParam().contains(
                VASAEvent.GUI_MOD_VOLSIZE))) {
            synchronized (lcokOfEvent) {
                return displayModifyLUNEvent(event, storageArray, storageEvent, uc);
            }
        }

        displatyStoragePortEvent(event, storageArray, storageEvent);

        // 新增几种事件处理
        if (!processThinLUNAndArrayEvent(event, storageArray, storageEvent, uc)) {
            return false;
        }

        return true;
    }

    /***
     * 是否是控制器增加或删除事件
     * @param event
     * @return
     */
    private boolean isAddOrDeleteProcessor(Event event) {
        boolean tempBoolean = event.getEventID() == MagicNumber.LONG77343883312
                || event.getEventID() == MagicNumber.LONG77343883346 || event.getEventID() == MagicNumber.LONG1103551725575
                || event.getEventID() == MagicNumber.LONG17656624119860;
        tempBoolean = tempBoolean || isVisAddOrDeleteProcessor(event) || isHVSC00AddProcessor(event.getEventID());
        return tempBoolean;
    }

    private boolean isHVSC00AddProcessor(long eventid) {
        return eventid == VASAEvent.HVSC00_RESTART_PROCESSOR || eventid == VASAEvent.HVSC00_START_PROCESSOR
                || eventid == VASAEvent.HVSC00_ADD_PROCESSOR;
    }

    /**
     * 是否是LUN删除事件
     *
     * @param event event
     * @return boolean 是否是LUN删除
     */
    private boolean isLunDelete(Event event) {
        boolean isLunDeleteEvent = false;
        isLunDeleteEvent = event.getEventID() == MagicNumber.LONG35248812654627
                || event.getEventID() == MagicNumber.LONG35244534595588
                || event.getEventID() == MagicNumber.LONG35248797319223 || event.getEventID() == VASAEvent.DELVOL;
        isLunDeleteEvent = isLunDeleteEvent || event.getEventID() == VASAEvent.HVSC00_REMOVE_LUN_FROM_LUNGROUP;
        return isLunDeleteEvent;
    }

    private boolean isVisAddOrDeleteProcessor(Event event) {
        return event.getEventID() == VASAEvent.ADDCTL42952753265 || event.getEventID() == VASAEvent.DELCTL42952753215;
    }

    /**
     * <处理thin LUN事件> <功能详细描述>
     *
     * @param event
     * @param storageArray
     * @param storageEvent [参数说明]
     * @return void [返回类型说明]
     * @see [类、类#方法、类#成员]
     */
    private boolean processThinLUNAndArrayEvent(Event event, DArray storageArray, StorageEvent storageEvent,
                                                UsageContext ucId) {
        // 创建Thin LUN
        // TV2与创建LUN是同一条事件，此处无需处理
        if (event.getEventID() == MagicNumber.LONG35244537806873) {
            synchronized (lcokOfEvent) {
                return dealCreateThinLUNEvent(event, storageArray, storageEvent, ucId);
            }
        }

        // 设置精简LUN大小成功
        // TV2与创建LUN是同一条事件，此处无需处理
        if (event.getEventID() == MagicNumber.LONG35244537806879 || event.getEventID() == VASAUtil.THIN_LUN_ENHENCE) {
            synchronized (lcokOfEvent) {
                return mofifyThinLunEvent(event, storageArray, storageEvent, ucId);
            }
        }

        // 修改阵列的名字
        if (isModifyArrayEvent(event)) {
            storageEvent.setEventType(EventTypeEnum.CONFIG.value());
        }
        return true;
    }

    private boolean isModifyArrayEvent(Event event) {
        return event.getEventID() == MagicNumber.LONG35244536102925
                || event.getEventID() == MagicNumber.LONG35248809771081 || event.getEventID() == VASAEvent.MODNAME
                || event.getEventID() == VASAEvent.HVSC00_MODIFY_ARRAY_LOCATION;
    }

    private boolean mofifyThinLunEvent(Event event, DArray storageArray, StorageEvent storageEvent,
                                       UsageContext uc) {
        try {
            String lunId = event.getEventParam();
            if (lunId == null || lunId.length() == 0) {
                LOGGER.debug("cannot get event param for modify thin lun event,eventid is " + event.getEventID());
                return false;
            }
            if (lunId.charAt(lunId.length() - 1) == ',') {
                lunId = lunId.substring(0, lunId.length() - 1);
            }

            String[] params = lunId.split(",");
            if (params.length == MagicNumber.INT3) {
                lunId = params[1];
            }
            // TV2扩容thin LUN参数有四个，LUN ID在第2个位置35248797319199
            else if (params.length == MagicNumber.INT4) {
                lunId = params[MagicNumber.INT2];
            }

            lunId = VASAUtil.getStorageEntityID(VASAUtil.getDeviceID(event, storageArray),
                    EntityTypeEnum.STORAGE_LUN.value(), lunId);

            LOGGER.debug("Get modify thin LUN id :" + lunId + ", UCUUID = " + VASAUtil.getUcUUID(uc));
            if (!VASAUtil.isStorageLunExistInUC(uc, lunId, event)) {
                LOGGER.debug("lunId:" + lunId + " not in usagecontext:" + VASAUtil.getUcUUID(uc)
                        + ", event sequence:" + event.getDeviceSN());
                return false;
            }

            storageEvent.setObjectId(lunId);
            storageEvent.setEventConfigType(EventConfigTypeEnum.UPDATE.value());
            storageEvent.setEventType(EventTypeEnum.CONFIG.value());
            storageEvent.setEventObjType(EntityTypeEnum.STORAGE_LUN.value());
            LOGGER.debug("Recive modify thin lun event:" + lunId);

            return true;
        } catch (Exception e) {
            LOGGER.warn("mofifyThinLunEvent error,", e);
            return false;
        }

    }

    private boolean dealCreateThinLUNEvent(Event event, DArray storageArray, StorageEvent storageEvent,
                                           UsageContext uc) {
        LOGGER.debug("get create thin lun event id is:" + event.getEventID());
        String lunName = event.getEventParam();
        if (lunName == null || lunName.length() == 0) {
            LOGGER.debug("cannot got event param for creat thin lun event");
            return false;
        }
        if (lunName.charAt(lunName.length() - 1) == ',') {
            lunName = lunName.substring(0, lunName.length() - 1);
        }

        String[] params = lunName.split(",");
        if (params.length == MagicNumber.INT4) {
            lunName = params[1];
        }

        String lunId = getLunIdByLunName(lunName, event);
        if (lunId == null) {
            LOGGER.debug("The thin LUN name: " + lunName + "is not in this UCUUID: ");
            return false;
        }
        lunId = VASAUtil.getStorageEntityID(VASAUtil.getDeviceID(event, storageArray),
                EntityTypeEnum.STORAGE_LUN.value(), lunId);

        LOGGER.debug("Get create thin LUN id :" + lunId + ", UCUUID = " + VASAUtil.getUcUUID(uc));
        if (!VASAUtil.isStorageLunExistInUC(uc, lunId, event)) {
            LOGGER.debug("lunId:" + lunId + " not in usagecontext:" + VASAUtil.getUcUUID(uc)
                    + ", event sequence:" + event.getDeviceSN());
            return false;
        }

        storageEvent.setObjectId(lunId);
        storageEvent.setEventConfigType(EventConfigTypeEnum.NEW.value());
        storageEvent.setEventType(EventTypeEnum.CONFIG.value());
        storageEvent.setEventObjType(EntityTypeEnum.STORAGE_LUN.value());
        LOGGER.debug("Recive create thin lun event:" + lunId);

        return true;
    }

    private String getLunIdByLunName(String lunName, Event event) {
        List<DLun> storageLuns = null;
        storageLuns = DataUtil.getInstance().getStorageLunsByDeviceID(event.getDeviceId());
        if (storageLuns.isEmpty()) {
            LOGGER.debug("getLunIdByLunName cache LUN is Empty:" + event.getDeviceId());
            return null;
        }

        for (DLun storageLun : storageLuns) {
            if (storageLun.getDisplayName().equals(lunName)) {
                String lunIdentifyUnique = storageLun.getUniqueIdentifier();
                String lunId = lunIdentifyUnique.substring(lunIdentifyUnique.lastIndexOf(":"));
                LOGGER.debug("match thin lunmame is:" + lunName + " lunIdentifyUnique is:" + lunIdentifyUnique
                        + " get lun Id is:" + lunId);
                return lunId;
            }
        }
        return null;
    }

    /**
     * <处理Port事件>
     *
     * @param event
     * @param storageArray
     * @param storageEvent [参数说明]
     * @return void [返回类型说明]
     * @throws throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    private void displatyStoragePortEvent(Event event, DArray storageArray, StorageEvent storageEvent) {
        // 主机端口已连接
        if (event.getEventID() == MagicNumber.LONG17652349861911
                || event.getEventID() == MagicNumber.LONG17656627855440 || event.getEventID() == VASAEvent.ADDFCPORT
                || event.getEventID() == VASAEvent.HVSC00_ADD_PORT) {
            displayAddPortEvent(event, storageArray, storageEvent);
        }
        // 主机端口断开
        else if (event.getEventID() == MagicNumber.LONG60163817473 || event.getEventID() == MagicNumber.LONG4026925060
                || event.getEventID() == VASAEvent.DELFCPORT) {
            displayDeletePortEvent(event, storageArray, storageEvent);
        }
        // 修改主机端口事件
        else if (isModifyPortEvent(event)) {
            displayModifyPortEvent(event, storageArray, storageEvent);
        }
    }

    /**
     * 是否是修改主机端口事件 降低复杂度 新添加HVSC00 修改主机iscsi端口事件
     *
     * @param event
     * @return
     */
    private boolean isModifyPortEvent(Event event) {
        boolean tempBoolean = event.getEventID() == MagicNumber.LONG35244536692755
                || event.getEventID() == MagicNumber.LONG35248813899836 || event.getEventID() == MagicNumber.LONG35248813899874
                || event.getEventID() == VASAEvent.MODFCPORT || event.getEventID() == VASAEvent.MODISCSIPORT;
        tempBoolean = tempBoolean || event.getEventID() == VASAEvent.HVSC00_MODIFY_ISCSI_PORT;
        return tempBoolean;
    }

    /**
     * <处理主机端口事件>
     *
     * @param event
     * @param storageArray
     * @param modifyLunEvent [参数说明]
     * @return void [返回类型说明]
     * @throws throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    private boolean displayModifyPortEvent(Event event, DArray storageArray, StorageEvent modifyLunEvent) {
        // 映射的lunId在最后一个参数
        String param = event.getEventParam();
        if (param == null || param.length() == 0) {
            LOGGER.debug("cannot got event param for modify port event,eventid is " + event.getEventID());
            return false;
        }

        // 去除后面的逗号
        if (param.charAt(param.length() - 1) == ',') {
            param = param.substring(0, param.length() - 1);
        }

        LOGGER.debug("modify port param is :" + param);
        // 此告警有3个参数，LUNID在第二个
        String[] params = param.split(",");
        // icp
        trimStrings(params);

        String portCombinationID = null;
        // TV2告警
        // HVSC00 FC端口修改事件
        boolean isHVS = false;
        if (event.getEventID() == MagicNumber.LONG35248813899836
                || event.getEventID() == MagicNumber.LONG35248813899874
                || event.getEventID() == VASAEvent.HVSC00_MODIFY_ISCSI_PORT)// 对于HVSC00
        // iSCSI
        // 端口修改事件
        {
            portCombinationID = VASAUtilDJConvert.getPortIdFromCache(storageArray.getUniqueIdentifier(),
                    params[MagicNumber.INT2], params[MagicNumber.INT5], params[MagicNumber.INT6]);
            // LogManager.debug("original port combination id is "+portCombinationID);
            portCombinationID = dealPortEvent(portCombinationID);
            LOGGER.debug("port combination id is " + portCombinationID);
            isHVS = true;
            //V3R3对于更改FC配置新定义了事件ID，统一成V3R2的事件ID
            if (event.getEventID() == MagicNumber.LONG35248813899874) {
                modifyLunEvent.setMessageId(String.valueOf(MagicNumber.LONG35248813899836));
            }
        } else if (event.getEventID() == VASAEvent.MODFCPORT || event.getEventID() == VASAEvent.MODISCSIPORT) {
            portCombinationID = VASAUtil.getPortCombinationID(params[0], params[1], params[MagicNumber.INT2]);
        } else {
            portCombinationID = VASAUtil.getPortCombinationID(params[1], params[MagicNumber.INT2],
                    params[MagicNumber.INT3]);
        }
        //
        LOGGER.debug("Got combination portid :" + portCombinationID);
        // ICP SIMPLIFY BOOLEAN EXPRESSION
        portCombinationID = dealHvsPortCombinationId(portCombinationID, isHVS, params);
        // if (isHVS
        // && (portCombinationID == null || portCombinationID.equals("")))
        // {
        // //如果前面获取不到 就重新生成一个 反正也无法获取
        // portCombinationID =
        // VASAUtil.getPortCombinationID(VASAUtil.getContorllerID(params[MagicNumber.INT5]),
        // VASAUtil.getLastChar(params[MagicNumber.INT5]),
        // VASAUtil.getLastChar(params[MagicNumber.INT6]));
        // }
        String portID = VASAUtil.getStorageEntityID(VASAUtil.getDeviceID(event, storageArray),
                EntityTypeEnum.STORAGE_PORT.value(), portCombinationID);

        modifyLunEvent.setObjectId(portID);
        modifyLunEvent.setEventConfigType(EventConfigTypeEnum.UPDATE.value());
        modifyLunEvent.setEventType(EventTypeEnum.CONFIG.value());
        modifyLunEvent.setEventObjType(EntityTypeEnum.STORAGE_PORT.value());
        LOGGER.debug("Recive update port event:" + portID);

        return true;

    }

    /**
     * trim Strings
     *
     * @param params
     */
    private void trimStrings(String[] params) {
        for (int i = 0; i < params.length; i++) {
            params[i] = params[i].trim();
        }
    }

    private String dealPortEvent(String portCombinationID) {
        if (portCombinationID != null && portCombinationID.length() > 0) {
            int index = portCombinationID.lastIndexOf(":");
            if (index != -1) {
                return portCombinationID.substring(index + 1);
            }
        }
        return portCombinationID;
    }

    /**
     * <处理主机端口删除事件>
     *
     * @param event
     * @param storageArray
     * @param deletePortEvent [参数说明]
     * @return void [返回类型说明]
     * @throws throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    private boolean displayDeletePortEvent(Event event, DArray storageArray, StorageEvent deletePortEvent) {
        // 屏蔽掉参数不齐 测试构造告警引起
        // 映射的lunId在最后一个参数
        String param = event.getEventParam();
        if (param == null || param.length() == 0) {
            LOGGER.debug("cannot got event param for delete port event,eventid is " + event.getEventID());
            return false;
        }
        String portCombinationID = null;
        // 去除后面的逗号
        if (param.charAt(param.length() - 1) == ',') {
            param = param.substring(0, param.length() - 1);
        }

        LOGGER.debug("delete port param is :" + param);
        // 此告警有3个参数，LUNID在第二个
        String[] params = param.split(",");
        trimStrings(params);
        // DeviceContext dc = dataManager.getDevice(event.getIdentifier()
        // .getDeviceID());
        // T V2告警
        boolean isHVS = false;
        if (event.getEventID() == MagicNumber.LONG4026925060) {
            // iSCSI主机端口（引擎 ENG0，ISCSI接口模块 B2，端口号 P3）连接断开。
            // FC主机端口（引擎 ENG0，FC接口模块 A1，端口号 P2）连接断开。","eventParam":"1, 2,
            // ENG0, FC, 2, A1, P2"
            // portCombinationID =
            // VASAUtil.getPortCombinationID(VASAUtil.getContorllerID(params[MagicNumber.INT5]),
            // VASAUtil.getLastChar(params[MagicNumber.INT5]),
            // VASAUtil.getLastChar(params[MagicNumber.INT6]));
            isHVS = true;
            portCombinationID = VASAUtilDJConvert.getPortIdFromCache(storageArray.getUniqueIdentifier(),
                    params[MagicNumber.INT2], params[MagicNumber.INT5], params[MagicNumber.INT6]);
            // LogManager.debug("original port combination id is "+portCombinationID);
            // 去掉获取的前面的deviceSN:StoragePort:portNo
            portCombinationID = dealPortEvent(portCombinationID);
            LOGGER.debug("delete port combination id is " + portCombinationID);
        } else if (event.getEventID() == VASAEvent.DELFCPORT) {

            portCombinationID = VASAUtil.getPortCombinationID(params[MagicNumber.INT2], params[MagicNumber.INT3],
                    params[MagicNumber.INT4]);

        } else {

            portCombinationID = VASAUtil.getPortCombinationID(VASAUtil.getContorllerID(params[MagicNumber.INT4]),
                    VASAUtil.getLastChar(params[MagicNumber.INT4]), VASAUtil.getLastChar(params[MagicNumber.INT5]));

        }

        // 删除端口事件
        portCombinationID = dealHvsPortCombinationId(portCombinationID, isHVS, params);
        // if (isHVS
        // && (portCombinationID == null || portCombinationID.equals("")))
        // {
        // //如果前面获取不到 就重新生成一个 反正也无法获取
        // portCombinationID =
        // VASAUtil.getPortCombinationID(VASAUtil.getContorllerID(params[MagicNumber.INT5]),
        // VASAUtil.getLastChar(params[MagicNumber.INT5]),
        // VASAUtil.getLastChar(params[MagicNumber.INT6]));
        // }

        String portID = VASAUtil.getStorageEntityID(VASAUtil.getDeviceID(event, storageArray),
                EntityTypeEnum.STORAGE_PORT.value(), portCombinationID);

        deletePortEvent.setObjectId(portID);
        deletePortEvent.setEventConfigType(EventConfigTypeEnum.DELETE.value());
        deletePortEvent.setEventType(EventTypeEnum.CONFIG.value());
        deletePortEvent.setEventObjType(EntityTypeEnum.STORAGE_PORT.value());
        LOGGER.debug("Recive delete port event:" + portID);

        return true;

    }

    // icp
    private String dealHvsPortCombinationId(String portCombinationID, boolean isHVS, String[] params) {
        if (isHVS && (portCombinationID == null || portCombinationID.equals(""))) {
            // 如果前面获取不到 就重新生成一个 反正也无法获取
            return VASAUtil.getPortCombinationID(VASAUtil.getContorllerID(params[MagicNumber.INT5]),
                    VASAUtil.getLastChar(params[MagicNumber.INT5]), VASAUtil.getLastChar(params[MagicNumber.INT6]));
        }
        return portCombinationID;
    }

    /**
     * <处理主机端口增加事件>
     *
     * @param event
     * @param storageArray
     * @param addPortEvent [参数说明]
     * @return void [返回类型说明]
     * @throws throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    private boolean displayAddPortEvent(Event event, DArray storageArray, StorageEvent addPortEvent) {
        String eventParam = event.getEventParam();
        if (eventParam == null || eventParam.length() == 0) {
            LOGGER.debug("cannot got event param for add port event");
            return false;
        }
        // 去除后面的逗号
        if (eventParam.charAt(eventParam.length() - 1) == ',') {
            eventParam = eventParam.substring(0, eventParam.length() - 1);
        }
        String portComID = null;
        String[] params = eventParam.split(",");
        LOGGER.debug("add port param is :" + eventParam);
        trimStrings(params);
        // TV2端口恢复事件
        if (event.getEventID() == MagicNumber.LONG17656627855440) {
            portComID = VASAUtil.getPortCombinationID(VASAUtil.getContorllerID(params[MagicNumber.INT5]),
                    VASAUtil.getLastChar(params[MagicNumber.INT5]), VASAUtil.getLastChar(params[MagicNumber.INT6]));

        } else if (event.getEventID() == VASAEvent.ADDFCPORT) {
            portComID = VASAUtil.getPortCombinationID(params[MagicNumber.INT2], params[MagicNumber.INT3],
                    params[MagicNumber.INT4]);

        } else if (event.getEventID() == VASAEvent.HVSC00_ADD_PORT) {
            portComID = VASAUtil.getPortCombinationID(VASAUtil.getLastCharNoLengthLimit(params[MagicNumber.INT2])
                            + VASAUtil.getFirstCharNoLengthLimit(params[MagicNumber.INT4]),
                    VASAUtil.getLastCharNoLengthLimit(params[MagicNumber.INT4]), "0");
            LOGGER.debug("Got hvsc00 add port com id :" + portComID);
        } else {
            portComID = VASAUtil.getPortCombinationID(VASAUtil.getContorllerID(params[MagicNumber.INT4]),
                    VASAUtil.getLastChar(params[MagicNumber.INT4]), VASAUtil.getLastChar(params[MagicNumber.INT5]));
        }

        String portID = VASAUtil.getStorageEntityID(VASAUtil.getDeviceID(event, storageArray),
                EntityTypeEnum.STORAGE_PORT.value(), portComID);

        addPortEvent.setObjectId(portID);
        addPortEvent.setEventConfigType(EventConfigTypeEnum.NEW.value());
        addPortEvent.setEventType(EventTypeEnum.CONFIG.value());
        addPortEvent.setEventObjType(EntityTypeEnum.STORAGE_PORT.value());
        LOGGER.debug("Recive add port event:" + portID);

        return true;

    }

    /**
     * <处理修改LUN名称事件>
     *
     * @param event
     * @param storageArray
     * @param modifyLunEvent [参数说明]
     * @return void [返回类型说明]
     */
    private boolean displayModifyLUNEvent(Event event, DArray storageArray, StorageEvent modifyLunEvent,
                                          UsageContext uc) {
        try {
            // 映射的lunId在最后一个参数
            String param = event.getEventParam();
            if (param == null || param.length() == 0) {
                LOGGER.debug("cannot got event param for modify thin lun event");
                return false;
            }
            // 去除后面的逗号
            if (param.charAt(param.length() - 1) == ',') {
                param = param.substring(0, param.length() - 1);
            }

            LOGGER.debug("modify lun param is :" + param);
            String[] params = param.split(",");

            String lunId = null;

            // 修改ICP深度超标
            lunId = produceLunId(event, storageArray, params);

            // 是否是VIS扩容LUN事件
            boolean isVisResizeLunEvent = false;
            if (event.getEventID() == VASAEvent.VIS_RESIZE_LUN) {
                isVisResizeLunEvent = true;
            }
            // if(eventId)
            if ((!VASAUtil.isStorageLunExistInUC(uc, lunId, event)) && !isVisResizeLunEvent) {
                LOGGER.debug("lunId:" + lunId + " not in usagecontext:" + VASAUtil.getUcUUID(uc)
                        + ", event sequence:" + event.getDeviceSN());
                return false;
            }
            // if (!isVisResizeLunEvent)
            // {
            // modifyLunEvent.setObjectId(lunId);
            // modifyLunEvent.setEventConfigType(EventConfigTypeEnum.UPDATE.value());
            // modifyLunEvent.setEventType(EventTypeEnum.CONFIG.value());
            // modifyLunEvent.setEventObjType(EntityTypeEnum.STORAGE_LUN.value());
            // LogManager.info("Recive modify lun event:" + lunId);
            // }
            // else
            // {
            // //是VIS lun扩容事件
            // modifyLunEvent.setObjectId(storageArray.getUniqueIdentifier());
            // modifyLunEvent.setEventConfigType(EventConfigTypeEnum.UPDATE.value());
            // modifyLunEvent.setEventType(EventTypeEnum.CONFIG.value());
            // modifyLunEvent.setEventObjType(EntityTypeEnum.STORAGE_ARRAY.value());
            // VasaLogManager.info(log,
            // "Recive VIS Resize lun event: param is " + param);
            // }
            // 降低复杂度
            displayModifyLunEventDegred(modifyLunEvent, storageArray, isVisResizeLunEvent, lunId, param);
            // 降低复杂度 end

            return true;
        } catch (Exception e) {
            LOGGER.warn("displayModifyLUNEvent error,", e);
            return false;
        }

    }

    /**
     * 修改ICP深度超标
     *
     * @param event
     * @param storageArray
     * @param params
     */
    private String produceLunId(Event event, DArray storageArray, String[] params) {
        String lunId = null;

        if (event.getEventID() == MagicNumber.LONG35248797319202) {
            // T V2 此告警有3个参数，LUNID在最后
            lunId = VASAUtil.getStorageEntityID(VASAUtil.getDeviceID(event, storageArray),
                    EntityTypeEnum.STORAGE_LUN.value(), params[params.length - 1]);
        } else if (event.getEventID() == VASAEvent.MODVOL) {
            // VIS LUNID在第一个
            lunId = VASAUtil.getStorageEntityID(VASAUtil.getDeviceID(event, storageArray),
                    EntityTypeEnum.STORAGE_LUN.value(), params[0]);

        } else {
            // T V1 此告警有3个参数，LUNID在第二个
            lunId = VASAUtil.getStorageEntityID(VASAUtil.getDeviceID(event, storageArray),
                    EntityTypeEnum.STORAGE_LUN.value(), params[1]);
        }

        return lunId;

    }

    private void displayModifyLunEventDegred(StorageEvent modifyLunEvent, DArray storageArray,
                                             boolean isVisResizeLunEvent, String lunId, String param) {
        if (!isVisResizeLunEvent) {
            modifyLunEvent.setObjectId(lunId);
            modifyLunEvent.setEventConfigType(EventConfigTypeEnum.UPDATE.value());
            modifyLunEvent.setEventType(EventTypeEnum.CONFIG.value());
            modifyLunEvent.setEventObjType(EntityTypeEnum.STORAGE_LUN.value());
            LOGGER.debug("Recive modify lun event:" + lunId);
        } else {
            // 是VIS lun扩容事件
            modifyLunEvent.setObjectId(storageArray.getUniqueIdentifier());
            modifyLunEvent.setEventConfigType(EventConfigTypeEnum.UPDATE.value());
            modifyLunEvent.setEventType(EventTypeEnum.CONFIG.value());
            modifyLunEvent.setEventObjType(EntityTypeEnum.STORAGE_ARRAY.value());
            LOGGER.debug("Recive VIS Resize lun event: param is " + param);
        }
    }

    /**
     * <处理控制器事件>
     *
     * @param event
     * @param storageArray
     * @param storageEventOfProcessor [参数说明]
     * @return void [返回类型说明]
     * @throws throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    private void displayProcessorAddAndDelete(Event event, DArray storageArray,
                                              StorageEvent storageEventOfProcessor) {
        try {
            // 映射的processor在最后一个参数
            String processorId = event.getEventParam();
            if (processorId == null || processorId.length() == 0) {
                LOGGER.debug("cannot got event param for processor add and delete event,eventid is "
                        + event.getEventID());
                return;
            }
            // 去除后面的逗号
            if (processorId.charAt(processorId.length() - 1) == ',') {
                processorId = processorId.substring(0, processorId.length() - 1);
            }

            String[] params = processorId.split(",");
            processorId = VASAUtil.getStorageEntityID(VASAUtil.getDeviceID(event, storageArray),
                    EntityTypeEnum.STORAGE_PROCESSOR.value(), params[params.length - 1]);
            // || event.getEventID() == MagicNumber.LONG17656624119860
            // 这个事件在c00 是控制器删除
            // 在C99是控制器增加            
            processorId = dealHVSC00ProcesorId(processorId, event.getEventID(), params);

            if (event.getEventID() == MagicNumber.LONG77343883346
                    || event.getEventID() == VASAEvent.ADDCTL42952753265 || isHVSC00AddProcessor(event.getEventID())) {
                storageEventOfProcessor.setEventConfigType(EventConfigTypeEnum.NEW.value());
                LOGGER.debug("Recive add Processor event:" + processorId);
            } else {
                storageEventOfProcessor.setEventConfigType(EventConfigTypeEnum.DELETE.value());
                LOGGER.debug("Recive delete Processor event:" + processorId);
            }

            storageEventOfProcessor.setObjectId(processorId);
            storageEventOfProcessor.setEventType(EventTypeEnum.CONFIG.value());
            storageEventOfProcessor.setEventObjType(EntityTypeEnum.STORAGE_PROCESSOR.value());

        } catch (Exception e) {
            LOGGER.warn("displayProcessorAddAndDelete error,", e);
        }
    }

    private String dealHVSC00ProcesorId(String processoreid, long eventid, String[] params) {
        if (eventid == VASAEvent.HVSC00_START_PROCESSOR) {
            // 获取控制器id
            String firstChar = VASAUtil.getLastCharNoLengthLimit(params[MagicNumber.INT2]);
            char secondChar = (params[MagicNumber.INT3].charAt(0)) == '0' ? 'A' : 'B';
            return processoreid.substring(0, processoreid.lastIndexOf(":") + 1) + firstChar + secondChar;
        } else if (eventid == VASAEvent.HVSC00_RESTART_PROCESSOR) {
            String firstChar = VASAUtil.getLastCharNoLengthLimit(params[1]);
            char secondChar = (params[MagicNumber.INT2].charAt(0)) == '0' ? 'A' : 'B';
            return processoreid.substring(0, processoreid.lastIndexOf(":") + 1) + firstChar + secondChar;
        } else if (eventid == VASAEvent.HVSC00_ADD_PROCESSOR) {
            // 添加这个控制器增加事件
            if (params.length == MagicNumber.INT4) {
                String firstChar = VASAUtil.getLastCharNoLengthLimit(params[1]);
                char secondChar = (params[MagicNumber.INT3].charAt(0)) == '0' ? 'A' : 'B';
                return processoreid.substring(0, processoreid.lastIndexOf(":") + 1) + firstChar + secondChar;
            }

        }
        return processoreid;
    }

    /**
     * <处理添加LUN映射事件>
     *
     * @param event
     * @param storageArray
     * @param addLunMappingEvent
     * @return boolean 是否处理成功
     */
    private boolean dispalyAddLunMapping(Event event, DArray storageArray, StorageEvent addLunMappingEvent,
                                         UsageContext uc) {
        try {
            // 映射的LUN ID在最后一个参数
            String lunId = event.getEventParam();
            if (lunId == null || lunId.length() == 0) {
                LOGGER.debug("cannot got event param for add lun mapping event,eventid is " + event.getEventID());
                return false;
            }
            // 去除后面的逗号
            if (lunId.charAt(lunId.length() - 1) == ',') {
                lunId = lunId.substring(0, lunId.length() - 1);
            }

            // 截取LUN ID,在最后一位,逗号隔开的形式
            // lunId = lunId.substring(lunId.length() - 1);
            String[] param = lunId.split(",");
            lunId = param[param.length - 1];

            if (event.getEventID() == VASAEvent.ADDMAP) {
                // 1.[Map-type] 2.[HostOrGroup-id] 3.[Hostlun-id] 4.[Volume-id]
                // 5.[Diskgroup-name] 6.[Volume-name]
                lunId = param[MagicNumber.INT3];

            } else if (event.getEventID() == VASAEvent.HVSC00_ADD_LUN) { // HVSC00添加LUN事件

                lunId = param[MagicNumber.INT2];
            }
            lunId = lunId.trim();
            // 获取阵列的ID 阵列ID的构造方式：StorageArray:deviceID
            lunId = VASAUtil.getStorageEntityID(VASAUtil.getDeviceID(event, storageArray),
                    EntityTypeEnum.STORAGE_LUN.value(), lunId);

            if (!VASAUtil.isStorageLunExistInUC(uc, lunId, event)) {
                LOGGER.debug("lunId:" + lunId + " not in usagecontext:" + VASAUtil.getUcUUID(uc)
                        + ", event sequence:" + event.getDeviceSN());
                return false;
            }

            addLunMappingEvent.setObjectId(lunId);
            addLunMappingEvent.setEventConfigType(EventConfigTypeEnum.NEW.value());
            addLunMappingEvent.setEventType(EventTypeEnum.CONFIG.value());
            addLunMappingEvent.setEventObjType(EntityTypeEnum.STORAGE_LUN.value());
            LOGGER.debug("Recive add LUN Mapping event:" + lunId);

            return true;
        } catch (Exception e) {
            LOGGER.warn("dispalyAddLunMapping error,", e);
            return false;
        }

    }

    /**
     * 处理删除LUN事件
     *
     * @param event
     * @param storageArray
     * @param deleteLunEvent [参数说明]
     * @return boolean
     */
    private boolean displayDeleteLun(Event event, DArray storageArray, StorageEvent deleteLunEvent) {
        try {
            String lunId = event.getEventParam();
            if (lunId == null || lunId.length() == 0) {
                LOGGER.debug("cannot get event param for delete lun event,eventid " + event.getEventID());
                return false;
            }
            if (lunId.charAt(lunId.length() - 1) == ',') {
                lunId = lunId.substring(0, lunId.length() - 1);
            }

            String[] params = lunId.split(",");
            long eventId = event.getEventID();
            if (eventId == VASAEvent.DELVOL) {
                // VIS LUNID在第一个
                lunId = params[0];

            } else if (eventId == MagicNumber.LONG35248797319223 || eventId == VASAEvent.HVSC00_REMOVE_LUN_FROM_LUNGROUP
                    || eventId == MagicNumber.LONG35248812654627) { // 这里添加了HVSC00中从LUN组移除LUN的事件
                if (params.length == MagicNumber.INT3) {
                    lunId = params[MagicNumber.INT2];
                }
            } else {
                if (params.length == MagicNumber.INT2) {
                    lunId = params[1];
                }
            }

            lunId = VASAUtil.getStorageEntityID(VASAUtil.getDeviceID(event, storageArray),
                    EntityTypeEnum.STORAGE_LUN.value(), lunId);

            // if (event.getEventID() != VASAEvent.DELVOL &&
            // !VASAUtil.isStorageLunExistInUC(uc, lunId, event))
            // {
            // return false;
            // }

            deleteLunEvent.setObjectId(lunId);
            deleteLunEvent.setEventConfigType(EventConfigTypeEnum.DELETE.value());
            deleteLunEvent.setEventType(EventTypeEnum.CONFIG.value());
            deleteLunEvent.setEventObjType(EntityTypeEnum.STORAGE_LUN.value());
            LOGGER.debug("Recive lun delete event:" + lunId);

            return true;
        } catch (Exception e) {
            LOGGER.warn("displayDeleteLun error,", e);
            return false;
        }

    }

    /**
     * <处理创建LUN事件>
     *
     * @param event
     * @param storageArray
     * @param addLunEvent  [参数说明]
     * @return void [返回类型说明]
     */
    private boolean displayAddLun(Event event, DArray storageArray, StorageEvent addLunEvent, UsageContext uc) {
        try {
            String lunId = event.getEventParam();
            if (lunId == null || lunId.length() == 0) {
                LOGGER.debug("cannot got event param for add lun event,eventid is " + event.getEventID());
                return false;
            }
            if (lunId.charAt(lunId.length() - 1) == ',') {
                lunId = lunId.substring(0, lunId.length() - 1);
            }

            // VIS 参数在第0个位置
            if (event.getEventID() == VASAEvent.ADDVOL) {
                String[] params = lunId.split(",");

                // VIS LUNID在第一个
                lunId = params[0];

            }

            // TV2，参数在第一个位置
            if (event.getEventID() == MagicNumber.LONG35248797319196) {
                String[] param = lunId.split(",");
                lunId = VASAUtil.getStorageEntityID(VASAUtil.getDeviceID(event, storageArray),
                        EntityTypeEnum.STORAGE_LUN.value(), param[1]);
            } else {
                lunId = VASAUtil.getStorageEntityID(VASAUtil.getDeviceID(event, storageArray),
                        EntityTypeEnum.STORAGE_LUN.value(), lunId);
            }

            if (!VASAUtil.isStorageLunExistInUC(uc, lunId, event)) {
                LOGGER.debug("lunId:" + lunId + " not in usagecontext:" + VASAUtil.getUcUUID(uc)
                        + ", event sequence:" + event.getDeviceSN());
                return false;
            }

            addLunEvent.setObjectId(lunId);
            addLunEvent.setEventConfigType(EventConfigTypeEnum.NEW.value());
            addLunEvent.setEventType(EventTypeEnum.CONFIG.value());
            addLunEvent.setEventObjType(EntityTypeEnum.STORAGE_LUN.value());
            LOGGER.debug("Recive lun add event:" + lunId);

            return true;
        } catch (Exception e) {
            LOGGER.warn("displayAddLun error,", e);
            return false;
        }
    }

    // public static void main(String[] args)
    // {
    // dataManager.init();
    // DeviceContext deviceContext = new DeviceContext("admin", "654321",
    // new String[] { "129.61.253.125" });
    // EventManagerImpl event = EventManagerImpl.getInstance();
    // while (true)
    // {
    // // alarm.scheduleAlarms();
    // event.queryEvents(deviceContext);
    // try
    // {
    // Thread.sleep(30000);
    // }
    // catch (InterruptedException e)
    // {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // }
    // }
    //
    //
    // }

    // /**
    // * 方法 ： main
    // *
    // * @param args 方法参数：args
    // */
    // public static void main(String[] args)
    // {
    // List<DeviceContext> dc = new ArrayList<DeviceContext>();
    // DeviceContext deviceContext = new DeviceContext("admin",
    // "Admin@123", new String[] {"100.133.10.21","100.133.190.122"});
    // deviceContext.setDeviceType(DeviceType.HVS88T);
    // dc.add(deviceContext);
    // dataManager.init();
    // dataManager.setDeviceContexts(dc);
    // dataManager.addUsageContextUUID("1233232");
    //
    // EventManagerImpl alarm = EventManagerImpl.getInstance();
    //
    // while (true)
    // {
    // alarm.populateConfigEvents();
    //
    // try
    // {
    // Thread.sleep(10000);
    // }
    // catch (InterruptedException e)
    // {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // }
    // }
    // }

//    public static void main(String[] args)
//    {
//    	new EventService().queryEvents("xxxxxxxxxxxx");
//    }
}
