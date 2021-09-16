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
import com.truescend.gofit.views.BloodPressureChartView;
import com.truescend.gofit.views.SplitLineChartView;
import com.truescend.gofit.views.bean.Label24H;

import java.util.Collections;
import java.util.List;

/**
 * 作者:东芝(2018/8/21).
 * 功能:血压血氧Item
 */

public class BloodPressureBloodOxygenDataItem extends LinearLayout {

    private TextView tvDate;
//    private SimpleDateFormat mHorizontalPickerDateFormat = new SimpleDateFormat("MM/dd", Locale.ENGLISH);
//    private SimpleDateFormat mDefDateFormat = new SimpleDateFormat("MM月dd日", Locale.ENGLISH);
    private SplitLineChartView bloodOxygenChartView;
    private ItemStatus mItemCenter;
    private ItemStatus mItemLeft;
    private ItemStatus mItemRight;
    private View ilItemCenter;
    private View ilItemLeft;
    private View ilItemRight;
    private BloodPressureChartView bloodPressureChartView;

    public BloodPressureBloodOxygenDataItem(Context context) {
        super(context);
        init();
    }

    public BloodPressureBloodOxygenDataItem(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BloodPressureBloodOxygenDataItem(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public BloodPressureBloodOxygenDataItem(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.item_bp_bo_views, this);
        tvDate = (TextView) findViewById(R.id.tvDate);
        bloodOxygenChartView = (SplitLineChartView) findViewById(R.id.bloodOxygenChartView);
        bloodPressureChartView = (BloodPressureChartView) findViewById(R.id.bloodPressureChartView);

        //平均舒张压
        ilItemLeft = findViewById(R.id.ilItemLeft);
        mItemLeft = new ItemStatus(ilItemLeft);
        mItemLeft.setSubTitle(R.string.content_average_pressure_diastolic);

        //平均收缩压
        ilItemCenter = findViewById(R.id.ilItemCenter);
        mItemCenter = new ItemStatus(ilItemCenter);
        mItemCenter.setSubTitle(R.string.content_average_pressure_systolic);

        //平均血氧
        ilItemRight = findViewById(R.id.ilItemRight);
        mItemRight = new ItemStatus(ilItemRight);
        mItemRight.setSubTitle(R.string.content_average_blood_oxygen);


        initBloodOxygenChartItem();
        initBloodPressureChartItem();

        mItemRight.setTitle(ResUtil.format("%d %%", 0));
        mItemLeft.setTitle(ResUtil.format("%d mmHg", 0));
        mItemCenter.setTitle(ResUtil.format("%d mmHg",0));
    }

    private void initBloodPressureChartItem() {

        bloodPressureChartView.setDrawLabel(true);
        bloodPressureChartView.setDrawBorder(true);
        bloodPressureChartView.setDrawLabelLimit(true);
        bloodPressureChartView.setDrawLimitLine(false);
        bloodPressureChartView.setDrawZeroLimitLine(true);
        bloodPressureChartView.setDataType(new Label24H());
        bloodPressureChartView.setBarWidth(BloodPressureChartView.BAR_WIDTH_NORMAL);
        bloodPressureChartView.setFoldPaddingEndModel(true);
        bloodPressureChartView.setColors(getResources().getColor(R.color.colorDiastolic),getResources().getColor(R.color.colorSystolic));

        bloodPressureChartView.setData(Collections.singletonList(new BloodPressureChartView.BloodPressureItem(0, 0)));
        bloodPressureChartView.setLimitLine(50, 100, 150, 200);
    }


    private void initBloodOxygenChartItem() {
        bloodOxygenChartView.setDrawLabel(true);
        bloodOxygenChartView.setDrawBorder(true);
        bloodOxygenChartView.setDrawLabelLimit(true);
        bloodOxygenChartView.setDrawLimitLine(false);
        bloodOxygenChartView.setDrawZeroLimitLine(false);
        bloodOxygenChartView.setBarColor(
                0xFFFE1A97 , 0x10FE1A97,0x00FE1A97 );
        //分割线
        bloodOxygenChartView.setBarWidth(SplitLineChartView.BAR_WIDTH_NORMAL);
        bloodOxygenChartView.setDataType(null);

        bloodOxygenChartView.setDrawLabelToRight(true);
        bloodOxygenChartView.setFoldPaddingEndModel(true);

        bloodOxygenChartView.setData(Collections.singletonList(0));
        bloodOxygenChartView.setLimitLine(84,86,88,90, 92, 94, 96, 98, 100);
    }

    public void setBloodOxygenData(List<Integer> data,int oxygen_avg) {
        if (bloodOxygenChartView != null) {
            bloodOxygenChartView.setData(data);
            bloodOxygenChartView.setLimitLine(84,86,88,90, 92, 94, 96, 98, 100);
        }
        mItemRight.setTitle(ResUtil.format("%d %%", oxygen_avg));
    }

    public void setBloodPressureData(List<BloodPressureChartView.BloodPressureItem> data,int diastolic_avg, int systolic_avg) {
        if (bloodPressureChartView != null) {
            bloodPressureChartView.setData(data);
            //分割线
            bloodPressureChartView.setLimitLine(50, 100, 150, 200);
        }

        mItemLeft.setTitle(ResUtil.format("%d mmHg", diastolic_avg));
        mItemCenter.setTitle(ResUtil.format("%d mmHg", systolic_avg));

    }


}

