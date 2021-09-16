package com.truescend.gofit.pagers.common.bean;

import android.view.View;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 功能:所有Item的父类
 * Author:Created by 泽鑫 on 2018/3/1 17:03.
 */
@Deprecated
public class ItemBase {
    private Unbinder unbinder;


    public ItemBase(View view) {
        unbinder = ButterKnife.bind(this, view);
    }

    public void unBind() {
        if (unbinder != null) {
            try {
                unbinder.unbind();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void unBindAll(ItemBase... items) {
        for (int i = 0; i < items.length; i++) {
            ItemBase item = items[i];
            if (item != null) {
                item.unBind();
            }
        }
    }public static void unBindAll(List<ItemBase> items) {
        for (int i = 0; i < items.size(); i++) {
            ItemBase item = items.get(i);
            if (item != null) {
                item.unBind();
            }
        }
    }
}
