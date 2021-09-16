package com.truescend.gofit.pagers.device.bean;

import android.graphics.Bitmap;
import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.truescend.gofit.R;

import java.lang.ref.WeakReference;

public class ItemWallpaperSetting {
    private WeakReference<ImageView> ivWallpaperItemIcon;
    private WeakReference<TextView> tvWallpaperSettingTitle;
    private WeakReference<CheckBox> cbWallpaperSettingSwitch;

    public ItemWallpaperSetting(View view) {
        ivWallpaperItemIcon = new WeakReference<>((ImageView) view.findViewById(R.id.ivWallpaperItemIcon));
        tvWallpaperSettingTitle = new WeakReference<>((TextView) view.findViewById(R.id.tvWallpaperSettingTitle));
        cbWallpaperSettingSwitch = new WeakReference<>((CheckBox) view.findViewById(R.id.cbWallpaperSettingSwitch));
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setChecked(!isChecked());
            }
        });
    }

    public void setIcon(@DrawableRes int resId) {
        ImageView iv = ivWallpaperItemIcon.get();
        if (iv != null) {
            iv.setImageResource(resId);
        }
    }

    public void setIcon(Bitmap icon) {
        ImageView iv = ivWallpaperItemIcon.get();
        if (iv != null) {
            iv.setImageBitmap(icon);
        }
    }

    public void setTitle(int resId) {
        TextView textView = tvWallpaperSettingTitle.get();
        if (textView != null) {
            textView.setText(resId);
        }
    }

    public void setTitle(String title) {
        TextView textView = tvWallpaperSettingTitle.get();
        if (textView != null) {
            textView.setText(title);
        }
    }

    public void setChecked(boolean checked) {
        CheckBox checkBox = cbWallpaperSettingSwitch.get();
        if (checkBox != null) {
            checkBox.setChecked(checked);
        }
    }

    public boolean isChecked() {
        CheckBox checkBox = cbWallpaperSettingSwitch.get();
        if (checkBox != null) {
            return checkBox.isChecked();
        }
        return false;
    }

    public void setOnCheckedChangeListener(@Nullable CompoundButton.OnCheckedChangeListener listener) {
        CheckBox checkBox = cbWallpaperSettingSwitch.get();
        if (checkBox != null) {
            checkBox.setOnCheckedChangeListener(listener);
        }
    }
}
