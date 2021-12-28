package com.truescend.gofit.views;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;


import com.truescend.gofit.R;

import androidx.annotation.NonNull;

/**
 * Created by Admin
 * Date 2020/10/28
 */
public class LocalPermissDescView extends Dialog implements View.OnClickListener {


    //To see maps for automatically tracked activities ,allow Fitness Funds to use your location all of the time.

    //Fitness Funds will use location in the background to show walks ,runs,and bike rides on a map


    private TextView btnTv;

    private LocalPermissionListener localPermissionListener;

    public void setLocalPermissionListener(LocalPermissionListener localPermissionListener) {
        this.localPermissionListener = localPermissionListener;
    }

    public LocalPermissDescView(@NonNull Context context) {
        super(context);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_local_permiss_layaout);

        initViews();

    }

    private void initViews() {
        btnTv = findViewById(R.id.localPermissionBtn);
        btnTv.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.localPermissionBtn:
                cancel();
                if(localPermissionListener != null)
                    localPermissionListener.allowPermiss();
                break;
        }
    }


    public interface LocalPermissionListener{
        void allowPermiss();
    }
}
