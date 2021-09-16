package com.truescend.gofit.views.bean;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import com.sn.utils.view.DIYViewUtil;
import com.truescend.gofit.views.bean.base.IBottomLabel;

import java.util.List;

/**
 * 作者:东芝(2017/12/11).
 * 功能:年,刻度
 */
public class LabelYear implements IBottomLabel {

    @Override
    public void onDraw(View v, Canvas canvas, Rect windowsRect, List<?> data, Paint labelScaleLinePaint, Rect windowsBottomScaleLineRect) {
        labelScaleLinePaint.setTextSize(windowsBottomScaleLineRect.width() * 0.035f);
        int size = data.size();
        int time = size == 0 ? 12 : size;//12个月
        float xOffset = windowsBottomScaleLineRect.width() * 1.0f / (time - 1);
        for (int i = 0; i < time; i++) {

            int left = windowsBottomScaleLineRect.left;
            float startX = xOffset * (i) + left;
//            canvas.drawLine(startX, windowsBottomScaleLineRect.top, startX, windowsBottomScaleLineRect.bottom - windowsBottomScaleLineRect.height() * 0.7f, labelScaleLinePaint);
            String mBottomScaleLineText = String.valueOf(i + 1);
            Rect mBottomScaleLineTextRect = DIYViewUtil.getTextRect(String.valueOf(mBottomScaleLineText), labelScaleLinePaint);
            canvas.drawText(mBottomScaleLineText/*值*/, startX - (mBottomScaleLineTextRect.width() / 2), windowsBottomScaleLineRect.centerY() + mBottomScaleLineTextRect.height(), labelScaleLinePaint);
        }
    }
}
