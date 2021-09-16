package com.truescend.gofit.pagers.help;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;

import com.truescend.gofit.R;
import com.truescend.gofit.pagers.base.BaseActivity;

import butterknife.BindView;

/**
 * 作者:东芝(2019/01/08).
 * 功能:帮助&隐私政策
 */
public class HelpActivity extends BaseActivity<HelpPresenterImpl, IHelpContract.IView> implements IHelpContract.IView {

    @BindView(R.id.mWebView)
    WebView mWebView;

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, HelpActivity.class));
    }

    @Override
    protected HelpPresenterImpl initPresenter() {
        return new HelpPresenterImpl(this);
    }

    @Override
    protected int initLayout() {
        return R.layout.activity_help;
    }

    @Override
    protected void onCreateActivity(Bundle savedInstanceState) {
        mWebView.loadUrl("file:///android_asset/privacy_policy.html");
    }


}
