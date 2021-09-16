package generalplus.com.GPCamLib;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class IncomingCall extends BroadcastReceiver {
	
	private Context m_Context;
	private String	TAG = "IncomingCall";

	public void onReceive(Context context, Intent intent) {
		
		m_Context = context;

		try {
			// TELEPHONY MANAGER class object to register one listner
			TelephonyManager tmgr = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);

			// Create Listner
			MyPhoneStateListener PhoneListener = new MyPhoneStateListener();

			// Register listener for LISTEN_CALL_STATE
			tmgr.listen(PhoneListener, PhoneStateListener.LISTEN_CALL_STATE);

		} catch (Exception e) {
			Log.e(TAG, " " + e);
		}

	}

	private class MyPhoneStateListener extends PhoneStateListener {

		public void onCallStateChanged(int state, String incomingNumber) {

			Log.d(TAG, state + " incoming no:" + incomingNumber);
		}
	}
}