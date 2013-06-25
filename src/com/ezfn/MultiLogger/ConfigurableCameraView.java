package com.ezfn.MultiLogger;

import java.util.ArrayList;
import java.util.List;

import org.opencv.android.JavaCameraView;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.util.AttributeSet;

public class ConfigurableCameraView extends JavaCameraView {

	public ConfigurableCameraView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public float getExposure(){
		return mCamera.getParameters().getExposureCompensation();
	}
	public void setExposure(int exp){
		Camera.Parameters params = mCamera.getParameters();
		params.setExposureCompensation(exp);
		mCamera.setParameters(params);
	}
	public List<Integer> getExposureCompensationVals(){
		List<Integer> ret_list = new ArrayList<Integer>();
		int first_quant = mCamera.getParameters().getMinExposureCompensation();
		for (int quant = first_quant; quant <= mCamera.getParameters().getMaxExposureCompensation(); quant++){
			ret_list.add(quant);
		}
		return ret_list;	
	}

	public String getWhiteBalance(){
		return mCamera.getParameters().getWhiteBalance();
	}
	public void setWhiteBalance(String balance){
		Camera.Parameters params = mCamera.getParameters();
		params.setWhiteBalance(balance);
		mCamera.setParameters(params);
	}
	public List<String> getWhiteBalnaceVals(){
		return mCamera.getParameters().getSupportedWhiteBalance();
	}

	public void resetExposure(){
		Camera.Parameters params = mCamera.getParameters();
		params.setExposureCompensation(params.getMinExposureCompensation());
		mCamera.setParameters(params);
	}

	public void resetFocusMode(){
		Camera.Parameters params = mCamera.getParameters();
		params.setFocusMode(Camera.Parameters.FOCUS_MODE_FIXED);
		mCamera.setParameters(params);
	}
	public void resetWbalance(){
		Camera.Parameters params = mCamera.getParameters();
		params.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_FLUORESCENT);
		mCamera.setParameters(params);
	}

	public void resetColorEffect(){
		Camera.Parameters params = mCamera.getParameters();
		params.setColorEffect(Camera.Parameters.EFFECT_NONE);
		mCamera.setParameters(params);
	}

	public Parameters getParams(){
		return mCamera.getParameters();
	}



	public void resetParams(){
		resetExposure();
		resetFocusMode();
		resetWbalance();
		resetColorEffect();
	}

}
