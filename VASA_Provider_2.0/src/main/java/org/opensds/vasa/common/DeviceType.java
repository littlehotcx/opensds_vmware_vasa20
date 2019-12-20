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

import java.io.ObjectStreamException;

/**
 * 设备类型
 *
 * @author l00102779  梁建国
 * @version [V100R011C00, 2009-3-30]
 * @see [相关类/方法]
 * @since [ISM/V100R001 Client]
 */
public enum DeviceType {
    /**
     * 枚举变量
     */
    N8000("N8000"),
    /**
     * 枚举变量
     */
    S2300("S2300"),
    /**
     * 枚举变量
     */
    S2600("S2600"),
    /**
     * 枚举变量
     */
    T3000("T3000"),
    /**
     * 枚举变量
     */
    S5000("S5000"),
    /**
     * 枚举变量
     */
    S5300("S5300"),
    /**
     * 枚举变量
     */
    AKI("AKI"),
    /**
     * 枚举变量
     */
    NEX("NEX"),
    /**
     * 枚举变量
     */
    OceanStor("OceanStor"),
    /**
     * 枚举变量
     */
    VIS("VIS"),
    /**
     * 枚举变量
     */
    S8000("S8000"),
    /**
     * 枚举变量
     */
    S5500("S5500"),
    /**
     * 枚举变量
     */
    S5600("S5600"),
    /**
     * 枚举变量
     */
    S6800e("S6800e"),
    /**
     * 枚举变量
     */
    C3("C3"),
    /**
     * 枚举变量
     */
    VTL3500("VTL3500"),
    /**
     * 枚举变量
     */
    VTL6000("VTL6000"),
    /**
     * 枚举变量
     */
    SIR6000("SIR6000"),
    /**
     * 枚举变量
     */
    HDP3500("HDP3500"),
    /**
     * 枚举变量
     */
    AllInOne("AllInOne"),
    /**
     * 枚举变量
     */
    Dorado2100("Dorado2100"),
    /**
     * 枚举变量
     */
    Dorado("Dorado"),
    /**
     * 枚举变量
     */
    N5000("N5000"),
    /**
     * 枚举变量
     */
    HVS88T("HVS88T"),
    /**
     * 枚举变量
     */
    SWITCHFC("SWITCHFC"),
    /**
     * 枚举变量
     */
    SWITCHFIBER("SWITCHFIBER"),

    //添加FusionStorage
    FusionStorage("FusionStorage"),
    OceanSto("OceanSto");


    private static final long serialVersionUID = 5126097720840441893L;

    private String subType;

    private String description = null;

    /**
     * 构造函数
     *
     * @param description 类型描述
     */
    private DeviceType(String description) {
        this.description = description;
    }

    /**
     * 获取设备类型
     *
     * @param type 方法参数：type
     * @return DeviceType 返回结果
     * @see
     */
    public static DeviceType getDeviceType(String type) {
        for (DeviceType deviceType : DeviceType.values()) {
            if (deviceType.getDescription().equals(type)) {
                return deviceType;
            }
        }

        return null;
    }

    /**
     * 方法 ： getDescription
     *
     * @return String 返回结果
     */
    public String getDescription() {
        return description;
    }

    /**
     * <功能详细描述>
     *
     * @return Object [返回类型说明]
     * @throws ObjectStreamException [参数说明]
     * @throws throws                [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    @SuppressWarnings("serial")
    private Object readResolve() throws ObjectStreamException {
        String desc = this.getDescription();
        DeviceType type = valueOf(desc);
        if (null != type) {
            type.subType = this.getSubType();
        }
        //异外情况 抛出异常
        throw new ObjectStreamException("Can't find the device type which description is \"" + desc + "\"") {
        };
    }

    /**
     * 判断选择的设备类型是否是NAS系列
     *
     * @param type 选择的设备类型的描述
     * @return boolean [返回类型说明]
     * @see [类、类#方法、类#成员]
     */
    public static boolean isNASStorSerias(String type) {
        DeviceType deviceType = valueOf(type);
        if (null == deviceType) {
            return false;
        }

        if (N8000.getDescription().equalsIgnoreCase(type) || T3000.getDescription().equalsIgnoreCase(type)) {
            return true;
        }

        return false;
    }

    /**
     * 判断选择的设备类型是否是NAS系列
     *
     * @param deviceType 方法参数：deviceType
     * @return boolean [返回类型说明]
     * @see [类、类#方法、类#成员]
     */
    public static boolean isNASStorSerias(DeviceType deviceType) {
        if (null == deviceType) {
            return false;
        }

        if (N8000.equals(deviceType) || T3000.equals(deviceType)) {
            return true;
        }

        return false;
    }

    /**
     * 判断选择的设备类型是否是VTL系列
     *
     * @param type 选择的设备类型的描述
     * @return boolean [返回类型说明]
     * @see [类、类#方法、类#成员]
     */
    public static boolean isVTLSerias(String type) {
        DeviceType deviceType = valueOf(type);
        if (null == deviceType) {
            return false;
        }

        if (VTL3500.getDescription().equalsIgnoreCase(type) || VTL6000.getDescription().equalsIgnoreCase(type)
                || AllInOne.getDescription().equalsIgnoreCase(type)) {
            return true;
        }

        return false;
    }

    /**
     * 是否是VIS系列产品
     *
     * @param type 方法参数：type
     * @return boolean [返回类型说明]
     */
    public static boolean isVISSerias(DeviceType type) {
        if (null == type) {
            return false;
        }

        if (S8000 == type || VIS == type) {
            return true;
        }

        return false;
    }

    /**
     * 是否是VIS系列产品
     *
     * @param type 方法参数：type
     * @return boolean [返回类型说明]
     */
    public static boolean isVISSerias(String type) {
        if (null == type) {
            return false;
        }

        DeviceType deviceType = valueOf(type);
        if (null == deviceType) {
            return false;
        }

        if (S8000 == deviceType || VIS == deviceType) {
            return true;
        }

        return false;
    }

    /**
     * 判断选择的设备类型是否是存储单元系列
     *
     * @param type 选择的设备类型的描述
     * @return boolean [返回类型说明]
     * @see [类、类#方法、类#成员]
     */
    public static boolean isStorageCellSerias(String type) {
        DeviceType deviceType = valueOf(type);
        if (null == deviceType) {
            return false;
        }

        return !isNASStorSerias(type) && !isVISSerias(deviceType);
    }

    /**
     * 每种设备可能具有不同的子类型：如T3000设备包括N3500,HDP3500,VTL3500等
     * <功能详细描述>
     *
     * @return String [返回类型说明]
     * @see [类、类#方法、类#成员]
     */
    public String getSubType() {
        return subType;
    }

    /**
     * 方法 ： setSubType
     *
     * @param subType 方法参数：subType
     */
    public void setSubType(String subType) {
        this.subType = subType;
    }

}
