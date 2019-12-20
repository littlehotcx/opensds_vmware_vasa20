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

package org.opensds.platform.abnormalevent;

import java.io.IOException;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.UnicastRemoteObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensds.platform.abnormalevent.itf.IAbnormalevent;
import org.opensds.platform.abnormalevent.itf.IAbnormaleventRemote;
import org.opensds.platform.common.bean.abnormalevent.AbnormaleventBean;
import org.opensds.platform.common.config.ConfigManager;

public class AbnormaleventManager extends UnicastRemoteObject
    implements IAbnormalevent, IAbnormaleventRemote
{
    /**
     * UID
     */
    private static final long serialVersionUID = 7662448415324111249L;

    private final static Logger LOGGER = LogManager.getLogger(AbnormaleventManager.class);

    private transient Map<String, AbnormaleventBean> exceptionMap = new HashMap<String, AbnormaleventBean>();
    
    private static String port = ConfigManager.getInstance().getValue("abnormalevent.port");

    static class AbnormalRMIClientSocketFactory implements RMIClientSocketFactory, Serializable
    {

        /**
         * UID
         */
        private static final long serialVersionUID = -8250629157365306228L;

        /**
    	 *codedex 	
    	 *FORTIFY.HW_-_Use_SSLSocket_rather_than_Socket_for_secure_data_exchange
    	 *nwx356892 
    	 */
        @Override
        public Socket createSocket(String host, int port)
            throws IOException
        {
        	SSLSocketFactory sslSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        	SSLSocket sslSocket = (SSLSocket) sslSocketFactory.createSocket(host,port);

            return sslSocket;
        }
        
    }
    
    static class AbnormalRMIServerSocketFactory implements RMIServerSocketFactory, Serializable
    {

        /**
         * UID
         */
        private static final long serialVersionUID = 3341818630198298082L;

        /**
    	 *codedex 	
    	 *FORTIFY.HW_-_Use_SSLSocket_rather_than_Socket_for_secure_data_exchange
    	 *nwx356892 
    	 */
        @Override
        public ServerSocket createServerSocket(int port)
            throws IOException
        {
        	SSLServerSocketFactory sslServerSocketFactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
        	SSLServerSocket sslServerSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(port);

            return sslServerSocket;
        }
        
    }
    
    public AbnormaleventManager() throws RemoteException
    {
        super(Integer.parseInt(port), new AbnormalRMIClientSocketFactory(), new AbnormalRMIServerSocketFactory());
    }
    
    @Override
    public void occurException(String id, AbnormaleventBean bean)
    {
        synchronized (exceptionMap)
        {
            if (null == exceptionMap.get(id))
            {
                exceptionMap.put(id, bean);
            }
        }
    }

    @Override
    public void endException(String id, AbnormaleventBean bean)
    {
        AbnormaleventBean excBean;
        synchronized (exceptionMap)
        {
            excBean = exceptionMap.get(id);
            if (null == excBean)
            {
                return;
            }
            excBean.setEndTime(new Date());
            exceptionMap.remove(id);
        }
        writeToFile(excBean);
    }

    private void writeToFile(AbnormaleventBean bean)
    {
        String format = "yyyyMMddHHmmss";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        LOGGER.info(bean.getObjName()
                + ","
                + bean.getOccurrence() + ","
                + sdf.format(bean.getOccurTime())
                + ","
                + sdf.format(bean.getEndTime()) + ","
                        + bean.getExceptionMessage());
    }

    @Override
    public boolean existException(String id)
    {
        return null == exceptionMap.get(id) ? false : true;
    }

    @Override
    public List<Integer> sumExceptions()
    {
        int neSum = 0;
        int fileSum = 0;
        int auSum = 0;
        List<Integer> sumList = new ArrayList<Integer>();
        sumList.add(exceptionMap.size());
        for (String key : exceptionMap.keySet())
        {
            if (key.contains("fail.to.connect"))
            {
                neSum++;
            }
            else if (key.contains("fail.to.authenticate"))
            {
                auSum++;
            }
            else
            {
                fileSum++;
            }
        }
        sumList.add(neSum);
        sumList.add(auSum);
        sumList.add(fileSum);
        return sumList;
    }

    @Override
    public List<AbnormaleventBean> queryExceptionInfos()
    {
        List<AbnormaleventBean> list = new ArrayList<AbnormaleventBean>();
        for (String key : exceptionMap.keySet())
        {
            list.add(exceptionMap.get(key));
        }
        return list;
    }
    
    public void destroy()
    {
        try
        {
            UnicastRemoteObject.unexportObject(this, true);
        }
        catch (NoSuchObjectException e)
        {
            LOGGER.error("", e);
        }
    }

}
