package com.truescend.gofit.pagers.friends.data;

import com.sn.app.net.AppNetReq;
import com.sn.app.net.callback.OnResponseListener;
import com.sn.app.net.data.app.bean.FriendInfoBean;
import com.sn.app.net.data.app.bean.StatBean;
import com.sn.utils.DateUtil;
import com.sn.utils.IF;
import com.sn.utils.task.SNAsyncTask;
import com.sn.utils.task.SNVTaskCallBack;
import com.truescend.gofit.pagers.base.BasePresenter;
import com.truescend.gofit.pagers.friends.data.item.SleepDataPickerItem;
import com.truescend.gofit.pagers.friends.data.item.SportDataPickerItem;
import com.truescend.gofit.views.BloodPressureChartView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 作者:东芝(2018/08/09).
 * 功能:好友数据
 */
public class FriendsDataPresenterImpl extends BasePresenter implements IFriendsDataContract.IPresenter {

    private IFriendsDataContract.IView view;

    public FriendsDataPresenterImpl(IFriendsDataContract.IView view) {
        this.view = view;
    }

    @Override
    public void getFriendsInfoResults(int user_id) {
        AppNetReq.getApi().friendHomepage(user_id).enqueue(new OnResponseListener<FriendInfoBean>() {
            @Override
            public void onResponse(FriendInfoBean body) throws Throwable {
                view.onFriendsInfoResults(body.getData());
                handle(body.getData().getStat());
            }

            @Override
            public void onFailure(int ret, String msg) {
                view.onShowTips(msg);
            }
        });

    }

