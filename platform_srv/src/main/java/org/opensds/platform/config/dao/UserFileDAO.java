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

package org.opensds.platform.config.dao;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.tree.DefaultElement;

import org.opensds.platform.common.bean.config.UserConfig;
import org.opensds.platform.common.bean.config.UserConfigHistory;
import org.opensds.platform.common.utils.StringUtils;
import org.opensds.platform.config.dao.itf.IUserDAO;

public class UserFileDAO extends BaseFileDAO implements IUserDAO
{
    private static final Logger LOGGER = LogManager.getLogger(UserFileDAO.class);
    
    private Document document;
    
    private String file;
    
    public UserFileDAO(String file)
    {
        this.file = file;
        if (file != null)
        {
            try
            {
                SAXReader reader = new SAXReader();
                document = reader.read(new File(file));
            }
            catch (DocumentException e)
            {
                LOGGER.error("", e);
            }
        }
    }
    
    public UserFileDAO(InputStream is, String fileName)
    {
        this.file = fileName;
        try
        {
            SAXReader reader = new SAXReader();
            document = reader.read(is);
        }
        catch (DocumentException e)
        {
            LOGGER.error("", e);
        }
    }
    
    @Override
    public boolean addUser(UserConfig user)
    {
        if(!readUserFile())
        {
        	return false;
        }
        
        try
        {
            Element rootElm = document.getRootElement();
            Element userEle = new DefaultElement("user");
            userEle.add(buildElement("userId", user.getUserId()));
            userEle.add(buildElement("firstName", user.getFirstName()));
            userEle.add(buildElement("lastName", user.getLastName()));
            userEle.add(buildElement("password", user.getPassword()));
            userEle.add(buildElement("userType", user.getUserType() == null ? "" : user.getUserType()));
            userEle.add(buildElement("status", user.getStatus() == null ? "" : user.getStatus()));
            userEle.add(buildElement("remark", user.getRemark() == null ? "" : user.getRemark()));
            userEle.add(buildElement("reserve", user.getReserve() == null ? "" : user.getReserve()));
            userEle.add(buildElement("ability", user.getAbility() == null ? "" : user.getAbility()));
            rootElm.add(userEle);
            
            writeXmlFile(document, this.file);
        }
        catch (IOException e)
        {
            LOGGER.error("", e);
            return false;
        }
        
        return true;
    }
    
    @Override
    public boolean updateUser(UserConfig user)
    {
        if(!readUserFile())
        {
        	return false;
        }
        
        try
        {
            Element rootElm = document.getRootElement();
            Element element = null;
            for (@SuppressWarnings("unchecked")
            Iterator<Element> it = rootElm.elementIterator(); it.hasNext();)
            {
                element = it.next();
                if (element.element("userId").getTextTrim().equals(user.getUserId()))
                {
                    if (null == element.element("ability"))
                    {
                        element.add(buildElement("ability", user.getAbility() == null ? "" : user.getAbility()));
                    }
                    element.element("firstName").setText(user.getFirstName());
                    element.element("lastName").setText(user.getLastName());
                    element.element("password").setText(user.getPassword());
                    element.element("userType").setText(StringUtils.avoidNull(user.getUserType()));
                    element.element("status").setText(StringUtils.avoidNull(user.getStatus()));
                    element.element("remark").setText(StringUtils.avoidNull(user.getRemark()));
                    element.element("reserve").setText(StringUtils.avoidNull(user.getReserve()));
                    element.element("ability").setText(StringUtils.avoidNull(user.getAbility()));
                    
                    Element hisel = element.element("historys");
                    if(hisel != null)
                    {
                    	element.remove(hisel);
                    }
                    hisel = element.addElement("historys");
                    
                    if(user.getHisList() != null)
                    {
                    	for(UserConfigHistory uch:user.getHisList())
                    	{
                    		Element el = new DefaultElement("history");
                    		Element el_uid = new DefaultElement("userId");
                    		Element el_pas = new DefaultElement("password");
                    		Element el_cng = new DefaultElement("changeTime");
                    		
                    		el_uid.setText(uch.getUserId());
                    		el_pas.setText(uch.getPassword());
                    		el_cng.setText(uch.getChangeTime());
                    		
                    		el.add(el_uid);
                    		el.add(el_pas);
                    		el.add(el_cng);
                    		
                    		hisel.add(el);
                    	}
                    }
                    
                    writeXmlFile(document, this.file);
                    return true;
                }
            }
        }
        catch (IOException e)
        {
            LOGGER.error("", e);
            return false;
        }
        
        return false;
    }
    
