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

import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import org.opensds.platform.common.utils.OSUtils;
import org.opensds.platform.config.service.itf.IConfigObserver;

public abstract class BaseService
{
    protected List<IConfigObserver> configObservers = new ArrayList<IConfigObserver>();
    
    protected String getFilePath(String configFile)
        throws Exception
    {
        String file = null;
        /**
    	 *codedex 	
    	 *FORTIFY.Missing_Check_against_Null
    	 *nwx356892 
    	 */
        URL url = null;
        ClassLoader classLoader = this.getClass().getClassLoader();
        if(classLoader!=null){
        	url = classLoader.getResource(configFile);
        }
        if (url != null)
        {
            file = url.getFile();
            if (OSUtils.isWindows())
            {
                if (file.startsWith("/"))
                {
                    file = file.substring(1);
                }
            }
        }
        if (file != null)
        {
        	file = URLDecoder.decode(file, "UTF-8");
        }
        return file;
    }
    
    public void registerObserver(IConfigObserver observer)
    {
        configObservers.add(observer);
    }
    
    protected void notifyObservers()
    {
        for (IConfigObserver observer : configObservers)
        {
            observer.doAction();
        }
    }
}
