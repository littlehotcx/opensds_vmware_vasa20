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

import org.opensds.platform.common.bean.config.UserConfig;
import org.opensds.platform.common.bean.config.UserConfigHistory;
import org.opensds.platform.common.utils.AES128System;
import org.opensds.platform.common.utils.Base64Utils;
import org.opensds.platform.common.utils.StringUtils;
import org.opensds.platform.config.dao.UserFileDAO;
import org.opensds.platform.config.dao.itf.IUserDAO;
import org.opensds.platform.config.service.itf.IUserConfigService;

public final class UserConfigService extends BaseService implements IUserConfigService
{
    private static final Logger LOGGER = LogManager.getLogger(UserConfigService.class);
    
    private static IUserConfigService instance = new UserConfigService();
    
    private IUserDAO userDao;
    
    public static IUserConfigService getInstance()
    {
        return instance;
    }
    
    public UserConfigService()
    {
        try
        {
            String file = getFilePath("users.xml");
            
            userDao = new UserFileDAO(file);
        }
        catch (Exception e)
        {
            LOGGER.error("", e);
        }
    }
    
    @Override
    public boolean addUser(UserConfig user)
    {
        try
        {
            user.setPassword(Base64Utils.encode(AES128System.encryptPwdByNewKey((user.getUserId() + user.getPassword()).getBytes("UTF-8"))));
            boolean result = userDao.addUser(user);
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
    public boolean updateUser(UserConfig user)
    {
        try
        {
            user.setPassword(Base64Utils.encode(AES128System.encryptPwdByNewKey((user.getUserId() + user.getPassword()).getBytes("UTF-8"))));
           
            List<UserConfigHistory> hisList = user.getHisList();
            if(hisList != null)
            {
            	for(UserConfigHistory uch:hisList)
            	{
            		uch.setPassword(Base64Utils.encode(AES128System.encryptPwdByNewKey((uch.getUserId()+uch.getPassword()).getBytes("UTF-8"))));
            	}
            }
            
            boolean result = userDao.updateUser(user);
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
    public boolean deleteUser(String userId)
    {
        boolean result = userDao.deleteUser(userId);
        if (result)
        {
            notifyObservers();
        }
        
        return result;
    }
    
    @Override
    public UserConfig getUserById(String userId)
    {
        return userDao.getUserById(userId);
    }
    
    @Override
    public List<UserConfig> getAllUsers()
    {
        List<UserConfig> result = userDao.getAllUsers();
        if (null != result && !result.isEmpty())
        {
            for (UserConfig user : result)
            {
                if (null != user && !StringUtils.isEmpty(user.getPassword()))
                {
                    user.setPassword(AES128System.decryptPwdByOldKey(user.getUserId(), user.getPassword()));
                }
                List<UserConfigHistory> hisList = user.getHisList();
                if(hisList != null)
                {
                	for(UserConfigHistory hisUser:hisList)
                	{
                		hisUser.setPassword(AES128System.decryptPwdByOldKey(hisUser.getUserId(), hisUser.getPassword()));
                	}
                }
            }
        }
        
        return result;
    }
    
    public IUserDAO getUserDao()
    {
        return userDao;
    }
    
    public void setUserDao(IUserDAO userDao)
    {
        this.userDao = userDao;
    }
}
