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

package org.opensds.vasa.vasa.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import org.opensds.platform.common.utils.ApplicationContextUtil;

public class CheckServiceOnService implements ApplicationListener<ContextRefreshedEvent> {

    private static Logger LOGGER = LogManager.getLogger(CheckServiceOnService.class);

    private static String sep = System.getProperty("line.separator");
    private static String errMsg = "<html> " + sep + "<head>" + sep + "<title>error message!</title>" + sep + "</head>" +
            sep + "<body>" + sep + " can not init all service,please restart vasa service." + sep + "</body>" + sep + "</html>";

    private static String normMsg = "<html> " + sep + "<head>" + sep + "<title>Congratulations!</title>" + sep + "</head>" +
            sep + "<body>" + sep + " Congratulations! You can see this page, it means you have installed the eSDK system successfully." + sep + "</body>" + sep + "</html>";


    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        // TODO Auto-generated method stub

        BasicDataSource dataSource = (BasicDataSource) ApplicationContextUtil.getBean("dataSource");
        String classPath = this.getClass().getClassLoader().getResource("/").getPath();
        String filePath = classPath.substring(0, classPath.length() - 32) + "webroot/index.html";
        LOGGER.info("filePath=" + filePath);
        try {
            if (null == dataSource || null == dataSource.getConnection()) {
                LOGGER.error("init spring beans error,bean [dataSource] can not init!!");
                updateIndexFile(filePath, errMsg);
            } else {
                updateIndexFile(filePath, normMsg);
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            LOGGER.error("init spring beans error,bean [dataSource] can not init!!", e);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            LOGGER.error("IO error", e);
        }


    }


    public void updateIndexFile(String filePath, String data) throws IOException {
        File file = new File(filePath);
        FileWriter fileWriter = null;
        if (file.exists()) {
            try {
                fileWriter = new FileWriter(file, false);
                fileWriter.write(data);
                fileWriter.flush();

            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                LOGGER.error("can not find the index.html file. path=" + filePath, e);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                LOGGER.error("open index.html file error. path=" + filePath, e);
            } finally {
                fileWriter.close();
            }

        } else {
            LOGGER.error("can not find the index.html file. path=" + filePath);
        }


    }

}
