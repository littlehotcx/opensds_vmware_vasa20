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

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Formatter;
import java.util.Locale;

import org.opensds.vasa.common.IsmConstant;
import org.opensds.vasa.common.MagicNumber;

/**
 * 用于存放所有单位的枚举与转换逻辑
 *
 * @author x00102290
 * @version [版本号V001R010C00, 2011-12-14]
 * @see [相关类/方法]
 * @since 1.0
 */
public class Unit {
    /**
     * 容量单位转换比例
     */
    //    private static final int UNIT_SCALE = 1024;
    private static final DecimalFormat DF = new DecimalFormat(
            "##,###,###,###,##0.00");

    private static final DecimalFormat DF2 = new DecimalFormat(
            "##,###,###,###,##0.000");

    private static final DecimalFormat DF_WITHOUT_COMMA = new DecimalFormat(
            "######0.00");

    private static final int SECTOR_SIZE = 512;

    /**
     * 用于表示可以按比例进行单位换算的实体，如时间、容量等
     */
    public static interface Scaleble {
        /**
         * 获取比例，一般会有一个最小单位比例为1，而其它单位的比例是相对于此单位而获得的比例
         *
         * @return 返回比例值
         */
        public long getScale();
    }

    /**
     * 时间单位枚举
     */
    public static enum TimeUnit implements Scaleble {
        /**
         * 枚举变量
         */
        Nanosecond(1L),

        /**
         * 枚举变量
         */
        Microsecond(1000L),

        /**
         * 枚举变量
         */
        Millisecond(1000L * 1000L),

        /**
         * 枚举变量
         */
        Second(1000L * 1000L * 1000L),

        /**
         * 枚举变量
         */
        Minute((1000L * 1000L * 1000L) * 60L),

        /**
         * 枚举变量
         */
        Hour((1000L * 1000L * 1000L) * (60L * 60L)),

        /**
         * 枚举变量
         */
        Day((1000L * 1000L * 1000L) * (60L * 60L) * 24L);

        private long scale;

        TimeUnit(long scale) {
            this.scale = scale;
        }

        /**
         * 方法 ： getScale
         *
         * @return long 返回结果
         */
        public long getScale() {
            return this.scale;
        }

        /**
         * 获取当前时间单位的下一时间单位
         *
         * @return TimeUnit 返回结果
         */
        public TimeUnit getNextUnit() {
            switch (this) {
                case Nanosecond:
                    return Microsecond;
                case Microsecond:
                    return Millisecond;
                case Millisecond:
                    return Second;
                case Second:
                    return Minute;
                case Minute:
                    return Hour;
                case Hour:
                    return Day;
                default:
                    return this;
            }
        }

        /**
         * 方法 ： toString
         *
         * @return String 返回结果
         */
        @Override
        public String toString() {
            return "Unit.TimeUnit." + this.name().toUpperCase(Util.getOSLocaleDefaultEn());
        }
    }

    /**
     * 数据大小单位枚举
     */
    public static enum DataUnit implements Scaleble {
        /**
         * 枚举变量
         */
        Bit(1L),

        /**
         * 枚举变量
         */
        Byte(8L),

        /**
         * 枚举变量
         */
        KB(1024L * 8L),

        /**
         * 枚举变量
         */
        MB((1024L * 1024L) * 8L),

        /**
         * 枚举变量
         */
        GB((1024L * 1024L * 1024L) * 8L),

        /**
         * 枚举变量
         */
        TB((1024L * 1024L * 1024L * 1024L) * 8L),

        /**
         * 枚举变量
         */
        PB((1024L * 1024L * 1024L * 1024L * 1024L) * 8L);

        private long scale;

        DataUnit(long scale) {
            this.scale = scale;
        }

        /**
         * 方法 ： getScale
         *
         * @return long 返回结果
         */
        public long getScale() {
            return this.scale;
        }

