package com.sn.blesdk.net;

import com.sn.blesdk.net.bean.HealthNetBean;
import com.sn.blesdk.net.bean.NetDeviceInfoBean;
import com.sn.blesdk.net.bean.SleepNetBean;
import com.sn.blesdk.net.bean.SportNetBean;
import com.sn.blesdk.net.bean.UploadStatusBean;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * 作者:东芝(2018/1/19).
 * 功能:设备数据网络部分
 */

public interface DeviceApiService {
    String  ACCESS_TOKEN_NEOON = "@access_token:neoon";
    //=========================================================运动相关=================================================================

    /**
     * 上传运动数据
     * 可以一次上传多天的数据，数组每个成员表示一天的数据。只接受服务器时间前7天内，后1天内的数据。当改用户的某天数据发生重复上传时，若此数据的日期为服务器保存该用户数据的最近日期，则对数据进行覆盖，否则忽略此数据。
     *
     * @param dataJsonString json
     * @return
     * @doc http://119.23.8.182:8081/doc/#api-record-sportUpload
     */
    @Headers({ACCESS_TOKEN_NEOON})
    @FormUrlEncoded
    @POST("/sport/upload")
    Call<UploadStatusBean> sportUpload(@Field("data") String dataJsonString);


    /**
     * 数据记录 - 获取某天的运动数据
     * 该日期无数据时，返回 data:[]，有数据时，返回 data:[{}]
     *
     * @param date 日期，格式：yyyy-mm-dd
     * @return
     */
    @Headers({ACCESS_TOKEN_NEOON})
    @GET("/sport/date")
    Call<SportNetBean> sportDownLoad(@Query("date") String date);

    /**
     * 数据记录 - 获取N天的运动数据列表
     * 每一个数组成员为一天的数据，截止日期与开始日期最大不能超过31天，否则返回 data:[]
     *
     * @param beginDate
     * @param endDate
     * @return
     */
    @Headers({ACCESS_TOKEN_NEOON})
    @GET("/sport/dates")
    Call<SportNetBean> sportDayRangeDownLoad(@Query("beginDate") String beginDate, @Query("endDate") String endDate);


    /**
     * 数据记录 - 获取N月的运动数据列表
     * 每一个数组成员为一月的数据，截止日期与开始日期最大不能超过12个月，否则返回 data:[]
     *
     * @param beginMonth 开始日期，格式：yyyy-mm
     * @param endMonth   截止日期，格式：yyyy-mm
     * @return
     */
    @Headers({ACCESS_TOKEN_NEOON})
    @GET("/sport/months")
    Call<SportNetBean> sportMonthsRangeDownLoad(@Query("beginMonth") String beginMonth, @Query("endMonth") String endMonth);


    //=========================================================运动相关=================================================================
    //=========================================================睡眠相关=================================================================

    /**
     * 上传睡眠数据
     * 可以一次上传多天的数据，数组每个成员表示一天的数据。只接受服务器时间前7天内，后1天内的数据。当改用户的某天数据发生重复上传时，若此数据的日期为服务器保存该用户数据的最近日期，则对数据进行覆盖，否则忽略此数据。
     *
     * @param dataJsonString json
     * @return
     * @doc http://119.23.8.182:8081/doc/#api-record-sportUpload
     */
    @Headers({ACCESS_TOKEN_NEOON})
    @FormUrlEncoded
    @POST("/sleep/upload")
    Call<UploadStatusBean> sleepUpload(@Field("data") String dataJsonString);


    /**
     * 数据记录 - 获取某天的睡眠数据
     * 该日期无数据时，返回 data:[]，有数据时，返回 data:[{}]
     *
     * @param date 日期，格式：yyyy-mm-dd
     * @return
     */
    @Headers({ACCESS_TOKEN_NEOON})
    @GET("/sleep/date")
    Call<SleepNetBean> sleepDownLoad(@Query("date") String date);


    /**
     * 数据记录 - 获取N天的睡眠数据列表
     * 每一个数组成员为一天的数据，截止日期与开始日期最大不能超过31天，否则返回 data:[]
     *
     * @param beginDate
     * @param endDate
     * @return
     */
    @Headers({ACCESS_TOKEN_NEOON})
    @GET("/sleep/dates")
    Call<SleepNetBean> sleepDayRangeDownLoad(@Query("beginDate") String beginDate, @Query("endDate") String endDate);


