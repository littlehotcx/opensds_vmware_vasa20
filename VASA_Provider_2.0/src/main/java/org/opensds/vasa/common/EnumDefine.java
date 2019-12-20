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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 文 件 名   :EnumDefine.java
 * 版 本 号   :初稿
 * 生成日期   :2011-10-15
 * 文件描述   :枚举定义
 *
 * @author V1R10
 * @version [版本号V001R010C00, 2011-12-14]
 */
public class EnumDefine {

    private static Logger LOGGER = LogManager
            .getLogger(EnumDefine.class);

    /**
     * ASSOCIATE_TYPE_E
     */
    public static enum ASSOCIATE_TYPE_E {
        /**
         * 映射
         */
        MAPPING(0);

        private int value;

        /**
         * 构造函数
         */
        private ASSOCIATE_TYPE_E(int value) {
            this.value = value;
        }

        /**
         * 根据整形值获得枚举类型
         *
         * @param iValue 方法参数：iValue
         * @return ASSOCIATE_TYPE_E 返回结果
         */
        public static ASSOCIATE_TYPE_E valueOf(int iValue) {
            for (ASSOCIATE_TYPE_E value : ASSOCIATE_TYPE_E.values()) {
                if (value.value == iValue) {
                    return value;
                }
            }
            throw new IllegalArgumentException("wrong ASSOCIATE_TYPE_E value:"
                    + iValue);
        }

        /**
         * 取得枚举的整型值
         *
         * @return int 返回结果
         */
        public int getValue() {
            return this.value;
        }

        /**
         * <该方法返回枚举对应的描述>
         *
         * @return String 返回结果
         */
        @Override
        public String toString() {
            return "EnumDefine." + this.getClass().getSimpleName() + '.' + this.name();
        }

    }

    /**
     * 定义状态归类枚举: 主要应用于硬件组件的状态
     *
     * @author f90002221
     */
    public static enum STATE_GROUP_E {
        /**
         * 枚举变量
         */
        NORMAL,
        /**
         * 枚举变量
         */
        FAULT,
        /**
         * 枚举变量
         */
        WARNING,
        /**
         * 枚举变量
         */
        OFFLINE;
    }

    /**
     * 组件的健康状态表示：默认将健康状态分组为WARING,应为大多数状态为警告状态，减少代码的繁冗
     * HEALTH_STATUS_E
     */
    public static enum HEALTH_STATUS_E {
        /**
         * 未知
         */
        UNKNOWN(0),
        /**
         * 正常
         */
        NORMAL(1,
                /** 正常 */
                STATE_GROUP_E.NORMAL),
        /**
         * 故障
         */
        FAULT(2,
                /** 故障 */
                STATE_GROUP_E.FAULT),
        /**
         * 即将故障
         */
        PRE_FAIL(3),
        /**
         * 部分损坏
         */
        PART_BROKEN(4),
        /**
         * 降级
         */
        DEGRADE(5),
        /**
         * 有坏块
         */
        HAS_BAD_BLOCK(6),
        /**
         * 有误码
         */
        HAS_ERR_CODE(7),
        /**
         * 一致
         */
        CONSISTENT(8,
                /** 一致 */
                STATE_GROUP_E.NORMAL),
        /**
         * 不一致
         */
        INCONSISTENT(9),
        /**
         * 繁忙
         */
        BUSY(10,
                /** 繁忙 */
                STATE_GROUP_E.NORMAL),
        /**
         * 无输入
         */
        POWER_NO_INPUT(11),
        /**
         * 电量不足
         */
        POWER_NOT_ENOUGH(12);


        private STATE_GROUP_E stateGroup = STATE_GROUP_E.WARNING;


        private int value;

        /**
         * 构造函数
         */
        private HEALTH_STATUS_E(int value) {
            this.value = value;
        }

        private HEALTH_STATUS_E(int value, STATE_GROUP_E stateGroup) {
            this.value = value;
            this.stateGroup = stateGroup;
        }

        /**
         * 根据整形值获得枚举类型
         *
         * @param iValue 方法参数：iValue
         * @return HEALTH_STATUS_E 返回结果
         */
        public static HEALTH_STATUS_E valueOf(int iValue) {
            for (HEALTH_STATUS_E value : HEALTH_STATUS_E.values()) {
                if (value.value == iValue) {
                    return value;
                }
            }
            return HEALTH_STATUS_E.UNKNOWN;
        }

        /**
         * 取得枚举的整型值
         *
         * @return int 返回结果
         */
        public int getValue() {
            return this.value;
        }

        /**
         * 将当前状态分组进行归纳
         *
         * @return STATE_GROUP_E 返回结果
         */
        public STATE_GROUP_E group() {
            return this.stateGroup;
        }

        /**
         * <该方法返回枚举对应的描述>
         *
         * @return String 返回结果
         */
        @Override
        public String toString() {
            return "EnumDefine." + this.getClass().getSimpleName() + '.' + this.name();
        }

    }

    /**
     * 组件的运行状态表示：默认将运行状态分组为正常,因为为大多数状态为正常状态，减少代码的繁冗
     */
    public static enum RUNNING_STATUS_E {
        /**
         * 未知
         */
        UNKNOWN(0,
                /** 未知 */
                STATE_GROUP_E.OFFLINE),
        /**
         * 正常
         */
        NORMAL(1),
        /**
         * 工作
         */
        RUNNING(2),
        /**
         * 未工作
         */
        NOT_RUNNING(3,
                /** 未工作 */
                STATE_GROUP_E.OFFLINE),
        /**
         * 不存在
         */
        NOT_EXIST(4,
                /** 不存在 */
                STATE_GROUP_E.OFFLINE),
        /**
         * 高温休眠
         */
        HIGH_TEMPERATURE_SLEEP(5,
                /** 高温休眠 */
                STATE_GROUP_E.WARNING),

        /**
         * 启动中
         */
        STARTING(6),
        /**
         * 掉电保护
         */
        POWER_FAILURE_PROTECTING(7,
                /** 掉电保护 */
                STATE_GROUP_E.WARNING),
        /**
         * 休眠
         */
        SLEEPING(8),
        /**
         * 启动
         */
        SPINGUP(9),
        /**
         * 连接
         */
        LINK_UP(10),
        /**
         * 未连接
         */
        LINK_DOWN(11,
                /** 未连接 */
                STATE_GROUP_E.WARNING),
        /**
         * 上电中
         */
        POWER_ON(12),
        /**
         * 已下电
         */
        POWER_OFF(13,
                /** 已下电 */
                STATE_GROUP_E.WARNING),
        /**
         * 预拷贝
         */
        PRE_COPY(14),
        /**
         * 回拷
         */
        COPYBACK(15),
        /**
         * 重构
         */
        RECONSTRUCTION(16,
                /** 重构 */
                STATE_GROUP_E.WARNING),
        /**
         * 扩容
         */
        EXPANSION(17),
        /**
         * 未格式化
         */
        NOT_FORMAT(18),
        /**
         * 格式化
         */
        FORMATTING(19),
        /**
         * 未映射
         */
        UNMAPPING(20),
        /**
         * 数据初始同步中
         */
        INITIAL_SYNCHRONIZING(21),
        /**
         * 数据一致(未作最新同步)
         */
        CONSISTENT(22),
        /**
         * 同步中
         */
        SYNCHRONIZING(23),
        /**
         * 已同步
         */
        SYNCHRONIZED(24),
        /**
         * 未同步
         */
        NOT_SYNCHRONIZED(25),
        /**
         * 已分裂
         */
        SPLIT(26),
        /**
         * 在线
         */
        ONLINE(27),
        /**
         * 离线
         */
        OFFLINE(28,
                /** 离线 */
                STATE_GROUP_E.OFFLINE),
        /**
         * 锁定
         */
        LOCKED(29),
        /**
         * 已启用
         */
        ENABLE(30),
        /**
         * 已禁用
         */
        DISABLED(31),
        /**
         * 均衡
         */
        LEVELING(32),
        /**
         * 待恢复
         */
        TO_BE_RECOVERD(33),
        /**
         * 异常断开
         */
        INTERRUPTED(34,
                /** 异常断开 */
                STATE_GROUP_E.WARNING),
        /**
         * 镜像失效
         */
        INVALID(35),
        /**
         * 未开始
         */
        NOSTART(36),
        /**
         * 排队等待中
         */
        QUEUING(37),
        /**
         * 停止
         */
        STOP(38),
        /**
         * 拷贝中
         */
        COPYING(39),
        /**
         * 拷贝完成
         */
        COMPLETED(40),
        /**
         * 暂停
         */
        PAUSE(41),
        /**
         * 反向同步中
         */
        REVSYNCHRONIZING(42),
        /**
         * 激活
         */
        ACTIVATED(43),
        /**
         * 回滚中
         */
        ROLLBACK(44),
        /**
         * 未激活
         */
        INACTIVATED(45),
        /**
         * 空闲
         */
        IDLE(46),
        /**
         * 正在下电
         */
        POWERING_OFF(47),
        /**
         * 正在充电
         */
        CHARGING(48),
        /**
         * 充电完成
         */
        CHARGED(49),
        /**
         * 正在放电
         */
        DISCHARGING(50),
        /**
         * 正在升级
         */
        UPGRADING(51),
        /**
         * 正常运行
         */
        ERASEMENT_RUNNING_NORMAL(63),
        /**
         * 销毁失败
         */
        ERASEMENT_RUNNING_FAIL(64),
        /**
         * 销毁成功
         */
        ERASEMENT_RUNNING_SUCCESS(65);

        private STATE_GROUP_E stateGroup = STATE_GROUP_E.NORMAL;


        private int value;

        /**
         * 构造函数
         */
        private RUNNING_STATUS_E(int value) {
            this.value = value;
        }

        private RUNNING_STATUS_E(int value, STATE_GROUP_E stateGroup) {
            this.value = value;
            this.stateGroup = stateGroup;
        }

        /**
         * 根据整形值获得枚举类型
         *
         * @param iValue 方法参数：iValue
         * @return RUNNING_STATUS_E 返回结果
         */
        public static RUNNING_STATUS_E valueOf(int iValue) {
            for (RUNNING_STATUS_E value : RUNNING_STATUS_E.values()) {
                if (value.value == iValue) {
                    return value;
                }
            }
            //            throw new IllegalArgumentException("wrong RUNNING_STATUS_E value:"
            //                    + iValue);
            return RUNNING_STATUS_E.UNKNOWN;
        }

        /**
         * 取得枚举的整型值
         *
         * @return int 返回结果
         */
        public int getValue() {
            return this.value;
        }

        /**
         * 将当前状态分组进行归纳
         *
         * @return STATE_GROUP_E 返回结果
         */
        public STATE_GROUP_E group() {
            return this.stateGroup;
        }

        /**
         * <该方法返回枚举对应的描述>
         *
         * @return String 返回结果
         */
        @Override
        public String toString() {
            return "EnumDefine." + this.getClass().getSimpleName() + '.' + this.name();
        }
    }

    /**
     * LUN_ALLOC_TYPE_E
     */
    public static enum LUN_ALLOC_TYPE_E {
        /**
         * 普通配置
         */
        FAT(0),
        /**
         * 精减配置
         */
        THIN(1);

        private int value;

        /**
         * 构造函数
         */
        private LUN_ALLOC_TYPE_E(int value) {
            this.value = value;
        }

        /**
         * 根据整形值获得枚举类型
         *
         * @param iValue 方法参数
         * @return LUN_ALLOC_TYPE_E 返回结果
         */
        public static LUN_ALLOC_TYPE_E valueOf(int iValue) {
            for (LUN_ALLOC_TYPE_E value : LUN_ALLOC_TYPE_E.values()) {
                if (value.value == iValue) {
                    return value;
                }
            }

            throw new IllegalArgumentException("wrong LUN_ALLOC_TYPE_E value:"
                    + iValue);
        }

        /**
         * <该方法返回枚举对应的描述>
         *
         * @return String 返回结果
         */
        @Override
        public String toString() {
            return "EnumDefine." + this.getClass().getSimpleName() + '.' + this.name();
        }

        /**
         * 取得枚举的整型值
         *
         * @return int 返回结果
         */
        public int getValue() {
            return this.value;
        }

    }

    /**
     * IO_TYPE_E
     */
    public static enum IO_TYPE_E {
        /**
         * 读
         */
        READ(0),
        /**
         * 写
         */
        WRITE(1),
        /**
         * 读写
         */
        READ_WRITE(2);

        private int value;

        /**
         * 构造函数
         */
        private IO_TYPE_E(int value) {
            this.value = value;
        }

        /**
         * 根据整形值获得枚举类型
         *
         * @param iValue 方法参数：iValue
         * @return IO_TYPE_E 返回结果
         */
        public static IO_TYPE_E valueOf(int iValue) {
            for (IO_TYPE_E value : IO_TYPE_E.values()) {
                if (value.value == iValue) {
                    return value;
                }
            }
            throw new IllegalArgumentException("wrong IO_TYPE_E value:"
                    + iValue);
        }

        /**
         * 取得枚举的整型值
         *
         * @return int 返回结果
         */
        public int getValue() {
            return this.value;
        }

        /**
         * <该方法返回枚举对应的描述>
         *
         * @return String 返回结果
         */
        @Override
        public String toString() {
            return "EnumDefine." + this.getClass().getSimpleName() + '.' + this.name();
        }

    }

    /**
     * IOCLASS_SCHEDULE_POLICY_E
     */
    public static enum IOCLASS_SCHEDULE_POLICY_E {
        /**
         * 调度一次
         */
        ONCE(0),
        /**
         * 每日调度
         */
        DAILY(1),
        /**
         * 按周调度
         */
        WEEKLY(2);

        private int value;

        /**
         * 构造函数
         */
        private IOCLASS_SCHEDULE_POLICY_E(int value) {
            this.value = value;
        }

        /**
         * 根据整形值获得枚举类型
         *
         * @param iValue 方法参数：iValue
         * @return IOCLASS_SCHEDULE_POLICY_E 返回结果
         */
        public static IOCLASS_SCHEDULE_POLICY_E valueOf(int iValue) {
            for (IOCLASS_SCHEDULE_POLICY_E value : IOCLASS_SCHEDULE_POLICY_E.values()) {
                if (value.value == iValue) {
                    return value;
                }
            }
            throw new IllegalArgumentException(
                    "wrong IOCLASS_SCHEDULE_POLICY_E value:" + iValue);
        }

        /**
         * 取得枚举的整型值
         *
         * @return int 返回结果
         */
        public int getValue() {
            return this.value;
        }

        /**
         * <该方法返回枚举对应的描述>
         *
         * @return String 返回结果
         */
        @Override
        public String toString() {
            return "EnumDefine." + this.getClass().getSimpleName() + '.' + this.name();
        }

    }

    /**
     * IOCLASS_LATENCY_E
     */
    public static enum IOCLASS_LATENCY_E {
        /**
         * 正常
         */
        NORMAL(0),
        /**
         * 低时延
         */
        LOWER(1);

        private int value;

        /**
         * 构造函数
         */
        private IOCLASS_LATENCY_E(int value) {
            this.value = value;
        }

        /**
         * 根据整形值获得枚举类型
         *
         * @param iValue 方法参数：iValue
         * @return IOCLASS_LATENCY_E 返回结果
         */
        public static IOCLASS_LATENCY_E valueOf(int iValue) {
            for (IOCLASS_LATENCY_E value : IOCLASS_LATENCY_E.values()) {
                if (value.value == iValue) {
                    return value;
                }
            }
            throw new IllegalArgumentException("wrong IOCLASS_LATENCY_E value:"
                    + iValue);
        }

        /**
         * 取得枚举的整型值
         *
         * @return int 返回结果
         */
        public int getValue() {
            return this.value;
        }

        /**
         * <该方法返回枚举对应的描述>
         *
         * @return String 返回结果
         */
        @Override
        public String toString() {
            return "EnumDefine." + this.getClass().getSimpleName() + '.' + this.name();
        }

    }

    /**
     * IOCLASS_PRIORITY_E
     */
    public static enum IOCLASS_PRIORITY_E {
        /**
         * 正常
         */
        NORMAL(0),
        /**
         * 高
         */
        HIGH(1);

        private int value;

        /**
         * 构造函数
         */
        private IOCLASS_PRIORITY_E(int value) {
            this.value = value;
        }

        /**
         * 根据整形值获得枚举类型
         *
         * @param iValue 方法参数：iValue
         * @return IOCLASS_PRIORITY_E 返回结果
         */
        public static IOCLASS_PRIORITY_E valueOf(int iValue) {
            for (IOCLASS_PRIORITY_E value : IOCLASS_PRIORITY_E.values()) {
                if (value.value == iValue) {
                    return value;
                }
            }
            throw new IllegalArgumentException(
                    "wrong IOCLASS_PRIORITY_E value:" + iValue);
        }

        /**
         * 取得枚举的整型值
         *
         * @return int 返回结果
         */
        public int getValue() {
            return this.value;
        }

        /**
         * <该方法返回枚举对应的描述>
         *
         * @return String 返回结果
         */
        @Override
        public String toString() {
            return "EnumDefine." + this.getClass().getSimpleName() + '.' + this.name();
        }

    }

    /**
     * SNAP_BELONG_E
     */
    public static enum SNAP_BELONG_E {
        /**
         * 公有快照
         */
        PUBLIC(1),
        /**
         * 私有快照
         */
        PRIVATE(2);

        private int value;

        /**
         * 构造函数
         */
        private SNAP_BELONG_E(int value) {
            this.value = value;
        }

        /**
         * 根据整形值获得枚举类型
         *
         * @param iValue 方法参数：iValue
         * @return SNAP_BELONG_E 返回结果
         */
        public static SNAP_BELONG_E valueOf(int iValue) {
            for (SNAP_BELONG_E value : SNAP_BELONG_E.values()) {
                if (value.value == iValue) {
                    return value;
                }
            }
            throw new IllegalArgumentException("wrong SNAP_BELONG_E value:"
                    + iValue);
        }

        /**
         * 取得枚举的整型值
         *
         * @return int 返回结果
         */
        public int getValue() {
            return this.value;
        }

        /**
         * <该方法返回枚举对应的描述>
         *
         * @return String 返回结果
         */
        @Override
        public String toString() {
            return "EnumDefine." + this.getClass().getSimpleName() + '.' + this.name();
        }

    }

    /**
     * SNAP_SPEED_E
     */
    public static enum SNAP_SPEED_E {
        /**
         * 低
         */
        SPEED_LEVEL_LOW(1),
        /**
         * 中等
         */
        SPEED_LEVEL_MIDDLE(2),
        /**
         * 高
         */
        SPEED_LEVEL_HIGH(3),
        /**
         * 最高
         */
        SPEED_LEVEL_ASAP(4);

        private int value;

        /**
         * 构造函数
         */
        private SNAP_SPEED_E(int value) {
            this.value = value;
        }

        /**
         * 根据整形值获得枚举类型
         *
         * @param iValue 方法参数：iValue
         * @return SNAP_SPEED_E 返回结果
         */
        public static SNAP_SPEED_E valueOf(int iValue) {
            for (SNAP_SPEED_E value : SNAP_SPEED_E.values()) {
                if (value.value == iValue) {
                    return value;
                }
            }
            throw new IllegalArgumentException("wrong SNAP_SPEED_E value:"
                    + iValue);
        }

