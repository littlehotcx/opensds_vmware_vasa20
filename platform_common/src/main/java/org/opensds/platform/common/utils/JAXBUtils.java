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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.SAXException;

import org.opensds.platform.common.utils.help.SaxSources;
import com.sun.xml.bind.marshaller.CharacterEscapeHandler;

public final class JAXBUtils
{
    private static final Logger LOGGER = LogManager.getLogger(JAXBUtils.class);

    public static String bean2Xml(Object bean)
    {
        return bean2Xml(bean, bean.getClass(), false, true);
    }
    
    public static String bean2Xml(Object bean, Class<?> clazz)
    {
        return bean2Xml(bean, clazz, false, true);
    }
    
    public static String bean2Xml(Object bean, Class<?> clazz, boolean noNeedDeclaration)
    {
        return bean2Xml(bean, clazz, noNeedDeclaration, true);
    }
    
    public static String bean2Xml(Object bean, Class<?> clazz, boolean noNeedDeclaration, boolean escape)
    {
        ByteArrayOutputStream os = null;
        try
        {
            JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

            // output pretty printed
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            if (!escape)
            {
                jaxbMarshaller.setProperty("com.sun.xml.bind.marshaller.CharacterEscapeHandler", NoEscapeHandler.getInstance());
            }
            
            if (noNeedDeclaration)
            {                           
                jaxbMarshaller.setProperty("com.sun.xml.bind.xmlDeclaration", Boolean.FALSE);
            }
            
            os = new ByteArrayOutputStream();
            jaxbMarshaller.marshal(bean, os);

            String result = os.toString("UTF-8");
            os.close();

            return result;
        }
        catch (JAXBException e)
        {
            LOGGER.error("", e);
            return null;
        }
        catch (UnsupportedEncodingException e)
        {
            LOGGER.error("", e);
            return null;
        }
        catch (IOException e)
        {
            LOGGER.error("", e);
            return null;
        }
        finally
        {
            ESDKIOUtils.closeOutputStream(os);
        }
    }
    
    public static Object xml2Bean(Class<?> clazz, String xmlString)
    {
        InputStream in = null;
        try
        {
            JAXBContext jaxbContext = JAXBContext.newInstance(clazz);

            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            in = new ByteArrayInputStream(xmlString.getBytes("UTF-8"));            
            Object object = jaxbUnmarshaller.unmarshal(SaxSources.newSecurityUnmarshalSource(in));
            in.close();
            return object;
        }
        catch (JAXBException e)
        {
            LOGGER.error("", e);
            return null;
        }
        catch (UnsupportedEncodingException e)
        {
            LOGGER.error("", e);
            return null;
        }
        catch (IOException e)
        {
            LOGGER.error("", e);
            return null;
        }
        catch (ParserConfigurationException e)
        {
            LOGGER.error("", e);
            return null;
        }
        catch (SAXException e)
        {
            LOGGER.error("", e);
            return null;
        }
        finally
        {
            ESDKIOUtils.closeInputStream(in);
        }
    }
    
    static class NoEscapeHandler implements CharacterEscapeHandler
    {
        private static NoEscapeHandler instance = new NoEscapeHandler();
        
        private NoEscapeHandler()
        {
        }
        
        public static NoEscapeHandler getInstance()
        {
            return instance;
        }
        
        @Override
        public void escape(char[] ch, int start, int length, boolean flag, Writer writer)
            throws IOException
        {
            int limit = start + length;
            for(int i = start; i < limit; i++)
            {
                if(ch[i] > '\177')
                {
                    writer.write("&#");
                    writer.write(Integer.toString(ch[i]));
                    writer.write(59);
                } else
                {
                    writer.write(ch[i]);
                }
            }
        }
    }
}
