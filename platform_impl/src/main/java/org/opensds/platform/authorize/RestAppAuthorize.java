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

package org.opensds.platform.authorize;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensds.platform.authorize.itf.IAuthorizeAdapter;

public class RestAppAuthorize
{
    private static final Logger LOGGER = LogManager.getLogger(RestAppAuthorize.class);
    
    private List<IAuthorizeAdapter> authorizeAdapters = new ArrayList<IAuthorizeAdapter>(1);
    
    private IAuthorizeAdapter defaultAuthorizeAdapter;
    
    public void regAuthorizeAdapter(IAuthorizeAdapter authorizeAdapter)
    {
        authorizeAdapters.add(authorizeAdapter);
    }
    
    public IAuthorizeAdapter getDefaultAuthorizeAdapter()
    {
        if (null == defaultAuthorizeAdapter)
        {
            defaultAuthorizeAdapter = new DefaultAuthorizeAdapter();
        }
        
        return defaultAuthorizeAdapter;
    }

    public int verify(String interfaceName, Map<String, String> message)
    {
        IAuthorizeAdapter authorizeAdapter = getAuthorizeAdapter(interfaceName, message);
        
        if (authorizeAdapter.needAuthorize(interfaceName, message))
        {
            String aaResult = null;
            try
            {                
                aaResult = authorizeAdapter.authorize(message);
            }
            catch (Exception e)
            {
                LOGGER.error("userLogin method error", e);
                return -1;
            }
            if (!"0".equals(aaResult))
            {
                LOGGER.warn("aaResult=" + aaResult);
                return -1;
            }
        }
        
        return 4;
    }
    
    private IAuthorizeAdapter getAuthorizeAdapter(String interfaceName, Map<String, String> message)
    {
        for (IAuthorizeAdapter item : authorizeAdapters)
        {
            if ("rest".equalsIgnoreCase(item.getAdapterType()) && item.reqMsgMatchesBuiness(interfaceName, message))
            {
                return item;
            }
        }
        
        return getDefaultAuthorizeAdapter();
    }
}
