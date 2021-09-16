package com.truescend.gofit.pagers.home.sport_mode.bean;

import android.view.animation.Interpolator;

/**
 * 作者:东芝(2019/6/10).
 * 功能:舒畅的动画插值器
 */
public class QuintOutInterpolator implements Interpolator {
    @Override
    public float getInterpolation(float t) {
        return (t-=1)*t*t*t*t + 1;
    }
}
