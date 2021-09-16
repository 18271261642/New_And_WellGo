package com.truescend.gofit.anim;

import android.view.View;

import com.truescend.gofit.views.QuickViewPager;

/**
 * 作者:东芝(2018/8/17).
 * 功能:ViewPager缩放效果
 */

public class ScalePageTransformer implements QuickViewPager.PageTransformer {
    private float MIN_SCALE = 0.83f;

    public ScalePageTransformer(float min_scale) {
        MIN_SCALE = min_scale;
    }

    @Override
    public void transformPage(View page, float position) {
        float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
        float rotate = 0;//10 * Math.abs(position);
        //position小于等于1的时候，代表page已经位于中心item的最左边，
        //此时设置为最小的缩放率以及最大的旋转度数
        if (position <= -1) {
            page.setScaleX(MIN_SCALE);
            page.setScaleY(MIN_SCALE);
            page.setRotationY(rotate);
        } else if (position < 0) {//position从0变化到-1，page逐渐向左滑动
            page.setScaleX(scaleFactor);
            page.setScaleY(scaleFactor);
            page.setRotationY(rotate);
        } else if (position >= 0 && position < 1) {//position从0变化到1，page逐渐向右滑动
            page.setScaleX(scaleFactor);
            page.setScaleY(scaleFactor);
            page.setRotationY(-rotate);
        } else if (position >= 1) {//position大于等于1的时候，代表page已经位于中心item的最右边
            page.setScaleX(scaleFactor);
            page.setScaleY(scaleFactor);
            page.setRotationY(-rotate);
        }
    }


}