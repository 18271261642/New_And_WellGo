package com.truescend.gofit.pagers.base.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;
/**
 * 作者:东芝(2017/11/17).
 * 功能:ListView用
 */

public abstract class BaseListViewAdapter<T> extends BaseAdapter {
    protected List<T> lists;
    protected LayoutInflater inflater;

    public BaseListViewAdapter(Context context) {
        this.lists = new ArrayList<T>();
        inflater = LayoutInflater.from(context);
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
    public void removeItem(int position) {
        if (this.lists != null&&position<this.lists.size()) {
            this.lists.remove(position);
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
    @Override
    public int getCount() {
        return lists==null?0:lists.size();
    }
    @Override
    public T getItem(int position) {
        return lists.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(initLayout(), parent, false);
        }
        onItemInflate(position,getItem(position), ViewHolder.BaseViewHolder.getViewHolder(convertView),convertView);
        return convertView;
    }
    public abstract void onItemInflate(int position, T item, ViewHolder.BaseViewHolder viewHolder, View rootView);
    public abstract int initLayout();
}