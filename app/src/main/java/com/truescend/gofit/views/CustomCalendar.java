package com.truescend.gofit.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;

import com.sn.utils.DateUtil;
import com.sn.utils.view.DIYViewUtil;
import com.truescend.gofit.R;
import com.truescend.gofit.views.bean.Day;
import com.truescend.gofit.views.bean.DayCompletion;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 功能：自定义日历视图
 * Author:Created by 泽鑫 on 2017/11/30 18:47.
 */

public class CustomCalendar extends View {
    private final float density;
    /**
     * 文字大小
     */
    private int weekTextSize;//星期文字大小
    private int dayTextSize;//日期文字大小
    /**
     * 背景颜色
     */
    private int weekTextColor;//星期文字颜色
    private int dayTextColor;//日期文字颜色
    private int circleBgColor;//充当背景圆环的颜色
    private int progressBarBgColor;//进度条颜色

    /**
     * 间距
     */
    private float weekSpacing;//星期间距
    /**
     * 高度
     */
    private float weekTextHeight;//星期高度
    private float dayTextHeight;//日期高度
    private float dayTextLeadingHeight;//文字基线以上的高度

    /**
     * 日期格子的宽高
     */
    private float columnWidthHeight;//日期宽高

    /**
     * 月和日
     */
    private int currentDay;//当前日期 xx天
    private int selectedDay;//选中的日期 第xx天

    private Date month;//当前月份
    private Calendar currentCalendar;//当前的年月
    private boolean isCurrentMonth;//是否是当前月份
//    private Map<Integer, DayCompletion> map;//存放每日进度条数据
    private SparseArray<DayCompletion> map;//存放每日进度条数据
    private List<Day> list;//存放绘制日期的列表
    private String[] NAME_OF_WEEK;//星期标题
    //圆环宽度
    private int ringWidth = 10;
    /**
     * 画笔
     */
    private Paint gPaint;//图形画笔
    private Paint tPaint;//文字画笔
    private boolean isActionDown;
    private float eventX;
    private float eventY;
    private int maxMoveValue = 10;
    //////////////////////////////////////////////////////////////////////////////////////////////////
    //------------------------------------------- 初始化 --------------------------------------------//
    //////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 初始化参数
     *
     * @param context      上下文参数
     * @param attrs        view参数数组
     * @param defStyleAttr 默认数组
     */
    private void initParameter(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.MyCustomCalendar, defStyleAttr, 0);
        int defSize = Math.round(density * 10);

        weekTextSize = array.getDimensionPixelSize(R.styleable.MyCustomCalendar_calendarWeekTextSize, Math.round(defSize * 1.5f));//星期文字大小
        dayTextSize = array.getDimensionPixelSize(R.styleable.MyCustomCalendar_calendarDayTextSize, defSize);//日期文字大小

        weekTextColor = array.getColor(R.styleable.MyCustomCalendar_calendarWeekTextColor, 0x000000);//星期文字颜色
        dayTextColor = array.getColor(R.styleable.MyCustomCalendar_calendarDayTextColor, 0x000000);//日期文字颜色
        circleBgColor = array.getColor(R.styleable.MyCustomCalendar_calendarCircleColor, 0xCCCCCC);//充当背景圆环的颜色
        progressBarBgColor = array.getColor(R.styleable.MyCustomCalendar_calendarProgressBarColor, 0x000000);//进度条颜色

