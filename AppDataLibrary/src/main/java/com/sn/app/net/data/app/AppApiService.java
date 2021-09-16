package com.sn.app.net.data.app;


import com.sn.app.BuildConfig;
import com.sn.app.net.data.app.bean.AppVersionNetBean;
import com.sn.app.net.data.app.bean.FriendInfoBean;
import com.sn.app.net.data.app.bean.FriendInvitesBean;
import com.sn.app.net.data.app.bean.FriendListBean;
import com.sn.app.net.data.app.bean.GetQuestionsBean;
import com.sn.app.net.data.app.bean.HWVersionBean;
import com.sn.app.net.data.app.bean.ImageBean;
import com.sn.app.net.data.app.bean.MeaHistoryBean;
import com.sn.app.net.data.app.bean.MealListBean;
import com.sn.app.net.data.app.bean.MealSingleBean;
import com.sn.app.net.data.app.bean.RankingDefBean;
import com.sn.app.net.data.app.bean.ResetPswQuestionStatusBean;
import com.sn.app.net.data.app.bean.SearchFoodResultBean;
import com.sn.app.net.data.app.bean.SearchFriendsResultBean;
import com.sn.app.net.data.app.bean.SignBean;
import com.sn.app.net.data.app.bean.SportTrackBean;
import com.sn.app.net.data.app.bean.SystemMessageBean;
import com.sn.app.net.data.app.bean.TmallGenieAuthCodeBean;
import com.sn.app.net.data.app.bean.UnreadNumberBean;
import com.sn.app.net.data.app.bean.UserDeviceBean;
import com.sn.app.net.data.app.bean.UserMessageBean;
import com.sn.app.net.data.app.bean.WeatherBean;
import com.sn.app.net.data.app.bean.WeatherListBean;
import com.sn.app.net.data.base.DefResponseBean;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * 作者:东芝(2017/7/28).
 * 功能:App网络部分
 */

public interface AppApiService {
    String ACCESS_TOKEN_NEOON = "@access_token:neoon";
    String APP_ID = "@app_id:" + BuildConfig.APP_ID;

    /**
     * 设置用户所属的app_id
     * 1.2.94更新后的APP需要在相关接口传app_id字段，android和ios协商定义下各个APP的app_id（规则：正整数）
     * 在调用 获取用户信息 接口后，若返回的app_id=0，则调用 设置用户所属的app_id 接口，将当前的app_id上传到服务器。
     * 服务器只会在 登录接口/第三方登陆接口/设置用户所属的app_id接口 修改用户的app_id，且当app_id不为0时，不作修改。
     *
     * @return
     */
    @Headers({ACCESS_TOKEN_NEOON, APP_ID})
    @POST("/user/set-appid")
    Call<DefResponseBean> setAppId();

    /**
     * 注册 使用邮箱和密保问题注册
     *
     * @param email    邮箱
     * @param password 密码
     * @return
     */
    @Headers({APP_ID})
    @FormUrlEncoded
    @POST("/user/signup2")
    Call<SignBean> signup2(@Field("email") String email, @Field("password") String password, @Field("keys") String keys);

    /**
     * 注册
     *
     * @param email    邮箱
     * @param password 密码
     * @return
     */
    @Headers({APP_ID})
    @FormUrlEncoded
    @POST("/user/signup")
    Call<SignBean> signup(@Field("email") String email, @Field("password") String password, @Field("keys") String keys);

    /**
     * 注册
     *
     * @param country_code 国家代码(86)
     * @param phone        电话
     * @param code         验证码
     * @param password     密码
     * @return
     */
    @Headers({APP_ID})
    @FormUrlEncoded
    @POST("/user/signup")
    Call<SignBean> signupPhone(@Field("country_code") String country_code, @Field("phone") String phone, @Field("code") String code, @Field("password") String password);


    /**
     * 登录
     *
     * @param email    邮箱
     * @param password 密码
     * @return
     */
    @Headers({APP_ID})
    @FormUrlEncoded
    @POST("/user/login")
    Call<SignBean> loginFromEmail(@Field("email") String email, @Field("password") String password);

