package com.javaapps.legaltracker.receiver;

import com.javaapps.legaltracker.io.FileType;
import com.javaapps.legaltracker.io.LegalTrackerFile;
import com.javaapps.legaltracker.pojos.Config;
import com.javaapps.legaltracker.pojos.Constants;
import com.javaapps.legaltracker.pojos.Monitor;
import com.javaapps.legaltracker.upload.GForceDataUploaderHandler;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.util.Log;

public class GForceDataUploaderReceiver extends BroadcastReceiver {


	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i(Constants.LEGAL_TRACKER_TAG, "beginning upload");
		try {
			WifiManager wifiManager = (WifiManager) context
					.getSystemService(Context.WIFI_SERVICE);
			if (!wifiManager.isWifiEnabled()) {
				Log.i(Constants.LEGAL_TRACKER_TAG,
						"cannot upload gforce data because wifi is not enabled");
				Monitor.getInstance().setWifiStatus("wifi not enabled");
				Monitor.getInstance().setLastGForceUploadStatusCode(Constants.WIFI_NOT_ENABLED);
				return;
			}
			Monitor.getInstance().setWifiStatus("pinging wifi connection");
			if (!wifiManager.pingSupplicant()) {
				Log.i(Constants.LEGAL_TRACKER_TAG,
						"cannot upload gforce data because could not could to wifi");
				Monitor.getInstance().setWifiStatus("could not ping backend server");
				Monitor.getInstance().setLastGForceUploadStatusCode(Constants.COULD_NOT_GET_WIFI_CONNECTION);
				return;
			}
			Monitor.getInstance().setWifiStatus("Wifi OK");
			GForceDataUploaderHandler gforceDataUploaderHandler=new GForceDataUploaderHandler(Config.getInstance().getFilesDir(),FileType.GForce.getPrefix());
			gforceDataUploaderHandler.uploadData();
			Log.i(Constants.LEGAL_TRACKER_TAG, "uploaded gforce file");
		} catch (Exception ex) {
			Log.e(Constants.LEGAL_TRACKER_TAG, "unable to retrieve GForce data file because "
					+ ex);
		}

	}

}
