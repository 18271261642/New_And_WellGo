/*
 *******************************************************************************
 *
 * Copyright (C) 2016 Dialog Semiconductor, unpublished work. This computer
 * program includes Confidential, Proprietary Information and is a Trade
 * Secret of Dialog Semiconductor. All use, disclosure, and/or reproduction
 * is prohibited unless authorized in writing. All Rights Reserved.
 *
 * bluetooth.support@diasemi.com
 *
 *******************************************************************************
 */

package com.dz.bleota.dialog.async;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.dz.bleota.dialog.bluetooth.BluetoothGattSingleton;
import com.dz.bleota.dialog.bluetooth.BluetoothManager;
import com.dz.bleota.dialog.bluetooth.Callback;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;

public class DeviceConnectTask extends AsyncTask<Void, BluetoothGatt, Boolean> {
    public static final String TAG = "DeviceGattTask";
    public WeakReference<Context> context;
    private final BluetoothDevice mmDevice;
    private static Callback callback = null;
    private boolean refreshResult;

    public DeviceConnectTask(Context context, BluetoothManager bluetoothManager, BluetoothDevice device) {
        Log.d(TAG, "init");
        this.context = new WeakReference<Context>(context.getApplicationContext());

        mmDevice = device;
        callback = new Callback(bluetoothManager,this);
    }

    /**
     * Will cancel an in-progress connection, and close the socket
     */
    public void cancel() {
    }

    private boolean refreshDeviceCache(BluetoothGatt gatt) {
        try {
            refreshResult = false;
            Method localMethod = gatt.getClass().getMethod("refresh", (Class[]) null);
            if (localMethod != null) {
                int attempt = 0;
                while (!refreshResult && ++attempt <= 200) {
                    Log.d(TAG, "refresh attempt " + attempt);
                    refreshResult = (Boolean) localMethod.invoke(gatt, (Object[]) null);
                    if (!refreshResult) {
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException exp) {
                        }
                    }
                }
                return refreshResult;
            }
        } catch (Exception localException) {
            Log.e(TAG, "An exception occurred while refreshing device");
        }
        return false;
    }

    public boolean refreshSucceeded() {
        return refreshResult;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        BluetoothGatt gatt = mmDevice.connectGatt(context.get(), false, callback);
        BluetoothGattSingleton.setGatt(gatt);
        //refreshDeviceCache(gatt);
        return true;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
    }

    @Override
    protected void onProgressUpdate(BluetoothGatt... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onCancelled(Boolean aBoolean) {
        super.onCancelled(aBoolean);
        cancel();
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        cancel();
    }


}