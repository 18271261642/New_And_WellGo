package com.truescend.gofit.utils;

import com.sn.utils.IF;
import com.sn.utils.SNLog;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 类型：杂项工具类
 * Create by 泽鑫 2017/11/28 10:15
 */

public class AppUtil {
	/**
	 *  判断是否是电子邮箱
	 * @param email 邮箱
	 * @return 是或否
	 */
	public static boolean isEmail(String email){
		if (IF.isEmpty(email)) {
			return false;
		}
		Pattern p =  Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");//复杂匹配
		Matcher m = p.matcher(email);
		return m.matches();
	}

	/**
	 * 匹配用户名是否合法，只能输入下划线，数字，英文，中文
	 * @param name 帐号
	 * @return true= 违规 false= 合法
	 */
	public static boolean inputIsLegal(String name) {
		String regex = "^[\\u4E00-\\u9FA5A-Za-z0-9_]+$";
		Pattern pattern = Pattern.compile(regex);
		Matcher m = pattern.matcher(name);
		SNLog.e("inputIsLegal: %s", m);
		return m.matches();
	}

}
