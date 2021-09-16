package com.truescend.gofit.pagers.health;

import android.os.Bundle;

import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.tabs.TabLayout;
import com.sn.blesdk.ble.DeviceType;
import com.sn.blesdk.ble.SNBLEHelper;
import com.sn.blesdk.net.bean.DeviceInfo;
import com.sn.utils.SNToast;
import com.sn.utils.view.FastClickChecker;
import com.truescend.gofit.R;
import com.truescend.gofit.pagers.base.BaseActivity;
import com.truescend.gofit.utils.TabLayoutUtils;
import com.truescend.gofit.views.ScrollingImageView;
import com.truescend.gofit.views.TitleLayout;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 作者:东芝(2018/1/31).
 * 功能:我的体检
 */
public class HealthCheckActivity extends BaseActivity<HealthCheckPresenterImpl, IHealthCheckContract.IView> implements IHealthCheckContract.IView {


    /**
     * 没选中的 界面
     */
    private static final int DISPLAYED_PAGE_UNSELECTED = 0;
    /**
     * 选择的界面
     */
    private static final int DISPLAYED_PAGE_SELECTED = 1;

    @BindView(R.id.tabHealthCheckType)
    TabLayout tabHealthCheckType;
    @BindView(R.id.sivHealthCheckScrollAnim)
    ScrollingImageView sivHealthCheckScrollAnim;
    @BindView(R.id.vsHealthCheckStatusSwitcher)
    ViewSwitcher vsHealthCheckStatusSwitcher;
    @BindView(R.id.tvHealthCheckValue)
    TextView tvHealthCheckValue;
    @BindView(R.id.tvHealthCheckUnit)
    TextView tvHealthCheckUnit;
    @BindView(R.id.ivHealthCheckRunning)
    ImageView ivHealthCheckRunning;

    private SparseArray<Object[]> item = null;

    @Override
    protected HealthCheckPresenterImpl initPresenter() {
        return new HealthCheckPresenterImpl(this);
    }

    @Override
    protected int initLayout() {
        return R.layout.activity_health_check;
    }


    @Override
    protected void onCreateTitle(TitleLayout titleLayout) {
        super.onCreateTitle(titleLayout);
        titleLayout.setTitle(R.string.check_self);
    }


    @Override
    protected void onCreateActivity(Bundle savedInstanceState) {
        initTabItemData();
        initTabLayout();
    }

