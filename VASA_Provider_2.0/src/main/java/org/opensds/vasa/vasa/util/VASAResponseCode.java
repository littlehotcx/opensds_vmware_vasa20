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

public interface VASAResponseCode {
    interface common {
        /*
         * 成功
         * */
        String SUCCESS = "0";
        String SUCCESS_DESC = "Successfully";

        /*
          同步错误
         */
        String SYNCERROR = "-1";
        String SYNCERROR_DESC = "sync error";

        /*
         * 失败
         */
        String ERROR = "1";
        String ERROR_DESC = "fail";

        /*
         * 参数错误
         * */
        String INVALID_PARAMETER = "10100001";
        String INVALID_PARAMETER_DESCRIPTION = "Invalid parameters";

        String INVALID_TYPE = "10100002";
        String INVALID_TYPE_DESCRIPTION = "Invalid type,NAS and SAN storageContain can not create in same VASA Provider";

    }

    interface storageManagerService {
        /*
         * 当前阵列不支持VVOL
         */
        String STORAGE_ARRAY_NOT_SUPPORT_VVOL = "10200001";
        String STORAGE_ARRAY_NOT_SUPPORT_VVOL_DESCRIPTION = "Storage array do not support VVOL";

        /*
         * 当前阵列正在被使用
         */
        String STORAGE_ARRAY_BEING_USED = "10200002";
        String STORAGE_ARRAY_BEING_USED_DESCRIPTION = "Storage array is being used";

        /*
         * 无法登陆阵列
         */
        String STORAGE_ARRAY_NOT_LOGIN = "10200003";
        String STORAGE_ARRAY_NOT_LOGIN_DESCRIPTION = "Storage array can not login";

        /*
         * 阵列无法获取system信息
         */
        String STORAGE_ARRAY_GET_SYSTEMINFO_ERROE = "10200004";
        String STORAGE_ARRAY_GET_SYSTEMINFO_ERROE_DESCRIPTION = "Storage array can not get system infomation";


        /*
         * 阵列无法获取控制器端口信息
         */
        String STORAGE_ARRAY_GET_ETHPORT_ERROR = "10200005";
        String STORAGE_ARRAY_GET_ETHPORT_ERROR_DESCRIPTION = "Storage array can not get ip port";

        /*
         * 当前阵列创建SSL连接失败，校验证书是否正确
         */
        String STORAGE_ARRAY_SSL_ERROR = "10200006";
        String STORAGE_ARRAY_SSL_ERROR_DESCRIPTION = "Create storage ssl handshake error.";

        /*
         * 修改阵列SN号与当前阵列SN号不匹配
         */
        String STORAGE_ARRAY_SN_MATCH_ERROR = "10200007";
        String STORAGE_ARRAY_SN_MATCH_ERROR_DESCRIPTION = "Update array's sn and current array's sn are not same.";

        /*
         * 当前添加的阵列已存在
         */
        String STORAGE_ARRAY_ALREADY_EXIST_ERROR = "10200009";
        String STORAGE_ARRAY_ALREADY_EXIST_ERROR_DESCRIPTION = "The storage array already exist.";

        /*
         * 阵列无法获取当前时间信息
         */
        String STORAGE_ARRAY_GET_TIME_ERROR = "10200009";
        String STORAGE_ARRAY_GET_TIME_ERROR_DESCRIPTION = "Storage array can not get currentTime";

        /*
         * 登陆阵列用户名或密码错误
         */
        String STORAGE_ARRAY_USER_PWD_ERR = "10200010";
        String STORAGE_ARRAY_USER_PWD_ERR_DESCRIPTION = "The user name or password is incorrect.";

    }

    interface storageCrtManagerService {
        String STORAGE_CRT_ALREADY_EXIST = "10600001";
        String STORAGE_CRT_ALREADY_EXIST_DESCRIPTION = "storage crt already_exist";

        String STORAGE_UNIQ_ID_ALREADY_EXIST = "10600002";
        String STORAGE_UNIQ_ID_ALREADY_EXIST_DESCRIPTION = "storage uniq id already_exist";

        String STORAGE_CRT_TYPE_ERROR = "10600003";
        String STORAGE_CRT_TYPE_ERROR_DESCRIPTION = "storage crt type error.";
    }

    interface storageContainerService {
        /*
         * 该名称的存储池已存在
         * */
        String STORAGE_CONTAINER_ALREADY_EXIST = "10300001";
        String STORAGE_CONTAINER_ALREADY_EXIST_DESCRIPTION = "Storage Container already exist";

