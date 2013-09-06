package com.iglulabs.callsdemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class IncomingReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			Intent callintent = new Intent(context, CallService.class);
			context.startService(callintent);
		} else {
			Toast.makeText(context,
					"SD Card NOT Mounted Recordings will not be saved",
					Toast.LENGTH_LONG).show();
		}
	}
}
