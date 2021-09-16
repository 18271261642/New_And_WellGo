package com.truescend.gofit.pagers.base;



import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 作者:东芝(2017/11/17).
 * 功能:
 */

public abstract class BaseFragment<P extends BasePresenter, V> extends Fragment {
    protected P mPresenter;
    protected Context mContext;//activity的上下文对象
    protected Bundle mBundle;
    protected Unbinder unBinder;
    private View inflate;
    protected boolean isVisible;

    public View getView() {
        return inflate;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mBundle != null) {
            outState.putBundle("bundle", mBundle);
        }

    }

    /**
     * 绑定activity
     *
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    /**
     * 运行在onAttach之后
     * 可以接受别人传递过来的参数,实例化对象.
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //获取bundle,并保存起来
        if (savedInstanceState != null) {
            mBundle = savedInstanceState.getBundle("bundle");
        } else {
            mBundle = getArguments() == null ? new Bundle() : getArguments();
        }
        //创建presenter
        mPresenter = initPresenter();
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        updateVisibleStatus();
    }

    private void updateVisibleStatus() {
        if (getUserVisibleHint() && inflate != null) {
            isVisible = true;
            if (mPresenter != null) {
                mPresenter.setUIEnable(true);
                mPresenter.onVisible();
            }
            onVisible();
        } else {
            isVisible = false;
            if (mPresenter != null) {
                mPresenter.setUIEnable(false);
                mPresenter.onInvisible();
            }
            onInvisible();
        }
    }

    /**
     * 运行在onCreate之后
     * 生成view视图
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        inflate = inflater.inflate(initLayout(), null, false);
        unBinder = ButterKnife.bind(this, inflate);
        onCreate();
        updateVisibleStatus();
        return inflate;
    }

    /**
     * 运行在onCreateView之后
     * 加载数据
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //由于fragment生命周期比较复杂,所以Presenter在onCreateView创建视图之后再进行绑定,不然会报空指针异常
        if (mPresenter != null) {
            mPresenter.attachView((V) this);
        }
    }

    protected void onVisible() {

    }

    protected void onInvisible() {

    }

    @Override
    public void onDestroyView() {
        if (mPresenter != null) {
            mPresenter.onDestroy();
            mPresenter.setUIEnable(false);
        }
        super.onDestroyView();
        //s
        unBinder.unbind();
    }

    @Override
    public void onResume() {
        if (mPresenter != null) {
            mPresenter.onResume();
        }
        super.onResume();

        isVisible = true;
        if (mPresenter != null) {
            mPresenter.setUIEnable(true);
            mPresenter.onVisible();
        }
        onVisible();
    }

    @Override
    public void onPause() {
        if (mPresenter != null) {
            mPresenter.onPause();
        }
        super.onPause();

        isVisible = false;
        if (mPresenter != null) {
            mPresenter.setUIEnable(false);
            mPresenter.onInvisible();
        }
        onInvisible();
    }

    @Override
    public void onStop() {
        if (mPresenter != null) {
            mPresenter.onStop();
        }
        super.onStop();
    }

    @Override
    public void onDetach() {
        if (mPresenter != null) {
            mPresenter.onDetach();
        }
        super.onDetach();
    }

    /**
     * 跳转fragment
     *
     * @param tofragment
     */
    public void startFragment(Fragment tofragment) {
        startFragment(tofragment, null);
    }

    /**
     * @param tofragment 跳转的fragment
     * @param tag        fragment的标签
     */
    public void startFragment(Fragment tofragment, String tag) {
        FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
        fragmentTransaction.hide(this).add(android.R.id.content, tofragment, tag);
        fragmentTransaction.addToBackStack(tag);
        fragmentTransaction.commitAllowingStateLoss();
    }

    /**
     * 类似Activity的OnBackgress
     * fragment进行回退
     */
    public void finish() {
        getFragmentManager().popBackStack();
    }

    public P getPresenter() {
        return mPresenter;
    }

    /**
     * 创建prensenter
     *
     * @return <T extends BasePresenter> 必须是BasePresenter的子类
     */
    protected abstract P initPresenter();

    protected abstract int initLayout();

    protected abstract void onCreate();

    @NonNull
    @Override
    public Context getContext() {
        return mContext;
    }

    public Bundle getBundle() {
        return mBundle;
    }

    public BaseFragment getFragment() {
        return this;
    }

}

