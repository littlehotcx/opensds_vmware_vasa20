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
 * ISM系统常用常量类
 * 元素分割符
 *
 * @author qKF 10218
 * @version [版本号V001R010C00, 2011-12-14]
 * @since ISM common:util
 */
public class IsmConstant {
    /**
     * qKF10218 定一个比较通用的分割符
     */
    public static final String ELEMENT_SEPARATOR = "@@@@@@";

    /**
     * 电子邮件分隔符号
     */
    public static final String EMAIL_SEPARATOR = ";";

    /**
     * 手机号码分隔符号
     */
    public static final String MOBILE_PHONE_SEPARATOR = ";";

    /**
     * 性能数据分隔符
     */
    public static final String PERFORMANCE_DATA_SEPARATOR = ",";

    /**
     * 告警参数分隔符
     */
    public static final String ALARM_PARAMETER_SEPARATOR = ",";

    //告警参数解释字符串中的占位字符串(正则表达式)前缀
    /**
     * 公共属性ALARM_PARAMETER_PLACEHOLDER_PREFIX
     */
    public static final String ALARM_PARAMETER_PLACEHOLDER_PREFIX = "##";

    //V1R1版本的告警参数解释字符串中的占位字符串(正则表达式)
    /**
     * 公共属性ALARM_PARAMETER_PLACEHOLDER_OLD
     */
    public static final String ALARM_PARAMETER_PLACEHOLDER_OLD = "#???";

    /**
     * 空内容
     */
    public static final String BLANK_CONTENT = "--";

    /**
     * 回车换行
     */
    public static final String NEW_LINE = System.getProperty("line.separator");

    /**
     * 空格
     */
    public static final String BLANK = " ";

    /**
     * 文件分隔符
     */
    public static final String FILE_SEPARATOR = System.getProperty("file.separator");

    /**
     * 电源温度状态门限
     */
    public static final int TEMPERATURE_THRESHOLD = 50;

    //begin　 qKF10218 全二明  2008-8-18 AZ7D01197　增加告警导出功能的常量分割符
    /**
     * 告警文件中属性分割符号
     */
    public static final String ALARM_ATTRIBUTE_SEPEARATOR = "\t";

    /**
     * xls分割符号
     */
    public static final String EXCEL_SEPEARATOR = "\t";

    /**
     * CSV分割符号
     */
    public static final String CSV_SEPEARATOR = ",";

    /**
     * 当告警中有分割符时，使用替代符号
     */
    public static final String REPLEASE_SEPEARATOR = " ";

    //end　 qKF10218 全二明  2008-8-18 AZ7D01197　增加告警导出功能的常量分割符

    /**
     * 系统中表示实物的名字不能使无效的字符串
     */
    public static final String INVALID_NAME_STRING = "--";

    /**
     * 设备类型分割符
     */
    public static final String DEVICE_TYPE_SEPARATOR = ";";

    /**
     * 设备子类型分割符
     */
    public static final String DEVICE_SUB_TYPE_SEPARATOR = ",";

    /**
     * 公共属性COLON
     */
    public static final String COLON = ":";

    /**
     * 每次最大清除告警数
     */
    public static final int MAXCLEARALARMNUM = 50;

    /**
     * 升级等待时间 单位：秒
     */
    public static final int TIMEOUTOFISMUPGRADE = 30;

    /**
     * 公共属性SWAP_SYSTEM_NINPERTIME
     */
    public static final int SWAP_SYSTEM_NINPERTIME = 2000;

    /**
     * 公共属性SWAP_SYSTEM_MAXPERTIME
     */
    public static final int SWAP_SYSTEM_MAXPERTIME = 32000;

    /**
     * 公共属性SWAP_SYSTEM_TOTALTIME
     */
    public static final int SWAP_SYSTEM_TOTALTIME = 300000;

    /**
     * 公共属性TIME_INTERVAL_OF_SES_UPGRADE_ONEBOARD
     */
    public static final int TIME_INTERVAL_OF_SES_UPGRADE_ONEBOARD = 10;

    /**
     * 公共属性SS_TIMES_INTERVAL
     */
    public static final int SS_TIMES_INTERVAL = 30;

    /**
     * 升级等待时间修改为80分钟返回。说s2600 cbb决议。r90003224
     * 修改升级时间为45分钟，xuchong知会；f90002221
     */
    public static final int SS_TIMESAFTERSYSTEMACTIVED = 90;

    //T系列产品升级过程的连续连接不上设备的检查次数
    /**
     * 公共属性NEX_UPGRADE_CHECK_TIMES
     */
    public static final int NEX_UPGRADE_CHECK_TIMES = 240;


    //T系列升级前检查与升级后验证的连续连接不上设备的检查时间次数
    /**
     * 公共属性NEX_UPGRADE_CHECHING_TIMES
     */
    public static final int NEX_UPGRADE_CHECHING_TIMES = 90;

