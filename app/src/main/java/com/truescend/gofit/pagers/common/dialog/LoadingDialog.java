package com.truescend.gofit.pagers.common.dialog;

import android.content.Context;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;

import android.view.View;
import android.widget.TextView;

import com.truescend.gofit.R;
import com.truescend.gofit.pagers.base.dialog.BaseDialog;

/**
 * 作者:东芝(2017/12/14).
 * 功能:加载中
 */

public class LoadingDialog {

    private static BaseDialog show;


    public static BaseDialog show(Fragment fragment, @StringRes int titleResId) {
        return show(fragment.getActivity(), fragment.getString(titleResId), false);
    }

    public static BaseDialog show(View v, @StringRes int titleResId) {
        return show(v.getContext(), v.getResources().getString(titleResId), false);
    }

    public static BaseDialog show(Context context, @StringRes int titleResId) {
        return show(context, context.getString(titleResId), false);
    }

    public static BaseDialog show(Context context, @StringRes int titleResId, boolean isCancelable) {
        return show(context, context.getString(titleResId), isCancelable);
    }

    public static BaseDialog show(Fragment fragment, String title) {
        return show(fragment.getActivity(), title, false);
    }

    public static BaseDialog show(View v, String title) {
        return show(v.getContext(), title, false);
    }

    public static BaseDialog show(Context context, String title) {
        return show(context, title, false);
    }

    public static BaseDialog show(Context context, String title, boolean isCancelable) {
        if (title == null) {
            title = context.getString(R.string.loading);
        }
        dismiss();
        try {
            show = new BaseDialog.Builder(context)
                    .setContentView(R.layout.dialog_loading)
                    .setCanceledOnTouchOutside(isCancelable)
                    .show();
            TextView tvDialogContent = show.findViewById(R.id.tvDialogContent);
            if (tvDialogContent != null) {
                tvDialogContent.setText(title);
            }
            show.show();
        } catch (Throwable ignored) {
        }
        return show;
    }

    public static  void updateTitle( String title){
        if(show==null)return;
        TextView tvDialogContent = show.findViewById(R.id.tvDialogContent);
        if (tvDialogContent != null) {
            tvDialogContent.setText(title);
        }
    }

    public static BaseDialog loading(Context context){
        show = new BaseDialog.Builder(context)
                .setContentView(R.layout.dialog_login)
                .setCanceledOnTouchOutside(false)
                .show();
        return show;
    }

    public static void dismiss() {
        try {
            if (show != null) {
                show.dismiss();
                show = null;
            }
        } catch (Throwable ignored) {
        }
    }
}
