package com.javaapps.legaltracker.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class LocationDataUploader extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i("legaluploader","beginning upload");
		try
		{
		LegalTrackerFile legalTrackerFile = LegalTrackerFileFactory.getLegalTrackerFile(
				context, FileType.Location.getPath());
		if (legalTrackerFile.isDataIsAvailable())
		{
		legalTrackerFile.readFromObjectFile();
		}
		Log.i("legaluploader","uploaded file");
		}catch(Exception ex)
		{
			Log.e("legaluploader","unable to retreive location file because "+ex);
		}

	}

}
