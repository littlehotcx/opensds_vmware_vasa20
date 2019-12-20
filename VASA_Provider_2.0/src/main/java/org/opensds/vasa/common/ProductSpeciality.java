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
 * 所有可管理设备的特性列表，界面可通过读取某个产品支持哪些特殊来动态改变界面
 *
 * @author x00102290
 * @version [版本号V001R010C00, 2011-12-14]
 * @since V1R5
 */
public enum ProductSpeciality {
    /**
     * 支持Thin功能（NEXR1不支持此特性，NEXR2支持此特性。）
     */
    THIN_PROVISION,

    /**
     * 支持LUN映射给多个主机（T些列支持，但NEXR1不支持此特性，NEXR2支持此特性。）
     */
    LUN_MUTI_MAPPING,

    /**
     * 支持IPV6（T些列支持，但NEXR1不支持此特性，NEXR2支持此特性。）
     */
    IPV6,

    /**
     * 支持新旧License对比（T些列支持，但NEXR1不支持此特性，NEXR2支持此特性。）
     */
    LICENSE_COMPARE,

    /**
     * 支持NTP（T些列支持）
     */
    NTP,

    /**
     * 支持时区的查询与设置（T些列支持）
     */
    TIME_ZONE,

    /**
     * 设备支持用同步的方式下发命令，使得创建远程复制之类的操作可以同步等待返回结果（T些列支持，但NEXR1不支持此特性，NEXR2支持此特性。）
     */
    SYNC_CMD,

    /**
     * 支持使用文件方式保存性能数据并分析（性能统计新接口特性，NEXR1不支持此特性，NEXR2支持此特性。）
     */
    PERFORMANCE_DATA_IN_FILE,

    /**
     * 获取原始数据（生成升级报告，NEXR1不支持此特性，NEXR2支持此特性。）
     */
    EXPORT_ORIG_DATA,

    /**
     * 创建数据磁盘（N8000 V2特性）
     */
    CREATE_DATA_DISK,

    /**
     * 初始化文件系统（N8000 V2特性）
     */
    INIT_FILE_SYSTEM,

    /**
     * 多路径模式
     */
    ALUA,

    /**
     * 可访问IP
     **/
    ACCESSIP,

    /**
     * 动态扩盘
     **/
    ExpandDisk,

    /**
     * 分裂镜像
     */
    HyperClone,

    /**
     * SmartCache（s2600t特性，在特定硬件环境才支持smartchache）
     */
    SMARTCACHE,

    /**
     * 框点灯 （s2600t特性）
     */
    DISKSUBRACK_LIGHT,

    /**
     * 控制框和硬盘框点灯
     */
    DISKSUBRACK_LIGHT_CTRL_HARDDISK,

    /**
     * 快照
     */
    SNAPSHOT,

    /**
     * LUN拷贝
     */
    LUNCOPY,

    /**
     * 远程复制
     */
    HYPERREPLICATION,

    /**
     * 一致性组
     */
    CONSISTENT_GROUP,

    /**
     * 功耗
     */
    INPUT_POWER,

    /**
     * 转储
     */
    PERF_STAT_DUMP,

    /**
     * 枚举变量
     */
    CLIENTINFO,

    /**
     * 性能导出
     */
    EXPORT_PERFORMANCE_DATA,

    /**
     * 用户配额查询
     */
    QUERY_USER_QUATO,

    /**
     * 查询cifs在线用户
     */
    QUERY_CIFS_ONLINE_USER,

    /**
     * N8000只读用户
     */
    USER_LEVEL_READONLY,

    /**
     * 告警分级
     */
    ALARM_CONFIG_CLASSIFICATION,

    /**
     * 告警短信支持分级发送
     */
    NOTIFICATION_SMS_CLASSIFICATION,

    /*2011-10-07 l00003723 修改问题单T11V-2292 N8000V1R2不提供此功能 begin*/
    /* 自动备份 */
    /**
     * 枚举变量
     */
    AUTO_BACKUP,
    /*2011-10-07 l00003723 修改问题单T11V-2292 N8000V1R2不提供此功能 end*/
    //ckf36661 P11G-3643  性能统计新需求  begin
    /**
     * 枚举变量
     */
    PERFORMANCE_THRESHOLD,
    //ckf36661 P11G-3643  性能统计新需求  end

    //V1R6 增加多路径监控
    /**
     * 枚举变量
     */
    MULTI_PATH_MONITORING,

    //NEXT V1R6 增加支持FCoE接口卡
    /**
     * 枚举变量
     */
    SUPPORT_FCOE_CARD,
    /**
     * 枚举变量
     */
    PERFORMANCE_SPLITED_INTERVAL,

    //支持NAS一体化
    /**
     * 枚举变量
     */
    INTEGERATED_NAS,