    /**
     * 登录
     *
     * @param country_code 国家代码
     * @param phone        手机号
     * @param password     密码
     * @return
     */
    @Headers({APP_ID})
    @FormUrlEncoded
    @POST("/user/login")
    Call<SignBean> loginFromPhoneNumber(@Field("country_code") String country_code, @Field("phone") String phone, @Field("password") String password);

    /**
     * 第三方登录
     *
     * @param openId   第三方提供的id
     * @param openType 密码
     * @return
     */
    @Headers({APP_ID})
    @FormUrlEncoded
    @POST("/user/open")
    Call<SignBean> loginFromOther(@Field("openid") String openId, @Field("opentype") String openType);

    /**
     * 重置密码
     * 此接口会清空access token，调用后需重新登录
     *
     * @param email    邮箱
     * @param code     验证码
     * @param password 密码
     * @return
     */
    @Headers({APP_ID})
    @FormUrlEncoded
    @POST("/user/resetpwd")
    Call<DefResponseBean> resetpwd(@Field("email") String email, @Field("code") String code, @Field("password") String password);

    /**
     * 重置密码
     * 此接口会清空access token，调用后需重新登录
     *
     * @param email    邮箱
     * @param keys     密保问题
     * @param password 密码
     * @return
     */
    @Headers({APP_ID})
    @FormUrlEncoded
    @POST("/user/reset-pwd-by-keys")
    Call<ResetPswQuestionStatusBean> resetpwdByKeys(@Field("email") String email, @Field("keys") String keys, @Field("password") String password);

    /**
     * 重置密码
     * 此接口会清空access token，调用后需重新登录
     *
     * @param country_code 国家代码
     * @param phone        手机号
     * @param code         验证码
     * @param password     密码
     * @return
     */
    @Headers({APP_ID})
    @FormUrlEncoded
    @POST("/user/resetpwd")
    Call<DefResponseBean> resetpwdPhone(@Field("country_code") String country_code, @Field("phone") String phone, @Field("code") String code, @Field("password") String password);

    /**
     * 获取用户的密保问题
     *
     * @param email 邮箱，没有传手机号时必选
     * @return
     */
    @Headers({APP_ID})
    @GET("/user/get-key-questions")
    Call<GetQuestionsBean> getKeyQuestions(@Query("email") String email);


    /**
     * 发送验证码
     *
     * @param email    邮箱，没有传手机号时必选
     * @param type     1：注册，2：重置密码
     * @param language 验证码的语言，简体中文：cmn-Hans 英文：en 默认值: cmn-Hans
     * @return
     */
    @Headers({APP_ID})
    @GET("/user/captcha")
    Call<DefResponseBean> captcha(@Query("email") String email, @Query("type") int type, @Query("language") String language);


    /**
     * 发送验证码
     *
     * @param country_code 国家代码
     * @param phone        手机号
     * @param type         1：注册，2：重置密码
     * @param language     验证码的语言，简体中文：cmn-Hans 英文：en 默认值: cmn-Hans
     * @return
     */
    @Headers({APP_ID})
    @GET("/user/captcha")
    Call<DefResponseBean> captchaPhone(@Query("country_code") String country_code,
                                       @Query("phone") String phone,
                                       @Query("type") int type,
                                       @Query("language") String language);


    /**
     * 上传图片
     * 通过  MultipartBody.Part  传入多个part实现多文件上传
     *
     * @param file parts 每个part代表一个
     * @return
     */
    @Multipart
    @POST("/user/upload-image")
    Call<ImageBean> uploadImage(@Part MultipartBody.Part file);


    /**
     * 修改食谱目标计划参数
     * http://119.23.8.182:8081/doc/#api-user-updateUser
     *
     * @return
     */
    @Headers({ACCESS_TOKEN_NEOON})
    @FormUrlEncoded
    @POST("/user/update")
    Call<UserMessageBean> updateUser(
            @Field("target_step") int target_step,
            @Field("weight") float current_weight,
            @Field("goal_weight") float target_weight,
            @Field("goal_calory") float target_calory
    );


