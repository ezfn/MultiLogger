package com.ezfn.MultiLogger;

import android.hardware.SensorEvent;

public class SensorReadingsToSave implements Runnable {
	
	SensorEvent eventToSave;
	public String currentDirName;
	public String fileName;
	public long timeCaptured;
	public float[] values;
	private Logger mLogger;
	
	public SensorReadingsToSave(SensorEvent event, String dirname, String filename, Logger logger) {
		eventToSave = event;
		currentDirName = dirname;
		mLogger = logger;
		fileName = filename;
		timeCaptured = eventToSave.timestamp / 1000000;/* have it in millis*/
		values = eventToSave.values;
	}

	@Override
	public void run() {
		mLogger.SaveSensorReading(this);
	}

}
