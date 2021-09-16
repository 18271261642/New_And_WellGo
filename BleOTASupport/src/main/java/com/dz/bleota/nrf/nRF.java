package com.dz.bleota.nrf;

import android.app.ActivityManager;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;

import com.dz.bleota.base.OTA;
import com.dz.bleota.nrf.service.DfuService;

import no.nordicsemi.android.dfu.DfuProgressListener;
import no.nordicsemi.android.dfu.DfuServiceController;
import no.nordicsemi.android.dfu.DfuServiceInitiator;
import no.nordicsemi.android.dfu.DfuServiceListenerHelper;

/**
 * 作者:东芝(2018/3/7).
 * 功能:nRF51822 DFU升级
 */

public class nRF extends OTA {
    private static final String TAG = "nRF";
    private DfuServiceController controller;
    private Class<DfuService> dfuServiceClass = DfuService.class;


    /**
     * 注意 nRF 有点特殊, 连接之前需要发送一个DFU升级的蓝牙命令, 这里我这个框架就不写了
     *
     * @param context    非UI上下文
     * @param scan       是否需要扫描 再连接? (SYD和nRF蓝牙模块在手机关闭蓝牙再打开时 直接连接是连不上设备的 都需要这种方式,先扫描让系统刷新缓存 再连接)
     * @param mDeviceMac 设备地址
     * @param file       ota文件
     * @param listener   监听
     */
    @Override
    public void startOTA(Context context, boolean scan, String mDeviceMac, String file, OnOTAUpdateStatusChangeListener listener) {
        super.startOTA(context, scan, mDeviceMac, file, listener);
    }



    @Override
    protected void connect(BluetoothDevice mDevice) {
        Log. i(TAG,"连接开始...");
        //注册空中升级的监听
        Context context = getContext();
        if(context==null)return;


        DfuServiceListenerHelper.registerProgressListener(context, dfuProgressListener);

        controller = new DfuServiceInitiator(mDevice.getAddress())
                .setDisableNotification(true)
                .setForeground(false)
                .setPacketsReceiptNotificationsEnabled(true)
                .setZip(getOTAFile())
                .start(context, dfuServiceClass);
    }

    private boolean isDfuServiceRunning() {
        ActivityManager manager = (ActivityManager) getContext().getSystemService(Context.ACTIVITY_SERVICE);
        if (manager != null) {
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (dfuServiceClass.getName().equals(service.service.getClassName())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected void close() {

        if (controller != null) {
            try {
                controller.abort();
            } catch (Exception ignored) {
            }
        }

    }

    @Override
    public void destroy() {
        close();
        try {
            Context context = getContext();
            if(context==null)return;
            DfuServiceListenerHelper.unregisterProgressListener(context, dfuProgressListener);//取消监听升级回调
        } catch (Exception ignored) {
        }
    }


    /**
     * 空中升级回调事件监听
     */
    private final DfuProgressListener dfuProgressListener = new DfuProgressListener() {
        @Override
        public void onDeviceConnecting(String deviceAddress) {
        }

        //设备开始连接
        @Override
        public void onDeviceConnected(String deviceAddress) {
        }

        //升级准备开始的时候调用
        @Override
        public void onDfuProcessStarting(String deviceAddress) {
        }

        //设备开始升级
        @Override
        public void onDfuProcessStarted(String deviceAddress) {
            updateStatus(STATUS_UPDATING);
        }

        @Override
        public void onEnablingDfuMode(String deviceAddress) {
        }

        //升级过程中的回调
        @Override
        public void onProgressChanged(String deviceAddress, int percent, float speed, float avgSpeed, int currentPart, int partsTotal) {
            updateProgress(percent, 100);
        }

        //固件验证
        @Override
        public void onFirmwareValidating(String deviceAddress) {
        }

        //设备正在断开
        @Override
        public void onDeviceDisconnecting(String deviceAddress) {
        }

        //设备已经断开
        @Override
        public void onDeviceDisconnected(String deviceAddress) {
//            if (getStatus() == STATUS_FAILED) {
//                return;
//            }
//            //如果断开的原因不是成功, 那么告诉调用者 升级错误,失败
//            if (getStatus() != STATUS_SUCCESSFUL) {
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            Thread.sleep(1000);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                        updateStatus(STATUS_FAILED);
//                    }
//                }).start();
//            }
        }

        //升级完成
        @Override
        public void onDfuCompleted(String deviceAddress) {
            updateStatus(STATUS_SUCCESSFUL);
        }

        @Override
        public void onDfuAborted(String deviceAddress) {
            if (getStatus() == STATUS_FAILED) {
                return;
            }
            updateStatus(STATUS_FAILED);
        }

        //升级失败
        @Override
        public void onError(String deviceAddress, int error, int errorType, String message) {
            if (getStatus() == STATUS_FAILED) {
                return;
            }
            updateStatus(STATUS_FAILED);

        }
    };
}
