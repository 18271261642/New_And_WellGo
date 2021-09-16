package com.truescend.gofit.pagers.friends.data.item;

import android.content.Context;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sn.app.db.data.config.bean.UnitConfig;
import com.sn.app.utils.AppUnitUtil;
import com.sn.utils.task.SNAsyncTask;
import com.sn.utils.task.SNVTaskCallBack;
import com.truescend.gofit.R;
import com.truescend.gofit.anim.ScalePageTransformer;
import com.truescend.gofit.utils.UnitConversion;
import com.truescend.gofit.views.CircleProgressBar;
import com.truescend.gofit.views.HorizontalPicker;
import com.truescend.gofit.views.QuickViewPager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * 作者:东芝(2018/8/17).
 * 功能:
 */

public class SportDataPickerItem extends LinearLayout {


    private QuickViewPager mViewPager;
    private List<SportDateItem> sportDateItems = new ArrayList<>();
    private HorizontalPicker mHorizontalPicker;
    private TextView tvDate;
    private SimpleDateFormat mHorizontalPickerDateFormat = new SimpleDateFormat("MM/dd", Locale.ENGLISH);
    private SimpleDateFormat mDefDateFormat = new SimpleDateFormat("MM-dd", Locale.ENGLISH);
    private boolean isViewPagerDragging;
    private TextView tvCalories;
    private TextView tvDistance;
    private TextView tvDistanceUnit;
    private boolean isMiles;

    public SportDataPickerItem(Context context) {
        super(context);
        init();
    }

    public SportDataPickerItem(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SportDataPickerItem(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public SportDataPickerItem(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.item_sport_views, this);


        mViewPager = (QuickViewPager) findViewById(R.id.viewpager);
        mHorizontalPicker = (HorizontalPicker) findViewById(R.id.mHorizontalPicker);
        tvDate = (TextView) findViewById(R.id.tvDate);
        tvCalories = (TextView) findViewById(R.id.tvCalories);
        tvDistance = (TextView) findViewById(R.id.tvDistance);
        tvDistanceUnit = (TextView) findViewById(R.id.tvDistanceUnit);


        //适配步骤, 因为逻辑比较绕,故写下以下计算步骤,以下各个定值均通过UI图纸测量得出
        //UI图w = 1080
        //中间的圆 w=340,h=340 ,左右两边两个的圆 w=216,h=216
        //取得实际屏幕宽度
        int widthPixels = getResources().getDisplayMetrics().widthPixels;
        //计算等比例的中间圆高度
        int centerViewHeight = 340 * widthPixels / 1080;
        //设置ViewPager高度, 此时ViewPager内的最大的View则按最高高度显示, 也就是说相当于间接设置了中间的View的高度
        mViewPager.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, centerViewHeight));


        //屏幕最左边到 最左边的小圆的最右边 测得为为266偏移量,同样,根据当前屏幕计算偏移量
        int leftOffset = 266 * widthPixels / 1080;
        //小圆 在当前屏幕实际宽度,因为是正方形, 所以宽高一样
        int smallViewWidth = 216 * widthPixels / 1080;
        //假设小圆不放大的情况下 在屏幕中间时,最左边与屏幕最左边的距离
        int smallViewLeftNeedOffset = widthPixels / 2 - smallViewWidth / 2;
        //需要的设计图的左偏移量对应实际屏幕的值+假设居中时的左误差值 恰好就是 需要小圆移动到的位置
        int smallViewMargin = leftOffset + smallViewLeftNeedOffset;
        //设置这个位置
        mViewPager.setPageMargin(-smallViewMargin);


        //放大倍数为,按设计图上看为 = 216/340
        float min_scale = 216.0f / 340.0f;
        mViewPager.setPageTransformer(true, new ScalePageTransformer(min_scale));

        mViewPager.setOffscreenPageLimit(3);


        //ViewPager 联动 HorizontalPicker
        mViewPager.addOnPageChangeListener(new QuickViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (mHorizontalPicker.getScrollState()== HorizontalPicker.SCROLL_STATE_IDLE) {
                    mHorizontalPicker.setCurrentItem(position + 1, positionOffset);
                }
                int left_pager = position ;
                View viewWithTag = mViewPager.findViewWithTag(left_pager);
                if (viewWithTag != null && viewWithTag instanceof CircleProgressBar) {
                    CircleProgressBar circleProgressBar = (CircleProgressBar) viewWithTag;
                     if (circleProgressBar.getProgress()!=0) {
                        circleProgressBar.rePlayAnim();
                     }
                }
            }

