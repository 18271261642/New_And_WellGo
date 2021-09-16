package com.truescend.gofit.pagers.device;

/**
 * Created by Admin
 * Date 2021/9/7
 */
public class IDeviceManagerContract {

    interface IView{
        void selectPosition(int position);
    }

    interface IPresenter{
        void getSelectPosition();
    }
}
