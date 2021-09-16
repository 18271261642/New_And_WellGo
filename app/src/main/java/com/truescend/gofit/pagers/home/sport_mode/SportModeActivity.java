package com.truescend.gofit.pagers.home.sport_mode;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.DiffUtil;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.truescend.gofit.App;
import com.truescend.gofit.R;
import com.truescend.gofit.pagers.base.BaseActivity;
import com.truescend.gofit.pagers.home.bean.ItemDetailsTitle;
import com.truescend.gofit.pagers.home.sport_mode.adapter.SportModeAdapter;
import com.truescend.gofit.pagers.home.sport_mode.bean.DiffCallBack;
import com.truescend.gofit.pagers.home.sport_mode.bean.QuintOutInterpolator;
import com.truescend.gofit.pagers.home.sport_mode.bean.SportModeDetailItem;
import com.truescend.gofit.utils.AppShareUtil;
import com.truescend.gofit.utils.RecycleViewUtil;
import com.truescend.gofit.views.PieChartView;
import com.truescend.gofit.views.TitleLayout;
import com.truescend.gofit.views.overscroll.OverScrollDecoratorHelper;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.OnClick;
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;

/**
 * 作者:东芝(2019/06/01).
 * 功能:运动模式
 */
public class SportModeActivity extends BaseActivity<SportModePresenterImpl, ISportModeContract.IView> implements ISportModeContract.IView, View.OnClickListener, PieChartView.OnChartValueSelectedListener {

