package com.truescend.gofit.pagers.home.pressure;

import com.sn.app.utils.AppUserUtil;
import com.sn.blesdk.db.data.base.SNBLEDao;
import com.sn.blesdk.db.data.health.blood_pressure.BloodPressureBean;
import com.sn.blesdk.db.data.health.blood_pressure.BloodPressureDao;
import com.sn.utils.DateUtil;
import com.sn.utils.task.SNAsyncTask;
import com.sn.utils.task.SNVTaskCallBack;
import com.truescend.gofit.App;
import com.truescend.gofit.R;
import com.truescend.gofit.pagers.base.BasePresenter;
import com.truescend.gofit.pagers.home.pressure.bean.BloodPressureDetailItem;
import com.truescend.gofit.utils.CalendarUtil;
import com.truescend.gofit.utils.ResUtil;
import com.truescend.gofit.views.BloodPressureChartView;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

/**
 * Author:Created by 泽鑫 on 2017/12/7 18:21.
 * 东芝:
 * 数据查询周月年显示,目前未做实时更新,实时更新放在HomeFragment 以后考虑做不做
 * 这里的代码 和心率的一样 如有修改 请也修改心率
 */

public class BloodPressurePresenterImpl extends BasePresenter<IBloodPressureContract.IView> implements IBloodPressureContract.IPresenter {
    private IBloodPressureContract.IView view;
    public static final int TYPE_YEAR = 12;
    public static final int TYPE_WEEK = 7;
    public static final int TYPE_DAY = 1;
    public static final int TYPE_MONTH = 30;


    public BloodPressurePresenterImpl(IBloodPressureContract.IView view) {
        this.view = view;
    }


    @Override
    public void requestLoadTodayChart(final Calendar calendar) {
        requestQueryForBetweenDate(TYPE_DAY, calendar, calendar);
    }

    @Override
    public void requestLoadWeekChart(Calendar calendar) {
        final Calendar dateFirst = DateUtil.getWeekFirstDate(calendar);
        final Calendar dateLast = DateUtil.getWeekLastDate(calendar);
        requestQueryForBetweenDate(TYPE_WEEK, dateFirst, dateLast);
    }

    @Override
    public void requestLoadMonthChart(Calendar calendar) {
        Calendar dateFirst = DateUtil.getMonthFirstDate(calendar);
        Calendar dateLast = DateUtil.getMonthLastDate(calendar);
        requestQueryForBetweenDate(TYPE_MONTH, dateFirst, dateLast);
    }


