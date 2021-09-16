package com.truescend.gofit.views;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import androidx.annotation.Nullable;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.view.View;

import com.sn.utils.view.DIYViewUtil;
import com.truescend.gofit.R;
import com.truescend.gofit.utils.ResUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者:东芝(2018/11/13).
 * 功能:区域进度条
 */

public class RegionalProgressBar extends View {


    public static final int COLOR_NONE = 0xFFD8D8D8;
    public static final int COLOR_HIGHLIGHT = ResUtil.getColor(R.color.colorRed);
    public static final int EGIONALPOSITION_DEF = -1;
    private Paint mPaintBarLine;
    private Paint mPaintSeparate;
    private RectF rectWindows = new RectF();
    private RectF rectProgressBar = new RectF();
    private RectF rectText = new RectF();
    private List<OccupiesValue> occupiesValues = new ArrayList<>();
    private ValueAnimator animator;
    private boolean isEmptyData;
    private List<OccupiesValue> mAnimBackupValues = new ArrayList<>();
    private int mMaxHeight;
    private Path p = new Path();
//    private Paint textPaint;
//    private int textHeight;
//    private float screenDensity;
    private int mHighlightRegionalPosition = EGIONALPOSITION_DEF;

    public boolean isEmptyData() {
        return isEmptyData;
    }


    public RegionalProgressBar(Context context) {
        super(context);
        init();
    }

    public RegionalProgressBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RegionalProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public RegionalProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
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
     *
     * @param values
     */
    public void setAnimOccupiesValues(final List<OccupiesValue> values) {
        //如果相同数据 则不做动画改变
        if (values != null && mAnimBackupValues != null && mAnimBackupValues.size() == values.size()) {
            int find = 0;
            for (OccupiesValue backupValue : mAnimBackupValues) {
                for (OccupiesValue value : values) {
                    if (backupValue.color == value.color &&
                            backupValue.srcValue == value.srcValue) {
                        find++;
                    }
                }
            }
            if (find == mAnimBackupValues.size()) {
                return;
            }
        }

        mAnimBackupValues.clear();
        mAnimBackupValues.addAll(values);
        setBaseAnimOccupiesValues(values);
    }


    /**
     * 设置仅某区域高亮, 默认-1, 表示显示全部,
     *
     * @param mHighlightRegionalPosition
     */
    public void setHighlightRegionalPosition(int mHighlightRegionalPosition) {
        this.mHighlightRegionalPosition = mHighlightRegionalPosition;
        invalidate();
    }


    private void setBaseAnimOccupiesValues(List<OccupiesValue> values) {
        int size = values.size();
        if (size == 0) return;
        int ratioTotal = 0;
        for (int i = 0; i < size; i++) {
            ratioTotal += values.get(i).ratio;
        }
        isEmptyData = ratioTotal == 0;
        if (isEmptyData) {
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
                    occupiesValueList.add(new OccupiesValue(value.color, r, value.srcValue));
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
                value.ratio = value.ratio * 100.0f / ratioTotal;
                this.occupiesValues.add(value);
            }
        } else {
            this.occupiesValues = values;
        }
        invalidate();
    }

    private void init() {


//        screenDensity = DIYViewUtil.getScreenDensity(getResources());

//        textPaint = DIYViewUtil.createTextPaint(0xff222222, screenDensity * 10);
//        textHeight = DIYViewUtil.getTextHeight("0123456789.", textPaint);

        mMaxHeight = 30 /*+ 2 * textHeight*/;

        mPaintBarLine = DIYViewUtil.createDefPaint(0xffffffff);
        mPaintBarLine.setStyle(Paint.Style.FILL);

        mPaintSeparate = DIYViewUtil.createDefPaint(0xff222200);
        mPaintSeparate.setStyle(Paint.Style.FILL);


        setWillNotDraw(false);
    }


    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measuredHeight = resolveSizeAndState(mMaxHeight, heightMeasureSpec, 0);
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), measuredHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        DIYViewUtil.createRect(rectWindows, this);
        DIYViewUtil.setRectValue(rectProgressBar, rectWindows);
//        DIYViewUtil.paddingRect(rectProgressBar, textHeight, 0, textHeight, 2 * textHeight);
        DIYViewUtil.setRectValue(rectText, rectProgressBar.left, rectProgressBar.bottom, rectProgressBar.right, rectWindows.bottom);


        int size = occupiesValues.size();

        float strokeWidth = rectProgressBar.height();

        mPaintBarLine.setStrokeWidth(strokeWidth);

        canvas.save();
        p.reset();
        p.addRoundRect(rectProgressBar, rectProgressBar.height() / 2, rectProgressBar.height() / 2, Path.Direction.CCW);
        canvas.clipPath(p);
        float start = rectProgressBar.left;
        for (int i = 0; i < size; i++) {
            OccupiesValue value = occupiesValues.get(i);
            if (mHighlightRegionalPosition == -1) {
                mPaintBarLine.setColor(value.color);
            } else {
                //需要高亮
                if (mHighlightRegionalPosition == i) {
                    mPaintBarLine.setColor(value.color);
                } else {
                    mPaintBarLine.setColor(COLOR_NONE);
                }
            }

            float end = rectProgressBar.width() * value.ratio / 100.0f;
            canvas.drawLine(start, rectProgressBar.centerY(), start + end, rectProgressBar.centerY(), mPaintBarLine);
            mPaintBarLine.setColor(0xffffffff);
            if (i < size - 1) {
                canvas.drawLine(start + end - 5, rectProgressBar.centerY(), start + end + 5, rectProgressBar.centerY(), mPaintBarLine);
            }
            start += end;
        }
        canvas.restore();


//        start = rectProgressBar.left;
//        for (int i = 0; i < size; i++) {
//            OccupiesValue value = occupiesValues.get(i);
//            float end = rectProgressBar.width() * value.ratio / 100.0f;
//            float tx = start + ((start + end) - start) / 2;
//            String text = String.format(Locale.ENGLISH, "%.0f", value.srcValue);
//            int textWidth = DIYViewUtil.getTextWidth(text, textPaint);
//            canvas.drawText(text, tx - textWidth / 2, rectText.centerY() + textHeight / 2/*理解 (2*h/2)/2  = h/2 */, textPaint);
//
//            start += end;
//        }

    }


    public static class OccupiesValue {
        public int color;
        public float ratio;
        public float srcValue;

        public OccupiesValue(int color, float ratio, float srcValue) {
            this.color = color;
            this.ratio = ratio;
            this.srcValue = srcValue;
        }
    }

}
