package com.javaapps.legaltracker.receiver;

import com.javaapps.legaltracker.io.FileType;
import com.javaapps.legaltracker.io.LegalTrackerFile;
import com.javaapps.legaltracker.pojos.Config;
import com.javaapps.legaltracker.pojos.Constants;
import com.javaapps.legaltracker.pojos.Monitor;
import com.javaapps.legaltracker.upload.LocationDataUploaderHandler;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.util.Log;

public class LocationDataUploaderReceiver extends BroadcastReceiver {


	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i(Constants.LEGAL_TRACKER_TAG, "beginning upload");
		try {
			WifiManager wifiManager = (WifiManager) context
					.getSystemService(Context.WIFI_SERVICE);
			if (!wifiManager.isWifiEnabled()) {
				Log.i(Constants.LEGAL_TRACKER_TAG,
						"cannot upload location data because wifi is not enabled");
				Monitor.getInstance().setWifiStatus("wifi not enabled");
				Monitor.getInstance().setLastUploadStatusCode(Constants.WIFI_NOT_ENABLED);
				return;
			}
			Monitor.getInstance().setWifiStatus("pinging wifi connection");
			if (!wifiManager.pingSupplicant()) {
				Log.i(Constants.LEGAL_TRACKER_TAG,
						"cannot upload location data because could not could to wifi");
				Monitor.getInstance().setWifiStatus("could not ping backend server");
				Monitor.getInstance().setLastUploadStatusCode(Constants.COULD_NOT_GET_WIFI_CONNECTION);
				return;

			}
			Monitor.getInstance().setWifiStatus("Wifi OK");
			LocationDataUploaderHandler locationDataUploaderHandler=new LocationDataUploaderHandler(Config.getInstance().getFilesDir(),FileType.Location.getPrefix()+"_archive_");
			locationDataUploaderHandler.uploadData();
			Log.i(Constants.LEGAL_TRACKER_TAG, "uploaded file");
		} catch (Exception ex) {
			Log.e(Constants.LEGAL_TRACKER_TAG, "unable to retreive location file because "
					+ ex);
		}

	}

}
