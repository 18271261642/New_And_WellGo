package com.dz.blesdk.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import androidx.annotation.RequiresApi;
import android.text.TextUtils;

import com.dz.blesdk.entity.BleDevice;
import com.dz.blesdk.entity.ParsedAd;
import com.dz.blesdk.utils.BLELog;
import com.dz.blesdk.utils.BLESupport;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingDeque;


/**
 * 蓝牙扫描类
 * 东芝 2014-8-3
 */
@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class BaseBleScanHelper {
    private static final int SHORTENED_LOCAL_NAME = 0x08;
    private static final int COMPLETE_LOCAL_NAME = 0x09;
    private static long SCAN_PERIOD = 15000L;

    /**
     * 扫描动态延迟 经过一系列的逻辑判断 最终让app在30秒内 扫描不超过5次,  当然在低于Android N的设备 则不需要处理
     */
    private static long SCAN_DELAY = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N?((35 * 1000L) / 5) /*6.2秒*/:100;


    private static BaseBleScanHelper instance = null;
    private Context mCtx;
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private Handler mHandler;

    private boolean isScaning = false;
    private CopyOnWriteArrayList<ScanDeviceListener> mScanDeviceListenerList = new CopyOnWriteArrayList<ScanDeviceListener>();
    private CopyOnWriteArrayList<String> mFilterNameList = new CopyOnWriteArrayList<String>();
    private boolean isScan;
    private BluetoothLeScannerCompat scannerCompat;
    private long mLastScanTime;

    public void setScanTimeOut(long time) {
        SCAN_PERIOD = time;
    }

    /**
     * must init in application
     */
    public void init(Context context) {
        BLELog.w("BleScanUtil init !");
        mCtx = context;
        mBluetoothManager = DZBLESDK.getBluetoothManager();
        mBluetoothAdapter = DZBLESDK.getBluetoothAdapter();
        BLESupport.refreshBleAppFromSystem(context, context.getPackageName());
    }


    public boolean isScanning() {
        return isScaning;
    }

    public void addFilterName(String filterName) {
        if (filterName != null) {
            if (!mFilterNameList.contains(filterName.toUpperCase())) {
                mFilterNameList.add(filterName.toUpperCase());
            }
        }
    }

    public void removeFilterName(String filterName) {
        if (filterName != null && (!mFilterNameList.isEmpty())) {
            mFilterNameList.remove(filterName);
        }
    }

    public void clearFilterName() {
        for (String filterName : mFilterNameList) {
            mFilterNameList.remove(filterName);
        }
    }

    public void addScanDeviceListener(ScanDeviceListener listener) {
        if (listener != null) {
            if (!mScanDeviceListenerList.contains(listener)) {
                mScanDeviceListenerList.add(listener);
            }
        }
    }

    public void removeScanDeviceListener(ScanDeviceListener listener) {
        if (listener != null && (!mScanDeviceListenerList.isEmpty())) {
            mScanDeviceListenerList.remove(listener);
        }
    }

    public void clearScanDeviceListener() {
        for (ScanDeviceListener listener : mScanDeviceListenerList) {
            mScanDeviceListenerList.remove(listener);
        }
    }

    public void scanLeDevice(boolean isScan) {
        this.isScan = isScan;
        BLELog.w("scanLeDevice=" + isScan);

        BLELog.stack("scanLeDevice stack");
        if (mBluetoothAdapter == null) {
            BLELog.w("错误 mBluetoothAdapter == null ");
            mBluetoothAdapter = DZBLESDK.getBluetoothAdapter();
        }
        if (mBluetoothAdapter == null) {
            BLELog.w("灾难性错误 mBluetoothAdapter 根本无法获取! 是否手机的蓝牙已损坏? ");
            return;
        }
        if (!mBluetoothAdapter.isEnabled()) {
            BLELog.w("扫描失败,蓝牙已关闭!");
            return;
        }
        if (scannerCompat == null) {
            scannerCompat = BluetoothLeScannerCompat.getScanner();
        }
        mHandler.removeCallbacks(mTimeoutRunnable);
        mHandler.removeCallbacks(mScanDelayRunnable);


        if (isScan) {
            isScaning = true;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (scannerCompat.isScan()) {
                        try {
                            scannerCompat.stopScan(mLeScanCallback);
                        } catch (Throwable ignored) {
                        }
                    }
                    long scanDelay = SCAN_DELAY;

                    long offset = System.currentTimeMillis() - mLastScanTime;
                    if (offset > SCAN_DELAY)//距离上次扫描时间已超过SCAN_DELAY秒 于是可重置为0
                    {
                        scanDelay = 100;
                    } else {//距离上次扫描时间未超过SCAN_DELAY秒
                        scanDelay = SCAN_DELAY - offset;
                    }
                    BLELog.w("扫描启动延时:" + (scanDelay / 1000.0f) + "秒");
                    mHandler.removeCallbacks(mScanDelayRunnable);
                    mHandler.postDelayed(mScanDelayRunnable, scanDelay);
                    mLastScanTime = System.currentTimeMillis();
                }
            }).start();
        } else {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (scannerCompat.isScan()) {
                        try {
                            scannerCompat.stopScan(mLeScanCallback);
                        } catch (Throwable ignored) {
                        }
                    }
                    blockingDeque.clear();
                    isScaning = false;
                }
            }).start();
        }
    }

    private Runnable mScanDelayRunnable = new Runnable() {
        @Override
        public void run() {
            if (scannerCompat != null) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        BLELog.w("扫描正式开始");
                        scannerCompat.startScan(mLeScanCallback);
                        mHandler.removeCallbacks(mTimeoutRunnable);
                        mHandler.postDelayed(mTimeoutRunnable, SCAN_PERIOD);
                    }
                }).start();
            }
        }
    };
    //超时检测
    private Runnable mTimeoutRunnable = new Runnable() {
        public void run() {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    //超时了 关闭蓝牙扫描
                    System.out.println("扫描 超时");
                    try {
                        BluetoothLeScannerCompat.getScanner().stopScan(mLeScanCallback);
                    } catch (Throwable ignored) {
                    }
                    mHandler.post(new Runnable() {
                        public void run() {
                            isScaning = false;
                            for (ScanDeviceListener listener : mScanDeviceListenerList) {
                                listener.onFinish();
                                listener.onTimeout();
                            }
                        }
                    });
                }
            }).start();
        }
    };
    private final BLEScanCallback mLeScanCallback = new BLEScanCallback();
    private final LinkedBlockingDeque<Object[]> blockingDeque = new LinkedBlockingDeque<>();

    private class BLEScanCallback extends BluetoothLeScannerCompat.ScanCallback {

        public BLEScanCallback() {
            new Thread() {
                public void run() {
                    while (true) {

                        try {
                            Object[] take = blockingDeque.take();
                            if (take == null) continue;
                            BluetoothDevice device = (BluetoothDevice) take[0];
                            int rssi = (int) take[1];
                            byte[] scanRecord = (byte[]) take[2];

                            String name = device.getName();
                            if (TextUtils.isEmpty(name)) {
                                name = decodeDeviceName(scanRecord);
                                if (TextUtils.isEmpty(name)) {
                                    //continue;
                                    name = "N/A";
                                }
                            }

                            // 设置名字过滤
                            boolean isFilterDevice = mFilterNameList.isEmpty() || mFilterNameList.contains(name.toUpperCase());

                            if (isFilterDevice) {
                                String address = device.getAddress();
                                final BleDevice d = new BleDevice();
                                d.mDeviceName = device.getName();
                                d.mDeviceAddress = address;
                                d.mRssi = rssi;
                                d.mScanRecord = scanRecord;
                                try {
                                    d.mParsedAd = parseData(scanRecord);
                                } catch (Exception ignored) {
                                }
                                d.mDevice = device;

                                for (ScanDeviceListener listener : mScanDeviceListenerList) {
                                    listener.onFind(d);
                                }

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }
            }.start();

        }

        @Override
        public void onScanResult(BluetoothDevice device, int rssi, byte[] scanRecord) {
//            BLELog.d("重连:扫描:" + "["+device.getAddress()+"]"+device.getName());
            blockingDeque.offer(new Object[]{device, rssi, scanRecord});
            if (!isScan) {
                scanLeDevice(false);
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);

            //出现异常 重新计算延迟并重新扫描
            if(errorCode== BluetoothLeScannerCompat.ScanCallback.SCAN_FAILED_OUT_OF_COUNT)
            {
                scanLeDevice(false);
                scanLeDevice(true);
            }
        }
    }


    public ParsedAd parseData(byte[] adv_data) {
        ParsedAd parsedAd = new ParsedAd();
        ByteBuffer buffer = ByteBuffer.wrap(adv_data).order(ByteOrder.LITTLE_ENDIAN);
        while (buffer.remaining() > 2) {
            byte length = buffer.get();
            if (length == 0)
                break;
            byte type = buffer.get();
            length -= 1;
            switch (type) {
                case 0x01: // Flags
                    parsedAd.flags = buffer.get();
                    length--;
                    break;
                case 0x02: // Partial list of 16-bit UUIDs
                case 0x03: // Complete list of 16-bit UUIDs
                case 0x14: // List of 16-bit Service Solicitation UUIDs
                    while (length >= 2) {
                        parsedAd.uuids.add(UUID.fromString(String.format(
                                "%08x-0000-1000-8000-00805f9b34fb", buffer.getShort())));
                        length -= 2;
                    }
                    break;
                case 0x04: // Partial list of 32 bit service UUIDs
                case 0x05: // Complete list of 32 bit service UUIDs
                    while (length >= 4) {
                        parsedAd.uuids.add(UUID.fromString(String.format(
                                "%08x-0000-1000-8000-00805f9b34fb", buffer.getInt())));
                        length -= 4;
                    }
                    break;
                case 0x06: // Partial list of 128-bit UUIDs
                case 0x07: // Complete list of 128-bit UUIDs
                case 0x15: // List of 128-bit Service Solicitation UUIDs
                    while (length >= 16) {
                        long lsb = buffer.getLong();
                        long msb = buffer.getLong();
                        parsedAd.uuids.add(new UUID(msb, lsb));
                        length -= 16;
                    }
                    break;
                case 0x08: // Short local device name
                case 0x09: // Complete local device name
                    byte sb[] = new byte[length];
                    buffer.get(sb, 0, length);
                    length = 0;
                    parsedAd.localName = new String(sb).trim();
                    break;
                case (byte) 0xFF: // Manufacturer Specific Data
                    int manufacturer = buffer.getShort()&0xFFFF;
                    //大小端互换
                    manufacturer = (((manufacturer & 0xff) << 8) | (manufacturer >> 8));
                    if (!parsedAd.manufacturers.contains(manufacturer)) {
                        parsedAd.manufacturers.add(manufacturer);
                    }
                    length -= 2;
                    break;

                default: // skip
                    break;
            }
            if (length > 0) {
                buffer.position(buffer.position() + length);
            }
        }
        return parsedAd;
    }




    private String decodeDeviceName(byte[] data) {
        String name = null;
        int fieldLength, fieldName;
        int packetLength = data.length;
        for (int index = 0; index < packetLength; index++) {
            fieldLength = data[index];
            if (fieldLength == 0)
                break;
            fieldName = data[++index];

            if (fieldName == COMPLETE_LOCAL_NAME
                    || fieldName == SHORTENED_LOCAL_NAME) {
                name = decodeLocalName(data, index + 1, fieldLength - 1);
                break;
            }
            index += fieldLength - 1;
        }
        return name;
    }

    private String decodeLocalName(final byte[] data, final int start,
                                   final int length) {
        try {
            return new String(data, start, length, "UTF-8");
        } catch (final UnsupportedEncodingException e) {
            BLELog.w("Unable to convert the complete local name to UTF-8");
            return null;
        } catch (final IndexOutOfBoundsException e) {
            BLELog.w("Error when reading complete local name");
            return null;
        }
    }

    public static synchronized BaseBleScanHelper getInstance() {
        if (instance == null) {
            instance = new BaseBleScanHelper();
        }
        return instance;
    }

    private BaseBleScanHelper() {
        mHandler = new Handler();
    }

    public interface ScanDeviceListener {
        void onFind(BleDevice device);

        void onFinish();

        void onTimeout();
    }


}
