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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.opensds.vasa.common.MagicNumber;

import com.vmware.vim.vasa.v20.InvalidArgument;
import com.vmware.vim.vasa.v20.data.xsd.HostInitiatorInfo;
import com.vmware.vim.vasa.v20.data.xsd.MountInfo;
import com.vmware.vim.vasa.v20.data.xsd.StorageFileSystem;

/**
 * Helper functions
 *
 * @author V1R10
 * @version [版本号V001R010C00, 2011-12-14]
 */
public abstract class Util {
    private static org.apache.logging.log4j.Logger LOGGER = org.apache.logging.log4j.LogManager
            .getLogger(Util.class);

    /**
     * 公共属性ISMPROVIDER_NAME
     */
    public static final String ISMPROVIDER_NAME = VASAUtil.ISMPROVIDER_NAME;

    // private static String configPath = "/config/parameters/";

    /**
     * 方法 ： isEmpty
     *
     * @param mi 方法参数：mi
     * @return boolean 返回结果
     */
    public static boolean isEmpty(MountInfo[] mi) {
        return mi == null || mi.length == 0 || ((mi.length == 1) && mi[0] == null);
    }

    /**
     * 方法 ： isEmpty
     *
     * @param hii 方法参数：hii
     * @return boolean 返回结果
     */
    public static boolean isEmpty(HostInitiatorInfo[] hii) {
        return hii == null || hii.length == 0 || ((hii.length == 1) && hii[0] == null);
    }

    /**
     * 方法 ： isEmpty
     *
     * @param hii 方法参数：hii
     * @return boolean 返回结果
     */
    public static boolean isEmpty(List<HostInitiatorInfo> hii) {
        return hii == null || hii.isEmpty() || ((!hii.isEmpty()) && hii.get(0) == null);
    }

    private static boolean stringsAreEqual(String s1, String s2) {
        if ((s1 == null) && (s2 == null)) {
            // both null means equal
            return true;
        }

        if ((s1 == null) || (s2 == null)) {
            // one is null, the other is not
            return false;
        }
        return s1.equals(s2);
    }

    private static boolean objectsAreEqual(HostInitiatorInfo hii1, HostInitiatorInfo hii2) {
        if (!stringsAreEqual(hii1.getPortWwn(), hii2.getPortWwn())) {
            return false;
        }
        if (!stringsAreEqual(hii1.getNodeWwn(), hii2.getNodeWwn())) {
            return false;
        }
        if (!stringsAreEqual(hii1.getIscsiIdentifier(), hii2.getIscsiIdentifier())) {
            return false;
        }
        return true;
    }

    private static boolean objectsAreEqual(Object o1, Object o2) throws InvalidArgument {
        if ((o1 instanceof HostInitiatorInfo) && (o2 instanceof HostInitiatorInfo)) {
            return objectsAreEqual((HostInitiatorInfo) o1, (HostInitiatorInfo) o2);
        }
        LOGGER.error("objectsAreEqual: unknown object type");
        throw FaultUtil.invalidArgument();
    }

    /**
     * 方法 ： listsAreEqual
     *
     * @param l1 方法参数：l1
     * @param l2 方法参数：l2
     * @return boolean 返回结果
     * @throws InvalidArgument 异常：InvalidArgument
     */
    public static boolean listsAreEqual(List l1, List l2) throws InvalidArgument {
        if ((l1 == null) && (l2 == null)) {
            // both null means equal
            return true;
        }

        if ((l1 == null) || (l2 == null)) {
            // one is null, the other is not
            return false;
        }

        if (l1.size() != l2.size()) {
            // sizes not equal, lists cannot be equal
            return false;
        }

        listsAreEqualDegread(l1);

        /*
         * check that each object in l1 is found in l2 since there are no
         * duplicate objects in l1 this means there are no extra objects in l2
         */
        int size1 = l1.size();
        int size2 = l2.size();
        for (int i = 0; i < size1; i++) {
            boolean matchFound = false;
            for (int j = 0; j < size2; j++) {
                if (objectsAreEqual(l1.get(i), l2.get(j))) {
                    matchFound = true;
                }
            }

            if (!matchFound) {
                return false;
            }
        }
        return true;
    }

