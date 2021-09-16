package com.truescend.gofit.pagers.home.diet.adapter;

import android.content.Context;
import android.view.View;

import com.sn.app.net.data.app.bean.FoodsBean;
import com.sn.app.net.data.app.bean.MealBean;
import com.truescend.gofit.R;
import com.truescend.gofit.pagers.base.adapter.BaseListViewAdapter;
import com.truescend.gofit.pagers.base.adapter.ViewHolder;
import com.truescend.gofit.utils.ResUtil;

import java.util.List;

/**
 * 作者:东芝(2018/11/26).
 * 功能:今天进餐的列表
 */

public class DietListAdapter extends BaseListViewAdapter<MealBean> {
    public DietListAdapter(Context context) {
        super(context);
    }

    @Override
    public void onItemInflate(int position, MealBean item, ViewHolder.BaseViewHolder viewHolder, View rootView) {
        viewHolder.setTextView(R.id.tvSerialNumber,String.valueOf(position+1));
        viewHolder.setTextView(R.id.tvFoodNames,getFoodNames(item));
        viewHolder.setTextView(R.id.tvCalories, ResUtil.format("%.0f",item.getCalory()));
    }

    private CharSequence getFoodNames(MealBean item) {
        StringBuilder sb  = new StringBuilder();
        List<FoodsBean> foods = item.getFoods();
        int size = foods.size();
        for (int i = 0; i < size; i++) {
            FoodsBean food = foods.get(i);
            sb.append(food.getName());
            if(i<size-1) {
                sb.append("/");
            }
        }
        return sb;
    }

    @Override
    public int initLayout() {
        return R.layout.item_diet_list;
    }
}
