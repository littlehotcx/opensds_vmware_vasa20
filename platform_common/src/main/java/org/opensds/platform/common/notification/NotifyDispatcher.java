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

package org.opensds.platform.common.notification;

import java.util.Iterator;
import java.util.LinkedList;

public abstract class NotifyDispatcher<T>
{
    private LinkedList<T> listeners = new LinkedList<T>();

    public void registerListener(T listener)
    {
        if (listener == null)
        {
            return;
        }
        synchronized (listeners)
        {
            listeners.add(listener);
        }
    }

    public void unregisterListener(T listener)
    {
        if (listener == null)
        {
            synchronized (listeners)
            {
                listeners.clear();
            }
        }
        else
        {
            synchronized (listeners)
            {
                listeners.remove(listener);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public boolean fireNotify(String ntfId, Object msg, Object additionalInfo)
    {
        LinkedList<T> backup;
        synchronized (listeners)
        {
            backup = (LinkedList<T>) listeners.clone();
        }
        boolean bReturn = false;
        for (Iterator<T> it = backup.iterator(); !bReturn && it.hasNext();)
        {
            bReturn = notifyToOneListener(it.next(), ntfId, msg, additionalInfo);
        }
        return bReturn;
    }

    public abstract boolean notifyToOneListener(T listener, String ntfId,
            Object msg, Object additionalInfo);
}
