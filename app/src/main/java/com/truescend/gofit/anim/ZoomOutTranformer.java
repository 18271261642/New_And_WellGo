package com.truescend.gofit.anim;


import android.view.View;

import androidx.viewpager.widget.ViewPager;

/**
 * 作者:东芝(2018/3/17).
 * 功能:
 */

public class ZoomOutTranformer implements ViewPager.PageTransformer  {

    /**
     * If the position offset of a fragment is less than negative one or greater than one, returning true will set the
     * fragment alpha to 0f. Otherwise fragment alpha is always defaulted to 1f.
     *
     * @return
     */
    protected boolean hideOffscreenPages() {
        return true;
    }

    /**
     * Indicates if the default animations of the view pager should be used.
     *
     * @return
     */
    protected boolean isPagingEnabled() {
        return false;
    }
    protected void onPreTransform(View page, float position) {
        final float width = page.getWidth();

        page.setRotationX(0);
        page.setRotationY(0);
        page.setRotation(0);
        page.setScaleX(1);
        page.setScaleY(1);
        page.setPivotX(0);
        page.setPivotY(0);
        page.setTranslationY(0);
        page.setTranslationX(isPagingEnabled() ? 0f : -width * position);

        if (hideOffscreenPages()) {
            page.setAlpha(position <= -1f || position >= 1f ? 0f : 1f);
//			page.setEnabled(false);
        } else {
//			page.setEnabled(true);
            page.setAlpha(1f);
        }
    }


    @Override
    public void transformPage(View view, float position) {
        onPreTransform(view,position);
        final float scale = 1f + Math.abs(position);
        view.setScaleX(scale);
        view.setScaleY(scale);
        view.setPivotX(view.getWidth() * 0.5f);
        view.setPivotY(view.getHeight() * 0.5f);
        view.setAlpha(position < -1f || position > 1f ? 0f : 1f - (scale - 1f));
        if(position == -1){
            view.setTranslationX(view.getWidth() * -1);
        }
    }
}
