package com.truescend.gofit.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.truescend.gofit.R;
import com.truescend.gofit.views.QuickViewPager;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;

import static androidx.viewpager.widget.ViewPager.SCROLL_STATE_DRAGGING;
import static androidx.viewpager.widget.ViewPager.SCROLL_STATE_IDLE;
import static androidx.viewpager.widget.ViewPager.SCROLL_STATE_SETTLING;


/**
 * 作者:东芝(2016/12/2).
 */

public class TabLayoutManager {

    public interface TabIconPagerAdapter {

        /**
         * Define a Drawable for a tab icon at a given position.
         * Use a StateListDrawable for pre-lollipop devices (tinting bug in SDK &lt;= 24).
         *
         * @param position tab position
         * @return Tab Icon Drawable
         * @see <a href="http://stackoverflow.com/questions/30828951/tab-with-icon-using-tablayout-in-android-design-library">stackoverflow 1</a>
         * @see <a href="http://stackoverflow.com/questions/30872101/drawablecompat-tinting-does-not-work-on-pre-lollipop">stackoverflow 2</a>
         */
        @Nullable
        Drawable getPageTitleIconDrawable(int position);


        /**
         * Define a DrawableRes for a tab icon at a given position.
         * Use a StateListDrawable for pre-lollipop devices (tinting bug in SDK &lt;= 24).
         *
         * @param position tab position
         * @return Tab Icon Drawable Id
         * @see <a href="http://stackoverflow.com/questions/30828951/tab-with-icon-using-tablayout-in-android-design-library">stackoverflow 1</a>
         * @see <a href="http://stackoverflow.com/questions/30872101/drawablecompat-tinting-does-not-work-on-pre-lollipop">stackoverflow 2</a>
         */
        @DrawableRes
        int[] getPageTitleIconDrawableRes(int position);

    }

    public static StateListDrawable addStateListBgDrawable(Context context,
                                                           int idNormal, int idSelectOrPressed) {
        StateListDrawable drawable = new StateListDrawable();

        drawable.addState(new int[]{android.R.attr.state_selected}, context
                .getResources().getDrawable(idSelectOrPressed));
        drawable.addState(new int[]{android.R.attr.state_pressed}, context
                .getResources().getDrawable(idSelectOrPressed));
        drawable.addState(new int[]{android.R.attr.state_enabled}, context
                .getResources().getDrawable(idNormal));
        drawable.addState(new int[]{},
                context.getResources().getDrawable(idNormal));
        return drawable;
    }

    /**
     * TabLayout Setup.
     *
     * @param tabLayout TabLayout
     * @param viewPager ViewPager
     */
    public static void setupWithViewPager(final TabLayout tabLayout, final ViewPager viewPager) {
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        PagerAdapter adapter = viewPager.getAdapter();
        Context context = tabLayout.getContext();
        for (int i = 0; i < adapter.getCount(); i++) {
            int icon[] = ((TabIconPagerAdapter) adapter).getPageTitleIconDrawableRes(i);
            CharSequence tabDescription = adapter.getPageTitle(i);

            LinearLayout root = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.layout_tabs_custom_indicator, null);
            TextView tabText = root.findViewById(R.id.text1);
            tabText.setText(tabDescription);
            tabText.setTextColor(tabLayout.getTabTextColors());
            ImageView tabIcon = root.findViewById(R.id.icon);
            tabIcon.setImageDrawable(addStateListBgDrawable(context, icon[0], icon[1]));


            //修复TabLayout布局错误 item的高度显示不全(不管你 换成xml TabItem的方式,customView方式, TextView+topDrawable的方式 来实现图标+文本的item 然而 item的高度仍然是显示不全! ),
            // 研究后发现 需要重新计算高度,设定tabLayout的LayoutParams
            int width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            int height = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            root.measure(width, height);
            int h = root.getMeasuredHeight() + 20;//20是padding
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, h);
            tabLayout.setLayoutParams(params);
            root.setLayoutParams(params);

