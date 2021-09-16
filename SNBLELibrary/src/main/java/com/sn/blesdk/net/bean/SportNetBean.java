package com.sn.blesdk.net.bean;

import com.sn.net.utils.JsonUtil;

import org.json.JSONArray;

import java.util.List;

/**
 * 作者:东芝(2017/8/2).
 * 功能:网络部分运动数据
 */
public class SportNetBean {


    /**
     * ret : 0
     * message :
     * timestamp : 1501615193
     * data : [{"user_id":7,"mac":"F3:C8:39:B4:F2:69","date":"2017-08-02","duration":3,"step":366,"distance":270,"calory":21,"data":"{\"cal\":[\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"12\",\"6\",\"1\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\"],\"step\":[\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"225\",\"108\",\"33\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\"],\"distance\":[\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"166\",\"82\",\"21\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\"]}"}]
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
         * mac : F3:C8:39:B4:F2:69
         * date : 2017-08-02
         * month : 2017-08
         * duration : 3
         * step : 366
         * distance : 270
         * calory : 21
         * data : {"cal":["0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","12","6","1","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0"],"step":["0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","225","108","33","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0"],"distance":["0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","166","82","21","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0","0"]}
         */

        private int user_id;
        private String mac;
        private String date;
        private String month;
        private int duration;
        private int step;
        private int distance;
        private int calory;
        private String data;
        private int target_step;

        public int getTarget_step() {
            return target_step;
        }

        public void setTarget_step(int target_step) {
            this.target_step = target_step;
        }

        public String getMonth() {
            return month;
        }

        public void setMonth(String month) {
            this.month = month;
        }

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

        public int getStep() {
            return step;
        }

        public void setStep(int step) {
            this.step = step;
        }

        public int getDistance() {
            return distance;
        }

        public void setDistance(int distance) {
            this.distance = distance;
        }

        public int getCalory() {
            return calory;
        }

        public void setCalory(int calory) {
            this.calory = calory;
        }

        public DetailedBean getData() {
                return JsonUtil.toBean(data,DetailedBean.class);
        }

        public void setData(String data) {
            this.data = data;
        }

        public static class DetailedBean{

            private List<Integer> cal;
            private List<Integer> step;
            private List<Integer> distance;

            public List<Integer> getCal() {
                return cal;
            }
            public String getCalJSONArray() {
                return new JSONArray(cal).toString();
            }

            public void setCal(List<Integer> cal) {
                this.cal = cal;
            }

            public List<Integer> getStep() {
                return step;
            }
            public String getStepJSONArray() {
                return new JSONArray(step).toString();
            }
            public void setStep(List<Integer> step) {
                this.step = step;
            }

            public List<Integer> getDistance() {
                return distance;
            }
            public String getDistanceJSONArray() {
                return new JSONArray(distance).toString();
            }
            public void setDistance(List<Integer> distance) {
                this.distance = distance;
            }

            /**
             * 制造一个假的数组,但总数是对的  的数组
             * @param total
             * @return
             */
            public static String getZeroJSONArray(int total) {
                JSONArray jsonArray = new JSONArray();
                for (int i = 0; i < 48; i++) {
                    if (i == 0) {
                        jsonArray.put(total);
                        continue;
                    }
                    jsonArray.put(0);
                }
                return jsonArray.toString();
            }
        }
    }
}
