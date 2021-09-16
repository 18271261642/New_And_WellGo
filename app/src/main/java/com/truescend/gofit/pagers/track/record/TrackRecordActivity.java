package com.truescend.gofit.pagers.track.record;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sn.utils.SNToast;
import com.truescend.gofit.BuildConfig;
import com.truescend.gofit.R;
import com.truescend.gofit.pagers.base.BaseActivity;
import com.truescend.gofit.pagers.common.dialog.LoadingDialog;
import com.truescend.gofit.pagers.track.bean.PathMapItem;
import com.truescend.gofit.utils.AppShareUtil;
import com.truescend.gofit.utils.PageJumpUtil;
import com.truescend.gofit.views.TitleLayout;

import butterknife.BindView;

/**
 * 作者:东芝(20178/01/06).
 * 功能: 运动轨迹记录查看
 */
public class TrackRecordActivity extends BaseActivity<TrackRecordPresenterImpl, ITrackRecordContract.IView> implements ITrackRecordContract.IView {


    public static final String KEY_PATH_MAP_ITEM = "KEY_PATH_MAP_ITEM";
    @BindView(R.id.ivTrackRecordDistanceTotal)
    TextView ivTrackRecordDistanceTotal;
    @BindView(R.id.ivTrackRecordSpendTime)
    TextView ivTrackRecordSpendTime;
    @BindView(R.id.ivTrackRecordAvgSpeed)
    TextView ivTrackRecordAvgSpeed;
    @BindView(R.id.ivTrackRecordMaxSpeed)
    TextView ivTrackRecordMaxSpeed;
    @BindView(R.id.ivTrackRecordAvgPace)
    TextView ivTrackRecordAvgPace;
    @BindView(R.id.ivTrackRecordCalories)
    TextView ivTrackRecordCalories;
    @BindView(R.id.ivTrackRecordMapImage)
    ImageView ivTrackRecordMapImage;

    private PathMapItem data;

    @Override
    protected TrackRecordPresenterImpl initPresenter() {
        return new TrackRecordPresenterImpl(this);
    }

    @Override
    protected int initLayout() {
        return R.layout.activity_track_record;
    }

    @Override
    protected void onCreateTitle(TitleLayout titleLayout) {
        titleLayout.setLeftIconFinishActivity(this);//默认左边按钮是finish
        titleLayout.setTitle("");
        titleLayout.addRightItem(TitleLayout.ItemBuilder.Builder().setIcon(R.mipmap.icon_share_black).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppShareUtil.shareCapture(TrackRecordActivity.this);
            }
        }));
    }

    @Override
    protected void onCreateActivity(Bundle savedInstanceState) {
        data = (PathMapItem) getIntent().getSerializableExtra(KEY_PATH_MAP_ITEM);
        if (data == null) {
            finish();
            return;
        }
        getPresenter().requestLoadTrackRecord(data);
    }


    @Override
    public void onUpdateTrackRecord(String distanceTotal, String spendTimeTotal, String speedAvgTotal, String speedMaxTotal, String speedAvgPaceTotal, String calories, String screenshotUrl) {
        ivTrackRecordDistanceTotal.setText(distanceTotal);
        ivTrackRecordSpendTime.setText(spendTimeTotal);
        ivTrackRecordAvgSpeed.setText(speedAvgTotal);
        ivTrackRecordMaxSpeed.setText(speedMaxTotal);
        ivTrackRecordAvgPace.setText(speedAvgPaceTotal);
        ivTrackRecordCalories.setText(calories);
        Glide.with(this).load(screenshotUrl).into(ivTrackRecordMapImage);
    }

    @Override
    public void onShowUploadToStravaItem(final boolean isUploaded) {
       if(BuildConfig.isSupportStrava){
           getTitleLayout().addRightItem(
                   TitleLayout.ItemBuilder.Builder()
                           .setText(isUploaded ? R.string.content_uploaded_to_strava : R.string.content_upload_to_strava)
                           .setTextViewTag("isUploadedButton")
                           .setOnClickListener(new View.OnClickListener() {
                               @Override
                               public void onClick(View v) {
                                   getPresenter().requestUpLoadToStrava();
                               }
                           }), 0);
       }
    }

    @Override
    public void onUpdateUploadToStravaUnAuthorizedFailed() {
        PageJumpUtil.startConnectStravaActivity(this);
    }

    @Override
    public void onUpdateUploadToStravaUpdating() {
        LoadingDialog.show(this, R.string.loading);
    }

    @Override
    public void onUpdateUploadToStravaSuccess() {
        getTitleLayout().setTextWithTag("isUploadedButton",getString(R.string.content_uploaded_to_strava));
        SNToast.toast(R.string.content_uploaded_to_strava);
        LoadingDialog.dismiss();
    }

    @Override
    public void onUpdateUploadToStravaFailed(String msg) {
        SNToast.toast(msg);
        LoadingDialog.dismiss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        data = null;

    }
}
