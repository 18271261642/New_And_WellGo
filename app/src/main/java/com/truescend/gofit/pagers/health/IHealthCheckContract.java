package com.truescend.gofit.pagers.health;

/**
 * 作者:东芝(2018/1/31).
 * 功能:我的体检
 */

public class IHealthCheckContract {

    interface IView {
        /**
         * 更新心率体检值
         * @param heartRate
         */
        void onUpdateHealthCheckHeartRate(String heartRate);
        /**
         * 更新血氧体检值
         * @param bloodOxygen
         */
        void onUpdateHealthCheckBloodOxygen(String bloodOxygen);
        /**
         * 更新血压体检值
         * @param bloodPressure
         */
        void onUpdateHealthCheckBloodPressure(String bloodPressure);

        /**
         * 开始体检
         */
        void onHealthCheckStarted();
        /**
         * 停止体检
         * @param hasError
         */
        void onHealthCheckStopped(boolean hasError);



    }

    interface IPresenter {
        /**
         * 体检开始
         * @param type 体检类型
         * @param on
         * @param isTimer
         */
        void requestStartHealthCheck(int type, boolean on, boolean isTimer);

        /**
         * 取得上次的值
         * @param type
         */
        void requestGetHealthCheckLastValue(int type);


    }
}
