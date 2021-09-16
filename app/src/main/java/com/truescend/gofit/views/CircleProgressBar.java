package com.truescend.gofit.views;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.sn.utils.view.DIYViewUtil;
import com.truescend.gofit.R;

import java.util.Locale;

/**
 * 作者:东芝(2017/12/12).
 * 功能:圆形步数进度条
 */

public class CircleProgressBar extends View implements ValueAnimator.AnimatorUpdateListener {
    /**
     * 增大倍数(为了动画好看)
     */
    protected static final int INCREASE_RATIO = 5;

    /**
     * 当前进度
     */
    private int mProgress = 0;


    /**
     * 最大进度
     */
    private int mMaxProgress = 100 * INCREASE_RATIO;

    /**
     * 窗口
     */
    private RectF windowsRect = new RectF();
    /**
     * 像素密度
     */
    private float density;
    /**
     * 进度条
     */
    private Paint mPaintProgressBar;
    /**
     * 进度背景
     */
    private Paint mPaintProgressBackground;
    /**
     * 圆点
     */
    private Paint mPaintProgressPoint;


    private int padding = 30;
    /**
     * 圆形小点的半径
     */
    private float mPointRadius;
    private Paint mPaintText;
    private Paint mPaintSubText;
    /**
     * 末端圆点是否可见
     */
    private boolean isEndDotEnable = true;
    private int mAnimBackupmProgressValue;


    public CircleProgressBar(Context context) {
        super(context);
        init();
    }

    /**
     * 注意这个方法不能被混淆
     *
     * @param value
     */
    private void setValue(int value) {
        this.mProgress = value;
        invalidate();
    }

    //    public void setProgress(int mProgress) {
//        this.mProgress = mProgress;
//        postInvalidate();
//    }
    private ValueAnimator animator;

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (animator!=null) {
            animator.removeAllListeners();
            animator.cancel();
            animator=null;
        }
        mProgress=0;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }


    public void rePlayAnim() {
        if(mAnimBackupmProgressValue>0) {
            setBaseProgress(mAnimBackupmProgressValue);
            mAnimBackupmProgressValue = 0;
        }
    }


    public void setProgress(int mProgress) {
        mAnimBackupmProgressValue = mProgress;
        setBaseProgress(mProgress);

    }

    private void setBaseProgress(int mProgress) {
        if (animator != null) {
            animator.removeAllListeners();
            animator.cancel();
            animator = null;
        }
        if (mProgress == 0) {
            this.mProgress = mProgress;
            invalidate();
        } else {
            if (Math.abs(mProgress * INCREASE_RATIO - this.mProgress) < 10) {
                setValue(mProgress * INCREASE_RATIO);
            } else {
                animator = ValueAnimator.ofInt(this.mProgress, mProgress * INCREASE_RATIO);
                animator.setDuration(1000);
                animator.start();
                animator.addUpdateListener(this);
            }
        }
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        setValue((Integer) animation.getAnimatedValue());
    }

    public void setMaxProgress(int mMaxProgress) {
        this.mMaxProgress = mMaxProgress* INCREASE_RATIO;
        invalidate();
    }

    public int getMaxProgress() {
        return mMaxProgress;
    }

    public int getProgress() {
        return mProgress;
    }

    public CircleProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircleProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public CircleProgressBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        density = DIYViewUtil.getScreenDensity(getResources());
        mPaintProgressBar = DIYViewUtil.createStrokePaint(0xFF000000, density * 5);
        mPaintProgressPoint = DIYViewUtil.createFillPaint(0xFF000000);
        mPaintText = DIYViewUtil.createTextPaint(0xFF000000,density*20);
        mPaintSubText = DIYViewUtil.createTextPaint(0xFF727272,density*10);
        mPaintText.setFlags(Paint.FAKE_BOLD_TEXT_FLAG| Paint.ANTI_ALIAS_FLAG);
        mPaintProgressBackground = DIYViewUtil.createStrokePaint(0xFFD8D8D8, density * 5);
        //圆的半径与ProgressBar的粗细绑定某个关系 则会自动适配
        mPointRadius = mPaintProgressBar.getStrokeWidth() * 1.6f;
