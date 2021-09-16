package com.truescend.gofit.pagers.home.sport;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.truescend.gofit.App;
import com.truescend.gofit.R;
import com.truescend.gofit.pagers.base.BaseActivity;
import com.truescend.gofit.pagers.home.bean.ItemDetailsTitle;
import com.truescend.gofit.pagers.home.bean.ItemStatus;
import com.truescend.gofit.utils.AppShareUtil;
import com.truescend.gofit.views.BarChartView;
import com.truescend.gofit.views.TitleLayout;
import com.truescend.gofit.views.bean.LabelMonth;
import com.truescend.gofit.views.bean.LabelWeek;
import com.truescend.gofit.views.bean.LabelYear;

import java.util.List;

import butterknife.BindView;

/**
 * 功能：运动步数详情页面
 * Author:Created by 泽鑫 on 2017/12/7 10:29.
 */

public class SportActivity extends BaseActivity<SportPresenterImpl, ISportContract.IView> implements ISportContract.IView {
    @BindView(R.id.bcvStepChart)
    BarChartView bcvStepChart;
    @BindView(R.id.tlTitle)
    TitleLayout ilTitle;
    @BindView(R.id.ilSportTitle)
    View ilSportTitle;
    @BindView(R.id.ilSportStandardDays)
    View ilSportStandardDays;
    @BindView(R.id.ilSportDailySteps)
    View ilSportDailySteps;
    @BindView(R.id.ilSportDailyConsume)
    View ilSportDailyConsume;
    @BindView(R.id.ilSportTotalDistance)
    View ilSportTotalDistance;
    @BindView(R.id.ilSportDailyDistance)
    View ilSportDailyDistance;
    @BindView(R.id.ilSportTotalSteps)
    View ilSportTotalSteps;
    @BindView(R.id.tvDate)
    TextView tvDate;

    private ItemDetailsTitle sportDetailsTitle;//界面标题

    private ItemStatus standardDaysStatus;//达标天数
    private ItemStatus stepAverageTotalStatus;//日均步数
    private ItemStatus calorieAverageTotalStatus;//日均消耗
    private ItemStatus distanceTotalStatus;//总距离
    private ItemStatus distanceAverageTotalStatus;//日均距离
    private ItemStatus stepTotalStatus;//总步数


    @Override
    protected SportPresenterImpl initPresenter() {
        return new SportPresenterImpl(this);
    }

    @Override
    protected int initLayout() {
        return R.layout.activity_sport;
    }

    @Override
    protected void onCreateActivity(Bundle savedInstanceState) {


        initTitle();
        initItems();
        initStepChart();

        getPresenter().requestLoadWeekChart(App.getSelectedCalendar());
    }

    private void initStepChart() {
        bcvStepChart.setDrawLabel(false);
        bcvStepChart.setDrawBorder(false);
        bcvStepChart.setDrawLabelLimit(false);
        bcvStepChart.setDrawLimitLine(false);
        bcvStepChart.setDrawZeroLimitLine(true);
        bcvStepChart.setBarColor(0xFF000000);

    }


    @Override
    public void onUpdateWeekChartData(List<Integer> data, List<Boolean> standardsPosData) {
        bcvStepChart.setBarWidth(BarChartView.BAR_WIDTH_MEDIUM);
        bcvStepChart.setDataType(new LabelWeek());
        bcvStepChart.setData(data);
        //达标
        bcvStepChart.setStandardsPosData(standardsPosData);
    }

    @Override
    public void onUpdateMonthChartData(List<Integer> data, List<Boolean> standardsPosData) {
        bcvStepChart.setBarWidth(BarChartView.BAR_WIDTH_MEDIUM);
        bcvStepChart.setDataType(new LabelMonth());
        bcvStepChart.setData(data);
        //达标
        bcvStepChart.setStandardsPosData(standardsPosData);
    }

    @Override
    public void onUpdateYearChartData(List<Integer> data, List<Boolean> standardsPosData) {
        bcvStepChart.setBarWidth(BarChartView.BAR_WIDTH_MEDIUM);
        bcvStepChart.setDataType(new LabelYear());
        bcvStepChart.setData(data);
        //达标
        bcvStepChart.setStandardsPosData(standardsPosData);
    }

