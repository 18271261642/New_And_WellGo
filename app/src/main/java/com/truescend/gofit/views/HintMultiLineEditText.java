package com.truescend.gofit.views;

import android.content.Context;
import android.content.res.TypedArray;
import androidx.annotation.ColorInt;
import androidx.annotation.StringRes;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.inputmethod.EditorInfo;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.truescend.gofit.R;

/**
 * 作者:东芝(2018/6/30).
 * 功能:
 */

public class HintMultiLineEditText extends FrameLayout implements TextWatcher {

    private AppCompatTextView editHint;
    private AppCompatEditText editText;
    private int mLastLength;

    public HintMultiLineEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public HintMultiLineEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {


        inflate(getContext(), R.layout.hint_multiline_edittext, this);
        editHint = findViewById(R.id.editHint);
        editText = findViewById(R.id.editText);

        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.HintMultiLineEditText);
            String sn_hint = typedArray.getString(R.styleable.HintMultiLineEditText_android_hint);
            String sn_text = typedArray.getString(R.styleable.HintMultiLineEditText_android_text);

            float sn_hintSize = typedArray.getDimensionPixelSize(R.styleable.HintMultiLineEditText_hintSize, 0);
            float sn_textSize = typedArray.getDimensionPixelSize(R.styleable.HintMultiLineEditText_android_textSize, 0);
            if (sn_hintSize == 0) {
                sn_hintSize = editText.getTextSize();
            }
            if (sn_textSize == 0) {
                sn_textSize = editText.getTextSize();
            }

            int sn_hintColor = typedArray.getColor(R.styleable.HintMultiLineEditText_android_textColorHint, editText.getCurrentHintTextColor());
            int sn_textColor = typedArray.getColor(R.styleable.HintMultiLineEditText_android_textColor, 0xFF000000);

            int inputType = typedArray.getInt(R.styleable.HintMultiLineEditText_android_inputType, EditorInfo.TYPE_NULL);
            int imeOptions = typedArray.getInt(R.styleable.HintMultiLineEditText_android_imeOptions, 0);
            int maxLines = typedArray.getInt(R.styleable.HintMultiLineEditText_android_maxLines, -1);
            int maxlength = typedArray.getInt(R.styleable.HintMultiLineEditText_android_maxLength, -1);
            editText.setInputType(inputType);
            editText.setImeOptions(imeOptions);
            if (maxLines != -1) {
                editText.setMaxLines(maxLines);
            }
            if (maxlength >= 0) {
                editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxlength)});
            } else {
                editText.setFilters(new InputFilter[0]);
            }

            editHint.getPaint().setTextSize(sn_hintSize);
            editText.getPaint().setTextSize(sn_textSize);


            editHint.setTextColor(sn_hintColor);
            editText.setTextColor(sn_textColor);


            editHint.setText(sn_hint);
            if (!TextUtils.isEmpty(sn_text)) {
                editText.setText(sn_text);
                editText.setSelection(sn_text.length());
                editHint.setVisibility(INVISIBLE);
            }

            typedArray.recycle();
        }


        editHint.setPadding(editText.getPaddingLeft(), editText.getPaddingTop(), editText.getPaddingRight(), editText.getPaddingBottom());
        updateEditTextLayout();
        editText.addTextChangedListener(this);
    }

    public void setHintTextSize(float size) {
        setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
    }


    public void setHintTextSize(int unit, float size) {
        if (editHint != null) {
            editHint.setTextSize(unit, size);
            updateEditTextLayout();
        }
    }

    private void updateEditTextLayout() {
        editHint.post(new Runnable() {
            @Override
            public void run() {
                final int hintH = editHint.getHeight();
                editText.post(new Runnable() {
                    @Override
                    public void run() {
                        int textH = editText.getHeight();
                        if (hintH > textH) {
                            editText.setLayoutParams(new RelativeLayout.LayoutParams(editHint.getLayoutParams().width, hintH));
                        }
                    }
                });
            }
        });
    }

    public void setTextSize(float size) {
        setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
    }


    public void setTextSize(int unit, float size) {
        if (editText != null) {
            editText.setTextSize(unit, size);
        }
    }


    public void setHintTextColor(@ColorInt int color) {
        if (editHint != null) {
            editHint.setTextColor(color);
        }
    }

    public void setTextColor(@ColorInt int color) {
        if (editText != null) {
            editText.setTextColor(color);
        }
    }

    public final void setHint(CharSequence text) {
        if (editHint != null) {
            editHint.setText(text);
            updateEditTextLayout();
        }
    }

    public final void setHint(@StringRes int resid) {
        setHint(getContext().getResources().getText(resid));
    }

    public CharSequence getHint() {
        return editHint.getText();
    }

    public final void setText(CharSequence text) {
        if (editText != null) {
            editText.setText(text);
        }
    }

    public Editable getText() {
        return editText.getText();
    }

    public final void setText(@StringRes int resid) {
        setText(getContext().getResources().getText(resid));
    }

    public void setError(CharSequence error) {
        if (editText != null) {
            try {
                editText.setError(error);
            } catch (Exception e) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public CharSequence getError() {
        return editText.getError();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        int length = s.length();
        if (length == 0) {
            editHint.setVisibility(VISIBLE);
        } else if (mLastLength == 0) {
            editHint.setVisibility(INVISIBLE);
        }
        mLastLength = length;
    }

}
