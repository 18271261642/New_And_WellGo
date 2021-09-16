package com.truescend.gofit.views;

import android.content.Context;
import android.content.res.TypedArray;
import androidx.core.widget.NestedScrollView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.truescend.gofit.R;

/**
 * 作者:东芝(2018/4/14).
 * 功能:
 */

public class VerticalScrollView extends NestedScrollView {

    private View listView;
    private float mDownPosX = 0;
    private float mDownPosY = 0;
    private int scrollWithId;

    public VerticalScrollView(Context context) {
        super(context);
    }

    public VerticalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public VerticalScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.VerticalScrollView);
            scrollWithId = typedArray.getResourceId(R.styleable.VerticalScrollView_scrollWithId, NO_ID);
            typedArray.recycle();
        }
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (listView == null && scrollWithId != NO_ID) {
            listView = findViewById(scrollWithId);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        if (listView != null && checkArea(listView, ev)) {
            return false;
        }


        final float x = ev.getX();
        final float y = ev.getY();

        final int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mDownPosX = x;
                mDownPosY = y;

                break;
            case MotionEvent.ACTION_MOVE:
                final float deltaX = Math.abs(x - mDownPosX);
                final float deltaY = Math.abs(y - mDownPosY);
                // 这里是否拦截的判断依据是左右滑动，读者可根据自己的逻辑进行是否拦截

                if (deltaX > deltaY) {// 左右滑动不拦截
                    return false;
                }
        }

        return super.onInterceptTouchEvent(ev);
    }


    /**
     * 测试view是否在点击范围内
     *
     * @param v
     * @return
     */
    private boolean checkArea(View v, MotionEvent event) {
        float x = event.getRawX();
        float y = event.getRawY();
        int[] locate = new int[2];
        v.getLocationOnScreen(locate);
        int l = locate[0];
        int r = l + v.getWidth();
        int t = locate[1];
        int b = t + v.getHeight();
        if (l < x && x < r && t < y && y < b) {
            return true;
        }
        return false;
    }

}
