package com.sn.blesdk.net;

import com.sn.app.net.callback.OnResponseListener;
import com.sn.app.utils.AppUserUtil;
import com.sn.blesdk.db.data.base.SNBLEDao;
import com.sn.blesdk.db.data.health.temperature.TemperatureBean;
import com.sn.blesdk.db.data.health.temperature.TemperatureDao;
import com.sn.blesdk.net.bean.HealthNetBean;
import com.sn.blesdk.net.bean.UploadStatusBean;
import com.sn.blesdk.utils.eventbus.SNBLEEvent;
import com.sn.net.utils.JsonUtil;
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
 * 功能:体温数据上传/下载
 */

public class HealthTemperatureDataNetworkSyncHelper {

    public final static int UPLOAD_TYPE_REAL_TIME = 1;
    public final static int TYPE_TEMPERATURE = 4;
    public final static int UPLOAD_TYPE_HISTORY = 2;
    ////////////////////////////////////////////////////////////////////////////////////////
    //--------------------------------------上传-----------------------------------
    ////////////////////////////////////////////////////////////////////////////////////////
 
    public static void uploadToServer( List<TemperatureBean> uploadList) {
        if(uploadList==null)return;

        JSONArray paramsArr = new JSONArray();
        try {
 
            //TODO 有可能这里先调用 则[实时体温]比[历史体温大数据] 先调用 则可能 直接提交今天的数据,导致等会才同步成功的昨天的体温大数据无法上传!  估计要后台改 否则APP层的处理逻辑很复杂(比如要各种判断 比如要等待数据同步处理完毕才能进自动检测界面等) 容易出bug

            for (TemperatureBean temperatureBean : uploadList) {

                boolean isToday = DateUtil.equalsToday(temperatureBean.getDate());
                if(isToday)continue;//TODO 2018-05-08 改成今天的数据不上传
                //如果数据为0 同时不是今天(则昨天)的数据,说明 这数据是0 不要替换昨天的数据 否则会把昨天的清零
                //而如果是今天的数据 不用管数据是不是0 我们都应该覆盖服务器上的
                if (temperatureBean.getAvg() == 0 && !isToday) {
                    continue;
                }

                JSONObject params = new JSONObject();
                params.put("type", TYPE_TEMPERATURE /*1：血氧，2：体温，3：血压 4:体温*/);
                params.put("max", temperatureBean.getMax());//最大值（当天）
                params.put("min", temperatureBean.getMin());//最小值（当天）
                params.put("average", temperatureBean.getAvg());//平均值（当天）
                params.put("mac", temperatureBean.getMac());//手环mac地址 （必传）
                params.put("date", temperatureBean.getDate());//日期(年月日)

//                if (isToday) {//今天的数据 就按今天的来
//                    params.put("time", DateUtil.getCurrentDate(DateUtil.YYYY_MM_DD_HH_MM_SS));//体检开始的时间 （必传,不能有重复）
//                } else {//昨天的数据,就造假 因为时间不能重复
//                    params.put("time", String.format("%s 23:59:59", temperatureBean.getWhichDate()));//体检开始的时间（必传,不能有重复）
//                }

                JSONArray dataArray = new JSONArray();
                ArrayList<TemperatureBean.TemperatureDetailsBean> temperatureDetailsBeans = temperatureBean.getTemperatureDetailsBeans();
                int count = 0;
                for (TemperatureBean.TemperatureDetailsBean temperatureDetailsBean : temperatureDetailsBeans) {
                    int val = temperatureDetailsBean.getValue();
                    if (val > 0) {
                        count++;
                        JSONObject data = new JSONObject();
                        //转成  时:分(HH:mm)
                        String time = DateUtil.convertStringToNewString(DateUtil.YYYY_MM_DD_HH_MM_SS, DateUtil.HH_MM, temperatureDetailsBean.getDateTime());
                        data.put("time", time);
                        data.put("value", val);
                        data.put("type", temperatureDetailsBean.getType());//type=0 默认手动检测.1为自动检测
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
        DeviceNetReq.getApi().examinationUploadDay(json, TYPE_TEMPERATURE).enqueue(new OnResponseListener<UploadStatusBean>() {
            @Override
            public void onResponse(final UploadStatusBean body) {
                UploadStatusBean.DataBean data = body.getData();
                int success_num = data.getSuccess_num();
                if (success_num > 0) {
                    SNLog.i("上传体温数据成功条数:" + success_num   );
                    if(!IF.isEmpty(data.getSuccess_dates())) {
                        updateUploadStatus(data.getSuccess_dates());
                    }
                } else {
                    SNLog.i("上传体温数据失败,服务器已经有数据");
                }
            }

            @Override
            public void onFailure(int ret, String msg) {
                SNLog.e("上传体温数据失败:%d,%s", ret, msg);
            }
        });
    }

    private static void updateUploadStatus(final List<String> success_dates) {
        SNAsyncTask.execute(new SNVTaskCallBack() {
            @Override
                public void run() throws Throwable {
                TemperatureDao temperatureDao = SNBLEDao.get(TemperatureDao.class);
                for (String date : success_dates) {
                    temperatureDao.updateUploadStatus(AppUserUtil.getUser().getUser_id(), date, true);
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
            call = DeviceNetReq.getApi().examinationDownLoad(beginDate, TYPE_TEMPERATURE);
        } else {
            call = DeviceNetReq.getApi().examinationDayRangeDownLoad(beginDate, endDate, TYPE_TEMPERATURE);
        }

        call.enqueue(new OnResponseListener<HealthNetBean>() {
            @Override
            public void onResponse(final HealthNetBean body) throws Throwable {
               SNLog.i("========================这里要改, 等会改,记得:  体温拉下来:\n" + JsonUtil.toJson(body));
               SNAsyncTask.execute(new SNVTaskCallBack() {

                   @Override
                   public void run() throws Throwable {
                       List<HealthNetBean.DataBean> dataBeans = body.getData();
                       if(dataBeans==null)return;

                       TemperatureDao temperatureDao = SNBLEDao.get(TemperatureDao.class);
                       for (HealthNetBean.DataBean dataBean : dataBeans) {
                           String data = dataBean.getData();
                           //数据完整校验, 这里要注意 []  代表无数据 不需要解析
                           if ((!IF.isContains(data, "[]")) && IF.isContains(data, "[", "]", "{", "}", "time", "value")) {
                               TemperatureBean temperatureBean = new TemperatureBean();
                               temperatureBean.setMax(dataBean.getMax());
                               temperatureBean.setMin(dataBean.getMin());
                               temperatureBean.setAvg(dataBean.getAverage());
                               temperatureBean.setUploaded(true);
                               temperatureBean.setDate(dataBean.getDate());
                               temperatureBean.setMac(dataBean.getMac());
                               temperatureBean.setUser_id(dataBean.getUser_id());
                               ArrayList<TemperatureBean.TemperatureDetailsBean> temperatureDetailsBeans = new ArrayList<>();
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
                                   temperatureDetailsBeans.add(new TemperatureBean.TemperatureDetailsBean(index, dateTime, value, type));
                               }
                               //具体详情
                               temperatureBean.setTemperatureDetailsBeans(temperatureDetailsBeans);
                               temperatureDao.insertOrUpdate(dataBean.getUser_id(),temperatureBean);
                           }
                       }
                   }
                   @Override
                   public void done() {
                       SNLog.i("体温网络数据拉取并存储成功");
                       SNEventBus.sendEvent(SNBLEEvent.EVENT_UPDATED_TEMPERATURE_DATA);
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
