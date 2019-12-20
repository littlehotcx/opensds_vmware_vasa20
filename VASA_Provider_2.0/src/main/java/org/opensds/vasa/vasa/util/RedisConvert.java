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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RedisConvert {

    public static <T> List<T> convertListOfString2Bean(List<String> jsonList, Class<T> beanClass) {
        List<T> convertResult = new ArrayList<T>();
        for (String jsonStr : jsonList) {
            convertResult.add(JsonUtil.parseJson2Bean(jsonStr, beanClass));
        }

        return convertResult;
    }

    public static <T> T convertString2Bean(String jsonAsString, Class<T> beanClass) {
        return JsonUtil.parseJson2Bean(jsonAsString, beanClass);
    }

    public static <T> Set<T> convertMapValueString2BeanWithSet(String jsonString, Class<T> beanClass) {
        return JsonUtil.parseJson2BeanAsSet(jsonString, beanClass);
    }

    public static <T> Map<String, List<T>> convertMapValueString2BeanWithList(Map<String, String> jsonMap,
                                                                              Class<T> beanClass) {
        Map<String, List<T>> convertMap = new HashMap<>();
        for (Map.Entry<String, String> entry : jsonMap.entrySet()) {
            convertMap.put(entry.getKey(), JsonUtil.parseJson2BeanAsList(entry.getValue(), beanClass));
        }

        return convertMap;
    }

    public static <T> Map<String, T> convertMapValueString2Bean(Map<String, String> jsonMap, Class<T> beanClass) {
        Map<String, T> convertMap = new HashMap<>();
        for (Map.Entry<String, String> entry : jsonMap.entrySet()) {
            convertMap.put(entry.getKey(), JsonUtil.parseJson2Bean(entry.getValue(), beanClass));
        }

        return convertMap;
    }

    public static <T> Map<String, T> convertMapValueString2Bean(String jsonString, Class<T> beanClass) {

        return JsonUtil.parseJson2BeanAsMap(jsonString, String.class, beanClass);
    }

    public static String convertBean2JsonString(Object objectValue) {
        return JsonUtil.parse2JsonString(objectValue);
    }

    public static List<String> convertListObject2String(List<?> listObjects) {
        List<String> convertResult = new ArrayList<String>();
        for (Object object : listObjects) {
            convertResult.add(JsonUtil.parse2JsonString(object));
        }

        return convertResult;
    }

}
