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

package org.opensds.platform.nemgr.conn;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensds.platform.abnormalevent.itf.IAbnormalevent;
import org.opensds.platform.common.bean.abnormalevent.AbnormaleventBean;
import org.opensds.platform.common.utils.ApplicationContextUtil;
import org.opensds.platform.nemgr.DeviceFactory;
import org.opensds.platform.nemgr.conn.itf.IDeviceReconnector;
import org.opensds.platform.nemgr.itf.IDeviceConnection;
import org.opensds.platform.nemgr.DeviceConnManager;

/**
 * 设备重连接
 * 
 * @author j00160659
 * 
 */
public class DeviceReconnector implements IDeviceReconnector
{
    private static final Logger LOGGER = LogManager.getLogger(DeviceReconnector.class);
    
    private DeviceConnManager connMgr;
    
    private List<IDeviceConnection> connectingDeviceConns = new LinkedList<IDeviceConnection>();
    
    private DeviceFactory deviceFactory;
    
    IAbnormalevent abnormaleventManager = ApplicationContextUtil.getBean("abnormaleventManager");
    
    public void addDevice(IDeviceConnection conn)
    {
        synchronized (connectingDeviceConns)
        {
            connectingDeviceConns.add(conn);
        }
    }
    
    @Override
    public void removeDevice(IDeviceConnection conn)
    {
        
        synchronized (connectingDeviceConns)
        {
            for (int i = 0; i < connectingDeviceConns.size(); i++)
            {
                if (retrieveStrValueFromCon(connectingDeviceConns.get(i), "deviceId")
                    .equals(retrieveStrValueFromCon(conn, "deviceId"))
                    && retrieveStrValueFromCon(connectingDeviceConns.get(i), "connId")
                        .equals(retrieveStrValueFromCon(conn, "connId")))
                {
                    connectingDeviceConns.remove(i);
                    break;
                }
            }
        }
    }
    
    private String retrieveStrValueFromCon(IDeviceConnection conn, String valueKey)
    {
         String result = (String) conn.getAdditionalData(valueKey);
         if (null == result)
         {
             result = "";
         }
         return result;
    }
    
    private void reConnectDevice()
    {
        synchronized (connectingDeviceConns)
        {
            for (int i = 0; i < connectingDeviceConns.size(); i++)
            {
                String connId = (String)connectingDeviceConns.get(i).getAdditionalData("connId");
                
                String exp = connId.substring(0, connId.length() / 2);
                StringBuilder repSb = new StringBuilder("");
                for (int j = 0; j < exp.length(); j++)
                {
                    repSb.append("*");
                }
                
                LOGGER.info(connectingDeviceConns.get(i).getAdditionalData("deviceId") + "_"
                    + connId.replace(exp, repSb.toString()) + " will reconnect");
                IDeviceConnection conn = connectingDeviceConns.get(i);
                
                if (conn.initConn((String)conn.getAdditionalData("connId")))
                {
                    // 如果连接成功，则将此设备从待重连列表中剔除，且将其放入保活列表中
                    connectingDeviceConns.remove(i);
                    conn.setAdditionalData("failTime", 0);
                    connMgr.addToKeepAliveSchedule(conn);
                    
                    AbnormaleventBean ebean = new AbnormaleventBean();
                    ebean.setEndTime(new Date());
                    abnormaleventManager.endException(conn.getAdditionalData("deviceName") + "_"
                        + IAbnormalevent.FAIL_TO_CONNECT, ebean);
                }
            }
        }
    }
    
    public void run()
    {
        reConnectDevice();
    }
    
    public DeviceConnManager getConnMgr()
    {
        return connMgr;
    }
    
    public void setConnMgr(DeviceConnManager connMgr)
    {
        this.connMgr = connMgr;
    }
    
    public DeviceFactory getDeviceFactory()
    {
        return deviceFactory;
    }
    
    public void setDeviceFactory(DeviceFactory deviceFactory)
    {
        this.deviceFactory = deviceFactory;
    }
}
