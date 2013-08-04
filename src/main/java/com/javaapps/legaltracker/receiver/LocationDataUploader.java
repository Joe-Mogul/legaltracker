package com.javaapps.legaltracker.receiver;

import com.javaapps.legaltracker.Monitor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.util.Log;

public class LocationDataUploader extends BroadcastReceiver {


	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i("legaluploader", "beginning upload");
		Monitor.getInstance().setStatus("checking for wifi connection");
		try {
			WifiManager wifiManager = (WifiManager) context
					.getSystemService(Context.WIFI_SERVICE);
			if (!wifiManager.isWifiEnabled()) {
				Log.i("legaltrackeruploader",
						"cannot upload location data because wifi is not enabled");
				Monitor.getInstance().setStatus("wifi not enabled");
				return;
			}
			Monitor.getInstance().setStatus("pinging wifi connection");
			if (!wifiManager.pingSupplicant()) {
				Log.i("legaltrackeruploader",
						"cannot upload location data because could not could to wifi");
				Monitor.getInstance().setStatus("could not ping backend server");

				return;

			}
			LegalTrackerFile legalTrackerFile = LegalTrackerFileFactory
					.getLegalTrackerFile(
							FileType.Location.getPrefix(),
							FileType.Location.getExtension());
			if (legalTrackerFile.isEmpty()){
				Monitor.getInstance().setStatus("got wifi connection but trackerfile is empty");
				return;
			}
			legalTrackerFile.closeOutObjectFile();
			Monitor.getInstance().setStatus("beginning upload of file");
			LocationDataUploaderHandler locationDataUploaderHandler=new LocationDataUploaderHandler(context.getFilesDir(),FileType.Location.getPrefix()+"_archive_");
			locationDataUploaderHandler.uploadData();
			Log.i("legaluploader", "uploaded file");
		} catch (Exception ex) {
			Log.e("legaluploader", "unable to retreive location file because "
					+ ex);
		}

	}

}
