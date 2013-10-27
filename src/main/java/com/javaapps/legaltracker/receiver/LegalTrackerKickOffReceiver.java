package com.javaapps.legaltracker.receiver;

import java.io.File;
import java.io.IOException;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
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

import com.javaapps.legaltracker.activity.LegalTrackerActivity;
import com.javaapps.legaltracker.listener.GForceListener;
import com.javaapps.legaltracker.listener.LegalTrackerLocationListener;
import com.javaapps.legaltracker.pojos.Config;
import com.javaapps.legaltracker.pojos.Constants;
import com.javaapps.legaltracker.pojos.Monitor;
import com.javaapps.legaltracker.service.LegalTrackerService;

public class LegalTrackerKickOffReceiver extends BroadcastReceiver {
	private Config config = Config.getInstance();
	private LegalTrackerLocationListener locationListener;

	@Override
	public synchronized void onReceive(Context context, Intent i) {
		if (!Monitor.getInstance().getServiceStarted()) {
			Log.i(Constants.LEGAL_TRACKER_TAG,
					"LegalTrackerKickOffReceiver received intent "
							+ i.getAction());
			Intent serviceIntent = new Intent(context,
					LegalTrackerService.class);
			context.startService(serviceIntent);
			Monitor.getInstance().setServiceStarted(true);
		/*	Intent legalTrackerActivityIntent = new Intent(context,
					LegalTrackerActivity.class);
			context.startActivity(legalTrackerActivityIntent);*/
		}
	}

	private boolean isServiceRunning(Context context) {
		ActivityManager manager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager
				.getRunningServices(Integer.MAX_VALUE)) {
			if (LegalTrackerService.class.getName().equals(
					service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}
}
