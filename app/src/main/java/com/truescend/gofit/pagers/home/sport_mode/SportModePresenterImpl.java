package com.truescend.gofit.pagers.home.sport_mode;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import androidx.annotation.IntRange;

import com.sn.app.utils.AppUserUtil;
import com.sn.blesdk.db.data.base.SNBLEDao;
import com.sn.blesdk.db.data.sport_mode.SportModeBean;
import com.sn.blesdk.db.data.sport_mode.SportModeDao;
import com.sn.utils.DateUtil;
import com.sn.utils.task.SNAsyncTask;
import com.sn.utils.task.SNVTaskCallBack;
import com.truescend.gofit.App;
import com.truescend.gofit.R;
import com.truescend.gofit.pagers.base.BasePresenter;
import com.truescend.gofit.pagers.home.sport_mode.bean.SportModeDetailItem;
import com.truescend.gofit.utils.CalendarUtil;
import com.truescend.gofit.utils.ResUtil;
import com.truescend.gofit.utils.SportModeTypeUtil;
import com.truescend.gofit.views.PieChartView;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 作者:东芝(2019/06/01).
 * 功能:运动模式
 */
public class SportModePresenterImpl extends BasePresenter<ISportModeContract.IView> implements ISportModeContract.IPresenter {
    private ISportModeContract.IView view;
    public static final int TYPE_WEEK = 7;
    public static final int TYPE_MONTH = 30;
    public static final int TYPE_MONTH_RANGE = -1;
    private int statisticalType;
    private int currentModeType;

    public SportModePresenterImpl(ISportModeContract.IView view) {
        this.view = view;
    }

    public @SportModeDetailItem.StatisticalType
    int getStatisticalType() {
        return statisticalType;
    }

    @Override
    public void requestLoadWeekChart(Calendar calendar) {
        final Calendar dateFirst = DateUtil.getWeekFirstDate(calendar);
        final Calendar dateLast = DateUtil.getWeekLastDate(calendar);
        requestQueryForBetweenDate(PieChartView.PieDataEntry.TYPE_SPORT_MODE_TYPE, TYPE_WEEK, dateFirst, dateLast);
    }

    @Override
    public void requestLoadWeekSportModeDetailChart(@IntRange(from = 0x01, to = 0x0B) int modeType, Calendar calendar) {
        this.currentModeType = modeType;
        final Calendar dateFirst = DateUtil.getWeekFirstDate(calendar);
        final Calendar dateLast = DateUtil.getWeekLastDate(calendar);
        requestQueryForBetweenDate(PieChartView.PieDataEntry.TYPE_DETAIL_WEEK, TYPE_WEEK, dateFirst, dateLast);
    }

    @Override
    public void requestLoadMonthChart(Calendar calendar) {
        Calendar dateFirstDay = DateUtil.getMonthFirstDate(calendar);
        Calendar dateLastDay = DateUtil.getMonthLastDate(calendar);
        requestQueryForBetweenDate(PieChartView.PieDataEntry.TYPE_SPORT_MODE_TYPE, TYPE_MONTH_RANGE, dateFirstDay, dateLastDay);
    }

    @Override
    public void requestLoadMonthRangeChart(Calendar calendar) {
        Calendar dateFirstDay = DateUtil.getQuarterFirstDate(calendar);
        Calendar dateLastDay = DateUtil.getQuarterLastDate(calendar);
        requestQueryForBetweenDate(PieChartView.PieDataEntry.TYPE_SPORT_MODE_TYPE, TYPE_MONTH_RANGE, dateFirstDay, dateLastDay);
    }

