package com.truescend.gofit.utils;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;

/**
 * 输入字符监听器
 * 功能：监听输入字符是否合法
 * Author:Created by 泽鑫 on 2017/9/8 17:14.
 */

public class InputTextWatcher implements TextWatcher {

    private EditText editText;
    private String beforeText;
    private boolean isChange = false;

    public InputTextWatcher(EditText editText){
        this.editText = editText;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        beforeText = s.toString();
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (isChange){
            return;
        }
        String afterText = s.toString();
        boolean isOk = true;
        if (!TextUtils.isEmpty(afterText)){
            isOk = AppUtil.inputIsLegal(afterText);
            if (!isOk){
                int different = afterText.length() - beforeText.length();
                isChange = true;
                editText.setText(beforeText);
                isChange = false;
                editText.setSelection(afterText.length() - different);
            }
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