    /**
     * 修改用户信息
     * http://119.23.8.182:8081/doc/#api-user-updateUser
     *
     * @return
     */
    @Headers({ACCESS_TOKEN_NEOON})
    @FormUrlEncoded
    @POST("/user/update")
    Call<UserMessageBean> updateUser(
            @Field("nickname") String nickname,
            @Field("sign") String sign,
            @Field("birthday") String birthday,
            @Field("height") int height,
            @Field("weight") float weight,
            @Field("last_weight_time") String lastWeightTime,
            @Field("gender") int gender,
            @Field("portrait") String portrait,
            @Field("target_step") int target_step
    );


    /**
     * 修改用户信息
     * http://119.23.8.182:8081/doc/#api-user-updateUser
     *
     * @return
     */
    @Headers({ACCESS_TOKEN_NEOON})
    @FormUrlEncoded
    @POST("/user/update")
    Call<UserMessageBean> updateUser(
            @Field("nickname") String nickname,
            @Field("sign") String sign,
            @Field("birthday") String birthday,
            @Field("height") int height,
            @Field("weight") float weight,
            @Field("last_weight_time") String lastWeightTime,
            @Field("gender") int gender,
            @Field("target_step") int target_step
    );

    /**
     * 修改用户信息
     * http://119.23.8.182:8081/doc/#api-user-updateUser
     *
     * @return
     */
    @Headers({ACCESS_TOKEN_NEOON})
    @FormUrlEncoded
    @POST("/user/update")
    Call<UserMessageBean> updateUser(
            @Field("nickname") String nickname,
            @Field("address") String address,
            @Field("birthday") String birthday,
            @Field("sign") String sign,
            @Field("job") String job,
            @Field("height") int height,
            @Field("weight") float weight,
            @Field("gender") int gender,
            @Field("portrait") String portrait,
            @Field("wallpaper") String wallpaper,
            @Field("target_step") int target_step,
            @Field("target_sleep") int target_sleep/*单位:秒*/
    );

    /**
     * 修改用户信息
     * http://119.23.8.182:8081/doc/#api-user-updateUser
     *
     * @return
     */
    @Headers({ACCESS_TOKEN_NEOON})
    @FormUrlEncoded
    @POST("/user/update")
    Call<UserMessageBean> updateUser(
            @Field("nickname") String nickname,
            @Field("address") String address,
            @Field("birthday") String birthday,
            @Field("sign") String sign,
            @Field("job") String job,
            @Field("height") int height,
            @Field("weight") float weight,
            @Field("gender") int gender,
            @Field("portrait") String portrait,
            @Field("wallpaper") String wallpaper

    );

    /**
     * 获取用户信息2
     * http://api.core.iwear88.com/doc/#api-user-userView2
     *
     * @return
     */
    @Headers({ACCESS_TOKEN_NEOON, APP_ID})
    @GET("/user/view2")
    Call<UserMessageBean> queryUser(@Query("phone_uuid") String phone_uuid);


    @Headers({ACCESS_TOKEN_NEOON, APP_ID})
    @GET("/user/view")
    Call<UserMessageBean> queryUser0(@Query("phone_uuid") String phone_uuid);

    //=========================================================其他=================================================================

    /**
     * 反馈
     *
     * @param content
     * @return
     */
    @Headers({ACCESS_TOKEN_NEOON})
    @FormUrlEncoded
    @POST("/support/feedback2")
    Call<String> feedback(@Field("content") String content);


    /**
     * 检测app更新
     *
     * @return
     */
    @GET("/support/appversion2")
    Call<AppVersionNetBean> checkAppNewVersion();


    @Headers({ACCESS_TOKEN_NEOON})
    @GET
    Call<ResponseBody> downloadFile(@Url String fileUrl);

    /**
     * 请求天气
     *
     * @param lat  经度
     * @param lon  纬度
     * @param city 城市
     * @return
     */
    @Headers({ACCESS_TOKEN_NEOON})
    @FormUrlEncoded
    @POST("/support/weather")
    Call<WeatherBean> requestWeather(@Field("lat") double lat, @Field("lon") double lon, @Field("city") String city);

