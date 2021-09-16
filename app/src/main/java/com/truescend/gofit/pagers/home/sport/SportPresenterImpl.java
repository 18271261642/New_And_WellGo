package com.truescend.gofit.pagers.home.sport;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.SparseArray;

import com.sn.app.db.data.config.bean.UnitConfig;
import com.sn.app.utils.AppUnitUtil;
import com.sn.app.utils.AppUserUtil;
import com.sn.blesdk.db.data.base.SNBLEDao;
import com.sn.blesdk.db.data.sport.SportBean;
import com.sn.blesdk.db.data.sport.SportDao;
import com.sn.utils.DateUtil;
import com.sn.utils.task.SNAsyncTask;
import com.sn.utils.task.SNVTaskCallBack;
import com.truescend.gofit.R;
import com.truescend.gofit.pagers.base.BasePresenter;
import com.truescend.gofit.utils.CalendarUtil;
import com.truescend.gofit.utils.ResUtil;
import com.truescend.gofit.utils.UnitConversion;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Author:Created by 泽鑫 on 2017/12/7 10:31.
 * 东芝: 数据查询周月年显示,目前未做实时更新,实时更新放在HomeFragment 以后考虑做不做
 */

public class SportPresenterImpl extends BasePresenter<ISportContract.IView> implements ISportContract.IPresenter {
    private ISportContract.IView view;
    public static final int TYPE_YEAR = 12;
    public static final int TYPE_WEEK = 7;
    public static final int TYPE_MONTH = 30;

