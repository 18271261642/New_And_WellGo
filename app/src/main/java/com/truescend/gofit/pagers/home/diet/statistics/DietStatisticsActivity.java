package com.truescend.gofit.pagers.home.diet.statistics;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.sn.utils.tuple.TupleTwo;
import com.truescend.gofit.App;
import com.truescend.gofit.R;
import com.truescend.gofit.pagers.base.BaseActivity;
import com.truescend.gofit.pagers.home.bean.ItemDetailsTitle;
import com.truescend.gofit.pagers.home.bean.ItemStatus;
import com.truescend.gofit.utils.AppShareUtil;
import com.truescend.gofit.views.BarChartView;
import com.truescend.gofit.views.DietChartView;
import com.truescend.gofit.views.bean.LabelMonth;
import com.truescend.gofit.views.bean.LabelWeek;
import com.truescend.gofit.views.bean.LabelYear;

import java.util.List;

import butterknife.BindView;

/**
 * 作者:东芝(2018/11/22).
 * 功能:(减肥/卡路里)分析
 */
public class DietStatisticsActivity extends BaseActivity<DietStatisticsPresenterImpl, IDietStatisticsContract.IView> implements IDietStatisticsContract.IView {

    @BindView(R.id.dcvCaloryChart)
    DietChartView dcvCaloryChart;
    @BindView(R.id.ilDietStatisticsTitle)
    View ilDietStatisticsTitle;
    @BindView(R.id.tvDate)
    TextView tvDate;
    @BindView(R.id.ilDeficitAverageCalorie)
    View ilDeficitAverageCalorie;
    @BindView(R.id.ilDietStandardDays)
    View ilDietStandardDays;
    @BindView(R.id.ilQualifiedDays)
    View ilQualifiedDays;
    @BindView(R.id.ilInCalorieAverage)
    View ilInCalorieAverage;
    @BindView(R.id.ilInCalorieMax)
    View ilInCalorieMax;
    @BindView(R.id.ilInvalidDays)
    View ilInvalidDays;

    private ItemDetailsTitle dietStatisticsTitle;