            tabLayout.addTab(tabLayout.newTab().setCustomView(root));
        }
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));


    }

    /**
     * TabLayout Setup.
     *
     * @param tabLayout TabLayout
     * @param viewPager ViewPager
     */
    public static void setupWithViewPager(final TabLayout tabLayout, final QuickViewPager viewPager) {
        viewPager.addOnPageChangeListener(new QuickTabLayoutOnPageChangeListener(tabLayout));

        PagerAdapter adapter = viewPager.getAdapter();
        Context context = tabLayout.getContext();
        for (int i = 0; i < adapter.getCount(); i++) {
            int icon[] = ((TabIconPagerAdapter) adapter).getPageTitleIconDrawableRes(i);
            CharSequence tabDescription = adapter.getPageTitle(i);

            LinearLayout root = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.layout_tabs_custom_indicator, null);
            TextView tabText = root.findViewById(R.id.text1);
            tabText.setText(tabDescription);
            tabText.setTextColor(tabLayout.getTabTextColors());
            ImageView tabIcon = root.findViewById(R.id.icon);
            tabIcon.setImageDrawable(addStateListBgDrawable(context, icon[0], icon[1]));


            //修复TabLayout布局错误 item的高度显示不全(不管你 换成xml TabItem的方式,customView方式, TextView+topDrawable的方式 来实现图标+文本的item 然而 item的高度仍然是显示不全! ),
            // 研究后发现 需要重新计算高度,设定tabLayout的LayoutParams
            int width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            int height = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            root.measure(width, height);
            int h = root.getMeasuredHeight() + 20;//20是padding
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, h);
            tabLayout.setLayoutParams(params);
            root.setLayoutParams(params);

            tabLayout.addTab(tabLayout.newTab().setCustomView(root));
        }
        tabLayout.addOnTabSelectedListener(new QuickViewPagerOnTabSelectedListener(viewPager));
    }

    public static class QuickTabLayoutOnPageChangeListener implements QuickViewPager.OnPageChangeListener {
        private final WeakReference<TabLayout> mTabLayoutRef;
        private int mPreviousScrollState;
        private int mScrollState;

        public QuickTabLayoutOnPageChangeListener(TabLayout tabLayout) {
            mTabLayoutRef = new WeakReference<>(tabLayout);
        }

        @Override
        public void onPageScrollStateChanged(final int state) {
            mPreviousScrollState = mScrollState;
            mScrollState = state;
        }

        @Override
        public void onPageScrolled(final int position, final float positionOffset,
                                   final int positionOffsetPixels) {
            final TabLayout tabLayout = mTabLayoutRef.get();
            if (tabLayout != null) {
                // Only update the text selection if we're not settling, or we are settling after
                // being dragged
                final boolean updateText = mScrollState != SCROLL_STATE_SETTLING ||
                        mPreviousScrollState == SCROLL_STATE_DRAGGING;
                // Update the indicator if we're not settling after being idle. This is caused
                // from a setCurrentItem() call and will be handled by an animation from
                // onPageSelected() instead.
                final boolean updateIndicator = !(mScrollState == SCROLL_STATE_SETTLING
                        && mPreviousScrollState == SCROLL_STATE_IDLE);
                try {
                    Method setScrollPosition = TabLayout.class.getDeclaredMethod("setScrollPosition", int.class, float.class, boolean.class, boolean.class);
                    setScrollPosition.setAccessible(true);
                    setScrollPosition.invoke(tabLayout, position, positionOffset, updateText, updateIndicator);
                } catch (Exception e) {
                    tabLayout.setScrollPosition(position, positionOffset, updateText);
                }
            }
        }

        @Override
        public void onPageSelected(final int position) {
            final TabLayout tabLayout = mTabLayoutRef.get();
            if (tabLayout != null && tabLayout.getSelectedTabPosition() != position
                    && position < tabLayout.getTabCount()) {
                // Select the tab, only updating the indicator if we're not being dragged/settled
                // (since onPageScrolled will handle that).
                final boolean updateIndicator = mScrollState == SCROLL_STATE_IDLE
                        || (mScrollState == SCROLL_STATE_SETTLING
                        && mPreviousScrollState == SCROLL_STATE_IDLE);
                TabLayout.Tab tabAt = tabLayout.getTabAt(position);
                try {
                    if (tabAt != null) {
                        Method selectTab = TabLayout.class.getDeclaredMethod("selectTab", TabLayout.Tab.class, boolean.class);
                        selectTab.setAccessible(true);
                        selectTab.invoke(tabAt, updateIndicator);
                    }
                } catch (Throwable e) {
                    tabAt.select();
                }
            }
        }

        void reset() {
            mPreviousScrollState = mScrollState = SCROLL_STATE_IDLE;
        }
    }
    public static class QuickViewPagerOnTabSelectedListener implements TabLayout.OnTabSelectedListener {
        private final QuickViewPager mViewPager;

        public QuickViewPagerOnTabSelectedListener(QuickViewPager viewPager) {
            mViewPager = viewPager;
        }

        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            mViewPager.setCurrentItem(tab.getPosition());
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {
            // No-op
        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {
            // No-op
        }
    }


}