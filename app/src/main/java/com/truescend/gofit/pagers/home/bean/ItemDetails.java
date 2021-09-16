package com.truescend.gofit.pagers.home.bean;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.truescend.gofit.R;
import com.truescend.gofit.pagers.common.bean.ItemBase;

import butterknife.BindView;

/**
 * 功能：详情横条布局
 * 适用于每个重要功能的详情布局
 * item_details.xml适配类，用于绑定布局控件，一个viewHolder
 * Author:Created by 泽鑫 on 2017/12/4 18:45.
 */

public class ItemDetails extends ItemBase implements View.OnClickListener {
    public static final int ITEM_TEXT_LOW = 0;
    public static final int ITEM_TEXT_HEIGHT = 1;

    @BindView(R.id.ivDetailsIconTitle)
    ImageView ivDetailsIconTitle;
    @BindView(R.id.tvDetailsTitle)
    TextView tvDetailsTitle;
    @BindView(R.id.ivDetailsIconRed)
    ImageView ivDetailsIconRed;
    @BindView(R.id.tvDetailsTextHeight)
    TextView tvDetailsTextHeight;
    @BindView(R.id.ivDetailsIconYellow)
    ImageView ivDetailsIconYellow;
    @BindView(R.id.tvDetailsTextLow)
    TextView tvDetailsTextLow;

    private OnTextClickListener textClickListener;

    /**
     * 绑定include进去的视图
     * @param view item视图
     */
    public ItemDetails(View view) {
        super(view);
        tvDetailsTextLow.setOnClickListener(this);
        tvDetailsTextHeight.setOnClickListener(this);
    }

    public void setIcon(int resId){
        ivDetailsIconTitle.setImageResource(resId);
    }

    public void setIcon(Drawable drawable){
        ivDetailsIconTitle.setImageDrawable(drawable);
    }

    public void setTitle(int resId){
        tvDetailsTitle.setText(resId);
    }

    public void setTitle(String title){
        tvDetailsTitle.setText(title);
    }

    public void setHeightText(int resId){
        tvDetailsTextHeight.setText(resId);
    }

    public void setHeightText(String text){
        tvDetailsTextHeight.setText(text);
    }

    public void setHeightTextVisibility(int visibility){
        tvDetailsTextHeight.setVisibility(visibility);
    }

    public void setLowText(int resId){
        tvDetailsTextLow.setText(resId);
    }

    public void setLowText(String text){
        tvDetailsTextLow.setText(text);
    }

    public void setLowTextVisibility(int visibility){
        tvDetailsTextLow.setVisibility(visibility);
    }

    public void setRedIcon(int resId){
        ivDetailsIconRed.setImageResource(resId);
    }

    public void setRedIcon(Drawable drawable){
        ivDetailsIconRed.setImageDrawable(drawable);
    }

    public void setRedIconVisibility(int visibility){
        ivDetailsIconRed.setVisibility(visibility);
    }

    public void setYellowIcon(int resId){
        ivDetailsIconYellow.setImageResource(resId);
    }

    public void setYellowIcon(Drawable drawable){
        ivDetailsIconYellow.setImageDrawable(drawable);
    }

    public void setYellowIconVisibility(int visibility){
        ivDetailsIconYellow.setVisibility(visibility);
    }

    public void setOnTextClickListener(OnTextClickListener clickListener){
        textClickListener = clickListener;
    }



    @Override
    public void onClick(View view) {
        if (textClickListener == null)return;
        switch (view.getId()){
            case R.id.tvDetailsTextLow:
                textClickListener.onTextClick(ITEM_TEXT_LOW);
                break;
            case R.id.tvDetailsTextHeight:
                textClickListener.onTextClick(ITEM_TEXT_HEIGHT)
                ;
                break;
        }
    }

    public interface OnTextClickListener{
        void onTextClick(int item);
    }
    
}

