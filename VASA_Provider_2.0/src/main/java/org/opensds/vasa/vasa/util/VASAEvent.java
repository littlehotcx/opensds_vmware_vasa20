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
 * <VASA事件>
 * <功能详细描述>
 *
 * @author l90006853
 * @version [V100R006C00, 2013-5-2]
 * @see [相关类/方法]
 * @since [VIS6000T/VASA模块版本]
 */
public class VASAEvent {
    /**
     * VIS扩容LUN 注意该ID不是只是扩容LUN
     * gui_mod_volsize
     */
    public static final long VIS_RESIZE_LUN = 592705486848L;

    /**
     * VIS 扩容
     */
    public static final String GUI_MOD_VOLSIZE = "gui_mod_volsize";

    /**
     * 节点([Node-id])加入集群。
     */
    public static final long ADDCTL42952753265 = 42952753265L;

    /**
     * 节点([Node-id])离线。
     */
    public static final long DELCTL42952753215 = 42952753215L;

    /**
     * 修改IPV4地址
     */
    public static final long MODCTLIPV4 = 42956881962L;

    /**
     * 修改IPV6地址。
     */
    public static final long MODCTLIPV6 = 42956881963L;

    /**
     * 设置系统名称（[System-name]）成功。
     */
    public static final long MODNAME = 42956881950L;

    /**
     * 创建卷（ID:[Volume-id]，[Diskgroup-name]，[Volume-name]）成功。
     */
    public static final long ADDVOL = 42956881951L;

    /**
     * 删除卷（ID:[Volume-id]，[Diskgroup-name]，[Volume-name]）成功。
     */
    public static final long DELVOL = 42956881952L;

    /**
     * 修改卷（ID:[Volume-id]，[Diskgroup-name]，[Old-volume-name]）名称为[New-volume-name]。
     */
    public static final long MODVOL = 42956881953L;

    /**
     * 添加卷映射（映射类型： [Map-type]{0:主机；1:主机组}， 主机ID或者主机组ID： [HostOrGroup-id]，主机LUN ID： [Hostlun-id]，设备卷ID：
     */
    public static final long ADDMAP = 42956881954L;

    /**
     * 删除映射。
     */
    public static final long DELMAP = 42956881955L;

    /**
     * 增加FC端口
     */
    public static final long ADDFCPORT = 42956881956L;

    /**
     * 删除FC端口。
     */
    public static final long DELFCPORT = 42956881957L;

    /**
     * 修改FC端口。
     */
    public static final long MODFCPORT = 42956881960L;

    /**
     * 修改ISCSI端口。
     */
    public static final long MODISCSIPORT = 42956881961L;

    /**
     * SF事件。
     */
    public static final long SFEVENT = 592705486848L;

    /**
     * 用户登陆。
     */
    public static final long VIS_LOGIN = 47252054880L;

    /*** 用户退出。**/
    public static final long VIS_LOGIN_OUT = 47252054881L;

    /**
     * 从LUN组移除LUN
     */
    public static final long HVSC00_REMOVE_LUN_FROM_LUNGROUP = 35248813375520L;

    /**
     * 添加LUN
     */
    public static final long HVSC00_ADD_LUN = 35248813375517L;
    /**
     * XVE 用户退出登录事件
     */
    public static final long XVE_LOGOUT = 35248799350809L;
    /**
     * XVE 用户登录事件
     */
    public static final long XVE_LOGIN = 35248799350805L;

    /**
     * 切归属控制器
     */
    public static final long REBIND_EVENT = 35248797319205L;

    public static final long CHANGE_WORKING_CONTROLLER_EVENT = 35248797319220L;

    /**
     * iSCSI 修改端口速率
     * detail="##00设置网口属性（##01{0:控制框;1:硬盘框;2:引擎}
     * ##02，##03{0:SAS;1:FC;2:ISCSI;4:COMBO;8:FCoE;3:PCIe}
     * ##04{2:接口模块;3:管理模块;0:控制器;1:级联模块} ##05，
     * 端口号 ##06，最大传输速率 ##07）成功。"
     * admin:100.133.189.6设置网口属性（引擎 ENG0，ISCSI接口模块 B1，端口号 P2，最大传输速率 2048）成功。
     */
    public static final long HVSC00_MODIFY_ISCSI_PORT = 35248813899839L;

    /**
     * admin:100.133.189.6设置FC端口属性（引擎 ENG0，FC接口模块 A1，端口号 P2，
     * 延迟 --，模式 --，速率 4 Gbit/s）成功。
     * <p>
     * HVSC00 修改FC 端口
     * description="##00设置FC端口属性（##01{0:控制框;1:硬盘框;2:引擎} ##02，
     * ##03{0:SAS;1:FC;2:ISCSI;4:COMBO;8:FCoE;3:PCIe}##04{2:接口模块;3:管理模块;0:控制器;1:级联模块} ##05，
     * 端口号 ##06，延迟 ##07，模式 ##08，速率 ##09）成功。
     */


    public static final long HVSC00_MODIFY_ARRAY_LOCATION = 35248809771084L;

    /**
     * {PARAM0} succeeded in powering on the interface module
     * ({PARAM1} {PARAM2}, {PARAM3} interface module {PARAM4})
     * 系统上电接口模块（引擎 ENG0，ISCSI接口模块 B2）成功
     * ##00{1:系统}上电接口模块（##01{0:控制框;1:硬盘框;2:引擎} ##02，
     * ##03{0:SAS;1:FC;2:ISCSI;4:COMBO;8:FCoE;3:PCIe}接口模块 ##04）成功。
     */
    public static final long HVSC00_ADD_PORT = 2203063484431L;

    /**
     * The controller ({PARAM0}{PARAM1}, controller {PARAM2}) restarted unexpectedly.
     * The error code is {PARAM3}
     * 控制器（引擎 ENG0，控制器 B)重启，错误码为0x404033C7
     */
    public static final long HVSC00_RESTART_PROCESSOR = 17656624119847L;

    /**
     * The {PARAM0} {PARAM1} has restored to work in dual-controller mode
     * 引擎 ENG0恢复双控模式
     */
    public static final long HVSC00_PROCESSOR_RECOVER = 1103551332452L;

    /**
     * {PARAM0} succeeded in restarting the controller ({PARAM1}{PARAM2}, controller {PARAM3})
     * ##00启动控制器（##01{0:控制框;1:硬盘框;2:引擎} ##02，控制器 ##03{0:A;1:B})重启成功。
     */
    public static final long HVSC00_START_PROCESSOR = 35248810164257L;

    /**
     * <param eventID="1103538552846"
     * name="管理网口已连接"
     * detail="管理网口（##00{0:控制框;1:硬盘框;2:引擎} ##01，##02{2:接口模块;3:管理模块;0:控制器;1:级联模块} ##03）已连接。"
     * description="管理网口（##00{0:控制框;1:硬盘框;2:引擎} ##01，##02{2:接口模块;3:管理模块;0:控制器;1:级联模块} ##03）已连接。"
     */
    public static final long HVSC00_ADD_PROCESSOR = 1103538552846L;
}
