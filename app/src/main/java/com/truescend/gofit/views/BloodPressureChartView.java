package com.truescend.gofit.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.sn.utils.view.DIYViewUtil;
import com.truescend.gofit.views.bean.base.IBottomLabel;

import java.util.Arrays;
import java.util.List;


/**
 * 作者:东芝(2017/11/7).
 * 功能: 血压图表
 */
public class BloodPressureChartView extends View {

    private int COLOR_SYSTOLIC = 0xFFF86B9A;
    private int COLOR_DIASTOLIC = 0xFF6DCBFD;
    private Paint limitLinePaint;
    private Paint labelPaint;
    private float[] mLimitLines;

    private float minSystolicBloodPressure= Float.MAX_VALUE;;
    private float maxSystolicBloodPressure= -Float.MAX_VALUE;;

    private float minDiastolicBloodPressure= Float.MAX_VALUE;;
    private float maxDiastolicBloodPressure= -Float.MAX_VALUE;;

    private Paint labelScaleLinePaint;
    private List<BloodPressureItem> data;
    private Paint barPaint;
    private float bar_width;
    private IBottomLabel label;
    private RectF barRect = new RectF();//柱状图
    private Rect windowsBottomScaleLineRect = new Rect();//底部刻度线RECT范围
    private Rect windowsRect = new Rect();
    private boolean isDrawBorder = true;//画边框 包括0刻度线
    private boolean isDrawLimitLine = true;//画limit刻度线
    private boolean isDrawLabelLimit = true;//画label刻度线
    private boolean isDrawZeroLimitLine = true;//画0刻度线
    private boolean isDrawLabel = true;//画label (该参数影响着)isDrawLabelLimit
    private boolean isFoldPaddingEndModel = false;//叠到其他我自己写的ViewView时的paddingEnd
    private boolean hasDefLabel;
    private String[] mLimitLinesLabel;
    private String maxTextString = "100";
    private int[] barColors = {0xFF000000, 0xFF000000, 0xFF000000, 0xEE000000, 0xDD000000, 0xCC000000, 0xBB000000, 0x55000000, 0x22000000};
    private float screenDensity;
    private RectF barBottomRect = new RectF();
    private RectF barTopRect = new RectF();

    /**
     * 小尺寸
     */
    public static float BAR_WIDTH_SMALL = 1.5f;
    /**
     * 正常尺寸
     */
    public static float BAR_WIDTH_NORMAL = 2;
    /**
     * 中等尺寸
     */
    public static float BAR_WIDTH_MEDIUM = 5;
    /**
     * 大尺寸
     */
    public static float BAR_WIDTH_LARGER = 8;

    /**
     * 设置颜色
     * @param colorDiastolic
     * @param colorSystolic
     */
    public void setColors(int colorDiastolic, int colorSystolic) {
        COLOR_DIASTOLIC = colorDiastolic;
        COLOR_SYSTOLIC = colorSystolic;
        invalidate();
    }


    /**
     * 0 刻度分割线
     * @param drawZeroLimitLine
     */
    public void setDrawZeroLimitLine(boolean drawZeroLimitLine) {
        isDrawZeroLimitLine = drawZeroLimitLine;
        invalidate();
    }
    /**
     * 叠到其他我自己写的ViewView时的paddingEnd
     *
     * @param foldPaddingEndModel
     */
    public void setFoldPaddingEndModel(boolean foldPaddingEndModel) {
        isFoldPaddingEndModel = foldPaddingEndModel;
        invalidate();
    }

    /**
     * 画左边和底部边框包括0 刻度分割线
     * @param isDrawBorder
     */
    public void setDrawBorder(boolean isDrawBorder) {
        this.isDrawBorder = isDrawBorder;
        invalidate();
    }

    /**
     * 画分割线
     * @param isDrawLimitLine
     */
    public void setDrawLimitLine(boolean isDrawLimitLine) {
        this.isDrawLimitLine = isDrawLimitLine;
        invalidate();
    }


    /**
     * 画左边 刻度文字
     * @param isDrawLabel
     */
    public void setDrawLabel(boolean isDrawLabel) {
        this.isDrawLabel = isDrawLabel;
        invalidate();
    }


    /**
     * 画左边刻度文字的 分割线
     * @param isDrawLabelLimit
     */
    public void setDrawLabelLimit(boolean isDrawLabelLimit) {
        this.isDrawLabelLimit = isDrawLabelLimit;
        invalidate();
    }

    public BloodPressureChartView(Context context) {
        super(context);
        init();
    }

    public BloodPressureChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BloodPressureChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public BloodPressureChartView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        screenDensity = DIYViewUtil.getScreenDensity(getResources());

        BAR_WIDTH_SMALL = screenDensity/1.34f;
        BAR_WIDTH_NORMAL = screenDensity;
        BAR_WIDTH_MEDIUM = screenDensity*2.5f;
        BAR_WIDTH_LARGER = screenDensity*4;


        barPaint = DIYViewUtil.createFillPaint(0xFF000000);

        limitLinePaint = DIYViewUtil.createFillPaint(0xFFD9D9D9);
        limitLinePaint.setStrokeWidth(screenDensity);

