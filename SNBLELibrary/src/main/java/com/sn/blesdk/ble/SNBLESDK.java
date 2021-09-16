package com.sn.blesdk.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Build;

import com.dz.blesdk.ble.DZBLESDK;
import com.dz.blesdk.ble.DZBLEScanner;
import com.sn.blesdk.service.SNBLEService;

import java.lang.ref.WeakReference;

/**
 * 作者:东芝(2017/11/18).
 * 功能:蓝牙SDK基类
 */

public class SNBLESDK
{

	private static Class<? extends SNBLEService> syncServiceCls;
	private static WeakReference<Context>        mBaseContext;

	public static void init(Context context, Class<? extends SNBLEService> syncServiceCls)
	{
		SNBLESDK.syncServiceCls = syncServiceCls;

		mBaseContext = new WeakReference<>(context.getApplicationContext());
		SNUUID.init();
		DZBLESDK.init(mBaseContext.get());
		long connectTimeOut = 15000L;
		SNBLEHelper.init(connectTimeOut, 10000L, 10000L);

		if ("HUAWEI".equals(Build.MANUFACTURER))
		{
			DZBLEScanner.setScanTimeOut(20000L);
		} else
		{
			DZBLEScanner.setScanTimeOut(15000L);
		}

		try
		{
			SNBLEService.startService(mBaseContext.get(), syncServiceCls);
		} catch (Exception ignored)
		{
		}
	}


	public static void close()
	{
		SNBLEService.stopService(mBaseContext.get(), syncServiceCls);
		DZBLESDK.close(mBaseContext.get());
		SNBLEHelper.close(mBaseContext.get());
	}

	/**
	 * 检查是否已经初始化
	 *
	 * @return
	 */
	public static boolean isInitialized()
	{
		return DZBLESDK.isInitialized();
	}

	/**
	 * 是否在4.3以上
	 *
	 * @return
	 */
	public static boolean isBluetoothSupportVersion()
	{
		return DZBLESDK.isBluetoothSupportVersion();
	}

	/**
	 * 是否支持蓝牙BLE
	 *
	 * @return
	 */
	public static boolean isBluetoothSupportBLE()
	{
		return DZBLESDK.isBluetoothSupportBLE();
	}

	/**
	 * 蓝牙是否开启
	 *
	 * @return
	 */
	public static boolean isBluetoothEnable()
	{
		return DZBLESDK.isBluetoothEnable();
	}

	/**
	 * 取得蓝牙适配器
	 *
	 * @return
	 */
	public static BluetoothAdapter getBluetoothAdapter()
	{
		return DZBLESDK.getBluetoothAdapter();
	}

	/**
	 * 取得蓝牙管理器
	 *
	 * @return
	 */
	public static BluetoothManager getBluetoothManager()
	{
		return DZBLESDK.getBluetoothManager();
	}

	/**
	 * 连接之前需要扫描的手机型号
	 */
	public static boolean isDeviceNeedScan()
	{
		return DZBLESDK.isDeviceNeedScan();
	}

}
