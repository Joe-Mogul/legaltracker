package com.javaapps.legaltracker.receiver;

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

import com.javaapps.legaltracker.io.FileType;
import com.javaapps.legaltracker.io.LegalTrackerFile;
import com.javaapps.legaltracker.pojos.Config;
import com.javaapps.legaltracker.pojos.Constants;
import com.javaapps.legaltracker.pojos.LegalTrackerLocation;
import com.javaapps.legaltracker.pojos.Monitor;

public class LocationBuffer {

	private static LocationBuffer locationBuffer;

	private List<LegalTrackerLocation> locationBufferList = new ArrayList<LegalTrackerLocation>();

	private LegalTrackerFile legalTrackerFile;

	private boolean gpsStatusOn = true;

	private Long lastGoodUpdate = System.currentTimeMillis();

	public static LocationBuffer getInstance() {
		if (locationBuffer == null) {
			locationBuffer = new LocationBuffer();
		}
		return locationBuffer;
	}

	private LocationBuffer() {
		try {
			Log.i(Constants.LEGAL_TRACKER_TAG, "opening internal file");
			legalTrackerFile=new LegalTrackerFile(FileType.Location.getPrefix(),FileType.Location.getExtension());
			Log.i(Constants.LEGAL_TRACKER_TAG, "opened internal file");
		} catch (Exception ex) {
			Log.e(Constants.LEGAL_TRACKER_TAG,
					"unable to get location file because " + ex.getMessage());
		}
	}

	
	public void logLocation(Location location) {
		if (gpsStatusOn) {
			LegalTrackerLocation legalTrackerLocation = new LegalTrackerLocation(
					location);
			legalTrackerLocation
					.setLastGoodUpdate(new Date(location.getTime()));
			lastGoodUpdate = (new Date()).getTime();
			locationBufferList.add(legalTrackerLocation);
			Monitor monitor = Monitor.getInstance();
			monitor.setLastLocation(legalTrackerLocation.getDisplayString());
			monitor.incrementTotalPointsLogged(1);
			monitor.setPointsInBuffer(locationBufferList.size());
			if (locationBufferList.size() > Config.getInstance()
					.getLocationListenerBufferSize()) {
				try {
					Log.i(Constants.LEGAL_TRACKER_TAG, "starting to save save to file");
					locationBufferList = legalTrackerFile
							.writeToObjectFile(locationBufferList);
					Log.i(Constants.LEGAL_TRACKER_TAG, legalTrackerLocation.getLatitude()
							+ " " + legalTrackerLocation.getLongitude()
							+ " saved to file");
				} catch (IOException e) {
					Log.e(Constants.LEGAL_TRACKER_TAG,
							"Unable to save location buffer because "
									+ e.getMessage());
				}
			}
		} else {
			Monitor.getInstance().setLastLocation(
					"No locations logged because GPS not enabled");
			Log.i(Constants.LEGAL_TRACKER_TAG, "gps not enabled");
		}

	}

	public void statusChanged(String provider, int status, Bundle bundle) {
		System.out.println("status changed");
		if (status == LocationProvider.AVAILABLE) {
			Log.i(Constants.LEGAL_TRACKER_TAG, provider
					+ " service is now available");
			Monitor.getInstance().setGpsStatus("GPS available");
			gpsStatusOn = true;
		} else if (status == LocationProvider.OUT_OF_SERVICE) {
			Log.i(Constants.LEGAL_TRACKER_TAG, provider
					+ " service is now available");
			gpsStatusOn = false;
			Monitor.getInstance().setGpsStatus("GPS out of service");
		} else if (status == LocationProvider.TEMPORARILY_UNAVAILABLE) {
			Log.i(Constants.LEGAL_TRACKER_TAG, provider
					+ " service is temporarily unavailable");
			gpsStatusOn = false;
			Monitor.getInstance().setGpsStatus("GPS temporarily unavailable");
		}
	}

}
