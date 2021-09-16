package com.sn.blesdk.net.bean;

import com.google.gson.reflect.TypeToken;
import com.sn.net.utils.JsonUtil;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * 作者:东芝(2017/8/4).
 * 描述:睡眠
 */
public class SleepNetBean {

    /**
     * ret : 0
     * message :
     * timestamp : 1501807740
     * data : [{"user_id":7,"mac":"C9:3C:EA:BA:A8:5B","date":"2017-08-04","duration":170,"deep":140,"light":30,"sober":0,"data":"[{\"endTime\":\"2016-12-20 16:10:00\",\"startTime\":\"2016-12-20 19:00:00\",\"sleepData\":[\"16409\",\"4\",\"16399\",\"3\",\"16423\",\"9\",\"16409\",\"3\",\"16396\",\"1\",\"16408\",\"10\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\"]}]"}]
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
         * user_id : 7
         * mac : C9:3C:EA:BA:A8:5B
         * date : 2017-08-04
         * duration : 170
         * deep : 140
         * light : 30
         * sober : 0
         * data : [{"endTime":"2016-12-20 16:10:00","startTime":"2016-12-20 19:00:00","sleepData":["16409","4","16399","3","16423","9","16409","3","16396","1","16408","10","0","0","0","0","0","0","0","0","0","0","0","0"]}]
         */

        private int user_id;
        private String mac;
        private String date;
        private int duration;
        private int deep;
        private int light;
        private int sober;
        private String data;
        private String month;

        public int getUser_id() {
            return user_id;
        }

        public void setUser_id(int user_id) {
            this.user_id = user_id;
        }

        public String getMac() {
            return mac;
        }

        public void setMac(String mac) {
            this.mac = mac;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public int getDuration() {
            return duration;
        }

        public void setDuration(int duration) {
            this.duration = duration;
        }

        public int getDeep() {
            return deep;
        }

        public void setDeep(int deep) {
            this.deep = deep;
        }

        public int getLight() {
            return light;
        }

        public void setLight(int light) {
            this.light = light;
        }

        public int getSober() {
            return sober;
        }

        public void setSober(int sober) {
            this.sober = sober;
        }

        public void setData(String data) {
            this.data = data;
        }

        public List<DataDataBean> getData() {
            Type type = new TypeToken<ArrayList<DataDataBean>>() {
            }.getType();
            return JsonUtil.toListBean(data, type);
        }



        public String getMonth() {
            return month;
        }

        public void setMonth(String month) {
            this.month = month;
        }

        public static class DataDataBean {

            /**
             * endTime : 2016-12-20 16:10:00
             * startTime : 2016-12-20 19:00:00
             * sleepData : ["16409","4","16399","3","16423","9","16409","3","16396","1","16408","10","0","0","0","0","0","0","0","0","0","0","0","0"]
             */

            private String endTime;
            private String startTime;
            private List<String> sleepData;

            public String getEndTime() {
                return endTime;
            }

            public void setEndTime(String endTime) {
                this.endTime = endTime;
            }

            public String getStartTime() {
                return startTime;
            }

            public void setStartTime(String startTime) {
                this.startTime = startTime;
            }

            public List<String> getSleepData() {
                return sleepData;
            }

            public void setSleepData(List<String> sleepData) {
                this.sleepData = sleepData;
            }
        }

    }
}
