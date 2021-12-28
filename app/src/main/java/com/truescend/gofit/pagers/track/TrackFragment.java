package com.truescend.gofit.pagers.track;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.truescend.gofit.R;
import com.truescend.gofit.pagers.base.BaseFragment;
import com.truescend.gofit.pagers.track.adapter.PathMapAdapter;
import com.truescend.gofit.pagers.track.bean.PathMapItem;
import com.truescend.gofit.utils.MapType;
import com.truescend.gofit.utils.PageJumpUtil;
import com.truescend.gofit.utils.RecycleViewUtil;
import com.truescend.gofit.utils.UIRefresh;
import com.truescend.gofit.views.BannerView;
import com.truescend.gofit.views.CircleRippleButton;
import com.truescend.gofit.views.EmptyRecyclerView;
import com.truescend.gofit.views.LocalPermissDescView;

import java.util.ArrayList;
import java.util.List;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * 作者:东芝(2018/3/1).
 * 功能:运动轨迹
 */
public class TrackFragment extends BaseFragment<TrackPresenterImpl, ITrackContract.IView> implements ITrackContract.IView, AdapterView.OnItemClickListener {
    @BindView(R.id.rvTrackPathMapView)
    EmptyRecyclerView rvTrackPathMapView;
    @BindView(R.id.vsEmptyContent)
    View ivEmptyContent;
    @BindView(R.id.crbGo)
    CircleRippleButton crbGo;
    @BindView(R.id.mBannerView)
    BannerView mBannerView;
    @BindView(R.id.ivDeviceSetting)
    ImageView ivDeviceSetting;

    private PathMapAdapter mPathMapAdapter;

    private LocalPermissDescView localPermissDescView;

    @Override
    public TrackPresenterImpl initPresenter() {
        return new TrackPresenterImpl(this);
    }

    @Override
    protected int initLayout() {
        return R.layout.fragment_track;
    }

    @Override
    protected void onCreate() {
        initItems();
        initBanner();
        getPresenter().requestLoadTrackItemData();
    }

    private void initBanner() {

        List<Integer> images = new ArrayList<>();
        images.add(R.drawable.icon_ad_bg1);
        images.add(R.drawable.icon_ad_bg2);
        images.add(R.drawable.icon_ad_bg3);
        mBannerView.setImages(images);
        mBannerView.stopAutoPlay();
    }

    @Override
    protected void onVisible() {
        super.onVisible();
        if (UIRefresh.isIsNeedRefreshTrackItem()) {
            UIRefresh.setIsNeedRefreshTrackItem(false);
            getPresenter().requestLoadTrackItemData();
        }
        if (mBannerView != null) {
            mBannerView.startAutoPlay();
        }
    }

    @Override
    protected void onInvisible() {
        super.onInvisible();
        if (mBannerView != null) {
            mBannerView.stopAutoPlay();
        }
    }

    @Override
    public void onUpdateTrackItemData(List<PathMapItem> list) {
        mPathMapAdapter.setList(list);
        rvTrackPathMapView.smoothScrollToPosition(0);
    }

    /**
     * 初始化Item
     */
    private void initItems() {
        mPathMapAdapter = new PathMapAdapter(getActivity());
        RecycleViewUtil.setAdapter(rvTrackPathMapView, mPathMapAdapter);
        rvTrackPathMapView.setEmptyView(ivEmptyContent);
        mPathMapAdapter.setOnItemClickListener(this);
    }

    @OnClick({R.id.crbGo,R.id.ivDeviceSetting})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.crbGo:
                //谷歌能访问,但用户卸载了谷歌服务
                MapType.TYPE type = MapType.getSmartMapType();
                if (type == MapType.TYPE.GOOGLE_MAP && !MapType.isGooglePlayServicesAvailable(getActivity())) {
                    Dialog dialog = MapType.getGooglePlayServicesErrorDialog(getActivity());
                    dialog.setCancelable(false);
                    dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            finish();
                        }
                    });
                    dialog.show();
                    return;
                }


                showLocalRequestDesc(type);


                break;
            case R.id.ivDeviceSetting:
                PageJumpUtil.startRunSettingActivity(getActivity());
                break;
        }


    }


    private void showLocalRequestDesc(MapType.TYPE type){
        if(localPermissDescView == null)
            localPermissDescView = new LocalPermissDescView(getActivity());
        localPermissDescView.show();
        localPermissDescView.setLocalPermissionListener(new LocalPermissDescView.LocalPermissionListener() {
            @Override
            public void allowPermiss() {
                localPermissDescView.dismiss();
                boolean isLocal = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
                if(isLocal){
                    PageJumpUtil.startRunningActivity(TrackFragment.this, type);
                }else{
                    ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.ACCESS_FINE_LOCATION},0x00);
                }


            }
        });
    }







    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //如果有需要刷新的数据 那就刷新
        if (requestCode == PageJumpUtil.REQUEST_CODE_RESULT && resultCode == Activity.RESULT_OK) {
            getPresenter().requestLoadTrackItemData();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        PageJumpUtil.startTrackRecordActivity(getActivity(), mPathMapAdapter.getItem(position));
    }
}
