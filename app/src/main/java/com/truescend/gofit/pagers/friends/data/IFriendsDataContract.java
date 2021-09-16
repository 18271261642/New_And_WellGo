package com.truescend.gofit.pagers.friends.data;


import com.sn.app.net.data.app.bean.FriendInfoBean;
import com.truescend.gofit.pagers.friends.data.item.SleepDataPickerItem;
import com.truescend.gofit.pagers.friends.data.item.SportDataPickerItem;
import com.truescend.gofit.views.BloodPressureChartView;

import java.util.List;

/**
 * 作者:东芝(2018/08/09).
 * 功能:好友数据
 */
public class IFriendsDataContract {

    interface IView {
        void onLoading();

        void onLoadingDone();

        void onShowTips(String str);

        void onSportDateData(List<SportDataPickerItem.SportDateItem> items);

        void onSleepDateData(List<SleepDataPickerItem.SleepDateItem> items);

        void onHeartRateDateData(List<Integer> items, int heart_max, int heart_ave, int heart_min);

        void onBloodPressureDateData(List<BloodPressureChartView.BloodPressureItem> items, int diastolic_avg, int systolic_avg);

        void onBloodOxygenDateData(List<Integer> items, int oxygen_avg);

        void onFriendsInfoResults(FriendInfoBean.DataBean data);

        void onFriendsThumbAction(int type);

        void onUpdateUserBestDays(String date, String stepTotal);

        void onUpdateUserBestContinuesDays(String dateRange, String days);

        void onUpdateUserBestWeeks(String dateRange, String days);

        void onUpdateUserBestMonths(String dateRange, String days);
    }

    interface IPresenter {
        void getFriendsInfoResults(int user_id);

        /**
         * 点赞和鼓励
         *
         * @param user_id
         * @param type    1：点赞，2：鼓励
         */
        void setFriendsThumbAction(int user_id, int type);
    }
}