        /**
         * 取得枚举的整型值
         *
         * @return int 返回结果
         */
        public int getValue() {
            return this.value;
        }

        /**
         * <该方法返回枚举对应的描述>
         *
         * @return String 返回结果
         */
        @Override
        public String toString() {
            return "EnumDefine." + this.getClass().getSimpleName() + '.' + this.name();
        }

    }

    /**
     * ENCLOSURE_MODEL_E
     */
    public static enum ENCLOSURE_MODEL_E {
        /**
         * BMC控制框
         */
        CTRL_BMC(0, 4),
        /**
         * 2U SAS 12盘主控框
         */
        CTRL_SAS2U_12(1, 2),
        /**
         * 2U SAS 24盘主控框
         */
        CTRL_SAS2U_24(2, 2),
        /**
         * 2U SAS 12盘级联框
         */
        EXPSAS2U_12(16, 2),
        /**
         * 2U SAS 24盘级联框
         */
        EXPSAS2U_24(17, 2),
        /**
         * 4U SAS 24盘级联框
         */
        EXPSAS4U(18, 4),
        /**
         * 4U FC 24盘级联框
         */
        EXPFC(19, 4),
        /**
         * 4U SAS 75盘高密框
         */
        HDEXPSAS4U(21, 4),
        /**
         * SVP(T3000)
         */
        SVP_T3000(22, 2),

        //以下是自己定义的枚举值，方便绘图，不在接口文档中体现
        /**
         * KVM
         */
        KVM(100, 1),
        /**
         * SVP(T3000)
         */
        SVP(101, 2);


        private int height;


        private int value;

        /**
         * 构造函数
         */
        private ENCLOSURE_MODEL_E(int value, int height) {
            this.value = value;
            this.height = height;
        }

        /**
         * 根据整形值获得枚举类型
         *
         * @param iValue 方法参数：iValue
         * @return ENCLOSURE_MODEL_E 返回结果
         */
        public static ENCLOSURE_MODEL_E valueOf(int iValue) {
            for (ENCLOSURE_MODEL_E value : ENCLOSURE_MODEL_E.values()) {
                if (value.value == iValue) {
                    return value;
                }
            }
            throw new IllegalArgumentException("wrong ENCLOSURE_MODEL_E value:"
                    + iValue);
        }

        /**
         * 取得枚举的整型值
         *
         * @return int 返回结果
         */
        public int getValue() {
            return this.value;
        }

        /**
         * 获取每种类型对应框的高度
         *
         * @return int 返回结果
         */
        public int getHeight() {
            return this.height;
        }

        /**
         * <该方法返回枚举对应的描述>
         *
         * @return String 返回结果
         */
        @Override
        public String toString() {
            return "EnumDefine." + this.getClass().getSimpleName() + '.' + this.name();
        }

    }

    /**
     * ENCLOSURE_TYPE_E
     */
    public static enum ENCLOSURE_TYPE_E {
        /**
         * 级联框（硬盘框）
         */
        EXP(0),
        /**
         * 控制框
         */
        CTRL(1),
        /**
         * 数据交换机
         */
        DSW(2),
        /**
         * 管理交换机
         */
        MSW(3),
        /**
         * SVP(T3000)
         */
        SVP(4),

        //以下是自己定义的枚举值，方便绘图，不在接口文档中体现
        /**
         * KVM
         */
        KVM(100);

        private int value;

        /**
         * 构造函数
         */
        private ENCLOSURE_TYPE_E(int value) {
            this.value = value;
        }

        /**
         * 根据整形值获得枚举类型
         *
         * @param iValue 方法参数：iValue
         * @return ENCLOSURE_TYPE_E 返回结果
         */
        public static ENCLOSURE_TYPE_E valueOf(int iValue) {
            for (ENCLOSURE_TYPE_E value : ENCLOSURE_TYPE_E.values()) {
                if (value.value == iValue) {
                    return value;
                }
            }
            throw new IllegalArgumentException("wrong ENCLOSURE_TYPE_E value:"
                    + iValue);
        }

        /**
         * 取得枚举的整型值
         *
         * @return int 返回结果
         */
        public int getValue() {
            return this.value;
        }

        /**
         * <该方法返回枚举对应的描述>
         *
         * @return String 返回结果
         */
        @Override
        public String toString() {
            return "EnumDefine." + this.getClass().getSimpleName() + '.' + this.name();
        }

    }

    /**
     * INTF_MODEL_E
     */
    public static enum INTF_MODEL_E {
        /**
         * 4x4G FC接口模块
         */
        FC_4X4G(1),
        /**
         * 2x4G FC接口模块
         */
        FC_2X4G(2),
        /**
         * 2xGE 电接口模块
         */
        GE_RJ45_2X(3),
        /**
         * 4xSAS I 接口模块
         */
        SASI_4X(4),
        /**
         * Swapped FC接口模块
         */
        FC_4X4G_EXPAND(5),
        /**
         * 2x10GE 光接口模块
         */
        ETH10G_2(6),
        /**
         * 1x8G FC光接口模块
         */
        FC_1X8G(7),
        /**
         * 2x8G FC光接口模块
         */
        FC_2X8G(8),
        /**
         * 2xSAS II电接口模块
         */
        SASII_2X(9),
        /**
         * 4xSAS II电接口模块
         */
        SASII_4X(10),
        /**
         * 2xFC+2xGE 接口模块
         */
        Combo_2FCx2GE(11),
        /**
         * 4xGE 电接口模块
         */
        iSCSI_4X(12),
        /**
         * 4x8G FC光接口模块
         */
        FC_4X8G(13),
        /**
         * 4xmini-SAS接口模块
         */
        MINISAS_4X(16),
        /**
         * 4x10G FCoE光接口模块
         */
        FCoE_4X(21),
        /**
         * 2x6G SASA接口模块
         */
        SASA_2X6G(22),
        /**
         * 2x6G SASB接口模块
         */
        SASB_2X6G(23),
        /**
         * 管理板
         */
        ManagerBoard(24),
        /**
         * 4x10GE接口模块
         */
        TOE(25),
        /**
         * 2x10G FCoE光接口模块
         */
        FCoE_2X(27);

        private int value;

        /**
         * 构造函数
         */
        private INTF_MODEL_E(int value) {
            this.value = value;
        }

        /**
         * 根据整形值获得枚举类型
         *
         * @param iValue 方法参数：iValue
         * @return INTF_MODEL_E 返回结果
         */
        public static INTF_MODEL_E valueOf(int iValue) {
            for (INTF_MODEL_E value : INTF_MODEL_E.values()) {
                if (value.value == iValue) {
                    return value;
                }
            }
            throw new IllegalArgumentException("wrong INTF_MODEL_E value:"
                    + iValue);
        }

        /**
         * 取得枚举的整型值
         *
         * @return int 返回结果
         */
        public int getValue() {
            return this.value;
        }

        /**
         * <该方法返回枚举对应的描述>
         *
         * @return String 返回结果
         */
        @Override
        public String toString() {
            return "EnumDefine." + this.getClass().getSimpleName() + '.' + this.name();
        }

    }

    /**
     * INI_OR_TGT_E
     */
    public static enum INI_OR_TGT_E {
        /**
         * 启动器
         */
        INI(2),
        /**
         * 目标器
         */
        TGT(3),
        /**
         * 启动器和目标器
         */
        INI_AND_TGT(4);

        private int value;

        /**
         * 构造函数
         */
        private INI_OR_TGT_E(int value) {
            this.value = value;
        }

        /**
         * 根据整形值获得枚举类型
         *
         * @param iValue 方法参数：iValue
         * @return INI_OR_TGT_E 返回结果
         */
        public static INI_OR_TGT_E valueOf(int iValue) {
            for (INI_OR_TGT_E value : INI_OR_TGT_E.values()) {
                if (value.value == iValue) {
                    return value;
                }
            }
            throw new IllegalArgumentException("wrong INI_OR_TGT_E value:"
                    + iValue);
        }

        /**
         * 取得枚举的整型值
         *
         * @return int 返回结果
         */
        public int getValue() {
            return this.value;
        }

        /**
         * <该方法返回枚举对应的描述>
         *
         * @return String 返回结果
         */
        @Override
        public String toString() {
            return "EnumDefine." + this.getClass().getSimpleName() + '.' + this.name();
        }

    }

    /**
     * DISK_TYPE_E
     */
    public static enum DISK_TYPE_E {
        /**
         * FC
         */
        FC(0),
        /**
         * SAS
         */
        SAS(1),
        /**
         * SATA
         */
        SATA(2),
        /**
         * SSD
         */
        SSD(3),
        /**
         * NL_SAS
         */
        NL_SAS(4),
        /**
         * SLC SSD
         */
        SLC_SSD(5),
        /**
         * MLC SSD
         */
        MLC_SSD(6);

        private int value;

        /**
         * 构造函数
         */
        private DISK_TYPE_E(int value) {
            this.value = value;
        }

        /**
         * 根据整形值获得枚举类型
         *
         * @param iValue 方法参数：iValue
         * @return DISK_TYPE_E 返回结果
         */
        public static DISK_TYPE_E valueOf(int iValue) {
            for (DISK_TYPE_E value : DISK_TYPE_E.values()) {
                if (value.value == iValue) {
                    return value;
                }
            }
            throw new IllegalArgumentException("wrong DISK_TYPE_E value:"
                    + iValue);
        }

        /**
         * 取得枚举的整型值
         *
         * @return int 返回结果
         */
        public int getValue() {
            return this.value;
        }

        /**
         * <该方法返回枚举对应的描述>
         *
         * @return String 返回结果
         */
        @Override
        public String toString() {
            return "EnumDefine." + this.getClass().getSimpleName() + '.' + this.name();
        }

    }

    /**
     * DISK_LIGHT_STATUS_E
     */
    public static enum DISK_LIGHT_STATUS_E {

        /**
         * 关
         */
        OFF(0),
        /**
         * 开
         */
        ON(1);

        private int value;

        /**
         * 构造函数
         */
        private DISK_LIGHT_STATUS_E(int value) {
            this.value = value;
        }

        /**
         * 根据整形值获得枚举类型
         *
         * @param iValue 方法参数：iValue
         * @return DISK_LIGHT_STATUS_E 返回结果
         */
        public static DISK_LIGHT_STATUS_E valueOf(int iValue) {
            for (DISK_LIGHT_STATUS_E value : DISK_LIGHT_STATUS_E.values()) {
                if (value.value == iValue) {
                    return value;
                }
            }
            throw new IllegalArgumentException(
                    "wrong DISK_LIGHT_STATUS_E value:" + iValue);
        }

        /**
         * 取得枚举的整型值
         *
         * @return int 返回结果
         */
        public int getValue() {
            return this.value;
        }

        /**
         * <该方法返回枚举对应的描述>
         *
         * @return String 返回结果
         */
        @Override
        public String toString() {
            return "EnumDefine." + this.getClass().getSimpleName() + '.' + this.name();
        }

    }

    /**
     * DISK_LOGIC_TYPE_E
     */
    public static enum DISK_LOGIC_TYPE_E {
        /**
         * 空闲盘
         */
        FREE(1),
        /**
         * 成员盘
         */
        MEMBER(2),
        /**
         * 热备
         */
        SPARE(3);

        private int value;

        /**
         * 构造函数
         */
        private DISK_LOGIC_TYPE_E(int value) {
            this.value = value;
        }

        /**
         * 根据整形值获得枚举类型
         *
         * @param iValue 方法参数：iValue
         * @return DISK_LOGIC_TYPE_E 返回结果
         */
        public static DISK_LOGIC_TYPE_E valueOf(int iValue) {
            for (DISK_LOGIC_TYPE_E value : DISK_LOGIC_TYPE_E.values()) {
                if (value.value == iValue) {
                    return value;
                }
            }
            throw new IllegalArgumentException("wrong DISK_LOGIC_TYPE_E value:"
                    + iValue);
        }

        /**
         * 取得枚举的整型值
         *
         * @return int 返回结果
         */
        public int getValue() {
            return this.value;
        }

        /**
         * <该方法返回枚举对应的描述>
         *
         * @return String 返回结果
         */
        @Override
        public String toString() {
            return "EnumDefine." + this.getClass().getSimpleName() + '.' + this.name();
        }

    }

    /**
     * POWER_TYPE_E
     */
    public static enum POWER_TYPE_E {
        /**
         * 未知
         */
        UNKNOWN(-1),
        /**
         * 直流
         */
        DC(0),
        /**
         * 交流
         */
        AC(1);

        private int value;

        /**
         * 构造函数
         */
        private POWER_TYPE_E(int value) {
            this.value = value;
        }

        /**
         * 根据整形值获得枚举类型
         *
         * @param iValue 方法参数：iValue
         * @return POWER_TYPE_E 返回结果
         */
        public static POWER_TYPE_E valueOf(int iValue) {
            for (POWER_TYPE_E value : POWER_TYPE_E.values()) {
                if (value.value == iValue) {
                    return value;
                }
            }

            LOGGER.error("wrong POWER_TYPE_E value:" + iValue);
            return UNKNOWN;
        }

        /**
         * 取得枚举的整型值
         *
         * @return int 返回结果
         */
        public int getValue() {
            return this.value;
        }

        /**
         * <该方法返回枚举对应的描述>
         *
         * @return String 返回结果
         */
        @Override
        public String toString() {
            return "EnumDefine." + this.getClass().getSimpleName() + '.' + this.name();
        }

    }

    /**
     * BACKUP_POWER_TYPE_E
     */
    public static enum BACKUP_POWER_TYPE_E {
        /**
         * BBU
         */
        BBU(0);

        private int value;

        /**
         * 构造函数
         */
        private BACKUP_POWER_TYPE_E(int value) {
            this.value = value;
        }

        /**
         * 根据整形值获得枚举类型
         *
         * @param iValue 方法参数：iValue
         * @return BACKUP_POWER_TYPE_E 返回结果
         */
        public static BACKUP_POWER_TYPE_E valueOf(int iValue) {
            for (BACKUP_POWER_TYPE_E value : BACKUP_POWER_TYPE_E.values()) {
                if (value.value == iValue) {
                    return value;
                }
            }
            throw new IllegalArgumentException(
                    "wrong BACKUP_POWER_TYPE_E value:" + iValue);
        }

        /**
         * 取得枚举的整型值
         *
         * @return int 返回结果
         */
        public int getValue() {
            return this.value;
        }

        /**
         * <该方法返回枚举对应的描述>
         *
         * @return String 返回结果
         */
        @Override
        public String toString() {
            return "EnumDefine." + this.getClass().getSimpleName() + '.' + this.name();
        }

    }

    /**
     * PORT_LOGIC_TYPE_E 网口逻辑类型
     */
    public static enum PORT_LOGIC_TYPE_E {
        /**
         * 主机端口-- ISC
         */
        HOST(0),

        /**
         * 级联端口
         */
        EXP(1),

        /**
         * 管理端口
         */
        MNGT(2),

        /**
         * 内部端口
         */
        INNER(3),

        /**
         * 维护端口
         */
        MAINTENANCE(4),

        /**
         * 管理业务口
         */
        MNGT_SRV(5),

        /**
         * 维护业务口
         */
        MAINTENANCE_SRV(6);

        private int value;

        /**
         * 构造函数
         */
        private PORT_LOGIC_TYPE_E(int value) {
            this.value = value;
        }

        /**
         * 根据整形值获得枚举类型
         *
         * @param iValue 方法参数：iValue
         * @return PORT_LOGIC_TYPE_E 返回结果
         */
        public static PORT_LOGIC_TYPE_E valueOf(int iValue) {
            for (PORT_LOGIC_TYPE_E value : PORT_LOGIC_TYPE_E.values()) {
                if (value.value == iValue) {
                    return value;
                }
            }
            throw new IllegalArgumentException("wrong PORT_LOGIC_TYPE_E value:"
                    + iValue);
        }

        /**
         * 取得枚举的整型值
         *
         * @return int 返回结果
         */
        public int getValue() {
            return this.value;
        }

        /**
         * <该方法返回枚举对应的描述>
         *
         * @return String 返回结果
         */
        @Override
        public String toString() {
            return "EnumDefine." + this.getClass().getSimpleName() + '.' + this.name();
        }

    }

    /**
     * PORT_SFP_STATUS_E
     */
    public static enum PORT_SFP_STATUS_E {
        /**
         * 端口没有光模块
         */
        NO_NEED(0),
        /**
         * 离线
         */
        OFFLINE(1),
        /**
         * 在线
         */
        ONLINE(2);

        private int value;

        /**
         * 构造函数
         */
        private PORT_SFP_STATUS_E(int value) {
            this.value = value;
        }

        /**
         * 根据整形值获得枚举类型
         *
         * @param iValue 方法参数：iValue
         * @return PORT_SFP_STATUS_E 返回结果
         */
        public static PORT_SFP_STATUS_E valueOf(int iValue) {
            for (PORT_SFP_STATUS_E value : PORT_SFP_STATUS_E.values()) {
                if (value.value == iValue) {
                    return value;
                }
            }
            throw new IllegalArgumentException("wrong PORT_SFP_STATUS_E value:"
                    + iValue);
        }

        /**
         * 取得枚举的整型值
         *
         * @return int 返回结果
         */
        public int getValue() {
            return this.value;
        }

        /**
         * <该方法返回枚举对应的描述>
         *
         * @return String 返回结果
         */
        @Override
        public String toString() {
            return "EnumDefine." + this.getClass().getSimpleName() + '.' + this.name();
        }

    }

    /**
     * FC_PORT_MODE_E
     */
    public static enum FC_PORT_MODE_E {
        /**
         * 未知
         */
        UNKNOWN(-1),
        /**
         * Fabric
         */
        FABRIC(0),
        /**
         * FC-AL
         */
        LOOP(1),
        /**
         * P2P
         */
        POINT2POINT(2),
        /**
         * AUTO
         */
        AUTO(3);

        private int value;

        /**
         * 构造函数
         */
        private FC_PORT_MODE_E(int value) {
            this.value = value;
        }

        /**
         * 根据整形值获得枚举类型
         *
         * @param iValue 方法参数：iValue
         * @return FC_PORT_MODE_E 返回结果
         */
        public static FC_PORT_MODE_E valueOf(int iValue) {
            for (FC_PORT_MODE_E value : FC_PORT_MODE_E.values()) {
                if (value.value == iValue) {
                    return value;
                }
            }
            throw new IllegalArgumentException("wrong FC_PORT_MODE_E value:"
                    + iValue);
        }

        /**
         * 取得枚举的整型值
         *
         * @return int 返回结果
         */
        public int getValue() {
            return this.value;
        }

        /**
         * <该方法返回枚举对应的描述>
         *
         * @return String 返回结果
         */
        @Override
        public String toString() {
            return "EnumDefine." + this.getClass().getSimpleName() + '.' + this.name();
        }

    }

    /**
     * ETH_PORT_DUPLEX_E
     */
    public static enum ETH_PORT_DUPLEX_E {
        /**
         * 半双工
         */
        HALF_DUPLEX(1),
        /**
         * 全双工
         */
        DUPLEX(2),
        /**
         * 自协商
         */
        AUTO(3);

        private int value;

        /**
         * 构造函数
         */
        private ETH_PORT_DUPLEX_E(int value) {
            this.value = value;
        }

