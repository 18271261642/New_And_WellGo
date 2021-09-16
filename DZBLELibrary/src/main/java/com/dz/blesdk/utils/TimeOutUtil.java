package com.dz.blesdk.utils;

import android.os.Handler;

/**
 * 超时工具
 * @author XianDongZhi[T-EXCEPTION]
 *
 */
public class TimeOutUtil {

	public interface OnTimeOutListener {
		void onTimeOut();
	}

	public static void removeTimeOut(Handler handler) {
		if (r != null) {
			handler.removeCallbacks(r);
			r = null;
		}
	}

	public static boolean hasTask() {
		return r != null;
	}

	private static Runnable r;

	public static void setTimeOut(final Handler handler, long delayMillis,
                                  final OnTimeOutListener tm) {
		removeTimeOut(handler);
		// 开始计时
		r = new Runnable() {
			@Override
			public void run() {
				if (tm != null)
					tm.onTimeOut();
				removeTimeOut(handler);
			}
		};
		handler.postDelayed(r, delayMillis);

	}

	
}