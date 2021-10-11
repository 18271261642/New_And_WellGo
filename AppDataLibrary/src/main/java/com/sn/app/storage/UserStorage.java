package com.sn.app.storage;

import com.google.gson.reflect.TypeToken;
import com.sn.net.utils.JsonUtil;
import com.sn.utils.storage.SNStorage;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * 作者:东芝(2018/1/19).
 * 功能:用户轻量级存储
 */

public class UserStorage extends SNStorage {

    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final String ACCESS_TOKEN = "access_token";
    private static final String IS_FIRST = "is_first";
    private static final String USER_ID = "user_id";
    private static final String STRAVA_ACCESS_TOKEN = "strava_access_token";
    private static final String LAST_INVITE_TIME = "last_invite_time";
    private static final String HAS_NEW_FRIEND_REQUEST_UNREAD_NUMBER = "NewFriendRequestUnreadNumber";
    private static final String NEW_SYS_MESSAGE_UNREAD_NUMBER = "NewSysMessageUnreadNumber";
    private static final String IS_FIRST_GUIDE = "is_first_guide";
    private static final String HOME_ITEM_ORDER = "home_item_order";
    private static final String HOME_ITEM_CLOSE = "home_item_close";
    private static final String LAST_DEVICE_SYNC_TIME = "last_device_sync_time";

    private static final String TMP_USER_ID = "tmp_user_id";

    public static void setTmpUserId(int userId){
        setValue(TMP_USER_ID,userId);
    }

    public static int getTmpUserId(){
        return getValue(TMP_USER_ID,0);
    }

    /**
     * 上次同步时间
     * @param currentTimeMillis
     */
    public static void setLastDeviceSyncTime(long currentTimeMillis) {
        setValue(LAST_DEVICE_SYNC_TIME, currentTimeMillis);
    }

    /**
     * 上次同步时间
     * @return
     */
    public static long getLastDeviceSyncTime() {
        return getValue(LAST_DEVICE_SYNC_TIME, -1L);
    }

    public static void setAccount(String account) {
        setValue(ACCOUNT, account);
    }

    public static String getAccount() {
        return getValue(ACCOUNT, "");
    }

    public static void setPassword(String password) {
        setValue(PASSWORD, password);
    }

    public static String getPassword() {
        return getValue(PASSWORD, "");
    }

    public static void setAccessToken(String access_token) {
        setValue(ACCESS_TOKEN, access_token);
    }

    public static String getAccessToken() {
        return getValue(ACCESS_TOKEN, "");
    }


    public static void setUserId(int userId) {
        setValue(USER_ID, userId);
    }

    public static int getUserId() {
        return getValue(USER_ID, -1);
    }


    public static void setIsFirst(boolean isFirst) {
        setValue(IS_FIRST, isFirst);
    }

    public static boolean isFirst() {
        return getValue(IS_FIRST, true);
    }

    public static void setIsFirstGuide(boolean isFirst) {
        setValue(IS_FIRST_GUIDE, isFirst);
    }

    public static boolean isFirstGuide() {
        return getValue(IS_FIRST_GUIDE, true);
    }

    /**
     * Strava token
     *
     * @param access_token
     */
    public static void setStravaAccessToken(String access_token) {
        setValue(STRAVA_ACCESS_TOKEN, access_token);
    }

    /**
     * Strava token
     */
    public static String getStravaAccessToken() {
        return getValue(STRAVA_ACCESS_TOKEN, "");
    }


    /**
     * 使用上一次调用邀请列表时的第一条的invite_time，首次传0
     *
     * @param invite_time
     */
    public static void setLastInviteTime(int invite_time) {
        setValue(LAST_INVITE_TIME, invite_time);
    }

    public static int getLastInviteTime() {
        return getValue(LAST_INVITE_TIME, 0);
    }


    /**
     * 新好友消息请求未读数
     *
     * @param number
     */
    public static void setNewFriendRequestUnreadNumber(int number) {
        setValue(HAS_NEW_FRIEND_REQUEST_UNREAD_NUMBER, number);
    }

    public static int getNewFriendRequestUnreadNumber() {
        return getValue(HAS_NEW_FRIEND_REQUEST_UNREAD_NUMBER, 0);
    }

    /**
     * 新系统消息未读数
     *
     * @param number
     */
    public static void setNewSysMessageUnreadNumber(int number) {
        setValue(NEW_SYS_MESSAGE_UNREAD_NUMBER, number);
    }

    public static int getNewSysMessageUnreadNumber() {
        return getValue(NEW_SYS_MESSAGE_UNREAD_NUMBER, 0);
    }

    public static void setHomeItemOrder(List<Integer> itemOrder) {
        if (itemOrder == null) {
            setValue(HOME_ITEM_ORDER, "");
            return;
        }
        setValue(HOME_ITEM_ORDER, JsonUtil.toJson(itemOrder));
    }

    public static List<Integer> getHomeItemOrder() {
        try {
            String value = getValue(HOME_ITEM_ORDER, "");
            return JsonUtil.toListBean(value, Integer.class);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }


    public static void setHomeItemClose(List<Integer> itemClose) {
        if (itemClose == null) {
            setValue(HOME_ITEM_CLOSE, "");
            return;
        }
        setValue(HOME_ITEM_CLOSE, JsonUtil.toJson(itemClose));
    }


    public static List<Integer> getHomeItemClose() {
        try {
            String value = getValue(HOME_ITEM_CLOSE, "");
            Type type = new TypeToken<ArrayList<Integer>>() {
            }.getType();
            return JsonUtil.toListBean(value, type);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}
