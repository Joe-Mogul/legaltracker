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

	private Long lastDate;

	private File filesDir;

	public LegalTrackerLocationListener(Context context) {
		this.filesDir = context.getFilesDir();
	}

	public void onLocationChanged(Location location) {
		if (lastDate == null) {
			lastDate = System.currentTimeMillis();
		} else if ((System.currentTimeMillis() - lastDate) >= Config
				.getInstance().getMinimumLoggingIntervals()) {
			Monitor.getInstance().setGpsStatus("GPS available");
			lastDate = System.currentTimeMillis();
			LocationBuffer.getInstance(filesDir).logLocation(location);
		}
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
