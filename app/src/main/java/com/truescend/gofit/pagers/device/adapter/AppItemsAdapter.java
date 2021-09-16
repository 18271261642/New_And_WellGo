package com.truescend.gofit.pagers.device.adapter;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.sn.app.storage.AppPushStorage;
import com.sn.utils.task.SNAsyncTask;
import com.sn.utils.task.SNVTaskCallBack;
import com.truescend.gofit.R;
import com.truescend.gofit.pagers.base.adapter.BaseRecycleViewAdapter;
import com.truescend.gofit.pagers.base.adapter.ViewHolder;
import com.truescend.gofit.pagers.device.bean.ItemApps;

/**
 * 作者:东芝(2019/12/18).
 * 功能:
 */
public class AppItemsAdapter extends BaseRecycleViewAdapter<ItemApps> {

    private final PackageManager pm;
    private Context context;

    public AppItemsAdapter(Context context) {
        super(context);
        this.context = context;
        pm = context.getPackageManager();
    }

    @Override
    public void onItemInflate(int position, final ItemApps item, ViewHolder.BaseViewHolder viewHolder, View rootView) {
        setIcon(item, viewHolder.getImageView(R.id.ivPushItemIcon));
        viewHolder.setTextView(R.id.tvPushItemTitle,item.getAppName());
        CheckBox cb = viewHolder.getView(R.id.cbPushItemSwitch);
        cb.setChecked(item.isChecked());
        cb.setTag(item.getAppInfo().packageName);
        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                AppPushStorage.setAppPushCheck((String) buttonView.getTag(),isChecked);
                if(isChecked) {
                    AppPushStorage.setAppPushCheckAppName((String) buttonView.getTag(), item.getAppName());
                }else{
                    //清空
                    AppPushStorage.setAppPushCheckAppName("", item.getAppName());
                }
            }
        });
    }

    private void setIcon(ItemApps item, final ImageView icon) {
        icon.setTag(item);
        SNAsyncTask.execute(new SNVTaskCallBack(icon) {
            Drawable drawable;

            @Override
            public void run() throws Throwable {
                ImageView target = getTarget();
                if (target != null) {
                    ItemApps item = (ItemApps) target.getTag();
                    drawable = item.getAppInfo().loadIcon(pm);
                }
            }

            @Override
            public void done() {
                super.done();
                ImageView target = getTarget();
                if (target != null && drawable != null) {
                    target.setImageDrawable(drawable);
                }
            }
        });
    }

    private void setText(ItemApps item, final TextView tv) {
        tv.setTag(item);
        SNAsyncTask.execute(new SNVTaskCallBack(tv) {
            String appName;

            @Override
            public void run() throws Throwable {
                TextView target = getTarget();
                if (target != null) {
                    ItemApps item = (ItemApps) target.getTag();
                    appName = item.getAppInfo().loadLabel(pm).toString();
                }
            }

            @Override
            public void done() {
                super.done();
                TextView target = getTarget();
                if (target != null && appName != null) {
                    target.setText(appName);
                }
            }
        });
    }

    @Override
    public int initLayout(int viewType) {
        return R.layout.item_apps;
    }
}
