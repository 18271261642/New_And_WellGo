package com.truescend.gofit.views;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.sn.utils.view.DIYViewUtil;
import com.truescend.gofit.R;


/**
 * 作者:东芝(2017/12/23).
 * 功能:波纹效果 按钮
 */

public class CircleRippleButton extends View {
    private float screenDensity;
    private Paint mainPaint;
    private Rect windowsRect = new Rect();
    private float scale = 0.05f;
    private final int WRAP_WIDTH = 200;
    private final int WRAP_HEIGHT = 200;
    private Bitmap bitmap;
    private  RectF iconRect = new RectF();
    private Paint mainAlphaPaint;

    public CircleRippleButton(Context context) {
        super(context);
        init(context,null);
    }

    public CircleRippleButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CircleRippleButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public CircleRippleButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private int crb_backgroundColor = 0xFF000000;
    private int crb_backgroundAlphaColor = crb_backgroundColor;

    private void init(Context context, AttributeSet attrs) {


        screenDensity = DIYViewUtil.getScreenDensity(getResources());

        if(attrs!=null) {
            TypedArray attributesArray = context.obtainStyledAttributes(attrs, R.styleable.CircleRippleButton);
            int crb_src_resourceId = attributesArray.getResourceId(R.styleable.CircleRippleButton_crb_src, 0);
            crb_backgroundColor = attributesArray.getColor(R.styleable.CircleRippleButton_crb_backgroundColor, crb_backgroundColor);
            crb_backgroundAlphaColor = attributesArray.getColor(R.styleable.CircleRippleButton_crb_backgroundAlphaColor, crb_backgroundColor);
            if(crb_src_resourceId!=0) {
                bitmap = BitmapFactory.decodeResource(getResources(), crb_src_resourceId);
            }
            attributesArray.recycle();
        }
        mainPaint = DIYViewUtil.createFillPaint(crb_backgroundColor);
        mainAlphaPaint = DIYViewUtil.createFillPaint(crb_backgroundAlphaColor);


        ValueAnimator valueAnimator = ValueAnimator.ofFloat(scale, 2 * scale);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                scale = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        valueAnimator.setDuration(1500);
        valueAnimator.start();
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.setRepeatMode(ValueAnimator.REVERSE);


    }



    @Override
    protected void onDraw(Canvas canvas) {
        //==============================================================================================================================================
        //====================================================================窗体大小计算================================================================
        //==============================================================================================================================================

        DIYViewUtil.createRect(windowsRect, this);
        ////////////////////////////////////////////////////////////////////////////////////////
        //--------------------------------------画 三个圆形-----------------------------------
        ////////////////////////////////////////////////////////////////////////////////////////

        float radius = windowsRect.height() / 2;

        mainAlphaPaint.setAlpha(255 / 4);
        canvas.drawCircle(windowsRect.centerX(), windowsRect.centerY(), radius * (0.5f + 2 * scale), mainAlphaPaint);

        mainAlphaPaint.setAlpha(255 / 2);
        canvas.drawCircle(windowsRect.centerX(), windowsRect.centerY(), radius * (0.5f + scale), mainAlphaPaint);

        canvas.drawCircle(windowsRect.centerX(), windowsRect.centerY(), radius * 0.5f, mainPaint);

        float v = radius * 0.25f;
        DIYViewUtil.setRectValue(iconRect,windowsRect.centerX()-v,windowsRect.centerY()-v,windowsRect.centerX()+v,windowsRect.centerY()+v);
        if (bitmap != null) {
            DIYViewUtil.drawBitmapFill(canvas, bitmap, iconRect, null);
        }
        super.onDraw(canvas);
    }


}
