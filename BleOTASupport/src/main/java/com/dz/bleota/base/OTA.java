package com.dz.bleota.base;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.Locale;

import no.nordicsemi.android.support.v18.scanner.BluetoothLeScannerCompat;
import no.nordicsemi.android.support.v18.scanner.ScanCallback;
import no.nordicsemi.android.support.v18.scanner.ScanResult;

/**
 * 作者:东芝(2018/1/16).
 * 功能:
 */
public abstract class OTA {

    private static final int HANDLER_PREPARE_TIME_OUT = 1;
    private static final int HANDLER_UPDATE_PROGRESS = 2;
    private static final int HANDLER_UPDATE_STATUS = 3;

    /**
     * 准备中
     */
    public static final int STATUS_PREPARE = 3;
    /**
     * 准备超时
     */
    public static final int STATUS_PREPARE_TIME_OUT = -3;

    /**
     * 升级失败
     */
    public static final int STATUS_FAILED = 0;

    /**
     * 升级因为某种正当原因而 取消,此时应该告诉用户 你不需要升级,并退出
     * 目前出现在Dialog芯片 在重复升级同一个升级包时 会出现校验重复错误, 意思是说 你已经升级了 不需要再升级
     */
    public static final int STATUS_CANCEL = 4;

    /**
     * 升级中...
     */
    public static final int STATUS_UPDATING = 1;

    /**
     * 升级成功
     */
    public static final int STATUS_SUCCESSFUL = 2;
    private static final String TAG = "OTA";
    private OnOTAUpdateStatusChangeListener listener;

    private String mOTAFile;
    private WeakReference<Context> context;
    private String mDeviceMac;
    private int status;
    private BluetoothAdapter mBluetoothAdapter;

    protected abstract void connect(BluetoothDevice mDevice);

    protected abstract void close();

    public abstract void destroy();


    /**
     * 开始OTA
     *custom_idcustom_id
     * @param context    非UI上下文
     * @param scan       是否需要扫描 再连接? (SYD和nRF蓝牙模块在手机关闭蓝牙再打开时 直接连接是连不上设备的 都需要这种方式,先扫描让系统刷新缓存 再连接)
     * @param mDeviceMac 设备地址
     * @param file       ota文件
     * @param listener   监听
     */
    public void startOTA(Context context, boolean scan, String mDeviceMac, String file, OnOTAUpdateStatusChangeListener listener) {

        this.context = new WeakReference<>(context);
        this.listener = listener;
        this.mDeviceMac = mDeviceMac;
        if (listener == null) {
            throw new NullPointerException("逗我呢,请必须设置监听!");
        }
        File f = new File(file);
        if (!f.exists() || f.length() == 0) {
            updateStatus(STATUS_FAILED);
            return;
        }
        this.mOTAFile = file;


        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            updateStatus(STATUS_FAILED);
            return;
        }
        if (!mBluetoothAdapter.isEnabled()) {
            updateStatus(STATUS_FAILED);
            return;
        }

        //是否需要先扫描 再连接
        if (scan) {
            Log.i(TAG, "扫描再直接连接");
            BluetoothLeScannerCompat.getScanner().startScan(mLeScanCallback);
        } else {
            Log.i(TAG, "直接连接");
            BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(mDeviceMac);
            connect(device);
        }
        //启动准备超时 准备120秒如果没有开始升级 则认为超时
        mHandler.removeMessages(HANDLER_PREPARE_TIME_OUT);
        mHandler.sendEmptyMessageDelayed(HANDLER_PREPARE_TIME_OUT, 120000);
        updateStatus(STATUS_PREPARE);
    }


    private boolean isDFUModel(String name) {
        if (name == null||name.length()==0) {
            return false;
        }
        return name.toLowerCase().contains("dfu")|| name.toLowerCase().contains("ota");
    }


    private ScanCallback mLeScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            if (result != null) {
                BluetoothDevice device = result.getDevice();
                if (device != null) {
                    String mac = device.getAddress();
                    if (mDeviceMac.equals(mac)&&isDFUModel(device.getName())) {
                        Log.i(TAG, "扫描到了 待升级的设备 mac="+mDeviceMac);
                        BluetoothLeScannerCompat.getScanner().stopScan(mLeScanCallback);
                        connect(device);
                    }else{
                        Log.i(TAG, "mac=" + mac +"!="+mDeviceMac);
                    }
                }
            }

        }
    };


    public void abortOTA() {
        close();
        //移除超时监控
        if (mHandler.hasMessages(HANDLER_PREPARE_TIME_OUT)) {
            mHandler.removeMessages(HANDLER_PREPARE_TIME_OUT);
        }
    }

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLER_PREPARE_TIME_OUT://准备超时
                    updateStatus(STATUS_FAILED);
                    updateStatus(STATUS_PREPARE_TIME_OUT);
                    break;
                case HANDLER_UPDATE_PROGRESS:
                    if (listener != null) {
                        listener.onProgressChanged(msg.arg1, msg.arg2);
                    }
                    break;
                case HANDLER_UPDATE_STATUS:
                    if (listener != null) {
                        listener.onStatusChanged(msg.arg1);
                    }
                    break;
            }
        }
    };

    protected void updateStatus(final int status) {
        this.status = status;
        mHandler.sendMessage(mHandler.obtainMessage(HANDLER_UPDATE_STATUS, status, 0));

    }

    protected void updateProgress(final int cur, final int total) {
        //移除超时监控
        if (mHandler.hasMessages(HANDLER_PREPARE_TIME_OUT)) {
            mHandler.removeMessages(HANDLER_PREPARE_TIME_OUT);
        }
        mHandler.sendMessage(mHandler.obtainMessage(HANDLER_UPDATE_PROGRESS, cur, total));
    }

    protected Context getContext() {
        return context.get();
    }

    protected BluetoothAdapter getBluetoothAdapter() {
        return mBluetoothAdapter;
    }

    protected String getOTAFile() {
        return mOTAFile;
    }

    public int getStatus() {
        return status;
    }

    public interface OnOTAUpdateStatusChangeListener {
        void onStatusChanged(int status);

        void onProgressChanged(int cur, int total);
    }


    /**
     * 转换普通 Mac地址 到DFU Mac 地址
     * 普通  F5 1E 66 E6 5D 5A ,进入dfu时mac+1
     * dfu   F5 1E 66 E6 5D 5B
     *
     * @param mac
     * @return
     */
    public static String convertToDFUMacAddress(String mac) {
        if(mac==null||mac.length()!=17){
            return "";
        }
        String head = mac.substring(0, 15);
        int val = Integer.parseInt(mac.substring(15, 17), 16);
        if (val == 0xFF) {
            val = 0x00;//魏工说的,nrf/phy升级 mac会变,然后有个特殊的情况是 当尾数 是0xFF时 +1 为0x00 并且前一位字节不变
        } else {
            val++;
        }
        return String.format(Locale.ENGLISH, "%s%02X", head, val);
    }




    public static String convertDfuToNormalMacAddress(String mac) {
        if(mac==null||mac.length()!=17){
            return "";
        }
        String head = mac.substring(0, 15);
        int val = Integer.parseInt(mac.substring(15, 17), 16);
        if (val == 0x00) {
            val = 0xFF;//魏工说的,nrf/phy升级 mac会变,然后有个特殊的情况是 当尾数 是0xFF时 +1 为0x00 并且前一位字节不变
        } else {
            val--;
        }
        return String.format(Locale.ENGLISH, "%s%02X", head, val);
    }

}
