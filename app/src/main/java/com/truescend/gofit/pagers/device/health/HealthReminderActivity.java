package com.truescend.gofit.pagers.device.health;

import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;

import com.sn.app.db.data.config.bean.HealthReminderConfig;
import com.truescend.gofit.R;
import com.truescend.gofit.pagers.base.BaseActivity;
import com.truescend.gofit.pagers.device.bean.ItemDeviceCommon;
import com.truescend.gofit.utils.PageJumpUtil;
import com.truescend.gofit.utils.ResUtil;
import com.truescend.gofit.views.TitleLayout;

import butterknife.BindView;

/**
 * 功能：健康提醒页面
 * Author:Created by 泽鑫 on 2017/11/24 11:07.
 */

public class HealthReminderActivity extends BaseActivity<HealthReminderPresenterImpl, IHealthReminderContract.IView> implements IHealthReminderContract.IView {
    public final static String DRINK_OR_SCHEDULE = "drinkOrSchedule";
    @BindView(R.id.ilDeviceHealthReminderSedentary)
    View ilDeviceHealthReminderSedentary;
    @BindView(R.id.ilDeviceHealthReminderDrink)
    View ilDeviceHealthReminderDrink;

    private ItemDeviceCommon sedentaryItem;
    private ItemDeviceCommon drinkItem;

    @Override
    protected HealthReminderPresenterImpl initPresenter() {
        return new HealthReminderPresenterImpl(this);
    }

    @Override
    protected int initLayout() {
        return R.layout.activity_health_reminder;
    }

    @Override
    protected void onCreateActivity(Bundle savedInstanceState) {
        initItem();
    }

    @Override
    protected void onCreateTitle(TitleLayout titleLayout) {
        titleLayout.setTitle(getString(R.string.title_health_reminder));
        titleLayout.setLeftIconFinishActivity(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPresenter().requestLoadReminderItemData();
    }

    private void initItem() {
        sedentaryItem = new ItemDeviceCommon(ilDeviceHealthReminderSedentary);
        sedentaryItem.setTitle(R.string.content_remind_sedentary);
        sedentaryItem.setSettingIconOnClickListener(mHealthReminderClickListener);
        sedentaryItem.setSwitchOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                getPresenter().requestChangeReminderData(isChecked, HealthReminderConfig.HealthReminder.TYPE_SEDENTARY);
            }
        });

        drinkItem = new ItemDeviceCommon(ilDeviceHealthReminderDrink);
        drinkItem.setTitle(R.string.content_reminder_drink);
        drinkItem.setSettingIconOnClickListener(mHealthReminderClickListener);
        drinkItem.setSwitchOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                getPresenter().requestChangeReminderData(isChecked, HealthReminderConfig.HealthReminder.TYPE_DRINK);
            }
        });
    }

    private View.OnClickListener mHealthReminderClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            View v = (View) view.getParent();
            switch (v.getId()) {
                case R.id.ilDeviceHealthReminderSedentary:
//                    getPresenter().requestShowReminderDialog(HealthReminderConfig.HealthReminder.TYPE_SEDENTARY);
                    PageJumpUtil.startReminderActivity(HealthReminderActivity.this, HealthReminderConfig.HealthReminder.TYPE_SEDENTARY);
                    break;
                case R.id.ilDeviceHealthReminderDrink:
//                    getPresenter().requestShowReminderDialog(HealthReminderConfig.HealthReminder.TYPE_DRINK);
                    PageJumpUtil.startReminderActivity(HealthReminderActivity.this, HealthReminderConfig.HealthReminder.TYPE_DRINK);
                    break;
                default:
                    break;
            }
        }
    };


    @Override
    public void onUpdateReminderItemData(boolean isSedentaryOn, boolean isDrinkOn) {
        sedentaryItem.setSwitchCheck(isSedentaryOn);
        drinkItem.setSwitchCheck(isDrinkOn);
    }

//    @Override
//    public void onShowReminderDialog(HealthReminderConfig.HealthReminder healthReminder, @HealthReminderConfig.HealthReminder.Type final int type) {
//        HealthReminderDialog healthReminderDialog = new HealthReminderDialog(HealthReminderActivity.this, healthReminder);
//        healthReminderDialog.setOnHealthReminderSettingListener(new HealthReminderDialog.OnHealthReminderSettingListener() {
//            @Override
//            public void onDataChanged(HealthReminderConfig.HealthReminder healthReminder) {
//                getPresenter().requestChangeReminderData(healthReminder,type);
//                getPresenter().requestLoadReminderItemData();
//            }
//        });
//        healthReminderDialog.show();
//    }

    @Override
    public void onUpdateRemindSedentaryData(int startHour, int startMinute, int endHour, int endMinute, int intervalTime) {
        float intervalHour = (float)intervalTime / 60f;
        sedentaryItem.setIntervalTime(ResUtil.format(getString(R.string.content_reminder_time),intervalHour, startHour, startMinute, endHour, endMinute));
    }

    @Override
    public void onUpdateRemindDrinkData(int startHour, int startMinute, int endHour, int endMinute, int intervalTime) {
        float intervalHour = (float)intervalTime / 60f;
        drinkItem.setIntervalTime(ResUtil.format(getString(R.string.content_reminder_time),intervalHour, startHour, startMinute, endHour, endMinute));

    }

}
