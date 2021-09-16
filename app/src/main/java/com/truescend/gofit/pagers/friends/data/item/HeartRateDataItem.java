package com.truescend.gofit.pagers.friends.data.item;

import android.content.Context;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.truescend.gofit.R;
import com.truescend.gofit.pagers.home.bean.ItemStatus;
import com.truescend.gofit.utils.ResUtil;
import com.truescend.gofit.views.SplitLineChartView;
import com.truescend.gofit.views.bean.Label24H;

import java.util.Collections;
import java.util.List;

/**
 * 作者:东芝(2018/8/21).
 * 功能:心率数据Item
 */

public class HeartRateDataItem extends LinearLayout {

    private TextView tvDate;
    private SplitLineChartView heartRateChartView;
    private ItemStatus mItemCenter;
    private ItemStatus mItemLeft;
    private ItemStatus mItemRight;
    private View ilItemCenter;
    private View ilItemLeft;
    private View ilItemRight;

    public HeartRateDataItem(Context context) {
        super(context);
        init();
    }

    public HeartRateDataItem(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HeartRateDataItem(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public HeartRateDataItem(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.item_heart_views, this);
        tvDate = (TextView) findViewById(R.id.tvDate);
        heartRateChartView = (SplitLineChartView) findViewById(R.id.heartRateChartView);

        ilItemLeft = findViewById(R.id.ilItemLeft);
        mItemLeft = new ItemStatus(ilItemLeft);
        mItemLeft.setSubTitle(R.string.content_highest_heart);//填充格子的标题

        ilItemCenter = findViewById(R.id.ilItemCenter);
        mItemCenter = new ItemStatus(ilItemCenter);
        mItemCenter.setSubTitle(R.string.content_average_heart);//填充格子的标题

        ilItemRight = findViewById(R.id.ilItemRight);
        mItemRight = new ItemStatus(ilItemRight);
        mItemRight.setSubTitle(R.string.content_minimum_heart);//填充格子的标题

        initHeartRateChartItem();
    }


    private void initHeartRateChartItem() {
        heartRateChartView.setDrawLabel(true);
        heartRateChartView.setDrawBorder(true);
        heartRateChartView.setDrawLabelLimit(true);
        heartRateChartView.setDrawLimitLine(false);
        heartRateChartView.setDrawZeroLimitLine(true);

        heartRateChartView.setBarColor(
                0xFFF90D1B,/* 0xFFF90D1B,
                0x90F90D1B, 0x90F90D1B,
                0x80F90D1B, 0x80F90D1B,
                0x70F90D1B, 0x70F90D1B,
                0x60F90D1B, 0x60F90D1B,
                0x50F90D1B, 0x50F90D1B,
                0x40F90D1B, 0x40F90D1B,
                0x30F90D1B, 0x30F90D1B,
                0x20F90D1B, 0x20F90D1B,
                0x10F90D1B, */0x10F90D1B);
        //分割线
        heartRateChartView.setBarWidth(SplitLineChartView.BAR_WIDTH_NORMAL);
        heartRateChartView.setDataType(new Label24H());
        heartRateChartView.setFoldPaddingEndModel(true);

        heartRateChartView.setData(Collections.singletonList(0));
        heartRateChartView.setLimitLine(0, 50, 100, 150, 200);

        mItemLeft.setTitle(ResUtil.format("%d bpm", 0));
        mItemCenter.setTitle(ResUtil.format("%d bpm",0));
        mItemRight.setTitle(ResUtil.format("%d bpm", 0));
    }

    public void setHeartRateData(List<Integer> data, int heart_max, int heart_ave, int heart_min) {
        if (heartRateChartView != null) {
            heartRateChartView.setData(data);
            heartRateChartView.setLimitLine(0, 50, 100, 150, 200);
        }
        mItemLeft.setTitle(ResUtil.format("%d bpm",heart_max));
        mItemCenter.setTitle(ResUtil.format("%d bpm", heart_ave));
        mItemRight.setTitle(ResUtil.format("%d bpm", heart_min));
    }


}

