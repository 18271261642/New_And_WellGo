package com.truescend.gofit.pagers.home.diet.setting;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.sn.app.db.data.config.DeviceConfigBean;
import com.sn.app.db.data.config.bean.UnitConfig;
import com.sn.app.db.data.user.UserBean;
import com.sn.app.utils.AppUserUtil;
import com.sn.utils.DateUtil;
import com.sn.utils.SNToast;
import com.sn.utils.tuple.TupleTwo;
import com.truescend.gofit.R;
import com.truescend.gofit.pagers.base.BaseActivity;
import com.truescend.gofit.pagers.base.dialog.BaseDialog;
import com.truescend.gofit.pagers.common.bean.ItemBannerButton;
import com.truescend.gofit.pagers.common.dialog.LoadingDialog;
import com.truescend.gofit.pagers.common.dialog.UserCommonDialog;
import com.truescend.gofit.utils.BMIUtil;
import com.truescend.gofit.utils.BMRUtil;
import com.truescend.gofit.utils.PageJumpUtil;
import com.truescend.gofit.utils.ResUtil;
import com.truescend.gofit.utils.UnitConversion;
import com.truescend.gofit.views.NumberPicker;
import com.truescend.gofit.views.RulerView;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 作者:东芝(2018/11/21).
 * 功能:我的理想体重/用户饮食减肥计划目标参数设置
 */
public class DietTargetSettingActivity extends BaseActivity<DietTargetSettingPresenterImpl, IDietTargetSettingContract.IView> implements IDietTargetSettingContract.IView {
    @BindView(R.id.ilUserSettingTarget)
    View ilUserSettingTarget;
    @BindView(R.id.ilUserSettingIntakeCalories)
    View ilUserSettingIntakeCalories;
    @BindView(R.id.tvCurWeight)
    TextView tvCurWeight;
    @BindView(R.id.rvCurWeight)
    RulerView rvCurWeight;
    @BindView(R.id.tvTargetWeight)
    TextView tvTargetWeight;
    @BindView(R.id.rvTargetWeight)
    RulerView rvTargetWeight;
    @BindView(R.id.tvTargetBMI)
    TextView tvTargetBMI;
    @BindView(R.id.tvUserSettingSave)
    TextView tvUserSettingSave;
    @BindView(R.id.tvCardInfo)
    TextView tvCardInfo;
    @BindView(R.id.tvDietHelp)
    TextView tvDietHelp;
    @BindView(R.id.rgUnitGroup)
    RadioGroup rgUnitGroup;

    private ItemBannerButton settingSportTargetItem;
    private ItemBannerButton settingIntakeCalories;
    private int mTargetStep;
    private String tempWeightSuffix;
    private boolean isUnitPounds;
    private float mCurWeight;
    private DeviceConfigBean bean;
    private float mTargetCalory;
    private float mTargetWeight;
    private float mUserHeight;
    private boolean isFirstSetTargetCalory;
    private UserBean user;
    private boolean hasDataError;


    @Override
    protected DietTargetSettingPresenterImpl initPresenter() {
        return new DietTargetSettingPresenterImpl(this);
    }

    @Override
    protected int initLayout() {
        return R.layout.activity_diet_target_setting;
    }

    @Override
    protected void onCreateActivity(Bundle savedInstanceState) {
        setTitle(R.string.content_my_ideal_weight);
        initUser();
        initItem();
        getPresenter().requestDeviceConfig();
    }

    private void initUser() {
        user = AppUserUtil.getUser();
        mUserHeight = user.getHeight();
        mCurWeight = (int) user.getWeight();
        mTargetStep = user.getTarget_step();
        mTargetCalory = user.getTarget_calory();
        mTargetWeight = (int) user.getTarget_weight();
        if (mTargetWeight <= 0) {
            mTargetWeight = mCurWeight - 5;// 安卓与ios保持一致
        }
        calculateProjectSvelte();
    }

