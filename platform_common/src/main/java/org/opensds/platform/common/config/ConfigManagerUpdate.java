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
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensds.platform.common.bean.config.ConfigFile;
import org.opensds.platform.common.utils.AES128System;
import org.opensds.platform.common.utils.ESDKIOUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.core.io.support.ResourcePatternResolver;

import org.opensds.platform.common.bean.config.ConfigItem;
import org.opensds.platform.common.utils.Base64Utils;
import org.opensds.platform.common.utils.FileAttributeUtility;
import org.opensds.platform.common.utils.StringUtils;

public final class ConfigManagerUpdate
{
    private static final Logger LOGGER = LogManager.getLogger(ConfigManagerUpdate.class);
    
    private static final List<ConfigFile> CONFIG_FILES = new ArrayList<ConfigFile>(8);
    
    private static final Map<String, ConfigItem> CONFIG_ITEMS = new HashMap<String, ConfigItem>(64);
    
    public static List<String> sensitiveList = new ArrayList<String>();
    
    private static List<String> sensitiveCsv = new ArrayList<String>();
    
    static
    {
        try
        {
            loadConfigs();
        }
        catch (Exception e)
        {
            LOGGER.error("", e);
        }
        getSensitives();
        updateProperties();
        updateSSLProperties();
    }
    
    public static void init()
    {
        
    }
    
    private static void updateSSLProperties() {
		// TODO Auto-generated method stub
		for (ConfigFile file : CONFIG_FILES) {
			if(file.getFileName().equalsIgnoreCase(SSLConfigManager.SSL_CONFIG_FILE)){
				try {
					SSLConfigManager.loadConfigs(file.getFilePath());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					LOGGER.error("Load ssl properties fail.",e);
				}
			}
		}
	}

	private static void loadConfigs()
        throws Exception
    {
        loadConfig("classpath*:*properties");
        loadConfig("classpath*:META-INF/*.properties");
    }
    
    private static void loadConfig(String pattern)
        throws Exception
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
    
    private static void processConfigFile(Resource resource)
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
    
    private static List<ConfigItem> parseConfigFile(Properties props)
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
    
    public static List<ConfigFile> getConfigFiles()
    {
        return CONFIG_FILES;
    }
    
