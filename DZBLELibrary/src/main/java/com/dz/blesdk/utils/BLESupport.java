package com.dz.blesdk.utils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.content.Context;
import android.os.Build;
import android.os.IBinder;
import androidx.annotation.RequiresApi;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 作者:东芝(2018/4/10).
 * 功能:BLE兼容类
 */

public class BLESupport {


    //来自BluetoothDevice 类的私有常量
    public static final int CONNECTION_STATE_DISCONNECTED = 0;
    public static final int CONNECTION_STATE_CONNECTED = 1;
    public static final int CONNECTION_STATE_ENCRYPTED_BREDR = 2;
    public static final int CONNECTION_STATE_ENCRYPTED_LE = 4;
    public static final int CONNECTION_STATE_UN_SUPPORT = -1;

    /**
     * 刷新蓝牙app的状态
     * (某些手机用久了会出现扫描不到任何设备的bug,
     * 此时是因为手机误认为本app不是ble类app,
     * 出现该问题时于是通过查看系统源码找到isBleAppPresent调用居然返回false!,
     * 换了一台能正常使用的手机 调用该方法 返回true! 因此证实了这个问题,
     * 然后发现系统有私有的updateBleAppCount 可以刷新ble类app的状态,反射调用之...
     * 因此解决了 [偶尔ble设备扫描不出来]的bug)
     * 该方法绝对全网独一无二 哈哈哈哈
     * 参考 IBluetoothManager.aidl 系统源码
     *
     * @param context
     * @param packageName
     */
    public static void refreshBleAppFromSystem(Context context, String packageName) {

        //6.0以上才有该功能,不是6.0以上就算了
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }

        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter == null) {
            return;
        }
        if (!adapter.isEnabled()) {
            return;
        }
        try {
            Object mIBluetoothManager = getIBluetoothManager(adapter);
            Method isBleAppPresentM = mIBluetoothManager.getClass().getDeclaredMethod("isBleAppPresent");
            isBleAppPresentM.setAccessible(true);
            boolean isBleAppPresent = (Boolean) isBleAppPresentM.invoke(mIBluetoothManager);
            if (isBleAppPresent) {
                return;
            }
            Field mIBinder = BluetoothAdapter.class.getDeclaredField("mToken");
            mIBinder.setAccessible(true);
            Object mToken = mIBinder.get(adapter);

            //刷新偶尔系统无故把app视为非 BLE应用 的错误标识 导致无法扫描设备
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                //8.0+ (部分手机是7.1.2 也是如此)
                Method updateBleAppCount = mIBluetoothManager.getClass().getDeclaredMethod("updateBleAppCount", IBinder.class, boolean.class, String.class);
                updateBleAppCount.setAccessible(true);
                //关一下 再开
                updateBleAppCount.invoke(mIBluetoothManager, mToken, false, packageName);
                updateBleAppCount.invoke(mIBluetoothManager, mToken, true, packageName);

            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                try {
                    //6.0~7.1.1

                    Method updateBleAppCount = mIBluetoothManager.getClass().getDeclaredMethod("updateBleAppCount", IBinder.class, boolean.class);
                    updateBleAppCount.setAccessible(true);
                    //关一下 再开
                    updateBleAppCount.invoke(mIBluetoothManager, mToken, false);
                    updateBleAppCount.invoke(mIBluetoothManager, mToken, true);
                } catch (NoSuchMethodException e) {
                    //8.0+ (部分手机是7.1.2 也是如此)
                    try {
                        Method updateBleAppCount = mIBluetoothManager.getClass().getDeclaredMethod("updateBleAppCount", IBinder.class, boolean.class, String.class);
                        updateBleAppCount.setAccessible(true);
                        //关一下 再开
                        updateBleAppCount.invoke(mIBluetoothManager, mToken, false, packageName);
                        updateBleAppCount.invoke(mIBluetoothManager, mToken, true, packageName);
                    } catch (NoSuchMethodException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 取得该mac地址在系统内部的连接状态
     * 有些手机连接蓝牙设备,操作: 断开,连接,断开,连接,断开...  反复重试,导致最终系统残留了该蓝牙设备的引用 无法清除,
     * 即使把你写的蓝牙app卸载掉了  蓝牙仍然没有断开 , 甚至有些手机把蓝牙关了 也没断开设备, 最终把手机开启飞行模式 才真正完全断开...
     * 现在这个方法则是获取系统内部是否有连接到该mac地址
     * 这是查看源码研究得来的 代码全网独一无二
     * 关键源码
     * IBluetooth.aidl
     *
     * @param mac
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("PrivateApi")
    public static int getInternalConnectionState(String mac) {
        if (Build.VERSION.SDK_INT >= 28/*Build.VERSION_CODES.P*/) {
            return CONNECTION_STATE_UN_SUPPORT;
        }

        //该功能是在21 (5.1.0)以上才支持,但有些手机5.1 也不支持, 干脆全部5.1以下的都不支持
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return CONNECTION_STATE_UN_SUPPORT;
        }
        //OPPO R9 android 5.1 明明连上了 但获取内部状态 返回false 导致反复连接断开
        if(Build.MANUFACTURER.equalsIgnoreCase("OPPO")||Build.MANUFACTURER.equalsIgnoreCase("VIVO")){
            return CONNECTION_STATE_UN_SUPPORT;
        }

        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothDevice remoteDevice = adapter.getRemoteDevice(mac);
        Object mIBluetooth = null;
        try {
            Field sService = BluetoothDevice.class.getDeclaredField("sService");
            sService.setAccessible(true);
            mIBluetooth = sService.get(null);
        } catch (Exception e) {
            return CONNECTION_STATE_UN_SUPPORT;
        }
        if (mIBluetooth == null) return CONNECTION_STATE_UN_SUPPORT;

        boolean isConnected;
        try {
            Method isConnectedMethod = BluetoothDevice.class.getDeclaredMethod("isConnected");
            isConnectedMethod.setAccessible(true);
            isConnected = (Boolean) isConnectedMethod.invoke(remoteDevice);
            isConnectedMethod.setAccessible(false);
        } catch (Exception e) {
            try {
                Method getConnectionState = mIBluetooth.getClass().getDeclaredMethod("getConnectionState", BluetoothDevice.class);
                getConnectionState.setAccessible(true);
                int state = (Integer) getConnectionState.invoke(mIBluetooth, remoteDevice);
                getConnectionState.setAccessible(false);
                isConnected = state == CONNECTION_STATE_CONNECTED;
            } catch (Exception e1) {
                return CONNECTION_STATE_UN_SUPPORT;
            }
        }
        return isConnected ? CONNECTION_STATE_CONNECTED : CONNECTION_STATE_DISCONNECTED;

    }

    /**
     * 重启BLE服务状态
     * 有些手机连接蓝牙设备,操作: 断开,连接,断开,连接,断开...  反复重试,导致最终系统残留了该蓝牙设备的引用 无法清除,
     * 即使把你写的蓝牙app卸载掉了  蓝牙仍然没有断开 , 甚至有些手机把蓝牙关了 也没断开设备, 最终把手机开启飞行模式 才真正完全断开...
     * 现在这个方法则是启BLE服务状态,使已经残留的BLE设备强制断开
     * 这是查看源码研究得来的 代码全网独一无二
     * 关键源码
     * AdapterService.java
     *
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void setLeServiceEnable(boolean isEnable) {

        Object mIBluetooth;
        try {
            Field sService = BluetoothDevice.class.getDeclaredField("sService");
            sService.setAccessible(true);
            mIBluetooth = sService.get(null);
        } catch (Exception e) {
            return;
        }
        if (mIBluetooth == null) return;

        try {
            if (isEnable) {
                Method onLeServiceUp = mIBluetooth.getClass().getDeclaredMethod("onLeServiceUp");
                onLeServiceUp.setAccessible(true);
                onLeServiceUp.invoke(mIBluetooth);
            } else {
                Method onLeServiceUp = mIBluetooth.getClass().getDeclaredMethod("onBrEdrDown");
                onLeServiceUp.setAccessible(true);
                onLeServiceUp.invoke(mIBluetooth);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 释放GATT扫描客户端
     * <p>
     * <p>
     * -问题:
     * 扫描周围的BLE设备时某些手机会遇到 GATT_Register: cant Register GATT client, MAX client reached!
     * 或者回调中的 onScanFailed 返回了 errorCode =2 则: ScanCallback.SCAN_FAILED_APPLICATION_REGISTRATION_FAILED
     * 具体表现则为 明明周围有很多设备,但是扫描不到任何东西
     * 查到
     * 1.https://blog.csdn.net/chy555chy/article/details/53788748
     * 2.https://stackoverflow.com/questions/27516399/solution-for-ble-scans-scan-failed-application-registration-failed
     * 3.http://detercode121.blogspot.com/2012/04/bluetooth-lowenergy-solution-for-ble.html
     * 4....等 没有一个是正常的解决办法
     * 上面这些修复方案是用代码实现关闭蓝牙然后重新打开蓝牙来释放  可是 国产的手机会弹出蓝牙授权的 比如我们要后台扫描重连设备时遇到这种情况 难道要弹出授权让用户确定? 那还要后台重连功能干啥...
     * 而且,有些手机即使关闭蓝牙再打开 也无法释放,有些手机关闭蓝牙后 再打开会卡死系统, 导致蓝牙图标一直卡在那很久 才打开了蓝牙...
     * <p>
     * -我解决该问题的步骤:
     * 问题就在于 一些手机在startScan扫描的过程中还没来得及stopScan ,就被系统强制杀掉了, 导致mClientIf未被正常释放,实例和相关蓝牙对象已被残留到系统蓝牙服务中,
     * 打开app后又重新初始化ScanCallback多次被注册,导致每次的扫描mClientIf的值都在递增, 于是mClientIf的值
     * 在增加到一定程度时(最大mClientIf数量视国产系统而定 不做深究),onScanFailed 返回了 errorCode =2 至今网上无任何正常的解决办法
     * 于是 我查看了系统源码 发现关键位置BluetoothLeScanner类下的 BleScanCallbackWrapper#startRegistration() 扫描是通过 registerClient 传入 mClientIf 来实现的,
     * 在stopScan时调用了iGatt.stopScan() 和 iGatt.unregisterClient()  进行解除注册. 了解该原理后 我们就可以反射调用这个方法 ,  至于解除mClientIf哪个值 需要你自己做存储记录
     * 这里我写的是解除全部客户端 mClientIf的范围是 0~40
     * 问题至此完美解决 这是目前全网唯一完美解决该问题的方案(2018-04-19)
     * <p>
     * 参考IBluetoothGatt.aidl
     * 参考BluetoothLeScanner.java
     * 参考ScanManager.java
     * 参考GattService.java
     *
     * @return
     */
    public static boolean releaseAllScanClient() {
        try {
            Object mIBluetoothManager = getIBluetoothManager(BluetoothAdapter.getDefaultAdapter());
            if (mIBluetoothManager == null) return false;
            Object iGatt = getIBluetoothGatt(mIBluetoothManager);
            if (iGatt == null) return false;

            Method unregisterClient = getDeclaredMethod(iGatt, "unregisterClient", int.class);
            Method stopScan;
            int type;
            try {
                type = 0;
                stopScan = getDeclaredMethod(iGatt, "stopScan", int.class, boolean.class);
            } catch (Exception e) {
                type = 1;
                stopScan = getDeclaredMethod(iGatt, "stopScan", int.class);
            }

            for (int mClientIf = 0; mClientIf <= 40; mClientIf++) {
                if (type == 0) {
                    try {
                        stopScan.invoke(iGatt, mClientIf, false);
                    } catch (Exception ignored) {
                    }
                }
                if (type == 1) {
                    try {
                        stopScan.invoke(iGatt, mClientIf);
                    } catch (Exception ignored) {
                    }
                }
                try {
                    unregisterClient.invoke(iGatt, mClientIf);
                } catch (Exception ignored) {
                }
            }
            stopScan.setAccessible(false);
            unregisterClient.setAccessible(false);
            BLESupport.getDeclaredMethod(iGatt, "unregAll").invoke(iGatt);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 释放一个GATT扫描客户端
     * 功能请参看{@link #releaseAllScanClient()}
     *
     * @param mClientIf
     * @return
     */
    public static boolean releaseScanClient(int mClientIf) {
        try {
            Object mIBluetoothManager = getIBluetoothManager(BluetoothAdapter.getDefaultAdapter());
            if (mIBluetoothManager == null) return false;
            Object iGatt = getIBluetoothGatt(mIBluetoothManager);
            if (iGatt == null) return false;

            Method unregisterClient = getDeclaredMethod(iGatt, "unregisterClient", int.class);

            try {
                Method stopScan = getDeclaredMethod(iGatt, "stopScan", int.class, boolean.class);
                stopScan.invoke(iGatt, mClientIf, false);
                stopScan.setAccessible(false);
            } catch (Exception e) {
                Method stopScan = getDeclaredMethod(iGatt, "stopScan", int.class);
                stopScan.invoke(iGatt, mClientIf);
                stopScan.setAccessible(false);
            }

            unregisterClient.invoke(iGatt, mClientIf);

            unregisterClient.setAccessible(false);
            return true;
        } catch (Exception ignored) {
        }
        return false;
    }


    /**
     * 功能: 是否真的成功初始化扫描?
     * <p>
     * 一些手机在startScan扫描的过程中还没来得及stopScan ,就被系统强制杀掉了, 导致mClientIf未被释放,多次被注册,导致每次扫描mClientIf的值都在递增, 于是
     * onScanFailed 返回了 errorCode =2 则: ScanCallback.SCAN_FAILED_APPLICATION_REGISTRATION_FAILED
     * 扫描错误,应用程序注册错误,  没错 就是这么坑! 实测在小米 mClientIf>12 左右,华为mClientIf>38 左右 则返回这个错误
     * 所以这个方法是用来判断这种情况是否发生的.
     * 至于如何去释放 mClientIf的值 使程序恢复正常扫描  请参看{@link #releaseAllScanClient()}和{@link #releaseScanClient(int)}
     * <p>
     * 参考IBluetoothGatt.aidl
     * 参考BluetoothLeScanner.java
     * 参考ScanManager.java
     * 参考GattService.java
     *
     * @param callback
     * @return
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static boolean isScanClientInitialize(ScanCallback callback) {
        try {
            Field mLeScanClientsField = getDeclaredField(BluetoothLeScanner.class, "mLeScanClients");
            //  HashMap<ScanCallback, BleScanCallbackWrapper>()
            HashMap callbackList = (HashMap) mLeScanClientsField.get(BluetoothAdapter.getDefaultAdapter().getBluetoothLeScanner());
            int     size         = callbackList == null ? 0 : callbackList.size();
            if (size > 0) {
                Iterator iterator = callbackList.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry entry = (Map.Entry) iterator.next();
                    Object    key   = entry.getKey();
                    Object    val   = entry.getValue();
                    if (val != null && key != null && key == callback) {
                        int mClientIf = 0;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            Field mScannerIdField = getDeclaredField(val, "mScannerId");
                            mClientIf = mScannerIdField.getInt(val);

                        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            Field mClientIfField = getDeclaredField(val, "mClientIf");
                            mClientIf = mClientIfField.getInt(val);
                        }
                        System.out.println("mClientIf=" + mClientIf);
                        return true;
                    }
                }
            } else {
                if (callback != null) {
                    return false;
                }
            }


        } catch (Exception ignored) {

        }
        //可能不兼容导致闪退 默认返回成功,避免出问题
        return true;
    }

    public static boolean isScanClientInitialize(BluetoothAdapter.LeScanCallback callback) {
        try {
            BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
            Field mLeScanClient = getDeclaredField(BluetoothAdapter.class, "mLeScanClients");
            mLeScanClient.setAccessible(true);
//Map<LeScanCallback, ScanCallback> mLeScanClients;
            HashMap callbackList = (HashMap) mLeScanClient.get(adapter);
            int size = callbackList == null ? 0 : callbackList.size();
            if (size > 0) {
                Iterator iterator = callbackList.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry entry = (Map.Entry) iterator.next();
                    Object key = entry.getKey();//LeScanCallback
                    Object val = entry.getValue();//ScanCallback
                    if (val != null && key != null && key == callback) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            return isScanClientInitialize((ScanCallback) val);
                        }
                        return true;
                    }
                }
            } else {
                if (callback != null) {
                    return false;
                }
            }
        } catch (Exception ignored) {
        }
        //可能不兼容导致闪退 默认返回成功,避免出问题
        return true;
    }


    @SuppressLint("PrivateApi")
    public static Object getIBluetoothGatt(Object mIBluetoothManager) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Method getBluetoothGatt = getDeclaredMethod(mIBluetoothManager, "getBluetoothGatt");
        return getBluetoothGatt.invoke(mIBluetoothManager);
    }


    @SuppressLint("PrivateApi")
    public static Object getIBluetoothManager(BluetoothAdapter adapter) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Method getBluetoothManager = getDeclaredMethod(BluetoothAdapter.class, "getBluetoothManager");
        return getBluetoothManager.invoke(adapter);
    }


    public static Field getDeclaredField(Class<?> clazz, String name) throws NoSuchFieldException {
        Field declaredField = clazz.getDeclaredField(name);
        declaredField.setAccessible(true);
        return declaredField;
    }


    public static Method getDeclaredMethod(Class<?> clazz, String name, Class<?>... parameterTypes) throws NoSuchMethodException {
        Method declaredMethod = clazz.getDeclaredMethod(name, parameterTypes);
        declaredMethod.setAccessible(true);
        return declaredMethod;
    }


    public static Field getDeclaredField(Object obj, String name) throws NoSuchFieldException {
        Field declaredField = obj.getClass().getDeclaredField(name);
        declaredField.setAccessible(true);
        return declaredField;
    }


    public static Method getDeclaredMethod(Object obj, String name, Class<?>... parameterTypes) throws NoSuchMethodException {
        Method declaredMethod = obj.getClass().getDeclaredMethod(name, parameterTypes);
        declaredMethod.setAccessible(true);
        return declaredMethod;
    }
}
