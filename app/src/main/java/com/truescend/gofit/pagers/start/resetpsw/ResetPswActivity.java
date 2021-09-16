package com.truescend.gofit.pagers.start.resetpsw;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import androidx.core.widget.NestedScrollView;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatSpinner;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.sn.app.net.data.app.bean.GetQuestionsBean;
import com.sn.utils.IF;
import com.sn.utils.SNToast;
import com.truescend.gofit.R;
import com.truescend.gofit.pagers.base.BaseActivity;
import com.truescend.gofit.pagers.common.dialog.LoadingDialog;
import com.truescend.gofit.utils.Constant;
import com.truescend.gofit.utils.ResUtil;
import com.truescend.gofit.views.HintMultiLineEditText;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 作者:东芝(2018/6/14).
 * 功能:通过密保找回密码
 */
public class ResetPswActivity extends BaseActivity<ResetPswPresenterImpl, IResetPswContract.IView> implements IResetPswContract.IView, AdapterView.OnItemSelectedListener,NestedScrollView.OnScrollChangeListener {

    @BindView(R.id.etAnswer1)
    HintMultiLineEditText etAnswer1;
    @BindView(R.id.etAnswer2)
    HintMultiLineEditText etAnswer2;
    @BindView(R.id.etResetFirstPasswords)
    HintMultiLineEditText etResetFirstPasswords;
    @BindView(R.id.etResetConfirmPasswords)
    HintMultiLineEditText etResetConfirmPasswords;
    @BindView(R.id.etAuthCode)
    HintMultiLineEditText etAuthCode;
    @BindView(R.id.tvNext)
    TextView tvNext;
    @BindView(R.id.tvQuestion1)
    TextView tvQuestion1;
    @BindView(R.id.tvQuestion2)
    TextView tvQuestion2;
    @BindView(R.id.llTypeAuthCode)
    View llTypeAuthCode;
    @BindView(R.id.llTypeQuestion)
    View llTypeQuestion;
    @BindView(R.id.spRetrieveType)
    AppCompatSpinner spRetrieveType;
    @BindView(R.id.btnObtainAuthCode)
    AppCompatButton btnObtainAuthCode;
    @BindView(R.id.scrollView)
    NestedScrollView scrollView;
    @BindView(R.id.tvResetHelp)
    TextView tvResetHelp;


    private String email;
    private int indexFromQuestion1;
    private int indexFromQuestion2;
    private boolean isHasSetQuestion;
    private HintMultiLineEditText mLastErrorEditText;

    @Override
    protected ResetPswPresenterImpl initPresenter() {
        return new ResetPswPresenterImpl();
    }

    @Override
    protected int initLayout() {
        return R.layout.activity_resetpsw_from_question;
    }

    @Override
    protected void onCreateActivity(Bundle savedInstanceState) {
        setTitle(R.string.title_forget_password);
        Intent intent = getIntent();
        if (intent == null) {
            finish();
            return;
        }
        isHasSetQuestion = intent.getBooleanExtra("isHasSetQuestion", false);
        email = intent.getStringExtra("email");


        tvResetHelp.setText(ResUtil.formatHtml(getString(R.string.content_verification_help), Constant.URL.HELP_VERIFICATION_CODE));
        tvResetHelp.setMovementMethod(LinkMovementMethod.getInstance());
        spRetrieveType.setOnItemSelectedListener(this);
        scrollView.setOnScrollChangeListener(this);

        //支持密保找回
        if (isHasSetQuestion) {
            GetQuestionsBean.DataBean data = (GetQuestionsBean.DataBean) intent.getSerializableExtra("data");


            //问题1 选择器
            String[] question1Strings = getResources().getStringArray(R.array.question1);


            //问题2 选择器
            String[] question2Strings = getResources().getStringArray(R.array.question2);


            //获取密保问题 并显示
            List<String> question = data.getQuestion();
            if (IF.isEmpty(question) || question.size() == 2) {
                indexFromQuestion1 = Integer.parseInt(question.get(0));
                indexFromQuestion2 = Integer.parseInt(question.get(1));


                tvQuestion1.setText(question1Strings[indexFromQuestion1 - 1]);
                tvQuestion2.setText(question2Strings[indexFromQuestion2 - 1 - 3]);
            } else {
                finish();
            }
            spRetrieveType.setEnabled(true);
            spRetrieveType.setClickable(true);
            spRetrieveType.setSelection(1);

        } else {
            //不支持密保找回
            spRetrieveType.setEnabled(false);
            spRetrieveType.setClickable(false);
            spRetrieveType.setSelection(0);

        }


    }

