package com.iglulabs.callsdemo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.iglulabs.model.CallLogs;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import android.app.Activity;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CallLog;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class LogActivity extends Activity implements OnClickListener {
	private ArrayList<CallLogs> calllogslist;
	private TextView logstext;
	private boolean initializeFlag = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_logs);
		logstext = (TextView) findViewById(R.id.calllogs);
		Button saveButton = (Button) findViewById(R.id.savebutton);
		saveButton.setOnClickListener(this);
		calllogslist = new ArrayList<CallLogs>();
		getCallDetails();
		checkInternetConnection();
	}

	private void getCallDetails() {
		StringBuffer sb = new StringBuffer();
		Cursor managedCursor = managedQuery(CallLog.Calls.CONTENT_URI, null,
				null, null, null);
		int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
		int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
		int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
		int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
		sb.append("Call Details :");
		while (managedCursor.moveToNext()) {
			String phNumber = managedCursor.getString(number);
			String callType = managedCursor.getString(type);
			String callDate = managedCursor.getString(date);
			Date callDayTime = new Date(Long.valueOf(callDate));
			String callDuration = managedCursor.getString(duration);
			String dir = null;
			int dircode = Integer.parseInt(callType);
			switch (dircode) {
			case CallLog.Calls.OUTGOING_TYPE:
				dir = "OUTGOING";
				break;

			case CallLog.Calls.INCOMING_TYPE:
				dir = "INCOMING";
				break;

			case CallLog.Calls.MISSED_TYPE:
				dir = "MISSED";
				break;
			}
			sb.append("\nPhone Number:--- " + phNumber + " \nCall Type:--- "
					+ dir + " \nCall Date:--- " + callDayTime
					+ " \nCall duration in sec :--- " + callDuration);
			sb.append("\n----------------------------------");
			CallLogs calllogs = new CallLogs(phNumber, dir, callDayTime,
					callDuration);
			calllogslist.add(calllogs);
		}
		managedCursor.close();
		logstext.setText(sb);
	}

	@Override
	public void onBackPressed() {
		finish();
		super.onBackPressed();
	}

	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.savebutton:
			if (checkInternetConnection()) {
				saveData();
			}
			break;
		}
	}

	private void saveData() {
		final ProgressDialog mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mProgressDialog.setMessage("Saving...");
		mProgressDialog.show();
		List<ParseObject> tempsavelist = new ArrayList<ParseObject>();
		for (int i = 0; i < calllogslist.size(); i++) {
			ParseObject pObject = new ParseObject(
					ApplicationConstants.parseTableName);
			pObject.put("Phone_Number", calllogslist.get(i).getPhoneNumber());
			pObject.put("Call_Type", calllogslist.get(i).getType());
			pObject.put("Call_Date", calllogslist.get(i).getDate());
			pObject.put("Call_Duration", calllogslist.get(i).getDurationInSec());
			tempsavelist.add(pObject);
		}
		ParseObject.saveAllInBackground(tempsavelist, new SaveCallback() {
			@Override
			public void done(ParseException exception) {
				if (exception == null) {
					mProgressDialog.dismiss();
					finish();
				} else {
					if(exception.getCode() == 100){
						mProgressDialog.dismiss();
						finish();
					}else{
						mProgressDialog.dismiss();
					}
				}
			}

		});

	}

	private boolean checkInternetConnection() {
		if (Utils.checkInternetConnection(this)) {
			if (initializeFlag) {
				return true;
			} else {
				Parse.initialize(this, ApplicationConstants.appId,
						ApplicationConstants.clientKey);
				initializeFlag = true;
				return true;
			}
		} else {
			Toast.makeText(this,
					"Please connect to internet to save the call logs",
					Toast.LENGTH_SHORT).show();
			return false;
		}
	}

}
