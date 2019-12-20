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

package org.opensds.platform.remote;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensds.platform.abnormalevent.itf.IAbnormaleventRemote;
import org.opensds.platform.common.config.ConfigManager;
import org.opensds.platform.debugging.itf.IRemoteDebugging;

public class RemoteManager
{
    private static final Logger LOGGER = LogManager.getLogger(RemoteManager.class);
    
    private IRemoteDebugging debuggingManager;
    
    private IAbnormaleventRemote abnormaleventRemote;
    
    public void bindRemote()
    {
        String serverEvent = ConfigManager.getInstance().getValue("remote.serverEvent");
        String serverLog4j = ConfigManager.getInstance().getValue("remote.serverLog4j");
        String port = ConfigManager.getInstance().getValue("remote.port");
        
        try
        {
            RMIServerSocketFactory ssf = new RMIServerSocketFactory()
            {
                
            	/**
            	 *codedex 	
            	 *FORTIFY.HW_-_Use_SSLSocket_rather_than_Socket_for_secure_data_exchange
            	 *nwx356892 
            	 */
                @Override
                public ServerSocket createServerSocket(int port)
                    throws IOException
                {
                	//===============begin===============
                	SSLServerSocket sslServerSocket = new SSLServerSocket(port, 0, InetAddress.getLoopbackAddress()) {
						
						@Override
						public void setWantClientAuth(boolean want) {
						}
						
						@Override
						public void setUseClientMode(boolean mode) {
						}
						
						@Override
						public void setNeedClientAuth(boolean need) {
						}
						
						@Override
						public void setEnabledProtocols(String[] protocols) {
						}
						
						@Override
						public void setEnabledCipherSuites(String[] suites) {
						}
						
						@Override
						public void setEnableSessionCreation(boolean flag) {
						}
						
						@Override
						public boolean getWantClientAuth() {
							return false;
						}
						
						@Override
						public boolean getUseClientMode() {
							return false;
						}
						
						@Override
						public String[] getSupportedProtocols() {
							return null;
						}
						
						@Override
						public String[] getSupportedCipherSuites() {
							return null;
						}
						
						@Override
						public boolean getNeedClientAuth() {
							return false;
						}
						
						@Override
						public String[] getEnabledProtocols() {
							return null;
						}
						
						@Override
						public String[] getEnabledCipherSuites() {
							return null;
						}
						
						@Override
						public boolean getEnableSessionCreation() {
							return false;
						}
					};
					//===============end===============
					
                	//SSLServerSocketFactory sslServerSocketFactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
					//SSLServerSocket sslServerSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(port, 0, InetAddress.getLoopbackAddress());
                    return sslServerSocket;
                }
            };
            
            RMIClientSocketFactory csf = new RMIClientSocketFactory()
            {
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
            };
            
            
            LocateRegistry.createRegistry(Integer.parseInt(port), csf, ssf);
            Naming.rebind("//127.0.0.1:" + port + "/" + serverEvent, abnormaleventRemote);
            Naming.rebind("//127.0.0.1:" + port + "/" + serverLog4j, debuggingManager);
        }
        catch (RemoteException e)
        {
            LOGGER.error("", e);
        }
        catch (MalformedURLException e)
        {
            LOGGER.error("", e);
        }
    }
    
    public IRemoteDebugging getDebuggingManager()
    {
        return debuggingManager;
    }
    
    public void setDebuggingManager(IRemoteDebugging debuggingManager)
    {
        this.debuggingManager = debuggingManager;
    }
    
    public IAbnormaleventRemote getAbnormaleventRemote()
    {
        return abnormaleventRemote;
    }
    
    public void setAbnormaleventRemote(IAbnormaleventRemote abnormaleventRemote)
    {
        this.abnormaleventRemote = abnormaleventRemote;
    }
}