        /**
         * 根据整形值获得枚举类型
         *
         * @param iValue 方法参数：iValue
         * @return ETH_PORT_DUPLEX_E 返回结果
         */
        public static ETH_PORT_DUPLEX_E valueOf(int iValue) {
            for (ETH_PORT_DUPLEX_E value : ETH_PORT_DUPLEX_E.values()) {
                if (value.value == iValue) {
                    return value;
                }
            }
            throw new IllegalArgumentException("wrong ETH_PORT_DUPLEX_E value:"
                    + iValue);
        }

        /**
         * 取得枚举的整型值
         *
         * @return int 返回结果
         */
        public int getValue() {
            return this.value;
        }

        /**
         * <该方法返回枚举对应的描述>
         *
         * @return String 返回结果
         */
        @Override
        public String toString() {
            return "EnumDefine." + this.getClass().getSimpleName() + '.' + this.name();
        }

    }

    /**
     * FAN_LEVEL_E
     */
    public static enum FAN_LEVEL_E {
        /** 枚举变量 */
        /**
         * 低
         */
        UNKNOWN(-1),
        /** 枚举变量 */
        /**
         * 低
         */
        LOW(0),
        /**
         * 正常
         */
        NORMAL(1),
        /**
         * 高
         */
        HIGH(2);

        private int value;

        /**
         * 构造函数
         */
        private FAN_LEVEL_E(int value) {
            this.value = value;
        }

        /**
         * 根据整形值获得枚举类型
         *
         * @param iValue 方法参数：iValue
         * @return FAN_LEVEL_E 返回结果
         */
        public static FAN_LEVEL_E valueOf(int iValue) {
            for (FAN_LEVEL_E value : FAN_LEVEL_E.values()) {
                if (value.value == iValue) {
                    return value;
                }
            }

            return UNKNOWN;
            //            throw new IllegalArgumentException("wrong FAN_LEVEL_E value:"
            //                    + iValue);
        }

        /**
         * 取得枚举的整型值
         *
         * @return int 返回结果
         */
        public int getValue() {
            return this.value;
        }

        /**
         * <该方法返回枚举对应的描述>
         *
         * @return String 返回结果
         */
        @Override
        public String toString() {
            return "EnumDefine." + this.getClass().getSimpleName() + '.' + this.name();
        }

    }

    /**
     * USER_LEVEL_E
     */
    public static enum USER_LEVEL_E {
        /**
         * 超级管理员级别
         */
        SUPER_ADMIN(1),
        /**
         * 管理员级别
         */
        ADMIN(2),
        /**
         * 只读用户级别
         */
        READONLY(3);

        private int value;

        /**
         * 构造函数
         */
        private USER_LEVEL_E(int value) {
            this.value = value;
        }

        /**
         * 根据整形值获得枚举类型
         *
         * @param iValue 方法参数：iValue
         * @return USER_LEVEL_E 返回结果
         */
        public static USER_LEVEL_E valueOf(int iValue) {
            for (USER_LEVEL_E value : USER_LEVEL_E.values()) {
                if (value.value == iValue) {
                    return value;
                }
            }
            throw new IllegalArgumentException("wrong USER_LEVEL_E value:"
                    + iValue);
        }

        /**
         * 取得枚举的整型值
         *
         * @return int 返回结果
         */
        public int getValue() {
            return this.value;
        }

        /**
         * <该方法返回枚举对应的描述>
         *
         * @return String 返回结果
         */
        @Override
        public String toString() {
            return "EnumDefine." + this.getClass().getSimpleName() + '.' + this.name();
        }

    }

    /**
     * USER_STATE_E
     */
    public static enum USER_STATE_E {
        /**
         * 不在线
         */
        OFF_LINE(0),
        /**
         * 在线
         */
        ON_LINE(1),
        /**
         * 用户被锁定
         */
        LOCKED(2),
        /**
         * 用户被锁定
         */
        PWD_EXPIRED(3),
        /**
         * 用户被锁定
         */
        PWD_INITIAL(4),
        /**
         * 用户被锁定
         */
        PWD_EXPIRING(5);


        private int value;

        /**
         * 构造函数
         */
        private USER_STATE_E(int value) {
            this.value = value;
        }

        /**
         * 根据整形值获得枚举类型
         *
         * @param iValue 方法参数：iValue
         * @return USER_STATE_E 返回结果
         */
        public static USER_STATE_E valueOf(int iValue) {
            for (USER_STATE_E value : USER_STATE_E.values()) {
                if (value.value == iValue) {
                    return value;
                }
            }
            throw new IllegalArgumentException("wrong USER_STATE_E value:"
                    + iValue);
        }

        /**
         * 取得枚举的整型值
         *
         * @return int 返回结果
         */
        public int getValue() {
            return this.value;
        }

        /**
         * <该方法返回枚举对应的描述>
         *
         * @return String 返回结果
         */
        @Override
        public String toString() {
            return "EnumDefine." + this.getClass().getSimpleName() + '.' + this.name();
        }

    }

    /**
     * USER_SCOPE_E
     */
    public static enum USER_SCOPE_E {
        /**
         * 本地用户
         */
        LOCAL(0),
        /**
         * LDAP用户
         */
        LDAP(1),
        /**
         * LDAP用户组
         */
        LDAPGROUP(2);

        private int value;

        /**
         * 构造函数
         */
        private USER_SCOPE_E(int value) {
            this.value = value;
        }

        /**
         * 根据整形值获得枚举类型
         *
         * @param iValue 方法参数：iValue
         * @return USER_SCOPE_E 返回结果
         */
        public static USER_SCOPE_E valueOf(int iValue) {
            for (USER_SCOPE_E value : USER_SCOPE_E.values()) {
                if (value.value == iValue) {
                    return value;
                }
            }
            throw new IllegalArgumentException("wrong USER_SCOPE_E value:"
                    + iValue);
        }

        /**
         * 取得枚举的整型值
         *
         * @return int 返回结果
         */
        public int getValue() {
            return this.value;
        }

        /**
         * <该方法返回枚举对应的描述>
         *
         * @return String 返回结果
         */
        @Override
        public String toString() {
            return "EnumDefine." + this.getClass().getSimpleName() + '.' + this.name();
        }

    }

    /**
     * DOMAIN_AUTH_SERVICE_TYPE_E
     */
    public static enum DOMAIN_AUTH_SERVICE_TYPE_E {
        /**
         * ldap验证
         */
        LDAP(0);

        private int value;

        /**
         * 构造函数
         */
        private DOMAIN_AUTH_SERVICE_TYPE_E(int value) {
            this.value = value;
        }

        /**
         * 根据整形值获得枚举类型
         *
         * @param iValue 方法参数：iValue
         * @return DOMAIN_AUTH_SERVICE_TYPE_E 返回结果
         */
        public static DOMAIN_AUTH_SERVICE_TYPE_E valueOf(int iValue) {
            for (DOMAIN_AUTH_SERVICE_TYPE_E value : DOMAIN_AUTH_SERVICE_TYPE_E.values()) {
                if (value.value == iValue) {
                    return value;
                }
            }
            throw new IllegalArgumentException(
                    "wrong DOMAIN_AUTH_SERVICE_TYPE_E value:" + iValue);
        }

        /**
         * 取得枚举的整型值
         *
         * @return int 返回结果
         */
        public int getValue() {
            return this.value;
        }

        /**
         * <该方法返回枚举对应的描述>
         *
         * @return String 返回结果
         */
        @Override
        public String toString() {
            return "EnumDefine." + this.getClass().getSimpleName() + '.' + this.name();
        }

    }

    /**
     * SPEED_LEVEL_3_E
     */
    public static enum SPEED_LEVEL_3_E {
        /**
         * 低
         */
        LOW(1),
        /**
         * 中等
         */
        MEDIUM(2),
        /**
         * 高
         */
        HIGH(3);

        private int value;

        /**
         * 构造函数
         */
        private SPEED_LEVEL_3_E(int value) {
            this.value = value;
        }

        /**
         * 根据整形值获得枚举类型
         *
         * @param iValue 方法参数：iValue
         * @return SPEED_LEVEL_3_E 返回结果
         */
        public static SPEED_LEVEL_3_E valueOf(int iValue) {
            for (SPEED_LEVEL_3_E value : SPEED_LEVEL_3_E.values()) {
                if (value.value == iValue) {
                    return value;
                }
            }
            throw new IllegalArgumentException("wrong SPEED_LEVEL_3_E value:"
                    + iValue);
        }

        /**
         * 取得枚举的整型值
         *
         * @return int 返回结果
         */
        public int getValue() {
            return this.value;
        }

        /**
         * <该方法返回枚举对应的描述>
         *
         * @return String 返回结果
         */
        @Override
        public String toString() {
            return "EnumDefine." + this.getClass().getSimpleName() + '.' + this.name();
        }

    }

    /**
     * DST_RUNNING_STATUS_E
     */
    public static enum DST_RUNNING_STATUS_E {
        /**
         * 未知
         */
        UNKNOWN(-1),
        /**
         * 准备
         */
        READY(1),
        /**
         * 迁移
         */
        MIGRATING(2),
        /**
         * 暂停
         */
        PAUSED(3);

        private int value;

        /**
         * 构造函数
         */
        private DST_RUNNING_STATUS_E(int value) {
            this.value = value;
        }

        /**
         * 根据整形值获得枚举类型
         *
         * @param iValue 方法参数：iValue
         * @return DST_RUNNING_STATUS_E 返回结果
         */
        public static DST_RUNNING_STATUS_E valueOf(int iValue) {
            for (DST_RUNNING_STATUS_E value : DST_RUNNING_STATUS_E.values()) {
                if (value.value == iValue) {
                    return value;
                }
            }

            return UNKNOWN;
            //            throw new IllegalArgumentException(
            //                    "wrong DST_RUNNING_STATUS_E value:" + iValue);
        }

        /**
         * 取得枚举的整型值
         *
         * @return int 返回结果
         */
        public int getValue() {
            return this.value;
        }

        /**
         * <该方法返回枚举对应的描述>
         *
         * @return String 返回结果
         */
        @Override
        public String toString() {
            return "EnumDefine." + this.getClass().getSimpleName() + '.' + this.name();
        }

    }

    /**
     * PREDICTION_SWITCH_E
     */
    public static enum PREDICTION_SWITCH_E {
        /**
         * 开
         */
        ON(1),
        /**
         * 关
         */
        OFF(2);

        private int value;

        /**
         * 构造函数
         */
        private PREDICTION_SWITCH_E(int value) {
            this.value = value;
        }

        /**
         * 根据整形值获得枚举类型
         *
         * @param iValue 方法参数：iValue
         * @return PREDICTION_SWITCH_E 返回结果
         */
        public static PREDICTION_SWITCH_E valueOf(int iValue) {
            for (PREDICTION_SWITCH_E value : PREDICTION_SWITCH_E.values()) {
                if (value.value == iValue) {
                    return value;
                }
            }
            throw new IllegalArgumentException(
                    "wrong PREDICTION_SWITCH_E value:" + iValue);
        }

        /**
         * 取得枚举的整型值
         *
         * @return int 返回结果
         */
        public int getValue() {
            return this.value;
        }

        /**
         * <该方法返回枚举对应的描述>
         *
         * @return String 返回结果
         */
        @Override
        public String toString() {
            return "EnumDefine." + this.getClass().getSimpleName() + '.' + this.name();
        }

    }

    /**
     * MIGRATION_MODE_E
     */
    public static enum MIGRATION_MODE_E {
        /**
         * 自动
         */
        AUTO(1),
        /**
         * 手动
         */
        MANUAL(2);

        private int value;

        /**
         * 构造函数
         */
        private MIGRATION_MODE_E(int value) {
            this.value = value;
        }

        /**
         * 根据整形值获得枚举类型
         *
         * @param iValue 方法参数：iValue
         * @return MIGRATION_MODE_E 返回结果
         */
        public static MIGRATION_MODE_E valueOf(int iValue) {
            for (MIGRATION_MODE_E value : MIGRATION_MODE_E.values()) {
                if (value.value == iValue) {
                    return value;
                }
            }
            throw new IllegalArgumentException("wrong MIGRATION_MODE_E value:"
                    + iValue);
        }

        /**
         * 取得枚举的整型值
         *
         * @return int 返回结果
         */
        public int getValue() {
            return this.value;
        }

        /**
         * <该方法返回枚举对应的描述>
         *
         * @return String 返回结果
         */
        @Override
        public String toString() {
            return "EnumDefine." + this.getClass().getSimpleName() + '.' + this.name();
        }

    }

    /**
     * HOTSPARE_SPACE_STRATEGY_E
     */
    public static enum HOTSPARE_SPACE_STRATEGY_E {
        /**
         * 低
         */
        LOW(1),
        /**
         * 高
         */
        HIGH(2),
        /**
         * 无
         */
        NONE(3);

        private int value;

        /**
         * 构造函数
         */
        private HOTSPARE_SPACE_STRATEGY_E(int value) {
            this.value = value;
        }

        /**
         * 根据整形值获得枚举类型
         *
         * @param iValue 方法参数：iValue
         * @return HOTSPARE_SPACE_STRATEGY_E 返回结果
         */
        public static HOTSPARE_SPACE_STRATEGY_E valueOf(int iValue) {
            for (HOTSPARE_SPACE_STRATEGY_E value : HOTSPARE_SPACE_STRATEGY_E.values()) {
                if (value.value == iValue) {
                    return value;
                }
            }
            throw new IllegalArgumentException(
                    "wrong HOTSPARE_SPACE_STRATEGY_E value:" + iValue);
        }

        /**
         * 取得枚举的整型值
         *
         * @return int 返回结果
         */
        public int getValue() {
            return this.value;
        }

        /**
         * 枚举对象顺序的比较方法
         *
         * @param oldValue 比较对象
         * @return int 返回结果
         */
        public int compareEnum(HOTSPARE_SPACE_STRATEGY_E oldValue) {
            int newValueNum = 0;
            int oldValueNum = 0;
            switch (this.value) {
                case 1:
                    newValueNum = 2;
                    break;
                case MagicNumber.INT2:
                    newValueNum = 3;
                    break;
                case MagicNumber.INT3:
                    newValueNum = 1;
                    break;
                default:
                    break;
            }
            switch (oldValue.value) {
                case 1:
                    oldValueNum = 2;
                    break;
                case MagicNumber.INT2:
                    oldValueNum = 3;
                    break;
                case MagicNumber.INT3:
                    oldValueNum = 1;
                    break;
                default:
                    break;
            }
            if (newValueNum > oldValueNum) {
                return 1;
            } else if (newValueNum < oldValueNum) {
                return -1;
            } else {
                return 0;
            }
        }

        /**
         * <该方法返回枚举对应的描述>
         *
         * @return String 返回结果
         */
        @Override
        public String toString() {
            return "EnumDefine." + this.getClass().getSimpleName() + '.' + this.name();
        }

    }

    /**
     * DST_STATUS_E
     */
    public static enum DST_STATUS_E {
        /**
         * 活跃
         */
        ACTIVE(1),
        /**
         * 不活跃
         */
        INACTIVE(2);

        private int value;

        /**
         * 构造函数
         */
        private DST_STATUS_E(int value) {
            this.value = value;
        }

        /**
         * 根据整形值获得枚举类型
         *
         * @param iValue 方法参数：iValue
         * @return DST_STATUS_E 返回结果
         */
        public static DST_STATUS_E valueOf(int iValue) {
            for (DST_STATUS_E value : DST_STATUS_E.values()) {
                if (value.value == iValue) {
                    return value;
                }
            }
            throw new IllegalArgumentException("wrong DST_STATUS_E value:"
                    + iValue);
        }

        /**
         * 取得枚举的整型值
         *
         * @return int 返回结果
         */
        public int getValue() {
            return this.value;
        }

        /**
         * <该方法返回枚举对应的描述>
         *
         * @return String 返回结果
         */
        @Override
        public String toString() {
            return "EnumDefine." + this.getClass().getSimpleName() + '.' + this.name();
        }

    }

    /**
     * SWITCH_E
     */
    public static enum SWITCH_E {
        /**
         * 开
         */
        ON(1),
        /**
         * 关
         */
        OFF(2);

        private int value;

        /**
         * 构造函数
         */
        private SWITCH_E(int value) {
            this.value = value;
        }

        /**
         * 根据整形值获得枚举类型
         *
         * @param iValue 方法参数：iValue
         * @return SWITCH_E 返回结果
         */
        public static SWITCH_E valueOf(int iValue) {
            for (SWITCH_E value : SWITCH_E.values()) {
                if (value.value == iValue) {
                    return value;
                }
            }
            throw new IllegalArgumentException("wrong SWITCH_E value:" + iValue);
        }

        /**
         * 取得枚举的整型值
         *
         * @return int 返回结果
         */
        public int getValue() {
            return this.value;
        }

        /**
         * <该方法返回枚举对应的描述>
         *
         * @return String 返回结果
         */
        @Override
        public String toString() {
            return "EnumDefine." + this.getClass().getSimpleName() + '.' + this.name();
        }

    }

    /**
     * BST_TYPE_E
     */
    public static enum BST_TYPE_E {
        /**
         * 逻辑坏扇区
         */
        LOGIC_BST(1),
        /**
         * 物理坏扇区
         */
        PHYSICAL_BST(2);

        private int value;

        /**
         * 构造函数
         */
        private BST_TYPE_E(int value) {
            this.value = value;
        }

        /**
         * 根据整形值获得枚举类型
         *
         * @param iValue 方法参数：iValue
         * @return BST_TYPE_E 返回结果
         */
        public static BST_TYPE_E valueOf(int iValue) {
            for (BST_TYPE_E value : BST_TYPE_E.values()) {
                if (value.value == iValue) {
                    return value;
                }
            }
            throw new IllegalArgumentException("wrong BST_TYPE_E value:"
                    + iValue);
        }

        /**
         * 取得枚举的整型值
         *
         * @return int 返回结果
         */
        public int getValue() {
            return this.value;
        }

        /**
         * <该方法返回枚举对应的描述>
         *
         * @return String 返回结果
         */
        @Override
        public String toString() {
            return "EnumDefine." + this.getClass().getSimpleName() + '.' + this.name();
        }

    }

    /**
     * TASK_TYPE_E
     */
    public static enum TASK_TYPE_E {
        /**
         * 预拷贝
         */
        TASK_PRECOPY(1),
        /**
         * 重构
         */
        TASK_RECONSTRUCT(2),
        /**
         * 迁移
         */
        TASK_MIGRATION(3),

        /**
         * 格式化
         */
        TASK_TYPE_TASK_FORMAT(4);

        private int value;

        /**
         * 构造函数
         */
        private TASK_TYPE_E(int value) {
            this.value = value;
        }

        /**
         * 根据整形值获得枚举类型
         *
         * @param iValue 方法参数：iValue
         * @return TASK_TYPE_E 返回结果
         */
        public static TASK_TYPE_E valueOf(int iValue) {
            for (TASK_TYPE_E value : TASK_TYPE_E.values()) {
                if (value.value == iValue) {
                    return value;
                }
            }
            throw new IllegalArgumentException("wrong TASK_TYPE_E value:"
                    + iValue);
        }

        /**
         * 取得枚举的整型值
         *
         * @return int 返回结果
         */
        public int getValue() {
            return this.value;
        }

