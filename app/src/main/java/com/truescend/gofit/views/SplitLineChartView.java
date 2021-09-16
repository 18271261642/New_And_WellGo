package com.truescend.gofit.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.Region;
import android.util.AttributeSet;
import android.view.View;

import com.sn.utils.view.DIYViewUtil;
import com.truescend.gofit.views.bean.base.IBottomLabel;

import java.util.Arrays;
import java.util.List;

/**
 * 作者:东芝(2018/08/20).
 * 功能: 分段式渐变曲线图表...贼长
 */
public class SplitLineChartView extends View {

    private Paint limitLinePaint;
    private Paint labelPaint;
    private float[] mLimitLines;
    private float min;
    private float max;
    private Paint labelScaleLinePaint;
    private List<Integer> data;
    private Paint barPaint;
    private float bar_width;
    private IBottomLabel label;
    private Rect windowsBottomScaleLineRect = new Rect();//底部刻度线RECT范围
    private Rect windowsRect = new Rect();
    private boolean isDrawBorder = true;//画边框 包括0刻度线
    private boolean isDrawLimitLine = true;//画limit刻度线
    private boolean isDrawLabelLimit = true;//画label刻度线
    private boolean isDrawZeroLimitLine = true;//画0刻度线
    private boolean isDrawLabel = true;//画label (该参数影响着)isDrawLabelLimit
    private boolean isDrawLabelToRight = false;//label画到右边
    private boolean isFoldPaddingEndModel = false;//叠到其他我自己写的ViewView时的paddingEnd
    private boolean hasDefLabel;
    private String[] mLimitLinesLabel;
    private String maxTextString = "100";
    private int[] barColors = {0xFF000000, 0xFF000000, 0xFF000000, 0xEE000000, 0xDD000000, 0xCC000000, 0xBB000000, 0x55000000, 0x22000000};
    private float screenDensity;
    private LinearGradient linearGradient;


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
     * 0 刻度分割线
     *
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
     *
     * @param isDrawBorder
     */
    public void setDrawBorder(boolean isDrawBorder) {
        this.isDrawBorder = isDrawBorder;
        invalidate();
    }

    /**
     * 画分割线
     *
     * @param isDrawLimitLine
     */
    public void setDrawLimitLine(boolean isDrawLimitLine) {
        this.isDrawLimitLine = isDrawLimitLine;
        invalidate();
    }


    /**
     * 画左边 刻度文字
     *
     * @param isDrawLabel
     */
    public void setDrawLabel(boolean isDrawLabel) {
        this.isDrawLabel = isDrawLabel;
        invalidate();
    }


    /**
     * 画左边刻度文字的 分割线
     *
     * @param isDrawLabelLimit
     */
    public void setDrawLabelLimit(boolean isDrawLabelLimit) {
        this.isDrawLabelLimit = isDrawLabelLimit;
        invalidate();
    }

    public SplitLineChartView(Context context) {
        super(context);
        init();
    }

    public SplitLineChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SplitLineChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public SplitLineChartView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        screenDensity = DIYViewUtil.getScreenDensity(getResources());

        BAR_WIDTH_SMALL = screenDensity / 1.34f;
        BAR_WIDTH_NORMAL = screenDensity;
        BAR_WIDTH_MEDIUM = screenDensity * 2.5f;
        BAR_WIDTH_LARGER = screenDensity * 4;

        barPaint = DIYViewUtil.createFillPaint(0xFF000000);
        barPaint.setStyle(Paint.Style.FILL);
        barPaint.setStrokeWidth(screenDensity);

        limitLinePaint = DIYViewUtil.createFillPaint(0xFFD9D9D9);
        limitLinePaint.setStrokeWidth(screenDensity);

