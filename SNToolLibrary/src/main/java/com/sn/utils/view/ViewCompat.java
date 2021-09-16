package com.sn.utils.view;

import android.view.View;
import android.widget.TextView;

/**
 * 作者:东芝(2018/1/17).
 * 功能:View(兼容)工具
 */

public class ViewCompat extends androidx.core.view.ViewCompat {


    public static String getText(TextView v) {
        return getText(v, false);
    }

    public static String getText(TextView v, boolean trim) {
        CharSequence text = v.getText();
        if (text != null) {
            return trim ? text.toString().trim() : text.toString();
        } else {
            return "";
        }
    }


    public static int[] getMeasuredSize(View v) {
        int width = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        int height = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        v.measure(width, height);
        return new int[]{v.getMeasuredWidth(), v.getMeasuredHeight()};
    }
}
