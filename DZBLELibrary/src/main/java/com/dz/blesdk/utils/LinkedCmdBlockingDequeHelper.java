package com.dz.blesdk.utils;

import com.dz.blesdk.ble.BleHelper;
import com.dz.blesdk.entity.BLEWriteOrRead;
import com.dz.blesdk.interfaces.LinkedCmdBlockingQueueCallBack;

import java.util.UUID;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * 作者:东芝(2017/11/25).
 * 功能:多线程命令发送同步工具
 * 为了解决快速发送命令导致失败的问题
 */

public class LinkedCmdBlockingDequeHelper {


    public static   int MILLIS = 3;
    private final LinkedBlockingDeque<BLEWriteOrRead> blockingDeque = new LinkedBlockingDeque<>();
    private Thread thread;
    private LinkedCmdBlockingQueueCallBack callBack;

    public LinkedCmdBlockingDequeHelper(final LinkedCmdBlockingQueueCallBack callBack) {
        this.callBack = callBack;
        thread = createThread();
        thread.setPriority(Thread.MAX_PRIORITY);
        thread.start();
    }

    private Thread createThread() {
        Thread thread = new Thread() {
            public void run() {
                while (true) {
                    try {
                        if (callBack == null) {
                            Thread.sleep(1000);
                            continue;
                        }
                        Thread.sleep(MILLIS);
                        BLEWriteOrRead take = blockingDeque.take();
                        if (take.reTryCount < 100000) {
                            if (!callBack.onTake(take)) {
                                Thread.sleep(50);
                                take.reTryCount++;
                                blockingDeque.putFirst(take);
//                                BLELog.w("send status = retry");
                                if (BleHelper.getInstance().isDisconnected()) {
                                    blockingDeque.clear();
                                }
                            } else {
                                take.reTryCount = 0;
                            }
                        } else {
//                            BLELog.w("send status =" + false);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        thread.setPriority(Thread.MAX_PRIORITY);
        return thread;
    }

    public void commit(byte[] data, UUID mGattServiceUUID, UUID mGattCharacteristicUUID, boolean isWrite) {
//        BLELog.d("发送前:" + BaseBleDataHelper.toHexString(data));
        if (thread == null ||thread.getState()==Thread.State.TERMINATED) {
            thread = createThread();
            thread.start();
        }
        final BLEWriteOrRead obj = new BLEWriteOrRead(data, mGattServiceUUID, mGattCharacteristicUUID, isWrite);
        blockingDeque.offer(obj);
//        System.err.println(Thread.currentThread().getName() + "已经放了数据 size="   + blockingDeque.size() );
    }

}
