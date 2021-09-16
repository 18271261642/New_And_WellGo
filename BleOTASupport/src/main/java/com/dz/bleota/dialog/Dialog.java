package com.dz.bleota.dialog;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.dz.bleota.base.OTA;
import com.dz.bleota.dialog.async.DeviceConnectTask;
import com.dz.bleota.dialog.bluetooth.BluetoothGattReceiver;
import com.dz.bleota.dialog.bluetooth.BluetoothGattSingleton;
import com.dz.bleota.dialog.bluetooth.BluetoothManager;
import com.dz.bleota.dialog.bluetooth.SuotaManager;
import com.dz.bleota.dialog.data.File;
import com.dz.bleota.dialog.data.Statics;

import java.io.FileInputStream;

/**
 * 作者:东芝(2018/3/6).
 * 功能:da14585的OTA
 */

public class Dialog extends OTA {

    private SuotaManager bluetoothManager;
    private DeviceConnectTask connectTask;
    private BluetoothDevice mSelectedDevice;

    @SuppressLint("StaticFieldLeak")
    @Override
    public void connect(BluetoothDevice mDevice) {
        bluetoothManager = new SuotaManager(getContext());
        mSelectedDevice = mDevice;
        registerReceiver();

        connectTask = new DeviceConnectTask(getContext(), bluetoothManager, mSelectedDevice) {
            @Override
            protected void onProgressUpdate(BluetoothGatt... gatt) {
                BluetoothGattSingleton.setGatt(gatt[0]);
            }
        };
        connectTask.execute();

    }


    @Override
    public void close() {
        if (bluetoothManager != null) {
            try {
                bluetoothManager.sendEndSignal();
                bluetoothManager.disconnect();
            } catch (Exception ignored) {
            }
        }
        try {
            if (BluetoothGattSingleton.getGatt() != null) {
                BluetoothGattSingleton.getGatt().disconnect();
                BluetoothGattSingleton.getGatt().close();
            }
        } catch (Exception ignored) {
        }

    }

    @Override
    public void destroy() {
        if (connectTask != null) {
            try {
                connectTask.cancel();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        close();
        try {
            unregisterReceiver();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private BluetoothGattReceiver connectionStateReceiver = new BluetoothGattReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == null) return;
            switch (action) {
                case Statics.CONNECTION_STATE_UPDATE:
                    int connectionState = intent.getIntExtra("state", 0);
                    connectionStateChanged(connectionState);
                    break;
                case Statics.BLUETOOTH_GATT_UPDATE:
                    bluetoothManager.processStep(intent);
                    int newStep = intent.getIntExtra("step", -1);
                    if (newStep == 4) {
                        updateStatus(STATUS_UPDATING);
                    }
                    int error = intent.getIntExtra("error", -1);
                    if (error != -1) {
                        if (error == 21) {
                            updateStatus(STATUS_CANCEL);
                        } else {
                            updateStatus(STATUS_FAILED);
                        }
                    }
                    break;
                case Statics.PROGRESS_UPDATE:
                    int progress = intent.getIntExtra("progess", 0);
                    updateProgress(progress, 100);
                    break;

                case Statics.UPDATE_STATUS:
                    int success = intent.getIntExtra("success", 0);
                    if (success == 1) {
                        updateStatus(STATUS_SUCCESSFUL);
                    } else if (success == 0) {
                        updateStatus(STATUS_FAILED);
                    }
                    break;
            }

        }
    };


    private void connectionStateChanged(int connectionState) {
        if (connectionState == BluetoothProfile.STATE_DISCONNECTED) {
            if (BluetoothGattSingleton.getGatt() != null) {
                // Refresh device cache if update was successful
                BluetoothManager.refresh(BluetoothGattSingleton.getGatt());
                BluetoothGattSingleton.getGatt().close();
            }
            if (bluetoothManager.getError()) {
                updateStatus(STATUS_FAILED);
            }
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        //歇会... 太快会出问题
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    startUpdate();
                }
            }).start();
        }
    }

    private void startUpdate() {
        bluetoothManager.setDevice(mSelectedDevice);
        try {
            bluetoothManager.setFile(new File(new FileInputStream(getOTAFile())));
        } catch (Exception e) {
            e.printStackTrace();
            updateStatus(STATUS_FAILED);
            return;
        }

        bluetoothManager.setMemoryType(Statics.MEMORY_TYPE_SPI);
        bluetoothManager.setMISO_GPIO(5);
        bluetoothManager.setMOSI_GPIO(6);
        bluetoothManager.setCS_GPIO(3);
        bluetoothManager.setSCK_GPIO(0);
        bluetoothManager.setImageBank(0);

        //Set default block size to 1 for SPOTA, this will not be used in this case
        int fileBlockSize = 240;
        bluetoothManager.getFile().setFileBlockSize(fileBlockSize);


        Intent intent = new Intent();
        intent.setAction(Statics.BLUETOOTH_GATT_UPDATE);
        intent.putExtra("step", 1);
        LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);

    }

    private void registerReceiver() {
        try {
            IntentFilter filter = new IntentFilter();
            filter.addAction(Statics.CONNECTION_STATE_UPDATE);
            filter.addAction(Statics.BLUETOOTH_GATT_UPDATE);
            filter.addAction(Statics.PROGRESS_UPDATE);
            filter.addAction(Statics.UPDATE_STATUS);
            LocalBroadcastManager.getInstance(this.getContext()).registerReceiver(
                    this.connectionStateReceiver,
                    filter);

        } catch (Exception ignored) {
        }
    }

    private void unregisterReceiver() {
        try {
            LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(this.connectionStateReceiver);
        } catch (Exception ignored) {
        }
    }


}
