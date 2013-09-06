package com.iglulabs.callsdemo;

import java.io.File;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.database.Cursor;

public class MainActivity extends Activity implements OnClickListener {
	private boolean detectEnabled;
	private Button startButton;
	private final int BROWSE_AUDIO = 1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		attachListeners();
		detectEnabled = getRecordCredentials();
	}

	private boolean saveRecordCredentials(boolean state) {
		Editor editor = getSharedPreferences(
				ApplicationConstants.RECORD_FLAG_PREFERENCES,
				Context.MODE_PRIVATE).edit();
		editor.putBoolean(ApplicationConstants.RECORD_STATUS, state);
		return editor.commit();
	}

	private boolean getRecordCredentials() {
		SharedPreferences checkdisclaimer = getSharedPreferences(
				ApplicationConstants.RECORD_FLAG_PREFERENCES,
				Context.MODE_PRIVATE);
		return checkdisclaimer.getBoolean(ApplicationConstants.RECORD_STATUS,
				false);

	}

	public void registerBroadcastReceiver() {
		ComponentName receiver = new ComponentName(this, IncomingReceiver.class);
		PackageManager pm = this.getPackageManager();
		pm.setComponentEnabledSetting(receiver,
				PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
				PackageManager.DONT_KILL_APP);

	}

	public void unregisterBroadcastReceiver() {
		ComponentName receiver = new ComponentName(this, IncomingReceiver.class);
		PackageManager pm = this.getPackageManager();
		pm.setComponentEnabledSetting(receiver,
				PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
				PackageManager.DONT_KILL_APP);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if ((resultCode == RESULT_OK) && (requestCode == BROWSE_AUDIO)) {
			if (data != null) {
				Uri selectedAudio = data.getData();
				String[] filePathColumn = { MediaStore.Audio.Media.DATA };
				Cursor cursor = managedQuery(selectedAudio, filePathColumn,
						null, null, null);
				int column_index = cursor
						.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
				cursor.moveToFirst();
				String filePath = cursor.getString(column_index);
				cursor.close();
				if (filePath != null) {
					try {
						MediaPlayer mMediaPlayer = new MediaPlayer();
						mMediaPlayer.setDataSource(filePath);
						mMediaPlayer.prepare();
						mMediaPlayer.start();
						mMediaPlayer
								.setOnCompletionListener(new OnCompletionListener() {
									public void onCompletion(MediaPlayer mp) {
										mp.release();
										mp = null;
									}
								});
					} catch (Exception exception) {
						exception.printStackTrace();
					}
				}
			}
		}
	}

	private void attachListeners() {
		startButton = (Button) findViewById(R.id.startbutton);
		Button exitButton = (Button) findViewById(R.id.exitbutton);
		Button converButton = (Button) findViewById(R.id.conversationbutton);
		Button logsButton = (Button) findViewById(R.id.calllogsbutton);
		startButton.setOnClickListener(this);
		exitButton.setOnClickListener(this);
		converButton.setOnClickListener(this);
		logsButton.setOnClickListener(this);
		if (getRecordCredentials()) {
			startButton.setText("STOP RECORDING");
		} else {
			startButton.setText("START RECORDING");
		}
	}

	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.calllogsbutton:
			Intent calllogIntent = new Intent(this, LogActivity.class);
			startActivity(calllogIntent);
			break;
		case R.id.conversationbutton:
			if (Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED)) {
				Intent browserecordIntent = new Intent(
						Intent.ACTION_GET_CONTENT);
				File path = new File(Environment.getExternalStorageDirectory()
						+ "/" + ApplicationConstants.SAVED_RECORD_FOLDER);
				if (!path.exists()) {
					path.mkdir();
				}
				Uri contentUri = Uri.fromFile(path);
				browserecordIntent.setDataAndType(contentUri, "file/*");
				startActivityForResult(browserecordIntent, BROWSE_AUDIO);
			} else {
				Toast.makeText(
						this,
						"No SDCARD Mounted.\nCurrent state: "
								+ Environment.getExternalStorageState(),
						Toast.LENGTH_LONG).show();
			}
			break;
		case R.id.exitbutton:
			detectCalls(false);
			finish();
			break;
		case R.id.startbutton:
			detectCalls(!detectEnabled);
			break;

		}
	}

	private void detectCalls(boolean flag) {
		if (flag) {
			detectEnabled = true;
			registerBroadcastReceiver();
			startButton.setText("STOP RECORDING");
		} else {
			detectEnabled = false;
			unregisterBroadcastReceiver();
			startButton.setText("START RECORDING");
		}
		saveRecordCredentials(flag);
	}
}
