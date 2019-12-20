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
package org.opensds.vasa.vasa.internal;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.opensds.vasa.common.IsmConstant;
import org.opensds.vasa.common.MagicNumber;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.opensds.vasa.vasa.util.TimeUtil;
import org.opensds.vasa.vasa.util.Util;
import org.opensds.vasa.vasa.util.XmlParser;


public class Event implements Serializable {
    private static org.apache.logging.log4j.Logger LOGGER = org.apache.logging.log4j.LogManager
            .getLogger(Event.class);

    private static final long serialVersionUID = -4467061853203606299L;

    //复合参数分割符常量，约定为ascill码为"$("
    private static final String MIXED_PARAM_SEPERATOR = "$(";

    //"$("查找替换过程中对应的正则表达式
    private static final String MIXED_PARAM_SEPERATOR_REGEX = "\\$\\(";

    private static final String ALARM_PARAMETER_SEPARATOR = ",";//告警参数分隔符

    //告警参数解释字符串中的占位字符串(正则表达式)前缀
    private static final String ALARM_PARAMETER_PLACEHOLDER_PREFIX = "##";

    //配置文件
    private static EventConfLoader cfgloader = new EventConfLoader();

    //参数替换的字符串
    private static final String PARAM_RAPLACE_REGEX = "@@@";

    /**
     * 告警配置数据
     *
     * @author j00102777
     * @version ISM-V V100R001C00
     * @date 2013-2-21
     */
    protected static class EventConfigData {
        private static Map<Long, String> consistentGrpMap =
                new HashMap<Long, String>();

        //事件配置文件内容装载
        private Map<Long, Definition> definitionMap =
                new HashMap<Long, Event.Definition>();

        private Map<Long, Definition> oprateMap =
                new HashMap<Long, Event.Definition>();

        private Locale locale;

        /**
         * <默认构造函数>
         *
         * @param loc locale
         */
        public EventConfigData(Locale loc) {
            locale = loc;
        }

        static {
            initConsistentGrp(consistentGrpMap);
        }

        private static void initConsistentGrp(Map<Long, String> consGrpMap) {

            consGrpMap.put(EVENT_ID_1, "");
            consGrpMap.put(EVENT_ID_2, "1");
            consGrpMap.put(EVENT_ID_3, "1");

            consGrpMap.put(EVENT_ID_4, "");

            consGrpMap.put(EVENT_ID_5, "1");
            consGrpMap.put(EVENT_ID_6, "1");


            consGrpMap.put(EVENT_ID_7, "1");

            consGrpMap.put(EVENT_ID_8, "1");

            consGrpMap.put(EVENT_ID_9, "1");

            consGrpMap.put(EVENT_ID_10, "1");

            consGrpMap.put(EVENT_ID_11, "1");

            consGrpMap.put(EVENT_ID_12, "1");

            consGrpMap.put(EVENT_ID_13, "1");

            consGrpMap.put(EVENT_ID_14, "1");

            consGrpMap.put(EVENT_ID_15, "");

            consGrpMap.put(EVENT_ID_16, "1");

            consGrpMap.put(EVENT_ID_17, "1");

            consGrpMap.put(EVENT_ID_18, "1,2");

            consGrpMap.put(EVENT_ID_19, "1,2");

            consGrpMap.put(EVENT_ID_20, "1");

            consGrpMap.put(EVENT_ID_21, "1");

            consGrpMap.put(EVENT_ID_22, "1");

            consGrpMap.put(EVENT_ID_23, "1");

            consGrpMap.put(EVENT_ID_24, "1");
        }


        /**
         * <功能详细描述>
         *
         * @param loc Locale
         */
        public void setLocale(Locale loc) {
            locale = loc;
        }

        /**
         * <功能详细描述>
         *
         * @return Locale [返回类型说明]
         */
        public Locale getLocale() {
            return locale;
        }

        /**
         * <功能详细描述>
         *
         * @return Map<Long, Definition> [返回类型说明]
         */
        public Map<Long, Definition> getDefintionMap() {
            return definitionMap;
        }

