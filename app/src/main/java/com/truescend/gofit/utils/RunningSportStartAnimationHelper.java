package com.truescend.gofit.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewPropertyAnimator;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;

import com.truescend.gofit.R;
import com.truescend.gofit.views.CircleRippleButton;

/**
 * 作者:东芝(2018/2/26).
 * 功能:运动开始动画
 */

public class RunningSportStartAnimationHelper {


    private CircleRippleButton startButtonView;
    private ImageView countDownView;
    private View sportDataView;

    public void start(CircleRippleButton startButtonView,  final View parentView,ImageView countDownView,View sportDataView ){
        this.startButtonView = startButtonView;
        this.countDownView = countDownView;
        this.sportDataView = sportDataView;

        //这时这个按钮不能多次点击
        startButtonView.setEnabled(false);
        startButtonView.setClickable(false);

        // get the center for the clipping circle
        int cx = (startButtonView.getLeft() + startButtonView.getRight()) / 2;
        int cy = (startButtonView.getTop() + startButtonView.getBottom()) / 2;


        //拦截所有事件 防止点击后面的控件
        parentView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        // get the final radius for the clipping circle
        int dx = Math.max(cx, parentView.getWidth() - cx);
        int dy = Math.max(cy, parentView.getHeight() - cy);
        float endRadius = (float) Math.hypot(dx, dy);

        // Android native animator
        int startRadius = startButtonView.getWidth() / 4;
        start(cx, cy, parentView, startRadius, endRadius);

    }


    private void start(final int cx, final int cy, final View parent, final float startRadius, final float endRadius) {
        Animator animator = createCircularReveal(parent, cx, cy, startRadius, endRadius);

        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                parent.setBackgroundColor(0xff222222);
                //此时动画隐藏 圆形按钮
                startButtonView.animate().setDuration(500).scaleX(0).scaleY(0).start();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                end(cx, cy, parent, endRadius, 0);

                final int[] icon = {R.mipmap.icon_track_pre_3, R.mipmap.icon_track_pre_2, R.mipmap.icon_track_pre_1, R.mipmap.icon_track_pre_go};
                final float h = countDownView.getHeight() / 2;
                countDownView.setVisibility(View.VISIBLE);
                countDownView.setTranslationY(-h);
                ViewPropertyAnimator animate = countDownView.animate();
                animate.setDuration(500).translationY(0).setInterpolator(new OvershootInterpolator());
                animate.setListener(new AnimatorListenerAdapter() {
                    int count = 0;

                    @Override
                    public void onAnimationStart(Animator animation) {
                        countDownView.setImageResource(icon[count]);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        final AnimatorListenerAdapter animatorListenerAdapter = this;
                        //为了效果 延时再设置setTranslationY
                        countDownView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                count++;
                                if (count >= icon.length) {
                                    countDownView.setVisibility(View.INVISIBLE);
                                    return;
                                }
                                countDownView.setTranslationY(-h);
                                ViewPropertyAnimator animate = countDownView.animate();
                                animate.setDuration(500).translationY(0).setInterpolator(new OvershootInterpolator());
                                animate.setListener(animatorListenerAdapter);
                                animate.start();
                            }
                        }, 500);
                    }
                });
                animate.start();

            }

        });
        animator.setDuration(800);
        animator.start();
    }

    private Animator createCircularReveal(View parent, int cx, int cy, float startRadius, float endRadius) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return ViewAnimationUtils.createCircularReveal(parent, cx, cy, startRadius, endRadius);
        }
        return ValueAnimator.ofInt(0,1);
    }

    private void end( int cx, int cy, final View parent, float startRadius, float endRadius) {
        Animator animator = createCircularReveal(parent, cx, cy, startRadius, endRadius);
        animator.setStartDelay(4000);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                parent.setBackgroundColor(0x00000000);
                parent.setVisibility(View.GONE);
                sportDataView.setVisibility(View.VISIBLE);
            }
        });
        animator.setDuration(500);
        animator.start();
    }


}
