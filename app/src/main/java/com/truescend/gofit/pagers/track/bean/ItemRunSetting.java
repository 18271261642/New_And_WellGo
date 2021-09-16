package com.truescend.gofit.pagers.track.bean;

import androidx.annotation.Nullable;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.truescend.gofit.R;

import java.lang.ref.WeakReference;

/**
 * 功能：跑道设置界面横条按钮布局
 * 一个ViewHolder,适配item_run_setting.xml布局文件
 * Author:Created by 泽鑫 on 2017/12/13 18:53.
 */

public class ItemRunSetting {
    WeakReference<TextView> tvRunSettingTitle;
    WeakReference<CheckBox> cbRunSettingSwitch;

    public ItemRunSetting(View view) {
        tvRunSettingTitle = new WeakReference<>((TextView) view.findViewById(R.id.tvRunSettingTitle));
        cbRunSettingSwitch = new WeakReference<>((CheckBox) view.findViewById(R.id.cbRunSettingSwitch));
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

    public void setChecked(boolean checked) {
        CheckBox checkBox = cbRunSettingSwitch.get();
        if (checkBox != null) {
            checkBox.setChecked(checked);
        }
    }

    public boolean isChecked() {
        CheckBox checkBox = cbRunSettingSwitch.get();
        if (checkBox != null) {
            return checkBox.isChecked();
        }
        return false;
    }

    public void setOnCheckedChangeListener(@Nullable CompoundButton.OnCheckedChangeListener listener) {
        CheckBox checkBox = cbRunSettingSwitch.get();
        if (checkBox != null) {
            checkBox.setOnCheckedChangeListener(listener);
        }
    }
}
