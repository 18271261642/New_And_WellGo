package com.truescend.gofit.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.sn.utils.view.DIYViewUtil;
import com.truescend.gofit.R;

/**
 * 作者:东芝(2019/4/11).
 * 功能:壁纸预览控件
 */

public class WallpaperPreviewView extends View {

    private Bitmap bmWallpaper;
    private Bitmap bmTime;
    private Bitmap bmStep;
    private Rect windowsRect;
    private Paint bmWallpaperPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
    private Paint bmTimePaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
    private Paint bmStepPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
    private RectF timeRectF = new RectF();
    private RectF stepRectF = new RectF();
    private int srcWallpaperWidth = 240;
    private int srcWallpaperHeight = 240;
    private int srcTimeWidth = 136;
    private int srcTimeHeight = 34;

    private int srcStepWidth = 92;
    private int srcStepHeight = 20;
    private float initialX;
    private float initialY;
    private float initialTouchX;
    private float initialTouchY;
    private float timeWidth;
    private float timeHeight;
    private float stepWidth;
    private float stepHeight;
    private int timeAndStepTBPadding = 10;
    private boolean isTouchTime;
    private boolean isStepEnable;
    private RectF all = new RectF();
    private OnPreviewViewLocationChangedListener listener;
    private Bitmap bmScreenTypeWhite;
    private Bitmap bmScreenTypeBlack;
    private int screenType;

    public WallpaperPreviewView(Context context) {
        super(context);
        init();
    }

