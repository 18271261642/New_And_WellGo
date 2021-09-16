package com.sn.app.net.data.app.bean;

import java.util.List;

/**
 * 作者:东芝(2018/8/14).
 * 功能:系统消息
 */

public class SystemMessageBean {

    /**
     * ret : 0
     * message :
     * timestamp : 1534212464
     * data : {"items":[{"id":18,"user_id":698,"send_user_id":31103,"content":"用户 黄建华 给你点赞","type":"encourage","send_time":"2018-08-14 10:06:57"},{"id":17,"user_id":698,"send_user_id":31103,"content":"用户 黄建华 鼓励你多运动","type":"thumb","send_time":"2018-08-14 10:06:54"}],"_meta":{"totalCount":2,"pageCount":1,"currentPage":1,"perPage":20}}
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
        /**
         * items : [{"id":18,"user_id":698,"send_user_id":31103,"content":"用户 黄建华 给你点赞","type":"encourage","send_time":"2018-08-14 10:06:57"},{"id":17,"user_id":698,"send_user_id":31103,"content":"用户 黄建华 鼓励你多运动","type":"thumb","send_time":"2018-08-14 10:06:54"}]
         * _meta : {"totalCount":2,"pageCount":1,"currentPage":1,"perPage":20}
         */

        private MetaBean _meta;
        private List<ItemsBean> items;

        public void set_meta(MetaBean _meta) {
            this._meta = _meta;
        }

        public void setItems(List<ItemsBean> items) {
            this.items = items;
        }

        public MetaBean get_meta() {
            return _meta;
        }

        public List<ItemsBean> getItems() {
            return items;
        }

        public static class MetaBean {
            /**
             * totalCount : 2
             * pageCount : 1
             * currentPage : 1
             * perPage : 20
             */

            private int totalCount;
            private int pageCount;
            private int currentPage;
            private int perPage;

            public void setTotalCount(int totalCount) {
                this.totalCount = totalCount;
            }

            public void setPageCount(int pageCount) {
                this.pageCount = pageCount;
            }

            public void setCurrentPage(int currentPage) {
                this.currentPage = currentPage;
            }

            public void setPerPage(int perPage) {
                this.perPage = perPage;
            }

            public int getTotalCount() {
                return totalCount;
            }

            public int getPageCount() {
                return pageCount;
            }

            public int getCurrentPage() {
                return currentPage;
            }

            public int getPerPage() {
                return perPage;
            }
        }

        public static class ItemsBean {

            /**
             * id : 3
             * user_id : 1
             * send_user_id : 93913
             * content : 用户 啊哈 给你点赞
             * type : encourage
             * sender : {"id":93913,"email":"rui1642@163.com","nickname":"啊哈","gender":1}
             * send_time : 2018-08-04 15:56:47
             */

            private int id;
            private int user_id;
            private int send_user_id;
            private String content;
            private String type;
            private SenderBean sender;
            private String send_time;

            public void setId(int id) {
                this.id = id;
            }

            public void setUser_id(int user_id) {
                this.user_id = user_id;
            }

            public void setSend_user_id(int send_user_id) {
                this.send_user_id = send_user_id;
            }

            public void setContent(String content) {
                this.content = content;
            }

            public void setType(String type) {
                this.type = type;
            }

            public void setSender(SenderBean sender) {
                this.sender = sender;
            }

            public void setSend_time(String send_time) {
                this.send_time = send_time;
            }

            public int getId() {
                return id;
            }

            public int getUser_id() {
                return user_id;
            }

            public int getSend_user_id() {
                return send_user_id;
            }

            public String getContent() {
                return content;
            }

            public String getType() {
                return type;
            }

            public SenderBean getSender() {
                return sender;
            }

            public String getSend_time() {
                return send_time;
            }

            public static class SenderBean {
                /**
                 * id : 93913
                 * email : rui1642@163.com
                 * nickname : 啊哈
                 * gender : 1
                 */

                private int id;
                private String email;
                private String nickname;
                private int gender;

                public void setId(int id) {
                    this.id = id;
                }

                public void setEmail(String email) {
                    this.email = email;
                }

                public void setNickname(String nickname) {
                    this.nickname = nickname;
                }

                public void setGender(int gender) {
                    this.gender = gender;
                }

                public int getId() {
                    return id;
                }

                public String getEmail() {
                    return email;
                }

                public String getNickname() {
                    return nickname;
                }

                public int getGender() {
                    return gender;
                }
            }
        }
    }
}