        /**
         * 获取当前容量单位的下一容量单位
         *
         * @return DataUnit 返回结果
         */
        public DataUnit getNextUnit() {
            switch (this) {
                case Bit:
                    return Byte;
                case Byte:
                    return KB;
                case KB:
                    return MB;
                case MB:
                    return GB;
                case GB:
                    return TB;
                case TB:
                    return PB;
                default:
                    return this;
            }
        }

        /**
         * 获取当前容量单位的前一容量单位
         *
         * @return DataUnit 返回结果
         */
        public DataUnit getPreviousUnit() {
            switch (this) {
                case PB:
                    return TB;
                case TB:
                    return GB;
                case GB:
                    return MB;
                case MB:
                    return KB;
                case KB:
                    return Byte;
                case Byte:
                    return Bit;
                default:
                    return this;
            }
        }
    }

    /**
     * 数据速率单位枚举
     */
    public static enum DataSpeedUnit implements Scaleble {
        /**
         * 枚举变量
         */
        bps(1L),

        /**
         * 枚举变量
         */
        Kbps(1000L),

        /**
         * 枚举变量
         */
        Mbps(1000L * 1000L),

        /**
         * 枚举变量
         */
        Gbps(1000L * 1000L * 1000L),

        /**
         * 枚举变量
         */
        Tbps(1000L * 1000L * 1000L * 1000L),

        /**
         * 枚举变量
         */
        Pbps(1000L * 1000L * 1000L * 1000L * 1000L);

        private long scale;

        DataSpeedUnit(long scale) {
            this.scale = scale;
        }

        /**
         * 方法 ： getScale
         *
         * @return long 返回结果
         */
        public long getScale() {
            return this.scale;
        }
    }

    /**
     * 频率单位枚举
     */
    public static enum FrequencyUnit implements Scaleble {
        /**
         * 枚举变量
         */
        Hz(1L),

        /**
         * 枚举变量
         */
        KHz(1000L),

        /**
         * 枚举变量
         */
        MHz(1000L * 1000L),

        /**
         * 枚举变量
         */
        GHz(1000L * 1000L * 1000L),

        /**
         * 枚举变量
         */
        THz(1000L * 1000L * 1000L * 1000L);

        private long scale;

        FrequencyUnit(long scale) {
            this.scale = scale;
        }

        /**
         * 方法 ： getScale
         *
         * @return long 返回结果
         */
        public long getScale() {
            return this.scale;
        }
    }

    /**
     * 速率的单位
     *
     * @author z00105170 郑梁
     * @version [V100R001, 2008-3-27]
     */
    public static enum SpeedUnit {
        /**
         * 枚举变量
         */
        Mbps(0),
        /**
         * 枚举变量
         */
        Kbps(1),
        /**
         * 枚举变量
         */
        Gbps(2);

        private int unitValue;

        SpeedUnit(int value) {
            this.unitValue = value;
        }

        /**
         * 方法 ： getSpeedUnit
         *
         * @param type 方法参数：type
         * @return SpeedUnit 返回结果
         */
        public static SpeedUnit getSpeedUnit(int type) {

            SpeedUnit dlType = null;
            switch (type) {
                case 0:
                    dlType = Mbps;
                    break;
                case 1:
                    dlType = Kbps;
                    break;
                case MagicNumber.INT2:
                    dlType = Gbps;
                    break;
                default:
                    throw new IllegalArgumentException(
                            "wrong SpeedUnit value:" + type); //$NON-NLS-1$
            }
            return dlType;
        }

        //begin w00221007 同步问题单P12N-3223  FC链路速率显示单位与用户设置不一致

        /**
         * 获取当前单位的下一个单位
         * *
         *
         * @return SpeedUnit 返回结果
         */
        public SpeedUnit getNextUnit() {
            switch (this) {
                case Kbps:
                    return Mbps;
                case Mbps:
                case Gbps:
                    return Gbps;
                default:
                    break;
            }
            return this;
        }

