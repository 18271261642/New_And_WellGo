package com.sn.app.net.data.app.bean;

import java.util.List;

/**
 * 作者:东芝(2017/8/7).
 * 描述:app更新
 */
public class AppVersionNetBean {

    /**
     * ret : 0
     * message :
     * timestamp : 1515550069
     * data : [{"version_code":1200,"version_name":"1.2.0","description":"全新的界面，全新的功能体验","download_url":"http://backend.core.iwear88.com/uploads/nmqllWzU8fqUD8v0B-ZGV1qjkmWXl_rf.apk","app_name":"getfit3.0"},{"version_code":100,"version_name":"1.0.0","description":"全新的界面，全新的功能体验","download_url":"http://backend.core.iwear88.com/uploads/nmqllWzU8fqUD8v0B-ZGV1qjkmWXl_rf.apk","app_name":"wellgo"}]
     */

    private int ret;
    private String message;
    private int timestamp;
    private List<DataBean> data;

    public int getRet() {
        return ret;
    }

    public void setRet(int ret) {
        this.ret = ret;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * version_code : 1200
         * version_name : 1.2.0
         * description : 全新的界面，全新的功能体验
         * download_url : http://backend.core.iwear88.com/uploads/nmqllWzU8fqUD8v0B-ZGV1qjkmWXl_rf.apk
         * app_name : getfit3.0
         */

        private int version_code;
        private int is_necessary;
        private String version_name;
        private String description;
        private String download_url;
        private String app_name;


        public boolean isNecessary() {
            return is_necessary==1;
        }

        public int getVersion_code() {
            return version_code;
        }

        public void setVersion_code(int version_code) {
            this.version_code = version_code;
        }

        public String getVersion_name() {
            return version_name;
        }

        public void setVersion_name(String version_name) {
            this.version_name = version_name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getDownload_url() {
            return download_url;
        }

        public void setDownload_url(String download_url) {
            this.download_url = download_url;
        }

        public String getApp_name() {
            return app_name;
        }

        public void setApp_name(String app_name) {
            this.app_name = app_name;
        }
    }
}
