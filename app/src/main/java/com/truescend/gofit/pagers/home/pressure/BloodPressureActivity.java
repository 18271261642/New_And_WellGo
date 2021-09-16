package com.truescend.gofit.pagers.home.pressure;

import android.os.Bundle;

import android.view.View;
import android.widget.TextView;

import com.truescend.gofit.App;
import com.truescend.gofit.R;
import com.truescend.gofit.pagers.base.BaseActivity;
import com.truescend.gofit.pagers.home.bean.ItemDetails;
import com.truescend.gofit.pagers.home.bean.ItemDetailsTitle;
import com.truescend.gofit.pagers.home.bean.ItemStatus;
import com.truescend.gofit.pagers.home.pressure.adapter.BloodPressureDetailAdapter;
import com.truescend.gofit.pagers.home.pressure.bean.BloodPressureDetailItem;
import com.truescend.gofit.utils.AppShareUtil;
import com.truescend.gofit.utils.RecycleViewUtil;
import com.truescend.gofit.views.BloodPressureChartView;
import com.truescend.gofit.views.TitleLayout;
import com.truescend.gofit.views.bean.Label24H;
import com.truescend.gofit.views.bean.LabelMonth;
import com.truescend.gofit.views.bean.LabelWeek;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;

/**
 * 功能：血压功能界面
 * Author:Created by 泽鑫 on 2017/12/7 18:20.
 */

public class BloodPressureActivity extends BaseActivity<BloodPressurePresenterImpl, IBloodPressureContract.IView> implements IBloodPressureContract.IView {
    @BindView(R.id.tlTitle)
    TitleLayout tlTitle;
    @BindView(R.id.ilBloodPressureTitle)
    View ilBloodPressureTitle;
    @BindView(R.id.ilBloodPressureAverageDiastolic)
    View ilBloodPressureAverageDiastolic;
    @BindView(R.id.ilBloodPressureAverageSystolic)
    View ilBloodPressureAverageSystolic;
    @BindView(R.id.ilBloodPressureDetails)
    View ilBloodPressureDetails;
    @BindView(R.id.bcvBloodPressureChart)
    BloodPressureChartView bcvBloodPressureChart;
    @BindView(R.id.rvDetails)
    RecyclerView rvDetails;
    @BindView(R.id.tvDate)
    TextView tvDate;

    private ItemDetailsTitle bloodPressureTitle;
    private ItemStatus averageDiastolicStatus;
    private ItemStatus averageSystolicStatus;
    private ItemDetails bloodPressureDetails;
    private BloodPressureDetailAdapter bloodPressureDetailAdapter;

    @Override
    protected BloodPressurePresenterImpl initPresenter() {
        return new BloodPressurePresenterImpl(this);
    }

    @Override
    protected int initLayout() {
        return R.layout.activity_blood_pressure;
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
        bloodPressureDetailAdapter = new BloodPressureDetailAdapter(this);
        RecycleViewUtil.setAdapter(rvDetails, bloodPressureDetailAdapter);
    }

    private void initBloodOxygenChart() {
        bcvBloodPressureChart.setDrawLabel(true);
        bcvBloodPressureChart.setDrawBorder(true);
        bcvBloodPressureChart.setDrawLabelLimit(true);
        bcvBloodPressureChart.setDrawLimitLine(false);
        bcvBloodPressureChart.setDrawZeroLimitLine(true);
        bcvBloodPressureChart.setBarWidth(BloodPressureChartView.BAR_WIDTH_NORMAL);
        bcvBloodPressureChart.setColors(getResources().getColor(R.color.colorDiastolic),getResources().getColor(R.color.colorSystolic));
        //分割线
        bcvBloodPressureChart.setLimitLine(0, 50, 100, 150, 200);
    }

    @Override
    public void onUpdateTodayChartData(List<BloodPressureChartView.BloodPressureItem> data) {
        bcvBloodPressureChart.setDataType(new Label24H());
        bcvBloodPressureChart.setData(data);
    }

    @Override
    public void onUpdateWeekChartData(List<BloodPressureChartView.BloodPressureItem> data) {
        bcvBloodPressureChart.setDataType(new LabelWeek());
        bcvBloodPressureChart.setData(data);
    }

    @Override
    public void onUpdateMonthChartData(List<BloodPressureChartView.BloodPressureItem> data) {

        bcvBloodPressureChart.setDataType(new LabelMonth());
        bcvBloodPressureChart.setData(data);
    }

    @Override
    public void onUpdateStatisticsData(CharSequence diastolic, CharSequence systolic) {
        averageDiastolicStatus.setTitle(diastolic);
        averageSystolicStatus.setTitle(systolic);
    }

    @Override
    public void onUpdateDetailListData(List<BloodPressureDetailItem> items) {
        if (bloodPressureDetailAdapter != null) {
            bloodPressureDetailAdapter.setList(items);
        }
    }

    @Override
    public void onUpdateDateRange(String dateFromAndTo) {
        tvDate.setText(dateFromAndTo);
    }

    private void initTitle() {
        tlTitle.setTitleShow(false);
        bloodPressureTitle = new ItemDetailsTitle(ilBloodPressureTitle);
        bloodPressureTitle.setFirstText(R.string.content_today);
        bloodPressureTitle.setSecondText(R.string.content_this_week);
        bloodPressureTitle.setThirdText(R.string.content_this_month);
        bloodPressureTitle.setRightIconOnClickListener(onClick);
        bloodPressureTitle.setLeftIconOnClickListener(onClick);
        bloodPressureTitle.setFirstOnClickListener(onClick);
        bloodPressureTitle.setSecondOnClickListener(onClick);
        bloodPressureTitle.setThirdOnClickListener(onClick);
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
                    AppShareUtil.shareCapture(BloodPressureActivity.this);
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
        averageDiastolicStatus = new ItemStatus(ilBloodPressureAverageDiastolic);
        averageDiastolicStatus.setSubTitle(R.string.content_average_pressure_diastolic);//填充格子的标题

        averageSystolicStatus = new ItemStatus(ilBloodPressureAverageSystolic);
        averageSystolicStatus.setSubTitle(R.string.content_average_pressure_systolic);//填充格子的标题

        bloodPressureDetails = new ItemDetails(ilBloodPressureDetails);
        bloodPressureDetails.setIcon(getResources().getDrawable(R.mipmap.icon_blood_pressure));
        bloodPressureDetails.setTitle(R.string.blood_pressure_details);
        bloodPressureDetails.setHeightText(R.string.content_abnormal_diastolic);
        bloodPressureDetails.setLowText(R.string.content_abnormal_systolic);

        ilBloodPressureAverageDiastolic.setBackgroundColor(getResources().getColor(R.color.transparent));//填充背景透明
        ilBloodPressureAverageSystolic.setBackgroundColor(getResources().getColor(R.color.transparent));//填充背景透明



    }


}
