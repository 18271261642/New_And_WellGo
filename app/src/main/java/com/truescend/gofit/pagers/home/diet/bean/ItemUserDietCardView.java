package com.truescend.gofit.pagers.home.diet.bean;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.truescend.gofit.R;
import com.truescend.gofit.pagers.common.bean.ItemBase;

import butterknife.BindView;


/**
 * 用户饮食卡片布局
 */
public class ItemUserDietCardView extends ItemBase {
    @BindView(R.id.llDietStatistics)
    View llDietStatistics;
    @BindView(R.id.llDietListMeal)
    View llDietFoodsExtraMeal;
    @BindView(R.id.rlDietPlanThinBodyStart)
    View rlDietPlanThinBodyStart;
    @BindView(R.id.tvDietPlanThinBodyStart)
    TextView tvDietPlanThinBodyStart;
    @BindView(R.id.llDietTargetSetting)
    View llDietTargetSetting;
    @BindView(R.id.tvCalorieData)
    TextView tvCalorieData;
    @BindView(R.id.tvDietMealDetails)
    TextView tvDietMealDetails;

    public ItemUserDietCardView(View view) {
        super(view);
    }


    public void setDietStatisticsClickListener(View.OnClickListener listener) {
        llDietStatistics.setOnClickListener(listener);
    }

    public void setDietListMealClickListener(View.OnClickListener listener) {
        llDietFoodsExtraMeal.setOnClickListener(listener);
    }

    public void setDietTargetSettingClickListener(View.OnClickListener listener) {
        llDietTargetSetting.setOnClickListener(listener);
    }

    public void setDietPlanThinBodyButtonEnable(boolean enable) {
        rlDietPlanThinBodyStart.setVisibility(enable ? View.VISIBLE : View.GONE);
    }

    public void setDietPlanThinBodyButtonClickListener(View.OnClickListener listener) {
        tvDietPlanThinBodyStart.setOnClickListener(listener);
    }

    public void setDietCalorieData(CharSequence content) {
        tvCalorieData.setText(content);
    }

    public void setDietMealDetails(CharSequence content) {
        if (TextUtils.isEmpty(content)) {
            tvDietMealDetails.setText("");
            tvDietMealDetails.setVisibility(View.GONE);
        }else {
            tvDietMealDetails.setText(content);
            tvDietMealDetails.setVisibility(View.VISIBLE);
        }
    }
}
