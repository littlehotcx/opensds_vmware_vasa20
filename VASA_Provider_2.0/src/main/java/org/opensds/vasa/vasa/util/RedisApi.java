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
import java.util.Map;
import java.util.Set;

public class RedisApi {

    public static <T> List<T> getListOfBeanStartsWithKey(String key, Class<T> beanClass) {
        List<String> jsonList = RedisUtil.getStringListStartsWithKey(key);

        return RedisConvert.convertListOfString2Bean(jsonList, beanClass);
    }

    public static <T> T getStringOfBeanByKey(String key, Class<T> beanClass) {
        String jsonString = RedisUtil.getStringKeyValue(key);

        if (jsonString == null) {
            return null;
        }

        return RedisConvert.convertString2Bean(jsonString, beanClass);
    }

    public static <T> T getValue2RedisMap(String key, String field, Class<T> beanClass) {
        String jsonString = RedisUtil.RedisMap.getValue(key, field);

        if (jsonString == null) {
            return null;
        }

        return RedisConvert.convertString2Bean(jsonString, beanClass);
    }

    public static <T> List<T> getListOfBean2RedisList(String key, Class<T> beanClass) {
        List<String> jsonList = RedisUtil.RedisList.getValue(key);

        return RedisConvert.convertListOfString2Bean(jsonList, beanClass);
    }

    public static <T> Set<T> getValueAsSetWithBean2RedisMap(String key, String field, Class<T> beanClass) {
        String jsonString = RedisUtil.RedisMap.getValue(key, field);

        if (jsonString == null) {
            return null;
        }

        return RedisConvert.convertMapValueString2BeanWithSet(jsonString, beanClass);
    }

    public static <T> Map<String, T> getValueAsMapWithMap2RedisMap(String key, String field, Class<T> beanClass) {
        String jsonString = RedisUtil.RedisMap.getValue(key, field);

        if (jsonString == null) {
            return null;
        }

        return RedisConvert.convertMapValueString2Bean(jsonString, beanClass);
    }

    public static <T> Map<String, List<T>> getValueAsMapWithList2RedisMap(String key, Class<T> beanClass) {
        Map<String, String> jsonMap = RedisUtil.RedisMap.getAllMap(key);

        return RedisConvert.convertMapValueString2BeanWithList(jsonMap, beanClass);
    }

    public static <T> Map<String, T> getValueAsMapWithBean2RedisMap(String key, Class<T> beanClass) {
        Map<String, String> jsonMap = RedisUtil.RedisMap.getAllMap(key);

        return RedisConvert.convertMapValueString2Bean(jsonMap, beanClass);
    }

    public static void putValue2RedisMap(String key, String field, Object objectValue) {
        RedisUtil.RedisMap.putValue(key, field, JsonUtil.parse2JsonString(objectValue));
    }

    public static void setStringKeyValue2TTL(String key, Object objectValue, int expiredSeconds) {
        RedisUtil.setStringKeyValue2TTL(key, JsonUtil.parse2JsonString(objectValue), expiredSeconds);
    }

    public static void setValue2RedisList(String key, List<?> listObjects) {
        List<String> listString = RedisConvert.convertListObject2String(listObjects);

        RedisUtil.RedisList.setValue(key, listString);
    }

    public static void putValue2RedisList(String key, Object objectValue) {
        String jsonString = RedisConvert.convertBean2JsonString(objectValue);

        RedisUtil.RedisList.addValue(key, jsonString);
    }

    public static void setExpired2Seconds(String key, int seconds) {
        RedisUtil.setExpireSeconds(key, seconds);
    }

    public static long getKeyExpireSeconds(String key) {
        return RedisUtil.getExpireSeconds(key);
    }

    public static void setIncrementValue2TTL(String key, int expiredSeconds) {
        RedisUtil.setIncrementValue2TTL(key, expiredSeconds);
    }

    public static void clearValueByKey(String key) {
        RedisUtil.clearByKey(key);
    }
}
