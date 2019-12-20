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

import java.io.UnsupportedEncodingException;
import java.util.Locale;

/**
 * byte转换工具类
 * 
 * @author  cWX191990
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public abstract class BytesUtils
{
    
    /**
     * 长度:2
     */
    private static final int LENGTH = 2;
    
    /**
     * 位移长度：4
     */
    private static final int DISPLACEMENT_LENGTH = 4;
    
    /**
     * 字符集合
     */
    private static final String CHAR_COLLECTIONS = "0123456789ABCDEF";
    
    /** 
    * byte转换为16进制
    * 
    * @param src byte字节数组
    * @return 转换后的16进制/null
    * @see [类、类#方法、类#成员]
    */
    public static String bytesToHexString(byte[] src)
    {
        if (src == null || src.length <= 0)
        {
            return null;
        }
        
        StringBuilder hexString = new StringBuilder("");
        int number = 0;
        String hNumber = null;
        
        for (int i = 0; i < src.length; i++)
        {
            // 转换成16进制表现形式0xFF
            number = src[i] & 0xFF;
            hNumber = Integer.toHexString(number);
            
            if (LENGTH > hNumber.length())
            {
                hexString.append(0);
            }
            
            hexString.append(hNumber);
        }
        
        return hexString.toString();
    }
    
    /** 
    * 将16进制转换成byte数组
    * 
    * @param hexString 16进制字符串形式
    * @return 转换后的byte数组
    * @see [类、类#方法、类#成员]
    */
    public static byte[] hexStringToBytes(String hexString)
    {
        if (StringUtils.isEmpty(hexString))
        {
            return new byte[0];
        }
        
        // 防止有字母小写，统一转转成大写后再转换
        hexString = hexString.toUpperCase(Locale.getDefault());
        int len = hexString.length() / LENGTH;
        char[] hexChars = hexString.toCharArray();
        byte[] bytes = new byte[len];
        int position = 0;
        
        for (int i = 0; i < len; i++)
        {
            position = i * LENGTH;
            bytes[i] =
                (byte)(charToByte(hexChars[position]) << DISPLACEMENT_LENGTH | charToByte(hexChars[position + 1]));
        }
        
        return bytes;
    }
    
    /** 
    * 将char转换为byte
    * 
    * @param c 字符
    * @return 字节
    * @see [类、类#方法、类#成员]
    */
    private static byte charToByte(char c)
    {
        return (byte)CHAR_COLLECTIONS.indexOf(c);
    }
    
    //以下方法用于IVS中String和byte[]之间的转换
    /** 
    * 将byte数组转换为长度为length的byte数组
    * 
    * @param 字节数组， 整形 
    * @return 字节数组
    * @see [类、类#方法、类#成员]
    */
    public static byte[] initBytesLength(byte[] value, int length)
    {
        byte[] res = new byte[length];
        System.arraycopy(value, 0, res, 0, value.length);
        return res;
    }
    
    /** 
    * 将字符串转换为GBK编码格式的byte数组
    * 
    * @param 字符串
    * @return 字节数组
    * @see [类、类#方法、类#成员]
    */
    public static byte[] stringToBytesForIVS(String value)
    {
        if (null == value)
        {
            return new byte[0];
        }
        
        try
        {
            if (OSUtils.isWindows())
            {
                return value.getBytes(System.getProperty("file.encoding"));
            }
            else
            {
                return value.getBytes("UTF-8");
            }
        }
        catch (UnsupportedEncodingException e)
        {
            return new byte[0];
        }
    }
    
    /** 
    * 将字符串转换为GBK编码格式并且长度为length的byte数组
    * 
    * @param 字符串， 整形
    * @return 字节数组
    * @see [类、类#方法、类#成员]
    */
    public static byte[] stringToBytesForIVS(String value, int length)
    {
        if (length < value.length())
        {
            value = value.substring(0, length);
        }
        return initBytesLength(stringToBytesForIVS(value), length);
    }
    
    /** 
    * 将字符串转换为GBK编码格式，长度为length，向右偏移offset位(前offset位补1)的byte数组
    * 
    * @param 字符串， 整形
    * @return 字节数组
    * @see [类、类#方法、类#成员]
    */
    public static byte[] stringToBytesForIVS(String value, int length, int offset)
    {
        byte[] oldBytes = stringToBytesForIVS(value, length);
        byte[] newBytes = new byte[oldBytes.length + offset];
        
        for (int i = 0; i < 4; i++)
        {
            newBytes[i] = 1;
        }
        
        for (int i = 0; i < oldBytes.length; i++)
        {
            newBytes[i + 4] = oldBytes[i];
        }
        
        return newBytes;
    }
    
    /** 
    * 将字节数组转换为为GBK编码格式字符串
    * 
    * @param 字节数组
    * @return 字符串
    * @see [类、类#方法、类#成员]
    */
    public static String bytesToStringForIVS(byte[] value)
    {
        
        if (null == value)
        {
            return null;
        }
        
        try
        {
            //问题单【DTS2016090600939】
            //IVS服务端windows和linux都改为使用UTF-8编解码（底层客户端类库配置文件中设置不转码）
            //            if (OSUtils.isWindows())
            //            {
            //                return new String(value, "GBK").trim();
            //            }
            //            else
            //            {
            return new String(value, "UTF-8").trim();
            //            }
        }
        catch (UnsupportedEncodingException e)
        {
            return null;
        }
    }
    
    public static byte[] getBytes(String str)
    {
        return getBytes(str, "UTF-8");
    }
    
    public static byte[] getBytes(String str, String encoding)
    {
        if (null == str)
        {
            str = "";
        }
        
        try
        {
            return str.getBytes(encoding);
        }
        catch (UnsupportedEncodingException e)
        {
            return new byte[] {};
        }
    }
}
