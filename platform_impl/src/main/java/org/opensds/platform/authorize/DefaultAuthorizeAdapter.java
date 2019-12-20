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

public class DefaultAuthorizeAdapter implements IAuthorizeAdapter
{
    
    @Override
    public boolean needAuthorize(String funcName, Object message)
    {
        return true;
    }

    @Override
    public String authorize(Object message)
    {
        return "1";
    }

    @Override
    public String getBusinessName()
    {
        return "";
    }

    @Override
    public boolean reqMsgMatchesBuiness(String funcName, Object message)
    {
        return true;
    }

    @Override
    public String getAdapterType()
    {
        return "all";
    }
    
}
