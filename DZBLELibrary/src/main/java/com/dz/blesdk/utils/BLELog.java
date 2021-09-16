package com.dz.blesdk.utils;

import android.util.Log;

import com.dz.blesdk.BuildConfig;

import java.lang.reflect.Method;

/**
 * 作者:东芝(2017/7/31).
 * 功能:
 */
public class BLELog {

    private static String TAG = "蓝牙框架";
    private static boolean isJavaTest;
    private static boolean isDebug = BuildConfig.DEBUG;
    public static long beginConnectTime = 0;

    private static Method method_printToDisk;

    static {
        try {
            //别说了 我也很绝望
            Class<?> clazz = Class.forName("com.truescend.gofit.utils.LogRecorder");
            method_printToDisk = clazz.getDeclaredMethod("printToDisk", String.class);
            method_printToDisk.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setIsJavaTestModel(boolean isJavaTest) {
        BLELog.isJavaTest = isJavaTest;
        isDebug = true;
    }

    private BLELog() {

    }

    private static boolean isDebuggable() {
        return isDebug;
    }

    private static String createLog(String log) {
        return log;
    }

//    private static void getTAG(StackTraceElement[] sElements) {
//        TAG = "蓝牙框架"/*sElements[1].getFileName()*/;
//    }

    public static void w(String message, Object... args) {
        String log = createLog(String.format(message, args));
        if (isDebuggable()) {

//        getTAG(new Throwable().getStackTrace());
            if (isJavaTest) {
                System.out.println(log);
            } else {
                Log.w(TAG, log);
            }
        }
        printToDisk(TAG + ":" + log);
    }

    public static void stack(String message) {
        StackTraceElement[] sElements  = new Throwable().getStackTrace();
        String methodName = "";
        try {
            if (sElements.length >= 6) {
                methodName = sElements[6].getMethodName() + ">"
                        + sElements[5].getMethodName() + ">"
                        + sElements[4].getMethodName() + ">"
                        + sElements[3].getMethodName() + ">"
                        + sElements[2].getMethodName() + ">"
                        + sElements[1].getMethodName();
            } else {
                methodName = sElements[1].getMethodName();
            }
        } catch (Exception e) {
            methodName = sElements[1].getMethodName();
        }
        Log.w(TAG, message+" "+methodName);
    }
    public static void w(String message) {
        String log = createLog(message);
        if (isDebuggable()) {
//        getTAG(new Throwable().getStackTrace());
            if (isJavaTest) {
                System.out.println(log);
            } else {
                Log.w(TAG, log);
            }
        }
        printToDisk(TAG + ":" + log);
    }



    public static void d(String message, Object... args) {
        String log = createLog(String.format(message, args));
        if (isDebuggable()) {
//        getTAG(new Throwable().getStackTrace());
            if (isJavaTest) {
                System.out.println(log);
            } else {
                Log.d(TAG, log);
            }
        }
        printToDisk(TAG + ":" + log);
    }


    public static void iDebug(String message) {
//        getTAG(new Throwable().getStackTrace());
        String log = createLog(message);
        if (isJavaTest) {
            System.out.println(log);
        } else {
            Log.d(TAG, log);
        }
    }
    private static void printToDisk(String log) {
        if (method_printToDisk != null) {
            try {
                method_printToDisk.invoke(null, log);
            } catch (Exception ignored) {
            }
        }
    }

}
