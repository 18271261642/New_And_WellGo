package com.truescend.gofit.pagers.user;


/**
 * 作者:东芝(2018/3/20).
 * 功能:用户信息
 */

public class IUserContract {

    public interface IView {
        /**
         * 某天最佳记录(个人最佳记录)
         * @param date
         * @param stepTotal
         */
        void onUpdateUserBestDays(String date,String stepTotal);

        /**
         * 最佳连续达标天数和日期范围
         * @param dateRange
         * @param days
         */
        void onUpdateUserBestContinuesDays(String dateRange,String days);

        /**
         * 最佳星期和日期范围
         * @param dateRange
         * @param days
         */
        void onUpdateUserBestWeeks(String dateRange,String days);

        /**
         * 最佳月份和日期范围
         * @param dateRange
         * @param days
         */
        void onUpdateUserBestMonths(String dateRange,String days);
    }

    interface IPresenter {
        /**
         * 请求获取用户最佳数据统计
         */
        void requestUserBestStatistical();
   /*     *//**
         * 请求获取用户基本信息
         *//*
        void requestUserData();*/
    }
}