//        setMaxProgress(2000);
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                while (true) {
//                    try {
//                        Thread.sleep(2000);
//                    } catch (InterruptedException w) {
//                        w.printStackTrace();
//                    }
//
//                    post(new Runnable() {
//                        @Override
//                        public void run() {
//                            Random random = new Random();
//                            setProgress(random.nextInt(2000));
//                        }
//                    });
//                }
//            }
//        }).startSync();
    }
//


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (widthMeasureSpec > heightMeasureSpec) {
            super.onMeasure(heightMeasureSpec, heightMeasureSpec);
        } else if (widthMeasureSpec < heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, widthMeasureSpec);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
        int measuredWidth = getMeasuredWidth();
        mPaintText.setTextSize(measuredWidth/6);
        //子文本是大文本的1/2
        mPaintSubText.setTextSize(mPaintText.getTextSize()/2);
    }




    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //创建矩形范围
        DIYViewUtil.createRectAdaptHeight(windowsRect, this);

        //padding一下
        DIYViewUtil.paddingRect(windowsRect, padding, padding, padding, padding);//窗体padding  为了左右上下有padding 好看点
        ////////////////////////////////////////////////////////////////////////////////////////
        //----------------------------------------进度条---------------------------------
        ////////////////////////////////////////////////////////////////////////////////////////

        canvas.save();
        //先画进度条背景
        canvas.drawArc(windowsRect, 0, 360, false, mPaintProgressBackground);
        float angle = mProgress == 0 ? 0 : ((float) mProgress / mMaxProgress) * 360;
        if(angle>=360)
        {
            angle = 360;
        }
        //后画进度条 则会叠在背景上面
        canvas.drawArc(windowsRect, -90, angle, false, mPaintProgressBar);
        canvas.restore();
        ////////////////////////////////////////////////////////////////////////////////////////
        //--------------------------------------圆点-----------------------------------
        ////////////////////////////////////////////////////////////////////////////////////////
        //存储
        canvas.save();
        //  圆弧上xy坐标

        if(isEndDotEnable) {
            float v = windowsRect.height() / 2;
            float mIndicatorX = windowsRect.centerX() + (float) (v * Math.cos(Math.toRadians(angle + 270)));
            float mIndicatorY = windowsRect.centerY() + (float) (v * Math.sin(Math.toRadians(angle + 270)));
            canvas.drawCircle(mIndicatorX, mIndicatorY, mPointRadius, mPaintProgressPoint);
        }

        //恢复
        canvas.restore();
        ////////////////////////////////////////////////////////////////////////////////////////
        //--------------------------------------中间文字-----------------------------------
        ////////////////////////////////////////////////////////////////////////////////////////
        {
            String text = String.format(Locale.ENGLISH, "%d", mProgress == 0 ? 0 : mProgress / INCREASE_RATIO);
            Rect textRect = DIYViewUtil.getTextRect(text, mPaintText);
            float x = windowsRect.centerX() - (textRect.width() / 2);
            float y = windowsRect.centerY() + (textRect.height() / 2);
            canvas.drawText(text, x, y, mPaintText);

            String text2 = getContext().getString(R.string.unit_steps);
            Rect textRect2 = DIYViewUtil.getTextRect(text2, mPaintSubText);
            canvas.drawText(text2, windowsRect.centerX() - (textRect2.width() / 2), y+ padding+padding+(textRect2.height() / 2), mPaintSubText);
        }
    }


    public void setEndDotEnable(boolean endDotEnable) {
        if(isEndDotEnable!=endDotEnable){
            invalidate();
        }
        isEndDotEnable = endDotEnable;

    }
}
