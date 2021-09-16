package com.truescend.gofit.pagers.scan.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;

import com.dz.bleota.base.OTA;
import com.sn.app.db.data.user.UserBean;
import com.sn.app.utils.AppUserUtil;
import com.sn.blesdk.ble.DeviceType;
import com.sn.blesdk.entity.SNBLEDevice;
import com.sn.blesdk.net.bean.DeviceInfo;
import com.sn.blesdk.storage.DeviceStorage;
import com.sn.utils.tuple.TupleFour;
import com.truescend.gofit.BuildConfig;
import com.truescend.gofit.R;
import com.truescend.gofit.pagers.base.adapter.BaseRecycleViewAdapter;
import com.truescend.gofit.pagers.base.adapter.ViewHolder;

import java.util.List;
import java.util.Locale;

/**
 * 功能：蓝牙设备适配类
 * Author:Created by 泽鑫 on 2017/12/13 21:51.
 */

public class BLEDevicesAdapter extends BaseRecycleViewAdapter<SNBLEDevice> {


    private final int color_error;
    private final int color_default;
    private final UserBean user;
    private boolean isDev;

    public BLEDevicesAdapter(Context context, List<SNBLEDevice> lists) {
        super(context, lists);
        color_error = 0xffff0000;
        color_default = context.getResources().getColor(R.color.black);
        user = AppUserUtil.getUser();
    }

    public void setDev(boolean dev) {
        isDev = dev;
        notifyDataSetChanged();
    }

    @SuppressLint("StringFormatInvalid")
    @Override
    public void onItemInflate(int position, SNBLEDevice item, ViewHolder.BaseViewHolder viewHolder, View rootView) {

        String mDeviceName = item.mDeviceName;
        if (user != null && DeviceType.isDFUModel(mDeviceName)) {
            TupleFour<String, String, Integer, DeviceInfo> deviceInfoTupleFour = DeviceStorage.findDeviceInfoConnectHistory(item.mDeviceAddress);
            if (deviceInfoTupleFour == null) {
                deviceInfoTupleFour = DeviceStorage.findDeviceInfoConnectHistory(OTA.convertDfuToNormalMacAddress(item.mDeviceAddress));
            }
            //找到了 就补充缺失的信息
            if (deviceInfoTupleFour != null) {
                mDeviceName = deviceInfoTupleFour.getV1();
            }

            viewHolder.setTextView(R.id.tvScanningDevicesName, String.format(context.getString(R.string.content_item_fix_band), mDeviceName)).setTextColor(color_error);
        } else {
            if(BuildConfig.DEBUG&&item.mRssi==0)
            {
                viewHolder.setTextView(R.id.tvScanningDevicesName, mDeviceName).setTextColor(0xFF00C56B);
            }else {
                viewHolder.setTextView(R.id.tvScanningDevicesName, mDeviceName).setTextColor(color_default);
            }
            if (isDev) {
                DeviceInfo deviceInfo = DeviceType.getDeviceInfo(item.mParsedAd.manufacturers);
                if (deviceInfo != null) {
                    String type = deviceInfo.isnRF() ? "nRF" : (deviceInfo.isDialog() ? "Dialog" : (deviceInfo.isSYD8801() ? "SYD" : (deviceInfo.isTi() ? "Ti" : (deviceInfo.isPhy() ? "XW1" : "N/A"))));
                    viewHolder.setTextView(R.id.tvScanningDevicesName, String.format(Locale.ENGLISH, "%s [%04X/%d][%s][%d]", mDeviceName, deviceInfo.getAdv_id(), deviceInfo.getAdv_id(), type, item.mRssi)).setTextColor(color_default);
                } else {
                    viewHolder.setTextView(R.id.tvScanningDevicesName, String.format(Locale.ENGLISH, "%s [unknown][%d]", mDeviceName, item.mRssi)).setTextColor(color_default);
                }
            }
        }


        viewHolder.setTextView(R.id.tvScanningDevicesAddress, item.mDeviceAddress);
        viewHolder.setImageView(R.id.ivScanningDevicesSignal, showSignalIcon(item.mRssi));
    }

    @Override
    public int initLayout(int viewType) {
        return R.layout.list_scanning_devices_item;
    }

    /**
     * 根据信号强度显示对应的图标
     *
     * @param mRssi 信号强度
     * @return resourceId
     */
    private int showSignalIcon(int mRssi) {
        if (mRssi < -90) {
            return R.mipmap.icon_signal_level_1;
        } else if (mRssi < -80) {
            return R.mipmap.icon_signal_level_2;
        } else if (mRssi < -70) {
            return R.mipmap.icon_signal_level_3;
        } else {
            return R.mipmap.icon_signal_level_4;
        }
    }
}