        /**
         * <功能详细描述>
         *
         * @return Map<Long, Definition> [返回类型说明]
         */
        public Map<Long, Definition> getOprateMap() {
            return oprateMap;
        }

        /**
         * <功能详细描述>
         *
         * @return Map<Long, String> [返回类型说明]
         */
        public static Map<Long, String> getConsistentGrpMap() {
            return consistentGrpMap;
        }
    }

    private static Map<Locale, EventConfigData> sharedEventConfigDataLocal =
            new HashMap<Locale, EventConfigData>();

    private static final long EVENT_ID_1 = 0x2002020A0014L;

    private static final long EVENT_ID_2 = 0x2002020A0015L;

    private static final long EVENT_ID_3 = 0x2002020A0016L;

    private static final long EVENT_ID_4 = 0x22020A0014L;

    private static final long EVENT_ID_5 = 0x22020A0015L;

    private static final long EVENT_ID_6 = 0x22020A0016L;

    private static final long EVENT_ID_7 = 0x22020A0017L;

    private static final long EVENT_ID_8 = 0x22020A0018L;

    private static final long EVENT_ID_9 = 0x22020A0019L;

    private static final long EVENT_ID_10 = 0x22020A001AL;

    private static final long EVENT_ID_11 = 0x22020A001BL;

    private static final long EVENT_ID_12 = 0x22020A001CL;

    private static final long EVENT_ID_13 = 0x22020A001DL;

    private static final long EVENT_ID_14 = 0x22020A001EL;

    private static final long EVENT_ID_15 = 0x22020A001FL;

    private static final long EVENT_ID_16 = 0x22020A0020L;

    private static final long EVENT_ID_17 = 0x22020A0021L;

    private static final long EVENT_ID_18 = 0x22020A0022L;

    private static final long EVENT_ID_19 = 0x22020A0023L;

    private static final long EVENT_ID_20 = 0x22020A0024L;

    private static final long EVENT_ID_21 = 0x22020A0025L;

    private static final long EVENT_ID_22 = 0x22020A0026L;

    private static final long EVENT_ID_23 = 0x22020A0027L;

    private static final long EVENT_ID_24 = 0x22020A0028L;

    private String name = ""; //事件名称

    private String detail = ""; //事件详细信息

    private String description = ""; //事件详细描述

    private String suggestion = ""; //事件修复建议

    private String eventParam;

    private Locale locale;

    /**
     * 以逗号隔开的参数，这个参数是经过转换的，比如，0代表A空，转换后就是A
     */
    private StringBuffer parserdParam = new StringBuffer();

    private long eventID; //事件id

    private Identifier identifier; //网管内告警唯一标识

    private Type type; //事件类型

    private Level level; //事件级别。

    private long startTime; //事件开始时间

    private long recoverTime; //事件恢复时间（包括手动清除和自动恢复）

    private long confirmTime; //事件确认时间

    //for json serialize
    private String deviceId;
    private long deviceSN;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public long getDeviceSN() {
        return deviceSN;
    }

    public void setDeviceSN(long deviceSN) {
        this.deviceSN = deviceSN;
    }

    /**
     * 告警标识抽象出的数据结构
     * 根据此结构可唯一定位一条告警
     *
     * @author V1R10
     * @version [版本号V001R010C00, 2011-12-14]
     */
    public static class Identifier implements Serializable {
        //序列化相关
        private static final long serialVersionUID = 8468503836385192415L;

        private long alarmSN;//告警序列号

        private String deviceID;//告警所属设备Id

        public Identifier() {

        }

        /**
         * 全量属性构造函数，用于构造系统中对告警的唯一标识
         *
         * @param deviceID 设备ID
         * @param alarmSN  告警流水号
         */
        public Identifier(String deviceID, long alarmSN) {
            this.deviceID = deviceID;
            this.alarmSN = alarmSN;
        }

        /**
         * 重写equals方法，使用deviceID和alarmSN两个属性判定
         *
         * @param object 需要与此对象比较的对象
         * @return boolean 返回结果
         */
        @Override
        public boolean equals(Object object) {
            //同一个对象
            if (object == this) {
                return true;
            }

            if (null == deviceID || null == object
                    || !(object instanceof Identifier)) {
                return false;
            }

            Identifier that = (Identifier) object;

            return deviceID.equals(that.deviceID)
                    && alarmSN == that.alarmSN;
        }

