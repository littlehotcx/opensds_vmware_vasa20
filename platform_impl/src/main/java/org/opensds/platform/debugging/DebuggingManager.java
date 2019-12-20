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

package org.opensds.platform.debugging;

import java.io.IOException;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.UnicastRemoteObject;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

import org.opensds.platform.common.config.ConfigManager;
import org.opensds.platform.debugging.itf.IRemoteDebugging;

public class DebuggingManager extends UnicastRemoteObject
    implements IRemoteDebugging
{
    /**
     * UID
     */
    private static final long serialVersionUID = -3677283869364699282L;

    private static final Logger LOGGER = LogManager.getLogger(DebuggingManager.class);
    
    private static String port = ConfigManager.getInstance().getValue("debugging.port");
    
    
    static class DebuggingRMIClientSocketFactory implements RMIClientSocketFactory, Serializable
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
    
    static class DebuggingRMIServerSocketFactory implements RMIServerSocketFactory, Serializable
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
    
    public DebuggingManager() throws RemoteException
    {
        super(Integer.parseInt(port), new DebuggingRMIClientSocketFactory(), new DebuggingRMIServerSocketFactory());
    }

    public boolean setLoggerLevel(String packageName, String levelName)
    {
        LOGGER.debug("packageName=" + packageName + ", levelName="+ levelName);
        Level level = Level.toLevel(levelName);
        if ("".equals(packageName))
        {
            Logger logger = LogManager.getRootLogger();
            //UPDATE: If you are using Log4j 2 you should remove the calls to setLevel per the documentationas this can be achieved via implementation classes.
           // logger.setLevel(level);
            Configurator.setRootLevel(level);
            if (logger.getLevel().toString().equalsIgnoreCase(levelName))
            {
                return true;
            }
            return false;
        }
        Logger logger = LogManager.getLogger(packageName);
        if (null != logger)
        {
            //logger.setLevel(level);
        	Configurator.setLevel(packageName, level);
            Level le = logger.getLevel();
            if (null != le && le.toString().equalsIgnoreCase(levelName))
            {
                return true;
            }
        }
        return false;
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
