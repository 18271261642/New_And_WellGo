package com.truescend.gofit.pagers.common.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.truescend.gofit.R;
import com.truescend.gofit.pagers.base.dialog.BaseDialog;

import com.truescend.gofit.pagers.base.dialog.*;
/**
 * 功能：通用弹框
 * 输入文本弹窗
 */

public class InputTextDialog {
    private static BaseDialog dialog;


    /**
     *
     * @param context           //上下文参数
     * @param title             //标题
     * @param content           //内容
     * @param cancel            //取消按钮文字
     * @param confirm           //确定按钮文字
     * @param cancelListener    //取消监听
     * @param confirmListener   //确定监听
     * @return create
     */
    public static BaseDialog create(Context context,
                                    String title,
                                    String content,
                                    String cancel,
                                    String confirm,
                                    DialogInterface.OnClickListener cancelListener,
                                    final DialogInterface.OnClickListener confirmListener, final OnInputTextCommitListener textCommitListener) {
        int titleVisible = TextUtils.isEmpty(title)? View.GONE : View.VISIBLE;
        int contentVisible = /*TextUtils.isEmpty(content) ? View.GONE : */View.VISIBLE;
        int cancelVisible = TextUtils.isEmpty(cancel) ? View.GONE : View.VISIBLE;
        int confirmVisible = TextUtils.isEmpty(confirm) ? View.GONE : View.VISIBLE;
        int width = Math.round(context.getResources().getDisplayMetrics().widthPixels * 0.8f);
        final View contentView = LayoutInflater.from(context).inflate(R.layout.dialog_input, null, false);
        final EditText editText = (EditText) contentView.findViewById(R.id.tvCommonContent);

        dialog = new BaseDialog.Builder(context)
                .setContentView(contentView)
                .setWidth(width)
                .setCanceledOnTouchOutside(false)
                .setText(R.id.tvCommonTitle, title)
                .setViewVisible(R.id.tvCommonTitle, titleVisible)
                .setText(R.id.tvCommonContent, content)
                .setViewVisible(R.id.tvCommonContent, contentVisible)
                .setText(R.id.tvCommonCancel, cancel)
                .setViewVisible(R.id.tvCommonCancel, cancelVisible)
                .setText(R.id.tvCommonConfirm,confirm)
                .setViewVisible(R.id.tvCommonConfirm,confirmVisible)
//                .setOnClickListener(tvCommonCancel, cancelListener)
//                .setOnClickListener(tvCommonConfirm, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        if (textCommitListener != null) {
//                            textCommitListener.onTextCommit(editText.getText().toString());
//                        }
//                        confirmListener.onClick(dialog,which);
//                    }
//                })
                .create();
        if (!TextUtils.isEmpty(content)) {
            editText.setSelection(content.length());
        }
        return dialog;
    }

    public interface OnInputTextCommitListener{
        void onTextCommit(String text);
    }

    public static void dismiss(){
        try {
            if (dialog != null) {
                if(dialog.isShowing()) {
                    dialog.dismiss();
                }
                dialog = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
