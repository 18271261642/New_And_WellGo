package com.truescend.gofit.views.bean.base;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import java.util.List;

/**
 * 作者:东芝(2017/12/11).
 * 功能:底部绘制重定义
 */

public interface IBottomLabel {
    void onDraw(View v, Canvas canvas, Rect windowsRect, List<?> data, Paint labelScaleLinePaint, Rect windowsBottomScaleLineRect);
}
