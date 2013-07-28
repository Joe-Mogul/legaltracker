package com.javaapps.legaltracker.receiver;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.javaapps.legaltracker.LegalTrackerActivity;
import com.javaapps.legaltracker.LegalTrackerActivity.LegalActivityUpdater;
import com.javaapps.legaltracker.Monitor;
import com.javaapps.legaltracker.R;
import com.javaapps.legaltracker.pojos.Config;
import com.javaapps.legaltracker.pojos.LegalTrackerLocation;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class LegalTrackerLocationListener implements LocationListener {

	private Long lastDate;
	

	private Config config = Config.getConfig();

	private LegalTrackerFile legalTrackerFile;

	private Context context;
	
	private boolean gpsStatusOn = true;

	private Long lastGoodUpdate = System.currentTimeMillis();


	private List<LegalTrackerLocation> locationBuffer = new ArrayList<LegalTrackerLocation>();

	public LegalTrackerLocationListener(Context context) {
		try {
			Log.i("legaltracker","opening internal file");
			legalTrackerFile = LegalTrackerFileFactory.getLegalTrackerFile(
					context, FileType.Location.getPrefix(),FileType.Location.getExtension());
			this.context=context;
			Log.i("legaltracker","opened internal file");
		} catch (Exception ex) {
			Log.e("legaltracker ",
					"unable to get location file because " + ex.getMessage());
		}
	}

	private void logLocation(Location location) {
		updateLegalActivity();
		if (gpsStatusOn) {
			LegalTrackerLocation legalTrackerLocation = new LegalTrackerLocation(
					location);
			if (lastDate == null) {
				lastDate = location.getTime();
			} else if ((location.getTime() - lastDate) >= config.getMinimumLoggingIntervals()) {
				lastDate = location.getTime();
				legalTrackerLocation.setLastGoodUpdate(new Date(location
						.getTime()));
				Monitor.getInstance().setLastLocation(legalTrackerLocation.getDisplayString());
				lastGoodUpdate = lastDate;
				locationBuffer.add(legalTrackerLocation);
				if (locationBuffer.size() > config
						.getLocationListenerBufferSize()) {
					try {
							Log.i("legaltracker", "starting to save save to file");
						locationBuffer = legalTrackerFile
								.writeToObjectFile(locationBuffer);
						Log.i("legaltracker", legalTrackerLocation.getLatitude()+" "+legalTrackerLocation.getLongitude()
								+ " saved to file");
					} catch (IOException e) {
						Log.e("LegalTrackerLocationListener",
								"Unable to save location buffer because "
										+ e.getMessage());
					}
				}
			}
		} else {
			Monitor.getInstance().setLastLocation("No locations logged because GPS not enabled");
			Log.i("legaltracker", "gps not enabled");
		}

	}

	private void updateLegalActivity() {
		Intent intent=new Intent();
		intent.setAction("com.javaapps.legaltracker.LegalActivityUpdater ");
		this.context.sendBroadcast(intent);
		
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
		System.out.println("status changed");
		if (status == LocationProvider.AVAILABLE) {
			Log.i("LegalTrackerLocationListener", provider
					+ " service is now available");
			Monitor.getInstance().setStatus("GPS available");
			gpsStatusOn = true;
		} else if (status == LocationProvider.OUT_OF_SERVICE) {
			Log.i("LegalTrackerLocationListener", provider
					+ " service is now available");
			gpsStatusOn = false;
			Monitor.getInstance().setStatus("GPS out of service");
		} else if (status == LocationProvider.TEMPORARILY_UNAVAILABLE) {
			Log.i("LegalTrackerLocationListener", provider
					+ " service is temporarily unavailable");
			gpsStatusOn = false;
			Monitor.getInstance().setStatus("GPS temporarily unavailable");
		}
	}

}
