package com.truescend.gofit.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.sn.blesdk.db.data.sleep.SleepBean;
import com.sn.utils.view.DIYViewUtil;
import com.truescend.gofit.R;

import java.util.List;


/**
 * 作者:东芝(2017/11/7).
 * 功能: 图表
 */
public class SleepChartView extends View {
    private Context context;
    private Paint limitLinePaint;
    private Paint labelPaint;
    private Paint labelScaleLinePaint;
    private Rect windowsBottomScaleLineRect = new Rect();//底部刻度线RECT范围
    private Rect windowsRect = new Rect();
    private float screenDensity;
    private String maxTextString = "100";
    private String[] indicatorLabel;
    private List<SleepItem> data;
    private Paint deepSleepPaint;
    private Paint lightSleepPaint;
    private Paint soberSleepPaint;
    private long maxDateTime;
    private RectF barRect = new RectF();
    private RectF lineRect = new RectF();

    public SleepChartView(Context context) {
        super(context);
        init();
    }

    public SleepChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SleepChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public SleepChartView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        indicatorLabel = getContext().getResources().getStringArray(R.array.sleep);
        screenDensity = DIYViewUtil.getScreenDensity(getResources());
        deepSleepPaint = DIYViewUtil.createFillPaint(getResources().getColor(R.color.colorSleepDeep));
        lightSleepPaint = DIYViewUtil.createFillPaint(getResources().getColor(R.color.colorSleepLight));
        soberSleepPaint = DIYViewUtil.createFillPaint(getResources().getColor(R.color.colorSleepSober));

        limitLinePaint = DIYViewUtil.createFillPaint(0xFFD9D9D9);
        limitLinePaint.setStrokeWidth(screenDensity);

        labelPaint = DIYViewUtil.createTextPaint(0xFF4D4D4D, screenDensity * 10);
        labelScaleLinePaint = DIYViewUtil.createTextPaint(0xFF4D4D4D, screenDensity * 10);
        labelScaleLinePaint.setStrokeWidth(3);


    /*    setTestData();*/

    }