        labelPaint = DIYViewUtil.createTextPaint(0xFF4D4D4D, screenDensity * 10);
        labelScaleLinePaint = DIYViewUtil.createTextPaint(0xFF4D4D4D, screenDensity * 10);
        labelScaleLinePaint.setStrokeWidth(3);

    }


    public void setDrawLabelToRight(boolean drawLabelToRight) {
        isDrawLabelToRight = drawLabelToRight;
        invalidate();
    }

    /**
     * 设置底部 刻度类型
     *
     * @param label
     */
    public void setDataType(IBottomLabel label) {
        this.label = label;
        invalidate();
    }

    /**
     * 设置 条形图的条的宽度
     *
     * @param bar_width
     */
    public void setBarWidth(float bar_width) {
        this.bar_width = bar_width;
        invalidate();
    }


    /**
     * 设置数据
     *
     * @param data
     */
    public void setData(List<Integer> data) {
        this.data = data;
        //如果设置了自定义的Label 所以不需要计算最大最小值 反之,如果没设置自定义的Label 则需要计算
        if (!hasDefLabel) {
            min = Float.MAX_VALUE;
            max = -Float.MAX_VALUE;
            for (Integer line : data) {
                min = Math.min(min, line);
                max = Math.max(max, line);
            }
            if (min == Float.MAX_VALUE || max == -Float.MAX_VALUE) {
                min = 0;
                max = 0;
            }
            setLimitLine(min, max);
        }
        invalidate();
    }

    /**
     * 柱状图颜色, 一个则
     *
     * @param colors
     */
    public void setBarColor(final int... colors) {
        post(new Runnable() {
            @Override
            public void run() {
                //柱状图颜色
                if (colors.length == 1) {
                    barPaint.setColor(colors[0]);
                }
                barColors = colors;
                invalidate();
            }
        });


    }

    /**
     * 设置分割线值
     *
     * @param lines
     */
    public void setLimitLine(float... lines) {
        this.mLimitLines = lines;
        min = Float.MAX_VALUE;
        max = -Float.MAX_VALUE;
        for (float line : lines) {
            min = Math.min(min, line);
            max = Math.max(max, line);
        }
        if (min == Float.MAX_VALUE || max == -Float.MAX_VALUE) {
            min = 0;
            max = 0;
        }
        maxTextString = String.valueOf(max);
        if (hasDefLabel) {
            int i = Arrays.binarySearch(mLimitLines, max);
            if (i >= 0) {
                maxTextString = String.valueOf(mLimitLinesLabel[i]);
            }
        }
        invalidate();
    }

    /**
     * 设置分割线的值 对应的显示的文字label
     *
     * @param lines
     * @param label
     */
    public void setLimitLine(float[] lines, String[] label) {
        this.mLimitLinesLabel = label;
        if (mLimitLinesLabel != null) {
            if (label.length != lines.length) {
                throw new IndexOutOfBoundsException("float[] lines和String[] label这两个参数数量必须一致!");
            }
            hasDefLabel = true;
        }
        setLimitLine(lines);
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
        if (isDrawLabelToRight) {
            DIYViewUtil.paddingRect(windowsRect, padding + padding , padding, labelTextWidth + padding + (isDrawLabel ? padding : 0), mScaleLineRectHeight + padding/*底部*/);//窗体padding  为了左右上下有padding 好看点
            if(isFoldPaddingEndModel)
            {
                DIYViewUtil.paddingRect(windowsRect, mFoldPaddingSize,0,0,0);
            }
        } else {
            DIYViewUtil.paddingRect(windowsRect, labelTextWidth + padding + (isDrawLabel ? padding : 0), padding, padding + padding, mScaleLineRectHeight + padding/*底部*/);//窗体padding  为了左右上下有padding 好看点
            if(isFoldPaddingEndModel)
            {
                DIYViewUtil.paddingRect(windowsRect, 0,0,mFoldPaddingSize,0);
            }
        }



        //==============================================================================================================================================
        //====================================================================画分割线和右侧分割线的文本================================================================
        //==============================================================================================================================================


        int limitLineNum = Math.round(max - min) + 1;
        float limitLineYOffset = windowsRect.height() * 1.0f / limitLineNum;
        for (int i = 0; i <= limitLineNum; i++) {
            float val = max - i;
            //采用过滤的方式 图表中有 max - min+1 个点 按窗口高度分等分 再过滤使用者传入的分割线值
            // 这样做的原因是 可以实现不按比例的分割线的显示 比如 10-20-30是传统方式,而现在可以支持10-15-16-17-30 这种乱序方式 于是更好的兼容使用者的需求变动
            int searchArrayIndex = mLimitLines == null ? -1 : Arrays.binarySearch(mLimitLines, val);//高性能搜索数组
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
                if (isDrawLabelToRight) {
                    canvas.drawLine(windowsRect.right + padding/*恢复全部*/, windowsRect.top, windowsRect.right + padding/*恢复全部*/, windowsRect.bottom, limitLinePaint);
                } else {
                    canvas.drawLine(windowsRect.left - padding/*恢复全部*/, windowsRect.top, windowsRect.left - padding/*恢复全部*/, windowsRect.bottom, limitLinePaint);
                }
            }
            if (isDrawLabel) {
                if (isDrawLabelLimit) {
                    //画limit分割刻度线
                    if (isDrawLabelToRight) {
                        canvas.drawLine((windowsRect.right + padding/*恢复全部*/) + (half / 2/*一半的一半.为了居中..不知道咋解释,注释掉这个括号里的你就知道了*/), limitLineY, windowsRect.right + padding/*恢复全部*/, limitLineY, limitLinePaint);
                    } else {
                        canvas.drawLine((windowsRect.left - padding/*恢复全部*/) - (half / 2/*一半的一半.为了居中..不知道咋解释,注释掉这个括号里的你就知道了*/), limitLineY, windowsRect.left - padding/*恢复全部*/, limitLineY, limitLinePaint);
                    }
                }

                String value;
                if (hasDefLabel) {//如果定义了自定义的Label 则使用Label
                    value = String.valueOf(mLimitLinesLabel[searchArrayIndex]);
                } else {
                    value = String.valueOf(Math.round(val));
                }

                Rect valRect = DIYViewUtil.getTextRect(value, labelPaint);
                //画Y轴左边文本
                if (isDrawLabelToRight) {
                    canvas.drawText(value/*值*/, (windowsRect.right + padding) + labelTextWidth - padding - (/*线误差*/limitLinePaint.getStrokeWidth() / 2) - half, limitLineY + (textRect.height() / 2)/*文字高/2是为了居中于分割线 为了好看*/, labelPaint);
                } else {
                    canvas.drawText(value/*值*/, (windowsRect.left - padding) - valRect.width() - (/*线误差*/limitLinePaint.getStrokeWidth() / 2) - half, limitLineY + (textRect.height() / 2)/*文字高/2是为了居中于分割线 为了好看*/, labelPaint);
                }

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
        Path rpath = new Path();
        Path path = new Path();

        canvas.save();
        float cy = 0;
        float cx = 0;
        float leftFirstValue = 0, rightLastValue = 0;
        for (int i = 0; i < size; i++) {
            Integer value = data.get(i);
            if (value == null) {//需要过滤掉0  华为会因为这个导致图表变乱 和底部白屏
                continue;
            }
            float y = ((value - min) * 1.0f) / (Math.round(max - min) + 1);

            cy = (windowsRect.height() * (1 - y)) + windowsRect.top/*顶部是有偏移的*/;

            cx = (maxOffset * i) + windowsRect.left;
            if (leftFirstValue == 0 && value > 0) {
                leftFirstValue = cx;
            }
            if (value > 0) {
                rightLastValue = cx;
            }
            if (value == 0) {
                float strokeWidth = barPaint.getStrokeWidth();
                path.addRect(cx - maxOffset - strokeWidth, windowsRect.top - strokeWidth, cx + maxOffset + strokeWidth, windowsRect.bottom + strokeWidth, Path.Direction.CCW);
            }

            //画折线图 曲线 ,写到这里 王腾说换成柱状图... 于是注释掉 以后如果要用回来折线图 恢复这个注释即可

            if (i == 0) {
                //开头
                rpath.moveTo(0, windowsRect.bottom);
                if (value > 0) {
                    rpath.lineTo(0, cy);
                } else {
                    rpath.lineTo(0, windowsRect.bottom);
                }

            } else {
                addCurvedLine(rpath, cx, cy);
            }
            if (i == size - 1) {
                //结尾收紧
                addCurvedLine(rpath, windowsRect.right + padding + padding, cy);
                rpath.lineTo(windowsRect.right + padding + padding, windowsRect.bottom);
            }
        }

        //切掉 0 数据
        canvas.clipPath(path, Region.Op.DIFFERENCE);
        path.reset();

        //切掉左右的0数据 和超出的'假'线段
        path.addRect(leftFirstValue + barPaint.getStrokeWidth(), windowsRect.top, rightLastValue - barPaint.getStrokeWidth(), windowsRect.bottom, Path.Direction.CCW);
        canvas.clipPath(path);


        if (barColors.length > 0) {
            barPaint.setShader(null);
            barPaint.setStyle(Paint.Style.STROKE);
            barPaint.setColor(barColors[0]);
        }
        canvas.drawPath(rpath, barPaint);


        if (barColors != null && barColors.length > 1) {
            //华为手机得这么写,所以 警告就警告吧...
            linearGradient = new LinearGradient(0, 0, 0, windowsRect.height(), barColors, null, LinearGradient.TileMode.CLAMP);
            barPaint.setStyle(Paint.Style.FILL);
            barPaint.setShader(linearGradient);
        }
        rpath.close();
        canvas.drawPath(rpath, barPaint);
        canvas.restore();
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

    private float lx = -1, ly = -1;

    private void addCurvedLine(Path p, float cx, float cy) {
        if (lx != -1 && ly != -1) {   //画线
            //canvas.drawLine(lx, ly, cx, cy, barPaint);
            float wt = (lx + cx) / 2;
            PointF p3 = new PointF();
            PointF p4 = new PointF();
            p3.y = ly;
            p3.x = wt;
            p4.y = cy;
            p4.x = wt;
            p.lineTo(cx, cy);
//            p.cubicTo(p3.x, p3.y, p4.x, p4.y, cx, cy);
        }
        lx = cx;
        ly = cy;
    }


}
