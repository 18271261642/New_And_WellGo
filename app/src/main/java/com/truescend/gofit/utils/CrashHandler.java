package com.truescend.gofit.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Looper;
import android.widget.Toast;

import com.truescend.gofit.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * 类型：异常捕获日志
 * Author:Created by 泽鑫 on 2017/11/16 11:22.
 */

public class CrashHandler implements Thread.UncaughtExceptionHandler {
    public static final String TAG = "CrashHandler";
    //上下文参数
    private Context mContext;
    //系统默认的处理类
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    //存储设备信息和异常信息
    private Map<String, String> infos = new HashMap<>();
    //格式化日期
    private DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINESE);

    //静态内部类单例模式
    private CrashHandler() {
    }

    public static CrashHandler getInstance() {
        return CrashHandlerHolder.instance;
    }

    private static class CrashHandlerHolder {
        private static final CrashHandler instance = new CrashHandler();
    }

    /**
     * 初始化
     *
     * @param context 上下文参数
     */
    public void init(Context context) {
        mContext = context;
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread t, Throwable ex) {
        if (!handleException(ex) && mDefaultHandler != null) {
            mDefaultHandler.uncaughtException(t, ex);
        } else {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }
    }

    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return false;
        }
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(mContext, R.string.content_application_exception, Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        }.start();
        collectDeviceInfo(mContext);
        saveCrashInfoToFile(ex);
        return true;
    }

    /**
     * 收集手机信息
     *
     * @param context 上下文参数
     */
    private void collectDeviceInfo(Context context) {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(),
                    PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                String versionName = pi.versionName == null ? "null" : pi.versionName;
                String versionCode = pi.versionCode + "";
                infos.put("versionName", versionName);
                infos.put("versionCode", versionCode);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                infos.put(field.getName(), field.get("").toString());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 将异常打印保存到文件夹下
     *
     * @param ex 异常
     * @return null
     */
    private String saveCrashInfoToFile(Throwable ex) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : infos.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key);
            sb.append("=");
            sb.append(value);
            sb.append("\r\n");
        }
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String result = writer.toString();
        sb.append(result);
        long timeTamp = System.currentTimeMillis();
        String time = format.format(new Date());
        String fileName = "crash-" + time + "-" + timeTamp + ".log";
        try {
            File crashFile = new File(Constant.Path.CACHE_LOG, fileName);
            FileOutputStream fos = new FileOutputStream(crashFile);
            fos.write(sb.toString().getBytes());
            fos.close();
            return fileName;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
