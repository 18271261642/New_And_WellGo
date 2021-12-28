package com.truescend.gofit.pagers.user.setting;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.sn.app.db.data.config.bean.UnitConfig;
import com.sn.app.db.data.user.UserBean;
import com.sn.app.utils.AppUnitUtil;
import com.sn.app.utils.AppUserUtil;
import com.sn.utils.DateUtil;
import com.sn.utils.IF;
import com.sn.utils.SNLog;
import com.sn.utils.SNToast;
import com.sn.utils.view.FastClickChecker;
import com.sn.utils.view.ViewCompat;
import com.truescend.gofit.R;
import com.truescend.gofit.pagers.base.BaseActivity;
import com.truescend.gofit.pagers.base.dialog.BaseDialog;
import com.truescend.gofit.pagers.common.bean.ItemBannerButton;
import com.truescend.gofit.pagers.common.dialog.LoadingDialog;
import com.truescend.gofit.pagers.common.dialog.UserCommonDialog;
import com.truescend.gofit.pagers.device.bean.ItemBmi;
import com.truescend.gofit.utils.BMIUtil;
import com.truescend.gofit.utils.HeadPickerUtil;
import com.truescend.gofit.utils.InputTextWatcher;
import com.truescend.gofit.utils.PageJumpUtil;
import com.truescend.gofit.utils.PermissionUtils;
import com.truescend.gofit.utils.ResUtil;
import com.truescend.gofit.utils.UnitConversion;
import com.truescend.gofit.views.DatePicker;
import com.truescend.gofit.views.NumberPicker;
import com.truescend.gofit.views.TitleLayout;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Date;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 功能：用户设置界面
 * Author:Created by 泽鑫 on 2018/1/24 11:04.
 * 东芝:注册适配+用户数据修改功能+公英制体重单位转换 修改注意:单位转换切勿改变原数据,仅仅改变UI显示 ,否则逻辑会越来越乱
 */

public class UserSettingActivity extends BaseActivity<UserSettingPresenterImpl, IUserSettingContract.IView> implements IUserSettingContract.IView {
    public final static String KEY_NEXT_TYPE = "KEY_NEXT_TYPE";
    public final static int TYPE_SAVE_AND_FINISH = 1;
    public final static int TYPE_SAVE_AND_START_MAIN_ACTIVITY = 2;
    private String tempWeightSuffix;
    /**
     * 单位是磅(lb) 吗? 否则 是 kg
     */
    private boolean isUnitPounds;

