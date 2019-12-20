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

package org.opensds.platform.usermgr;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensds.platform.base.BaseManager;
import org.opensds.platform.config.SystemConfig;
import org.opensds.platform.config.service.itf.IConfigObserver;
import org.opensds.platform.jobs.RefershUserConfigJob;
import org.opensds.platform.usermgr.itf.IUserManager;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;

import org.opensds.platform.common.bean.config.UserConfig;
import org.opensds.platform.common.config.ConfigManager;
import org.opensds.platform.common.utils.NumberUtils;
import org.opensds.platform.common.utils.SHA256Utils;
import org.opensds.platform.config.service.itf.IUserConfigService;

public class UserManager extends BaseManager implements IUserManager
{
    private static final Logger LOGGER = LogManager.getLogger(UserManager.class);
    
    private Scheduler scheduler;
    
    private List<UserConfig> users;
    
    IUserConfigService userConfigService;
    
    public synchronized void init()
    {
        // 加载系统密钥，更新系统密钥，并保存至文件。
        LOGGER.info("UserManager init()");
        SystemConfig.init();
        
        refreshUsers();
        
        userConfigService.registerObserver(new IConfigObserver()
        {
            @Override
            public void doAction()
            {
                LOGGER.info("users configuration changed");
                refreshUsers();
            }
        });
        
        try
        {
            JobDetail jobDetail = JobBuilder.newJob(RefershUserConfigJob.class).withIdentity("refresh_user_config").build();
//            jobDetail.setName("refresh_user_config");
            jobDetail.getJobDataMap().put("userManager", this);
//            jobDetail.setJobClass(RefershUserConfigJob.class);
            
            Trigger trigger =
                TriggerBuilder.newTrigger()
                    .withIdentity("cron_refersh_user_config", "eSDK_Jobs")
                    .startNow()
                    .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .repeatMinutelyForever(NumberUtils.parseIntValue(ConfigManager.getInstance()
                            .getValue("refreshInterval", "30")))
                        .repeatForever())
                    .build();
            
            
//            SimpleTrigger trigger =
//                new SimpleTrigger("cron_refersh_user_config", "eSDK_Jobs", new Date(), null,
//                    SimpleTrigger.REPEAT_INDEFINITELY, 1000L * 60 * NumberUtils.parseIntValue(ConfigManager.getInstance()
//                        .getValue("refreshInterval", "30")));
            
            scheduler.scheduleJob(jobDetail, trigger);
        }
        catch (SchedulerException e)
        {
            LOGGER.error("User configuration job trigger error", e);
        }
    }
    
    public synchronized void refreshUsers()
    {
        users = userConfigService.getAllUsers();
    }
    
    @Override
    public synchronized List<UserConfig> getUserList()
    {
        if (null == users)
        {
            users = userConfigService.getAllUsers();
        }
        
        return users;
    }
    
    @Override
    public synchronized UserConfig getUserById(String userId)
    {
        if (null == users)
        {
            users = userConfigService.getAllUsers();
        }
        
        for (UserConfig user : users)
        {
            if (user.getUserId().equals(userId))
            {
                return user;
            }
        }
        
        return null;
    }
    
    @Override
    public synchronized boolean checkUser(String userId, String password)
    {
        UserConfig user = getUserById(userId);
        if (null == user)
        {
            return false;
        }
        String pwdWithSHA256 = SHA256Utils.encrypt(user.getPassword());
        if (password.equals(pwdWithSHA256))
        {
            return true;
        }
        
        return false;
    }
    
    public IUserConfigService getUserConfigService()
    {
        return userConfigService;
    }
    
    public void setUserConfigService(IUserConfigService userConfigService)
    {
        this.userConfigService = userConfigService;
    }
    
    public Scheduler getScheduler()
    {
        return scheduler;
    }
    
    public void setScheduler(Scheduler scheduler)
    {
        this.scheduler = scheduler;
    }
}
