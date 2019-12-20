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

package org.opensds.platform.common.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
public abstract class DateUtils
{
    private static final Logger LOGGER = LogManager.getLogger(DateUtils.class);

    /**
     * 将String类型的data转换成XMLGregorianCalendar类型的date 输入时间类型：yyyy-MM-dd HH:mm:ss
     * 返回XMLGregorianCalendar类型的时间
     * 
     * @param date
     * @return
     */
    public static XMLGregorianCalendar toGregorianCalendarDate(String date)
    {

        if (StringUtils.isEmpty(date))
        {
            return null;
        }
        GregorianCalendar nowGregorianCalendar = new GregorianCalendar();
        XMLGregorianCalendar xmlDatetime = null;
        try
        {
            xmlDatetime = DatatypeFactory.newInstance()
                    .newXMLGregorianCalendar(nowGregorianCalendar);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss");
            nowGregorianCalendar.setTime(simpleDateFormat.parse(date));
            xmlDatetime = DatatypeFactory.newInstance()
                    .newXMLGregorianCalendar(nowGregorianCalendar);
        }
        catch (DatatypeConfigurationException e)
        {
            LOGGER.error("", e);
        }
        catch (ParseException e)
        {
            LOGGER.error("", e);
        }
        return xmlDatetime;
    }

    /**
     * Date类型转XMLGregorianCalendar类型 * @param date * @return
     */
    public static XMLGregorianCalendar toGregorianCalendarDate(Date date)
    {
        return toGregorianCalendarDateSMC(date);
    }

    /**
     * Date类型转XMLGregorianCalendar类型 * @param date * @return
     */
    public static XMLGregorianCalendar toGregorianCalendarDatePlusTimezone(
            Date date)
    {
        return setTimezoneToUTC(toGregorianCalendarDateSMC(date));
    }

    /**
     * Date类型转XMLGregorianCalendar类型 * @param date * @return 用于预约周期会议返回的情况
     */
    public static XMLGregorianCalendar toGregorianCalendarDateSMC(Date date)
    {

        if (null == date)
        {
            return null;
        }
        GregorianCalendar nowGregorianCalendar = new GregorianCalendar();
        XMLGregorianCalendar xmlDatetime = null;
        try
        {
            nowGregorianCalendar.setTime(date);
            xmlDatetime = DatatypeFactory.newInstance()
                    .newXMLGregorianCalendar(nowGregorianCalendar);
        }
        catch (DatatypeConfigurationException e)
        {
            LOGGER.error("", e);
        }
        return xmlDatetime;
    }

    /**
     * 获取UTC时间
     * @return
     */
    public static XMLGregorianCalendar getCurrentUTCTime()
    {
        Date date = new Date();
        GregorianCalendar nowGregorianCalendar = new GregorianCalendar();
        XMLGregorianCalendar xmlDatetime = null;
        try
        {
            nowGregorianCalendar.setTime(date);
            xmlDatetime = DatatypeFactory.newInstance()
                    .newXMLGregorianCalendar(nowGregorianCalendar);
        }
        catch (DatatypeConfigurationException e)
        {
            LOGGER.error("", e);
        }
        return xmlDatetime;
    }

//    /**
//     * 将会议时间减去相应的时区 如：北京时间的情况，将data时间加上八小时
//     */
//    public static XMLGregorianCalendar getUTCDatePlusTimezone(
//            XMLGregorianCalendar data)
//    {
//        if (null == data)
//        {
//            return null;
//        }
//        int duration = data.getTimezone();
//        Duration du = null;
//        try
//        {
//            du = DatatypeFactory.newInstance().newDuration(true, 0, 0, 0, 0,
//                    duration, 0);
//        }
//        catch (DatatypeConfigurationException e)
//        {
//            LOGGER.error("", e);
//        }
//        data.add(du);
//        return data;
//    }

    /**
     * XMLGregorianCalendar 转 Date
     */
    public static Date toDate(XMLGregorianCalendar xmlGregorianCalendar)
    {
        if (null == xmlGregorianCalendar)
        {
            return null;
        }
        GregorianCalendar gregorianCalendar = xmlGregorianCalendar
                .toGregorianCalendar();
        return gregorianCalendar.getTime();
    }

    /**
     * XMLGregorianCalendar 转 Date 减8小时
     */
    public static Date toDateSMC(XMLGregorianCalendar xmlGregorianCalendar)
    {
        if (null == xmlGregorianCalendar)
        {
            return null;
        }
        GregorianCalendar gregorianCalendar = xmlGregorianCalendar
                .toGregorianCalendar();
        return gregorianCalendar.getTime();
    }

    public static String dateToString(Date date)
    {

        if (null == date)
        {
            return null;
        }
        // date减去时区
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int timeZone = calendar.getTimeZone().getRawOffset() / (3600 * 1000);

        int hour = calendar.get(Calendar.HOUR_OF_DAY) - timeZone;
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        String format = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'";
        SimpleDateFormat sdf = new SimpleDateFormat(format);

        String resDate = sdf.format(calendar.getTime());
        return resDate;
    }

    /**
     * date 转string 2002-09-28 16:00格式 用于SMC 1.0中预约会议的时间格式
     */
    public static String dateTo1String(Date date)
    {

        if (null == date)
        {
            return null;
        }
        // date减去时区

        String format = "yyyy-MM-dd HH:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(format);

        String resDate = sdf.format(date);
        return resDate;
    }

