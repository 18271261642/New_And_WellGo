package com.truescend.gofit.pagers.device.bean;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.truescend.gofit.R;
import com.truescend.gofit.pagers.common.bean.ItemBase;

import butterknife.BindView;

/**
 * 功能：功能选项按钮
 * 适用于设备页面的各种功能按钮
 * 设备界面选项适配类，适配item_device_icon.xml
 * Author:Created by 泽鑫 on 2017/11/21 11:10.
 */

public class ItemDeviceIcon extends ItemBase{

    @BindView(R.id.ivDeviceIconImage)
    ImageView ivDeviceIconImage;
    @BindView(R.id.tvDeviceIconTitle)
    TextView tvDeviceIconTitle;

    public ItemDeviceIcon(View view) {
        super(view);
    }

    public void setIcon(int resId){
        ivDeviceIconImage.setImageResource(resId);
    }

    public void setIcon(Drawable drawable){
        ivDeviceIconImage.setImageDrawable(drawable);
    }

    public void setTitle(int resId){
        tvDeviceIconTitle.setText(resId);
    }

    public void setTitle(String title){
        tvDeviceIconTitle.setText(title);
    }

    public void setTitleVisibility(int visibility){
        tvDeviceIconTitle.setVisibility(visibility);
    }
}
