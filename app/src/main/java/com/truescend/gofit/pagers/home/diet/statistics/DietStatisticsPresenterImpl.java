package com.truescend.gofit.pagers.home.diet.statistics;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.sn.app.db.data.diet.DietBean;
import com.sn.app.db.data.diet.DietDao;
import com.sn.app.db.data.user.UserBean;
import com.sn.app.utils.AppUserUtil;
import com.sn.blesdk.db.data.sport.SportBean;
import com.sn.db.data.base.dao.SNBaseDao;
import com.sn.utils.DateUtil;
import com.sn.utils.IF;
import com.sn.utils.task.SNAsyncTask;
import com.sn.utils.task.SNVTaskCallBack;
import com.sn.utils.tuple.TupleTwo;
import com.truescend.gofit.R;
import com.truescend.gofit.pagers.base.BasePresenter;
import com.truescend.gofit.utils.CalendarUtil;
import com.truescend.gofit.utils.ResUtil;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 作者:东芝(2018/11/22).
 * 功能:分析
 */
public class DietStatisticsPresenterImpl extends BasePresenter<IDietStatisticsContract.IView> implements IDietStatisticsContract.IPresenter {
    private IDietStatisticsContract.IView view;


    public static final int TYPE_YEAR = 12;
    public static final int TYPE_WEEK = 7;
    public static final int TYPE_MONTH = 30;

