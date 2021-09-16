package com.truescend.gofit.pagers.common.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.View;

import com.truescend.gofit.R;
import com.truescend.gofit.pagers.base.dialog.BaseDialog;


/**
 * 功能：通用弹框
 * 适用懒得写布局文件且样式没多要求的弹框
 * Author:Created by 泽鑫 on 2018/1/26 10:20.
 */

public class CommonDialog {
    private static BaseDialog dialog;

    /**
     *
     * @param context           //上下文参数
     * @param title             //标题
     * @param cancelListener    //取消监听
     * @param confirmListener   //确定监听
     * @return create
     */
    public static BaseDialog createNoContent(Context context,
                                    String title,
                                    DialogInterface.OnClickListener cancelListener,
                                    DialogInterface.OnClickListener confirmListener){
        return create(context, title, "", cancelListener, confirmListener);

    }

    /**
     *
     * @param context           //上下文参数
     * @param content           //内容
     * @param cancelListener    //取消监听
     * @param confirmListener   //确定监听
     * @return create
     */
    public static BaseDialog createNoTitle(Context context,
                                    String content,
                                    DialogInterface.OnClickListener cancelListener,
                                    DialogInterface.OnClickListener confirmListener){
        return create(context, "", content,  cancelListener, confirmListener);

    }

    /**
     *
     * @param context           //上下文参数
     * @param title             //标题
     * @param cancel            //取消
     * @param confirm           //确定
     * @param cancelListener    //取消监听
     * @param confirmListener   //确定监听
     * @return create
     */
    public static BaseDialog createNoContent(Context context,
                                    String title,
                                    String cancel,
                                    String confirm,
                                    DialogInterface.OnClickListener cancelListener,
                                    DialogInterface.OnClickListener confirmListener){
        return create(context, title, "", cancel, confirm, cancelListener, confirmListener);
    }

    /**
     *
     * @param context           //上下文参数
     * @param content           //内容
     * @param cancel            //取消
     * @param confirm           //确定
     * @param cancelListener    //取消监听
     * @param confirmListener   //确定监听
     * @return create
     */
    public static BaseDialog createNoTitle(Context context,
                                             String content,
                                             String cancel,
                                             String confirm,
                                             DialogInterface.OnClickListener cancelListener,
                                             DialogInterface.OnClickListener confirmListener){
        return create(context, "", content, cancel, confirm, cancelListener, confirmListener);
    }


    /**
     *
     * @param context           //上下文参数
     * @param title             //标题
     * @param content           //内容
     * @param cancelListener    //取消监听
     * @param confirmListener   //确定监听
     * @return create
     */
    public static BaseDialog create(Context context,
                                    String title,
                                    String content,
                                    DialogInterface.OnClickListener cancelListener,
                                    DialogInterface.OnClickListener confirmListener){
        return create(context, title, content,context.getString(R.string.content_cancel),context.getString(R.string.content_confirm), cancelListener, confirmListener);
    }


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
                                    DialogInterface.OnClickListener confirmListener) {
        int titleVisible = TextUtils.isEmpty(title)? View.GONE : View.VISIBLE;
        int contentVisible = TextUtils.isEmpty(content) ? View.GONE : View.VISIBLE;
        int cancelVisible = TextUtils.isEmpty(cancel) ? View.GONE : View.VISIBLE;
        int confirmVisible = TextUtils.isEmpty(confirm) ? View.GONE : View.VISIBLE;
        int width = Math.round(context.getResources().getDisplayMetrics().widthPixels * 0.8f);
        dialog = new BaseDialog.Builder(context)
                .setContentView(R.layout.dialog_common)
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
                .setOnClickListener(R.id.tvCommonCancel, cancelListener)
                .setOnClickListener(R.id.tvCommonConfirm, confirmListener)
                .create();
        return dialog;
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
