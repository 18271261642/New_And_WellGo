package com.sn.blesdk.service;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.dz.blesdk.comm.GattError;
import com.dz.blesdk.interfaces.BluetoothStatusListener;
import com.dz.blesdk.interfaces.ConnectListener;
import com.dz.blesdk.interfaces.NotifyReceiverListener;
import com.dz.blesdk.utils.BLELog;
import com.sn.blesdk.ble.SNBLEControl;
import com.sn.blesdk.ble.SNBLEHelper;
import com.sn.blesdk.ble.SNBLEScanner;
import com.sn.blesdk.control.AutoReconnectHelper;
import com.sn.blesdk.control.DeviceInfoDataDecodeHelper;
import com.sn.blesdk.control.HealthDataDecodeHelper;
import com.sn.blesdk.control.SleepDataDecodeHelper;
import com.sn.blesdk.control.SportDataDecodeHelper;
import com.sn.blesdk.control.SportModeDecodeHelper;
import com.sn.blesdk.utils.eventbus.SNBLEEvent;
import com.sn.utils.SNLog;
import com.sn.utils.eventbus.SNEventBus;

/**
 * 作者:东芝(2017/11/20).
 * 功能:蓝牙后台处理服务
 */
public abstract class SNBLEService extends Service
{


	public static void startService(Context context, Class<? extends SNBLEService> service)
	{
		try
		{
			Intent intent = new Intent(context, service);
			context.startService(intent);
		} catch (Exception ignored)
		{
			ignored.printStackTrace();
		}
	}

	public static void stopService(Context context, Class<? extends SNBLEService> service)
	{
		context.stopService(new Intent(context, service));
	}

	private AutoReconnectHelper        mAutoReconnectHelper;
	private SportDataDecodeHelper      mSportDataDecodeHelper;
	private SleepDataDecodeHelper      mSleepDataDecodeHelper;
	private HealthDataDecodeHelper     mHealthDataDecodeHelper;
	private DeviceInfoDataDecodeHelper mDeviceInfoDataDecodeHelper;
	private SportModeDecodeHelper      mSportModeDecodeHelper;
//    private EnvironmentInfoDataDecodeHelper mEnvironmentInfoDataDecodeHelper;

	@Override
	public IBinder onBind(Intent intent)
	{
		return null;
	}

	@Override
	public void onCreate()
	{
		super.onCreate();


		mAutoReconnectHelper = new AutoReconnectHelper(this);
		mSportDataDecodeHelper = new SportDataDecodeHelper();
		mSportModeDecodeHelper = new SportModeDecodeHelper();
		mSleepDataDecodeHelper = new SleepDataDecodeHelper();
		mHealthDataDecodeHelper = new HealthDataDecodeHelper();
		mDeviceInfoDataDecodeHelper = new DeviceInfoDataDecodeHelper();
//      mEnvironmentInfoDataDecodeHelper = new EnvironmentInfoDataDecodeHelper();

		SNBLEHelper.addConnectListener(connectListener);
		SNBLEHelper.addCommunicationListener(notifyReceiverListener);
		SNBLEControl.addBluetoothStatusListener(bluetoothStatusListener);

		//启用重连功能
		mAutoReconnectHelper.startReConnect();
		SNLog.i("蓝牙通讯后台服务已开启");
	}


	@Override
	public void onDestroy()
	{
		super.onDestroy();
		if (mAutoReconnectHelper != null)
		{
			mAutoReconnectHelper.abortReConnect();
		}
		SNBLEHelper.removeConnectListener(connectListener);
		SNBLEHelper.removeCommunicationListener(notifyReceiverListener);
		SNBLEControl.removeBluetoothStatusListener(bluetoothStatusListener);
		SNLog.i("蓝牙通讯后台服务已销毁");
	}

	/**
	 * 蓝牙状态监听
	 */
	private BluetoothStatusListener bluetoothStatusListener = new BluetoothStatusListener()
	{
		@Override
		public void onBluetoothStatusChange(int state)
		{


			switch (state)
			{
				case BluetoothAdapter.STATE_TURNING_ON:// 蓝牙正在开:

					break;
				case BluetoothAdapter.STATE_ON://蓝牙开
					BLELog.w("蓝牙状态:蓝牙已开");
					SNEventBus.sendEvent(SNBLEEvent.EVENT_BLE_STATUS_BLUETOOTH_ON);
					mAutoReconnectHelper.onReConnect();
					break;
				case BluetoothAdapter.STATE_TURNING_OFF://蓝牙正在关

					break;
				case BluetoothAdapter.STATE_OFF://蓝牙关
					BLELog.w("蓝牙状态:蓝牙已关");
					SNEventBus.sendEvent(SNBLEEvent.EVENT_BLE_STATUS_BLUETOOTH_OFF);
					if (SNBLEScanner.isScanning())
					{
						SNBLEScanner.stopScan();
					}
					break;

			}
		}
	};

	protected abstract void startSyncIfNeed();

	protected abstract void startSync();

	protected abstract void processSync(byte[] buffer);

	protected abstract void stopSync();

