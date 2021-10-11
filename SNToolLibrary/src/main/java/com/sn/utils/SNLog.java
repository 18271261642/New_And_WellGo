package com.sn.utils;

import android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;

/**
 * 作者:东芝(2017/7/31).
 * 功能:日志
 */
public class SNLog {

    private static String className = "应用层";
    ;
    //    private static String methodName;
//    public static boolean isJavaTest;
//    private static boolean isGoneEndLog = true;
    private static boolean isDebug = BuildConfig.DEBUG;

//    public static void setIsJavaTestModel(boolean isJavaTest) {
//        SNLog.isJavaTest = isJavaTest;
//        isDebug = true;
//    }

    //private static Method method_printToDisk;

//    static {
//        try {
//            //别说了 我也很绝望,you can you up
//            Class<?> clazz = Class.forName("com.truescend.well.utils.LogRecorder");
//            method_printToDisk = clazz.getDeclaredMethod("printToDisk", String.class);
//            method_printToDisk.setAccessible(true);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    private SNLog() {

    }

    private static boolean isDebuggable() {
        return isDebug;
    }

    private static String createLog(String log) {
//        StringBuffer buffer = new StringBuffer();
//        buffer.append(log);
//        if(!isJavaTest&&!isGoneEndLog) {
//            buffer.append("   [来自:");
//            buffer.append(methodName);
//            buffer.append("]");
//        }
//        return buffer.toString();
        return log;
    }

    private static void getMethodNames(StackTraceElement[] sElements) {

//        className = sElements[1].getFileName() + " [线程:" + Thread.currentThread().getName() + "][第:" + sElements[1].getLineNumber() + "行]";

//        try {
//            if (sElements.length >= 4) {
//                methodName = sElements[4].getMethodName() + ">"
//                        + sElements[3].getMethodName() + ">"
//                        + sElements[2].getMethodName() + ">"
//                        + sElements[1].getMethodName();
//            } else {
//                methodName = sElements[1].getMethodName();
//            }
//        } catch (Exception e) {
//            methodName = sElements[1].getMethodName();
//        }
    }

    public static void e(String message, Object... args) {
        String log = createLog(String.format(message, args));
        if (isDebuggable()) {
//            getMethodNames(new Throwable().getStackTrace());
//            if (isJavaTest) {
//                System.out.println(log);
//            } else {
            Log.e(className, log);
//            }
        }
       // printToDisk(className+":"+log);
    }

    public static void e(String message) {
        String log = createLog(message);
        if (isDebuggable()) {


//        getMethodNames(new Throwable().getStackTrace());
//        if (isJavaTest) {
//            System.out.println( log);
//        } else {
            Log.e(className, log);
//        }
        }
       // printToDisk(className+":"+log);
    }


    public static void i(String message, Object... args) {
        String log = createLog(String.format(message, args));
        if (isDebuggable()) {

//            getMethodNames(new Throwable().getStackTrace());
//            if (isJavaTest) {
//                System.out.println(log);
//            } else {
            Log.i(className, log);
//            }
        }
        //printToDisk(className+":"+log);
    }

    public static void i(String message) {
        String log = createLog(message);
        if (isDebuggable()) {
//            getMethodNames(new Throwable().getStackTrace());
//            if (isJavaTest) {
//                System.out.println(log);
//            } else {
            Log.i(className, log);
//            }
        }
       // printToDisk(className+":"+log);
    }

//    private static void printToDisk(String log) {
//        if (method_printToDisk != null) {
//            try {
//                method_printToDisk.invoke(null, log);
//            } catch (Exception ignored) {
//            }
//        }
//    }

//    public static void jsonLog(String json) {
//        if (!isDebuggable())
//            return;
//
//        getMethodNames(new Throwable().getStackTrace());
//        printJson(className, json, className);
//    }
//
//    public static void dumpHex(String message, byte[] buff) {
//        if (buff == null) {
//            return;
//        }
//        StringBuffer sb = new StringBuffer();
//        for (byte b : buff) {
//            sb.append(String.format("%02X", b));
//            sb.append("-");
//        }
//        i(message + " -> " + sb.toString());
//    }
//
//    public static void iDebug(String message) {
//        getMethodNames(new Throwable().getStackTrace());
//        String log = createLog(message);
//        if (isJavaTest) {
//            System.out.println( log);
//        } else {
//            Log.i(className, log);
//        }
//    }