        /**
         * <该方法返回枚举对应的描述>
         *
         * @return String 返回结果
         */
        @Override
        public String toString() {
            return "EnumDefine." + this.getClass().getSimpleName() + '.' + this.name();
        }

    }

    /**
     * LUN_STRIPE_UNIT_SIZE_E
     */
    public static enum LUN_STRIPE_UNIT_SIZE_E {
        /**
         * 分条深度为8KB
         */
        LUN_STRIPE_UNIT_SIZE_8K(8),
        /**
         * 分条深度为16KB
         */
        LUN_STRIPE_UNIT_SIZE_16K(16),
        /**
         * 分条深度为32KB
         */
        LUN_STRIPE_UNIT_SIZE_32K(32),
        /**
         * 分条深度为64KB
         */
        LUN_STRIPE_UNIT_SIZE_64K(64),
        /**
         * 分条深度为128KB
         */
        LUN_STRIPE_UNIT_SIZE_128K(128),
        /**
         * 分条深度为256KB
         */
        LUN_STRIPE_UNIT_SIZE_256K(256),
        /**
         * 分条深度为512KB
         */
        LUN_STRIPE_UNIT_SIZE_512K(512),
        /**
         * 分条深度为1024KB
         */
        LUN_STRIPE_UNIT_SIZE_1024K(1024);

        private int value;

        /**
         * 构造函数
         */
        private LUN_STRIPE_UNIT_SIZE_E(int value) {
            this.value = value;
        }

        /**
         * 根据整形值获得枚举类型
         *
         * @param iValue 方法参数：iValue
         * @return LUN_STRIPE_UNIT_SIZE_E 返回结果
         */
        public static LUN_STRIPE_UNIT_SIZE_E valueOf(int iValue) {
            for (LUN_STRIPE_UNIT_SIZE_E value : LUN_STRIPE_UNIT_SIZE_E.values()) {
                if (value.value == iValue) {
                    return value;
                }
            }
            throw new IllegalArgumentException(
                    "wrong LUN_STRIPE_UNIT_SIZE_E value:" + iValue);
        }

        /**
         * 取得枚举的整型值
         *
         * @return int 返回结果
         */
        public int getValue() {
            return this.value;
        }

        /**
         * <该方法返回枚举对应的描述>
         *
         * @return String 返回结果
         */
        @Override
        public String toString() {
            return "EnumDefine." + this.getClass().getSimpleName() + '.' + this.name();
        }

    }

    /**
     * CACHE_WRITE_BACK_E
     */
    public static enum CACHE_WRITE_BACK_E {
        /**
         * 回写
         */
        WRITE_BACK(1),
        /**
         * 透写
         */
        WRITE_THROUGH(2),
        /**
         * 强制回写
         */
        WRITE_BACK_MANDATORY(3);

        private int value;

        /**
         * 构造函数
         */
        private CACHE_WRITE_BACK_E(int value) {
            this.value = value;
        }

        /**
         * 根据整形值获得枚举类型
         *
         * @param iValue 方法参数：iValue
         * @return CACHE_WRITE_BACK_E 返回结果
         */
        public static CACHE_WRITE_BACK_E valueOf(int iValue) {
            for (CACHE_WRITE_BACK_E value : CACHE_WRITE_BACK_E.values()) {
                if (value.value == iValue) {
                    return value;
                }
            }
            throw new IllegalArgumentException(
                    "wrong CACHE_WRITE_BACK_E value:" + iValue);
        }

        /**
         * 取得枚举的整型值
         *
         * @return int 返回结果
         */
        public int getValue() {
            return this.value;
        }

        /**
         * <该方法返回枚举对应的描述>
         *
         * @return String 返回结果
         */
        @Override
        public String toString() {
            return "EnumDefine." + this.getClass().getSimpleName() + '.' + this.name();
        }

    }

    /**
     * CACHE_MIRROR_STATUS_E
     */
    public static enum CACHE_MIRROR_STATUS_E {
        /**
         * 非镜像
         */
        CACHE_MIRROR_DISABLE(0),
        /**
         * 镜像
         */
        CACHE_MIRROR_ENABLE(1);

        private int value;

        /**
         * 构造函数
         */
        private CACHE_MIRROR_STATUS_E(int value) {
            this.value = value;
        }

        /**
         * 根据整形值获得枚举类型
         *
         * @param iValue 方法参数：iValue
         * @return CACHE_MIRROR_STATUS_E 返回结果
         */
        public static CACHE_MIRROR_STATUS_E valueOf(int iValue) {
            for (CACHE_MIRROR_STATUS_E value : CACHE_MIRROR_STATUS_E.values()) {
                if (value.value == iValue) {
                    return value;
                }
            }
            throw new IllegalArgumentException(
                    "wrong CACHE_MIRROR_STATUS_E value:" + iValue);
        }

        /**
         * 取得枚举的整型值
         *
         * @return int 返回结果
         */
        public int getValue() {
            return this.value;
        }

        /**
         * <该方法返回枚举对应的描述>
         *
         * @return String 返回结果
         */
        @Override
        public String toString() {
            return "EnumDefine." + this.getClass().getSimpleName() + '.' + this.name();
        }

    }

    /**
     * CACHE_READ_AHEAD_STRATEGY_E
     */
    public static enum CACHE_READ_AHEAD_STRATEGY_E {
        /**
         * 不预取
         */
        CACHE_READ_AHEAD_STRATEGY_NOTHING(0),
        /**
         * 固定预取
         */
        CACHE_READ_AHEAD_STRATEGY_FASTNESS(1),
        /**
         * 可变预取
         */
        CACHE_READ_AHEAD_STRATEGY_MULTIPLIER(2),
        /**
         * 只能预取
         */
        CACHE_READ_AHEAD_STRATEGY_INTELLIGENT(3);

        private int value;

        /**
         * 构造函数
         */
        private CACHE_READ_AHEAD_STRATEGY_E(int value) {
            this.value = value;
        }

        /**
         * 根据整形值获得枚举类型
         *
         * @param iValue 方法参数：iValue
         * @return CACHE_READ_AHEAD_STRATEGY_E 返回结果
         */
        public static CACHE_READ_AHEAD_STRATEGY_E valueOf(int iValue) {
            for (CACHE_READ_AHEAD_STRATEGY_E value : CACHE_READ_AHEAD_STRATEGY_E.values()) {
                if (value.value == iValue) {
                    return value;
                }
            }
            throw new IllegalArgumentException(
                    "wrong CACHE_READ_AHEAD_STRATEGY_E value:" + iValue);
        }

        /**
         * 取得枚举的整型值
         *
         * @return int 返回结果
         */
        public int getValue() {
            return this.value;
        }

        /**
         * <该方法返回枚举对应的描述>
         *
         * @return String 返回结果
         */
        @Override
        public String toString() {
            return "EnumDefine." + this.getClass().getSimpleName() + '.' + this.name();
        }

    }

    /**
     * RAID_LEVEL_E
     */
    public static enum RAID_LEVEL_E {
        /**
         * RADI10
         */
        RAID_LEVEL_RAID10(1),
        /**
         * RAID5
         */
        RAID_LEVEL_RAID5(2),
        /**
         * RAID0
         */
        RAID_LEVEL_RAID0(3),
        /**
         * RAID1
         */
        RAID_LEVEL_RAID1(4),
        /**
         * RAID6
         */
        RAID_LEVEL_RAID6(5),
        /**
         * RAID50
         */
        RAID_LEVEL_RAID50(6),
        /**
         * RAID3
         */
        RAID_LEVEL_RAID3(7);

        private int value;

        /**
         * 构造函数
         */
        private RAID_LEVEL_E(int value) {
            this.value = value;
        }

        /**
         * 根据整形值获得枚举类型
         *
         * @param iValue 方法参数：iValue
         * @return RAID_LEVEL_E 返回结果
         */
        public static RAID_LEVEL_E valueOf(int iValue) {
            for (RAID_LEVEL_E value : RAID_LEVEL_E.values()) {
                if (value.value == iValue) {
                    return value;
                }
            }
            throw new IllegalArgumentException("wrong RAID_LEVEL_E value:"
                    + iValue);
        }

        /**
         * 取得枚举的整型值
         *
         * @return int 返回结果
         */
        public int getValue() {
            return this.value;
        }

        /**
         * <该方法返回枚举对应的描述>
         *
         * @return String 返回结果
         */
        @Override
        public String toString() {
            return "EnumDefine." + this.getClass().getSimpleName() + '.' + this.name();
        }

    }

    /**
     * RM_UPDATE_TYPE_E
     */
    public static enum RM_UPDATE_TYPE_E {
        /**
         * 未知
         */
        UNKNOWN(-1),
        /**
         * 手动同步
         */
        RM_UPDATE_TYPE_MANUAL(1),
        /**
         * 同步开始后定时等待
         */
        RM_UPDATE_TYPE_AUTO_START_TIMING(2),
        /**
         * 同步完成后定时等待
         */
        RM_UPDATE_TYPE_AUTO_END_TIMING(3);

        private int value;

        /**
         * 构造函数
         */
        private RM_UPDATE_TYPE_E(int value) {
            this.value = value;
        }

        /**
         * 根据整形值获得枚举类型
         *
         * @param iValue 方法参数：iValue
         * @return RM_UPDATE_TYPE_E 返回结果
         */
        public static RM_UPDATE_TYPE_E valueOf(int iValue) {
            for (RM_UPDATE_TYPE_E value : RM_UPDATE_TYPE_E.values()) {
                if (value.value == iValue) {
                    return value;
                }
            }
            //            throw new IllegalArgumentException("wrong RM_UPDATE_TYPE_E value:"
            //                    + iValue);
            return UNKNOWN;
        }

        /**
         * 取得枚举的整型值
         *
         * @return int 返回结果
         */
        public int getValue() {
            return this.value;
        }

        /**
         * <该方法返回枚举对应的描述>
         *
         * @return String 返回结果
         */
        @Override
        public String toString() {
            return "EnumDefine." + this.getClass().getSimpleName() + '.' + this.name();
        }

    }

    /**
     * RM_LUN_MIRROR_STATE_E
     */
    public static enum RM_LUN_MIRROR_STATE_E {
        /**
         * 已同步
         */
        RM_LUN_MIRROR_STATE_SYNCED(1),
        /**
         * 一致
         */
        RM_LUN_MIRROR_STATE_CONSISTENT(2),
        /**
         * 同步中
         */
        RM_LUN_MIRROR_STATE_SYNCING(3),
        /**
         * 初始同步
         */
        RM_LUN_MIRROR_STATE_INIT_SYNCING(4),
        /**
         * 不一致
         */
        RM_LUN_MIRROR_STATE_INCONSISTENT(5);
        //        RM_LUN_MIRROR_STATE_NOT_SYNC(6); //未同步

        private int value;

        /**
         * 构造函数
         */
        private RM_LUN_MIRROR_STATE_E(int value) {
            this.value = value;
        }

        /**
         * 根据整形值获得枚举类型
         *
         * @param iValue 方法参数：iValue
         * @return RM_LUN_MIRROR_STATE_E 返回结果
         */
        public static RM_LUN_MIRROR_STATE_E valueOf(int iValue) {
            for (RM_LUN_MIRROR_STATE_E value : RM_LUN_MIRROR_STATE_E.values()) {
                if (value.value == iValue) {
                    return value;
                }
            }
            throw new IllegalArgumentException(
                    "wrong RM_LUN_MIRROR_STATE_E value:" + iValue);
        }

        /**
         * 取得枚举的整型值
         *
         * @return int 返回结果
         */
        public int getValue() {
            return this.value;
        }

        /**
         * <该方法返回枚举对应的描述>
         *
         * @return String 返回结果
         */
        @Override
        public String toString() {
            return "EnumDefine." + this.getClass().getSimpleName() + '.' + this.name();
        }

    }

    /**
     * SPEED_LEVEL_E
     */
    public static enum SPEED_LEVEL_E {
        /** 枚举变量 */
        /**
         * 低
         */
        UNKNOWN(-1),
        /** 枚举变量 */
        /**
         * 低
         */
        SPEED_LEVEL_LOW(1),
        /**
         * 中等
         */
        SPEED_LEVEL_MIDDLE(2),
        /**
         * 高
         */
        SPEED_LEVEL_HIGH(3),
        /**
         * 最高
         */
        SPEED_LEVEL_ASAP(4);

        private int value;

        /**
         * 构造函数
         */
        private SPEED_LEVEL_E(int value) {
            this.value = value;
        }

        /**
         * 根据整形值获得枚举类型
         *
         * @param iValue 方法参数：iValue
         * @return SPEED_LEVEL_E 返回结果
         */
        public static SPEED_LEVEL_E valueOf(int iValue) {
            for (SPEED_LEVEL_E value : SPEED_LEVEL_E.values()) {
                if (value.value == iValue) {
                    return value;
                }
            }
            return UNKNOWN;
            //            throw new IllegalArgumentException("wrong SPEED_LEVEL_E value:"
            //                    + iValue);
        }

        /**
         * 取得枚举的整型值
         *
         * @return int 返回结果
         */
        public int getValue() {
            return this.value;
        }

        /**
         * <该方法返回枚举对应的描述>
         *
         * @return String 返回结果
         */
        @Override
        public String toString() {
            return "EnumDefine." + this.getClass().getSimpleName() + '.' + this.name();
        }

    }

    /**
     * LUN_RELATION_E
     */
    public static enum LUN_RELATION_E {
        /**
         * 主LUN或源lUN
         */
        LUN_RELATION_MASTER(1),
        /**
         * 从LUN或目标LUN
         */
        LUN_RELATION_SLAVE(2);

        private int value;

        /**
         * 构造函数
         */
        private LUN_RELATION_E(int value) {
            this.value = value;
        }

        /**
         * 根据整形值获得枚举类型
         *
         * @param iValue 方法参数：iValue
         * @return LUN_RELATION_E 返回结果
         */
        public static LUN_RELATION_E valueOf(int iValue) {
            for (LUN_RELATION_E value : LUN_RELATION_E.values()) {
                if (value.value == iValue) {
                    return value;
                }
            }
            throw new IllegalArgumentException("wrong LUN_RELATION_E value:"
                    + iValue);
        }

        /**
         * 取得枚举的整型值
         *
         * @return int 返回结果
         */
        public int getValue() {
            return this.value;
        }

        /**
         * <该方法返回枚举对应的描述>
         *
         * @return String 返回结果
         */
        @Override
        public String toString() {
            return "EnumDefine." + this.getClass().getSimpleName() + '.' + this.name();
        }

    }

    /**
     * CPY_TYPE_E
     */
    public static enum CPY_TYPE_E {
        /**
         * 全量LUN拷贝
         */
        CPY_TYPE_FULL(1),
        /**
         * 增量LUN拷贝
         */
        CPY_TYPE_INCREMENT(2);

        private int value;

        /**
         * 构造函数
         */
        private CPY_TYPE_E(int value) {
            this.value = value;
        }

        /**
         * 根据整形值获得枚举类型
         *
         * @param iValue 方法参数：iValue
         * @return CPY_TYPE_E 返回结果
         */
        public static CPY_TYPE_E valueOf(int iValue) {
            for (CPY_TYPE_E value : CPY_TYPE_E.values()) {
                if (value.value == iValue) {
                    return value;
                }
            }
            throw new IllegalArgumentException("wrong CPY_TYPE_E value:"
                    + iValue);
        }

        /**
         * 取得枚举的整型值
         *
         * @return int 返回结果
         */
        public int getValue() {
            return this.value;
        }

        /**
         * <该方法返回枚举对应的描述>
         *
         * @return String 返回结果
         */
        @Override
        public String toString() {
            return "EnumDefine." + this.getClass().getSimpleName() + '.' + this.name();
        }

    }

    /**
     * EXC_AND_REC_MODE_E
     */
    public static enum EXC_AND_REC_MODE_E {
        /**
         * 自动恢复
         */
        EXC_AND_REC_MODE_AUTO(1),
        /**
         * 手动恢复
         */
        EXC_AND_REC_MODE_MANUAL(2);

        private int value;

        /**
         * 构造函数
         */
        private EXC_AND_REC_MODE_E(int value) {
            this.value = value;
        }

        /**
         * 根据整形值获得枚举类型
         *
         * @param iValue 方法参数：iValue
         * @return EXC_AND_REC_MODE_E 返回结果
         */
        public static EXC_AND_REC_MODE_E valueOf(int iValue) {
            for (EXC_AND_REC_MODE_E value : EXC_AND_REC_MODE_E.values()) {
                if (value.value == iValue) {
                    return value;
                }
            }
            throw new IllegalArgumentException(
                    "wrong EXC_AND_REC_MODE_E value:" + iValue);
        }

        /**
         * 取得枚举的整型值
         *
         * @return int 返回结果
         */
        public int getValue() {
            return this.value;
        }

        /**
         * <该方法返回枚举对应的描述>
         *
         * @return String 返回结果
         */
        @Override
        public String toString() {
            return "EnumDefine." + this.getClass().getSimpleName() + '.' + this.name();
        }

    }

    /**
     * HYPERCOPY_LUN_TYPE_E
     */
    public static enum HYPERCOPY_LUN_TYPE_E {
        /**
         * 本阵列的LUN
         */
        HYPERCOPY_LUN_TYPE_LOCAL(0),
        /**
         * 外部私有阵列的LUN
         */
        HYPERCOPY_LUN_TYPE_EXTS5000(1),
        /**
         * 第三方阵列的LUN
         */
        HYPERCOPY_LUN_TYPE_THIRD(2);

        private int value;

        /**
         * 构造函数
         */
        private HYPERCOPY_LUN_TYPE_E(int value) {
            this.value = value;
        }

        /**
         * 根据整形值获得枚举类型
         *
         * @param iValue 方法参数：iValue
         * @return HYPERCOPY_LUN_TYPE_E 返回结果
         */
        public static HYPERCOPY_LUN_TYPE_E valueOf(int iValue) {
            for (HYPERCOPY_LUN_TYPE_E value : HYPERCOPY_LUN_TYPE_E.values()) {
                if (value.value == iValue) {
                    return value;
                }
            }
            throw new IllegalArgumentException(
                    "wrong HYPERCOPY_LUN_TYPE_E value:" + iValue);
        }

        /**
         * 取得枚举的整型值
         *
         * @return int 返回结果
         */
        public int getValue() {
            return this.value;
        }

        /**
         * <该方法返回枚举对应的描述>
         *
         * @return String 返回结果
         */
        @Override
        public String toString() {
            return "EnumDefine." + this.getClass().getSimpleName() + '.' + this.name();
        }

    }

    /**
     * HYPERCLONE_LUN_STATUS_E
     */
    public static enum HYPERCLONE_LUN_STATUS_E {
        /**
         * 数据不一致
         */
        HYPERCLONE_LUN_STATUS_INCONSISTENT(1),
        /**
         * 反向数据不一致
         */
        HYPERCLONE_LUN_STATUS_REVINCONSISTENT(2),
        /**
         * 同步中
         */
        HYPERCLONE_LUN_STATUS_SYNCHRONIZING(3),
        /**
         * 反向同步中
         */
        HYPERCLONE_LUN_STATUS_REVSYNCHRONIZING(4),
        /**
         * 数据一致
         */
        HYPERCLONE_LUN_STATUS_CONSISTENT(5),
        /**
         * 同步完成
         */
        HYPERCLONE_LUN_STATUS_SYNCHRONIZED(6),
        /**
         * 故障
         */
        HYPERCLONE_LUN_STATUS_FAULT(7);

