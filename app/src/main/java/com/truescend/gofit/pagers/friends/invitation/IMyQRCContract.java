package com.truescend.gofit.pagers.friends.invitation;


/**
 * 作者:东芝(2018/08/08).
 * 功能:添加好友
 */
public class IMyQRCContract {

    interface IView {
        void onSaveScreenshots(String path);
    }

    interface IPresenter
    {
        void saveScreenshots();
    }
}