    /**
     * 请求天气
     *
     * @param lat  经度
     * @param lon  纬度
     * @param city 城市
     * @return
     */
    @Headers({ACCESS_TOKEN_NEOON})
    @FormUrlEncoded
    @POST("/support/weather-aggregate")
    Call<WeatherListBean> requestWeatherMulti(@Field("lat") double lat, @Field("lon") double lon, @Field("city") String city);


    /**
     * 上传最后一次连接的设备(可能用于app被卸载后 然后重装救砖 )
     *
     * @return
     */
    @Headers({ACCESS_TOKEN_NEOON})
    @FormUrlEncoded
    @POST("/user/userdevice")
    Call<UserDeviceBean> uploadDevice(
            @Field("mac") String mac,
            @Field("device_name") String device_name,
            @Field("function") int function,
            @Field("adv_id") int adv_id,
            @Field("uuid") String ios_uuid,
            @Field("adv_service") String ios_adv_service
    );

    @Headers({ACCESS_TOKEN_NEOON})
    @FormUrlEncoded
    @POST("/user/userdevice")
    Call<UserDeviceBean> uploadDeviceAllMessage(
            @Field("mac") String mac,
            @Field("device_name") String device_name,
            @Field("function") int function,
            @Field("adv_id") int adv_id,
            @Field("customid") int customid,
            @Field("upgradeid") int upgradeid,
            @Field("uuid") String ios_uuid,
            @Field("adv_service") String ios_adv_service
    );

    /**
     * 传用户手环原生Mac地址
     * @param mac
     * @param origin_mac
     * @return
     */
    @Headers({ACCESS_TOKEN_NEOON})
    @FormUrlEncoded
    @POST("/user/origin-mac")
    Call<DefResponseBean> uploadDeviceOriginMac(
            @Field("mac") String mac,
            @Field("origin_mac") String origin_mac
    );

    /**
     * 检测救砖固件
     *
     * @param mac
     * @return
     */
    @Headers({ACCESS_TOKEN_NEOON})
    @GET("/support/hwversion-fix")
    Call<HWVersionBean> checkHWVersionFixFromMac(
            @Query("mac") String mac,
            @Query("origin_mac") String origin_mac
    );




    /**
     * 上传轨迹
     * 这框架有点蛋疼的就是这点 上传文件只能自己在外面装参数
     */
    @POST("/sport/uploadtrack")
    Call<DefResponseBean> uploadTrack(@Body RequestBody Body);

    /**
     * 下载运动轨迹
     */
    @Headers({ACCESS_TOKEN_NEOON})
    @POST("/sport/track")
    Call<SportTrackBean> downloadTrack();


    /**
     * 检测固件更新
     *
     * @return
     */
    @GET("/support/hwversion")
    Call<HWVersionBean> checkHWVersion(@Query("type") int type);

    /**
     * 检测固件更新 二代
     *
     * @return
     */
    @GET("/support/hwversion2")
    Call<HWVersionBean> checkHWVersion2(@Query("adv_id") int adv_id,
                                        @Query("customid") int customid,
                                        @Query("upgradeid") int upgradeid);


    /**
     * 上传日志
     * 这框架有点蛋疼的就是这点 上传文件只能自己在外面装参数
     */
    @POST("/support/userlog")
    Call<String> uploadLog(@Body RequestBody Body);


    /**
     * 用户 - 查找用户
     *
     * @return
     */
    @Headers({ACCESS_TOKEN_NEOON})
    @GET("/user/search")
    Call<SearchFriendsResultBean> userSearch(@Query("key") String key);

    /**
     * 好友 - 邀请好友
     *
     * @return
     */
    @Headers({ACCESS_TOKEN_NEOON})
    @FormUrlEncoded
    @POST("/friend/invite")
    Call<DefResponseBean> friendInvite(@Field("invite_user_id") int invite_user_id);

    /**
     * 好友 - 好友邀请信息列表
     *
     * @return
     */
    @Headers({ACCESS_TOKEN_NEOON})
    @GET("/friend/invites")
    Call<FriendInvitesBean> friendRequest();

    /**
     * 好友 - 好友列表
     *
     * @return
     */
    @Headers({ACCESS_TOKEN_NEOON})
    @GET("/friend/list")
    Call<FriendListBean> friendlist();