    public DietStatisticsPresenterImpl(IDietStatisticsContract.IView view) {
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
     *
     * @param dateType
     * @param dateFirst
     * @param dateLast
     */
    private void requestQueryForBetweenDate(final int dateType, final Calendar dateFirst, final Calendar dateLast) {

        SNAsyncTask.execute(new SNVTaskCallBack() {
            private int count = 0;
            private List<TupleTwo<Integer, Integer>> data = new ArrayList<>();
            private String dateFrom;
            private String dateTo;
            private CharSequence deficitAverageCalorie;//日均卡路里赤字
            private String standardDays;//达标天数
            private String qualifiedDays;//摄入合格天数
            private String inCalorieAverage; //日均摄入热量
            private String inCalorieMax;//最高摄入热量
            private String invalidDays;  //未打卡天数

            @Override
            public void prepare() {
                //日均卡路里赤字
                deficitAverageCalorie = "0";
                //达标天数
                standardDays = ResUtil.format("%d %s", 0, ResUtil.getString(R.string.day));
                //摄入合格天数
                qualifiedDays = ResUtil.format("%d %s", 0, ResUtil.getString(R.string.day));
                //日均摄入热量
                inCalorieAverage = ResUtil.format("%.0f %s", 0f, ResUtil.getString(R.string.unit_cal));
                //最高摄入热量
                inCalorieMax = ResUtil.format("%.0f %s", 0f, ResUtil.getString(R.string.unit_cal));
                //未打卡天数
                invalidDays = ResUtil.format("%d %s", 0, ResUtil.getString(R.string.day));
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
                    data.add(TupleTwo.create(0, 0));
                }


                UserBean user = AppUserUtil.getUser();
                int user_id = user.getUser_id();
                String first_meal_date = user.getFirst_meal_date();
                //tempDao 只是为了拿到getReadableDatabase
                DietDao tempDao = SNBaseDao.get(DietDao.class);

                if(!IF.isEmpty(first_meal_date)) {
                    if(DateUtil.getDateOffset(dateFrom, first_meal_date) < 0){
                        dateFrom = first_meal_date;
                    }
                }else{

                }
//                String currentDate = DateUtil.getCurrentDate(DateUtil.YYYY_MM_DD);
//                if (DateUtil.getDateOffset(currentDate, dateTo) < 0) {
//                    dateTo = currentDate;
//                }

                SQLiteDatabase db = tempDao.getReadableDatabase();
                switch (dateType) {
                    case TYPE_WEEK:
                        calcWeekChart(db, user_id, data, dateFrom, dateTo);
                        break;
                    case TYPE_MONTH:
                        calcMonthChart(db, user_id, data, dateFrom, dateTo);
                        break;
                    case TYPE_YEAR:
                        calcYearChart(db, user_id, data, dateFrom, dateTo);
                        break;
                }

                calc(db, user_id, dateFrom, dateTo);


            }

            /**
             * select
             * -avg((sport_calorie+basic_calorie)-total_in_calories) deficit_calorie,
             * sum(stepTotal>=stepTarget) target_days,
             * sum((sport_calorie+basic_calorie)>total_in_calories) qualified_days,
             * avg(total_calories)avg_total_in_calorie_days,
             * max(total_calories) max_total_calorie,
             * (((CAST(strftime('%s', '2018-12-31') as integer) - CAST(strftime('%s', '2018-01-01') as integer))/86400)+1) - sum(total_calories>0) invalid_days
             * from (
             * select ifnull(calorieTotal,0)sport_calorie,basic_calorie, ifnull(total_calories,0) total_in_calories,B.date,* from
             * (select * from DietBean where user_id=233082 and date between '2018-01-01' and '2018-12-31' )B
             * left join
             * (select * from SportBean where user_id=233082 and date between '2018-01-01' and '2018-12-31' )A
             * on A.date=B.date
             )
             * @param db
             * @param user_id
             */
            private void calc(SQLiteDatabase db, int user_id, String dateFrom, String dateTo) {

                String sql_chart = "select \n" +
                        "-avg((sport_calorie+basic_calorie)-total_in_calories) avg_deficit_calorie,\n" +
                        " sum(stepTotal>=stepTarget) target_days,\n" +
                        " sum((sport_calorie+basic_calorie)>total_in_calories) qualified_days,\n" +
                        " avg(total_calories)avg_total_in_calorie_days,\n" +
                        " max(total_calories) max_total_calorie,\n" +
                        " (((CAST(strftime('%s', '" + dateTo + "') as integer) - CAST(strftime('%s', '" + dateFrom + "') as integer))/86400)+1) - sum(total_calories>0) invalid_days \n" +
                        "from (\n" +
                        "    select ifnull(calorieTotal,0)sport_calorie,basic_calorie, ifnull(total_calories,0) total_in_calories,B.date,* from\n" +
                        "        (select * from " + DietBean.TABLE_NAME + " where user_id=" + user_id + " and date between '" + dateFrom + "' and '" + dateTo + "' )B\n" +
                        "    left join\n" +
                        "        (select * from " + SportBean.TABLE_NAME + " where user_id=" + user_id + " and date between '" + dateFrom + "' and '" + dateTo + "' )A\n" +
                        "    on A.date=B.date \n" +
                        ") ";

                Cursor cursor = db.rawQuery(sql_chart, null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        //卡路里赤字
                        float avg_deficit_calorie = cursor.getFloat(cursor.getColumnIndex("avg_deficit_calorie"));
                        //运动达标天数 (不是纯运动达标天数,而是以当天有进行食谱记录为前提的对应此时的达标情况统计)
                        int target_days = cursor.getInt(cursor.getColumnIndex("target_days"));
                        //摄入合格天数
                        int qualified_days = cursor.getInt(cursor.getColumnIndex("qualified_days"));
                        //日均摄入热量
                        float avg_total_in_calorie_days = cursor.getFloat(cursor.getColumnIndex("avg_total_in_calorie_days"));
                        //最高摄入热量
                        float max_total_calorie = cursor.getFloat(cursor.getColumnIndex("max_total_calorie"));
                        //未打卡天数
                        int invalid_days = cursor.getInt(cursor.getColumnIndex("invalid_days"));


                        //日均卡路里赤字
                        deficitAverageCalorie = ResUtil.formatHtml("<strong><font color=#ff0000>%s</font></strong>%.0f", avg_deficit_calorie == 0 ? " " : avg_deficit_calorie < 0 ? "+" : "-", Math.abs(avg_deficit_calorie));
                        //达标天数
                        standardDays = ResUtil.format("%d %s", target_days, ResUtil.getString(R.string.day));
                        //摄入合格天数
                        qualifiedDays = ResUtil.format("%d %s", qualified_days, ResUtil.getString(R.string.day));

                        //日均摄入热量
                        inCalorieAverage = ResUtil.format("%.0f %s", avg_total_in_calorie_days, ResUtil.getString(R.string.unit_cal));
                        //最高摄入热量
                        inCalorieMax = ResUtil.format("%.0f %s", max_total_calorie, ResUtil.getString(R.string.unit_cal));
                        //未打卡天数
                        invalidDays = ResUtil.format("%d %s", invalid_days, ResUtil.getString(R.string.day));
                        break;
                    }
                    cursor.close();
                }
            }


            /**
             * select  CAST(substr(B.date,6, 2)AS integer)-1 month_index,   ifnull(sum_sport_calorie,0) sum_total_out_calorie, ifnull(sum_total_calories,0) sum_total_in_calorie,B.sum_basic_calorie   from
             * (select  CAST(substr(date,6, 2)AS integer)-1 month_index,sum(total_calories)sum_total_calories,sum(basic_calorie) sum_basic_calorie, date from DietBean where date between '2018-01-01' and '2018-12-31'  and total_calories>0 and user_id=233082 group by strftime('%m',date))B
             * left  join
             * (select  CAST(substr(date,6, 2)AS integer)-1 month_index,sum(calorieTotal)sum_sport_calorie,date from SportBean where date between '2018-01-01' and '2018-12-31'  and stepTotal>0 and user_id=233082 group by strftime('%m',date))A
             * on A.month_index=B.month_index
             * @param db
             * @param data
             * @param dateFrom
             * @param dateTo
             */
            private void calcYearChart(SQLiteDatabase db, int user_id, List<TupleTwo<Integer, Integer>> data, String dateFrom, String dateTo) {

                String sql_chart = "select  CAST(substr(B.date,6, 2)AS integer)-1 month_index,   ifnull(sum_sport_calorie,0) sum_total_out_calorie, ifnull(sum_total_calories,0) sum_total_in_calorie,B.sum_basic_calorie   from\n" +
                        "\n" +
                        "(select  CAST(substr(date,6, 2)AS integer)-1 month_index,sum(total_calories)sum_total_calories,sum(basic_calorie) sum_basic_calorie, date from " + DietBean.TABLE_NAME + " where date between '" + dateFrom + "' and '" + dateTo + "'  and total_calories>0 and user_id=" + user_id + " group by strftime('%m',date))B\n" +
                        "\n" +
                        "left  join\n" +
                        "\n" +
                        "(select  CAST(substr(date,6, 2)AS integer)-1 month_index,sum(calorieTotal)sum_sport_calorie,date from " + SportBean.TABLE_NAME + " where date between '" + dateFrom + "' and '" + dateTo + "'  and stepTotal>0 and user_id=" + user_id + " group by strftime('%m',date))A\n" +
                        "\n" +
                        "on A.month_index=B.month_index\n";
                Cursor cursor = db.rawQuery(sql_chart, null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        int week_index = cursor.getInt(cursor.getColumnIndex("month_index"));
                        float sum_total_out_calorie = cursor.getFloat(cursor.getColumnIndex("sum_total_out_calorie"));
                        float sum_total_in_calorie = cursor.getFloat(cursor.getColumnIndex("sum_total_in_calorie"));
                        float sum_basic_calorie = cursor.getFloat(cursor.getColumnIndex("sum_basic_calorie"));

                        data.set(week_index, TupleTwo.create(Math.round(sum_total_out_calorie + sum_basic_calorie), Math.round(sum_total_in_calorie)));
                    }
                    cursor.close();
                }
            }


            /**
             * SQL语法举例:
             * select  CAST(strftime("%w", B.date)AS integer) week_index,   ifnull(calorieTotal,0) total_out_calories, ifnull(total_calories,0) total_in_calories,B.date  from
             * (select CAST(strftime("%w", date)AS integer) week_index ,total_calories,date  from DietBean where date between '2018-11-25' and '2018-12-01' and total_calories>0  and user_id=233082 group by strftime('%Y-%m-%d',date)) B
             * left  join
             * (select CAST(strftime("%w", date)AS integer) week_index, calorieTotal ,date   from SportBean where date between '2018-11-25' and '2018-12-01'  and stepTotal>0 and user_id=233082 group by strftime('%Y-%m-%d',date)) A
             * on A.week_index=B.week_index
             * @param db
             * @param data
             * @param dateFrom
             * @param dateTo
             */
            private void calcWeekChart(SQLiteDatabase db, int user_id, List<TupleTwo<Integer, Integer>> data, String dateFrom, String dateTo) {

                String sql_chart = "select  CAST(strftime(\"%w\", B.date)AS integer) week_index,   ifnull(calorieTotal,0) total_out_calorie, ifnull(total_calories,0) total_in_calorie,basic_calorie,B.date  from \n" +
                        " \n" +
                        "(select CAST(strftime('%w', date)AS integer) week_index , total_calories , basic_calorie, date  from " + DietBean.TABLE_NAME + " where date between '" + dateFrom + "' and '" + dateTo + "' and total_calories>0  and user_id=" + user_id + " group by strftime('%Y-%m-%d',date)) B\n" +
                        "\n" +
                        "left  join\n" +
                        "\n" +
                        "(select CAST(strftime('%w', date)AS integer) week_index, calorieTotal , date   from " + SportBean.TABLE_NAME + " where date between '" + dateFrom + "' and '" + dateTo + "'  and stepTotal>0 and user_id=" + user_id + " group by strftime('%Y-%m-%d',date)) A\n" +
                        "\n" +
                        "on A.week_index=B.week_index \n" +
                        " ";

                Cursor cursor = db.rawQuery(sql_chart, null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        int week_index = cursor.getInt(cursor.getColumnIndex("week_index"));
                        float total_out_calories = cursor.getFloat(cursor.getColumnIndex("total_out_calorie"));
                        float total_in_calories = cursor.getFloat(cursor.getColumnIndex("total_in_calorie"));
                        float basic_calorie = cursor.getFloat(cursor.getColumnIndex("basic_calorie"));

                        data.set(week_index, TupleTwo.create(Math.round(total_out_calories + basic_calorie), Math.round(total_in_calories)));
                    }
                    cursor.close();
                }
            }

