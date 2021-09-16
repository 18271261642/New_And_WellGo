package com.truescend.gofit.pagers.home.sleep;

import android.os.Bundle;
import android.view.View;

import com.truescend.gofit.App;
import com.truescend.gofit.R;
import com.truescend.gofit.pagers.base.BaseActivity;
import com.truescend.gofit.pagers.base.dialog.BaseDialog;
import com.truescend.gofit.pagers.home.bean.ItemDetails;
import com.truescend.gofit.pagers.home.bean.ItemSleepDetails;
import com.truescend.gofit.pagers.home.bean.ItemStatus;
import com.truescend.gofit.utils.PageJumpUtil;
import com.truescend.gofit.views.SleepChartView;
import com.truescend.gofit.views.TitleLayout;

import java.util.List;

import butterknife.BindView;

/**
 * 功能：睡眠数据页面
 * Author:Created by 泽鑫 on 2017/12/01 10:04.
 */
public class SleepActivity extends BaseActivity<SleepPresenterImpl, ISleepContract.IView> implements ISleepContract.IView {
    @BindView(R.id.tlTitle)
    TitleLayout tlTitle;
    @BindView(R.id.ilSleepDetailsTotalTime)
    View ilSleepDetailsTotalTime;
    @BindView(R.id.ilSleepDetailsValidTime)
    View ilSleepDetailsValidTime;
    @BindView(R.id.ilSleepDetailsSleepQuality)
    View ilSleepDetailsSleepQuality;
    @BindView(R.id.ilSleepDetails)
    View ilSleepDetails;
    @BindView(R.id.ilSleepDeepSleep)
    View ilSleepDeepSleep;
    @BindView(R.id.ilSleepLightSleep)
    View ilSleepLightSleep;
    @BindView(R.id.ilSleepAwake)
    View ilSleepAwake;
    @BindView(R.id.scvSleepChartView)
    SleepChartView scvSleepChartView;

    private ItemStatus totalTimeStatus;//总睡眠情况
    private ItemStatus validTimeStatus;//有效睡眠情况
    private ItemStatus qualityTimeStatus;//睡眠质量情况
    private ItemDetails sleepDetails;//睡眠详情
    private ItemSleepDetails deepDetails;//深睡详情
    private ItemSleepDetails lightDetails;//浅睡详情
    private ItemSleepDetails awakeDetails;//清醒详情

    @Override
    protected SleepPresenterImpl initPresenter() {
        return new SleepPresenterImpl(this);
    }

    @Override
    protected int initLayout() {
        return R.layout.activity_sleep;
    }

    @Override
    protected void onCreateActivity(Bundle savedInstanceState) {
        initTitle();
        initView();
        reLoadData();
    }
    private void reLoadData() {
        getPresenter().requestLoadSleepChart(App.getSelectedCalendar());
    }
    private void initTitle() {
        //设置标题
        tlTitle.setTitle(R.string.title_today_sleep);

        //设置数据按钮
        tlTitle.addRightItem(TitleLayout.ItemBuilder.Builder().setIcon(R.mipmap.icon_data).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PageJumpUtil.startSleepDetailsActivity(SleepActivity.this);
            }
        }));


    }

    /**
     * 初始化界面
     */
    private void initView() {
        totalTimeStatus = new ItemStatus(ilSleepDetailsTotalTime);
        totalTimeStatus.setSubTitle(R.string.sleep_total_time);//填充文字

        validTimeStatus = new ItemStatus(ilSleepDetailsValidTime);
        validTimeStatus.setSubTitle(R.string.sleep_valid_time);//填充文字

        qualityTimeStatus = new ItemStatus(ilSleepDetailsSleepQuality);
        qualityTimeStatus.setSubTitle(R.string.sleep_quality_time);//填充文字

        sleepDetails = new ItemDetails(ilSleepDetails);
        sleepDetails.setIcon(R.mipmap.icon_sleep);//睡眠图标
        sleepDetails.setTitle(R.string.sleep_details);//睡眠详情
        sleepDetails.setRedIconVisibility(View.GONE);//隐藏小红点
        sleepDetails.setYellowIconVisibility(View.GONE);//隐藏小黄点
        sleepDetails.setLowTextVisibility(View.GONE);

        sleepDetails.setHeightText(R.string.content_accuracy_title);
        sleepDetails.setOnTextClickListener(textClickListener);

        lightDetails = new ItemSleepDetails(ilSleepLightSleep);
        lightDetails.setTitle(R.string.sleep_light);//浅睡
        lightDetails.setProgressDrawable(getResources().getDrawable(R.drawable.item_progressbar_light));//浅睡背景


        deepDetails = new ItemSleepDetails(ilSleepDeepSleep);
        deepDetails.setTitle(R.string.sleep_deep);//深睡
        deepDetails.setProgressDrawable(getResources().getDrawable(R.drawable.item_progressbar_deep));//深睡背景


        awakeDetails = new ItemSleepDetails(ilSleepAwake);
        awakeDetails.setTitle(R.string.sleep_awake);//清醒
        awakeDetails.setProgressDrawable(getResources().getDrawable(R.drawable.item_progressbar_awake));//清醒背景


        ilSleepDetailsTotalTime.setBackgroundColor(getResources().getColor(R.color.transparent));//设置背景颜色为透明
        ilSleepDetailsValidTime.setBackgroundColor(getResources().getColor(R.color.transparent));//设置背景颜色为透明
        ilSleepDetailsSleepQuality.setBackgroundColor(getResources().getColor(R.color.transparent));//设置背景颜色为透明

    }


    @Override
    public void onUpdateSleepChartData(List<SleepChartView.SleepItem> data) {
        scvSleepChartView.setData(data);
    }

    @Override
    public void onUpdateSleepItemData(CharSequence totalTime, CharSequence validTime, String quality, int deepPercent, String deepPercentText, int lightPercent, String lightPercentText, int soberPercent, String soberPercentText, String deepTotal, String lightTotal, String soberTotal) {
        totalTimeStatus.setTitle(totalTime);

        validTimeStatus.setTitle(validTime);

        qualityTimeStatus.setTitle(quality);
        deepDetails.setProgress(deepPercent);
        deepDetails.setPercent(deepPercentText);
        deepDetails.setTime(deepTotal);

        lightDetails.setProgress(lightPercent);
        lightDetails.setPercent(lightPercentText);
        lightDetails.setTime(lightTotal);

        awakeDetails.setProgress(soberPercent);
        awakeDetails.setPercent(soberPercentText);
        awakeDetails.setTime(soberTotal);
    }

    ItemDetails.OnTextClickListener textClickListener = new ItemDetails.OnTextClickListener() {
        @Override
        public void onTextClick(int item) {
            sleepAccuracyDialog();
        }
    };

    private void sleepAccuracyDialog() {
        new BaseDialog.Builder(this)
                .setContentView(R.layout.dialog_sleep_accuracy)
                .setCanceledOnTouchOutside(true)
                .show();
    }
}
