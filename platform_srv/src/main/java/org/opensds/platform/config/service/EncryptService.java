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

package org.opensds.platform.config.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.opensds.platform.common.exception.SDKException;
import org.opensds.platform.common.utils.AES128System;
import org.opensds.platform.common.utils.Base64Utils;
import org.opensds.platform.config.service.itf.IEncryptService;

public class EncryptService extends BaseService implements IEncryptService
{
    private static final Logger LOGGER = LogManager.getLogger(EncryptService.class);
    
    private static EncryptService instance = new EncryptService();
    
    public static EncryptService getInstance()
    {
        return instance;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String encryptContent(String content)
        throws SDKException
    {
        if (null == content)
        {
            return content;
        }
        
        try
        {
            return Base64Utils.encode(AES128System.encryptPwdByNewKey(content.getBytes("UTF-8")));
        }
        catch (Exception e)
        {
            LOGGER.error("", e);
            throw new SDKException("Encryption error");
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String encryptContent(String content, String algorithm)
        throws SDKException
    {
        if ("AES128".equalsIgnoreCase(algorithm))
        {
            return encryptContent(content);
        }
        else
        {
            throw new IllegalArgumentException("Algorithm is not supported");
        }
    }
}
