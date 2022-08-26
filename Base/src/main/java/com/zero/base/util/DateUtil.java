package com.zero.base.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class DateUtil {
    public static final String format1 = "yyyyMMdd";
    public static final String format2 = "yyyy-MM-dd HH:mm";

    /**
     * 获取当前日期 yyyy-MM-dd HH:mm
     *
     * @return String
     */
    public static String getNowDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat(format2);
        return sdf.format(new Date());
    }

    /**
     * 获取当前日期 yyyy-MM-dd
     *
     * @return String
     */
    public static String getNowDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(new Date());
    }

    /**
     * 获取当前时间 HH:mm
     *
     * @return String
     */
    public static String getNowTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(new Date());
    }

    /**
     * 获取当前日期
     *
     * @param format String(eg: "yyyyMMdd")
     * @return String
     * */
    public static String getNowDate(String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        return sdf.format(new Date());
    }

    /**
     * 昨天
     *
     * @param date Date
     * @return String
     */
    public static String getYesterday(Date date) {
        String tomorrow = "";
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, -1);
        date = calendar.getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        tomorrow = formatter.format(date);
        return tomorrow;
    }

    /**
     * 明天
     *
     * @param date Date
     * @return String
     */
    public static String getTomorrow(Date date) {
        String tomorrow = "";
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, +1);
        date = calendar.getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        tomorrow = formatter.format(date);
        return tomorrow;
    }

    /**
     * 后天
     *
     * @param date Date
     * @return String
     */
    public static String getTomorrowAfter(Date date) {
        String tomorrow = "";
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, +2);
        date = calendar.getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        tomorrow = formatter.format(date);
        return tomorrow;
    }

    /**
     * 获取当前日期(精确到毫秒)
     *
     * @return String
     */
    public static String getNowTimeDetail() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");
        return sdf.format(new Date());
    }

    /**
     * 获取今天是星期几
     * @param date Date
     * @return String
     */
    public static String getWeekOfDate(Date date) {
        String[] weekDays = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        return weekDays[w];
    }

    /**
     * 计算星期几
     *
     * @param dateTime String
     * @return int
     */
    private static int getDayOfWeek(String dateTime) {
        Calendar cal = Calendar.getInstance();
        if (dateTime.equals("")) {
            cal.setTime(new Date(System.currentTimeMillis()));
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date;
            try {
                date = sdf.parse(dateTime);
            } catch (ParseException e) {
                date = null;
                e.printStackTrace();
            }
            if (date != null) {
                cal.setTime(new Date(date.getTime()));
            }
        }
        return cal.get(Calendar.DAY_OF_WEEK);
    }

    /**
     * 根据年月日计算是星期几并与当前日期判断，非昨天、今天、明天 则以星期显示
     *
     * @param dateTime String
     * @return String
     */
    public static String Week(String dateTime) {
        String[] weekDays = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
        String yesterday = getYesterday(new Date());
        String today = getNowDate();
        String tomorrow = getTomorrow(new Date());
        String week = "";
        if (dateTime.equals(yesterday)) {
            week = "昨天";
        } else if (dateTime.equals(today)) {
            week = "今天";
        } else if (dateTime.equals(tomorrow)) {
            week = "明天";
        } else {
            week = weekDays[getDayOfWeek(dateTime) - 1];
        }
        return week;
    }

    /**
     * 获取“昨天”、“今天”、“明天”、“后天”，其他直接返回日期
     *
     * @param date String
     * @return String
     */
    public static String showDateInfo(String date) {
        String yesterday = getYesterday(new Date());
        String today = getNowDate();
        String tomorrow = getTomorrow(new Date());
        String tomorrowAfter = getTomorrowAfter(new Date());
        String dateInfo = "";
        if (date.equals(yesterday)) {
            dateInfo = "昨天";
        } else if (date.equals(today)) {
            dateInfo = "今天";
        } else if (date.equals(tomorrow)) {
            dateInfo = "明天";
        } else if (date.equals(tomorrowAfter)) {
            dateInfo = "后天";
        } else {
            dateInfo = date;
        }
        return dateInfo;
    }

    /**
     * 根据传入的时间显示时间段描述信息
     *
     * @param timeData String
     * @return String
     */
    public static String getTimeInfo(String timeData) {
        String timeInfo = null;
        int time = 0;
        time = Integer.parseInt(timeData.trim().substring(0, 2));
        if (time >= 0 && time <= 6) {
            timeInfo = "凌晨";
        } else if (time > 6 && time <= 12) {
            timeInfo = "上午";
        } else if (time > 12 && time <= 13) {
            timeInfo = "中午";
        } else if (time > 13 && time <= 18) {
            timeInfo = "下午";
        } else if (time > 18 && time <= 24) {
            timeInfo = "晚上";
        } else {
            timeInfo = "未知";
        }
        return timeInfo;
    }

    /**
     * 获取两个日期之间的时间差
     *
     * @param start Date
     * @param end Date
     * @return long
     */
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
     * 字符串转换成时间对象
     *
     * @param dateString String
     * @param format String
     * @return Date
     */
    public static Date stringToDate(String dateString, String format) {
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
     * 将时间字符串转为时间戳字符串
     *
     * @param time String
     * @return String
     */
    public static String stringToTimestamp(String time) {
        String timestamp = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            long longTime = sdf.parse(time).getTime() / 1000;
            timestamp = Long.toString(longTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timestamp;
    }

    /**
     * 将时间戳转化为对应的时间(10位或者13位都可以)
     *
     * @param time long
     * @return "yyyy-MM-dd HH:mm"
     */
    public static String formatTime(long time) {
        String times = null;
        if (String.valueOf(time).length() > 10) {
            // 10位的秒级别的时间戳
            times = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(time * 1000));
        } else {
            // 13位的秒级别的时间戳
            times = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(time);
        }
        return times;
    }
}

