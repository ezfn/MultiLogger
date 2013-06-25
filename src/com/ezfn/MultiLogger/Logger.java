package com.ezfn.MultiLogger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import android.os.Environment;
import android.util.Log;


public class Logger {
	static String APP_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Logger";
	static String SAVEIMAGETAG = "SAVEIMAGE";
	private HashMap<String, BufferedWriter> fileWriters = new HashMap<String, BufferedWriter>();


	public Logger() {
		super();
		// TODO Auto-generated constructor stub
	}

	public static void makeDir(String DIRNAME){
		File logsDir = new File( APP_PATH + "/" + DIRNAME + "/");
		logsDir.mkdirs();
		File images_dir = new File( logsDir.toString() + "/" + StaticNames.IMAGESDIRNAME);
		images_dir.mkdir();
		File sensors_dir = new File( logsDir.toString() + "/" + StaticNames.SENSORSDIRNAME);
		sensors_dir.mkdir();
	}

	public void createLog(String DIRNAME, String FILENAME, boolean do_append, int buffer_size){
		File logFile = new File(APP_PATH + "/" + DIRNAME + "/" + FILENAME);
		if (!logFile.exists())
		{
			try
			{
				logFile.createNewFile();
				BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, do_append), 1048576);
				fileWriters.put(FILENAME, writer);
			} 
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public void printToLog(String text, String FILENAME)
	{      
		try
		{
			BufferedWriter buf = fileWriters.get(FILENAME);
			buf.write(text);
			buf.newLine();

		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void closeLogs(){

		for (BufferedWriter writer : fileWriters.values()) {
			try {
				writer.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public void SaveImage (FrameBundleToSave bundle) {
		Mat mIntermediateMat = new Mat();

		Imgproc.cvtColor(bundle.frame.rgba(), mIntermediateMat, Imgproc.COLOR_RGBA2BGR, 3);
		File file = new File(APP_PATH + "/" + bundle.currentDirName + "/" + StaticNames.IMAGESDIRNAME, bundle.fileName);

		try
		{
			file.createNewFile();
		} 
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Boolean bool = null;
		String fullpath = file.toString();
		bool = Highgui.imwrite(fullpath, mIntermediateMat);		
		if (bool == true){
			Log.d(SAVEIMAGETAG, "SUCCESS writing image to external storage");
			printToLog(bundle.timeCaptured + "," + bundle.frameNumber, StaticNames.FRAMELOGFILENAM);
		}
		else
			Log.d(SAVEIMAGETAG, "Fail writing image to external storage");
	}

	public void SaveSensorReading (SensorReadingsToSave what_and_where) {
		String values_string = new String();
		for (int i=0; i < what_and_where.values.length; i++){
			values_string = values_string.concat("," + what_and_where.values[i]);
		}	
		printToLog(what_and_where.timeCaptured + values_string, what_and_where.fileName);
	}

	public void prepareLogFile (String DIRNAME, String FILENAME, List<String> columns){
		createLog(DIRNAME, FILENAME, true, 1048576);

		String col_string = new String();
		for(String col: columns){
			col_string = col_string.concat("\"" + col + "\",");
		}
		printToLog(col_string, FILENAME);
	}
}



