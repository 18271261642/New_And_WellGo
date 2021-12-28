package com.truescend.gofit.utils;



import com.truescend.gofit.pagers.base.BaseFragment;
import com.truescend.gofit.pagers.device.DeviceManagerFragment;
import com.truescend.gofit.pagers.track.TrackFragment;
import com.truescend.gofit.pagers.home.HomeFragment;
import com.truescend.gofit.pagers.device.DeviceFragment;
import com.truescend.gofit.pagers.user.UserFragment;

import androidx.collection.SparseArrayCompat;


/**
 * 生产Fragment的工厂类
 */
public class FragmentFactory {

    private static FragmentFactory fragmentFactory;

    public SparseArrayCompat<BaseFragment> mCaches = new SparseArrayCompat<BaseFragment>();


    public final static int HOME = 0;//首页
    public final static int TRACK = 1;//跑道
    public final static int DEVICE = 2;//设备
    public final static int SETTING = 3;//设置

    public static FragmentFactory getFragmentFactoryInstance() {
        if (null == fragmentFactory) {
            fragmentFactory = new FragmentFactory();
        }
        return fragmentFactory;
    }


    public BaseFragment getFragment(int position) {

        // 去缓存中取
        BaseFragment fragment = mCaches.get(position);
        if (fragment != null) {
            // 缓存中有
            return fragment;
        }

        switch (position) {
            case HOME:
                fragment = new HomeFragment();
                break;
            case TRACK:
                fragment = new TrackFragment();
                break;
            case DEVICE:
                fragment = new DeviceFragment();
//                fragment = DeviceManagerFragment.getInstance();
                break;
            case SETTING:
                fragment = new UserFragment();
                break;
             default:
                break;
        }

        // 缓存起来
        //LogUtil.show("为" + position + "缓存");
        mCaches.put(position, fragment);
        return fragment;
    }

    /**
     * 清除缓存
     */
    public void destroyFragmentView() {
        if (null != fragmentFactory) {
            fragmentFactory.mCaches.clear();
            fragmentFactory.mCaches = null;
            fragmentFactory = null;
        }
    }

    /**
     * 清除缓存
     */
    public void destroyFragmentView(int index) {
        if (null != fragmentFactory) {
            fragmentFactory.mCaches.remove(index);
        }
    }


}