        //end w00221007 同步问题单P12N-3223  FC链路速率显示单位与用户设置不一致

        /**
         * 方法 ： getValue
         *
         * @return int 返回结果
         */
        public int getValue() {
            return this.unitValue;
        }

        /**
         * 方法 ： toString
         *
         * @return String 返回结果
         */
        @Override
        public String toString() {
            return this.name();
        }

    }

    /**
     * 用于在同种单位之间进行换算
     *
     * @param value      要换算的原始值
     * @param srcUnit    value指定的原单位
     * @param targetUnit 要换算成的目标单位
     * @return 换算结果
     */
    private static double convert(double value, Scaleble srcUnit,
                                  Scaleble targetUnit) {
        double returnValue = value;
        if (srcUnit.getScale() > targetUnit.getScale()) {
            long tmp = srcUnit.getScale() / targetUnit.getScale();
            returnValue = tmp * value;
        }

        if (srcUnit.getScale() < targetUnit.getScale()) {
            long tmp = targetUnit.getScale() / srcUnit.getScale();
            returnValue = value / tmp;
        }

        return returnValue;
    }

    /**
     * 用于在同种单位之间进行换算
     *
     * @param srcUnit    value指定的原单位
     * @param targetUnit 要换算成的目标单位
     * @param t          方法参数：t
     * @param <T>        泛型
     * @return T 返回结果
     */
    @SuppressWarnings("unchecked")
    public static <T extends Number> T convert(T t, Scaleble srcUnit,
                                               Scaleble targetUnit) {
        if (!srcUnit.getClass().isInstance(targetUnit)) {
            throw new IllegalArgumentException("unmatiching unit");
        }

        if (t.doubleValue() == IsmConstant.UNDEFINE_VALUE
                || IsmConstant.UNDEFINE_LONG_VALUE == t.doubleValue()) {
            return t;
        }

        Double value = convert(t.doubleValue(), srcUnit, targetUnit);
        T returnValue = t;
        if (t instanceof Integer) {
            returnValue = (T) Integer.class.cast(value.intValue());
        } else if (t instanceof Long) {
            returnValue = (T) Long.class.cast(value.longValue());
        } else if (t instanceof Double) {
            returnValue = (T) Double.class.cast(value.doubleValue());
        } else if (t instanceof Short) {
            returnValue = (T) Short.class.cast(value.shortValue());
        } else if (t instanceof Float) {
            returnValue = (T) Float.class.cast(value.floatValue());
        } else {
            throw new IllegalArgumentException("The T type cannot supported.");
        }

        //如果是容量转换，传入的是double型的，需要保留三位小数，去尾法。
        if (srcUnit instanceof DataUnit
                && t instanceof Double) {
            BigDecimal bd = new BigDecimal(Double.class.cast(returnValue));
            bd = bd.setScale(MagicNumber.INT3, BigDecimal.ROUND_DOWN);
            returnValue = (T) Double.valueOf(bd.doubleValue());
        }
        return returnValue;
    }

    /**
     * 将扇区转换为指定的容量单位
     *
     * @param sectors    扇区数
     * @param sectorSize 扇区大小
     * @param targetUnit 目标单位
     * @param <T>        泛型
     * @return double 返回结果
     */
    public static <T extends Number> double convert(T sectors, int sectorSize,
                                                    DataUnit targetUnit) {
        double capacity = sectors.doubleValue() * sectorSize;
        return convert(capacity, DataUnit.Byte, targetUnit);
    }

    /**
     * 将扇区转换为指定的容量单位,使用512字节扇区进行容量转换
     *
     * @param sectors    扇区数
     * @param targetUnit 目标单位
     * @param <T>        泛型
     * @return double 返回结果
     */
    public static <T extends Number> double convert(T sectors,
                                                    DataUnit targetUnit) {
        double capacity = sectors.doubleValue() * SECTOR_SIZE;
        return convert(capacity, DataUnit.Byte, targetUnit);
    }

