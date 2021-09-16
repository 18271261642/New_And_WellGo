package com.truescend.gofit.pagers.test;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.truescend.gofit.R;
import com.truescend.gofit.pagers.base.BaseActivity;

/**
 * 作者:东芝(2017/11/16).
 * 功能:供复制用
 */
public class TestActivity extends BaseActivity<TestPresenterImpl, ITestContract.IView> implements ITestContract.IView {

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, TestActivity.class));
    }

    @Override
    protected TestPresenterImpl initPresenter() {
        return new TestPresenterImpl(this);
    }

    @Override
    protected int initLayout() {
        return R.layout.activity_test;
    }

    @Override
    protected void onCreateActivity(Bundle savedInstanceState) {

    }


}
