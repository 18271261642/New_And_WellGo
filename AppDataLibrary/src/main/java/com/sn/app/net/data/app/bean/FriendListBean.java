package com.sn.app.net.data.app.bean;

import java.util.List;

/**
 * 作者:东芝(2018/8/10).
 * 功能:朋友列表
 */

public class FriendListBean {

    /**
     * ret : 0
     * message :
     * timestamp : 1534878170
     * data : [{"friend_user_id":"52","remark":"我的手机号","create_time":"2018-08-15 03:44:31","friend":{"id":"52","nickname":"东芝的账号231","sign":"","portrait":"http://api.core.iwear88.com/uploads/E7bVq2iX8GJErruJ.jpg","gender":"0","sport":{"duration":0,"step":0,"distance":0,"calory":0,"today_date":"2018-08-22"}}},{"friend_user_id":"68394","remark":"小号1","create_time":"2018-08-10 02:30:00","friend":{"id":"68394","nickname":"这是无验证码注册的号码","sign":"","portrait":"http://api.core.iwear88.com/uploads/nfuVQDD9bmxynCVy.jpg","gender":"1","sport":{"duration":0,"step":0,"distance":0,"calory":0,"today_date":"2018-08-22"}}}]
     */

    private int ret;
    private String message;
    private int timestamp;
    private List<DataBean> data;

    public void setRet(int ret) {
        this.ret = ret;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public void setData(List<DataBean> data) {
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

    public List<DataBean> getData() {
        return data;
    }

    public static class DataBean {
        /**
         * friend_user_id : 52
         * remark : 我的手机号
         * create_time : 2018-08-15 03:44:31
         * friend : {"id":"52","nickname":"东芝的账号231","sign":"","portrait":"http://api.core.iwear88.com/uploads/E7bVq2iX8GJErruJ.jpg","gender":"0","sport":{"duration":0,"step":0,"distance":0,"calory":0,"today_date":"2018-08-22"}}
         */
        private int friend_user_id;
        private String remark;
        private String create_time;
        private FriendBean friend;

        public void setFriend_user_id(int friend_user_id) {
            this.friend_user_id = friend_user_id;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public void setCreate_time(String create_time) {
            this.create_time = create_time;
        }

        public void setFriend(FriendBean friend) {
            this.friend = friend;
        }

        public int getFriend_user_id() {
            return friend_user_id;
        }

        public String getRemark() {
            return remark;
        }

        public String getCreate_time() {
            return create_time;
        }

        public FriendBean getFriend() {
            return friend;
        }

        public static class FriendBean {
            /**
             * id : 52
             * nickname : 东芝的账号231
             * sign :
             * portrait : http://api.core.iwear88.com/uploads/E7bVq2iX8GJErruJ.jpg
             * gender : 0
             * sport : {"duration":0,"step":0,"distance":0,"calory":0,"today_date":"2018-08-22"}
             */

            private String id;
            private String nickname;
            private String sign;
            private String portrait;
            private String gender;
            private SportBean sport;

            public void setId(String id) {
                this.id = id;
            }

            public void setNickname(String nickname) {
                this.nickname = nickname;
            }

            public void setSign(String sign) {
                this.sign = sign;
            }

            public void setPortrait(String portrait) {
                this.portrait = portrait;
            }

            public void setGender(String gender) {
                this.gender = gender;
            }

            public void setSport(SportBean sport) {
                this.sport = sport;
            }

            public String getId() {
                return id;
            }

            public String getNickname() {
                return nickname;
            }

            public String getSign() {
                return sign;
            }

            public String getPortrait() {
                return portrait;
            }

            public String getGender() {
                return gender;
            }

            public SportBean getSport() {
                return sport;
            }

            public static class SportBean {
                /**
                 * duration : 0
                 * step : 0
                 * distance : 0
                 * calory : 0
                 * today_date : 2018-08-22
                 */

                private int duration;
                private int step;
                private int distance;
                private int calory;
                private String today_date;

                public void setDuration(int duration) {
                    this.duration = duration;
                }

                public void setStep(int step) {
                    this.step = step;
                }

                public void setDistance(int distance) {
                    this.distance = distance;
                }

                public void setCalory(int calory) {
                    this.calory = calory;
                }

                public void setToday_date(String today_date) {
                    this.today_date = today_date;
                }

                public int getDuration() {
                    return duration;
                }

                public int getStep() {
                    return step;
                }

                public int getDistance() {
                    return distance;
                }

                public int getCalory() {
                    return calory;
                }

                public String getToday_date() {
                    return today_date;
                }
            }
        }
    }
}
