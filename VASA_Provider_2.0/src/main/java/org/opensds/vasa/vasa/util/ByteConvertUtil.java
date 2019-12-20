/*
 * // Copyright 2019 The OpenSDS Authors.
 * //
 * // Licensed under the Apache License, Version 2.0 (the "License"); you may
 * // not use this file except in compliance with the License. You may obtain
 * // a copy of the License at
 * //
 * //     http://www.apache.org/licenses/LICENSE-2.0
 * //
 * // Unless required by applicable law or agreed to in writing, software
 * // distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * // WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * // License for the specific language governing permissions and limitations
 * // under the License.
 *
 */

package org.opensds.vasa.vasa.util;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.vmware.vim.vasa.v20.StorageFault;

public class ByteConvertUtil {

    private static Logger LOGGER = LogManager.getLogger(ByteConvertUtil.class);

    public static byte[] file2byte(String filePath) throws StorageFault {
        FileInputStream inputStream = null;
        ByteArrayOutputStream outputStream = null;
        try {
            inputStream = new FileInputStream(filePath);
            outputStream = new ByteArrayOutputStream();
            byte[] bs = new byte[1024 * 8];
            int n;
            while ((n = inputStream.read(bs)) != -1) {
                outputStream.write(bs, 0, n);
            }

            return outputStream.toByteArray();
        } catch (FileNotFoundException e) {
            LOGGER.error("ByteConvertUtil/file2byte FileNotFoundException.", e);
            throw FaultUtil.storageFault("ByteConvertUtil/file2byte FileNotFoundException", e);
        } catch (IOException e) {
            LOGGER.error("ByteConvertUtil/file2byte IOException.", e);
            throw FaultUtil.storageFault("ByteConvertUtil/file2byte IOException", e);
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    LOGGER.error("ByteConvertUtil/file2byte close stream IOException.", e);
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    LOGGER.error("ByteConvertUtil/file2byte close stream IOException.", e);
                }
            }
        }
    }

    public static void byte2file(byte[] data, String filePath) throws StorageFault {
        FileOutputStream fileOutputStream = null;
        BufferedOutputStream outputStream = null;
        try {
            fileOutputStream = new FileOutputStream(filePath);

            outputStream = new BufferedOutputStream(fileOutputStream);

            outputStream.write(data);

        } catch (FileNotFoundException e) {
            LOGGER.error("ByteConvertUtil/byte2file FileNotFoundException.", e);
            throw FaultUtil.storageFault("ByteConvertUtil/byte2file FileNotFoundException", e);
        } catch (IOException e) {
            LOGGER.error("ByteConvertUtil/byte2file IOException.", e);
            throw FaultUtil.storageFault("ByteConvertUtil/byte2file IOException", e);
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    LOGGER.error("ByteConvertUtil/byte2file close stream IOException.", e);
                    e.printStackTrace();
                }
            }
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    LOGGER.error("ByteConvertUtil/byte2file close stream IOException.", e);
                }
            }
        }
    }

}
