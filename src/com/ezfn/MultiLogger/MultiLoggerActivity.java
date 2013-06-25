package com.ezfn.MultiLogger;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.Toast;

public class MultiLoggerActivity extends Activity implements CvCameraViewListener2 {
	private static final String TAG = "OCVSample::Activity";

	private List<Sensor> sensors;
	private SensorManager mSensorManager;
	private ConfigurableCameraView mConfCameraView;
	private Boolean              isRecording = false;
	private MenuItem             mItemToggleRecord = null;
	private MenuItem[]             exposureMenu = null;
	private MenuItem[]             wBalanceMenu = null;
	private MenuItem[]             focusModeMenu = null;
	SubMenu exposureOptions;
	SubMenu wBalanceOptions;
	SubMenu focusModeOptions;
	List<String> wBalanceVals;
	List<String> focusModeVals;
	List<Integer> exposureVals;

	private String currentDir = new String();
	private static String dirPrefix = "Record started at ";
	private FileSaverLooper fileSaver = new FileSaverLooper();
	private int frame_counter = 0;
	List<Integer> sensors_idxs_to_listen = new ArrayList<Integer>();
	Logger mLogger = new Logger();
	private static int EXPOSURE_GROUP = 1;
	private static int WHITE_BALANCE_GROUP = 2;
	private static int FOCUS_MODE_GROUP = 3;
	private HashMap<Sensor, String> sensor_file_names = new HashMap<Sensor, String>();

	/**TODO: 1. make sure all frames are saved- even if app is being closed
	 * */



