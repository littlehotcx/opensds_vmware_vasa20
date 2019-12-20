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

import org.opensds.platform.authorize.itf.IAuthorizeAdapter;
import org.opensds.platform.authorize.itf.IAuthorizeAdapterRegister;
import org.opensds.platform.interceptor.AppAuthorize;

public class AuthorizeAdapterRegister implements IAuthorizeAdapterRegister
{
    private AppAuthorize appAuthorize;
    
    private RestAppAuthorize restAppAuthorize;
    
    public AppAuthorize getAppAuthorize()
    {
        return appAuthorize;
    }

    public void setAppAuthorize(AppAuthorize appAuthorize)
    {
        this.appAuthorize = appAuthorize;
    }

    public RestAppAuthorize getRestAppAuthorize()
    {
        return restAppAuthorize;
    }

    public void setRestAppAuthorize(RestAppAuthorize restAppAuthorize)
    {
        this.restAppAuthorize = restAppAuthorize;
    }

    @Override
    public void regAuthorizeAdapter(IAuthorizeAdapter authorizeAdapter)
    {
        if ("rest".equalsIgnoreCase(authorizeAdapter.getAdapterType()))
        {
            if (null != restAppAuthorize)
            {
                restAppAuthorize.regAuthorizeAdapter(authorizeAdapter);
            }
        }
        else if ("all".equalsIgnoreCase(authorizeAdapter.getAdapterType()))
        {
            if (null != restAppAuthorize)
            {
                restAppAuthorize.regAuthorizeAdapter(authorizeAdapter);
            }
            if (null != appAuthorize)
            {
                appAuthorize.regAuthorizeAdapter(authorizeAdapter);
            }
        }
        else if ("soap".equalsIgnoreCase(authorizeAdapter.getAdapterType()))
        {
            if (null != appAuthorize)
            {
                appAuthorize.regAuthorizeAdapter(authorizeAdapter);
            }
        }
    }    
}
