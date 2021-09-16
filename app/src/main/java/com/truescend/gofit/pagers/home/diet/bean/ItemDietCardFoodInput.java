package com.truescend.gofit.pagers.home.diet.bean;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Rect;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.sn.app.net.data.app.bean.FoodsBean;
import com.sn.app.net.data.app.bean.SearchFoodResultBean;
import com.sn.utils.LanguageUtil;
import com.truescend.gofit.R;
import com.truescend.gofit.pagers.common.bean.ItemBase;
import com.truescend.gofit.utils.ResUtil;
import com.truescend.gofit.views.AppCompatMultiLineSpinner;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;

/**
 * 作者:东芝(2018/11/23).
 * 功能:
 */

public class ItemDietCardFoodInput extends ItemBase implements AdapterView.OnItemClickListener {

    private final ArrayAdapter<String> keywordListAdapter;
    @BindView(R.id.tvSerialNumber)
    TextView tvSerialNumber;
    @BindView(R.id.autoTipsInputFood)
    AppCompatAutoCompleteTextView autoTipsInputFood;
    @BindView(R.id.etAmount)
    EditText etAmount;
    @BindView(R.id.spUnit)
    AppCompatMultiLineSpinner spUnit;
    @BindView(R.id.etCalories)
    EditText etCalories;
    private InputTextWatcher inputTextWatcher;
    private List<SearchFoodResultBean.DataBean> foodResults;
    private SearchFoodResultBean.DataBean dataBean;
    private List<String> foodStringResults = new ArrayList<>();
    private Rect mRectAutoTipsInputFood = new Rect();


