package com.sn.blesdk.net;

import com.dz.blesdk.utils.BLELog;
import com.sn.app.net.callback.OnResponseListener;
import com.sn.app.utils.AppUserUtil;
import com.sn.blesdk.db.data.base.SNBLEDao;
import com.sn.blesdk.db.data.sleep.SleepBean;
import com.sn.blesdk.db.data.sleep.SleepBean.SleepDetailsBean;
import com.sn.blesdk.db.data.sleep.SleepDao;
import com.sn.blesdk.net.bean.SleepNetBean;
import com.sn.blesdk.net.bean.UploadStatusBean;
import com.sn.blesdk.utils.DataAnalysisUtil;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;

/**
 * 作者:东芝(2018/1/26).
 * 功能:睡眠数据上传/下载
 */

public class SleepDataNetworkSyncHelper {

    ////////////////////////////////////////////////////////////////////////////////////////
    //--------------------------------------上传-----------------------------------
    ////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 上传到服务器
     *
     * @param uploadList
     */
    public static void uploadToServer(List<SleepBean> uploadList) {
        if(uploadList==null)return;
        JSONArray paramsArr = new JSONArray();
        for (SleepBean sleepBean : uploadList) {
            boolean isToday = DateUtil.equalsToday(sleepBean.getDate());
            if(isToday)continue;//TODO 2018-05-08 改成今天的数据不上传
            //如果数据为0 同时不是今天(则昨天)的数据,说明 这数据是0 不要替换昨天的数据 否则会把昨天的清零
            //而如果是今天的数据 不用管数据是不是0 我们都应该覆盖服务器上的
            int totalTime = sleepBean.getSoberTotal() + sleepBean.getDeepTotal() + sleepBean.getLightTotal();
            if (totalTime == 0 && !isToday) {
                continue;
            }
            if (totalTime > 1440) {
                BLELog.w("睡眠:数据异常,不提交到服务器");
                continue;
            }

            addSleepJson(paramsArr, sleepBean);
        }
        //如果没有任何要传的数据 则不请求上传, 减少服务器压力和用户流量
        if (paramsArr.length() == 0) {
            return;
        }
        String json = paramsArr.toString();
        DeviceNetReq.getApi().sleepUpload(json).enqueue(new OnResponseListener<UploadStatusBean>() {
            @Override
            public void onResponse(final UploadStatusBean body) {
                UploadStatusBean.DataBean data = body.getData();
                int success_num = data.getSuccess_num();
                if (success_num > 0) {
                    SNLog.i("上传睡眠数据成功条数:" + success_num);
                    if(!IF.isEmpty(data.getSuccess_dates())) {
                        updateUploadStatus(data.getSuccess_dates());
                    }
                } else {
                    SNLog.i("上传睡眠数据失败,服务器已经有数据");
                }
            }

            @Override
            public void onFailure(int ret, String msg) {
                SNLog.e("上传睡眠数据失败:%d,%s", ret, msg);
            }
        });
    }

    private static void updateUploadStatus(final List<String> success_dates) {
        SNAsyncTask.execute(new SNVTaskCallBack() {
            @Override
            public void run() throws Throwable {
                SleepDao sleepDao = SNBLEDao.get(SleepDao.class);
                for (String date : success_dates) {
                    sleepDao.updateUploadStatus(AppUserUtil.getUser().getUser_id(), date, true);
                }
            }
        });
    }