    private ItemStatus deficitAverageCalorieStatus;
    private ItemStatus standardDaysStatus;
    private ItemStatus qualifiedDaysStatus;
    private ItemStatus inCalorieAverageStatus;
    private ItemStatus inCalorieMaxStatus;
    private ItemStatus invalidDaysStatus;


    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, DietStatisticsActivity.class));
    }

    @Override
    protected DietStatisticsPresenterImpl initPresenter() {
        return new DietStatisticsPresenterImpl(this);
    }

    @Override
    protected int initLayout() {
        return R.layout.activity_diet_statistics;
    }

    @Override
    protected void onCreateActivity(Bundle savedInstanceState) {
        initTitle();
        initCaloryChart();
        initItems();
        getPresenter().requestLoadWeekChart(App.getSelectedCalendar());
    }

    /**
     * 初始化标题
     */
    private void initTitle() {
        getTitleLayout().setTitleShow(false);
        dietStatisticsTitle = new ItemDetailsTitle(ilDietStatisticsTitle);
        dietStatisticsTitle.setFirstText(R.string.unit_week);
        dietStatisticsTitle.setSecondText(R.string.unit_month);
        dietStatisticsTitle.setThirdText(R.string.unit_year);

        dietStatisticsTitle.setLeftIconOnClickListener(onClick);
        dietStatisticsTitle.setRightIconOnClickListener(onClick);
        dietStatisticsTitle.setFirstOnClickListener(onClick);
        dietStatisticsTitle.setSecondOnClickListener(onClick);
        dietStatisticsTitle.setThirdOnClickListener(onClick);
    }


    private void initCaloryChart() {
        dcvCaloryChart.setDrawLabel(true);
        dcvCaloryChart.setDrawBorder(true);
        dcvCaloryChart.setDrawLabelLimit(false);
        dcvCaloryChart.setDrawLimitLine(false);
        dcvCaloryChart.setDrawZeroLimitLine(true);
        dcvCaloryChart.setBarColor(0xFF000000);

    }


    /**
     * 初始化视图
     */
    private void initItems() {
        deficitAverageCalorieStatus = new ItemStatus(ilDeficitAverageCalorie);
        standardDaysStatus = new ItemStatus(ilDietStandardDays);
        qualifiedDaysStatus = new ItemStatus(ilQualifiedDays);
        inCalorieAverageStatus = new ItemStatus(ilInCalorieAverage);
        inCalorieMaxStatus = new ItemStatus(ilInCalorieMax);
        invalidDaysStatus = new ItemStatus(ilInvalidDays);


        setItemType(DietStatisticsPresenterImpl.TYPE_WEEK);
    }


    @Override
    public void onUpdateWeekChartData(List<TupleTwo<Integer, Integer>> data) {
        dcvCaloryChart.reset();
        dcvCaloryChart.setBarWidth(BarChartView.BAR_WIDTH_MEDIUM);
        dcvCaloryChart.setDataType(new LabelWeek());
        dcvCaloryChart.setData(data);
        updateCaloryChartLimitLine();
    }


    @Override
    public void onUpdateMonthChartData(List<TupleTwo<Integer, Integer>> data) {
        dcvCaloryChart.reset();
        dcvCaloryChart.setBarWidth(DietChartView.BAR_WIDTH_MEDIUM * 0.7f);
        dcvCaloryChart.setDataType(new LabelMonth());
        dcvCaloryChart.setData(data);
        updateCaloryChartLimitLine();
    }

    @Override
    public void onUpdateYearChartData(List<TupleTwo<Integer, Integer>> data) {
        dcvCaloryChart.reset();
        dcvCaloryChart.setBarWidth(BarChartView.BAR_WIDTH_MEDIUM);
        dcvCaloryChart.setDataType(new LabelYear());
        dcvCaloryChart.setData(data);
        updateCaloryChartLimitLine();
    }

    private void updateCaloryChartLimitLine() {

        //获取data中的最大值
        int round = Math.round(dcvCaloryChart.getMax());
        if (round > 0) {
            String maxValue = String.valueOf(round);
            int head = Integer.parseInt(maxValue.substring(0, 1)) + 1;
            int value = Integer.parseInt(String.format("%d%0" + (maxValue.length() - 1) + "d", head, 1)) - 1;
            //计算每一格的label
            int every = value / head;
            float[] lines = new float[head + 1];
            String[] label = new String[head + 1];
            for (int i = 0; i < head + 1; i++) {
                lines[i] = every * i;
                label[i] = String.valueOf(every * i);
            }
            dcvCaloryChart.setLimitLine(lines, label);
        } else {
            dcvCaloryChart.setLimitLine(new float[]{0, 2000}, new String[]{"0", "2000"});
        }
    }


    @Override
    public void onUpdateDateRange(String dateFromAndTo) {
        tvDate.setText(dateFromAndTo);
    }

    @Override
    public void onUpdateItemChartData(int dateType, CharSequence deficitAverageCalorie, String standardDays, String qualifiedDays, String inCalorieAverage, String inCalorieMax, String invalidDays) {
        setItemType(dateType);
        deficitAverageCalorieStatus.setTitle(deficitAverageCalorie);
        standardDaysStatus.setTitle(standardDays);
        qualifiedDaysStatus.setTitle(qualifiedDays);
        inCalorieAverageStatus.setTitle(inCalorieAverage);
        inCalorieMaxStatus.setTitle(inCalorieMax);
        invalidDaysStatus.setTitle(invalidDays);
    }

    private void setItemType(int dateType) {
        switch (dateType) {
            case DietStatisticsPresenterImpl.TYPE_WEEK:
            case DietStatisticsPresenterImpl.TYPE_MONTH:
            case DietStatisticsPresenterImpl.TYPE_YEAR:
                deficitAverageCalorieStatus.setSubTitle(R.string.content_average_daily_calorie_deficit);
                standardDaysStatus.setSubTitle(R.string.content_standard_days);
                qualifiedDaysStatus.setSubTitle(R.string.content_intake_of_qualified_days);
                inCalorieAverageStatus.setSubTitle(R.string.content_average_daily_intake_of_calories);
                inCalorieMaxStatus.setSubTitle(R.string.content_the_highest_intake_of_calories);
                invalidDaysStatus.setSubTitle(R.string.content_not_clock_in_number_of_days);
                break;

        }
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
                    AppShareUtil.shareCapture(DietStatisticsActivity.this);
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


}
