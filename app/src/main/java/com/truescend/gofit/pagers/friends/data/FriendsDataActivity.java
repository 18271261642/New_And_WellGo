package com.truescend.gofit.pagers.friends.data;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.core.widget.NestedScrollView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.sn.app.net.data.app.bean.FriendInfoBean;
import com.sn.utils.SNToast;
import com.truescend.gofit.R;
import com.truescend.gofit.pagers.base.BaseActivity;
import com.truescend.gofit.pagers.common.dialog.LoadingDialog;
import com.truescend.gofit.pagers.friends.data.item.BloodPressureBloodOxygenDataItem;
import com.truescend.gofit.pagers.friends.data.item.HeartRateDataItem;
import com.truescend.gofit.pagers.friends.data.item.SleepDataPickerItem;
import com.truescend.gofit.pagers.friends.data.item.SportDataPickerItem;
import com.truescend.gofit.pagers.friends.info.FriendsInfoPresenterImpl;
import com.truescend.gofit.pagers.user.bean.ItemUserFile;
import com.truescend.gofit.utils.AppShareUtil;
import com.truescend.gofit.utils.ResUtil;
import com.truescend.gofit.utils.StatusBarUtil;
import com.truescend.gofit.views.BloodPressureChartView;
import com.truescend.gofit.views.TitleLayout;
import com.truescend.gofit.views.VerticalScrollView;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 作者:东芝(2018/08/09).
 * 功能:好友数据
 */
public class FriendsDataActivity extends BaseActivity<FriendsDataPresenterImpl, IFriendsDataContract.IView> implements IFriendsDataContract.IView {

    @BindView(R.id.mVerticalScrollView)
    VerticalScrollView mVerticalScrollView;
    @BindView(R.id.llTitle)
    View llTitle;

    @BindView(R.id.civHead)
    CircleImageView civHead;
    @BindView(R.id.ivZan)
    ImageView ivZan;
    @BindView(R.id.ivEncourage)
    ImageView ivEncourage;
    @BindView(R.id.tvZan)
    TextView tvZan;
    @BindView(R.id.tvEncourage)
    TextView tvEncourage;
    @BindView(R.id.tvNickname)
    TextView tvNickname;
    @BindView(R.id.ivGender)
    ImageView ivGender;
    @BindView(R.id.tvIdName)
    TextView tvIdName;
    @BindView(R.id.ivBack)
    ImageView ivBack;
    @BindView(R.id.ivShare)
    ImageView ivShare;
    @BindView(R.id.ilUserBestRecord)
    View ilUserBestRecord;
    @BindView(R.id.ilUserStandardDay)
    View ilUserStandardDay;
    @BindView(R.id.ilUserBestWeek)
    View ilUserBestWeek;
    @BindView(R.id.ilUserBestMonth)
    View ilUserBestMonth;
    @BindView(R.id.sportPickerLayout)
    SportDataPickerItem mSportPickerLayout;
    @BindView(R.id.sleepPickerLayout)
    SleepDataPickerItem mSleepPickerLayout;
    @BindView(R.id.mHeartRateDataItem)
    HeartRateDataItem mHeartRateDataItem;
    @BindView(R.id.mBloodPressureBloodOxygenDataItem)
    BloodPressureBloodOxygenDataItem mBloodPressureBloodOxygenDataItem;

    private int user_id;


    private ItemUserFile bestRecordItem;
    private ItemUserFile standardDayItem;
    private ItemUserFile bestWeekItem;
    private ItemUserFile bestMonthItem;



    public static void startActivity(Context context, int user_id) {
        context.startActivity(new Intent(context, FriendsDataActivity.class).putExtra("user_id", user_id));
    }

    @Override
    protected FriendsDataPresenterImpl initPresenter() {
        return new FriendsDataPresenterImpl(this);
    }

    @Override
    protected int initLayout() {
        return R.layout.activity_friends_data;
    }

    @Override
    protected void onCreateTitle(TitleLayout titleLayout) {
        titleLayout.setTitleShow(false);
    }

    @Override
    protected void onCreateActivity(Bundle savedInstanceState) {
        initTheme();

        user_id = getIntent().getIntExtra("user_id", -1);
        if (user_id == -1) {
            finish();
            return;
        }

        initTheBestDataItem();
        initTitleAlphaAnimation();
        getPresenter().getFriendsInfoResults(user_id);

    }

