package com.truescend.gofit.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;

import android.text.Html;

import com.truescend.gofit.App;

import java.util.Locale;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

/**
 * 作者:东芝(2018/3/17).
 * 功能:资源工具
 */

public class ResUtil {

    public static Context getContext() {
        return App.getContext().getApplicationContext();
    }

    public static Resources getResources() {
        return getContext().getResources();
    }

    public static int getColor(@ColorRes int id) {
        return getResources().getColor(id);
    }

    public static String getString(@StringRes int id) {
        return getResources().getString(id);
    }

    public static Drawable getDrawable(@DrawableRes int id) {
        return getResources().getDrawable(id);
    }

    public static Bitmap getBitmap(@DrawableRes int id) {
        return BitmapFactory.decodeResource(getResources(), id);
    }

    public static String format(String format, Object... args) {
        return String.format(format, args);
    }
    public static String format(Locale l,String format, Object... args) {
        return String.format(l,format, args);
    }

    public static CharSequence formatHtml(String format, Object... args) {
        return html(String.format(format, args));
    }

    public static CharSequence html(String html) {
        return Html.fromHtml(html);
    }
}
