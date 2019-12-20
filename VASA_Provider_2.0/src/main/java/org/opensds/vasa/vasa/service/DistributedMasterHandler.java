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

package org.opensds.vasa.vasa.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.opensds.vasa.vasa.util.IPUtil;
import org.opensds.vasa.vasa.util.RedisUtil;

import com.vmware.vim.vasa.v20.StorageFault;

public class DistributedMasterHandler {

    private static String Lock_Key = "distributed_lock_vasa";
    private static Integer lock_key_expire_sec = 60 * 12;
    private static Log logger = LogFactory.getLog(DistributedMasterHandler.class);

    public static boolean isMaster() {
        String currentIP = getCurrentIP();
        if (null == currentIP) {
            return false;
        }
        Long setnx = RedisUtil.setnx(Lock_Key, currentIP);
        if (setnx == 1) {
            logger.info("Current key is null,set current node is master");
            RedisUtil.setExpireSeconds(Lock_Key, lock_key_expire_sec);
            return true;
        } else if (setnx == 0) {
            String lockIP = RedisUtil.getStringKeyValue(Lock_Key);
            if (null != lockIP) {
                if (lockIP.equalsIgnoreCase(currentIP)) {
                    RedisUtil.setExpireSeconds(Lock_Key, lock_key_expire_sec);
                    return true;
                }
            } else {
                return isMaster();
            }
        }
        //add fs8.0
        return true;
    }

    private static String getCurrentIP() {
        try {
            return IPUtil.getLocalIP();
        } catch (StorageFault e) {
            logger.error("getLocalIP error e: ", e);
            return null;
        }
    }
}
