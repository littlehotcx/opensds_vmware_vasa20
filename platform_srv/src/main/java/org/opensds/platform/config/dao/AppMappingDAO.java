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

import org.opensds.platform.common.bean.config.AppMappingConfig;
import org.opensds.platform.config.dao.itf.IAppMappingDAO;

public class AppMappingDAO extends BaseFileDAO implements IAppMappingDAO
{
    private static final Logger LOGGER = LogManager.getLogger(DeviceFileDAO.class);
    
    private Document document;
    
    private String file;
    
    public AppMappingDAO(String file)
    {
        this.file = file;
        if (file != null)
        {
            try
            {
                SAXReader reader = new SAXReader();
                reader.setEncoding("UTF-8");
                document = reader.read(new File(file));
            }
            catch (DocumentException e)
            {
                LOGGER.error("", e);
            }
        }
    }
    
    public AppMappingDAO(InputStream is, String fileName)
    {
        this.file = fileName;
        try
        {
            SAXReader reader = new SAXReader();
            reader.setEncoding("UTF-8");
            document = reader.read(is);
        }
        catch (DocumentException e)
        {
            LOGGER.error("", e);
        }
    }
    
    @Override
    public boolean addAppMapping(AppMappingConfig appMapping)
    {
        if(!readAppMapFile())
        {
        	return false;
        }
        
        try
        {
            Element rootElm = document.getRootElement();
            Element mappingEle = new DefaultElement("mapping");
            mappingEle.add(buildElement("deviceId", appMapping.getDeviceId()));
            mappingEle.add(buildElement("esdkApp", appMapping.getEsdkApp()));
            mappingEle.add(buildElement("deviceApp", appMapping.getDeviceApp()));
            mappingEle.add(buildElement("deviceAppPwd", appMapping.getDeviceAppPwd()));
            rootElm.add(mappingEle);
            
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
    public boolean updateAppMapping(AppMappingConfig appMapping)
    {
        if(!readAppMapFile())
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
                if (element.element("esdkApp").getTextTrim().equals(appMapping.getEsdkApp())
                    && element.element("deviceId").getTextTrim().equals(appMapping.getDeviceId()))
                {
                    element.element("deviceApp").setText(appMapping.getDeviceApp());
                    element.element("deviceAppPwd").setText(appMapping.getDeviceAppPwd());
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
    public boolean deleteAppMapping(String deviceId, String esdkApp)
    {
        if(!readAppMapFile())
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
                if (element.element("esdkApp").getTextTrim().equals(esdkApp))
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
    public AppMappingConfig getAppMappingByESDKApp(String esdkApp)
    {
        List<AppMappingConfig> appMappings = getAllAppMappings();
        if (null != appMappings && !appMappings.isEmpty())
        {
            for (AppMappingConfig appMapping : appMappings)
            {
                if (appMapping.getEsdkApp().equals(esdkApp))
                {
                    return appMapping;
                }
            }
        }
        
        return null;
    }
    
    @Override
    public List<AppMappingConfig> getAllAppMappings()
    {
    	List<AppMappingConfig> appMappings = new ArrayList<AppMappingConfig>();
        if (null == file)
        {
        	return appMappings;
        }
        
        SAXReader reader = new SAXReader();
        reader.setEncoding("UTF-8");
        try
        {
            document = reader.read(new File(file));
        }
        catch (DocumentException e)
        {
            LOGGER.error("AppMappingDAO.getAllAppMappings() error", e);
        }
        
        Element rootElm = document.getRootElement();
        @SuppressWarnings("unchecked")
        List<Element> elements = rootElm.elements("mapping");
        AppMappingConfig appMapping = null;
        for (Element ele : elements)
        {
            appMapping = new AppMappingConfig();
            appMapping.setDeviceId(ele.element("deviceId").getTextTrim());
            appMapping.setEsdkApp(ele.element("esdkApp").getTextTrim());
            appMapping.setDeviceApp(ele.element("deviceApp").getTextTrim());
            appMapping.setDeviceAppPwd(ele.element("deviceAppPwd").getTextTrim());
            
            appMappings.add(appMapping);
        }
        
        return appMappings;
    }
    
    private boolean readAppMapFile()
    {
        if (null == file)
        {
        	LOGGER.error("AppMap file is null");
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