        /*
         * 该存储池还绑定在Container上
         * */
        String STORAGE_POOL_BIND_IN_CONTAINER = "10300002";
        String STORAGE_POOL_BIND_IN_CONTAINER_DESCRIPTION = "Storage pool still bind in container";

        /*
         * 该profile还绑定在Container上
         * */
        String STORAGE_PROFILE_BIND_IN_CONTAINER = "10300003";
        String STORAGE_PROFILE_BIND_IN_CONTAINER_DESCRIPTION = "Storage profile still bind in container";

        /*
         * 当前Contaienr正在被使用，已有卷在Contaienr上
         */
        String STORAGE_CONTAINER_BEING_USED = "10300004";
        String STORAGE_CONTAINER_BEING_USED_DESCRIPTION = "Storage Container is being used";

        /*
         * StorageContainer 不存在
         */
        String STORAGE_CONTAINER_IS_NOT_EXIST = "10300005";
        String STORAGE_CONTAINER_IS_NOT_EXIST_DESCRIPTION = "Storage Container is not exist";
    }

    interface storageProfileService {
        /*
         * storageProfile qos control type 不正确
         * */
        String STORAGE_PROFILE_QOSCONTROLTYPE_ERROR = "10400001";
        String STORAGE_PROFILE_QOSCONTROLTYPE_ERROR_DESCRIPTION = "The storageProfile qos control type is error";

        /*
         * storageProfile qos control policy 不正确
         * */
        String STORAGE_PROFILE_QOSCONTROLPOLICY_ERROR = "10400002";
        String STORAGE_PROFILE_QOSCONTROLPOLICY_ERROR_DESCRIPTION = "The storageProfile qos control policy is error";

        /*
         * storageProfile name 不符合命名规范
         * */
        String STORAGE_PROFILE_NAME_INVALID = "10400003";
        String STORAGE_PROFILE_NAME_INVALID_DESCRIPTION = "Storage profile name is invalid";

        /*
         * storageProfile name 不符合命名规范
         * */
        String STORAGE_PROFILE_NAME_TOOLONG = "10400004";
        String STORAGE_PROFILE_NAME_TOOLONG_DESCRIPTION = "Storage profile name is too long";

        /*
         * storageProfile name 已存在
         * */
        String STORAGE_PROFILE_NAME_ALREADY_EXIST = "10400005";
        String STORAGE_PROFILE_NAME_ALREADY_EXIST_DESCRIPTION = "Storage profile name is already exist";

        /*
         * storageProfile qosSmartTier参数无效
         * */
        String STORAGE_PROFILE_QOSSMARTTIER_INVALID = "10400006";
        String STORAGE_PROFILE_QOSSMARTTIER_INVALID_DESCRIPTION = "Storage profile qosSmartTier parameter invalid";

        /*
         * storageProfile QoSLowerLimitControl参数无效
         * */
        String STORAGE_PROFILE_QOSLOWERLIMITCONTROL_INVALID = "10400007";
        String STORAGE_PROFILE_QOSLOWERLIMITCONTROL_INVALID_DESCRIPTION = "Storage profile QoSLowerLimitControl parameter invalid";

        /*
         * storageProfile QosControl参数无效
         */
        String STORAGE_PROFILE_QOSCONTROL_INVALID = "10400008";
        String STORAGE_PROFILE_QOSCONTROL_INVALID_DESCRIPTION = "Storage profile QosControl parameter invalid";

        /*
         * storageProfile storageProfileId参数无效
         */
        String STORAGE_PROFILE_PROFILEID_INVALID = "10400009";
        String STORAGE_PROFILE_PROFILEID_INVALID_DESCRIPTION = "Storage profileId parameter invalid";

        /*
         * storageProfile profile类型不一致
         */
        String STORAGE_PROFILE_TYPR_INVALID = "10400011";
        String STORAGE_PROFILE_TYPR_INVALID_DESCRIPTION = "Storage profileId type is not same as befor";

        /*
         * storageProfile profile类型不一致
         */
        String STORAGE_PROFILE_TYPR_CAPABILITY_ONLY_ONE = "10400012";
        String STORAGE_PROFILE_TYPR_CAPABILITY_ONLY_ONE_DESCRIPTION = "The Capability Control type is only one";

        /* storageProfile qos control type 不正确
         *
         * */
        String STORAGE_PROFILE_QOS_CONTROL_POLICY_UPPER_NOT_INCLUDE_LATENCY = "10400010";
        String STORAGE_PROFILE_QOS_CONTROL_POLICY_UPPER_NOT_INCLUDE_LATENCY_DESCRIPTION = "The storageProfile qos control policy is upper, could not include  latency.";