    /**
     * @param paramsArr
     */
    private static void addSleepJson(JSONArray paramsArr, SleepBean sleepBean) {

        JSONObject params = new JSONObject();
        try {

            ArrayList<SleepBean.SleepDetailsBean> sleepDetailsBeans = sleepBean.getSleepDetailsBeans();
            if (!IF.isEmpty(sleepDetailsBeans)) {

                params.put("mac", sleepBean.getMac());//手环mac地址 （必传）
                params.put("date", sleepBean.getDate());//日期(年月日) （必传）
                params.put("duration", sleepBean.getDeepTotal() + sleepBean.getLightTotal() + sleepBean.getSoberTotal());// 时长
                params.put("deep", sleepBean.getDeepTotal());//总深睡时长
                params.put("light", sleepBean.getLightTotal());//总浅睡时长
                params.put("sober", sleepBean.getSoberTotal());//总清醒时长


                JSONArray dataJson = new JSONArray();
                //sleepData 字段的数据
                for (SleepBean.SleepDetailsBean sleepDetailsBean : sleepDetailsBeans) {

                    JSONObject sleepDataJson = new JSONObject();
                    sleepDataJson.put("startTime", sleepDetailsBean.getBeginDateTime());
                    sleepDataJson.put("endTime", sleepDetailsBean.getEndDateTime());
                    JSONArray arrays = new JSONArray();
                    List<SleepBean.SleepDetailsBean.SleepData> sleepData = sleepDetailsBean.getSleepData();
                    if (!IF.isEmpty(sleepData)) {
                        for (SleepBean.SleepDetailsBean.SleepData sleepDatum : sleepData) {
                            arrays.put(String.valueOf(sleepDatum.getValue()));
                        }
                    }
                    sleepDataJson.put("sleepData", arrays);
                    dataJson.put(sleepDataJson);
                }

                params.put("data", dataJson.toString());
                paramsArr.put(params);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


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
        Call<SleepNetBean> call;
        //如果是只查当天的的数据
        if (beginDate.equalsIgnoreCase(endDate)) {
            call =  DeviceNetReq.getApi().sleepDownLoad(beginDate);
        } else {
            call =  DeviceNetReq.getApi().sleepDayRangeDownLoad(beginDate, endDate);
        }

        call.enqueue(new OnResponseListener<SleepNetBean>() {
            @Override
            public void onResponse(final SleepNetBean body) throws Throwable {
//                SNLog.i("睡眠拉下来:\n" + JsonUtil.toJson(body));
                SNAsyncTask.execute(new SNVTaskCallBack() {
                    @Override
                    public void run() throws Throwable {
                        List<SleepNetBean.DataBean> dataBeans = body.getData();
                        if(dataBeans==null)return;
                        SleepDao sleepDao = SNBLEDao.get(SleepDao.class);
                        for (SleepNetBean.DataBean bean : dataBeans) {
                            SleepBean sleepBean = new SleepBean();

                            sleepBean.setMac(bean.getMac());
                            sleepBean.setDate(bean.getDate());
                            //既然服务器都有 那么肯定是已上传的
                            sleepBean.setUploaded(true);
                            sleepBean.setUser_id(bean.getUser_id());

                            sleepBean.setDeepTotal(bean.getDeep());
                            sleepBean.setLightTotal(bean.getLight());
                            sleepBean.setSoberTotal(bean.getSober());
                            ArrayList<SleepDetailsBean> sleepDetailsBeans = new ArrayList<>();
                            {
                                List<SleepNetBean.DataBean.DataDataBean> beanData = bean.getData();
                                for (SleepNetBean.DataBean.DataDataBean beanDatum : beanData) {
                                    sleepDetailsBeans.add(getSleepDetailsBean(beanDatum));
                                }
                            }
                            //排序
                            Collections.sort(sleepDetailsBeans, new Comparator<SleepDetailsBean>() {
                                @Override
                                public int compare(SleepBean.SleepDetailsBean o1, SleepBean.SleepDetailsBean o2) {
                                    return o1.getBeginDateTime().compareToIgnoreCase(o2.getBeginDateTime());
                                }
                            });
                            sleepBean.setSleepDetailsBeans(sleepDetailsBeans);

                            int totalTime = sleepBean.getSoberTotal() + sleepBean.getDeepTotal() + sleepBean.getLightTotal();
                            if (totalTime > 1440) {
                                BLELog.w("睡眠拉下来:数据异常,超过了24小时,不存储");
                                continue;
                            }

                            sleepDao.insertOrUpdate(bean.getUser_id(), sleepBean);
                        }
                    }

                    @Override
                    public void done() {
                        SNLog.i("睡眠网络数据拉取并存储成功");
                        SNEventBus.sendEvent(SNBLEEvent.EVENT_UPDATED_SLEEP_DATA);
                    }
                });
            }

            @Override
            public void onFailure(int ret, String msg) {

            }
        });
    }

    private static SleepDetailsBean getSleepDetailsBean(SleepNetBean.DataBean.DataDataBean beanDatum) {
        int mDeepSleepMinutes = 0;
        int mLightSleepMinutes = 0;
        int mSoberSleepMinutes = 0;

        ArrayList<SleepDetailsBean.SleepData> sleepDatas = new ArrayList<>();

        List<String> sleepDataString = beanDatum.getSleepData();
        for (String src16BitStr : sleepDataString) {
            SleepDetailsBean.SleepData sleepData = new SleepDetailsBean.SleepData();
            //原始值
            int src16Bit = Integer.parseInt(src16BitStr);

            //睡眠状态
            int sleepStatus = DataAnalysisUtil.getSleepStatus(src16Bit);
            //睡眠分钟数
            int sleepMinutes = DataAnalysisUtil.getSleepMinutes(src16Bit);

            //根据睡眠状态来累计 对应的总分钟数
            switch (sleepStatus) {
                case 0://浅睡
                    mLightSleepMinutes += sleepMinutes;
                    break;
                case 1://深睡
                    mDeepSleepMinutes += sleepMinutes;
                    break;
                case 2://醒着
                    mSoberSleepMinutes += sleepMinutes;
                    break;
            }
            sleepData.setMinutes(sleepMinutes);
            sleepData.setStatus(sleepStatus);
            sleepData.setValue(src16Bit);
            sleepDatas.add(sleepData);
        }

        SleepDetailsBean sleepDetailsBean = new SleepDetailsBean();
        sleepDetailsBean.setBeginDateTime(beanDatum.getStartTime());
        sleepDetailsBean.setEndDateTime(beanDatum.getEndTime());
        sleepDetailsBean.setDeep(mDeepSleepMinutes);
        sleepDetailsBean.setLight(mLightSleepMinutes);
        sleepDetailsBean.setSober(mSoberSleepMinutes);
        sleepDetailsBean.setSleepData(sleepDatas);
        return sleepDetailsBean;
    }

    ////////////////////////////////////////////////////////////////////////////////////////
    //--------------------------------------下载-----------------------------------
    ////////////////////////////////////////////////////////////////////////////////////////


}
