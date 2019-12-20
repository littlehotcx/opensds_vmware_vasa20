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

package org.opensds.vasa.vasa.fusionstorageservice.enumandconst;

/**
 * Created by z00389905 on 2018/4/8 in vCenter-Web-Plugin-FS.
 */
public enum DeviceTypeEnum {
    HuaweiOceanStorStorage("HuaweiOceanStorStorage"),
    HuaweiFusionStorage("HuaweiFusionStorage"),
    ;

    private String name;

    DeviceTypeEnum(String name) {
        this.name = name;
    }

    public static DeviceTypeEnum getDeviceTypeEnum(String name) {
        for (DeviceTypeEnum deviceTypeEnum : DeviceTypeEnum.values()) {
            if (deviceTypeEnum.name().equals(name)) {
                return deviceTypeEnum;
            }
        }
        return null;
    }
}