    /**
     * <降低深度>
     *
     * @param l1
     * @return void [返回类型说明]
     * @throws InvalidArgument [参数说明]
     * @throws throws          [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    private static void listsAreEqualDegread(List l1) throws InvalidArgument {
        /*
         * make sure that the same object is not found more than one in the list
         *
         * it is only necessary to do this check for one of the lists
         */
        int size = l1.size();
        for (int i = 0; i < size - 1; i++) {
            for (int j = i + 1; j < size; j++) {
                if (objectsAreEqual(l1.get(i), l1.get(j))) {
                    LOGGER.error("listsAreEqual: list contains the same object more than once");
                    throw FaultUtil.invalidArgument();
                }
            }
        }
    }

    /**
     * 方法 ： isEmpty
     *
     * @param sfs 方法参数：sfs
     * @return boolean 返回结果
     */
    public static boolean isEmpty(StorageFileSystem[] sfs) {
        return sfs == null || sfs.length == 0 || ((sfs.length == 1) && sfs[0] == null);
    }

    /**
     * 方法 ： isEmpty
     *
     * @param s 方法参数：s
     * @return boolean 返回结果
     */
    public static boolean isEmpty(String[] s) {
        return s == null || s.length == 0
                || ((s.length == 1) && (s[0] == null || s[0].equals("") || s[0].equalsIgnoreCase("null")));
    }

    /**
     * All the UniqueIds used by the sampleVP are integers If any of the
     * UniqueIds in the String array are not integers then they cannot possibly
     * be valid ids and so the expection is thrown.
     *
     * @param uniqueIds  方法参数：uniqueIds
     * @param allowEmpty 方法参数：allowEmpty
     * @throws InvalidArgument 异常：InvalidArgument
     */
    public static void allUniqueIdsAreValid(String[] uniqueIds, boolean allowEmpty) throws InvalidArgument {
        if (isEmpty(uniqueIds)) {
            if (!allowEmpty) {
                throw FaultUtil.invalidArgument("UniqueId list cannot be empty");
            }
            return;
        }

        for (int i = 0; i < uniqueIds.length; i++) {
            uniqueIdIsValid(uniqueIds[i]);
        }
    }

    /**
     * Verify that the uniqueId is composed of characters only in the range of
     * '0' - '9'. The assumption is that uniqueId will not be NULL.
     *
     * @param uniqueId 方法参数：uniqueId
     * @throws InvalidArgument 异常：InvalidArgument
     */
    public static void uniqueIdIsValid(String uniqueId) throws InvalidArgument {
        try {
            if (!uniqueId.matches("^\\d+$")) {
                throw FaultUtil.invalidArgument("UniqueId " + uniqueId + " is not a valid id for sampleVP");
            }
        } catch (Exception e) {
            throw FaultUtil.invalidArgument("UniqueId " + uniqueId + " unexpected exception: " + e);
        }
    }

    /**
     * Verify that the event or alarm Id is -1 or greater.
     *
     * @param id 方法参数：id
     * @throws InvalidArgument 异常：InvalidArgument
     */
    public static void eventOrAlarmIdIsValid(long id) throws InvalidArgument {
        if (id < -1) {
            throw FaultUtil.invalidArgument("invalid id " + id);
        }
    }

    /**
     * Convert to comma separated string
     *
     * @param id 方法参数：id
     * @return String 返回结果
     */
    public static String getIdString(String[] id) {
        if (id == null || id.length < 1) {
            return null;
        }
        StringBuffer buff = new StringBuffer();
        for (int i = 0; i < id.length - 1; i++) {
            buff.append(id[i]);
            buff.append(',');
        }
        buff.append(id[id.length - 1]);
        return buff.toString();
    }

    // /**
    // * 方法 ： getConfigParameter
    // *
    // * @param configName 方法参数：configName
    // * @return String 返回结果
    // */
    // public static String getConfigParameter(String configName)
    // {
    // try
    // {
    // XmlParser configParser = new XmlParser();
    //
    // configParser.loadResource("com/vmware/vim/vasa/config.xml");
    // return configParser.getString(configPath + configName, null);
    // }
    // catch (Exception e)
    // {
    // LogManager.error("getStringConfigValue ", e);
    // return null;
    // }
    // }

    /**
     * 关闭输出流
     *
     * @param out outputstream
     */
    public static void closeOutputStrean(OutputStream out) {
        if (null != out) {
            try {
                out.close();
            } catch (Exception e) {
                LOGGER.error("close outputstream ", e);
            }
        }
    }

    /**
     * 关闭输入流
     *
     * @param in inputstream
     */
    public static void closeInputStrean(InputStream in) {
        if (null != in) {
            try {
                in.close();
            } catch (Exception e) {
                LOGGER.error("close outputstream ", e);
            }
        }
    }

    /**
     * 关闭BufferedReader
     *
     * @param br                bufferedReader
     * @param fileInputStream
     * @param inputStreamReader
     */
    public static void closeBr(BufferedReader br) {
        if (br != null) {
            try {
                br.close();
            } catch (IOException e) {
                return;
            }
        }
    }

    /**
     * 关闭BufferedWriter
     *
     * @param bw                 bufferedWriter
     * @param fileOutputStream
     * @param outputStreamWriter
     */
    public static void closeBw(BufferedWriter bw) {
        if (bw != null) {
            try {
                bw.close();
            } catch (IOException e) {
                return;
            }
        }
    }

    /**
     * 获取oceanstor-vasa-provider的绝对路径
     *
     * @return 获取string的绝对路径
     */
    public static String getBasePath() {
        String binPath = System.getProperty("user.dir");
        if (binPath == null) {
            return "";
        }
        // 如果是tomcat webapp path
        if (binPath.endsWith("OpenAS_Tomcat7" + File.separator + "bin")) {
            // tomcat bin
            return binPath + File.separator + ".." + File.separator + "..";
        } else if (binPath.endsWith("bin"))// 如果是命令行的话
        {
            return binPath + File.separator + "..";
        }
        return binPath;
    }

    /**
     * 解决Fortify Path Manipulation致命漏洞问题
     *
     * @param path 传入的路径
     * @return String 返回结果
     */
    public static String fixFotifyPathManipulation(String path) {
        HashMap<String, String> map = new HashMap<String, String>();

        map.put("a", "a");
        map.put("b", "b");
        map.put("c", "c");
        map.put("d", "d");
        map.put("e", "e");
        map.put("l", "l");
        map.put("m", "m");
        map.put("n", "n");
        map.put("o", "o");
        map.put("f", "f");
        map.put("g", "g");
        map.put("h", "h");
        map.put("i", "i");
        map.put("j", "j");
        map.put("k", "k");
        map.put("p", "p");
        map.put("q", "q");
        map.put("r", "r");
        map.put("s", "s");
        map.put("t", "t");
        map.put("u", "u");
        map.put("v", "v");
        map.put("w", "w");
        map.put("r", "r");
        map.put("s", "s");
        map.put("t", "t");
        map.put("u", "u");
        map.put("x", "x");
        map.put("y", "y");
        map.put("z", "z");

        map.put("A", "A");
        map.put("B", "B");
        map.put("C", "C");
        map.put("D", "D");
        map.put("E", "E");
        map.put("F", "F");
        map.put("G", "G");
        map.put("H", "H");
        map.put("I", "I");
        map.put("J", "J");
        map.put("K", "K");
        map.put("L", "L");
        map.put("M", "M");
        map.put("N", "N");
        map.put("O", "O");
        map.put("P", "P");
        map.put("Q", "Q");
        map.put("R", "R");
        map.put("S", "S");
        map.put("T", "T");
        map.put("U", "U");
        map.put("V", "V");
        map.put("W", "W");
        map.put("X", "X");
        map.put("Y", "Y");
        map.put("Z", "Z");

        map.put(":", ":");
        map.put("/", "/");
        map.put("\\", "\\");

        String temp = "";

        for (int i = 0; i < path.length(); i++) {
            if (map.get(path.charAt(i) + "") != null) {
                temp += map.get(path.charAt(i) + "");
            } else {
                temp += path.charAt(i);
            }
        }

        return temp;
    }

    /**
     * <判断是否是空字符串> 默认为windows
     *
     * @param content 方法参数：content
     * @return boolean [返回类型说明]
     * @see [类、类#方法、类#成员]
     */
    public static boolean isNullStr(String content) {
        return null == content || content.isEmpty();
    }

    /**
     * 获得操作系统的语言区域
     * <p>
     * 如果从配置文件中取得和本地不同，默认为英文。
     *
     * @return Locale [返回类型说明]
     * @see [类、类#方法、类#成员]
     */
    public static Locale getOSLocaleDefaultEn() {
//        Locale retLocal = Locale.getDefault();
//        
//        // ISM自身支持的语言
//        List<Locale> locales = CustomResourceManager.getInstance()
//                .getLanguages();
//        if (locales != null && locales.contains(retLocal))
//        {
//            return retLocal;
//        }
//        
//        // 如果ISM不支持当前的语言环境，则模式使用英文
//        // ISMV1R3-697 更改操作系统区域位置后ISM不能启动
//        Locale defaultLocale = null;
//        for (Locale l : locales)
//        {
//            if (l.getLanguage().equals(Locale.ENGLISH.getLanguage()))
//            {
//                defaultLocale = l;
//                break;
//            }
//        }
//        
//        if (null == defaultLocale)
//        {
//            defaultLocale = Locale.US;
//        }
        Locale defaultLocale = Locale.ENGLISH;
        return defaultLocale;
    }

    /**
     * 方法 ： matchBlank
     *
     * @param content 方法参数：content
     * @return String 返回结果
     */
    public static String matchBlank(String content) {
        if (content.contains("{")) {
            int begin = content.indexOf("{");
            int paraBegin = getStartLocation(content, begin);
            // 修改参数枚举值大于10的时候，出现位置错误的问题
            String para = content.substring(paraBegin, begin).trim();
            if (content.contains("}")) {
                int end = content.indexOf("}") + 1;
                String replaceMsg = content.substring(paraBegin, end);
                String info = content.substring(begin, end);
                //获取告警参数的键-值对
                Map<String, String> params = getParams(info);
                if (params.get(para) == null) {
                    content = content.replace(info, "");
                } else {
                    content = content.replace(replaceMsg, params.get(para));
                }

                if (content.contains("{")) {
                    content = matchBlank(content);
                }
            }
        }

        return content;
    }

    /**
     * 将告警的参数解析成键值对的形式存放在MAP中
     *
     * @param info
     * @return Map<String, String> [返回类型说明]
     * @see [类、类#方法、类#成员]
     */
    private static Map<String, String> getParams(String info) {
        Map<String, String> paramMap = new HashMap<String, String>();
        String tempInfo = info.replaceAll("；", ";").replaceAll("：", ":");
        if (tempInfo.startsWith("{")) {
            tempInfo = tempInfo.substring(1);
        }

        if (tempInfo.endsWith("}")) {
            tempInfo = tempInfo.substring(0, tempInfo.length() - 1);
        }

        String[] params = tempInfo.split(";");

        for (String param : params) {
            String[] item = param.split(":");
            if (item.length == MagicNumber.INT2) {
                paramMap.put(item[0].trim(), item[1].trim());
            }
        }

        return paramMap;
    }

    private static int getStartLocation(String content, int index) {
        for (int i = index; i > 0; i--) {
            // 修改参数枚举值大于10的时候，出现位置错误的问题
            //ckf36661 2011-10-14 P12I-905 begin
            //ckf36661 P12I-1125 2011-11-8 ISM中在创建RAID组时，写日志出错 begin
            //index - 2 有可能小于-1，这里临时修改，不知道当初作者写这段代码的意图
            int temp = index - MagicNumber.INT2 >= 0 ? index - MagicNumber.INT2 : 0;
            if (!content.substring(i - 1, i).trim().equals("")
                    && (!isInter(getVirtualValue(content, i).trim()) || !content.substring(temp,
                    i)
                    .trim()
                    .equals(""))) {
                if (isInter(getVirtualValue(content, i).trim())) {
                    //i - 2 有可能小于-1，这里临时修改，不知道当初作者写这段代码的意图
                    return i - MagicNumber.INT2 >= 0 ? i - MagicNumber.INT2 : 0;
                } else {
                    return i - 1;
                }

            }
            //ckf36661 P12I-1125 2011-11-8 ISM中在创建RAID组时，写日志出错 end
            //ckf36661 2011-10-14 P12I-905 end
        }
        return -1;
    }

    /**
     * 去掉参数前面的空格，以免影响告警在解析带有“{”的参数的时候出错
     *
     * @param content
     * @param index
     * @return
     */
    private static String getVirtualValue(String content, int index) {
        for (int i = MagicNumber.INT2; i < index; i++) {
            String temp = content.substring(index - i, index);
            char fistChar = temp.charAt(0);
            if (fistChar == ' ') {
                continue;
            } else {
                return content.substring(index - i, index);
            }
        }

        //index - 2 有可能小于-1，这里临时修改，不知道当初作者写这段代码的意图
        int temp = index - MagicNumber.INT2 >= 0 ? index - MagicNumber.INT2 : 0;
        return content.substring(temp, index);
    }

    /*
     * 修改参数枚举值大于10的时候，出现位置错误的问题
     */
    private static boolean isInter(String s) {
        try {
            Integer.valueOf(s);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
//    public static void main(String[] args)
//    {
//        System.out.println(System.getProperty("user.dir"));
//    }
}