    /**
     * 容量的小数点保留3位小数。允许精度损失控制在1/1000范围内。
     * （注：精度损失仅为界面上显示时容量转换而导致，非损失真实的物理设备容量）
     * 容量显示的最小单位为MB.
     * 容量大于进制（1024），自动向上转换；如1025M，自动转换为G为1.001G
     *
     * @param t       源容量的数值
     * @param srcUnit 源容量的单位
     * @param <T>     泛型
     * @return String 新容量的字符串表示
     */
    public static <T extends Number> String adaptConvert2(T t, DataUnit srcUnit) {
        return adaptConvertDeleteZero(t, srcUnit, false);
    }

    /**
     * 容量的小数点保留3位小数。允许精度损失控制在1/1000范围内。
     * （注：精度损失仅为界面上显示时容量转换而导致，非损失真实的物理设备容量）
     * 容量显示的最小单位为MB.
     * 容量大于进制（1024），自动向上转换；如1025M，自动转换为G为1.001G
     *
     * @param t            源容量的数值
     * @param srcUnit      源容量的单位
     * @param <T>          泛型
     * @param isDeleteZero 是否删除无用的0
     * @return String 新容量的字符串表示
     */
    public static <T extends Number> String adaptConvertDeleteZero(T t, DataUnit srcUnit, boolean isDeleteZero) {
        Double value = t.doubleValue();
        if (IsmConstant.UNDEFINE_VALUE == value
                || IsmConstant.UNDEFINE_LONG_VALUE == value || 0 > value) {
            return IsmConstant.BLANK_CONTENT;
        }

        if (0 == value) {
            return "0 " + DataUnit.MB.name();
        }

        Double currentValue = value;
        DataUnit currentDataUnit = srcUnit;

        DataUnit nextDataUtil = null;
        //如果大于1024，则向更大的单位转换
        while (currentValue >= MagicNumber.DOUBLE1024) {
            nextDataUtil = currentDataUnit.getNextUnit();
            //表示是最大单位了,无需再除以1024
            if (currentDataUnit.equals(nextDataUtil)) {
                break;
            }

            currentValue = currentValue / MagicNumber.DOUBLE1024;
            currentDataUnit = currentDataUnit.getNextUnit();
        }

        //将单位为KB和B的容量，转换成单位为MB的容量
        if (DataUnit.KB == currentDataUnit) {
            currentValue = currentValue / MagicNumber.DOUBLE1024;
            currentDataUnit = DataUnit.MB;
        } else if (DataUnit.Byte == currentDataUnit) {
            currentValue = currentValue / MagicNumber.DOUBLE1024 / MagicNumber.DOUBLE1024;
            currentDataUnit = DataUnit.MB;
        }

        BigDecimal bd = new BigDecimal(currentValue);
        bd = bd.setScale(MagicNumber.INT3, BigDecimal.ROUND_DOWN);

        //需要删除无用的小数点
        if (isDeleteZero) {
            return deleteUnnecessaryZero(DF2.format(bd.doubleValue())) + ' ' + currentDataUnit.toString();
        }
        //默认保留三位小数
        else {
            return DF2.format(bd.doubleValue()) + ' ' + currentDataUnit.toString();
        }
    }

    /**
     * 自动将扇区容量转换为界面上显示容量, 默认扇区数量
     *
     * @param sectors 扇区数量
     * @param <T>     泛型
     * @return String 返回结果
     */
    public static <T extends Number> String adaptSectorConvert(T sectors) {
        return adaptSectorConvert(sectors, SECTOR_SIZE);
    }

    /**
     * 自动将扇区容量转换为界面上显示容量
     *
     * @param sectors    扇区数量
     * @param sectorSize 扇区大小
     * @param <T>        泛型
     * @return String 返回结果
     */
    public static <T extends Number> String adaptSectorConvert(T sectors,
                                                               long sectorSize) {
        Double capacity = sectors.doubleValue() * sectorSize;
        return adaptConvert2(capacity, DataUnit.Byte);
    }

