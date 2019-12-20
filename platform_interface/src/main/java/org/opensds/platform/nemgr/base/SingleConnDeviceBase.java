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

package org.opensds.platform.nemgr.base;

import org.opensds.platform.nemgr.itf.IDevice;
import org.opensds.platform.nemgr.itf.IDeviceConnection;

/**
 * 设备适配基础类，应用可从这个类继承以减少非业务部分的工作
 * 
 * @author j00160659
 * 
 */
public abstract class SingleConnDeviceBase<CONNTYPE extends IDeviceConnection> implements IDevice
{
    private IDeviceConnection conn = null;
    
    private Integer refCount = 0;
    
    /**
     * 获取设备使用的业务协议类型
     * 
     * @return
     */
    public abstract String getProtocolType();
    
    public abstract boolean doHeartbeat(String coonId);
    /*
     * CodeDex
     * GUARDED_BY_VIOLATION   wwx340678
     */
    @Override
    public synchronized IDeviceConnection getConnById(String key)
    {
        return conn;
    }
    
    @Override
    public synchronized void addId2ConnMap(String key, IDeviceConnection value)
    {
        this.conn = value;
        if (value == null)
        {
            refCount = 0;
        }
        else
        {
            ++refCount;
        }
    }
    
    @Override
    public synchronized boolean removeConnId(String key)
    {
        if (refCount > 0)
        {
            --refCount;
        }
        if (refCount == 0)
        {
            this.conn = null;
        }
        return true;
    }
}
