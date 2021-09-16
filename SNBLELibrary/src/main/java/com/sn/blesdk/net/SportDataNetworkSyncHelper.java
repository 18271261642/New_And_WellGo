package com.sn.blesdk.net;

import com.sn.app.net.callback.OnResponseListener;
import com.sn.app.utils.AppUserUtil;
import com.sn.blesdk.control.SportDataDecodeHelper;
import com.sn.blesdk.db.data.base.SNBLEDao;
import com.sn.blesdk.db.data.sport.SportBean;
import com.sn.blesdk.db.data.sport.SportBean.CalorieBean;
import com.sn.blesdk.db.data.sport.SportBean.DistanceBean;
import com.sn.blesdk.db.data.sport.SportBean.StepBean;
import com.sn.blesdk.db.data.sport.SportDao;
import com.sn.blesdk.net.bean.SportNetBean;
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

import retrofit2.Call;

/**
 * 作者:东芝(2018/1/21).
 * 功能:运动数据上传/下载
 */

public class SportDataNetworkSyncHelper {

    ////////////////////////////////////////////////////////////////////////////////////////
    //--------------------------------------上传-----------------------------------
    ////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 上传到服务器
     *
     * @param uploadList
     */
    public static void uploadToServer(List<SportBean> uploadList) {
        if(uploadList==null)return;

        JSONArray paramsArr = new JSONArray();
        for (SportBean sportBean : uploadList) {
            boolean isToday = DateUtil.equalsToday(sportBean.getDate());
            if(isToday)continue;//TODO 2018-05-08 改成今天的数据不上传
            //如果步数为0 同时不是今天(则昨天)的数据,说明 这数据是0 不要替换昨天的数据 否则会把昨天的清零
            //而如果是今天的数据 不用管数据是不是0 我们都应该覆盖服务器上的
            if (sportBean.getStepTotal() == 0 && !isToday) {
                continue;
            }
            addSportJson(paramsArr, sportBean);
        }
        //如果没有任何要传的数据 则不请求上传, 减少服务器压力和用户流量
        if (paramsArr.length() == 0) {
            return;
        }
        String json = paramsArr.toString();
        DeviceNetReq.getApi().sportUpload(json).enqueue(new OnResponseListener<UploadStatusBean>() {
            @Override
            public void onResponse(final UploadStatusBean body) {
                UploadStatusBean.DataBean data = body.getData();
                int success_num = data.getSuccess_num();
                if (success_num > 0) {
                    SNLog.i("上传运动数据成功条数:" + success_num);
                    if(!IF.isEmpty(data.getSuccess_dates())) {
                        updateUploadStatus(data.getSuccess_dates());
                    }
                } else {
                    SNLog.i("上传运动数据失败,服务器已经有数据");
                }
            }

            @Override
            public void onFailure(int ret, String msg) {
                SNLog.e("上传运动数据失败:%d,%s", ret, msg);
            }
        });
    }

    private static void updateUploadStatus(final List<String> success_dates) {
        SNAsyncTask.execute(new SNVTaskCallBack() {
            @Override
            public void run() throws Throwable {
                SportDao sportDao = SNBLEDao.get(SportDao.class);
                for (String date : success_dates) {
                    sportDao.updateUploadStatus(AppUserUtil.getUser().getUser_id(), date, true);
                }
            }
        });
    }

