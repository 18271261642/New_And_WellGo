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

import com.sn.utils.task.SNAsyncTask;
import com.sn.utils.task.SNVTaskCallBack;
import com.truescend.gofit.R;
import com.truescend.gofit.anim.ScalePageTransformer;
import com.truescend.gofit.utils.ResUtil;
import com.truescend.gofit.utils.SleepDataUtil;
import com.truescend.gofit.views.HorizontalPicker;
import com.truescend.gofit.views.QuickViewPager;
import com.truescend.gofit.views.SleepRatioView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * 作者:东芝(2018/8/17).
 * 功能:
 */

public class SleepDataPickerItem extends LinearLayout {


    private QuickViewPager mViewPager;
    private List<SleepDateItem> sleepDateItems = new ArrayList<>();
    private HorizontalPicker mHorizontalPicker;
    private TextView tvDate;
    private SimpleDateFormat mHorizontalPickerDateFormat = new SimpleDateFormat("MM/dd", Locale.ENGLISH);
    private SimpleDateFormat mDefDateFormat = new SimpleDateFormat("MM-dd", Locale.ENGLISH);
    private boolean isViewPagerDragging;
    private TextView tvSleepQuality;
    private TextView tvSleepValidTime;

    public SleepDataPickerItem(Context context) {
        super(context);
        init();
    }

