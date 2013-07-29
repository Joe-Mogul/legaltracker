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
		Monitor.getInstance().setStatus("beginning upload");
		try {
			WifiManager wifiManager = (WifiManager) context
					.getSystemService(Context.WIFI_SERVICE);
			if (!wifiManager.isWifiEnabled()) {
				Log.i("legaltrackeruploader",
						"cannot upload location data because wifi is not enabled");
				Monitor.getInstance().setStatus("wifi not enabled");
				return;
			}
			if (!wifiManager.pingSupplicant()) {
				Log.i("legaltrackeruploader",
						"cannot upload location data because could not could to wifi");
				Monitor.getInstance().setStatus("could not ping backend server");

				return;

			}
			Monitor.getInstance().setStatus("beginning upload of file");
			LegalTrackerFile legalTrackerFile = LegalTrackerFileFactory
					.getLegalTrackerFile(context,
							FileType.Location.getPrefix(),
							FileType.Location.getExtension());
			legalTrackerFile.closeOutObjectFile();
			LocationDataUploaderHandler locationDataUploaderHandler=new LocationDataUploaderHandler(context.getFilesDir(),FileType.Location.getPrefix()+"_archive_");
			locationDataUploaderHandler.uploadData();
			Log.i("legaluploader", "uploaded file");
		} catch (Exception ex) {
			Log.e("legaluploader", "unable to retreive location file because "
					+ ex);
		}

	}

}
