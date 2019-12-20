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

package org.opensds.platform.common.utils.help;

import java.io.InputStream;
import java.io.Reader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.sax.SAXSource;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public final class SaxSources
{
    private SaxSources()
    {
    }
    
    public static SAXSource newSecurityUnmarshalSource(InputStream in)
        throws ParserConfigurationException, SAXException
    {
        return newSecurityUnmarshalSource(new InputSource(in));
    }
    
    public static SAXSource newSecurityUnmarshalSource(Reader reader)
        throws ParserConfigurationException, SAXException
    {
        return newSecurityUnmarshalSource(new InputSource(reader));
    }
    
    public static SAXSource newSecurityUnmarshalSource(InputSource inputSource)
        throws ParserConfigurationException, SAXException
    {
        SAXParserFactory factory = SaxParserFactories.newSecurityInstance();
        // as followed copy from javax.xml.bind.helpers.AbstractUnmarshallerImpl.getXMLReader()
        factory.setNamespaceAware(true);
        // there is no point in asking a validation because there is no guarantee that the document will come with a proper schemaLocation.
        /*
         * CodeDex:  FORTIFY.Missing_XML_Validation      by nWX285177
         */
        factory.setValidating(true);
        XMLReader xmlReader = factory.newSAXParser().getXMLReader();
        SAXSource saxSource = new SAXSource(xmlReader, inputSource);
        
        return saxSource;
    }
}
