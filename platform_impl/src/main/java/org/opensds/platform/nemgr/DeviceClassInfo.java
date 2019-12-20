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

package org.opensds.platform.nemgr;

import org.opensds.platform.nemgr.itf.IDevice;

public class DeviceClassInfo
{
    private String devType;
    
    private String verStart;
    
    private String verEnd;
    
    private Class<? extends IDevice> inst;
    
    public String getDevType()
    {
        return devType;
    }
    
    public void setDevType(String devType)
    {
        this.devType = devType;
    }
    
    public String getVerStart()
    {
        return verStart;
    }
    
    public void setVerStart(String verStart)
    {
        this.verStart = verStart;
    }
    
    public String getVerEnd()
    {
        return verEnd;
    }
    
    public void setVerEnd(String verEnd)
    {
        this.verEnd = verEnd;
    }
    
    public Class<? extends IDevice> getInst()
    {
        return inst;
    }
    
    public void setInst(Class<? extends IDevice> inst)
    {
        this.inst = inst;
    }
    
    public boolean isVersionSupported(DeviceInstance devInfo)
    {
        if (null != verStart && null != verEnd)
        {
            return true;
        }
        else
        {
            return true;
        }
    }
}