/*
    *//**
     * 测试数据
     *//*
    private void setTestData() {
        List<SleepItem> data = new ArrayList<>();
        String dataJson = "[{\"deepTotal\":23,\"lightTotal\":109,\"soberTotal\":0,\"sleepDetailsBeans\":[{\"deep\":0,\"light\":70,\"sober\":0,\"beginDateTime\":\"2017-12-05 06:18:00\",\"endDateTime\":\"2017-12-05 07:37:00\",\"sleepData\":[{\"value\":70,\"minutes\":70,\"status\":0},{\"value\":0,\"minutes\":0,\"status\":0},{\"value\":0,\"minutes\":0,\"status\":0},{\"value\":0,\"minutes\":0,\"status\":0},{\"value\":0,\"minutes\":0,\"status\":0},{\"value\":0,\"minutes\":0,\"status\":0},{\"value\":0,\"minutes\":0,\"status\":0},{\"value\":0,\"minutes\":0,\"status\":0},{\"value\":0,\"minutes\":0,\"status\":0},{\"value\":0,\"minutes\":0,\"status\":0},{\"value\":0,\"minutes\":0,\"status\":0},{\"value\":0,\"minutes\":0,\"status\":0},{\"value\":0,\"minutes\":0,\"status\":0},{\"value\":0,\"minutes\":0,\"status\":0},{\"value\":0,\"minutes\":0,\"status\":0},{\"value\":0,\"minutes\":0,\"status\":0},{\"value\":0,\"minutes\":0,\"status\":0},{\"value\":0,\"minutes\":0,\"status\":0},{\"value\":0,\"minutes\":0,\"status\":0},{\"value\":0,\"minutes\":0,\"status\":0},{\"value\":0,\"minutes\":0,\"status\":0},{\"value\":0,\"minutes\":0,\"status\":0},{\"value\":0,\"minutes\":0,\"status\":0},{\"value\":0,\"minutes\":0,\"status\":0}]},{\"deep\":23,\"light\":39,\"sober\":0,\"beginDateTime\":\"2017-12-05 12:48:00\",\"endDateTime\":\"2017-12-05 13:59:00\",\"sleepData\":[{\"value\":39,\"minutes\":39,\"status\":0},{\"value\":16407,\"minutes\":23,\"status\":1},{\"value\":0,\"minutes\":0,\"status\":0},{\"value\":0,\"minutes\":0,\"status\":0},{\"value\":0,\"minutes\":0,\"status\":0},{\"value\":0,\"minutes\":0,\"status\":0},{\"value\":0,\"minutes\":0,\"status\":0},{\"value\":0,\"minutes\":0,\"status\":0},{\"value\":0,\"minutes\":0,\"status\":0},{\"value\":0,\"minutes\":0,\"status\":0},{\"value\":0,\"minutes\":0,\"status\":0},{\"value\":0,\"minutes\":0,\"status\":0},{\"value\":0,\"minutes\":0,\"status\":0},{\"value\":0,\"minutes\":0,\"status\":0},{\"value\":0,\"minutes\":0,\"status\":0},{\"value\":0,\"minutes\":0,\"status\":0},{\"value\":0,\"minutes\":0,\"status\":0},{\"value\":0,\"minutes\":0,\"status\":0},{\"value\":0,\"minutes\":0,\"status\":0},{\"value\":0,\"minutes\":0,\"status\":0},{\"value\":0,\"minutes\":0,\"status\":0},{\"value\":0,\"minutes\":0,\"status\":0},{\"value\":0,\"minutes\":0,\"status\":0},{\"value\":0,\"minutes\":0,\"status\":0}]}],\"id\":0,\"mac\":\"\",\"isUploaded\":false,\"date\":\"2017-12-05\"},{\"deepTotal\":120,\"lightTotal\":321,\"soberTotal\":7,\"sleepDetailsBeans\":[{\"deep\":120,\"light\":260,\"sober\":7,\"beginDateTime\":\"2017-12-05 23:51:00\",\"endDateTime\":\"2017-12-06 06:32:00\",\"sleepData\":[{\"value\":55,\"minutes\":55,\"status\":0},{\"value\":16410,\"minutes\":26,\"status\":1},{\"value\":3,\"minutes\":3,\"status\":0},{\"value\":16421,\"minutes\":37,\"status\":1},{\"value\":2,\"minutes\":2,\"status\":0},{\"value\":16419,\"minutes\":35,\"status\":1},{\"value\":7,\"minutes\":7,\"status\":0},{\"value\":32775,\"minutes\":7,\"status\":2},{\"value\":125,\"minutes\":125,\"status\":0},{\"value\":16406,\"minutes\":22,\"status\":1},{\"value\":68,\"minutes\":68,\"status\":0},{\"value\":0,\"minutes\":0,\"status\":0},{\"value\":0,\"minutes\":0,\"status\":0},{\"value\":0,\"minutes\":0,\"status\":0},{\"value\":0,\"minutes\":0,\"status\":0},{\"value\":0,\"minutes\":0,\"status\":0},{\"value\":0,\"minutes\":0,\"status\":0},{\"value\":0,\"minutes\":0,\"status\":0},{\"value\":0,\"minutes\":0,\"status\":0},{\"value\":0,\"minutes\":0,\"status\":0},{\"value\":0,\"minutes\":0,\"status\":0},{\"value\":0,\"minutes\":0,\"status\":0},{\"value\":0,\"minutes\":0,\"status\":0},{\"value\":0,\"minutes\":0,\"status\":0}]},{\"deep\":0,\"light\":61,\"sober\":0,\"beginDateTime\":\"2017-12-06 06:36:00\",\"endDateTime\":\"2017-12-06 07:41:00\",\"sleepData\":[{\"value\":61,\"minutes\":61,\"status\":0},{\"value\":0,\"minutes\":0,\"status\":0},{\"value\":0,\"minutes\":0,\"status\":0},{\"value\":0,\"minutes\":0,\"status\":0},{\"value\":0,\"minutes\":0,\"status\":0},{\"value\":0,\"minutes\":0,\"status\":0},{\"value\":0,\"minutes\":0,\"status\":0},{\"value\":0,\"minutes\":0,\"status\":0},{\"value\":0,\"minutes\":0,\"status\":0},{\"value\":0,\"minutes\":0,\"status\":0},{\"value\":0,\"minutes\":0,\"status\":0},{\"value\":0,\"minutes\":0,\"status\":0},{\"value\":0,\"minutes\":0,\"status\":0},{\"value\":0,\"minutes\":0,\"status\":0},{\"value\":0,\"minutes\":0,\"status\":0},{\"value\":0,\"minutes\":0,\"status\":0},{\"value\":0,\"minutes\":0,\"status\":0},{\"value\":0,\"minutes\":0,\"status\":0},{\"value\":0,\"minutes\":0,\"status\":0},{\"value\":0,\"minutes\":0,\"status\":0},{\"value\":0,\"minutes\":0,\"status\":0},{\"value\":0,\"minutes\":0,\"status\":0},{\"value\":0,\"minutes\":0,\"status\":0},{\"value\":0,\"minutes\":0,\"status\":0}]}],\"id\":0,\"mac\":\"\",\"isUploaded\":false,\"date\":\"2017-12-06\"}]";
        ArrayList<SleepBean> list = JsonUtil.toBean(dataJson, new TypeToken<ArrayList<SleepBean>>() {
        }.getType());
        SleepBean sleepBean = list.get(1);


        String jsonData = "[{\"beginVal\":1512489060000,\"endVal\":1512492360000,\"sleepType\":0},{\"beginVal\":1512492360000,\"endVal\":1512493920000,\"sleepType\":1},{\"beginVal\":1512493920000,\"endVal\":1512494100000,\"sleepType\":0},{\"beginVal\":1512494100000,\"endVal\":1512496320000,\"sleepType\":1},{\"beginVal\":1512496320000,\"endVal\":1512496440000,\"sleepType\":0},{\"beginVal\":1512496440000,\"endVal\":1512498540000,\"sleepType\":1},{\"beginVal\":1512498540000,\"endVal\":1512498960000,\"sleepType\":0},{\"beginVal\":1512498960000,\"endVal\":1512499380000,\"sleepType\":2},{\"beginVal\":1512499380000,\"endVal\":1512506880000,\"sleepType\":0},{\"beginVal\":1512506880000,\"endVal\":1512508200000,\"sleepType\":1},{\"beginVal\":1512508200000,\"endVal\":1512512280000,\"sleepType\":0},{\"beginVal\":1512512280000,\"endVal\":1512512280000,\"sleepType\":0},{\"beginVal\":1512512280000,\"endVal\":1512512280000,\"sleepType\":0},{\"beginVal\":1512512280000,\"endVal\":1512512280000,\"sleepType\":0},{\"beginVal\":1512512280000,\"endVal\":1512512280000,\"sleepType\":0},{\"beginVal\":1512512280000,\"endVal\":1512512280000,\"sleepType\":0},{\"beginVal\":1512512280000,\"endVal\":1512512280000,\"sleepType\":0},{\"beginVal\":1512512280000,\"endVal\":1512512280000,\"sleepType\":0},{\"beginVal\":1512512280000,\"endVal\":1512512280000,\"sleepType\":0},{\"beginVal\":1512512280000,\"endVal\":1512512280000,\"sleepType\":0},{\"beginVal\":1512512280000,\"endVal\":1512512280000,\"sleepType\":0},{\"beginVal\":1512512280000,\"endVal\":1512512280000,\"sleepType\":0},{\"beginVal\":1512512280000,\"endVal\":1512512280000,\"sleepType\":0},{\"beginVal\":1512512280000,\"endVal\":1512512280000,\"sleepType\":0},{\"beginVal\":1512512280000,\"endVal\":1512512880000,\"sleepType\":-1},{\"beginVal\":1512512880000,\"endVal\":1512516540000,\"sleepType\":0},{\"beginVal\":1512516540000,\"endVal\":1512516540000,\"sleepType\":0},{\"beginVal\":1512516540000,\"endVal\":1512516540000,\"sleepType\":0},{\"beginVal\":1512516540000,\"endVal\":1512516540000,\"sleepType\":0},{\"beginVal\":1512516540000,\"endVal\":1512516540000,\"sleepType\":0},{\"beginVal\":1512516540000,\"endVal\":1512516540000,\"sleepType\":0},{\"beginVal\":1512516540000,\"endVal\":1512516540000,\"sleepType\":0},{\"beginVal\":1512516540000,\"endVal\":1512516540000,\"sleepType\":0},{\"beginVal\":1512516540000,\"endVal\":1512516540000,\"sleepType\":0},{\"beginVal\":1512516540000,\"endVal\":1512516540000,\"sleepType\":0},{\"beginVal\":1512516540000,\"endVal\":1512516540000,\"sleepType\":0},{\"beginVal\":1512516540000,\"endVal\":1512516540000,\"sleepType\":0},{\"beginVal\":1512516540000,\"endVal\":1512516540000,\"sleepType\":0},{\"beginVal\":1512516540000,\"endVal\":1512516540000,\"sleepType\":0},{\"beginVal\":1512516540000,\"endVal\":1512516540000,\"sleepType\":0},{\"beginVal\":1512516540000,\"endVal\":1512516540000,\"sleepType\":0},{\"beginVal\":1512516540000,\"endVal\":1512516540000,\"sleepType\":0},{\"beginVal\":1512516540000,\"endVal\":1512516540000,\"sleepType\":0},{\"beginVal\":1512516540000,\"endVal\":1512516540000,\"sleepType\":0},{\"beginVal\":1512516540000,\"endVal\":1512516540000,\"sleepType\":0},{\"beginVal\":1512516540000,\"endVal\":1512516540000,\"sleepType\":0},{\"beginVal\":1512516540000,\"endVal\":1512516540000,\"sleepType\":0},{\"beginVal\":1512516540000,\"endVal\":1512516540000,\"sleepType\":0},{\"beginVal\":1512516540000,\"endVal\":1512516540000,\"sleepType\":0}]";
        ArrayList<SleepItem> list = JsonUtil.toBean(jsonData, new TypeToken<ArrayList<SleepItem>>() {
        }.getType());
        setData(list);
    }*/


    public void setScaleLineLabelColor(int color){
        labelScaleLinePaint.setColor(color);
        invalidate();
    }
    public void setLabelColor(int color){
        labelPaint.setColor(color);
        invalidate();
    }
    /**
     * 设置数据
     *
     * @param data
     */
    public void setData(List<SleepItem> data) {
        this.data = data;
        maxDateTime = 0;
        for (SleepItem item : data) {
            maxDateTime += (item.endVal - item.beginVal);
        }

        invalidate();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (data == null) {
            return;
        }


        //==============================================================================================================================================
        //====================================================================窗体大小计算================================================================
        //==============================================================================================================================================

        DIYViewUtil.createRect(windowsRect, this);

        Rect textRect = DIYViewUtil.getTextRect(maxTextString, labelPaint);
        int padding = 30;
        int mScaleLineRectHeight = (textRect.height() * 2);//底部刻度线高度 设置为文字高度两倍
        DIYViewUtil.paddingRect(windowsRect, padding, padding, padding, mScaleLineRectHeight + padding/*底部*/);//窗体padding  为了左右上下有padding 好看点

        //确定底部 刻度位置的大小 供子类 随意自定义
        DIYViewUtil.setRectValue(windowsBottomScaleLineRect, windowsRect.left, windowsRect.bottom, windowsRect.right, windowsRect.bottom + mScaleLineRectHeight);
        //底部分割线
        canvas.drawLine(windowsBottomScaleLineRect.left, windowsBottomScaleLineRect.top, windowsBottomScaleLineRect.right, windowsBottomScaleLineRect.top, limitLinePaint);
        labelScaleLinePaint.setTextSize(windowsBottomScaleLineRect.width() * 0.035f);


        //==============================================================================================================================================
        //====================================================================画分段================================================================
        //==============================================================================================================================================
        int size = data.size();
        float yOffset = (windowsRect.height() * 1.0f) / indicatorLabel.length/*3种类型*/;
        float xv = (maxDateTime) * 1.0f / windowsRect.width();
        double firstOffset = 0;
        float lastRight = 0;
        String lastText = null;
        float lastX=0;
        float lastY=0;
        for (int i = 0; i < size; i++) {
            SleepItem sleepItem = data.get(i);

            double cur_st_w = sleepItem.beginVal * 1.0d / xv;
            double cur_et_w = sleepItem.endVal * 1.0d / xv;
            if (firstOffset == 0) {//存储第一次的偏移  画的时候需要减去它 才能从0开始
                firstOffset = cur_st_w;
            }

            float cy = 0;
            if (sleepItem.sleepType == SleepItem.ITEM_LIMIT) {
                //多段时的分割线
                DIYViewUtil.setRectValue(lineRect, ((float) (cur_st_w - firstOffset + padding)), windowsRect.top, ((float) (cur_et_w - firstOffset + padding)), windowsRect.bottom);
                canvas.drawLine(lineRect.centerX(), lineRect.top, lineRect.centerX(), lineRect.bottom, limitLinePaint);

            } else {
                Paint paint = null;
                switch (sleepItem.sleepType) {
                    case SleepItem.ITEM_LIGHT://浅睡
                        paint = lightSleepPaint;
                        cy = windowsRect.top + (1 * yOffset);
                        break;
                    case SleepItem.ITEM_DEEP://深睡
                        paint = deepSleepPaint;
                        cy = windowsRect.top;
                        break;
                    case SleepItem.ITEM_SOBER://醒着
                        paint = soberSleepPaint;
                        cy = windowsRect.top + (2 * yOffset);
                        break;

                }
                if (paint == null) continue;

                float left = (float) (cur_st_w - firstOffset + padding);
                float right = (float) (cur_et_w - firstOffset + padding);
                //计算如果出现精度丢失 则说明是在1分钟内,造一个最小像素的线
                if (left == right && cur_st_w != cur_et_w) {
                    right += 1;
                    left -= 1;
                }
                DIYViewUtil.setRectValue(barRect, left, cy, right, cy + yOffset);
                canvas.drawRect(barRect, paint);

                if (sleepItem.dateShowType != SleepItem.DATE_CENTER) {
                    String text = sleepItem.dateShowStr;
                    Rect mBottomScaleLineTextRect = DIYViewUtil.getTextRect(text, labelScaleLinePaint);
                    float y = windowsBottomScaleLineRect.centerY() + mBottomScaleLineTextRect.height();
                    switch (sleepItem.dateShowType) {
                        case SleepItem.DATE_BEGIN: {
                            float x = barRect.left;
                            lastRight = x + mBottomScaleLineTextRect.width();
                            lastText = text;
                            lastX = x;
                            lastY = y;

                            break;
                        }
                        case SleepItem.DATE_END: {
                            float x = barRect.right - mBottomScaleLineTextRect.width();
                            if (x < lastRight) {

                            } else {
                                if (lastText != null) {
                                    canvas.drawText(lastText/*值*/, lastX, lastY, labelScaleLinePaint);
                                }
                            }
                            canvas.drawText(text/*值*/, x, y, labelScaleLinePaint);
                            break;
                        }

                    }
                }

            }

        }
        //==============================================================================================================================================
        //====================================================================画主体曲线折线柱状图等================================================================
        //==============================================================================================================================================


        //==============================================================================================================================================
        //====================================================================画底部刻度等================================================================
        //==============================================================================================================================================


//        {//最右边
//            String text = dateTimes.get(0)[0];
//            Rect mBottomScaleLineTextRect = DIYViewUtil.getTextRect(text, labelScaleLinePaint);
//            canvas.drawText(text/*值*/, windowsBottomScaleLineRect.left, windowsBottomScaleLineRect.centerY() + mBottomScaleLineTextRect.height(), labelScaleLinePaint);
//        }
//
//        {
//            String text = dateTimes.get(dateTimes.size() - 1)[1];
//            Rect mBottomScaleLineTextRect = DIYViewUtil.getTextRect(text, labelScaleLinePaint);
//            canvas.drawText(text/*值*/, windowsBottomScaleLineRect.right - (mBottomScaleLineTextRect.width()), windowsBottomScaleLineRect.centerY() + mBottomScaleLineTextRect.height(), labelScaleLinePaint);
//        }


    }

    public static class SleepItem {
        /**
         * 浅睡
         */
        public final static int ITEM_LIGHT = SleepBean.SleepDetailsBean.SleepData.STATUS_LIGHT;
        /**
         * 深睡
         */
        public final static int ITEM_DEEP = SleepBean.SleepDetailsBean.SleepData.STATUS_DEEP;
        /**
         * 醒着
         */
        public final static int ITEM_SOBER = SleepBean.SleepDetailsBean.SleepData.STATUS_SOBER;
        /**
         * 分割线
         */
        public final static int ITEM_LIMIT = -1;
        //数据开始-中间-结尾
        public final static int DATE_BEGIN = -10;
        public final static int DATE_CENTER = -11;
        public final static int DATE_END = -12;

        int sleepType;
        int dateShowType;
        long beginVal;
        long endVal;
        String dateShowStr;

        public SleepItem(int sleepType, int dateShowType, String dateShowStr, long beginVal, long endVal) {
            this.sleepType = sleepType;
            this.dateShowType = dateShowType;
            this.dateShowStr = dateShowStr;
            this.beginVal = beginVal;
            this.endVal = endVal;
        }
    }

}
