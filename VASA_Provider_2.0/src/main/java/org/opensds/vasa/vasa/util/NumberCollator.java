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


/**
 * 数字比较器（带有逗号的数字描述的比较器对象）
 * 使用放入表格中使用逗号分开的数字字串比较使用该对象
 *
 * @author m38476 毛木金
 * @version [版本号V001R010C00, 2011-12-14]
 * @since [ISM/V100R001 Client]
 */
public class NumberCollator implements Comparable {
    /**
     * double/long数值类型
     */
    private double number;

    /**
     * 使用逗号分割的字串
     */
    private String numberDesc;


    /**
     * <默认构造函数>
     *
     * @param number long类型的数字
     */
    public NumberCollator(long number) {
        this.number = number;
        this.numberDesc = Unit.commaFormat(number);
    }

    /**
     * <默认构造函数>
     *
     * @param number double类型的数字
     */
    public NumberCollator(double number) {
        this.number = number;
        this.numberDesc = Unit.doubleFormat(number);
    }

    /**
     * 方法 ： toString
     *
     * @return String 返回结果
     */
    @Override
    public String toString() {
        return this.numberDesc;
    }

    /**
     * 方法 ： compareTo
     *
     * @param o 方法参数：o
     * @return int 返回结果
     */
    public int compareTo(Object o) {
        if (o instanceof NumberCollator) {
            NumberCollator obj = (NumberCollator) o;

            return Double.valueOf(this.number)
                    .compareTo(Double.valueOf(obj.number));
        }
        return 0;
    }

    /**
     * 方法 ： equals
     *
     * @param o 方法参数：o
     * @return boolean 返回结果
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof NumberCollator)) {
            return false;
        } else {
            NumberCollator nc = (NumberCollator) o;

            return Double.valueOf(number).equals(nc.number);
        }
    }

    /**
     * 方法 ： hashCode
     *
     * @return int 返回结果
     */
    @Override
    public int hashCode() {
        return Double.valueOf(number).hashCode();
    }

    /**
     * 方法 ： getValue
     *
     * @return double 返回结果
     */
    public double getValue() {
        return this.number;
    }

}
