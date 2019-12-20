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

package org.opensds.platform.common.utils;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Locale;

public abstract class OSUtils
{
    private static String os = null;

    private static String ipAddr = null;
    
    public static String getOSName()
    {
        if (os == null)
        {
            os = System.getProperty("os.name");
        }
        return os;
    }

    public static boolean isWindows()
    {
        return (getOSName().toLowerCase(Locale.getDefault()).indexOf("win") > -1);
    }

    public static boolean isMac()
    {
        return (getOSName().toLowerCase(Locale.getDefault()).indexOf("mac") > -1);
    }

    public static boolean isUnix()
    {
        return (getOSName().toLowerCase(Locale.getDefault()).indexOf("nix") > -1
                || getOSName().indexOf("nux") > -1 || getOSName()
                .indexOf("aix") > -1);
    }

    public static boolean isSolaris()
    {
        return (getOSName().toLowerCase(Locale.getDefault()).indexOf("sunos") > -1);
    }

    public static String getOSLineSepartor()
    {
        return System.getProperty("line.separator");
    }

    /**
	 *codedex 	
	 *FORTIFY.Missing_Check_against_Null
	 *nwx356892 
	 */
    public static String getOsBit()
    {
        String realArch;
        String arch = System.getProperty("os.arch");
        realArch = arch!=null && arch.endsWith("64") ? "64" : "32";

        if (!"64".equals(realArch) && isWindows())
        {
            arch = System.getenv("PROCESSOR_ARCHITECTURE");
            String wow64Arch = System.getenv("PROCESSOR_ARCHITEW6432");
            realArch = arch!=null && arch.endsWith("64") || wow64Arch != null
                    && wow64Arch.endsWith("64") ? "64" : "32";
        }

        return realArch;
    }

    public static boolean isOS64Bit()
    {
        return "64".equals(getOsBit());
    }
    
    public static void setLocalIP(String ip)
    {
        if (null == ipAddr)
        {
            ipAddr = ip;
        }
    }
    
    public static String getLocalIP()
    {
        if (null == ipAddr)
        {
        	ipAddr = getLocalHostIP();
            return ipAddr;
        }
        else
        {
            return ipAddr;
        }
    }
    
    public static String getLocalHostIP(){
    	String ip = "";
		try{
			Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
			InetAddress ipAddress = null;
			while (allNetInterfaces.hasMoreElements()) {
				NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
				Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
				while (addresses.hasMoreElements()) {
					ipAddress = (InetAddress) addresses.nextElement();
					if (ipAddress != null && ipAddress instanceof Inet4Address && !ipAddress.isLoopbackAddress()) {
						ip = ipAddress.getHostAddress();
					}
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
		return ip;
	}
}
