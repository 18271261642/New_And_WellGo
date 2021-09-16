package com.truescend.gofit.pagers.home.pressure;

import com.truescend.gofit.pagers.home.pressure.bean.BloodPressureDetailItem;
import com.truescend.gofit.views.BloodPressureChartView;

import java.util.Calendar;
import java.util.List;

/**
 * Author:Created by 泽鑫 on 2017/12/7 18:21.
 */

public class IBloodPressureContract {
    interface IView {
        /**
         * 更新天周月数据
         *
         * @param data
         */
        void onUpdateTodayChartData(List<BloodPressureChartView.BloodPressureItem> data);

        void onUpdateWeekChartData(List<BloodPressureChartView.BloodPressureItem> data);

        void onUpdateMonthChartData(List<BloodPressureChartView.BloodPressureItem> data);
        /**
         * 日期访问 从-到
         * @param dateFromAndTo
         */
        void onUpdateDateRange(String dateFromAndTo);
        /**
         * 更新统计
         * @param diastolic 舒张压
         * @param systolic 收缩压
         */
        void onUpdateStatisticsData(CharSequence diastolic, CharSequence systolic);

        /**
         * 刷新详情数据
         */
        void onUpdateDetailListData(List<BloodPressureDetailItem> items);

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
