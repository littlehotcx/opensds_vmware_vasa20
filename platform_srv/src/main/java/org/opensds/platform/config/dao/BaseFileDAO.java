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

package org.opensds.platform.config.dao;

import java.io.IOException;
import java.io.OutputStream;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.dom4j.tree.DefaultElement;

import org.opensds.platform.common.utils.ESDKIOUtils;
import org.opensds.platform.common.utils.FileAttributeUtility;

public class BaseFileDAO
{
//    protected IFileService fileService;
    
    public BaseFileDAO()
    {
//        fileService = FileService.getInstance();
    }
    
    protected Element buildElement(String name, String value)
    {
        Element ele = new DefaultElement(name);
        ele.setText(value);
        return ele;
    }
    
    /**
	 *codedex 	
	 *FORTIFY.HW_-_Create_files_with_appropriate_access_permissions_in_multiuser_system
	 *FORTIFY.Unreleased_Resource--Streams    
	 *nwx356892 
	 */
    protected void writeXmlFile(Document document, String file)
        throws IOException
    {
        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setEncoding("UTF-8");
        OutputStream out = null;
        XMLWriter writer = null;
        try{
	        out = FileAttributeUtility.getSafeOutputStream(file,false);
	        writer = new XMLWriter(out, format);
	        writer.write(document);
        }finally{
        	ESDKIOUtils.closeFileStreamNotThrow(out);
        	if(null != writer){
        		try
                {
        			writer.close();
                }
                catch (IOException e)
                {
                	e.getStackTrace();
                }
        	}
        }
        writer.close();
        
        synFile(file);
    }
    
    protected void synFile(String file)
    {
        //        String destDir = ConfigManager.getInstance().getValue("file.sync.driver");
        //        if (!StringUtils.isEmpty(destDir) && !fileService.synFile(destDir, file))
        //        {
        //        	PrinterUtils.println(I18nPropUtils.getValue("msg.syn.file.failed",
        //                    new String[]
        //                    { PropertiesUtils.getValue("file.sync.driver") }));
        //        }
    }
}
