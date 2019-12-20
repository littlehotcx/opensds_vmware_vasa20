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

package org.opensds.platform.common.bean.callback;

/**
 * 回调接口信息类
 * 
 * @author z00209306
 * @since V001V002C01
 */
public class CallbackItfInfo
{
    /*
     * 通知消息的类型: callback, notify
     */
    private String notifyMsgType;
    
    /*
     * 通知消息的处理器ID
     */
    private String processorId;
    
    /*
     * 回调消息的来源设备ID
     */
    private String devId;
    
    /*
     * 回调消息的来源连接ID
     */
    private String connectionId;
    
    private String itfName;
    
    private String module;
    
    public String getDevId()
    {
        return devId;
    }
    
    public void setDevId(String devId)
    {
        this.devId = devId;
    }
    
    public String getConnectionId()
    {
        return connectionId;
    }
    
    public void setConnectionId(String connectionId)
    {
        this.connectionId = connectionId;
    }
    
    public String getItfName()
    {
        return itfName;
    }
    
    public void setItfName(String itfName)
    {
        this.itfName = itfName;
    }
    
    public String getModule()
    {
        return module;
    }
    
    public void setModule(String module)
    {
        this.module = module;
    }
    
    public String getNotifyMsgType()
    {
        return notifyMsgType;
    }
    
    public void setNotifyMsgType(String notifyMsgType)
    {
        this.notifyMsgType = notifyMsgType;
    }
    
    public String getProcessorId()
    {
        return processorId;
    }
    
    public void setProcessorId(String processorId)
    {
        this.processorId = processorId;
    }
}