    /**
     * 公共属性SS_OPLDTIMESAFTERSYSTEMACTIVED
     */
    public static final int SS_OPLDTIMESAFTERSYSTEMACTIVED = 90;

    /**
     * 公共属性CONN_HEARTBEATPERIOD
     */
    public static final int CONN_HEARTBEATPERIOD = 60;

    /**
     * 公共属性CONN_SHORTTIMEOUT
     */
    public static final int CONN_SHORTTIMEOUT = 15;

    /**
     * 连接设备超时的超时时间
     */
    public static final int CONN_TIMEOUT = 310;

    //改用HTTPS后，在不支持HTTPS的agent上会超时返回，所以将超时时间改短，如果使用HTTPS不能连上，则切换HTTP建立连接
    /**
     * 用户鉴权超时时间
     */
    public static final int AUTHENTICATE_SHORT_TIMEOUT = 60;

    /**
     * 公共属性GET_BATCH_RESULTS_TIMEOUT
     */
    public static final int GET_BATCH_RESULTS_TIMEOUT = 120;

    /**
     * 重试机制中的重试次数设置。lkf23274 AZ8D10491，修改为不重试
     */
    public static final int CONN_RETRYTIMES = 0;

    /**
     * 公共属性TIME_INTERVAL_OF_CHECK_ISM_UPGRADESTATUS
     */
    public static final int TIME_INTERVAL_OF_CHECK_ISM_UPGRADESTATUS = 15;

    /**
     * 公共属性CHECK_ISM_UPGRADE_STATUS_TIMES
     */
    public static final int CHECK_ISM_UPGRADE_STATUS_TIMES = 12;

    /**
     * 公共属性CHECK_SES_UPGRADE_STATUS_TIMES
     */
    public static final int CHECK_SES_UPGRADE_STATUS_TIMES = 24;

    /**
     * 公共属性CHECK_FORCE_UPGRADE_TIMES
     */
    public static final int CHECK_FORCE_UPGRADE_TIMES = 24;

    /**
     * 公共属性TIME_INTERVAL_CHECK_FOR_UPGRADING
     */
    public static final int TIME_INTERVAL_CHECK_FOR_UPGRADING = 10;

    /*
     * 整数常量
     */
    /**
     * 创建定时会话的最大源LUN数
     */
    public static final int MAX_SRCLUN = 8;

    /**
     * 公共属性CONST_ZERO
     */
    public static final int CONST_ZERO = 0;

    /**
     * 公共属性CONST_ONE
     */
    public static final int CONST_ONE = 1;

    /**
     * 公共属性CONST_TWO
     */
    public static final int CONST_TWO = 2;

    /**
     * 公共属性CONST_THREE
     */
    public static final int CONST_THREE = 3;

    /**
     * 公共属性CONST_FOUR
     */
    public static final int CONST_FOUR = 4;

    /**
     * 公共属性CONST_FIVE
     */
    public static final int CONST_FIVE = 5;

    /**
     * 公共属性CONST_SIX
     */
    public static final int CONST_SIX = 6;

    /**
     * 公共属性CONST_SEVEN
     */
    public static final int CONST_SEVEN = 7;

    /**
     * 公共属性CONST_EIGHT
     */
    public static final int CONST_EIGHT = 8;


    /**
     * 公共属性CONST_NINE
     */
    public static final int CONST_NINE = 9;


    /**
     * 公共属性CONST_TEN
     */
    public static final int CONST_TEN = 10;


    /**
     * 公共属性CONST_ELEVEN
     */
    public static final int CONST_ELEVEN = 11;


    /**
     * 公共属性CONST_TWELVE
     */
    public static final int CONST_TWELVE = 12;


    /**
     * 公共属性CONST_13
     */
    public static final int CONST_13 = 13;


    /**
     * 公共属性CONST_14
     */
    public static final int CONST_14 = 14;


    /**
     * 公共属性CONST_16
     */
    public static final int CONST_16 = 16;


    /**
     * 公共属性CONST_17
     */
    public static final int CONST_17 = 17;


    /**
     * 公共属性CONST_20
     */
    public static final int CONST_20 = 20;


    /**
     * 公共属性CONST_21
     */
    public static final int CONST_21 = 21;


    /**
     * 公共属性CONST_22
     */
    public static final int CONST_22 = 22;


    /**
     * 公共属性CONST_23
     */
    public static final int CONST_23 = 23;


    /**
     * 公共属性CONST_30
     */
    public static final int CONST_30 = 30;


    /**
     * 公共属性CONST_37
     */
    public static final int CONST_37 = 37;


    /**
     * 公共属性CONST_3600
     */
    public static final int CONST_3600 = 3600;


