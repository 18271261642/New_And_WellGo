package com.truescend.gofit.pagers.home.sleep.details;

import java.util.Calendar;
import java.util.List;

/**
 * Author:Created by 泽鑫 2017/12/7 15:54.
 */

public class ISleepDetailsContract {
    interface IView {
        /**
         * 更新周月年数据
         *
         * @param data
         */
        void onUpdateWeekChartData(List<Integer> data);

        void onUpdateMonthChartData(List<Integer> data);

        void onUpdateYearChartData(List<Integer> data);
        /**
         * 日期访问 从-到
         * @param dateFromAndTo
         */
        void onUpdateDateRange(String dateFromAndTo);
        /**
         * 更新Item
         * @param goodDays                          良好天数
         * @param validTotalSleepTime               有效睡眠市时长 h
         * @param validAvgSleepTime
         * @param maxSingleSleepTime
         * @param validSleepPercent
         * @param quality                           睡眠质量
         */
        void onUpdateItemChartData(String goodDays, CharSequence validTotalSleepTime, CharSequence validAvgSleepTime, CharSequence maxSingleSleepTime, int validSleepPercent, String quality);
    }

    interface IPresenter {
        /**
         * 请求加载周图表数据
         *
         * @param calendar
         */
        void requestLoadWeekChart(Calendar calendar);

        /**
         * 请求加载月图表数据
         *
         * @param calendar
         */
        void requestLoadMonthChart(Calendar calendar);

        /**
         * 请求加载年图表数据
         *
         * @param calendar
         */
        void requestLoadYearChart(Calendar calendar);
    }
}