    private void requestQueryForBetweenDate(final int dateType, final Calendar dateFirst, final Calendar dateLast) {
        SNAsyncTask.execute(new SNVTaskCallBack() {
            private CharSequence mBloodPressureDiastolic="--";//舒张压 (值较小)
            private CharSequence mBloodPressureSystolic ="--";//收缩压 (值较大)
            private int count = 0;
            private List<BloodPressureChartView.BloodPressureItem> data;
            private String dateFrom;
            private String dateTo;
            private List<BloodPressureDetailItem> detailItems = new ArrayList<>();

            @Override
            public void run() throws Throwable {
                BloodPressureDao bloodPressureDao = SNBLEDao.get(BloodPressureDao.class);
                dateFrom = DateUtil.getDate(DateUtil.YYYY_MM_DD, dateFirst);
                dateTo = DateUtil.getDate(DateUtil.YYYY_MM_DD, dateLast);
                //查询这天的数据
                List<BloodPressureBean> bloodPressureBeans;
                if (dateType == TYPE_DAY) {
                    bloodPressureBeans = bloodPressureDao.queryForDay(AppUserUtil.getUser().getUser_id(), dateFrom);
                    count = 1440;
                } else {
                    bloodPressureBeans = bloodPressureDao.queryForBetween(AppUserUtil.getUser().getUser_id(), dateFrom, dateTo);
                    //  一共
                    count = DateUtil.getDateOffset(dateLast, dateFirst) + 1;
                }


                data = new ArrayList<>(count);

                //初始化
                for (int i = 0; i < count; i++) {
                    data.add(new BloodPressureChartView.BloodPressureItem(0, 0));
                }

                int diastolicTotalAll = 0;
                int systolicTotalAll = 0;
                int len = 0;

                for (BloodPressureBean bloodPressureBean : bloodPressureBeans) {

                    int index = 0;
                    switch (dateType) {
                        case TYPE_DAY: {
                            //取出详情
                            ArrayList<BloodPressureBean.BloodPressureDetailsBean> heartRateDetails = bloodPressureBean.getBloodPressureDetails();
                            for (BloodPressureBean.BloodPressureDetailsBean detail : heartRateDetails) {
                                int diastolic = detail.getBloodDiastolic();
                                int systolic = detail.getBloodSystolic();
                                if (diastolic != 0 && systolic != 0) {
                                    index = detail.getIndex();//分钟为索引
                                    data.set(index, new BloodPressureChartView.BloodPressureItem(diastolic, systolic));
                                    diastolicTotalAll += diastolic;
                                    systolicTotalAll += systolic;
                                    len++;

                                    //item
                                    DateUtil.HMS hms = DateUtil.convertIndexToTime(index, 1);
                                    detailItems.add(new BloodPressureDetailItem(ResUtil.format("%02d:%02d", hms.getHour(), hms.getMinute()), diastolic, systolic));
                                }
                            }
                        }
                        break;
                        case TYPE_WEEK: {

                            int diastolic = bloodPressureBean.getBloodDiastolic();
                            int systolic = bloodPressureBean.getBloodSystolic();
                            if (diastolic != 0 && systolic != 0) {
                                //取得数据在这7天的哪一天
                                index = DateUtil.getWeekIndex(DateUtil.convertStringToCalendar(DateUtil.YYYY_MM_DD, bloodPressureBean.getDate()));
                                data.set(index, new BloodPressureChartView.BloodPressureItem(diastolic, systolic));

                                diastolicTotalAll += diastolic;
                                systolicTotalAll += systolic;
                                len++;

                                //item
                                String[] weeks = App.getContext().getResources().getStringArray(R.array.week_day);
                                detailItems.add(new BloodPressureDetailItem(weeks[index], diastolic, systolic));
                            }

                        }
                        break;
                        case TYPE_MONTH: {
                            int diastolic = bloodPressureBean.getBloodDiastolic();
                            int systolic = bloodPressureBean.getBloodSystolic();
                            if (diastolic != 0 && systolic != 0) {
                                //取得数据在这7天的哪一天
                                index = DateUtil.getMonthIndex(DateUtil.convertStringToCalendar(DateUtil.YYYY_MM_DD, bloodPressureBean.getDate()));
                                data.set(index, new BloodPressureChartView.BloodPressureItem(diastolic, systolic));

                                diastolicTotalAll += diastolic;
                                systolicTotalAll += systolic;
                                len++;

                                //item
                                String date = DateUtil.getDate(DateUtil.YYYY_MM_DD, ResUtil.getString(R.string.content_day_format), bloodPressureBean.getDate());
                                detailItems.add(new BloodPressureDetailItem(date, diastolic, systolic));
                            }

                        }
                        break;
                    }
                }
                if (len == 0) {//无详细数据
                    return;
                }

                mBloodPressureDiastolic =  ResUtil.formatHtml("%02d <small>%s</small>", diastolicTotalAll / len,ResUtil.getString(R.string.unit_pressure));
                mBloodPressureSystolic =  ResUtil.formatHtml("%02d <small>%s</small>", systolicTotalAll / len,ResUtil.getString(R.string.unit_pressure));
            }

            @Override
            public void done() {
                super.done();
                if(!isUIEnable()){
                    return;
                }

                switch (dateType) {
                    case TYPE_WEEK:
                        try {
                            String mFromDate = DateUtil.convertStringToNewString(DateUtil.YYYY_MM_DD, CalendarUtil.YYYYMMDD, dateFrom);
                            String mToDate = DateUtil.convertStringToNewString(DateUtil.YYYY_MM_DD, CalendarUtil.YYYYMMDD, dateTo);
                            view.onUpdateDateRange(String.format("%s-%s", mFromDate, mToDate));
                        } catch (ParseException ignored) {
                        }
                        view.onUpdateWeekChartData(data);
                        break;
                    case TYPE_MONTH:
                        try {
                            String mFromDate = DateUtil.convertStringToNewString(DateUtil.YYYY_MM_DD, CalendarUtil.YYYYMMDD, dateFrom);
                            String mToDate = DateUtil.convertStringToNewString(DateUtil.YYYY_MM_DD, CalendarUtil.YYYYMMDD, dateTo);
                            view.onUpdateDateRange(String.format("%s-%s", mFromDate, mToDate));
                        } catch (ParseException ignored) {
                        }
                        view.onUpdateMonthChartData(data);
                        break;
                    case TYPE_DAY:
                        view.onUpdateDateRange(DateUtil.getDate(CalendarUtil.YYYYMMDD, App.getSelectedCalendar()));
                        Collections.reverse(detailItems);
                        view.onUpdateTodayChartData(data);
                        break;
                }

                view.onUpdateStatisticsData(mBloodPressureDiastolic, mBloodPressureSystolic);

                view.onUpdateDetailListData(detailItems);

            }
        });

    }
}
