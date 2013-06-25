package com.ezfn.MultiLogger;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public class FileSaverLooper extends Thread {
	
	public Handler mHandler;
	public static String TAG = "Image Saver";

	  public void run() {
	      Looper.prepare();

	      mHandler = new Handler() {
	          public void handleMessage(Message msg) {
	        	  
	          }
	      };

	      Looper.loop();
	  }

}
