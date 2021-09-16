package com.truescend.gofit.pagers.device.push;

import com.sn.app.db.data.config.DeviceConfigBean;
import com.truescend.gofit.pagers.device.bean.ItemApps;

import java.util.List;

/**
 * 作者:东芝(2018/2/5).
 * 功能:app推送
 */
public class IPushContract {

    interface IView{
        /**
         * 配置更新
         * @param bean
         */
        void onUpdateDeviceConfig(DeviceConfigBean bean);
        void onUpdateAllApps(List<ItemApps> itemApps);
    }

    interface IPresenter{
        void requestDeviceConfig();
        void requestLoadAllApps();
        /**
         * 请求更新DeviceConfigBean
         * @param bean
         */
        void requestChangeDeviceConfigData(DeviceConfigBean bean);
    }
}
