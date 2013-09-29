package com.javaapps.legaltracker.upload;

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
import com.javaapps.legaltracker.pojos.GForceData;
import com.javaapps.legaltracker.pojos.GForceDataUpload;
import com.javaapps.legaltracker.pojos.Monitor;

public class GForceDataUploaderHandler {

	private HttpClientFactory httpClientFactory = new HttpClientFactoryImpl();

	private File filesDir;
	private String filePrefix;

	public GForceDataUploaderHandler(File filesDir, String filePrefix) {
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
			Log.i(Constants.LEGAL_TRACKER_TAG,"reading legal tracker directory");
			if (file.getName().startsWith(filePrefix+LegalTrackerFile.ARCHIVE_STRING)) {
				Log.i(Constants.LEGAL_TRACKER_TAG,"uploading file "+file.getName());
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
		ObjectInputStream objectInputStream = null;
		try {
			objectInputStream = new ObjectInputStream(new FileInputStream(file));
			int bufferCounter = 0;
			int objectCounter = 0;
			int batchSize = Config.getInstance().getUploadBatchSize();
			try {
				while ((objectInputStream.readObject()) != null) {
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
			closeInputStream(objectInputStream);

		}
	}

	private void loadFile(File file) {
		ObjectInputStream objectInputStream = null;
		try {
			FileResultMap fileResultMap = getResultMap(file);
			objectInputStream = new ObjectInputStream(new FileInputStream(file));
			List<GForceData> gforceDataList = new ArrayList<GForceData>();
			int index = 0;
			try {
				Object object = null;
				while ((object = objectInputStream.readObject()) != null) {
					GForceData gforceData = (GForceData) object;
					gforceDataList.add(gforceData);
					if (gforceDataList.size() > Config.getInstance()
							.getUploadBatchSize()) {
						uploadBatch(fileResultMap, index, gforceDataList);
						index++;
						gforceDataList = new ArrayList<GForceData>();
					}
				}
			} catch (EOFException ex) {
			}
			if (gforceDataList.size() > 0) {
				uploadBatch(fileResultMap, index, gforceDataList);
			}
		} catch (Exception ex) {
			Log.e(Constants.LEGAL_TRACKER_TAG,
					"unable to open gforce data file because "
							+ ex.getMessage());
		} finally {
			closeInputStream(objectInputStream);
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
			List<GForceData> gforceDataList) {
		if (gforceDataList.size() == 0) {
			return true;
		}
		boolean retValue = true;
		Monitor.getInstance().incrementTotalGForcePointsUploaded(
				gforceDataList.size());
		//upload timestamp will be the first date in the list
		GForceDataUpload gforceDataUpload = new GForceDataUpload(Config.getInstance().getDeviceId(),
				new Date(gforceDataList.get(0).getSampleDateInMillis()), gforceDataList);
		try {
			Monitor.getInstance().setLastGForceUploadDate(new Date());
			String jsonStr = gforceDataUpload.toJsonString();
			DataUploadTask dataUploadTask = new DataUploadTask(
					gforceDataList.size(), fileResultMap, index, jsonStr);
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

		public void run() {
			HttpClient httpClient = httpClientFactory.getHttpClient();
			if (httpClient != null) {
				try {
					HttpPost httppost = new HttpPost(Config.getInstance()

					.getGforceDataEndpoint());
					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
							2);
					nameValuePairs.add(new BasicNameValuePair("data", jsonStr));
					httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
					HttpResponse response = httpClient.execute(httppost);
					int statusCode = response.getStatusLine().getStatusCode();
					fileResultMap.getResultMap().put(index, statusCode);
					if (statusCode / 100 == 2) {
						Monitor.getInstance().incrementTotalGForcePointsProcessed(
								batchSize);
						if (fileResultMap.allBatchesUploaded()){
							File file=new File(fileResultMap.getFileName());
							if ( ! file.delete()){
								Log.i(Constants.LEGAL_TRACKER_TAG,"Could not delete "+file.getAbsolutePath());
							}
						}
					} else {
						Monitor.getInstance().incrementTotalGForcePointsNotProcessed(
								batchSize);
					}
					Monitor.getInstance().setLastGForceUploadStatusCode(statusCode);
				} catch (Exception e) {
					Monitor.getInstance().setLastGForceUploadStatusCode(-99);
					Monitor.getInstance()
							.setLastGForceConnectionError(e.getMessage());
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
