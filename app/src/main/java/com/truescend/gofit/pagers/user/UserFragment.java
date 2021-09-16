package com.truescend.gofit.pagers.user;

import android.app.Dialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.sn.app.db.data.config.bean.UnitConfig;
import com.sn.app.db.data.user.UserBean;
import com.sn.app.utils.AppUnitUtil;
import com.sn.app.utils.AppUserUtil;
import com.sn.blesdk.ble.DeviceType;
import com.sn.blesdk.net.bean.DeviceInfo;
import com.sn.utils.IF;
import com.sn.utils.LanguageUtil;
import com.truescend.gofit.BuildConfig;
import com.truescend.gofit.R;
import com.truescend.gofit.pagers.base.BaseFragment;
import com.truescend.gofit.pagers.common.bean.ItemBannerButton;
import com.truescend.gofit.pagers.common.dialog.CommonDialog;
import com.truescend.gofit.pagers.user.bean.ItemInformation;
import com.truescend.gofit.pagers.user.bean.ItemUserFile;
import com.truescend.gofit.utils.AppShareUtil;
import com.truescend.gofit.utils.BMIUtil;
import com.truescend.gofit.utils.MapType;
import com.truescend.gofit.utils.PageJumpUtil;
import com.truescend.gofit.utils.ResUtil;
import com.truescend.gofit.utils.UnitConversion;
import com.truescend.gofit.utils.UnreadMessages;
import com.truescend.gofit.views.BadgeHelper;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 功能：用户界面
 * Author:Created by 泽鑫 on 2018/1/24 11:04.
 */
public class UserFragment extends BaseFragment<UserPresenterImpl, IUserContract.IView> implements IUserContract.IView {

    @BindView(R.id.ivUserShare)
    ImageView ivUserShare;
    @BindView(R.id.civUserHeadPortrait)
    CircleImageView civUserHeadPortrait;
    @BindView(R.id.ilUserNickName)
    View ilUserNickName;
    @BindView(R.id.ilUserWeight)
    View ilUserWeight;
    @BindView(R.id.ilUserDate)
    View ilUserDate;
    @BindView(R.id.ilUserBMI)
    View ilUserBMI;
    @BindView(R.id.ilUserID)
    View ilUserID;
    @BindView(R.id.ilUserBestRecord)
    View ilUserBestRecord;
    @BindView(R.id.ilUserStandardDay)
    View ilUserStandardDay;
    @BindView(R.id.ilUserBestWeek)
    View ilUserBestWeek;
    @BindView(R.id.ilUserBestMonth)
    View ilUserBestMonth;
    @BindView(R.id.ilUserPersonalSettings)
    View ilUserPersonalSettings;
    @BindView(R.id.ilUserFriends)
    View ilUserFriends;
    @BindView(R.id.ilUserGoogleFit)
    View ilUserGoogleFit;
    @BindView(R.id.ilUserStrava)
    View ilUserStrava;
    @BindView(R.id.ilUserTmallGenie)
    View ilUserTmallGenie;

    @BindView(R.id.ilDeviceSettingFeedback)
    View ilDeviceSettingFeedback;
    @BindView(R.id.ilDeviceSettingHelp)
    View ilDeviceSettingHelp;

    @BindView(R.id.tvUserExitCurrentAccount)
    View tvUserExitCurrentAccount;

    private ItemInformation nickNameItem;
    private ItemInformation weightItem;
    private ItemInformation dateItem;
    private ItemInformation BMIItem;
    private ItemInformation userIDItem;

    private ItemUserFile bestRecordItem;
    private ItemUserFile standardDayItem;
    private ItemUserFile bestWeekItem;
    private ItemUserFile bestMonthItem;

    private ItemBannerButton userFriendsItem;
    private ItemBannerButton personalSettingItem;
    private ItemBannerButton googleFitItem;
    private ItemBannerButton feedbackItem;
    private ItemBannerButton helpItem;
    private ItemBannerButton stravaItem;
    private ItemBannerButton tmallGenieItem;
    private BadgeHelper badgeHelper;


    @Override
    protected void onCreate() {
        initItem();
        initBadge();
    }

    private void initBadge() {
        badgeHelper = new BadgeHelper(getActivity())
                .setBadgeType(BadgeHelper.Type.TYPE_POINT)
                .setBadgeOverlap(false);
        badgeHelper
                .bindToTargetView(ilUserFriends.findViewById(R.id.tvBannerButtonTitle));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        CommonDialog.dismiss();
    }

    @Override
    public UserPresenterImpl initPresenter() {
        return new UserPresenterImpl(this);
    }

    @Override
    protected int initLayout() {
        return R.layout.fragment_user;
    }

