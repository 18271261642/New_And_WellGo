package com.dz.blesdk.ble;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.dz.blesdk.interfaces.BluetoothStatusListener;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 蓝牙控制类,控制蓝牙开关和监听回调
 *
 * @author XianDongZhi[T-EXCEPTION]
 */
public class BleControl {


    protected static final int REQUEST_ENABLE_BT = 23;
    private static List<BluetoothStatusListener> callback = new CopyOnWriteArrayList<>();

    protected BleControl() {
    }

    public static void init(Context context){
        context.registerReceiver(bluetoothState, new IntentFilter(
                BluetoothAdapter.ACTION_STATE_CHANGED));

    }

    /**
     * 开启蓝牙(系统弹窗)
     *
     * @param activity
     */
    public static void setBluetoothOpen(Activity activity) {
        Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        activity.startActivityForResult(enableIntent, REQUEST_ENABLE_BT);

    }

    /**
     * 不建议用 小米等手机会自动倒计时 自动拒绝
     * 静默开启蓝牙
     * <p>
     * 用enable()方法来开启，无需询问用户(实惠无声息的开启蓝牙设备),这时就需要用到android.permission.
     * BLUETOOTH_ADMIN权限。
     *
     * @param activity
     */
    @Deprecated
    public static void setBluetoothOpen(Activity activity, boolean open) {
        if (open) {
            DZBLESDK.getBluetoothAdapter().enable();
        } else {
            DZBLESDK.getBluetoothAdapter().disable();
        }

    }

    public static boolean isBluetoothEnable() {
        BluetoothAdapter bluetoothAdapter = DZBLESDK.getBluetoothAdapter();
        return bluetoothAdapter != null && bluetoothAdapter.isEnabled();
    }


    /**
     * 静默关闭蓝牙
     */
    public static void setBluetoothClose() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter
                .getDefaultAdapter();
        if (mBluetoothAdapter != null)
            mBluetoothAdapter.disable();
    }

    /**
     * 增加一个回调
     *
     * @param bleOpenListener
     */
    public static void addBluetoothStatusListener(BluetoothStatusListener bleOpenListener) {
        if (bleOpenListener != null) {
            if (!callback.contains(bleOpenListener)) {
                callback.add(bleOpenListener);
            }
        }
    }

    /**
     * 移除一个回调
     *
     * @param bleOpenListener
     */
    public static void removeBluetoothStatusListener(BluetoothStatusListener bleOpenListener) {
        if (bleOpenListener != null) {
            if (callback.contains(bleOpenListener)) {
                callback.remove(bleOpenListener);
            }
        }
    }

    /**
     * 清除监听
     */
    public static void clearBluetoothStatusListener() {
        callback.clear();
    }


    /**
     * 释放资源,所有回调和监听都会清除
     *
     * @param context
     */
    public static void close(Context context) {
        context.unregisterReceiver(bluetoothState);
        clearBluetoothStatusListener();
    }

    /**
     * 想监听是否打开蓝牙成功 必须设置监听这个
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public static void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                for (BluetoothStatusListener bluetoothStatusListener : callback) {
                    bluetoothStatusListener.onBluetoothStatusChange(BluetoothAdapter.STATE_ON);
                }
            }

            if (resultCode == Activity.RESULT_CANCELED) {
                for (BluetoothStatusListener bluetoothStatusListener : callback) {
                    bluetoothStatusListener.onBluetoothStatusChange(BluetoothAdapter.STATE_OFF);
                }
            }

        }
    }
    private static BroadcastReceiver bluetoothState = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (callback == null || callback.size() == 0) {
                return;
            }
            String stateExtra = BluetoothAdapter.EXTRA_STATE;
            int state = intent.getIntExtra(stateExtra, -1);

            for (BluetoothStatusListener bluetoothStatusListener : callback) {
                bluetoothStatusListener.onBluetoothStatusChange(state);
            }
        }
    };

}
