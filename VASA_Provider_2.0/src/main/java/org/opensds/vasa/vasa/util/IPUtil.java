/*
 * // Copyright 2019 The OpenSDS Authors.
 * //
 * // Licensed under the Apache License, Version 2.0 (the "License"); you may
 * // not use this file except in compliance with the License. You may obtain
 * // a copy of the License at
 * //
 * //     http://www.apache.org/licenses/LICENSE-2.0
 * //
 * // Unless required by applicable law or agreed to in writing, software
 * // distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * // WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * // License for the specific language governing permissions and limitations
 * // under the License.
 *
 */

package org.opensds.vasa.vasa.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.opensds.platform.common.config.ConfigManager;

import com.vmware.vim.vasa.v20.StorageFault;


public class IPUtil {

    private static Logger LOGGER = LogManager.getLogger(IPUtil.class);


    public static String getLocalIP() throws StorageFault {
        try {
            String ip = "";
			/*Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
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
			}*/
            ip = ConfigManager.getInstance().getValue("vasa.node.ip");
            return ip;
        } catch (Exception e) {
            LOGGER.error("getLocalIP error e: ", e);
            throw FaultUtil.storageFault("runtime ", e);
        }
    }

}
