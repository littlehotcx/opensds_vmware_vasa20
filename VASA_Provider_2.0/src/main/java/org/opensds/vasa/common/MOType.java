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

public enum MOType {

    /**
     * 阵列
     */
    ARRAY(2),
    /**
     * 前端控制器
     */
    FRONT_CONTROLLER_(3),
    /**
     * 前后端合一控制器
     */
    UNIFY_CONTROLLER(4),
    /**
     * 后端控制器
     */
    BACK_CONTROLLER(5),
    /**
     * 前端端口
     */
    FRONT_PORT(6),
    /**
     * 后端端口
     */
    BACK_PORT(7),
    /**
     * 卷
     */
    VOLUME(8),
    /**
     * 范围
     */
    SCOPE(9),
    /**
     * 远程镜像组
     */
    REMOTE_MIRRORGROUP(12),
    /**
     * 远程连接链路
     */
    REMOTE_LINK(13),
    /**
     * RAID
     */
    RAID(15),
    /**
     * 复制带宽
     */
    REPLICATION_BANDWIDTH(17),
    /**
     * 快照一致性组
     */
    SNAPSHOT_CONSISTENTGROUP(18),
    /**
     * 一致性卷组
     */
    VOLUME_CONSISTENTGROUP(19),
    /**
     * 磁盘组
     */
    DISKGROUP(20),
    /**
     * CPU核心
     */
    CPU_CORE(24),
    /**
     * 节点
     */
    NODE(25),
    /**
     * 风扇框
     */
    FAN_ENCLOSURE(26),
    /**
     * 远程LUN拷贝
     */
    REMOTE_LUNCOPY(29),
    /**
     * Thin pool(精简池)
     */
    THINPOOL(30),
    /**
     * 硬盘
     */
    DISK(10),
    /**
     * LUN
     */
    LUN(11),
    /**
     * 主机组
     */
    HOSTGROUP(14),
    /**
     * CPU
     */
    CPU(16),
    /**
     * 主机
     */
    HOST(21),
    /**
     * 电源模块
     */
    POWER(23),
    /**
     * 快照
     */
    SNAPSHOT(27),
    /**
     * 远程复制
     */
    REMOTEREPLICATION(28),
    /**
     * 安全策略
     */
    USER_SECURITY_POLICY(47),

    /**
     * 系统License,只用于上报
     */
    SYSTEMLICENSE(199),
    /**
     * 系统
     */
    SYSTEM(201),
    /**
     * 用户
     */
    USER(202),
    /**
     * 域
     */
    DOMAIN(203),
    /**
     * 域成员
     */
    DOMAINMEMBER(204),
    /**
     * 柜
     */
    BAY(205),
    /**
     * 框
     */
    ENCLOSURE(206),
    /**
     * 控制器
     */
    CONTROLLER(207),
    /**
     * 级联板
     */
    EXPBOARD(208),
    /**
     * 接口模块
     */
    INTF_MODULE(209),
    /**
     * 备电模块
     */
    BACKUP_POWER(210),
    /**
     * 风扇模块
     */
    FAN(211),
    /**
     * FC端口
     */
    FC_PORT(212),
    /**
     * 以太网口
     */
    ETH_PORT(213),
    /**
     * SAS端口
     */
    SAS_PORT(214),
    /**
     * 串口
     */
    SERIAL_PORT(215),
    /**
     * 存储池
     */
    STORAGEPOOL(216),
    /**
     * 存储层
     */
    STORAGETIER(217),
    /**
     * SmartCache
     */
    SMARTCACHE(218),
    /**
     * LUN拷贝
     */
    LUNCOPY(219),
    /**
     * 分裂镜像
     */
    SPLITMIRROR(220),
    /**
     * 一致性组
     */
    CONSISTENTGROUP(221),
    /**
     * iSCSI启动器
     */
    ISCSI_INITIATOR(222),
    /**
     * FC启动器
     */
    FC_INITIATOR(223),
    /**
     * IB启动器
     */
    IB_INITIATOR(16499),
    /**
     * 远端设备
     */
    REMOTE_DEVICE(224),
    /**
     * FC远端链路
     */
    FC_LINK(225),
    /**
     * 远程复制成员LUN
     */
    REMOTEREPLICATIONMEMBERLUN(226),
    /**
     * LUN拷贝成员LUN
     */
    LUNCOPYMEMBERLUN(227),
    /**
     * 分裂镜像从LUN
     */
    SPLITMIRRORTARGETLUN(228),
    /**
     * iSCSI会话
     */
    ISCSI_SESSION(229),
    /**
     * IO分类
     */
    IOCLASS(230),
    /**
     * UPS
     */
    UPS(231),
    /**
     * PCIE数据交换机
     */
    PCIE_DATA_SWITCH(232),
    /**
     * PCIE端口
     */
    PCIE_PORT(233),
    /**
     * 光模块
     */
    SFP_OPTICAL_TRANSCEIVER(234),
    /**
     * 绑定端口
     */
    BOND_PORT(235),
    /**
     * 安全规则
     */
    IPRule(236),
    /**
     * 内存
     */
    MEMORY(237),
    /**
     * ldap配置
     */
    LDAPConfig(238),
    /**
     * ldap用户映射
     */
    LDAP_USER_MAP(239),
    /**
     * SNMP Trap地址
     */
    SNMP_TRAP_ADDR(240),
    /**
     * SNMP 团体字
     */
    SNMP_COMMUNITY(241),
    /**
     * 基于用户的安全模型
     */
    SNMP_USM(242),
    /**
     * iSCSI远端链路
     */
    iSCSI_LINK(243),
    /**
     * License
     */
    License(244),
    /**
     * 映射视图
     */
    MAPPINGVIEW(245),
    /**
     * 时刻表
     */
    SCHEDULE(246),
    /** 枚举变量 */
    /**
     * 远端LUN
     */
    REMOTE_LUN(250),
    /**
     * LUN组
     */
    FCOE_PORT(252),
    /** 枚举变量 */
    /**
     * LUN组
     */
    LUN_MIGRATION(253),
    /** 枚举变量 */
    /**
     * LUN组
     */
    HOST_LINK(255),
    /** 枚举变量 */
    /**
     * LUN组
     */
    LUNGroup(256),
    /**
     * Port组
     */
    PortGroup(257),
    /**
     * LUN后台任务
     */
    LUNBACKGROUDTASK(265),
    /**
     * 硬盘池
     */
    DISKPOOL(266),
    /**
     * 存储引擎
     */
    STORAGEENGINE(267),
    /**
     * 存储分区
     */
    CACHEPARTITION(268),
    /**
     * IO分类模板
     */
    IOCLASSTEMPLATE(269),
    /**
     * LUN数据销毁
     */
    LUNDestroyData(270),
    /**
     * 硬盘数据销毁
     */
    DiskDestroyData(271),
    /**
     * LUN优先级统计
     */
    LUNPriorityStatistic(272),


