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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.apache.logging.log4j.Logger;

import org.opensds.platform.common.utils.StringUtils;

public class DateUtil {
    public static final String FORMART_A = "yyyy-MM-dd HH:mm:ss";

    public static final String FORMART_B = "yyyy-MM-dd HH:mm:ss z";

    public static String getLastDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        date = calendar.getTime();
        SimpleDateFormat foo = new SimpleDateFormat("yyyy-MM-dd");
        return foo.format(date);
    }

    public static String getFormateDateStr(Date date, String pattemFormat) {

        SimpleDateFormat foo = null;
        if (StringUtils.isEmpty(pattemFormat)) {
            foo = new SimpleDateFormat(FORMART_A);
        } else {
            foo = new SimpleDateFormat(pattemFormat);
        }

        return foo.format(date);
    }

    public static Date getFormateDate(String date, String pattemFormat) throws ParseException {
        String subDate = date.substring(0, date.length() - 3);
        SimpleDateFormat foo = new SimpleDateFormat(pattemFormat);
        // foo.setTimeZone(TimeZone.getTimeZone("UTC"));

        // DateTimeFormatter formatter =
        // DateTimeFormat.forPattern(pattemFormat);
        // DateTime dateTime = formatter.withOffsetParsed().parseDateTime(date);
        // DateTime dateTimeUtc = dateTime.toDateTime(DateTimeZone.UTC);

        // SimpleDateFormat foo = new SimpleDateFormat(pattemFormat);
        return foo.parse(subDate);
    }

    public static Date getFormateDate2(String date, String pattemFormat) throws ParseException {
        Date tempDate = getFormateDate(date, FORMART_A);
        SimpleDateFormat foo = new SimpleDateFormat(pattemFormat);
        String tempStr = foo.format(tempDate);
        return foo.parse(tempStr);
    }

    public static Date getUTCDate() {
        java.util.Calendar cal = java.util.Calendar.getInstance();

        int zoneOffset = cal.get(java.util.Calendar.ZONE_OFFSET);

        int dstOffset = cal.get(java.util.Calendar.DST_OFFSET);

        cal.add(java.util.Calendar.MILLISECOND, -(zoneOffset + dstOffset));

        return new Date(cal.getTimeInMillis());
    }

    /**
     * 取得当前时间的减去unit的时间
     *
     * @param unit: 分钟
     * @return
     */
    public static Date getUTCDate(int unit) {
        java.util.Calendar cal = java.util.Calendar.getInstance();

        int zoneOffset = cal.get(java.util.Calendar.ZONE_OFFSET);

        int dstOffset = cal.get(java.util.Calendar.DST_OFFSET);

        cal.add(java.util.Calendar.MILLISECOND, -(zoneOffset + dstOffset));

        cal.add(java.util.Calendar.MINUTE, -1 * unit);

        return new Date(cal.getTimeInMillis());
    }

    public static Calendar getUTCCalendar() {
        java.util.Calendar cal = java.util.Calendar.getInstance();

        int zoneOffset = cal.get(java.util.Calendar.ZONE_OFFSET);

        int dstOffset = cal.get(java.util.Calendar.DST_OFFSET);

        cal.add(java.util.Calendar.MILLISECOND, -(zoneOffset + dstOffset));

        return cal;
    }

    public static Date getUtCFromLocalTime(Date localTime) {
        java.util.Calendar cal = java.util.Calendar.getInstance();

        cal.setTime(localTime);

        int zoneOffset = cal.get(java.util.Calendar.ZONE_OFFSET);

        int dstOffset = cal.get(java.util.Calendar.DST_OFFSET);

        cal.add(java.util.Calendar.MILLISECOND, -(zoneOffset + dstOffset));

        return cal.getTime();
    }

    public static Date getLocalTimeFromUTC(Date utclTime) {
        java.util.Calendar cal = java.util.Calendar.getInstance();

        cal.setTime(utclTime);

        int zoneOffset = cal.get(java.util.Calendar.ZONE_OFFSET);

        int dstOffset = cal.get(java.util.Calendar.DST_OFFSET);

        cal.add(java.util.Calendar.MILLISECOND, +(zoneOffset + dstOffset));

        return cal.getTime();
    }

    public static String utcDateStrToLocal(String utcDateStr) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String localDateStr = null;
        //dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date date = dateFormat.parse(utcDateStr);
        //SimpleDateFormat destDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        localDateStr = dateFormat.format(date);

        return localDateStr;
    }

}
