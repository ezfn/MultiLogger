package com.ezfn.MultiLogger;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.util.Printer;

public class FileSaverLooper extends Thread {

	private static final String SAVERCOMMTAG = "SAVERCOMM";

	public FileSaverLooper() {
		super();

	}
	public Handler mHandler;
	public static String TAG = "Image Saver";



	public void run() {
		Looper.prepare();

		mHandler = new Handler() {
			public void handleMessage(Message msg) {
				OnQueueFinishedListener listener = (OnQueueFinishedListener)msg.obj;
				listener.onQueueFinished();
//				if( msg.what == -1){
//					Log.d(SAVERCOMMTAG, "quiting looper...!");
//					this.getLooper().quit();
//				}
				
			}
		};

		Looper.loop();
	}

}
