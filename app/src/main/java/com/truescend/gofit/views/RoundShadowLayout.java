package com.truescend.gofit.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * 作者:东芝(2017/3/27).
 */

public class RoundShadowLayout extends RelativeLayout {
    private static float K_VALUE = 20.0f;
    private Path path = new Path();
    private Region.Op op = Region.Op.REPLACE;
    private float rx = -1;
    private float ry = -1;


    public void setRadiuValue(float rx, float ry) {
        this.rx = rx;
        this.ry = ry;
        postInvalidate();
    }

    public RoundShadowLayout(Context context) {
        super(context);
        init();
    }

    public RoundShadowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RoundShadowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public RoundShadowLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr);
        init();

    }

    private void init() {
    }


    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        try {
            canvas.save();
            transform(canvas, child);
            return super.drawChild(canvas, child, drawingTime);
        } finally {
            canvas.restore();
        }
    }

    boolean transform(Canvas canvas, View child) {
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        path.reset();
        RectF rect = new RectF(0, 0, getWidth(), getHeight());
        if (rx == -1 && ry == -1) {
            float rx2 = rect.width() / K_VALUE;
            path.addRoundRect(rect, rx2, rx2, Path.Direction.CCW);
        } else {
            path.addRoundRect(rect, rx, ry, Path.Direction.CCW);
        }

        canvas.clipPath(path, op);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            child.invalidateOutline();
        }
        return true;
    }

}
