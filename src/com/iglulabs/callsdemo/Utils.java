package com.iglulabs.callsdemo;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Utils {
	
	public static boolean saveRecordCredentials(Context context, boolean state) {
		Editor editor = context.getSharedPreferences(ApplicationConstants.RECORD_FLAG_PREFERENCES,
				Context.MODE_PRIVATE).edit();
		editor.putBoolean(ApplicationConstants.RECORD_STATUS, state);
		return editor.commit();
	}
	
	public static boolean checkInternetConnection(Context context){
		ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null)
				for (int i = 0; i < info.length; i++)
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}

		}
		return false;
	}


}