    private static void addSportJson(JSONArray paramsArr, SportBean sportBean) {

        JSONObject params = new JSONObject();
        try {
            params.put("mac", sportBean.getMac());//手环mac地址 （必传）
            params.put("date", sportBean.getDate());//日期(年月日) （必传）
            params.put("duration", 0);//运动时长
            params.put("step", sportBean.getStepTotal());//当日总步数
            params.put("distance", sportBean.getDistanceTotal());//当日总距离
            params.put("calory", sportBean.getCalorieTotal());//当日总卡路里

            JSONObject data = new JSONObject();
            data.put("cal", convertToJsonArrays(sportBean.getCalories()));
            data.put("step", convertToJsonArrays(sportBean.getSteps()));
            data.put("distance", convertToJsonArrays(sportBean.getDistances()));
            params.put("data", data.toString());
            paramsArr.put(params);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 通用 数据库的运动数据详情转 服务器的数据jsonArrays
     *
     * @param list
     * @return
     */
    private static JSONArray convertToJsonArrays(List<? extends SportBean.AbsSportBean> list) {
        JSONArray newJsonArray = new JSONArray();
        for (SportBean.AbsSportBean bean : list) {
            newJsonArray.put(bean.getValue());
        }
        return newJsonArray;
    }

    /**
     * 通用 服务器的数据jsonArrays转 List<Integer> 后的 运动数据详情 转 成数据库的 List<AbsSportBean> 数据
     *
     * @param date
     * @param list
     * @param <T>
     * @return
     */
    private static <T extends SportBean.AbsSportBean> ArrayList<T> convertToSportBeanList(String date, List<Integer> list, Class<T> cls) {
        ArrayList<T> beans = new ArrayList<>();
        try {
            int size = list.size();
            Calendar calendar = DateUtil.convertStringToCalendar(DateUtil.YYYY_MM_DD, date);
            for (int i = 0; i < size; i++) {
                //通过该日期 转换取得 每个index的时间
                DateUtil.HMS hms = DateUtil.convertIndexToTime(i, SportDataDecodeHelper.EVERY_MINUTES);
                calendar.set(Calendar.HOUR_OF_DAY, hms.getHour());
                calendar.set(Calendar.MINUTE, hms.getMinute());
                calendar.set(Calendar.SECOND, hms.getSecond());
                String dateTime = DateUtil.getDate(DateUtil.YYYY_MM_DD_HH_MM_SS, calendar);
                SportBean.AbsSportBean bean;
                if (cls == StepBean.class) {
                    bean = new StepBean(i, dateTime, list.get(i));
                } else if (cls == CalorieBean.class) {
                    bean = new CalorieBean(i, dateTime, list.get(i));
                } else if (cls == DistanceBean.class) {
                    bean = new DistanceBean(i, dateTime, list.get(i));
                } else {
                    throw new IllegalArgumentException("只能传AbsSportBean 的子类");
                }
                beans.add((T) bean);//警告请无视
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return beans;
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
        Call<SportNetBean> call;
        //如果是只查当天的的数据
        if (beginDate.equalsIgnoreCase(endDate)) {
            call =  DeviceNetReq.getApi().sportDownLoad(beginDate);
        } else {
            call =  DeviceNetReq.getApi().sportDayRangeDownLoad(beginDate, endDate);
        }

        call.enqueue(new OnResponseListener<SportNetBean>() {
            @Override
            public void onResponse(final SportNetBean body) throws Throwable {
//                String s = JsonUtil.toJson(body);
//                SNLog.i("运动拉下来:\n" + s);
                SNAsyncTask.execute(new SNVTaskCallBack() {
                    @Override
                    public void run() throws Throwable {
                        List<SportNetBean.DataBean> dataBeans = body.getData();
                        if(dataBeans==null)return;
                        SportDao sportDao = SNBLEDao.get(SportDao.class);
                        for (SportNetBean.DataBean bean : dataBeans) {
                            SportBean sportBean = new SportBean();
                            sportBean.setMac(bean.getMac());
                            sportBean.setDate(bean.getDate());
                            //既然服务器都有 那么肯定是已上传的
                            sportBean.setUploaded(true);
                            sportBean.setUser_id(bean.getUser_id());
                            sportBean.setStepTotal(bean.getStep());
                            sportBean.setCalorieTotal(bean.getCalory());
                            sportBean.setDistanceTotal(bean.getDistance());
                            sportBean.setStepTarget(bean.getTarget_step());
                            SportNetBean.DataBean.DetailedBean detailedBean = bean.getData();
                            if (detailedBean != null) {
                                sportBean.setSteps(convertToSportBeanList(bean.getDate(), detailedBean.getStep(), StepBean.class));
                                sportBean.setCalories(convertToSportBeanList(bean.getDate(), detailedBean.getCal(), CalorieBean.class));
                                sportBean.setDistances(convertToSportBeanList(bean.getDate(), detailedBean.getDistance(), DistanceBean.class));
                            } else {
                                //TODO 这可能是旧版app 没传 步数详情.
                                //sportBean.setSteps();
                                //sportBean.setCalories();
                                //sportBean.setDistances();
                            }

                            sportDao.insertOrUpdate(bean.getUser_id(), sportBean);
                        }

                    }


                    @Override
                    public void done() {
                        SNLog.i("运动网络数据拉取并存储成功");
                        SNEventBus.sendEvent(SNBLEEvent.EVENT_UPDATED_SPORT_DATA);

                    }

                    @Override
                    public void error(Throwable e) {
                        super.error(e);
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
