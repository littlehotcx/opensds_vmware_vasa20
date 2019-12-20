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

import java.util.regex.Pattern;

/**
 * check ipv4 format
 *
 * @author h00451513
 * @since 2019-09-17
 */
public class IPv4Util {

    // ipv4Pattern
    public static String ipv4Pattern =
            "(((\\d{1,2})|(1\\d{1,2})|(2[0-4]\\d)|(25[0-5]))\\.){3}((\\d{1,2})|(1\\d{1,2})|(2[0-4]\\d)|(25[0-5]))";

    /**
     * @return boolean
     * @Description check if it is valid ipv4 address
     * @Param ip address
     * @date 2019/9/12
     * @author h00451513
     */
    public static boolean isIPv4(String str) {
        if (!Pattern.matches(ipv4Pattern, str)) {
            return false;
        } else {
            String[] parts = str.split("\\.");
            for (int i = 0; i < 4; i++) {
                if (Integer.parseInt(parts[i]) >= 256) {
                    return false;
                }
            }
            return true;
        }
    }
}
