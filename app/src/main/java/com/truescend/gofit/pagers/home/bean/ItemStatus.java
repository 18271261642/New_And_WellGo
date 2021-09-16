package com.truescend.gofit.pagers.home.bean;

import androidx.annotation.StringRes;
import android.view.View;
import android.widget.TextView;

import com.truescend.gofit.R;
import com.truescend.gofit.pagers.common.bean.ItemBase;

import butterknife.BindView;

/**
 * 功能：状态布局
 * 适用于简单显示某种状态的数值布局
 * item_home_status.xml适配类，用于绑定布局控件，一个viewHolder
 * Author:Created by 泽鑫 on 2017/12/5 10:48.
 * 东芝:全部修改 , 简化
 */

public class ItemStatus extends ItemBase {

    @BindView(R.id.tvItemStatusTitle)
    TextView tvItemStatusTitle;


    @BindView(R.id.tvItemStatusSubTitle)
    TextView tvItemStatusSubTitle;


    public ItemStatus(View view) {
        super(view);
    }

    public void setTitle(CharSequence title) {
        tvItemStatusTitle.setText(title);
    }
    public void setTitle(@StringRes int id) {
        tvItemStatusTitle.setText(id);
    }
    public void setSubTitle(CharSequence title) {
        tvItemStatusSubTitle.setText(title);
    }
    public void setSubTitle(@StringRes int id) {
        tvItemStatusSubTitle.setText(id);
    }
}
