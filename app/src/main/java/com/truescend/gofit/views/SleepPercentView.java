package com.truescend.gofit.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.sn.utils.view.DIYViewUtil;

import java.util.Locale;

/**
 * 作者:东芝(2019/1/7).
 * 功能:睡眠平均占比
 */

public class SleepPercentView extends View {
    private static final String SLEEP_FORMAT = "%.0f%%";
    private RectF rectWindows = new RectF();
    private RectF rectContent = new RectF();
    private RectF rectSober = new RectF();
    private RectF rectLight = new RectF();
    private RectF rectDeep = new RectF();
    private Paint mPaintSoberBar;
    private Paint mPaintLightBar;
    private Paint mPaintDeepBar;
    private Paint mPaintAvgIndicator;
    private Paint mPaintAvgPointIndicator;
    private int avgSoberTime = 0;
    private int avgLightTime = 0;
    private int avgDeepTime = 0;

    private int curSoberTime = 0;
    private int curLightTime = 0;
    private int curDeepTime = 0;
    private Paint curPercentTextPaint;
    private Paint avgPercentTextPaint;
    private String mDeepSleepStr = "深睡";
    private String mLightSleepStr = "浅睡";
    private String mSoberSleepStr = "清醒";
    private int curMaxTime;
    private int avgMaxTime;
    private int curTestTextWidth;
    private String curTestPercentText;

    public void setSleepData(String deepSleepStr, String lightSleepStr, String soberSleepStr, int curDeepTime, int curLightTime, int curSoberTime, int avgDeepTime, int avgLightTime, int avgSoberTime) {
        this.mDeepSleepStr = deepSleepStr;
        this.mLightSleepStr = lightSleepStr;
        this.mSoberSleepStr = soberSleepStr;
        this.curSoberTime = curSoberTime;
        this.curLightTime = curLightTime;
        this.curDeepTime = curDeepTime;
        this.avgSoberTime = avgSoberTime;
        this.avgLightTime = avgLightTime;
        this.avgDeepTime = avgDeepTime;

        curMaxTime = Math.max(curMaxTime, curSoberTime);
        curMaxTime = Math.max(curMaxTime, curLightTime);
        curMaxTime = Math.max(curMaxTime, curDeepTime);
        if (curMaxTime == 0) {
            curMaxTime = 1;
        }
        avgMaxTime = Math.max(avgMaxTime, avgSoberTime);
        avgMaxTime = Math.max(avgMaxTime, avgLightTime);
        avgMaxTime = Math.max(avgMaxTime, avgDeepTime);
        if (avgMaxTime == 0) {
            avgMaxTime = 1;
        }
        invalidate();
    }

    public SleepPercentView(Context context) {
        super(context);
        init();
    }

    public SleepPercentView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SleepPercentView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public SleepPercentView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaintSoberBar = DIYViewUtil.createFillPaint(0xffB7BAE5);
        mPaintLightBar = DIYViewUtil.createFillPaint(0xff8373CB);
        mPaintDeepBar = DIYViewUtil.createFillPaint(0xff7046A8);
        mPaintAvgIndicator = DIYViewUtil.createDefPaint(0xff222222);
        mPaintAvgIndicator.setStrokeWidth(5);
        mPaintAvgIndicator.setStyle(Paint.Style.STROKE);
        mPaintAvgPointIndicator = DIYViewUtil.createFillPaint(mPaintAvgIndicator.getColor());


        curPercentTextPaint = DIYViewUtil.createTextPaint(0xFF30235C, 40);
        avgPercentTextPaint = DIYViewUtil.createTextPaint(0xFF000000, 40);

