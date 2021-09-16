package com.sn.app.net.data.app.bean;

/**
 * 作者:东芝(2018/8/9).
 * 功能:朋友对象
 */

public   class FriendshipBean {
    /**
     * user_id : 93913
     * friend_user_id : 1
     * remark : 小可爱
     * create_time : 2018-08-04 13:33:07
     */

    private int user_id;
    private int friend_user_id;
    private String remark;
    private String create_time;

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getFriend_user_id() {
        return friend_user_id;
    }

    public void setFriend_user_id(int friend_user_id) {
        this.friend_user_id = friend_user_id;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }
}