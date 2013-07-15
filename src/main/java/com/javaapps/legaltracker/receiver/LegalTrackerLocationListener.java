package com.javaapps.legaltracker.receiver;

import java.util.Date;

import com.javaapps.legaltracker.pojos.LegalTrackerLocation;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;

public class LegalTrackerLocationListener implements LocationListener {

	private Long lastDate;
	
	private long minimumLoggingIntervals=10000;
	
	private boolean gpsStatusOn=true;
	
	public void logLocation(Location location) {
		if ( gpsStatusOn)
		{
		LegalTrackerLocation legalTrackerLocation=new LegalTrackerLocation(location);
		if ( lastDate == null ){
			lastDate=location.getTime();
			toast(legalTrackerLocation.toString());
		}else if ((location.getTime()-lastDate)>=this.minimumLoggingIntervals){
			lastDate=location.getTime();
			toast(legalTrackerLocation.toString());
		}}else{
			toast(new Date()+" no GPS data available");
		}
		
	}	

	public void onLocationChanged(Location location) {
		logLocation(location);

	}

	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub

	}

	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub

	}

	public void onStatusChanged(String provider, int status, Bundle bundle) {
		if (status == LocationProvider.AVAILABLE) {
			System.out.println(provider + " service is now available");
			gpsStatusOn=true;
		} else if (status == LocationProvider.OUT_OF_SERVICE) {
			System.out.println(provider + " service is now available");
			gpsStatusOn=false;
		} else if (status == LocationProvider.TEMPORARILY_UNAVAILABLE) {
			System.out
					.println(provider + " service is temporarily unavailable");
			gpsStatusOn=false;
		}
	}

	private void toast(String toastStr) {
		Log.i("djs " , toastStr);

	}

}