    /**
     * date 转string 2002-09-28 16:00格式 用于SMC 1.0中预约会议计算end time的情况
     */
    public static String dateAddDuration(Date date, Integer duration)
    {

        if (null == date)
        {
            return null;
        }
        // date加上时间长
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        int minute = calendar.get(Calendar.MINUTE) + duration;
        calendar.set(Calendar.MINUTE, minute);
        String format = "yyyy-MM-dd HH:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(format);

        String resDate = sdf.format(calendar.getTime());
        return resDate;
    }

    /**
     * date 转string 用于UC
     */
    public static String dateToString(Date date, String formatPattern)
    {

        if (null == date || null == formatPattern)
        {
            return null;
        }

        if (formatPattern.isEmpty())
        {
            return null;
        }

        SimpleDateFormat sdf = new SimpleDateFormat(formatPattern);

        String resDate = sdf.format(date);
        return resDate;
    }

    /**
     * String 转 Date 用于UC
     */
    public static Date stringToDate(String dateStr, String formatPattern)
    {
        if (null == dateStr || null == formatPattern)
        {
            return null;
        }

        if (dateStr.isEmpty() || formatPattern.isEmpty())
        {
            return null;
        }

        Date date = null;

        try
        {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
                    formatPattern);
            date = simpleDateFormat.parse(dateStr);
        }
        catch (ParseException e)
        {
            LOGGER.error("date parse error happened in stringToDate method", e);
        }
        return date;
    }

    /**
     * String 转 Date 用于UC （英文环境， 如Wed, 4 Jul 2001 12:08:56）
     */
    public static Date stringToDate(String dateStr, String formatPattern, Locale localLanguage)
    {
        if (null == dateStr || null == formatPattern)
        {
            return null;
        }

        if (dateStr.isEmpty() || formatPattern.isEmpty())
        {
            return null;
        }

        Date date = null;

        try
        {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
                    formatPattern, localLanguage);
            date = simpleDateFormat.parse(dateStr);
        }
        catch (ParseException e)
        {
            LOGGER.error("date parse error happened in stringToDate method", e);
        }
        return date;
    }
    
    /**
     * 将时间转换为格林威治时间格式的字符串
     * 
     * @param date 需要格式化的时间
     * @return 格式化后的时间格式
     */
    public static String date2GregorianFormat(Date date)
    {
        if (null == date)
        {
         return "";   
        }
        try
        {
            DatatypeFactory factory = DatatypeFactory.newInstance();
            GregorianCalendar gCal = (GregorianCalendar) GregorianCalendar
                    .getInstance();
            gCal.setTime(date);
            String result = factory.newXMLGregorianCalendar(gCal).toString();

            return result;
        }
        catch (DatatypeConfigurationException e)
        {
            LOGGER.error("date2GregorianFromatd error", e);
            return date.toString();
        }
    }

    /**
     * 将日期格式化成需要的格式的字符串
     * 
     * @param date 需要格式化的日期
     * @param formatPatter 需要的时间格式
     * @return 格式化后的日期字符串
     */
    public static String formatDate(Date date, String formatPatter)
    {
        if (null == date)
        {
            return null;
        }

        if (null == formatPatter)
        {
            throw new IllegalArgumentException("formatPatter is null");
        }

        SimpleDateFormat sdf = new SimpleDateFormat(formatPatter);
        return sdf.format(date);
    }

    /**
     * 获取当前时间
     * 
     * @return 当前时间
     */
    public static Date getCurrentDate()
    {
        Calendar cal = Calendar.getInstance();
        return cal.getTime();
    }
    
    /**
     * 将字符串中格林威治日期格式的中的时区转换为相反的时区，如将东八区换为西八区、西八区换为东八区。
     * 2013-07-05T22:02:10.000+08:00 -> 2013-07-05T22:02:10.000-08:00
     * 
     * @param gregorianTime 格林威治日期格式的字符串
     * @return 转换后的日期字符串
     */
    public static String swapTimeZone(String gregorianTime)
    {
        if (null == gregorianTime || gregorianTime.length() < 19)
        {
            return gregorianTime;
        }
        
        char[] chars = gregorianTime.toCharArray();
        char[] resultChars = new char[gregorianTime.length()];
        for (int index = 0; index < gregorianTime.length(); index++)
        {
            if (index >= 19 && chars[index] == '+')
            {
                resultChars[index] = '-';
            }
            else if (index >= 19 && chars[index] == '-')
            {
                resultChars[index] = '+';
            }
            else
            {
                resultChars[index] = chars[index];
            }
        }

        return new String(resultChars);
    }
    
    //把非UTC时区转换为UTC时区，TP OA使用
    public static XMLGregorianCalendar setTimezoneToUTC(XMLGregorianCalendar date)
    {
        if(null == date)
        {
            return null;
        }

        boolean isPositive = false;
        int duration = date.getTimezone();
        if(duration < 0)
        {
            isPositive = true;
            duration = 0 - duration;
        }
        Duration du = null;
        try
        {
            du = DatatypeFactory.newInstance().newDuration(isPositive, 0, 0, 0, 0,
                    duration, 0);
        }
        catch (DatatypeConfigurationException e)
        {
            LOGGER.error("", e);
        }
        date.add(du);
        date.setTimezone(0);

        return date;
    }
    
}
