package com.javaapps.legaltracker.listener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.javaapps.legaltracker.pojos.Config;
import com.javaapps.legaltracker.pojos.Constants;
import com.javaapps.legaltracker.pojos.GForceData;
import com.javaapps.legaltracker.receiver.GForceBuffer;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;

public class GForceListener implements SensorEventListener{

	private File filesDir;

	private static GForceData lastGforceData;
	
	public GForceListener()
	{
		this.filesDir = Config.getInstance().getFilesDir();
	}
	
	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// Not needed yet
	}

	@Override
	public void onSensorChanged(SensorEvent sensorEvent) {
		try {
			GForceBuffer gForceBuffer=GForceBuffer.getInstance();
			if ( sensorEvent.values.length < 3)
			{
				return;
			}		
			GForceData gforceData=new GForceData(sensorEvent.values[0],sensorEvent.values[1],sensorEvent.values[2],System.currentTimeMillis());
			if ( gforceData.isEqual(lastGforceData)){
				return;
			}
			gForceBuffer.logGForce(gforceData);
			lastGforceData=gforceData;
		} catch (Exception e) {
			Log.e(Constants.LEGAL_TRACKER_TAG,"Unable to log gforce data because "+e.getMessage());
		} 
		
	}

}