    /**
     * 默认路由支持，（TR5的设备，支持目的地址和掩码全为0）
     */
    SUPPORT_DEFAULT_ROUTE,

    //热补丁支持
    /**
     * 枚举变量
     */
    HOT_PATCH,

    //  VIS6000T增加端口模式
    /**
     * 枚举变量
     */
    FC_PORT_MODE,

    //支持主机nas操作系统类型
    /**
     * 枚举变量
     */
    FILE_ENGINE_NODE,

    //支持导出性能统计文件时记录操作日志
    /**
     * 枚举变量
     */
    LOG_OF_DOWNLOAD_PERF_STAT_ARCHIVE,

    /**
     * 枚举变量
     */
    HOSTGROUP_PORTMODE,
    //Begin l90005692 分裂镜像一致性组特性，新增枚举值 T11V-2918
    /**
     * 枚举变量
     */
    SPLIT_MIRROR_CONSISTENTGROUP,
    //End l90005692 分裂镜像一致性组特性，新增枚举值 T11V-2918

    //新增LDAP特性
    /**
     * 枚举变量
     */
    LDAP_CONFIG,

    /**
     * 支持Syslog（n8000V2支持Syslog的tlv通道）
     */
    SYS_LOG_SUPPORT,

    //是否属于T系列归一版本
    /**
     * 枚举变量
     */
    T_SERIES_UNITARY,

    //是否支持修改THIN LUN的归属控制器
    /**
     * 枚举变量
     */
    THIN_LUN_CONTROLLER_MODIFY,

    // 是否支持动态配置SF-AGENT超时时间
    /**
     * 枚举变量
     */
    DYNAMIC_TIME_OUT_ENABLE,

    // 是否支持升级补丁前健康检查
    /**
     * 枚举变量
     */
    PATHCH_HEALTH_CHECK,

    // 是否支持显示启动器与哪个物理端口连接
    /**
     * 枚举变量
     */
    INITIALTOR_TO_PHYSIC_PORT,

    //begin h90005710 新增需求 LUN IO 优先级特性
    /**
     * 枚举变量
     */
    LUN_IO_PRIORITY,
    //end h90005710 新增需求 LUN IO 优先级特性

    //Begin xiekaiji T12T-985 N8000下挂Dorado2100的时候，主机节点下，无法查看添加的命令设备。
    // 是否支持命令设备
    /**
     * 枚举变量
     */
    COMMAND_DEVICE,
    //End xiekaiji T12T-985 N8000下挂Dorado2100的时候，主机节点下，无法查看添加的命令设备。

//  begin DTS2012101003248,DTS2012100806548  S2600TV1R5C01系列新增云备份,WIFI的功能 y00219326 2012-10-11     
    //是否支持V1R5C01的新特性，包括WIFI和云备份
    /**
     * 枚举变量
     */
    T_SERIES_V1R5C01,
//  end DTS2012101003248,DTS2012100806548  S2600TV1R5C01系列新增云备份,WIFI的功能 y00219326 2012-10-11  

    /**
     * 设备具有SVP（管理服务器）
     */
    HAS_SVP,

    /**
     * 可查询到机柜
     */
    BAY_RETRIEVEABLE,

    /**
     * 设备所在机柜上有监控板
     */
    HAS_MONITOR_BOARD,

    /**
     * 端口映射
     */
    PORT_MAPPING_VIEW,

    /**
     * 主机路径信息
     */
    HOST_LINK,
    //Begin xiekaiji workspaceMgr
    /**
     * 枚举变量
     */
    NAS_WORKSPACE_MGR,
    //End xiekaiji workspaceMgr

    //begin DTS2012111702789 2200T不支持云备份功能，界面上却有云备份结点 y00219326 2012-11-18
    //云备份
    /**
     * 枚举变量
     */
    CLOUDBACKUP,

    /**
     * 枚举变量
     */
    T_DNS_CONFIG,

    //WiFi
    /**
     * 枚举变量
     */
    WIFI,
    //end DTS2012111702789 2200T不支持云备份功能，界面上却有云备份结点 y00219326 2012-11-18

    /**
     * LUN迁移
     */
    LUN_MIGRATION,

    /**
     * NAS发现时候的TLV通道支持
     */
    NAS_DISCOVERY_TLV,

    /**
     * 支持文件系统一级子目录
     */
    FILESYSTEM_SUBDIR,

    /**
     * N8000支持发现FC交换机
     */
    FCSWITCH,

    /**
     * N8000支持发现GE交换机
     */
    GESWITCH,

    /**
     * R11C02当中文件系统创建向导当中的自动选盘功能
     */
    NASSELECTEDDISK,

    /**
     * R11C02中展示新设备图
     */
    NAS_NEW_DEVICE_VIEW,

    /**
     * R11C02版本去掉创建数据磁盘功能
     */
    NO_CREATE_LOGICDISK;


}
