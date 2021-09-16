package com.truescend.gofit.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;

import com.sn.utils.view.DIYViewUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者:东芝(2019/4/3).
 * 功能:颜色选择器
 */

public class ColorPickerView extends View {

    private Paint fillPaint;
    private Paint borderPaint;
    private Rect windowsRect;
    private Rect windowsSmallRect;
    private int colors[] = {0xFFFFFFFF, 0xFFFECCCD, 0xFFFD999A, 0xFFFD9927, 0xFF689ACA, 0xFF000000};
    private List<RectF> rectList = new ArrayList<>();
    private int position = 3;
    private int paddingTB;
    private int currentColor = colors[position];


    public ColorPickerView(Context context) {
        super(context);
        init();
    }

    public ColorPickerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ColorPickerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public ColorPickerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        fillPaint = DIYViewUtil.createFillPaint(0xffffffff);
        borderPaint = DIYViewUtil.createStrokePaint(0xff000000, 5);
    }


    public int getCurrentColor() {
        return colors[position];
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        windowsRect = DIYViewUtil.createRect(this);

        windowsSmallRect = DIYViewUtil.copyRect(windowsRect);
        paddingTB = Math.round(windowsSmallRect.height() * 0.2f);
        DIYViewUtil.paddingRect(windowsSmallRect, 0, paddingTB, 0, paddingTB);
        float w = (windowsSmallRect.width() - borderPaint.getStrokeWidth()) / colors.length;
        float offset = borderPaint.getStrokeWidth() / 2;
        if (rectList.size() != colors.length) {
            rectList.clear();

            for (int i = 0; i < colors.length; i++) {
                int color = colors[i];
                fillPaint.setColor(color);
                //请无视性能警告,因为上面已经判断了 这里只会进去一次,不会有性能问题
                rectList.add(new RectF(i * w + offset, windowsSmallRect.top, (i + 1) * w + offset, windowsSmallRect.bottom));
            }
        }
    }

    public void setPosition(int position) {
        this.position = position;
        invalidate();
    }

    public void setPositionFromColor(int color) {
        for (int i = 0; i < colors.length; i++) {
            int c = colors[i];
            if (c == color) {
                setPosition(i);
                break;
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {

        canvas.drawRect(windowsSmallRect, borderPaint);
        int size = rectList.size();
        if (size == colors.length) {
            RectF temp = null;
            for (int i = 0; i < size; i++) {
                RectF rectF = rectList.get(i);
                fillPaint.setColor(colors[i]);

                if (position == i) {
                    temp = DIYViewUtil.copyRect(rectF);
                    DIYViewUtil.paddingRect(temp, 0, -paddingTB, 0, -paddingTB);
                    canvas.drawRect(temp, fillPaint);
                } else {
                    canvas.drawRect(rectF, fillPaint);
                }
            }
            if (temp != null) {
                float paddingTB = borderPaint.getStrokeWidth() / 2;
                DIYViewUtil.paddingRect(temp, 0, paddingTB, 0, paddingTB);
                canvas.drawRect(temp, borderPaint);
            }
        }

    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                playSoundEffect(SoundEffectConstants.CLICK);
                for (int position = 0; position < rectList.size(); position++) {
                    RectF rectF = rectList.get(position);
                    RectF temp = DIYViewUtil.copyRect(rectF);
                    DIYViewUtil.paddingRect(temp, 0, -paddingTB, 0, -paddingTB);
                    if (temp.contains(event.getX(), event.getY())) {
                        this.position = position;
                        invalidate();
                    }
                }
                if (listener != null && this.position != -1) {
                    currentColor = colors[this.position];
                    listener.onItemSelected(this.position, currentColor);
                }

                break;
            case MotionEvent.ACTION_MOVE:

                break;
            case MotionEvent.ACTION_UP:

                break;
        }
        return true;
    }

    private OnColorItemPickListener listener;

    public void setOnColorItemPickListener(OnColorItemPickListener listener) {
        this.listener = listener;
    }

    public interface OnColorItemPickListener {
        void onItemSelected(int position, int color);
    }
}
