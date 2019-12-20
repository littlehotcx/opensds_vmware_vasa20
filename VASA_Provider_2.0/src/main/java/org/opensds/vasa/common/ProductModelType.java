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
 * 产品类型枚举类
 *
 * @author x00102290
 * @version [版本号V001R010C00, 2011-12-14]
 * @since 1.0
 */
public enum ProductModelType {

    /**
     * 枚举变量
     */
    V1500(0,
            /** 枚举变量 */
            "V1500"),

    /**
     * 枚举变量
     */
    OSP_V1800(1,
            /** 枚举变量 */
            "V1800"),

    /**
     * 枚举变量
     */
    OSP_S2100(2,
            /** 枚举变量 */
            "S2100"),

    /**
     * 枚举变量
     */
    OSP_S2300(3,
            /** 枚举变量 */
            "S2300"),

    /**
     * 枚举变量
     */
    OSP_S5100(4,
            /** 枚举变量 */
            "S5100"),

    /**
     * 枚举变量
     */
    OSP_S5300(5,
            /** 枚举变量 */
            "S5300"),

    /**
     * 枚举变量
     */
    OSP_S5500(6,
            /** 枚举变量 */
            "S5500"),

    /**
     * 枚举变量
     */
    OSP_S5600(7,
            /** 枚举变量 */
            "S5600"),

    /**
     * 枚举变量
     */
    VIS(8,
            /** 枚举变量 */
            "VIS6000"),

    /**
     * 枚举变量
     */
    OSP_S2600(9,
            /** 枚举变量 */
            "S2600"),

    /**
     * 枚举变量
     */
    OSP_S6800E(10,
            /** 枚举变量 */
            "S6800E"),

    /**
     * 枚举变量
     */
    OSP_V1500N(11,
            /** 枚举变量 */
            "V1500N"),

    /**
     * 枚举变量
     */
    OSP_S2300E(12,
            /** 枚举变量 */
            "S2300E"),

    /**
     * 枚举变量
     */
    OSP_COMMON(13,
            /** 枚举变量 */
            "OSP_COMMON"),

    /**
     * 枚举变量
     */
    N8000(15,
            /** 枚举变量 */
            "N8000"),

    /**
     * 枚举变量
     */
    S6900(16,
            /** 枚举变量 */
            "S6900"),

    /**
     * 枚举变量
     */
    S3900(17,
            /** 枚举变量 */
            "S3900"),

    S2900(42,

            "S2900"),

    /**
     * 枚举变量
     */
    S5500T(18,
            /** 枚举变量 */
            "S5500T"),

    /**
     * 枚举变量
     */
    S5600T(19,
            /** 枚举变量 */
            "S5600T"),

    /**
     * 枚举变量
     */
    S6800T(20,
            /** 枚举变量 */
            "S6800T"),

    /**
     * 枚举变量
     */
    S6900_M100(21,
            /** 枚举变量 */
            "S6900-M100"),

    /**
     * 枚举变量
     */
    S3900_M200(22,
            /** 枚举变量 */
            "S3900-M200"),

    /**
     * 枚举变量
     */
    S3900_M300(23,
            /** 枚举变量 */
            "S3900-M300"),

    /**
     * 枚举变量
     */
    S5900_M100(24,
            /** 枚举变量 */
            "S5900-M100"),

    /**
     * 等待王亞東接口更新
     */
    S8100(25,
            /** 等待王亞東接口更新 */
            "S8100"),

    /**
     * 枚举变量
     */
    S5800T(26,
            /** 枚举变量 */
            "S5800T"),

    /**
     * S5000C02新增设备类型(S2600的一种)
     */
    V1600N(27,
            /** S5000C02新增设备类型(S2600的一种) */
            "V1600N"),

    /**
     * 枚举变量
     */
    S5900_M200(28,
            /** 枚举变量 */
            "S5900-M200"),

    /**
     * 枚举变量
     */
    N8300(35,
            /** 枚举变量 */
            "N8000"),

    /**
     * 枚举变量
     */
    N8500(36,
            /** 枚举变量 */
            "N8000"),

    /**
     * 枚举变量
     */
    S2600T(30,
            /** 枚举变量 */
            "S2600T"),
    /**
     * 枚举变量
     */
    VIS_800(800,
            /** 枚举变量 */
            "VIS8200"),

    /**
     * 引入新产品型号S8000-I（临时使用，ISM对外发布目前不知该产品型号）
     */
    S8000_I(31,
            /** 引入新产品型号S8000-I（临时使用，ISM对外发布目前不知该产品型号） */
            "S8000-I"),

    /**
     * 枚举变量
     */
    VIS_801(801,
            /** 枚举变量 */
            "VIS8400"),

    /**
     * 枚举变量
     */
    VIS_802(802,
            /** 枚举变量 */
            "VIS8600"),

    /**
     * 枚举变量
     */
    VIS_803(803,
            /** 枚举变量 */
            "VIS8800"),
    /**
     * 暂时写成100，后面分配正式ID后再改
     */
    C3(100,
            /** 暂时写成100，后面分配正式ID后再改 */
            "C3"),

