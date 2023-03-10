package com.bll.lnkteacher.utils;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by seatrend on 2018/8/21.
 */

public class DateUtils {

    public static long daylong=24*60*60*1000;

    /**
     * 基于当天剩余天数
     * @param date
     * @return
     */
    public static int sublongToDay(long date,long now){

        long sub=date-now;
        int day = 0;
        if (sub<0){
            day=0;
        }else {
            day= (int) Math.ceil (sub/daylong);
        }
        return day;
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
            return sdf.format(new Date(date));
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
            return sdf.format(new Date(date));
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
            return sdf.format(new Date(date));
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
            return sdf.format(new Date(date));
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
            return sdf.format(new Date(date));
        } catch (Exception e) {
            return null;
        }
    }

    public static String longToStringWeek(long date) {
        if(0 == date){
            return null;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("MM月dd  EEEE", Locale.CHINA); // "yyyy-MM-dd HH:mm:ss"
            return sdf.format(new Date(date));
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
            return sdf.format(new Date(date));
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
            return sdf.format(new Date(date));
        } catch (Exception e) {
            return null;
        }
    }
    public static String longToStringDataNoYearNoHour(long date) {
        if(0 == date){
            return null;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日", Locale.CHINA); // "yyyy-MM-dd HH:mm:ss"
            return sdf.format(new Date(date));
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
    public static String intToStringDataNoHour(int date) {
        if(0 == date){
            return null;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA); // "yyyy-MM-dd HH:mm:ss"
            return sdf.format(new Date(date*1000L));
        } catch (Exception e) {
            return null;
        }
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

}
