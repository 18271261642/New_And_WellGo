package com.truescend.gofit.pagers.home.heart;

import android.os.Bundle;

import android.view.View;
import android.widget.TextView;

import com.truescend.gofit.App;
import com.truescend.gofit.R;
import com.truescend.gofit.pagers.base.BaseActivity;
import com.truescend.gofit.pagers.home.bean.ItemDetails;
import com.truescend.gofit.pagers.home.bean.ItemDetailsTitle;
import com.truescend.gofit.pagers.home.bean.ItemStatus;
import com.truescend.gofit.pagers.home.heart.adapter.HeartRateDetailAdapter;
import com.truescend.gofit.pagers.home.heart.bean.HeartRateDetailItem;
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
 * 功能：心率功能界面
 * Author:Created by 泽鑫 on 2017/12/7 16:38.
 *
 */

public class HeartActivity extends BaseActivity<HeartPresenterImpl, IHeartContract.IView> implements IHeartContract.IView {
    @BindView(R.id.tlTitle)
    TitleLayout tlTitle;
    @BindView(R.id.ilHeartTitle)
    View ilHeartTitle;
    @BindView(R.id.ilItemCenter)
    View ilHeartAverageHeart;
    @BindView(R.id.ilItemLeft)
    View ilHeartHighestHeart;
    @BindView(R.id.ilItemRight)
    View ilHeartMinimumHeart;
    @BindView(R.id.ilHeartDetails)
    View ilHeartDetails;
    @BindView(R.id.bcvHeartRateChart)
    BarChartView bcvHeartRateChart;
    @BindView(R.id.rvDetails)
    RecyclerView rvDetails;
    @BindView(R.id.tvDate)
    TextView tvDate;

    private ItemDetailsTitle heartTitle;//标题
    private ItemStatus averageHeartStatus;//平均心率
    private ItemStatus highestHeartStatus;//最高心率
    private ItemStatus minimumHeartStatus;//最低心率
    private ItemDetails heartDetails;//心率详情
    private HeartRateDetailAdapter heartRateDetailAdapter;

    @Override
    protected HeartPresenterImpl initPresenter() {
        return new HeartPresenterImpl(this);
    }

    @Override
    protected int initLayout() {
        return R.layout.activity_heart;
    }

    @Override
    protected void onCreateActivity(Bundle savedInstanceState) {
        initTitle();
        initView();

        initHeartRateChart();
        initDetailsList();
        getPresenter().requestLoadTodayChart(App.getSelectedCalendar());


    }

    private void initDetailsList() {
        heartRateDetailAdapter = new HeartRateDetailAdapter(this);
        RecycleViewUtil.setAdapter(rvDetails, heartRateDetailAdapter);
    }

    private void initHeartRateChart() {
        bcvHeartRateChart.setDrawLabel(true);
        bcvHeartRateChart.setDrawBorder(true);
        bcvHeartRateChart.setDrawLabelLimit(true);
        bcvHeartRateChart.setDrawLimitLine(false);
        bcvHeartRateChart.setDrawZeroLimitLine(true);
        bcvHeartRateChart.setBarColor(0xFFFA1011);


        //分割线
        bcvHeartRateChart.setLimitLine(new float[]{0, 50, 100, 150, 200}, new String[]{"", "50", "100", "150", "200"});
    }

    @Override
    public void onUpdateTodayChartData(List<Integer> data) {
        bcvHeartRateChart.setBarWidth(BarChartView.BAR_WIDTH_NORMAL);
        bcvHeartRateChart.setDataType(new Label24H());
        bcvHeartRateChart.setData(data);
    }

    @Override
    public void onUpdateWeekChartData(List<Integer> data) {
        bcvHeartRateChart.setBarWidth(BarChartView.BAR_WIDTH_MEDIUM);
        bcvHeartRateChart.setDataType(new LabelWeek());
        bcvHeartRateChart.setData(data);
    }

    @Override
    public void onUpdateMonthChartData(List<Integer> data) {
        bcvHeartRateChart.setBarWidth(BarChartView.BAR_WIDTH_MEDIUM);
        bcvHeartRateChart.setDataType(new LabelMonth());
        bcvHeartRateChart.setData(data);
    }

    @Override
    public void onUpdateStatisticsData(CharSequence max, CharSequence avg, CharSequence min) {
        averageHeartStatus.setTitle(avg);
        highestHeartStatus.setTitle(max);
        minimumHeartStatus.setTitle(min);
    }

    @Override
    public void onUpdateDetailListData(List<HeartRateDetailItem> items) {
        if (heartRateDetailAdapter != null) {
            heartRateDetailAdapter.setList(items);
        }
    }

    @Override
    public void onUpdateDateRange(String dateFromAndTo) {
        tvDate.setText(dateFromAndTo);
    }


    private void initTitle() {
        tlTitle.setTitleShow(false);
        heartTitle = new ItemDetailsTitle(ilHeartTitle);
        heartTitle.setFirstText(R.string.content_today);
        heartTitle.setSecondText(R.string.content_this_week);
        heartTitle.setThirdText(R.string.content_this_month);
        heartTitle.setRightIconOnClickListener(onClick);
        heartTitle.setLeftIconOnClickListener(onClick);
        heartTitle.setFirstOnClickListener(onClick);
        heartTitle.setSecondOnClickListener(onClick);
        heartTitle.setThirdOnClickListener(onClick);
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
                    AppShareUtil.shareCapture(HeartActivity.this);
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
        averageHeartStatus = new ItemStatus(ilHeartAverageHeart);
        averageHeartStatus.setSubTitle(R.string.content_average_heart);//填充格子的标题

        highestHeartStatus = new ItemStatus(ilHeartHighestHeart);
        highestHeartStatus.setSubTitle(R.string.content_highest_heart);//填充格子的标题

        minimumHeartStatus = new ItemStatus(ilHeartMinimumHeart);
        minimumHeartStatus.setSubTitle(R.string.content_minimum_heart);//填充格子的标题

        heartDetails = new ItemDetails(ilHeartDetails);
        heartDetails.setIcon(getResources().getDrawable(R.mipmap.icon_heart));
        heartDetails.setTitle(R.string.heart_details);
        heartDetails.setHeightText(R.string.content_heart_too_high);
        heartDetails.setLowText(R.string.content_heart_too_low);

        ilHeartAverageHeart.setBackgroundColor(getResources().getColor(R.color.transparent));//填充背景透明
        ilHeartHighestHeart.setBackgroundColor(getResources().getColor(R.color.transparent));//填充背景透明
        ilHeartMinimumHeart.setBackgroundColor(getResources().getColor(R.color.transparent));//填充背景透明

    }


}
