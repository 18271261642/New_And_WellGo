package com.truescend.gofit.pagers.start.resetpsw.checker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.sn.app.net.data.app.bean.GetQuestionsBean;
import com.sn.utils.IF;
import com.sn.utils.SNToast;
import com.truescend.gofit.R;
import com.truescend.gofit.pagers.base.BaseActivity;
import com.truescend.gofit.pagers.common.dialog.LoadingDialog;
import com.truescend.gofit.utils.PageJumpUtil;
import com.truescend.gofit.views.HintMultiLineEditText;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 作者:东芝(2018/6/14).
 * 功能:重置密码前的检测器, 检测是否支持密保
 */

public class ResetPswCheckerActivity extends BaseActivity<ResetPswCheckerPresenterImpl, IResetPswCheckerContract.IView> implements IResetPswCheckerContract.IView {
    
    public static void startActivity(Context context){
        context.startActivity(new Intent(context,ResetPswCheckerActivity.class));
    }
    
    @BindView(R.id.etResetPswAccount)
    HintMultiLineEditText etResetPswAccount;
    @BindView(R.id.tvNext)
    TextView tvNext;

    @Override
    protected ResetPswCheckerPresenterImpl initPresenter() {
        return new ResetPswCheckerPresenterImpl(this);
    }

    @Override
    protected int initLayout() {
        return R.layout.activity_resetpsw_checker;
    }

    @Override
    protected void onCreateActivity(Bundle savedInstanceState) {
        setTitle(getString(R.string.title_forget_password));
    }


    @OnClick({R.id.tvNext})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tvNext:
                String input = etResetPswAccount.getText().toString().trim();
                if (IF.isEmpty(input)) {
                    setEditTextErrorTips(etResetPswAccount, etResetPswAccount.getHint());
                    return;
                }

                getPresenter().requestHasSetQuestionsStatus(input);
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
        et.requestFocus();
        et.setError(error);

    }

    @Override
    public void onHasSetQuestionsStatus(boolean has, String email, GetQuestionsBean.DataBean bean) {
            PageJumpUtil.startResetPswFromQuestionActivity(this,email,has,bean);
    }


    @Override
    public void onShowLoading(boolean show) {
        if (show) {
            LoadingDialog.show(this, R.string.loading);
        } else {
            LoadingDialog.dismiss();
        }
    }

    @Override
    public void onShowMessage(String msg) {
        SNToast.toast(msg);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //重置密码成功! 直接回到登录页面
        if(requestCode==PageJumpUtil.REQUEST_CODE_RESULT&&resultCode== Activity.RESULT_OK)
        {
            finish();
        }
    }
}
