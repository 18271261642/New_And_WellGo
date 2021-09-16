package com.truescend.gofit.pagers.start.register;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.core.widget.NestedScrollView;
import android.view.View;
import android.widget.TextView;

import com.sn.utils.IF;
import com.sn.utils.RegexUtil;
import com.sn.utils.SNToast;
import com.truescend.gofit.R;
import com.truescend.gofit.pagers.base.BaseActivity;
import com.truescend.gofit.pagers.common.dialog.LoadingDialog;
import com.truescend.gofit.views.AppCompatMultiLineSpinner;
import com.truescend.gofit.views.HintMultiLineEditText;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 作者:东芝(2018/06/13).
 * 功能:新注册界面
 */
public class RegisterActivity extends BaseActivity<RegisterPresenterImpl, IRegisterContract.IView> implements IRegisterContract.IView, NestedScrollView.OnScrollChangeListener {

    @BindView(R.id.etEmail)
    HintMultiLineEditText etEmail;
    @BindView(R.id.etEmailAgain)
    HintMultiLineEditText etEmailAgain;
    @BindView(R.id.etRegisterPasswords)
    HintMultiLineEditText etRegisterPasswords;
    @BindView(R.id.etRegisterConfirmPasswords)
    HintMultiLineEditText etRegisterConfirmPasswords;
    @BindView(R.id.tvRegister)
    TextView tvRegister;
    @BindView(R.id.etAnswer1)
    HintMultiLineEditText etAnswer1;
    @BindView(R.id.etAnswer2)
    HintMultiLineEditText etAnswer2;
    @BindView(R.id.scrollView)
    NestedScrollView scrollView;
    @BindView(R.id.spQuestion1)
    AppCompatMultiLineSpinner spQuestion1;
    @BindView(R.id.spQuestion2)
    AppCompatMultiLineSpinner spQuestion2;
    private HintMultiLineEditText mLastErrorEditText;

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, RegisterActivity.class));
    }

    @Override
    protected RegisterPresenterImpl initPresenter() {
        return new RegisterPresenterImpl(this);
    }

    @Override
    protected int initLayout() {
        return R.layout.activity_register;
    }

    @Override
    protected void onCreateActivity(Bundle savedInstanceState) {
        setTitle( R.string.title_register );
        scrollView.setOnScrollChangeListener(this);


    }


    @OnClick({R.id.tvRegister})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tvRegister:

                String email = etEmail.getText().toString().trim();
                String emailAgain = etEmailAgain.getText().toString().trim();
                String psw = etRegisterPasswords.getText().toString().trim();
                String pswAgain = etRegisterConfirmPasswords.getText().toString().trim();
                String answer1 = etAnswer1.getText().toString().trim();
                String answer2 = etAnswer2.getText().toString().trim();
                if (IF.isEmpty(email)) {
                    setEditTextErrorTips(etEmail, getString(R.string.content_input_email));
                    return;
                }
                if (!RegexUtil.isEmail(email)) {
                    setEditTextErrorTips(etEmail, getString(R.string.content_input_correct_email));
                    return;
                }
                if (!emailAgain.equals(email)) {
                    setEditTextErrorTips(etEmailAgain, getString(R.string.content_input_email_different));
                    return;
                }
                if (IF.isEmpty(psw)) {
                    setEditTextErrorTips(etRegisterPasswords, getString(R.string.content_input_password));
                    return;
                }

                if (psw.length() < 6 || psw.length() > 14) {
                    setEditTextErrorTips(etRegisterPasswords, getString(R.string.content_password_length));
                    return;
                }

                if (!psw.equals(pswAgain)) {
                    setEditTextErrorTips(etRegisterConfirmPasswords, getString(R.string.content_valid_input));
                    return;
                }
                if (IF.isEmpty(answer1)) {
                    setEditTextErrorTips(etAnswer1, getString(R.string.content_answer));
                    return;
                }
                if (IF.isEmpty(answer2)) {
                    setEditTextErrorTips(etAnswer2, getString(R.string.content_answer));
                    return;
                }
                int indexFromQuestion1 = spQuestion1.getSelectedItemPosition() + 1;
                int indexFromQuestion2 = 3 + spQuestion2.getSelectedItemPosition() + 1;

                getPresenter().requestRegister(email, psw, answer1, answer2, indexFromQuestion1, indexFromQuestion2);

                break;
        }
    }




    /**
     * 设置编辑框错误提示信息
     *
     * @param et
     * @param error
     */
    private void setEditTextErrorTips(HintMultiLineEditText et, CharSequence error) {
        mLastErrorEditText = et;
        mLastErrorEditText.requestFocus();
        mLastErrorEditText.setError(error);

    }

    @Override
    public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        if (mLastErrorEditText != null && mLastErrorEditText.getError() != null) {
            mLastErrorEditText.setError(null);
        }
    }

    @Override
    public void updateRegisterStatue(boolean isSuccess, String msg) {

            SNToast.toast(msg);
        if(isSuccess) {
            finish();
        }
    }



    @Override
    public void onShowLoading(boolean show) {
        if (show) {
            LoadingDialog.show(this, R.string.loading);
        } else {
            LoadingDialog.dismiss();
        }
    }
}