    /**
     * 将时间转换为界面上显示的时间
     *
     * @param time     时间的绝对值
     * @param timeUnit 时间的单位
     * @param <T>      泛型
     * @return String 界面上显示的时间
     */
    public static <T extends Number> String adaptTimeConvert(T time,
                                                             TimeUnit timeUnit) {
        StringBuilder timeStr = new StringBuilder("");
        Long timeValue = time.longValue();

        if (IsmConstant.UNDEFINE_VALUE == timeValue
                || IsmConstant.UNDEFINE_LONG_VALUE == timeValue
                || 0 > timeValue) {
            return IsmConstant.BLANK_CONTENT;
        }

        if (timeValue <= 0) {
            return "0 " + timeUnit;
        }

        // 获得需要转换的最大次数
        int range = getTwoTimeUnitRange(timeUnit);
        for (int i = range; i >= 0; i--) {
            // 获得当前单位与上一级单位之间的换算率
            long rate = timeUnit.getNextUnit().getScale() / timeUnit.getScale();
            // 不能向上一级转换时
            if (timeValue / rate == 0 || rate == 1L) {
                timeStr.append(timeValue + IsmConstant.BLANK + timeUnit);
                break;
            }
            // 能向上转换时，首先取得当前时间单位下的时间数目
            long num = timeValue % rate;
            // 不能被整除时
            if (num != 0L) {
                timeStr.append(num + IsmConstant.BLANK + timeUnit + ',');
            }
            // 时间单位向上跳一级，修改时间值
            timeUnit = timeUnit.getNextUnit();
            timeValue = timeValue / rate;
        }
        // 更改显示顺序
        StringBuilder result = new StringBuilder();
        for (int i = timeStr.toString().trim().split(",").length - 1; i >= 0; i--) {
            result.append(timeStr.toString().trim().split(",")[i] + ' ');
        }
        return result.toString();
    }

    /**
     * 获得指定时间单位与最大时间单位（天）之间的换算次数
     */
    private static int getTwoTimeUnitRange(TimeUnit timeUnit) {
        int range = 0;
        switch (timeUnit) {
            case Nanosecond:
                range = MagicNumber.INT6;
                break;
            case Microsecond:
                range = MagicNumber.INT5;
                break;
            case Millisecond:
                range = MagicNumber.INT4;
                break;
            case Second:
                range = MagicNumber.INT3;
                break;
            case Minute:
                range = MagicNumber.INT2;
                break;
            case Hour:
                range = 1;
                break;
            default:
                break;
        }
        return range;
    }

    /**
     * 首先将传入的容量单位转换为Byte，然后通过递归方式转换为UCD要求的容量显示
     * 自适应转换容量单位，如果该数量级单位大于1024的倍数，将自动转换为更大級別的数量单位.反之亦然。
     *
     * @param srcUnit 输入的源数量级单位
     * @param t       方法参数：t
     * @param <T>     泛型
     * @return String 返回结果
     */
    @Deprecated
    public static <T extends Number> String adaptConvert(T t, DataUnit srcUnit) {
        double value = t.doubleValue();
        if (value == IsmConstant.UNDEFINE_VALUE
                || IsmConstant.UNDEFINE_LONG_VALUE == value) {
            return IsmConstant.BLANK_CONTENT;
        }

        //如果传入t为0，则返回值应该为0+单位，而非0.0000+单位
        if (0 == value) {
            return "0 " + srcUnit.name();
        }

        Pair<DataUnit, Double> currentPair = new Pair<DataUnit, Double>(
                srcUnit, value);
        if (canUpConvert(currentPair)) {
            currentPair = upConvert(currentPair);
        } else if (canDownConvert(currentPair)) {
            currentPair = downConvert(currentPair);
        }

        return doubleFormat(currentPair.getValue()) + ' '
                + currentPair.getKey().toString();
    }

