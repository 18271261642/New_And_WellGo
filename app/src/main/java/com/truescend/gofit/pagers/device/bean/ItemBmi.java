package com.truescend.gofit.pagers.device.bean;

import android.view.View;
import android.widget.TextView;

import com.truescend.gofit.R;

/**
 * BMI指数
 * Author:Created by 泽鑫 on 2018/3/29 11:12.
 */

public class ItemBmi implements View.OnClickListener {
    private TextView tvBMITitle;
    private TextView tvBMITips;
    private TextView tvBMIContent;

    private OnTipsClickListener listener;

    public ItemBmi(View view) {
        tvBMITitle = (TextView) view.findViewById(R.id.tvBMITitle);
        tvBMITips = (TextView) view.findViewById(R.id.tvBMITips);
        tvBMIContent = (TextView) view.findViewById(R.id.tvBMIContent);
        tvBMITips.setOnClickListener(this);
    }

    public void setTitle(int resId){
        tvBMITitle.setText(resId);
    }

    public void setTitle(String title){
        tvBMITitle.setText(title);
    }

    public void setTips(int resId){
        tvBMITips.setText(resId);
    }

    public void setTips(String tips){
        tvBMITips.setText(tips);
    }

    public void setContent(int resId){
        tvBMIContent.setText(resId);
    }

    public void setContent(String content){
        tvBMIContent.setText(content);
    }

    public void setContent(CharSequence content){
        tvBMIContent.setText(content);
    }

    public void setTipsOnClickListener(OnTipsClickListener listener){
        this.listener = listener;
    }

    @Override
    public void onClick(View view) {
        if (listener == null)return;
        listener.onTipsClick(view);
    }

    public interface OnTipsClickListener{
        void onTipsClick(View view);
    }
}
