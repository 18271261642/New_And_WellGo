package com.truescend.gofit.pagers.start.login;

import android.Manifest;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;

import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sn.app.storage.UserStorage;
import com.sn.login.LoginSDK;
import login.utils.LoginUtil;
import com.sn.utils.IF;
import com.sn.utils.LanguageUtil;
import com.sn.utils.RegexUtil;
import com.sn.utils.SNToast;
import com.sn.utils.SystemUtil;
import com.truescend.gofit.BuildConfig;
import com.truescend.gofit.R;
import com.truescend.gofit.pagers.base.BaseActivity;
import com.truescend.gofit.pagers.common.dialog.CommonDialog;
import com.truescend.gofit.pagers.common.dialog.LoadingDialog;
import com.truescend.gofit.pagers.common.dialog.TermsOfServiceDialog;
import com.truescend.gofit.pagers.user.setting.UserSettingActivity;
import com.truescend.gofit.utils.PageJumpUtil;
import com.truescend.gofit.utils.PermissionUtils;
import com.truescend.gofit.views.CustomVideoView;
import com.truescend.gofit.views.HintMultiLineEditText;
import com.truescend.gofit.views.TitleLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

/**
 * 作者:东芝(2018/2/28).
 * 功能:登录页+视频引导
 */

public class LoginActivity extends BaseActivity<LoginPresenterImpl, ILoginContract.IView> implements ILoginContract.IView {

    @BindView(R.id.cvvVideoView)
    CustomVideoView cvvVideoView;
    @BindView(R.id.vFloatingLayerView)
    View vFloatingLayerView;
    @BindView(R.id.btnVideoGuideGuestLoginIn)
    Button btnVideoGuideGuestLoginIn;
    @BindView(R.id.btnVideoGuideUserLoginIn)
    Button btnVideoGuideUserLoginIn;
    @BindView(R.id.btnVideoGuideUserRegister)
    TextView btnVideoGuideUserRegister;
    @BindView(R.id.llBottomLayout)
    View llBottomLayout;
    @BindView(R.id.etVideoGuideAccount)
    HintMultiLineEditText etVideoGuideAccount;
    @BindView(R.id.etVideoGuidePassword)
    HintMultiLineEditText etVideoGuidePassword;
    @BindView(R.id.tvVideoGuideForgetPwd)
    TextView tvVideoGuideForgetPwd;

    @BindView(R.id.ivLoginQQSignIn)
    ImageView ivLoginQQSignIn;
    @BindView(R.id.ivLoginWeChatSignIn)
    ImageView ivLoginWeChatSignIn;
    @BindView(R.id.ivLoginTwitterSignIn)
    ImageView ivLoginTwitterSignIn;
    @BindView(R.id.tvLoginTerms)
    CheckBox tvLoginTerms;
    @BindView(R.id.tvVideoGuideFeedback)
    TextView tvVideoGuideFeedback;
    private String[] qqPackages = {"com.tencent.mobileqq", "com.tencent.tim", "com.tencent.minihd.qq", "com.tencent.qqlite", "com.tencent.mobileqqi", "com.tencent.qq.kddi", "com.tencent.eim"};

    @Override
    protected void onCreateTitle(TitleLayout titleLayout) {
        titleLayout.setTitleShow(false);
    }

    private int stopPosition;

    @Override
    protected LoginPresenterImpl initPresenter() {
        return new LoginPresenterImpl(this);
    }

    @Override
    protected int initLayout() {
        return R.layout.activity_login;
    }