    private void initTabItemData() {
        item = new SparseArray<>();
        //加载第一张gif默认图
        Glide.with(HealthCheckActivity.this)
                .load(R.mipmap.icon_health_check_heart)
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.RESOURCE))
                .into(ivHealthCheckRunning);
        //根据设备类型.移除不显示的tab
        DeviceInfo deviceInfo = DeviceType.getCurrentDeviceInfo();
        if (deviceInfo != null) {
            if (deviceInfo.isSupportHeartRate()) {
                item.put(HealthCheckPresenterImpl.TYPE_HEART_RATE, new Object[]{R.string.heart_rate, getString(R.string.unit_heart), R.mipmap.icon_health_check_heart, R.mipmap.icon_heart_anim});
            }
            if (deviceInfo.isSupportBloodPressure()) {
                item.put(HealthCheckPresenterImpl.TYPE_BLOOD_PRESSURE, new Object[]{R.string.blood_pressure, getString(R.string.unit_pressure), R.mipmap.icon_health_check_blood_pressure, R.mipmap.icon_blood_pressure_anim});
            }
            if (deviceInfo.isSupportBloodOxygen()) {
                item.put(HealthCheckPresenterImpl.TYPE_BLOOD_OXYGEN, new Object[]{R.string.blood_oxygen, "%", R.mipmap.icon_health_check_blood_oxygen, R.mipmap.icon_blood_oxygen_anim});
            }
        }
    }

    private void initTabLayout() {
        for (int i = 0; i < item.size(); i++) {
            tabHealthCheckType.addTab(tabHealthCheckType.newTab().setText((Integer) item.valueAt(i)[0]));
        }
        tabHealthCheckType.addOnTabSelectedListener(onTabSelectedListener);
    }


    @OnClick({R.id.vsHealthCheckStatusSwitcher})
    public void onClick(View view) {
        if (!SNBLEHelper.isConnected()) {
            SNToast.toast(R.string.toast_band_is_disconnect);
            return;
        }
        switch (view.getId()) {
            case R.id.vsHealthCheckStatusSwitcher:
                int selectedTabPosition = tabHealthCheckType.getSelectedTabPosition();
                if (selectedTabPosition==-1) {
                    return;
                }
                switch (vsHealthCheckStatusSwitcher.getCurrentView().getId()) {
                    case R.id.ivHealthCheckNormal:
                        tvHealthCheckValue.setText("--");
                        getPresenter().requestStartHealthCheck(item.keyAt(selectedTabPosition), true, false);
                        break;
                    case R.id.ivHealthCheckRunning:
                        getPresenter().requestStartHealthCheck(item.keyAt(selectedTabPosition), false, false);
                        break;
                }

                break;
        }
    }

    @Override
    public void onUpdateHealthCheckHeartRate(String heartRate) {
        tvHealthCheckValue.setText(heartRate);
    }

    @Override
    public void onUpdateHealthCheckBloodOxygen(String bloodOxygen) {
        tvHealthCheckValue.setText(bloodOxygen);
    }

    @Override
    public void onUpdateHealthCheckBloodPressure(String bloodPressure) {
        tvHealthCheckValue.setText(bloodPressure);
    }

    @Override
    public void onHealthCheckStopped(boolean hasError) {
        if(isFinished()){
            return;
        }
        if (vsHealthCheckStatusSwitcher.getDisplayedChild() == DISPLAYED_PAGE_UNSELECTED) {
            return;
        }
        vsHealthCheckStatusSwitcher.setDisplayedChild(DISPLAYED_PAGE_UNSELECTED);
        sivHealthCheckScrollAnim.stop();
        TabLayoutUtils.setEnable(tabHealthCheckType, true);
        if (hasError) {
            SNToast.toast(getString(R.string.content_stop_check));
        } else {
            SNToast.toast(getString(R.string.content_check_complete));
        }
    }


    public void onHealthCheckStarted() {
        if (vsHealthCheckStatusSwitcher.getDisplayedChild() == DISPLAYED_PAGE_SELECTED) {
            return;
        }
        //切换View视图
        vsHealthCheckStatusSwitcher.setDisplayedChild(DISPLAYED_PAGE_SELECTED);
        //开启动画
        sivHealthCheckScrollAnim.start();
        //设置tab不可点击
        TabLayoutUtils.setEnable(tabHealthCheckType, false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (tabHealthCheckType != null) {
            tabHealthCheckType.removeOnTabSelectedListener(onTabSelectedListener);
        }
    }

    private TabLayout.OnTabSelectedListener onTabSelectedListener = new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            //切换TAb时切换界面 图片/单位/动画图片颜色
            int position = tab.getPosition();
            Object[] items = item.valueAt(position);
            tvHealthCheckUnit.setText((String) items[1]);

            Glide.with(HealthCheckActivity.this)
                    .load((Integer) items[2])
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.RESOURCE))
                    .into(ivHealthCheckRunning);

//            ivHealthCheckRunning.setImageResource((Integer) items[2]);


            sivHealthCheckScrollAnim.setImageResources((Integer) items[3]);
            getPresenter().requestGetHealthCheckLastValue(position);
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {

        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    };

    /**
     * 是否正在体检
     *
     * @return
     */
    private boolean isChecking() {
        return vsHealthCheckStatusSwitcher.getCurrentView().getId() == R.id.ivHealthCheckRunning;
    }

    @Override
    public void onBackPressed() {
        if (isChecking() && SNBLEHelper.isConnected()) {
            if (FastClickChecker.isFast()) {
                return;
            }
            SNToast.toast(getString(R.string.content_checking));
            return;
        }
        super.onBackPressed();
    }


}