    public SportPresenterImpl(ISportContract.IView view) {
        this.view = view;
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

    @Override
    public void requestLoadYearChart(Calendar calendar) {
        Calendar dateFirst = DateUtil.getYearFirstDate(calendar);
        Calendar dateLast = DateUtil.getYearLastDate(calendar);
        requestQueryForBetweenDate(TYPE_YEAR, dateFirst, dateLast);
    }

    /**
     * 使用数据库实现 (是不是感觉比java更复杂了,但实测 数据库只需1毫秒执行完成,java版需要50毫秒)
     * @param dateType
     * @param dateFirst
     * @param dateLast
     */
    private void requestQueryForBetweenDate(final int dateType, final Calendar dateFirst, final Calendar dateLast) {
        SNAsyncTask.execute(new SNVTaskCallBack() {
            private String unit_distance;
            private String standardDays;//达标天数
            private String stepTotal;//总步数
            private String stepAverageTotal;//步数 (周月)日均/(年)月均
            private String calorieAverageTotal;//卡路里 (周月)日均/(年)月均
            private String distanceTotal;//总距离
            private String distanceAverageTotal;//距离 (周月)日均/(年)月均
            private int count = 0;
            private String dateFrom;
            private String dateTo;
            private List<Integer> data = new ArrayList<>();
            private List<Boolean> standardsPosData = new ArrayList<>();

            @Override
            public void prepare() {
                unit_distance = ResUtil.getString(R.string.unit_km);
                //达标天数
                standardDays = ResUtil.format("%d %s", 0, ResUtil.getString(R.string.day));
                //平均步数
                stepAverageTotal = ResUtil.format("%d %s", 0, ResUtil.getString(R.string.step));
                //平均消耗
                calorieAverageTotal = ResUtil.format("%.2f %s", 0.0, ResUtil.getString(R.string.unit_cal));
                //总距离
                distanceTotal = ResUtil.format("%.2f %s", 0.0, unit_distance);
                //平均距离
                distanceAverageTotal = ResUtil.format("%.2f %s", 0.0, unit_distance);
                //总步数
                stepTotal = ResUtil.format("%d %s", 0, ResUtil.getString(R.string.step));
            }

            @Override
            public void run() throws Throwable {

                dateFrom = DateUtil.getDate(DateUtil.YYYY_MM_DD, dateFirst);
                dateTo = DateUtil.getDate(DateUtil.YYYY_MM_DD, dateLast);
                if (dateType == TYPE_YEAR) {
                    count = 12;
                } else {
                    //  一共
                    count = DateUtil.getDateOffset(dateLast, dateFirst) + 1;
                }

                for (int i = 0; i < count; i++) {
                    data.add(0);
                    standardsPosData.add(false);
                }

                SportDao sportDao = SNBLEDao.get(SportDao.class);
                SQLiteDatabase db = sportDao.getReadableDatabase();
                switch (dateType) {
                    case TYPE_WEEK:
                        calcWeekChart(db, data, standardsPosData, dateFrom, dateTo);
                        break;
                    case TYPE_MONTH:
                        calcMonthChart(db, data, standardsPosData, dateFrom, dateTo);
                        break;
                    case TYPE_YEAR:
                        calcYearChart(db, data, standardsPosData, dateFrom, dateTo);
                        break;
                }

                calc(db, dateFrom, dateTo);


            }

            private void calc(SQLiteDatabase db, String dateFrom, String dateTo) {
                //select  sum(stepTotal)stepSum,sum(distanceTotal)distanceSum, sum(stepTotal>=stepTarget) targetDays, avg(stepTotal) stepAvg,avg(distanceTotal) distanceAvg ,avg(calorieTotal)calorieAvg from (select * from SportBean where user_id=698 and date between '2018-03-01' and '2018-03-31' and stepTotal>0)t

                int user_id = AppUserUtil.getUser().getUser_id();
                String sql_chart = "select sum("+SportBean.COLUMN_STEP_TOTAL+")stepSum,sum("+SportBean.COLUMN_DISTANCE_TOTAL+")distanceSum, sum("+SportBean.COLUMN_STEP_TOTAL+">="+ SportBean.COLUMN_STEP_TARGET+") targetDays, avg("+SportBean.COLUMN_STEP_TOTAL+") stepAvg,avg("+SportBean.COLUMN_DISTANCE_TOTAL+") distanceAvg ,avg("+SportBean.COLUMN_CALORIE_TOTAL+")calorieAvg from (select * from "+SportBean.TABLE_NAME+" where " + SportBean.COLUMN_USER_ID + "=" + user_id + " and date between '" + dateFrom + "' and '" + dateTo + "' and "+SportBean.COLUMN_STEP_TOTAL+">0)t";
                Cursor cursor = db.rawQuery(sql_chart, null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        int stepSum = cursor.getInt(cursor.getColumnIndex("stepSum"));
                        int distanceSum = cursor.getInt(cursor.getColumnIndex("distanceSum"));
                        int targetDays = cursor.getInt(cursor.getColumnIndex("targetDays"));
                        double stepAvg = cursor.getDouble(cursor.getColumnIndex("stepAvg"));
                        double distanceAvg = cursor.getDouble(cursor.getColumnIndex("distanceAvg"));
                        double calorieAvg = cursor.getDouble(cursor.getColumnIndex("calorieAvg"));

                        //平均距离 km
                        float tempDistanceAverageTotal = (float) (distanceAvg / 1000.0f);
                        //总距离 km
                        float tempDistanceTotal = distanceSum / 1000.0f;

                        UnitConfig unitConfig = AppUnitUtil.getUnitConfig();
                        //如果单位是英里,则需要转一下
                        if (unitConfig.distanceUnit == UnitConfig.DISTANCE_MILES) {
                            unit_distance = ResUtil.getString(R.string.unit_mile);
                            tempDistanceAverageTotal = UnitConversion.kmToMiles(tempDistanceAverageTotal);
                            tempDistanceTotal = UnitConversion.kmToMiles(tempDistanceTotal);
                        }

                        //达标天数
                        standardDays = ResUtil.format("%d %s", targetDays, ResUtil.getString(R.string.day));
                        //平均步数
                        stepAverageTotal = ResUtil.format("%d %s", Math.round(stepAvg), ResUtil.getString(R.string.step));
                        //平均消耗
                        calorieAverageTotal = ResUtil.format("%.2f %s", calorieAvg, ResUtil.getString(R.string.unit_cal));
                        //总距离
                        distanceTotal = ResUtil.format("%.2f %s", tempDistanceTotal, unit_distance);
                        //平均距离
                        distanceAverageTotal = ResUtil.format("%.2f %s", tempDistanceAverageTotal, unit_distance);
                        //总步数
                        stepTotal = ResUtil.format("%d %s", stepSum, ResUtil.getString(R.string.step));


                        break;

                    }
                    cursor.close();
                }
            }

            private void calcYearChart(SQLiteDatabase db, List<Integer> data, List<Boolean> standardsPosData, String dateFrom, String dateTo) {
                //select  CAST(substr(date,6, 2)AS integer)-1 month_index,sum(stepTotal)stepSum from SportBean where date between '2018-01-01' and '2018-12-31'  and stepTotal>0 and user_id=698 group by strftime('%m',date)
                int user_id = AppUserUtil.getUser().getUser_id();
                String sql_chart = "select  CAST(substr(" + SportBean.COLUMN_DATE + ",6, 2)AS integer)-1 month_index,sum(" + SportBean.COLUMN_STEP_TOTAL + ")stepSum from " + SportBean.TABLE_NAME + " where " + SportBean.COLUMN_DATE + " between '" + dateFrom + "' and '" + dateTo + "'  and " + SportBean.COLUMN_STEP_TOTAL + ">0 and " + SportBean.COLUMN_USER_ID + "=" + user_id + " group by strftime('%m',date)";
                Cursor cursor = db.rawQuery(sql_chart, null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        int month_index = cursor.getInt(cursor.getColumnIndex("month_index"));
                        int stepSum = cursor.getInt(cursor.getColumnIndex("stepSum"));
                        data.set(month_index, stepSum);
                        standardsPosData.set(month_index, false);
                    }
                    cursor.close();
                }
            }

            private void calcMonthChart(SQLiteDatabase db, List<Integer> data, List<Boolean> standardsPosData, String dateFrom, String dateTo) {
//                select CAST(substr(date,9, 2) AS integer)-1 day_index, stepTotal ,stepTotal>=stepTarget is_standard from SportBean where date between '2018-03-01' and '2018-03-31'  and stepTotal>0 and user_id=698 group by strftime('%Y-%m-%d',date)
                int user_id = AppUserUtil.getUser().getUser_id();
                String sql_chart = "select CAST(substr(" + SportBean.COLUMN_DATE + ",9, 2) AS integer)-1 day_index, " + SportBean.COLUMN_STEP_TOTAL + " ," + SportBean.COLUMN_STEP_TOTAL + ">=" + SportBean.COLUMN_STEP_TARGET + " is_standard from " + SportBean.TABLE_NAME + " where " + SportBean.COLUMN_DATE + " between '" + dateFrom + "' and '" + dateTo + "'  and " + SportBean.COLUMN_STEP_TOTAL + ">0 and " + SportBean.COLUMN_USER_ID + "=" + user_id + " group by strftime('%Y-%m-%d'," + SportBean.COLUMN_DATE + ")";
                Cursor cursor = db.rawQuery(sql_chart, null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        int day_index = cursor.getInt(cursor.getColumnIndex("day_index"));
                        int stepTotal = cursor.getInt(cursor.getColumnIndex(SportBean.COLUMN_STEP_TOTAL));
                        boolean is_standard = cursor.getInt(cursor.getColumnIndex("is_standard")) == 1;
                        data.set(day_index, stepTotal);
                        standardsPosData.set(day_index, is_standard);
                    }
                    cursor.close();
                }
            }

            private void calcWeekChart(SQLiteDatabase db, List<Integer> data, List<Boolean> standardsPosData, String dateFrom, String dateTo) {
                //select CAST(strftime("%w", date)AS integer) week_index, stepTotal ,stepTotal>=stepTarget is_standard from SportBean where date between '2018-03-25' and '2018-03-31'  and stepTotal>0 and user_id=698 group by strftime('%Y-%m-%d',date)
                int user_id = AppUserUtil.getUser().getUser_id();
                String sql_chart = "select CAST(strftime('%w', " + SportBean.COLUMN_DATE + ") AS integer) week_index, " + SportBean.COLUMN_STEP_TOTAL + " ," + SportBean.COLUMN_STEP_TOTAL + ">=" + SportBean.COLUMN_STEP_TARGET + " is_standard from " + SportBean.TABLE_NAME + " where " + SportBean.COLUMN_DATE + " between '" + dateFrom + "' and '" + dateTo + "'  and " + SportBean.COLUMN_STEP_TOTAL + ">0 and " + SportBean.COLUMN_USER_ID + "=" + user_id + " group by strftime('%Y-%m-%d'," + SportBean.COLUMN_DATE + ")";
                Cursor cursor = db.rawQuery(sql_chart, null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        int week_index = cursor.getInt(cursor.getColumnIndex("week_index"));
                        int stepTotal = cursor.getInt(cursor.getColumnIndex(SportBean.COLUMN_STEP_TOTAL));
                        boolean is_standard = cursor.getInt(cursor.getColumnIndex("is_standard")) == 1;
                        data.set(week_index, stepTotal);
                        standardsPosData.set(week_index, is_standard);
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

                        view.onUpdateWeekChartData(data, standardsPosData);
                        break;
                    case TYPE_MONTH:
                        try {
                            String mFromDate = DateUtil.convertStringToNewString(DateUtil.YYYY_MM_DD, CalendarUtil.YYYYMMDD, dateFrom);
                            String mToDate = DateUtil.convertStringToNewString(DateUtil.YYYY_MM_DD, CalendarUtil.YYYYMMDD, dateTo);
                            view.onUpdateDateRange(String.format("%s-%s", mFromDate, mToDate));
                        } catch (ParseException ignored) {
                        }
                        view.onUpdateMonthChartData(data, standardsPosData);
                        break;
                    case TYPE_YEAR:
                        try {
                            int year = Integer.parseInt(DateUtil.convertStringToNewString(DateUtil.YYYY_MM_DD, DateUtil.YYYY, dateFrom));
                            view.onUpdateDateRange(String.format("%s/01-%s/12", year, year));
                        } catch (ParseException ignored) {
                        }
                        view.onUpdateYearChartData(data, standardsPosData);
                        break;
                }
                view.onUpdateItemChartData(
                        dateType,
                        standardDays,
                        stepTotal,
                        stepAverageTotal,
                        calorieAverageTotal,
                        distanceTotal,
                        distanceAverageTotal
                );


            }
        });
    }


    /**
     * 使用java实现
     * @param dateType
     * @param dateFirst
     * @param dateLast
     */
    @Deprecated
    private void requestQueryForBetweenDate2(final int dateType, final Calendar dateFirst, final Calendar dateLast) {
        SNAsyncTask.execute(new SNVTaskCallBack() {
            private int upToStandardCount;//达标次数
            private String standardDays;//达标天数
            private String stepTotal;//总步数
            private String stepAverageTotal;//步数 (周月)日均/(年)月均
            private String calorieAverageTotal;//卡路里 (周月)日均/(年)月均
            private String distanceTotal;//总距离
            private String distanceAverageTotal;//距离 (周月)日均/(年)月均
            private List<Integer> data;
            private int count = 0;
            private String dateFrom;
            private String dateTo;
            private List<Boolean> standardsPosData = new ArrayList<>();

            @Override
            public void prepare() {
                String unit_distance = ResUtil.getString(R.string.unit_km);
                //达标天数
                standardDays = ResUtil.format("%d %s", 0, ResUtil.getString(R.string.day));
                //平均步数
                stepAverageTotal = ResUtil.format("%d %s", 0, ResUtil.getString(R.string.step));
                //平均消耗
                calorieAverageTotal = ResUtil.format("%.2f %s", 0.0, ResUtil.getString(R.string.unit_cal));
                //总距离
                distanceTotal = ResUtil.format("%.2f %s", 0.0, unit_distance);
                //平均距离
                distanceAverageTotal = ResUtil.format("%.2f %s", 0.0, unit_distance);
                //总步数
                stepTotal = ResUtil.format("%d %s", 0, ResUtil.getString(R.string.step));
            }

            @Override
            public void run() throws Throwable {
                long l = System.currentTimeMillis();
                try {
                    dateFrom = DateUtil.getDate(DateUtil.YYYY_MM_DD, dateFirst);
                    dateTo = DateUtil.getDate(DateUtil.YYYY_MM_DD, dateLast);

                    SportDao sportDao = SNBLEDao.get(SportDao.class);

                    //查询n天数据
                    List<SportBean> sportBeans;
                    if (dateType == TYPE_YEAR) {
                        sportBeans = sportDao.queryForYear(AppUserUtil.getUser().getUser_id(), dateFirst.get(Calendar.YEAR));
                        count = 12;
                    } else {
                        sportBeans = sportDao.queryForBetween(AppUserUtil.getUser().getUser_id(), dateFrom, dateTo);
                        //  一共
                        count = DateUtil.getDateOffset(dateLast, dateFirst) + 1;
                    }

                    data = new ArrayList<>(count);
                    //初始化
                    for (int i = 0; i < count; i++) {
                        data.add(0);
                        standardsPosData.add(false);
                    }
                    int stepTotalAll = 0;
                    int calorieTotalAll = 0;
                    int distanceTotalAll = 0;
                    int len = 0;//有效数据
                    //int[] 第0位: 步数值,  第1位 重复次数
                    SparseArray<int[]> tempDataArrays = new SparseArray<>();
                    for (SportBean sportBean : sportBeans) {
                        int step = sportBean.getStepTotal();
                        //达标次数累计
                        boolean isStandard = step > 0 && (step >= sportBean.getStepTarget());
                        Calendar c = DateUtil.convertStringToCalendar(DateUtil.YYYY_MM_DD, sportBean.getDate());
                        int index = 0;
                        switch (dateType) {
                            case TYPE_WEEK:
                                //取得数据在这7天的哪一天
                                index = DateUtil.getWeekIndex(DateUtil.getYear(c), DateUtil.getMonth(c), DateUtil.getDay(c));
                                standardsPosData.set(index, isStandard);
                                break;
                            case TYPE_MONTH:
                                index = DateUtil.getMonthIndex(DateUtil.getYear(c), DateUtil.getMonth(c), DateUtil.getDay(c));
                                standardsPosData.set(index, isStandard);
                                break;
                            case TYPE_YEAR:
                                index = c.get(Calendar.MONTH);//月为索引
                                //年不显示达标
                                standardsPosData.set(index, false);
                                break;
                        }

                        if (step == 0) {
                            continue;//无数据
                        }
                        int calorieTotal = sportBean.getCalorieTotal();
                        int distanceTotal = sportBean.getDistanceTotal();

                        //在count个index中填充数据并记录其次数
                        //这段是为了大数据  的平均计算 才用的数组来装步数总数和计数器数量
                        if (tempDataArrays.indexOfKey(index) < 0) {//无历史 则添加到里面
                            //首次存放历史
                            tempDataArrays.put(index, new int[]{step, 1});
                        } else {
                            //取出历史
                            int[] valueAndCount = tempDataArrays.get(index);
                            //值累加
                            valueAndCount[0] += step;
                            //次数累加
                            valueAndCount[1]++;

                            //放回去
                            tempDataArrays.put(index, valueAndCount);
                        }


                        if (isStandard) {
                            upToStandardCount++;
                        }
                        stepTotalAll += step;
                        calorieTotalAll += calorieTotal;
                        distanceTotalAll += distanceTotal;
                        len++;
                    }

                    standardDays = ResUtil.format("%d %s", upToStandardCount, ResUtil.getString(R.string.day));


                    //总步数
                    stepTotal = ResUtil.format("%d %s", stepTotalAll, ResUtil.getString(R.string.step));
                    if (len == 0) {//无详细数据
                        return;
                    }

                    //从遍历来的数据中 抽出每段的平均值作为图表数据 使用 SparseArray 会飞快地取出键值对 不用担心性能
                    for (int i = 0; i < tempDataArrays.size(); i++) {
                        int index = tempDataArrays.keyAt(i);
                        int[] valueAndCount = tempDataArrays.valueAt(i);
                        //取出总步数和总次数 求平均
                        int stepTotal = valueAndCount[0];
                        int stepValueCount = valueAndCount[1];
                        int stepValueAvg = stepTotal / stepValueCount;
                        //放到图表数据中去
                        data.set(index, stepValueAvg);
                    }
                    //平均步数
                    stepAverageTotal = ResUtil.format("%d %s", stepTotalAll / len, ResUtil.getString(R.string.step));

                    //平均卡路里
                    calorieAverageTotal = ResUtil.format("%.2f %s", calorieTotalAll * 1.0f / len, ResUtil.getString(R.string.unit_cal));

                    String unit_distance = ResUtil.getString(R.string.unit_km);
                    //平均距离 km
                    float tempDistanceAverageTotal = (distanceTotalAll * 1.0f / len) / 1000.0f;
                    //总距离 km
                    float tempDistanceTotal = distanceTotalAll / 1000.0f;
                    UnitConfig unitConfig = AppUnitUtil.getUnitConfig();
                    //如果单位是英里,则需要转一下
                    if (unitConfig.distanceUnit == UnitConfig.DISTANCE_MILES) {
                        unit_distance = ResUtil.getString(R.string.unit_mile);
                        tempDistanceAverageTotal = UnitConversion.kmToMiles(tempDistanceAverageTotal);
                        tempDistanceTotal = UnitConversion.kmToMiles(tempDistanceTotal);
                    }
                    //平均距离
                    distanceAverageTotal = ResUtil.format("%.2f %s", tempDistanceAverageTotal, unit_distance);
                    //总距离
                    distanceTotal = ResUtil.format("%.2f %s", tempDistanceTotal, unit_distance);


                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println("查询时间="+(System.currentTimeMillis()-l));
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

                        view.onUpdateWeekChartData(data, standardsPosData);
                        break;
                    case TYPE_MONTH:
                        try {
                            String mFromDate = DateUtil.convertStringToNewString(DateUtil.YYYY_MM_DD, CalendarUtil.YYYYMMDD, dateFrom);
                            String mToDate = DateUtil.convertStringToNewString(DateUtil.YYYY_MM_DD, CalendarUtil.YYYYMMDD, dateTo);
                            view.onUpdateDateRange(String.format("%s-%s", mFromDate, mToDate));
                        } catch (ParseException ignored) {
                        }
                        view.onUpdateMonthChartData(data, standardsPosData);
                        break;
                    case TYPE_YEAR:
                        try {
                            int year = Integer.parseInt(DateUtil.convertStringToNewString(DateUtil.YYYY_MM_DD, DateUtil.YYYY, dateFrom));
                            view.onUpdateDateRange(String.format("%s/01-%s/12", year, year));
                        } catch (ParseException ignored) {
                        }
                        view.onUpdateYearChartData(data, standardsPosData);
                        break;
                }
                view.onUpdateItemChartData(
                        dateType,
                        standardDays,
                        stepTotal,
                        stepAverageTotal,
                        calorieAverageTotal,
                        distanceTotal,
                        distanceAverageTotal
                );


            }
        });
    }

}
