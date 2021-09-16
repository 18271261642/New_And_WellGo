package com.truescend.gofit.pagers.home.sport;

import java.util.Calendar;
import java.util.List;

/**
 * Author:Created by 泽鑫 on 2017/12/1 10:30.
 */

public class ISportContract {
    interface IView {
        /**
         * 更新周月年数据
         * @param data
         * @param standardsPosData
         */
        void onUpdateWeekChartData(List<Integer> data, List<Boolean> standardsPosData);
        void onUpdateMonthChartData(List<Integer> data, List<Boolean> standardsPosData);
        void onUpdateYearChartData(List<Integer> data, List<Boolean> standardsPosData);

        /**
         * 日期访问 从-到
          * @param dateFromAndTo
         */
        void onUpdateDateRange(String dateFromAndTo);

        /**
         * item数据
         * @param dateType 数据类型(周年月)
         * @param standardDays 达标次数
         * @param stepTotal 总步数
         * @param stepAverageTotal 步数 (周月)日均/(年)月均
         * @param calorieAverageTotal 卡路里 (周月)日均/(年)月均
         * @param distanceTotal  总距离
         * @param distanceAverageTotal  距离 (周月)日均/(年)月均
         */
        void onUpdateItemChartData(int dateType, String standardDays, String stepTotal, String stepAverageTotal, String calorieAverageTotal, String distanceTotal, String distanceAverageTotal);
    }

    interface IPresenter {
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
        /**
         * 请求加载年图表数据
         * @param calendar
         */
        void requestLoadYearChart(Calendar calendar);
    }
}