    public SleepDataPickerItem(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SleepDataPickerItem(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public SleepDataPickerItem(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.item_sleep_views, this);


        mViewPager = (QuickViewPager) findViewById(R.id.viewpager);
        mHorizontalPicker = (HorizontalPicker) findViewById(R.id.mHorizontalPicker);
        tvDate = (TextView) findViewById(R.id.tvDate);
        tvSleepQuality = (TextView) findViewById(R.id.tvSleepQuality);
        tvSleepValidTime = (TextView) findViewById(R.id.tvSleepValidTime);


        //适配步骤, 因为逻辑比较绕,故写下以下计算步骤,以下各个定值均通过UI图纸测量得出
        //UI图w = 1080
        //中间的圆 w=340,h=340 ,左右两边两个的圆 w=216,h=216
        //取得实际屏幕宽度
        int widthPixels = getResources().getDisplayMetrics().widthPixels;
        //计算等比例的中间圆高度
        int centerViewHeight = 340 * widthPixels / 1080;
        //设置ViewPager高度, 此时ViewPager内的最大的View则按最高高度显示, 也就是说相当于间接设置了中间的View的高度
        mViewPager.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, centerViewHeight));


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
                if (mHorizontalPicker.getScrollState() == HorizontalPicker.SCROLL_STATE_IDLE) {
                    mHorizontalPicker.setCurrentItem(position + 1, positionOffset);
                }
                int left_pager = position - 1;
                View viewWithTag = mViewPager.findViewWithTag(left_pager);
                if (viewWithTag != null && viewWithTag instanceof SleepRatioView) {
                    SleepRatioView sleepRatioView = (SleepRatioView) viewWithTag;
                    if (!sleepRatioView.isEmptyData()) {
                        sleepRatioView.rePlayAnim();
                    }
                }
            }

            @Override
            public void onPageSelected(int position) {
                SleepDateItem item = sleepDateItems.get(position);
                updateItemText(item);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                isViewPagerDragging = state == ViewPager.SCROLL_STATE_DRAGGING;
            }
        });
        //HorizontalPicker 联动 ViewPager
        mHorizontalPicker.setSimpleOnScrollChangeListener(new HorizontalPicker.SimpleOnScrollChangeListener() {
            @Override
            public void onPageScrolled(int position, int positionOffsetPixels) {
                if (!isViewPagerDragging) {//反而注释掉更好看
                    mViewPager.setCurrentItem(position, true);
                }
            }
        });
    }

    private void updateItemText(SleepDateItem item) {
        tvDate.setText(mDefDateFormat.format(item.dateTimeMillis));
        float sleepQualityFloat = SleepDataUtil.getSleepQualityFloat(item.deepTime, item.lightTime, item.soberTime);
        tvSleepQuality.setText(SleepDataUtil.getSleepQualityStr(sleepQualityFloat));

        int sleepTimeValidTotal = SleepDataUtil.getSleepValidTotal(item.deepTime, item.lightTime);
        tvSleepValidTime.setText(ResUtil.format("%02d:%02d", sleepTimeValidTotal / 60, sleepTimeValidTotal % 60));
    }


    public QuickViewPager getViewPager() {
        return mViewPager;
    }

    public void setSleepDateItems(final List<SleepDateItem> items) {
        SNAsyncTask.execute(new SNVTaskCallBack() {
            List<String> data = new ArrayList<>();

            @Override
            public void run() throws Throwable {
                if (items.isEmpty()) {
                    return;
                }
                sleepDateItems.clear();
                sleepDateItems.addAll(items);
                Collections.sort(sleepDateItems, new Comparator<SleepDateItem>() {
                    @Override
                    public int compare(SleepDateItem o1, SleepDateItem o2) {
                        if (o1.dateTimeMillis - o2.dateTimeMillis > 0) {
                            return 1;
                        } else if (o1.dateTimeMillis - o2.dateTimeMillis < 0) {
                            return -1;
                        }
                        return 0;
                    }
                });
                data.clear();
                for (SleepDateItem item : sleepDateItems) {
                    data.add(mHorizontalPickerDateFormat.format(item.dateTimeMillis));
                }
            }

            @Override
            public void done() {
                if (mHorizontalPicker == null) return;
                if (mViewPager == null) return;

                mHorizontalPicker.setData(data);
                mHorizontalPicker.post(new Runnable() {
                    @Override
                    public void run() {
                        mHorizontalPicker.setCurrentItem(sleepDateItems.size() - 1, false);
                    }
                });
                //这里换成了 QuickViewPager ,自定义的ViewPager能实现快速滚动,但弱点是需要先设置有数据的Adapter
                //不能先设置无数据的adapter...  所以...  如果想换回官方ViewPager ,需要在这行设置notifyDataSetChanged,然后去掉这行的setAdapter 改成 init下设置setAdapter 即可
                mViewPager.setAdapter(adapter);
                //adapter.notifyDataSetChanged();
                mViewPager.setCurrentItem(sleepDateItems.size() - 1, false);
            }
        });


    }


    private final PagerAdapter adapter = new PagerAdapter() {


        @Override
        public int getCount() {
            return sleepDateItems.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View v = LayoutInflater.from(getContext()).inflate(R.layout.item_sleep_pager_views, null, false);

            SleepRatioView ratioView = v.findViewById(R.id.srvView);
            ratioView.setTag(position);


            TextView tvCenterText = v.findViewById(R.id.tvCenterText);

            SleepDateItem sleepDateItem = sleepDateItems.get(position);
            int time = sleepDateItem.deepTime + sleepDateItem.lightTime + sleepDateItem.soberTime;

            ratioView.setAnimOccupiesValues(Arrays.asList(
                    new SleepRatioView.OccupiesValue(0xFF6D3397, sleepDateItem.deepTime),
                    new SleepRatioView.OccupiesValue(0xFF8171CA, sleepDateItem.lightTime),
                    new SleepRatioView.OccupiesValue(0xFFB7BAE5, sleepDateItem.soberTime)
            ));

            tvCenterText.setText(String.format(Locale.ENGLISH, "%dh%dm", time / 60, time % 60));

            container.addView(v);
            return v;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

    };

    public static class SleepDateItem {
        public long dateTimeMillis;
        public int deepTime;
        public int lightTime;
        public int soberTime;

        public SleepDateItem(long dateTimeMillis, int deepTime, int lightTime, int soberTime) {
            this.dateTimeMillis = dateTimeMillis;
            this.deepTime = deepTime;
            this.lightTime = lightTime;
            this.soberTime = soberTime;
        }
    }


}

