package com.truescend.gofit.pagers.home.oxygen;

import com.sn.app.utils.AppUserUtil;
import com.sn.blesdk.db.data.base.SNBLEDao;
import com.sn.blesdk.db.data.health.blood_oxygen.BloodOxygenBean;
import com.sn.blesdk.db.data.health.blood_oxygen.BloodOxygenDao;
import com.sn.utils.DateUtil;
import com.sn.utils.task.SNAsyncTask;
import com.sn.utils.task.SNVTaskCallBack;
import com.truescend.gofit.App;
import com.truescend.gofit.R;
import com.truescend.gofit.pagers.base.BasePresenter;
import com.truescend.gofit.pagers.home.oxygen.bean.BloodOxygenDetailItem;
import com.truescend.gofit.utils.CalendarUtil;
import com.truescend.gofit.utils.ResUtil;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

/**
 * Author:Created by 泽鑫 on 2017/12/8 10:23.
 * 东芝:
 * 数据查询周月年显示,目前未做实时更新,实时更新放在HomeFragment 以后考虑做不做
 * 这里的代码 和心率的一样 如有修改 请也修改心率
 */

public class BloodOxygenPresenterImpl extends BasePresenter<IBloodOxygenContract.IView> implements IBloodOxygenContract.IPresenter {
    private IBloodOxygenContract.IView view;
    public static final int TYPE_YEAR = 12;
    public static final int TYPE_WEEK = 7;
    public static final int TYPE_DAY = 1;
    public static final int TYPE_MONTH = 30;

    public BloodOxygenPresenterImpl(IBloodOxygenContract.IView view) {
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
            private int count = 0;
            private List<Integer> data;
            private CharSequence maxTotal = "--";
            private CharSequence avgTotal = "--";
            private CharSequence minTotal = "--";
            private String dateFrom;
            private String dateTo;
            private List<BloodOxygenDetailItem> detailItems = new ArrayList<>();

            @Override
            public void run() throws Throwable {
                BloodOxygenDao bloodOxygenDao = SNBLEDao.get(BloodOxygenDao.class);
                dateFrom = DateUtil.getDate(DateUtil.YYYY_MM_DD, dateFirst);
                dateTo = DateUtil.getDate(DateUtil.YYYY_MM_DD, dateLast);
                //查询这天的数据
                List<BloodOxygenBean> bloodOxygenBeans;
                if (dateType == TYPE_DAY) {
                    bloodOxygenBeans = bloodOxygenDao.queryForDay(AppUserUtil.getUser().getUser_id(),dateFrom);
                    count = 1440;
                } else {
                    bloodOxygenBeans = bloodOxygenDao.queryForBetween(AppUserUtil.getUser().getUser_id(), dateFrom, dateTo);
                    //  一共
                    count = DateUtil.getDateOffset(dateLast, dateFirst) + 1;
                }


                data = new ArrayList<>(count);

                //初始化
                for (int i = 0; i < count; i++) {
                    data.add(0);
                }

                int bloodOxygenTotalAll = 0;
                int len = 0;

                int max = -Integer.MAX_VALUE;
                int min = Integer.MAX_VALUE;

                for (BloodOxygenBean bloodOxygenBean : bloodOxygenBeans) {

                    int index = 0;
                    switch (dateType) {
                        case TYPE_DAY: {
                            //取出详情
                            ArrayList<BloodOxygenBean.BloodOxygenDetailsBean> bloodOxygenDetails = bloodOxygenBean.getBloodOxygenDetails();
                            for (BloodOxygenBean.BloodOxygenDetailsBean detail : bloodOxygenDetails) {
                                int bloodOxygenValue = detail.getValue();//血氧值
                                if (bloodOxygenValue != 0) {
                                    index = detail.getIndex();//分钟为索引
                                    data.set(index, bloodOxygenValue);
                                    bloodOxygenTotalAll += bloodOxygenValue;
                                   max = Math.max( max, bloodOxygenValue);
                                   min = Math.min( min, bloodOxygenValue);
                                    len++;

                                    //item
                                    DateUtil.HMS hms = DateUtil.convertIndexToTime(index, 1);
                                    detailItems.add(new BloodOxygenDetailItem(ResUtil.format("%02d:%02d", hms.getHour(), hms.getMinute()), bloodOxygenValue));
                                }
                            }
                        }
                        break;
                        case TYPE_WEEK: {

                            int bloodOxygenValue = bloodOxygenBean.getAvg();//血氧值
                            if (bloodOxygenValue != 0) {
                                //取得数据在这7天的哪一天
                                index = DateUtil.getWeekIndex(DateUtil.convertStringToCalendar(DateUtil.YYYY_MM_DD, bloodOxygenBean.getDate()));
                                data.set(index, bloodOxygenValue);
                                bloodOxygenTotalAll += bloodOxygenValue;
                                 max = Math.max( max, bloodOxygenValue);
                                 min = Math.min( min, bloodOxygenValue);
                                len++;
                                //item
                                String[] weeks = App.getContext().getResources().getStringArray(R.array.week_day);
                                detailItems.add(new BloodOxygenDetailItem(weeks[index], bloodOxygenValue));
                            }

                        }
                        break;
                        case TYPE_MONTH: {
                            int bloodOxygenValue = bloodOxygenBean.getAvg();//血氧值
                            if (bloodOxygenValue != 0) {
                                //取得数据在这7天的哪一天
                                index = DateUtil.getMonthIndex(DateUtil.convertStringToCalendar(DateUtil.YYYY_MM_DD, bloodOxygenBean.getDate()));
                                data.set(index, bloodOxygenValue);
                                bloodOxygenTotalAll += bloodOxygenValue;
                                max = Math.max( max, bloodOxygenValue);
                                min = Math.min( min, bloodOxygenValue);
                                len++;
                                //item
                                String date = DateUtil.getDate(DateUtil.YYYY_MM_DD, ResUtil.getString(R.string.content_day_format), bloodOxygenBean.getDate());
                                detailItems.add(new BloodOxygenDetailItem(date, bloodOxygenValue));
                            }

                        }
                        break;
                    }
                }
                if (len == 0) {//无详细数据
                    return;
                }

                maxTotal =  ResUtil.formatHtml("%02d <small>%s</small>", max,"%");
                minTotal =  ResUtil.formatHtml("%02d <small>%s</small>", min,"%");
                avgTotal =  ResUtil.formatHtml("%02d <small>%s</small>",  bloodOxygenTotalAll / len,"%");
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
                view.onUpdateStatisticsData(maxTotal, avgTotal, minTotal);


                view.onUpdateDetailListData(detailItems);

            }
        });

    }
}