    /*
     * 是否能够向上转换
     */
    private static boolean canUpConvert(Pair<DataUnit, Double> currentPair) {
        if (currentPair.getKey() == DataUnit.PB) {
            return false;
        }

        if (currentPair.getValue() < MagicNumber.DOUBLE1024) {
            return false;
        }

        BigDecimal result = new BigDecimal(currentPair.getValue().toString()).divide(new BigDecimal(
                "1024"));

        if (result.scale() > MagicNumber.INT2) {
            return false;
        }

        return true;

    }

    /*
     * 是否能够向下转换
     */
    private static boolean canDownConvert(Pair<DataUnit, Double> currentPair) {
        if (currentPair.getKey() == DataUnit.Byte) {
            return false;
        }

        BigDecimal value = new BigDecimal(currentPair.getValue().toString());

        if (value.scale() <= MagicNumber.INT2) {
            return false;
        }

        String strValue = new DecimalFormat("#.######").format(value.multiply(new BigDecimal(
                "1024")));
        BigDecimal result = new BigDecimal(strValue);

        if (result.scale() > MagicNumber.INT2) {
            return false;
        }

        return true;
    }

    /*
     * 向上转换
     */
    private static Pair<DataUnit, Double> upConvert(
            Pair<DataUnit, Double> currentPair) {
        double newValue = currentPair.getValue() / MagicNumber.DOUBLE1024;
        Pair<DataUnit, Double> newPair = new Pair<DataUnit, Double>(
                currentPair.getKey().getNextUnit(), newValue);
        if (canUpConvert(newPair)) {
            return upConvert(newPair);
        } else {
            return newPair;
        }
    }

    /*
     * 向下转换
     */
    private static Pair<DataUnit, Double> downConvert(
            Pair<DataUnit, Double> currentPair) {
        double newValue = currentPair.getValue() * MagicNumber.DOUBLE1024;
        Pair<DataUnit, Double> newPair = new Pair<DataUnit, Double>(
                currentPair.getKey().getPreviousUnit(), newValue);
        if (canDownConvert(newPair)) {
            return downConvert(newPair);
        } else {
            return newPair;
        }
    }

    //    public static void main(String[] args)
    //    {
    //        System.out.println(adaptConvert(Double.valueOf(5242880), DataUnit.MB));
    //        System.out.println(adaptConvert(Double.valueOf(5079.04), DataUnit.MB));
    //        System.out.println(adaptConvert(Double.valueOf(3.625), DataUnit.GB));
    //        System.out.println(adaptConvert(Double.valueOf(399.36), DataUnit.TB));
    //        System.out.println(adaptConvert(Double.valueOf(76533.76), DataUnit.TB));
    //        System.out.println(adaptConvert(Double.valueOf(4096), DataUnit.TB));
    //        System.out.println(adaptConvert(Double.valueOf(4.96), DataUnit.TB));
    //    }

    //    /*
    //     * 通过递归方式将输入的容量转换为UCD需要显示的容量
    //     */
    //    private static Pair<DataUnit, Double> adaptConvert(double value,
    //            DataUnit srcUnit, DataUnit targetUnit)
    //    {
    //        //如果转换为目标单位后的值满足显示要求或已经转换为最大容量单位了，则直接返回
    //        double targetUnitVal = convert(value, srcUnit, targetUnit);
    //        double floorValue = Math.floor(value);
    //        double diff = value - floorValue;
    //        double diffValue = diff * UNIT_SCALE;
    //        if (diffValue > 0.01d)
    //        {
    //            double targetValue = convert(targetUnitVal,
    //                    targetUnit,
    //                    targetUnit.getPreviousUnit());
    //            return new Pair<DataUnit, Double>(targetUnit.getPreviousUnit(),
    //                    targetValue);
    //        }
    //
    //        if (targetUnitVal < UNIT_SCALE
    //                || targetUnit == targetUnit.getNextUnit())
    //        {
    //            return new Pair<DataUnit, Double>(targetUnit, targetUnitVal);
    //        }
    //
    //        //如果转换后还不能满足UCD要求，则进行下一次递归转换
    //        return adaptConvert(targetUnitVal, targetUnit, targetUnit.getNextUnit());
    //    }

