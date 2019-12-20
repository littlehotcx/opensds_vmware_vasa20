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

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;


/**
 * 解析properties文件，并提供对其中的文本进行替换的功能
 *
 * @author f00102803
 * @version [版本号V001R010C00, 2011-12-14]
 * @since ISM Server:fileUtils
 */
public class PropertiesFileParser extends ResourceBundle {
    private static org.apache.logging.log4j.Logger LOGGER = org.apache.logging.log4j.LogManager
            .getLogger(PropertiesFileParser.class);

    /**
     * 公共属性DEFAULT_NULL_STRING
     */
    public static final String DEFAULT_NULL_STRING = "--";

    private ClassLoader loader = Thread.currentThread()
            .getContextClassLoader();


    private ResourceBundle bundle = null;


    private Map<String, String> keyWordMap;//需要替换文本的映射列表

    private Map<String, String> thisMap;//本配置文件所有配置信息构成的Map


    /**
     * 包装一个已有的数据包
     *
     * @param bundle 需要被包裝的数据包
     */
    public PropertiesFileParser(ResourceBundle bundle) {
        this.bundle = bundle;

        keyWordMap = getDefalutKeyMap();
    }

    public PropertiesFileParser(String propertyRelativePath) {
        this(propertyRelativePath, Util.getOSLocaleDefaultEn());
    }

    /**
     * 封装当前的数据包，并且指定一个关键字映射
     *
     * @param propertyRelativePath 方法参数：propertyRelativePath
     * @param locale               方法参数：locale
     * @param keyWordsMap          方法参数：keyWordsMap
     */
    public PropertiesFileParser(String propertyRelativePath, Locale locale,
                                Map<String, String> keyWordsMap) {
        this(propertyRelativePath, locale);
        keyWordMap = keyWordsMap;
    }

    public PropertiesFileParser(String propertyRelativePath, Locale locale,
                                ClassLoader classLoader) {
        // 先在classLoader下加载配置文件
        try {
            if (null != classLoader) {
                this.bundle = ResourceBundle.getBundle(propertyRelativePath,
                        locale,
                        classLoader);
                setClassLoader(classLoader);
            }
        } catch (MissingResourceException e) {
            toLog(e);
        }

        this.init(propertyRelativePath, locale);
    }

    /**
     * 封装当前的数据包，并且指定一个关键字映射
     *
     * @param propertyRelativePath 方法参数：propertyRelativePath
     * @param locale               方法参数：locale
     * @param classLoader          方法参数：classLoader
     * @param keyWordsMap          方法参数：keyWordsMap
     */
    public PropertiesFileParser(String propertyRelativePath, Locale locale,
                                ClassLoader classLoader, Map<String, String> keyWordsMap) {
        this(propertyRelativePath, locale, classLoader);
        keyWordMap = keyWordsMap;
    }

    /**
     * 封装当前的数据包，并且指定一个关键字映射
     *
     * @param propertyRelativePath 方法参数：propertyRelativePath
     * @param keyWordsMap          方法参数：keyWordsMap
     */
    public PropertiesFileParser(String propertyRelativePath,
                                Map<String, String> keyWordsMap) {
        this(propertyRelativePath);
        keyWordMap = keyWordsMap;
    }


    public PropertiesFileParser(String propertyRelativePath, Locale locale) {
        try {
            this.bundle = ResourceBundle.getBundle(propertyRelativePath,
                    locale,
                    loader);

            //start AZ7D01886修改，y90003176，增加文本内容索引功能
            //            _keyWordsMap = getDefalutKeyMap(propertyRelativePath, locale);
            keyWordMap = getDefalutKeyMap();
            //end AZ7D01886修改，y90003176，增加文本内容索引功能
        } catch (Exception e) {
            LOGGER.error(e);
        }
    }

    /*
     * 设置类加载器
     *
     * @param cl 类加载器
     */
    private void setClassLoader(ClassLoader cl) {
        this.loader = cl;
    }

    /**
     * 获取类加载器
     *
     * @return ClassLoader 类加载器
     */
    public ClassLoader getClassLoader() {
        return this.loader;
    }

    /**
     * 获取所有的关键字
     *
     * @return Enumeration<String> 所有的关键字
     */
    @Override
    public Enumeration<String> getKeys() {
        return this.bundle.getKeys();
    }

