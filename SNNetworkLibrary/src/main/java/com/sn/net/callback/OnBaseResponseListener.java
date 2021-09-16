package com.sn.net.callback;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 作者:东芝(2017/7/31).
 * 功能:网络响应
 */

public abstract class OnBaseResponseListener<T> implements Callback<T> {

    protected abstract void onBaseResponse(T body) throws Throwable;

    protected abstract void onBaseFailure(int ret, String msg);

    protected abstract String onBaseNetworkError(String msg);

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        if (call.isCanceled()) {
            return;
        }
        if (response.isSuccessful()) {
            try {
                onBaseResponse(response.body());
            } catch (Throwable throwable) {
                onBaseFailure(-3, insertErrorMessage(throwable.toString()));
            }
        } else {
            onBaseFailure(-2, insertErrorMessage(response.message()));
        }
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        t.printStackTrace();
        if (call.isCanceled()) {
            return;
        }
        onBaseFailure(-1, insertErrorMessage(t.toString()));

    }

    protected String insertErrorMessage(String msg) {
        if (msg != null) {
            if ((msg.contains("Exception") || msg.toLowerCase().contains("timeout") || msg.contains("only-if-cached")))
                msg = onBaseNetworkError(msg);
        }
        return msg;
    }


}
