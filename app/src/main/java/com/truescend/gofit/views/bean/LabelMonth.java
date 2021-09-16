package com.truescend.gofit.views.bean;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import com.sn.utils.view.DIYViewUtil;
import com.truescend.gofit.views.bean.base.IBottomLabel;

import java.util.Arrays;
import java.util.List;

/**
 * 作者:东芝(2017/12/11).
 * 功能:月,刻度
 */
public class LabelMonth implements IBottomLabel {
    //不能按比例,比如i%5==0则画,错误的,因为需求是要求乱序的
    private List<Integer> labels = Arrays.asList(1, 5, 10, 15, 20, 25, 30);
    private int pointY;
    private Integer[] months;

    public LabelMonth(Integer[] months) {
        this.months = months;
    }
    public LabelMonth( ) {

    }

    @Override
    public void onDraw(View v, Canvas canvas, Rect windowsRect, List<?> data, Paint labelScaleLinePaint, Rect windowsBottomScaleLineRect) {
        labelScaleLinePaint.setTextSize(windowsBottomScaleLineRect.width() * 0.026f);
        int size = data.size();
        int time = size == 0 ? 30 : size;

        float xOffset = windowsBottomScaleLineRect.width() * 1.0f / (time - 1);
        for (int i = 0; i < time; i++) {
            int left = windowsBottomScaleLineRect.left;
//                canvas.drawLine(startX, windowsBottomScaleLineRect.top, startX, windowsBottomScaleLineRect.bottom - windowsBottomScaleLineRect.height() * 0.7f, labelScaleLinePaint);
            float startX = xOffset * (i) + left;
            String mBottomScaleLineText = String.valueOf(months==null?i+1:months[i]);
            Rect mBottomScaleLineTextRect = DIYViewUtil.getTextRect(String.valueOf(mBottomScaleLineText), labelScaleLinePaint);
            if (labels.contains(i + 1)) {
                canvas.drawText(mBottomScaleLineText/*值*/, startX - (mBottomScaleLineTextRect.width() / 2), windowsBottomScaleLineRect.centerY() + mBottomScaleLineTextRect.height(), labelScaleLinePaint);
            } else {
                if (pointY == 0) {//因为以字体为居中标准 但是自动高度不是固定的(很轻微的误差 但我强迫症...)  所以以最初一个字体的高度基准来 昨晚圆点的y
                    pointY = windowsBottomScaleLineRect.centerY() + (mBottomScaleLineTextRect.height() / 2);
                }
                canvas.drawCircle(startX, pointY, 3, labelScaleLinePaint);
            }
        }
    }
}
