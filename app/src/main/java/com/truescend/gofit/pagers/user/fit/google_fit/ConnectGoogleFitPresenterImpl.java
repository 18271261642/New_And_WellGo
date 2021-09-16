package com.truescend.gofit.pagers.user.fit.google_fit;

import android.content.Context;
import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.truescend.gofit.pagers.base.BasePresenter;
import com.truescend.gofit.utils.GoogleFitTool;

/**
 * Author Created by 泽鑫 on 2018/6/1.
 */
public class ConnectGoogleFitPresenterImpl extends BasePresenter<ConnectGoogleFitContract.IView> implements ConnectGoogleFitContract.IPresenter {
    private ConnectGoogleFitContract.IView view;

    public ConnectGoogleFitPresenterImpl(ConnectGoogleFitContract.IView view) {
        this.view = view;
    }

    @Override
    public void requestDisconnectGoogleFit(Context context) {
        Task<Void> response = GoogleFitTool.disconnectedToGoogleFit(context);
        if (response != null) {
            response.addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    if(!isUIEnable())return;
                    view.updateDisconnectSuccessful();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if(!isUIEnable())return;
                    view.updateDisconnectFailure(e.getMessage());
                }
            });
        }
    }
}
