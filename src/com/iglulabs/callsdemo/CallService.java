package com.iglulabs.callsdemo;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

public class CallService extends Service {

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		TelephonyManager telephony = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);																										// object
		CallDetectionHelper customPhoneListener = new CallDetectionHelper(getApplicationContext());
		telephony.listen(customPhoneListener,
				PhoneStateListener.LISTEN_CALL_STATE);
		return START_NOT_STICKY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

}