    private void initTitleAlphaAnimation() {
        int heightPixels = getResources().getDisplayMetrics().heightPixels;
        final float bgHeight = heightPixels * 0.4f;//布局中 黑色背景区域刚好是 屏幕的40%sh
        mVerticalScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                int v1 = 127;
                int alpha = Math.round(scrollY * v1 * 1.0f / (bgHeight+55));
                if (alpha > v1) {
                    alpha = v1;
                }
                llTitle.setBackgroundColor(argb(alpha,0,0,0));
            }

            int argb(int alpha, int red, int green, int blue) {
                return (alpha << 24) | (red << 16) | (green << 8) | blue;
            }
        });
    }



    private void initTheBestDataItem() {

        bestRecordItem = new ItemUserFile(ilUserBestRecord);
        standardDayItem = new ItemUserFile(ilUserStandardDay);
        bestWeekItem = new ItemUserFile(ilUserBestWeek);
        bestMonthItem = new ItemUserFile(ilUserBestMonth);

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
    }

    private void initTheme() {
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);
        StatusBarUtil.setStatusBarDarkTheme(this, false);
        StatusBarUtil.setStatusBarColor(this, 0x00000000);

        int color = getResources().getColor(R.color.white);
        tvIdName.setTextColor(color);
        tvNickname.setTextColor(color);
        civHead.setBorderColor(color);
        tvZan.setTextColor(color);
        tvEncourage.setTextColor(color);
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

    @Override
    public void onSportDateData(List<SportDataPickerItem.SportDateItem> items) {
        mSportPickerLayout.setSportDateItems(items);
    }

    @Override
    public void onSleepDateData(List<SleepDataPickerItem.SleepDateItem> items) {
        mSleepPickerLayout.setSleepDateItems(items);
    }

    @Override
    public void onHeartRateDateData(List<Integer> items, int heart_max, int heart_ave, int heart_min) {
        mHeartRateDataItem.setHeartRateData(items ,  heart_max,   heart_ave,   heart_min);
    }

    @Override
    public void onBloodPressureDateData(List<BloodPressureChartView.BloodPressureItem> items, int diastolic_avg, int systolic_avg) {
        mBloodPressureBloodOxygenDataItem.setBloodPressureData(items,diastolic_avg,systolic_avg);
    }

    @Override
    public void onBloodOxygenDateData(List<Integer> items, int oxygen_avg) {
        mBloodPressureBloodOxygenDataItem.setBloodOxygenData(items,oxygen_avg);
    }

    @Override
    public void onFriendsInfoResults(FriendInfoBean.DataBean data) {

        Glide.with(civHead)
                .load(data.getPortrait())
                .apply(RequestOptions.errorOf(R.mipmap.img_test_picture))
                .apply(RequestOptions.placeholderOf(R.mipmap.img_test_picture))
                .into(civHead);


        tvIdName.setText(ResUtil.format("ID:%d", data.getId()));
        tvNickname.setText(data.getNickname());
        ivGender.setSelected(data.getGender() == 1);//1：男，2：女
        ivZan.setSelected(data.getIs_thumb() == 1);
        ivEncourage.setSelected(data.getIs_encourage() == 1);


    }

    @Override
    public void onFriendsThumbAction(int type) {
        switch (type) {
            case FriendsInfoPresenterImpl.THUMBACTION_TYPE_ZAN:
                ivZan.setSelected(true);
                break;
            case FriendsInfoPresenterImpl.THUMBACTION_TYPE_ENCOURAGE:
                ivEncourage.setSelected(true);
                break;
        }
    }

    @OnClick({R.id.ivZan, R.id.ivEncourage, R.id.ivBack, R.id.ivShare})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ivZan:
                getPresenter().setFriendsThumbAction(user_id, FriendsInfoPresenterImpl.THUMBACTION_TYPE_ZAN);
                break;
            case R.id.ivEncourage:
                getPresenter().setFriendsThumbAction(user_id, FriendsInfoPresenterImpl.THUMBACTION_TYPE_ENCOURAGE);
                break;
            case R.id.ivBack:
                onBackPressed();
                break;
            case R.id.ivShare:
                AppShareUtil.shareCapture(this,R.id.mVerticalScrollView);
                break;
        }
    }

}