    /**
     * 公共属性CONST_1800
     */
    public static final int CONST_1800 = 1800;


    /**
     * 公共属性CONST_4095
     */
    public static final int CONST_4095 = 4095;


    /**
     * 公共属性CONST_252
     */
    public static final int CONST_252 = 252;


    /**
     * 公共属性CONST_365
     */
    public static final int CONST_365 = 365;


    /**
     * 公共属性CONST_24
     */
    public static final int CONST_24 = 24;


    /**
     * 公共属性CONST_32
     */
    public static final int CONST_32 = 32;


    /**
     * 公共属性CONST_40
     */
    public static final int CONST_40 = 40;


    /**
     * 公共属性CONST_48
     */
    public static final int CONST_48 = 48;


    /**
     * 公共属性CONST_56
     */
    public static final int CONST_56 = 56;


    /**
     * 公共属性CONST_60
     */
    public static final int CONST_60 = 60;


    /**
     * 公共属性CONST_LONG_123
     */
    public static final long CONST_LONG_123 = 123L;


    /**
     * 公共属性CONST_LONG_80
     */
    public static final long CONST_LONG_80 = 80;

    /**
     * 公共属性TABLE_NULL_SELECT
     */
    public static final int TABLE_NULL_SELECT = -1;

    /**
     * 没有定义的值
     */
    public static final int UNDEFINE_VALUE = Integer.MIN_VALUE;

    /**
     * 没有定义的值
     */
    public static final long UNDEFINE_LONG_VALUE = Long.MIN_VALUE;

    /**
     * 公共属性UNDEFINE_DOUBLE_VALUE
     */
    public static final double UNDEFINE_DOUBLE_VALUE = Double.MIN_VALUE;

    /**
     * 全F值
     */
    public static final long MAX_VALUE = 4294967295L;

    /*
     * 模块不可用的标识常量，用于功能模块裁减
     */
    /**
     * 用户管理模块
     */
    public static final String USER_MODULE = "DISABLE_USER_MODULE";

    /**
     * TRAPIP
     */
    public static final String TRAPIP_MODULE = "DISABLE_TRAPIP_MODULE";

    /**
     * 告警模块
     */
    public static final String ALARM_MODULE = "DISABLE_ALARM_MODULE";

    /**
     * 性能统计
     */
    public static final String FPS_MODULE = "DISABLE_PFS_MODULE";

    /**
     * 发现
     */
    public static final String DISCOVERY_MODUEL = "DISABLE_DISCOVERY_MODULE";

    /**
     * 启动器  只能接后端硬盘端口
     **/
    public static final String DEVICE_PORT_TYPE_INI = "1";

    /**
     * 目标器  只能接主机
     **/
    public static final String DEVICE_PORT_TYPE_TGT = "2";

    /**
     * 同口  可以接主机与阵列
     ***/
    public static final String DEVICE_PORT_TYPE_BOTH = "3";

    /**
     * 未定义的值 -1
     */
    public static final int UNCONFIG_VALUE = 0xFFFFFFFF;


    /**
     * 最大可管理的设备数
     */
    public static final int MAX_DEVICE = 32;

    //  begin DTS2012100806548 新需求-云备份-添加未同步数据量 y00219326 2012-11-08
    /**
     * uint_64 全F值
     */
    public static final double DOUBLE_MAX_VALUE = 18446744073709551615D;

    //  end DTS2012100806548 新需求-云备份-添加未同步数据量 y00219326 2012-11-08

    /**
     * long 类型的1000
     */
    public static final long TIME_NUM = 1000L;


    /**
     * Long 类型的345
     */
    public static final long CONST_345 = 345L;


    /**
     * Long 类型的50
     */
    public static final long CONST_LONG_50 = 50L;


    /**
     * Long 类型的1000
     */
    public static final int CONST_INT_1000 = 1000;


    /**
     * Double 类型的1024
     */
    public static final double DOUBLE_1024 = 1024d;


    /**
     * 十六进制两个F
     */
    public static final int DOUBLE_FF = 0xFF;


    /**
     * 公共属性ONE_FF
     */
    public static final int ONE_FF = 0xF;


    /**
     * 公共属性THREE_FF
     */
    public static final int THREE_FF = 0xFFF;


    /**
     * 公共属性DOUBLE_LONG_FF
     */
    public static final long DOUBLE_LONG_FF = 0xFFL;


    /**
     * 公共属性EIGHT_LONG_FF
     */
    public static final long EIGHT_LONG_FF = 0xffffffffL;


    /**
     * 公共属性NEGATIVE_THREE
     */
    public static final int NEGATIVE_THREE = -3;


    /**
     * 公共属性NEGATIVE_ONE
     */
    public static final int NEGATIVE_ONE = -1;

}
