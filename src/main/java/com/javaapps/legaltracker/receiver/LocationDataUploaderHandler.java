package com.javaapps.legaltracker.receiver;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

import com.javaapps.legaltracker.pojos.Config;
import com.javaapps.legaltracker.pojos.LocationDataUpload;
import com.javaapps.legaltracker.pojos.LegalTrackerLocation;

public class LocationDataUploaderHandler implements UploadHandler {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.javaapps.legaltracker.receiver.UploadHandler#uploadData(java.util
	 * .List)
	 */
	@Override
	public boolean uploadData(List<LegalTrackerLocation> locationDataList) {
		if (locationDataList.size() == 0) {
			return true;
		}
		boolean retValue = true;
		LocationDataUpload locationDataUpload = new LocationDataUpload(
				new Date(), locationDataList);
		try {
			String jsonStr = locationDataUpload.toJsonString();
			new DataUploadTask().execute(jsonStr);
			Log.i("legaltrack", jsonStr);
		} catch (Exception e) {
			Log.e("legaltracker",
					"cannot convert upload data to server because because"
							+ e.getMessage());
		}
		return retValue;
	}

	private class DataUploadTask extends AsyncTask<String, Void, Integer> {

		@Override
		protected Integer doInBackground(String... args) {
			try {
				String jsonStr=args[0];
                System.out.println(jsonStr);
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(Config.getConfig()
						.getLocationDataEndpoint());
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
						2);
				nameValuePairs.add(new BasicNameValuePair("data", jsonStr));
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				HttpResponse response = httpclient.execute(httppost);
				return (response.getStatusLine().getStatusCode());
			} catch (Exception e) {
				Log.e("legaltracker",
						"cannot  upload data to server because because"
								+ e.getMessage());
				return -1;
			}

		}

		protected void onPostExecute(Integer statusCode) {
			Log.i("legaltracker", "upload status code is" + statusCode);
		}

	}
}
