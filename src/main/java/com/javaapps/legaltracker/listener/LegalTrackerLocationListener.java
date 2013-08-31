package com.javaapps.legaltracker.listener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;

import com.javaapps.legaltracker.pojos.Config;
import com.javaapps.legaltracker.pojos.LegalTrackerLocation;
import com.javaapps.legaltracker.pojos.Monitor;
import com.javaapps.legaltracker.receiver.LocationBuffer;

public class LegalTrackerLocationListener implements LocationListener {

	private static long lastDate=0;

	private File filesDir;
	
	private static long lastSampleDate=0;
	
	private static double lastLatitude=0;
	
	private static double lastLongitude=0;
	
	private static double GEO_VARIANCE=0.000002;
	
	private static long MILLI_VARIANCE=75;// milli seconds

	public LegalTrackerLocationListener(Context context) {
		this.filesDir = context.getFilesDir();
	}

	public void onLocationChanged(Location location) {
		long minimumLoggingIntervals=Config
				.getInstance().getMinimumLoggingIntervals();
		if (System.currentTimeMillis() - lastDate >= minimumLoggingIntervals) {
			Monitor.getInstance().setGpsStatus("GPS available");
			/*this point has been logged already , skip it */
			if ( ! sampleDateHasChanged(location)){
				return;
			}
			if ( ! locationHasChanged(location)){
				return;
			}
			lastSampleDate=location.getTime();
			lastLatitude=location.getLatitude();
			lastLongitude=location.getLongitude();
			lastDate = System.currentTimeMillis();
			LocationBuffer.getInstance(filesDir).logLocation(location);
		}
	}

	private boolean sampleDateHasChanged(Location location) {
		boolean retValue=Math.abs(location.getTime()-lastSampleDate)>MILLI_VARIANCE;
		return retValue;
	}

	private boolean locationHasChanged(Location location) {
		boolean retValue=Math.abs(location.getLatitude()-lastLatitude)>GEO_VARIANCE || 
				Math.abs(location.getLongitude()-lastLongitude)>GEO_VARIANCE;
		return retValue;
	}

	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub

	}

	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub

	}

	public void onStatusChanged(String provider, int status, Bundle bundle) {
		LocationBuffer.getInstance(filesDir).statusChanged(provider, status,
				bundle);
	}

}