    @Override
    protected void onCreateActivity(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            stopPosition = savedInstanceState.getInt("position");
        }
        //TOKEN  过期/或是首次(用户未填写信息)则直接进入 登录页 否则直接进入主页面
        if (IF.isEmpty(UserStorage.getAccessToken()) || UserStorage.getUserId() == -1 || UserStorage.isFirst()) {
            etVideoGuideAccount.setText(UserStorage.getAccount());
            etVideoGuidePassword.setText(UserStorage.getPassword());
            initVideoData();
        } else {
            onLoginSuccess(false);
        }
        if (!BuildConfig.isSupportLoginFromQQ &&
                !BuildConfig.isSupportLoginFromWX &&
                !BuildConfig.isSupportLoginFromTwitter) {
            ((View) ivLoginQQSignIn.getParent()).setVisibility(View.GONE);
        } else {
            if (!BuildConfig.isSupportLoginFromQQ) {
                ivLoginQQSignIn.setVisibility(View.GONE);
            }
            if (!BuildConfig.isSupportLoginFromWX) {
                ivLoginWeChatSignIn.setVisibility(View.GONE);
            }
            if (!BuildConfig.isSupportLoginFromTwitter) {
                ivLoginTwitterSignIn.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (etVideoGuideAccount != null) {
            etVideoGuideAccount.setText(UserStorage.getAccount());
        }
    }

    private void initVideoData() {
        //设置播放加载路径
        cvvVideoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.login_video));
        //播放
        cvvVideoView.start();
        cvvVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                //循环播放
                cvvVideoView.start();

            }
        });
        showLoginOrRegisterLayout();
    }

    private void showLoginOrRegisterLayout() {
        vFloatingLayerView.setAlpha(0);
        vFloatingLayerView.animate()
                .setStartDelay(1000)
                .setDuration(2000)
                .alpha(1)
                .start();
        //显示底部布局
        llBottomLayout.setVisibility(View.VISIBLE);
        //显示顶部注册按钮
        btnVideoGuideUserRegister.setVisibility(View.VISIBLE);

        //先设置底部布局的高度为屏幕高+布局高 则布局在屏幕下方并不可见,  然后通过动画 把布局移动到可见位置
        int heightPixels = getResources().getDisplayMetrics().heightPixels;
        llBottomLayout.setTranslationY((heightPixels + llBottomLayout.getHeight()));

        llBottomLayout.animate().setStartDelay(1000)
                .setDuration(2000)
                .translationY(0)
                .setInterpolator(new DecelerateInterpolator())
                .start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cvvVideoView.seekTo(stopPosition);
        cvvVideoView.start();

    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        stopPosition = cvvVideoView.getCurrentPosition();
        cvvVideoView.pause();
        outState.putInt("position", stopPosition);
    }

    @OnCheckedChanged({R.id.tvLoginTerms})


    void OnCheckedChanged(CompoundButton buttonView, boolean isChecked){
        switch (buttonView.getId()) {
            case R.id.tvLoginTerms:
                if(isChecked) {
                    TermsOfServiceDialog.create(this).show();
                    LoginSDK.init(this);
                }
        }
    }

    @OnClick({//R.id.tvLoginTerms,
            R.id.btnVideoGuideGuestLoginIn,
            R.id.btnVideoGuideUserLoginIn,
            R.id.btnVideoGuideUserRegister,
            R.id.tvVideoGuideForgetPwd,
            R.id.ivLoginQQSignIn,
            R.id.ivLoginWeChatSignIn,
            R.id.ivLoginTwitterSignIn,
            R.id.tvVideoGuideFeedback
    })



    public void onViewClicked(View view) {
        switch (view.getId()) {
//            case R.id.tvLoginTerms:
//                TermsOfServiceDialog.create(this).show();
//                //用户登录后
//                //第三方登录
//                if(tvLoginTerms.isChecked()) {
//                    LoginSDK.init(this);
//                }
//                break;
            case R.id.btnVideoGuideUserRegister:
                PageJumpUtil.startRegisterActivity(this);
                break;
            case R.id.tvVideoGuideForgetPwd:
                PageJumpUtil.startResetPswCheckerActivity(this);
                break;
            case R.id.btnVideoGuideGuestLoginIn:
                if (SystemUtil.isMIUI12()) {
                    String tips = "小米 MIUI12 需要将[获取手机信息]权限从[空白通行证]修改为[始终允许], 否则你以前的数据可能会丢失. 为了你的数据稳定, 建议使用账号注册";
                    if(!LanguageUtil.isZH()){
                        tips="Xiaomi MIUI12 needs to change the [Get Device info] permission from [Return black message] to [Always allow], otherwise your previous data may be lost. For your data stability, it is recommended to use account registration";
                    }
                    CommonDialog.create(this,
                            getString(R.string.content_authorized),
                            tips,
                            getString(R.string.content_register),
                            getString(R.string.content_setting_next),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                    PageJumpUtil.startRegisterActivity(LoginActivity.this);
                                }
                            },
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                    List<String> permissions = new ArrayList<>();
                                    permissions.add(Manifest.permission.READ_PHONE_STATE);
                                    PermissionUtils.requestPermissions(LoginActivity.this, permissions, new PermissionUtils.OnPermissionGrantedListener() {
                                        @Override
                                        public void onGranted() {
                                            LoadingDialog.loading(LoginActivity.this);
                                            getPresenter().requestLoginOther(SystemUtil.getUniqueId(LoginActivity.this), "guest");

                                        }

                                        @Override
                                        public void onDenied() {

                                        }
                                    });

                                }
                            }
                    ).show();



                }else {
                    CommonDialog.create(this,
                            getString(R.string.content_guest_login_in),
                            getString(R.string.content_guest_login_alert),
                            getString(R.string.content_register),
                            getString(R.string.content_guest_login_in),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                    PageJumpUtil.startRegisterActivity(LoginActivity.this);
                                }
                            },
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();

                                    LoadingDialog.loading(LoginActivity.this);
                                    getPresenter().requestLoginOther(SystemUtil.getUniqueId(LoginActivity.this), "guest");

                                }
                            }
                    ).show();
                }

                break;
            case R.id.btnVideoGuideUserLoginIn:
                if(tvLoginTerms.isChecked()) {
                    login();
                }else{
                    Toast.makeText(this, getString(R.string.content_terms_accept), Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.ivLoginQQSignIn:
                boolean isInstallQQ = false;
                for (String qqPackage : qqPackages) {
                    if(PermissionUtils.isAppInstalled(this,qqPackage)){
                        isInstallQQ = true;
                        break;
                    }
                }
                if(!isInstallQQ){
                    return;
                }
                LoadingDialog.loading(this).setCancelable(true);
                LoginUtil.authorize(LoginUtil.PLATFORM_QQ, requestOtherSignInCallback);
                break;
            case R.id.ivLoginWeChatSignIn:
                LoadingDialog.loading(this).setCancelable(true);
                LoginUtil.authorize(LoginUtil.PLATFORM_WECHAT, requestOtherSignInCallback);
                break;
            case R.id.ivLoginTwitterSignIn:
                LoadingDialog.loading(this).setCancelable(true);
                LoginUtil.authorize(LoginUtil.PLATFORM_TWITTER, requestOtherSignInCallback);
                break;
            case R.id.tvVideoGuideFeedback:
                PageJumpUtil.startFeedbackActivity(this);
                break;

        }
    }

    /**
     * 用户登录
     */
    private void login() {
        String account = etVideoGuideAccount.getText().toString().trim();
        String password = etVideoGuidePassword.getText().toString().trim();
        if (IF.isEmpty(account)) {
            setEditTextErrorTips(etVideoGuideAccount, getString(R.string.content_input_email));
            return;
        }
        if (account.contains("@")) {
            if (!RegexUtil.isEmail(account)) {
                setEditTextErrorTips(etVideoGuideAccount, getString(R.string.content_input_correct_email));
                return;
            }
        } else {
            if (!RegexUtil.isPhoneNumber(account)) {
                setEditTextErrorTips(etVideoGuideAccount, getString(R.string.content_input_correct_phone));
                return;
            }
        }

        if (IF.isEmpty(password)) {
            setEditTextErrorTips(etVideoGuidePassword, getString(R.string.content_input_password));
            return;
        }
        if (password.length() < 6 || password.length() > 14) {
            setEditTextErrorTips(etVideoGuidePassword, getString(R.string.content_password_length));
            return;
        }
        LoadingDialog.loading(this);
        getPresenter().requestLogin(account, password);
    }

    /**
     * 设置编辑框错误提示信息
     *
     * @param et    编辑框控件
     * @param error 错误信息
     */
    private void setEditTextErrorTips(final HintMultiLineEditText et, CharSequence error) {
        et.setFocusable(true);
        et.setFocusableInTouchMode(true);
        et.requestFocus();
        et.setError(error);
    }

    /**
     * 请求第三方登录授权监听
     */
    private LoginUtil.RequestOtherSignInCallback requestOtherSignInCallback = new LoginUtil.RequestOtherSignInCallback() {
        @Override
        public void authorizedSuccess(String platform, String userId) {
            LoadingDialog.dismiss();
            if (platform.equals(LoginUtil.PLATFORM_QQ)) {
                getPresenter().requestLoginOther(userId, "qq");
            } else if (platform.equals(LoginUtil.PLATFORM_WECHAT)) {
                getPresenter().requestLoginOther(userId, "weixin");
            } else if (platform.equals(LoginUtil.PLATFORM_TWITTER)) {
                getPresenter().requestLoginOther(userId, "twitter");
            }
        }

        @Override
        public void authorizedFailed(String errorMsg) {
            LoadingDialog.dismiss();
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionUtils.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    @Override
    public void onLoginFailed(String errMsg) {
        LoadingDialog.dismiss();
        SNToast.toast(errMsg);
    }

    @Override
    public void onLoginSuccess(boolean isNewUser) {
        LoadingDialog.dismiss();
        if (isNewUser) {
            PageJumpUtil.startUserSettingActivity(this, UserSettingActivity.TYPE_SAVE_AND_START_MAIN_ACTIVITY);
        } else {
            PageJumpUtil.startMainActivity(this);
            finish();
        }

    }


}