    private void initItem() {
        nickNameItem = new ItemInformation(ilUserNickName);
        weightItem = new ItemInformation(ilUserWeight);
        dateItem = new ItemInformation(ilUserDate);
        BMIItem = new ItemInformation(ilUserBMI);
        userIDItem = new ItemInformation(ilUserID);

        bestRecordItem = new ItemUserFile(ilUserBestRecord);
        standardDayItem = new ItemUserFile(ilUserStandardDay);
        bestWeekItem = new ItemUserFile(ilUserBestWeek);
        bestMonthItem = new ItemUserFile(ilUserBestMonth);

        userFriendsItem = new ItemBannerButton(ilUserFriends);
        personalSettingItem = new ItemBannerButton(ilUserPersonalSettings);

        googleFitItem = new ItemBannerButton(ilUserGoogleFit);
        stravaItem = new ItemBannerButton(ilUserStrava);
        tmallGenieItem = new ItemBannerButton(ilUserTmallGenie);

        feedbackItem = new ItemBannerButton(ilDeviceSettingFeedback);


        helpItem = new ItemBannerButton(ilDeviceSettingHelp);


        nickNameItem.setContentVisibility(View.GONE);
        nickNameItem.setNoteVisibility(View.GONE);
        weightItem.setTitle(R.string.content_last_weight);
        weightItem.setNoteVisibility(View.GONE);
        dateItem.setTitle(R.string.content_last_date);
        dateItem.setNoteVisibility(View.GONE);
        BMIItem.setTitle(R.string.content_bmi_index);

        userIDItem.setTitle("ID:");
        userIDItem.setNoteVisibility(View.GONE);

        bestRecordItem.setTitle(R.string.content_personal_best_record);
        bestRecordItem.setImage(R.mipmap.icon_best_record);
        bestRecordItem.setUnit(R.string.step);
        bestRecordItem.setData("0");
        bestRecordItem.setDate(getString(R.string.content_no_data));


        standardDayItem.setTitle(R.string.content_standard_day);
        standardDayItem.setImage(R.mipmap.icon_standard_day);
        standardDayItem.setUnit(R.string.day);
        standardDayItem.setData("0");
        standardDayItem.setDate(getString(R.string.content_no_data));


        bestWeekItem.setTitle(R.string.content_best_week);
        bestWeekItem.setUnit(R.string.step);
        bestWeekItem.setImage(R.mipmap.icon_best_week);
        bestWeekItem.setData("0");
        bestWeekItem.setDate(getString(R.string.content_no_data));

        bestMonthItem.setTitle(R.string.content_best_month);
        bestMonthItem.setImage(R.mipmap.icon_best_month);
        bestMonthItem.setUnit(R.string.step);
        bestMonthItem.setData("0");
        bestMonthItem.setDate(getString(R.string.content_no_data));


        userFriendsItem.setTitle(R.string.content_my_friends);
        userFriendsItem.setTitleIcon(R.mipmap.icon_setting_friends);

        personalSettingItem.setTitle(R.string.content_personal_setting);
        personalSettingItem.setTitleIcon(R.mipmap.icon_setting_user);

        helpItem.setTitle(R.string.content_help);
        helpItem.setTitleIcon(R.mipmap.icon_setting_help);

        feedbackItem.setTitle(R.string.content_feedback);
        feedbackItem.setTitleIcon(R.mipmap.icon_setting_feedback);


        googleFitItem.setTitle(R.string.content_connect_google_fit);
        googleFitItem.setTitleIcon(R.mipmap.icon_setting_google_fit);

        stravaItem.setTitle(R.string.content_connect_strava);
        stravaItem.setTitleIcon(R.mipmap.icon_setting_strava);

        if (!BuildConfig.isSupportStrava) {
            ilUserStrava.setVisibility(View.GONE);
        }
        if (!BuildConfig.isSupportTmallGenie) {
            ilUserTmallGenie.setVisibility(View.GONE);
        } else {
            DeviceInfo deviceInfo = DeviceType.getCurrentDeviceInfo();
            if (deviceInfo != null && !deviceInfo.isSupportTmallGenie()) {
                ilUserTmallGenie.setVisibility(View.GONE);
            } else if (LanguageUtil.isZH()) {
                //目前仅支持中文,不需要做多语言翻译,所以直接写死
                tmallGenieItem.setTitle("关联 天猫精灵");
                tmallGenieItem.setTitleIcon(R.mipmap.icon_setting_tmall_genie);
            } else {
                ilUserTmallGenie.setVisibility(View.GONE);
            }

        }


    }

    @Override
    protected void onVisible() {
        super.onVisible();
        initUserInfo();
        initUserBestStatistical();
        refreshBadgeStatus();

    }

    private void refreshBadgeStatus() {
        if (badgeHelper != null) {
            badgeHelper.setBadgeEnable(UnreadMessages.hasUnreadMessage());
        }
    }

