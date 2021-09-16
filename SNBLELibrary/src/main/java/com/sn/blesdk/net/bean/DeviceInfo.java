package com.sn.blesdk.net.bean;

import android.text.TextUtils;

import com.google.gson.reflect.TypeToken;
import com.sn.net.utils.JsonUtil;

import java.io.Serializable;
import java.util.List;

/**
 * 作者:东芝(2018/1/8).
 * 功能:
 */

public class DeviceInfo implements Serializable {
    /**
     * 心率
     */
    private boolean isSupportHeartRate = false;
    /**
     * 气压
     */
    private boolean isSupportAirPressure = false;
    /**
     * 体温
     */
    private boolean isSupportTemperature = false;

    /**
     * 血氧
     */
    private boolean isSupportBloodOxygen = false;
    /**
     * 血压
     */
    private boolean isSupportBloodPressure = false;

    /**
     * 消息推送
     */
    private boolean isSupportMessagePush = false;

    /**
     * 是否支持手环自己设置
     */
    private boolean isSupportBandSelfSetting = false;
    /**
     * 心电
     */
    private boolean isSupportECG = false;

    public boolean isSupportHeartRate() {
        return isSupportHeartRate;
    }

    public void setSupportHeartRate(boolean supportHeartRate) {
        isSupportHeartRate = supportHeartRate;
    }

    public boolean isSupportAirPressure() {
        return isSupportAirPressure;
    }

    public void setSupportAirPressure(boolean supportAirPressure) {
        isSupportAirPressure = supportAirPressure;
    }

    public boolean isSupportTemperature() {
        return isSupportTemperature;
    }

    public void setSupportTemperature(boolean supportTemperature) {
        isSupportTemperature = supportTemperature;
    }

    public boolean isSupportBloodOxygen() {
        return isSupportBloodOxygen;
    }

    public void setSupportBloodOxygen(boolean supportBloodOxygen) {
        isSupportBloodOxygen = supportBloodOxygen;
    }

    public boolean isSupportBloodPressure() {
        return isSupportBloodPressure;
    }

    public void setSupportBloodPressure(boolean supportBloodPressure) {
        isSupportBloodPressure = supportBloodPressure;
    }

    public boolean isSupportMessagePush() {
        return isSupportMessagePush;
    }

    public boolean isSupportWallpaper() {
        ExtraInfo extra = getExtra();
        if (extra == null) return false;
        return extra.isSupportWallpaper();
    }

    public boolean isSupportSportMode() {
        ExtraInfo extra = getExtra();
        if (extra == null) return false;
        return extra.isSupportSportMode();
    }

    public boolean isSupportTmallGenie() {
        ExtraInfo extra = getExtra();
        if (extra == null) return false;
        return extra.isSupportTmallGenie();
    }

    public boolean isSupportDiet() {
        ExtraInfo extra = getExtra();
        if (extra == null) return false;
        return extra.isSupportDiet();
    }

    public int getScreenType() {
        ExtraInfo extra = getExtra();
        if (extra == null) return 0;
        return extra.getScreenType();
    }

    public void setSupportMessagePush(boolean supportMessagePush) {
        isSupportMessagePush = supportMessagePush;
    }

    public boolean isSupportECG() {
        return isSupportECG;
    }

    public void setSupportECG(boolean supportECG) {
        isSupportECG = supportECG;
    }


    public boolean isSupportBandSelfSetting() {
        switch (getAdv_id()) {
            case 3089://IT110
                return true;
            case 0x071D://IT109
            case 0x2E1D://IT109
            case 0x5C1D://IT109
                return true;

        }
        return isSupportBandSelfSetting;
    }

    public boolean isDialog() {
        return getChip().toLowerCase().startsWith("da") || getChip().startsWith("dialog");
    }

    public boolean isnRF() {
        return getChip().toLowerCase().startsWith("nrf") || getChip().toLowerCase().contains("nordic");
    }

    public boolean isSYD8801() {
        return getChip().toLowerCase().startsWith("syd") || getChip().toLowerCase().contains("syd8801");
    }

    public boolean isTi() {
        return getChip().toLowerCase().startsWith("ti");
    }

    public boolean isPhy() {
        return getChip().toLowerCase().startsWith("phyxw");
    }

    /*
        public boolean isDFU() {
            String name = getDevice_name();
            return isDFU || name.toLowerCase().contains("dfu") || name.toLowerCase().contains("ota");
        }

        public void setDFU(boolean isDFU) {
            this.isDFU = isDFU;
        }

        private boolean isDFU = false;*/
    private int id;
    private String type;
    private int version;
    private String download_url;
    private String update_time;
    private String describe;
    private int head;
    private int customid;
    private int adv_id;
    private int upgradeid;
    private String device_name;
    private int apptype;
    private int function;
    private String chip;
    private String extra;

    public int getUpgradeid() {
        return upgradeid;
    }

    public void setUpgradeid(int upgradeid) {
        this.upgradeid = upgradeid;
    }

    public ExtraInfo getExtra() {
        return JsonUtil.toBean(extra, ExtraInfo.class);
    }


    public void setAdv_id(int adv_id) {
        this.adv_id = adv_id;
    }

    public int getAdv_id() {
        return adv_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getDownload_url() {
        return download_url;
    }

    public void setDownload_url(String download_url) {
        this.download_url = download_url;
    }

    public String getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public int getHead() {
        return head;
    }

    public void setHead(int head) {
        this.head = head;
    }

    public int getCustomid() {
        return customid;
    }

    public void setCustomid(int customid) {
        this.customid = customid;
    }

    public String getDevice_name() {
        return device_name;
    }

    public void setDevice_name(String device_name) {
        this.device_name = device_name;
    }

    public int getApptype() {
        return apptype;
    }

    public void setApptype(int apptype)   {
        this.apptype = apptype;
    }

    public int getFunction() {
        return function;
    }

    public void setFunction(int function) {
        this.function = function;
    }

    public String getChip() {
        return TextUtils.isEmpty(chip) ? "" : chip;
    }

    public void setChip(String chip) {
        this.chip = chip;
    }


    public static class ExtraInfo implements Serializable {
        int anti_drop_state;
        @Deprecated
        int default_language;
        String language;
        int wallpaper;
        int sport_mode_data;
        int time_format = 1;
        int is_support_food = 1;
        int is_support_tianmao = 0;
        int screen_type = 0;//0:普通240x240非异形屏幕,1:118系列的异形屏

        public int getScreenType() {
            return screen_type;
        }

        public boolean isTimeFormatViewEnable() {
            return time_format == 1;
        }

        public boolean isRemindLostEnable() {
            return anti_drop_state == 1;
        }

        /**
         * 支持运动模式
         *
         * @return
         */
        public boolean isSupportSportMode() {
            return sport_mode_data == 1;
        }

        /**
         * 支持天猫精灵
         *
         * @return
         */
        public boolean isSupportTmallGenie() {
            return is_support_tianmao == 1;
        }

        /**
         * 支持食谱
         *
         * @return
         */
        public boolean isSupportDiet() {
            return is_support_food == 1;
        }

        /**
         * 支持壁纸
         *
         * @return
         */
        public boolean isSupportWallpaper() {
            return wallpaper == 1;
        }

        /**
         * 用于区别以前的手环的默认语言是中文和英文, 后来的新手环支持其他语言 所以通过这个字段区分
         *
         * @return
         */
        @Deprecated
        public int getDefault_language() {
            return default_language;
        }

        public List<Integer> getLanguage() {
            return JsonUtil.toListBean(language, new TypeToken<List<Integer>>() {
            }.getType());
        }


    }
}