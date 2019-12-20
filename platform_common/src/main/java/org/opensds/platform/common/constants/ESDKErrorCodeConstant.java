/*
 *
 *  * // Copyright 2019 The OpenSDS Authors.
 *  * //
 *  * // Licensed under the Apache License, Version 2.0 (the "License"); you may
 *  * // not use this file except in compliance with the License. You may obtain
 *  * // a copy of the License at
 *  * //
 *  * //     http://www.apache.org/licenses/LICENSE-2.0
 *  * //
 *  * // Unless required by applicable law or agreed to in writing, software
 *  * // distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *  * // WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *  * // License for the specific language governing permissions and limitations
 *  * // under the License.
 *  *
 *
 */

package org.opensds.platform.common.constants;

public interface ESDKErrorCodeConstant
{
    /**
     * ESDK system internal error SDK内部服务错误
     */
    int ERROR_CODE_SYS_ERROR = 2130000010;
    String ERROR_DESC_SYS_ERROR = "SYSTEM ERROR!";
    
    /**
     * Network error or the destination service is not available eSDK与设备间网络异常
     */
    int ERROR_CODE_NETWORK_ERROR = 2130000011;
    
    // 设备连接失败
    int ERROR_CODE_DEVICE_CONN_ERROR = 2130000012;
    
    // 设备不在平台列表中
    int ERROR_CODE_DEVICE_DOES_NOT_EXIST = 2130000013;
    
    //
    int ERROR_CODE_SDK_AUTHORIZE_FAILURE = 2130000014;
    
    int ERROR_CODE_APP_OUTOFABILITY = 2130000015;
    
    // 设备服务发生异常
    int ERROR_CODE_DEVICE_SERVICE_EXCEPTION = 2130000016;
    
    // 不支持该接口
    int ERROR_CODE_API_NOT_SUPPORT = 2130000017;
    
    // 设备驱动不存在
    int ERROR_CODE_DEVICEITF_NOT_EXIST = 2130000018;
    
    // 账户被锁定
    int ERROR_CODE_SDK_AUTHORIZE_ACCT_LOCK = 2130000027;
    
    // 系统忙
    int ERROR_CODE_SDK_SYSBUSY = 2130000028;
    
    // 设备连接为空
    int ERROR_CODE_CONN_NULL = 2130000029;
    
    /**
     *  参数错误
     */
    int DATA_ERRORCODE = 2130000030;
    String DATA_ERRORDESC = "DATA ERROR!";
    
    /**
     *  系统暂不支持该算法
     */
    int NO_SUCH_ALGORITHM_ERRORCODE = 2130000038;
    String NO_SUCH_ALGORITHM_ERRORDESC = "No Such Algorithm!";
    
    /**
     *  必填参数不全
     */
    int SDK_PARAM_NOT_COMPLETE_ERRORCODE = 2130000039;
    String SDK_PARAM_NOT_COMPLETE_ERRORDESC = "SDK PARAM NOT COMPLETE ERROR!";
    
    /**
     * 数值非法或超过取值范围
     */
    int SDK_DATA_INVALID_ERROR = 2130000040;
    String SDK_DATA_INVALID_ERRORDESC = "DATA INVALID OR OUT OF RANGE";
    
    /**
     * session id 无效
     */
    int SESSIONID_ERRORCODE = 2130000041;
    String SESSIONID_ERRORDESC = "SESSIONID IS ERROR!";
    
    
    /**
     * 解密工作密钥失败
     */
    int SECRETKEY_DECODE_ERRORCODE = 2130000042;
    String SECRETKEY_DECODE_ERRORDESC = "secret key decode error!";
    
    /**
     * eSDK缺少协商的密钥
     */
    int SECRETKEY_LACK_ERRORCODE = 2130000043;
    String SECRETKEY_LACK_ERRORDESC = "secret key lack!";
    
    /**
     * eSDK插件没有权限
     */
    int PLUGIN_NO_PERMISSION_ERRORCODE = 2130000044;
    String PLUGIN_NO_PERMISSION_ERRORDESC = "plugin no permission!";
    
    // IP被锁定
    int ERROR_CODE_SDK_AUTHORIZE_IP_LOCK = 2130000045;
    String SDK_AUTHORIZE_IP_LOCK_ERRORDESC = "authorize ip is locked";
}