        String STORAGE_PROFILE_IS_NOT_EXIST = "10400011";
        String STORAGE_PROFILE_IS_NOT_EXIST_DESCRIPTION = "The storageProfile is not exist.";

    }

    interface storagePoolService {
        /*
         * storageContainer storageContainerId参数无效
         */
        String STORAGE_CONTAINERID_INVALID = "10500001";
        String STORAGE_CONTAINERID_INVALID_DESCRIPTION = "Storage containerId parameter invalid";

        /*
         * storageContainer不能绑定不通阵列的存储池
         */
        String STORAGE_CONTAINERID_CANNOT_BIND_DIFFARRAY_STORAGEPOOL = "10500002";
        String STORAGE_CONTAINERID_CANNOT_BIND_DIFFARRAY_STORAGEPOOL_DESCRIPTION = "The storage container can't bind different storageArray pool";

        /*
         * storageContainer解绑存在vvol卷的存储池
         */
        String STORAGE_CONTAINER_CANNOT_REMOVE_STORAGEPOOL = "10500003";
        String STORAGE_CONTAINER_CANNOT_REMOVE_STORAGEPOOL_DESCRIPTION = "The storage container can't remove storage pool, there still virtualVolume in storagePool";

        /*
         * 阵列不存在
         */
        String STORAGE_ARRAY_IS_NOT_EXIST = "10500004";
        String STORAGE_ARRAY_IS_NOT_EXIST_DESCRIPTION = "The storage array is not exist.";

        /*
         * 存储池不存在
         */
        String STORAGE_POOL_IS_NOT_EXIST = "10500005";
        String STORAGE_POOL_IS_NOT_EXIST_DESCRIPTION = "The storage pool is not exist.";

        /*
         * storage Container不存在
         */
        String STORAGE_CONTAINER_IS_NOT_EXIST = "10500006";
        String STORAGE_CONTAINER_IS_NOT_EXIST_DESCRIPTION = "The storage container is not exist.";

        /*
         * 存储池上还存在vvol lun
         */
        String STORAGE_POOL_IS_VVOL_LUN_ON_STORAGE_POOL = "10500007";
        String STORAGE_POOL_IS_VVOL_LUN_ON_STORAGE_POOL_DESCRIPTION = "The storage pool has vvol lun.";

        /*
         * 存储池不支持控制下限Policy
         */
        String STORAGE_POOL_NOT_SUPPORT_LOWER_QOS = "10500008";
        String STORAGE_POOL_NOT_SUPPORT_LOWER_QOS_DESCRIPTION = "The storage array can not support control lower policy.";

        /*
         * 存储池不支持smartTier Policy
         */
        String STORAGE_POOL_NOT_SUPPORT_SMART_TIER = "10500009";
        String STORAGE_POOL_NOT_SUPPORT_SMART_TIER_DESCRIPTION = "The storage array can not support smartTier.";

        /*
         * 存在已绑定的存储池
         */
        String STORAGE_POOL_HAS_ALREADY_BIND_POOLS = "10500010";
        String STORAGE_POOL_HAS_ALREADY_BIND_POOLS_DESCRIPTION = "The storage pools has already bind pool, the pool id is : ";
    }

    interface virtualVolumeService {
        /*
         * VVOLid 不存在
         */
        String STORAGE_VOLUME_IS_NOT_EXIST = "10600001";
        String STORAGE_VOLUME_IS_NOT_EXIST_DESCRIPTION = "Storage VVOL LUN is not exist.";

        /*
         * VVOL lun部分删除成功
         */
        String STORAGE_VOLUME_NOT_ALL_DELETE_SUCCESS = "10600002";
        String STORAGE_VOLUME_NOT_ALL_DELETE_SUCCESS_DESCRIPTION = "Not all vvol lun delete success.";

        /*
         * VVOL lun存在依赖卷
         */
        String STORAGE_VOLUME_IS_PARENT_VOLUME = "10600003";
        String STORAGE_VOLUME_IS_PARENT_VOLUME_DESCRIPTION = "Vvol lun is parent LUN.";

        /*
         * VVOL lun还处于绑定状态
         */
        String STORAGE_VOLUME_IN_BAND_STATUTE = "10600004";
        String STORAGE_VOLUME_IN_BAND_STATUTE_DESCRIPTION = "VVOL lun is in bound state.";
    }

    interface storageProfileLevel {

        String STORAGE_PROFILE_LEVEL_USING = "10700001";
        String STORAGE_PROFILE_LEVEL_USING_DESCRIPTION = "The storage profile level is in using";

    }
}
