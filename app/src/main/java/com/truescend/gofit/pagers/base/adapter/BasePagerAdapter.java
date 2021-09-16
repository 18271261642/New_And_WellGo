package com.truescend.gofit.pagers.base.adapter;

import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import androidx.viewpager.widget.PagerAdapter;


/**
 * 作者:东芝 2015/12/21.
 * 功能:ViewPager+View 用
 */
public class BasePagerAdapter<T extends View> extends PagerAdapter {
    protected List<T> lists;
    public BasePagerAdapter() {
        this.lists = new ArrayList<T>();
    }
    // 获取要滑动的控件的数量，在这里我们以滑动的广告栏为例，那么这里就应该是展示的广告图片的ImageView数量
    @Override
    public int getCount() {
        return lists==null?0:lists.size();
    }
    // 来判断显示的是否是同一张图片，这里我们将两个参数相比较返回即可
    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }
    // PagerAdapter只缓存三张要显示的图片，如果滑动的图片超出了缓存的范围，就会调用这个方法，将图片销毁
    @Override
    public void destroyItem(ViewGroup view, int position, Object object) {
        view.removeView(lists.get(position));
    }
    // 当要显示的图片可以进行缓存的时候，会调用这个方法进行显示图片的初始化，我们将要显示的ImageView加入到ViewGroup中，然后作为返回值返回即可
    @Override
    public Object instantiateItem(ViewGroup view, int position) {

        T child = lists.get(position);
        view.addView(child);
        return child;
    }
    public void addList(List<T> lists) {
        if (this.lists != null && lists != null) {
            this.lists.addAll(lists);
            notifyDataSetChanged();
        }
    }
    public void clear() {
        if (this.lists != null) {
            this.lists.clear();
            notifyDataSetChanged();
        }
    }
    public List<T> getList() {
        return this.lists;
    }
    public void addItem(T items) {
        if (this.lists == null) {
            this.lists = new ArrayList<T>();
        }
        if (items != null) {
            this.lists.add(items);
            notifyDataSetChanged();
        }
    }
    public void setList(List<T> lists) {
        if (lists != null) {
            if (this.lists == null) {
                this.lists = new ArrayList<T>();
            }
            this.lists.clear();
            this.lists.addAll(lists);
            notifyDataSetChanged();
        }
    }
}