    /**
     * 数据记录 - 获取N月的睡眠数据列表
     * 每一个数组成员为一月的数据，截止日期与开始日期最大不能超过12个月，否则返回 data:[]
     *
     * @param beginMonth 开始日期，格式：yyyy-mm
     * @param endMonth   截止日期，格式：yyyy-mm
     * @return
     */
    @Headers({ACCESS_TOKEN_NEOON})
    @GET("/sleep/months")
    Call<SleepNetBean> sleepMonthsRangeDownLoad(@Query("beginMonth") String beginMonth, @Query("endMonth") String endMonth);

    //=========================================================睡眠相关=================================================================
    //=========================================================体检相关=================================================================

    /**
     * 上传体检数据
     * 可以一次上传多天的数据，数组每个成员表示一天的数据。只接受服务器时间前7天内，后1天内的数据。当改用户的某天数据发生重复上传时，若此数据的日期为服务器保存该用户数据的最近日期，则对数据进行覆盖，否则忽略此数据。
     *
     * @param dataJsonString json [{}]形式的JSON字符串
     * @return
     * @doc http://119.23.8.182:8081/doc/#api-record-sportUpload
     */
    @Headers({ACCESS_TOKEN_NEOON})
    @FormUrlEncoded
    @POST("/examination/upload")
    Call<UploadStatusBean> examinationUpload(@Field("data") String dataJsonString);


    /**
     * 上传一天的体检数据
     *
     * @param dataJsonString json [{}]形式的JSON字符串
     * @return
     * @doc https://api.core.iwear88.com/examination/uploadday
     */
    @Headers({ACCESS_TOKEN_NEOON})
    @FormUrlEncoded
    @POST("/examination/uploadday")
    Call<UploadStatusBean> examinationUploadDay(@Field("data") String dataJsonString,@Field("type") int type);


    /**
     * 数据记录 - 获取某天的体检数据
     * 该日期无数据时，返回 data:[]，有数据时，返回 data:[{}]
     *
     * @param date 日期，格式：yyyy-mm-dd
     * @param type 类型
     * @return
     */
    @Headers({ACCESS_TOKEN_NEOON})
    @GET("/examination/date")
    Call<HealthNetBean> examinationDownLoad(@Query("date") String date, @Query("type") int type);


    /**
     * 数据记录 -获取N天的体检数据列表
     * 每一个数组成员为一天的数据，截止日期与开始日期最大不能超过31天，否则返回 data:[]
     *
     * @param beginDate
     * @param endDate
     * @param type
     * @return
     */
    @Headers({ACCESS_TOKEN_NEOON})
    @GET("/examination/dates2")
    Call<HealthNetBean> examinationDayRangeDownLoad(@Query("beginDate") String beginDate, @Query("endDate") String endDate, @Query("type") int type);


    /**
     * 数据记录 - 获取N月的体检数据列表
     * 每一个数组成员为一月的数据，截止日期与开始日期最大不能超过12个月，否则返回 data:[]
     *
     * @param beginMonth 开始日期，格式：yyyy-mm
     * @param endMonth   截止日期，格式：yyyy-mm
     * @param type
     * @return
     */
    @Headers({ACCESS_TOKEN_NEOON})
    @GET("/examination/months")
    Call<HealthNetBean> examinationMonthsRangeDownLoad(@Query("beginMonth") String beginMonth, @Query("endMonth") String endMonth, @Query("type") int type);

    //=========================================================体检相关=================================================================

    /**
     * 获取所有支持的设备 包括升级包 和设备对应的功能 备注等
     *
     * @return
     */
    @POST("/support/devices")
    Call<NetDeviceInfoBean> queryAllDevices();

    /**
     * 上传最后一次连接的设备(可能用于app被卸载后 然后重装救砖 )
     *
     * @return
     */
    @FormUrlEncoded
    @POST("/user/userdevice")
    Call<String> uploadDevice(@Query("mac") String mac, @Query("function") String function, @Query("device_name") String device_name);
    


}
