package com.javaapps.legaltracker.receiver;

import com.javaapps.legaltracker.io.FileType;
import com.javaapps.legaltracker.io.LegalTrackerFile;
import com.javaapps.legaltracker.io.LegalTrackerFileFactory;
import com.javaapps.legaltracker.pojos.Config;
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
		Log.i("legaluploader", "beginning upload");
		try {
			WifiManager wifiManager = (WifiManager) context
					.getSystemService(Context.WIFI_SERVICE);
			if (!wifiManager.isWifiEnabled()) {
				Log.i("legaltrackeruploader",
						"cannot upload location data because wifi is not enabled");
				Monitor.getInstance().setWifiStatus("wifi not enabled");
				return;
			}
			Monitor.getInstance().setWifiStatus("pinging wifi connection");
			if (!wifiManager.pingSupplicant()) {
				Log.i("legaltrackeruploader",
						"cannot upload location data because could not could to wifi");
				Monitor.getInstance().setWifiStatus("could not ping backend server");
				return;

			}
			Monitor.getInstance().setWifiStatus("Wifi OK");
			LegalTrackerFile legalTrackerFile = LegalTrackerFileFactory
					.getLegalTrackerFile(
							FileType.Location.getPrefix(),
							FileType.Location.getExtension());
			if (legalTrackerFile.isEmpty()){
				return;
			}
			legalTrackerFile.closeOutObjectFile();
			LocationDataUploaderHandler locationDataUploaderHandler=new LocationDataUploaderHandler(Config.getInstance().getFilesDir(),FileType.Location.getPrefix()+"_archive_");
			locationDataUploaderHandler.uploadData();
			Log.i("legaluploader", "uploaded file");
		} catch (Exception ex) {
			Log.e("legaluploader", "unable to retreive location file because "
					+ ex);
		}

	}

}
