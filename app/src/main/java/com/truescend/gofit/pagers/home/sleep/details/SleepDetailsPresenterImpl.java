package com.truescend.gofit.pagers.home.sleep.details;

import android.util.SparseArray;

import com.sn.app.utils.AppUserUtil;
import com.sn.blesdk.db.data.base.SNBLEDao;
import com.sn.blesdk.db.data.sleep.SleepBean;
import com.sn.blesdk.db.data.sleep.SleepDao;
import com.sn.utils.DateUtil;
import com.sn.utils.task.SNAsyncTask;
import com.sn.utils.task.SNVTaskCallBack;
import com.truescend.gofit.R;
import com.truescend.gofit.pagers.base.BasePresenter;
import com.truescend.gofit.utils.CalendarUtil;
import com.truescend.gofit.utils.ResUtil;
import com.truescend.gofit.utils.SleepDataUtil;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Author:Created by 泽鑫 on 2017/12/7 15:54.
 * 东芝:
 * 睡眠详情数据操作 和运动详情界面 代码相似
 * 如有修改 请对比 运动/睡眠 详情界面 同步修改 别一会这边这样处理 一会那边那样处理
 */

public class SleepDetailsPresenterImpl extends BasePresenter<ISleepDetailsContract.IView> implements ISleepDetailsContract.IPresenter {
    private ISleepDetailsContract.IView view;
    public static final int TYPE_YEAR = 12;
    public static final int TYPE_WEEK = 7;
    public static final int TYPE_MONTH = 30;

    public SleepDetailsPresenterImpl(ISleepDetailsContract.IView view) {
        this.view = view;
    }


    @Override
    public void requestLoadWeekChart(final Calendar calendar) {

        final Calendar dateFirstWeek = DateUtil.getWeekFirstDate(calendar);
        final Calendar dateLastWeek = DateUtil.getWeekLastDate(calendar);
        requestQueryForBetweenDate(TYPE_WEEK, dateFirstWeek, dateLastWeek);

    }

    @Override
    public void requestLoadMonthChart(final Calendar calendar) {
        Calendar dateFirstDay = DateUtil.getMonthFirstDate(calendar);
        Calendar dateLastDay = DateUtil.getMonthLastDate(calendar);
        requestQueryForBetweenDate(TYPE_MONTH, dateFirstDay, dateLastDay);


    }

    @Override
    public void requestLoadYearChart(Calendar calendar) {
        requestQueryForBetweenDate(TYPE_YEAR, calendar, calendar);
    }


