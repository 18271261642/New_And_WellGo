package com.truescend.gofit.pagers.home.diet.add;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.sn.app.net.data.app.bean.FoodsBean;
import com.sn.app.net.data.app.bean.MealBean;
import com.sn.app.net.data.app.bean.SearchFoodResultBean;
import com.sn.utils.DateUtil;
import com.sn.utils.IF;
import com.sn.utils.SNToast;
import com.truescend.gofit.R;
import com.truescend.gofit.pagers.base.BaseActivity;
import com.truescend.gofit.pagers.common.dialog.LoadingDialog;
import com.truescend.gofit.pagers.home.diet.bean.ItemDietCardFoodInput;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 作者:东芝(2018/11/22).
 * 功能:加餐/更新餐
 */
public class DietEditMealActivity extends BaseActivity<DietEditMealPresenterImpl, IDietEditMealContract.IView> implements IDietEditMealContract.IView, ItemDietCardFoodInput.InputTextWatcher {
    public final static String KEY_PAGE_TYPE = "KEY_PAGE_TYPE";
    public final static String KEY_MEAL_BEAN = "KEY_MEAL_BEAN";
    public final static int TYPE_SAVE_AND_FINISH = 1;
    public final static int TYPE_UPDATE_AND_FINISH = 2;
    private int mPageType;
    private MealBean mealBean;

    public static void startDietAddMealActivity(Activity activity, int requestCode) {
        activity.startActivityForResult(
                new Intent(activity, DietEditMealActivity.class)
                        .putExtra(KEY_PAGE_TYPE, TYPE_SAVE_AND_FINISH)
                , requestCode);
    }

    public static void startDietUpdateMealActivity(Activity activity, int requestCode, MealBean bean) {
        activity.startActivityForResult(
                new Intent(activity, DietEditMealActivity.class)
                        .putExtra(KEY_PAGE_TYPE, TYPE_UPDATE_AND_FINISH)
                        .putExtra(KEY_MEAL_BEAN, bean)
                , requestCode);
    }


    @BindView(R.id.itemFoodInfoInput1)
    View itemFoodInfoInput1;
    @BindView(R.id.itemFoodInfoInput2)
    View itemFoodInfoInput2;
    @BindView(R.id.tvDietSave)
    View tvDietSave;
    private List<ItemDietCardFoodInput> itemDietCardFoodInputs = new ArrayList<>();

    @Override
    protected void onDestroy() {
        for (ItemDietCardFoodInput itemDietCardFoodInput : itemDietCardFoodInputs) {
            itemDietCardFoodInput.unBind();
        }
        super.onDestroy();
    }

    @Override
    protected DietEditMealPresenterImpl initPresenter() {
        return new DietEditMealPresenterImpl(this);
    }

    @Override
    protected int initLayout() {
        return R.layout.activity_diet_edit_meal;
    }

    @Override
    protected void onCreateActivity(Bundle savedInstanceState) {
        setTitle(getString(R.string.content_this_meal_to_eat));

        if ((mPageType = getIntent().getIntExtra(KEY_PAGE_TYPE, -1)) == -1) {
            //拒绝访问
            setResult(Activity.RESULT_CANCELED);
            finish();
            return;
        }


        initItems();
    }

    private void initItems() {
        mealBean = (MealBean) getIntent().getSerializableExtra(KEY_MEAL_BEAN);


        //这里后期可改成listview 或循环addView
        itemDietCardFoodInputs.add(new ItemDietCardFoodInput(itemFoodInfoInput1));
        itemDietCardFoodInputs.add(new ItemDietCardFoodInput(itemFoodInfoInput2));


        int size = itemDietCardFoodInputs.size();
        for (int i = 0; i < size; i++) {
            ItemDietCardFoodInput itemDietCardFoodInput = itemDietCardFoodInputs.get(i);
            itemDietCardFoodInput.setSerialNumber(String.valueOf(i + 1));
            if(i==size-1){
                itemDietCardFoodInput.setIsLasted(true);
            }
            //赋值
            if (mealBean != null) {
                List<FoodsBean> foods = mealBean.getFoods();
                if (foods != null && i < foods.size()) {
                    itemDietCardFoodInput.setFood(foods.get(i));
                    itemDietCardFoodInput.setSerialNumber(String.valueOf(i + 1));

                }
            }
            itemDietCardFoodInput.setInputTextWatcher(this);
        }
    }


