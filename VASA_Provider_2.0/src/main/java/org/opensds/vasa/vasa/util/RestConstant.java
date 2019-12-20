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

public interface RestConstant {
    public static final String HTTPS_SCHEME = "https";

    public static final String HTTP_SCHEME = "http";

    public static final String SSL_SECURE_SOCKET_PROTOCOL = "TLS";

    public static final String SSL_SECURE_SOCKET_PROTOCOLTLSv1_2 = "TLSv1.2";

    public static final String HTTP_METHOD_GET = "GET";

    public static final String HTTP_METHOD_POST = "POST";

    public static final String HTTP_METHOD_PUT = "PUT";

    public static final String HTTP_METHOD_DELETE = "DELETE";

    public static final String HTTP_METHOD_PATCH = "PATCH";

    public static final float FREE_SPACE_RENT = 0.8f;

    /**
     * 每页显示的记录条数
     */
    public static final int NUM_PER_PAGE = 6;

    public static final int PSWD_EXPIRE_DAY = 60;

    public static final int PSWD_EXPIRE_ALTER_DAY = 15;
}
