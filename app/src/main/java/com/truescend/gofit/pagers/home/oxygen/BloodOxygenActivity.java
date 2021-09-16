package com.truescend.gofit.pagers.home.oxygen;

import android.os.Bundle;

import android.view.View;
import android.widget.TextView;

import com.truescend.gofit.App;
import com.truescend.gofit.R;
import com.truescend.gofit.pagers.base.BaseActivity;
import com.truescend.gofit.pagers.home.bean.ItemDetails;
import com.truescend.gofit.pagers.home.bean.ItemDetailsTitle;
import com.truescend.gofit.pagers.home.bean.ItemStatus;
import com.truescend.gofit.pagers.home.oxygen.adapter.BloodOxygenDetailAdapter;
import com.truescend.gofit.pagers.home.oxygen.bean.BloodOxygenDetailItem;
import com.truescend.gofit.utils.AppShareUtil;
import com.truescend.gofit.utils.RecycleViewUtil;
import com.truescend.gofit.views.BarChartView;
import com.truescend.gofit.views.TitleLayout;
import com.truescend.gofit.views.bean.Label24H;
import com.truescend.gofit.views.bean.LabelMonth;
import com.truescend.gofit.views.bean.LabelWeek;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;

/**
 * 功能：血氧功能界面
 * Author:Created by 泽鑫 on 2017/12/8 10:21.
 */

public class BloodOxygenActivity extends BaseActivity<BloodOxygenPresenterImpl, IBloodOxygenContract.IPresenter> implements IBloodOxygenContract.IView {
    @BindView(R.id.tlTitle)
    TitleLayout tlTitle;
    @BindView(R.id.ilBloodOxygenTitle)
    View ilBloodOxygenTitle;
    @BindView(R.id.ilBloodOxygenMaximum)
    View ilBloodOxygenMaximum;
    @BindView(R.id.ilBloodOxygenAverage)
    View ilBloodOxygenAverage;
    @BindView(R.id.ilBloodOxygenMinimum)
    View ilBloodOxygenMinimum;
    @BindView(R.id.ilBloodOxygenDetails)
    View ilBloodOxygenDetails;
    @BindView(R.id.bcvBloodOxygenChart)
    BarChartView bcvBloodOxygenChart;
    @BindView(R.id.rvDetails)
    RecyclerView rvDetails;
    @BindView(R.id.tvDate)
    TextView tvDate;

    private ItemDetailsTitle bloodOxygenTitle;
    private ItemStatus maximumValueStatus;
    private ItemStatus averageValueStatus;
    private ItemStatus minimumValueStatus;
    private ItemDetails bloodOxygenDetails;
    private BloodOxygenDetailAdapter bloodOxygenDetailAdapter;

    @Override
    protected BloodOxygenPresenterImpl initPresenter() {
        return new BloodOxygenPresenterImpl(this);
    }

    @Override
    protected int initLayout() {
        return R.layout.activity_blood_oxygen;
    }

    @Override
    protected void onCreateActivity(Bundle savedInstanceState) {
        initTitle();
        initView();
        initBloodOxygenChart();
        initDetailsList();
        getPresenter().requestLoadTodayChart(App.getSelectedCalendar());
    }
    private void initDetailsList() {
        bloodOxygenDetailAdapter = new BloodOxygenDetailAdapter(this);
        RecycleViewUtil.setAdapter(rvDetails, bloodOxygenDetailAdapter);
    }

    private void initBloodOxygenChart() {
        bcvBloodOxygenChart.setDrawLabel(true);
        bcvBloodOxygenChart.setDrawBorder(true);
        bcvBloodOxygenChart.setDrawLabelLimit(true);
        bcvBloodOxygenChart.setDrawLimitLine(false);
        bcvBloodOxygenChart.setDrawZeroLimitLine(true);
        bcvBloodOxygenChart.setBarColor(getResources().getColor(R.color.colorBloodOxygen));

        //分割线
        bcvBloodOxygenChart.setLimitLine(new float[]{90, 92, 94, 96, 98, 100}, new String[]{"90", "92", "94", "96","98", "100"});
    }

    @Override
    public void onUpdateTodayChartData(List<Integer> data) {
        bcvBloodOxygenChart.setBarWidth(BarChartView.BAR_WIDTH_NORMAL);
        bcvBloodOxygenChart.setDataType(new Label24H());
        bcvBloodOxygenChart.setData(data);
    }

