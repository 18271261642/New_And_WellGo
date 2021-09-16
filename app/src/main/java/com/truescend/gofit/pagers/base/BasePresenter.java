package com.truescend.gofit.pagers.base;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

/**
 * 作者:东芝(2017/11/16).
 * 功能:
 */

public abstract class BasePresenter<V>{
    private boolean isUIEnable;

    public void setUIEnable(boolean UIEnable) {
        isUIEnable = UIEnable;
    }

    private Reference<V> mViewRef; //View接口类型的弱引用
    protected void attachView(V view) {
        mViewRef = new WeakReference<V>(view); //建立关联
        onCreate();
    }

    protected V getView() {
        return mViewRef.get(); //获取View
    }

    public boolean isViewAttached() {
        return mViewRef != null && mViewRef.get() != null; //判断是否与View建立关联
    }


    /**
     * onDestroy
     */
    protected void onDestroy(){

    }
    /**
     * onResume
     */
    protected void onResume(){

    }
    /**
     * onCreateActivity
     */
    protected void onCreate(){

    }

    /**
     * onPause
     */
    protected void onPause(){

    }
    /**
     * onStop
     */
    protected void onStop(){

    }

    /**
     * 可见
     */
    protected   void onVisible(){

    }
    /**
     * 不可见
     */
    protected void onInvisible(){

    }
    /**
     * UI是否可见
     */
    protected boolean isUIEnable(){
        return isUIEnable;
    }

    /**
     * 在V销毁的时候调用,解除绑定
     */
    protected void onDetach() {
        if (mViewRef != null) {
            mViewRef.clear(); //解除关联
            mViewRef = null;
        }
    }

}