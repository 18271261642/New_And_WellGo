package com.sn.utils;

import android.content.res.Resources;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;

import java.util.Locale;

/**
 * 作者:东芝(2017/8/28).
 * 描述:语言判断
 */
public class LanguageUtil {
    public static String getCurrentLanguage() {
        String language = Locale.getDefault().getLanguage().toLowerCase();
        switch (language){
            case "zh":
                String country = Locale.getDefault().getCountry().toLowerCase();
                switch (country) {
                    case "tw":
                    case "hk":
                    case "mo":
                        language = country;
                        break;
                }
                break;
        }
        //华为可能获取不到繁体中文
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N&&("zh".equalsIgnoreCase(language)||"cn".equalsIgnoreCase(language))){
            try {
                String tag = Locale.getDefault().toLanguageTag();
                if("zh-Hant-CN".equals(tag)){
                    language="tw";
                }else {
                    Locale locale = Resources.getSystem().getConfiguration().getLocales().get(0);
                    if ("中國".equalsIgnoreCase(locale.getDisplayCountry())) {
                        language = "tw";
                    }
                }
            } catch (Exception ignored) {

            }
        }
        return language;
    }

    /**
     * 是否是中文
     *
     * @return
     */
    public static boolean isZH() {
        String language = Locale.getDefault().getLanguage();
//        SNLog.i("getLanguage=%s", language);
        if (language == null) {
            return false;
        }
        return "zh".equalsIgnoreCase(language) ||
                "cn".equalsIgnoreCase(language) ||
                "hk".equalsIgnoreCase(language) ||
                "mo".equalsIgnoreCase(language) ||
                "tw".equalsIgnoreCase(language) ||
                language.toLowerCase().contains("zh")||
                language.toLowerCase().contains("cn")||
                language.toLowerCase().contains("hk")||
                language.toLowerCase().contains("mo")||
                language.toLowerCase().contains("tw");
    }

    /**
     * 是否是意大利语
     *
     * @return
     */
    public static boolean isItaly() {
        String language = Locale.getDefault().getLanguage();
        if (language == null) {
            return false;
        }
        return "it".equalsIgnoreCase(language.toLowerCase());
    }

    public static boolean isRTL() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return TextUtils.getLayoutDirectionFromLocale(Locale.getDefault()) == View.LAYOUT_DIRECTION_RTL;
        }else{
            String language = Locale.getDefault().getLanguage();
            return  language.equals("ar") || language.equals("fa") || language.equals("ur");
        }
    }
//
    /**
     * 是否是英
     *
     * @return
     */
    public static boolean isEn() {
        String language = Locale.getDefault().getLanguage();
        if (language == null) {
            return false;
        }
        return "en".equalsIgnoreCase(language.toLowerCase());
    }

    /**
     * 是否是德语
     *
     * @return
     */
    public static boolean isDeutsch() {
        String language = Locale.getDefault().getLanguage();
        if (language == null) {
            return false;
        }
        return "de".equalsIgnoreCase(language.toLowerCase());
    }

    //基站定位 暂时用不到
//    private static boolean isZH;
//    private static NetWorkReceiver myReceiver;
//
//    private static void registerReceiver(Context context) {
//        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
//        myReceiver = new NetWorkReceiver();
//        context.registerReceiver(myReceiver, filter);
//    }
//
//    private static void unregisterReceiver(Context context) {
//        context.unregisterReceiver(myReceiver);
//    }
//
//    static class NetWorkReceiver extends BroadcastReceiver {
//
//        @Override
//        public void onReceive(final Context context, Intent intent) {
//            StringBuffer sb = new StringBuffer();
//            TelephonyManager mTelephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//
//            try {
//                // 返回值MCC + MNC
//                String operator = mTelephonyManager.getNetworkOperator();
//                // 中国移动和中国联通获取LAC、CID的方式
//                sb.append(operator);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//            try {
//                GsmCellLocation location = (GsmCellLocation) mTelephonyManager.getCellLocation();
//                sb.append(location == null ? "" : location.toString());
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            try {
//                sb.append(mTelephonyManager.getCellLocation().toString());
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            try {
//                sb.append(mTelephonyManager.getSimCountryIso());
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            try {
//                sb.append(mTelephonyManager.getNetworkCountryIso());
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            String msg = sb.toString();
//            boolean isCN = msg.contains("460") && msg.contains("cn");
//            String text = isCN ? "你可能在国内" : "你可能在国外";
//        }
//    }

}
