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

package org.opensds.platform.nemgr.handler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class DeviceServiceHandler implements InvocationHandler
{
    /**
     * Constructs a DeviceCommuProxyHandler
     * 
     * @param t
     *            the implicit parameter of the method call
     */
    public DeviceServiceHandler(Object t)
    {
        target = t;
    }
    
    public Object invoke(Object proxy, Method m, Object[] args)
        throws Throwable// 此方法在代理类中
    {
        // invoke actual method
        return m.invoke(target, args);
    }
    
    private Object target;
}
