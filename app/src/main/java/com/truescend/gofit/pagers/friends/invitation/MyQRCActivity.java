package com.truescend.gofit.pagers.friends.invitation;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.sn.app.db.data.user.UserBean;
import com.sn.app.utils.AppUserUtil;
import com.sn.utils.SNToast;
import com.truescend.gofit.R;
import com.truescend.gofit.pagers.base.BaseActivity;
import com.truescend.gofit.utils.AppShareUtil;
import com.truescend.gofit.utils.ResUtil;
import com.truescend.gofit.utils.StatusBarUtil;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 作者:东芝(2018/08/22).
 * 功能:面对面邀请好友
 */
public class MyQRCActivity extends BaseActivity<MyQRCPresenterImpl, IMyQRCContract.IView> implements IMyQRCContract.IView {


    @BindView(R.id.ivBackground)
    ImageView ivWindowBackground;
    @BindView(R.id.ivBack)
    ImageView ivBack;
    @BindView(R.id.civUserHeadPortrait)
    CircleImageView civUserHeadPortrait;
    @BindView(R.id.tvNickname)
    TextView tvNickname;
    @BindView(R.id.tvSign)
    TextView tvSign;
    @BindView(R.id.tvIdName)
    TextView tvIdName;
    @BindView(R.id.btnSaveImage)
    Button btnSaveImage;

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, MyQRCActivity.class));
    }

    @Override
    protected MyQRCPresenterImpl initPresenter() {
        return new MyQRCPresenterImpl(this);
    }

    @Override
    protected int initLayout() {
        return R.layout.activity_my_qrcode;
    }

    @Override
    protected void onCreateActivity(Bundle savedInstanceState) {
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);
        StatusBarUtil.setStatusBarDarkTheme(this, false);
        StatusBarUtil.setStatusBarColor(this, 0x00000000);
        setHDBackground();
        getTitleLayout().setTitleShow(false);


        UserBean user = AppUserUtil.getUser();
        Glide.with(this)
                .asBitmap()
                .load(user.getPortrait())
                .apply(RequestOptions.errorOf(R.mipmap.img_test_picture))
                .apply(RequestOptions.placeholderOf(R.mipmap.img_test_picture))
                .apply(RequestOptions.skipMemoryCacheOf(true))
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                .into(civUserHeadPortrait);

        tvNickname.setText(user.getNickname());
        tvIdName.setText(ResUtil.format("ID:%d", user.getUser_id()));
        tvSign.setText(user.getSign());
    }

    private void setHDBackground() {
        Glide.with(this)
                .load(R.mipmap.icon_my_qrc)
                .into(ivWindowBackground);
    }


    @OnClick({R.id.ivBack, R.id.ivShare, R.id.btnSaveImage})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ivBack:
                onBackPressed();
                break;
            case R.id.btnSaveImage:
                getPresenter().saveScreenshots();
                break;
            case R.id.ivShare:
                AppShareUtil.shareCapture(this);
                break;
        }
    }

    @Override
    public void onSaveScreenshots(String path) {
        btnSaveImage.setEnabled(false);
        btnSaveImage.setClickable(false);
        SNToast.toast(getString(R.string.content_save_picture_to) + path);
    }
}