	/**
	 * 连接监听
	 */
	private ConnectListener connectListener = new ConnectListener()
	{
		@Override
		public void onConnected()
		{
			SNEventBus.sendEvent(SNBLEEvent.EVENT_BLE_STATUS_CONNECTED);
			BLELog.d("蓝牙状态:已连接");
			mAutoReconnectHelper.onConnected();

		}


		@Override
		public void onNotifyEnable()
		{
			SNEventBus.sendEvent(SNBLEEvent.EVENT_BLE_STATUS_NOTIFY_ENABLE);
			BLELog.d("蓝牙状态:已开启通知");
			if (SNBLEScanner.isScanning())
			{
				SNBLEScanner.stopScan();
			}
			startSyncIfNeed();//现在改成不强制同步,按需同步

		}

		@Override
		public void onDisconnected()
		{
			SNEventBus.sendEvent(SNBLEEvent.EVENT_BLE_STATUS_DISCONNECTED);
			BLELog.d("蓝牙状态:已断开");
			stopSync();
			mAutoReconnectHelper.onDisconnected();
		}

		@Override
		public void onFailed(int errorType)
		{
			SNEventBus.sendEvent(SNBLEEvent.EVENT_BLE_STATUS_CONNECT_FAILED);
			mAutoReconnectHelper.onFailed(errorType);
			stopSync();
			switch (errorType)
			{
				case GattError.ERROR_TIMEOUT_CONNECT:
					BLELog.w("蓝牙状态:连接错误: 连接超时");
					break;
				case GattError.ERROR_TIMEOUT_DISCOVERSERVICES:
					BLELog.w("蓝牙状态:连接错误: 发现服务超时");
					break;
				case GattError.ERROR_TIMEOUT_NOTIFY_ENABLE:
					BLELog.w("蓝牙状态:连接错误: 开启通知超时");
					break;
				case GattError.ERROR_SERVICES_DISCOVERED_ERROR:
					BLELog.w("蓝牙状态:连接错误: 发现服务错误");
					break;
				case GattError.ERROR_SERVICES_NOTIFY_ERROR:
					BLELog.w("蓝牙状态:连接错误: 通知服务开启错误");
					stopSync();
					mAutoReconnectHelper.onDisconnected();
					break;
				default:
					BLELog.w("蓝牙状态:未知错误: " + GattError.parse(errorType));
					break;
			}
		}
	};

	/**
	 * 通知数据回调监听
	 */
	private final NotifyReceiverListener notifyReceiverListener = new NotifyReceiverListener()
	{
		@Override
		public void onReceive(byte[] buffer)
		{
			try
			{
				BLELog.w("接收:" + SNBLEHelper.toHexString(buffer));
			} catch (Exception e)
			{
				e.printStackTrace();
			}
			processSync(buffer);
			//解码操作...
			mDeviceInfoDataDecodeHelper.decode(buffer);
			mSportDataDecodeHelper.decode(buffer);
			mSportModeDecodeHelper.decode(buffer);
			mSleepDataDecodeHelper.decode(buffer);
			mHealthDataDecodeHelper.decode(buffer);
//          mEnvironmentInfoDataDecodeHelper.decode(buffer);


			//TODO 下面的判断代码以后要封装起来

			//手环点击拍照
			if (SNBLEHelper.startWith(buffer, "05060101"))
			{
				SNEventBus.sendEvent(SNBLEEvent.EVENT_BLE_CAMERA_TAKE_PHOTO);
			}

			//手环找手机
			if (SNBLEHelper.startWith(buffer, "05060201"))
			{
				SNEventBus.sendStickyEvent(SNBLEEvent.EVENT_BLE_BAND_CALL_PHONE);
			}

			//手环电量等
			if (SNBLEHelper.startWith(buffer, "050102"))
			{
				SNEventBus.sendEvent(SNBLEEvent.EVENT_BLE_BAND_ELECTRIC, buffer);
			}

			//来电挂断
			if (SNBLEHelper.startWith(buffer, "05060301"))
			{
				SNEventBus.sendEvent(SNBLEEvent.EVENT_BLE_HANG_UP_THE_PHONE);
			}

			//来电静音
			if (SNBLEHelper.startWith(buffer, "05060302"))
			{
				SNEventBus.sendEvent(SNBLEEvent.EVENT_BLE_MUTE_CALLS);
			}

			//播放音乐下一首
			if (SNBLEHelper.startWith(buffer, "05060401"))
			{
				SNEventBus.sendEvent(SNBLEEvent.EVENT_DEVICE_MUSIC_NEXT);
			}

			//播放音乐开始或暂停
			if (SNBLEHelper.startWith(buffer, "05060402"))
			{
				SNEventBus.sendEvent(SNBLEEvent.EVENT_DEVICE_MUSIC_START_OR_PAUSE);
			}

			//播放音乐上一首
			if (SNBLEHelper.startWith(buffer, "05060403"))
			{
				SNEventBus.sendEvent(SNBLEEvent.EVENT_DEVICE_MUSIC_PREVIOUS);
			}
			//音量+
			if (SNBLEHelper.startWith(buffer, "05060501"))
			{
				SNEventBus.sendEvent(SNBLEEvent.EVENT_DEVICE_MUSIC_VOLUME_UP);
			}
			//音量-
			if (SNBLEHelper.startWith(buffer, "05060502"))
			{
				SNEventBus.sendEvent(SNBLEEvent.EVENT_DEVICE_MUSIC_VOLUME_DOWN);
			}
		}
	};


}