            /**
             * SQL语法举例:
             * select  CAST(substr(B.date,9, 2) AS integer)-1 day_index,   ifnull(calorieTotal,0) total_out_calorie, ifnull(total_calories,0) total_in_calorie,basic_calorie,B.date  from
             *
             * (select CAST(substr(date,9, 2) AS integer)-1 day_index,  total_calories,basic_calorie,date from DietBean where date between '2018-11-01' and '2018-11-30'  and total_calories>0 and user_id=233082 group by strftime('%Y-%m-%d',date))B
             *
             * left  join
             * @param db
             * @param data
             * @param dateFrom
             * @param dateTo
             */
            private void calcMonthChart(SQLiteDatabase db, int user_id, List<TupleTwo<Integer, Integer>> data, String dateFrom, String dateTo) {

                String sql_chart = " \n" +
                        "select  CAST(substr(B.date,9, 2) AS integer)-1 day_index,   ifnull(calorieTotal,0) total_out_calorie, ifnull(total_calories,0) total_in_calorie,basic_calorie,B.date  from\n" +
                        "\n" +
                        "(select CAST(substr(date,9, 2) AS integer)-1 day_index,  total_calories,basic_calorie,date from " + DietBean.TABLE_NAME + " where date between '" + dateFrom + "' and '" + dateTo + "'  and total_calories>0 and user_id=" + user_id + " group by strftime('%Y-%m-%d',date))B\n" +
                        "\n" +
                        "left  join\n" +
                        "\n" +
                        "(select CAST(substr(date,9, 2) AS integer)-1 day_index, calorieTotal ,date from " + SportBean.TABLE_NAME + " where date between '" + dateFrom + "' and '" + dateTo + "'  and stepTotal>0 and user_id=" + user_id + " group by strftime('%Y-%m-%d',date))A\n" +
                        "\n" +
                        "on A.day_index=B.day_index";

                Cursor cursor = db.rawQuery(sql_chart, null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        int day_index = cursor.getInt(cursor.getColumnIndex("day_index"));
                        float total_out_calories = cursor.getFloat(cursor.getColumnIndex("total_out_calorie"));
                        float total_in_calories = cursor.getFloat(cursor.getColumnIndex("total_in_calorie"));
                        float basic_calorie = cursor.getFloat(cursor.getColumnIndex("basic_calorie"));

                        data.set(day_index, TupleTwo.create(Math.round(total_out_calories + basic_calorie), Math.round(total_in_calories)));
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
                    case TYPE_YEAR:
                        try {
                            int year = Integer.parseInt(DateUtil.convertStringToNewString(DateUtil.YYYY_MM_DD, DateUtil.YYYY, dateFrom));
                            view.onUpdateDateRange(String.format("%s/01-%s/12", year, year));
                        } catch (ParseException ignored) {
                        }
                        view.onUpdateYearChartData(data);
                        break;
                }
                view.onUpdateItemChartData(
                        dateType,
                        deficitAverageCalorie,
                        standardDays,
                        qualifiedDays,
                        inCalorieAverage,
                        inCalorieMax,
                        invalidDays);

            }
        });

    }
}
