package com.javaapps.legaltracker.receiver;

import java.io.File;
import java.io.IOException;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.os.Environment;
import android.provider.Settings.Secure;
import android.util.Log;

import com.javaapps.legaltracker.listener.GForceListener;
import com.javaapps.legaltracker.listener.LegalTrackerLocationListener;
import com.javaapps.legaltracker.pojos.Config;
import com.javaapps.legaltracker.pojos.Constants;
import com.javaapps.legaltracker.pojos.Monitor;

public class LegalTrackerKickOffReceiver extends BroadcastReceiver {
	private Config config = Config.getInstance();
	private LegalTrackerLocationListener locationListener;

	@Override
	public synchronized void onReceive(Context context, Intent i) {
		Log.i(Constants.LEGAL_TRACKER_TAG,
				"LegalTrackerKickOffReceiver received intent " + i.getAction());
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
		String deviceId = Secure.getString(context.getContentResolver(),
				Secure.ANDROID_ID);
		config.setFilesDir(filesSubDir);
		config.setDeviceId(deviceId);
		locationListener = new LegalTrackerLocationListener();
		scheduleLocationPolling(context);
		scheduleFileUploads(context);
		registerSensorListener(context);
	}

	private void registerSensorListener(Context context) {
		SensorManager sensorManager = (SensorManager) context
				.getSystemService(Context.SENSOR_SERVICE);
		if (sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER).size() != 0) {
			Sensor sensor = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER)
					.get(0);
			sensorManager.registerListener(new GForceListener(), sensor,
					10000);
		} else {
			Log.e(Constants.LEGAL_TRACKER_TAG, "Accelerometer sensor not found!!!");
		}
	}

	private void scheduleFileUploads(Context context) {
		AlarmManager mgr = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		Intent i = new Intent(context, LocationDataUploaderReceiver.class);
		PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
		mgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
				config.getLocationUploadDelay(),
				config.getLocationUploadPeriod(), pi);
		Intent gForceIntent = new Intent(context, GForceDataUploaderReceiver.class);
		PendingIntent pendingGForceIntent = PendingIntent.getBroadcast(context, 0, gForceIntent, 0);
		mgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
				config.getGforceUploadDelay(),
				config.getGforceUploadPeriod(), pendingGForceIntent);
	}

	private void scheduleLocationPolling(Context context) {
		LocationManager locationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				1000, 10, this.locationListener);
	}

}