    @Override
    public void onUpdateWeekChartData(List<Integer> data) {
        bcvBloodOxygenChart.setBarWidth(BarChartView.BAR_WIDTH_MEDIUM);
        bcvBloodOxygenChart.setDataType(new LabelWeek());
        bcvBloodOxygenChart.setData(data);
    }

    @Override
    public void onUpdateMonthChartData(List<Integer> data) {
        bcvBloodOxygenChart.setBarWidth(BarChartView.BAR_WIDTH_MEDIUM);
        bcvBloodOxygenChart.setDataType(new LabelMonth());
        bcvBloodOxygenChart.setData(data);
    }

    @Override
    public void onUpdateStatisticsData(CharSequence max, CharSequence avg, CharSequence min) {
        maximumValueStatus.setTitle(max);
        averageValueStatus.setTitle(avg);
        minimumValueStatus.setTitle(min);
    }
    @Override
    public void onUpdateDetailListData(List<BloodOxygenDetailItem> items) {
        if (bloodOxygenDetailAdapter != null) {
            bloodOxygenDetailAdapter.setList(items);
        }
    }


    @Override
    public void onUpdateDateRange(String dateFromAndTo) {
        tvDate.setText(dateFromAndTo);
    }



    private void initTitle() {
        tlTitle.setTitleShow(false);
        bloodOxygenTitle = new ItemDetailsTitle(ilBloodOxygenTitle);
        bloodOxygenTitle.setFirstText(R.string.content_today);
        bloodOxygenTitle.setSecondText(R.string.content_this_week);
        bloodOxygenTitle.setThirdText(R.string.content_this_month);
        bloodOxygenTitle.setRightIconOnClickListener(onClick);
        bloodOxygenTitle.setLeftIconOnClickListener(onClick);
        bloodOxygenTitle.setFirstOnClickListener(onClick);
        bloodOxygenTitle.setSecondOnClickListener(onClick);
        bloodOxygenTitle.setThirdOnClickListener(onClick);
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
                    AppShareUtil.shareCapture(BloodOxygenActivity.this);
                    break;
                case R.id.rbDetailsTitleFirst:
                    getPresenter().requestLoadTodayChart(App.getSelectedCalendar());
                    break;
                case R.id.rbDetailsTitleSecond:
                    getPresenter().requestLoadWeekChart(App.getSelectedCalendar());
                    break;
                case R.id.rbDetailsTitleThird:
                    getPresenter().requestLoadMonthChart(App.getSelectedCalendar());
                    break;
                default:
                    break;
            }
        }
    };

    private void initView() {
        maximumValueStatus = new ItemStatus(ilBloodOxygenMaximum);
        maximumValueStatus.setSubTitle(R.string.content_max_value);//填充格子的标题

        averageValueStatus = new ItemStatus(ilBloodOxygenAverage);
        averageValueStatus.setSubTitle(R.string.content_avg_value);//填充格子的标题

        minimumValueStatus = new ItemStatus(ilBloodOxygenMinimum);
        minimumValueStatus.setSubTitle(R.string.content_min_value);//填充格子的标题

        bloodOxygenDetails = new ItemDetails(ilBloodOxygenDetails);
        bloodOxygenDetails.setIcon(getResources().getDrawable(R.mipmap.icon_blood_oxygen));//填充血氧图标
        bloodOxygenDetails.setTitle(R.string.blood_oxygen_details);
        bloodOxygenDetails.setRedIconVisibility(View.GONE);//隐藏小红点
        bloodOxygenDetails.setYellowIconVisibility(View.GONE);//隐藏小黄点
        bloodOxygenDetails.setHeightTextVisibility(View.GONE);
        bloodOxygenDetails.setLowTextVisibility(View.GONE);

        ilBloodOxygenMaximum.setBackgroundColor(getResources().getColor(R.color.transparent));//填充背景透明
        ilBloodOxygenAverage.setBackgroundColor(getResources().getColor(R.color.transparent));//填充背景透明
        ilBloodOxygenMinimum.setBackgroundColor(getResources().getColor(R.color.transparent));//填充背景透明


    }


}