    private synchronized static void updateProperties()
    {
        List<ConfigFile> configFiles = getConfigFiles();
        if (null != configFiles && !configFiles.isEmpty())
        {
            List<String> fileLines = null;
            for (ConfigFile configFile : configFiles)
            {
                if (!StringUtils.isEmpty(configFile.getFilePath()))
                {
                    fileLines = readConfigFile(configFile.getFilePath());
                }
                
                List<ConfigItem> configList = configFile.getConfigList();
                boolean needUpdate = false;
                if (null != configList && !configList.isEmpty())
                {
                    for (ConfigItem configItem : configList)
                    {
                        if (null != configItem && !StringUtils.isEmpty(configItem.getKey()))
                        {
                            String value = configItem.getValue();
                            if (sensitiveList.contains(configItem.getKey()))
                            {
                                if (!StringUtils.isEmpty(value))
                                {
                                    value = AES128System.decryptPwdByOldKey("", value);
                                    try
                                    {
                                        value = Base64Utils.encode(AES128System.encryptPwdByNewKey(value.getBytes("UTF-8")));
                                    }
                                    catch (UnsupportedEncodingException e)
                                    {
                                        LOGGER.error("UnsupportedEncodingException: ", e);
                                        value = "";
                                    }
                                    
                                    if (null != fileLines && !fileLines.isEmpty())
                                    {
                                        for (int i = 0; i < fileLines.size(); i++)
                                        {
                                            String fileLine = fileLines.get(i);
                                            if (!StringUtils.isEmpty(fileLine) && !fileLine.startsWith("#"))
                                            {
                                                fileLine = fileLine.replaceAll(" ", "");
                                                if (fileLine.startsWith(configItem.getKey() + "="))
                                                {
                                                    fileLines.set(i, configItem.getKey() + "=" + value);
                                                    needUpdate = true;
                                                }
                                            }
                                        }
                                    }
                                }
                            }else if (sensitiveCsv.contains(configItem.getKey()))
                            {
                                if (!StringUtils.isEmpty(value))
                                {
                                    String[] pwds = value.split(",");
                                    String valueTemp = "";
                                    for (int i = 0; i < pwds.length; i++)
                                    {
                                        String temp = AES128System.decryptPwdByOldKey("", pwds[i]);
                                        try
                                        {
                                            temp =
                                                Base64Utils.encode(AES128System.encryptPwdByNewKey(temp.getBytes("UTF-8")));
                                        }
                                        catch (UnsupportedEncodingException e)
                                        {
                                            LOGGER.error("UnsupportedEncodingException: ", e);
                                            temp = "";
                                        }
                                        
                                        valueTemp = valueTemp + "," + temp;
                                    }
                                    value = valueTemp.replaceFirst(",", "");
                                    
                                    if (null != fileLines && !fileLines.isEmpty())
                                    {
                                        for (int i = 0; i < fileLines.size(); i++)
                                        {
                                            String fileLine = fileLines.get(i);
                                            if (!StringUtils.isEmpty(fileLine) && !fileLine.startsWith("#"))
                                            {
                                                fileLine = fileLine.replaceAll(" ", "");
                                                if (fileLine.startsWith(configItem.getKey() + "="))
                                                {
                                                    fileLines.set(i, configItem.getKey() + "=" + value);
                                                    needUpdate = true;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (needUpdate)
                    {
                        if (!StringUtils.isEmpty(configFile.getFilePath()))
                        {
                            saveConfigFile(configFile.getFilePath(), fileLines);
                        }
                    }
                }
            }
        }
    }
    
    public static List<String> readConfigFile(String filePath)
    {
        LOGGER.info("ConfigManagerUpdate.readConfigFile(): " + filePath);
        List<String> fileLines = null;
        InputStream is = null;
        try
        {
            is = new FileInputStream(filePath);
            fileLines = IOUtils.readLines(is, "UTF-8");
        }
        catch (IOException e1)
        {
            LOGGER.error("ConfigManagerUpdate.readConfigFile() error", e1);
        }
        finally
        {
            if (null != is)
            {
                try
                {
                    is.close();
                }
                catch (IOException e)
                {
                    LOGGER.error("ConfigManagerUpdate.readConfigFile() error", e);
                }
            }
        }
        return fileLines;
    }
    
    /**
	 *codedex 	
	 *FORTIFY.HW_-_Create_files_with_appropriate_access_permissions_in_multiuser_system
	 *FORTIFY.Unreleased_Resource--Streams    
	 *nwx356892 
	 */
    public static void saveConfigFile(String filePath, List<String> fileLines)
    {
        LOGGER.info("ConfigManagerUpdate.saveConfigFile(): " + filePath);
        OutputStream out = null;
        try
        {
            out = FileAttributeUtility.getSafeOutputStream(filePath,false);
            IOUtils.writeLines(fileLines, null, out, "UTF-8");
        }
        catch (IOException e)
        {
            LOGGER.error("ConfigManagerUpdate.saveConfigFile() error", e);
        }
        finally
        {
        	ESDKIOUtils.closeFileStreamNotThrow(out);
        }
    }
    
    private static void getSensitives()
    {
        String sensitive = ConfigManagerNoDecrypt.getInstance().getValue("platform.config.sensitive.words");
        if (!StringUtils.isEmpty(sensitive))
        {
            String[] sens = sensitive.split(",");
            sensitiveList.addAll(Arrays.asList(sens));
        }
        
        sensitive = ConfigManagerNoDecrypt.getInstance().getValue("platform.config.sensitive.wordcsv");
        if (!StringUtils.isEmpty(sensitive))
        {
            String[] sens = sensitive.split(",");
            sensitiveCsv.addAll(Arrays.asList(sens));
        }
        LOGGER.info("platform.config.sensitive.words: " + sensitive);
    }
}