        private int value;

        /**
         * 构造函数
         */
        private HYPERCLONE_LUN_STATUS_E(int value) {
            this.value = value;
        }

        /**
         * 根据整形值获得枚举类型
         *
         * @param iValue 方法参数：iValue
         * @return HYPERCLONE_LUN_STATUS_E 返回结果
         */
        public static HYPERCLONE_LUN_STATUS_E valueOf(int iValue) {
            for (HYPERCLONE_LUN_STATUS_E value : HYPERCLONE_LUN_STATUS_E.values()) {
                if (value.value == iValue) {
                    return value;
                }
            }
            throw new IllegalArgumentException(
                    "wrong HYPERCLONE_LUN_STATUS_E value:" + iValue);
        }

        /**
         * 取得枚举的整型值
         *
         * @return int 返回结果
         */
        public int getValue() {
            return this.value;
        }

        /**
         * <该方法返回枚举对应的描述>
         *
         * @return String 返回结果
         */
        @Override
        public String toString() {
            return "EnumDefine." + this.getClass().getSimpleName() + '.' + this.name();
        }

    }

    /**
     * HYPERCLONE_PAIR_STATUS_E
     */
    public static enum HYPERCLONE_PAIR_STATUS_E {
        /**
         * 分裂
         */
        HYPERCLONE_PAIR_STATUS_SPLIT(1),
        /**
         * 同步中
         */
        HYPERCLONE_PAIR_STATUS_SYNCHRONIZING(2),
        /**
         * 反向同步中
         */
        HYPERCLONE_PAIR_STATUS_REVSYNCHRONIZING(3),
        /**
         * 正常
         */
        HYPERCLONE_PAIR_STATUS_NORMAL(4),
        /**
         * 排队
         */
        HYPERCLONE_PAIR_STATUS_QUEUE(5),
        /**
         * 异常断开
         */
        HYPERCLONE_PAIR_STATUS_INTERRUPTED(6),
        /**
         * 待恢复
         */
        HYPERCLONE_PAIR_STATUS_TOBERECOVERED(7),
        /**
         * 故障
         */
        HYPERCLONE_PAIR_STATUS_FAULT(8);

        private int value;

        /**
         * 构造函数
         */
        private HYPERCLONE_PAIR_STATUS_E(int value) {
            this.value = value;
        }

        /**
         * 根据整形值获得枚举类型
         *
         * @param iValue 方法参数：iValue
         * @return HYPERCLONE_PAIR_STATUS_E 返回结果
         */
        public static HYPERCLONE_PAIR_STATUS_E valueOf(int iValue) {
            for (HYPERCLONE_PAIR_STATUS_E value : HYPERCLONE_PAIR_STATUS_E.values()) {
                if (value.value == iValue) {
                    return value;
                }
            }
            throw new IllegalArgumentException(
                    "wrong HYPERCLONE_PAIR_STATUS_E value:" + iValue);
        }

        /**
         * 取得枚举的整型值
         *
         * @return int 返回结果
         */
        public int getValue() {
            return this.value;
        }

        /**
         * <该方法返回枚举对应的描述>
         *
         * @return String 返回结果
         */
        @Override
        public String toString() {
            return "EnumDefine." + this.getClass().getSimpleName() + '.' + this.name();
        }

    }

    /**
     * RM_PAIR_STATE_E
     */
    public static enum RM_PAIR_STATE_E {
        /**
         * 正常
         */
        RM_PAIR_STATE_NORMAL(1),
        /**
         * 同步中
         */
        RM_PAIR_STATE_SYNCING(2),
        /**
         * 待恢复
         */
        RM_PAIR_STATE_TO_BE_RECOVERD(3),
        /**
         * 异常断开
         */
        RM_PAIR_STATE_INTERRUPTED(4),
        /**
         * 已分裂
         */
        RM_PAIR_STATE_SPLITED(5),
        /**
         * 镜像失效
         */
        RM_PAIR_STATE_INVALID(6);
        //        RM_PAIR_STATE_CG_INTERRUPTED(7); //因一致性组异常断开，导致远程复制异常断开

        private int value;

        /**
         * 构造函数
         */
        private RM_PAIR_STATE_E(int value) {
            this.value = value;
        }

        /**
         * 根据整形值获得枚举类型
         *
         * @param iValue 方法参数：iValue
         * @return RM_PAIR_STATE_E 返回结果
         */
        public static RM_PAIR_STATE_E valueOf(int iValue) {
            for (RM_PAIR_STATE_E value : RM_PAIR_STATE_E.values()) {
                if (value.value == iValue) {
                    return value;
                }
            }
            throw new IllegalArgumentException("wrong RM_PAIR_STATE_E value:"
                    + iValue);
        }

        /**
         * 取得枚举的整型值
         *
         * @return int 返回结果
         */
        public int getValue() {
            return this.value;
        }

        /**
         * <该方法返回枚举对应的描述>
         *
         * @return String 返回结果
         */
        @Override
        public String toString() {
            return "EnumDefine." + this.getClass().getSimpleName() + '.' + this.name();
        }

    }

    /**
     * LINK_CONNECT_TYPE_E
     */
    public static enum LINK_CONNECT_TYPE_E {
        /**
         * 平行
         */
        LINK_CONNECT_TYPE_PARALLEL(1),
        /**
         * 交叉
         */
        LINK_CONNECT_TYPE_ACROSS(2);

        private int value;

        /**
         * 构造函数
         */
        private LINK_CONNECT_TYPE_E(int value) {
            this.value = value;
        }

        /**
         * 根据整形值获得枚举类型
         *
         * @param iValue 方法参数：iValue
         * @return LINK_CONNECT_TYPE_E 返回结果
         */
        public static LINK_CONNECT_TYPE_E valueOf(int iValue) {
            for (LINK_CONNECT_TYPE_E value : LINK_CONNECT_TYPE_E.values()) {
                if (value.value == iValue) {
                    return value;
                }
            }
            throw new IllegalArgumentException(
                    "wrong LINK_CONNECT_TYPE_E value:" + iValue);
        }

        /**
         * 取得枚举的整型值
         *
         * @return int 返回结果
         */
        public int getValue() {
            return this.value;
        }

        /**
         * <该方法返回枚举对应的描述>
         *
         * @return String 返回结果
         */
        @Override
        public String toString() {
            return "EnumDefine." + this.getClass().getSimpleName() + '.' + this.name();
        }

    }

    /**
     * LINK_CONNECT_STATUS_E
     */
    public static enum LINK_CONNECT_STATUS_E {
        /**
         * 已连接
         */
        LINK_CONNECT_STATUS_CONNECTED(1),
        /**
         * 未连接
         */
        LINK_CONNECT_STATUS_UNCONNECTED(2);

        private int value;

        /**
         * 构造函数
         */
        private LINK_CONNECT_STATUS_E(int value) {
            this.value = value;
        }

        /**
         * 根据整形值获得枚举类型
         *
         * @param iValue 方法参数：iValue
         * @return LINK_CONNECT_STATUS_E 返回结果
         */
        public static LINK_CONNECT_STATUS_E valueOf(int iValue) {
            for (LINK_CONNECT_STATUS_E value : LINK_CONNECT_STATUS_E.values()) {
                if (value.value == iValue) {
                    return value;
                }
            }
            throw new IllegalArgumentException(
                    "wrong LINK_CONNECT_STATUS_E value:" + iValue);
        }

        /**
         * 取得枚举的整型值
         *
         * @return int 返回结果
         */
        public int getValue() {
            return this.value;
        }

        /**
         * <该方法返回枚举对应的描述>
         *
         * @return String 返回结果
         */
        @Override
        public String toString() {
            return "EnumDefine." + this.getClass().getSimpleName() + '.' + this.name();
        }

    }

    /**
     * LINK_TYPE_E
     */
    public static enum LINK_TYPE_E {
        /**
         * FC链路
         */
        LINK_TYPE_FC(1),
        /**
         * iSCSI链路
         */
        LINK_TYPE_ISCSI(2);

        private int value;

        /**
         * 构造函数
         */
        private LINK_TYPE_E(int value) {
            this.value = value;
        }

        /**
         * 根据整形值获得枚举类型
         *
         * @param iValue 方法参数：iValue
         * @return LINK_TYPE_E 返回结果
         */
        public static LINK_TYPE_E valueOf(int iValue) {
            for (LINK_TYPE_E value : LINK_TYPE_E.values()) {
                if (value.value == iValue) {
                    return value;
                }
            }
            throw new IllegalArgumentException("wrong LINK_TYPE_E value:"
                    + iValue);
        }

        /**
         * 取得枚举的整型值
         *
         * @return int 返回结果
         */
        public int getValue() {
            return this.value;
        }

        /**
         * <该方法返回枚举对应的描述>
         *
         * @return String 返回结果
         */
        @Override
        public String toString() {
            return "EnumDefine." + this.getClass().getSimpleName() + '.' + this.name();
        }

    }

    /**
     * EPL_ARRAY_TYPE_E
     */
    public static enum EPL_ARRAY_TYPE_E {
        /**
         * 私有阵列
         */
        EPL_ARRAY_PRIVATE(1),
        /**
         * 第三方阵列
         */
        EPL_ARRAY_3RD(2),
        /**
         * 未知阵列
         */
        EPL_ARRAY_UNKNOWN(3);

        private int value;

        /**
         * 构造函数
         */
        private EPL_ARRAY_TYPE_E(int value) {
            this.value = value;
        }

        /**
         * 根据整形值获得枚举类型
         *
         * @param iValue 方法参数：iValue
         * @return EPL_ARRAY_TYPE_E 返回结果
         */
        public static EPL_ARRAY_TYPE_E valueOf(int iValue) {
            for (EPL_ARRAY_TYPE_E value : EPL_ARRAY_TYPE_E.values()) {
                if (value.value == iValue) {
                    return value;
                }
            }
            throw new IllegalArgumentException("wrong EPL_ARRAY_TYPE_E value:"
                    + iValue);
        }

        /**
         * 取得枚举的整型值
         *
         * @return int 返回结果
         */
        public int getValue() {
            return this.value;
        }

        /**
         * <该方法返回枚举对应的描述>
         *
         * @return String 返回结果
         */
        @Override
        public String toString() {
            return "EnumDefine." + this.getClass().getSimpleName() + '.' + this.name();
        }

    }

    /**
     * RM_MODEL_E
     */
    public static enum RM_MODEL_E {
        /**
         * 同步远程复制
         */
        RM_MODEL_SYNC(1),
        /**
         * 异步远程复制
         */
        RM_MODEL_ASYNC(2);
        //        define(-2147483648), //中文术语
        //        WORM_LUN_READ_ONLY(1), //只读
        //        WORM_LUN_READ_WRITE(2); //读写

        private int value;

        /**
         * 构造函数
         */
        private RM_MODEL_E(int value) {
            this.value = value;
        }

        /**
         * 根据整形值获得枚举类型
         *
         * @param iValue 方法参数：iValue
         * @return RM_MODEL_E 返回结果
         */
        public static RM_MODEL_E valueOf(int iValue) {
            for (RM_MODEL_E value : RM_MODEL_E.values()) {
                if (value.value == iValue) {
                    return value;
                }
            }
            throw new IllegalArgumentException("wrong RM_MODEL_E value:"
                    + iValue);
        }

        /**
         * 取得枚举的整型值
         *
         * @return int 返回结果
         */
        public int getValue() {
            return this.value;
        }

        /**
         * <该方法返回枚举对应的描述>
         *
         * @return String 返回结果
         */
        @Override
        public String toString() {
            return "EnumDefine." + this.getClass().getSimpleName() + '.' + this.name();
        }

    }

    /**
     * EXT_LUN_ID_TYPE
     */
    public static enum EXT_LUN_ID_TYPE {
        /**
         * LUN WWN方式标识第三方LUN
         */
        EXT_LUN_LUNWWN(1),
        /**
         * WWPN+HOSTID方式标识第三方LUN
         */
        EXT_LUN_WWPN_AND_HOSTID(2);

        private int value;

        /**
         * 构造函数
         */
        private EXT_LUN_ID_TYPE(int value) {
            this.value = value;
        }

        /**
         * 根据整形值获得枚举类型
         *
         * @param iValue 方法参数：iValue
         * @return EXT_LUN_ID_TYPE 返回结果
         */
        public static EXT_LUN_ID_TYPE valueOf(int iValue) {
            for (EXT_LUN_ID_TYPE value : EXT_LUN_ID_TYPE.values()) {
                if (value.value == iValue) {
                    return value;
                }
            }
            throw new IllegalArgumentException("wrong EXT_LUN_ID_TYPE value:"
                    + iValue);
        }

        /**
         * 取得枚举的整型值
         *
         * @return int 返回结果
         */
        public int getValue() {
            return this.value;
        }

        /**
         * <该方法返回枚举对应的描述>
         *
         * @return String 返回结果
         */
        @Override
        public String toString() {
            return "EnumDefine." + this.getClass().getSimpleName() + '.' + this.name();
        }

    }

    /**
     * TargetSessionIdentifyingHandle_E
     */
    public static enum TargetSessionIdentifyingHandle_E {
        /**
         * Discovery
         */
        Discovery(2),
        /**
         * Normal
         */
        Normal(3);

        private int value;

        /**
         * 构造函数
         */
        private TargetSessionIdentifyingHandle_E(int value) {
            this.value = value;
        }

        /**
         * 根据整形值获得枚举类型
         *
         * @param iValue 方法参数：iValue
         * @return TargetSessionIdentifyingHandle_E 返回结果
         */
        public static TargetSessionIdentifyingHandle_E valueOf(int iValue) {
            for (TargetSessionIdentifyingHandle_E value : TargetSessionIdentifyingHandle_E.values()) {
                if (value.value == iValue) {
                    return value;
                }
            }
            throw new IllegalArgumentException(
                    "wrong TargetSessionIdentifyingHandle_E value:" + iValue);
        }

        /**
         * 取得枚举的整型值
         *
         * @return int 返回结果
         */
        public int getValue() {
            return this.value;
        }

        /**
         * <该方法返回枚举对应的描述>
         *
         * @return String 返回结果
         */
        @Override
        public String toString() {
            return "EnumDefine." + this.getClass().getSimpleName() + '.' + this.name();
        }

    }

    /**
     * OS_TYPE_E
     */
    public static enum OS_TYPE_E {
        /**
         * Linux
         */
        Linux(0),
        /**
         * Windows
         */
        Windows(1),
        /**
         * Solaris
         */
        Solaris(2),
        /**
         * HP-UX
         */
        HPUX(3),
        /**
         * AIX
         */
        AIX(4),
        /**
         * XenServer
         */
        XenServer(5),
        /**
         * Mac OS
         */
        MacOS(6),
        /**
         * 支持VASA后需要增加ESX
         */
        ESX(7),
        //VIS6000(8)为HVSV1R1C99新增的类型，只能在cli下创建，cli上显示类型为VIS6000,ISM上显示类型为linux
        /**
         * HVSV1R1C99新增类型
         */
        VIS6000(8);

        private int value;

        /**
         * 构造函数
         */
        private OS_TYPE_E(int value) {
            this.value = value;
        }

        /**
         * 根据整形值获得枚举类型
         *
         * @param iValue 方法参数：iValue
         * @return OS_TYPE_E 返回结果
         */
        public static OS_TYPE_E valueOf(int iValue) {
            for (OS_TYPE_E value : OS_TYPE_E.values()) {
                if (value.value == iValue) {
                    //VIS6000(8)为HVSV1R1C99新增的类型，只能在cli下创建，cli上显示类型为VIS6000,ISM上显示类型为linux
                    if (VIS6000.getValue() == value.value) {
                        return Linux;
                    }
                    return value;
                }
            }
            throw new IllegalArgumentException("wrong OS_TYPE_E value:"
                    + iValue);
        }

        /**
         * 取得枚举的整型值
         *
         * @return int 返回结果
         */
        public int getValue() {
            return this.value;
        }

        /**
         * <该方法返回枚举对应的描述>
         *
         * @return String 返回结果
         */
        @Override
        public String toString() {
            return "EnumDefine." + this.getClass().getSimpleName() + '.' + this.name();
        }

    }

    /**
     * REMOTE_DEVICE_SCOPE_E
     */
    public static enum REMOTE_DEVICE_SCOPE_E {
        /**
         * 所有已经创建的远端设备
         */
        CREATED_REMOTE_DEVICE(1),
        /**
         * 所有已经创建的且处于已连接状态的远端设备
         */
        CREATED_AND_LINKED_REMOTE_DEVICE(2);

        private int value;

        /**
         * 构造函数
         */
        private REMOTE_DEVICE_SCOPE_E(int value) {
            this.value = value;
        }

        /**
         * 根据整形值获得枚举类型
         *
         * @param iValue 方法参数：iValue
         * @return REMOTE_DEVICE_SCOPE_E 返回结果
         */
        public static REMOTE_DEVICE_SCOPE_E valueOf(int iValue) {
            for (REMOTE_DEVICE_SCOPE_E value : REMOTE_DEVICE_SCOPE_E.values()) {
                if (value.value == iValue) {
                    return value;
                }
            }
            throw new IllegalArgumentException(
                    "wrong REMOTE_DEVICE_SCOPE_E value:" + iValue);
        }

        /**
         * 取得枚举的整型值
         *
         * @return int 返回结果
         */
        public int getValue() {
            return this.value;
        }

        /**
         * <该方法返回枚举对应的描述>
         *
         * @return String 返回结果
         */
        @Override
        public String toString() {
            return "EnumDefine." + this.getClass().getSimpleName() + '.' + this.name();
        }

    }

    //begin w00221007 同步问题单P12N-3103 在ISM上创建同步远程复制添加一个从LUN后ISM提示第三方设备不可用，没对第三方设备做过滤

    /**
     * REMOTE_DEVICE_SERVICE_TYPE_E
     */
    public static enum REMOTE_DEVICE_SERVICE_TYPE_E {
        /**
         * LUN拷贝
         */
        LUN_COPY(1),
        /**
         * 远程复制
         */
        REMOTE_REPLICATION(2);

        private int value;

        /**
         * 构造函数
         */
        private REMOTE_DEVICE_SERVICE_TYPE_E(int value) {
            this.value = value;
        }

        /**
         * 根据整形值获得枚举类型
         *
         * @param iValue 方法参数：iValue
         * @return REMOTE_DEVICE_SERVICE_TYPE_E 返回结果
         */
        public static REMOTE_DEVICE_SERVICE_TYPE_E valueOf(int iValue) {
            for (REMOTE_DEVICE_SERVICE_TYPE_E value : REMOTE_DEVICE_SERVICE_TYPE_E.values()) {
                if (value.value == iValue) {
                    return value;
                }
            }
            throw new IllegalArgumentException(
                    "wrong REMOTE_DEVICE_SERVICE_TYPE_E value:" + iValue);
        }

        /**
         * 取得枚举的整型值
         *
         * @return int 返回结果
         */
        public int getValue() {
            return this.value;
        }
    }

    //end w00221007 同步问题单P12N-3103 在ISM上创建同步远程复制添加一个从LUN后ISM提示第三方设备不可用，没对第三方设备做过滤

