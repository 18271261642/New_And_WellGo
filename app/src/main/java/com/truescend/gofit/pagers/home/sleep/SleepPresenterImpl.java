package com.truescend.gofit.pagers.home.sleep;

import com.sn.app.utils.AppUserUtil;
import com.sn.blesdk.db.data.base.SNBLEDao;
import com.sn.blesdk.db.data.sleep.SleepBean;
import com.sn.blesdk.db.data.sleep.SleepDao;
import com.sn.blesdk.utils.eventbus.SNBLEEvent;
import com.sn.utils.DateUtil;
import com.sn.utils.SNLog;
import com.sn.utils.eventbus.SNEvent;
import com.sn.utils.task.SNAsyncTask;
import com.sn.utils.task.SNVTaskCallBack;
import com.truescend.gofit.App;
import com.truescend.gofit.R;
import com.truescend.gofit.pagers.base.BasePresenter;
import com.truescend.gofit.utils.ResUtil;
import com.truescend.gofit.utils.SleepDataUtil;
import com.truescend.gofit.views.SleepChartView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * 作者:东芝(2017/12/01).
 * 功能:睡眠数据查询解析显示
 */
public class SleepPresenterImpl extends BasePresenter<ISleepContract.IView> implements ISleepContract.IPresenter {
    private ISleepContract.IView view;


    public SleepPresenterImpl(ISleepContract.IView view) {
        this.view = view;
    }

