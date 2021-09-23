package com.truescend.gofit.pagers.start.splash;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.sn.app.BuildConfig;
import com.sn.app.net.AppNetReq;
import com.sn.app.net.callback.OnResponseListener;
import com.sn.app.net.data.app.bean.UserMessageBean;
import com.sn.app.net.data.base.DefResponseBean;
import com.sn.app.storage.UserStorage;
import com.sn.app.utils.AppUserUtil;
import com.sn.utils.IF;
import com.sn.utils.SystemUtil;
import com.sn.utils.task.SNAsyncTask;
import com.sn.utils.task.SNVTaskCallBack;
import com.truescend.gofit.R;
import com.truescend.gofit.utils.Constant;
import com.truescend.gofit.utils.PageJumpUtil;
import com.truescend.gofit.utils.PermissionUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

/**
 * 作者:东芝(2018/01/25).
 * 功能:启动页  没啥逻辑,就不写MVP了
 */
public class SplashActivity extends Activity {

    public static int WAIT_TIME = 1000;
    private Call<UserMessageBean> queryUser;
    private ImageView ivWindowBackground;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        hideNavigation();

        //按下home键将app设置为后台,修复华为每次打开都显示启动页的bug
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            finish();
            return;
        }
        if (!isTaskRoot()) {
            finish();
            return;
        }
        ivWindowBackground = (ImageView) findViewById(R.id.ivWindowBackground);
        final RequestBuilder<Drawable> builder = Glide.with(this).asDrawable()
                .load(R.drawable.icon_splash_icon);
        builder.into(new SimpleTarget<Drawable>() {
            @Override
            public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                if (!isFinishing()) {
                    ivWindowBackground.setImageDrawable(resource);
                    //把透明的背景设置回来
                    ivWindowBackground.setBackgroundColor(getResources().getColor(R.color.black));
                    next1();
                }
            }


            /**
             * 如果加载失败 则分辨率修改为低分辨率 再尝试显示
             * @param errorDrawable
             */
            @Override
            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                Glide.with(SplashActivity.this).asDrawable()
                        .load(R.drawable.icon_splash_icon)
                        .apply(RequestOptions.overrideOf(360, 640))
                        .into(new SimpleTarget<Drawable>() {
                            @Override
                            public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                                if (!isFinishing()) {
                                    ivWindowBackground.setImageDrawable(resource);
                                    next1();
                                }
                            }
                        });
            }
        });


    }

    private void hideNavigation() {
        try {
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        } catch (Exception ignored) {
        }
    }


    private void next1() {
        List<String> permissions = new ArrayList<>();
        permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.CALL_PHONE);
        if (!SystemUtil.isMIUI12()) {
            permissions.add(Manifest.permission.READ_PHONE_STATE);
        }

        //9.0来电号码和挂电话需要正式申请权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            permissions.add(Manifest.permission.ANSWER_PHONE_CALLS);
        }
        //Google Play版本阉割功能
        if (!com.truescend.gofit.BuildConfig.isGooglePlayVersion) {
            permissions.add(Manifest.permission.READ_CONTACTS);
            permissions.add(Manifest.permission.RECEIVE_SMS);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                permissions.add(Manifest.permission.READ_CALL_LOG);
            }
        }
        PermissionUtils.requestPermissions(this, permissions, new PermissionUtils.OnPermissionGrantedListener() {
            @Override
            public void onGranted() {
                next2();
            }

            @Override
            public void onDenied() {
                finish();
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionUtils.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    private void next2() {
        //异步任务
        SNAsyncTask.execute(new SNVTaskCallBack() {
            @Override
            public void run() throws Throwable {

                if (Constant.isEmulator()) {
                    WAIT_TIME = 0;//调试模式更快点
                }
                //启动页停留一会儿
               // Thread.sleep(WAIT_TIME);


                if (IF.isEmpty(UserStorage.getAccessToken()) || UserStorage.getUserId() == -1 || UserStorage.isFirst()) {
                    //如果没登录,则重登录,并进入主页.  重登录那边已经有更新用户信息了  所以这里不需要实现
                    toReLoginPager();
                } else {
                    //如果是二次打开(已登录)  就更新下用户信息 并进入主页
                    queryUser = AppNetReq.getApi().queryUser(SystemUtil.getUniqueId(getApplicationContext()));
                    queryUser.enqueue(new OnResponseListener<UserMessageBean>() {
                        @Override
                        public void onResponse(UserMessageBean body) throws Throwable {
                            final UserMessageBean.DataBean data = body.getData();
                            if (data.getApp_id() == 0) {
                                AppNetReq.getApi().setAppId().enqueue(new OnResponseListener<DefResponseBean>() {
                                    @Override
                                    public void onResponse(DefResponseBean body) throws Throwable {
                                        if (body.isSuccessful()) {
                                            data.setApp_id(BuildConfig.APP_ID);
                                        }
                                        //用户数据初始化
                                        initUserData(data);
                                        toMainPager();
                                    }

                                    @Override
                                    public void onFailure(int ret, String msg) {
                                        //用户数据初始化
                                        initUserData(data);
                                        toMainPager();
                                    }
                                });
                            } else {
                                //用户数据初始化
                                initUserData(data);
                                toMainPager();
                            }

                        }

                        @Override
                        public void onFailure(int ret, String msg) {
                            toReLoginPager();
                        }
                    });
                }

            }
        });
    }

    private void initUserData(UserMessageBean.DataBean data) {
        try {
            //用户数据初始化
            AppUserUtil.initialize(data);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 到主页界面
     */
    private void toMainPager() {
        if (!isFinished()) {
            PageJumpUtil.startMainActivity(SplashActivity.this);
            finish();
        }
    }

    /**
     * 到重新登录界面
     */
    private void toReLoginPager() {
        if (!isFinished()) {
            PageJumpUtil.startLoginActivity(SplashActivity.this, false);
            finish();
        }
    }


    /**
     * 界面已销毁
     *
     * @return
     */
    public boolean isFinished() {
        return isDestroyed() || isFinishing();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (queryUser != null) {
            queryUser.cancel();
        }
        try {
            if (!isFinished()) {
                Glide.with(this).onDestroy();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