    @Override
    public void onSearchFoodResult(List<SearchFoodResultBean.DataBean> foodResults) {
        for (int i = 0; i < itemDietCardFoodInputs.size(); i++) {
            ItemDietCardFoodInput itemDietCardFoodInput = itemDietCardFoodInputs.get(i);
            itemDietCardFoodInput.refreshAndDropDown(foodResults);
        }
    }

    @Override
    public void onAddMeal(boolean success, String error) {
        if (success) {
            setResult(Activity.RESULT_OK);
            finish();
        } else {
            SNToast.toast(error);
        }
    }

    @Override
    public void onUpdateMeal(boolean success, String error) {
        if (success) {
            setResult(Activity.RESULT_OK);
            finish();
        } else {
            SNToast.toast(error);
        }
    }


    @Override
    public void onTextChanged(String key) {
        getPresenter().searchFoodKeywords(key);
    }


    @OnClick({R.id.tvDietSave})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tvDietSave:
                MealBean tempMealBean = new MealBean();

                tempMealBean.setDate(DateUtil.getCurrentDate(DateUtil.YYYY_MM_DD));
                ArrayList<FoodsBean> foods = new ArrayList<>();
                for (int i = 0; i < itemDietCardFoodInputs.size(); i++) {
                    ItemDietCardFoodInput itemDietCardFoodInput = itemDietCardFoodInputs.get(i);
                    String foodName = itemDietCardFoodInput.getFoodName();
                    float calories = itemDietCardFoodInput.getCalories();
                    float amount = itemDietCardFoodInput.getAmount();
                    boolean foodNameIsEmpty = IF.isEmpty(foodName);
                    boolean amountIsEmpty = amount <=0;
                    boolean caloriesIsEmpty = calories <=0;

                    String error = getString(R.string.content_food_input_empty_tips);


                    //第0条 不填写就提示
                    if (i == 0 && foodNameIsEmpty) {
                        itemDietCardFoodInput.setFoodNameError(error);
                        return;
                    } else if (i > 0) {
                        //第非0条 可不填写 但必须都不填写,否则仍然需要填写
                        if (foodNameIsEmpty && amountIsEmpty && caloriesIsEmpty) {
                            continue;
                        }
                        if (foodNameIsEmpty && !amountIsEmpty) {
                            itemDietCardFoodInput.setFoodNameError(error);
                            return;
                        }
                    }

                    if (foodNameIsEmpty) {
                        itemDietCardFoodInput.setFoodNameError(error);
                        return;
                    }
                    if (amountIsEmpty) {
                        itemDietCardFoodInput.setAmountError(error);
                        return;
                    }
                    if (caloriesIsEmpty) {
                        itemDietCardFoodInput.setCaloriesError(error);
                        return;
                    }

                    foods.add(new FoodsBean(
                            foodName,
                            itemDietCardFoodInput.getUnitString(),
                            calories,
                            amount)
                    );
                    //x+=y
                    tempMealBean.setCalory(tempMealBean.getCalory() + calories);
                }
                tempMealBean.setFoods(foods);

                switch (mPageType) {
                    case TYPE_SAVE_AND_FINISH:
                        getPresenter().addMeal(tempMealBean);
                        break;
                    case TYPE_UPDATE_AND_FINISH:
                        if (mealBean != null) {
                            tempMealBean.setId(mealBean.getId());
                            tempMealBean.setUser_id(mealBean.getUser_id());
                        }
                        getPresenter().updateMeal(tempMealBean);
                        break;
                }
                break;
        }
    }

    @Override
    public void onDialogLoading(String msg) {
        LoadingDialog.show(this, msg);
    }

    @Override
    public void onDialogDismiss() {
        LoadingDialog.dismiss();
    }
}
