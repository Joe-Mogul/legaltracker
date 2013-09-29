package com.javaapps.legaltracker.service;

import java.io.File;
import java.io.IOException;

import com.javaapps.legaltracker.listener.GForceListener;
import com.javaapps.legaltracker.listener.LegalTrackerLocationListener;
import com.javaapps.legaltracker.pojos.Config;
import com.javaapps.legaltracker.pojos.Constants;
import com.javaapps.legaltracker.pojos.Monitor;
import com.javaapps.legaltracker.receiver.GForceDataUploaderReceiver;
import com.javaapps.legaltracker.receiver.LocationDataUploaderReceiver;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.os.Environment;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings.Secure;
import android.util.Log;

public class LegalTrackerService extends Service implements Handler.Callback{
	
	private static String LEGAL_TRACKER_INTENT="legalTrackerDataIntent";

	
	private Config config = Config.getInstance();
	private LegalTrackerLocationListener locationListener;

		
	@Override
    public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i(Constants.LEGAL_TRACKER_TAG,
				"LegalTrackerService thread started " );
		try {
			Process p = Runtime.getRuntime().exec("su");
		} catch (IOException e) {
			Log.e(Constants.LEGAL_TRACKER_TAG, "Could not gain superuse access");
		}
		String filesSubDirPath = "legaltracker";
		File filesSubDir = new File(Environment.getExternalStorageDirectory(),
				filesSubDirPath);
		if (!filesSubDir.exists()) {
			if (!filesSubDir.mkdirs()) {
				Monitor.getInstance().setStatus(
						"could not create external directory "
								+ filesSubDirPath);
			}
		}
		Config config = Config.getInstance();
		String deviceId = Secure.getString(getContentResolver(),
				Secure.ANDROID_ID);
		config.setFilesDir(filesSubDir);
		config.setDeviceId(deviceId);
		locationListener = new LegalTrackerLocationListener();
		scheduleLocationPolling();
		scheduleFileUploads();
		registerSensorListener();
		return Service.START_STICKY;
	}

	private void registerSensorListener() {
		HandlerThread handlerThread = new HandlerThread("Accelerator Listener");
		handlerThread.start();
		Looper looper = handlerThread.getLooper();
		Handler handler = new Handler(looper,this);
		SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		if (sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER).size() != 0) {
			Sensor sensor = sensorManager.getSensorList(
					Sensor.TYPE_ACCELEROMETER).get(0);
			sensorManager.registerListener(new GForceListener(), sensor, 10000,handler);
		} else {
			Log.e(Constants.LEGAL_TRACKER_TAG,
					"Accelerometer sensor not found!!!");
		}
	}

	private void scheduleFileUploads() {
		HandlerThread handlerThread = new HandlerThread("LegalTrackerUploaders");
		handlerThread.start();
		Looper looper = handlerThread.getLooper();
		Handler handler = new Handler(looper,this);
		IntentFilter intentFilter=new IntentFilter();
		intentFilter.addAction(LEGAL_TRACKER_INTENT);
		GForceDataUploaderReceiver gforceDataUploaderReceiver=new GForceDataUploaderReceiver();
		LocationDataUploaderReceiver locationDataUploaderReceiver=new LocationDataUploaderReceiver();
		registerReceiver (gforceDataUploaderReceiver, intentFilter, null, handler);
		registerReceiver (locationDataUploaderReceiver, intentFilter, null, handler);
		AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		Intent i = new Intent(LEGAL_TRACKER_INTENT);
		PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
		mgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
				config.getLocationUploadDelay(),
				config.getLocationUploadPeriod(), pi);
		Intent gForceIntent = new Intent(LEGAL_TRACKER_INTENT);
		PendingIntent pendingGForceIntent = PendingIntent.getBroadcast(this, 0,
				gForceIntent, 0);
		mgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
				config.getGforceUploadDelay(), config.getGforceUploadPeriod(),
				pendingGForceIntent);
	}

	private void scheduleLocationPolling() {
		HandlerThread handlerThread = new HandlerThread("LocationUpdater");
		handlerThread.start();
		Looper looper = handlerThread.getLooper();
		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				1000, 10, this.locationListener,looper);
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean handleMessage(Message msg) {
		Log.i(Constants.LEGAL_TRACKER_TAG,"received callback message "+msg.toString());
		return false;
	}

}
