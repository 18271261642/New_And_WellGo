package com.truescend.gofit.pagers.home.oxygen;

import com.truescend.gofit.pagers.home.oxygen.bean.BloodOxygenDetailItem;

import java.util.Calendar;
import java.util.List;

/**
 * Author:Created by 泽鑫 on 2017/12/8 10:22.
 */

public class IBloodOxygenContract {
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
        void onUpdateDetailListData(List<BloodOxygenDetailItem> items);
    }

    interface IPresenter {
        /**
         * 请求加载天图表数据
         */
        void requestLoadTodayChart(Calendar calendar);

        /**
         * 请求加载周图表数据
         */
        void requestLoadWeekChart(Calendar calendar);

        /**
         * 请求加载月图表数据
         */
        void requestLoadMonthChart(Calendar calendar);

    }
}
