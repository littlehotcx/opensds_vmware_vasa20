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

package org.opensds.platform.common.bean.nemgr;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="proxys")
@XmlAccessorType(XmlAccessType.FIELD)
public class Proxys
{
    @XmlElement(name = "deviceProxy")
    private List<DeviceProxy> list = new ArrayList<DeviceProxy>();
    
    public List<DeviceProxy> getList()
    {
        return list;
    }
    
    public void setList(List<DeviceProxy> list)
    {
        this.list = list;
    }
    
    /**
     * 供Digester调用的方法
     * @param deviceClass
     */
    public void addDeviceClass(DeviceProxy deviceProxy)
    {
        this.list.add(deviceProxy);
    }
}
