package com.truescend.gofit.pagers.home.sleep;

import com.truescend.gofit.views.SleepChartView;

import java.util.Calendar;
import java.util.List;
/**
 * 作者:东芝(2017/12/01).
 * 功能:睡眠数据查询解析显示
 */
public class ISleepContract {

    interface IView {

        /**
         * 更新睡眠数据
         * @param data
         *
         */
        void onUpdateSleepChartData(List<SleepChartView.SleepItem> data);

        /**
         *  睡眠 Item 数据
         * @param totalTime
         * @param validTime
         * @param quality
         * @param deepPercent
         * @param deepPercentText
         * @param lightPercent
         * @param lightPercentText
         * @param soberPercent
         * @param soberPercentText
         * @param deepTotal
         * @param lightTotal
         * @param soberTotal
         */
        void onUpdateSleepItemData(CharSequence totalTime, CharSequence validTime, String quality, int deepPercent, String deepPercentText, int lightPercent, String lightPercentText, int soberPercent, String soberPercentText, String deepTotal, String lightTotal, String soberTotal);
    }

    interface IPresenter {
        /**
         * 获取睡眠数据
         * @param calendar
         */
        void requestLoadSleepChart(Calendar calendar);
    }
}
