package com.sn.utils.net;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;

import android.view.View;
import android.view.ViewGroup;

import java.io.File;

import androidx.core.content.FileProvider;

/**
 * 作者:东芝(2017/8/24).
 * 描述:原生分享
 */
public class ShareUtil {
    /**
     * 分享文字内容
     *
     * @param title   分享对话框标题
     * @param subject 主题
     * @param content 分享内容（文字）
     */
    public static void shareText(Context context, String title, String subject, String content) {
        if (content == null || "".equals(content)) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        if (subject != null && !"".equals(subject)) {
            intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        }

        intent.putExtra(Intent.EXTRA_TEXT, content);

        // 设置弹出框标题
        if (title != null && !"".equals(title)) { // 自定义标题
            context.startActivity(Intent.createChooser(intent, title));
        } else { // 系统默认标题
            context.startActivity(intent);
        }
    }

    /**
     * 分享图片和文字内容
     *
     * @param title   分享对话框标题
     * @param subject 主题
     * @param content 分享内容（文字）
     * @param path    图片资源path
     */
    public static void shareImage(Context context, String title, String subject, String content, String path) {
        shareImage(context, title, subject, content, new File(path) );
    }

    /**
     * 分享图片和文字内容
     *
     * @param title   分享对话框标题
     * @param subject 主题
     * @param content 分享内容（文字）
     * @param file    图片资源file
     */
    public static void shareImage(Context context, String title, String subject, String content, File file) {
        shareImage(context, title, subject, content,getUriCompat(context,file));
    }

    private static void shareImage(Context context, String title, String subject, String content, Uri uri) {
        if (uri == null) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        if (subject != null && !"".equals(subject)) {
            intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        }
        if (content != null && !"".equals(content)) {
            intent.putExtra(Intent.EXTRA_TEXT, content);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // 设置弹出框标题
        if (title != null && !"".equals(title)) { // 自定义标题
            context.startActivity(Intent.createChooser(intent, title));
        } else { // 系统默认标题
            context.startActivity(intent);
        }
    }

    /**
     * 获取当前屏幕截图，不包含状态栏
     *
     * @param activity
     * @return
     */
    public static Bitmap capture(Activity activity)
    {
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bmp = view.getDrawingCache();
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int top = frame.top;
        int width = frame.width();
        int height = frame.height();
        Bitmap bp = Bitmap.createBitmap(bmp, 0, top, width, height);
        view.destroyDrawingCache();
        return bp;

    }
    public static Bitmap capture(Activity activity, ViewGroup mScrollView)
    {
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int width = frame.width();
        int height =  mScrollView.getChildAt(0).getHeight();
        Bitmap bp = Bitmap.createBitmap( width, height,Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bp);
        Paint paint = new Paint();
        paint.setColor(0xFFFFFFFF);
        paint.setAntiAlias(true);
        canvas.drawRect(0,0, width, height, paint);
        mScrollView.draw(canvas);

        return bp;

    }


    public static Uri getUriCompat(Context context, File file) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", file);
        } else {
            return Uri.fromFile(file);
        }
    }

}
