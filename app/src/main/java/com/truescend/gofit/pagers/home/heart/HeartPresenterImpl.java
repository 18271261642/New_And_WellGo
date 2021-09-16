package com.truescend.gofit.pagers.home.heart;

import com.sn.app.utils.AppUserUtil;
import com.sn.blesdk.db.data.base.SNBLEDao;
import com.sn.blesdk.db.data.health.heart_rate.HeartRateBean;
import com.sn.blesdk.db.data.health.heart_rate.HeartRateDao;
import com.sn.utils.DateUtil;
import com.sn.utils.task.SNAsyncTask;
import com.sn.utils.task.SNVTaskCallBack;
import com.truescend.gofit.App;
import com.truescend.gofit.R;
import com.truescend.gofit.pagers.base.BasePresenter;
import com.truescend.gofit.pagers.home.heart.bean.HeartRateDetailItem;
import com.truescend.gofit.utils.CalendarUtil;
import com.truescend.gofit.utils.ResUtil;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

/**
 * Author:Created by 泽鑫 on 2017/12/7 16:40.
 * 东芝: 数据查询周月年显示,目前未做实时更新,实时更新放在HomeFragment 以后考虑做不做
 */

public class HeartPresenterImpl extends BasePresenter<IHeartContract.IView> implements IHeartContract.IPresenter {
    private IHeartContract.IView view;
    public static final int TYPE_YEAR = 12;
    public static final int TYPE_WEEK = 7;
    public static final int TYPE_DAY = 1;
    public static final int TYPE_MONTH = 30;

    public HeartPresenterImpl(IHeartContract.IView view) {
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
            private List<HeartRateDetailItem> detailItems = new ArrayList<>();


            @Override
            public void error(Throwable e) {
                super.error(e);
            }

            @Override
            public void run() throws Throwable {
                HeartRateDao heartRateDao = SNBLEDao.get(HeartRateDao.class);
                dateFrom = DateUtil.getDate(DateUtil.YYYY_MM_DD, dateFirst);
                dateTo = DateUtil.getDate(DateUtil.YYYY_MM_DD, dateLast);
                //查询这天的数据
                List<HeartRateBean> heartRateBeans;
                if (dateType == TYPE_DAY) {
                    heartRateBeans = heartRateDao.queryForDay(AppUserUtil.getUser().getUser_id(),dateFrom);
                    count = 1440;
                } else {
                    heartRateBeans = heartRateDao.queryForBetween(AppUserUtil.getUser().getUser_id(), dateFrom, dateTo);
                    //  一共
                    count = DateUtil.getDateOffset(dateLast, dateFirst) + 1;
                }


                data = new ArrayList<>(count);

                //初始化
                for (int i = 0; i < count; i++) {
                    data.add(0);
                }
                int heartRateTotalAll = 0;
                int len = 0;

                int max = -Integer.MAX_VALUE;
                int min = Integer.MAX_VALUE;
                for (HeartRateBean heartRateBean : heartRateBeans) {

                    int index = 0;
                    switch (dateType) {
                        case TYPE_DAY: {
                            //取出详情
                            ArrayList<HeartRateBean.HeartRateDetailsBean> heartRateDetails = heartRateBean.getHeartRateDetails();
                            for (HeartRateBean.HeartRateDetailsBean detail : heartRateDetails) {
                                int heartRateValue = detail.getValue();//心率值
                                if (heartRateValue != 0) {
                                    index = detail.getIndex();//分钟为索引
                                    data.set(index, heartRateValue);
                                    heartRateTotalAll += heartRateValue;
                                     max = Math.max( max, heartRateValue);
                                     min = Math.min( min, heartRateValue);
                                    len++;

                                    //item
                                    DateUtil.HMS hms = DateUtil.convertIndexToTime(index, 1);
                                    detailItems.add(new HeartRateDetailItem(ResUtil.format("%02d:%02d", hms.getHour(), hms.getMinute()), heartRateValue));
                                }
                            }
                        }
                        break;
                        case TYPE_WEEK: {

                            int heartRateValue = heartRateBean.getAvg();//心率值
                            if (heartRateValue != 0) {
                                //取得数据在这7天的哪一天
                                index = DateUtil.getWeekIndex(DateUtil.convertStringToCalendar(DateUtil.YYYY_MM_DD, heartRateBean.getDate()));
                                data.set(index, heartRateValue);
                                heartRateTotalAll += heartRateValue;
                                max = Math.max( max, heartRateValue);
                                min = Math.min( min, heartRateValue);
                                len++;
                                //item
                                String[] weeks = App.getContext().getResources().getStringArray(R.array.week_day);
                                detailItems.add(new HeartRateDetailItem(weeks[index], heartRateValue));
                            }

                        }
                        break;
                        case TYPE_MONTH: {
                            int heartRateValue = heartRateBean.getAvg();//心率值
                            if (heartRateValue != 0) {
                                //取得数据在这7天的哪一天
                                index = DateUtil.getMonthIndex(DateUtil.convertStringToCalendar(DateUtil.YYYY_MM_DD, heartRateBean.getDate()));
                                data.set(index, heartRateValue);
                                heartRateTotalAll += heartRateValue;
                                max = Math.max( max, heartRateValue);
                                min = Math.min( min, heartRateValue);
                                len++;
                                //item
                                String date = DateUtil.getDate(DateUtil.YYYY_MM_DD, ResUtil.getString(R.string.content_day_format), heartRateBean.getDate());
                                detailItems.add(new HeartRateDetailItem(date, heartRateValue));
                            }

                        }
                        break;
                    }
                }
                if (len == 0) {//无详细数据
                    return;
                }
                maxTotal =  ResUtil.formatHtml("%02d <small>%s</small>", max,ResUtil.getString(R.string.unit_heart));
                minTotal =  ResUtil.formatHtml("%02d <small>%s</small>", min,ResUtil.getString(R.string.unit_heart));
                avgTotal =  ResUtil.formatHtml("%02d <small>%s</small>",  heartRateTotalAll / len,ResUtil.getString(R.string.unit_heart));
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