    public ItemDietCardFoodInput(View view) {
        super(view);
        keywordListAdapter = new ArrayAdapter<>(view.getContext(), R.layout.simple_spinner_dropdown_item, android.R.id.text1);
        autoTipsInputFood.setAdapter(keywordListAdapter);
        autoTipsInputFood.setThreshold(1);
        autoTipsInputFood.addTextChangedListener(mKeywordWatcher);
        autoTipsInputFood.setOnItemClickListener(this);

        autoTipsInputFood.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                autoTipsInputFood.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                autoTipsInputFood.getGlobalVisibleRect(mRectAutoTipsInputFood);
            }
        });


        keywordListAdapter.registerDataSetObserver(new DataSetObserver() {

            @Override
            public void onChanged() {
                autoTipsInputFood.postDelayed(new Runnable() {
                    @Override
                    public void run() {


                    }
                }, 2000);
            }
        });

        etAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence text, int start, int before, int count) {
                if (TextUtils.isEmpty(text)) return;
                String inputText = text.toString();
                updateOtherEditTextValue(inputText);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        spUnit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Editable text = etAmount.getText();
                if (TextUtils.isEmpty(text)) return;
                String inputText = text.toString();
                updateOtherEditTextValue(inputText);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }


    private void updateOtherEditTextValue(String inputText) {
        String foodName = getFoodName();
        if (TextUtils.isEmpty(foodName)) return;
        if (dataBean != null) {
            if (foodName.equalsIgnoreCase(dataBean.getName())) {
                float amount = parserFloat(inputText);

                int unitIndex = spUnit.getSelectedItemPosition();

                //100g/kcal
                double calory = dataBean.getCalory();
                //转成对应的单位
                double amountUnit = amount;

                switch (unitIndex) {
                    case 0://g
                    case 3://ml (不精准)
                        amountUnit = amount;
                        break;
                    case 1://lb
                        amountUnit = amount * 453.59237f;
                        break;
                    case 2://oz
                        amountUnit = amount * 28.3495231f;
                        break;


                }

                double cal = calory * amountUnit / 100;
                etCalories.setText(removeDecimalEndZeroString(cal));
            } else {
                etCalories.setText("");
            }
        }
    }

    private float parserFloat(String inputText) {
        String replaceAll =
                inputText.replaceAll(",", ".")
                .replaceAll("١", "1")
                .replaceAll("٢", "2")
                .replaceAll("٣", "3")
                .replaceAll("٤", "4")
                .replaceAll("٥", "5")
                .replaceAll("٦", "6")
                .replaceAll("٧", "7")
                .replaceAll("٨", "8")
                .replaceAll("٩", "9")
                .replaceAll("٠", "0")
                .replaceAll("٫", ".");
        return Float.parseFloat(replaceAll);
    }

    @NonNull
    private String removeDecimalEndZeroString(double cal) {
//        String format = ResUtil.format("%.0f", cal);
        String format = ResUtil.format(Locale.ENGLISH,"%.1f", cal);
        if (format.indexOf(".") > 0) {
            format = format.replaceAll("0+?$", "");//去掉多余的0
            format = format.replaceAll("[.]$", "");//如最后一位是.则去掉
        }
        return format;
    }

    /**
     * 输入关键字监听器
     */
    private TextWatcher mKeywordWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (TextUtils.isEmpty(s)) return;
            if (inputTextWatcher != null) {
                inputTextWatcher.onTextChanged(s.toString());
            }
        }
    };

    @Override
    public void unBind() {
        if (autoTipsInputFood != null) {
            autoTipsInputFood.removeTextChangedListener(mKeywordWatcher);
        }
        super.unBind();

    }

    /**
     * 序号
     *
     * @param serialNumber
     */
    public void setSerialNumber(String serialNumber) {
        tvSerialNumber.setText(serialNumber);
    }

    public float getAmount() {
        try {
            return parserFloat(etAmount.getText().toString().trim());
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public void setAmountError(CharSequence error) {
        etAmount.setError(error);
        etAmount.requestFocus();
    }

    private void setAmount(float amount) {
        etAmount.setText(removeDecimalEndZeroString(amount));
    }

    public void setFoodNameError(CharSequence error) {
        autoTipsInputFood.setError(error);
        autoTipsInputFood.requestFocus();
    }

    public void setFood(FoodsBean foodsBean) {
        autoTipsInputFood.setText(foodsBean.getName());
        setUnitString(foodsBean.getUnit());
        setCalories(foodsBean.getCalory());
        float amount = foodsBean.getAmount();
        setAmount(amount);


        int unitIndex = spUnit.getSelectedItemPosition();
        dataBean = new SearchFoodResultBean.DataBean();
        dataBean.setName(foodsBean.getName());
        dataBean.setState("ml".equalsIgnoreCase(foodsBean.getUnit()) ? 2 : 1); //1：固态，2：液态
        switch (unitIndex) {
            case 0://g
            case 3://ml
                dataBean.setCalory(foodsBean.getCalory() * 100 / amount);//转回100g/kcal
                break;
            case 1://lb
                dataBean.setCalory(foodsBean.getCalory() * 100 / (amount * 453.59237f));
                break;
            case 2://oz
                dataBean.setCalory(foodsBean.getCalory() * 100 / (amount * 28.3495231f));
                break;


        }
    }

    public void setCaloriesError(CharSequence error) {
        etCalories.setError(error);
        etCalories.requestFocus();
    }

    private void setCalories(float calories) {
        etCalories.setText(removeDecimalEndZeroString(calories));
    }

    public String getFoodName() {
        try {
            return autoTipsInputFood.getText().toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setIsLasted(boolean isLasted) {
        if (isLasted) {
            etCalories.setImeOptions(EditorInfo.IME_ACTION_DONE);
        } else {
            etCalories.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        }
    }

    private void setUnitString(String unit) {
        //通过字符串寻找索引
        SpinnerAdapter adapter = spUnit.getAdapter();
        int count = adapter.getCount();
        int selectIndex = 0;
        for (int i = 0; i < count; i++) {
            if (unit.equalsIgnoreCase(String.valueOf(adapter.getItem(i)))) {
                selectIndex = i;
                break;
            }
        }
        spUnit.setSelection(selectIndex);
    }

    public int getUnit() {
        return spUnit.getSelectedItemPosition();
    }

    public String getUnitString() {
        return (String) spUnit.getSelectedItem();
    }


    public float getCalories() {
        try {
            return parserFloat(etCalories.getText().toString().trim());
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }


    public void refreshAndDropDown(List<SearchFoodResultBean.DataBean> foodResults) {
        this.foodResults = foodResults;
        foodStringResults.clear();
        for (SearchFoodResultBean.DataBean foodResult : foodResults) {
            foodStringResults.add(foodResult.getName());
        }

        keywordListAdapter.clear();
        keywordListAdapter.addAll(foodStringResults);

        final Editable text = autoTipsInputFood.getText();
        if (TextUtils.isEmpty(text)) {
            return;
        }

        //过滤
        keywordListAdapter.getFilter().filter(text, new Filter.FilterListener() {
            String maxLengthStr = null;
            int maxLength = 0;

            @Override
            public void onFilterComplete(int count) {
                if (keywordListAdapter.getCount() == 0) {
                    return;
                }
                //取出最长长度的搜索结果
                for (int i = 0; i < keywordListAdapter.getCount(); i++) {
                    String item = keywordListAdapter.getItem(i);
                    if (item != null) {
                        if (maxLength < item.length()) {
                            maxLength = item.length();
                            maxLengthStr = item;
                        }
                    }
                }
                if (maxLengthStr == null) {
                    return;
                }

                //模拟最长item
                Context context = autoTipsInputFood.getContext();
                View rootView = LayoutInflater.from(context).inflate(R.layout.simple_spinner_dropdown_item, null, false);
                TextView tv = rootView.findViewById(android.R.id.text1);
                tv.setText(maxLengthStr);
                if (!LanguageUtil.isRTL()) {
                    //测量最长item 宽度
                    int widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                    int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                    rootView.measure(widthSpec, heightSpec);
                    int measuredWidth = rootView.getMeasuredWidth();//测量得到的宽
                    //最小宽度
                    if (measuredWidth < mRectAutoTipsInputFood.width()) {
                        measuredWidth = mRectAutoTipsInputFood.width();
                    }
                    int widthPixels = context.getResources().getDisplayMetrics().widthPixels;
                    int maxWidth = widthPixels - 2 * mRectAutoTipsInputFood.left;
                    if (measuredWidth > maxWidth) {
                        measuredWidth = maxWidth;
                    }
                    //设置下拉布局宽度跟随最长item宽度 动态变化
                    autoTipsInputFood.setDropDownWidth(measuredWidth);
                }
                //重新刷新使setDropDownWidth生效,传null防止死循环回调
                keywordListAdapter.getFilter().filter(text, null);
            }
        });


    }

    /**
     * 正在输入
     *
     * @return
     */
    public boolean isInputting() {
        return autoTipsInputFood.hasFocus();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (foodResults != null && position < foodResults.size()) {
            dataBean = foodResults.get(position);
            if (dataBean.getState() == 2/*1：固态，2：液态*/) {
                spUnit.setSelection(3);//选择ml 毫升
            }
            Editable text = etAmount.getText();
            if (TextUtils.isEmpty(text)) return;
            String inputText = text.toString();
            updateOtherEditTextValue(inputText);

            etAmount.performClick();
        }
    }


    public void setInputTextWatcher(InputTextWatcher inputTextWatcher) {
        this.inputTextWatcher = inputTextWatcher;
    }


    public interface InputTextWatcher {
        void onTextChanged(String key);
    }
}