    @Override
    public void onUpdateDateRange(String dateFromAndTo) {
        tvDate.setText(dateFromAndTo);
    }
    @Override
    public void onUpdateItemChartData(int dateType, String standardDays, String stepTotal, String stepAverageTotal, String calorieAverageTotal, String distanceTotal, String distanceAverageTotal) {
        setItemType(dateType);
        standardDaysStatus.setTitle(standardDays);
        stepAverageTotalStatus.setTitle(stepAverageTotal);
        calorieAverageTotalStatus.setTitle(calorieAverageTotal);
        distanceTotalStatus.setTitle(distanceTotal);
        distanceAverageTotalStatus.setTitle(distanceAverageTotal);
        stepTotalStatus.setTitle(stepTotal);

    }



    /**
     * 初始化标题
     */
    private void initTitle() {
        ilTitle.setTitleShow(false);
        sportDetailsTitle = new ItemDetailsTitle(ilSportTitle);
        sportDetailsTitle.setFirstText(R.string.unit_week);
        sportDetailsTitle.setSecondText(R.string.unit_month);
        sportDetailsTitle.setThirdText(R.string.unit_year);

        sportDetailsTitle.setLeftIconOnClickListener(onClick);
        sportDetailsTitle.setRightIconOnClickListener(onClick);
        sportDetailsTitle.setFirstOnClickListener(onClick);
        sportDetailsTitle.setSecondOnClickListener(onClick);
        sportDetailsTitle.setThirdOnClickListener(onClick);
    }

    /**
     * 点击事件
     */
    View.OnClickListener onClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ivDetailsTitleLeftIcon:
                    onBackPressed();
                    break;
                case R.id.ivDetailsTitleRightIcon:
                    AppShareUtil.shareCapture(SportActivity.this);
                    break;
                case R.id.rbDetailsTitleFirst:
                    getPresenter().requestLoadWeekChart(App.getSelectedCalendar());
                    break;
                case R.id.rbDetailsTitleSecond:
                    getPresenter().requestLoadMonthChart(App.getSelectedCalendar());
                    break;
                case R.id.rbDetailsTitleThird:
                    getPresenter().requestLoadYearChart(App.getSelectedCalendar());
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 初始化视图
     */
    private void initItems() {
        standardDaysStatus = new ItemStatus(ilSportStandardDays);

        stepAverageTotalStatus = new ItemStatus(ilSportDailySteps);

        calorieAverageTotalStatus = new ItemStatus(ilSportDailyConsume);

        distanceAverageTotalStatus = new ItemStatus(ilSportDailyDistance);

        stepTotalStatus = new ItemStatus(ilSportTotalSteps);

        distanceTotalStatus = new ItemStatus(ilSportTotalDistance);

        setItemType(SportPresenterImpl.TYPE_WEEK);
    }


    private void setItemType(int dateType) {
        switch (dateType) {
            case SportPresenterImpl.TYPE_WEEK:
            case SportPresenterImpl.TYPE_MONTH:
            case SportPresenterImpl.TYPE_YEAR:

                standardDaysStatus.setSubTitle(R.string.content_standard_days);
                stepAverageTotalStatus.setSubTitle(R.string.content_avg_day_steps);
                calorieAverageTotalStatus.setSubTitle(R.string.content_avg_day_consume);
                distanceAverageTotalStatus.setSubTitle(R.string.content_avg_day_distance);
                stepTotalStatus.setSubTitle(R.string.content_total_steps);
                distanceTotalStatus.setSubTitle(R.string.content_total_distance);
                break;
//            case SportPresenterImpl.TYPE_YEAR:
//                standardDaysStatus.setSubTitle(R.string.content_standard_days);
//                stepAverageTotalStatus.setSubTitle(R.string.content_avg_month_steps);
//                calorieAverageTotalStatus.setSubTitle(R.string.content_avg_month_consume);
//                distanceAverageTotalStatus.setSubTitle(R.string.content_avg_month_distance);
//                stepTotalStatus.setSubTitle(R.string.content_total_steps);
//                distanceTotalStatus.setSubTitle(R.string.content_total_distance);
//                break;
        }
    }


}