    private void handle(final StatBean stat) {

        ////////////////////////////////////////////////////////////////////////////////////////
        //--------------------------------------最佳xxx-----------------------------------
        ////////////////////////////////////////////////////////////////////////////////////////
        try {
            //最佳单日天数
            view.onUpdateUserBestDays(stat.getMax_step().getDate(), stat.getMax_step().getValue());
        } catch (Exception ignored) {
        }
        try {
            //连续最佳天数
            view.onUpdateUserBestContinuesDays(stat.getFinish_days().getDate(), stat.getFinish_days().getValue());
        } catch (Exception ignored) {
        }
        try {
            //最佳月份
            view.onUpdateUserBestMonths(stat.getMax_month().getDate(), stat.getMax_month().getValue());
        } catch (Exception ignored) {
        }
        try {
            //最佳星期
            view.onUpdateUserBestWeeks(stat.getMax_week().getDate(), stat.getMax_week().getValue());
        } catch (Exception ignored) {
        }

        ////////////////////////////////////////////////////////////////////////////////////////
        //--------------------------------------运动-----------------------------------
        ////////////////////////////////////////////////////////////////////////////////////////
        SNAsyncTask.execute(new SNVTaskCallBack() {
            List<SportDataPickerItem.SportDateItem> sportDateItems = new ArrayList<>();

            @Override
            public void run() throws Throwable {
                sportDateItems.clear();

                List<StatBean.SportHistory> sportHistories = null;
                if(stat!=null) {
                    sportHistories = stat.getSportHistory();
                }

                int showDay = 30;
                Calendar c = DateUtil.getCurrentCalendarBegin();
                c.add(Calendar.DAY_OF_MONTH, -showDay);
                A:
                for (int i = 0; i < showDay; i++) {
                    c.add(Calendar.DAY_OF_MONTH, 1);
                    String date = DateUtil.getDate(DateUtil.YYYY_MM_DD, c);
                    if(sportHistories!=null) {
                        for (StatBean.SportHistory sportHistory : sportHistories) {
                            if (date.equalsIgnoreCase(sportHistory.getDatestring())) {
                                sportDateItems.add(new SportDataPickerItem.SportDateItem(c.getTimeInMillis(), sportHistory.getSteps(), sportHistory.getSteptarget(), sportHistory.getCalors(), sportHistory.getDistances()));
                                continue A;
                            }
                        }
                    }
                    sportDateItems.add(new SportDataPickerItem.SportDateItem(c.getTimeInMillis(), 0, 0, 0, 0));
                }
            }

            @Override
            public void done() {
                if (!isUIEnable()) return;
                view.onSportDateData(sportDateItems);
            }
        });

        ////////////////////////////////////////////////////////////////////////////////////////
        //--------------------------------------睡眠-----------------------------------
        ////////////////////////////////////////////////////////////////////////////////////////

        SNAsyncTask.execute(new SNVTaskCallBack() {
            List<SleepDataPickerItem.SleepDateItem> sleepDateItems = new ArrayList<>();

            @Override
            public void run() throws Throwable {
                sleepDateItems.clear();
                List<StatBean.SleepHistory> sleepHistories = null;
                if(stat!=null) {
                    sleepHistories = stat.getSleepHistory();
                }


                int showDay = 30;
                Calendar c = DateUtil.getCurrentCalendarBegin();
                c.add(Calendar.DAY_OF_MONTH, -showDay);
                A:
                for (int i = 0; i < showDay; i++) {
                    c.add(Calendar.DAY_OF_MONTH, 1);
                    String date = DateUtil.getDate(DateUtil.YYYY_MM_DD, c);

                    if(sleepHistories!=null) {
                        for (StatBean.SleepHistory sleepHistory : sleepHistories) {
                            if (date.equalsIgnoreCase(sleepHistory.getDatestring())) {
                                sleepDateItems.add(new SleepDataPickerItem.SleepDateItem(c.getTimeInMillis(), sleepHistory.getDeeps(), sleepHistory.getLights(), sleepHistory.getSobers()));
                                continue A;
                            }
                        }
                    }
                    sleepDateItems.add(new SleepDataPickerItem.SleepDateItem(c.getTimeInMillis(), 0, 0, 0));
                }
            }

            @Override
            public void done() {
                if (!isUIEnable()) return;
                view.onSleepDateData(sleepDateItems);
            }
        });

        ////////////////////////////////////////////////////////////////////////////////////////
        //--------------------------------------心率-----------------------------------
        ////////////////////////////////////////////////////////////////////////////////////////

        SNAsyncTask.execute(new SNVTaskCallBack() {
            private int mHeartMin;
            private int mHeartMax;
            private int mHeartAve;
            List<Integer> heartRateItems = new ArrayList<>();

            @Override
            public void run() throws Throwable {
                if (stat == null) return;
                heartRateItems.clear();

                final List<StatBean.HeartHistory> heart_historyBean = stat.getHeartHistory();
                if (!heart_historyBean.isEmpty()) {

                    List<StatBean.HeartHistory.HeartdatasBean> heartdatasBeans = null;
                    //找出今天的数据
                    for (StatBean.HeartHistory heartHistory : heart_historyBean) {
                        if (DateUtil.equalsToday(heartHistory.getDatestring())) {
                            heartdatasBeans = heartHistory.getHeartdatas();
                            mHeartAve = heartHistory.getHeartAve();
                            mHeartMax = heartHistory.getHeartMax();
                            mHeartMin = heartHistory.getHeartMIn();
                            break;
                        }
                    }
                    if (heartdatasBeans == null) {
                        return;
                    }
                    int[] count = new int[48];
                    int[] values = new int[48];
                    for (StatBean.HeartHistory.HeartdatasBean heartdatasBean : heartdatasBeans) {
                        DateUtil.HMS hms = DateUtil.getHMSFromString(DateUtil.HH_MM, heartdatasBean.getTime());
                        int index = DateUtil.convertTimeToIndex(hms.getHour(), hms.getMinute(), 30);
                        values[index] += heartdatasBean.getValue();
                        count[index]++;

                    }
                    for (int i = 0; i < values.length; i++) {
                        values[i] = values[i]/(count[i]==0?1:count[i]);
                    }
                    //处理一下,让图形现在独立点时更好看
                    fillSinglePointIfNeed(values);

                    for (int value : values) {
                        heartRateItems.add(value);
                    }
                }
                if(IF.isEmpty(mHeartAve,mHeartMax,mHeartMin)) {
                    mHeartAve = stat.getToday_avg();
                    mHeartMax = stat.getToday_highest();
                    mHeartMin = stat.getToday_lowest();
                }
            }

            @Override
            public void done() {
                if (!isUIEnable()) return;
                view.onHeartRateDateData(heartRateItems,mHeartMax , mHeartAve,mHeartMin );
            }
        });

        ////////////////////////////////////////////////////////////////////////////////////////
        //--------------------------------------血氧-----------------------------------
        ////////////////////////////////////////////////////////////////////////////////////////

        SNAsyncTask.execute(new SNVTaskCallBack() {
            private int mOXAvg;
            List<Integer> bloodOxygenItems = new ArrayList<>();

            @Override
            public void run() throws Throwable {
                if (stat == null) return;
                bloodOxygenItems.clear();

                final List<StatBean.BloodOxygenHistory> bloodOxygenHistories = stat.getBloodOxygenHistory();
                if (!bloodOxygenHistories.isEmpty()) {


                    List<StatBean.BloodOxygenHistory.OxdatasBean> oxdatasBeans = null;
                    for (StatBean.BloodOxygenHistory bloodOxygenHistory : bloodOxygenHistories) {
                        if (DateUtil.equalsToday(bloodOxygenHistory.getDatestring())) {
                            oxdatasBeans = bloodOxygenHistory.getOxdatas();
                            mOXAvg = bloodOxygenHistory.getOxAve();
                            break;
                        }
                    }
                    if (oxdatasBeans == null) {
                        return;
                    }

                    int[] count = new int[48];
                    int[] values = new int[48];
                    for (StatBean.BloodOxygenHistory.OxdatasBean oxdatasBean : oxdatasBeans) {
                        DateUtil.HMS hms = DateUtil.getHMSFromString(DateUtil.HH_MM, oxdatasBean.getTime());
                        int index = DateUtil.convertTimeToIndex(hms.getHour(), hms.getMinute(), 30);
                        values[index] += oxdatasBean.getValue();
                        count[index]++;
                    }
                    for (int i = 0; i < values.length; i++) {
                        values[i] = values[i]/(count[i]==0?1:count[i]);
                    }
                    //处理一下,让图形现在独立点时更好看
                    fillSinglePointIfNeed(values);
                    for (int value : values) {
                        bloodOxygenItems.add(value);
                    }
                }
                if(IF.isEmpty(mOXAvg)) {
                    mOXAvg = stat.getToday_ox_avg();
                }
            }

            @Override
            public void done() {
                if (!isUIEnable()) return;
                view.onBloodOxygenDateData(bloodOxygenItems,mOXAvg );
            }
        });


        ////////////////////////////////////////////////////////////////////////////////////////
        //--------------------------------------血压-----------------------------------
        ////////////////////////////////////////////////////////////////////////////////////////

        SNAsyncTask.execute(new SNVTaskCallBack() {
             int mSBPAvg;
             int mDBPAvg;
            List<BloodPressureChartView.BloodPressureItem> bloodPressureItems = new ArrayList<>();

            @Override
            public void run() throws Throwable {
                if (stat == null) return;
                bloodPressureItems.clear();

                final List<StatBean.BloodPressureHistory> bloodPressureHistories = stat.getBloodPressureHistory();
                if (!bloodPressureHistories.isEmpty()) {

                    List<StatBean.BloodPressureHistory.BpdatasBean> bpdatasBeans  = null;
                    for (StatBean.BloodPressureHistory bloodPressureHistory : bloodPressureHistories) {
                        if (DateUtil.equalsToday(bloodPressureHistory.getDatestring())) {
                            bpdatasBeans = bloodPressureHistory.getBpdatas();
                            mDBPAvg = bloodPressureHistory.getDBPave();
                            mSBPAvg = bloodPressureHistory.getSBPave();
                            break;
                        }
                    }
                    if (bpdatasBeans == null) {
                        return;
                    }
                    int[] count = new int[48];
                    int[] valuesDBP = new int[48] ;
                    int[] valuesSBP = new int[48] ;
                    for (StatBean.BloodPressureHistory.BpdatasBean bpdatasBean : bpdatasBeans) {
                        DateUtil.HMS hms = DateUtil.getHMSFromString(DateUtil.HH_MM, bpdatasBean.getTime());
                        int index = DateUtil.convertTimeToIndex(hms.getHour(), hms.getMinute(), 30);
                        valuesDBP[index]  += bpdatasBean.getDBPValue();
                        valuesSBP[index]  += bpdatasBean.getSBPValue();
                        count[index]++;
                    }
                    for (int i = 0; i < valuesDBP.length; i++) {
                        int diastolic = valuesDBP[i] / (count[i] == 0 ? 1 : count[i]);
                        int systolic = valuesSBP[i] / (count[i] == 0 ? 1 : count[i]);
                        bloodPressureItems.add(new BloodPressureChartView.BloodPressureItem(diastolic, systolic));
                    }
                }
                if(IF.isEmpty(mDBPAvg,mSBPAvg)) {
                    mDBPAvg = stat.getToday_dbp_avg();
                    mSBPAvg = stat.getToday_sbp_avg();
                }
            }

            @Override
            public void done() {
                if (!isUIEnable()) return;
                view.onBloodPressureDateData(bloodPressureItems, mDBPAvg, mSBPAvg);
            }

            @Override
            public void error(Throwable e) {
                super.error(e);
            }
        });
    }

    @Override
    public void setFriendsThumbAction(int user_id, final int type) {
        AppNetReq.getApi().friendThumbAction(user_id, type).enqueue(new OnResponseListener<String>() {
            @Override
            public void onResponse(String body) throws Throwable {
                view.onFriendsThumbAction(type);
            }

            @Override
            public void onFailure(int ret, String msg) {
                view.onShowTips(msg);
            }
        });
    }

    /**
     * 填充独立的点
     * @param values
     */
    private void fillSinglePointIfNeed(int[] values){
        try {
            int size = values.length;
            for (int i = 0; i < size; i++) {
                int cur = values[i];
                if(cur>0&&i>0&&i<size-1){
                    int left = values[i-1];
                    int right = values[i+1];
                    //单独的点
                    if(left==0&&right==0){
                        values[i+1]=cur;
                        i++;
                    }
                }
            }
        } catch (Exception ignored) {
        }
    }
}
