package com.dz.blesdk.utils;

import java.util.concurrent.LinkedBlockingDeque;

/**
 * 作者:东芝(2018/6/30).
 * 功能:队列处理
 */

public abstract class LinkedBlockingDequeHelper<T> {
    private Thread thread;
    private final LinkedBlockingDeque<T> blockingDeque = new LinkedBlockingDeque<>();
    private boolean isRunning = true;


    protected abstract void onProcess(T t);

    private Thread createThread() {
        Thread thread = new Thread() {
            public void run() {
                isRunning = true;
                while (isRunning) {
                    try {
                        onProcess(take());
                    } catch (InterruptedException ignored) {
                    }
                }
                isRunning = false;
            }
        };
        thread.setPriority(Thread.MAX_PRIORITY);
        thread.start();
        return thread;
    }

    public void close() {
        clear();
        isRunning = false;
    }

    public void clear() {
        blockingDeque.clear();
    }

    public T take() throws InterruptedException {
        return blockingDeque.take();
    }

    public void offer(T t) {
        if (!isRunning || thread == null || !thread.isAlive()) {
            thread = createThread();
        }
        blockingDeque.offer(t);
    }
}