    @IntDef(flag = true, value = {
            TYPE_SAVE_AND_FINISH,
            TYPE_SAVE_AND_START_MAIN_ACTIVITY
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface NextType {
    }

    @BindView(R.id.civUserSettingHeadPortrait)
    CircleImageView civUserSettingHeadPortrait;
    @BindView(R.id.tvUserSettingNickname)
    TextView tvUserSettingNickname;
    @BindView(R.id.etUserSettingNickname)
    EditText etUserSettingNickname;
    @BindView(R.id.tvUserSettingSign)
    TextView tvUserSettingSign;
    @BindView(R.id.etUserSettingSign)
    EditText etUserSettingSign;
    @BindView(R.id.ilUserSettingGender)
    View ilUserSettingGender;
    @BindView(R.id.ilUserSettingBirthYear)
    View ilUserSettingBirthYear;
    @BindView(R.id.ilUserSettingHeight)
    View ilUserSettingHeight;
    @BindView(R.id.ilUserSettingLastWeight)
    View ilUserSettingLastWeight;
    @BindView(R.id.ilUserSettingBMIIndex)
    View ilUserSettingBMIIndex;
    @BindView(R.id.ilUserSettingTarget)
    View ilUserSettingTarget;
    @BindView(R.id.tvUserSettingSave)
    TextView tvUserSettingSave;

    private ItemBannerButton genderItem;
    private ItemBannerButton birthYearItem;
    private ItemBannerButton heightItem;
    private ItemBannerButton lastWeightItem;
    private ItemBannerButton dayTargetItem;

    private ItemBmi BMIItem;

    private String mUserNickname;
    private String mUserSign;
    private int mUserGender;
    private String mUserBirthDate;
    private float mUserHeight;
    private float mUserWeight;
    private String mUserWeightMeasureTime;
    private CharSequence mUserBMI;
    private int mUserTarget;
    private String mUserHeadUrl;
    private String mUserLocalImagePath;
    private int mNextType;

    @Override
    protected UserSettingPresenterImpl initPresenter() {
        return new UserSettingPresenterImpl(this);
    }

    @Override
    protected int initLayout() {
        return R.layout.activity_user_setting;
    }

    @Override
    protected void onCreateTitle(TitleLayout titleLayout) {
        super.onCreateTitle(titleLayout);
        titleLayout.setTitle(getString(R.string.title_user_setting));
    }

    @Override
    protected void onCreateActivity(Bundle savedInstanceState) {
        mNextType = getIntent().getIntExtra(KEY_NEXT_TYPE, TYPE_SAVE_AND_FINISH);
        initItem();
        switch (mNextType) {
            case TYPE_SAVE_AND_FINISH:
                initUser();
                break;
            case TYPE_SAVE_AND_START_MAIN_ACTIVITY:
                initDefaultValue();
                break;
        }
        initData();
    }

    private void initItem() {
        tvUserSettingNickname.setText(R.string.content_setting_nickname);
        tvUserSettingSign.setText(R.string.content_setting_sign);

        genderItem = new ItemBannerButton(ilUserSettingGender);
        genderItem.setTitle(R.string.content_setting_gender);

        birthYearItem = new ItemBannerButton(ilUserSettingBirthYear);
        birthYearItem.setTitle(R.string.content_setting_birth_year);

        heightItem = new ItemBannerButton(ilUserSettingHeight);
        heightItem.setTitle(R.string.content_setting_height);

        lastWeightItem = new ItemBannerButton(ilUserSettingLastWeight);
        lastWeightItem.setTitle(R.string.content_setting_last_weight);

        BMIItem = new ItemBmi(ilUserSettingBMIIndex);
        BMIItem.setTitle(R.string.content_setting_bmi_index);
        BMIItem.setTips(getString(R.string.content_what_is_this));
        BMIItem.setTipsOnClickListener(listener);

        dayTargetItem = new ItemBannerButton(ilUserSettingTarget);
        dayTargetItem.setTitle(R.string.content_setting_day_target);
    }

    ItemBmi.OnTipsClickListener listener = new ItemBmi.OnTipsClickListener() {
        @Override
        public void onTipsClick(View view) {
            bmiDialog();
        }
    };

    private void initUser() {
        UserBean userBean = AppUserUtil.getUser();
        mUserNickname = userBean.getNickname();
        mUserSign = userBean.getSign();
        mUserGender = userBean.getGender();
        mUserBirthDate = userBean.getBirthday();
        mUserHeight = userBean.getHeight();
        mUserWeight = userBean.getWeight();
        String BMI = BMIUtil.getUserWeightBMI(mUserHeight, mUserWeight);
        String BMIInstructions = BMIUtil.getUserWeightType(mUserHeight, mUserWeight);
        mUserBMI = ResUtil.formatHtml("%s  <small>%s</small>", BMI, BMIInstructions);
        mUserTarget = userBean.getTarget_step();
        mUserHeadUrl = userBean.getPortrait();
        mUserWeightMeasureTime = userBean.getWeight_measure_date();
        if (IF.isEmpty(mUserWeightMeasureTime) || mUserWeightMeasureTime.equals("0000-00-00")) {
            mUserWeightMeasureTime = DateUtil.getCurrentDate(DateUtil.YYYY_MM_DD);
        }
    }

    private void initDefaultValue() {
        mUserNickname = "";
        mUserSign = "";
        mUserGender = 1;
        mUserBirthDate = DateUtil.getCurrentDate(DateUtil.YYYY_MM_DD);
        mUserHeight = 160;
        mUserWeight = 50;
        String BMI = BMIUtil.getUserWeightBMI(mUserHeight, mUserWeight);
        String BMIInstructions = BMIUtil.getUserWeightType(mUserHeight, mUserWeight);
        mUserBMI = ResUtil.formatHtml("%s  <small>%s</small>", BMI, BMIInstructions);
        mUserTarget = 10000;
        mUserHeadUrl = "";
        mUserWeightMeasureTime = DateUtil.getCurrentDate(DateUtil.YYYY_MM_DD);
    }


    private void initData() {

        ////////////////////////////////////////////////////////////////////////////////////////
        //--------------------------------------单位配置-----------------------------------
        ////////////////////////////////////////////////////////////////////////////////////////
        double tempWeight = mUserWeight;
        UnitConfig config = AppUnitUtil.getUnitConfig();
        tempWeightSuffix = getString(R.string.unit_kg);
        isUnitPounds = config != null && config.weightUnit == UnitConfig.WEIGHT_LB;
        if (isUnitPounds) {
            tempWeight = UnitConversion.kgTolb(tempWeight);
            tempWeightSuffix = getString(R.string.unit_pounds);
        }


        ////////////////////////////////////////////////////////////////////////////////////////
        //--------------------------------------用户信息初始化-----------------------------------
        ////////////////////////////////////////////////////////////////////////////////////////

        initUserHead(mUserHeadUrl);
        String genderStr = mUserGender == 1 ? getString(R.string.content_gender_male) : getString(R.string.content_gender_female);
        etUserSettingNickname.setText(mUserNickname);
        etUserSettingNickname.setSelection(mUserNickname.length());
        etUserSettingNickname.addTextChangedListener(new InputTextWatcher(etUserSettingNickname));

        etUserSettingSign.setText(mUserSign);
        etUserSettingSign.setSelection(mUserSign.length());

        genderItem.setContent(genderStr);
        birthYearItem.setContent(mUserBirthDate.substring(0, 4));
        heightItem.setContent(ResUtil.format("%.0f%s",mUserHeight,getString(R.string.unit_cm)));

        lastWeightItem.setContent(ResUtil.format("%.0f%s", tempWeight, tempWeightSuffix));
        BMIItem.setContent(mUserBMI);
        dayTargetItem.setContent(mUserTarget + getString(R.string.step));

        switch (mNextType) {
            case TYPE_SAVE_AND_FINISH:
                tvUserSettingSave.setText(R.string.content_setting_save);
                break;
            case TYPE_SAVE_AND_START_MAIN_ACTIVITY:
                tvUserSettingSave.setText(R.string.content_setting_next);
                break;
        }
    }

    /**
     * 用户头像显示
     *
     * @param model 可以是本地文件地址,网络图片url 或资源id 或bitmap
     */
    private void initUserHead(Object model) {
        if (model instanceof Bitmap) {//如果是bitmap 则直接显示, Glide不支持直接显示bitmap
            civUserSettingHeadPortrait.setImageBitmap((Bitmap) model);
        } else {
            Glide.with(this)
                    .asBitmap()
                    .load(model)
                    .apply(RequestOptions.errorOf(R.mipmap.img_test_picture))
                    .apply(RequestOptions.placeholderOf(R.mipmap.img_test_picture))
                    .apply(RequestOptions.skipMemoryCacheOf(true))
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                    .into(civUserSettingHeadPortrait);
        }
    }


    @OnClick({
            R.id.tvUserSettingSave,
            R.id.civUserSettingHeadPortrait,
            R.id.ilUserSettingGender,
            R.id.ilUserSettingBirthYear,
            R.id.ilUserSettingHeight,
            R.id.ilUserSettingLastWeight,
            R.id.ilUserSettingTarget})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.civUserSettingHeadPortrait:
                portraitDialog();
                break;
            case R.id.ilUserSettingGender:
                genderDialog();
                break;
            case R.id.ilUserSettingBirthYear:
                birthDialog();
                break;
            case R.id.ilUserSettingHeight:
                heightDialog();
                break;
            case R.id.ilUserSettingLastWeight:
                weightDialog();
                break;
            case R.id.ilUserSettingTarget:
                targetDialog();
                break;
            case R.id.tvUserSettingSave:
                FastClickChecker.isFast();
                mUserNickname = ViewCompat.getText(etUserSettingNickname, true);
                mUserSign = ViewCompat.getText(etUserSettingSign, true);
                if (IF.isEmpty(mUserNickname)) {
                    setEditTextErrorTips(etUserSettingNickname, getString(R.string.content_nickname_not_be_null));
                    return;
                } else if (mUserNickname.length() < 1 || mUserNickname.length() > 16) {
                    setEditTextErrorTips(etUserSettingNickname, getString(R.string.content_nickname_illegal));
                    return;
                }

                LoadingDialog.show(UserSettingActivity.this, R.string.loading);
                getPresenter().requestUpdateUserData(mUserLocalImagePath, mUserNickname, mUserSign, mUserGender, mUserBirthDate, mUserHeight, mUserWeight, mUserWeightMeasureTime, mUserTarget);
                break;
        }
    }

