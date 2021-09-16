package com.truescend.gofit.pagers.home.diet.list;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.sn.app.net.data.app.bean.MealBean;
import com.sn.utils.IF;
import com.truescend.gofit.R;
import com.truescend.gofit.pagers.base.BaseActivity;
import com.truescend.gofit.pagers.common.dialog.LoadingDialog;
import com.truescend.gofit.pagers.home.diet.adapter.DietListAdapter;
import com.truescend.gofit.utils.PageJumpUtil;
import com.truescend.gofit.utils.ResUtil;
import com.truescend.gofit.utils.UnitConversion;
import com.truescend.gofit.views.RegionalProgressBar;
import com.truescend.gofit.views.TitleLayout;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 作者:东芝(2018/11/22).
 * 功能:餐列表
 */
public class DietListMealActivity extends BaseActivity<DietListMealPresenterImpl, IDietListMealContract.IView> implements IDietListMealContract.IView, SwipeMenuListView.OnMenuItemClickListener, AdapterView.OnItemClickListener, OnRefreshListener {


    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, DietListMealActivity.class));
    }

    private DietListAdapter mDietListAdapter;


    @BindView(R.id.lvList)
    SwipeMenuListView lvList;
    @BindView(R.id.tvAddMeal)
    TextView tvAddMeal;
    @BindView(R.id.rlRefresh)
    SmartRefreshLayout rlRefresh;
    @BindView(R.id.tvCalorieIntake)
    TextView tvCalorieIntake;
    @BindView(R.id.tvCalorieTotal)
    TextView tvCalorieTotal;
    @BindView(R.id.rpbCalorieLimit)
    RegionalProgressBar rpbCalorieLimit;


    @Override
    protected DietListMealPresenterImpl initPresenter() {
        return new DietListMealPresenterImpl(this);
    }

    @Override
    protected int initLayout() {
        return R.layout.activity_diet_list_meal;
    }

    @Override
    protected void onCreateTitle(TitleLayout titleLayout) {
        super.onCreateTitle(titleLayout);
        setTitle(getString(R.string.content_record_meal_to_eat));
        //右侧添加按钮
        titleLayout.addRightItem(
                TitleLayout.ItemBuilder.Builder()
                        .setIcon(R.mipmap.icon_add)
                        .setIconSizeRatio(45)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                PageJumpUtil.startDietAddMealActivity(DietListMealActivity.this);
                            }
                        }));

    }

    @Override
    protected void onCreateActivity(Bundle savedInstanceState) {
        initListItem();
        //刷新&重载
        getPresenter().loadMealList();
    }

    private void initListItem() {
        mDietListAdapter = new DietListAdapter(this);
        lvList.setOnMenuItemClickListener(this);
        lvList.setOnItemClickListener(this);
        lvList.setMenuCreator(creator);
        lvList.setAdapter(mDietListAdapter);

        rlRefresh.setOnRefreshListener(this);
    }

    private SwipeMenuCreator creator = new SwipeMenuCreator() {
        @Override
        public void create(SwipeMenu menu) {
            SwipeMenuItem deleteItem = new SwipeMenuItem(getApplicationContext());
            deleteItem.setBackground(R.color.black);
            deleteItem.setWidth(UnitConversion.dipToPx(DietListMealActivity.this, 90));
            deleteItem.setTitle(getString(R.string.delete));
            deleteItem.setTitleSize(15);
            deleteItem.setTitleColor(Color.WHITE);

            SwipeMenuItem updateItem = new SwipeMenuItem(getApplicationContext());
            updateItem.setBackground(R.color.colorRed);
            updateItem.setWidth(UnitConversion.dipToPx(DietListMealActivity.this, 90));
            updateItem.setTitle(getString(R.string.content_edit));
            updateItem.setTitleSize(15);
            updateItem.setTitleColor(Color.WHITE);

            menu.addMenuItem(updateItem);
            menu.addMenuItem(deleteItem);
        }
    };

    @Override
    public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
        //删除
        if (mDietListAdapter != null && position < mDietListAdapter.getCount()) {
            switch (index) {
                case 0:
                    //更新
                    PageJumpUtil.startDietUpdateMealActivity(this, mDietListAdapter.getItem(position));
                    break;
                case 1:
                    //删除
                    getPresenter().deleteMeal(position, mDietListAdapter.getItem(position).getId());
                    break;
            }

        }
        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        rpbCalorieLimit.setHighlightRegionalPosition(position);
    }

    @Override
    public void onRefresh(RefreshLayout refreshlayout) {
        //刷新&重载
        getPresenter().loadMealList();
    }


    @Override
    public void onLoadMealList(List<MealBean> data) {
        if (IF.isEmpty(data)) {
            lvList.setVisibility(View.GONE);
            tvAddMeal.setVisibility(View.VISIBLE);
        } else {
            lvList.setVisibility(View.VISIBLE);
            tvAddMeal.setVisibility(View.GONE);
            mDietListAdapter.setList(data);
        }
        rpbCalorieLimit.setHighlightRegionalPosition(RegionalProgressBar.EGIONALPOSITION_DEF);
    }

    @Override
    public void onMealCalorieTotal(CharSequence totalCalorie) {
        tvCalorieTotal.setText(totalCalorie);
    }

    @Override
    public void onMealCalorieRange(CharSequence rangeCalorie) {
        tvCalorieIntake.setText(rangeCalorie);
    }

    @Override
    public void onMealDelete(int position) {
        mDietListAdapter.removeItem(position);
        //删除 时 数据改变, 此时改变标记
        setResult(Activity.RESULT_OK);
    }

    @Override
    public void onMealRegionalProgressBar(List<RegionalProgressBar.OccupiesValue> values, boolean isOutOfSize) {
        rpbCalorieLimit.setAnimOccupiesValues(values);
        tvCalorieTotal.setTextColor(isOutOfSize ? ResUtil.getColor(R.color.colorRed) : ResUtil.getColor(R.color.black));
    }


    @Override
    public void onFinishRefresh() {
        if (rlRefresh != null && rlRefresh.isRefreshing()) {
            rlRefresh.finishRefresh();
        }
    }

    @Override
    public void onDialogLoading(String msg) {
        LoadingDialog.loading(this);
    }

    @Override
    public void onDialogDismiss() {
        LoadingDialog.dismiss();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            //更新或增加 时 数据改变, 此时改变标记
            setResult(Activity.RESULT_OK);
            //做出修改时 会回调 这里, 然后静静地刷新&重载列表
            getPresenter().loadMealList();
        }
    }


    @OnClick({R.id.tvAddMeal })
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tvAddMeal:
                PageJumpUtil.startDietAddMealActivity(DietListMealActivity.this);
                break;

        }
    }
}
