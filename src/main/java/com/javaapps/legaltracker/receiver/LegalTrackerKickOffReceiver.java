package com.javaapps.legaltracker.receiver;


import java.io.File;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Environment;
import android.provider.Settings.Secure;
import android.util.Log;

import com.javaapps.legaltracker.listener.LegalTrackerLocationListener;
import com.javaapps.legaltracker.pojos.Config;
import com.javaapps.legaltracker.pojos.Monitor;

public class LegalTrackerKickOffReceiver extends BroadcastReceiver {
	private Config config = Config.getInstance();
    private LegalTrackerLocationListener locationListener;
    
     
	@Override
	public synchronized void onReceive(Context context, Intent i) {
		Log.i("legaltracker init " , "LegalTrackerKickOffReceiver received intent "+i.getAction());
		String filesSubDirPath="legaltracker";
		File filesSubDir=new File(Environment.getExternalStorageDirectory(),filesSubDirPath);
		if ( ! filesSubDir.exists()){
			if ( !filesSubDir.mkdirs())
			{
				Monitor.getInstance().setStatus("could not create external directory "+filesSubDirPath);
			}
			
		}
		Config config=Config.getInstance();
		String deviceId=Secure.getString(context.getContentResolver(),Secure.ANDROID_ID);
		config.setFilesDir( filesSubDir);
		config.setDeviceId(deviceId);
		locationListener=new LegalTrackerLocationListener(context);
		scheduleLocationPolling(context);
		scheduleFileUploads(context);
	}


	
	private void scheduleFileUploads(Context context) {
		AlarmManager mgr=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		Intent i=new Intent(context, LocationDataUploaderReceiver.class);
		PendingIntent pi=PendingIntent.getBroadcast(context, 0, i, 0);
		mgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, config.getLocationUploadDelay(), config.getLocationUploadPeriod(), pi);
	}




	private void scheduleLocationPolling(Context context) {
		LocationManager locationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
			locationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, 15000, 5,
					this.locationListener);
		}

}
