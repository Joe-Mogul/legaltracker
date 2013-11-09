package com.javaapps.legaltracker.upload;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import android.util.Log;

import com.javaapps.legaltracker.io.LegalTrackerFile;
import com.javaapps.legaltracker.pojos.Config;
import com.javaapps.legaltracker.pojos.Constants;
import com.javaapps.legaltracker.pojos.LegalTrackerLocation;
import com.javaapps.legaltracker.pojos.LocationDataUpload;
import com.javaapps.legaltracker.pojos.Monitor;

public class LocationDataUploaderHandler {

	private HttpClientFactory httpClientFactory = new HttpClientFactoryImpl();

	private File filesDir;
	private String filePrefix;

	public LocationDataUploaderHandler(File filesDir, String filePrefix) {
		this.filesDir = filesDir;
		this.filePrefix = filePrefix;
	}

	public void setHttpClientFactory(HttpClientFactory httpClientFactory) {
		this.httpClientFactory = httpClientFactory;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.javaapps.legaltracker.receiver.UploadHandler#uploadData(java.util
	 * .List)
	 */
	public void uploadData() {
		for (File file : this.filesDir.listFiles()) {
			if (file.getName().startsWith(filePrefix+LegalTrackerFile.ARCHIVE_STRING)) {
				loadFile(file);
			}
		}
	}

	private FileResultMap getResultMap(File file) throws FileNotFoundException,
			IOException, ClassNotFoundException {
		String fileName = file.getAbsolutePath();
		FileResultMap fileResultMap = FileResultMapsWrapper.getInstance()
				.getFileResultMaps().get(fileName);
		if (fileResultMap == null) {
			fileResultMap = new FileResultMap(fileName);
			FileResultMapsWrapper.getInstance().getFileResultMaps()
					.put(fileName, fileResultMap);
		}
		DataInputStream inputStream = null;
		try {
			inputStream = new DataInputStream(new FileInputStream(file));
			int bufferCounter = 0;
			int objectCounter = 0;
			int batchSize = Config.getInstance().getUploadBatchSize();
			try {
				while ((inputStream.readLine()) != null) {
					objectCounter++;
					if (objectCounter >= batchSize) {
						bufferCounter++;
						objectCounter = 0;
					}
				}
			} catch (EOFException ex) {
			}
			if (objectCounter > 0) {
				bufferCounter++;
			}
			for (int ii = 0; ii < bufferCounter; ii++) {
				fileResultMap.getResultMap().put(ii, -1);
			}
			return (fileResultMap);
		} finally {
			closeInputStream(inputStream);

		}
	}

	@SuppressWarnings("deprecation")
	private void loadFile(File file) {
		DataInputStream inputStream = null;
		try {
			FileResultMap fileResultMap = getResultMap(file);
			inputStream = new DataInputStream(new FileInputStream(file));
			List<LegalTrackerLocation> batchLocationDataList = new ArrayList<LegalTrackerLocation>();
			int index = 0;
			try {
				String csvLine = null;
				while ((csvLine = inputStream.readLine()) != null) {
					LegalTrackerLocation legalTrackerLocation = new LegalTrackerLocation(csvLine);
					batchLocationDataList.add(legalTrackerLocation);
					if (batchLocationDataList.size() > Config.getInstance()
							.getUploadBatchSize()) {
						uploadBatch(fileResultMap, index, batchLocationDataList);
						index++;
						batchLocationDataList = new ArrayList<LegalTrackerLocation>();
					}
				}
			} catch (EOFException ex) {
			}
			if (batchLocationDataList.size() > 0) {
				uploadBatch(fileResultMap, index, batchLocationDataList);
			}
		} catch (Exception ex) {
			Monitor.getInstance().setLastGForceUploadStatusCode(
					Constants.SERIALIZATION_ERROR);
			Log.e(Constants.LEGAL_TRACKER_TAG,
					"unable to open location data file because "
							+ ex.getMessage());
		} finally {
			closeInputStream(inputStream);
		}

	}

	private void closeInputStream(InputStream is) {
		try {
			if (is != null) {
				is.close();
			}
		} catch (Exception ex) {
			Log.e(Constants.LEGAL_TRACKER_TAG,
					"Could not close archive input stream because "
							+ ex.getMessage());
		}
	}

	public boolean uploadBatch(FileResultMap fileResultMap, int index,
			List<LegalTrackerLocation> locationDataList) {
		if (locationDataList.size() == 0) {
			return true;
		}
		boolean retValue = true;
		Monitor.getInstance().incrementTotalPointsUploaded(
				locationDataList.size());
		// upload timestamp will be the first date in the list
		String deviceIdentifier=(Config.getInstance().getCustomIdentifier()!= null)?Config.getInstance().getCustomIdentifier():Config.getInstance().getDeviceId();
		LocationDataUpload locationDataUpload = new LocationDataUpload(deviceIdentifier,
				locationDataList.get(0).getDate(), locationDataList);
		try {
			Monitor.getInstance().setLastUploadDate(new Date());
			String jsonStr = locationDataUpload.toJsonString();
			DataUploadTask dataUploadTask = new DataUploadTask(
					locationDataList.size(), fileResultMap, index, jsonStr);
			Thread thread = new Thread(dataUploadTask);

			thread.start();
		} catch (Exception e) {
			Log.e(Constants.LEGAL_TRACKER_TAG,
					"cannot convert upload data to server because because"
							+ e.getMessage());
		}
		return retValue;
	}

	private class DataUploadTask implements Runnable {
		private int index;
		private String jsonStr;
		private FileResultMap fileResultMap;
		private int batchSize = 0;

		public DataUploadTask(int batchSize, FileResultMap fileResultMap,
				int index, String jsonStr) {
			this.index = index;
			this.batchSize = batchSize;
			this.jsonStr = jsonStr;
			this.fileResultMap = fileResultMap;
		}

		@Override
		public void run() {
			HttpClient httpClient = httpClientFactory.getHttpClient();
			if (httpClient != null) {
				try {
					HttpPost httppost = new HttpPost(Config.getInstance()

					.getLocationDataEndpoint());
					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
							2);
					nameValuePairs.add(new BasicNameValuePair("data", jsonStr));
					httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
					HttpResponse response = httpClient.execute(httppost);
					int statusCode = response.getStatusLine().getStatusCode();
					fileResultMap.getResultMap().put(index, statusCode);
					if (statusCode / 100 == 2) {
						Monitor.getInstance().incrementTotalPointsProcessed(
								batchSize);
						if (fileResultMap.allBatchesUploaded()) {
							File file = new File(fileResultMap.getFileName());
							if (!file.delete()) {
								Log.i(Constants.LEGAL_TRACKER_TAG,
										"Could not delete "
												+ file.getAbsolutePath());
							}
						}
					} else {
						Monitor.getInstance().incrementTotalPointsNotProcessed(
								batchSize);
					}
					Monitor.getInstance().setLastUploadStatusCode(statusCode);
				} catch (Exception e) {
					Monitor.getInstance().setLastUploadStatusCode(-99);
					Monitor.getInstance()
							.setLastConnectionError(e.getMessage());
					Log.e(Constants.LEGAL_TRACKER_TAG,
							"cannot  upload data to server because because"
									+ e.getMessage());
				}
			} else {
				Log.i(Constants.LEGAL_TRACKER_TAG,
						"Could not get http client, in use by other process");
			}
		}
	}

}
