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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.opensds.vasa.common.MagicNumber;


/**
 * 时间相关处理的类
 *
 * @author V1R10
 * @version [版本号V001R010C00, 2011-12-14]
 */
public final class TimeUtil {
    private static final String YYYY_MM_DD_HH_MM_SS_UTC_Z = "yyyy-MM-dd HH:mm:ss 'UTC'Z";


    private static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";


    private TimeUtil() {
    }

    /**
     * 将字符串解析成date对象
     *
     * @param dateString 方法参数：dateString
     * @return Date 返回结果
     */
    @Deprecated
    public static synchronized Date yYYYMMddmmssToDate(String dateString) {
        try {
            DateFormat yyyyMMddhhmmssSSDateFormate = new SimpleDateFormat(
                    "yyyy-MM-ddHHmmss"); //yyyyMMddhhmmssSS 日期解析器
            return yyyyMMddhhmmssSSDateFormate.parse(dateString);

        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 将字符串解析成date对象
     *
     * @param dateString 方法参数：dateString
     * @return Date 返回结果
     */
    public static synchronized Date timeStrToDate(String dateString) {
        try {
            int index = dateString.lastIndexOf(':');

            dateString = dateString.substring(0, index)
                    + dateString.substring(index + 1);

            DateFormat yyyyMMddhhmmssSSDateFormate = new SimpleDateFormat(
                    YYYY_MM_DD_HH_MM_SS_UTC_Z);
            return yyyyMMddhhmmssSSDateFormate.parse(dateString);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 将字符串解析成date对象
     *
     * @param dateString 方法参数：dateString
     * @param timezone   方法参数：timezone
     * @return Date 返回结果
     */
    public static synchronized Date timeStrToDate(String dateString,
                                                  TimeZone timezone) {
        try {
            DateFormat yyyyMMddhhmmssSSDateFormate = new SimpleDateFormat(
                    YYYY_MM_DD_HH_MM_SS);
            yyyyMMddhhmmssSSDateFormate.setTimeZone(timezone);
            return yyyyMMddhhmmssSSDateFormate.parse(dateString);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 将字符串解析成date对象
     *
     * @param dateString 方法参数：dateString
     * @return Date 返回结果
     */
    public static synchronized Date yYYYMMddmmssToDate2(String dateString) {
        try {
            DateFormat yyyyMMddhhmmssSSDateFormate = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss"); //yyyyMMddhhmmssSS 日期解析器
            return yyyyMMddhhmmssSSDateFormate.parse(dateString);

        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 将Date解析成String对象yyyy-MM-dd hh:mm:ss
     *
     * @param date 要转换的date对象
     * @return String 转换过后的结果：yyyy-MM-dd hh:mm:ss
     */
    public static synchronized String toLocalString(Date date) {
        if (null == date) {
            return "";
        } else {
            DateFormat yyyyMmDdDateFormate = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss z"); //yyyy-MM-dd hh:mm:ss 日期解析器
            String id = TimeZone.getDefault().getID();
            yyyyMmDdDateFormate.setTimeZone(TimeZone.getTimeZone(id));
            return yyyyMmDdDateFormate.format(date);
        }
    }

    /**
     * 将Date解析成String对象yyyy-MM-dd hh:mm:ss
     *
     * @param date 要转换的date对象
     * @return String 转换过后的结果：yyyy-MM-dd hh:mm:ss
     */
    public static synchronized String toLocalString(long date) {
        if (0 >= date) {
            return "";
        }

        return toLocalString(new Date(date));
    }

    /**
     * 将Date解析成String对象yyyy-MM-dd hh:mm:ss UTC
     *
     * @param date 要转换的date对象
     * @return String 转换过后的结果：yyyy-MM-dd hh:mm:ss UTC
     */
    public static synchronized String toUTCString(Date date) {
        if (null == date) {
            return "";
        } else {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //yyyy-MM-dd hh:mm:ss 日期解析器

            df.setTimeZone(TimeZone.getTimeZone("GMT"));

            return df.format(date) + " UTC";
        }
    }

    /**
     * 将Date解析成String对象yyyy-MM-dd hh:mm:ss UTC
     *
     * @param date 要转换的date对象
     * @return String 转换过后的结果：yyyy-MM-dd hh:mm:ss UTC
     */
    public static synchronized String toUTCString(long date) {
        return toUTCString(new Date(date));
    }

    /**
     * 将Date解析成String对象yyyy-MM-dd hh:mm:ss
     *
     * @param date 要转换的date对象
     * @param tz   方法参数：tz
     * @return String 转换过后的结果：yyyy-MM-dd hh:mm:ss
     */
    public static synchronized String toGMTString(Date date, TimeZone tz) {
        if (null == date || null == tz) {
            return "";
        } else {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //yyyy-MM-dd hh:mm:ss 日期解析器

            df.setTimeZone(tz);

            return df.format(date);
        }
    }

    /**
     * 将Date解析成String对象yyyy-MM-dd hh:mm:ss
     *
     * @param date 要转换的date对象
     * @param tz   方法参数：tz
     * @return String 转换过后的结果：yyyy-MM-dd hh:mm:ss
     */
    public static synchronized String toGMTString(long date, TimeZone tz) {
        return toGMTString(new Date(date), tz);
    }

    /**
     * 时间转换器 GTM -> CST
     * <p>
     * 对 GTM 的 yyyy-MM-dd' 'HH:mm:ss 转换为 CST 的  yyyy-MM-dd' 'HH:mm:ss
     *
     * @param gmtString 方法参数：gmtString
     * @return String [返回类型说明]
     * @author r90003224
     * @see [类、类#方法、类#成员]
     */
    public static synchronized String gmtToLocalDateString(String gmtString) {
        try {
            Date s = gmtStr2Date(gmtString);

            if (null == s) {
                return "";
            }

            return TimeUtil.toLocalString(s);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 将GMT标准时间字符串转换为指定时区格式显示的字符串
     *
     * @param gmtString 方法参数：gmtString
     * @param timeZone  方法参数：timeZone
     * @return String 返回结果
     */
    public static synchronized String gmtToString(String gmtString,
                                                  TimeZone timeZone) {
        try {
            Date s = gmtStr2Date(gmtString);

            if (null == s) {
                return "";
            }
            return gmtToString(s, timeZone);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 将以毫秒形式表示的GMT标准时间转换为指定时区格式显示的字符串
     *
     * @param gmtTimeInMS 方法参数：gmtTimeInMS
     * @param timeZone    方法参数：timeZone
     * @return String 返回结果
     */
    public static synchronized String gmtToString(long gmtTimeInMS,
                                                  TimeZone timeZone) {
        Date s = new Date(gmtTimeInMS);
        return gmtToString(s, timeZone);
    }

    /**
     * 将GMT标准时间转换为指定时区格式显示的字符串
     *
     * @param gmtTime  方法参数：gmtTime
     * @param timeZone 方法参数：timeZone
     * @return String 返回结果
     */
    public static synchronized String gmtToString(Date gmtTime,
                                                  TimeZone timeZone) {
        try {
            DateFormat yyyyMmDdDateFormate = new SimpleDateFormat(
                    YYYY_MM_DD_HH_MM_SS_UTC_Z);
            yyyyMmDdDateFormate.setTimeZone(timeZone);

            String str = yyyyMmDdDateFormate.format(gmtTime);
            StringBuilder sb = new StringBuilder(str);
            sb.insert(str.length() - MagicNumber.INT2, ":");
            if (timeZone.useDaylightTime()) {
                sb.append(" DST");
            }
            return sb.toString();
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 方法 ： gmtStr2Date
     *
     * @param gmtString 方法参数：gmtString
     * @return Date 返回结果
     */
    public static synchronized Date gmtStr2Date(String gmtString) {
        DateFormat yyyyMMddhhmmssSSDateFormate = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
        yyyyMMddhhmmssSSDateFormate.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date retDate = null;
        try {
            retDate = yyyyMMddhhmmssSSDateFormate.parse(gmtString);
        } catch (ParseException e) {
            return null;
        }
        return retDate;
    }

    /**
     * 返回 yyyy-MM-dd HH:mm:ss 的时间
     *
     * @param localString 方法参数：localString
     * @return String [返回类型说明]
     * @author r90003224
     * @see [类、类#方法、类#成员]
     */
    public static synchronized String localToGMT(String localString) {
        DateFormat yyyyMMddhhmmssSSDateFormate = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");

        Date retDate = null;
        try {
            retDate = yyyyMMddhhmmssSSDateFormate.parse(localString);
        } catch (ParseException e) {
            return "";
        }
        return toGMTString(retDate, TimeZone.getTimeZone("GMT"));
    }

    /**
     * 将格式为格式为UTC{+|-}hh:mm的时间偏移量字符串转换为毫秒数
     *
     * @param rowOffsetStr 方法参数：rowOffsetStr
     * @return int 返回结果
     */
    public static synchronized int rowOffsetStr2MS(String rowOffsetStr) {
        int offset = 0;
        int tempInt = MagicNumber.INT3600 * MagicNumber.INT1000;
        offset += Integer.valueOf(rowOffsetStr.substring(MagicNumber.INT4, MagicNumber.INT6)) * tempInt;
        offset += Integer.valueOf(rowOffsetStr.substring(MagicNumber.INT7)) * MagicNumber.INT60 * MagicNumber.INT1000;
        if (rowOffsetStr.charAt(MagicNumber.INT3) == '-') {
            offset = -offset;
        }
        return offset;
    }

    /**
     * 方法 ： getGmtDate
     *
     * @param gmtString 方法参数：gmtString
     * @param timeZone  方法参数：timeZone
     * @return Date 返回结果
     */
    public static synchronized Date getGmtDate(String gmtString,
                                               TimeZone timeZone) {
        DateFormat yyyyMMddhhmmssSSDateFormate = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
        yyyyMMddhhmmssSSDateFormate.setTimeZone(timeZone);
        Date retDate = null;
        try {
            retDate = yyyyMMddhhmmssSSDateFormate.parse(gmtString);
        } catch (ParseException e) {
            return null;
        }
        return retDate;
    }

    //begin w00221007 同步问题单T12R-4095 要求NTP时间同步周期默认为10分钟，最短可以设置为1分钟。

    /**
     * <根据时间的数字和单位，转换成秒后返回>
     *
     * @param ntpSchedule 方法参数：ntpSchedule
     * @param unit        方法参数：unit
     * @return int [返回类型说明]
     * @see [类、类#方法、类#成员]
     */
    public static int getSecondNum(int ntpSchedule, Unit.TimeUnit unit) {
        int period = -1;
        switch (unit) {
            case Day: {
                period = ntpSchedule * MagicNumber.INT24 * MagicNumber.INT60 * MagicNumber.INT60;
                break;
            }
            case Hour: {
                period = ntpSchedule * MagicNumber.INT60 * MagicNumber.INT60;
                break;
            }
            case Minute: {
                period = ntpSchedule * MagicNumber.INT60;
                break;
            }
            case Second: {
                period = ntpSchedule;
                break;
            }
            default:
                break;
        }
        return period;
    }
    //end w00221007 同步问题单T12R-4095 要求NTP时间同步周期默认为10分钟，最短可以设置为1分钟。

    /**
     * 将以毫秒形式表示的GMT标准时间转换为指定设备上的时区格式显示的字符串
     *
     * @param gmtTimeInMS 方法参数：gmtTimeInMS
     * @param deviceID    设备ID
     * @return String 返回结果
     */
    public static synchronized String gmtToString(String deviceID,
                                                  long gmtTimeInMS) {
        Date s = new Date(gmtTimeInMS);
        TimeZone timeZone = TimeZone.getDefault();
        //begin wkf68466 由于归一版本修改了代码，造成编译通不过。暂时这样修改
        //        Launcher launcher = LauncherManager.getInstance().getLauncher(deviceID);
        //        if (launcher != null)
        //        {
        //            timeZone = launcher.getDeviceTimeZone();
        //        }
        //end wkf68466 由于归一版本修改了代码，造成编译通不过。暂时这样修改
        return gmtToString(s, timeZone);
    }

}