    @BindView(R.id.ivShowLastTimeChart)
    ImageView ivShowLastTimeChart;
    @BindView(R.id.tvDate)
    TextView tvDate;
    @BindView(R.id.mPieChartView)
    PieChartView mPieChartView;
    @BindView(R.id.ilSportTitle)
    View ilSportTitle;
    @BindView(R.id.rvSportModeList)
    RecyclerView rvSportModeList;
    private SportModeAdapter mSportModeAdapter;
    private List<SportModeDetailItem> items = new ArrayList<>();
    private boolean reloadInsertAnimation = true;

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, SportModeActivity.class));
    }

    @Override
    protected SportModePresenterImpl initPresenter() {
        return new SportModePresenterImpl(this);
    }

    @Override
    protected int initLayout() {
        return R.layout.activity_sport_mode;
    }

    @Override
    protected void onCreateTitle(TitleLayout titleLayout) {
        titleLayout.setTitleShow(false);
    }

    @Override
    protected void onCreateActivity(Bundle savedInstanceState) {
        initTitle();
        setUpList();
        setUpPieChart();
        getPresenter().requestLoadWeekChart(App.getSelectedCalendar());
    }

    private void setUpPieChart() {
        mPieChartView.setOnPieSelectedListener(this);
    }

    private void setUpList() {
        mSportModeAdapter = new SportModeAdapter(this, items);
        RecycleViewUtil.setAdapter(rvSportModeList, mSportModeAdapter);
        SlideInLeftAnimator animator = new SlideInLeftAnimator();
        animator.setInterpolator(new QuintOutInterpolator());
        animator.setAddDuration(800L);
        animator.setRemoveDuration(200L);
        animator.setChangeDuration(800L);
        rvSportModeList.setItemAnimator(animator);
        OverScrollDecoratorHelper.setUpOverScroll(rvSportModeList, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);
    }

    private void initTitle() {
        ItemDetailsTitle sportDetailsTitle = new ItemDetailsTitle(ilSportTitle);
        sportDetailsTitle.setFirstText(R.string.unit_week);
        sportDetailsTitle.setSecondText(R.string.unit_month);
        sportDetailsTitle.setThirdText(R.string.unit_three_month);

        sportDetailsTitle.setLeftIconOnClickListener(this);
        sportDetailsTitle.setRightIconOnClickListener(this);
        sportDetailsTitle.setFirstOnClickListener(this);
        sportDetailsTitle.setSecondOnClickListener(this);
        sportDetailsTitle.setThirdOnClickListener(this);
        sportDetailsTitle.setRightIconVisibility(View.INVISIBLE);
    }

    @OnClick({R.id.ivShowLastTimeChart})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivDetailsTitleLeftIcon:
                onBackPressed();
                break;
            case R.id.ivDetailsTitleRightIcon:
                AppShareUtil.shareCapture(this);
                break;
            case R.id.ivShowLastTimeChart://里面的返回键也是一样的功能
                getPresenter().requestLoadWeekChart(App.getSelectedCalendar());
                break;
            case R.id.rbDetailsTitleFirst:
                reloadInsertAnimation = true;
                getPresenter().requestLoadWeekChart(App.getSelectedCalendar());
                break;
            case R.id.rbDetailsTitleSecond:
                reloadInsertAnimation = true;
                getPresenter().requestLoadMonthChart(App.getSelectedCalendar());
                break;
            case R.id.rbDetailsTitleThird:
                reloadInsertAnimation = true;
                getPresenter().requestLoadMonthRangeChart(App.getSelectedCalendar());
                break;
        }
    }


    @Override
    public void onValueSelected(int position, PieChartView.PieDataEntry entry) {
        //如果饼状图表显示是运动模式图 ,那么点击的时候 显示周统计图
        switch (entry.getType()) {
            case PieChartView.PieDataEntry.TYPE_SPORT_MODE_TYPE:
                //需要前提是在tab=周 选项卡界面
                if (getPresenter().getStatisticalType() == SportModeDetailItem.STATISTICAL_TYPE_WEEK) {
                    if (entry.getObj() instanceof SportModeDetailItem) {
                        int modeType = ((SportModeDetailItem) entry.getObj()).getModeType();
                        getPresenter().requestLoadWeekSportModeDetailChart(modeType, App.getSelectedCalendar());
                    }
                }
                break;
            case PieChartView.PieDataEntry.TYPE_DETAIL_WEEK:
                getPresenter().requestLoadWeekChart(App.getSelectedCalendar());
                break;
            case PieChartView.PieDataEntry.TYPE_EMPTY:
                //暂无处理
                break;
        }
    }

    @Override
    public void onNothingSelected() {

    }

    @Override
    public void onUpdateWeekChartData(List<PieChartView.PieDataEntry> data, CharSequence chartCenterText) {
        mPieChartView.setChartData(data);
        mPieChartView.setCenterText(chartCenterText);
        ivShowLastTimeChart.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onUpdateWeekSportModeDetailChart(List<PieChartView.PieDataEntry> data, CharSequence chartCenterText) {
        mPieChartView.setChartData(data);
        mPieChartView.setCenterText(chartCenterText);
        ivShowLastTimeChart.setVisibility(View.VISIBLE);
    }

    @Override
    public void onUpdateMonthChartData(List<PieChartView.PieDataEntry> data, CharSequence chartCenterText) {
        mPieChartView.setChartData(data);
        mPieChartView.setCenterText(chartCenterText);
        ivShowLastTimeChart.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onUpdateMonthRangeChartData(List<PieChartView.PieDataEntry> data, CharSequence chartCenterText) {
        mPieChartView.setChartData(data);
        mPieChartView.setCenterText(chartCenterText);
        ivShowLastTimeChart.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onUpdateDateRange(String dateFromAndTo) {
        tvDate.setText(dateFromAndTo);
    }

    @Override
    public void onUpdateItemChartData(List<SportModeDetailItem> detailItems) {
        rvSportModeList.scrollToPosition(0);
        LinearLayoutManager mLayoutManager =
                (LinearLayoutManager) rvSportModeList.getLayoutManager();
        mLayoutManager.scrollToPositionWithOffset(0, 0);

        if (reloadInsertAnimation) {
            items.clear();
            items.addAll(detailItems);
            mSportModeAdapter.notifyItemRangeInserted(0, items.size());
            reloadInsertAnimation = false;
        } else {
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffCallBack(items, detailItems), true);
            diffResult.dispatchUpdatesTo(mSportModeAdapter);
            items.clear();
            items.addAll(detailItems);
        }
    }

    @Override
    public void onBackPressed() {
        if (ivShowLastTimeChart.getVisibility() == View.VISIBLE) {
            onClick(ivShowLastTimeChart);
        } else {
            super.onBackPressed();
        }

    }
}
