package com.dz.blesdk.interfaces;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattService;

import java.util.List;
import java.util.UUID;

/**
 * 作者:东芝(2017/11/20).
 * 功能:
 */

public interface IBleHelper {
    /**
     * 增加一个连接监听
     * @param listener
     */
    void addConnectListener(ConnectListener listener);

    /**
     * 移除一个连接监听
     * @param listener
     */
    void removeConnectListener(ConnectListener listener);

    /**
     * 通常传入
     * {@link com.dz.blesdk.interfaces.ReadListener}
     * {@link com.dz.blesdk.interfaces.WriteListener}
     * {@link NotifyReceiverListener}
     * {@link com.dz.blesdk.interfaces.RssiListener}
     *
     * @param listener
     */
    void addCommunicationListener(CommunicationListener listener);

    /**
     * 移除
     * @param listener
     */
    void removeCommunicationListener(CommunicationListener listener);


    /**
     * 连接设备
     *
     * @param address
     * @return
     */
    boolean connect(String address);

    /**
     * 断开当前设备
     */
    void disconnect();
    void close();


    /**
     * 设置连接超时时间
     *
     * @param connectTimeOut
     */
    void setConnectTimeOut(long connectTimeOut);

    /**
     * 设置通知超时时间
     * @param notifyEnableTimeOut
     */
    void setNotifyEnableTimeOut(long notifyEnableTimeOut);



    /**
     * 设置发现服务超时时间
     *
     * @param discoverServicesTimeOut
     */
    void setDiscoverServicesTimeOut(long discoverServicesTimeOut);

    /**
     * 向设备请求通知开启
     *
     * @param mGattServiceUUID
     * @param mGattCharacteristicUUID
     * @param mGattDescriptor
     * @param enable
     * @return
     */
    boolean setNotifyEnable(UUID mGattServiceUUID,
                            UUID mGattCharacteristicUUID, UUID mGattDescriptor, boolean enable);

    /**
     * 向设备发送指令
     */
    boolean write(byte[] data, UUID mGattServiceUUID, UUID mGattCharacteristicUUID);

    /**
     * 向设备发送读取指令
     */
    boolean read(byte[] data, UUID mGattServiceUUID, UUID mGattCharacteristicUUID);

    /**
     * 读信号
     * 先设置回调{@link RssiListener} 再调用该方法
     *
     * @return
     */
    boolean readRemoteRssi();

    /**
     * @return supported services.
     * 得到列表支持 GATT 服务列表 。这方法应该在调用 BluetoothGatt.discoverServices() 之后调用
     */
    List<BluetoothGattService> getSupportedGattServices();

    BluetoothGatt getBluetoothGatt();


    boolean isConnected();

    boolean isConnecting();

    boolean isDisconnected();

    boolean isDisconnectException();

}
