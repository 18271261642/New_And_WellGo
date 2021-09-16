package com.truescend.gofit.pagers.ranking;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.sn.app.net.data.app.bean.FriendListBean;
import com.sn.utils.DateUtil;
import com.sn.utils.SNToast;
import com.truescend.gofit.R;
import com.truescend.gofit.pagers.base.BaseActivity;
import com.truescend.gofit.pagers.common.dialog.LoadingDialog;
import com.truescend.gofit.pagers.ranking.adapter.FriendsRankingAdapter;
import com.truescend.gofit.utils.RecycleViewUtil;
import com.truescend.gofit.utils.ResUtil;
import com.truescend.gofit.utils.StatusBarUtil;
import com.truescend.gofit.views.RankingBarChartView;
import com.truescend.gofit.views.SleepPercentView;
import com.truescend.gofit.views.bean.LabelRange;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 作者:东芝(2019/01/11).
 * 功能:世界排行
 */
public class RankingActivity extends BaseActivity<RankingPresenterImpl, IRankingContract.IView> implements IRankingContract.IView, OnRefreshListener {



    @BindView(R.id.rlRefresh)
    SmartRefreshLayout rlRefresh;
    @BindView(R.id.rvRankingList)
    RecyclerView rvRankingList;
    @BindView(R.id.rbcStepChart)
    RankingBarChartView rbcStepChart;
    @BindView(R.id.rbcSleepDurationChart)
    RankingBarChartView rbcSleepDurationChart;
    @BindView(R.id.rbcSleepTimeChart)
    RankingBarChartView rbcSleepTimeChart;
    @BindView(R.id.spvSleepDetailsChart)
    SleepPercentView spvSleepDetailsChart;
    @BindView(R.id.rgUnitGroup)
    RadioGroup rgUnitGroup;
    @BindView(R.id.tvDateRange)
    TextView tvDateRange;
    @BindView(R.id.civHead)
    CircleImageView civHead;
    @BindView(R.id.tvDistance)
    TextView tvDistance;
    @BindView(R.id.tvDistanceUnit)
    TextView tvDistanceUnit;
    @BindView(R.id.tvLevelText)
    TextView tvLevelText;
    @BindView(R.id.tvStepTotal)
    TextView tvStepTotal;
    @BindView(R.id.tvSportMoreThanPercent)
    TextView tvSportMoreThanPercent;
    @BindView(R.id.tvSportEncouragePrompt)
    TextView tvSportEncouragePrompt;
    @BindView(R.id.tvSleepDurationMoreThanPercent)
    TextView tvSleepDurationMoreThanPercent;
    @BindView(R.id.tvSleepDurationEncouragePrompt)
    TextView tvSleepDurationEncouragePrompt;
    @BindView(R.id.tvSleepTimeMoreThanPercent)
    TextView tvSleepTimeMoreThanPercent;
    @BindView(R.id.tvSleepTimeEncouragePrompt)
    TextView tvSleepTimeEncouragePrompt;


