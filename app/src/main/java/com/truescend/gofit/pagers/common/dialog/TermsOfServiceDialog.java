package com.truescend.gofit.pagers.common.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.TextView;

import com.sn.login.LoginSDK;
import com.sn.utils.LanguageUtil;
import com.truescend.gofit.R;
import com.truescend.gofit.pagers.base.dialog.BaseDialog;
import com.truescend.gofit.utils.Constant;
import com.truescend.gofit.utils.PageJumpUtil;
import com.truescend.gofit.utils.ResUtil;

/**
 * Author Created by 泽鑫 on 2018/5/4.
 */
public class TermsOfServiceDialog {
    private static BaseDialog dialog;

    public static BaseDialog create(final Context context) {
        int width = Math.round(context.getResources().getDisplayMetrics().widthPixels * 0.9f);
        int height  = Math.round(context.getResources().getDisplayMetrics().heightPixels * 0.7f);
        dialog = new BaseDialog.Builder(context)
                .setContentView(R.layout.dialog_terms_of_service)
                .setCanceledOnTouchOutside(false)
                .setOnClickListener(R.id.tvTermsConfirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setWidth(width)
                .setHeight(height)
                .create();
        TextView tvTermsFirst = dialog.findViewById(R.id.tvTermsFirst);
        TextView tvTermsSecond = dialog.findViewById(R.id.tvTermsSecond);
        if (tvTermsFirst != null && LanguageUtil.isZH()){
            tvTermsFirst.setVisibility(View.GONE);
        }
        if (tvTermsSecond != null) {
            tvTermsSecond.setText(ResUtil.formatHtml(context.getString(R.string.content_terms_second), Constant.URL.HELP_PRIVACY_POLICY));
//            tvTermsSecond.setMovementMethod(LinkMovementMethod.getInstance());
            tvTermsSecond.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PageJumpUtil.startHelpActivity(context);
                }
            });
        }
        return dialog;
    }
}
