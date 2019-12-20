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

package org.opensds.platform.common.config;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensds.platform.common.bean.config.ConfigFile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.core.io.support.ResourcePatternResolver;

import org.opensds.platform.common.bean.config.ConfigItem;
import org.opensds.platform.common.utils.ESDKIOUtils;
import org.opensds.platform.common.utils.StringUtils;

public final class ConfigManagerNoDecrypt
{
    private static final Logger LOGGER = LogManager.getLogger(ConfigManagerNoDecrypt.class);
    
    private static final List<ConfigFile> CONFIG_FILES = new ArrayList<ConfigFile>(8);
    
    private static final Map<String, ConfigItem> CONFIG_ITEMS = new HashMap<String, ConfigItem>(64);
    
    private static ConfigManagerNoDecrypt instance = new ConfigManagerNoDecrypt();
    
    private ConfigManagerNoDecrypt()
    {
        try
        {
            loadConfigs();
        }
        catch (Exception e)
        {
            LOGGER.error("", e);
        }
    }
    
    public static ConfigManagerNoDecrypt getInstance()
    {
        return instance;
    }
    
    private void loadConfigs()
        throws Exception
    {
        LOGGER.info("load config: platform_common_in_conf.properties");
    	loadConfig("classpath*:platform_common_in_conf.properties");
    	loadConfig("classpath*:META-INF/platform_common_in_conf.properties");
    	
    	LOGGER.info("load config: config_tool_in_conf.properties");
    	loadConfig("classpath*:config_tool_in_conf.properties");
    	loadConfig("classpath*:META-INF/config_tool_in_conf.properties");
    	
    	LOGGER.info("load config: platform_mgmt_in_conf.properties");
    	loadConfig("classpath*:platform_mgmt_in_conf.properties");
    	loadConfig("classpath*:META-INF/platform_mgmt_in_conf.properties");
    }
    
    private void loadConfig(String pattern)throws Exception
    {
    	ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources(pattern);
        
        if (null != resources)
        {
            for (Resource item : resources)
            {
                processConfigFile(item);
            }
        }
    }
    
    private void processConfigFile(Resource resource)
        throws IOException
    {
    	String absPath = null;
    	if (null != resource.getURL() && !resource.getURL().getFile().contains("jar!"))
    	{
    		absPath = resource.getFile().getAbsolutePath();
    	}
        LOGGER.info("Loading configuration file " + resource.getFilename() + " from " + absPath + "|" + resource);
        Properties props = PropertiesLoaderUtils.loadProperties(resource);
        ConfigFile configFile = new ConfigFile();
        configFile.setFileName(resource.getFilename());
        configFile.setFilePath(absPath);
        configFile.setConfigList(parseConfigFile(props));
        CONFIG_FILES.add(configFile);
    }
    
    private List<ConfigItem> parseConfigFile(Properties props)
    {
        List<ConfigItem> result = new ArrayList<ConfigItem>();
        if (null != props)
        {
            ConfigItem configItem;
            for (Entry<Object, Object> config : props.entrySet())
            {
                configItem = new ConfigItem();
                configItem.setKey((String)config.getKey());
                configItem.setValue(StringUtils.trim((String)config.getValue()));
                result.add(configItem);
                CONFIG_ITEMS.put((String)config.getKey(), configItem);
            }
        }
        
        return result;
    }
    
    public Map<String, ConfigItem> getAllConfigs()
    {
        return CONFIG_ITEMS;
    }
    
    public List<ConfigFile> getConfigFiles()
    {
        return CONFIG_FILES;
    }
    
    public String getValue(String key)
    {
        return getValue(key, null);
    }
    
    public String getPureValue(String key)
    {
        String fileName = null;
        for (ConfigFile item : CONFIG_FILES)
        {
            for (ConfigItem inItem : item.getConfigList())
            {
                if (inItem.getKey().equals(key))
                {
                    fileName = item.getFilePath();
                    break;
                }
            }
            
            if (null != fileName)
            {
                break;
            }
        }
        
        if (null != fileName)
        {
            InputStream is = null;
            try
            {
                is = new FileInputStream(fileName);
                List<String> contents = IOUtils.readLines(is);
                for (String line : contents)
                {
                    if (line.startsWith(key))
                    {
                        return line.split("=")[1];
                    }
                }
            }
            catch (FileNotFoundException e)
            {
                LOGGER.error("", e);
            }
            catch (IOException e)
            {
                LOGGER.error("", e);
            }
            finally
            {
                ESDKIOUtils.closeInputStream(is);
            }
        }
        
        return null;
    }
    
    public String getValue(String key, String defaultValue)
    {
        ConfigItem configItem = CONFIG_ITEMS.get(key);
        String result = null;
        if (null != configItem)
        {
            result = configItem.getValue();
        }
        else
        {
            LOGGER.warn("Cannot find the configuration key of " + key);
        }
        
        if (null == result)
        {
            result = defaultValue;
        }
        
        return result;
    }
}
