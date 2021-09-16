package com.truescend.gofit.utils;

import android.Manifest;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import androidx.annotation.NonNull;

import com.sn.app.net.AppNetReq;
import com.sn.app.net.callback.OnResponseListener;
import com.sn.app.storage.UserStorage;
import com.sn.utils.DateUtil;
import com.sn.utils.SNLog;
import com.sn.utils.SystemUtil;
import com.sn.utils.task.SNAsyncTask;
import com.sn.utils.task.SNVTaskCallBack;
import com.truescend.gofit.BuildConfig;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * 作者:东芝(2018/4/3).
 * 功能:日志记录器
 */

public class LogRecorder implements Serializable/*实现这个是为了防止混淆,因为该类被各种子依赖库逆向反射*/ {


    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static SimpleDateFormat hms = new SimpleDateFormat("MM-dd HH:mm:ss");

    private static Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    if (messageList.isEmpty()) {
                        break;
                    }
                    PrintWriter pw = null;
                    try {
                        File file = getFile(System.currentTimeMillis());
                        pw = new PrintWriter(new BufferedWriter(new FileWriter(file, true)));
                        for (String log : messageList) {
                            pw.println(log);
                        }
                        pw.flush();
                        messageList.clear();
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            if (pw != null) {
                                pw.close();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;
            }
        }
    };

    @NonNull
    private static File getFile(long time) {
        File file = new File(Constant.Path.CACHE_LOG, String.format("logcat-%s.log", dateFormat.format(time)));
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        return file;
    }

    private static List<String> messageList = new CopyOnWriteArrayList<>();

    public static void printToDisk(final String message) {
        messageList.add(hms.format(System.currentTimeMillis()) + ":" + message);
        mHandler.removeMessages(0);
        mHandler.sendEmptyMessageDelayed(0, 10 * 1000);


    }


    /**
     * 上传日志文件
     *
     * @param context   上下文
     * @param user_id   用户id
     * @param dayOffset 上传今天传0, 上传昨天传-1 上传前天传-2 以此类推
     */
    public static void uploadLog(Context context, final int user_id, final int dayOffset, final OnLogcatUploadStatusListener listener) {
        if(user_id==-1)return;

        if (!PermissionUtils.hasPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            return;
        }


        if (!PermissionUtils.hasPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            return;
        }
        final String uniqueId = SystemUtil.getUniqueId(context.getApplicationContext());
        //有数据吗? 有就上传,没有就不往下执行
        Calendar currentCalendar = DateUtil.getCurrentCalendar();
        currentCalendar.add(Calendar.DAY_OF_MONTH, dayOffset);
        final File file = getFile(currentCalendar.getTimeInMillis());
        if (!file.exists()) {
            if (listener != null) {
                listener.onFailed();
            }
            return;
        }
        final String date = dateFormat.format(currentCalendar.getTimeInMillis());

        SNAsyncTask.execute(new SNVTaskCallBack( ) {
            MultipartBody.Builder builder;


            @Override
            public void run() throws Throwable {

                int error_count = 0;
                BufferedReader reader = null;
                try {
                    reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.contains("数据异常")) {
                            error_count++;
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }


                builder = new MultipartBody.Builder();
                builder.setType(MultipartBody.FORM);

                builder.addFormDataPart("access_token", UserStorage.getAccessToken());
                builder.addFormDataPart("user_id", String.valueOf(user_id));
                builder.addFormDataPart("model", Build.MODEL);
                builder.addFormDataPart("brand", Build.MANUFACTURER);
                builder.addFormDataPart("appversion", BuildConfig.VERSION_NAME + "(" + BuildConfig.VERSION_CODE + ")");
                builder.addFormDataPart("apptype", "1");
                builder.addFormDataPart("error_times", String.valueOf(error_count));
                builder.addFormDataPart("systemversion", Build.VERSION.RELEASE + "(API " + Build.VERSION.SDK_INT + ")");
                builder.addFormDataPart("device_uuid", uniqueId);
                builder.addFormDataPart("date", date);
                builder.addFormDataPart("logfile", file.getName(), RequestBody.create(MediaType.parse("text/plain"), file));

            }

            @Override
            public void done() {
                if(builder==null)return;
                AppNetReq.getApi().uploadLog(builder.build()).enqueue(new OnResponseListener<String>() {
                    @Override
                    public void onResponse(String body) throws Throwable {
                        SNLog.i("日志上传成功!");
                        if (dayOffset < 0) {//如果是历史数据则上传完成后删掉
                            boolean delete = file.delete();
                        }
                        if (listener != null) {
                            listener.onSuccess();
                        }
                    }

                    @Override
                    public void onFailure(int ret, String msg) {
                        SNLog.i("日志上传失败!" + msg);
                        if (listener != null) {
                            listener.onFailed();
                        }
                    }
                });

            }
        });




    }

    public interface OnLogcatUploadStatusListener {
        void onSuccess();

        void onFailed();

    }

}