    private void initUserBestStatistical() {
        getPresenter().requestUserBestStatistical();
    }

    private void initUserInfo() {
        UserBean user = AppUserUtil.getUser();
        UnitConfig config = AppUnitUtil.getUnitConfig();
        Glide.with(getActivity())
                .asBitmap()
                .load(user.getPortrait())
                .apply(RequestOptions.errorOf(R.mipmap.img_test_picture))
                .apply(RequestOptions.placeholderOf(R.mipmap.img_test_picture))
                .apply(RequestOptions.skipMemoryCacheOf(true))
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                .into(civUserHeadPortrait);

        nickNameItem.setTitle(user.getNickname());
        nickNameItem.setTitleColor(getResources().getColor(R.color.white));


        double tempWeight = user.getWeight();
        String tempWeightSuffix = getString(R.string.unit_kg);
        if (config.weightUnit == UnitConfig.WEIGHT_LB) {
            tempWeight = UnitConversion.kgTolb(tempWeight);
            tempWeightSuffix = getString(R.string.unit_pounds);
        }
        weightItem.setContent(user.getWeight() == 0 ? getString(R.string.content_no_data) : ResUtil.format("%.1f%s", tempWeight, tempWeightSuffix));


        String weightMeasureDate = user.getWeight_measure_date();
        if (IF.isEmpty(weightMeasureDate) || weightMeasureDate.equals("0000-00-00")) {
            dateItem.setContent(getString(R.string.content_no_data));
        } else {
            dateItem.setContent(user.getWeight_measure_date());
        }
        userIDItem.setContent(String.valueOf(user.getUser_id()));

        userIDItem.setContent(String.valueOf(user.getUser_id()));

        String BMI = BMIUtil.getUserWeightBMI(user.getHeight(), user.getWeight());
        String BMIInstructions = BMIUtil.getUserWeightType(user.getHeight(), user.getWeight());
        BMIItem.setContent(BMI);
        BMIItem.setNote(BMIInstructions);
    }


    @OnClick({
            R.id.ilUserGoogleFit,
            R.id.ilUserStrava,
            R.id.ilUserTmallGenie,
            R.id.ilDeviceSettingHelp,
            R.id.ivUserShare,
            R.id.ilUserPersonalSettings,
            R.id.ilUserFriends,
            R.id.tvUserExitCurrentAccount,
            R.id.ilDeviceSettingFeedback})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ilUserFriends:
                PageJumpUtil.startFriendsListActivity(getActivity());
                break;
            case R.id.ivUserShare:
                AppShareUtil.shareCapture(getActivity());
                break;
            case R.id.ilUserPersonalSettings:
                PageJumpUtil.startUserSettingActivity(UserFragment.this);
                break;
            case R.id.ilUserStrava:
                PageJumpUtil.startConnectStravaActivity(getContext());
                break;
            case R.id.ilUserGoogleFit:
                boolean available = MapType.isGooglePlayServicesAvailable(getContext());
                if (!available) {
                    Dialog dialog = MapType.getGooglePlayServicesErrorDialog(getActivity());
                    dialog.setCancelable(false);
                    dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            finish();
                        }
                    });
                    dialog.show();
                } else {
                    PageJumpUtil.startConnectGoogleFitActivity(getContext());

                }
                break;
            case R.id.tvUserExitCurrentAccount:
                CommonDialog.create(getContext(),
                        getString(R.string.content_are_you_sure),
                        getString(R.string.content_exit_login),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        },
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                PageJumpUtil.startLoginActivity(getContext(), true);
                                dialogInterface.dismiss();
                            }
                        }
                ).show();
                break;
            case R.id.ilDeviceSettingFeedback:
                PageJumpUtil.startFeedbackActivity(getActivity());
                break;
            case R.id.ilDeviceSettingHelp:
                PageJumpUtil.startHelpActivity(getActivity());
                break;
            case R.id.ilUserTmallGenie:
                PageJumpUtil.startTmallGenieAuthActivity(getActivity());
                break;
            default:
                break;
        }
    }

    @Override
    public void onUpdateUserBestDays(String date, String stepTotal) {
        bestRecordItem.setData(stepTotal);
        bestRecordItem.setDate(date);
    }

    @Override
    public void onUpdateUserBestContinuesDays(String dateRange, String days) {
        standardDayItem.setData(days);
        standardDayItem.setDate(dateRange);

    }

    @Override
    public void onUpdateUserBestWeeks(String dateRange, String days) {
        bestWeekItem.setData(days);
        bestWeekItem.setDate(dateRange);

    }

    @Override
    public void onUpdateUserBestMonths(String dateRange, String days) {
        bestMonthItem.setData(days);
        bestMonthItem.setDate(dateRange);
    }


}
