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
 * ��������
 * check ipv6 format
 *
 * @author h00451513
 * @since 2019-09-12
 */
public class IPv6Util {

    // ipv6Pattern
    public static String ipv6Pattern = "^(?:[0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}$";

    // ipv6Pattern
    public static String ipv6PatternCompress =
            "^((?:[0-9A-Fa-f]{1,4}(?::[0-9A-Fa-f]{1,4})*)?)::((?:[0-9A-Fa-f]{1,4}(?::[0-9A-Fa-f]{1,4})*)?)$";


    /**
     * @return boolean
     * @Description check if it is valid ipv6 address
     * @Param ip address
     * @date 2019/9/12
     * @author h00451513
     */
    public static boolean isIPv6(String str) {
        return isIPv6Standard(str) || isIPv6Abbreviated(str);
    }

    /**
     * @return boolean
     * @Description check if it is valid Abbreviated ipv6 address
     * @Param ip address
     * @date 2019/9/12
     * @author h00451513
     */
    public static boolean isIPv6Abbreviated(String str) {
        if (!Pattern.matches(ipv6PatternCompress, str)) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * @return boolean
     * @Description check if it is valid standard ipv6 address
     * @Param ip address
     * @date 2019/9/12
     * @author h00451513
     */
    public static boolean isIPv6Standard(String str) {
        if (!Pattern.matches(ipv6Pattern, str)) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * @return boolean
     * @Description add [] to ip v6
     * @Param ip address
     * @date 2019/9/12
     * @author h00451513
     */
    public static String addBracket2IPv6(String str) {
        String ipv6 = "[" + str + "]";
        return ipv6;
    }

}
