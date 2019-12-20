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

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensds.platform.nemgr.conn.DeviceReconnector;
import org.opensds.platform.nemgr.itf.IDeviceConnManager;
import org.opensds.platform.nemgr.itf.IDeviceConnection;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;

import org.opensds.platform.jobs.KeepAliveJob;

/**
 * 负责保活
 * 
 * @author t00212088
 * 
 */
public class DeviceConnManager implements IDeviceConnManager
{
    private static final Logger LOGGER = LogManager.getLogger(DeviceConnManager.class);
    
    private DeviceFactory deviceFactory;
    
    // 进行连接或正在连接的设备
    private DeviceReconnector reconnector;
    
    private DeviceManager deviceManager;
    
    // quartz定时器
    private Scheduler scheduler;
    
    private Integer fileMonitorInterval;
    
    public void addToKeepAliveSchedule(IDeviceConnection conn)
    {
        // 用来防止保活线程靠的太近
        try
        {
            TimeUnit.MICROSECONDS.sleep(10);
        }
        catch (InterruptedException e1)
        {
            LOGGER.error("", e1);
        }
        
        try
        {
            JobDetail jobDetail = JobBuilder.newJob(KeepAliveJob.class).withIdentity(conn.getAdditionalData("connId") + "_" + conn.getAdditionalData("deviceId")
                + "_keep_live", "eSDK_Jobs").build();
//            jobDetail.setName(conn.getAdditionalData("connId") + "_" + conn.getAdditionalData("deviceId")
//                + "_keep_live");
            jobDetail.getJobDataMap().put("deviceConn", conn);
            jobDetail.getJobDataMap().put("conn", this);
            jobDetail.getJobDataMap().put("reConn", reconnector);
            jobDetail.getJobDataMap().put("deviceManager", deviceManager);
//            jobDetail.setJobClass(KeepAliveJob.class);
            
            Trigger trigger =
                TriggerBuilder.newTrigger()
                    .withIdentity("cron_" + conn.getAdditionalData("connId") + "_" + conn.getAdditionalData("deviceId"),
                        "eSDK_Jobs")
                    .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .repeatSecondlyForever(conn.getKeepAlivePeriod())
                        .repeatForever())
                    .startAt(null == conn.getStartTime() ? new Date() : conn.getStartTime())
                    .build();
//            SimpleTrigger   trigger =     new SimpleTriggerImpl(
//                    "cron_" + conn.getAdditionalData("connId") + "_" + conn.getAdditionalData("deviceId"), "eSDK_Jobs",
//                    null == conn.getStartTime() ? new Date() : conn.getStartTime(), null,
//                    SimpleTrigger.REPEAT_INDEFINITELY, conn.getKeepAlivePeriod() * 1000L);
            
            scheduler.scheduleJob(jobDetail, trigger);
        }
        catch (SchedulerException e)
        {
            LOGGER.error("", e);
        }
    }
    
    @Override
    public void removeFromKeepAliveSchedule(IDeviceConnection conn)
    {
        try
        {
            // 将其从保活列表中移除
            scheduler.pauseTrigger(TriggerKey.triggerKey("cron_" + conn.getAdditionalData("connId") + "_"
                + conn.getAdditionalData("deviceId"),
                "eSDK_Jobs"));
            scheduler.unscheduleJob(TriggerKey.triggerKey("cron_" + conn.getAdditionalData("connId") + "_"
                + conn.getAdditionalData("deviceId"),
                "eSDK_Jobs"));
            scheduler.deleteJob(JobKey.jobKey(conn.getAdditionalData("connId") + "_" + conn.getAdditionalData("deviceId")
                + "_keep_live", "eSDK_Jobs"));
        }
        catch (SchedulerException e)
        {
            LOGGER.error("", e);
        }
    }
    
    public DeviceManager getDeviceManager()
    {
        return deviceManager;
    }
    
    public void setDeviceManager(DeviceManager deviceManager)
    {
        this.deviceManager = deviceManager;
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
    
    public Scheduler getScheduler()
    {
        return scheduler;
    }
    
    public void setScheduler(Scheduler scheduler)
    {
        this.scheduler = scheduler;
    }
    
    public Integer getFileMonitorInterval()
    {
        return fileMonitorInterval;
    }
    
    public void setFileMonitorInterval(Integer fileMonitorInterval)
    {
        this.fileMonitorInterval = fileMonitorInterval;
    }
}
