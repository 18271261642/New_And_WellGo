package com.truescend.gofit.pagers.common.dialog;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.truescend.gofit.R;
import com.truescend.gofit.pagers.base.dialog.BaseDialog;
import com.truescend.gofit.utils.ResUtil;
import com.truescend.gofit.views.TimePicker;

/**
 * 作者:东芝(2018/2/5).
 * 功能:时间范围选择器
 */

public class TimeCyclePickerDialog {
    private BaseDialog dialog;
    private final String startTime;
    private final String endTime;


    private TimePicker startTimeTimePicker;
    private TimePicker endTimeTimePicker;

    public TimeCyclePickerDialog(Context context,String startTime,String endTime) {
        dialog = new BaseDialog.Builder(context)
                .setContentView(R.layout.dialog_time_cycle_picker)
                .fullWidth()
                .fromBottom(true)
                .create();
        this.startTime = startTime;
        this.endTime = endTime;
        initItem();
        initData();
    }


    /**
     * 获取控件并开启监听
     */
    private void initItem() {
        TextView tvExit = dialog.findViewById(R.id.tvExit);
        TextView tv = dialog.findViewById(R.id.tvDone);

        startTimeTimePicker = dialog.findViewById(R.id.tpStartTime);
        endTimeTimePicker = dialog.findViewById(R.id.tpEndTime);


        if (tvExit != null) {
            tvExit.setOnClickListener(clickListener);
        }
        if (tv != null) {
            tv.setOnClickListener(clickListener);
        }

        startTimeTimePicker.setOnTimePickerListener(timePickerListener);
        endTimeTimePicker.setOnTimePickerListener(timePickerListener);

    }

    /**
     * 初始化数据
     */
    private void initData() {
        String[] st = startTime.split(":");
        String[] et = endTime.split(":");

        startTimeTimePicker.setHour(Integer.parseInt(st[0]));
        startTimeTimePicker.setMinute(Integer.parseInt(st[1]));
        endTimeTimePicker.setHour(Integer.parseInt(et[0]));
        endTimeTimePicker.setMinute(Integer.parseInt(et[1]));

    }


    public void show() {
        dialog.show();
    }

    public void dismiss() {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }


    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tvExit:
                    dismiss();
                    break;
                case R.id.tvDone:
                    if (onSettingListener != null) {
                        String startTime =ResUtil.format( "%02d:%02d", startTimeTimePicker.getHour(), startTimeTimePicker.getMinute());
                        String endTime = ResUtil.format( "%02d:%02d", endTimeTimePicker.getHour(), endTimeTimePicker.getMinute());
                        onSettingListener.onTimeChanged(startTime,endTime);
                    }
                    dismiss();
                    break;
            }
        }
    };

    private TimePicker.OnTimePickerListener timePickerListener = new TimePicker.OnTimePickerListener() {
        @Override
        public void onValueChange(TimePicker picker, int hour, int minute) {
//            correctErrorTime(startTimeTimePicker.getHour(), startTimeTimePicker.getMinute(), endTimeTimePicker.getHour(), endTimeTimePicker.getMinute());
        }
    };

    /**
     * 强制选择的时间结束时间不能小于开始时间
     *
     * @param sHour   开始小时
     * @param sMinute 开始分钟
     * @param eHour   结束小时
     * @param eMinute 结束分钟
     */
    private void correctErrorTime(int sHour, int sMinute, int eHour, int eMinute) {
        if (eHour < sHour) {
            endTimeTimePicker.setHour(sHour);
        }
        if (eMinute < sMinute) {
            endTimeTimePicker.setMinute(sMinute);
        }
    }

    private OnSettingListener onSettingListener;

    public void setOnSettingListener(OnSettingListener onSettingListener) {
        this.onSettingListener = onSettingListener;
    }

    public interface OnSettingListener{
        void onTimeChanged(String startTime,String endTime);
    }
}
