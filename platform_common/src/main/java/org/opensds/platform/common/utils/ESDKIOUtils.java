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

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.util.zip.ZipFile;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
/**
 * 
 * eSDK IO操作的工具类 
 *
 * @author  z00209306
 * @since  eSDK Solutions Platform V100R003C00
 */
public abstract class ESDKIOUtils
{
    private static final Logger LOGGER = LogManager.getLogger(ESDKIOUtils.class);
    
    public static final int MAX_LINE_LENGTH = 50000;
    
    /**
     * 
     * 从classpath下的文件中获取文件内容
     *
     * @param fileName 文件名
     * @return 文件内容
     * @since eSDK Solutions Platform V100R003C00
     */
    public static String getClasspathFileContent(String fileName)
    {
        return getClasspathFileContent(fileName, "UTF-8");
    }
    
    /**
     * 
     * 从classpath下的文件中获取文件内容
     *
     * @param fileName 文件名
     * @param fileEncode 文件编码
     * @return 文件内容
     * @since eSDK Solutions Platform V100R003C00
     */
    public static String getClasspathFileContent(String fileName, String fileEncode)
    {
        InputStream in = null;
        StringWriter writer = new StringWriter();
        try
        {
            in = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
            IOUtils.copy(in, writer, fileEncode);
        }
        catch (IOException e)
        {
            LOGGER.error("File load failed[fileName=" + fileName + "]", e);
        }
        finally
        {
            try
            {
                if (null != in)
                {
                    in.close();
                }
            }
            catch (IOException e)
            {
                LOGGER.error("", e);
            }
        }
        
        return writer.toString();
    }
    
    public static void closeReader(Reader reader)
    {
        try
        {
            if (null != reader)
            {
                reader.close();
            }
        }
        catch (IOException e)
        {
            LOGGER.error("", e);
        }
    }
    
    public static void closeInputStream(InputStream is)
    {
        try
        {
            if (null != is)
            {
                is.close();
            }
        }
        catch (IOException e)
        {
            LOGGER.error("", e);
        }
    }
    
    public static void closeOutputStream(OutputStream out)
    {
        try
        {
            if (null != out)
            {
                out.close();
            }
        }
        catch (IOException e)
        {
            LOGGER.error("", e);
        }
    }
    
    public static int readLine(BufferedReader reader, StringBuffer buffer) throws IOException {
		char[] data = new char[1];

		boolean leftLean = false;

		int number = 0;
		
		while (MAX_LINE_LENGTH >= buffer.length() && -1 != (number = reader.read(data))) {
			// 获取读取的字符
			char readed = data[0];

			// 如果左边是\r，则当前位置必须为\n否则需要reset
			if (leftLean) {
				if ('\n' != readed) {
					reader.reset();
				}
				break;
			}

			// 如果是\r,则需要记住，并且标记已经为一行结束
			if ('\r' == readed) {
				reader.mark(2);
				leftLean = true;
			}
			// 如果是\n则一行结束
			else if ('\n' == readed) {
				break;
			} else {
				buffer.append(readed);
			}
		}

		if (MAX_LINE_LENGTH < buffer.length()) {
			throw new IllegalStateException("Line is too long.");
		}

		return number;
	}
    
    public static void closeIgnoringException(ZipFile c) 
    {
	    if(c != null) 
	    {
	        try 
	        {
	            c.close();
	        } catch (IOException e) 
	        {
	        	LOGGER.error("close failed.", e);
	        }
	    }
    }
    
    public static void closeFileStreamNotThrow(Closeable c)
    {
        if (c != null)
        {
            try
            {
                c.close();
            }
            catch (IOException e)
            {
            	LOGGER.error("close failed.", e);
            }
        }
    }
    
}
