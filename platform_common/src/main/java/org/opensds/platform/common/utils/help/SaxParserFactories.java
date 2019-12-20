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

import javax.xml.XMLConstants;
import javax.xml.parsers.SAXParserFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SaxParserFactories
{
    private static final Logger LOGGER = LogManager.getLogger(SaxParserFactories.class);
    
    private SaxParserFactories()
    {
    }
    
    public static SAXParserFactory newSecurityInstance()
    {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        try
        {
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
        }
        catch (Exception ex)
        {
            LOGGER.error("FAILED to set feature http://xml.org/sax/features/external-general-entities to false", ex);
        }
        try
        {
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        }
        catch (Exception ex)
        {
            LOGGER.error("FAILED to set feature http://xml.org/sax/features/external-parameter-entities to false", ex);
        }
        try
        {
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        }
        catch (Exception ex)
        {
            LOGGER.error("FAILED to set feature XMLConstants.FEATURE_SECURE_PROCESSING to true", ex);
        }
        
        return factory;
    }
}