    /**
     * 好友 - 好友列表
     *
     * @return
     */
    @Headers({ACCESS_TOKEN_NEOON})
    @GET("/friend/list")
    Call<String> friendlistTest();

    /**
     * 消息列表
     *
     * @return
     */
    @Headers({ACCESS_TOKEN_NEOON})
    @GET("/notice/list")
    Call<SystemMessageBean> friendMsglist();


    /**
     * 好友 - 同意好友邀请
     *
     * @return
     */
    @Headers({ACCESS_TOKEN_NEOON})
    @FormUrlEncoded
    @POST("/friend/pass")
    Call<DefResponseBean> friendAgreed(@Field("invite_id") int invite_id);

    /**
     * 好友 - 拒绝好友邀请
     *
     * @return
     */
    @Headers({ACCESS_TOKEN_NEOON})
    @FormUrlEncoded
    @POST("/friend/reject")
    Call<DefResponseBean> friendRefused(@Field("invite_id") int invite_id);


    /**
     * 好友 - 删除(忽略)好友邀请
     *
     * @return
     */
    @Headers({ACCESS_TOKEN_NEOON})
    @FormUrlEncoded
    @POST("/friend/unshow-invite")
    Call<DefResponseBean> friendIgnore(@Field("invite_id") int invite_id);

    /**
     * 好友 - 删除好友关系
     *
     * @return
     */
    @Headers({ACCESS_TOKEN_NEOON})
    @FormUrlEncoded
    @POST("/friend/delete")
    Call<DefResponseBean> friendDelete(@Field("friend_user_id") int friend_user_id);

    /**
     * 好友 - 备注好友
     *
     * @return
     */
    @Headers({ACCESS_TOKEN_NEOON})
    @FormUrlEncoded
    @POST("/friend/remark")
    Call<DefResponseBean> friendRemark(@Field("friend_user_id") int friend_user_id, @Field("remark") String remark);


    /**
     * 好友 - 备注好友
     *
     * @return
     */
    @Headers({ACCESS_TOKEN_NEOON})
    @FormUrlEncoded
    @POST("/user/sync")
    Call<DefResponseBean> uploadTodayDataToMyFriendsSee
    (
            @Field("today_avg") int today_heart_avg,//今天心率平均
            @Field("today_highest") int today_heart_max,//今天心率最高
            @Field("today_lowest") int today_heart_min,//今天心率最低

            @Field("today_duration") int today_sport_duration,//今天运动时长
            @Field("today_calory") int today_sport_calories,//今天卡路里
            @Field("today_distance") int today_sport_distance,//今天距离值
            @Field("today_step") int today_sport_step,//今天步数值

            @Field("today_ox_avg") int today_oxygen_avg,//今天血氧平均值
            @Field("today_ox_max") int today_oxygen_max,//今天血氧最大值
            @Field("today_ox_min") int today_oxygen_min,//今天血氧最小值

            @Field("today_dbp_avg") int today_diastolic_avg,//今天平均舒张压
            @Field("today_sbp_avg") int today_systolic_avg,//今天平均收缩压

            @Field("today_sleeps") int today_sleep_duration,//今天睡眠时长
            @Field("today_lights") int today_sleep_light,//今天睡眠浅睡分钟数
            @Field("today_deeps") int today_sleep_deep,//今天睡眠深睡分钟数
            @Field("today_wakes") int today_sleep_sober,//今天睡眠清醒分钟数

            @Field("finish_days") String stat_continuous_best_days,//连续达标天数
            @Field("max_step") String stat_best_day,//最佳单日记录
            @Field("max_month") String stat_best_month,//最佳月份
            @Field("max_week") String stat_best_week,//最佳周

            @Field("bp_history") String blood_pressure_history,//血压历史
            @Field("ox_history") String blood_oxygen_history,//血氧历史
            @Field("heart_history") String heart_history,//心率历史
            @Field("sleep_history") String sleep_history,//睡眠历史
            @Field("sport_history") String sport_history, //运动历史
            @Field("today_date") String today_date //日期
    );


