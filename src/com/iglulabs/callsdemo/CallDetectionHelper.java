package com.iglulabs.callsdemo;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.content.Context;
import android.media.MediaRecorder;
import android.os.Environment;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

public class CallDetectionHelper extends PhoneStateListener {
	private Context context;
	private MediaRecorder recorder;
	private String audiofile;

	public CallDetectionHelper(Context context) {
		this.context = context;
	}

	@Override
	public void onCallStateChanged(int state, String incomingNumber) {
		switch (state) {
		case TelephonyManager.CALL_STATE_RINGING:
			try {
				startRecord(incomingNumber);
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		case TelephonyManager.CALL_STATE_OFFHOOK:
			break;
		case TelephonyManager.CALL_STATE_IDLE:
			try {
				stopRecord();
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		}
	}

	public void startRecord(String incomingNumber)
			throws IllegalStateException, IOException {
		String out = new SimpleDateFormat("dd-MM-yyyy hh-mm-ss")
				.format(new Date());
		TelephonyManager telephony = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String userNumber = telephony.getLine1Number();
		String file_name;
		if (userNumber != null) {
			if(userNumber != ""){
				file_name = incomingNumber + "_" + userNumber + "_" + out;
			}else{
				file_name = incomingNumber + "_" + "unknown" + "_" + out;
			}
		} else {
			file_name = incomingNumber + "_" + "unknown" + "_" + out;
		}
		File sampleDir = new File(Environment.getExternalStorageDirectory(),
				"/" + ApplicationConstants.SAVED_RECORD_FOLDER);
		if (!sampleDir.exists()) {
			sampleDir.mkdirs();
		}
		audiofile = sampleDir.getAbsolutePath() + "/" + file_name + ".mp4";
		int audioSource = MediaRecorder.AudioSource.MIC;
		recorder = new MediaRecorder();
		recorder.setAudioSource(audioSource);
		recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
		recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		recorder.setOutputFile(audiofile);
		recorder.prepare();
		recorder.start();
	}

	public void stopRecord() throws IOException {
		if (recorder != null) {
			recorder.stop();
			recorder.release();
			recorder.reset();
		}
	}
}
