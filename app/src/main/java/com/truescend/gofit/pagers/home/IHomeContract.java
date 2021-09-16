package com.truescend.gofit.pagers.home;

import androidx.annotation.DrawableRes;

import java.util.Calendar;
import java.util.List;

/**
 * 作者:东芝(2017/11/16).
 * 功能:主页
 */

public class IHomeContract {

    interface IView {
        /**
         * 更新步数数据
         * @param targetStepValue
         * @param currentStepValue
         * @param distanceTotal
         * @param caloriesTotal
         * @param distanceUnit
         * @param data
         */
        void onUpdateStepChartData(int targetStepValue, int currentStepValue, float distanceTotal, float caloriesTotal, String distanceUnit, List<Integer> data);

        /**
         * 睡眠 项 数据
         */
        void onUpdateSleepItemData(CharSequence title, CharSequence content);


        /**
         * 心率 项 数据
         * @param title
         *
         */
        void onUpdateHeartRateItemData(CharSequence title, CharSequence content);

        /**
         * 血氧 项 数据
         * @param content
         */
        void onUpdateBloodOxygenItemData(CharSequence title, CharSequence content);

        /**
         * 血压 项 数据
         */
        void onUpdateBloodPressureItemData(CharSequence title, CharSequence content);

        /**
         * 运动模式
         * @param icon
         * @param subTitle1
         * @param subTitle2
         */
        void onUpdateSportModeItemData(@DrawableRes int icon, CharSequence subTitle1, CharSequence subTitle2, CharSequence subTitle3);

        /**
         * 进餐减肥统计
         * @param content
         */
        void onUpdateDietStatisticsItemData(CharSequence content);

        /**
         * 进餐详情
         * @param content
         */
        void onUpdateDietMealDetailsItemData(CharSequence content);

        /**
         * 设备数据同步完成
         */
        void onDeviceDataSyncSuccess();



        /**
         * 更新天气 信息
         * @param weatherType
         * @param weatherTemperatureRange
         * @param weatherQuality
         */
        void onUpdateWeatherData(int weatherType,String weatherTemperatureRange,String weatherQuality);
        void onUpdateDietPlanThinBodyEnableStatus(boolean enable);
    }

    interface IPresenter {

        void requestLoadStepChart(Calendar calendar);
        void requestLoadSleepItemData(Calendar calendar);
        void requestLoadSportModeItemData(Calendar calendar);
        void requestLoadHeartRateItemData(Calendar calendar);
        void requestLoadBloodOxygenItemData(Calendar calendar);
        void requestLoadBloodPressureItemData(Calendar calendar);
        void requestLoadDietStatisticsItemData(Calendar calendar);
        void requestLoadNetworkDietStatisticsItemData(Calendar calendar);
        void requestLoadDietPlanThinBodyEnableStatus();

        /**
         * 请求开始手动同步设备数据
         */
        void requestStartDeviceDataSync();

        void requestWeatherData();
    }
}
