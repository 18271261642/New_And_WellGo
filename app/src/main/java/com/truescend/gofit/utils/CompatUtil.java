package com.truescend.gofit.utils;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import androidx.core.content.FileProvider;

import java.io.File;

/**
 * 作者:东芝(2018/3/16).
 * 功能:兼容工具
 */

public class CompatUtil {
    /**
     * 兼容Android N 文件URI问题
     * @param context
     * @param file
     * @return
     */
    public static Uri getUriForFile(Context context, File file){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return FileProvider.getUriForFile(context, context.getPackageName()+".fileprovider", file);
        } else {
            return Uri.fromFile(file);
        }
    }
}
