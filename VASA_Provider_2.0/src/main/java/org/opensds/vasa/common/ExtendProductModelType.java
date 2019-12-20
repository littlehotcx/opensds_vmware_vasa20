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

package org.opensds.vasa.common;

/**
 * 扩展的设备类型枚举
 *
 * @author g00250185
 * @version V100R001C10
 */
public enum ExtendProductModelType {
    /**
     * HVS_18800F
     */
    HVS_18800F(56, "18800F"),
    /**
     * HVS_18500
     */
    HVS_18500(57, "18500"),
    /**
     * HVS_18800
     */
    HVS_18800(58, "18800"),
    /**
     * H_18500V3
     */
    H_18500V3(72, "18500 V3"),

    HVS_18500V5(103, "18500 V5"),

    HVS_18500FV5(104, "18500F V5"),

    HVS_18800V5(105, "18800 V5"),

    HVS_18800FV5(106, "18800F V5"),

    /**
     * 枚举变量
     */
    UNKNOW(-1, "UNKNOW");


    private String name;


    private int value;


    private ExtendProductModelType(int value, String nameIn) {
        this.value = value;
        this.name = nameIn;
    }

    /**
     * 方法 ： getProductModel
     *
     * @param productModelValue 方法参数：productModelValue
     * @return ExtendProductModelType 返回结果
     */
    public static ExtendProductModelType getProductModel(int productModelValue) {
        ExtendProductModelType[] types = ExtendProductModelType.values();
        for (ExtendProductModelType productModelType : types) {
            if (productModelType.getValue() == productModelValue) {
                return productModelType;
            }
        }
        return UNKNOW;
    }

    /**
     * 方法 ： getProductModel
     *
     * @param productModelValue 方法参数：productModelValue
     * @return ExtendProductModelType 返回结果
     */
    public static ExtendProductModelType getProductModel(String productModelValue) {
        ExtendProductModelType[] types = ExtendProductModelType.values();
        for (ExtendProductModelType productModelType : types) {
            if (productModelType.name.equalsIgnoreCase(productModelValue)) {
                return productModelType;
            }
        }
        return UNKNOW;
    }

    /**
     * 方法 ： getValue
     *
     * @return int 返回结果
     */
    public int getValue() {
        return this.value;
    }

    /**
     * 方法 ： getDeviceType
     *
     * @return DeviceType 返回结果
     */
    public DeviceType getDeviceType() {
        switch (this) {
            case HVS_18800F:
            case HVS_18500:
            case HVS_18800:
            case H_18500V3:
                return DeviceType.C3;
            default:
                return DeviceType.OceanStor;
        }
    }

    /**
     * 方法 ： toString
     *
     * @return String 返回结果
     */
    @Override
    public String toString() {
        String des = "ProductModel_" + this.name();
        return des;
    }

    public String getName() {
        return name;
    }
}
