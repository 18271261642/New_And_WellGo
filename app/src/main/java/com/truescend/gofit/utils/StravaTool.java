package com.truescend.gofit.utils;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.google.gson.JsonSyntaxException;
import com.sn.app.storage.UserStorage;
import com.sn.utils.DateUtil;
import com.sn.utils.SNLog;
import com.sn.utils.task.SNAsyncTask;
import com.sn.utils.task.SNVTaskCallBack;
import com.sweetzpot.stravazpot.authenticaton.api.AccessScope;
import com.sweetzpot.stravazpot.authenticaton.api.ApprovalPrompt;
import com.sweetzpot.stravazpot.authenticaton.api.AuthenticationAPI;
import com.sweetzpot.stravazpot.authenticaton.api.StravaLogin;
import com.sweetzpot.stravazpot.authenticaton.model.AppCredentials;
import com.sweetzpot.stravazpot.authenticaton.model.LoginResult;
import com.sweetzpot.stravazpot.authenticaton.model.Token;
import com.sweetzpot.stravazpot.authenticaton.ui.StravaLoginActivity;
import com.sweetzpot.stravazpot.common.api.AuthenticationConfig;
import com.sweetzpot.stravazpot.common.api.StravaConfig;
import com.sweetzpot.stravazpot.common.api.exception.StravaUnauthorizedException;
import com.sweetzpot.stravazpot.upload.api.UploadAPI;
import com.sweetzpot.stravazpot.upload.model.DataType;
import com.sweetzpot.stravazpot.upload.model.UploadActivityType;
import com.sweetzpot.stravazpot.upload.model.UploadStatus;
import com.truescend.gofit.App;
import com.truescend.gofit.BuildConfig;
import com.truescend.gofit.R;

import java.io.File;
import java.text.ParseException;
import java.util.List;

public class StravaTool {
    private static final int RQ_LOGIN = 1243;
    private static final int CLIENT_ID = BuildConfig.Strava_ClientID;
    private static final String CLIENT_SECRET = BuildConfig.Strava_ClientSecret;

    public static UploadStatus uploadGpxFile(final File file, String description) {
        UploadAPI uploadAPI = new UploadAPI(StravaConfig.withToken(UserStorage.getStravaAccessToken()).build());
        UploadStatus execute = null;
        try {
            execute = uploadAPI.uploadFile(file)
                    .withDataType(DataType.GPX)
                    .withActivityType(UploadActivityType.RUN)
                    .withName("Share from " + ResUtil.getString(R.string.app_name))
                    .withDescription(description)
                    .isPrivate(false)
                    .hasTrainer(false)
                    .isCommute(false)
                    .withExternalID(file.getName())
                    .execute();
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            //SDK过旧 可能成功后返回的json数据中 因数据类型的变动导致Gson解析失败...   其实是成功的
        } catch (NumberFormatException e) {
            e.printStackTrace();
            //SDK过旧 可能成功后返回的json数据中 因数据类型的变动导致Gson解析失败...   其实是成功的
        }
        setGpxFileUploaded(file);

        return execute;

    }


