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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.opensds.platform.common.utils.ESDKIOUtils;
import org.opensds.platform.common.utils.PathUtil;
import org.opensds.platform.common.bean.log.LogBean;
import org.opensds.platform.common.utils.ReverseLineInputStream;
import org.opensds.platform.config.dao.itf.ILogDao;
import org.opensds.platform.config.util.PathUtils;

public class LogDaoImpl implements ILogDao
{
    private static final Logger LOGGER = LogManager.getLogger(LogDaoImpl.class);
    
    private List<LogBean> list = null;
    
    private BufferedReader reader = null;
    
    private List<File> fileList = null;
    
    // 读到第几个文件
    int i = 0;
    
    // 读到20行中的第几行
    int j = 0;
    
    boolean read = false;
    
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
    
    @Override
    public List<LogBean> listLogs(Long begin, Long end, Integer pageSize)
    {
        list = new ArrayList<LogBean>();
        
        if (reader == null)
        {
            File fileD = null;
            try
            {
                fileD = getLogsPath();
            }
            catch (Exception e1)
            {
                LOGGER.error("", e1);
            }
            if (null == fileD)
            {
                return list;
            }
            File[] files = fileD.listFiles();
            
            fileList = new ArrayList<File>();
            Arrays.sort(files, new LastModifiedFileComparator());
            int index = 0;
            for (File file : files)
            {
                if (file.lastModified() >= begin && file.lastModified() <= end)
                {
                    fileList.add(file);
                    index++;
                }
            }
            if (index == 0)
            {
                fileList.add(files[0]);
            }
            else if (index > 0 && index < fileList.size())
            {
                fileList.add(files[index]);
            }
            i = fileList.size() - 1;
            ReverseLineInputStream ris = null;
            InputStreamReader isr = null;
            try
            {
            	ris = new ReverseLineInputStream(fileList.get(i));
            	isr = new InputStreamReader(ris, "UTF-8");
                reader = new BufferedReader(isr);
            }
            catch (FileNotFoundException e)
            {
                LOGGER.error("", e);
            }
            catch (UnsupportedEncodingException e)
            {
                LOGGER.error("", e);
            }finally {
    			ESDKIOUtils.closeFileStreamNotThrow(ris);
            	ESDKIOUtils.closeFileStreamNotThrow(isr);
    		}
        }
        
        return readPageSizeLogs(begin, end, pageSize);
    }
    
    private List<LogBean> readPageSizeLogs(Long begin, Long end, Integer pageSize)
    {
        LogBean log = null;
        String temp = "";
        FileInputStream fis = null;
        InputStreamReader isr = null;
        try
        {
        	int line = 0;
        	do{
        		StringBuffer lineBuffer = new StringBuffer();
        		line = ESDKIOUtils.readLine(reader, lineBuffer);
        		temp = lineBuffer.toString();
                String[] logStr = temp.split(",");
                if (sdf.parse(logStr[3]).getTime() < begin || sdf.parse(logStr[3]).getTime() > end)
                {
                    if (read == false)
                    {
                        continue;
                    }
                    else
                    {
                        return list;
                    }
                }
                read = true;
                log = new LogBean();
                
                log.setActionName(logStr[0]);
                log.setIp(logStr[1]);
                log.setPort(logStr[2]);
                log.setRequestTime(sdf.parse(logStr[3]));
                log.setResponseTime(sdf.parse(logStr[4]));
                log.setResultCode(logStr[5]);
                list.add(log);
                j++;
                if (pageSize == j)
                {
                    j = 0;
                    return list;
                }
        	}while(line==-1);
        	
            i--;
            if (i < 0)
            {
                return list;
            }
            reader.close();
            reader = null;
            
            fis = new FileInputStream(fileList.get(i)); 
            isr = new InputStreamReader(fis, "UTF-8");
            reader = new BufferedReader(isr);
            return readPageSizeLogs(begin, end, pageSize - list.size());
        }
        catch (IOException e)
        {
            LOGGER.error("", e);
        }
        catch (ParseException e)
        {
            LOGGER.error("", e);
        }finally{
        	ESDKIOUtils.closeFileStreamNotThrow(fis);
        	ESDKIOUtils.closeFileStreamNotThrow(isr);
        }
        return list;
    }
    
    private File getLogsPath()
        throws Exception
    {
        String file = null;
        
        String path = PathUtil.getAppPath(this.getClass());
        if (null == path)
        {
            return null;
        }
        if (path.endsWith("/"))
        {
            path = path.substring(0, path.length() - 1);
        }
        path = path.substring(0, path.lastIndexOf("/"));
        
        /*
         * CodeDex:  FORTIFY.HW_-_Path_Manipulation      by nWX285177
         */
        File confFile = new File(PathUtils.FilePathFormatWithEncode(path + "/esdk/webcontent/WEB-INF/classes/log4j.properties", "UTF-8"));
        if (!confFile.exists())
        {
            return null;
        }
        InputStream is = null;
        Properties p = new Properties();
        try
        {
            is = new FileInputStream(confFile);
            p.load(is);
        }
        catch (IOException e)
        {
            LOGGER.error("", e);
            throw e;
        }
        finally
        {
            // ESDKIOUtils.closeInputStream(is);
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
        }
        String str = p.getProperty("log4j.appender.FILE1.File");
        
        if (str.contains("${catalina.base}"))
        {
            file = path + "/esdk" + str.substring(str.indexOf("/"), str.lastIndexOf("/"));
        }
        else
        {
            file = str.substring(0, str.lastIndexOf("/"));
        }
        p.clear();
        
        /*
         * CodeDex:  FORTIFY.Path_Manipulation      by nWX285177
         */
        if (!(new File(PathUtils.FilePathFormatWithEncode(file, "UTF-8"))).exists())
        {
            return null;
        }
        /*
         * CodeDex:  FORTIFY.HW_-_Path_Manipulation      by nWX285177
         */
        return new File(PathUtils.FilePathFormatWithEncode(file, "UTF-8"));
    }
    
    @Override
    public void closeReader()
    {
        if (reader != null)
        {
            try
            {
                reader.close();
                list = null;
                
                reader = null;
                
                fileList = null;
                
                // 读到第几个文件
                i = 0;
                
                // 读到20行中的第几行
                j = 0;
                
                read = false;
            }
            catch (IOException e)
            {
                LOGGER.error("", e);
            }
        }
    }
}
