package com.truescend.gofit.utils;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;

/**
 * 作者:东芝(2017/10/17).
 * 功能:拍照修复
 */

public class CameraFixUtil {
    /**
     * 该函数在某些情况下会出现闪退
     *
     * @param mCamera
     * @param shutter
     * @param raw
     * @param jpeg
     */
    public static void takePicture(Camera mCamera, Camera.ShutterCallback shutter, Camera.PictureCallback raw, Camera.PictureCallback jpeg) {
        try {
            mCamera.takePicture(shutter, raw, jpeg);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static void notifyImage(Context context, File pictureFile) {
        if (pictureFile == null) return;
        // 其次把文件插入到系统图库
        try {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DATA, pictureFile.getAbsolutePath());
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            Uri uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            // 最后通知图库更新
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
        } catch (Exception e) {
            // 最后通知图库更新
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, CompatUtil.getUriForFile(context, pictureFile)));
        }

    }
}