    private void initItem() {
        rgUnitGroup.setOnCheckedChangeListener(onItemUnitCheckedChangeListener);


        settingSportTargetItem = new ItemBannerButton(ilUserSettingTarget);
        settingSportTargetItem.setTitle(getString(R.string.content_sport_target));

        settingIntakeCalories = new ItemBannerButton(ilUserSettingIntakeCalories);
        settingIntakeCalories.setTitle(getString(R.string.content_intake_calories));


        rvCurWeight.setOnValueChangeListener(new RulerView.OnValueChangeListener() {
            @Override
            public void onValueChange(float value) {
                if (isUnitPounds) {
                    mCurWeight = (float) UnitConversion.lbTokg(value);
                } else {
                    mCurWeight = value;
                }
                tvCurWeight.setText(ResUtil.format("%.0f %s", value, tempWeightSuffix));
                calculateProjectSvelte();
            }
        });

        rvTargetWeight.setOnValueChangeListener(new RulerView.OnValueChangeListener() {
            @Override
            public void onValueChange(float value) {
                if (isUnitPounds) {
                    mTargetWeight = (float) UnitConversion.lbTokg(value);
                } else {
                    mTargetWeight = value;
                }
                tvTargetWeight.setText(ResUtil.format("%.0f %s", value, tempWeightSuffix));
                refreshTargetBMI();
                calculateProjectSvelte();
            }
        });
    }

    private void calculateProjectSvelte() {
        //注: 一下单位应全以公制计算

        //运动消耗
        int sportCalorie = (int) (mCurWeight * 2 * 1.036f * (mUserHeight * 0.45f * mTargetStep * 0.00001f));

        //基础代谢消耗 26*体重KG
        float basicMetabolismCalorie = 26 * mCurWeight;

        //摄入量(=摄入目标）（当为摄入目标为0的时候设置成1200/50KG*当前的体重KG)

        if (mTargetCalory <= 0) {
            isFirstSetTargetCalory = true;
//            mTargetCalory = 1200 / 50 * mCurWeight;
            mTargetCalory = BMRUtil.getBMRRange(mUserHeight, mCurWeight, user).getV1();
        }

        //食物的基础消耗默认为BMR*0.1
        float foodCalorie = 0.1f * mTargetCalory;

        //每天消耗
        float mDayCalorie = sportCalorie + basicMetabolismCalorie + foodCalorie;

        //每天减少的重量(kg) = 每日消耗/7.776  每克对应的热量系数得出重量克
        float mDayLoseWeight = ((mDayCalorie - mTargetCalory) / 7.776f) / 1000f;

        //要减少的体重
        float mNeedLossWeight = mCurWeight - mTargetWeight;

        //需要的天数 = 要减少的体重/每天减少的
        int mNeedDays = (int) (mNeedLossWeight / mDayLoseWeight);

        Calendar calendar = DateUtil.getCurrentCalendar();
        calendar.add(Calendar.DAY_OF_MONTH, mNeedDays);
        String mTargetDate = DateUtil.getDate(DateUtil.YYYY_MM_DD, calendar);

        String unitStr = "kg";
        float mTempDayLoseWeight = mDayLoseWeight;
        float mTempNeedLossWeight = mNeedLossWeight;
        if(isUnitPounds){
            mTempDayLoseWeight = (float) UnitConversion.kgTolb(mDayLoseWeight);
            mTempNeedLossWeight = (float) UnitConversion.kgTolb(mNeedLossWeight);
            unitStr = "lb";
        }
        String mYourGoalIsToSubtract = getString(R.string.content_your_goal_is_to_subtract);
        String mTheCurrentEveryDayForAMovingTarget = getString(R.string.content_the_current_every_day_for_a_sport_target);
        String mStepDay = ResUtil.getString(R.string.step)+"/"+ResUtil.getString(R.string.day);
        String mDay = ResUtil.getString(R.string.day);
        String mWhenIsTheDailyConsumption = getString(R.string.content_when_is_the_daily_consumption);
        String mDailyIntakeIs = getString(R.string.content_daily_intake_is);
        String mTheCurrentIsExpectedToReduceADay = getString(R.string.content_the_current_is_expected_to_reduce_a_day);
        String mNeedToComplete = getString(R.string.content_need_to_complete);
        String mExpectedTimeForSuccess = getString(R.string.content_expected_time_for_success);
        String mPleaseAdjustThePlan = getString(R.string.content_please_adjust_the_plan);
        String mTargetWeightShouldBeLessThanTheCurrentWeight = getString(R.string.content_target_weight_should_be_less_than_the_current_weight);
        String mTheCurrentPlanIsNotSuitableForWeightLoss = getString(R.string.content_the_current_plan_is_not_suitable_for_weight_loss);



        String mNeedLossWeightStr = mYourGoalIsToSubtract + " <strong><font color=#000000>%.0f " + unitStr + "</font></strong><br /><br />";
        String mTargetStepStr = mTheCurrentEveryDayForAMovingTarget + " <strong><font color=#000000>%d " + mStepDay + "</font></strong><br /><br />";
        String mDayCalorieStr = mWhenIsTheDailyConsumption + "  <strong><font color=#000000>%.0f Kcal</font></strong>," + mDailyIntakeIs + " <strong><font color=#000000>%.0f Kcal</font></strong><br /><br />";
        String mDayLoseWeightStr = mTheCurrentIsExpectedToReduceADay + " <strong><font color=#000000>%.2f " + unitStr + "</font></strong> <br /><br />";
        String mNeedDaysStr = mNeedToComplete + "  <strong><font color=#000000>%d " + mDay + "</font></strong>," + mExpectedTimeForSuccess + " <br /><br /> ";
        String mTargetDateStr = "<h1>%s<h1/>";

        hasDataError = false;

        if (mTargetWeight >= mCurWeight) {
            hasDataError = true;
            mNeedLossWeightStr = "<strong><font color=#ff0000>" + mTargetWeightShouldBeLessThanTheCurrentWeight + " <!--%.0f--></font></strong><br /><br />";
            mTargetDateStr = "<h1><font color=#ff0000>" + mPleaseAdjustThePlan + "<!--%s--></font><h1/>";
            mDayLoseWeightStr = "<!--%.2f-->";
            mNeedDaysStr = "<!--%d--><br /><br />";
        }
        if (mDayCalorie < mTargetCalory) {
            hasDataError = true;
            mNeedDaysStr = "<strong><font color=#000000>" + mTheCurrentPlanIsNotSuitableForWeightLoss + "<!--%d--></font></strong><br /><br />";
            mTargetDateStr = "<h1><font color=#ff0000>" + mPleaseAdjustThePlan + "<!--%s--></font><h1/>";
            mDayLoseWeightStr = "<!--%.2f-->";
        }



        tvCardInfo.setText(ResUtil.formatHtml(
                mNeedLossWeightStr +
                        mTargetStepStr +
                        mDayCalorieStr +
                        mDayLoseWeightStr +
                        mNeedDaysStr +
                        mTargetDateStr,

                mTempNeedLossWeight,
                mTargetStep,
                mDayCalorie, mTargetCalory,
                mTempDayLoseWeight,
                mNeedDays,
                mTargetDate)
        );

        tvUserSettingSave.setEnabled(!hasDataError);
    }


