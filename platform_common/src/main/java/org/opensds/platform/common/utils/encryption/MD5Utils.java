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

package org.opensds.platform.common.utils.encryption;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensds.platform.common.utils.BytesUtils;
import org.opensds.platform.common.utils.StringUtils;

public abstract class MD5Utils
{
    private static final Logger LOGGER = LogManager.getLogger(MD5Utils.class);
    
    public static String do16BitMD5(String plainStr)
    {
        String result = do32BitMD5(plainStr);
        if (StringUtils.isNotEmpty(result) && result.length() >= 24)
        {
            result = result.substring(8, 24);
        }
        
        return result;
    }
    
    public static String do32BitMD5(String plainStr)
    {
        if (null == plainStr)
        {
            return plainStr;
        }
        
        String result;
        try
        {
        	/*
             * CodeDex:  FORTIFY.Weak_Cryptographic_Hash      by nWX285177
             */
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(BytesUtils.getBytes(plainStr));
            byte b[] = md.digest();
            int i;
            StringBuilder sb = new StringBuilder("");
            for (int index = 0; index < b.length; index++)
            {
                i = b[index];
                if (i < 0)
                {
                    i += 256;
                }
                if (i < 16)
                {
                    sb.append("0");
                }
                sb.append(Integer.toHexString(i));
            }
            result = sb.toString();            
        }
        catch (NoSuchAlgorithmException e)
        {
            LOGGER.error("", e);
            result = plainStr;
        }
        
        return result;
    }
}
