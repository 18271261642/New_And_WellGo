package com.sn.app.db.data.config;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.sn.app.db.data.base.AppBean;
import com.sn.app.db.data.config.bean.HealthReminderConfig;
import com.sn.app.db.data.config.bean.RemindConfig;
import com.sn.app.db.data.config.bean.TimeCycleSwitch;
import com.sn.app.db.data.config.bean.UnitConfig;

/**
 * 作者:东芝(2018/2/3).
 * 功能:(对)设置(的)功能配置
 */
@DatabaseTable(tableName = DeviceConfigBean.TABLE_NAME)
public class DeviceConfigBean extends AppBean {

    public static final String TABLE_NAME = "DeviceConfigBean";
    public static final String COLUMN_HEART_RATE_AUTO_CHECK_CONFIG = "heartRateAutoCheckConfig";
    public static final String COLUMN_TEMPERATURE_AUTO_CHECK_CONFIG = "temperatureAutoCheckConfig";
    public static final String COLUMN_LIFT_WRIST_BRIGHT_SCREEN_CONFIG = "liftWristBrightScreenConfig";
    public static final String COLUMN_DO_NOT_DISTURB = "doNotDisturb";
    public static final String COLUMN_REMIND_CONFIG = "remindConfig";
    public static final String COLUMN_UNIT_CONFIG = "unitConfig";
    public static final String COLUMN_HEALTH_REMINDER_CONFIG = "healthReminderConfig";
    public static final String COLUMN_AUTO_SYNC_DEVICE_DATA = "autoSyncDeviceData";

    /**
     * 心率自动检测
     */
    @DatabaseField(columnName = COLUMN_HEART_RATE_AUTO_CHECK_CONFIG, dataType = DataType.SERIALIZABLE)
    private TimeCycleSwitch heartRateAutoCheckConfig = new TimeCycleSwitch("00:00", "23:59", false);

    /**
     * 体温自动检测
     */
    @DatabaseField(columnName = COLUMN_TEMPERATURE_AUTO_CHECK_CONFIG, dataType = DataType.SERIALIZABLE)
    private TimeCycleSwitch temperatureAutoCheckConfig = new TimeCycleSwitch("00:00", "23:59", false);

    /**
     * 抬腕亮屏
     */
    @DatabaseField(columnName = COLUMN_LIFT_WRIST_BRIGHT_SCREEN_CONFIG, dataType = DataType.SERIALIZABLE)
    private TimeCycleSwitch liftWristBrightScreenConfig = new TimeCycleSwitch("00:00", "23:59", false);


    /**
     * 免打扰开关
     */
    @DatabaseField(columnName = COLUMN_DO_NOT_DISTURB, dataType = DataType.SERIALIZABLE)
    private TimeCycleSwitch doNotDisturb = new TimeCycleSwitch("00:00","23:59",false);


    /**
     * 消息推送
     */
    @DatabaseField(columnName = COLUMN_REMIND_CONFIG, dataType = DataType.SERIALIZABLE)
    private RemindConfig remindConfig = new RemindConfig();


    /**
     * 单位设置
     */
    @DatabaseField(columnName = COLUMN_UNIT_CONFIG, dataType = DataType.SERIALIZABLE)
    private UnitConfig unitConfig = new UnitConfig();

    /**
     * 健康提醒设置
     */
    @DatabaseField(columnName = COLUMN_HEALTH_REMINDER_CONFIG, dataType = DataType.SERIALIZABLE)
    private HealthReminderConfig healthReminderConfig = new HealthReminderConfig();



    /**
     * 手环数据自动同步
     */
    @DatabaseField(columnName = COLUMN_AUTO_SYNC_DEVICE_DATA,dataType = DataType.BOOLEAN, defaultValue = "true" )
    private boolean autoSyncDeviceData = true;


    public boolean isAutoSyncDeviceData() {
        return autoSyncDeviceData;
    }

    public void setAutoSyncDeviceData(boolean autoSyncDeviceData) {
        this.autoSyncDeviceData = autoSyncDeviceData;
    }

    public HealthReminderConfig getHealthReminderConfig() {
        return healthReminderConfig;
    }

    public void setHealthReminderConfig(HealthReminderConfig healthReminderConfig) {
        this.healthReminderConfig = healthReminderConfig;
    }

    public TimeCycleSwitch getHeartRateAutoCheckConfig() {
        return heartRateAutoCheckConfig;
    }

    public void setHeartRateAutoCheckConfig(TimeCycleSwitch heartRateAutoCheckConfig) {
        this.heartRateAutoCheckConfig = heartRateAutoCheckConfig;
    }

    public TimeCycleSwitch getTemperatureAutoCheckConfig() {
        return temperatureAutoCheckConfig;
    }

    public void setTemperatureAutoCheckConfig(TimeCycleSwitch temperatureAutoCheckConfig) {
        this.temperatureAutoCheckConfig = temperatureAutoCheckConfig;
    }

    public TimeCycleSwitch getLiftWristBrightScreenConfig() {
        return liftWristBrightScreenConfig;
    }

    public void setLiftWristBrightScreenConfig(TimeCycleSwitch liftWristBrightScreenConfig) {
        this.liftWristBrightScreenConfig = liftWristBrightScreenConfig;
    }

    public TimeCycleSwitch getDoNotDisturb() {
        return doNotDisturb;
    }

    public void setDoNotDisturb(TimeCycleSwitch doNotDisturb) {
        this.doNotDisturb = doNotDisturb;
    }

    public RemindConfig getRemindConfig() {
        return remindConfig;
    }

    public void setRemindConfig(RemindConfig remindConfig) {
        this.remindConfig = remindConfig;

    }

    public UnitConfig getUnitConfig() {
        return unitConfig;
    }

    public void setUnitConfig(UnitConfig unitConfig) {
        this.unitConfig = unitConfig;
    }
}
