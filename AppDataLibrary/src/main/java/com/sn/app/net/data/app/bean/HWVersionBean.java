package com.sn.app.net.data.app.bean;

/**
 * 作者:东芝(2017/9/21).
 * 描述:
 */
public class HWVersionBean {

    /**
     * ret : 0
     * message :
     * timestamp : 1505937802
     * data : {"type":"2","download_url":"https://api.core.iwear88.com/uploads/--j0Qt4sfpsu2YVy6BC548VB_6YQllw2.img","version":10,"update_time":"2017-09-16 14:40:29"}
     */

    private int ret;
    private String message;
    private int timestamp;
    private DataBean data;

    public void setRet(int ret) {
        this.ret = ret;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public int getRet() {
        return ret;
    }

    public String getMessage() {
        return message;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public DataBean getData() {
        return data;
    }

    public static class DataBean {

        private int id;
        private String type;
        private int version;
        private String download_url;
        private String update_time;
        private String describe;
        private int head;
        private int customid;
        private String device_name;
        private int apptype;
        private int function;
        private String chip;
        private int adv_id;
        private int upgradeid;
        private String extra;
        public void setId(int id) {
            this.id = id;
        }
        public int getId() {
            return id;
        }

        public void setType(String type) {
            this.type = type;
        }
        public String getType() {
            return type;
        }

        public void setVersion(int version) {
            this.version = version;
        }
        public int getVersion() {
            return version;
        }

        public void setDownload_url(String download_url) {
            this.download_url = download_url;
        }
        public String getDownload_url() {
            return download_url;
        }


        public void setDescribe(String describe) {
            this.describe = describe;
        }
        public String getDescribe() {
            return describe;
        }

        public void setHead(int head) {
            this.head = head;
        }
        public int getHead() {
            return head;
        }

        public void setCustomid(int customid) {
            this.customid = customid;
        }
        public int getCustomid() {
            return customid;
        }

        public void setDevice_name(String device_name) {
            this.device_name = device_name;
        }
        public String getDevice_name() {
            return device_name;
        }

        public void setApptype(int apptype) {
            this.apptype = apptype;
        }
        public int getApptype() {
            return apptype;
        }

        public void setFunction(int function) {
            this.function = function;
        }
        public int getFunction() {
            return function;
        }

        public void setChip(String chip) {
            this.chip = chip;
        }
        public String getChip() {
            return chip;
        }

        public void setAdv_id(int adv_id) {
            this.adv_id = adv_id;
        }
        public int getAdv_id() {
            return adv_id;
        }

        public void setUpgradeid(int upgradeid) {
            this.upgradeid = upgradeid;
        }
        public int getUpgradeid() {
            return upgradeid;
        }

        public void setExtra(String extra) {
            this.extra = extra;
        }
        public String getExtra() {
            return extra;
        }
    }
}
