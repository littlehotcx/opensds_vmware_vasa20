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

package org.opensds.platform.common.utils;

import java.util.Locale;
import java.util.ResourceBundle;

public abstract class I18nPropUtils
{
    private static ResourceBundle bundle = ResourceBundle.getBundle("i18n/resources", Locale.getDefault());
    
    public static String getValue(String key)
    {
        return bundle.getString(key);
    }
    
    public static String getValue(String key, String[] parameters)
    {
        String value = bundle.getString(key);
        
        if (null == parameters)
        {
            return value;
        }
        
        for (int i = 0; i < parameters.length; i++)
        {
            value = value.replace("{" + i + "}", parameters[i] == null ? "" : parameters[i]);
        }
        
        return value;
    }    
}