    private void requestQueryForBetweenDate(final @PieChartView.PieDataEntry.PieType int pieType, final int dateType, final Calendar dateFirst, final Calendar dateLast) {
        SNAsyncTask.execute(new SNVTaskCallBack() {

            private String[] week;
            private String dateFrom;
            private String dateTo;
            private List<SportModeDetailItem> detailItemsForAll = new ArrayList<>();
            private List<SportModeDetailItem> detailItemsForStatistic = new ArrayList<>();
            private List<PieChartView.PieDataEntry> chartData = new ArrayList<>();
            private String unit_h;
            private String unit_m;
            private String formatHM = "<strong>%d</strong><small>%s</small><br><strong>%d</strong><small>%s</small>";
            private String formatHM_Min = "<strong>%d</strong><small>%s</small>";
            private CharSequence chartCenterText;
            private String nodata;
            private boolean isOnlyNeedDetailWeek;

            @Override
            public void prepare() {
                unit_h = ResUtil.getString(R.string.unit_hours);
                unit_m = ResUtil.getString(R.string.unit_min);
                nodata = ResUtil.getString(R.string.content_no_data);
                chartCenterText = ResUtil.formatHtml(formatHM_Min,   0L, unit_m);
                week = App.getContext().getResources().getStringArray(R.array.week_day);
            }

            @Override
            public void run() throws Throwable {

                isOnlyNeedDetailWeek = pieType == PieChartView.PieDataEntry.TYPE_DETAIL_WEEK && dateType == TYPE_WEEK;

                dateFrom = DateUtil.getDate(DateUtil.YYYY_MM_DD, dateFirst);
                dateTo = DateUtil.getDate(DateUtil.YYYY_MM_DD, dateLast);

                //一共
                // count = DateUtil.getDateOffset(dateLast, dateFirst) + 1;


                SportModeDao sportModeDao = SNBLEDao.get(SportModeDao.class);
                SQLiteDatabase db = sportModeDao.getReadableDatabase();


                int user_id = AppUserUtil.getUser().getUser_id();

                calcDetailItem(sportModeDao, db, user_id);

                long timeTotal = 0;

                for (SportModeDetailItem item : detailItemsForStatistic) {
                    int modeType = item.getModeType();
                    String label = isOnlyNeedDetailWeek ? week[DateUtil.getWeekIndex(DateUtil.convertStringToCalendar(DateUtil.YYYY_MM_DD, item.getDate()))] : ResUtil.getString(SportModeTypeUtil.getLabelResForSportModeType(modeType));
                    chartData.add(new PieChartView.PieDataEntry(
                            pieType,
                            label,
                            item.getTakeMinutes(),
                            SportModeTypeUtil.getColorForSportModeType(modeType),
                            item)
                    );
                    timeTotal += item.getTakeMinutes();
                }


                long hours = timeTotal / 60;
                long minutes = timeTotal % 60;

                if (hours == 0) {
                    chartCenterText = ResUtil.formatHtml(formatHM_Min, minutes, unit_m);
                } else {
                    chartCenterText = ResUtil.formatHtml(formatHM, hours, unit_h, minutes, unit_m);
                }
                if (chartData.isEmpty()) {
                    chartData.add(new PieChartView.PieDataEntry(PieChartView.PieDataEntry.TYPE_EMPTY,nodata,100,0xFFAAAAAA,null));
                }
            }

            private void calcDetailItem(SportModeDao sportModeDao, SQLiteDatabase db, int user_id) {
                //统计的类型
                statisticalType = dateType == TYPE_WEEK ? SportModeDetailItem.STATISTICAL_TYPE_WEEK : SportModeDetailItem.STATISTICAL_TYPE_DATE_RANGE;


                try {
                    List<SportModeBean> query = sportModeDao.getDao()
                            .queryBuilder()
                            .orderBy(SportModeBean.COLUMN_BEGIN_DATE_TIME, false)
                            .where()
                            .eq(SportModeBean.COLUMN_USER_ID, user_id)
                            .and()
                            .between(SportModeBean.COLUMN_DATE, dateFrom, dateTo)
                            .query();
                    for (SportModeBean bean : query) {
                        if (isOnlyNeedDetailWeek) {
                            int modeType = bean.getModeType();
                            if (modeType != currentModeType) {
                                continue;
                            }
                        }
                        //增加列表详情内容
                        SportModeDetailItem item = new SportModeDetailItem(statisticalType);
                        item.setDate(bean.getDate());
                        item.setBeginDateTime(bean.getBeginDateTime());
                        item.setModeType(bean.getModeType());
                        item.setCount(1);
                        item.setTakeMinutes(bean.getTakeMinutes());
                        item.setStepTotal(bean.getStep());
                        item.setCalorie(bean.getCalorie());
                        item.setDistance(bean.getDistance());
                        item.setHrMax(bean.getHeartRateMax());
                        item.setHrAvg(bean.getHeartRateAvg());
                        detailItemsForAll.add(item);
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }

                String sql_chart =
                        "select " +
                                "    date," +
                                "    mode_type," +
                                "    begin_date_time," +
                                "    count(mode_type) count," +
                                "    sum(take_minutes) take_minutes_total," +
                                "    sum(step) step_total," +
                                "    sum(calorie) calorie_total," +
                                "    sum(distance) distance_total," +
                                "    avg(heart_rate_max) hr_max_total," +
                                "    avg(heart_rate_min) hr_min_total," +
                                "    avg(heart_rate_avg) hr_avg_total" +
                                "    from " + SportModeBean.TABLE_NAME + " where date between '" + dateFrom + "' and '" + dateTo + "' and user_id=" + user_id;


                if (isOnlyNeedDetailWeek) {
                    sql_chart += "  and mode_type = " + currentModeType + " group by strftime('%Y-%m-%d',date) order by begin_date_time desc";
                } else {
                    sql_chart += " group by mode_type order by begin_date_time desc";
                }


                Cursor cursor = db.rawQuery(sql_chart, null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        String date = cursor.getString(cursor.getColumnIndex("date"));
                        String begin_date_time = cursor.getString(cursor.getColumnIndex("begin_date_time"));
                        int mode_type = cursor.getInt(cursor.getColumnIndex("mode_type"));
                        int count = cursor.getInt(cursor.getColumnIndex("count"));
                        long take_minutes_total = cursor.getLong(cursor.getColumnIndex("take_minutes_total"));
                        long step_total = cursor.getLong(cursor.getColumnIndex("step_total"));
                        long calorie_total = cursor.getLong(cursor.getColumnIndex("calorie_total"));
                        long distance_total = cursor.getLong(cursor.getColumnIndex("distance_total"));
                        float hr_max_total = cursor.getFloat(cursor.getColumnIndex("hr_max_total"));
                        float hr_min_total = cursor.getFloat(cursor.getColumnIndex("hr_min_total"));
                        float hr_avg_total = cursor.getFloat(cursor.getColumnIndex("hr_avg_total"));

                        //增加列表详情内容
                        SportModeDetailItem item = new SportModeDetailItem(statisticalType);
                        item.setDate(date);
                        item.setBeginDateTime(begin_date_time);
                        item.setModeType(mode_type);
                        item.setCount(count);
                        item.setTakeMinutes(take_minutes_total);
                        item.setStepTotal(step_total);
                        item.setCalorie(calorie_total);
                        item.setDistance(distance_total);
                        item.setHrMax(hr_max_total);
                        item.setHrAvg(hr_avg_total);
                        detailItemsForStatistic.add(item);
                    }
                    cursor.close();
                }
            }

            @Override
            public void done() {
                super.done();
                if (!isUIEnable()) {
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
                        if (isOnlyNeedDetailWeek) {
                            view.onUpdateWeekSportModeDetailChart(chartData, chartCenterText);
                        } else {
                            view.onUpdateWeekChartData(chartData, chartCenterText);
                        }
                        view.onUpdateItemChartData(detailItemsForAll);
                        break;
                    case TYPE_MONTH:
                        try {
                            String mFromDate = DateUtil.convertStringToNewString(DateUtil.YYYY_MM_DD, CalendarUtil.YYYYMMDD, dateFrom);
                            String mToDate = DateUtil.convertStringToNewString(DateUtil.YYYY_MM_DD, CalendarUtil.YYYYMMDD, dateTo);
                            view.onUpdateDateRange(String.format("%s-%s", mFromDate, mToDate));
                        } catch (ParseException ignored) {
                        }
                        view.onUpdateMonthChartData(chartData, chartCenterText);
                        view.onUpdateItemChartData(detailItemsForStatistic);
                        break;
                    case TYPE_MONTH_RANGE:
                        try {
                            String mFromDate = DateUtil.convertStringToNewString(DateUtil.YYYY_MM_DD, CalendarUtil.YYYYMMDD, dateFrom);
                            String mToDate = DateUtil.convertStringToNewString(DateUtil.YYYY_MM_DD, CalendarUtil.YYYYMMDD, dateTo);
                            view.onUpdateDateRange(String.format("%s-%s", mFromDate, mToDate));
                        } catch (ParseException ignored) {
                        }
                        view.onUpdateMonthRangeChartData(chartData, chartCenterText);
                        view.onUpdateItemChartData(detailItemsForStatistic);
                        break;
                }
            }
        });
    }

}