	private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
		@Override
		public void onManagerConnected(int status) {
			switch (status) {
			case LoaderCallbackInterface.SUCCESS:
			{
				Log.i(TAG, "OpenCV loaded successfully");
				mConfCameraView.enableView();
			} break;
			default:
			{
				super.onManagerConnected(status);
			} break;
			}
		}
	};

	public MultiLoggerActivity() {
		Log.i(TAG, "Instantiated new " + this.getClass());
	}

	public void init(){
		frame_counter = 0;
		fileSaver.setPriority(Thread.MIN_PRIORITY);
	}

	public void suspend(){
		isRecording = false;
		stopSensorListening();
		mLogger.closeLogs();
		fileSaver.setPriority(Thread.MAX_PRIORITY);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "called onCreate");
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		setContentView(R.layout.multilogger_surface_view);
		mConfCameraView = (ConfigurableCameraView) findViewById(R.id.multilogger_activity_configurable_surface_view);

		mConfCameraView.setVisibility(SurfaceView.VISIBLE);

		mConfCameraView.setCvCameraViewListener(this);

		//fileSaver.setDaemon(true);//TODO: think about the handler being inside a daemon thread
		fileSaver.setPriority(Thread.MIN_PRIORITY);
		fileSaver.start();
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
	}

	@Override
	public void onPause()
	{
		super.onPause();
		if (mConfCameraView != null)
			mConfCameraView.disableView();
		//suspend();

	}

	@Override
	public void onResume()
	{
		super.onResume();
		OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this, mLoaderCallback);
		init();
	}

	public void onDestroy() {
		super.onDestroy();
		if (mConfCameraView != null)
			mConfCameraView.disableView();
		suspend();
	}
	public void onStop() {
		super.onStop();
		if (mConfCameraView != null)
			mConfCameraView.disableView();
		suspend();
	}
	public void onRestart() {
		super.onStop();
		OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this, mLoaderCallback);
		init();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.i(TAG, "called onCreateOptionsMenu");
		mItemToggleRecord = menu.add("Toggle Record/Stop");

		/** create exposure item */
		exposureVals = mConfCameraView.getExposureCompensationVals();
		if (exposureVals == null) {
			Log.e(TAG, "compensation vals are not supported by device!");
			return true;
		}
		exposureOptions = menu.addSubMenu("Exposure Level");
		exposureMenu = new MenuItem[exposureVals.size()];
		int idx = 0;
		for (Integer value : exposureVals){
			exposureMenu[idx] = exposureOptions.add(EXPOSURE_GROUP, idx, Menu.NONE, value.toString());
			idx++;
		}

		/** create WB item */
		wBalanceVals = mConfCameraView.getWhiteBalnaceVals();
		if (wBalanceVals == null) {
			Log.e(TAG, "white balance not supported by device!");
			return true;
		}
		wBalanceOptions = menu.addSubMenu("White balance type");
		wBalanceMenu = new MenuItem[wBalanceVals.size()];
		idx = 0;
		for (String value : wBalanceVals){
			wBalanceMenu[idx] = wBalanceOptions.add(WHITE_BALANCE_GROUP, idx, Menu.NONE, value.toString());
			idx++;
		}

		/** create Focus mode item */
		focusModeVals = mConfCameraView.getFocusModes();
		if (focusModeVals == null) {
			Log.e(TAG, "focus modes not supported by device!");
			return true;
		}
		focusModeOptions = menu.addSubMenu("Focus mode");
		focusModeMenu = new MenuItem[focusModeVals.size()];
		idx = 0;
		for (String value : focusModeVals){
			focusModeMenu[idx] = focusModeOptions.add(FOCUS_MODE_GROUP, idx, Menu.NONE, value.toString());
			idx++;
		}

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		String toastMesage = new String();
		Log.i(TAG, "called onOptionsItemSelected; selected item: " + item);

		if (item.getGroupId() == EXPOSURE_GROUP || item.getGroupId() == WHITE_BALANCE_GROUP || item.getGroupId() == FOCUS_MODE_GROUP){

			if (item.getGroupId() == EXPOSURE_GROUP)
			{

				mConfCameraView.setExposure(Integer.parseInt((String)item.getTitle()));
				Toast.makeText(this, String.valueOf(mConfCameraView.getExposure()), Toast.LENGTH_SHORT).show();
			}
			else if (item.getGroupId() == WHITE_BALANCE_GROUP) {
				mConfCameraView.setWhiteBalance((String)item.getTitle());
				Toast.makeText(this, mConfCameraView.getWhiteBalance(), Toast.LENGTH_SHORT).show();
			}

			else if (item.getGroupId() == FOCUS_MODE_GROUP) {
				mConfCameraView.setFocusMode((String)item.getTitle());
				Toast.makeText(this, mConfCameraView.getFocusMode(), Toast.LENGTH_SHORT).show();
			}
			/*update the log*/
			if(isRecording){
				updateCameraModeChange();
			}
		}
		
		else if (item == mItemToggleRecord) {
			isRecording = !isRecording;
			if (isRecording) {
				Date date= new Date() ;
				currentDir = dirPrefix + date.getTime(); 
				Logger.makeDir(currentDir);/** TODO: there is a race here*/
				mLogger.prepareLogFile (currentDir + "/" + StaticNames.IMAGESDIRNAME, StaticNames.FRAMELOGFILENAM, StaticNames.frameLogColumns, 1048576);/** TODO: there is a race here*/
				mLogger.prepareLogFile (currentDir + "/" + StaticNames.IMAGESDIRNAME, StaticNames.CAMMODEFILENAM, StaticNames.cameraParamsColumns, 1024);/** TODO: there is a race here*/
				updateCameraModeChange();
				toastMesage = "Record started...!";  
				setupSensors();
				startSensorListening();
				fileSaver.setPriority(Thread.MIN_PRIORITY);
			} else {
				stopSensorListening();
				mLogger.closeLogs();
				toastMesage = "Record stopped...!";
				frame_counter = 0;
				fileSaver.setPriority(Thread.MAX_PRIORITY);
			}
			Toast toast = Toast.makeText(this, toastMesage, Toast.LENGTH_LONG);
			toast.show();
		} 

		return true;
	}

	public void onCameraViewStarted(int width, int height) {
		mConfCameraView.resetParams();
	}

	public void onCameraViewStopped() {
	}

	public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
		Date now = new Date();
		final long millis = now.getTime();
		if(isRecording){
			frame_counter++;
			String filename = "image" + frame_counter + ".jpg";
			FrameBundleToSave bundle = new FrameBundleToSave(inputFrame, currentDir, filename, millis, frame_counter, mLogger);
			fileSaver.mHandler.post(bundle);
		}
		return inputFrame.rgba();
	}

	private SensorEventListener mSensorEventListener = new SensorEventListener() {

		@Override
		public void onSensorChanged(SensorEvent event) {
			SensorReadingsToSave reading = new SensorReadingsToSave(event, currentDir, sensor_file_names.get(event.sensor), mLogger);
			mLogger.SaveSensorReading(reading);/* Do it on the main thread*/
			//fileSaver.mHandler.post(reading);/* Do it on a different thread*/
		}

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}


	};

	private void setupSensors() {
		int idx = 0;
		sensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);
		for (Sensor s: sensors) {
			Log.d("sensors: ", s.getName());
			ArrayList<String> col_names = StaticNames.getLogColumns(s.getType());
			if (col_names != null){
				String sensorFilename = StaticNames.getSensorFileName(s);
				mLogger.prepareLogFile(currentDir + "/" + StaticNames.SENSORSDIRNAME, sensorFilename, col_names, 1048576);
				sensor_file_names.put(s,sensorFilename);
				sensors_idxs_to_listen.add(idx);
			}
			idx++;
		}
	}

	private void startSensorListening(){
		for (Integer idx: sensors_idxs_to_listen){
			mSensorManager.registerListener(
					mSensorEventListener, sensors.get(idx), SensorManager.SENSOR_DELAY_GAME);
		}

	}
	private void stopSensorListening(){
		mSensorManager.unregisterListener(mSensorEventListener);
	}
	private void updateCameraModeChange(){
		Date now = new Date();
		final long millis = now.getTime();
		String what_to_write = millis + "," + mConfCameraView.getExposure() + "," + mConfCameraView.getFocusMode() + "," + mConfCameraView.getWhiteBalance();
		mLogger.printToLog(what_to_write, StaticNames.CAMMODEFILENAM);
		
	}


}
