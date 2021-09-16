package com.truescend.gofit.views;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.sn.utils.view.DIYViewUtil;

/**
 * 作者:东芝(2018/8/27).
 * 功能:点赞按钮
 */

public class ThumbAnimImageView extends AppCompatImageView {

    private static int OFFSET = 20;
    private Rect windowsRect = new Rect();
    private Paint fillPaint;
    private Paint fillPaint2;
    private int padding = 0;
    private int paddingBig = 0;
    private ValueAnimator animator = null;
    private Bitmap unSelectedBitmap;
    private Bitmap selectedBitmap;


    public ThumbAnimImageView(Context context) {
        super(context);
        init();
    }


    public ThumbAnimImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();

    }

    public ThumbAnimImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public ThumbAnimImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        fillPaint = DIYViewUtil.createFillPaint(0xffffffff);
        fillPaint2 = DIYViewUtil.createFillPaint(0xffffffff);

        selectedBitmap = ((BitmapDrawable) getDrawable()).getBitmap() ;
        unSelectedBitmap = tintBitmap(selectedBitmap , 0xFFD8D2D5);
        setSelected(false);
    }


    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        invalidate();
    }

    private Bitmap tintBitmap(Bitmap inBitmap,@ColorInt int tintColor) {
        if (inBitmap == null) {
            return null;
        }
        Bitmap outBitmap = Bitmap.createBitmap(inBitmap.getWidth(), inBitmap.getHeight(), inBitmap.getConfig());
        Canvas canvas = new Canvas(outBitmap);
        Paint paint = new Paint();
        paint.setColorFilter(new PorterDuffColorFilter(tintColor, PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(inBitmap, 0, 0, paint);

        return outBitmap;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        DIYViewUtil.createRect(windowsRect, this);

        OFFSET = Math.round(windowsRect.width() * 0.2f);

        DIYViewUtil.paddingRect(windowsRect, OFFSET);

        Rect bmRect = DIYViewUtil.copyRect(windowsRect);
        DIYViewUtil.paddingRect(bmRect, padding);

        Bitmap bitmap = isSelected() ? selectedBitmap : unSelectedBitmap;
        if (canDrawBitmap(bitmap)) {
            DIYViewUtil.drawBitmapFill(canvas, bitmap, bmRect, fillPaint);
        }

        Rect bmRectBig = DIYViewUtil.copyRect(windowsRect);
        DIYViewUtil.paddingRect(bmRectBig, paddingBig);

        if (canDrawBitmap(bitmap)) {
            DIYViewUtil.drawBitmapFill(canvas, bitmap, bmRectBig, fillPaint2);
        }
    }

    private boolean canDrawBitmap(Bitmap bm) {
        return bm != null && !bm.isRecycled();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                setSelected(true);
                if (animator != null) {
                    animator.removeAllUpdateListeners();
                    animator.cancel();
                    animator = null;
                }
                animator = ValueAnimator.ofInt(0, -OFFSET * 2);
                animator.setDuration(500);
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        paddingBig = (int) animation.getAnimatedValue();
                        int v = Math.abs(paddingBig) * 255 / (OFFSET * 2);
                        fillPaint2.setAlpha(255 - v);
                        invalidate();
                    }
                });
                animator.start();
                break;

        }
        return super.onTouchEvent(event);
    }


}
