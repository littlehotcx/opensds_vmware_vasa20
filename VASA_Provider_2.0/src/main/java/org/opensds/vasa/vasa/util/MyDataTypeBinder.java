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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * use this to change the vasaService.wsdl date object mapping
 *
 * @author g00250185
 * @version V100R001C10
 */
public final class MyDataTypeBinder {

    /**
     * yyyy-MM-dd'T'HH:mm:ss
     */
    private static DateFormat dateTime = new SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ss");

    /**
     * yyyy-MM-dd
     */
    private static DateFormat date = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * private constructor
     */
    private MyDataTypeBinder() {

    }

    /**
     * String 2 calendar
     *
     * @param value string
     * @return Calendar
     */
    public static synchronized Calendar unmarshalDate(String value) {
        if (value == null || value.length() == 0) {
            return null;
        }
        Date d = null;

        try {
            d = date.parse(value);
        } catch (Exception e) {
            //e.printStackTrace();
            return null;
        }
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        return c;
    }

    /**
     * Calendar 2 string
     *
     * @param value Calendar
     * @return String
     */
    public static synchronized String marshalDate(Calendar value) {
        if (value == null) {
            return null;
        }

        return date.format(value.getTime());
    }

    /**
     * calendar 2 String
     *
     * @param value Calendar
     * @return String
     */
    public static synchronized String marshalDateTime(Calendar value) {
        if (value == null) {
            return null;
        }

        return dateTime.format(value.getTime());
    }

    /**
     * String 2 Calendar
     *
     * @param value String
     * @return Calendar
     */
    public static synchronized Calendar unmarshalDateTime(String value) {
        if (value == null || value.length() == 0) {
            return null;
        }
        Date d = null;

        try {
            d = dateTime.parse(value);
        } catch (Exception e) {
            //e.printStackTrace();
            return null;
        }
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        return c;
    }

}