    @Override
    public boolean deleteUser(String userId)
    {
        if(!readUserFile())
        {
        	return false;
        }
        
        try
        {
            Element rootElm = document.getRootElement();
            Element element = null;
            for (@SuppressWarnings("unchecked")
            Iterator<Element> it = rootElm.elementIterator(); it.hasNext();)
            {
                element = it.next();
                if (element.element("userId").getTextTrim().equals(userId))
                {
                    rootElm.remove(element);
                    writeXmlFile(document, this.file);
                    return true;
                }
            }
        }
        catch (IOException e)
        {
            LOGGER.error("", e);
            return false;
        }
        
        return false;
    }
    
    @Override
    public UserConfig getUserById(String userId)
    {
        List<UserConfig> users = getAllUsers();
        if (null != users && !users.isEmpty())
        {
            for (UserConfig user : users)
            {
                if (user.getUserId().equals(userId))
                {
                    return user;
                }
            }
        }
        
        return null;
    }
    
    @Override
    public List<UserConfig> getAllUsers()
    {
    	List<UserConfig> users = new ArrayList<UserConfig>();
        if (null == file)
        {
        	return users;
        }
        
        SAXReader reader = new SAXReader();
        reader.setEncoding("UTF-8");
        try
        {
            document = reader.read(new File(file));
        }
        catch (DocumentException e)
        {
            LOGGER.error("UserFileDAO.getAllUsers() error", e);
        }
        
        Element rootElm = document.getRootElement();
        @SuppressWarnings("unchecked")
        List<Element> elements = rootElm.elements("user");
        UserConfig user = null;
        for (Element ele : elements)
        {
            user = new UserConfig();
            user.setUserId(ele.element("userId").getTextTrim());
            user.setFirstName(ele.element("firstName").getTextTrim());
            user.setLastName(ele.element("lastName").getTextTrim());
            user.setPassword(ele.element("password").getTextTrim());
            user.setUserType(ele.element("userType").getTextTrim());
            user.setStatus(ele.element("status").getTextTrim());
            user.setRemark(ele.element("remark").getTextTrim());
            user.setAbility(null == ele.element("ability") ? "" : ele.element("ability").getTextTrim());
            user.setReserve(ele.element("reserve").getTextTrim());
			//add history pass
            List<UserConfigHistory> uchs = new ArrayList<UserConfigHistory>();
            Element historyElement = ele.element("historys");
            if(historyElement != null)
            {
				@SuppressWarnings("unchecked")
				List<Element> hisList = historyElement.elements("history");
				if(hisList != null)
				{
		            for(Element hisel:hisList)
		            {
		            	UserConfigHistory uch = new UserConfigHistory();
		            	uch.setUserId(hisel.element("userId").getTextTrim());
		            	uch.setPassword(hisel.element("password").getTextTrim());
		            	uch.setChangeTime(hisel.element("changeTime").getTextTrim());
		            	
		            	uchs.add(uch);
		            }
				}
            }
            
            user.setHisList(uchs);
            users.add(user);
        }
        
        return users;
    }
    
    private boolean readUserFile()
    {
        if (null == file)
        {
        	LOGGER.error("user file is null");
        	return false;
        }
        
        SAXReader reader = new SAXReader();
        reader.setEncoding("UTF-8");
        try
        {
            document = reader.read(new File(file));
        }
        catch (DocumentException e)
        {
        	LOGGER.error("", e);
        	return false;
        }
        
        return true;
    }
}