    /**
     * 枚举变量
     */
    Dorado2100(60,
            /** 枚举变量 */
            "Dorado2100"),

    /**
     * 枚举变量
     */
    Dorado2100G2(44,
            /** 枚举变量 */
            "Dorado2100 G2"),

    /**
     * VIS6000T新增
     */
    VIS6000T(45,
            /** VIS6000T新增 */
            "VIS6000T"),

    /**
     * 枚举变量
     */
    Dorado5100(32,
            /** 枚举变量 */
            "Dorado5100"),

    /**
     * 枚举变量
     */
    S2200T(43,
            /** 枚举变量 */
            "S2200T"),

    /**
     * 枚举变量
     */
    HVS_85T(46,
            /** 枚举变量 */
            "HVS_85T"),

    /**
     * 枚举变量
     */
    HVS_88T(47,
            /** 枚举变量 */
            "HVS_88T"),

    /**
     * 枚举变量
     */
    V3_6800(61,
            /** 枚举变量 */
            "6800 V3"),

    /**
     * 枚举变量
     */
    V3_6900(62,
            /** 枚举变量 */
            "6900 V3"),

    /**
     * 枚举变量
     */
    V3_5600(63,
            /** 枚举变量 */
            "5600 V3"),

    /**
     * 枚举变量
     */
    V3_5800(64,
            /** 枚举变量 */
            "5800 V3"),

    /**
     * 枚举变量
     */
    V3_5500(68,
            /** 枚举变量 */
            "6800 V3"),

    /**
     * 枚举变量
     */
    V3_2600(69,
            /** 枚举变量 */
            "6800 V3"),

    /**
     * 枚举变量
     */
    V3_5300(70,
            /** 枚举变量 */
            "5300 V3"),

    V5_2800(92,
            "2800 V5"),
    V5_5300(93,
            "5300 V5"),
    V5_5300F(94,
            "5300F V5"),
    V5_5500(95,
            "5500 V5"),
    V5_5500F(96,
            "5500F V5"),
    V5_5600(97,
            "5600 V5"),
    V5_5600F(98,
            "5600F V5"),
    V5_5800(99,
            "5800 V5"),
    V5_5800F(100,
            "5800F V5"),
    V5_6800(101,
            "6800 V5"),
    V5_6800F(102,
            "6800F V5"),
    V5_5500Elite(107,
            "5500 V5 Elite"),
    /**
     * 枚举变量
     */
    UNKNOW(-1,
            /** 枚举变量 */
            "UNKNOW");

    private String name;

    private int value;

    private ProductModelType(int value, String nameIn) {
        this.value = value;
        this.name = nameIn;
    }

    /**
     * 方法 ： getProductModel
     *
     * @param productModelValue 方法参数：productModelValue
     * @return ProductModelType 返回结果
     */
    public static ProductModelType getProductModel(int productModelValue) {
        ProductModelType[] types = ProductModelType.values();
        for (ProductModelType productModelType : types) {
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
     * @return ProductModelType 返回结果
     */
    public static ProductModelType getProductModel(String productModelValue) {
        ProductModelType[] types = ProductModelType.values();
        for (ProductModelType productModelType : types) {
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
            case V1500:
            case OSP_V1800:
            case OSP_S2100:
            case OSP_S2300:
            case OSP_V1500N:
            case OSP_S2300E:
                return DeviceType.S2300;
            case OSP_S5100:
            case OSP_S5300:
            case OSP_S5500:
            case OSP_S5600:
            case OSP_S6800E:
                return DeviceType.S5000;
            case OSP_S2600:
            case V1600N:
                return DeviceType.S2600;
            case VIS_800:
            case VIS_801:
            case VIS_802:
            case VIS_803:
            case VIS6000T:
            case VIS:
                return DeviceType.VIS;
            case S8100:
                return DeviceType.S8000;
            case N8000:
                /* 2011-09-28 l00003723 增加两种类型否则会返回OceanStor，导致无法发现 begin */
            case N8300:
            case N8500:
                /* 2011-09-28 l00003723 增加两种类型否则会返回OceanStor，导致无法发现 end */
                return DeviceType.N8000;
            case S8000_I:
            case S2900:
            case S3900:
            case S6900:
            case S5500T:
            case S5600T:
            case S5800T:
            case S6800T:
            case S6900_M100:
            case S3900_M200:
            case S3900_M300:
            case S5900_M100:
            case S5900_M200:
            case S2600T:
            case S2200T:
                return DeviceType.NEX;
            case C3:
            case HVS_85T:
            case HVS_88T:
                return DeviceType.C3;
            case Dorado2100:
            case Dorado2100G2:
            case Dorado5100:
                return DeviceType.Dorado2100;
            default:
                return DeviceType.OceanStor;
        }
    }

    /**
     * 方法 ： getDeviceTypeV2
     *
     * @return DeviceType 返回结果
     */
    public DeviceType getDeviceTypeV2() {
        switch (this) {
            case S5500T:
            case S5600T:
            case S5800T:
            case S6800T:
            case S2600T:
            case HVS_85T:
            case HVS_88T:
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
}
