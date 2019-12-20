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

package org.opensds.platform.log;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.FileAppender;

import org.opensds.platform.common.config.ConfigManager;
import org.opensds.platform.common.utils.NumberUtils;
import org.opensds.platform.common.utils.StringUtils;
import org.opensds.platform.util.PathUtils;

public abstract class LogFileUploaderHelper
{
    private static final Logger LOGGER = LogManager.getLogger(LogFileUploaderHelper.class);
    
    private static final String log_backup_run = "/backup/run";
    private static final String log_backup_interface = "/backup/interface";
    private static final String log_backup_operation = "/backup/operation";
    
    private static String loggerPath;
    
    static
    {
        String file = "";
//        @SuppressWarnings("unchecked")
//        Enumeration<Appender> appenders = LogManager.getRootLogger().getAllAppenders();
        
        /**
		 *codedex NULL_RETURNS nwx356892 
		 */
        LoggerContext context = (LoggerContext)LogManager.getContext(false);
        Map<String, Appender> appendersMap = context.getConfiguration().getAppenders();
        if(null != appendersMap){
        	Set<String> keySet = appendersMap.keySet();
        	for (String key : keySet) {
        		Appender app = appendersMap.get(key);
        		if (app instanceof FileAppender)
	            {
	                file = ((FileAppender)app).getFileName();
	                break;
	            }
        	}
        	if (StringUtils.isNotEmpty(file))
	        {
	            File logFile = new File(file);
	            if (logFile.isFile())
	            {
	                file = logFile.getParent();
	            }
	        }
        }
        
        /*if(null != appenders){
        	
	        while (appenders.hasMoreElements())
	        {
	            Appender app = appenders.nextElement();
	            if (app instanceof FileAppender)
	            {
	                file = ((FileAppender)app).getFile();
	                break;
	            }
	        }
	        
	        if (StringUtils.isNotEmpty(file))
	        {
	            File logFile = new File(file);
	            if (logFile.isFile())
	            {
	                file = logFile.getParent();
	            }
	        }
        }*/
        loggerPath = file;
        
        String[] dirNames = new String[] {log_backup_run, log_backup_interface, log_backup_operation};
        File dir;
        for (String dirName : dirNames)
        {
            dir = new File(loggerPath + dirName);
            if (!dir.exists())
            {
                dir.mkdirs();
            }
        }
       
    }
    
    public static void setLoggerLevel(String packageName, String levelName)
    {
        LOGGER.debug("packageName=" + packageName + ", levelName=" + levelName);
        Level level = Level.toLevel(levelName);
        if ("".equals(packageName))
        {
            Logger logger = LogManager.getRootLogger();
            //logger.setLevel(level);
        }
        Logger logger = LogManager.getLogger(packageName);
        if (null != logger)
        {
            //logger.setLevel(level);
        }
    }
    
    public static String getLogPath(String logType)
    {
        String file = loggerPath;
        if ("interface".equalsIgnoreCase(logType))
        {
            file = loggerPath + File.separator + "interface_log";
        }
        else if ("operation".equalsIgnoreCase(logType))
        {
            file = loggerPath + File.separator + "operation_log";
        }
        
        return file;
    }
    
    public static String getOldestLogFile(String logType)
    {
        List<String> list = getLogFiles(new File(getLogPath(logType)));
        if (list.size() > 0)
        {
            return list.get(0);
        }
        
        return null;
    }
    
    public static List<String> getLogFiles(File dir)
    {
        List<String> result = new ArrayList<String>();
        File[] files = null;
        if (null != dir)
        {
            files = dir.listFiles();
        }
        
        if (null != files)
        {
            Arrays.sort(files, new Comparator<File>()
            {
                public int compare(File f1, File f2)
                {
                	int result = Long.compare(f1.lastModified(),f2.lastModified());
                	new Integer(1);
                		
                	if(result == 0)
                	{
                		if(f1.getName().endsWith(".log") && !f2.getName().endsWith(".log"))
                		{
                			return 1;
                		}
                		else if(!f1.getName().endsWith(".log") && f2.getName().endsWith(".log"))
                		{
                			return -1;
                		}
                	}
                    return result;
                }
            });
            
            for (File file : files)
            {
                if (!file.isDirectory())
                {
                    LOGGER.debug("file name=" + file.getName());
                    if (file.getName().startsWith("eSDK-Server"))
                    {
                        
                        result.add(file.getPath());
                    }
                }
            }
        }
        
        return result;
    }
    
    public static void deleteLogFile(String fileNameWithPath)
    {
        File file = new File(fileNameWithPath);
        if (file.exists() && !file.delete())
        {
            LOGGER.warn(fileNameWithPath + " deleted failed");
        }
    }
    
    public static boolean isBackLogFile(String fileNameWithPath)
    {
        if (fileNameWithPath.contains(".log."))
        {
            return true;
        }
        return false;
    }

    public static void backup(String logFile, String logType)
    {
    	/*
         * CodeDex:  FORTIFY.HW_-_Path_Manipulation      by nWX285177
         */
        File src = new File(PathUtils.FilePathFormatWithEncode(logFile, "UTF-8"));
        
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss_SSS");
        String fileName = src.getName() + "." + sf.format(new Date()) + ".bak";
        
        
        File dest;
        
        if ("interface".equalsIgnoreCase(logType))
        {
            dest = new File(loggerPath + log_backup_interface + File.separator + fileName);
        }
        else if ("operation".equalsIgnoreCase(logType))
        {
            dest = new File(loggerPath + log_backup_operation + File.separator + fileName);
        }
        else
        {
            dest = new File(loggerPath + log_backup_run + File.separator + fileName);
        }
        
        try
        {
            FileUtils.moveFile(src, dest);
        }
        catch (IOException e)
        {
            LOGGER.error("", e);
            LOGGER.info("back file " + src.getName() + " failed");
        }
    }
    
    public static void deleteBackupFile()
    {
        int remainDays = NumberUtils.parseIntValue(ConfigManager.getInstance().getValue("platform.backup.log.file.delete", "90"));
        String[] dirNames = new String[] {log_backup_run, log_backup_interface, log_backup_operation};
        final Long remainTime = new Date().getTime() - remainDays * 24 * 60 * 60 * 1000L;
        
        File dir;
        File[] files;
        for (String dirName : dirNames)
        {
            dir = new File(loggerPath + dirName);
            if (dir.exists())
            {
                files = dir.listFiles(new FileFilter()
                {
                    @Override
                    public boolean accept(File file)
                    {
                        if (file.lastModified() < remainTime)
                        {
                            return true;
                        }
                        return false;
                    }
                    
                });
                
                if (null != files)
                {
                    for (File file : files)
                    {
                        if (file.delete())
                        {
                            LOGGER.info("The backup file " + file.getName() + " was deleted successed!");
                        }
                        else
                        {
                            LOGGER.info("The backup file " + file.getName() + " was deleted failed!");
                        }
                    }
                }
            }
        }
    }
}
