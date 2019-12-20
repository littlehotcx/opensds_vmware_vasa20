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

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class JsonUtil {

    private static ObjectMapper objectMapper = new ObjectMapper();

    private static final Logger LOGGER = LogManager.getLogger(JsonUtil.class);

    static {
        objectMapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
        objectMapper.setSerializationInclusion(Include.NON_NULL);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * 将Java对象转换成json字符串
     *
     * @param bean
     * @return
     */
    public static <T> String parse2JsonString(T bean) {
        try {
            if (bean instanceof String) {
                return (String) bean;
            }
            return objectMapper.writeValueAsString(bean);
        } catch (JsonProcessingException e) {
            LOGGER.error("parse2JsonString/JsonProcessingException ERROR.", e);
        }

        return null;
    }

    /**
     * 将json字符串转换成Java对象
     *
     * @param jsonAsString
     * @param pojoClass
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T parseJson2Bean(String jsonAsString, Class<T> pojoClass) {
        try {
            if (String.class.equals(pojoClass)) {
                return (T) jsonAsString;
            }
            return objectMapper.readValue(jsonAsString, pojoClass);
        } catch (JsonParseException e) {
            LOGGER.error("parseJson2Bean/JsonParseException ERROR.", e);
        } catch (JsonMappingException e) {
            LOGGER.error("parseJson2Bean/JsonMappingException ERROR.", e);
        } catch (IOException e) {
            LOGGER.error("parseJson2Bean/IOException ERROR.", e);
        }

        return null;
    }

    /**
     * 将json字符串转换成list对象
     *
     * @param jsonAsString
     * @param pojoClass
     * @return
     */
    public static <T> List<T> parseJson2BeanAsList(String jsonAsString, Class<T> pojoClass) {
        try {
            JavaType type = objectMapper.getTypeFactory().constructParametricType(List.class, pojoClass);
            return objectMapper.readValue(jsonAsString, type);
        } catch (JsonParseException e) {
            LOGGER.error("parseJson2BeanAsList/JsonParseException ERROR.", e);
        } catch (JsonMappingException e) {
            LOGGER.error("parseJson2BeanAsList/JsonMappingException ERROR.", e);
        } catch (IOException e) {
            LOGGER.error("parseJson2BeanAsList/IOException ERROR.", e);
        }

        return null;
    }

    /**
     * 将json字符串转换成set对象
     *
     * @param jsonAsString
     * @param pojoClass
     * @return
     */
    public static <T> Set<T> parseJson2BeanAsSet(String jsonAsString, Class<T> pojoClass) {
        try {
            JavaType type = objectMapper.getTypeFactory().constructParametricType(Set.class, pojoClass);
            return objectMapper.readValue(jsonAsString, type);
        } catch (JsonParseException e) {
            LOGGER.error("parseJson2BeanAsSet/JsonParseException ERROR.", e);
        } catch (JsonMappingException e) {
            LOGGER.error("parseJson2BeanAsSet/JsonMappingException ERROR.", e);
        } catch (IOException e) {
            LOGGER.error("parseJson2BeanAsSet/IOException ERROR.", e);
        }

        return null;
    }

    /**
     * 将json字符串转换成map对象
     *
     * @param jsonAsString
     * @param keyTypeClass
     * @param valueTypeClass
     * @return
     */
    public static <K, V> Map<K, V> parseJson2BeanAsMap(String jsonAsString, Class<K> keyTypeClass,
                                                       Class<V> valueTypeClass) {
        try {
            JavaType type = objectMapper.getTypeFactory().constructParametricType(Map.class,
                    new Class[]{keyTypeClass, valueTypeClass});
            return objectMapper.readValue(jsonAsString, type);
        } catch (JsonParseException e) {
            LOGGER.error("parseJson2BeanAsMap/JsonParseException ERROR.", e);
        } catch (JsonMappingException e) {
            LOGGER.error("parseJson2BeanAsMap/JsonMappingException ERROR.", e);
        } catch (IOException e) {
            LOGGER.error("parseJson2BeanAsMap/IOException ERROR.", e);
        }

        return null;
    }

    /**
     * 将json字符串转换成更复杂的reference对象
     *
     * @param jsonAsString
     * @param typeReference
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T parseJson2BeanAsTypeReference(String jsonAsString, TypeReference<T> typeReference) {
        try {
            if (String.class.equals(typeReference.getType())) {
                return (T) jsonAsString;
            }
            return objectMapper.readValue(jsonAsString, typeReference);
        } catch (JsonParseException e) {
            LOGGER.error("parseJson2BeanAsTypeReference/JsonParseException ERROR.", e);
        } catch (JsonMappingException e) {
            LOGGER.error("parseJson2BeanAsTypeReference/JsonMappingException ERROR.", e);
        } catch (IOException e) {
            LOGGER.error("parseJson2BeanAsTypeReference/IOException ERROR.", e);
        }

        return null;
    }
}
