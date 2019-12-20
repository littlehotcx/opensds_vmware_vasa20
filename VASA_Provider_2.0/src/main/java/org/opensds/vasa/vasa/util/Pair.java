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

import org.opensds.vasa.common.IsmConstant;

/**
 * 提供以key为排序对象的键值对
 *
 * @param <K> 参数k
 * @param <V> 参数V
 * @author x00102290
 * @version [版本号V001R010C00, 2011-12-14]
 * @see
 * @since
 */
public class Pair<K, V> implements Comparable<Pair<K, V>> {
    private K key;


    private V value;


    /**
     * 方法 ： Pair
     *
     * @param keyIn   方法参数：keyIn
     * @param valueIn 方法参数：valueIn
     */
    public Pair(K keyIn, V valueIn) {
        super();
        if (keyIn == null) {
            throw new NullPointerException();
        }

        this.key = keyIn;
        this.value = valueIn;
    }

    /**
     * 方法 ： getKey
     *
     * @return K 返回结果
     */
    public K getKey() {
        return key;
    }

    /**
     * 方法 ： getValue
     *
     * @return V 返回结果
     */
    public V getValue() {
        return value;
    }

    /**
     * 方法 ： compareTo
     *
     * @param pair 方法参数：pair
     * @return int 返回结果
     */
    @SuppressWarnings("unchecked")
    public int compareTo(Pair<K, V> pair) {
        if (null != pair && null != key) {
            if (key instanceof Comparable) {
                return ((Comparable) key).compareTo(pair.key);
            }
        }

        return 0;
    }

    /**
     * 方法 ： equals
     *
     * @param obj 方法参数：obj
     * @return boolean 返回结果
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Pair)) {
            return false;
        }

        Pair that = (Pair) obj;
        return key.equals(that.key);
    }

    /**
     * 方法 ： hashCode
     *
     * @return int 返回结果
     */
    @Override
    public int hashCode() {
        return key.hashCode();
    }

    /**
     * 方法 ： toString
     *
     * @return String 返回结果
     */
    @Override
    public String toString() {
        if (null == value || "".equals(value)) {
            return IsmConstant.BLANK_CONTENT;
        }

        return value.toString();
        //        return _key.toString();
    }

}
