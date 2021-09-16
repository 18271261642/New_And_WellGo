package com.truescend.gofit.views.bean;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextUtils;
import android.view.View;

import com.sn.utils.view.DIYViewUtil;
import com.truescend.gofit.views.bean.base.IBottomLabel;

import java.util.List;

/**
 * 作者:东芝(2019/01/04).
 * 功能:范围
 */
public class LabelRange implements IBottomLabel {
    private String[][] ranges;


    public LabelRange(String[][] ranges) {
        this.ranges = ranges;
    }


    @Override
    public void onDraw(View v, Canvas canvas, Rect windowsRect, List<?> data, Paint labelScaleLinePaint, Rect windowsBottomScaleLineRect) {
        labelScaleLinePaint.setTextSize(windowsBottomScaleLineRect.width() * 0.028f);

        int time = ranges.length;
        float xOffset = windowsBottomScaleLineRect.width() * 1.0f / (time - 1);
        float startY = 0;
        float stopY = 0;
        for (int i = 0; i < time; i++) {
            int left = windowsBottomScaleLineRect.left;
            float startX = xOffset * (i) + left;

            String mTopText = ranges[i][0];
            Rect mTopTextRect = DIYViewUtil.getTextRect(String.valueOf(mTopText), labelScaleLinePaint);
            int topY = windowsBottomScaleLineRect.top + mTopTextRect.height() + 10;
            canvas.drawText(mTopText/*值*/, startX - (mTopTextRect.width() / 2), topY, labelScaleLinePaint);

            String mBottomText = ranges[i][1];
            Rect mBottomTextRect = DIYViewUtil.getTextRect(String.valueOf(mBottomText), labelScaleLinePaint);
            int bottomY = windowsBottomScaleLineRect.bottom - mBottomTextRect.height() / 2;
            canvas.drawText(mBottomText/*值*/, startX - (mBottomTextRect.width() / 2), bottomY, labelScaleLinePaint);


            if (startY == 0 || stopY == 0) {
                if(TextUtils.isEmpty(ranges[i][0])||TextUtils.isEmpty(ranges[i][1])){
                    continue;
                }
                startY = topY + mTopTextRect.height() / 2.5f;
                stopY = bottomY - 1.25f * mBottomTextRect.height();
            }


            float centerY = startY + (stopY - startY) / 2;
            if(!TextUtils.isEmpty(ranges[i][1])) {
                canvas.drawLine(startX - 5, centerY, startX + 5, centerY, labelScaleLinePaint);
            }


        }
    }
}
