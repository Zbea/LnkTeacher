package com.bll.lnkteacher.utils;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;



public class DateUtils {

    // 判断是闰年还是平年
    public boolean isYear(int year) {
        boolean judge = false;
        if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) {
            judge = true;
        }
        return judge;
    }

    /**
     * 基于当天剩余天数
     * @param date
     * @return
     */
    public static int sublongToDay(long date,long now){
        long daylong=24*60*60*1000;
        long sub=date-now;
        int day = 0;
        if (sub<0){
            day=-1;
        }else {
            day= (int) Math.ceil (sub/daylong);
        }
        return day;
    }

    public static String secondToString(int second){
        SimpleDateFormat sdf = new SimpleDateFormat("mm:ss", Locale.CHINA); // "yyyy-MM-dd HH:mm:ss"
        return sdf.format(new Date(second));
    }

    public static long longToMinute(long date) {
        int minuteTime=60*1000;
        if (date>0){
            long minute=date/minuteTime;
            if (date%minuteTime>0){
                minute+=1;
            }
            return minute;
        }
        return 0L;
    }

    /**
     * 时间戳转换为字符串类型
     *
     * @return
     */
    public static String longToStringData(long date) {
        if(0 == date){
            return null;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA); // "yyyy-MM-dd HH:mm:ss"
            return sdf.format(new Date(date10ToDate13(date)));
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 时间戳转换为字符串类型
     *
     * @return
     */
    public static String longToStringNoYear(long date) {
        if(0 == date){
            return null;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm", Locale.CHINA); // "yyyy-MM-dd HH:mm:ss"
            return sdf.format(new Date(date10ToDate13(date)));
        } catch (Exception e) {
            return null;
        }
    }

    public static String longToStringNoYear1(long date) {
        if(0 == date){
            return null;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日HH:mm", Locale.CHINA); // "yyyy-MM-dd HH:mm:ss"
            return sdf.format(new Date(date10ToDate13(date)));
        } catch (Exception e) {
            return null;
        }
    }

    public static String longToString(long date) {
        if(0 == date){
            return null;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmSS", Locale.CHINA);
            return sdf.format(new Date(date10ToDate13(date)));
        } catch (Exception e) {
            return null;
        }
    }
    public static String longToStringDataNoYear(long date) {
        if(0 == date){
            return null;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日", Locale.CHINA); // "yyyy-MM-dd HH:mm:ss"
            return sdf.format(new Date(date10ToDate13(date)));
        } catch (Exception e) {
            return null;
        }
    }


    /**
     * 时间戳转换为字符串类型
     *
     * @return
     */
    public static String longToHour(long date) {
        if(0 == date){
            return null;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.CHINA); // "yyyy-MM-dd HH:mm:ss"
            return sdf.format(new Date(date10ToDate13(date)));
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 时间戳转换为字符串类型
     *
     * @return
     */
    public static String longToHour1(long date) {
        if(0 == date){
            return null;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("H:m", Locale.CHINA); // "yyyy-MM-dd HH:mm:ss"
            return sdf.format(new Date(date10ToDate13(date)));
        } catch (Exception e) {
            return null;
        }
    }

    public static String longToStringWeek(long date) {
        if(0 == date){
            return null;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日  E", Locale.CHINA); // "yyyy-MM-dd HH:mm:ss"
            return sdf.format(new Date(date10ToDate13(date)));
        } catch (Exception e) {
            return null;
        }
    }



    public static String longToStringWeek1(long date) {
        if(0 == date){
            return null;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日  E", Locale.CHINA); // "yyyy-MM-dd HH:mm:ss"
            return sdf.format(new Date(date10ToDate13(date)));
        } catch (Exception e) {
            return null;
        }
    }

    public static String longToStringCalender(long date) {
        if(0 == date){
            return null;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.CHINA); // "yyyy-MM-dd HH:mm:ss"
            return sdf.format(new Date(date10ToDate13(date)));
        } catch (Exception e) {
            return null;
        }
    }

    public static String longToStringDataNoHour(long date) {
        if(0 == date){
            return null;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA); // "yyyy-MM-dd HH:mm:ss"
            return sdf.format(new Date(date10ToDate13(date)));
        } catch (Exception e) {
            return null;
        }
    }

    //返回当前年月日
    String getNowDate()
    {
        Date date = new Date();
        String nowDate = new SimpleDateFormat("yyyy年MM月dd日").format(date);
        return nowDate;
    }

    //返回当前年份
    public static int getYear()
    {
        Date date = new Date();
        String year = new SimpleDateFormat("yyyy").format(date);
        return Integer.parseInt(year);
    }

    //返回当前月份
    public static int getMonth()
    {
        Date date = new Date();
        String month = new SimpleDateFormat("MM").format(date);
        return Integer.parseInt(month);
    }
    //返回当前日
    public static int getDay()
    {
        Date date = new Date();
        String month = new SimpleDateFormat("dd").format(date);
        return Integer.parseInt(month);
    }


    /**
     *  把秒换算成 "yyyy-MM-dd"
     * @param date
     * @return
     */
    public static String intToStringDataNoHour(long date) {
        if(0 == date){
            return null;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA); // "yyyy-MM-dd HH:mm:ss"
            return sdf.format(new Date(date10ToDate13(date)));
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 字符串日期时间格式化为long型
     * @param dateStr
     * @return
     * @throws ParseException
     */
    public static long dateStrToLong(String dateStr) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        Date date = sdf.parse(dateStr);
        return date.getTime();
    }

    /**
     * 将10位转成13位
     * @param date
     * @return
     */
    public static long date10ToDate13(long date){
        String dateStr=""+date;
        if (dateStr.length()==10)
        {
            date=date*1000;
        }
        return date;
    }

    /**
     * 获取每月第一天为星期几 1周日 7周六
     */
    public static int getMonthOneDayWeek(int year,int month){
        Calendar a = Calendar.getInstance();
        a.set(Calendar.YEAR, year);
        a.set(Calendar.MONTH, month);
        a.set(Calendar.DATE, 1); //把日期设置为当月第一天
        return a.get(Calendar.DAY_OF_WEEK);
    }

    /**
     * 获取当月有几天
     */
    public static int getMonthMaxDay(int year,int month) {
        Calendar a = Calendar.getInstance();
        a.set(Calendar.YEAR, year);
        a.set(Calendar.MONTH, month);
        return a.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    /**
     * 获取当天星期几 2星期一 8星期日
     * @param date
     * @return
     */
    public static int getWeek(long date) {

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(date);
        int week = cal.get(Calendar.DAY_OF_WEEK);
        if (week <=1) {
            week = 8;
        }
        return week;
    }

    public static long dateToStamp(int year,int month,int day) {
        String s=year+"-"+month+"-"+day;
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
            Date date = simpleDateFormat.parse(s);
            return date.getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static long dateToStamp(String s) {

        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
            Date date = simpleDateFormat.parse(s);
            return date.getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public static long date2Stamp(String s) {

        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
            Date date = simpleDateFormat.parse(s);
            return date.getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public static long date3Stamp(String s) {

        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm",Locale.CHINA);
            Date date = simpleDateFormat.parse(s);
            return date.getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public static long date4Stamp(String s) {

        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日HH:mm",Locale.CHINA);
            Date date = simpleDateFormat.parse(s);
            return date.getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }


    /**
     *
     * 把时间long 的格式转为天
     * @return
     */
    public static String longToDay(long time){
        return String.valueOf(time/(24*60*60*1000));
    }

    //得到当前年月日数值
    public static int[] getDateNumber(long date){
        Calendar a = Calendar.getInstance();
        a.setTime(new Date(date));
        int year = a.get(Calendar.YEAR);
        int month = a.get(Calendar.MONTH);
        int day=a.get(Calendar.DAY_OF_MONTH);
        return new int[]{year,month,day};
    }

    public static long getStartOfDayInMillis() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    public static long getEndOfDayInMillis() {
        // Add one day's time to the beginning of the day.
        // 24 hours * 60 minutes * 60 seconds * 1000 milliseconds = 1 day
        return getStartOfDayInMillis() + (24 * 60 * 60 * 1000);
    }

    public static long getStartOfDayInMillis(long date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(date));
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    /**
     * @param date the date in the format "yyyy-MM-dd"
     */
    public static long getEndOfDayInMillis(long date){
        return getStartOfDayInMillis(date) + (24 * 60 * 60 * 1000);
    }

    /**
     * 获取当前时间所在周开始、结束时间
     * @return
     */
    public static long[] getCurrentWeekTimeFrame() {
        Calendar calendar = Calendar.getInstance();
        //start of the week
        if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            calendar.add(Calendar.DAY_OF_YEAR,-1);
        }
        calendar.add(Calendar.DAY_OF_WEEK, -(calendar.get(Calendar.DAY_OF_WEEK) - 2));
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        long startTime = calendar.getTimeInMillis();
        //end of the week
        calendar.add(Calendar.DAY_OF_WEEK, 6);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        long endTime = calendar.getTimeInMillis();
        return new long[]{startTime, endTime};
    }

    /**
     * 获取当前时间所在周开始、结束时间
     * @return
     */
    public static long[] getCurrentWeekTimeFrame(long date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(date));
        //start of the week
        if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            calendar.add(Calendar.DAY_OF_YEAR,-1);
        }
        calendar.add(Calendar.DAY_OF_WEEK, -(calendar.get(Calendar.DAY_OF_WEEK) - 2));
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        long startTime = calendar.getTimeInMillis();
        //end of the week
        calendar.add(Calendar.DAY_OF_WEEK, 6);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        long endTime = calendar.getTimeInMillis();
        return new long[]{startTime, endTime};
    }

}
