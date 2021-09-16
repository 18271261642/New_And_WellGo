package com.truescend.gofit.pagers.user.fit.tmall_genie;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sn.utils.SNToast;
import com.truescend.gofit.R;
import com.truescend.gofit.pagers.base.BaseActivity;
import com.truescend.gofit.utils.PageJumpUtil;
import com.truescend.gofit.utils.PermissionUtils;
import com.truescend.gofit.views.LargeImageWebView;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 作者:东芝(2019/6/18).
 * 功能:天猫精灵授权
 */
public class TmallGenieAuthActivity extends BaseActivity<TmallGenieAuthPresenterImpl, ITmallGenieAuthContract.IView> implements ITmallGenieAuthContract.IView {

    @BindView(R.id.largeImageWebView)
    LargeImageWebView largeImageWebView;
    @BindView(R.id.tvUserCode)
    TextView tvUserCode;
    @BindView(R.id.llUserCodeLayout)
    LinearLayout llUserCodeLayout;
    private ClipboardManager clipboardManager;
    private String code;
    private static final String TMALL_GENIE_PACKAGE_NAME = "com.alibaba.ailabs.tg";

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, TmallGenieAuthActivity.class));
    }

    @Override
    protected TmallGenieAuthPresenterImpl initPresenter() {
        return new TmallGenieAuthPresenterImpl(this);
    }

    @Override
    protected int initLayout() {
        return R.layout.activity_tmall_genie_auth;
    }

    @Override
    protected void onCreateActivity(Bundle savedInstanceState) {
        setTitle("如何关联天猫精灵与GetFitPro?");
        largeImageWebView.loadUrl("file:///android_asset/web/h5_tmall_genie_auth_help.html");
        largeImageWebView.setWebViewClient(webViewClient);

        //获取剪贴板管理器：
        clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        getPresenter().requestGetUserCode();
    }

    private WebViewClient webViewClient = new WebViewClient() {
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (llUserCodeLayout != null) {
                llUserCodeLayout.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                onUpdateUserCodeFailed(error.getDescription().toString());
            }
        }
    };

    @Override
    public void onUpdateUserCode(String code) {
        this.code = code;
        tvUserCode.setText(code);
    }

    @Override
    public void onUpdateUserCodeFailed(String errMsg) {
        SNToast.toast(errMsg);
    }


    @OnClick({R.id.tvUserCode, R.id.tvCopy})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tvUserCode:
            case R.id.tvCopy:
                if (TextUtils.isEmpty(code)) {
                    SNToast.toast("错误,用户码为空,请退出重新进入该界面 或重新登录GetFitPro账号");
                    return;
                }
                clipboardManager.setPrimaryClip(ClipData.newPlainText("用户码", code));
                if (PermissionUtils.isAppInstalled(this, TMALL_GENIE_PACKAGE_NAME)) {
                    PageJumpUtil.startToApp(this, TMALL_GENIE_PACKAGE_NAME);
                    SNToast.toast("用户码已复制到粘贴板! 请按教程进行授权", Toast.LENGTH_LONG);
                } else {
                    SNToast.toast("未安装天猫精灵app,请先安装", Toast.LENGTH_LONG);
                    PageJumpUtil.startToMarket(this, TMALL_GENIE_PACKAGE_NAME);
                }
                break;
        }
    }


}
