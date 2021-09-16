package com.sn.utils;

import java.util.regex.Pattern;

/**
 * 作者:东芝(2018/1/17).
 * 功能:正则工具
 */
public class RegexUtil {

    //这个正则在超过30左右个字符的时候 判断会卡住UI
//  private static final String RG_EMAIL = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
    private static final String RG_EMAIL = "^[a-zA-Z0-9!#$%&'*+\\/=?^_`{|}~-]+(?:\\.[a-zA-Z0-9!#$%&'*+\\/=?^_`{|}~-]+)*@(?:[a-zA-Z0-9](?:[a-zA-Z0-9-]*[a-zA-Z0-9])?\\.)+[a-zA-Z0-9](?:[a-zA-Z0-9-]*[a-zA-Z0-9])?$";



    private static final String RG_CHINESE = "[\u4e00-\u9fa5]+";
    private static final String RG_PACKAGE_NAME = "[A-Za-z0-9.]+";
    private static final String RG_FILE_NAME = "[^\\s\\\\/:\\*\\?\\\"<>\\|](\\x20|[^\\s\\\\/:\\*\\?\\\"<>\\|])*[^\\s\\\\/:\\*\\?\\\"<>\\|\\.]$";

    /**
     * 是邮箱
     *
     * @param email
     * @return
     */
    public static boolean isEmail(String email) {

        return Pattern.matches(RG_EMAIL, email);
//        return email!=null&&email.contains("@")&& (!email.contains("@.")) &&email.contains(".")&& (!email.contains(".."));
    }

    /**
     * 是电话号码
     *
     * @param str
     * @return
     */
    public static boolean isPhoneNumber(String str) {
        // 因为要适配国外号码 所以无法确定电话号码长度  于是直接判断是否是数字即可
        return isNumber(str);
    }

    /**
     * 是否含有数字
     *
     * @param str
     * @return
     */
    public static boolean containNumber(String str) {
        if (Pattern.matches(".*\\d+.*", str)) {
            return true;
        }
        return false;
    }

    /**
     * 是数字
     *
     * @param str
     * @return
     */
    public static boolean isNumber(String str) {
        if (Pattern.matches("[0-9]*", str)) {
            return true;
        }
        return false;
    }

    /**
     * 是中文
     *
     * @param str
     * @return
     */
    public static boolean isChinese(String str) {
        return str.matches(RG_CHINESE);
    }

    /**
     * 是包名
     *
     * @param str
     * @return
     */
    public static boolean isPackageName(String str) {
        return str.matches(RG_PACKAGE_NAME);
    }

    /**
     * 是合法的文件名
     *
     * @param fileName
     * @return
     */
    public static boolean isFileName(String fileName) {
        if (fileName == null || fileName.length() == 0
                || fileName.length() > 255)
            return false;
        else
            return fileName
                    .matches(RG_FILE_NAME);
    }


}