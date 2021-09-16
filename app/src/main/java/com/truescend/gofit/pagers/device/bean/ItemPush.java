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
 * 功能：消息推送按钮布局
 * 适用于各种app消息推送按钮
 * 适配item_push.xml布局，一个ViewHolder
 * Author:Created by 泽鑫 on 2018/1/2 10:36.
 */

public class ItemPush extends ItemBase{
    @BindView(R.id.ivPushItemIcon)
    ImageView ivPushItemIcon;
    @BindView(R.id.tvPushItemTitle)
    TextView tvPushItemTitle;
    @BindView(R.id.cbPushItemSwitch)
    CheckBox cbPushItemSwitch;

    public ItemPush(View view) {
        super(view);
    }

    public void setIcon(int resId){
        ivPushItemIcon.setImageResource(resId);
    }

    public void setIcon(Drawable drawable){
        ivPushItemIcon.setImageDrawable(drawable);
    }

    public ImageView getIcon(){
        return ivPushItemIcon;
    }

    public void setTitle(int resId){
        tvPushItemTitle.setText(resId);
    }

    public void setTitle(String title){
        tvPushItemTitle.setText(title);
    }

    public void setSwitchChecked(boolean checked){
        cbPushItemSwitch.setChecked(checked);
    }
    public void setSwitchTag(int tag){
        cbPushItemSwitch.setTag(tag);
    }

    public void setSwitchOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener listener){
        cbPushItemSwitch.setOnCheckedChangeListener(listener);
    }
}
