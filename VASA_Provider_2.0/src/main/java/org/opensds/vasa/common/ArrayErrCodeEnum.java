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

public enum ArrayErrCodeEnum {
    RETURN_PARAM_ERROR(50331651),
    RETURN_SYSTEM_BUSY(1077949020),

    /**
     * 差异拷贝
     */
    RETURN_CPY_CREATED_TOOMUCH(1077950145),
    RETURN_CPY_COPYING_STATE_CANT_MODIFY_NAME(1077950146),
    RETURN_CPY_FULLCPY_CANT_LABEL(1077950147),
    RETURN_CPY_COPYING_STATE_CANT_DELETE(1077950148),
    RETURN_CPY_FAIL_TO_CREATE_PAIR(1077950149),
    RETURN_CPY_ILLEGAL_OPERATION(1077950150),
    RETURN_CPY_FAIL_TO_MODIFY(1077950151),
    RETURN_CPY_NOT_PAUSE_STATE_CANT_CONTINUE(1077950152),
    RETURN_CPY_FULLCPY_CANT_TAG(1077950153),
    RETURN_CPY_SEND_TO_PEER_OPERATION_FAILED(1077950154),
    RETURN_CPY_PAUSE_STATE_CANT_PAUSE(1077950155),
    RETURN_CPY_HAVE_NOT_DST(1077950156),
    RETURN_CPY_LUN_STATE_ERROR(1077950157),
    RETURN_CPY_DSTLUN_TOOMUCH(1077950158),
    RETURN_CPY_ADD_DST_CONTROLLER_OFFLINE(1077950159),
    RETURN_CPY_DEL_DST_CONTROLLER_OFFLINE(1077950160),
    RETURN_CPY_ALREADY_CPY_MEMBER_LUN(1077950161),
    RETURN_CPY_CANNOT_DEL_ONLY_DST(1077950162),
    RETURN_CPY_SRCID_EQUAL_DSTID(1077950163),
    RETURN_CPY_SRC_LARGER_THAN_DST(1077950164),
    RETURN_CPY_ABNORMAL_LUN(1077950165),
    RETURN_CPY_BOTH_EXTLUN(1077950166),
    RETURN_CPY_TYPE_ERROR(1077950167),
    RETURN_CPY_SPEED_ERROR(1077950168),
    RETURN_CPY_SYN_ERROR(1077950169),
    RETURN_CPY_STATUS_ERROR(1077950170),
    RETURN_CPY_FAIL_TO_MODIFY_SPEED(1077950171),
    RETURN_CPY_FAIL_TO_MODIFY_NAME(1077950172),
    RETURN_CPY_INC_CANT_SHARE_SRCID(1077950173),
    RETURN_CPY_CANT_SHARE_DSTID(1077950174),
    RETURN_CPY_OPERATION_FAILED(1077950175),
    RETURN_CPY_DST_LUN_DOESNOT_EXIST(1077950176),
    RETURN_CPY_PAIR_STATE_COPYING(1077950177),
    RETURN_CPY_PAIR_STATE_STOPPED(1077950178),
    RETURN_CPY_PAIR_STATE_CREATED(1077950179),
    RETURN_CPY_PAIR_STATE_COMPLETED(1077950180),
    RETURN_CPY_NO_LICENSE(1077950181),
    RETURN_CPY_LUN_DOESNOT_EXIST(1077950182),
    RETURN_CPY_PAIRID_NOT_EXIST(1077950183),
    RETURN_CPY_PAIR_STATE_QUEUING(1077950184),
    RETURN_CPY_PAIR_STATE_PAUSED(1077950185),
    RETURN_CPY_INC_REMOTE_SRCLUN(1077950186),
    RETURN_CPY_CREATE_REMOTELUN_FAILED(1077950187),
    RETURN_CPY_REMOTE_ARRAY_CONNECT_ABNORMAL(1077950188),
    RETURN_CPY_PAIR_BUSY(1077950189),
    RETURN_CPY_SRCSNAP_REACH_MAX_NUM(1077950190),

    RETURN_CPY_EXTLUN_UPPER_MAX(1077950192),
    RETURN_CPY_EXTLUN_WWN_NOT_MATCH(1077950193),
    RETURN_CPY_SNAP_NOT_CORRESPOND_TO_SRC(1073797889),
    RETURN_CPY_SUBTYPE_IS_DIF_TO_SUBTYPE_IN_DB(1073797890),

    /**
     * 查询位图
     */
    RETURN_SNAP_OBJECTS_NOT_IN_SNAPSHOT_RALATIONSHIP(1073797889);
    private long value;

    /**
     * ArrayErrCodeEnum类型
     *
     * @param value
     */
    private ArrayErrCodeEnum(long value) {
        this.value = value;
    }

    /**
     * 方法 ： getTypeName
     *
     * @param value 方法参数：value
     * @return String 返回结果
     */
    public static String getTypeName(long value) {
        ArrayErrCodeEnum erCodeType = null;
        for (ArrayErrCodeEnum type : ArrayErrCodeEnum.values()) {
            if (type.getValue() == value) {
                erCodeType = type;
            }
        }
        return erCodeType == null ? null : erCodeType.name();
    }

    /**
     * 方法 ： getType
     *
     * @param value 方法参数：value
     * @return MOType 返回结果
     */
    public static ArrayErrCodeEnum getType(long value) {
        ArrayErrCodeEnum errCodeType = null;
        for (ArrayErrCodeEnum type : ArrayErrCodeEnum.values()) {
            if (type.getValue() == value) {
                errCodeType = type;
            }
        }
        return errCodeType;
    }

    /**
     * 方法 ： getValue
     *
     * @return int 返回结果
     */
    public long getValue() {
        return value;
    }
}
