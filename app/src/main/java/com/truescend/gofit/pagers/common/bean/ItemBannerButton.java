package com.truescend.gofit.pagers.common.bean;

import android.graphics.drawable.Drawable;
import androidx.annotation.ColorRes;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;



import com.truescend.gofit.R;

import butterknife.BindView;

/**
 * 功能：横条按钮布局
 * 适配item_banner_button.xml布局，类似ViewHolder
 * Author:Created by 泽鑫 on 2017/12/16 18:34.
 */

public class ItemBannerButton extends ItemBase {
    @BindView(R.id.tvBannerButtonTitle)
    TextView tvBannerButtonTitle;
    @BindView(R.id.tvBannerButtonContent)
    TextView tvBannerButtonContent;
    @BindView(R.id.ivBannerButtonIcon)
    ImageView ivBannerButtonIcon;
    @BindView(R.id.ivBannerButtonTitleIcon)
    ImageView ivBannerButtonTitleIcon;
    private View view;

    public ItemBannerButton(View view) {
        super(view);
        this.view = view;
    }

    public void setBackgroundColor(@ColorRes int colorResId){
        view.setBackgroundColor(view.getResources().getColor(colorResId));
    }

    /**
     * 设置标题
     *
     * @param title 标题
     */
    public void setTitle(String title) {
        tvBannerButtonTitle.setText(title);
    }

    /**
     * 设置标题
     *
     * @param resId 标题
     */
    public void setTitle(int resId) {
        tvBannerButtonTitle.setText(resId);
    }


    /**
     * 设置标题文字颜色
     *
     * @param colorResId 颜色
     */
    public void setTitleColor(@ColorRes int colorResId) {
        tvBannerButtonTitle.setTextColor(tvBannerButtonTitle.getResources().getColor(colorResId));
    }

    /**
     * 设置标题是否显示
     *
     * @param visibility 可见
     */
    public void setTitleVisibility(int visibility) {
        tvBannerButtonTitle.setVisibility(visibility);
    }
    /**
     * 设置图片
     *
     * @param resId 图片
     */
    public void setTitleIcon(int resId) {
        ivBannerButtonTitleIcon.setImageResource(resId);
        setTitleIconVisibility(View.VISIBLE);
    }

    /**
     * 设置图片
     *
     * @param drawable 图片
     */
    public void setTitleIcon(Drawable drawable) {
        ivBannerButtonTitleIcon.setImageDrawable(drawable);
        setTitleIconVisibility(View.VISIBLE);
    }
    /**
     * 设置标题是否显示
     *
     * @param visibility 可见
     */
    public void setTitleIconVisibility(int visibility) {
        ivBannerButtonTitleIcon.setVisibility(visibility);
    }

    /**
     * 设置内容
     *
     * @param content 内容
     */
    public void setContent(String content) {
        tvBannerButtonContent.setText(content);
    }

    /**
     * 设置内容
     *
     * @param resId 内容
     */
    public void setContent(int resId) {
        tvBannerButtonContent.setText(resId);
    }

    /**
     * 设置内容文字颜色
     *
     * @param resId 颜色
     */
    public void setContentColor(int resId) {
        tvBannerButtonContent.setTextColor(resId);
    }

    /**
     * 设置内容是否显示
     *
     * @param visibility 可见
     */
    public void setContentVisibility(int visibility) {
        tvBannerButtonContent.setVisibility(visibility);
    }

    /**
     * 设置图片
     *
     * @param resId 图片
     */
    public void setIcon(int resId) {
        ivBannerButtonIcon.setImageResource(resId);
    }

    /**
     * 设置图片
     *
     * @param drawable 图片
     */
    public void setIcon(Drawable drawable) {
        ivBannerButtonIcon.setImageDrawable(drawable);
    }

    /**
     * 设置图片是否显示
     *
     * @param visibility 可见
     */
    public void setIconVisibility(int visibility) {
        ivBannerButtonIcon.setVisibility(visibility);
    }
}
