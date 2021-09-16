package com.sn.app.db.data.config;

import com.sn.app.db.data.base.AppDao;
import com.sn.app.db.data.config.bean.HealthReminderConfig;
import com.sn.app.db.data.config.bean.RemindConfig;
import com.sn.app.db.data.config.bean.TimeCycleSwitch;
import com.sn.app.db.data.config.bean.UnitConfig;

/**
 * 作者:东芝(2018/02/03).
 * 功能:手环配置数据存储
 */
public class DeviceConfigDao extends AppDao<DeviceConfigBean, Integer> {


    /**
     * 取得消息推送设置
     * @param user_id
     * @return
     */
    public RemindConfig queryRemindConfig(int user_id){
        return (RemindConfig) super.queryColumnObject(DeviceConfigBean.COLUMN_REMIND_CONFIG, user_id);
    }
    /**
     * 取得健康提醒设置
     *
     * @param user_id
     * @return
     */
    public HealthReminderConfig queryHealthReminderConfig(int user_id) {
        return (HealthReminderConfig) super.queryColumnObject(DeviceConfigBean.COLUMN_HEALTH_REMINDER_CONFIG, user_id);
    }

    /**
     * 更新健康提醒设置
     *
     * @param user_id
     * @return
     */
    public boolean updateHealthReminderConfig(int user_id, HealthReminderConfig config) {
        return super.updateColumnObject(DeviceConfigBean.COLUMN_HEALTH_REMINDER_CONFIG, user_id, config);
    }

    /**
     * 取得心率自动检测设置
     *
     * @param user_id
     * @return
     */
    public TimeCycleSwitch queryHeartRateAutoCheckConfig(int user_id) {
        return (TimeCycleSwitch) super.queryColumnObject(DeviceConfigBean.COLUMN_HEART_RATE_AUTO_CHECK_CONFIG, user_id);
    }

    /**
     * 更新心率自动检测设置
     *
     * @param user_id
     * @return
     */
    public boolean updateHeartRateAutoCheckConfig(int user_id, TimeCycleSwitch config) {
        return super.updateColumnObject(DeviceConfigBean.COLUMN_HEART_RATE_AUTO_CHECK_CONFIG, user_id, config);
    }

    /**
     * 取得抬腕亮屏设置
     *
     * @param user_id
     * @return
     */
    public TimeCycleSwitch queryLiftWristBrightScreenConfig(int user_id) {
        return (TimeCycleSwitch) super.queryColumnObject(DeviceConfigBean.COLUMN_LIFT_WRIST_BRIGHT_SCREEN_CONFIG, user_id);
    }

    /**
     * 更新抬腕亮屏设置
     *
     * @param user_id
     * @return
     */
    public boolean updateLiftWristBrightScreenConfig(int user_id, TimeCycleSwitch config) {
        return super.updateColumnObject(DeviceConfigBean.COLUMN_LIFT_WRIST_BRIGHT_SCREEN_CONFIG, user_id, config);
    }

    /**
     * 取得免打扰开关设置
     *
     * @param user_id
     * @return
     */
    public TimeCycleSwitch queryDoNotDisturbConfig(int user_id) {
        return (TimeCycleSwitch) super.queryColumnObject(DeviceConfigBean.COLUMN_DO_NOT_DISTURB, user_id);
    }

    /**
     * 更新免打扰开关设置
     *
     * @param user_id
     * @return
     */
    public boolean updateDoNotDisturbConfig(int user_id, TimeCycleSwitch config) {
        return super.updateColumnObject(DeviceConfigBean.COLUMN_DO_NOT_DISTURB, user_id, config);
    }

    /**
     * 取得单位设置
     *
     * @param user_id
     * @return
     */
    public UnitConfig queryUnitConfig(int user_id) {
        return (UnitConfig) super.queryColumnObject(DeviceConfigBean.COLUMN_UNIT_CONFIG, user_id);
    }

    /**
     * 更新单位设置
     *
     * @param user_id
     * @return
     */
    public boolean updateUnitConfig(int user_id, UnitConfig config) {
        return super.updateColumnObject(DeviceConfigBean.COLUMN_UNIT_CONFIG, user_id, config);
    }
}
