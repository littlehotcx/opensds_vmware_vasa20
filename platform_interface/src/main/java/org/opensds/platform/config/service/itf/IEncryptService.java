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

package org.opensds.platform.config.service.itf;

import org.opensds.platform.common.exception.SDKException;

public interface IEncryptService
{
    /**
     * 
     * 加密字符串，使用AES128算法
     *
     * @param content 需要加密的内容
     * @return 加密后的密文
     * @since eSDK Solutions Platform V100R003C00
     */
    String encryptContent(String content) throws SDKException;
    
    /**
     * 
     * 加密字符串
     *
     * @param content 需要加密的内容
     * @param algorithm 指定需要使用的加密算法
     * @return 加密后的密文
     * @since eSDK Solutions Platform V100R003C00
     */
    
    String encryptContent(String content, String algorithm) throws SDKException;;
}