//    public static void dumpBundle(Bundle extras) {
//        if (extras != null) {
//
//            JSONObject object = new JSONObject();
//            for (String key : extras.keySet()) {
//                try {
//                    object.put(key, extras.get(key));
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    e("dumpBundle:" + e);
//                }
//            }
//            if (isJavaTest) {
//                System.out.println( object.toString());
//            } else {
//                Log.i(className, object.toString());
//            }
//        } else {
//            if (isJavaTest) {
//                System.out.println(className + "	dumpBundle:null");
//            } else {
//                Log.e(className, "dumpBundle:null");
//            }
//
//        }
//    }
//
//    public static void dumpIntent(Intent intent) {
//        JSONObject object = new JSONObject();
//        try {
//            object.put("action", intent.getAction());
//            object.put("component", intent.getComponent().toString());
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//            if (isJavaTest) {
//                System.out.println(className +  "	dumpIntent:" + e);
//            } else {
//                Log.i(className, "dumpIntent:" + e);
//            }
//        }
//        Bundle extras = intent.getExtras();
//        if (extras != null) {
//            for (String key : extras.keySet()) {
//                try {
//                    object.put(key, extras.get(key));
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    if (isJavaTest) {
//                        System.out.println(className + "	dumpIntent:" + e);
//                    } else {
//                        Log.i(className, "dumpIntent:" + e);
//                    }
//                }
//            }
//            if (isJavaTest) {
//                System.out.println(  object.toString());
//            } else {
//                Log.i(className, object.toString());
//            }
//        }
//    }

//    public static void printStackTrace() {
//        if (isJavaTest) {
//            System.out.println( getStackTrace(new Throwable()));
//        } else {
//            Log.e(className, getStackTrace(new Throwable()));
//        }
//    }
//
//    public static void printStackTrace(Throwable e) {
//        if (isJavaTest) {
//            System.out.println(  getStackTrace(e));
//        } else {
//            Log.e(className, getStackTrace(e));
//        }
//    }

    public static String getStackTrace(Throwable e) {
        try {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            return "\r\n" + sw.toString() + "\r\n";
        } catch (Exception e2) {
            return "无法获取堆栈信息";
        }
    }

//    public static String getStackTrace() {
//        return getStackTrace(new Throwable());
//    }


//    public static void printHexString(byte[] values) {
//        i(getHexString(values));
//    }
//
//    public static String getHexString(byte[] values) {
//        if (values == null) {
//            return "";
//        }
//        StringBuilder buffer = new StringBuilder();
//
//        for (byte b : values) {
//            buffer.append(String.format("%02X", b));
//            buffer.append(" ");
//        }
//        return buffer.toString();
//    }

//    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

//
//    public static void printJson(String tag, String msg, String headString) {
//
//        String message;
//
//        try {
//            message = jsonFormat(msg);
//        } catch (Exception e) {
//            message = msg;
//        }
//
//
//        message = headString + LINE_SEPARATOR + message;
//        if (message.length() > 4000) {
//            Log.w(tag, "║ (log太长 不格式化)" + msg);
//        } else {
//            Log.w(tag, "╔═══════════════════════════════════════════════════════════════════════════════");
//            String[] lines = message.split(LINE_SEPARATOR);
//            for (String line : lines) {
//                Log.w(tag, "║ " + line);
//            }
//            Log.w(tag, "╚═══════════════════════════════════════════════════════════════════════════════");
//        }
//
//    }

//    public static String jsonFormat(String jsonStr) {
//        StringBuffer jsonForMatStr = null;
//        try {
//            jsonForMatStr = new StringBuffer();
//            int level = 0;
//            for (int i = 0; i < jsonStr.length(); i++) {
//                char c = jsonStr.charAt(i);
//                if (level > 0 && '\n' == jsonForMatStr.charAt(jsonForMatStr.length() - 1)) {
//                    jsonForMatStr.append(getLevelStr(level));
//                }
//                switch (c) {
//                    case '{':
//                    case '[':
//                        jsonForMatStr.append(c + "\n");
//                        level++;
//                        break;
//                    case ',':
//                        jsonForMatStr.append(c + "\n");
//                        break;
//                    case '}':
//                    case ']':
//                        jsonForMatStr.append("\n");
//                        level--;
//                        jsonForMatStr.append(getLevelStr(level));
//                        jsonForMatStr.append(c);
//                        break;
//                    default:
//                        jsonForMatStr.append(c);
//                        break;
//                }
//            }
//        } catch (Exception e) {
//            jsonForMatStr = null;
//        }
//
//        return jsonForMatStr == null ? jsonStr : jsonForMatStr.toString();
//
//    }

//    private static String getLevelStr(int level) {
//        StringBuffer levelStr = new StringBuffer();
//        for (int levelI = 0; levelI < level; levelI++) {
//            levelStr.append("\t");
//        }
//        return levelStr.toString();
//    }


}
