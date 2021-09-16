package com.truescend.gofit.pagers.home.bean;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.truescend.gofit.R;
import com.truescend.gofit.pagers.common.bean.ItemBase;

import butterknife.BindView;

/**
 * 功能：睡眠详情布局
 * 适用于要使用进度条显示信息的页面
 * item_sleep_details.xml 适配类，用于绑定布局控件，一个viewHolder
 * Author:Created by 泽鑫 on 2017/12/5 10:40.
 */

public class ItemSleepDetails extends ItemBase{

    @BindView(R.id.tvSleepDetailsTitle)
    TextView tvSleepDetailsTitle;
    @BindView(R.id.pbSleepDetails)
    ProgressBar pbSleepDetails;
    @BindView(R.id.tvSleepDetailsPercent)
    TextView tvSleepDetailsPercent;
    @BindView(R.id.tvSleepDetailsTime)
    TextView tvSleepDetailsTime;

    public ItemSleepDetails(View view) {
        super(view);
    }


    public void setTitle(int resId){
        tvSleepDetailsTitle.setText(resId);
    }

    public void setTitle(String title){
        tvSleepDetailsTitle.setText(title);
    }

    public void setProgressDrawable(Drawable drawable){
        pbSleepDetails.setProgressDrawable(drawable);
    }

    public void setProgress(int progress){
        pbSleepDetails.setProgress(progress);
    }

    public void setPercent(int resId){
        tvSleepDetailsPercent.setText(resId);
    }

    public void setPercent(String percent){
        tvSleepDetailsPercent.setText(percent);
    }

    public void setTime(int resId){
        tvSleepDetailsTime.setText(resId);
    }

    public void setTime(String time){
        tvSleepDetailsTime.setText(time);
    }
}