    /**
     * 把双精度型转换为逗号分隔的字符串，小数点后面保留2位小数 当参数为整数时，小数点后面带三个0 并进行四舍五入 例： 12345 ->
     * 12,345.0 <br>
     * 123456.4567892 -> 123,456.5 <br>
     * 0.003-> 0.003 如果小数点前为0,小数点后面从第一个不为0的数开始至少1位有效数字
     *
     * @param data 方法参数：data
     *             双精度数
     * @return String 返回结果
     * @author AZ7D00113 g00117967
     */
    public static String doubleFormat(double data) {
        if (IsmConstant.UNDEFINE_VALUE == data
                || IsmConstant.UNDEFINE_LONG_VALUE == data) {
            return IsmConstant.BLANK_CONTENT;
        } else {
            return DF.format(data);
        }
    }

    /**
     * 把双精度型转换为逗号分隔的字符串，小数点后面保留3位小数
     *
     * @param data 方法参数：data
     * @return String 返回结果
     */
    public static String doubleFormat2(double data) {
        if (IsmConstant.UNDEFINE_VALUE == data
                || IsmConstant.UNDEFINE_LONG_VALUE == data) {
            return IsmConstant.BLANK_CONTENT;
        }

        if (0 == data) {
            return "0";
        }

        return DF2.format(data);
    }

    /**
     * 方法 ： format
     *
     * @param data 方法参数：data
     * @return String 返回结果
     */
    public static String format(double data) {
        if (IsmConstant.UNDEFINE_VALUE == data
                || IsmConstant.UNDEFINE_LONG_VALUE == data) {
            return IsmConstant.BLANK_CONTENT;
        } else {
            return DF_WITHOUT_COMMA.format(data);
        }
    }

    /**
     * 把长整型转换为逗号分隔字符串, 当参数为整型时，发生整型到长整型的宽转换。 十进制表示。当参数为八进制时，转化为十进制，省略前导零。
     *
     * @param data 方法参数：data
     *             长整型数字，如12345678L, 1234
     * @return String 返回结果
     */
    public static String commaFormat(long data) {
        if (IsmConstant.UNDEFINE_VALUE == data
                || IsmConstant.UNDEFINE_LONG_VALUE == data) {
            return IsmConstant.BLANK_CONTENT;
        } else {
            return new Formatter(new StringBuilder(), Locale.CHINA).format("%,"
                            + String.valueOf(data).length() + "d",
                    data).toString();
        }
    }

    /**
     * 将MB转换为GB后再四舍五入
     *
     * @param mb 容量MB
     * @return NumberCollator 返回结果
     */
    public static NumberCollator convertMB2GB(double mb) {
        return new NumberCollator(Unit.convert(mb,
                Unit.DataUnit.MB,
                Unit.DataUnit.GB));
    }

    /**
     * 删除小数后面多余的0 如2.230编程2.23
     * 请保证value为一个double型的字符串，勿传入非法字符
     *
     * @param value 小数
     */
    private static String deleteUnnecessaryZero(String value) {
        String result = String.valueOf(value);
        //只有存在小数点时才做处理
        if (result.indexOf(".") != -1) {
            int length = result.length();
            char c = result.charAt(--length);
            //最后一位是0，证明此0无用 删除到不需删除为止
            while (c == '0') {
                result = result.substring(0, length);
                c = result.charAt(--length);
            }

            //如果最后只剩下.，进行删除
            if (c == '.') {
                result = result.substring(0, length);
            }
        }

        return result;
    }
}
