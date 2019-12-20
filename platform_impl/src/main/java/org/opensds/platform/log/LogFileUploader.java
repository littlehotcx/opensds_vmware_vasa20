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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensds.platform.common.config.ConfigManager;
import org.opensds.platform.common.utils.StringUtils;

public class LogFileUploader
{
    private static final Logger LOGGER = LogManager.getLogger(LogFileUploader.class);
    
    private String logServerURL;
    
    private String logUploadSwitch;
    
    private ExecutorService es = Executors.newFixedThreadPool(1);
    
    public void init()
    {
        logServerURL = ConfigManager.getInstance().getValue("log.server.url");
        
        logUploadSwitch = ConfigManager.getInstance().getValue("log.upload.switch");
        
        LOGGER.info("logServerURL: " + logServerURL);
        LOGGER.info("logUploadSwitch: " + logUploadSwitch);
        
        if ("true".equalsIgnoreCase(logUploadSwitch) && StringUtils.isNotEmpty(logServerURL))
        {
            es.execute(new LogFileUploaderTask());
            LOGGER.info("The Log uploader thread started");
        }
    }
    
    public void destroy()
    {
        es.shutdownNow();
    }
}
