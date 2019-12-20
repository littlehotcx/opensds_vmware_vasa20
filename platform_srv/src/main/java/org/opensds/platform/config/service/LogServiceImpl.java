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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.opensds.platform.common.bean.log.LogBean;
import org.opensds.platform.common.utils.FileAttributeUtility;
import org.opensds.platform.common.utils.PathUtil;
import org.opensds.platform.common.utils.StringUtils;
import org.opensds.platform.config.dao.LogDaoImpl;
import org.opensds.platform.config.service.itf.ILogService;
import org.opensds.platform.config.dao.itf.ILogDao;


public final class LogServiceImpl implements ILogService
{

    private static ILogService instance = new LogServiceImpl();
    private static final Logger LOGGER = LogManager.getLogger(LogServiceImpl.class);

    private ILogDao logDao;
    
    public static ILogService getInstance()
    {
        return instance;
    }

    private LogServiceImpl()
    {
        logDao = new LogDaoImpl();
    }

    @Override
    public List<LogBean> listLogs(Long beginTime, Long endTime,Integer pageSize)
    {
        return logDao.listLogs(beginTime, endTime,pageSize);
    }

    @Override
    public void closeReader()
    {
        logDao.closeReader();
    }

    /**
	 *codedex 	
	 *FORTIFY.HW_-_Create_files_with_appropriate_access_permissions_in_multiuser_system
	 *FORTIFY.Unreleased_Resource--Streams    
	 *nwx356892 
	 */
	@Override
	public boolean logUploadSwitch(boolean isUpload)
	{
		String path = null;
		try {
			path = PathUtil.getAppPath(this.getClass());
		} catch (Exception e1) {
			return false;
		}
		
		if(null == path)
		{
			return false;
		}
		if(path.endsWith("/"))
		{
			path = path.substring(0, path.length() - 1);
		}
        path = path.substring(0, path.lastIndexOf("esdk"));
        File confFilePath = new File(path + "esdk/webcontent/conf/platform_common_ext_conf.properties");
        LOGGER.info("configFilePath: "+path + "esdk/webcontent/conf/platform_common_ext_conf.properties");
        
        if(!confFilePath.exists())
        {
        	return false;
        }
        InputStream is = null;
        OutputStream out = null;
        try
        {
        	is = new FileInputStream(confFilePath);
        	List<String> fileLines = IOUtils.readLines(is, "UTF-8");
        	int index = 0;
        	for(String line : fileLines)
        	{
        		if (StringUtils.isNotEmpty(line)&& (line.startsWith("log.upload.switch=") || line.startsWith("log.upload.switch =")))
        		{
        			if(isUpload)
        			{
        				fileLines.set(index, "log.upload.switch=true");
        			}
        			else
        			{
        				fileLines.set(index, "log.upload.switch=false");
        			}
        			break;
        		}
        		index++;
        	}
        	out = FileAttributeUtility.getSafeOutputStream(confFilePath.getAbsolutePath(),false);
        	IOUtils.writeLines(fileLines, null, out, "UTF-8");
        }
        catch (IOException e)
        {
            return false;
        }
        finally
        {
        	/**
			 * CodeDEX modified by wWX315527 2017/02/07 start
			 * FORTIFY.Unreleased_Resource--Streams
			 */
        	try
            {
                if (null != is)
                {
                    is.close();
                }
            }
            catch (IOException e)
            {
                LOGGER.error("", e);
            }
        	try
            {
                if (null != out)
                {
                	out.close();
                }
            }
            catch (IOException e)
            {
                LOGGER.error("", e);
            }
        	
        	// ESDKIOUtils.closeFileStreamNotThrow(is);
        	// ESDKIOUtils.closeFileStreamNotThrow(out);
        	/**
			 * CodeDEX modified by wWX315527 2017/02/07 end
			 * FORTIFY.Unreleased_Resource--Streams
			 */
        }
        
        return true;
	}

}
