package com.truescend.gofit.pagers.device;

import com.google.android.material.tabs.TabLayout;
import com.truescend.gofit.R;
import com.truescend.gofit.pagers.base.BaseFragment;
import com.truescend.gofit.pagers.base.BasePresenter;
import com.truescend.gofit.pagers.base.adapter.BaseFragmentStatePagerAdapter;
import com.truescend.gofit.utils.StatusBarUtil;
import com.truescend.gofit.utils.TabLayoutManager;
import com.truescend.gofit.views.QuickViewPager;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

/**
 * Created by Admin
 * Date 2021/9/7
 */
public class DeviceManagerFragment extends BaseFragment<DeviceManagerPresenterImpl,IDeviceManagerContract.IView> implements IDeviceManagerContract.IView {


    private ViewPager deviceManagerContainer;
    private TabLayout deviceManagerTopTabs;
    public BaseFragmentStatePagerAdapter pagerAdapter;

    private List<Fragment> list ;

    public static DeviceManagerFragment getInstance(){
        return new DeviceManagerFragment();
    }


    @Override
    protected DeviceManagerPresenterImpl initPresenter() {
        return new DeviceManagerPresenterImpl(this);
    }

    @Override
    protected int initLayout() {
        return R.layout.fragment_device_manager_layout;
    }

    @Override
    protected void onCreate() {
        StatusBarUtil.setRootViewFitsSystemWindows(getActivity(), false);
        //设置状态栏白底黑字
        if (!StatusBarUtil.setStatusBarDarkTheme(getActivity(), true)) {
            StatusBarUtil.setStatusBarColor(getActivity(),0x55000000);
        }
        assert getView() != null;
        deviceManagerContainer  = getView().findViewById(R.id.deviceManagerContainer);
        deviceManagerTopTabs = getView().findViewById(R.id.deviceManagerTopTabs);
        String[] tabTitle = {
                "蓝牙设备",
                "Wifi设备"  };
        if(list == null)
            list = new ArrayList<>();
        list.clear();
        list.add(new DeviceFragment());
        list.add(WifiFragment.getInstance());
        deviceManagerContainer.setAdapter(new FragmentPagerAdapter(getParentFragmentManager()) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                return list.get(position);
            }

            @Override
            public int getCount() {
                return list.size();
            }
        });
        deviceManagerTopTabs.setupWithViewPager(deviceManagerContainer);
        deviceManagerTopTabs.getTabAt(0).select();

        for(int i = 0;i<deviceManagerTopTabs.getTabCount();i++){
            deviceManagerTopTabs.getTabAt(i).setText(tabTitle[i]);
        }



        deviceManagerTopTabs.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                deviceManagerContainer.setCurrentItem(tab.getPosition());

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }


    @Override
    public void selectPosition(int position) {

    }
}
