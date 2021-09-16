package com.truescend.gofit.pagers.ranking;


import android.graphics.Bitmap;

import com.sn.app.net.data.app.bean.FriendListBean;

import java.util.Calendar;
import java.util.List;

/**
 * 作者:东芝(2019/01/11).
 * 功能:世界排行
 */
public class IRankingContract {

    interface IView {
        void onLoading();

        void onLoadingDone();

        void onShowTips(String str);

        void onLoadFriendRanking(List<FriendListBean.DataBean> data, String headUrl, int selfLevelIndex, int stepTotal, float distanceTotal, String distanceUnit);

        void onLoadSportRanking(String[][] rankingLabel, List<Integer> data, int max, Bitmap headIcon, int headIconIndex, String moreThanPercentText);

        void onLoadSleepDurationRanking(String[][] rankingLabel, List<Integer> data, int max, Bitmap headIcon, int headIconIndex, String moreThanPercentText);

        void onLoadSleepTimeRanking(String[][] rankingLabel, List<Integer> data, int max, Bitmap headIcon, int headIconIndex, String moreThanPercentText);

        void onLoadSleepDetailsChartData(String dateFromAndTo, int curDeepTime, int curLightTime, int curSoberTime, int avgDeepTime, int avgLightTime, int avgSoberTime);


        void onFinishRefresh();
    }

    interface IPresenter {
        void loadFriendRanking();

        void loadSportRanking();

        void loadSleepDurationRanking();

        void loadSleepTimeRanking();


        /**
         * 请求加载周图表数据
         *
         * @param calendar
         */
        void loadWeekSleepDetailsChart(Calendar calendar);

        /**
         * 请求加载月图表数据
         *
         * @param calendar
         */
        void loadMonthSleepDetailsChart(Calendar calendar);
    }
}