    private FriendsRankingAdapter friendsRankingAdapter;

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, RankingActivity.class));
    }

    @Override
    protected RankingPresenterImpl initPresenter() {
        return new RankingPresenterImpl(this);
    }

    @Override
    protected int initLayout() {
        return R.layout.activity_ranking;
    }

    @Override
    protected void onCreateActivity(Bundle savedInstanceState) {
        getTitleLayout().setTitleShow(false);
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);
        StatusBarUtil.setStatusBarDarkTheme(this, false);
        StatusBarUtil.setStatusBarColor(this, 0x00000000);


        initRankingListView();
        initRankingStepChart();
        initRankingSleepQualityChart();
        initRankingSleepTimeChart();
        initRankingSleepDetailsChart();

        getPresenter().loadFriendRanking();
        getPresenter().loadSportRanking();
        getPresenter().loadSleepDurationRanking();
        getPresenter().loadSleepTimeRanking();
        getPresenter().loadWeekSleepDetailsChart(DateUtil.getCurrentCalendar());

    }


    private void initRankingSleepDetailsChart() {
        rgUnitGroup.setOnCheckedChangeListener(onDataRangeCheckedChangeListener);
    }


    private void initRankingSleepTimeChart() {
        rbcSleepTimeChart.setDrawLabel(false);
        rbcSleepTimeChart.setDrawBorder(false);
        rbcSleepTimeChart.setDrawLabelLimit(false);
        rbcSleepTimeChart.setDrawLimitLine(false);
        rbcSleepTimeChart.setDrawZeroLimitLine(true);
        rbcSleepTimeChart.setBarWidth(RankingBarChartView.BAR_WIDTH_LARGER * 1.5f);
        rbcSleepTimeChart.setBarColor(0xFF7046AC);


    }

    private void initRankingSleepQualityChart() {
        rbcSleepDurationChart.setDrawLabel(false);
        rbcSleepDurationChart.setDrawBorder(false);
        rbcSleepDurationChart.setDrawLabelLimit(false);
        rbcSleepDurationChart.setDrawLimitLine(false);
        rbcSleepDurationChart.setDrawZeroLimitLine(true);
        rbcSleepDurationChart.setBarWidth(RankingBarChartView.BAR_WIDTH_LARGER * 1.5f);
        rbcSleepDurationChart.setBarColor(0xFF7046AC);


    }

    private void initRankingStepChart() {
        rbcStepChart.setDrawLabel(false);
        rbcStepChart.setDrawBorder(false);
        rbcStepChart.setDrawLabelLimit(false);
        rbcStepChart.setDrawLimitLine(false);
        rbcStepChart.setDrawZeroLimitLine(true);
        rbcStepChart.setBarWidth(RankingBarChartView.BAR_WIDTH_LARGER * 1.5f);
        rbcStepChart.setBarColor(0xFF000000);

    }

    private void initRankingListView() {
        friendsRankingAdapter = new FriendsRankingAdapter(this);
        rlRefresh.setOnRefreshListener(this);
        RecycleViewUtil.setAdapter(rvRankingList, friendsRankingAdapter);
    }


    @Override
    public void onRefresh(RefreshLayout refreshlayout) {
        getPresenter().loadFriendRanking();
    }


    @Override
    public void onLoadFriendRanking(List<FriendListBean.DataBean> data, String headUrl, int selfLevelIndex, int stepTotal, float distanceTotal, String distanceUnit) {
        friendsRankingAdapter.setList(data);
        tvDistance.setText(ResUtil.format("%.2f", distanceTotal));
        tvStepTotal.setText(String.valueOf(stepTotal));
        tvLevelText.setText(String.valueOf(selfLevelIndex));
        tvDistanceUnit.setText(distanceUnit);


        Glide.with(this)
                .asBitmap()
                .load(headUrl)
                .apply(RequestOptions.errorOf(R.mipmap.img_test_picture))
                .apply(RequestOptions.placeholderOf(R.mipmap.img_test_picture))
                .apply(RequestOptions.skipMemoryCacheOf(true))
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                .into(civHead);
    }

    @Override
    public void onLoadSportRanking(String[][] rankingLabel, List<Integer> data, int max, Bitmap headIcon, int headIconIndex, String moreThanPercentText) {
        rbcStepChart.setDataType(new LabelRange(rankingLabel));
        rbcStepChart.setData(data);
        rbcStepChart.setLimitLine(new float[]{0, max}, new String[]{"", ""});

        if (headIcon != null && headIconIndex != -1) {
            rbcStepChart.setHeadIcon(headIcon, headIconIndex);
        }
        String[] array = getResources().getStringArray(R.array.sport_encourage_prompt);
        if (headIconIndex != -1 && headIconIndex < array.length) {
            tvSportEncouragePrompt.setText(array[headIconIndex]);
        } else if (headIconIndex == -1) {
            tvSportEncouragePrompt.setText(array[0]);
        } else {
            tvSportEncouragePrompt.setText(array[array.length - 1]);
        }
        tvSportMoreThanPercent.setText(moreThanPercentText);

    }

    @Override
    public void onLoadSleepDurationRanking(String[][] rankingLabel, List<Integer> data, int max, Bitmap headIcon, int headIconIndex, String moreThanPercentText) {
        rbcSleepDurationChart.setDataType(new LabelRange(rankingLabel));
        rbcSleepDurationChart.setData(data);
        rbcSleepDurationChart.setLimitLine(new float[]{0, max}, new String[]{"", ""});
        if (headIcon != null && headIconIndex != -1) {
            rbcSleepDurationChart.setHeadIcon(headIcon, headIconIndex);
        }

        String[] array = getResources().getStringArray(R.array.sleep_duration_encourage_prompt);
        if (headIconIndex != -1 && headIconIndex < array.length) {
            tvSleepDurationEncouragePrompt.setText(array[headIconIndex]);
        } else if (headIconIndex == -1) {
            tvSleepDurationEncouragePrompt.setText(array[0]);
        } else {
            tvSleepDurationEncouragePrompt.setText(array[array.length - 1]);
        }
        tvSleepDurationMoreThanPercent.setText(moreThanPercentText);
    }

    @Override
    public void onLoadSleepTimeRanking(String[][] rankingLabel, List<Integer> data, int max, Bitmap headIcon, int headIconIndex, String moreThanPercentText) {
        rbcSleepTimeChart.setDataType(new LabelRange(rankingLabel));
        rbcSleepTimeChart.setData(data);
        rbcSleepTimeChart.setLimitLine(new float[]{0, max}, new String[]{"", ""});
        if (headIcon != null && headIconIndex != -1) {
            rbcSleepTimeChart.setHeadIcon(headIcon, headIconIndex);
        }
        String[] array = getResources().getStringArray(R.array.sleep_time_encourage_prompt);
        if (headIconIndex != -1 && headIconIndex < array.length) {
            tvSleepTimeEncouragePrompt.setText(array[headIconIndex]);
        } else if (headIconIndex == -1) {
            tvSleepTimeEncouragePrompt.setText(array[0]);
        } else {
            tvSleepTimeEncouragePrompt.setText(array[array.length - 1]);
        }
        tvSleepTimeMoreThanPercent.setText(moreThanPercentText);
    }


    @Override
    public void onLoadSleepDetailsChartData(String dateFromAndTo, int curDeepTime, int curLightTime, int curSoberTime, int avgDeepTime, int avgLightTime, int avgSoberTime) {
        spvSleepDetailsChart.setSleepData(getString(R.string.sleep_deep), getString(R.string.sleep_light), getString(R.string.sleep_awake),
                curDeepTime, curLightTime, curSoberTime,
                avgDeepTime, avgLightTime, avgSoberTime
        );
        tvDateRange.setText(dateFromAndTo);
    }

    private RadioGroup.OnCheckedChangeListener onDataRangeCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                case R.id.rbUnitLeft:
                    getPresenter().loadWeekSleepDetailsChart(DateUtil.getCurrentCalendar());
                    break;
                case R.id.rbUnitRight:
                    getPresenter().loadMonthSleepDetailsChart(DateUtil.getCurrentCalendar());
                    break;
            }
        }
    };

    @Override
    public void onFinishRefresh() {
        if (rlRefresh != null && rlRefresh.isRefreshing()) {
            rlRefresh.finishRefresh();
        }
    }

    @Override
    public void onLoading() {
        LoadingDialog.loading(this);
    }

    @Override
    public void onLoadingDone() {
        LoadingDialog.dismiss();
    }

    @Override
    public void onShowTips(String str) {
        SNToast.toast(str);
    }


    @OnClick({R.id.ivBack})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivBack:
                onBackPressed();
                break;
        }
    }

}
