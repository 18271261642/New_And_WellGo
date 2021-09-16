package com.sn.blesdk.control;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;

import com.dz.blesdk.comm.GattError;
import com.dz.blesdk.utils.BLELog;
import com.sn.app.db.data.user.UserBean;
import com.sn.app.utils.AppUserUtil;
import com.sn.blesdk.ble.DeviceType;
import com.sn.blesdk.ble.SNBLEControl;
import com.sn.blesdk.ble.SNBLEHelper;

/**
 * 作者:东芝(2017/11/23).
 * 功能:自动重连工具
 * 重连机制目前因为兼容性考虑了:
 * 1.在小米和华为手机的前提下,关闭蓝牙再打开蓝牙 的第二个前提下,第一次需要扫描一下 才能重接
 * 2.发生灾难性错误 则不进行重连
 * 3.蓝牙关闭时不会重连 蓝牙打开时继续自动重连
 * 4.无历史mac地址不进行重连
 * 5.开发者主动调用disconnect的情况不进行重连(比如解绑,清除重启设备,OTA/DFU升级,切换设备 等)
 * 6.超过最大重连数 不进行重连  @getMaxReConnectCount() 可设置最大重连数 默认无穷大
 * 7.开发者可以设置不重连@setAutoReConnect(),不设置的话 如果以前连接过则断开时默认自动重连
 * 8.发生 蓝牙异常断开的任意情况 除以上的灾难性错误, 其他均进行重连
 * 9.已经连接 不会进行重连
 * 10.本工具 建议放服务里使用
 */

public class AutoReconnectHelper
{
	private String  mLastDeviceMacAddress;
	private Handler mHandler = null;
	private Run     run      = null;
	private int     mCurrentReConnectCount;
	private Context context;

	public AutoReconnectHelper(Context context)
	{
		this.context = context;
		//设置重连
		mHandler = new Handler();
		run = new Run();
	}

	public void onConnected()
	{
		refreshMacAddress();
		long s = (System.currentTimeMillis() - BLELog.beginConnectTime) / 1000;
		BLELog.w("重连:连接耗时:" + s + "秒");
		if (mCurrentReConnectCount > 0)
		{
			BLELog.w("重连:重连耗时:" + s + "秒");
		}
		mCurrentReConnectCount = 0;
	}

	private void refreshMacAddress()
	{
		String mCurrentDeviceMacAddress = SNBLEHelper.getDeviceMacAddress();
		if (TextUtils.isEmpty(mCurrentDeviceMacAddress))
		{
			try
			{
				UserBean user = AppUserUtil.getUser();
				if (user != null)
				{
					mCurrentDeviceMacAddress = user.getMac();
				}
			} catch (Exception e)
			{
				mCurrentDeviceMacAddress = DeviceType.getDeviceMac();
			}
		}
		mLastDeviceMacAddress = mCurrentDeviceMacAddress;
	}

	public void onReConnect()
	{
		onDisconnected();
	}

	public void onDisconnected()
	{
		refreshMacAddress();
		if (!SNBLEHelper.isAutoReConnect())
		{
			BLELog.w("重连:开发者设置了禁止重连");
			abortReConnect();
			return;//开发者设置了禁止重连
		}
		if (SNBLEHelper.isIsUserDisconnected())
		{
			BLELog.w("重连:开发者主动断开,不能重连");
			abortReConnect();
			return;
		}
		if (mCurrentReConnectCount >= SNBLEHelper.getMaxReConnectCount())
		{
			BLELog.w("重连:超过最大重连数:" + mCurrentReConnectCount);
			abortReConnect();
			return;
		}
		mCurrentReConnectCount++;
		//稍后重连
		BLELog.stack("重连:准备重连到:" + mLastDeviceMacAddress);
		abortReConnect();
		startReConnect();

	}

	public void startReConnect()
	{
		mHandler.postDelayed(run, 1000L);
	}

	public void abortReConnect()
	{
		mHandler.removeCallbacks(run);
	}

	public void onFailed(int errorType)
	{

		if (errorType == GattError.ERROR_SERVICES_DISCOVERED_ERROR/*发现服务错误*/ || errorType == GattError.ERROR_SERVICES_NOTIFY_ERROR/*通知服务开启错误*/)
		{
			//这两个错误是灾难性错误...  具体错误原因是 app的UUID特征/服务写错,手机蓝牙损坏,设备协议写错 或设备不支持某些特性却跟工程师说支持,该手机已连接超过最大通知数量 等.
			//属于这种错误 不能再进行重连 因为是不可能连接成功的!
			onDisconnected();
			return;
		}
		if (!SNBLEHelper.isConnecting())
		{
			onDisconnected();
		}

	}


	class Run implements Runnable
	{

		@Override
		public void run()
		{
			//如果没有权限, 请求SNBLEControl.isBluetoothEnable()会出现安全权限 , 闪退
			try
			{
				if (!SNBLEControl.isBluetoothEnable())
				{
					BLELog.w("重连:开始重连失败,因为蓝牙关闭了");
					SNBLEHelper.setIsBluetoothRecycled(true);
					abortReConnect();
					return;
				}
			} catch (Throwable e)
			{
				abortReConnect();
				return;
			}

			if (SNBLEHelper.isConnected())
			{
				BLELog.w("重连:开始重连失败,因为已经连接了");
				abortReConnect();
				return;
			}
			if (SNBLEHelper.isIsUserDisconnected())
			{
				BLELog.w("重连:开发者主动断开,不能重连");
				abortReConnect();
				return;
			}
			if (!SNBLEHelper.isAutoReConnect())
			{
				BLELog.w("重连:开始重连失败,开发者设置了禁止重连");
				abortReConnect();
				return;
			}
			refreshMacAddress();
			if (TextUtils.isEmpty(mLastDeviceMacAddress))
			{
				BLELog.w("重连:开始重连失败,mac地址为空");
				abortReConnect();
				return;
			}

//            if (SNBLEScanner.isScanning()) {
//                SNBLEScanner.stopScan();
//                BLELog.w("重连:停止扫描");
//            }
			BLELog.w("重连:开始重连:" + mLastDeviceMacAddress);
			if (!SNBLEHelper.isConnecting())
			{
				SNBLEHelper.connect(mLastDeviceMacAddress);
			}
		}
	}


}