    /**
     * 好友用户主页
     *
     * @return
     */
    @Headers({ACCESS_TOKEN_NEOON})
    @GET("/user/homepage")
    Call<FriendInfoBean> friendHomepage(@Query("user_id") int user_id);


    /**
     * 好友 - 点赞/鼓励
     *
     * @param type 1：点赞，2：鼓励
     * @return
     */
    @Headers({ACCESS_TOKEN_NEOON})
    @FormUrlEncoded
    @POST("/friend/thumb")
    Call<String> friendThumbAction(@Field("thumb_user_id") int thumb_user_id, @Field("type") int type);

    /**
     * 获取是否有新的好友请求
     *
     * @return
     */
    @Headers({ACCESS_TOKEN_NEOON})
    @GET("/friend/is-new-invite")
    Call<UnreadNumberBean> hasNewFriendRequestUnreadNumber(@Query("base_time") int last_invite_time);

    /**
     * 获取通知未读消息数量
     *
     * @return
     */
    @Headers({ACCESS_TOKEN_NEOON})
    @GET("/notice/unread-count")
    Call<UnreadNumberBean> hasNewSysMessageUnreadNumber();

    /**
     * 消息全部通知未读消息标为已读
     *
     * @return
     */
    @Headers({ACCESS_TOKEN_NEOON})
    @GET("/notice/read-all")
    Call<String> setReadAllNewSysMessage();

    ////////////////////////////////////////////////////////////////////////////////////////
    //--------------------------------------食谱-----------------------------------
    ////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 搜索匹配食物
     *
     * @return
     */
    @Headers({ACCESS_TOKEN_NEOON})
    @GET("/meal/match-food")
    Call<SearchFoodResultBean> searchFoods(@Query("key") String key);

    /**
     * 增加餐
     *
     * @return
     */
    @Headers({ACCESS_TOKEN_NEOON})
    @FormUrlEncoded
    @POST("/meal/add")
    Call<MealSingleBean> addMeal(@Field("date") String date,
                                 @Field("calory") float calory,
                                 @Field("foods") String foodsJson
    );

    /**
     * 更新餐
     *
     * @return
     */
    @Headers({ACCESS_TOKEN_NEOON})
    @FormUrlEncoded
    @POST("/meal/update")
    Call<MealSingleBean> updateMeal(@Field("id") int id,
                                    @Field("calory") float calory,
                                    @Field("foods") String foodsJson);


    /**
     * 删除餐
     *
     * @param id
     * @return
     */
    @Headers({ACCESS_TOKEN_NEOON})
    @FormUrlEncoded
    @POST("/meal/remove")
    Call<MealSingleBean> deleteMeal(@Field("id") int id);

    /**
     * 获取某天餐列表
     *
     * @param date
     * @return
     */
    @Headers({ACCESS_TOKEN_NEOON})
    @GET("/meal/daily-list")
    Call<MealListBean> loadMealList(@Query("date") String date);

    /**
     * 获取历史餐列表
     *
     * @return
     */
    @Headers({ACCESS_TOKEN_NEOON})
    @GET("/meal/history-list")
    Call<MeaHistoryBean> loadMealList(@Query("begin_date") String begin_date, @Query("end_date") String end_date);


    /**
     * 步数分布统计
     *
     * @return
     */
    @Headers({ACCESS_TOKEN_NEOON})
    @GET("/sport/distribution")
    Call<RankingDefBean> loadSportRankingList();


    /**
     * 睡眠质量分布统计
     *
     * @return
     */
    @Headers({ACCESS_TOKEN_NEOON})
    @GET("/sleep/duration-distribution")
    Call<RankingDefBean> loadSleepQualityRankingList();


    /**
     * 入睡时间分布统计
     *
     * @return
     */
    @Headers({ACCESS_TOKEN_NEOON})
    @GET("/sleep/fall-distribution")
    Call<RankingDefBean> loadSleepTimeRankingList();

    /**
     * 获取天猫精灵用户码
     *
     * @return
     */
    @Headers({ACCESS_TOKEN_NEOON})
    @GET("/user/auth-code")
    Call<TmallGenieAuthCodeBean> getTmallGenieAuthCode();


}