        /**
         * 获取告警流水号
         *
         * @return long 返回结果
         */
        public long getEventSN() {
            return alarmSN;
        }

        /**
         * 获取告警所属阵列的ID
         *
         * @return String 返回结果
         */
        public String getDeviceID() {
            return deviceID;
        }

        /**
         * 重写hashCode方法，使用deviceID和alarmSN两个属性计算
         *
         * @return int 计算出的hashCode值
         */
        @Override
        public int hashCode() {
            long hashCode = IsmConstant.CONST_17;

            hashCode = IsmConstant.CONST_37 * hashCode + this.getClass().hashCode();
            hashCode = IsmConstant.CONST_37 * hashCode + this.deviceID.hashCode();
            hashCode = IsmConstant.CONST_37 * hashCode + alarmSN;

            return (int) hashCode;
        }

        /**
         * 该唯一标识的字符串形式
         *
         * @return String 返回结果
         */
        @Override
        public String toString() {
            return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).appendSuper(super.toString())
                    .append("DeviceID", deviceID)
                    .append("AlarmSN", alarmSN)
                    .toString();
        }

    }

    /**
     * 告警级别枚举
     * SMI-S定义理多种告警级别，此处使用枚举进行强制合法性校验。具体各级别定义如下：
     * 2、信息；3、警告；5、主要；6、紧急
     */
    public static enum Level {
        /**
         * 枚举变量
         */
        Info(2, "info.png"),
        /**
         * 枚举变量
         */
        Warning(3, "warning.png"),
        /**
         * 枚举变量
         */
        Major(5, "major.png"),
        /**
         * 枚举变量
         */
        Critical(6, "critical.png");


        //级别的枚举数值
        private int levelValue;

        private String iconKey;

        private String strKey = "Event_Level_" + name();//防止出现大量的临时文本对象创建


        //构造器
        private Level(int level, String iconKey) {
            this.levelValue = level;
            this.iconKey = iconKey;
        }

        /**
         * 根据输入的整形值返回具体级别的EventLevel对象
         *
         * @param nLevel 方法参数：nLevel
         * @return Level 返回结果
         */
        public static Level valueOf(int nLevel) {
            for (Level level : values()) {
                if (level.levelValue == nLevel) {
                    return level;
                }
            }

            return Info;
        }

        /**
         * 查询该级别的数值
         *
         * @return int 返回结果
         */
        public int getValue() {
            return this.levelValue;
        }

        /**
         * 查询当前的级别是否为故障级别
         *
         * @return boolean 返回结果
         */
        public boolean isFault() {
            switch (this) {
                case Warning:
                case Major:
                case Critical:
                    return true;
                default:
                    return false;
            }
        }

        /**
         * 方法 ： getIconKey
         *
         * @return String 返回结果
         */
        public String getIconKey() {
            return iconKey;
        }


    }

    /**
     * 类型枚举
     */
    public static enum Type {
        /**
         * 故障
         */
        FAULT(0),
        /**
         * 事件
         */
        EVENT(1),
        /**
         * 恢复
         */
        RESUME(2),
        /**
         * 操作日志
         */
        OPERATION_LOG(3),
        /**
         * 运行日志
         */
        RUNNING_LOG(4);


        private int val;

        private Type(int val) {
            this.val = val;
        }

        /**
         * 方法 ： valueOf
         *
         * @param nType 方法参数：nType
         * @return Type 返回结果
         */
        public static Type valueOf(int nType) {
            for (Type type : values()) {
                if (type.val == nType) {
                    return type;
                }
            }

            return EVENT;
        }

        /**
         * 查询该类型的数值
         *
         * @return int 返回结果
         */
        public int getValue() {
            return this.val;
        }
    }

    public Event() {

    }

    /**
     * 告警构造函数，根据事件的EventIdentifier构造事件
     *
     * @param identifier 方法参数：identifier
     */
    public Event(Identifier identifier) {
        this.identifier = identifier;
    }

    /**
     * 构造事件对象，不带参数解析
     *
     * @param identifier 唯一标识
     * @param level      告警级别
     * @param eventID    事件ID
     */
    public Event(Identifier identifier, Level level, long eventID) {
        this(identifier, level, eventID, "");
    }

    /**
     * 构造事件对象，带参数解析
     *
     * @param identifier 唯一标识
     * @param level      告警级别
     * @param eventID    事件ID
     * @param eventParam 事件参数
     */
    public Event(Identifier identifier, Level level, long eventID,
                 String eventParam) {
        this(identifier, level, eventID, eventParam, Util.getOSLocaleDefaultEn());
    }

    /**
     * 构造事件对象，带参数解析
     *
     * @param identifier 唯一标识
     * @param level      告警级别
     * @param eventID    事件ID
     * @param eventParam 事件参数
     * @param loc        Locale
     */
    public Event(Identifier identifier, Level level, long eventID,
                 String eventParam, Locale loc) {
        this.identifier = identifier;
        this.eventID = eventID;
        this.level = level;
        this.eventParam = eventParam;
        this.locale = loc;
//        EventConfigData eventConfigData = sharedEventConfigDataLocal.get(loc);
//        if (null != eventConfigData)
//        {
//          //通过告警文件生成描述等信息
//            parseEventParam(eventParam);           
//        }
    }


    /**
     * event 使用的locale
     *
     * @return Locale [返回类型说明]
     */
    public Locale getLocle() {
        return getEventCfgData() == null ? null : getEventCfgData().getLocale();
    }

    /**
     * 重写equals方法，如果两个告警的标识相等，则这两个告警相等
     *
     * @param object 需要对比的对象
     * @return boolean 返回结果
     */
    @Override
    public boolean equals(Object object) {
        if (null == object || !(object instanceof Event)) {
            return false;
        }

        if (object == this) {
            return true;
        }

        return identifier.equals(((Event) object).identifier);
    }

    //得到参数

    /**
     * 方法 ： getEventParam
     *
     * @return String 返回结果
     */
    public String getEventParam() {
        return this.eventParam;
    }

    //得到转换后的参数

    /**
     * 方法 ： getEventParserdParam
     *
     * @return String 返回结果
     */
    public String getEventParserdParam() {
        if (null == this.parserdParam
                || "".equals(this.parserdParam.toString())) {
            return this.eventParam;
        } else {
            if (parserdParam.toString().endsWith(ALARM_PARAMETER_SEPARATOR)) {
                parserdParam.setLength(parserdParam.length() - 1);
            }

            return this.parserdParam.toString();
        }
    }

    /**
     * 获取事件Id
     *
     * @return long 返回结果
     */
    public long getEventID() {
        return this.eventID;
    }

    /**
     * 获取告警级别
     *
     * @return Level 返回结果
     */
    public Level getLevel() {
        return this.level;
    }

    /**
     * 设置告警名称
     *
     * @param name 方法参数：name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取告警名称
     *
     * @return String 返回结果
     */
    public String getName() {
        return this.name;
    }

    /**
     * 获取告警确认时间
     *
     * @return long 返回结果
     */
    public long getConfirmTime() {
        return confirmTime;
    }

    /**
     * 获取告警描述
     *
     * @return String 返回结果
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * 设置告警描述
     *
     * @param desc 事件描述
     */
    public void setDescription(String desc) {
        this.description = desc;
    }

    /**
     * 获取告警恢复时间
     *
     * @return long 返回结果
     */
    public long getRecoverTime() {
        return recoverTime;
    }

    /**
     * 获取此条告警的唯一标识
     *
     * @return Identifier 返回结果
     */
    public Identifier getIdentifier() {
        return this.identifier;
    }

    /**
     * 设置事件的类型
     *
     * @param type 方法参数：type
     */
    public void setType(Type type) {
        this.type = type;
    }

    /**
     * 获取事件的类型
     *
     * @return Type 返回结果
     */
    public Type getType() {
        return this.type;
    }

    /**
     * 获取修复建议
     *
     * @return String 返回结果
     */
    public String getSuggestion() {
        return this.suggestion;
    }

    /**
     * 获取详细信息
     *
     * @return String 返回结果
     */
    public String getDetail() {
        return this.detail;
    }

    /**
     * 设置告警开始时间
     *
     * @param startTime 方法参数：startTime
     */
    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    /**
     * 获取告警开始时间
     *
     * @return long 返回结果
     */
    public long getStartTime() {
        return startTime;
    }

    /**
     * 重写hashCode方法，返回的hashCode根据类、arrayID、告警序列号计算得出
     *
     * @return int 返回结果
     */
    @Override
    public int hashCode() {
        int hashCode = IsmConstant.CONST_17;

        hashCode = IsmConstant.CONST_37 * hashCode + getClass().hashCode();
        hashCode += IsmConstant.CONST_37 * hashCode + identifier.hashCode();

        return hashCode;
    }

    /**
     * 判断是否为已清除告警
     *
     * @return boolean 返回结果
     */
    public boolean isConfirmed() {
        return confirmTime > 0;
    }

    /**
     * 根据恢复时间判定该告警是否为已恢复
     *
     * @return boolean 如果为已恢复告警，则返回true，否则返回false
     */
    public boolean isRecovered() {
        return recoverTime > 0;
    }

    /**
     * 设置确认时间
     * 清除时间一旦不为空或者零，则表示该告警已经被清除，因此同步修改清除状态
     *
     * @param confirmTime 清除告警的时间
     */
    public void setConfirmTime(long confirmTime) {
        this.confirmTime = confirmTime;
    }

    /**
     * 设置告警恢复时间，手动清除和自动恢复都将触发此方法调用
     *
     * @param recoverTime 告警恢复时间
     */
    public void setRecoverTime(long recoverTime) {
        this.recoverTime = recoverTime;
    }

    /**
     * 方法 ： toString
     *
     * @return String 返回结果
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).appendSuper(super.toString())
                .append("Identifier", identifier)
                .append("EventID", eventID)
                .append("EventName", name)
                .append("StartTime", new Date(startTime))
                .append("RecoverTime", new Date(recoverTime))
                .append("ConfirmTime", new Date(confirmTime))
                .append("EventLevel", level)
                .append("Description", description)
                .append("Suggest", suggestion)
                .toString();
    }

    protected EventConfigData getEventCfgData() {
        return sharedEventConfigDataLocal.get(locale);
    }

//    private void parseEventParam(String eventParamValue)
//    {
//        //获得指定事件ID对应的事件定义
//        Definition eventDef = getEventCfgData().getDefintionMap().get(eventID);
//        if (eventDef == null)
//        {
//            return;
//        }
//        
//        String paramDesc = eventDef.definitionParamDesc;
//        
//        // 事件名称
//        name = replaceParams(eventParamValue, paramDesc, eventDef.definitionName, false);
//        
//        // 事件详细信息
//        detail = replaceParams(eventParamValue, paramDesc, eventDef.definitionDetail, false);
//        
//        //事件修复建议
//        suggestion = replaceParams(eventParamValue,
//                paramDesc,
//                eventDef.definitionSuggestion,
//                false);
//        
//        // 事件描述
//        description = replaceParams(eventParamValue,
//                paramDesc,
//                eventDef.definitionDescription,
//                true);
//    }

    /**
     * 将设备上报的告警参数填充道告警参数解析中 (提供V1R2的##\\d{2}结构支持)
     *
     * @param parameters 设备上报告警的参数
     * @param rawContent 从配置文件中读取出来的告警参数解释
     * @param ifParserd  是否将解析后的值放入到解析后字符传中，（VASA需要,在description中转换字符串）
     * @return String 将参数填充道解释中的字符串
     * @author y90003176
     */
