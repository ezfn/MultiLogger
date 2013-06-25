package com.ezfn.MultiLogger;

import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.core.Mat;

import android.util.Log;

public class FrameBundleToSave implements Runnable {
	CvCameraViewFrame frame;
	String currentDirName;
	String fileName;
	long timeCaptured;
	int frameNumber;
	private Logger mLogger;

	public FrameBundleToSave(CvCameraViewFrame new_frame, String dirname, String filename, long time_captured, int frame_number, Logger logger){
		frame = new_frame;
		currentDirName = dirname;
		fileName = filename;
		timeCaptured = time_captured;
		frameNumber = frame_number;
		mLogger = logger;
	}
	@Override
	public void run() {
		mLogger.SaveImage(this);
	}
	
}
