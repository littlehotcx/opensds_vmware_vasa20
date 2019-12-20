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

public class MaskUtils
{
    public static String mask(String content, String words)
    {
        if (StringUtils.isEmpty(words))
        {
            return content;
        }
        
        String[] sensitiveWords = words.split(",");

        for (String word : sensitiveWords)
        {
            content = replaceSensitiveWords(content, word);
        }
        return content;
    }

    private static String replaceSensitiveWords(String strBuffer, String word)
    {
        int startIndex = 0;
        while (true)
        {
            if (-1 == strBuffer.indexOf("<" + word + " ", startIndex) 
                && -1 == strBuffer.indexOf("<" + word + ">", startIndex))
            {
                break;
            }
            else
            {
                int node1Begin = strBuffer.indexOf("<" + word, startIndex);
                int node1End = strBuffer.indexOf(">", node1Begin) + 1;
                String node1 = strBuffer.substring(node1Begin, node1End);
                
                int begin = node1End;
                
                int end = strBuffer.indexOf("</" + word + ">",startIndex);
                if (begin < end)
                {
                    String exp = strBuffer.substring(begin, end);
                    StringBuffer rep = new StringBuffer(node1);
                    for (int i = 0; i < exp.length(); i++)
                    {
                        rep.append("*");
                    }
                    rep.append("</" + word + ">");
                    strBuffer = strBuffer.replace(node1 + exp + "</" + word
                            + ">", rep);
                    startIndex = end + word.length() + 3;
                }
                else
                {
                    startIndex = begin;
                }
                
            }
        }

        return strBuffer;
    }
    
    public static String maskXMLElementValue(String srcXML, String maskElement)
    {
        return maskXMLElementValue(srcXML, maskElement, '*');
    }
    
    public static String maskXMLElementValue(String srcXML, String maskElements, char maskChar)
    {
        if (StringUtils.isEmpty(srcXML) || StringUtils.isEmpty(maskElements))
        {
            return srcXML;
        }
        
        String[] words = maskElements.trim().split(",");
        String result = srcXML;
        for (String word : words)
        {
            if (StringUtils.isEmpty(word) || "null".equals(word))
            {
                continue;
            }
            result = doMaskXMLEle(result, word, '*');
        }
        
        return result;
    }
    
    private static String doMaskXMLEle(String srcXML, String maskElement, char maskChar)
    {
        
        return replaceSensitiveWords(srcXML, maskElement);
    }
    
    public static String maskJson(String content, String words)
    {
        if (StringUtils.isEmpty(words))
        {
            return content;
        }
        
        String[] sensitiveWords = words.split(",");

        for (String word : sensitiveWords)
        {
            content = replaceJsonSensitiveWords(content, word);
        }
        return content;
    }
    
    private static String replaceJsonSensitiveWords(String strBuffer, String word)
    {
        int startIndex = 0;
        int begin;
        int end;
        while (true)
        {
            if (-1 == strBuffer.indexOf("\"" + word + "\":\"", startIndex))
            {
                break;
            }
            else
            {
                begin = strBuffer.indexOf("\"" + word + "\"", startIndex) + word.length() + 4;
                if (0 == startIndex)
                {
                    startIndex = begin;
                }
                end = strBuffer.indexOf("\"", startIndex);
                if (begin != end)
                {
                    String exp = strBuffer.substring(begin, end);
                    StringBuffer rep = new StringBuffer("\"" + word + "\":\"");
                    for (int i = 0; i < exp.length(); i++)
                    {
                        rep.append("*");
                    }
                    rep.append("\"");
                    strBuffer = strBuffer.replace("\"" + word + "\":\"" + exp + "\"", rep);
                }
                
                startIndex = end + word.length() + 3;
            }
        }

        return strBuffer;
    }
}
