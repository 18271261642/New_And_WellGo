package com.truescend.gofit.pagers.user.bean;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.truescend.gofit.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 功能：功能卡片布局
 * 适用于设置fragment
 * Author:Created by 泽鑫 on 2018/1/25 15:57.
 */

public class ItemUserFile {
    @BindView(R.id.tvUserFileTitle)
    TextView tvUserFileTitle;
    @BindView(R.id.ivUserFileImage)
    ImageView ivUserFileImage;
    @BindView(R.id.tvUserFileData)
    TextView tvUserFileData;
    @BindView(R.id.tvUserFileUnit)
    TextView tvUserFileUnit;
    @BindView(R.id.tvUserFileDate)
    TextView tvUserFileDate;

    public ItemUserFile(View view) {
        ButterKnife.bind(this, view);
    }

    public void setTitle(int resId){
        tvUserFileTitle.setText(resId);
    }

    public void setTitle(String title){
        tvUserFileTitle.setText(title);
    }

    public void setImage(int resId){
        ivUserFileImage.setImageResource(resId);
    }

    public void setImage(Drawable drawable){
        ivUserFileImage.setImageDrawable(drawable);
    }

    public void setData(int resId){
        tvUserFileData.setText(resId);
    }

    public void setData(String data){
        tvUserFileData.setText(data);
    }

    public void setUnit(int resId){
        tvUserFileUnit.setText(resId);
    }

    public void setUnit(String unit){
        tvUserFileUnit.setText(unit);
    }

    public void setDate(int resId){
        tvUserFileDate.setText(resId);
    }

    public void setDate(String date){
        tvUserFileDate.setText(date);
    }
}