        weekSpacing = array.getDimensionPixelSize(R.styleable.MyCustomCalendar_calendarWeekSpacing,0);
        array.recycle();
        NAME_OF_WEEK = context.getResources().getStringArray(R.array.week_day);
    }

    private void initConstant() {
//        map = new HashMap<>();
        map = new SparseArray<DayCompletion>();
        gPaint = new Paint();
        tPaint = new Paint();
        gPaint.setAntiAlias(true);//抗锯齿
        tPaint.setAntiAlias(true);//抗锯齿

        //获取星期高度
        tPaint.setTextSize(weekTextSize);
        weekTextHeight = DIYViewUtil.getFontHeight(tPaint);
        //获取日期文本高度
        tPaint.setTextSize(dayTextSize);
        dayTextHeight = DIYViewUtil.getFontHeight(tPaint);
        dayTextLeadingHeight = DIYViewUtil.getFontLeading(tPaint);

    }

    /**
     * 设置月份
     *
     * @param date 传过来的日期
     */
    private void initMonth(Calendar date) {
        currentCalendar = date;
        list = initDays(date);
        month = date.getTime();
        Calendar calendar = DateUtil.getCurrentCalendar();
        calendar.setTime(new Date());
        currentDay = calendar.get(Calendar.DAY_OF_MONTH);//获取今天的日期
        //如果是当前月
        if (DateUtil.equalsDate(DateUtil.YYYY_MM, date, calendar)) {
            isCurrentMonth = true;//设置是这个月为真
            selectedDay = currentDay;//默认选中今天
        } else {
            isCurrentMonth = false;//设置是这个月为假
            selectedDay = 0;//默认不选中任何天数
        }
        //设置日期为传进来的日期
        calendar.setTime(date.getTime());
    }

    /**
     * 填充某月的天数
     *
     * @param date 日期
     * @return 列表
     */
    private List<Day> initDays(Calendar date) {
        //当月的天数
        int currentDays = getMonthDays(date, 0);
        List<Day> dayList = new ArrayList<>();
        for (int i = 0; i < currentDays; i++) {
            date.set(Calendar.DAY_OF_MONTH, i + 1);
            Day day = new Day();
            day.setDay(i + 1);
            day.setDate(date.getTime());
            day.setMonth(date.get(Calendar.MONTH) + 1);
            day.setLine(date.get(Calendar.WEEK_OF_MONTH) - 1);
            day.setColumn(date.get(Calendar.DAY_OF_WEEK) - 1);
            dayList.add(day);
        }
        return dayList;
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////
    //------------------------------------------ 绘图方法 -------------------------------------------//
    //////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 获取某个月的天数
     *
     * @param calendar 当前月份
     * @return 天数
     */
    private int getMonthDays(Calendar calendar, int num) {
        calendar.add(Calendar.MONTH, num);
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    /**
     * 绘制星期文本
     *
     * @param canvas 画布
     */
    private void drawWeek(Canvas canvas) {
        tPaint.setTextSize(weekTextSize);
        tPaint.setColor(weekTextColor);
        for (int i = 0; i < NAME_OF_WEEK.length; i++) {
            //文字宽度
            float lengthOfWeek = DIYViewUtil.getFontLength(tPaint, NAME_OF_WEEK[i]);
            //文字高度
            float heightOfWeek = DIYViewUtil.getFontHeight(tPaint);
            //开始绘制的地方
            float startX = i * columnWidthHeight + (columnWidthHeight - lengthOfWeek) / 2;
            canvas.drawText(NAME_OF_WEEK[i], startX, weekTextHeight, tPaint);
        }
    }


    /**
     * 绘制日期
     *
     * @param canvas 画布
     */
    private void drawDay(Canvas canvas) {
        //实心圆的半径
        float radius = columnWidthHeight / 2 - 20;

        tPaint.setTextSize(dayTextSize);
        tPaint.setColor(Color.rgb(0, 0, 0));
        //绘制日期的开始高度
        float topHeight = weekTextHeight + weekSpacing;
        for (Day day : list) {
            //日期文字的长度
            float dayLengthString = DIYViewUtil.getFontLength(tPaint, day.getDay() + "");

            //圆心X点 = 第几列 * 宽度 + 每格宽度/2
            float radiusX = day.getColumn() * columnWidthHeight + columnWidthHeight/2;
            //圆心Y点 = 星期文本高度 + 日期和星期的间距 + 第几行 * 每行的高度 + 文字的高度（因为0行开始，所以+ 1/2 高度）
            float radiusY = topHeight + day.getLine() * columnWidthHeight + columnWidthHeight/2;

            //文字的绘制点
            float textX = radiusX - dayLengthString/2;
            float textY = radiusY - dayTextHeight/2 + dayTextLeadingHeight;


            //绘制灰色圆圈
            gPaint.setColor(circleBgColor);
            gPaint.setColor(Color.rgb(200, 200, 200));
            gPaint.setStrokeWidth(ringWidth);
            gPaint.setStyle(Paint.Style.STROKE);
            canvas.drawCircle(radiusX, radiusY, radius, gPaint);

            //获取当天完成目标的情况
            DayCompletion completion = map.get(day.getDay());
            float current = completion == null ? 0 : completion.getCurrentStep();//当前的步数
            float target = completion == null ? 10000 : completion.getTargetStep();//目标步数
            float angle = current / target * 360;//比例

            //绘制进度条
            RectF oval = new RectF(radiusX - radius, radiusY - radius, radiusX + radius, radiusY + radius);
            day.setRectF(new RectF(radiusX - radius - 20, radiusY - radius - 20, radiusX + radius + 20, radiusY + radius + 20));
            gPaint.setColor(progressBarBgColor);
            canvas.drawArc(oval, -90, angle, false, gPaint);

            tPaint.setTextSize(dayTextSize);
            if (isCurrentMonth && currentDay == day.getDay()) {
                gPaint.setColor(Color.rgb(0xcc, 0xcc, 0xcc));
                gPaint.setStyle(Paint.Style.FILL);
                //绘制当天的背景
                canvas.drawCircle(radiusX, radiusY, radius - ringWidth, gPaint);
            }
            if (selectedDay == day.getDay()) {
                //绘制被选中的背景
                tPaint.setColor(Color.rgb(0xff, 0xff, 0xff));
                gPaint.setColor(Color.rgb(0x0, 0x0, 0x0));
                gPaint.setStyle(Paint.Style.FILL);
                canvas.drawCircle(radiusX, radiusY, radius - ringWidth, gPaint);
            } else {
                tPaint.setColor(dayTextColor);
            }
            //绘制日期文字
            canvas.drawText(day.getDay() + "", textX, textY, tPaint);
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////
    //------------------------------------------ 点击事件 -------------------------------------------//
    //////////////////////////////////////////////////////////////////////////////////////////////////


    /**
     * 点击事件
     */
    private PointF focusPoint = new PointF();

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                isActionDown = true;
                eventX = event.getX();
                eventY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                //最大移动值10
                if (Math.abs(event.getX() - eventX) > maxMoveValue ||
                        Math.abs(event.getY() - eventY) > maxMoveValue) {
                    isActionDown = false;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (isActionDown) {
                    focusPoint.set(event.getX(), event.getY());
                    touchDay(focusPoint);
                }
                return true;
        }
        return true;
    }

    private void touchDay(PointF point) {
        for (Day day : list) {
            RectF rectF = day.getRectF();
            if (rectF!=null&&rectF.contains(point.x, point.y)) {
                setSelectedDay(day);
                break;
            }
        }
    }


    /**
     * 点击的日期
     *
     * @param day 对象
     */
    private void setSelectedDay(Day day) {
        selectedDay = day.getDay();
        invalidate();
        if (listener != null) {
            listener.onDayClick(day);
        }
    }

    /**
     * 获取当前的日历日期
     *
     * @return
     */
    public Calendar getCurrentCalendar() {
        return currentCalendar;
    }

    /**
     * 设置年月日期
     *
     * @param calendar 传进来的日期
     */
    public void setSelectedDate(Calendar calendar) {

        Calendar tempCalendar = DateUtil.getCurrentCalendar();
        tempCalendar.setTime(calendar.getTime());
        initMonth(tempCalendar);
        selectedDay = calendar.get(Calendar.DAY_OF_MONTH);
        map.clear();
        invalidate();
    }

    /**
     * 设置选择的日
     *
     * @param day 日期
     */
    public void setSelectedDay(int day) {
        selectedDay = day;
        invalidate();
    }

    /**
     * 月份变化
     *
     * @param change 变化值
     */
    public void monthChange(int change) {
        Calendar calendar = DateUtil.getCurrentCalendar();
        calendar.setTime(month);
        calendar.add(Calendar.MONTH, change);
        initMonth(calendar);
        map.clear();
        invalidate();
    }

    /**
     * 画进度条
     *
     * @param list 完成情况列表
     */
    public void drawProgress(List<DayCompletion> list) {
        if (list != null && list.size() > 0) {
            map.clear();
            for (DayCompletion finish : list) {
                map.put(finish.getDay(), finish);
            }
        }
        invalidate();
    }

    @Override
    public void invalidate() {
        requestLayout();
        super.invalidate();
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////
    //------------------------------------------- 正文类 --------------------------------------------//
    //////////////////////////////////////////////////////////////////////////////////////////////////
    public CustomCalendar(Context context) {
        this(context, null);
    }

    public CustomCalendar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomCalendar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        density = getResources().getDisplayMetrics().density;
        ringWidth = Math.round(density * 2);

        initParameter(context, attrs, defStyleAttr);
        initConstant();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        columnWidthHeight = widthSize / 7;//宽高 = 视图的宽度 / 7
        float height = weekTextHeight + weekSpacing + dayTextHeight + columnWidthHeight * 6 + columnWidthHeight / 2;
        setMeasuredDimension(getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec), (int) height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawWeek(canvas);
        drawDay(canvas);
    }

    private OnClickListener listener;

    public void setOnClickListener(OnClickListener listener) {
        this.listener = listener;
    }

    public interface OnClickListener {
        void onDayClick(Day day);
    }
}