          curTestPercentText = String.format(Locale.ENGLISH, SLEEP_FORMAT, 100.0f);


    }

    @Override
    protected void onDraw(Canvas canvas) {
        DIYViewUtil.createRect(rectWindows, this);
        float barPadding = (rectWindows.height() * 0.1f);

        DIYViewUtil.setRectValue(rectContent, rectWindows.left + barPadding+curTestTextWidth, rectWindows.top, rectWindows.left + rectWindows.width() * 0.7f, rectWindows.bottom);
        float barH = (rectContent.height() - (barPadding * 4)) / 3;

        curPercentTextPaint.setTextSize(barH*0.35f);
        avgPercentTextPaint.setTextSize(barH*0.35f);
        curTestTextWidth = DIYViewUtil.getTextWidth(curTestPercentText, curPercentTextPaint);

        //平均

        //深睡
        DIYViewUtil.setRectValue(rectDeep, rectContent.left, rectContent.top + barPadding, rectContent.left + avgDeepTime * rectContent.width() / avgMaxTime, rectContent.top + barH + barPadding);
        mPaintDeepBar.setAlpha(127);
        canvas.drawRect(rectDeep, mPaintDeepBar);
        RectF rectDeepTemp = DIYViewUtil.copyRect(rectDeep);
        //浅睡
        DIYViewUtil.setRectValue(rectLight, rectContent.left, rectDeep.bottom + barPadding, rectContent.left + avgLightTime * rectContent.width() / avgMaxTime, rectDeep.bottom + barH + barPadding);
        mPaintLightBar.setAlpha(127);
        canvas.drawRect(rectLight, mPaintLightBar);
        RectF rectLightTemp = DIYViewUtil.copyRect(rectLight);

        //清醒
        DIYViewUtil.setRectValue(rectSober, rectContent.left, rectLight.bottom + barPadding, rectContent.left + avgSoberTime * rectContent.width() / avgMaxTime, rectLight.bottom + barH + barPadding);
        mPaintSoberBar.setAlpha(127);
        canvas.drawRect(rectSober, mPaintSoberBar);
        RectF rectSoberTemp = DIYViewUtil.copyRect(rectSober);

        //今日

        //深睡
        DIYViewUtil.setRectValue(rectDeep, rectContent.left-curTestTextWidth, rectContent.top + barPadding, rectContent.left + curDeepTime * rectContent.width() / curMaxTime, rectContent.top + barH + barPadding);
        mPaintDeepBar.setAlpha(255);
        canvas.drawRect(rectDeep, mPaintDeepBar);

        //浅睡
        DIYViewUtil.setRectValue(rectLight, rectContent.left-curTestTextWidth, rectDeep.bottom + barPadding, rectContent.left + curLightTime * rectContent.width() / curMaxTime, rectDeep.bottom + barH + barPadding);
        mPaintLightBar.setAlpha(255);
        canvas.drawRect(rectLight, mPaintLightBar);


        //清醒
        DIYViewUtil.setRectValue(rectSober, rectContent.left-curTestTextWidth, rectLight.bottom + barPadding, rectContent.left + curSoberTime * rectContent.width() / curMaxTime, rectLight.bottom + barH + barPadding);
        mPaintSoberBar.setAlpha(255);
        canvas.drawRect(rectSober, mPaintSoberBar);


        //画平均值的指示器
        int radius = Math.round(rectContent.height() * 0.018f);

        //深睡
        mPaintAvgIndicator.setColor(0xFF53347D);
        mPaintAvgPointIndicator.setColor(0xFF53347D);
        canvas.drawLine(rectDeepTemp.right, rectDeepTemp.top - 10, rectDeepTemp.right, rectDeepTemp.bottom, mPaintAvgIndicator);
        canvas.drawCircle(rectDeepTemp.right, rectDeepTemp.bottom, radius, mPaintAvgPointIndicator);
        //浅睡
        mPaintAvgIndicator.setColor(0xFF6B5EA5);
        mPaintAvgPointIndicator.setColor(0xFF6B5EA5);
        canvas.drawLine(rectLightTemp.right, rectLightTemp.top - 10, rectLightTemp.right, rectLightTemp.bottom, mPaintAvgIndicator);
        canvas.drawCircle(rectLightTemp.right, rectLightTemp.bottom, radius, mPaintAvgPointIndicator);
        //清醒
        mPaintAvgIndicator.setColor(0xFF8C8EAE);
        mPaintAvgPointIndicator.setColor(0xFF8C8EAE);
        canvas.drawLine(rectSoberTemp.right, rectSoberTemp.top - 10, rectSoberTemp.right, rectSoberTemp.bottom, mPaintAvgIndicator);
        canvas.drawCircle(rectSoberTemp.right, rectSoberTemp.bottom, radius, mPaintAvgPointIndicator);

        //今日百分比文本
        //计算
        int curTotalTime = curSoberTime + curLightTime + curDeepTime;
        float curDeepPercent = 0f;
        float curLightPercent = 0f;
        float curSoberPercent = 0f;
        if (curTotalTime > 0) {
            curDeepPercent = curDeepTime * 100.0f / curTotalTime;
            curLightPercent = curLightTime * 100.0f / curTotalTime;
            curSoberPercent = curSoberTime * 100.0f / curTotalTime;
        }
        String curDeepPercentText = String.format(Locale.ENGLISH, SLEEP_FORMAT, curDeepPercent);
        String curLightPercentText = String.format(Locale.ENGLISH, SLEEP_FORMAT, curLightPercent);
        String curSoberPercentText = String.format(Locale.ENGLISH, SLEEP_FORMAT, curSoberPercent);

        //画文字
        //深睡
        int curDeepTextHeight = DIYViewUtil.getTextHeight(curDeepPercentText, curPercentTextPaint);
        canvas.drawText(curDeepPercentText, rectDeep.left + curDeepTextHeight / 2, rectDeep.bottom - curDeepTextHeight / 2, curPercentTextPaint);
        //浅睡
        int curLightTextHeight = DIYViewUtil.getTextHeight(curLightPercentText, curPercentTextPaint);
        canvas.drawText(curLightPercentText, rectLight.left + curLightTextHeight / 2, rectLight.bottom - curLightTextHeight / 2, curPercentTextPaint);
        //清醒
        int curSoberTextHeight = DIYViewUtil.getTextHeight(curSoberPercentText, curPercentTextPaint);
        canvas.drawText(curSoberPercentText, rectSober.left + curSoberTextHeight / 2, rectSober.bottom - curSoberTextHeight / 2, curPercentTextPaint);

        //平均百分比文本
        //计算
        int avgTotalTime = avgSoberTime + avgLightTime + avgDeepTime;
        float avgDeepPercent = 0f;
        float avgLightPercent = 0f;
        float avgSoberPercent = 0f;
        if (avgTotalTime > 0) {
            avgDeepPercent = avgDeepTime * 100.0f / avgTotalTime;
            avgLightPercent = avgLightTime * 100.0f / avgTotalTime;
            avgSoberPercent = avgSoberTime * 100.0f / avgTotalTime;
        }
        String avgDeepPercentText = String.format(Locale.ENGLISH, SLEEP_FORMAT, avgDeepPercent);
        String avgLightPercentText = String.format(Locale.ENGLISH, SLEEP_FORMAT, avgLightPercent);
        String avgSoberPercentText = String.format(Locale.ENGLISH, SLEEP_FORMAT, avgSoberPercent);

        //深睡
        int avgDeepTextHeight = DIYViewUtil.getTextHeight(avgDeepPercentText, avgPercentTextPaint);

        if (rectDeepTemp.right < rectDeep.right) {
            canvas.drawText(avgDeepPercentText, doNotOverlap(rectDeep.right) + avgDeepTextHeight / 2, rectDeep.bottom - avgDeepTextHeight / 2, avgPercentTextPaint);
            canvas.drawText(mDeepSleepStr, doNotOverlap(rectDeep.right) + avgDeepTextHeight / 2, rectDeep.top + avgDeepTextHeight + avgDeepTextHeight / 2, avgPercentTextPaint);
        } else {
            canvas.drawText(avgDeepPercentText, doNotOverlap(rectDeepTemp.right) + avgDeepTextHeight / 2, rectDeepTemp.bottom - avgDeepTextHeight / 2, avgPercentTextPaint);
            canvas.drawText(mDeepSleepStr, doNotOverlap(rectDeepTemp.right) + avgDeepTextHeight / 2, rectDeepTemp.top + avgDeepTextHeight + avgDeepTextHeight / 2, avgPercentTextPaint);
        }

        //浅睡
        int avgLightTextHeight = DIYViewUtil.getTextHeight(avgLightPercentText, avgPercentTextPaint);

        if (rectLightTemp.right < rectLight.right) {
            canvas.drawText(avgLightPercentText, doNotOverlap(rectLight.right) + avgLightTextHeight / 2, rectLight.bottom - avgLightTextHeight / 2, avgPercentTextPaint);
            canvas.drawText(mLightSleepStr, doNotOverlap(rectLight.right) + avgLightTextHeight / 2, rectLight.top + avgLightTextHeight + avgLightTextHeight / 2, avgPercentTextPaint);
        } else {
            canvas.drawText(avgLightPercentText, doNotOverlap(rectLightTemp.right) + avgLightTextHeight / 2, rectLightTemp.bottom - avgLightTextHeight / 2, avgPercentTextPaint);
            canvas.drawText(mLightSleepStr, doNotOverlap(rectLightTemp.right) + avgLightTextHeight / 2, rectLightTemp.top + avgLightTextHeight + avgLightTextHeight / 2, avgPercentTextPaint);

        }

        //清醒
        int avgSoberTextHeight = DIYViewUtil.getTextHeight(avgSoberPercentText, avgPercentTextPaint);

        if (rectSoberTemp.right < rectSober.right) {
            canvas.drawText(avgSoberPercentText, doNotOverlap(rectSober.right) + avgSoberTextHeight / 2, rectSober.bottom - avgSoberTextHeight / 2, avgPercentTextPaint);
            canvas.drawText(mSoberSleepStr, doNotOverlap(rectSober.right) + avgSoberTextHeight / 2, rectSober.top + avgSoberTextHeight + avgSoberTextHeight / 2, avgPercentTextPaint);
        } else {
            canvas.drawText(avgSoberPercentText, doNotOverlap(rectSoberTemp.right) + avgSoberTextHeight / 2, rectSoberTemp.bottom - avgSoberTextHeight / 2, avgPercentTextPaint);
            canvas.drawText(mSoberSleepStr, doNotOverlap(rectSoberTemp.right) + avgSoberTextHeight / 2, rectSoberTemp.top + avgSoberTextHeight + avgSoberTextHeight / 2, avgPercentTextPaint);
        }

    }


    private float doNotOverlap(float x) {
        if (x < rectContent.left ) {
            x = rectContent.left ;
        }
        return x;
    }

}
