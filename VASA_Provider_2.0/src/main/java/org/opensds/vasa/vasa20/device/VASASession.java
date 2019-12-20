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

package org.opensds.vasa.vasa20.device;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.http.client.CookieStore;

public class VASASession {
    private static Map<String, String> tokenCache = new ConcurrentHashMap<String, String>();
    private static Map<String, String> sessionCache = new ConcurrentHashMap<>();

    public synchronized static String getToken(String deviceId) {
        if (null != deviceId) {
            return tokenCache.get(deviceId);
        } else {
            return null;
        }
    }

    public synchronized static void setToken(String deviceId, String token) {
        if (null != deviceId && null != token) {
            tokenCache.put(deviceId, token);
        } else if (null != deviceId && null == token) {
            tokenCache.remove(deviceId);
        }
    }

    public synchronized static String getSession(String deviceId) {
        if (null != deviceId) {
            return sessionCache.get(deviceId);
        } else {
            return null;
        }
    }

    public synchronized static void setSession(String deviceId, String session) {
        if (null != deviceId && null != session) {
            sessionCache.put(deviceId, session);
        } else if (null != deviceId && null == session) {
            sessionCache.remove(deviceId);
        }
    }

}
