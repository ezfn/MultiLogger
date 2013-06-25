package com.ezfn.MultiLogger;

import java.util.ArrayList;
import java.util.Arrays;

import android.hardware.Sensor;


public class StaticNames {

	/** file names*/
	public static final String FRAMELOGFILENAM = "frame_log.csv";
	public static final String CAMMODEFILENAM = "Camera_modes_changes.csv";
	static String IMAGESDIRNAME = "Frames";
	static String SENSORSDIRNAME = "Sensor_Readings";

	/** column names for logs*/
	public static ArrayList<String> frameLogColumns = new ArrayList<String>(Arrays.asList("Time_captured","FrameNumber"));
	public static ArrayList<String> cameraParamsColumns = new ArrayList<String>(Arrays.asList("Time_changed","Exposure_comp","Focus Mode","WB_mode" ));
	public static ArrayList<String> getLogColumns(int sensor_type){
		ArrayList<String> ret = new ArrayList<String>();
		ret.add("Time_captured");
		switch (sensor_type){
		case Sensor.TYPE_ACCELEROMETER:
			ret.add("X_acc");
			ret.add("y_acc");
			ret.add("z_acc");
			break;
		case Sensor.TYPE_MAGNETIC_FIELD:
			ret.add("X_field");
			ret.add("y_field");
			ret.add("z_field");
			break;
		case Sensor.TYPE_GYROSCOPE:
			ret.add("speed_around_x");
			ret.add("speed_around_y");
			ret.add("speed_around_z");
			break;
		case Sensor.TYPE_GRAVITY:
			ret.add("X_grav");
			ret.add("y_grav");
			ret.add("z_grav");
			break;
		case Sensor.TYPE_LINEAR_ACCELERATION:
			ret.add("X_linacc");
			ret.add("y_linacc");
			ret.add("z_linacc");
			break;
		case Sensor.TYPE_ROTATION_VECTOR:
			ret.add("X*sin(theta/2)");
			ret.add("y*sin(theta/2)");
			ret.add("z*sin(theta/2)");
			ret.add("cos(theta/2)");
			break;
		default:
			ret = null;
		}
		return ret;
	}

	/** generate file names */
	public static String getSensorFileName(Sensor s){
		return (s.getName().replaceAll(" ", "_") + ".csv");
	}


}
