package com.truescend.gofit.pagers.base.adapter;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

/**
 * 作者: DongZhi 2015/12/24.
 * 保佑以下代码无bug...
 * RecycleView的adapter
 */
public abstract class BaseRecycleViewAdapter<T> extends RecyclerView.Adapter<ViewHolder> {
    protected List<T> lists;
    protected Context context;
    private ViewGroup parentView;

    public BaseRecycleViewAdapter(Context context) {
        this.context = context;
    }

    public BaseRecycleViewAdapter(Context context, List<T> lists) {
        this.context = context;
        this.lists = lists;
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

    public void addItem(T items, int position) {
        if (this.lists == null) {
            this.lists = new ArrayList<T>();
        }
        if (items != null) {
            this.lists.add(position, items);
            notifyDataSetChanged();
        }
    }

    //新增，移除item
    public void deleteItem(int position) {
        if (this.lists == null) {
            this.lists = new ArrayList<T>();
        }
        if (position > -1 && position < lists.size()) {
            this.lists.remove(position);
            notifyDataSetChanged();
        }
    }

    public T getItem(int position) {
        return this.lists.get(position);
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
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(initLayout(viewType), parent, false);
//        TypedValue typedValue = new TypedValue();
//        context.getTheme().resolveAttribute(R.attr.selectableItemBackground, typedValue, true);
//        view.setBackgroundResource(typedValue.resourceId);
        try {
            parentView = (ViewGroup) view;
        } catch (Exception e) {
        }
        return new ViewHolder(view);
    }

    public abstract void onItemInflate(int position, T item, ViewHolder.BaseViewHolder viewHolder, View rootView);

    public abstract int initLayout(int viewType);

    @Override
    public void onViewRecycled(final ViewHolder holder) {
        super.onViewRecycled(holder);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        onItemInflate(position, lists.get(position), holder.getViewHolder(), holder.itemView);
        if (onItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(null, v, holder.getPosition(), holder.getItemId());
                }
            });
        }
        if (onItemLongClickListener != null) {
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    onItemLongClickListener.onItemLongClick(null, holder.itemView, holder.getPosition(), holder.getItemId());
                    return true;
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return lists == null ? 0 : lists.size();
    }

    private AdapterView.OnItemClickListener onItemClickListener;
    private AdapterView.OnItemLongClickListener onItemLongClickListener;

    //新增，item长按事件监听
    public void setOnItemLongClickListener(AdapterView.OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    public AdapterView.OnItemLongClickListener getOnItemLongClickListener() {
        return onItemLongClickListener;
    }

    public AdapterView.OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

}