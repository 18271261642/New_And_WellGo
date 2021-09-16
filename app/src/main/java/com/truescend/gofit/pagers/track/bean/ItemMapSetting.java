package com.truescend.gofit.pagers.track.bean;

import android.view.View;
import android.widget.TextView;

import com.truescend.gofit.R;

import java.lang.ref.WeakReference;


public class ItemMapSetting {
    private WeakReference<TextView> tvRunSettingTitle;

    public ItemMapSetting(View view) {
        tvRunSettingTitle = new WeakReference<>((TextView) view.findViewById(R.id.tvItemTitle));
    }

    public void setTitle(int resId) {
        TextView textView = tvRunSettingTitle.get();
        if (textView != null) {
            textView.setText(resId);
        }
    }

    public void setTitle(String title) {
        TextView textView = tvRunSettingTitle.get();
        if (textView != null) {
            textView.setText(title);
        }
    }

}
