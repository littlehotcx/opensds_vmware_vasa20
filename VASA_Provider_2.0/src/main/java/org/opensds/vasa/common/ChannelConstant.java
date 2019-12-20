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
package org.opensds.vasa.common;


/**
 * <功能详细描述>
 *
 * @author z90004997
 * @version [版本号V001R010C00, 2011-12-14]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public class ChannelConstant {
    /**
     * 下发命令超时时间
     */
    public static final int CMD_TIME_OUT = 3 * 60 * 1000;

    /**
     * N8000等待较长时间的命令  下发命令超时时间 30分钟
     */
    public static final int CMD_MAX_TIME_OUT = 30 * 60 * 1000;

    /**
     * 连接超时时间
     */
    public static final int CONNECT_TIME_OUT = 20 * 1000;

    /**
     * 默认端口号
     */
    public static final int DEFAULT_PORT_ID = 22;

    /**
     * 默认编码方式:utf-8
     */
    public static final String DEFAULT_CHARSET = "UTF-8";

    /**
     * 缓冲字节数
     */
    public static final int BUFFER_SIZE = 4096;

    /**
     * CLI命令的通用结束符
     */
    public static final String CLI_COMMON_END_SIGN = ":/>";

    /**
     * VIS CLI命令的结束符
     */
    public static final String VIS_CLI_COMMON_END_SIGN = "VIS CLI:";

    /**
     * 通用的命令换行符
     */
    public static final String COMMON_COMMAND_ENTER_SIGN = "\n";

    /**
     * 默认的CLI信息收集命令
     */
    public static final String DEFAULT_CLI_COMMAND_DATA_COLLECT = "datacollect";

    /**
     * CLI命令不存在
     */
    public static final String CLI_COMMAND_NOT_EXSIT = "not exist!";

    /**
     * 默认的信息收集路径
     */
    public static final String DEFAULT_COLLECT_DATA_SERVER_PATH = "/ServiceTool/DataCollect.zip";

    /**
     * XML通道节点重启是否需要修改超时时间的判断依据
     */
    public static final String SET_XML_TIME_OUT = " SetXmlTimeOut ";

}
