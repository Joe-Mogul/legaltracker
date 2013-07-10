package com.javaapps.legaltracker.receiver;

import java.util.Date;

import com.javaapps.legaltracker.LegalTrackerLocation;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;

public class LegalTrackerLocationListener implements LocationListener {

	public void logLocation(Location location) {
		LegalTrackerLocation legalTrackerLocation=new LegalTrackerLocation(location);
		toast(legalTrackerLocation.toString());
		//
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
		} else if (status == LocationProvider.OUT_OF_SERVICE) {
			System.out.println(provider + " service is now available");
		} else if (status == LocationProvider.TEMPORARILY_UNAVAILABLE) {
			System.out
					.println(provider + " service is temporarily unavailable");
		}
	}

	private void toast(String toastStr) {
		Log.i("djs " , toastStr);

	}

}
