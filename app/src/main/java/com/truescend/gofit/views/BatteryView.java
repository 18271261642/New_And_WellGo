package com.truescend.gofit.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.sn.utils.view.DIYViewUtil;
import com.truescend.gofit.R;

/**
 * 作者:东芝(2019/3/15).
 * 功能:电池
 */

public class BatteryView extends View {

    private Bitmap bmpBatteryEmpty;
    private Bitmap bmpBatteryFill;
    private Paint bmpPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Rect rectWindows = new Rect();
    private int progress;
    private int max = -1;

    public BatteryView(Context context) {
        super(context);
        init();
    }

    public BatteryView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BatteryView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public BatteryView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        bmpBatteryEmpty = BitmapFactory.decodeResource(getResources(), R.mipmap.icon_electricity_empty);
        bmpBatteryFill = BitmapFactory.decodeResource(getResources(), R.mipmap.icon_electricity_fill);

        //测试
//        setMax(100);
//        ValueAnimator valueAnimator = ValueAnimator.ofInt(0, 100);
//        valueAnimator.setDuration(5000);
//        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator animation) {
//                int animatedValue = (int) animation.getAnimatedValue();
//                setProgress(animatedValue);
//            }
//        });
//        valueAnimator.start();

    }


    public void setMax(int max) {
        this.max = max;
        invalidate();
    }

    public void setProgress(int progress) {
        this.progress = progress;
        invalidate();
    }

    public int getProgress() {
        return progress;
    }

    public int getMax() {
        return max;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (max <1) {
         return;
        }
        DIYViewUtil.createRect(rectWindows, this);
        DIYViewUtil.drawBitmapFill(canvas, bmpBatteryEmpty, rectWindows, bmpPaint);
        canvas.save();
        int width = rectWindows.width();
        Rect clipRect = DIYViewUtil.copyRect(rectWindows);
        int padding = Math.round(width * 0.13f);
        DIYViewUtil.paddingRect(clipRect, padding, 0, padding, 0);
        width = clipRect.width();


        DIYViewUtil.paddingRect(clipRect, 0, 0, width - (width * progress / max), 0);
        canvas.clipRect(clipRect);
        DIYViewUtil.drawBitmapFill(canvas, bmpBatteryFill, rectWindows, bmpPaint);
        canvas.restore();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (widthMeasureSpec > heightMeasureSpec) {
            super.onMeasure(heightMeasureSpec, heightMeasureSpec);
        } else if (widthMeasureSpec < heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, widthMeasureSpec);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }
}