    @Override
    public void updateResetStatue(boolean isSuccess) {
        if (isSuccess) {
            setResult(Activity.RESULT_OK);
            finish();
        }
    }

    @Override
    public void updateGetAuthCodeStatue(boolean isSuccess) {
        if (isSuccess) {
            countdown.start();
        } else {
            countdown.onFinish();
            countdown.cancel();
        }
    }

    @Override
    public void onShowMessage(String msg) {
        SNToast.toast(msg);
    }

    @Override
    public void onShowLoading(boolean show) {
        if (show) {
            LoadingDialog.show(this, R.string.loading);
        } else {
            LoadingDialog.dismiss();
        }
    }


    @OnClick({R.id.tvNext, R.id.btnObtainAuthCode})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.tvNext:
                String psw = etResetFirstPasswords.getText().toString().trim();
                String pswAgain = etResetConfirmPasswords.getText().toString().trim();
                String authCode = etAuthCode.getText().toString().trim();
                String answer1 = etAnswer1.getText().toString().trim();
                String answer2 = etAnswer2.getText().toString().trim();

                //密保方式 需要校验回答的内容
                if (isHasSetQuestion) {
                    if (IF.isEmpty(answer1)) {
                        setEditTextErrorTips(etAnswer1, getString(R.string.content_answer));
                        return;
                    }
                    if (IF.isEmpty(answer2)) {
                        setEditTextErrorTips(etAnswer2, getString(R.string.content_answer));
                        return;
                    }
                } else {
                    //验证码方式 需要填写验证码
                    if (IF.isEmpty(authCode)) {
                        setEditTextErrorTips(etAuthCode, getString(R.string.content_input_verification_code));
                        return;
                    }
                }
                if (IF.isEmpty(psw)) {
                    setEditTextErrorTips(etResetFirstPasswords, getString(R.string.content_input_password));
                    return;
                }
                if (psw.length() < 6 || psw.length() > 14) {
                    setEditTextErrorTips(etResetFirstPasswords, getString(R.string.content_password_length));
                    return;
                }
                if (!psw.equals(pswAgain)) {
                    setEditTextErrorTips(etResetConfirmPasswords, getString(R.string.content_valid_input));
                    return;
                }
                if (isHasSetQuestion) {
                    getPresenter().requestReset(email, answer1, answer2, indexFromQuestion1, indexFromQuestion2, psw);
                } else {
                    getPresenter().requestReset(email, authCode, psw);
                }
                break;
            case R.id.btnObtainAuthCode:
                btnObtainAuthCode.setClickable(false);
                getPresenter().requestAuthCode(email);
                break;
        }
    }


    private CountDownTimer countdown = new CountDownTimer(60 * 1000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            if (isFinished()) return;
            String seconds = millisUntilFinished / 1000 + "s";
            btnObtainAuthCode.setClickable(false);
            btnObtainAuthCode.setText(seconds);
        }

        @Override
        public void onFinish() {
            if (isFinished()) return;
            btnObtainAuthCode.setClickable(true);
            btnObtainAuthCode.setText(R.string.content_reacquire);
            cancel();
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        countdown.cancel();
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
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (position == 0) {
            llTypeAuthCode.setVisibility(View.VISIBLE);
            tvResetHelp.setVisibility(View.VISIBLE);
            llTypeQuestion.setVisibility(View.GONE);
            isHasSetQuestion  = false;
        } else {
            llTypeAuthCode.setVisibility(View.GONE);
            tvResetHelp.setVisibility(View.GONE);
            llTypeQuestion.setVisibility(View.VISIBLE);
            isHasSetQuestion  = true;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