    @OnClick({R.id.tvDietHelp, R.id.ilUserSettingTarget, R.id.ilUserSettingIntakeCalories, R.id.tvUserSettingSave})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ilUserSettingTarget:
                setSportTargetDialog();
                break;
            case R.id.ilUserSettingIntakeCalories:
                setIntakeCaloryTargetDialog();
                break;
            case R.id.tvUserSettingSave:
                if(hasDataError){
                    return;
                }
                getPresenter().requestUpdateDietTarget(mTargetStep, mCurWeight, mTargetWeight, mTargetCalory);
                break;
            case R.id.tvDietHelp:
                new BaseDialog.Builder(this)
                        .setContentView(R.layout.dialog_user_diet_help)
                        .setCanceledOnTouchOutside(true)
                        .show();
                break;

        }
    }

    private void setIntakeCaloryTargetDialog() {
        String s = getString(R.string.content_recommended_daily_intake);
        String formatHtml = s + " <strong><font color=#000000>%d-%d</font></strong> Kcal";

        TupleTwo<Integer, Integer> bmrRange = BMRUtil.getBMRRange(mUserHeight, mCurWeight, user);

        CharSequence content = ResUtil.formatHtml(formatHtml, bmrRange.getV1(), bmrRange.getV2());
        final float targetTemp = mTargetCalory;
        UserCommonDialog.create(this,
                "Kcal",
                UserCommonDialog.FORMAT_2,
                bmrRange.getV1() / 100,
                bmrRange.getV2() / 100,
                Math.round(mTargetCalory / 100),
                getString(R.string.content_intake_calories),
                content,
                new NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                        mTargetCalory = picker.getValue() * 100;
                    }
                },
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mTargetCalory = targetTemp;
                        dialogInterface.cancel();
                    }
                },
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        settingIntakeCalories.setContent(ResUtil.format("%.0f Kcal", mTargetCalory));
                        calculateProjectSvelte();
                        dialogInterface.dismiss();
                    }
                }).show();
    }

    /**
     * 体重单位用户点击按钮导致的改变,这里是存储
     */
    private RadioGroup.OnCheckedChangeListener onItemUnitCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if (bean == null) return;
            UnitConfig unitConfig = bean.getUnitConfig();
            switch (checkedId) {
                case R.id.rbUnitLeft:
                    unitConfig.setWeightUnit(UnitConfig.WEIGHT_KG);
                    break;
                case R.id.rbUnitRight:
                    unitConfig.setWeightUnit(UnitConfig.WEIGHT_LB);
                    break;
            }
            getPresenter().requestChangeDeviceConfigData(bean);

        }
    };

    /**
     * 体重单位真实改变,这里是存储成功后UI作出的变化
     *
     * @param bean
     */
    @Override
    public void onUpdateDeviceConfig(DeviceConfigBean bean) {
        this.bean = bean;
        UnitConfig unitConfig = bean.getUnitConfig();
        if (unitConfig == null) return;
        rgUnitGroup.setOnCheckedChangeListener(null);
        switch (unitConfig.weightUnit) {
            case UnitConfig.WEIGHT_KG:
                rgUnitGroup.check(R.id.rbUnitLeft);
                break;
            case UnitConfig.WEIGHT_LB:
                rgUnitGroup.check(R.id.rbUnitRight);
                break;
        }
        rgUnitGroup.setOnCheckedChangeListener(onItemUnitCheckedChangeListener);
        float tempCurWeight = getWeightFromCurUnit(mCurWeight, unitConfig.weightUnit);
        float tempTargetWeight = getWeightFromCurUnit(mTargetWeight, unitConfig.weightUnit);

        tvCurWeight.setText(ResUtil.format("%.0f %s", tempCurWeight, tempWeightSuffix));
        tvTargetWeight.setText(ResUtil.format("%.0f %s", tempTargetWeight, tempWeightSuffix));
        refreshTargetBMI();

        rvCurWeight.setValue(
                isUnitPounds ? ((int)tempCurWeight): ((int)mCurWeight),
                isUnitPounds ? 60 : 30,
                isUnitPounds ? 330 : 150,
                1
        );

        rvTargetWeight.setValue(
                isUnitPounds ? ((int) tempTargetWeight) :((int)  mTargetWeight),
                isUnitPounds ? 60 : 30,
                isUnitPounds ? 330 : 150,
                1
        );

        settingSportTargetItem.setContent(mTargetStep +" "+ getString(R.string.step));
        settingIntakeCalories.setContent(ResUtil.format("%.0f Kcal", mTargetCalory));
        calculateProjectSvelte();
    }

    private void refreshTargetBMI() {
        if (BMIUtil.getUserWeightBMIValue(mUserHeight, mTargetWeight) > 24) {
            tvTargetBMI.setTextColor(getResources().getColor(R.color.red));
        } else {
            tvTargetBMI.setTextColor(getResources().getColor(R.color.black));
        }
        tvTargetBMI.setText(ResUtil.format("%s %s", BMIUtil.getUserWeightBMI(mUserHeight, mTargetWeight), BMIUtil.getUserWeightType(mUserHeight, mTargetWeight)));
    }

    @Override
    public void onUpdateUserDataSuccess() {
        setResult(Activity.RESULT_OK);
        if (isFirstSetTargetCalory) {
            PageJumpUtil.startDietListMealActivity(this);
            finish();
        } else {
            finish();
        }
    }

    @Override
    public void onUpdateUserDataFailed(String msg) {
        SNToast.toast(msg);
    }

    private float getWeightFromCurUnit(float mWeight, int weightUnit) {
        float tempWeight = mWeight;
        tempWeightSuffix = getString(R.string.unit_kg);
        isUnitPounds = weightUnit == UnitConfig.WEIGHT_LB;
        if (isUnitPounds) {
            tempWeight = (float) UnitConversion.kgTolb(tempWeight);
            tempWeightSuffix = getString(R.string.unit_pounds);
        }
        return tempWeight;
    }

    /**
     * 目标步数弹框
     */
    private void setSportTargetDialog() {
        final int targetTemp = mTargetStep;
        UserCommonDialog.create(this,
                getString(R.string.step),
                UserCommonDialog.FORMAT_3,
                1,
                30,
                mTargetStep / 1000,
                ViewGroup.VISIBLE,
                ViewGroup.VISIBLE,
                new NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                        mTargetStep = picker.getValue() * 1000;
                    }
                },
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mTargetStep = targetTemp;
                        dialogInterface.cancel();
                    }
                },
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        settingSportTargetItem.setContent(mTargetStep +" "+  getString(R.string.step));
                        calculateProjectSvelte();
                        dialogInterface.dismiss();
                    }
                }).show();
    }

    @Override
    public void onDialogLoading(String msg) {
        LoadingDialog.show(this, msg);
    }

    @Override
    public void onDialogDismiss() {
        LoadingDialog.dismiss();
    }

}
