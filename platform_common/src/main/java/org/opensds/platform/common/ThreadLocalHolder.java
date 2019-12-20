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

package org.opensds.platform.common;

import java.util.HashMap;
import java.util.Map;

public abstract class ThreadLocalHolder
{
    public static final ThreadLocal<MessageContext> userThreadLocal = new ThreadLocal<MessageContext>();

    public static void set(MessageContext user)
    {
        userThreadLocal.set(user);
    }

    public static void unset()
    {
        userThreadLocal.remove();
    }

    public static MessageContext get()
    {
        MessageContext result = userThreadLocal.get();
        if (null == result)
        {
            result = new MessageContext();
            set(result);
        }
        return result;
    }
    
    public static void put(String key, Object value)
    {
        MessageContext mc = get();
        Map<String, Object> entities = mc.getEntities();
        if (null == entities)
        {
            entities = new HashMap<String, Object>();
        }
        mc.setEntities(entities);
        entities.put(key, value);
        
        ThreadLocalHolder.set(mc);
    }
    
    public static Object get(String key)
    {
        MessageContext mc = get();
        if (null != mc.getEntities())
        {
            return mc.getEntities().get(key);
        }
        
        return null;
    }
}
