package com.truescend.gofit.views;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.ProgressBar;

/**
 * 作者:东芝(2018/4/13).
 * 功能:带动画的 进度条
 */

public class AnimateProgressBar extends ProgressBar {
    private ValueAnimator progressAnimator;
    public AnimateProgressBar(Context context) {
        super(context);
    }

    public AnimateProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AnimateProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public AnimateProgressBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public synchronized void setProgress(int progress) {
        if(progressAnimator!=null){
            progressAnimator.removeAllUpdateListeners();
            progressAnimator.cancel();
        }
        progressAnimator = ValueAnimator.ofInt(getProgress(), progress);
        progressAnimator.setDuration(800);
        progressAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int animatedValue = (int) animation.getAnimatedValue();
                setInternalProgress(animatedValue);
            }
        });
        progressAnimator.start();
    }


    private void setInternalProgress(int progress) {
        super.setProgress(progress);
    }

}
