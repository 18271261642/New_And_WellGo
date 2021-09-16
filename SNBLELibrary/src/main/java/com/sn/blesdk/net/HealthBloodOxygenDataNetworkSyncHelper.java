package com.sn.blesdk.net;

import com.sn.app.net.callback.OnResponseListener;
import com.sn.app.utils.AppUserUtil;
import com.sn.blesdk.db.data.base.SNBLEDao;
import com.sn.blesdk.db.data.health.blood_oxygen.BloodOxygenBean;
import com.sn.blesdk.db.data.health.blood_oxygen.BloodOxygenDao;
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
 * 功能:血氧数据上传/下载
 * 注意:如果要大改心率/血氧/血压体检数据结构 和 上传逻辑 请先按心率的先写好 然后复制到此处 再批量替换字符串即可 HeartRate->BloodOxygen
 * 当然 如果有修改 则这三个类都得同步修改
 *
 * {@link HealthBloodOxygenDataNetworkSyncHelper}
 * {@link HealthHeartRateDataNetworkSyncHelper}
 * {@link HealthBloodPressureDataNetworkSyncHelper}
 */

public class HealthBloodOxygenDataNetworkSyncHelper {

    private final static int UPLOAD_TYPE_REAL_TIME = 1;
    private final static int TYPE_BLOOD_OXYGEN = 1;
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
    public static void uploadToServer(List<BloodOxygenBean> uploadList) {
        if(uploadList==null)return;
        JSONArray paramsArr = new JSONArray();
        try {
            //上传到服务器/......

            for (BloodOxygenBean bloodOxygenBean : uploadList) {

                boolean isToday = DateUtil.equalsToday(bloodOxygenBean.getDate());
                if(isToday)continue;//TODO 2018-05-08 改成今天的数据不上传

                //如果数据为0 同时不是今天(则昨天)的数据,说明 这数据是0 不要替换昨天的数据 否则会把昨天的清零
                //而如果是今天的数据 不用管数据是不是0 我们都应该覆盖服务器上的
                if (bloodOxygenBean.getAvg() == 0 && !isToday) {
                    continue;
                }

                JSONObject params = new JSONObject();
                params.put("type", TYPE_BLOOD_OXYGEN /*1：血氧，2：心率，3：血压*/);
                params.put("max", bloodOxygenBean.getMax());//最大值（当天）
                params.put("min", bloodOxygenBean.getMin());//最小值（当天）
                params.put("average", bloodOxygenBean.getAvg());//平均值（当天）
                params.put("mac", bloodOxygenBean.getMac());//手环mac地址 （必传）
                params.put("date", bloodOxygenBean.getDate());//日期(年月日)

//                if (isToday) {//今天的数据 就按今天的来
//                    params.put("time", DateUtil.getCurrentDate(DateUtil.YYYY_MM_DD_HH_MM_SS));//体检开始的时间 （必传,不能有重复）
//                } else {//昨天的数据,就造假 因为时间不能重复
//                    params.put("time", String.format("%s 23:59:59", bloodOxygenBean.getWhichDate()));//体检开始的时间（必传,不能有重复）
//                }

                JSONArray dataArray = new JSONArray();
                ArrayList<BloodOxygenBean.BloodOxygenDetailsBean> bloodOxygenDetails = bloodOxygenBean.getBloodOxygenDetails();
                int count = 0;
                for (BloodOxygenBean.BloodOxygenDetailsBean bloodOxygenDetail : bloodOxygenDetails) {
                    int val = bloodOxygenDetail.getValue();
                    if (val > 0) {
                        count++;
                        JSONObject data = new JSONObject();
                        //转成  时:分(HH:mm)
                        String time = DateUtil.convertStringToNewString(DateUtil.YYYY_MM_DD_HH_MM_SS, DateUtil.HH_MM, bloodOxygenDetail.getDateTime());
                        data.put("time", time);
                        data.put("value", val);
                        data.put("type", bloodOxygenDetail.getType());//type=0 默认手动检测.1为自动检测
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
        DeviceNetReq.getApi().examinationUploadDay(json, TYPE_BLOOD_OXYGEN).enqueue(new OnResponseListener<UploadStatusBean>() {
            @Override
            public void onResponse(final UploadStatusBean body) {
                UploadStatusBean.DataBean data = body.getData();
                int success_num = data.getSuccess_num();
                if (success_num > 0) {
                    SNLog.i("上传血氧数据成功条数:" + success_num + " ,类型:实时数据");
                    if (!IF.isEmpty(data.getSuccess_dates())) {
                        updateUploadStatus(data.getSuccess_dates());
                    }
                } else {
                    SNLog.i("上传血氧数据失败,服务器已经有数据");
                }

            }

            @Override
            public void onFailure(int ret, String msg) {
                SNLog.e("上传血氧数据失败:%d,%s", ret, msg);
            }
        });
    }

    private static void updateUploadStatus(final List<String> success_dates) {
        SNAsyncTask.execute(new SNVTaskCallBack() {
            @Override
            public void run() throws Throwable {
                BloodOxygenDao bloodOxygenDao = SNBLEDao.get(BloodOxygenDao.class);
                for (String date : success_dates) {
                    bloodOxygenDao.updateUploadStatus(AppUserUtil.getUser().getUser_id(), date, true);
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
            call = DeviceNetReq.getApi().examinationDownLoad(beginDate, TYPE_BLOOD_OXYGEN);
        } else {
            call = DeviceNetReq.getApi().examinationDayRangeDownLoad(beginDate, endDate, TYPE_BLOOD_OXYGEN);
        }

        call.enqueue(new OnResponseListener<HealthNetBean>() {
            @Override
            public void onResponse(final HealthNetBean body) throws Throwable {
//                SNLog.i("血氧拉下来:\n" + JsonUtil.toJson(body));
                SNAsyncTask.execute(new SNVTaskCallBack() {

                    @Override
                    public void run() throws Throwable {
                        List<HealthNetBean.DataBean> dataBeans = body.getData();
                        if(dataBeans==null)return;
                        BloodOxygenDao bloodOxygenDao = SNBLEDao.get(BloodOxygenDao.class);
                        for (HealthNetBean.DataBean dataBean : dataBeans) {
                            String data = dataBean.getData();
                            //数据完整校验, 这里要注意 []  代表无数据 不需要解析
                            if ((!IF.isContains(data, "[]")) && IF.isContains(data, "[", "]", "{", "}", "time", "value")) {
                                BloodOxygenBean bloodOxygenBean = new BloodOxygenBean();
                                bloodOxygenBean.setMax(dataBean.getMax());
                                bloodOxygenBean.setMin(dataBean.getMin());
                                bloodOxygenBean.setAvg(dataBean.getAverage());
                                bloodOxygenBean.setUploaded(true);
                                bloodOxygenBean.setDate(dataBean.getDate());
                                bloodOxygenBean.setMac(dataBean.getMac());
                                bloodOxygenBean.setUser_id(dataBean.getUser_id());
                                ArrayList<BloodOxygenBean.BloodOxygenDetailsBean> bloodOxygenDetails = new ArrayList<>();
                                //解析时间在1440中对应的值 并转换为数据库对象
                                JSONArray jsonArray = new JSONArray(data);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    String dateTime = String.format(Locale.ENGLISH, "%s %s:00", dataBean.getDate(), jsonObject.optString("time"));
                                    Calendar calendar = DateUtil.convertStringToCalendar(DateUtil.YYYY_MM_DD_HH_MM_SS, dateTime);
                                    int value = jsonObject.optInt("value");
                                    int type = jsonObject.optInt("type");
                                    int index = DateUtil.convertTimeToIndex(calendar, 1);
                                    bloodOxygenDetails.add(new BloodOxygenBean.BloodOxygenDetailsBean(index, dateTime, value,type ));
                                }
                                //具体详情
                                bloodOxygenBean.setBloodOxygenDetails(bloodOxygenDetails);
                                bloodOxygenDao.insertOrUpdate(dataBean.getUser_id(), bloodOxygenBean);
                            }
                        }
                    }

                    @Override
                    public void done() {
                        SNLog.i("血氧网络数据拉取并存储成功");
                        SNEventBus.sendEvent(SNBLEEvent.EVENT_UPDATED_BLOOD_OXYGEN_DATA);

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
