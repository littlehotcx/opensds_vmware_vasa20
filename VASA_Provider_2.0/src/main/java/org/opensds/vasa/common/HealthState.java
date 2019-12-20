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
 * 健康状态类
 *
 * @author x00102290
 * @version [版本号V001R010C00, 2011-12-14]
 * @since 1.0
 */
public enum HealthState {
    /**
     * 枚举变量
     */
    NORMAL("NORMAL"),

    /**
     * 枚举变量
     */
    UNKNOWN("UNKNOWN"),

    /**
     * 枚举变量
     */
    FAULTED("FAULT"),

    /**
     * 枚举变量
     */
    DEGRADE("DEGRADE"),

    /**
     * 枚举变量
     */
    I2CFAULT("I2CFAULT"),

    //电池状态,检测中 lkf23274 同步问题单AZ8D08854 增加电池状态
    /**
     * 枚举变量
     */
    CHECK("CHECK"),

    /**
     * 枚举变量
     */
    ILLEGAL("ILLEGAL"),

    //单链路故障状态
    /**
     * 枚举变量
     */
    SINGLE_LINK_FAULT("SINGLE_LINK_FAULT"),

    //隔离状态
    /**
     * 枚举变量
     */
    SEPARATE("SEPARATE"),

    //磁盘写保护
    /**
     * 枚举变量
     */
    WRITE_PROTECTED("WRITE_PROTECTED"),

    //休眠状态
    /**
     * 枚举变量
     */
    HIBERNATE("HIBERNATE"),

    //失效状态
    /**
     * 枚举变量
     */
    HALFLIFE("HALFLIFE"),

    //未认证
    /**
     * 枚举变量
     */
    UNAUTHENTICATION("UNAUTHENTICATION"),

    //阵列状态：系统正在上电
    /**
     * 枚举变量
     */
    SYS_STATUS_POWER_ON("SYS_STATUS_POWER_ON"),

    // 阵列状态：系统正在下电
    /**
     * 枚举变量
     */
    SYS_STATUS_POWER_OFF("SYS_STATUS_POWER_OFF"),

    //阵列状态： 系统正常模式运行
    /**
     * 枚举变量
     */
    SYS_STATUS_NORMALMODE("SYS_STATUS_NORMALMODE"),

    //阵列状态： 系统安全模式运行
    /**
     * 枚举变量
     */
    SYS_STATUS_SAFEMODE("SYS_STATUS_SAFEMODE"),

    //阵列状态： 系统正在升级第一个控制器
    /**
     * 枚举变量
     */
    SYS_STATUS_UPGRADE_FIRST("SYS_STATUS_UPGRADE_FIRST"),

    //阵列状态： 系统正在升级第二个控制器
    /**
     * 枚举变量
     */
    SYS_STATUS_UPGRADE_SECOND("SYS_STATUS_UPGRADE_SECOND"),

    //UPS健康状态
    /**
     * 枚举变量
     */
    INPUT_OVER_VOLT("INPUT_OVER_VOLT"),

    /**
     * 枚举变量
     */
    INPUT_UNDER_VOLT("INPUT_UNDER_VOLT"),

    /**
     * 枚举变量
     */
    POWER_OUTAGE("POWER_OUTAGE"),

    /**
     * 枚举变量
     */
    UPS_OVER_TEMP("UPS_OVER_TEMP"),

    /**
     * 枚举变量
     */
    UPS_OVER_CURRENT("UPS_OVER_CURRENT"),

    /**
     * 枚举变量
     */
    OUTPUT_SHORT_CIRCUIT("OUTPUT_SHORT_CIRCUIT"),

    /**
     * 枚举变量
     */
    OUTPUT_OVER_VOLT("OUTPUT_OVER_VOLT"),

    /**
     * 枚举变量
     */
    CHARGER_FAULT("CHARGER_FAULT"),

    /**
     * 枚举变量
     */
    BATTERY_FAULT("BATTERY_FAULT"),

    /**
     * 枚举变量
     */
    BATTERY_OVER_VOLT_PROTECT("BATTERY_OVER_VOLT_PROTECT"),

    /**
     * 枚举变量
     */
    BATTERY_UNDER_VOLT_PROTECT("BATTERY_UNDER_VOLT_PROTECT"),

    /**
     * 枚举变量
     */
    NOT_ENOUGH_TO_WRITE("NOT_ENOUGH_TO_WRITE"),

    /**
     * 枚举变量
     */
    BATTERY_OVERDUE_ALARM("BATTERY_OVERDUE_ALARM"),

    /**
     * 状态 忙
     */
    BUSY("BUSY"),

    // 初始化
    /**
     * 枚举变量
     */
    INITIALIZE("INITIALIZE"),

    // 初始化失败
    /**
     * 枚举变量
     */
    INITIALIZE_FAILD("INITIALIZE_FAILD"),

    // 删除中
    /**
     * 枚举变量
     */
    DELETING("DELETING"),

    // 删除失败
    /**
     * 枚举变量
     */
    DELETE_FAILD("DELETE_FAILD"),

    // 扩容中
    /**
     * 枚举变量
     */
    EXPANDING("EXPANDING");

    private String name;

    private HealthState(String nameIn) {
        name = nameIn;
    }

    /**
     * 方法 ： getName
     *
     * @return String 返回结果
     */
    public String getName() {
        return this.name;
    }

    /**
     * 方法 ： getDescription
     *
     * @return String 返回结果
     */
    public String getDescription() {
        return "State." + this.getName();
    }

    /**
     * 方法 ： toString
     *
     * @return String 返回结果
     */
    @Override
    public String toString() {
        return this.getDescription() + '|' + this.name;
    }
}
