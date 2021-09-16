package com.truescend.gofit.pagers.home.bean;

import androidx.annotation.StringRes;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.truescend.gofit.R;
import com.truescend.gofit.pagers.common.bean.ItemBase;

import butterknife.BindView;

/**
 * 功能：卡片布局
 * 适用于主界面各种重要功能布局
 * item_home_card_view.xml适配类，用于绑定布局控件，一个viewHolder
 * Author:Created by 泽鑫 on 2017/11/30 10:28.
 * 东芝:全改,简化使用
 */
public class ItemCardView extends ItemBase {
    @BindView(R.id.ivHomeCardPicture)
    ImageView ivHomeCardPicture;
    @BindView(R.id.tvHomeCardTitle)
    TextView tvHomeCardTitle;
    @BindView(R.id.tvHomeCardType)
    TextView tvHomeCardType;
    @BindView(R.id.tvHomeCardSubTitle)
    TextView tvHomeCardSubTitle;

    public ItemCardView(View view) {
        super(view);
    }

    public void setPicture(int resId) {
        ivHomeCardPicture.setImageResource(resId);
    }

    public void setTitle(@StringRes int id) {
        tvHomeCardTitle.setText(id);
    }

    public void setTitle(CharSequence title) {
        tvHomeCardTitle.setText(title);
    }

    public void setSubTitle(@StringRes int id) {
        tvHomeCardSubTitle.setText(id);
    }

    public void setSubTitle(CharSequence title) {
        tvHomeCardSubTitle.setText(title);
    }

    public void setType(int resId) {
        tvHomeCardType.setText(resId);
    }

    public void setType(CharSequence type) {
        tvHomeCardType.setText(type);
    }


}