    private void requestQueryForBetweenDate(final int dateType, final Calendar dateFirst, final Calendar dateLast) {
        SNAsyncTask.execute(new SNVTaskCallBack() {
            private int goodDaysCount = 0;//良好天数
            private String goodDays;//良好天数
            private CharSequence validTotalSleepTime;//有效睡眠市时长 h
            private CharSequence validAvgSleepTime;//最大单条睡眠时长
            private CharSequence maxSingleSleepTime;//有效睡眠占比
            private int validSleepPercent = 0;
            private List<Integer> data;
            private String quality = "";//睡眠质量
            private int count = 0;
            private String dateFrom;
            private String dateTo;

            @Override
            public void prepare() {
                validTotalSleepTime = ResUtil.formatHtml("%d<small>h</small> %d<small>min</small>", 0, 0);
                validAvgSleepTime = ResUtil.formatHtml("%d<small>h</small> %d<small>min</small>", 0, 0);
                maxSingleSleepTime = ResUtil.formatHtml("%d<small>h</small> %d<small>min</small>", 0, 0);
                quality = ResUtil.getString(R.string.content_no_data);
            }

            @Override
            public void run() throws Throwable {
                try {
                    dateFrom = DateUtil.getDate(DateUtil.YYYY_MM_DD, dateFirst);
                    dateTo = DateUtil.getDate(DateUtil.YYYY_MM_DD, dateLast);

                    SleepDao sleepDao = SNBLEDao.get(SleepDao.class);
                    //查询n天数据
                    List<SleepBean> sleepBeans;
                    if (dateType == TYPE_YEAR) {
                        sleepBeans = sleepDao.queryForYear(AppUserUtil.getUser().getUser_id(), dateFirst.get(Calendar.YEAR));
                        count = 12;
                    } else {
                        sleepBeans = sleepDao.queryForBetween(AppUserUtil.getUser().getUser_id(), dateFrom, dateTo);
                        //  一共
                        count = DateUtil.getDateOffset(dateLast, dateFirst) + 1;
                    }

                    data = new ArrayList<>(count);
                    //初始化
                    for (int i = 0; i < count; i++) {
                        data.add(0);
                    }
                    //总睡眠
//                    int deepTotal = 0;
//                    int lightTotal = 0;
//                    int soberTotal = 0;
                    int sleepTimeValidTotalAll = 0;
                    int sleepTimeTotalAll = 0;
                    int sleepTimeMaxTotal = -Integer.MAX_VALUE;
                    float sleepQualityAll = 0;
                    int len = 0;//有效数据

                    //int[] 第0位: 睡眠良好状态值,  第1位 重复次数
                    SparseArray<int[]> tempDataArrays = new SparseArray<>();

                    for (SleepBean sleepBean : sleepBeans) {
                        Calendar c = DateUtil.convertStringToCalendar(DateUtil.YYYY_MM_DD, sleepBean.getDate());
                        int index = 0;
                        switch (dateType) {
                            case TYPE_WEEK:
                                //取得数据在这7天的哪一天
                                index = DateUtil.getWeekIndex(DateUtil.getYear(c), DateUtil.getMonth(c), DateUtil.getDay(c));
                                break;
                            case TYPE_MONTH:
                                index = DateUtil.getMonthIndex(DateUtil.getYear(c), DateUtil.getMonth(c), DateUtil.getDay(c));
                                break;
                            case TYPE_YEAR:
                                index = c.get(Calendar.MONTH);//月为索引
                                break;
                        }

                        int sleepTimeTotal = SleepDataUtil.getSleepTotal(sleepBean.getDeepTotal(), sleepBean.getLightTotal(), sleepBean.getSoberTotal());
                        if (sleepTimeTotal == 0) {
                            continue;//无数据
                        }
                        //取出睡眠质量 睡眠质量 1234 作为睡眠的图表显示值
                        int sleepQualityInt = SleepDataUtil.getSleepQualityInt(sleepBean);

                        //在count个index中填充数据并记录其次数
                        //这段是为了大数据  的平均计算 才用的数组来装步数总数和计数器数量
                        if (tempDataArrays.indexOfKey(index) < 0) {//无历史 则添加到里面
                            //首次存放历史
                            tempDataArrays.put(index, new int[]{sleepQualityInt, 1});
                        } else {
                            //取出历史
                            int[] valueAndCount = tempDataArrays.get(index);
                            //值累加
                            valueAndCount[0] += sleepQualityInt;
                            //次数累加
                            valueAndCount[1]++;
                            //放回去
                            tempDataArrays.put(index, valueAndCount);
                        }
//                      deepTotal += sleepBean.getDeepTotal();
//                      soberTotal += sleepBean.getSoberTotal();
//                      lightTotal += sleepBean.getLightTotal();
                        int sleepTimeValidTotal = SleepDataUtil.getSleepValidTotal(sleepBean.getDeepTotal(), sleepBean.getLightTotal());
                        sleepTimeValidTotalAll += sleepTimeValidTotal;
                        sleepTimeTotalAll += sleepTimeTotal;
                        sleepTimeMaxTotal = Math.max(sleepTimeMaxTotal, sleepTimeTotal);
                        float sleepQualityFloat = SleepDataUtil.getSleepQualityFloat(sleepBean);
                        sleepQualityAll += sleepQualityFloat;
                        //质量大于良好 的次数,0.6以上为良好
                        if (sleepQualityInt > 0.6f) {
                            goodDaysCount++;
                        }
                        len++;
                    }

                    goodDays = ResUtil.format("%d %s", goodDaysCount, ResUtil.getString(R.string.day));

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

                    //有效睡眠时长
                    validTotalSleepTime = ResUtil.formatHtml("%d<small>h</small> %d<small>min</small>", sleepTimeValidTotalAll / 60, sleepTimeValidTotalAll % 60);

                    //日均有效睡眠总时长
                    int sleepTimeValidTotalAvg = sleepTimeValidTotalAll / len;
                    validAvgSleepTime = ResUtil.formatHtml("%d<small>h</small> %d<small>min</small>", sleepTimeValidTotalAvg / 60, sleepTimeValidTotalAvg % 60);

                    if (sleepTimeMaxTotal > 0) {
                        //单次最长睡眠
                        maxSingleSleepTime = ResUtil.formatHtml("%d<small>h</small> %d<small>min</small>", sleepTimeMaxTotal / 60, sleepTimeMaxTotal % 60);
                    }
                    //睡眠质量
                    //求总平均
                    float sleepQualityAvg = sleepQualityAll / len;
                    //取得质量字符串
                    quality = SleepDataUtil.getSleepQualityStr(sleepQualityAvg);
                    //有效睡眠占比
                    validSleepPercent = (int) ((sleepTimeValidTotalAll*1.0f/sleepTimeTotalAll) * 100.0f);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void done() {
                super.done();
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
                view.onUpdateItemChartData(goodDays, validTotalSleepTime, validAvgSleepTime, maxSingleSleepTime, validSleepPercent, quality);
            }
        });
    }


//
//
//    private void requestQueryForBetweenDate(final int dateType, final Calendar dateFirst, final Calendar dateLast) {
//        //不用说为什么采用 我怕以后改更麻烦 于是直接重复复制, 毕竟下面有周有 星期偏移数组  月有天数偏移数组 年有月份偏移数组  不能写多个if判断类型什么的 重用 太乱了
//        SNAsyncTask.execute(new SNVTaskCallBack() {
//            private int goodDaysCount = 0;//良好天数
//            private int sleepTimeValidHours = 0;//有效睡眠市时长 h
//            private int sleepTimeValidMinutes = 0;//有效睡眠市时长 m
//            private int sleepTimeAverageDailyTotalHours = 0;// 日均有效睡眠时长
//            private int sleepTimeAverageDailyTotalMinutes = 0;//
//            private int sleepTimeMaxSingleTotalHours = 0;//最大单条睡眠时长
//            private int sleepTimeMaxSingleTotalMinutes = 0;//
//            private int sleepValidPercent = 0;//有效睡眠占比
//            private List<Integer> data;
//            private String quality = "";//睡眠质量
//            private int count = 0;
//            private String dateFrom;
//            private String dateTo;
//            @Override
//            public void run() throws Throwable {
//                try {
//                    dateFrom = DateUtil.getWhichDate(DateUtil.YYYY_MM_DD, dateFirst);
//                    dateTo = DateUtil.getWhichDate(DateUtil.YYYY_MM_DD, dateLast);
//
//                    SleepDao sleepDao = SNDao.get(SleepDao.class);
//                    //查询n天数据
//                    List<SleepBean> sleepBeans;
//                    if (dateType == TYPE_YEAR) {
//                        sleepBeans = sleepDao.queryForYear(dateFirst.get(Calendar.YEAR), SNBLEHelper.getDeviceMacAddress());
//                        count = 12;
//                    } else {
//                        sleepBeans = sleepDao.queryForBetween(dateFrom, dateTo, SNBLEHelper.getDeviceMacAddress());
//                        //  一共
//                        count = DateUtil.getDateOffset(dateLast, dateFirst) + 1;
//                    }
//
//                    data = new ArrayList<>(count);
//                    //初始化
//                    for (int d = 0; d < count; d++) {
//                        data.add(0);
//                    }
//                    //总睡眠
//                    int deepTotal = 0;
//                    int lightTotal = 0;
//                    int soberTotal = 0;
//                    int sleepTimeValidTotalAll = 0;
//                    int sleepTimeMaxTotal = -Integer.MAX_VALUE;
//                    float sleepQualityAll = 0;
//                    int len = 0;//有效数据
//                    for (SleepBean sleepBean : sleepBeans) {
//                        Calendar c = DateUtil.convertStringToCalendar(DateUtil.YYYY_MM_DD, sleepBean.getWhichDate());
//                        int index =0;
//                        switch (dateType) {
//                            case TYPE_WEEK:
//                                //取得数据在这7天的哪一天
//                                index=DateUtil.getWeekIndex(DateUtil.getYear(c), DateUtil.getMonth(c), DateUtil.getDay(c));
//                                break;
//                            case TYPE_MONTH:
//                                index=DateUtil.getMonthIndex(DateUtil.getYear(c), DateUtil.getMonth(c), DateUtil.getDay(c));
//                                break;
//                        }
//                        int sleepTimeTotal = SleepDataUtil.getSleepTotal(sleepBean.getDeepTotal(), sleepBean.getLightTotal(), sleepBean.getSoberTotal());
//                        if (sleepTimeTotal==0) {
//                            continue;//无数据
//                        }
//
//                        //取出睡眠质量 睡眠质量 1234 作为睡眠的图表显示值
//                        int sleepQualityInt = SleepDataUtil.getSleepQualityInt(sleepBean);
//                        //设置该天有数据
//                        data.set(index, sleepQualityInt);//设置index 对应的值
//
//                        deepTotal += sleepBean.getDeepTotal();
//                        soberTotal += sleepBean.getSoberTotal();
//                        lightTotal += sleepBean.getLightTotal();
//
//                        int sleepTimeValidTotal = SleepDataUtil.getSleepValidTotal(sleepBean.getDeepTotal(), sleepBean.getLightTotal());
//                        sleepTimeValidTotalAll += sleepTimeValidTotal;
//
//                        sleepTimeMaxTotal = Math.max(sleepTimeMaxTotal, sleepTimeTotal);
//
//                        float sleepQualityFloat = SleepDataUtil.getSleepQualityFloat(sleepBean);
//                        sleepQualityAll += sleepQualityFloat;
//                        //质量大于良好 的次数,0.6以上为良好
//                        if (sleepQualityInt > 0.6f) {
//                            goodDaysCount++;
//                        }
//                        len++;
//                    }
//                    if (len==0) {//无详细数据
//                        return;
//                    }
//
//                    //有效睡眠时长
//                    sleepTimeValidHours += (sleepTimeValidTotalAll / 60);
//                    sleepTimeValidMinutes += (sleepTimeValidTotalAll % 60);
//                    //日均有效睡眠总时长
//                    int sleepTimeValidTotalAvg = sleepTimeValidTotalAll / len;
//                    sleepTimeAverageDailyTotalHours = (sleepTimeValidTotalAvg / 60);
//                    sleepTimeAverageDailyTotalMinutes = (sleepTimeValidTotalAvg % 60);
//                    if(sleepTimeMaxTotal>0) {
//                        //单次最长睡眠
//                        sleepTimeMaxSingleTotalHours = (sleepTimeMaxTotal / 60);
//                        sleepTimeMaxSingleTotalMinutes = (sleepTimeMaxTotal % 60);
//                    }
//                    //睡眠质量
//                    //求总平均
//                    float sleepQualityAvg = sleepQualityAll / len;
//                    //取得质量字符串
//                    quality = SleepDataUtil.getSleepQualityStr(sleepQualityAvg);
//                    //有效睡眠占比
//                    sleepValidPercent = goodDaysCount * 100 / len;
//
//                } catch (Exception w) {
//                    w.printStackTrace();
//                }
//            }
//
//            @Override
//            public void done() {
//                super.done();
//                switch (dateType) {
//                    case TYPE_WEEK:
//                        view.onUpdateWeekChartData(data);
//                        break;
//                    case TYPE_MONTH:
//                        view.onUpdateMonthChartData(data);
//                        break;
//                }
//                view.onUpdateItemChartData(
//                        goodDaysCount,
//                        sleepTimeValidHours,
//                        sleepTimeValidMinutes,
//                        sleepTimeAverageDailyTotalHours,
//                        sleepTimeAverageDailyTotalMinutes,
//                        sleepTimeMaxSingleTotalHours,
//                        sleepTimeMaxSingleTotalMinutes,
//                        sleepValidPercent,
//                        quality);
//            }
//        });
//    }

}