        labelPaint = DIYViewUtil.createTextPaint(0xFF4D4D4D, screenDensity * 10);
        labelScaleLinePaint = DIYViewUtil.createTextPaint(0xFF4D4D4D, screenDensity * 10);
        labelScaleLinePaint.setStrokeWidth(3);

    }

    /**
     * 设置底部 刻度类型
     * @param label
     */
    public void setDataType(IBottomLabel label) {
        this.label = label;
        invalidate();
    }

    /**
     * 设置 条形图的条的宽度
     * @param bar_width
     */
    public void setBarWidth(float bar_width) {
        this.bar_width = bar_width;
        invalidate();
    }

    /**
     * 设置数据
     * @param data
     */
    public void setData(List<BloodPressureItem> data) {
        this.data = data;
        invalidate();
    }


    /**
     * 设置分割线值
     * @param lines
     */
    public void setLimitLine(float... lines) {
        this.mLimitLines = lines;

        for (float line : lines) {
            minSystolicBloodPressure=minDiastolicBloodPressure = Math.min(minSystolicBloodPressure, line);
            maxSystolicBloodPressure=maxDiastolicBloodPressure = Math.max(maxSystolicBloodPressure, line);
        }


        maxTextString = String.valueOf(maxSystolicBloodPressure);
        if (hasDefLabel) {
            int i = Arrays.binarySearch(mLimitLines, maxSystolicBloodPressure);
            if (i >= 0) {
                maxTextString = String.valueOf(mLimitLinesLabel[i]);
            }
        }
        invalidate();
    }



    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (data == null) {
            return;
        }

        //==============================================================================================================================================
        //====================================================================窗体大小计算================================================================
        //==============================================================================================================================================

        DIYViewUtil.createRect(windowsRect, this);

        Rect textRect = DIYViewUtil.getTextRect(maxTextString, labelPaint);
        int padding = 30;
        int labelTextWidth = isDrawLabel ? textRect.width() : padding / 2;

        int mScaleLineRectHeight = (textRect.height() * 2);//底部刻度线高度 设置为文字高度两倍
        int mFoldPaddingSize = padding + padding + padding / 2 - ((int) (limitLinePaint.getStrokeWidth() / 2));
        DIYViewUtil.paddingRect(windowsRect, labelTextWidth + padding + (isDrawLabel ? padding : 0), padding, padding + padding, mScaleLineRectHeight + padding/*底部*/);//窗体padding  为了左右上下有padding 好看点
        if(isFoldPaddingEndModel)
        {
            DIYViewUtil.paddingRect(windowsRect, 0,0,mFoldPaddingSize,0);
        }
        //==============================================================================================================================================
        //====================================================================画分割线和右侧分割线的文本================================================================
        //==============================================================================================================================================



        int limitLineNum = Math.round(maxSystolicBloodPressure - minSystolicBloodPressure) + 1;
        float limitLineYOffset = windowsRect.height() * 1.0f / limitLineNum;
        for (int i = 0; i <= limitLineNum; i++) {
            float val = maxSystolicBloodPressure - i;
            //采用过滤的方式 图表中有 maxSystolicBloodPressure - minSystolicBloodPressure+1 个点 按窗口高度分等分 再过滤使用者传入的分割线值
            // 这样做的原因是 可以实现不按比例的分割线的显示 比如 10-20-30是传统方式,而现在可以支持10-15-16-17-30 这种乱序方式 于是更好的兼容使用者的需求变动
            int searchArrayIndex = Arrays.binarySearch(mLimitLines, val);//高性能搜索数组
            if (searchArrayIndex < 0) {//不是包含指定分割线的 忽略不画
                continue;//忽略
            }
            int half = padding / 2;

            float limitLineY = windowsRect.top + (limitLineYOffset * (i + 1)) + (limitLinePaint.getStrokeWidth() / 2);//分割线Y偏移  这个是一直按等分累加的
            //画limit分割线
            if (isDrawLimitLine) {
                //画limit分割线
                canvas.drawLine(windowsRect.left - padding/*恢复全部*/, limitLineY, windowsRect.right + padding/*恢复全部*/, limitLineY, limitLinePaint);
            } else if (isDrawBorder || isDrawZeroLimitLine) {//如要求只画(边框)0刻度 就画一条
                if (i == limitLineNum - 1) {
                    //画limit分割线
                    canvas.drawLine(windowsRect.left - padding/*恢复全部*/, limitLineY, windowsRect.right + padding/*恢复全部*/, limitLineY, limitLinePaint);
                }
            }

            if (isDrawBorder) {
                //画label垂直分割线(边框)
                canvas.drawLine(windowsRect.left - padding/*恢复全部*/, windowsRect.top, windowsRect.left - padding/*恢复全部*/, windowsRect.bottom, limitLinePaint);
            }
            if (isDrawLabel) {
                if (isDrawLabelLimit) {
                    //画limit分割刻度线
                    canvas.drawLine((windowsRect.left - padding/*恢复全部*/) - (half / 2/*一半的一半.为了居中..不知道咋解释,注释掉这个括号里的你就知道了*/), limitLineY, windowsRect.left - padding/*恢复全部*/, limitLineY, limitLinePaint);
                }

                String value;
                if (hasDefLabel) {//如果定义了自定义的Label 则使用Label
                    value = String.valueOf(mLimitLinesLabel[searchArrayIndex]);
                } else {
                    value = String.valueOf(Math.round(val));
                }

                Rect valRect = DIYViewUtil.getTextRect(value, labelPaint);
                //画Y轴左边文本
                canvas.drawText(value/*值*/, (windowsRect.left - padding) - valRect.width() - (/*线误差*/limitLinePaint.getStrokeWidth() / 2) - half, limitLineY + (textRect.height() / 2)/*文字高/2是为了居中于分割线 为了好看*/, labelPaint);
            }
        }
        //==============================================================================================================================================
        //====================================================================画主体曲线折线柱状图等================================================================
        //==============================================================================================================================================

        int size = data.size();
