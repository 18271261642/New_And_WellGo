package com.truescend.gofit.pagers.home.sport_mode;


import androidx.annotation.IntRange;

import com.truescend.gofit.pagers.home.sport_mode.bean.SportModeDetailItem;
import com.truescend.gofit.views.PieChartView;

import java.util.Calendar;
import java.util.List;

/**
 * 作者:东芝(2019/06/01).
 * 功能:运动模式
 */

public class ISportModeContract {

    interface IView {
        /**
         * 更新周月年数据
         *
         * @param data
         * @param chartCenterText
         */
        void onUpdateWeekChartData(List<PieChartView.PieDataEntry> data, CharSequence chartCenterText);

        void onUpdateWeekSportModeDetailChart(List<PieChartView.PieDataEntry> data, CharSequence chartCenterText);

        void onUpdateMonthChartData(List<PieChartView.PieDataEntry> data, CharSequence chartCenterText);

        void onUpdateMonthRangeChartData(List<PieChartView.PieDataEntry> data, CharSequence chartCenterText);

        /**
         * 日期访问 从-到
         *
         * @param dateFromAndTo
         */
        void onUpdateDateRange(String dateFromAndTo);

        /**
         * item数据
         */
        void onUpdateItemChartData(List<SportModeDetailItem> detailItems);

    }

    interface IPresenter {
        /**
         * 请求加载周图表数据
         * @param calendar
         */
        void requestLoadWeekChart(  Calendar calendar);

        /**
         * 请求加载周详情图表数据
         * @param modeType
         * @param calendar
         */
        void requestLoadWeekSportModeDetailChart(@IntRange(from = 0x01, to = 0x0B) int modeType, Calendar calendar);

        /**
         * 请求加载月图表数据
         *
         * @param calendar
         */
        void requestLoadMonthChart( Calendar calendar);

        /**
         * 请求加载月范围图表数据
         */
        void requestLoadMonthRangeChart( Calendar calendar);
    }
}