            @Override
            public void onPageSelected(int position) {
                SportDateItem item = sportDateItems.get(position);
                updateItemText(item);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                isViewPagerDragging = state == ViewPager.SCROLL_STATE_DRAGGING;
            }
        });
        //HorizontalPicker 联动 ViewPager
        mHorizontalPicker.setSimpleOnScrollChangeListener(new HorizontalPicker.SimpleOnScrollChangeListener(){
            @Override
            public void onPageScrolled(int position, int positionOffsetPixels) {
                if(!isViewPagerDragging) {//反而注释掉更好看
                    mViewPager.setCurrentItem(position, true);
                }
            }
        });
        SNAsyncTask.execute(new SNVTaskCallBack() {
            @Override
            public void run() throws Throwable {
                UnitConfig unitConfig = AppUnitUtil.getUnitConfig();
                //如果单位是英里,则需要转一下
                isMiles = unitConfig.distanceUnit == UnitConfig.DISTANCE_MILES;
            }
            @Override
            public void done() {

            }
        });
    }

    private void updateItemText(SportDateItem item) {
        tvDate.setText(mDefDateFormat.format(item.dateTimeMillis));
        tvCalories.setText(String.valueOf(item.calories));
        if (isMiles) {
            tvDistanceUnit.setText(R.string.unit_mile);
            tvDistance.setText(String.format(Locale.ENGLISH, "%.2f", UnitConversion.toBandK(UnitConversion.kmToMiles(item.distance))));
        } else {
            tvDistanceUnit.setText(R.string.unit_km);
            tvDistance.setText(String.format(Locale.ENGLISH, "%.2f", UnitConversion.toBandK(item.distance)));
        }
    }


    public QuickViewPager getViewPager() {
        return mViewPager;
    }

    public void setSportDateItems(final List<SportDateItem> items) {
        SNAsyncTask.execute(new SNVTaskCallBack() {
            List<String> data = new ArrayList<>();
            @Override
            public void run() throws Throwable {
                if(items.isEmpty()){
                    return;
                }
                sportDateItems.clear();
                sportDateItems.addAll(items);
                Collections.sort(sportDateItems, new Comparator<SportDateItem>() {
                    @Override
                    public int compare(SportDateItem o1, SportDateItem o2) {
                        if (o1.dateTimeMillis - o2.dateTimeMillis > 0) {
                            return 1;
                        } else if (o1.dateTimeMillis - o2.dateTimeMillis < 0) {
                            return -1;
                        }
                        return 0;
                    }
                });
                data.clear();
                for (SportDateItem item : sportDateItems) {
                    data.add(mHorizontalPickerDateFormat.format(item.dateTimeMillis));
                }

            }

            @Override
            public void done() {
                if(mHorizontalPicker==null)return;
                if(mViewPager==null)return;

                mHorizontalPicker.setData(data);
                mHorizontalPicker.post(new Runnable() {
                    @Override
                    public void run() {
                        mHorizontalPicker.setCurrentItem(sportDateItems.size() - 1,false);
                    }
                });
                //这里换成了 QuickViewPager ,自定义的ViewPager能实现快速滚动,但弱点是需要先设置有数据的Adapter
                //不能先设置无数据的adapter...  所以...  如果想换回官方ViewPager ,需要在这行设置notifyDataSetChanged,然后去掉这行的setAdapter 改成 init下设置setAdapter 即可
                mViewPager.setAdapter(adapter);
                //adapter.notifyDataSetChanged();
                mViewPager.setCurrentItem(sportDateItems.size() - 1,false);
            }
        });
    }




    private final PagerAdapter adapter = new PagerAdapter() {
        @Override
        public int getCount() {
            return sportDateItems.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View v = LayoutInflater.from(getContext()).inflate(R.layout.item_sport_pager_views, null, false);

            CircleProgressBar cpbStepProgressBar = v.findViewById(R.id.cpbStepProgressBar);
            cpbStepProgressBar.setTag(position);

            cpbStepProgressBar.setEndDotEnable(false);
            SportDateItem sportDateItem = sportDateItems.get(position);

            cpbStepProgressBar.setMaxProgress(sportDateItem.targetStep);
            cpbStepProgressBar.setProgress(sportDateItem.step);


            container.addView(v);
            return v;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

    };
    public static class SportDateItem {
        public long dateTimeMillis;
        public int step;
        public int calories;
        public int distance;
        public int targetStep;

        public SportDateItem(long dateTimeMillis, int step,int targetStep, int calories, int distance) {
            this.dateTimeMillis = dateTimeMillis;
            this.step = step;
            this.targetStep = targetStep;
            this.calories = calories;
            this.distance = distance;
        }
    }


}