//    private String replaceParams(String params, String paramDescs,
//            String rawContent, boolean ifParserd)
//    {
//        //如果参数解释字符串不为null或者不为空字符，则进行替换
//        if (null != rawContent && rawContent.length() > 0 && null != params)
//        {
//            String[] paramList = splitParams(params);
//            Map<String, String> paramDescMap = parseParamDesc(paramDescs);
//            
//            // 处理告警参数需要转义的情况
//            //            String[] parserdParams =
//            // 在对内容做V1R2新结构替换。遍历所有的参数,以参数顺序构造替换标示
//            String number = null;
//            String regex = null;
//            String param = null;
//            String parserdParamValue = null;
//            String paramDesc = null;
//            String oprateType = null;
//            String paramDescKey = null;
//            for (int i = 0; null != paramList && i < paramList.length; i++)
//            {
//                //获得格式化后的数字，占用两位字符
//                number = ((i <= IsmConstant.CONST_NINE) ? "0" : "") + i;
//                
//                //构造替换占位符的正则表达式
//                regex = ALARM_PARAMETER_PLACEHOLDER_PREFIX + number;
//                
//                param = paramList[i];
//                if (param.contains(MIXED_PARAM_SEPERATOR))
//                {
//                    //与设备端进行约定，所有涉及到在一个参数中存在标点符号“，”的都用“$(”代替，ISM进行反解析
//                    param = param.replaceAll(MIXED_PARAM_SEPERATOR_REGEX,
//                            IsmConstant.ALARM_PARAMETER_SEPARATOR);
//                }
//                
//                //处理告警参数需要转义的情况
//                parserdParamValue = param;
//                if (paramDescMap.containsKey(String.valueOf(i)))
//                {
//                    paramDesc = paramDescMap.get(String.valueOf(i));
//                    oprateType = "";
//                    if (paramDescs.contains("OprateType"))
//                    {
//                        oprateType =
//                                getEventCfgData().getOprateMap()
//                                        .get(this.eventID).definitionOprateType;
//                    }
//                    
//                    paramDescKey = "EventParamDesc_" + paramDesc
//                            + oprateType + '_' + param.trim();
//                    param = handleNonEnumTypeParamDesc(paramDesc, param);
//                    parserdParamValue = ResourceManager.getString(paramDescKey,
//                            param);
//                }
//                
//                parserdParamValue = parseIDParams(i, param, parserdParamValue);
//                //以参数内容替换所有占位符,对传上来的参数trim一下，保证没有空格显示
//                try
//                {
//                    
//                    String temp0 = parserdParamValue.trim();
//                    temp0 = convertSpecialChars(temp0);
//                    rawContent = rawContent.replaceAll(regex,
//                            PARAM_RAPLACE_REGEX + temp0);
//                }
//                catch (Exception e)
//                {
//                    LOGGER.error("Event.replaceParams error: Event ID:"
//                            + eventID + " Event Params:" + params
//                            + " Raw content:" + rawContent, e);
//                }
//                
//                if (ifParserd)
//                {
//                    parserdParam.append(parserdParamValue);
//                    parserdParam.append(ALARM_PARAMETER_SEPARATOR);
//                }
//            }
//            
//            try
//            {
//                rawContent = Util.matchBlank(rawContent);
//                rawContent = rawContent.replaceAll(PARAM_RAPLACE_REGEX, "");
//            }
//            catch (Exception e)
//            {
//                LOGGER.info("matchBlank error:" + e.getMessage()
//                        + " alarmId is:" + this.eventID, e);
//            }
//        }
//        
//        return rawContent;
//    }
    private String parseIDParams(int i, String param, String parserdParamValue) {
        //将一致性组和远程复制的ID改为16进制进行显示
        if (EventConfigData.getConsistentGrpMap().containsKey(this.eventID)) {
            try {
                String s =
                        EventConfigData.getConsistentGrpMap().get(this.eventID);
                if (!"".equals(s) && s.contains(String.valueOf(i))) {
                    parserdParamValue =
                            Long.toHexString(Long.valueOf(param.trim()));
                }
            } catch (Exception e) {
                LOGGER.error(
                        "Process ConsistentGrp Alarm error:" + e.getMessage(),
                        e);
            }
        }
        return parserdParamValue;
    }

    private Map<String, String> parseParamDesc(String paramDescs) {
        Map<String, String> paramDescMap = new HashMap<String, String>(0);
        if (null != paramDescs && paramDescs.length() > 0) {
            String[] paramDescList = paramDescs.split(ALARM_PARAMETER_SEPARATOR);
            String[] keyValue = null;
            for (String paramDesc : paramDescList) {
                keyValue = paramDesc.split("=");
                if (null == keyValue || keyValue.length < IsmConstant.CONST_TWO) {
                    continue;
                }

                paramDescMap.put(keyValue[0], keyValue[1]);
            }
        }
        return paramDescMap;
    }

    /*
     * 拆分告警参数
     */
    private String[] splitParams(String params) {
        String[] paramList = params.split(ALARM_PARAMETER_SEPARATOR, -1);

        int index = 0;
        for (String param : paramList) {
            if (null == param || "".equals(param)) {
                paramList[index] = IsmConstant.BLANK_CONTENT;
            }
            index++;
        }
        return paramList;
    }

    /**
     * 处理参数描述类型为非枚举的情况
     * <p>
     * 上报参数值为秒数，要转换为对应的时间格式显示
     *
     * @param paramDesc
     * @param param
     * @return String [返回类型说明]
     * @see [类、类#方法、类#成员]
     */
    private String handleNonEnumTypeParamDesc(String paramDesc, String param) {
        if ("TimeUnit".equals(paramDesc)) {
            long value = Long.parseLong(param.trim());
            //            DateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            //            return timeFormat.format(new Date(value * 1000));

            Date s = new Date(value * MagicNumber.INT1000);
            TimeZone timeZone = TimeZone.getDefault();
            return TimeUtil.gmtToString(s, timeZone);
        }
        //用来处理时间格式：hour:min:sec格式
        if ("TimeUnitNoYear".equals(paramDesc)) {
            long value = Long.parseLong(param.trim());
            DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
            return timeFormat.format(new Date(value * MagicNumber.INT1000));
        }
        //处理操作日志ID转换为16进制（远程复制和一致性组）
        try {
            if ("ConsistentGroupID".equals(paramDesc)
                    || "ReplicationID".equals(paramDesc)) {
                return convert2HEX(param);
            }
        } catch (Exception e) {
            LOGGER.error("Convert ConsistentGroupID or ReplicationID error,id is "
                            + param,
                    e);
        }
        return param;
    }

    //转换为16进制
    private String convert2HEX(String param) {
        BigInteger bigInteger = new BigInteger(param);
        String id = bigInteger.toString(IsmConstant.CONST_16)
                .toUpperCase(Util.getOSLocaleDefaultEn());
        if (id.length() < IsmConstant.CONST_16) {
            int count = IsmConstant.CONST_16 - id.length();
            for (int i = 0; i < count; i++) {
                id = '0' + id;
            }
        }
        return id;
    }

    //告警定义结构体，对应于告警定义文件

    /**
     * Definition
     *
     * @author V1R10
     * @version [版本号V001R010C00, 2011-12-14]
     */
    private static class Definition {

        private String definitionDetail;

        private String definitionName;

        private String definitionDescription;

        private String definitionSuggestion;

        private String definitionParamDesc;

        //增加同步异步操作日志的判断
        private String definitionOprateType;
    }

    /**
     * 配置文件加载
     *
     * @author j00102777
     * @version ISM-V V100R001C00
     * @date 2013-5-30
     */
    public static class EventConfLoader {
        /**
         * 初始告警配置
         *
         * @param eventCfgData eventCfgData
         * @throws IOException IOException
         */
        public void initEventDefinition(EventConfigData eventCfgData) throws IOException {

            for (XmlParser domParser : getConfParser(eventCfgData.getLocale())) {
                parseEvents(domParser, eventCfgData);
            }


        }

        /**
         * 获取对应loc的配置文件
         *
         * @param loc Locale
         * @return List<XmlParser> [返回类型说明]
         * @throws IOException IOException
         */
        protected List<XmlParser> getConfParser(Locale loc) throws IOException {
            List<XmlParser> ret = new ArrayList<XmlParser>();
//            ret.add(XmlParser.getXmlDomParser("config/framework/configuration/event_" + loc.getLanguage()
//                    + ".xml"));
//            ret.add(XmlParser.getXmlDomParser("config/framework/configuration/event_xve_"
//                    + loc.getLanguage() + ".xml"));
            return ret;
        }


        /**
         * 解析配置文件
         *
         * @param domParser    domParser
         * @param eventCfgData eventCfgData
         */
        protected void parseEvents(XmlParser domParser,
                                   EventConfigData eventCfgData) {

            Node node = domParser.treeWalkOfOneElementNode("eventDefinition");

            // 修改covertiy
            if (node == null) {
                return;
            }

            Element element = (Element) node;

            NodeList nodeList = element.getElementsByTagName("param");
            Definition definition = null;
            Node rowNode = null;
            int length = nodeList.getLength();
            NamedNodeMap nodeMap = null;
            String eventIDStr = null;
            long eventID = 0;
            Node oprateNode = null;
            for (int i = 0; i < length; i++) {
                definition = new Definition();
                rowNode = nodeList.item(i);
                nodeMap = rowNode.getAttributes();

                eventIDStr = nodeMap.getNamedItem("eventID").getNodeValue();

                if (null == eventIDStr || eventIDStr.length() == 0) {
                    continue;
                }

                //事件ID
                eventID = Long.decode(eventIDStr);

                //事件描述
                definition.definitionDetail = nodeMap.getNamedItem("detail").getNodeValue();

                //事件修复建议
                definition.definitionSuggestion = nodeMap.getNamedItem("suggestion")
                        .getNodeValue();

                //事件名称
                definition.definitionName = nodeMap.getNamedItem("name").getNodeValue();

                //事件简要描述
                definition.definitionDescription = nodeMap.getNamedItem("description")
                        .getNodeValue();

                //事件参数描述
                if (nodeMap.getNamedItem("paramDesc") == null) {
                    definition.definitionParamDesc = "";
                } else {
                    definition.definitionParamDesc = nodeMap.getNamedItem("paramDesc")
                            .getNodeValue();
                }

                eventCfgData.getDefintionMap().put(eventID, definition);
                //操作日志类型(0表示同步命令,1表示异步命令)
                oprateNode = nodeMap.getNamedItem("OprateType");
                if (null != oprateNode) {
                    definition.definitionOprateType = oprateNode.getNodeValue();
                    eventCfgData.getOprateMap().put(eventID, definition);
                }
            }
        }
    }


    /**
     * 更新一致性组相关eventID
     *
     * @param consGrp consGrp
     */
    public static void updateConsistenGrp(Map<Long, String> consGrp) {
        EventConfigData.getConsistentGrpMap().clear();
        EventConfigData.getConsistentGrpMap().putAll(consGrp);
    }

    /**
     * 加载指定local的告警配置
     *
     * @param loc Locale
     * @throws IOException IOException
     */
    public static void loadEventConfig(Locale loc) throws IOException {
        EventConfigData eventCfg = new EventConfigData(loc);
        cfgloader.initEventDefinition(eventCfg);
        sharedEventConfigDataLocal.put(loc, eventCfg);
    }

    /**
     * 更新eventloader
     *
     * @param loader loader
     */
    public static void setLoader(EventConfLoader loader) {
        cfgloader = loader;
    }

    static {
        try {
            Locale loc = Util.getOSLocaleDefaultEn();
            EventConfigData eventConfigDataLocal = new EventConfigData(loc);
            cfgloader.initEventDefinition(eventConfigDataLocal);
            sharedEventConfigDataLocal.put(loc, eventConfigDataLocal);
        } catch (Exception e) {
            LOGGER.warn("initEventDefinition error", e);
        }
    }

    /**
     * 对original中的$ \ 来进行转义处理 防止界面显示与实际输入不一致
     *
     * @param original 原始字符串
     * @return
     * @author g00250185
     */
    private String convertSpecialChars(String original) {
        StringBuilder sb = new StringBuilder();
        char[] x2 = original.toCharArray();
        for (char ch : x2) {
            if (ch == '$') {
                sb.append("\\$");
            } else if (ch == '\\') {
                sb.append("\\\\");
            } else {
                sb.append(ch);
            }
        }
        return sb.toString();
    }
}
