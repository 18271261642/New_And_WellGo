//package com.truescend.gofit.pagers.start.resetpsw.from_authcode;
//
//import android.content.Context;
//import android.content.Intent;
//import android.os.Bundle;
//import android.os.CountDownTimer;
//import android.support.v7.widget.AppCompatButton;
//import android.text.Html;
//import android.text.Spanned;
//import android.text.method.LinkMovementMethod;
//import android.view.View;
//import android.widget.EditText;
//import android.widget.TextView;
//
//import com.sn.utils.IF;
//import com.sn.utils.RegexUtil;
//import com.sn.utils.SNToast;
//import com.sn.utils.math.MD5Util;
//import com.truescend.gofit.R;
//import com.truescend.gofit.pagers.base.BaseActivity;
//import com.truescend.gofit.pagers.common.dialog.LoadingDialog;
//import com.truescend.gofit.views.TitleLayout;
//
//import butterknife.BindView;
//import butterknife.OnClick;
//
///**
// * 作者:东芝(2018/6/14).
// * 功能:通过验证码找回密码
// * 遗弃
// */
//@Deprecated
//public class ResetPswFromAuthCodeActivity extends BaseActivity<ResetPswFromAuthCodePresenterImpl, IResetPswFromAuthCodeContract.IView> implements IResetPswFromAuthCodeContract.IView {
//
//    public static void startActivity(Context context){
//        context.startActivity(new Intent(context,ResetPswFromAuthCodeActivity.class));
//    }
//
//    @BindView(R.id.etResetAccount)
//    EditText etResetAccount;
//    @BindView(R.id.etResetVerificationCode)
//    EditText etResetVerificationCode;
//    @BindView(R.id.btResetObtain)
//    AppCompatButton btResetObtain;
//    @BindView(R.id.etResetFirstPasswords)
//    EditText etResetFirstPasswords;
//    @BindView(R.id.etResetConfirmPasswords)
//    EditText etResetConfirmPasswords;
//    @BindView(R.id.tvResetSignIn)
//    TextView tvResetSignIn;
//    @BindView(R.id.tvResetHelp)
//    TextView tvResetHelp;
//
//    private TitleLayout titleLayout;
//
//    @Override
//    protected ResetPswFromAuthCodePresenterImpl initPresenter() {
//        return new ResetPswFromAuthCodePresenterImpl(this);
//    }
//
//    @Override
//    protected int initLayout() {
//        return R.layout.activity_resetpsw_from_auth_code;
//    }
//
//    @Override
//    protected void onCreateActivity(Bundle savedInstanceState) {
//        setTitle(R.string.title_forget_password);
//        initView();
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        countdown.cancel();
//    }
//
//    @Override
//    protected void onCreateTitle(TitleLayout titleLayout) {
//        this.titleLayout = titleLayout;
//        titleLayout.setLeftIconFinishActivity(this);
//    }
//
//    private CountDownTimer countdown = new CountDownTimer(60 * 1000, 1000) {
//        @Override
//        public void onTick(long millisUntilFinished) {
//            if (isFinished()) return;
//            String seconds = millisUntilFinished / 1000 + "s";
//            btResetObtain.setClickable(false);
//            btResetObtain.setText(seconds);
//        }
//
//        @Override
//        public void onFinish() {
//            if (isFinished()) return;
//            btResetObtain.setClickable(true);
//            btResetObtain.setText(R.string.content_reacquire);
//            cancel();
//        }
//    };
//
//    @Override
//    public void updateObtainVerification(boolean isSuccess, String message) {
//        if (!isSuccess) {
//            SNToast.toast(message);
//            countdown.onFinish();
//            countdown.cancel();
//        }
//    }
//
//    @Override
//    public void updateResetStatue(boolean isSuccess, String message) {
//        if (isSuccess) {
//            finish();
//        }
//        SNToast.toast(message);
//    }
//
//    @Override
//    public void onShowLoading(boolean show) {
//        if (show) {
//            LoadingDialog.show(this, R.string.loading);
//        } else {
//            LoadingDialog.dismiss();
//        }
//    }
//
//    private void initView() {
//        titleLayout.setTitle(getString(R.string.title_forget_password));
//        etResetFirstPasswords.setHint(R.string.hint_input_new_password);
//        etResetConfirmPasswords.setHint(R.string.content_confirm_new_password);
//        tvResetSignIn.setText(R.string.content_commit);
//        Spanned html = Html.fromHtml(getString(R.string.content_verification_help));
//        tvResetHelp.setText(html);
//        tvResetHelp.setMovementMethod(LinkMovementMethod.getInstance());
//
//    }
//
//    @OnClick({R.id.btResetObtain, R.id.tvResetSignIn, R.id.tvResetHelp})
//    public void OnClick(View view) {
//        String email = etResetAccount.getText().toString().trim();
//        switch (view.getId()) {
//            case R.id.btResetObtain:
//                if (RegexUtil.isEmail(email)) {
//                    countdown.start();
//                    getPresenter().requestVerificationCode(email);
//                } else {
//                    setEditTextErrorTips(etResetAccount, getString(R.string.content_enter_email_is_correct));
//                }
//                break;
//            case R.id.tvResetSignIn:
//                String account = etResetAccount.getText().toString().trim();
//                String code = etResetVerificationCode.getText().toString().trim();
//                String password = etResetFirstPasswords.getText().toString().trim();
//                String confirmPassword = etResetConfirmPasswords.getText().toString().trim();
//                if (!registerOrCommit(account, code, password, confirmPassword)) {
//                    return;
//                }
//                getPresenter().requestReset(email, code, MD5Util.md5(password));
//                break;
//        }
//    }
//
//    private boolean registerOrCommit(String account, String code, String password, String confirmPassword) {
//        if (IF.isEmpty(account)) {
//            setEditTextErrorTips(etResetAccount, getString(R.string.content_input_email));
//            return false;
//        }
//        if (!RegexUtil.isEmail(account)) {
//            setEditTextErrorTips(etResetAccount, getString(R.string.content_input_correct_email));
//            return false;
//        }
//        if (IF.isEmpty(code)) {
//            setEditTextErrorTips(etResetVerificationCode, getString(R.string.content_input_verification_code));
//            return false;
//        }
//        if (IF.isEmpty(password)) {
//            setEditTextErrorTips(etResetFirstPasswords, getString(R.string.content_input_password));
//            return false;
//        }
//        if (password.length() < 6 || password.length() > 14) {
//            setEditTextErrorTips(etResetFirstPasswords, getString(R.string.content_password_length));
//            return false;
//        }
//        if (!password.equals(confirmPassword)) {
//            setEditTextErrorTips(etResetConfirmPasswords, getString(R.string.content_valid_input));
//            return false;
//        }
//        return true;
//    }
//
//    /**
//     * 设置编辑框错误提示信息
//     *
//     * @param et
//     * @param error
//     */
//    private void setEditTextErrorTips(EditText et, CharSequence error) {
//        et.setFocusable(true);
//        et.setFocusableInTouchMode(true);
//        et.requestFocus();
//        et.setError(error);
//    }
//
//}
