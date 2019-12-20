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

package org.opensds.platform.commu;

import org.apache.cxf.endpoint.Client;

public class CachedObjectsBean
{
    private Object service;
    
    private Client client;
    
    private Object reserved1;

    public Object getService()
    {
        return service;
    }

    public void setService(Object service)
    {
        this.service = service;
    }

    public Client getClient()
    {
        return client;
    }

    public void setClient(Client client)
    {
        this.client = client;
    }

    public Object getReserved1()
    {
        return reserved1;
    }

    public void setReserved1(Object reserved1)
    {
        this.reserved1 = reserved1;
    }
}
