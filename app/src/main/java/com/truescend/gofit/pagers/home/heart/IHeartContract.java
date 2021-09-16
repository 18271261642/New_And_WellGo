package com.truescend.gofit.pagers.home.heart;

import com.truescend.gofit.pagers.home.heart.bean.HeartRateDetailItem;

import java.util.Calendar;
import java.util.List;

/**
 * Author:Created by 泽鑫 on 2017/12/7 16:39.
 */

public class IHeartContract {
    interface IView {
        /**
         * 更新天周月数据
         *
         * @param data
         */
        void onUpdateTodayChartData(List<Integer> data);

        void onUpdateWeekChartData(List<Integer> data);

        void onUpdateMonthChartData(List<Integer> data);
        /**
         * 日期访问 从-到
         * @param dateFromAndTo
         */
        void onUpdateDateRange(String dateFromAndTo);
        /**
         * 更新统计
         * @param max
         * @param avg
         * @param min
         */
        void onUpdateStatisticsData(CharSequence max, CharSequence avg, CharSequence min);

        /**
         * 刷新详情数据
         */
        void onUpdateDetailListData(List<HeartRateDetailItem> items);
    }

    interface IPresenter {
        /**
         * 请求加载天图表数据
         * @param calendar
         */
        void requestLoadTodayChart(Calendar calendar);

        /**
         * 请求加载周图表数据
         * @param calendar
         */
        void requestLoadWeekChart(Calendar calendar);

        /**
         * 请求加载月图表数据
         * @param calendar
         */
        void requestLoadMonthChart(Calendar calendar);



    }
}