    /**
     * 设置编辑框错误提示信息
     *
     * @param et    编辑框控件
     * @param error 错误信息
     */
    private void setEditTextErrorTips(final EditText et, CharSequence error) {
        et.setFocusable(true);
        et.setFocusableInTouchMode(true);
        et.requestFocus();
        et.setError(error, null);

    }


    /**
     * 头像弹框
     */
    private void portraitDialog() {
        new BaseDialog.Builder(this)
                .setContentView(R.layout.dialog_user_portrait)
                .fullWidth().setCanceledOnTouchOutside(true)
                .fromBottom(true)
                .setOnClickListener(R.id.tvUserPortraitTakePhoto, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        fromCamera();
                        dialogInterface.dismiss();
                    }
                })
                .setOnClickListener(R.id.tvUserPortraitChoosePhoto, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        HeadPickerUtil.fromPhotoFile(UserSettingActivity.this);
                        dialogInterface.dismiss();
                    }
                })
                .setOnClickListener(R.id.tvUserPortraitCancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                })
                .show();
    }

    private void fromCamera() {
        PermissionUtils.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, new PermissionUtils.OnPermissionGrantedListener() {
            @Override
            public void onGranted() {
                HeadPickerUtil.fromCamera(UserSettingActivity.this);
            }

            @Override
            public void onDenied() {

            }
        });


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            PermissionUtils.requestPermissions(this, new String[]{Manifest.permission.MANAGE_EXTERNAL_STORAGE}, new PermissionUtils.OnPermissionGrantedListener() {
                @Override
                public void onGranted() {

                }

                @Override
                public void onDenied() {

                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionUtils.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        SNLog.e("返回码 %d", resultCode);
        HeadPickerUtil.onActivityResult(this, requestCode, resultCode, data, onHeadPickerListener);
    }

    private HeadPickerUtil.OnHeadPickerListener onHeadPickerListener = new HeadPickerUtil.OnHeadPickerListener() {

        @Override
        public void onResult(String mImagePath) {
            mUserLocalImagePath = mImagePath;
            initUserHead(mImagePath);
        }

        @Override
        public void onFailed() {

        }
    };

    /**
     * 性别弹框
     */
    private void genderDialog() {
        final int temGender = mUserGender;
        BaseDialog dialog = new BaseDialog.Builder(this)
                .setContentView(R.layout.dialog_user_gender)
                .fullWidth()
                .setCanceledOnTouchOutside(false)
                .fromBottom(true)
                .setOnClickListener(R.id.rbUserGenderFemale, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mUserGender = 0;
                    }
                })
                .setOnClickListener(R.id.rbUserGenderMale, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mUserGender = 1;
                    }
                })
                .setOnClickListener(R.id.tvUserGenderExit, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mUserGender = temGender;
                        dialogInterface.cancel();
                    }
                })
                .setOnClickListener(R.id.tvUserGenderConfirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String genderStr = mUserGender == 1 ? getString(R.string.content_gender_male) : getString(R.string.content_gender_female);
                        genderItem.setContent(genderStr);
                        dialogInterface.dismiss();
                    }
                })
                .show();
        RadioButton female = dialog.findViewById(R.id.rbUserGenderFemale);
        RadioButton male = dialog.findViewById(R.id.rbUserGenderMale);
        switch (mUserGender) {
            case 0:
                if (female != null) {
                    female.setChecked(true);
                }
                break;
            case 1:
                if (male != null) {
                    male.setChecked(true);
                }
                break;
        }

    }

    /**
     * 生日弹框
     */
    private void birthDialog() {
        final String date = mUserBirthDate;
        BaseDialog dialog = new BaseDialog.Builder(this)
                .setContentView(R.layout.dialog_user_birthday)
                .fullWidth()
                .setCanceledOnTouchOutside(false)
                .fromBottom(true)
                .setOnClickListener(R.id.tvUserBirthdayExit, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mUserBirthDate = date;
                        dialogInterface.cancel();
                    }
                })
                .setOnClickListener(R.id.tvUserBirthdayConfirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        birthYearItem.setContent(mUserBirthDate.substring(0, 4));
                        dialogInterface.dismiss();
                    }
                })
                .show();

        DatePicker datePicker = dialog.findViewById(R.id.dpUserBirthdayDate);
        if (datePicker != null) {
            datePicker.setDate(mUserBirthDate);
            datePicker.setMaxYear(DateUtil.getYear(new Date()));
            datePicker.setOnDatePickerListener(new DatePicker.OnDatePickerListener() {
                @Override
                public void onValueChange(DatePicker picker, int year, int month, int day) {
                    resetDay(picker, year, month);
                    mUserBirthDate = picker.getDate();
                }
            });
        }


    }

    /**
     * 重设日期
     *
     * @param datePicker 日期选择器
     * @param year       年
     * @param month      月
     */
    private void resetDay(DatePicker datePicker, int year, int month) {
        int currentYear = DateUtil.getYear(new Date());
        int currentMonth = DateUtil.getMonth(new Date());
        int currentDay = DateUtil.getDay(new Date());
        if (currentYear == year) {
            datePicker.setMaxMonth(currentMonth);
        } else {
            datePicker.setMaxMonth(12);
        }

        if (currentYear == year && currentMonth == month) {
            datePicker.setMaxDay(currentDay);
        }

    }

    /**
     * 身高弹框
     */
    private void heightDialog() {
        final float heightTemp = mUserHeight;
        UserCommonDialog.create(this,
                getString(R.string.unit_cm),
                UserCommonDialog.FORMAT_ZERO_3,
                60,
                210,
                (int) mUserHeight,
                new NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                        mUserHeight = picker.getValue();
                    }
                },
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mUserHeight = heightTemp;
                        dialogInterface.cancel();
                    }
                },
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        heightItem.setContent(ResUtil.format("%.0f%s",mUserHeight,getString(R.string.unit_cm)));


                        String BMI = BMIUtil.getUserWeightBMI(mUserHeight, mUserWeight);
                        String BMIInstructions = BMIUtil.getUserWeightType(mUserHeight, mUserWeight);
                        mUserBMI = ResUtil.formatHtml("%s  <small>%s</small>", BMI, BMIInstructions);
                        BMIItem.setContent(mUserBMI);
                        dialogInterface.dismiss();
                    }
                }).show();
    }

    /**
     * 体重弹框
     */
    private void weightDialog() {
        final float tempWeight = mUserWeight;
        final String timeTemp = mUserWeightMeasureTime;

        UserCommonDialog.create(this,
                tempWeightSuffix,
                UserCommonDialog.FORMAT_ZERO_3,
                isUnitPounds ? 60 : 30,
                isUnitPounds ? 330 : 150,
                isUnitPounds ? (int) Math.round(UnitConversion.kgTolb(tempWeight)) : Math.round(tempWeight),
                new NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                        mUserWeight = picker.getValue();
                    }
                },
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mUserWeight = tempWeight;
                        mUserWeightMeasureTime = timeTemp;
                        dialogInterface.cancel();
                    }
                },
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mUserWeightMeasureTime = DateUtil.getDate(DateUtil.YYYY_MM_DD, new Date());
                        String BMI = BMIUtil.getUserWeightBMI(mUserHeight, mUserWeight);
                        String BMIInstructions = BMIUtil.getUserWeightType(mUserHeight, mUserWeight);
                        mUserBMI = ResUtil.formatHtml("%s  <small>%s</small>", BMI, BMIInstructions);

                        double tempWeight = mUserWeight;
                        if (isUnitPounds) {
                            mUserWeight = (float) UnitConversion.lbTokg(mUserWeight);
                        }
                        lastWeightItem.setContent(ResUtil.format("%.0f%s", tempWeight, tempWeightSuffix));

                        BMIItem.setContent(mUserBMI);
                        dialogInterface.dismiss();
                    }
                }).show();
    }

    /**
     * BMI指数弹框
     */
    private void bmiDialog() {
        new BaseDialog.Builder(this)
                .setContentView(R.layout.dialog_user_bmi)
                .setCanceledOnTouchOutside(true)
                .show();
    }

    /**
     * 目标步数弹框
     */
    private void targetDialog() {
        final int targetTemp = mUserTarget;
        UserCommonDialog.create(this,
                getString(R.string.step),
                UserCommonDialog.FORMAT_3,
                1,
                30,
                mUserTarget / 1000,
                ViewGroup.VISIBLE,
                ViewGroup.VISIBLE,
                new NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                        mUserTarget = picker.getValue() * 1000;
                    }
                },
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mUserTarget = targetTemp;
                        dialogInterface.cancel();
                    }
                },
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dayTargetItem.setContent(mUserTarget + getString(R.string.step));
                        dialogInterface.dismiss();
                    }
                }).show();
    }

    @Override
    public void onUpdateUserDataSuccess() {
        LoadingDialog.dismiss();
        switch (mNextType) {
            case TYPE_SAVE_AND_FINISH:
                setResult(Activity.RESULT_OK);
                finish();
                break;
            case TYPE_SAVE_AND_START_MAIN_ACTIVITY:
                PageJumpUtil.startMainActivity(this);
                finish();
                break;
        }
    }

    @Override
    public void onUpdateUserDataFailed(String msg) {
        LoadingDialog.dismiss();
        SNToast.toast(msg);
    }
}
