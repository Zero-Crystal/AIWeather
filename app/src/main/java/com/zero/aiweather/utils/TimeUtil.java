package com.zero.aiweather.utils;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeUtil {
    public static final String format1 = "HH:mm";
    public static final String format2 = "yyyy-MM-dd HH:mm";

    /**
     * 获取当前时间：HH:mm
     * */
    public static String getCurrentTime(String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        String date = sdf.format(new java.util.Date());
        return date;
    }

    /**
     * 字符串转换成时间对象
     */
    public static Date stringToDate(String dateString, String format) {
        if (dateString.equals(null)||dateString==null) {
            return null;
        }
        Date formatDate = null;
        DateFormat dateFormat = new SimpleDateFormat(format);
        try {
            formatDate = dateFormat.parse(dateString);
        } catch (ParseException e) {
            return null;
        }
        return formatDate;
    }

    /**
     * 获取两个日期之间的时间差
     * */
    public static long getTwoDaysDiffer(Date start, Date end) {
        if (start == null) {
            return 0;
        }
        if (end == null) {
            end = new Date();
        }
        long longs = (end.getTime() - start.getTime());
        return longs;
    }

    /**
     * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指
     * 定精度，以后的数字四舍五入。
     * @param v1 被除数
     * @param v2 除数
     * @param scale 表示表示需要精确到小数点以后几位。
     * @return 两个参数的商
     */
    public static double div(double v1, double v2, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException(
                    "The scale must be a positive integer or zero");
        }
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }
}
