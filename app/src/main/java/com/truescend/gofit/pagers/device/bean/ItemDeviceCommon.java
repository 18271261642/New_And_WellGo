package com.truescend.gofit.pagers.device.bean;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.truescend.gofit.R;
import com.truescend.gofit.pagers.common.bean.ItemBase;

import butterknife.BindView;

/**
 * 功能：设置通用横条布局
 * 适用于复杂功能的横条
 * 适配item_device_common.xml布局，类似ViewHolder
 * Author:Created by 泽鑫 on 2017/12/16 18:34.
 */

public class ItemDeviceCommon extends ItemBase {
    @BindView(R.id.tvDeviceCommonTitle)
    TextView tvDeviceCommonTitle;
    @BindView(R.id.tvDeviceCommonTime)
    TextView tvDeviceCommonTime;
    @BindView(R.id.tvDeviceCommonIntervalTime)
    TextView tvDeviceCommonIntervalTime;
    @BindView(R.id.ivDeviceCommonSetting)
    ImageView ivDeviceCommonSetting;
    @BindView(R.id.cbDeviceCommonSwitch)
    CheckBox cbDeviceCommonSwitch;

    public ItemDeviceCommon(View view) {
        super(view);
    }

    public void setTitle(int resId) {
        tvDeviceCommonTitle.setText(resId);
    }

    public void setTitle(String title) {
        tvDeviceCommonTitle.setText(title);
    }

    public void setTitleColor(int resId) {
        tvDeviceCommonTitle.setTextColor(resId);
    }

    public void setTitleVisibility(int visibility) {
        tvDeviceCommonTitle.setVisibility(visibility);
    }

    public void setTime(int resId) {
        tvDeviceCommonTime.setText(resId);
    }

    public void setTime(String time) {
        tvDeviceCommonTime.setText(time);
    }

    public void setTimeVisibility(int visibility) {
        tvDeviceCommonTime.setVisibility(visibility);
    }

    public void setIntervalTime(int resId) {
        tvDeviceCommonIntervalTime.setText(resId);
    }

    public void setIntervalTime(String intervalTime) {
        tvDeviceCommonIntervalTime.setText(intervalTime);
    }

    public void setIntervalTimeVisibility(int visibility) {
        tvDeviceCommonIntervalTime.setVisibility(visibility);
    }

    public void setSettingIcon(int resId) {
        ivDeviceCommonSetting.setImageResource(resId);
    }

    public void setSettingIcon(Drawable drawable) {
        ivDeviceCommonSetting.setImageDrawable(drawable);
    }

    public void setSettingIconVisibility(int visibility) {
        ivDeviceCommonSetting.setVisibility(visibility);
    }

    public void setSettingIconOnClickListener(View.OnClickListener listener) {
        ivDeviceCommonSetting.setOnClickListener(listener);
    }

    public void setSwitchCheck(boolean check) {
        cbDeviceCommonSwitch.setChecked(check);
    }

    public void setSwitchVisibility(int visibility) {
        cbDeviceCommonSwitch.setVisibility(visibility);
    }

    public void setSwitchOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener listener) {
        cbDeviceCommonSwitch.setOnCheckedChangeListener(listener);
    }

    public void setItemClickable(boolean clickable) {
        ivDeviceCommonSetting.setClickable(clickable);
        ivDeviceCommonSetting.setEnabled(clickable);
        cbDeviceCommonSwitch.setClickable(clickable);
        cbDeviceCommonSwitch.setEnabled(clickable);
    }
}