    /**
     * NAS 部分的数据对象
     */
    FILESYSTEM(40), //文件系统
    /**
     * 枚举变量
     */
    FOLDER(53249),
    /**
     * 枚举变量
     */
    SHARE(53250),
    /**
     * 枚举变量
     */
    HOMEDIR(53251),
    /**
     * 枚举变量
     */
    SHAREDSERVER(53252),
    /**
     * 枚举变量
     */
    QUOTA(53254),
    /**
     * 枚举变量
     */
    FSMIGRATION(53255),
    /**
     * 枚举变量
     */
    IP_LINK(261),
    /**
     * 枚举变量
     */
    REPLICATIONPAIR(263),
    /**
     * 枚举变量
     */
    NETGROUP(259),
    /**
     * 枚举变量
     */
    SUBNET(258),
    /**
     * 枚举变量
     */
    USERGROUP(262),
    /**
     * 枚举变量
     */
    DNS_SERVER(260),
    /**
     * 枚举变量
     */
    AUTHORISATION(53256),

    //VASA 对象使用
    PROTOCOL_ENDPOINT(279),
    CAPABILITYSCHEMA(281),
    CAPABILITYMETADATA(287),
    PROPERTYMETADATA(284),
    STORAGEPROFILE(333),
    SUBPROFILE(288),
    CAPBILITYINSTANCE(289),
    PROPERTYINSTANCE(290),
    CAPABILITYMETADATACATEGORY(291),


    //ISM自己使用，接口文档中有定义，从49152开始
    /**
     * NAS节点
     */
    NAS_NODE(49153),
    /**
     * NAS框属性
     */
    NAS_CHASSIS_PROPERTY(49154),
    /**
     * 同口的业务口属性
     */
    COMPOND_PORT_FOR_ISCSI(49155),
    /**
     * SVP磁盘
     */
    DISK_SVP(49156),
    /**
     * SVP端口
     */
    ETH_PORT_SVP(49157),
    /**
     * NAS框
     */
    NAS_CHASSIS(49158),
    /**
     * NEX
     */
    NEX_UNIT_NODE(49159),
    /**
     * NEX图
     */
    NEX_FIGURE_NODE(49160),
    /**
     * NEX控制器节点
     */
    NEX_CONTROLLER_NODE(49161),
    /**
     * NEX框
     */
    NEX_CHASSIS_NODE(49162),
    /**
     * NEX接口模块
     */
    NEX_INTERFACE_MODULE_NODE(49163),
    /**
     * Nex端口
     */
    NEX_PORT_NODE(49164),
    /**
     * Nex磁盘节点
     */
    NEX_DISK_NODE(49165),
    /**
     * NAS交换节点
     */
    NAS_SWITCH_NODE(49166),

    /**
     * SNAS集群系统
     */
    SNAS_SYSTEM(16384),
    /**
     * SNAS集群节点
     */
    SNAS_NODE(16385),
    /**
     * SNAS集群客户端
     */
    SNAS_CLIENT(16390),
    /**
     * SNAS集群管理节点
     */
    SNAS_MGR_NODE(16421),
    /**
     * SNAS集群节点性能
     */
    SNAS_NODE_PERFORMANCE(16422),
    /**
     * SNAS集群节点文件系统服务
     */
    SNAS_NODE_FS_SERV(16397),
    /**
     * 公共配置
     */
    COMMCONFIGURE(16444);

    /**
     * value变量
     */
    private int value;

    /**
     * MO类型
     *
     * @param value
     */
    private MOType(int value) {
        this.value = value;
    }

    /**
     * 方法 ： getTypeName
     *
     * @param value 方法参数：value
     * @return String 返回结果
     */
    public static String getTypeName(int value) {
        MOType moType = null;
        for (MOType type : MOType.values()) {
            if (type.getValue() == value) {
                moType = type;
            }
        }
        return moType == null ? null : moType.name();
    }

    /**
     * 方法 ： getType
     *
     * @param value 方法参数：value
     * @return MOType 返回结果
     */
    public static MOType getType(int value) {
        MOType moType = null;
        for (MOType type : MOType.values()) {
            if (type.getValue() == value) {
                moType = type;
            }
        }
        return moType;
    }

    /**
     * 方法 ： getValue
     *
     * @return int 返回结果
     */
    public int getValue() {
        return value;
    }

    /**
     * <该方法返回枚举对应的描述>
     *
     * @return String 返回结果
     */
    public String getDescription() {
        return "EnumDefine."
                + this.getClass().getSimpleName() + '.' + this.name();
    }
}
