package com.truescend.gofit.pagers.device.setting.feedback;

import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.sn.utils.SNToast;
import com.sn.utils.view.ViewCompat;
import com.truescend.gofit.R;
import com.truescend.gofit.pagers.base.BaseActivity;
import com.truescend.gofit.pagers.common.dialog.LoadingDialog;
import com.truescend.gofit.utils.Constant;
import com.truescend.gofit.utils.ResUtil;
import com.truescend.gofit.views.TitleLayout;

import butterknife.BindView;
import butterknife.OnClick;

import static com.truescend.gofit.utils.ResUtil.getString;


/**
 * 功能：意见反馈界面
 * 作者:东芝(2018/2/3).
 * 描述:反馈
 */
public class FeedbackActivity extends BaseActivity<FeedbackPresenter, IFeedbackContract.IView> implements IFeedbackContract.IView {
    @BindView(R.id.tlTitle)
    TitleLayout tlTitle;
    @BindView(R.id.etFeedBackInput)
    EditText etFeedBackInput;
    @BindView(R.id.etFeedBackEmail)
    EditText etFeedBackEmail;
    @BindView(R.id.etFeedBackBand)
    EditText etFeedBackBand;
    @BindView(R.id.tvFeedBackPhone)
    TextView tvFeedBackPhone;
    @BindView(R.id.tvFeedBackSubmit)
    TextView tvFeedBackSubmit;
    @BindView(R.id.tvFeedBackHelp)
    TextView tvFeedBackHelp;

    @Override
    protected FeedbackPresenter initPresenter() {
        return new FeedbackPresenter(this);
    }

    @Override
    protected int initLayout() {
        return R.layout.activity_feedback;
    }

    @Override
    protected void onCreateActivity(Bundle savedInstanceState) {
        initTitle();
        initView();
    }

    private void initTitle() {
        tlTitle.setTitle(getString(R.string.title_feedback));
    }

    private void initView() {


        tvFeedBackHelp.setText(ResUtil.formatHtml(getString(R.string.content_troubleshooting_help), Constant.URL.HELP_VERIFICATION_CODE));
        tvFeedBackHelp.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    public void onSendFeedbackMessageError(String msg) {
        SNToast.toast(msg);
    }

    @Override
    public void onSendFeedbackMessageSuccess() {
        SNToast.toast(R.string.toast_feedback_success);
        finish();
    }


    @OnClick({R.id.tvFeedBackSubmit})
    public void onClick(View view) {
        String msg = ViewCompat.getText(etFeedBackInput);
        String email = ViewCompat.getText(etFeedBackEmail);
        String deviceName = ViewCompat.getText(etFeedBackBand);
        getPresenter().requestSendFeedbackMessage(this, email, deviceName, msg);
    }

    @Override
    public void onInputEmailError(String msg) {
        setEditTextErrorTips(etFeedBackEmail, msg);
    }

    @Override
    public void onInputContentError(String msg) {
        setEditTextErrorTips(etFeedBackInput, msg);
    }

    @Override
    public void onInputDeviceNameError(String msg) {
        setEditTextErrorTips(etFeedBackBand, msg);
    }

    @Override
    public void setInputEmail(String value) {
        etFeedBackEmail.setText(value);
//        etFeedBackEmail.setKeyListener(null);
    }

    @Override
    public void setInputDeviceName(String value) {
        etFeedBackBand.setText(value);
        etFeedBackBand.setKeyListener(null);
    }

    @Override
    public void setPhoneName(String value) {
        tvFeedBackPhone.setText(value);
    }

    @Override
    public void onShowLoading(boolean isShow) {
        if (isShow) {
            LoadingDialog.show(this, R.string.dialog_waiting);
        } else {
            LoadingDialog.dismiss();
        }
    }

    /**
     * 设置编辑框错误提示信息
     *
     * @param et    编辑框控件
     * @param error 错误信息
     */
    private void setEditTextErrorTips(final EditText et, CharSequence error) {
        et.setFocusable(true);
        et.setFocusableInTouchMode(true);
        et.requestFocus();
        et.setError(error);
    }

}
