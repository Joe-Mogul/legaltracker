package com.javaapps.legaltracker.receiver;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.SystemClock;
import android.util.Log;

public class LegalTrackerKickOffReceiver extends BroadcastReceiver {
	private static final int PERIOD = 20000; // 2 minutes
	private static final int INITIAL_DELAY = 2000; // 0 seconds
    private LegalTrackerLocationListener locationListener=new LegalTrackerLocationListener();
    public static boolean loggerHasStarted=false;
    
	@Override
	public synchronized void onReceive(Context ctxt, Intent i) {
		if ( !loggerHasStarted)
		{
		scheduleLocationPolling(ctxt);
		}
		loggerHasStarted=true;
	}


	
	void scheduleLocationPolling(Context context) {
		Log.i("legaltracker init " , "LegalTrackerKickOffReceiver received intent");
		LocationManager locationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
			locationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, 4000, 0,
					this.locationListener);
		}

}
