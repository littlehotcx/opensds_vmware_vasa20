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

import org.opensds.platform.common.config.ConfigManager;
import org.opensds.platform.common.utils.StringUtils;

public class LockManager {

    private static final String VASA_LOGIN_LOCK_MAP = "VASA_LOGIN_LOCK_MAP_";

    private static final String LOCKED_LIMIT = ConfigManager.getInstance().getValue("vasa.login.lock.limit");

    private static final String LOCKED_DELAY = ConfigManager.getInstance().getValue("vasa.login.lock.second");

    public synchronized static void lockUser(String uid) {
        RedisApi.setIncrementValue2TTL(VASA_LOGIN_LOCK_MAP + uid, Integer.parseInt(LOCKED_DELAY));
    }

    public synchronized static boolean isLocked(String uid) {
        String locked = RedisApi.getStringOfBeanByKey(VASA_LOGIN_LOCK_MAP + uid, String.class);

        if (StringUtils.isEmpty(locked)) {
            return false;
        }

        int lockedCount = Integer.parseInt(locked);
        if (lockedCount >= Integer.parseInt(LOCKED_LIMIT)) {
            return true;
        }

        return false;
    }

    public static long getLockRealseTime(String uid) {
        long ttl = RedisApi.getKeyExpireSeconds(VASA_LOGIN_LOCK_MAP + uid);

        return ttl;
    }

    public static int getLockHasCount(String uid) {
        int lockCount = Integer.parseInt(RedisApi.getStringOfBeanByKey(VASA_LOGIN_LOCK_MAP + uid, String.class));
        return Integer.parseInt(LOCKED_LIMIT) - lockCount;
    }

    public static void resetLock(String userId) {
        RedisApi.clearValueByKey(VASA_LOGIN_LOCK_MAP + userId);
    }

}
