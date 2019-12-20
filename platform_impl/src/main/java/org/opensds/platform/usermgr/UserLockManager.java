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

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.opensds.platform.common.bean.aa.UserLockInfo;
import org.opensds.platform.common.config.ConfigManager;
import org.opensds.platform.common.utils.DateUtils;
import org.opensds.platform.usermgr.itf.IUserLockManager;

public class UserLockManager implements IUserLockManager
{
    private static Map<String, UserLockInfo> LOCK_INFO_MAPPING = new ConcurrentHashMap<String, UserLockInfo>();
    
    private int retryMaxTimes;
    
    private int autoUnlockDuration;
    
    public UserLockManager()
    {
    }
    
    public void init()
    {
        String retryMaxTimesStr = ConfigManager.getInstance().getValue("login.failed.max.allowed.times");
        String autoUnlockDurationStr = ConfigManager.getInstance().getValue("locked.user.auto.unlock.duration");
        try
        {
            retryMaxTimes = Integer.parseInt(retryMaxTimesStr);
        }
        catch (NumberFormatException e)
        {
            retryMaxTimes = 30;
        }
        
        try
        {
            autoUnlockDuration = Integer.parseInt(autoUnlockDurationStr);
        }
        catch (NumberFormatException e)
        {
            autoUnlockDuration = 30;
        }
    }
    
    @Override
    public boolean lockUser(String userId)
    {
        UserLockInfo userLockInfo = getUserLockInfo(userId);
        userLockInfo.setLocked(true);
        return true;
    }
    
    @Override
    public boolean unlockUser(String userId)
    {
        UserLockInfo userLockInfo = getUserLockInfo(userId);
        userLockInfo.setLocked(false);
        return true;
    }
    
    @Override
    public boolean tryUnlockUser(String userId)
    {
        UserLockInfo userLockInfo = getUserLockInfo(userId);
        if (userLockInfo.isLocked())
        {
            if (DateUtils.getCurrentDate().getTime() - userLockInfo.getLastRetryTime().getTime() > 1000L * 60 * autoUnlockDuration)
            {
                userLockInfo.setLocked(false);
                userLockInfo.setRetryTimes(0);
                return true;
            }
            
            return false;
        }
        else
        {
            return true;
        }
    }
    
    @Override
    public boolean isUserLocked(String userId)
    {
        UserLockInfo userLockInfo = getUserLockInfo(userId);
        return userLockInfo.isLocked();
    }
    
    @Override
    public int getRetryTimes(String userId)
    {
        UserLockInfo userLockInfo = getUserLockInfo(userId);
        return userLockInfo.getRetryTimes();
    }
    
    @Override
    public void increaseRetryTimes(String userId)
    {
        UserLockInfo userLockInfo = getUserLockInfo(userId);
        userLockInfo.setRetryTimes(userLockInfo.getRetryTimes() + 1);
    }
    
    @Override
    public void resetRetryTimes(String userId)
    {
        UserLockInfo userLockInfo = getUserLockInfo(userId);
        userLockInfo.setRetryTimes(0);
    }
    
    @Override
    public void setRetryTime(String userId, Date date)
    {
        UserLockInfo userLockInfo = getUserLockInfo(userId);
        userLockInfo.setLastRetryTime(date);
    }
    
    @Override
    public boolean reachMaxRetryTimes(String userId)
    {
        UserLockInfo userLockInfo = getUserLockInfo(userId);
        if (userLockInfo.getRetryTimes() >= retryMaxTimes)
        {
            return true;
        }
        
        return false;
    }
    
    private UserLockInfo getUserLockInfo(String userId)
    {
        UserLockInfo userLockInfo = LOCK_INFO_MAPPING.get(userId);
        
        if (null == userLockInfo)
        {
            userLockInfo = new UserLockInfo();
            userLockInfo.setUserId(userId);
            LOCK_INFO_MAPPING.put(userId, userLockInfo);
        }
        
        return userLockInfo;
    }
}
