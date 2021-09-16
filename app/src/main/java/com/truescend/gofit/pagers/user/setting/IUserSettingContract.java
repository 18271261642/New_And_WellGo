package com.truescend.gofit.pagers.user.setting;

/**
 * 作者:东芝(2018/2/23).
 * 功能:用户信息设置
 */

public class IUserSettingContract {

    interface IView{
        void onUpdateUserDataSuccess();
        void onUpdateUserDataFailed(String msg);
    }

    interface IPresenter{
        void requestUpdateUserData(String mUserLocalImagePath, String mUserNickname, String mUserSign, int mUserGender, String mUserBirthDate, float mUserHeight, float mUserWeight, String lastWeightTime, int mUserTarget);
    }
}
