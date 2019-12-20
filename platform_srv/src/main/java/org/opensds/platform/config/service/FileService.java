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

package org.opensds.platform.config.service;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.opensds.platform.config.service.itf.IFileService;

public final class FileService implements IFileService
{
    private static final Logger LOGGER = LogManager.getLogger(FileService.class);

    private static IFileService instance = new FileService();
    
    private FileService()
    {
    }
    
    public static IFileService getInstance()
    {
        return instance;
    }
    
    /**
     * * @see
     * org.opensds.platform.tool.service.impl.IFileService#synFile(java
     * .lang.String, java.lang.String)
     */
    @Override
    public boolean synFile(String destDir, String filePath)
    {
        return syncFileBySharingFolder(filePath, destDir);
    }
    
    private boolean syncFileBySharingFolder(String filePath, String destDir)
    {
        try
        {
            File srcFile = new File(filePath);
            FileUtils.copyFileToDirectory(srcFile, new File(destDir));
            return true;
        }
        catch (IOException e)
        {
            LOGGER.error("", e);
        }
        
        return false;
    }
}
