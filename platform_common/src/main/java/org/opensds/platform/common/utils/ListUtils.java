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

import java.util.List;
import java.util.Locale;

public class ListUtils
{
    public static boolean isEmptyList(List<String> list)
    {
        if (null == list || 0 == list.size())
        {
            return true;
        }
        
        boolean flag = false;
        for (String item : list)
        {
            if (null != item && !("".equals(item)))
            {
                flag = true;
                break;
            }
        }
        
        if (flag)
        {
            return false;
        }
        
        return true;
    }
    
    public static boolean containStr(List<String> list, String str)
    {
        return containStr(list, str, false);
    }
    
    public static boolean containStr(List<String> list, String str, boolean ignoreCase)
    {
        if (null == list || 0 == list.size() || null == str)
        {
            return false;
        }
        
        for (String item : list)
        {
            if (ignoreCase)
            {
                item = item.toLowerCase(Locale.getDefault());
                str = str.toLowerCase(Locale.getDefault());
            }
            if (item.equals(str))
            {
                return true;
            }
        }
        
        return false;
    }
    
    //DTS2015012603196 mcus为空，查未来时间MCU的资源占用情况，返回2130000010错误
    //对列表的每个值都要判断
    public static boolean isEmptyListForInt(List<Integer> list)
    {
        if (null == list || 0 == list.size())
        {
            return true;
        }
        
        for (Integer item : list)
        {
            if (null == item)
            {
                return true;
            }
        }
        
        return false;
    }
}