    public WallpaperPreviewView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WallpaperPreviewView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public WallpaperPreviewView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr);
        init();
    }


    public void setWallpaperImage(Bitmap image) {
        this.bmWallpaper = image;
        invalidate();
    }

    public void setWallpaperImage(Bitmap image, int width, int height) {
        this.bmWallpaper = image;
        srcWallpaperWidth = width;
        srcWallpaperHeight = height;
        invalidate();
    }

    public Bitmap getWallpaperImageSrc() {
        return bmWallpaper;
    }

    public Bitmap getWallpaperImage() {
        if(screenType==0){
            return bmWallpaper;
        }
        Bitmap b1 = bmWallpaper;
        if (!b1.isMutable()) {
            //设置图片为背景为透明
            b1 = b1.copy(Bitmap.Config.ARGB_8888, true);
        }
        Paint paint = new Paint();
        Canvas canvas = new Canvas(b1);
        DIYViewUtil.drawBitmapFill(canvas, bmScreenTypeBlack, new RectF(0, 0, b1.getWidth(), b1.getHeight()), paint);
        canvas.save();
        canvas.restore();
        return b1;
    }

    public void setScreenType(int screenType) {
        this.screenType = screenType;
        final int screenTypeIcon;
        switch (screenType) {
            case 1:
                screenTypeIcon = R.mipmap.icon_wallpaper_screen_type_1;
                break;
            case 0:
            default:
                return;
        }

        if (windowsRect == null) {
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (windowsRect != null) {
                        bmScreenTypeBlack = BitmapFactory.decodeResource(getResources(), screenTypeIcon);
                        bmScreenTypeWhite = tintBitmap(bmScreenTypeBlack, 0xffffffff);
                        invalidate();
                    }
                }
            }, 500);
        } else {
            bmScreenTypeBlack = BitmapFactory.decodeResource(getResources(), screenTypeIcon);
            bmScreenTypeWhite = tintBitmap(bmScreenTypeBlack, 0xffffffff);
            invalidate();
        }


    }


    public void setTimeImage(Bitmap image, int width, int height) {
        this.bmTime = image;
        srcTimeWidth = width;
        srcTimeHeight = height;
        invalidate();
    }

    public void setStepImage(Bitmap image, int width, int height) {
        this.bmStep = image;
        srcStepWidth = width;
        srcStepHeight = height;
        invalidate();
    }

    public void setTimeSize(int width, int height) {
        srcTimeWidth = width;
        srcTimeHeight = height;
        calculateTimeWidget();
        boundaryRestrictions();
        invalidate();
    }

    public void setStepSize(int width, int height) {
        srcStepWidth = width;
        srcStepHeight = height;
        calculateStepWidget();
        boundaryRestrictions();
        invalidate();
    }


    public void setStepColor(int color) {
        bmStepPaint.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
        invalidate();
    }

    public void setTimeColor(int color) {
        bmTimePaint.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
        invalidate();
    }


    public void setTimeLocation(final float x, final float y) {
        if (windowsRect == null) {
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (windowsRect != null) {
                        timeRectF.left = x * windowsRect.width() / srcWallpaperWidth;
                        timeRectF.top = y * windowsRect.height() / srcWallpaperHeight;
                        invalidate();
                    }
                }
            }, 500);
        } else {
            timeRectF.left = x * windowsRect.width() / srcWallpaperWidth;
            timeRectF.top = y * windowsRect.height() / srcWallpaperHeight;
            invalidate();
        }

    }

    public PointF getTimeLocation() {
        if (windowsRect == null) {
            return new PointF(0, 0);
        }
        return new PointF(
                srcWallpaperWidth * timeRectF.left * 1.0f / windowsRect.width(),
                srcWallpaperHeight * timeRectF.top * 1.0f / windowsRect.height()
        );
    }

    public PointF getStepLocation() {
        if (windowsRect == null) {
            return new PointF(0, 0);
        }
        return new PointF(
                srcWallpaperWidth * stepRectF.left * 1.0f / windowsRect.width(),
                srcWallpaperHeight * stepRectF.top * 1.0f / windowsRect.height()
        );
    }

    public void setStepEnable(boolean enable) {
        isStepEnable = enable;
        post(new Runnable() {
            @Override
            public void run() {
                boundaryRestrictions();
                boundarySpecialShapedScreenRestrictions();
                invalidate();
            }
        });
        invalidate();
    }

    private void init() {

    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        windowsRect = DIYViewUtil.createRect(this);

    }

    public Bitmap tintBitmap(Bitmap inBitmap, int tintColor) {
        if (inBitmap == null) {
            return null;
        }
        Bitmap outBitmap = Bitmap.createBitmap(windowsRect.width(), windowsRect.height(), inBitmap.getConfig());
        Canvas canvas = new Canvas(outBitmap);
        Paint paint = new Paint();
        paint.setColorFilter(new PorterDuffColorFilter(tintColor, PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(inBitmap, new Rect(0, 0, inBitmap.getWidth(), inBitmap.getHeight()), new Rect(0, 0, outBitmap.getWidth(), outBitmap.getHeight()), paint);
        return outBitmap;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (bmWallpaper == null || bmTime == null || bmStep == null) {
            return;
        }

        DIYViewUtil.drawBitmapFill(canvas, bmWallpaper, windowsRect, bmWallpaperPaint);
        calculateTimeWidget();
        DIYViewUtil.drawBitmapFill(canvas, bmTime, timeRectF, bmTimePaint);
        calculateStepWidget();
        if (isStepEnable) {
            DIYViewUtil.drawBitmapFill(canvas, bmStep, stepRectF, bmStepPaint);
        }
        if (screenType != 0 && bmScreenTypeWhite != null) {
            DIYViewUtil.drawBitmapFill(canvas, bmScreenTypeWhite, windowsRect, bmWallpaperPaint);
        }
    }

    private void calculateStepWidget() {
        stepWidth = srcStepWidth * windowsRect.width() * 1.0f / srcWallpaperWidth;
        stepHeight = srcStepHeight * windowsRect.height() * 1.0f / srcWallpaperHeight;

        stepRectF.right = timeRectF.right;

        stepRectF.top = timeRectF.bottom + timeAndStepTBPadding;// 20的padding 为了好看点, 具体看需求要不要
        stepRectF.left = stepRectF.right - stepWidth;
        stepRectF.bottom = stepRectF.top + stepHeight;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                all.left = timeRectF.left;
                all.top = timeRectF.top;
                all.right = stepRectF.right;
                all.bottom = stepRectF.bottom;
                if (all.contains(x, y)) {
                    isTouchTime = true;
                    getParent().requestDisallowInterceptTouchEvent(true);
                    if (listener != null) {
                        listener.onStartDrag();
                    }
                }
                initialX = all.left;
                initialY = all.top;
                initialTouchX = x;
                initialTouchY = y;


                break;
            case MotionEvent.ACTION_MOVE:
                if (isTouchTime) {
                    timeRectF.left = initialX + x - initialTouchX;
                    timeRectF.top = initialY + y - initialTouchY;
                    boundaryRestrictions();
                    boundarySpecialShapedScreenRestrictions();
                    if (listener != null) {
                        listener.onChanged(getTimeLocation(), getStepLocation());
                    }
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                if (isTouchTime) {
                    boundaryRestrictions();
                    boundarySpecialShapedScreenRestrictions();
                    invalidate();
                }
                isTouchTime = false;
                getParent().requestDisallowInterceptTouchEvent(false);
                if (listener != null) {
                    listener.onStopDrag();
                }
                break;
        }
        return true;
    }

    private void boundarySpecialShapedScreenRestrictions() {
        if(screenType==0){
            return;
        }

        if (bmScreenTypeWhite != null && !bmScreenTypeWhite.isRecycled()) {
            try {
                int xLT = (int) all.left;
                int yLT = (int) all.top;

                int xLB = (int) timeRectF.left + 1;
                int yLB = (int) timeRectF.bottom - 1;

                int xRT = (int) timeRectF.right - 1;
                int yRT = (int) timeRectF.top + 1;
                int xRB = (int) timeRectF.right - 1;
                int yRB = (int) timeRectF.bottom - 1;

                if (isStepEnable) {
                    xRB = (int) all.right - 1;
                    yRB = (int) all.bottom - 1;
                }


                if (isBitmapValid(bmScreenTypeWhite, xLT, yLT)) {//左上角
                    int color = bmScreenTypeWhite.getPixel(xLT, yLT);
                    if (color == 0xFFFFFFFF) {
                        for (; xLT < bmScreenTypeWhite.getWidth() - all.width() && yLT < bmScreenTypeWhite.getHeight() - all.height(); xLT++, yLT++) {
                            if (isBitmapValid(bmScreenTypeWhite, xLT, yLT)) {
                                color = bmScreenTypeWhite.getPixel(xLT, yLT);
                                if (color == 0) {
                                    timeRectF.offsetTo(xLT, yLT);
                                    break;
                                }
                            }
                        }
                    }
                }
                if (isBitmapValid(bmScreenTypeWhite, xRT, yRT)) {//右上角
                    int color = bmScreenTypeWhite.getPixel(xRT, yRT);
                    if (color == 0xFFFFFFFF) {
                        for (; xRT > 0 && yRT < bmScreenTypeWhite.getHeight() - timeRectF.height(); xRT--, yRT++) {
                            if (isBitmapValid(bmScreenTypeWhite, xRT, yRT)) {
                                color = bmScreenTypeWhite.getPixel(xRT, yRT);
                                if (color == 0) {
                                    timeRectF.offsetTo(xRT - timeRectF.width(), yRT);
                                    break;
                                }
                            }
                        }
                    }
                }
                if (isBitmapValid(bmScreenTypeWhite, xLB, yLB)) {//左下角
                    int color = bmScreenTypeWhite.getPixel(xLB, yLB);
                    if (color == 0xFFFFFFFF) {
                        for (; xLB < bmScreenTypeWhite.getWidth() - timeRectF.width() && yLB > 0; xLB++, yLB--) {
                            if (isBitmapValid(bmScreenTypeWhite, xLB, yLB)) {
                                color = bmScreenTypeWhite.getPixel(xLB, yLB);
                                if (color == 0) {
                                    timeRectF.offsetTo(xLB, yLB - timeRectF.height());
                                    break;
                                }
                            }
                        }
                    }
                }

                if (isBitmapValid(bmScreenTypeWhite, xRB, yRB)) {//右下角
                    int color = bmScreenTypeWhite.getPixel(xRB, yRB);
                    if (color == 0xFFFFFFFF) {
                        for (; xRB > 0 && yRB > 0; xRB--, yRB--) {
                            if (isBitmapValid(bmScreenTypeWhite, xRB, yRB)) {
                                color = bmScreenTypeWhite.getPixel(xRB, yRB);
                                if (color == 0) {
                                    if (isStepEnable) {
                                        timeRectF.offsetTo(xRB - all.width(), yRB - all.height());
                                    } else {
                                        timeRectF.offsetTo(xRB - timeRectF.width(), yRB - timeRectF.height());
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }

            } catch (Exception ignored) {
            }
        }
    }

    private boolean isBitmapValid(Bitmap bm, float x, float y) {
        return x >= 0 && y >= 0 && x <= bm.getWidth() && y <= bm.getHeight();
    }

    private void calculateTimeWidget() {
        //假设手环支持240*240壁纸, 时间控件在240*240中 为 136*34 , 同理 步数控件为 92*20
        //分布在View上
        //  w? =    w136 * windowsRect.width() / w240
        //  h? =    h34 * windowsRect.height() / h240

        timeWidth = srcTimeWidth * windowsRect.width() * 1.0f / srcWallpaperWidth;
        timeHeight = srcTimeHeight * windowsRect.height() * 1.0f / srcWallpaperHeight;

        timeRectF.right = timeRectF.left + timeWidth;
        timeRectF.bottom = timeRectF.top + timeHeight;

    }

    /**
     * 边界限制
     */
    private void boundaryRestrictions() {
        if (timeRectF.left < 0) {
            timeRectF.left = 0;
        }
        if (timeRectF.left + timeWidth > windowsRect.right) {
            timeRectF.left = windowsRect.right - timeWidth;
        }
        if (timeRectF.top < 0) {
            timeRectF.top = 0;
        }
        if (isStepEnable) {
            if (timeRectF.top + timeHeight + stepHeight + timeAndStepTBPadding > windowsRect.bottom) {
                timeRectF.top = windowsRect.bottom - timeHeight - stepHeight - timeAndStepTBPadding;
            }
        } else {
            if (timeRectF.top + timeHeight > windowsRect.bottom) {
                timeRectF.top = windowsRect.bottom - timeHeight;
            }
        }
        calculateTimeWidget();
        calculateStepWidget();

        all.left = timeRectF.left;
        all.top = timeRectF.top;
        all.right = stepRectF.right;
        all.bottom = stepRectF.bottom;
    }


    public void setOnPreviewViewLocationChangedListener(OnPreviewViewLocationChangedListener listener) {
        this.listener = listener;
    }


    public interface OnPreviewViewLocationChangedListener {
        void onStartDrag();

        void onStopDrag();

        void onChanged(PointF time, PointF step);
    }
}
