package com.javaapps.legaltracker.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.util.Log;

public class LocationDataUploader extends BroadcastReceiver {


	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i("legaluploader", "beginning upload");
		try {
			WifiManager wifiManager = (WifiManager) context
					.getSystemService(Context.WIFI_SERVICE);
			if (!wifiManager.isWifiEnabled()) {
				Log.i("legaltrackeruploader",
						"cannot upload location data because wifi is not enabled");
				return;
			}
			if (!wifiManager.pingSupplicant()) {
				Log.i("legaltrackeruploader",
						"cannot upload location data because could not could to wifi");
				return;

			}
			LegalTrackerFile legalTrackerFile = LegalTrackerFileFactory
					.getLegalTrackerFile(context,
							FileType.Location.getPrefix(),
							FileType.Location.getExtension());
			legalTrackerFile.readFromObjectFile();
			Log.i("legaluploader", "uploaded file");
		} catch (Exception ex) {
			Log.e("legaluploader", "unable to retreive location file because "
					+ ex);
		}

	}

}