    /**
     * EXPBOARD_TYPE_E
     */
    public static enum EXPBOARD_TYPE_E {
        /**
         * SAS
         */
        SAS(0),
        /**
         * FC
         */
        FC(1);

        private int value;

        /**
         * 构造函数
         */
        private EXPBOARD_TYPE_E(int value) {
            this.value = value;
        }

        /**
         * 根据整形值获得枚举类型
         *
         * @param iValue 方法参数：iValue
         * @return EXPBOARD_TYPE_E 返回结果
         */
        public static EXPBOARD_TYPE_E valueOf(int iValue) {
            for (EXPBOARD_TYPE_E value : EXPBOARD_TYPE_E.values()) {
                if (value.value == iValue) {
                    return value;
                }
            }
            throw new IllegalArgumentException("wrong EXPBOARD_TYPE_E value:"
                    + iValue);
        }

        /**
         * 取得枚举的整型值
         *
         * @return int 返回结果
         */
        public int getValue() {
            return this.value;
        }

        /**
         * <该方法返回枚举对应的描述>
         *
         * @return String 返回结果
         */
        @Override
        public String toString() {
            return "EnumDefine." + this.getClass().getSimpleName() + '.' + this.name();
        }

    }

    /**
     * INIT_TIER_POLICY_E
     */
    public static enum INIT_TIER_POLICY_E {
        /**
         * 自动
         */
        INIT_TIER_POLICY_AUTO(0),
        /**
         * 最高性能
         */
        INIT_TIER_POLICY_EXTREME_PERFORMANCE(1),
        /**
         * 性能
         */
        INIT_TIER_POLICY_PERFORMANCE(2),
        /**
         * 容量
         */
        INIT_TIER_POLICY_CAPACITY(3);

        private int value;

        /**
         * 构造函数
         */
        private INIT_TIER_POLICY_E(int value) {
            this.value = value;
        }

        /**
         * 根据整形值获得枚举类型
         *
         * @param iValue 方法参数：iValue
         * @return INIT_TIER_POLICY_E 返回结果
         */
        public static INIT_TIER_POLICY_E valueOf(int iValue) {
            for (INIT_TIER_POLICY_E value : INIT_TIER_POLICY_E.values()) {
                if (value.value == iValue) {
                    return value;
                }
            }
            throw new IllegalArgumentException(
                    "wrong INIT_TIER_POLICY_E value:" + iValue);
        }

        /**
         * 取得枚举的整型值
         *
         * @return int 返回结果
         */
        public int getValue() {
            return this.value;
        }

        /**
         * <该方法返回枚举对应的描述>
         *
         * @return String 返回结果
         */
        @Override
        public String toString() {
            return "EnumDefine." + this.getClass().getSimpleName() + '.' + this.name();
        }

    }

    /**
     * MIGRATE_LUN_POLICY_E
     */
    public static enum MIGRATE_LUN_POLICY_E {
        /**
         * 不迁移
         */
        MIGRATE_LUN_POLICY_NONE(0),
        /**
         * 自动迁移
         */
        MIGRATE_LUN_POLICY_AUTO(1),
        /**
         * 保证最高性能迁移
         */
        MIGRATE_LUN_POLICY_HIGHEST(2),
        /**
         * 保证最低性能迁移
         */
        MIGRATE_LUN_POLICY_LOWEST(3);

        private int value;

        /**
         * 构造函数
         */
        private MIGRATE_LUN_POLICY_E(int value) {
            this.value = value;
        }

        /**
         * 根据整形值获得枚举类型
         *
         * @param iValue 方法参数：iValue
         * @return MIGRATE_LUN_POLICY_E 返回结果
         */
        public static MIGRATE_LUN_POLICY_E valueOf(int iValue) {
            for (MIGRATE_LUN_POLICY_E value : MIGRATE_LUN_POLICY_E.values()) {
                if (value.value == iValue) {
                    return value;
                }
            }
            throw new IllegalArgumentException(
                    "wrong MIGRATE_LUN_POLICY_E value:" + iValue);
        }

        /**
         * 取得枚举的整型值
         *
         * @return int 返回结果
         */
        public int getValue() {
            return this.value;
        }

        /**
         * <该方法返回枚举对应的描述>
         *
         * @return String 返回结果
         */
        @Override
        public String toString() {
            return "EnumDefine." + this.getClass().getSimpleName() + '.' + this.name();
        }

    }

    /**
     * SFP_MODE_E
     */
    public static enum SFP_MODE_E {
        /**
         * 单模
         */
        SINGLEMODE(0),
        /**
         * 多模
         */
        MULTIMODE(1);

        private int value;

        /**
         * 构造函数
         */
        private SFP_MODE_E(int value) {
            this.value = value;
        }

        /**
         * 根据整形值获得枚举类型
         *
         * @param iValue 方法参数：iValue
         * @return SFP_MODE_E 返回结果
         */
        public static SFP_MODE_E valueOf(int iValue) {
            for (SFP_MODE_E value : SFP_MODE_E.values()) {
                if (value.value == iValue) {
                    return value;
                }
            }
            throw new IllegalArgumentException("wrong SFP_MODE_E value:"
                    + iValue);
        }

        /**
         * 取得枚举的整型值
         *
         * @return int 返回结果
         */
        public int getValue() {
            return this.value;
        }

        /**
         * <该方法返回枚举对应的描述>
         *
         * @return String 返回结果
         */
        @Override
        public String toString() {
            return "EnumDefine." + this.getClass().getSimpleName() + '.' + this.name();
        }

    }

    /**
     * DISKSCAN_IOTYPE_E
     */
    public static enum DISKSCAN_IOTYPE_E {
        /**
         * 读
         */
        READ(1),
        /**
         * 验证
         */
        VERIFY(2);

        private int value;

        /**
         * 构造函数
         */
        private DISKSCAN_IOTYPE_E(int value) {
            this.value = value;
        }

        /**
         * 根据整形值获得枚举类型
         *
         * @param iValue 方法参数：iValue
         * @return DISKSCAN_IOTYPE_E 返回结果
         */
        public static DISKSCAN_IOTYPE_E valueOf(int iValue) {
            for (DISKSCAN_IOTYPE_E value : DISKSCAN_IOTYPE_E.values()) {
                if (value.value == iValue) {
                    return value;
                }
            }
            throw new IllegalArgumentException("wrong DISKSCAN_IOTYPE_E value:"
                    + iValue);
        }

        /**
         * 取得枚举的整型值
         *
         * @return int 返回结果
         */
        public int getValue() {
            return this.value;
        }

        /**
         * <该方法返回枚举对应的描述>
         *
         * @return String 返回结果
         */
        @Override
        public String toString() {
            return "EnumDefine." + this.getClass().getSimpleName() + '.' + this.name();
        }

    }

    /**
     * HOSTGROUP_WORKMODE_E
     */
    public static enum HOSTGROUP_WORKMODE_E {
        /**
         * A/P-F
         */
        APF(0),
        /**
         * A/A-A
         */
        AAA(1);

        private int value;

        /**
         * 构造函数
         */
        private HOSTGROUP_WORKMODE_E(int value) {
            this.value = value;
        }

        /**
         * 根据整形值获得枚举类型
         *
         * @param iValue 方法参数：iValue
         * @return HOSTGROUP_WORKMODE_E 返回结果
         */
        public static HOSTGROUP_WORKMODE_E valueOf(int iValue) {
            for (HOSTGROUP_WORKMODE_E value : HOSTGROUP_WORKMODE_E.values()) {
                if (value.value == iValue) {
                    return value;
                }
            }
            throw new IllegalArgumentException(
                    "wrong HOSTGROUP_WORKMODE_E value:" + iValue);
        }

        /**
         * 取得枚举的整型值
         *
         * @return int 返回结果
         */
        public int getValue() {
            return this.value;
        }

        /**
         * <该方法返回枚举对应的描述>
         *
         * @return String 返回结果
         */
        @Override
        public String toString() {
            return "EnumDefine." + this.getClass().getSimpleName() + '.' + this.name();
        }

    }

    /**
     * IP_SEC_RULE_E
     */
    public static enum IP_SEC_RULE_E {
        /**
         * 枚举变量
         */
        WHITE_NAME_LIST(1),

        /**
         * 白名单
         */
        BLACK_NAME_LIST(2);

        private int value;

        /**
         * 构造函数
         */
        private IP_SEC_RULE_E(int value) {
            this.value = value;
        }

        /**
         * 根据整形值获得枚举类型
         *
         * @param iValue 方法参数：iValue
         * @return IP_SEC_RULE_E 返回结果
         */
        public static IP_SEC_RULE_E valueOf(int iValue) {
            for (IP_SEC_RULE_E value : IP_SEC_RULE_E.values()) {
                if (value.value == iValue) {
                    return value;
                }
            }
            throw new IllegalArgumentException("wrong IP_SEC_RULE_E value:"
                    + iValue);
        }

        /**
         * 取得枚举的整型值
         *
         * @return int 返回结果
         */
        public int getValue() {
            return this.value;
        }

        /**
         * <该方法返回枚举对应的描述>
         *
         * @return String 返回结果
         */
        @Override
        public String toString() {
            return "EnumDefine." + this.getClass().getSimpleName() + '.' + this.name();
        }

    }

    /**
     * WORM_LUN_STATE
     */
    public static enum WORM_LUN_STATE {
        /**
         * 只读
         */
        WORM_LUN_READ_ONLY(1),
        /**
         * 读写
         */
        WORM_LUN_READ_WRITE(2);

        private int value;

        /**
         * 构造函数
         */
        private WORM_LUN_STATE(int value) {
            this.value = value;
        }

        /**
         * 根据整形值获得枚举类型
         *
         * @param iValue 方法参数：iValue
         * @return WORM_LUN_STATE 返回结果
         */
        public static WORM_LUN_STATE valueOf(int iValue) {
            for (WORM_LUN_STATE value : WORM_LUN_STATE.values()) {
                if (value.value == iValue) {
                    return value;
                }
            }
            throw new IllegalArgumentException("wrong WORM_LUN_STATE value:"
                    + iValue);
        }

        /**
         * 取得枚举的整型值
         *
         * @return int 返回结果
         */
        public int getValue() {
            return this.value;
        }

        /**
         * <该方法返回枚举对应的描述>
         *
         * @return String 返回结果
         */
        @Override
        public String toString() {
            return "EnumDefine." + this.getClass().getSimpleName() + '.' + this.name();
        }

    }

    /**
     * ANALYSIS_RAID_LEVEL_E
     */
    public static enum ANALYSIS_RAID_LEVEL_E {
        /**
         * RAID0
         */
        RAID0(0),
        /**
         * RAID10
         */
        RAID10(1),
        /**
         * 3盘RAID5(2D+1P)
         */
        RAID5_3(2),
        /**
         * 5盘RAID5(4D+1P)
         */
        RAID5_5(3),
        /**
         * 9盘RAID5(8D+1P)
         */
        RAID5_9(4),
        /**
         * 3盘RAID3(2D+1P)
         */
        RAID3_3(5),
        /**
         * 5盘RAID3(4D+1P)
         */
        RAID3_5(6),
        /**
         * 9盘RAID3(8D+1P)
         */
        RAID3_9(7),
        /**
         * 6盘RAID6(4D+2P)
         */
        RAID6_6(8),
        /**
         * 10盘RAID6(8D+2P)
         */
        RAID6_10(9);

        private int value;

        /**
         * 构造函数
         */
        private ANALYSIS_RAID_LEVEL_E(int value) {
            this.value = value;
        }

        /**
         * 根据整形值获得枚举类型
         *
         * @param iValue 方法参数：iValue
         * @return ANALYSIS_RAID_LEVEL_E 返回结果
         */
        public static ANALYSIS_RAID_LEVEL_E valueOf(int iValue) {
            for (ANALYSIS_RAID_LEVEL_E value : ANALYSIS_RAID_LEVEL_E.values()) {
                if (value.value == iValue) {
                    return value;
                }
            }
            throw new IllegalArgumentException(
                    "wrong ANALYSIS_RAID_LEVEL_E value:" + iValue);
        }

        /**
         * 取得枚举的整型值
         *
         * @return int 返回结果
         */
        public int getValue() {
            return this.value;
        }

        /**
         * <该方法返回枚举对应的描述>
         *
         * @return String 返回结果
         */
        @Override
        public String toString() {
            return "EnumDefine." + this.getClass().getSimpleName() + '.' + this.name();
        }

    }

    /**
     * TIER0_ANALYSIS_DISK_TYPE_E
     */
    public static enum TIER0_ANALYSIS_DISK_TYPE_E {
        /**
         * 100G的STAT接口的SSD盘
         */
        SATA_SSD_100G(0);

        private int value;

        /**
         * 构造函数
         */
        private TIER0_ANALYSIS_DISK_TYPE_E(int value) {
            this.value = value;
        }

        /**
         * 根据整形值获得枚举类型
         *
         * @param iValue 方法参数：iValue
         * @return TIER0_ANALYSIS_DISK_TYPE_E 返回结果
         */
        public static TIER0_ANALYSIS_DISK_TYPE_E valueOf(int iValue) {
            for (TIER0_ANALYSIS_DISK_TYPE_E value : TIER0_ANALYSIS_DISK_TYPE_E.values()) {
                if (value.value == iValue) {
                    return value;
                }
            }
            throw new IllegalArgumentException(
                    "wrong TIER0_ANALYSIS_DISK_TYPE_E value:" + iValue);
        }

        /**
         * 取得枚举的整型值
         *
         * @return int 返回结果
         */
        public int getValue() {
            return this.value;
        }

        /**
         * <该方法返回枚举对应的描述>
         *
         * @return String 返回结果
         */
        @Override
        public String toString() {
            return "EnumDefine." + this.getClass().getSimpleName() + '.' + this.name();
        }

    }

    /**
     * TIER1_ANALYSIS_DISK_TYPE_E
     */
    public static enum TIER1_ANALYSIS_DISK_TYPE_E {
        /**
         * 10K 300G SAS盘
         */
        SAS_300G_10K(1),
        /**
         * 10K 600G SAS盘
         */
        SAS_600G_10K(2);

        private int value;

        /**
         * 构造函数
         */
        private TIER1_ANALYSIS_DISK_TYPE_E(int value) {
            this.value = value;
        }

        /**
         * 根据整形值获得枚举类型
         *
         * @param iValue 方法参数：iValue
         * @return TIER1_ANALYSIS_DISK_TYPE_E 返回结果
         */
        public static TIER1_ANALYSIS_DISK_TYPE_E valueOf(int iValue) {
            for (TIER1_ANALYSIS_DISK_TYPE_E value : TIER1_ANALYSIS_DISK_TYPE_E.values()) {
                if (value.value == iValue) {
                    return value;
                }
            }
            throw new IllegalArgumentException(
                    "wrong TIER1_ANALYSIS_DISK_TYPE_E value:" + iValue);
        }

        /**
         * 取得枚举的整型值
         *
         * @return int 返回结果
         */
        public int getValue() {
            return this.value;
        }

        /**
         * <该方法返回枚举对应的描述>
         *
         * @return String 返回结果
         */
        @Override
        public String toString() {
            return "EnumDefine." + this.getClass().getSimpleName() + '.' + this.name();
        }

    }

    /**
     * TIER2_ANALYSIS_DISK_TYPE_E
     */
    public static enum TIER2_ANALYSIS_DISK_TYPE_E {
        /**
         * 1T 近线SAS盘
         */
        NLSAS_1T_7200(3),
        /**
         * 2T 近线SAS盘
         */
        NLSAS_2T_7200(4);

        private int value;

        /**
         * 构造函数
         */
        private TIER2_ANALYSIS_DISK_TYPE_E(int value) {
            this.value = value;
        }

        /**
         * 根据整形值获得枚举类型
         *
         * @param iValue 方法参数：iValue
         * @return TIER2_ANALYSIS_DISK_TYPE_E 返回结果
         */
        public static TIER2_ANALYSIS_DISK_TYPE_E valueOf(int iValue) {
            for (TIER2_ANALYSIS_DISK_TYPE_E value : TIER2_ANALYSIS_DISK_TYPE_E.values()) {
                if (value.value == iValue) {
                    return value;
                }
            }
            throw new IllegalArgumentException(
                    "wrong TIER2_ANALYSIS_DISK_TYPE_E value:" + iValue);
        }

        /**
         * 取得枚举的整型值
         *
         * @return int 返回结果
         */
        public int getValue() {
            return this.value;
        }

        /**
         * <该方法返回枚举对应的描述>
         *
         * @return String 返回结果
         */
        @Override
        public String toString() {
            return "EnumDefine." + this.getClass().getSimpleName() + '.' + this.name();
        }

    }

    /**
     * SNAPSHOT_STATUS_E
     */
    public static enum SNAPSHOT_STATUS_E {
        /**
         * 停用
         */
        SNAPSHOT_STATUS_DISABLE(1),
        /**
         * 激活
         */
        SNAPSHOT_STATUS_ACTIVE(2),
        /**
         * 回滚
         */
        SNAPSHOT_STATUS_ROLLBACK(3),
        /**
         * 错误
         */
        SNAPSHOT_STATUS_ERROR(4);

        private int value;

        /**
         * 构造函数
         */
        private SNAPSHOT_STATUS_E(int value) {
            this.value = value;
        }

        /**
         * 根据整形值获得枚举类型
         *
         * @param iValue 方法参数：iValue
         * @return SNAPSHOT_STATUS_E 返回结果
         */
        public static SNAPSHOT_STATUS_E valueOf(int iValue) {
            for (SNAPSHOT_STATUS_E value : SNAPSHOT_STATUS_E.values()) {
                if (value.value == iValue) {
                    return value;
                }
            }
            throw new IllegalArgumentException("wrong SNAPSHOT_STATUS_E value:"
                    + iValue);
        }

        /**
         * 取得枚举的整型值
         *
         * @return int 返回结果
         */
        public int getValue() {
            return this.value;
        }

        /**
         * <该方法返回枚举对应的描述>
         *
         * @return String 返回结果
         */
        @Override
        public String toString() {
            return "EnumDefine." + this.getClass().getSimpleName() + '.' + this.name();
        }

    }

    /**
     * SNAPSHOT_HEALTH_STATUS_E
     */
    public static enum SNAPSHOT_HEALTH_STATUS_E {
        /**
         * 正常
         */
        SNAPSHOT_NORMAL(1),
        /**
         * 故障
         */
        SNAPSHOT_FAULT(2);

        private int value;

        /**
         * 构造函数
         */
        private SNAPSHOT_HEALTH_STATUS_E(int value) {
            this.value = value;
        }

        /**
         * 根据整形值获得枚举类型
         *
         * @param iValue 方法参数：iValue
         * @return SNAPSHOT_HEALTH_STATUS_E 返回结果
         */
        public static SNAPSHOT_HEALTH_STATUS_E valueOf(int iValue) {
            for (SNAPSHOT_HEALTH_STATUS_E value : SNAPSHOT_HEALTH_STATUS_E.values()) {
                if (value.value == iValue) {
                    return value;
                }
            }
            throw new IllegalArgumentException(
                    "wrong SNAPSHOT_HEALTH_STATUS_E value:" + iValue);
        }

        /**
         * 取得枚举的整型值
         *
         * @return int 返回结果
         */
        public int getValue() {
            return this.value;
        }

