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

import java.io.File;
import java.net.URL;

/**
 * 提供各种配置文件的路径
 * 1.资源文件:图片,声音,中英文文字
 * 2.配置文件:用户信息文件,设备信息文件
 * 3.日志文件:日志文件
 * <一句话功能简述>
 * <功能详细描述>
 *
 * @author zWX228053
 * @version V100R001C00
 * @date 2015年1月9日
 * @see [相关类/方法]
 */
public class FileManager {
    /**
     * @return 日志文件存放路径
     */
    public static String getLogFilePath() {
        String logFilePath = getBasePath() + "logs" + File.separator;
        File dir = new File(logFilePath);
        if (!dir.exists()) {
            dir.mkdir();
        }
        return logFilePath;
    }

    /**
     * @return 返回配置文件存放路径
     */
    public static String getConfigFilePath() {
        return getBasePath() + "config" + File.separator;
    }

    /**
     * 返回当前应用的根目录，如C:/tomcat_home/webapps/MyWebApp/
     *
     * @return MyWebApp/
     */
    public static String getBasePath() {
        URL url = FileManager.class.getResource("/");//classes
        /**
         *  修改CodeDEX问题：NULL_RETURNS url may return the value of "null" here
         *  Modified by wWX315527 2016/11/19
         */
        if (url != null) {
            File file = new File(url.getFile());
            return file.getParentFile().getParentFile().getParent() + File.separator;
        } else {
            return "unknown";
        }
    }

    /**
     * 获得eSDK服务的Server.xml文件路径
     */
    public static String getServerXMLPath() {
        return getBasePath() + "../../conf/server.xml";
    }
}
