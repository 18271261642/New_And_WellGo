package com.truescend.gofit.utils;

import android.content.Context;

import com.sn.utils.DateUtil;
import com.truescend.gofit.R;

import java.util.Calendar;
import java.util.Date;

/**
 * 类型：日历工具类
 * Create by 泽鑫
 */
public class CalendarUtil {
    public static final String YYYYMMDD = "yyyy/MM/dd";
    public static final String YYYYMM = "yyyy/MM";
    public static final String MMDD = "MM/dd";

    private CalendarUtil() {}

    /**
     * 获取年月日，星期字符串
     * @return 字符串
     */
    public static String getDateAndWeekString(Context context, Calendar calendar){
        String[] week = context.getResources().getStringArray(R.array.week_day);
        int num = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        return DateUtil.getDate(DateUtil.YYYY_MM_DD,calendar) + " " + week[num];
    }

    /**
     * 获取年月日，星期字符串
     * @return 字符串
     */
    public static String getDateAndWeekString(Context context, Date date){
        String[] week = context.getResources().getStringArray(R.array.week_day);
        Calendar calendar = DateUtil.getCurrentCalendar();
        calendar.setTime(date);
        int num = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        return DateUtil.getDate(DateUtil.YYYY_MM_DD,date) + week[num];
    }

}