//        float stroke_width = barPaint.getStrokeWidth();
        float maxOffset = (windowsRect.width() * 1.0f) / ((size - 1 == 0) ? size : size - 1)/*格*/;

        if (bar_width <= 0) {
            //至少是2
            bar_width = maxOffset < 0 ? 2 : Math.round(maxOffset) + 1;
        }
        for (int i = 0; i < size; i++) {
            BloodPressureItem value = data.get(i);
            if (value == null || value.diastolicBloodPressure == 0) {//需要过滤掉0  华为会因为这个导致图表变乱 和底部白屏
                continue;
            }
            float ydbp = ((value.systolicBloodPressure - minSystolicBloodPressure) * 1.0f) / (Math.round(maxSystolicBloodPressure - minSystolicBloodPressure) + 1);
            float ysbp = ((value.diastolicBloodPressure - minDiastolicBloodPressure) * 1.0f) / (Math.round(maxDiastolicBloodPressure - minDiastolicBloodPressure) + 1);

            float cydbp = (windowsRect.height() * (1 - ydbp)) + windowsRect.top/*顶部是有偏移的*/;
            float cysbp = (windowsRect.height() * (1 - ysbp)) + windowsRect.top/*顶部是有偏移的*/;

            float cx = (maxOffset * i) + windowsRect.left;

                //这里多次使用setRectValue 是为了节省对象内存消耗 而不用每次都 new Rect()
            DIYViewUtil.setRectValue(barRect, cx - bar_width, cydbp , cx + bar_width, cysbp);

            //收缩压
            barPaint.setColor(COLOR_SYSTOLIC);
            //画柱状图
            DIYViewUtil.setRectValue(barTopRect,barRect.left, barRect.top, barRect.right, barRect.bottom - barRect.height() / 2);
            canvas.drawRect(barTopRect, barPaint);
            float radius = 2.5f * bar_width;
            canvas.drawCircle(barRect.centerX(),barRect.top, radius,barPaint);


            //舒张压

            barPaint.setColor(COLOR_DIASTOLIC);
            DIYViewUtil.setRectValue(barBottomRect,barRect.left, barTopRect.bottom, barRect.right, barRect.bottom);
            canvas.drawRect(barBottomRect, barPaint);
            canvas.drawCircle(barRect.centerX(),barRect.bottom, radius,barPaint);
        }

        //==============================================================================================================================================
        //====================================================================画底部刻度等================================================================
        //==============================================================================================================================================


        //确定底部 刻度位置的大小 供子类 随意自定义
        DIYViewUtil.setRectValue(windowsBottomScaleLineRect, windowsRect.left, windowsRect.bottom, windowsRect.right, windowsRect.bottom + mScaleLineRectHeight);
        if (label != null) {
            //供子类 随意自定义
            label.onDraw(this, canvas, windowsRect, data, labelScaleLinePaint, windowsBottomScaleLineRect);
        }
 
    }



    public static class BloodPressureItem{
        /**
         * 舒张压
         */
        int diastolicBloodPressure;
        /**
         * 收缩压
         */
        int systolicBloodPressure;

        /**
         * 正常血压收缩压是90-140之间，舒张压是60-90之间。
         * @param diastolicBloodPressure 舒张压
         * @param systolicBloodPressure 收缩压
         */
        public BloodPressureItem(int diastolicBloodPressure, int systolicBloodPressure) {
            this.diastolicBloodPressure = diastolicBloodPressure;
            this.systolicBloodPressure = systolicBloodPressure;
//            this.diastolicBloodPressure = diastolicBloodPressure;
//            this.systolicBloodPressure = systolicBloodPressure;
        }

        public int getDiastolicBloodPressure() {
            return diastolicBloodPressure;
        }

        public void setDiastolicBloodPressure(int diastolicBloodPressure) {
            this.diastolicBloodPressure = diastolicBloodPressure;
        }

        public int getSystolicBloodPressure() {
            return systolicBloodPressure;
        }

        public void setSystolicBloodPressure(int systolicBloodPressure) {
            this.systolicBloodPressure = systolicBloodPressure;
        }
    }
}