        /**
         * <该方法返回枚举对应的描述>
         *
         * @return String 返回结果
         */
        @Override
        public String toString() {
            return "EnumDefine." + this.getClass().getSimpleName() + '.' + this.name();
        }

    }

    /**
     * TRAP_VERSION_E
     */
    public static enum TRAP_VERSION_E {
        /**
         * V1 Trap
         */
        TRAP_V1(1),
        /**
         * V2c Trap
         */
        TRAP_V2C(2),
        /**
         * V3 Trap
         */
        TRAP_V3(3);

        private int value;

        /**
         * 构造函数
         */
        private TRAP_VERSION_E(int value) {
            this.value = value;
        }

        /**
         * 根据整形值获得枚举类型
         *
         * @param iValue 方法参数：iValue
         * @return TRAP_VERSION_E 返回结果
         */
        public static TRAP_VERSION_E valueOf(int iValue) {
            for (TRAP_VERSION_E value : TRAP_VERSION_E.values()) {
                if (value.value == iValue) {
                    return value;
                }
            }
            throw new IllegalArgumentException("wrong TRAP_VERSION_E value:"
                    + iValue);
        }

        /**
         * 取得枚举的整型值
         *
         * @return int 返回结果
         */
        public int getValue() {
            return this.value;
        }

        /**
         * <该方法返回枚举对应的描述>
         *
         * @return String 返回结果
         */
        @Override
        public String toString() {
            return "EnumDefine." + this.getClass().getSimpleName() + '.' + this.name();
        }

    }

    /**
     * TRAP_TYPE_E
     */
    public static enum TRAP_TYPE_E {

        TRAP1(1),

        TRAP2(2),
        /**
         * All Trap
         */
        TRAP_ALL(3);

        private int value;

        /**
         * 构造函数
         */
        private TRAP_TYPE_E(int value) {
            this.value = value;
        }

        /**
         * 根据整形值获得枚举类型
         *
         * @param iValue 方法参数：iValue
         * @return TRAP_TYPE_E 返回结果
         */
        public static TRAP_TYPE_E valueOf(int iValue) {
            for (TRAP_TYPE_E value : TRAP_TYPE_E.values()) {
                if (value.value == iValue) {
                    return value;
                }
            }
            throw new IllegalArgumentException("wrong TRAP_TYPE_E value:"
                    + iValue);
        }

        /**
         * 取得枚举的整型值
         *
         * @return int 返回结果
         */
        public int getValue() {
            return this.value;
        }

        /**
         * <该方法返回枚举对应的描述>
         *
         * @return String 返回结果
         */
        @Override
        public String toString() {
            return "EnumDefine." + this.getClass().getSimpleName() + '.' + this.name();
        }

    }

    /**
     * LDAP_MAP_TYPE_E
     */
    public static enum LDAP_MAP_TYPE_E {
        /**
         * LDAP用户映射
         */
        USER_MAP(0),
        /**
         * LDAP组映射
         */
        GROUP_MAP(1);

        private int value;

        /**
         * 构造函数
         */
        private LDAP_MAP_TYPE_E(int value) {
            this.value = value;
        }

        /**
         * 根据整形值获得枚举类型
         *
         * @param iValue 方法参数：iValue
         * @return LDAP_MAP_TYPE_E 返回结果
         */
        public static LDAP_MAP_TYPE_E valueOf(int iValue) {
            for (LDAP_MAP_TYPE_E value : LDAP_MAP_TYPE_E.values()) {
                if (value.value == iValue) {
                    return value;
                }
            }
            throw new IllegalArgumentException("wrong LDAP_MAP_TYPE_E value:"
                    + iValue);
        }

        /**
         * 取得枚举的整型值
         *
         * @return int 返回结果
         */
        public int getValue() {
            return this.value;
        }

        /**
         * <该方法返回枚举对应的描述>
         *
         * @return String 返回结果
         */
        @Override
        public String toString() {
            return "EnumDefine." + this.getClass().getSimpleName() + '.' + this.name();
        }

    }

    /**
     * LDAP_TRANSFER_E
     */
    public static enum LDAP_TRANSFER_E {
        /**
         * LDAP
         */
        LDAP(1),
        /**
         * LDAPS
         */
        LDAPS(2);

        private int value;

        /**
         * 构造函数
         */
        private LDAP_TRANSFER_E(int value) {
            this.value = value;
        }

        /**
         * 根据整形值获得枚举类型
         *
         * @param iValue 方法参数：iValue
         * @return LDAP_TRANSFER_E 返回结果
         */
        public static LDAP_TRANSFER_E valueOf(int iValue) {
            for (LDAP_TRANSFER_E value : LDAP_TRANSFER_E.values()) {
                if (value.value == iValue) {
                    return value;
                }
            }
            throw new IllegalArgumentException("wrong LDAP_TRANSFER_E value:"
                    + iValue);
        }

        /**
         * 取得枚举的整型值
         *
         * @return int 返回结果
         */
        public int getValue() {
            return this.value;
        }

        /**
         * <该方法返回枚举对应的描述>
         *
         * @return String 返回结果
         */
        @Override
        public String toString() {
            return "EnumDefine." + this.getClass().getSimpleName() + '.' + this.name();
        }

    }

    /**
     * 设备类型
     */
    public static enum LUNCOPY_DEVICE_TYPE_E {
        HAWEI_DEVICE(1),
        /**
         * 其他设备
         */
        OTHER_DEVICE(2);

        private int value;

        /**
         * 构造函数
         */
        private LUNCOPY_DEVICE_TYPE_E(int value) {
            this.value = value;
        }

        /**
         * 根据整形值获得枚举类型
         *
         * @param iValue 方法参数：iValue
         * @return LUNCOPY_DEVICE_TYPE_E 返回结果
         */
        public static LUNCOPY_DEVICE_TYPE_E valueOf(int iValue) {
            for (LUNCOPY_DEVICE_TYPE_E value : LUNCOPY_DEVICE_TYPE_E.values()) {
                if (value.value == iValue) {
                    return value;
                }
            }
            throw new IllegalArgumentException(
                    "wrong LUNCOPY_DEVICE_TYPE_E value:" + iValue);
        }

        /**
         * 取得枚举的整型值
         *
         * @return int 返回结果
         */
        public int getValue() {
            return this.value;
        }

        /**
         * <该方法返回枚举对应的描述>
         *
         * @return String 返回结果
         */
        @Override
        public String toString() {
            return "EnumDefine." + this.getClass().getSimpleName() + '.' + this.name();
        }

    }

    /**
     * LDAP_DIRECTORY_TYPE_E
     */
    public static enum LDAP_DIRECTORY_TYPE_E {
        /**
         * LDAP
         */
        LDAP(1),
        /**
         * AD
         */
        AD(2);

        private int value;

        /**
         * 构造函数
         */
        private LDAP_DIRECTORY_TYPE_E(int value) {
            this.value = value;
        }

        /**
         * 根据整形值获得枚举类型
         *
         * @param iValue 方法参数：iValue
         * @return LDAP_DIRECTORY_TYPE_E 返回结果
         */
        public static LDAP_DIRECTORY_TYPE_E valueOf(int iValue) {
            for (LDAP_DIRECTORY_TYPE_E value : LDAP_DIRECTORY_TYPE_E.values()) {
                if (value.value == iValue) {
                    return value;
                }
            }
            throw new IllegalArgumentException(
                    "wrong LDAP_DIRECTORY_TYPE_E value:" + iValue);
        }

        /**
         * 取得枚举的整型值
         *
         * @return int 返回结果
         */
        public int getValue() {
            return this.value;
        }

        /**
         * <该方法返回枚举对应的描述>
         *
         * @return String 返回结果
         */
        @Override
        public String toString() {
            return "EnumDefine." + this.getClass().getSimpleName() + '.' + this.name();
        }

    }

    /**
     * ENUM_ROUTE_TYPE_E
     */
    public static enum ENUM_ROUTE_TYPE_E {
        /** 枚举变量 */
        /**
         * 目标网段
         */
        UNKNOWN(-1),
        /** 枚举变量 */
        /**
         * 目标网段
         */
        ROUTE_TYPE_NET(0),
        /**
         * 目标IP
         */
        ROUTE_TYPE_HOST(1),
        /**
         * 默认网关
         */
        ROUTE_TYPE_DEFAULT(2);

        private int value;

        /**
         * 构造函数
         */
        private ENUM_ROUTE_TYPE_E(int value) {
            this.value = value;
        }

        /**
         * 根据整形值获得枚举类型
         *
         * @param iValue 方法参数：iValue
         * @return ENUM_ROUTE_TYPE_E 返回结果
         */
        public static ENUM_ROUTE_TYPE_E valueOf(int iValue) {
            for (ENUM_ROUTE_TYPE_E value : ENUM_ROUTE_TYPE_E.values()) {
                if (value.value == iValue) {
                    return value;
                }
            }
            return UNKNOWN;
            //throw new IllegalArgumentException("wrong ENUM_ROUTE_TYPE_E value:" + iValue);
        }

        /**
         * 取得枚举的整型值
         *
         * @return int 返回结果
         */
        public int getValue() {
            return this.value;
        }

        /**
         * <该方法返回枚举对应的描述>
         *
         * @return String 返回结果
         */
        @Override
        public String toString() {
            return "EnumDefine." + this.getClass().getSimpleName() + '.' + this.name();
        }

    }

    /**
     * LDAP_DIRECTORY_TYPE_E
     */
    public static enum CREATE_USER_TYPE_E {
        /**
         * 本地用户
         */
        LOCAL_USER(1),
        /**
         * 域用户
         */
        LDAP_SINGLE_USER(2),
        /**
         * 域用户组
         */
        LDAP_GROUP_USER(3);

        private int value;

        /**
         * 构造函数
         */
        private CREATE_USER_TYPE_E(int value) {
            this.value = value;
        }

        /**
         * 根据整形值获得枚举类型
         *
         * @param iValue 方法参数：iValue
         * @return CREATE_USER_TYPE_E 返回结果
         */
        public static CREATE_USER_TYPE_E valueOf(int iValue) {
            for (CREATE_USER_TYPE_E value : CREATE_USER_TYPE_E.values()) {
                if (value.value == iValue) {
                    return value;
                }
            }
            throw new IllegalArgumentException(
                    "wrong CREATE_USER_TYPE_E value:" + iValue);
        }

        /**
         * 取得枚举的整型值
         *
         * @return int 返回结果
         */
        public int getValue() {
            return this.value;
        }

        /**
         * <该方法返回枚举对应的描述>
         *
         * @return String 返回结果
         */
        @Override
        public String toString() {
            return "EnumDefine." + this.getClass().getSimpleName() + '.' + this.name();
        }

    }

    /**
     * SCHEDULE_TYPE_E
     */
    public static enum SCHEDULE_TYPE_E {
        /**
         * IO监控时刻表
         */
        MONITOR_SCHEDULE(1),
        /**
         * 迁移时刻表
         */
        MIGRATION_SCHEDULE(2),
        /**
         * 休眠时刻表
         */
        SPINDOWN_SCHEDULE(3);

        private int value;

        /**
         * 构造函数
         */
        private SCHEDULE_TYPE_E(int value) {
            this.value = value;
        }

        /**
         * 根据整形值获得枚举类型
         *
         * @param iValue 方法参数：iValue
         * @return SCHEDULE_TYPE_E 返回结果
         */
        public static SCHEDULE_TYPE_E valueOf(int iValue) {
            for (SCHEDULE_TYPE_E value : SCHEDULE_TYPE_E.values()) {
                if (value.value == iValue) {
                    return value;
                }
            }
            throw new IllegalArgumentException("wrong SCHEDULE_TYPE_E value:"
                    + iValue);
        }

        /**
         * 取得枚举的整型值
         *
         * @return int 返回结果
         */
        public int getValue() {
            return this.value;
        }

        /**
         * <该方法返回枚举对应的描述>
         *
         * @return String 返回结果
         */
        @Override
        public String toString() {
            return "EnumDefine." + this.getClass().getSimpleName() + '.' + this.name();
        }

    }

    /**
     * TRIGGER_MODE_E
     * <p>
     * Pool 休眠触发模式
     */
    public static enum TRIGGER_MODE_E {
        /**
         * 自动
         */
        AUTO(1),
        /**
         * 定时
         */
        SCHEDULE(2),
        /**
         * 手动
         */
        MANUAL(3);

        private int value;

        /**
         * 构造函数
         */
        private TRIGGER_MODE_E(int value) {
            this.value = value;
        }

        /**
         * 根据整形值获得枚举类型
         *
         * @param iValue 方法参数：iValue
         * @return SCHEDULE_TYPE_E 返回结果
         */
        public static TRIGGER_MODE_E valueOf(int iValue) {
            for (TRIGGER_MODE_E value : TRIGGER_MODE_E.values()) {
                if (value.value == iValue) {
                    return value;
                }
            }
            throw new IllegalArgumentException("wrong TRIGGER_MODE_E value:"
                    + iValue);
        }

        /**
         * 取得枚举的整型值
         *
         * @return int 返回结果
         */
        public int getValue() {
            return this.value;
        }

        /**
         * <该方法返回枚举对应的描述>
         *
         * @return String 返回结果
         */
        @Override
        public String toString() {
            return "EnumDefine." + this.getClass().getSimpleName() + '.' + this.name();
        }

    }

    /**
     * MULTIPATH_TYPE_E
     */
    public static enum MULTIPATH_TYPE_E {
        /**
         * 默认
         */
        DEFAULT(0),
        /**
         * ALUA
         */
        ALUA(1);

        private int value;

        /**
         * 构造函数
         */
        private MULTIPATH_TYPE_E(int value) {
            this.value = value;
        }

        /**
         * 根据整形值获得枚举类型
         *
         * @param iValue 方法参数：iValue
         * @return MULTIPATH_TYPE_E 返回结果
         */
        public static MULTIPATH_TYPE_E valueOf(int iValue) {
            for (MULTIPATH_TYPE_E value : MULTIPATH_TYPE_E.values()) {
                if (value.value == iValue) {
                    return value;
                }
            }
            throw new IllegalArgumentException("wrong MULTIPATH_TYPE_E value:"
                    + iValue);
        }

        /**
         * 取得枚举的整型值
         *
         * @return int 返回结果
         */
        public int getValue() {
            return this.value;
        }

        /**
         * <该方法返回枚举对应的描述>
         *
         * @return String 返回结果
         */
        @Override
        public String toString() {
            return "EnumDefine." + this.getClass().getSimpleName() + '.' + this.name();
        }

    }

    /**
     * STORAGE_CAPABILITY
     */
    public static enum STORAGE_CAPABILITY {
        /**
         * No Raid Protection
         */
        NoProtected(0),
        /**
         * Raid Protected;uses capacity oriented drives such as SATA or SAS
         */
        Capacity(1),
        /**
         * Raid-Protected;uses Fibres Channel or high end SAS drives
         */
        Performance(2),
        /**
         * Raid-Protected;uses Solid State drives
         */
        ExtremePerformance(3),
        /**
         * Raid-Protected;uses multipe tiers of drives
         */
        Muti_Tiers(4);

        private int value;

        /**
         * 构造函数
         */
        private STORAGE_CAPABILITY(int value) {
            this.value = value;
        }

        /**
         * 根据整形值获得枚举类型
         *
         * @param iValue 方法参数：iValue
         * @return STORAGE_CAPABILITY 返回结果
         */
        public static STORAGE_CAPABILITY valueOf(int iValue) {
            for (STORAGE_CAPABILITY value : STORAGE_CAPABILITY.values()) {
                if (value.value == iValue) {
                    return value;
                }
            }
            throw new IllegalArgumentException(
                    "wrong STORAGE_CAPABILITY value:" + iValue);
        }

        /**
         * 取得枚举的整型值
         *
         * @return int 返回结果
         */
        public int getValue() {
            return this.value;
        }

        /**
         * <该方法返回枚举对应的描述>
         *
         * @return String 返回结果
         */
        @Override
        public String toString() {
            return "EnumDefine." + this.getClass().getSimpleName() + '.' + this.name();
        }

    }

    //begin h90005710 新增需求 LUN 增加LUN优先级设置 

    /**
     * LUN 优先级枚举
     */
    public static enum LUN_PRIORITY_TYPE_E {
        /**
         * 低 默认
         */
        LOW(1),
        /**
         * 中
         */
        MIDDLE(2),
        /**
         * 高
         */
        HIGHT(3);

        private int value;

        /**
         * 构造函数
         */
        private LUN_PRIORITY_TYPE_E(int value) {
            this.value = value;
        }

        /**
         * 根据整形值获得枚举类型
         *
         * @param iValue 方法参数：iValue
         * @return LUN_PRIORITY_TYPE_E 返回结果
         */
        public static LUN_PRIORITY_TYPE_E valueOf(int iValue) {
            for (LUN_PRIORITY_TYPE_E value : LUN_PRIORITY_TYPE_E.values()) {
                if (value.value == iValue) {
                    return value;
                }
            }
            throw new IllegalArgumentException(
                    "wrong LUN_PRIORITY_TYPE_E value:" + iValue);
        }

        /**
         * 取得枚举的整型值
         *
         * @return int 返回结果
         */
        public int getValue() {
            return this.value;
        }

        /**
         * <该方法返回枚举对应的描述>
         *
         * @return String 返回结果
         */
        @Override
        public String toString() {
            return "EnumDefine." + this.getClass().getSimpleName() + '.' + this.name();
        }

    }

    //end h90005710 新增需求 LUN 增加LUN优先级设置

    /**
     * SYSTEM_ROLE_E
     */
    public static enum SYSTEM_ROLE_E {
        /**
         * 正常
         */
        SYSTEM_ROLE_NORMAL(0),
        /**
         * 主端
         */
        SYSTEM_ROLE_MASTER(1),
        /**
         * 从端
         */
        SYSTEM_ROLE_SLAVE(2);

        private int value;

        /**
         * 构造函数
         */
        private SYSTEM_ROLE_E(int value) {
            this.value = value;
        }

        /**
         * 根据整形值获得枚举类型
         *
         * @param iValue 方法参数：iValue
         * @return SYSTEM_ROLE_E 返回结果
         */
        public static SYSTEM_ROLE_E valueOf(int iValue) {
            for (SYSTEM_ROLE_E value : SYSTEM_ROLE_E.values()) {
                if (value.value == iValue) {
                    return value;
                }
            }
            throw new IllegalArgumentException("wrong SYSTEM_ROLE_E value:"
                    + iValue);
        }

        /**
         * 取得枚举的整型值
         *
         * @return int 返回结果
         */
        public int getValue() {
            return this.value;
        }

        /**
         * <该方法返回枚举对应的描述>
         *
         * @return String 返回结果
         */
        @Override
        public String toString() {
            return "EnumDefine." + this.getClass().getSimpleName() + '.' + this.name();
        }

    }

    /**
     * PRIORITY_E
     */
    public static enum PRIORITY_E {
        /**
         * 低
         */
        LOW(1),
        /**
         * 中
         */
        MIDDLE(2),
        /**
         * 高
         */
        HIGH(3);

        private int value;

        /**
         * 构造函数
         */
        private PRIORITY_E(int value) {
            this.value = value;
        }