    public static void saveToGpxFile(List<GPXCreator.GPX> gpxs, String dateTime) {
        GPXCreator gpxCreator = new GPXCreator();
        gpxCreator.addAll(gpxs);
        if (!gpxCreator.isEmpty()) {
            boolean save = false;
            try {
                save = gpxCreator.save(getGpxFile(dateTime), "Share from " + ResUtil.getString(R.string.app_name), 9);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            SNLog.i("保存GPX:" + save);
        }
    }

    public static File getGpxFile(String dateTime) throws ParseException {
        return new File(Constant.Path.CACHE_MAP_GPX_PATH, "gpx_" + DateUtil.convertStringToNewString(DateUtil.YYYY_MM_DD_HH_MM_SS, "yyyy_MM_dd_HH_mm_ss", dateTime) + ".gpx");
    }

    public static boolean checkGpxFileExists(String dateTime) {
        try {
            File file = getGpxFile(dateTime);
            if (file.exists() && file.canRead() && file.length() > 0) {
                return true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }


    public static boolean isGpxFileUploaded(String dateTime) {
        try {
            File file = new File(Constant.Path.CACHE_MAP_GPX_PATH, "gpx_uploaded_" + DateUtil.convertStringToNewString(DateUtil.YYYY_MM_DD_HH_MM_SS, "yyyy_MM_dd_HH_mm_ss", dateTime) + ".gpx");
            if (file.exists() && file.canRead() && file.length() > 0) {
                return true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void setGpxFileUploaded(File file) {
        if (file.exists() && file.canRead() && file.length() > 0) {
            String newName = file.getName().replaceAll("gpx_", "gpx_uploaded_");
            boolean b = file.renameTo(new File(file.getParent(), newName));
        }
    }

    public static boolean isAuthorized() {
        return !TextUtils.isEmpty(UserStorage.getStravaAccessToken());
    }

    public static void clearAuth(final OnStravaAuthListener listener) {
        SNAsyncTask.execute(new SNVTaskCallBack() {
            @Override
            public void run() throws Throwable {
                try {

                    AuthenticationAPI api = new AuthenticationAPI(AuthenticationConfig.create().build());
                    api.deauthorize().execute();

                } catch (StravaUnauthorizedException e) {
                    clearWebViewCache(App.getContext());
                    UserStorage.setStravaAccessToken(null);
                }
            }

            @Override
            public void done() {
                if (listener != null) {
                    listener.isUnAuthorized();
                }
            }

            @Override
            public void error(Throwable e) {
                super.error(e);
                if (listener != null) {
                    listener.failed(e);
                }
            }

            /**
             * 清除WebView缓存
             */
            void clearWebViewCache(Context context) {
                // 清除cookie即可彻底清除缓存
                CookieSyncManager.createInstance(context);
                CookieManager.getInstance().removeAllCookie();
            }


        });
    }

    public static void startAuth(Activity activity) {
        Intent intent = StravaLogin.withContext(activity)
                .withClientID(CLIENT_ID)
                .withRedirectURI("http://iwear88.com")
                .withApprovalPrompt(ApprovalPrompt.AUTO)
                .withAccessScope(AccessScope.VIEW_PRIVATE_WRITE)
                .makeIntent();
        activity.startActivityForResult(intent, RQ_LOGIN);
    }


    public static void onActivityResult(int requestCode, int resultCode, Intent data, final OnStravaAuthListener listener) {
        if (requestCode == RQ_LOGIN && resultCode == Activity.RESULT_OK && data != null) {
            final String code = data.getStringExtra(StravaLoginActivity.RESULT_CODE);
            SNAsyncTask.execute(new SNVTaskCallBack() {


                @Override
                public void prepare() {
                    super.prepare();
                    if (listener != null) {
                        listener.isAuthorization();
                    }
                }

                @Override
                public void run() throws Throwable {
                    AuthenticationConfig config = AuthenticationConfig.create()
                            .build();
                    AuthenticationAPI api = new AuthenticationAPI(config);
                    LoginResult result = api.getTokenForApp(AppCredentials.with(CLIENT_ID, CLIENT_SECRET))
                            .withCode(code)
                            .execute();
                    Token token = result.getToken();
                    publishToMainThread(0, token);
                }

                @Override
                public void main(int what, Object... obj) {
                    if (obj != null && obj[0] != null) {
                        UserStorage.setStravaAccessToken(obj[0].toString());
                        if (listener != null) {
                            listener.isAuthorized();
                        }
                    } else {
                        if (listener != null) {
                            listener.failed(new Throwable("token is null"));
                        }
                    }

                }

                @Override
                public void error(Throwable e) {
                    super.error(e);
                    if (listener != null) {
                        listener.failed(e);
                    }
                }


            });
        }
    }

    public interface OnStravaAuthListener {
        void isAuthorization();

        void isAuthorized();

        void isUnAuthorized();

        void failed(Throwable e);
    }
}
