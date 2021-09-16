package com.truescend.gofit.pagers.device.health;

import com.sn.app.db.data.config.bean.HealthReminderConfig;

/**
 * 功能：健康提醒页面接口
 * Author:Created by 泽鑫 on 2017/11/24 11:08.
 */

public class IHealthReminderContract {


    interface IView{
        /**
         * 作者:东芝(2018/2/5).
         * 功能:刷新页面对应的Item数据
         */
        void onUpdateReminderItemData(boolean isSedentaryOn, boolean isDrinkOn);
        /*
         * 作者:东芝(2018/2/5).
         * 功能:x显示dialog
         *
         * 于2018/03/16日修改ui界面，弃用
         */
//        void onShowReminderDialog(HealthReminderConfig.HealthReminder healthReminder, @HealthReminderConfig.HealthReminder.Type int type);

        /**
         * 显示久坐的信息
         * Author:Created by 泽鑫 on 2018/03/05 10:15.
         */
        void onUpdateRemindSedentaryData(int startHour, int startMinute, int endHour, int endMinute, int intervalTime);
        /**
         * 显示喝水的信息
         * Author:Created by 泽鑫 on 2018/03/05 10:15.
         */
        void onUpdateRemindDrinkData(int startHour, int startMinute, int endHour, int endMinute, int intervalTime);
    }

    interface IPresenter{
        /**
         * 作者:东芝(2018/2/5).
         * 功能:请求修改并发送设置到设备
         * @param isOn 开启/关闭
         * @param type 提醒类型 已限制@Type规范, 请按规范传值
         */
        void requestChangeReminderData(boolean isOn, @HealthReminderConfig.HealthReminder.Type int type);

        /*
         * 于2018/03/16日修改ui界面，弃用
         *
         */
//        void requestChangeReminderData(HealthReminderConfig.HealthReminder healthReminder, @HealthReminderConfig.HealthReminder.Type int type);
        /**
         * 作者:东芝(2018/2/5).
         * 功能:请求加载item数据
         */
        void requestLoadReminderItemData();
        /*
         * 作者:东芝(2018/2/5).
         * 功能:请求弹出设置弹窗
         * @param type 提醒类型 已限制@Type规范, 请按规范传值
         *
         * 于2018/03/16日修改ui界面，弃用
         */
//        void requestShowReminderDialog(@HealthReminderConfig.HealthReminder.Type int type);
    }
}