        /**
         * 根据整形值获得枚举类型
         *
         * @param iValue 方法参数：iValue
         * @return PRIORITY_E 返回结果
         */
        public static PRIORITY_E valueOf(int iValue) {
            for (PRIORITY_E value : PRIORITY_E.values()) {
                if (value.value == iValue) {
                    return value;
                }
            }
            throw new IllegalArgumentException("wrong PRIORITY_E value:"
                    + iValue);
        }

        /**
         * 取得枚举的整型值
         *
         * @return int 返回结果
         */
        public int getValue() {
            return this.value;
        }

        /**
         * <该方法返回枚举对应的描述>
         *
         * @return String 返回结果
         */
        @Override
        public String toString() {
            return "EnumDefine." + this.getClass().getSimpleName() + '.' + this.name();
        }

    }

    /**
     * DISK_SSD_IF_TYPE_E
     */
    public static enum DISK_SSD_IF_TYPE_E {
        /**
         * Not Available
         */
        NA(0),
        /**
         * FC SSD
         */
        FC_SSD(1),
        /**
         * SAS SSD
         */
        SAS_SSD(2),
        /**
         * NL SAS SSD
         */
        NL_SAS_SSD(3),
        /**
         * SATA SSD
         */
        SATA_SSD(4),
        /**
         * SATA2 SSD
         */
        SATA2_SSD(5),
        /**
         * SATA3 SSD
         */
        SATA3_SSD(6);

        private int value;

        /**
         * 构造函数
         */
        private DISK_SSD_IF_TYPE_E(int value) {
            this.value = value;
        }

        /**
         * 根据整形值获得枚举类型
         *
         * @param iValue 方法参数：iValue
         * @return DISK_SSD_IF_TYPE_E 返回结果
         */
        public static DISK_SSD_IF_TYPE_E valueOf(int iValue) {
            for (DISK_SSD_IF_TYPE_E value : DISK_SSD_IF_TYPE_E.values()) {
                if (value.value == iValue) {
                    return value;
                }
            }
            throw new IllegalArgumentException(
                    "wrong DISK_SSD_IF_TYPE_E value:" + iValue);
        }

        /**
         * 取得枚举的整型值
         *
         * @return int 返回结果
         */
        public int getValue() {
            return this.value;
        }

        /**
         * <该方法返回枚举对应的描述>
         *
         * @return String 返回结果
         */
        @Override
        public String toString() {
            return "EnumDefine." + this.getClass().getSimpleName() + '.' + this.name();
        }

    }

    /**
     * BAD_DISK_TYPY_E
     */
    public static enum BAD_DISK_TYPY_E {
        /**
         * 注册失败
         */
        REGISTER_FAILED(1),
        /**
         * 写保护
         */
        WRITE_PROTECT(2),
        /**
         * 慢盘
         */
        SLOW_DISK(3),
        /**
         * 故障盘
         */
        FAULT_DISK(4);

        private int value;

        /**
         * 构造函数
         */
        private BAD_DISK_TYPY_E(int value) {
            this.value = value;
        }

        /**
         * 根据整形值获得枚举类型
         *
         * @param iValue 方法参数：iValue
         * @return BAD_DISK_TYPY_E 返回结果
         */
        public static BAD_DISK_TYPY_E valueOf(int iValue) {
            for (BAD_DISK_TYPY_E value : BAD_DISK_TYPY_E.values()) {
                if (value.value == iValue) {
                    return value;
                }
            }
            throw new IllegalArgumentException("wrong BAD_DISK_TYPY_E value:"
                    + iValue);
        }

        /**
         * 取得枚举的整型值
         *
         * @return int 返回结果
         */
        public int getValue() {
            return this.value;
        }

        /**
         * <该方法返回枚举对应的描述>
         *
         * @return String 返回结果
         */
        @Override
        public String toString() {
            return "EnumDefine." + this.getClass().getSimpleName() + '.' + this.name();
        }

    }

    /**
     * DISK_FORM_E
     */
    public static enum DISK_FORM_E {
        /**
         * 未知
         */
        Unknown(0),
        /**
         * 5.25英寸
         */
        DISK_5_25_INCH(1),
        /**
         * 3.5英寸
         */
        DISK_3_5_INCH(2),
        /**
         * 2.5英寸
         */
        DISK_2_5_INCH(3),
        /**
         * 1.8英寸
         */
        DISK_1_8_INCH(4);

        private int value;

        /**
         * 构造函数
         */
        private DISK_FORM_E(int value) {
            this.value = value;
        }

        /**
         * 根据整形值获得枚举类型
         *
         * @param iValue 方法参数：iValue
         * @return DISK_FORM_E 返回结果
         */
        public static DISK_FORM_E valueOf(int iValue) {
            for (DISK_FORM_E value : DISK_FORM_E.values()) {
                if (value.value == iValue) {
                    return value;
                }
            }
            throw new IllegalArgumentException("wrong DISK_FORM_E value:"
                    + iValue);
        }

        /**
         * 取得枚举的整型值
         *
         * @return int 返回结果
         */
        public int getValue() {
            return this.value;
        }

        /**
         * <该方法返回枚举对应的描述>
         *
         * @return String 返回结果
         */
        @Override
        public String toString() {
            return "EnumDefine." + this.getClass().getSimpleName() + '.' + this.name();
        }

    }

    /**
     * SYS_STATUS_E
     */
    public static enum SYS_STATUS_E {
        /**
         * 正常
         */
        Normal(0),
        /**
         * 异常
         */
        Abnormal(1),
        /**
         * 上电中
         */
        POWERONING(2),
        /**
         * 下电中
         */
        POWEROFF(3),
        /**
         * 安全保护状态
         */
        SAFE_MODE(4),
        /**
         * 升级中
         */
        UPGRADING(5),
        /**
         * 掉电
         */
        POWER_LOST(6),
        /**
         * 离线
         */
        OFFLINE(7);

        private int value;

        /**
         * 构造函数
         */
        private SYS_STATUS_E(int value) {
            this.value = value;
        }

        /**
         * 根据整形值获得枚举类型
         *
         * @param iValue 方法参数：iValue
         * @return SYS_STATUS_E 返回结果
         */
        public static SYS_STATUS_E valueOf(int iValue) {
            for (SYS_STATUS_E value : SYS_STATUS_E.values()) {
                if (value.value == iValue) {
                    return value;
                }
            }
            throw new IllegalArgumentException("wrong SYS_STATUS_E value:"
                    + iValue);
        }

        /**
         * 取得枚举的整型值
         *
         * @return int 返回结果
         */
        public int getValue() {
            return this.value;
        }

        /**
         * <该方法返回枚举对应的描述>
         *
         * @return String 返回结果
         */
        @Override
        public String toString() {
            return "EnumDefine." + this.getClass().getSimpleName() + '.' + this.name();
        }

    }

    /**
     * FILE_ID_E
     */
    public static enum FILE_ID_E {
        /**
         * 事件资源文件(英文)
         */
        event_en(0),
        /**
         * 事件定义文件(中文)
         */
        event_zh(1),
        /**
         * 事件转储文件
         */
        event_restore_file(10),
        /**
         * 错误码资源文件(英文)
         */
        error_en(20),
        /**
         * 错误码资源文件(中文)
         */
        error_zh(21),
        /**
         * 运行任务资源文件(英文)
         */
        task_en(30),
        /**
         * 运行任务资源文件(中文)
         */
        task_zh(31),
        /**
         * license文件
         */
        license(40),
        /**
         * 配置数据文件
         */
        configData(50),
        /**
         * 系统日志文件
         */
        systemLog(60),
        /**
         * 运行数据文件
         */
        runningData(70),
        /**
         * 性能文件
         */
        performanceData(80);

        private int value;

        /**
         * 构造函数
         */
        private FILE_ID_E(int value) {
            this.value = value;
        }

        /**
         * 根据整形值获得枚举类型
         *
         * @param iValue 方法参数：iValue
         * @return FILE_ID_E 返回结果
         */
        public static FILE_ID_E valueOf(int iValue) {
            for (FILE_ID_E value : FILE_ID_E.values()) {
                if (value.value == iValue) {
                    return value;
                }
            }
            throw new IllegalArgumentException("wrong FILE_ID_E value:"
                    + iValue);
        }

        /**
         * 取得枚举的整型值
         *
         * @return int 返回结果
         */
        public int getValue() {
            return this.value;
        }

        /**
         * <该方法返回枚举对应的描述>
         *
         * @return String 返回结果
         */
        @Override
        public String toString() {
            return "EnumDefine." + this.getClass().getSimpleName() + '.' + this.name();
        }

    }

    /**
     * Scale out NAS新增
     */
    public static enum SCALE_OUT_NAS {
        /**
         * 枚举变量
         */
        SNAS_SYSTEM(16384),
        /**
         * 枚举变量
         */
        SNAS_NODE(16385),
        /**
         * 枚举变量
         */
        PROTOCOL_CLUSTER(16386),
        /**
         * 枚举变量
         */
        PROTOCOL_CLUSTER_NODE(16387),
        /**
         * 枚举变量
         */
        PROTOCOL_CLUSTER_ZONE(16388),
        /**
         * 枚举变量
         */
        SNAS_IP_POOL(16389),
        /**
         * 枚举变量
         */
        SNAS_CLIENT(16390),
        /**
         * 枚举变量
         */
        SNAS_DNS_CONFIG(16391),
        /**
         * 枚举变量
         */
        SNAS_LOCAL_USER(16392),
        /**
         * 枚举变量
         */
        SNAS_LOCAL_GROUP(16393),
        /**
         * 枚举变量
         */
        SNAS_USERANDGROUP(16394),
        /**
         * 枚举变量
         */
        SNAS_IDMAP_CONFIG(16395),
        /**
         * 枚举变量
         */
        SNAS_ROOT_USER(16396),
        /**
         * 枚举变量
         */
        SNAS_NODE_FS_SERV(16397),
        /**
         * 枚举变量
         */
        SNAS_NODE_SHARE_SERV(16398),
        /**
         * 枚举变量
         */
        SNAS_CIFS_SERVICE(16399),
        /**
         * 枚举变量
         */
        SNAS_FILE(16400),
        /**
         * 枚举变量
         */
        SNAS_NFS_SHARE(16401),
        /**
         * 枚举变量
         */
        SNAS_CIFS_SHARE(16402),
        /**
         * 枚举变量
         */
        SNAS_USER_QUOTA(16403),
        /**
         * 枚举变量
         */
        SNAS_CIFS_SHARE_AUTH_CLIENT(16404),
        /**
         * 枚举变量
         */
        DATA_RECOVER_SCHEDULE(16405),
        /**
         * 枚举变量
         */
        SNAS_TASK(16406),
        /**
         * 枚举变量
         */
        SNAS_KEY_VALUE_STORE(16407),
        /**
         * 枚举变量
         */
        SNAS_FILE_QUOTA(16408),
        /**
         * 枚举变量
         */
        SNAS_NFS_SHARE_AUTH_CLIENT(16409),
        /**
         * 枚举变量
         */
        SNAS_ACL_STRATEGY(16410),
        /**
         * 枚举变量
         */
        SNAS_LOCAL_AUTH(16411),
        /**
         * 枚举变量
         */
        SNAS_NIS_CONFIG(16412),
        /**
         * 枚举变量
         */
        SNAS_LDAP_CONFIG(16413),
        /**
         * 枚举变量
         */
        SNAS_AD_CONFIG(16414),
        /**
         * 枚举变量
         */
        SNAS_SYS_WATERLEVEL(16415),
        /**
         * 枚举变量
         */
        SNAS_SYS_ENERGY_SAVING(16416),
        /**
         * 枚举变量
         */
        SNAS_ALARM_VOICE(16417),
        /**
         * 枚举变量
         */
        SNAS_SECURITY_POLICY(16418),
        /**
         * 枚举变量
         */
        SNAS_HEARTBEAT(16419),
        /**
         * 枚举变量
         */
        SNAS_HANDSHAKE(16420),
        /**
         * 枚举变量
         */
        SNAS_MGR_NODE(16421),
        /**
         * 枚举变量
         */
        SNAS_NODE_PERFORMANCE(16422),
        /**
         * 枚举变量
         */
        SNAS_SECURITY_SMTP(16423);

        private int value;

        /**
         * 构造函数
         */
        private SCALE_OUT_NAS(int value) {
            this.value = value;
        }

        /**
         * 根据整形值获得枚举类型
         *
         * @param iValue 方法参数：iValue
         * @return SCALE_OUT_NAS 返回结果
         */
        public static SCALE_OUT_NAS valueOf(int iValue) {
            for (SCALE_OUT_NAS value : SCALE_OUT_NAS.values()) {
                if (value.value == iValue) {
                    return value;
                }
            }
            throw new IllegalArgumentException("wrong LUN_ALLOC_TYPE_E value:"
                    + iValue);
        }

        /**
         * 取得枚举的整型值
         *
         * @return int 返回结果
         */
        public int getValue() {
            return this.value;
        }

        /**
         * <该方法返回枚举对应的描述>
         *
         * @return String 返回结果
         */
        @Override
        public String toString() {
            return "EnumDefine." + this.getClass().getSimpleName() + '.' + this.name();
        }

    }

    /**
     * UNIT_E
     */
    public static enum UNIT_E {
        /**
         * 默认为个
         */
        UNIT(0),
        /**
         * Byte
         */
        UNIT_BYTE(1),
        /**
         * KB
         */
        UNIT_KB(2),
        /**
         * MB
         */
        UNIT_MB(3),
        /**
         * GB
         */
        UNIT_GB(4),
        /**
         * TB
         */
        UNIT_TB(5),
        /**
         * PB
         */
        UNIT_PB(6),
        /**
         * KiB
         */
        UNIT_KIB(11),
        /**
         * MiB
         */
        UNIT_MIB(12),
        /**
         * GiB
         */
        UNIT_GIB(13),
        /**
         * TiB
         */
        UNIT_TIB(14),

        /**
         * PiB
         */
        UNIT_PIB(15),

        /**
         * 个
         */
        UNIT_ENTRIES(72);

        private int value;

        /**
         * 构造函数
         */
        private UNIT_E(int value) {
            this.value = value;
        }

        /**
         * 根据整形值获得枚举类型
         *
         * @param iValue 方法参数：iValue
         * @return UNIT_E 返回结果
         */
        public static UNIT_E valueOf(int iValue) {
            for (UNIT_E value : UNIT_E.values()) {
                if (value.value == iValue) {
                    return value;
                }
            }
            throw new IllegalArgumentException("wrong UNIT_E value:" + iValue);
        }

        /**
         * 取得枚举的整型值
         *
         * @return int 返回结果
         */
        public int getValue() {
            return this.value;
        }

        /**
         * <该方法返回枚举对应的描述>
         *
         * @return String 返回结果
         */
        @Override
        public String toString() {
            return "EnumDefine." + this.getClass().getSimpleName() + '.' + this.name();
        }

    }

    /**
     * DESTROY_TYPE_E
     */
    public static enum DESTROY_TYPE_E {
        /**
         * DoD方式
         */
        DOD_ERASEMENT(1),
        /**
         * 自定义反复写
         */
        REP_ERASEMENT(2);

        private int value;

        /**
         * 构造函数
         */
        private DESTROY_TYPE_E(int value) {
            this.value = value;
        }

        /**
         * 根据整形值获得枚举类型
         *
         * @param iValue 方法参数：iValue
         * @return UNIT_E 返回结果
         */
        public static DESTROY_TYPE_E valueOf(int iValue) {
            for (DESTROY_TYPE_E value : DESTROY_TYPE_E.values()) {
                if (value.value == iValue) {
                    return value;
                }
            }
            throw new IllegalArgumentException("wrong DESTROY_TYPE_E value:"
                    + iValue);
        }

        /**
         * 取得枚举的整型值
         *
         * @return int 返回结果
         */
        public int getValue() {
            return this.value;
        }

        /**
         * <该方法返回枚举对应的描述>
         *
         * @return String 返回结果
         */
        @Override
        public String toString() {
            return "EnumDefine." + this.getClass().getSimpleName() + '.' + this.name();
        }

    }

    /**
     * POWERON_STATUS_E
     */
    public static enum POWERON_STATUS_E {
        /**
         * 上电成功
         */
        POWERON_SUCCESS(0),
        /**
         * 上电失败
         */
        POWERON_FAIL(1),
        /**
         * 上电进行中
         */
        POWERON_POWERONING(2),
        /**
         * 下电中
         */
        POWEROFF(3);

        private int value;

        /**
         * 构造函数
         */
        private POWERON_STATUS_E(int value) {
            this.value = value;
        }

        /**
         * 根据整形值获得枚举类型
         *
         * @param iValue 方法参数：iValue
         * @return POWERON_STATUS_E 返回结果
         */
        public static POWERON_STATUS_E valueOf(int iValue) {
            for (POWERON_STATUS_E value : POWERON_STATUS_E.values()) {
                if (value.value == iValue) {
                    return value;
                }
            }
            return null;
        }

        /**
         * 取得枚举的整型值
         *
         * @return int 返回结果
         */
        public int getValue() {
            return this.value;
        }

        /**
         * <该方法返回枚举对应的描述>
         *
         * @return String 返回结果
         */
        @Override
        public String toString() {
            return "EnumDefine." + this.getClass().getSimpleName() + '.' + this.name();
        }

    }

    /**
     * COMM_CONFIGURE_E
     */
    public static enum COMM_CONFIGURE_E {
        /**
         * DoD方式
         */
        VMWARE(1);

        private int value;

        /**
         * 构造函数
         */
        private COMM_CONFIGURE_E(int value) {
            this.value = value;
        }

        /**
         * 根据整形值获得枚举类型
         *
         * @param iValue 方法参数：iValue
         * @return UNIT_E 返回结果
         */
        public static COMM_CONFIGURE_E valueOf(int iValue) {
            for (COMM_CONFIGURE_E value : COMM_CONFIGURE_E.values()) {
                if (value.value == iValue) {
                    return value;
                }
            }
            throw new IllegalArgumentException("wrong COMM_CONFIGURE_E value:"
                    + iValue);
        }

        /**
         * 取得枚举的整型值
         *
         * @return int 返回结果
         */
        public int getValue() {
            return this.value;
        }

        /**
         * <该方法返回枚举对应的描述>
         *
         * @return String 返回结果
         */
        @Override
        public String toString() {
            return "EnumDefine." + this.getClass().getSimpleName() + '.' + this.name();
        }

    }

    /**
     * COMM_CONFIGURE_E
     */
    public static enum TRANSFER_PROTOCAL_E {
        /**
         * SFTP方式
         */
        SFTP(1),
        /**
         * FTP方式
         */
        FTP(0),
        /**
         * 返回空
         */
        NONE(-1);

        private int value;

        /**
         * 构造函数
         */
        private TRANSFER_PROTOCAL_E(int value) {
            this.value = value;
        }

        /**
         * 根据整形值获得枚举类型
         *
         * @param iValue 方法参数：iValue
         * @return UNIT_E 返回结果
         */
        public static TRANSFER_PROTOCAL_E valueOf(int iValue) {
            for (TRANSFER_PROTOCAL_E value : TRANSFER_PROTOCAL_E.values()) {
                if (value.value == iValue) {
                    return value;
                }
            }
            throw new IllegalArgumentException("wrong TRANSFER_PROTOCAL_E value:"
                    + iValue);
        }

        /**
         * 取得枚举的整型值
         *
         * @return int 返回结果
         */
        public int getValue() {
            return this.value;
        }

    }

}
