package com.truescend.gofit.views;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import androidx.annotation.Nullable;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.view.View;

import com.sn.utils.view.DIYViewUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者:东芝(2018/8/16).
 * 功能:睡眠比例View
 */

public class SleepRatioView extends View {

    private Paint mPaintBarLine;
    private Paint mPaintSeparate;
    private RectF rectWindows = new RectF();
    private List<OccupiesValue> occupiesValues = new ArrayList<>();
    private ValueAnimator animator;
    private boolean isEmptyData;
    private List<OccupiesValue> mAnimBackupValues = new ArrayList<>();
    public boolean isEmptyData() {
        return isEmptyData;
    }

    public SleepRatioView(Context context) {
        super(context);
        init();
    }

    public SleepRatioView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SleepRatioView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public SleepRatioView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void rePlayAnim() {
        if (!mAnimBackupValues.isEmpty()) {
            setBaseAnimOccupiesValues(mAnimBackupValues);
            mAnimBackupValues.clear();
        }
    }


    /**
     * 带动画
     * @param values
     */
    public void setAnimOccupiesValues(final List<OccupiesValue> values) {
        mAnimBackupValues.clear();
        mAnimBackupValues.addAll(values);
        setBaseAnimOccupiesValues(values);
    }

    private void setBaseAnimOccupiesValues(List<OccupiesValue> values) {
        int size = values.size();
        if (size == 0) return;
        int ratioTotal = 0;
        for (int i = 0; i < size; i++) {
            ratioTotal += values.get(i).ratio;
        }
        isEmptyData = ratioTotal == 0;
        if(isEmptyData){
            invalidate();
            return;
        }
        float max_ratio = 0;
        final List<OccupiesValue> ratios = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            OccupiesValue value = values.get(i);
            value.ratio = value.ratio * 100.0f / ratioTotal;
            max_ratio = Math.max(max_ratio, value.ratio);
            ratios.add(value);
        }
        if (animator != null) {
            animator.removeAllUpdateListeners();
            animator.cancel();
            animator = null;
        }
        animator = ValueAnimator.ofFloat(max_ratio, 0);
        animator.setInterpolator(new FastOutSlowInInterpolator());
        animator.setDuration(1000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            List<OccupiesValue> occupiesValueList = new ArrayList<>();
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animatedValue = (float) animation.getAnimatedValue();
                occupiesValueList.clear();
                for (int i = 0; i < ratios.size(); i++) {
                    OccupiesValue value = ratios.get(i);
                    float r = value.ratio - animatedValue;
                    if (r < 0) {
                        r = 0;
                    }
                    occupiesValueList.add(new OccupiesValue(value.color, r));
                }
                occupiesValues = occupiesValueList;
                invalidate();
            }
        });
        animator.start();
    }

    /**
     * @param values
     * @param autoCalculation 自动计算百分比
     */
    public void setOccupiesValues(List<OccupiesValue> values, boolean autoCalculation) {
        if (autoCalculation) {
            int size = values.size();
            if (size == 0) return;
            int ratioTotal = 0;
            for (int i = 0; i < size; i++) {
                ratioTotal += values.get(i).ratio;
            }
            isEmptyData = ratioTotal == 0;
            this.occupiesValues.clear();
            for (int i = 0; i < size; i++) {
                OccupiesValue value = values.get(i);
                value.ratio = value.ratio * 100.0f / ratioTotal ;
                this.occupiesValues.add(value);
            }
        } else {
            this.occupiesValues = values;
        }
        invalidate();
    }

    private void init() {
        mPaintBarLine = DIYViewUtil.createDefPaint(0xffffffff);
        mPaintSeparate = DIYViewUtil.createDefPaint(0xffffffff);
        mPaintBarLine.setStyle(Paint.Style.STROKE);
        mPaintSeparate.setStyle(Paint.Style.STROKE);

//        BlurMaskFilter maskFilter = new BlurMaskFilter(shine, BlurMaskFilter.Blur.SOLID);
//        mPaintBarLine.setMaskFilter(maskFilter);
//        mPaintSeparate.setMaskFilter(maskFilter);


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

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        DIYViewUtil.createRect(rectWindows, this);

        canvas.save();
        int size = occupiesValues.size();

        float strokeWidth = rectWindows.height() / 9.0f;

        DIYViewUtil.paddingRect(rectWindows, Math.round(strokeWidth / 2) + 2);
        mPaintBarLine.setStrokeWidth(strokeWidth);
        mPaintSeparate.setStrokeWidth(strokeWidth);

        //无数据
//        if (isEmptyData) {
            mPaintBarLine.setColor(0xFFDDDDDD);
            canvas.drawArc(rectWindows, 0, 360, false, mPaintBarLine);
//        }else {
            float start = -90;
            float angleTotal = 0;
            int separate = 1;

            for (int i = 0; i < size; i++) {
                OccupiesValue value = occupiesValues.get(i);
                mPaintBarLine.setColor(value.color);
                float end = 360.0f * value.ratio / 100.0f;
                canvas.drawArc(rectWindows, start, end, false, mPaintBarLine);
                angleTotal += end;
                //大于0加上 分隔
                if (i > 0) {

                    canvas.drawArc(rectWindows, start - separate, separate, false, mPaintSeparate);
                }
                //最后一个如果等于或超过360度则也加上分隔
                if (angleTotal >= 360) {
                    canvas.drawArc(rectWindows, start + end - separate, separate, false, mPaintSeparate);
                }
                start += end;
//            }
        }

        canvas.restore();


    }


    public static class OccupiesValue {
        public int color;
        public float ratio;

        public OccupiesValue(int color, float ratio) {
            this.color = color;
            this.ratio = ratio;
        }
    }
}
