package com.sn.blesdk.net;

import com.sn.app.net.callback.OnResponseListener;
import com.sn.app.utils.AppUserUtil;
import com.sn.blesdk.db.data.base.SNBLEDao;
import com.sn.blesdk.db.data.health.heart_rate.HeartRateBean;
import com.sn.blesdk.db.data.health.heart_rate.HeartRateDao;
import com.sn.blesdk.net.bean.HealthNetBean;
import com.sn.blesdk.net.bean.UploadStatusBean;
import com.sn.blesdk.utils.eventbus.SNBLEEvent;
import com.sn.utils.DateUtil;
import com.sn.utils.IF;
import com.sn.utils.SNLog;
import com.sn.utils.eventbus.SNEventBus;
import com.sn.utils.task.SNAsyncTask;
import com.sn.utils.task.SNVTaskCallBack;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;

/**
 * 作者:东芝(2018/1/21).
 * 功能:心率数据上传/下载
 */

public class HealthHeartRateDataNetworkSyncHelper {

    public final static int UPLOAD_TYPE_REAL_TIME = 1;
    public final static int TYPE_HEART_RATE = 2;
    public final static int UPLOAD_TYPE_HISTORY = 2;
    ////////////////////////////////////////////////////////////////////////////////////////
    //--------------------------------------上传-----------------------------------
    ////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 上传到服务器
     * JSON 举例
     * [
     * {
     * "max":98,
     * "min":69,
     * "average":80,
     * "mac":"81:FA:CA:DE:AD:B0",
     * "date":"2018-01-27",
     * "time":"2018-01-27 00:00:11",//必须不能重复同一个时间 否则不能重复提交
     * "type":2,
     * "data":"[{"time":"14:45","value":74},{"time":"14:49","value":78},{"time":"14:50","value":87},{"time":"14:53","value":94},{"time":"15:00","value":69},{"time":"15:15","value":72},{"time":"15:30","value":98},{"time":"15:45","value":72},{"time":"16:00","value":75},{"time":"16:15","value":80},{"time":"16:30","value":81}]"
     * }
     * ]
     */
    public static void uploadToServer( List<HeartRateBean> uploadList) {
        if(uploadList==null)return;

        JSONArray paramsArr = new JSONArray();
        try {
//            HeartRateDao heartRateDao = SNBLEDao.get(HeartRateDao.class);
//            List<HeartRateBean> uploadList;
//            switch (type) {
//                case UPLOAD_TYPE_REAL_TIME:
//                    //实时数据只能是今天的数据 所以查今天
//                    uploadList = heartRateDao.queryForToday(AppUserUtil.getUser().getUser_id());
//                    break;
//                case UPLOAD_TYPE_HISTORY:
//                    //获取今天数据
//                    Calendar currentCalendar = DateUtil.getCurrentCalendar();
//                    //今天date
//                    String toDate = DateUtil.getDate(DateUtil.YYYY_MM_DD, currentCalendar);
//                    //减去一天
//                    currentCalendar.add(Calendar.DAY_OF_MONTH, -1);//昨天
//                    //昨天date
//                    String fromDate = DateUtil.getDate(DateUtil.YYYY_MM_DD, currentCalendar);
//
//                    //查询这两天的数据
//                    uploadList = heartRateDao.queryForBetween(AppUserUtil.getUser().getUser_id(), fromDate, toDate);
//                    break;
//                default:
//                    return;
//            }
            //上传到服务器/......
            //TODO 有可能这里先调用 则[实时心率]比[历史心率大数据] 先调用 则可能 直接提交今天的数据,导致等会才同步成功的昨天的心率大数据无法上传!  估计要后台改 否则APP层的处理逻辑很复杂(比如要各种判断 比如要等待数据同步处理完毕才能进自动检测界面等) 容易出bug

            for (HeartRateBean heartRateBean : uploadList) {

                boolean isToday = DateUtil.equalsToday(heartRateBean.getDate());
                if(isToday)continue;//TODO 2018-05-08 改成今天的数据不上传
                //如果数据为0 同时不是今天(则昨天)的数据,说明 这数据是0 不要替换昨天的数据 否则会把昨天的清零
                //而如果是今天的数据 不用管数据是不是0 我们都应该覆盖服务器上的
                if (heartRateBean.getAvg() == 0 && !isToday) {
                    continue;
                }

                JSONObject params = new JSONObject();
                params.put("type", TYPE_HEART_RATE /*1：血氧，2：心率，3：血压*/);
                params.put("max", heartRateBean.getMax());//最大值（当天）
                params.put("min", heartRateBean.getMin());//最小值（当天）
                params.put("average", heartRateBean.getAvg());//平均值（当天）
                params.put("mac", heartRateBean.getMac());//手环mac地址 （必传）
                params.put("date", heartRateBean.getDate());//日期(年月日)

//                if (isToday) {//今天的数据 就按今天的来
//                    params.put("time", DateUtil.getCurrentDate(DateUtil.YYYY_MM_DD_HH_MM_SS));//体检开始的时间 （必传,不能有重复）
//                } else {//昨天的数据,就造假 因为时间不能重复
//                    params.put("time", String.format("%s 23:59:59", heartRateBean.getWhichDate()));//体检开始的时间（必传,不能有重复）
//                }

                JSONArray dataArray = new JSONArray();
                ArrayList<HeartRateBean.HeartRateDetailsBean> heartRateDetails = heartRateBean.getHeartRateDetails();
                int count = 0;
                for (HeartRateBean.HeartRateDetailsBean heartRateDetail : heartRateDetails) {
                    int val = heartRateDetail.getValue();
                    if (val > 0) {
                        count++;
                        JSONObject data = new JSONObject();
                        //转成  时:分(HH:mm)
                        String time = DateUtil.convertStringToNewString(DateUtil.YYYY_MM_DD_HH_MM_SS, DateUtil.HH_MM, heartRateDetail.getDateTime());
                        data.put("time", time);
                        data.put("value", val);
                        data.put("type", heartRateDetail.getType());//type=0 默认手动检测.1为自动检测
                        dataArray.put(data);
                    }
                }
                params.put("times", count);//当天平均值大于0的总次数，有效次数


                params.put("data", dataArray.toString());

                paramsArr.put(params);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //如果没有任何要传的数据 则不请求上传, 减少服务器压力和用户流量
        if (paramsArr.length() == 0) {
            return;
        }
        String json = paramsArr.toString();
        DeviceNetReq.getApi().examinationUploadDay(json, TYPE_HEART_RATE).enqueue(new OnResponseListener<UploadStatusBean>() {
            @Override
            public void onResponse(final UploadStatusBean body) {
                UploadStatusBean.DataBean data = body.getData();
                int success_num = data.getSuccess_num();
                if (success_num > 0) {
                    SNLog.i("上传心率数据成功条数:" + success_num   );
                    if(!IF.isEmpty(data.getSuccess_dates())) {
                        updateUploadStatus(data.getSuccess_dates());
                    }
                } else {
                    SNLog.i("上传心率数据失败,服务器已经有数据");
                }
            }

            @Override
            public void onFailure(int ret, String msg) {
                SNLog.e("上传心率数据失败:%d,%s", ret, msg);
            }
        });
    }

    private static void updateUploadStatus(final List<String> success_dates) {
        SNAsyncTask.execute(new SNVTaskCallBack() {
            @Override
            public void run() throws Throwable {
                HeartRateDao heartRateDao = SNBLEDao.get(HeartRateDao.class);
                for (String date : success_dates) {
                    heartRateDao.updateUploadStatus(AppUserUtil.getUser().getUser_id(), date, true);
                }
            }
        });
    }

    ////////////////////////////////////////////////////////////////////////////////////////
    //--------------------------------------上传-----------------------------------
    ////////////////////////////////////////////////////////////////////////////////////////


    ////////////////////////////////////////////////////////////////////////////////////////
    //--------------------------------------下载-----------------------------------
    ////////////////////////////////////////////////////////////////////////////////////////


    /**
     * 从服务器下载
     *
     * @param beginDate
     * @param endDate
     */
    public static void downloadFromServer(String beginDate, String endDate) {
        Call<HealthNetBean> call;
        //如果是只查当天的的数据
        if (beginDate.equalsIgnoreCase(endDate)) {
            call = DeviceNetReq.getApi().examinationDownLoad(beginDate, TYPE_HEART_RATE);
        } else {
            call = DeviceNetReq.getApi().examinationDayRangeDownLoad(beginDate, endDate, TYPE_HEART_RATE);
        }

        call.enqueue(new OnResponseListener<HealthNetBean>() {
            @Override
            public void onResponse(final HealthNetBean body) throws Throwable {
//                SNLog.i("心率拉下来:\n" + JsonUtil.toJson(body));
               SNAsyncTask.execute(new SNVTaskCallBack() {

                   @Override
                   public void run() throws Throwable {
                       List<HealthNetBean.DataBean> dataBeans = body.getData();
                       if(dataBeans==null)return;

                       HeartRateDao heartRateDao = SNBLEDao.get(HeartRateDao.class);
                       for (HealthNetBean.DataBean dataBean : dataBeans) {
                           String data = dataBean.getData();
                           //数据完整校验, 这里要注意 []  代表无数据 不需要解析
                           if ((!IF.isContains(data, "[]")) && IF.isContains(data, "[", "]", "{", "}", "time", "value")) {
                               HeartRateBean heartRateBean = new HeartRateBean();
                               heartRateBean.setMax(dataBean.getMax());
                               heartRateBean.setMin(dataBean.getMin());
                               heartRateBean.setAvg(dataBean.getAverage());
                               heartRateBean.setUploaded(true);
                               heartRateBean.setDate(dataBean.getDate());
                               heartRateBean.setMac(dataBean.getMac());
                               heartRateBean.setUser_id(dataBean.getUser_id());
                               ArrayList<HeartRateBean.HeartRateDetailsBean> heartRateDetails = new ArrayList<>();
                               //解析时间在1440中对应的值 并转换为数据库对象
                               JSONArray jsonArray = new JSONArray(data);
                               for (int i = 0; i < jsonArray.length(); i++) {
                                   JSONObject jsonObject = jsonArray.getJSONObject(i);
                                   String time = jsonObject.optString("time");
                                   String dateTime;
                                   //IOS可能已经带yyyy-MM-dd HH:mm:ss
                                   if (time.contains("-")) {
                                       dateTime = time;
                                   }else{
                                       //如果是HH:mm 则转成 yyyy-MM-dd HH:mm:ss
                                       dateTime = String.format(Locale.ENGLISH, "%s %s:00", dataBean.getDate(), time);
                                   }
                                   Calendar calendar = DateUtil.convertStringToCalendar(DateUtil.YYYY_MM_DD_HH_MM_SS, dateTime);
                                   int value = jsonObject.optInt("value");
                                   int type = jsonObject.optInt("type");
                                   int index = DateUtil.convertTimeToIndex(calendar, 1);
                                   heartRateDetails.add(new HeartRateBean.HeartRateDetailsBean(index, dateTime, value, type));
                               }
                               //具体详情
                               heartRateBean.setHeartRateDetails(heartRateDetails);
                               heartRateDao.insertOrUpdate(dataBean.getUser_id(),heartRateBean);
                           }
                       }
                   }
                   @Override
                   public void done() {
                       SNLog.i("心率网络数据拉取并存储成功");
                       SNEventBus.sendEvent(SNBLEEvent.EVENT_UPDATED_HEART_RATE_DATA);
                   }
               });

            }

            @Override
            public void onFailure(int ret, String msg) {

            }
        });

    }

    ////////////////////////////////////////////////////////////////////////////////////////
    //--------------------------------------下载-----------------------------------
    ////////////////////////////////////////////////////////////////////////////////////////


}