    @Override
    public void requestLoadSleepChart(final Calendar calendar) {


        SNAsyncTask.execute(new SNVTaskCallBack() {
            private String quality = ResUtil.getString(R.string.content_no_data);
            private CharSequence totalTime;
            private CharSequence validTime;

            private int deepPercent;
            private int lightPercent;
            private int soberPercent;
            private String deepTotal;
            private String lightTotal;
            private String soberTotal;
            String deepPercentText;
            String lightPercentText;
            String soberPercentText;

            private List<SleepChartView.SleepItem> sleepItems = new ArrayList<>();

            @Override
            public void prepare() {
                totalTime = ResUtil.formatHtml("%d<small>h</small> %d<small>min</small>", 0, 0);
                validTime = ResUtil.formatHtml("%d<small>h</small> %d<small>min</small>", 0, 0);
                deepPercentText="0.0%";
                lightPercentText="0.0%";
                soberPercentText="0.0%";
            }

            @Override
            public void run() throws Throwable {
                SleepDao sleepDao = SNBLEDao.get(SleepDao.class);
                String date = DateUtil.getDate(DateUtil.YYYY_MM_DD, calendar);
                List<SleepBean> sleepBeans = sleepDao.queryForDay(AppUserUtil.getUser().getUser_id(), date);

                if (!sleepBeans.isEmpty()) {
                    SleepBean sleepBean = sleepBeans.get(0);

                    //总睡眠
                    int sleepTimeTotal = SleepDataUtil.getSleepTotal(sleepBean.getDeepTotal(), sleepBean.getLightTotal(), sleepBean.getSoberTotal());
                    if (sleepTimeTotal > 1440) {
                        SNLog.i("睡眠显示:数据异常,用户睡眠时长为" + (sleepTimeTotal / 60) + "小时,已超过24小时,不显示");
                        return;
                    }
                    int sleepTimeValidTotal = SleepDataUtil.getSleepValidTotal(sleepBean.getDeepTotal(), sleepBean.getLightTotal());

                    quality = SleepDataUtil.getSleepQuality(sleepBean);

                    totalTime = ResUtil.formatHtml("%d<small>h</small> %d<small>min</small>", sleepTimeTotal / 60, sleepTimeTotal % 60);
                    validTime = ResUtil.formatHtml("%d<small>h</small> %d<small>min</small>", sleepTimeValidTotal / 60, sleepTimeValidTotal % 60);

                    deepTotal  = ResUtil.format("%d min",sleepBean.getDeepTotal());
                    lightTotal = ResUtil.format("%d min",sleepBean.getLightTotal());
                    soberTotal = ResUtil.format("%d min", sleepBean.getSoberTotal());

                    float k = 100.0f / sleepTimeTotal;
                    deepPercent = Math.round(k * sleepBean.getDeepTotal());
                    deepPercentText = ResUtil.format("%.1f%%", k * sleepBean.getDeepTotal());

                    lightPercent = Math.round(k * sleepBean.getLightTotal());
                    lightPercentText = ResUtil.format("%.1f%%", k * sleepBean.getLightTotal());

                    soberPercent = Math.round(k * sleepBean.getSoberTotal());
                    soberPercentText = ResUtil.format("%.1f%%", k * sleepBean.getSoberTotal());

                    try {
                        ArrayList<SleepBean.SleepDetailsBean> sleepDetailsBeans = sleepBean.getSleepDetailsBeans();
                        Collections.sort(sleepDetailsBeans, new Comparator<SleepBean.SleepDetailsBean>() {
                            @Override
                            public int compare(SleepBean.SleepDetailsBean o1, SleepBean.SleepDetailsBean o2) {
                                return o1.getBeginDateTime().compareToIgnoreCase(o2.getBeginDateTime());
                            }
                        });

                        Calendar beginDateTimeCalendar = null;
                        int limitDateTimeOffset = 0;
                        long ten_min = 10 * 60 * 1000;//10分钟分隔, 传0为无分隔 //TODO 这个10分钟可能会导致画出来的图 出现不同的总时长
                        int size = sleepDetailsBeans.size();
                        for (int i = 0; i < size; i++) {
                            SleepBean.SleepDetailsBean bean = sleepDetailsBeans.get(i);//  分段遍历
                            if (beginDateTimeCalendar != null) {
                                Calendar nextBeginDateTimeCalendar = DateUtil.convertStringToCalendar(DateUtil.YYYY_MM_DD_HH_MM_SS, bean.getBeginDateTime());
                                long beginDateTime = beginDateTimeCalendar.getTimeInMillis();
                                long endDateTime = nextBeginDateTimeCalendar.getTimeInMillis();
                                //每段 加一个假的 分隔
                                limitDateTimeOffset = (int) ((endDateTime - beginDateTime) - (ten_min));
                                sleepItems.add(new SleepChartView.SleepItem(SleepChartView.SleepItem.ITEM_LIMIT,SleepChartView.SleepItem.DATE_CENTER ,"",beginDateTime, beginDateTime + ten_min));
                            }
                            beginDateTimeCalendar = DateUtil.convertStringToCalendar(DateUtil.YYYY_MM_DD_HH_MM_SS, bean.getBeginDateTime());
                            beginDateTimeCalendar.add(Calendar.MILLISECOND, -limitDateTimeOffset);
                            List<SleepBean.SleepDetailsBean.SleepData> sleepData = bean.getSleepData();
                            int size1 = sleepData.size();
                            for (int i1 = 0; i1 < size1; i1++) {
                                SleepBean.SleepDetailsBean.SleepData sleepDatum = sleepData.get(i1);//  详情遍历
                                //组装 每段数据
                                long beginDateTime = beginDateTimeCalendar.getTimeInMillis();
                                beginDateTimeCalendar.add(Calendar.MINUTE, +sleepDatum.getMinutes());
                                long endDateTime = beginDateTimeCalendar.getTimeInMillis();

                                int dateType = SleepChartView.SleepItem.DATE_CENTER;
                                String dateShowStr = "";
                                if(i1 == 0){
                                    dateType =  SleepChartView.SleepItem.DATE_BEGIN;
//                                    dateShowStr =DateUtil.getDate(DateUtil.HH_MM,beginDateTime);
                                    dateShowStr =DateUtil.convertStringToNewString(DateUtil.YYYY_MM_DD_HH_MM_SS, DateUtil.HH_MM,bean.getBeginDateTime());
                                }else if(i1==size1-1){
                                    dateType =  SleepChartView.SleepItem.DATE_END;
//                                    dateShowStr =DateUtil.getDate(DateUtil.HH_MM,endDateTime);
                                    dateShowStr =DateUtil.convertStringToNewString(DateUtil.YYYY_MM_DD_HH_MM_SS, DateUtil.HH_MM,bean.getEndDateTime());
                                }
                                //183分 391分
                                sleepItems.add(new SleepChartView.SleepItem(sleepDatum.getStatus(), dateType,dateShowStr, beginDateTime, endDateTime));
                            }

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }

            }

            @Override
            public void done() {
                if(!isUIEnable())return;
                view.onUpdateSleepChartData(sleepItems);
                view.onUpdateSleepItemData(totalTime, validTime, quality, deepPercent, deepPercentText, lightPercent, lightPercentText, soberPercent,soberPercentText , deepTotal, lightTotal, soberTotal);
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventReceived(SNEvent event) {
        switch (event.getCode()) {
            case SNBLEEvent.EVENT_UPDATED_SLEEP_DATA:
                //如果 选择日期的是今天 就刷新数据
                if (DateUtil.equalsToday(App.getSelectedCalendar())) {
                    requestLoadSleepChart(App.getSelectedCalendar());
                }
                break;

        }

    }
}
