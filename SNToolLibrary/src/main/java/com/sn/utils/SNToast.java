package com.sn.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.Toast;

import java.lang.ref.WeakReference;

/**
 * 作者:东芝(2017/7/31).
 * 功能:全局快速Toast 无上下文参数 不需要考虑是否子线程
 */
public class SNToast {
    private static final String MAIN = "main";
    private static WeakReference<Context> context;
    private static Handler mHandler;

    public static void init(Context context) {
        SNToast.context = new WeakReference< >(context.getApplicationContext());
        mHandler = new Handler(Looper.getMainLooper());
    }

    public static void toast(String text) {
        if (TextUtils.isEmpty(text)) {
            return;
        }
        toast(text, Toast.LENGTH_SHORT);
    }

    public static void toast(int textRes) {
        toast(textRes, Toast.LENGTH_SHORT);
    }

    public static void toast(int textRes, int duration) {
        toast(context.get().getResources().getString(textRes), duration);
    }

    public static void toast(final String text, final int duration) {
        if (Thread.currentThread().getName().equalsIgnoreCase(MAIN)) {//主线程
            Toast.makeText(context.get(), text, duration).show();
        } else {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context.get(), text, duration).show();
                }
            });
        }

    }
    public static void toast(final Drawable image) {
        if (Thread.currentThread().getName().equalsIgnoreCase(MAIN)) {//主线程
            Toast toast = Toast.makeText(context.get(), "", Toast.LENGTH_SHORT);
            ImageView view = new ImageView(context.get());
            view.setImageDrawable(image);
            toast.setView(view);
            toast.show();
        } else {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast toast = Toast.makeText(context.get(), "", Toast.LENGTH_SHORT);
                    ImageView view = new ImageView(context.get());
                    view.setImageDrawable(image);
                    toast.setView(view);
                    toast.show();
                }
            });
        }

    }

}