    /**
     * 方法 ： getKeyValueMap
     *
     * @return Map<String, String> 返回结果
     */
    public Map<String, String> getKeyValueMap() {
        if (null == thisMap) {
            thisMap = new HashMap<String, String>(0);

            Enumeration<String> enums = getKeys();
            while (enums.hasMoreElements()) {
                String key = enums.nextElement();
                thisMap.put(key, getString(key));
            }
        }
        Map<String, String> map = new HashMap<String, String>(thisMap);
        return map;

    }

    /**
     * 获取指定关键字关联的String型的参数，如果找不到则返回提供的缺省值
     *
     * @param key          参数关键字
     * @param defaultValue 找不到参数时返回的缺省值
     * @return String 返回结果
     */
    public String getStringArg(String key, String defaultValue) {
        String arg = getString(key);
        return arg.equals(DEFAULT_NULL_STRING) ? defaultValue : arg;
    }

    /**
     * 获取指定关键字关联的int型的参数，如果找不到则返回提供的缺省值
     *
     * @param key          参数关键字
     * @param defaultValue 找不到参数时返回的缺省值
     * @return int 返回结果
     */
    public int getIntArg(String key, int defaultValue) {
        String arg = getString(key);
        try {
            return Integer.parseInt(arg);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * 获取指定关键字关联的long型的参数，如果找不到则返回提供的缺省值
     *
     * @param key          参数关键字
     * @param defaultValue 找不到参数时返回的缺省值
     * @return long 返回结果
     */
    public long getLongArg(String key, long defaultValue) {
        String arg = getString(key);
        try {
            return Long.parseLong(arg);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * 从此资源包中获取给定键的对象。如果此资源包未包含给定键的对象，则返回 null。
     *
     * @param key 所需对象的键。
     * @return Object 返回结果
     */
    @Override
    protected Object handleGetObject(String key) {
        Object obj = null;
        try {
            obj = bundle.getObject(key);
        } catch (MissingResourceException ex) {
            toLog(ex);
            return DEFAULT_NULL_STRING;
        }

        //如果当前的对象是文本或者文本数组对象，替换索引的文本
        if (null != obj) {
            if (obj instanceof String) {
                obj = replaceKeyWords((String) obj);
            } else if (obj instanceof String[]) {
                obj = replaceKeyWords((String[]) obj);
            }
        }

        return obj;
    }

    /*
     * 记录日志
     *
     * @param t 异常
     */
    private void toLog(Throwable t) {
        //Start y90003176 BE1D03600 添加资源日志开关
        if (Boolean.valueOf(System.getProperty("ENABLE_RESOURCE_LOG", "false"))) {
            /**当找到不资源时，用专用的日志记录下来*/
            LOGGER.warn(t.getMessage());
        }
        //End y90003176 BE1D03600 添加资源日志开关
    }

    /*
     * 使用map中的键所对应的文本值替换，输入文本中的相应内容
     *
     * @param input 需要替换的信息
     * @return String 替换后的信息
     */
    private String replaceKeyWords(String input) {
        if (null == keyWordMap || null == input) {
            return input;
        }

        String output = input;
        for (String key : keyWordMap.keySet()) {
            String value = keyWordMap.get(key);
            output = output.replaceAll(key, value);
        }

        return output;
    }

    /*
     * 使用map中的键所对应的文本值替换，输入文本中的相应内容
     *
     * @param input 需要替换的信息
     * @return String[]  替换后的信息
     */
    private String[] replaceKeyWords(String[] input) {
        String[] output = new String[input.length];

        for (int i = 0; i < input.length; i++) {
            output[i] = replaceKeyWords(output[i]);
        }
        return output;
    }

    // start AZ7D01886修改，y90003176，增加文本内容索引功能
    /*
     * 返回默认的文本映射
     *
     * @param propertyRelativePath
     * @param locale 本地化对象
     * @return Map<String,String> OEM资源信息集合
     */
    //    private Map<String, String> getDefalutKeyMap(String propertyRelativePath,
    //            Locale locale)
    private Map<String, String> getDefalutKeyMap() {
        return new HashMap<String, String>(0);
    }

    // end AZ7D01886修改，y90003176，增加文本内容索引功能

    /*
     * 初始化
     */
    private void init(String propertyRelativePath, Locale locale) {
        //如果没有在指定的配置文件中找到，则从包内部查找
        if (null == this.bundle) {
            this.bundle = ResourceBundle.getBundle(propertyRelativePath,
                    locale,
                    loader);
        }

        //start AZ7D01886修改，y90003176，增加文本内容索引功能
        //        _keyWordsMap = getDefalutKeyMap(propertyRelativePath, locale);
        keyWordMap = getDefalutKeyMap();
        //end AZ7D01886修改，y90003176，增加文本内容索引功能
    }
}
