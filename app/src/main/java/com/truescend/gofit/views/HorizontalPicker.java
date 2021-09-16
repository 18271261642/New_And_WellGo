package com.truescend.gofit.views;


import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

/**
 * 作者:东芝(2018/8/17).
 * 功能:横向滚动选择器
 */

public class HorizontalPicker extends HorizontalScrollView {


    private static final int TEXT_COLOR_NORMAL = 0xffffffff;
    private static final int TEXT_COLOR_SELECTED = 0xff000000;
    private int textWidth;
    private LinearLayout child;
    private TextView currentTextView;
    private int currentItem = 0;
    private SimpleOnScrollChangeListener  listener;
    public static final int SCROLL_STATE_IDLE = 0;
    public static final int SCROLL_STATE_FLING = 1;


    private int scrollState = SCROLL_STATE_IDLE;
    private int scrollDealy = 50;
    private int currentX = -9999999;
    public void setSimpleOnScrollChangeListener(SimpleOnScrollChangeListener listener) {
        this.listener = listener;
    }

    public HorizontalPicker(Context context) {
        super(context);
        init();
    }

    public HorizontalPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HorizontalPicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public HorizontalPicker(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        child = new LinearLayout(getContext());
        addView(child);

        setHorizontalScrollBarEnabled(false);
    }


    public void setData(List<String> data){
        int widthPixels = getResources().getDisplayMetrics().widthPixels;
        if (child.getChildCount()>0) {
            child.removeAllViews();
        }
        final int maxDay = data.size() ;
        final int mLRNullTextCount = 3;
        for (int i = 0; i < maxDay + mLRNullTextCount + mLRNullTextCount; i++) {
            TextView textView = new TextView(getContext());
            if (i > mLRNullTextCount - 1 && i < maxDay + mLRNullTextCount) {
                final int position = i - mLRNullTextCount;
                textView.setTag(position);
                textView.setText(data.get(position));
                textView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (v.getTag()==null) {
                            return;
                        }
                        if(v.getTag() instanceof Integer)
                        {
                             int pos = (Integer) v.getTag();
                             setCurrentItem(pos, true);
                        }
                    }
                });
            } else {
                textView.setText(String.format(Locale.ENGLISH, "%02d/%02d", 0, 0));
                textView.setVisibility(INVISIBLE);
            }

            if(i== currentItem){
                textView.setTextColor(TEXT_COLOR_SELECTED);
            }else{
                textView.setTextColor(TEXT_COLOR_NORMAL);
            }
            textView.setTextSize(12);

            textWidth = getTextWidth(textView);
            float interval = (widthPixels - (textWidth * 7.0f)) / 7.0f;
            int lrPadding = Math.round(interval / 2);
            int tbPadding = widthPixels/70;
            textView.setPadding(lrPadding, tbPadding, lrPadding, tbPadding);
            textWidth = getTextWidth(textView);
            child.addView(textView);



        }

    }



    public void setCurrentItem(int position,float ratio){
        currentItem = position;
        final int i = position * textWidth;
        scrollTo(Math.round(i+ (textWidth*ratio))-textWidth, getScrollY());
        removeCallbacks(setCurrentItemAction);
    }

    public void setCurrentItem(int position, boolean smoothScroll) {
        currentItem = position;
        if (smoothScroll) {
            smoothScrollTo(position * textWidth, getScrollY());
        } else {
            scrollTo(position * textWidth, getScrollY());
        }
        removeCallbacks(setCurrentItemAction);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        currentItem = (l + (textWidth / 2)) / textWidth;

//        removeCallbacks(setCurrentItemAction);
//        postDelayed(setCurrentItemAction,500);


        if(currentTextView!=null){
            currentTextView.setTextColor(TEXT_COLOR_NORMAL);
        }
        currentTextView = (TextView) child.getChildAt(currentItem+3);
        currentTextView.setTextColor(TEXT_COLOR_SELECTED);

        if (listener != null) {
            listener.onPageScrolled(currentItem, l);
        }
    }

    public int getScrollState() {
        return scrollState;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_MOVE:
                scrollState = SCROLL_STATE_FLING;
                if(listener!=null){
                    listener.onPageScrollStateChanged(scrollState);
                }
                //手指在上面移动的时候   取消滚动监听线程
                removeCallbacks(scrollRunnable);
                break;
            case MotionEvent.ACTION_UP:
                //手指移动的时候
                post(scrollRunnable);

                removeCallbacks(setCurrentItemAction);
                postDelayed(setCurrentItemAction,500);
                break;
        }
        return super.onTouchEvent(ev);
    }
   private Runnable setCurrentItemAction = new Runnable() {
        @Override
        public void run() {
            int scrollX = getScrollX();
            int pos = (scrollX + (textWidth / 2)) / textWidth;
            setCurrentItem(pos, true);

            if (listener != null) {
                listener.onPageSelected(pos);
            }
        }
    };

    private int getTextWidth(TextView textView) {
        int widthMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        int heightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        textView.measure(widthMeasureSpec, heightMeasureSpec);
        return textView.getMeasuredWidth();
    }

    /**
     * 滚动监听runnable
     */
    private Runnable scrollRunnable = new Runnable() {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            if(getScrollX()==currentX){
                //滚动停止  取消监听线程
                scrollState = SCROLL_STATE_IDLE;
                if(listener!=null){
                    listener.onPageScrollStateChanged(scrollState);
                }
                removeCallbacks(this);
                return;
            }else{
                //手指离开屏幕    view还在滚动的时候
                scrollState = SCROLL_STATE_FLING;
                if(listener!=null){
                    listener.onPageScrollStateChanged(scrollState);
                }
            }
            currentX = getScrollX();
            postDelayed(this, scrollDealy);
        }
    };



    public static class SimpleOnScrollChangeListener  {
        public void onPageScrolled(int position,  int positionOffsetPixels) {

        }

        public void onPageSelected(int position) {
        }

        public void onPageScrollStateChanged(int state) {
        }
    }

}