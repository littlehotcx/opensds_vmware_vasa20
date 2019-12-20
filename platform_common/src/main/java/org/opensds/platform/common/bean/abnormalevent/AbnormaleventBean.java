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

package org.opensds.platform.common.bean.abnormalevent;

import java.io.Serializable;
import java.util.Date;

public class AbnormaleventBean implements Serializable
{
    /**
     * UID
     */
    private static final long serialVersionUID = 1L;
    
    private String objName;
    
    private String occurrence;
    
    private Date occurTime;
    
    private Date endTime;
    
    private String exceptionMessage;
    
    public String getObjName()
    {
        return objName;
    }
    
    public void setObjName(String objName)
    {
        this.objName = objName;
    }
    
    public String getOccurrence()
    {
        return occurrence;
    }
    
    public void setOccurrence(String occurrence)
    {
        this.occurrence = occurrence;
    }
    
    public Date getOccurTime()
    {
        return occurTime;
    }
    
    public void setOccurTime(Date occurTime)
    {
        this.occurTime = occurTime;
    }
    
    public Date getEndTime()
    {
        return endTime;
    }
    
    public void setEndTime(Date endTime)
    {
        this.endTime = endTime;
    }
    
    public String getExceptionMessage()
    {
        return exceptionMessage;
    }
    
    public void setExceptionMessage(String exceptionMessage)
    {
        this.exceptionMessage = exceptionMessage;
    }
}
