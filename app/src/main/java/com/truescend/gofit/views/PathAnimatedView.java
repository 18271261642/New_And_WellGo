package com.truescend.gofit.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import com.truescend.gofit.R;


/**
 * 作者:东芝(2018/2/1).
 * 功能:路径动画
 */
@Deprecated
public class PathAnimatedView extends View {

    Paint mPaint;
    Path mPath = new Path();
    int mStrokeColor;
    float mStrokeWidth;

    private float[] lastPoints;


    public PathAnimatedView(Context context) {
        this(context, null);
        init();
    }

    public PathAnimatedView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        init();
    }

    public PathAnimatedView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PathAnimatedView);
        mStrokeColor = a.getColor(R.styleable.PathAnimatedView_strokeColor, 0xff00ff00);
        mStrokeWidth = a.getFloat(R.styleable.PathAnimatedView_stroke_Width, 8.0f);
        a.recycle();

        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setColor(mStrokeColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mStrokeWidth);
        mPaint.setAntiAlias(true);


    }


    public void setPath(final float[] points) {
        lastPoints = points;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    float[] newPoints = new float[lastPoints.length];
                    System.arraycopy(lastPoints, 1, newPoints, 0, lastPoints.length-1);
                    newPoints[newPoints.length-1]  =  lastPoints[0];
                    mPath.reset();
                    for (int i = 0; i < newPoints.length; i++) {
                            if (i == 0) {
                                mPath.moveTo(i, newPoints[i]);
                            } else {
                                mPath.lineTo(i, newPoints[i]);
                            }
                    }
                    postInvalidate();
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    lastPoints = newPoints;
                }
            }
        }).start();

    }

    private void createJavaPath(float[][] points) {
        if (points.length == 0)
            throw new IllegalArgumentException("Cannot have zero lastPoints in the line");

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        System.out.println("draw");
        canvas.save();
        canvas.translate(getPaddingLeft(), getPaddingTop());
        canvas.drawPath(mPath, mPaint);
        canvas.restore();

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(widthMeasureSpec);

        int measuredWidth, measuredHeight;

        if (widthMode == MeasureSpec.AT_MOST)
            throw new IllegalStateException("PathAnimatedView cannot have a WRAP_CONTENT property");
        else
            measuredWidth = widthSize;

        if (heightMode == MeasureSpec.AT_MOST)
            throw new IllegalStateException("PathAnimatedView cannot have a WRAP_CONTENT property");
        else
            measuredHeight = heightSize;

        setMeasuredDimension(measuredWidth, measuredHeight);
    }
}
