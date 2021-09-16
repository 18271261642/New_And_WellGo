package com.sn.blesdk.interfaces;

/**
 * 作者:东芝(2019/3/20).
 * 功能:
 */

public interface OnWallpaperUploadListener {
    void onProgress(int max, int progress, int total, int current);
    void onSuccess(long spendTime);
    void onFailed(Exception e);
}

