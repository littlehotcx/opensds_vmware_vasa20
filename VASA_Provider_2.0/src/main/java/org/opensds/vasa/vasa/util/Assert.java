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

import java.util.List;

/**
 * 模仿断言的辅助类
 *
 * @author x00102290
 * @version [版本号V001R010C00, 2011-12-14]
 * @see
 * @since
 */
public final class Assert {
    private Assert() {
    }

    /**
     * 测试某对象，以保证它们不为null
     *
     * @param o 输入参数
     */
    public static void notNull(Object o) {
        if (null == o) {
            throw new IllegalArgumentException("input arguments equals null");
        }
    }

    /**
     * 测试某字符串，以保证它们不为null或''
     *
     * @param str 输入参数
     */
    public static void notNullStr(String str) {
        if (null == str || str.equals("")) {
            throw new IllegalArgumentException("input str equals null or ''");
        }
    }

    /**
     * 测试某对象，以保证它们不为null
     *
     * @param o        输入参数
     * @param errorMsg 如果o为null时的提示信息
     */
    public static void notNull(Object o, String errorMsg) {
        if (null == o) {
            throw new IllegalArgumentException(errorMsg);
        }
    }

    /**
     * 测试某个数组是否为null或者长度为0
     *
     * @param <T> 参数T
     * @param t   数组对象
     */
    public static <T> void notNullArray(T... t) {
        if (null == t || t.length == 0) {
            throw new IllegalArgumentException(
                    "the array object is null or length is 0");
        }
    }

    /**
     * 判断List是null或无元素
     *
     * @param list 方法参数：list
     */
    public static void notNullList(List<?> list) {
        if (null == list || list.isEmpty()) {
            throw new IllegalArgumentException("List is null or empty!");
        }
    }
}
