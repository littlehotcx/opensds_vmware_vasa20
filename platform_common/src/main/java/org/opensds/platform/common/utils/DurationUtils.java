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

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
/** * @author w00208247 * *  */
/**
  "P1D"（一天）> "PT12H"（12 小时），并且 "P2Y"（两年）>"P23M"（23 月）。
 * PT6000S 秒
 * PT10M 分
 * PT2H 时
 * P5D 天
 * P5M 月
 * P5Y 年
 */
public abstract class DurationUtils
{
    private static final Logger LOGGER = LogManager.getLogger(DurationUtils.class);
    
    /**
     *输入duration类型，返回 单位为分钟
     ** @param duration
    /** * @return
     */
    public static int duration2int(Duration duration){
        if (duration==null)
        {
            return 0;
        }
        //添加传入的int型符号
        int sign = duration.getSign();
        int years = duration.getYears();
        int months  = duration.getMonths();
        int days = duration.getDays();
        int hours = duration.getHours();
        int minutes = duration.getMinutes();
        int seconds = duration.getSeconds();
        
        int res =years*365*24*60 + months*30*24*60+days*24*60 + hours*60 +minutes + seconds/60;
        return res*sign;
    }
    
    
    /**
     * 输入duration类型的字符串，返回的时长以分钟为单位
     * * @param duration
    /** * @return
     */
    public static int durationStr2int(String duration){
        Duration dur = null;
        try
        {
             dur = DatatypeFactory.newInstance().newDuration(duration);
        }
        catch (DatatypeConfigurationException e)
        {
        	LOGGER.debug("DatatypeConfigurationException duration");
        }
        return duration2int(dur);
    }
    
    /**
     * 输入以分钟为单位的数据，返回duration类型数据
     * * @param dur
    /** * @return
     */
    public static Duration durationInt2dur(Integer dur)
    {
        Duration duration = null;
        try
        {
            if (0<=dur)
            {
                duration = DatatypeFactory.newInstance().newDuration("PT"+Math.abs(dur)+"M");
            }else {
                duration = DatatypeFactory.newInstance().newDuration("-PT"+Math.abs(dur)+"M");
            }
           
        }
        catch (DatatypeConfigurationException e)
        {
            LOGGER.debug("DatatypeConfigurationException duration");
        }
        return duration;
    }

    /**
     * 输入duration类型的字符串，返回duration类型数据
     * * @param duration
    /** * @return
     */
    public static Duration durationStr2dur(String dur)
    {
        Duration duration = null;
        try
        {
            duration = DatatypeFactory.newInstance().newDuration(dur);
        }
        catch (DatatypeConfigurationException e)
        {
        	LOGGER.debug("DatatypeConfigurationException duration");
        }
        return duration;
    }

}
