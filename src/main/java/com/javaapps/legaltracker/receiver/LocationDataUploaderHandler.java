package com.javaapps.legaltracker.receiver;

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

import com.javaapps.legaltracker.Monitor;
import com.javaapps.legaltracker.pojos.Config;
import com.javaapps.legaltracker.pojos.LegalTrackerLocation;
import com.javaapps.legaltracker.pojos.LocationDataUpload;

public class LocationDataUploaderHandler {


	private HttpClientFactory httpClientFactory = new HttpClientFactoryImpl();

	private File filesDir;
	private String filePrefix;
 
	Map<String,FileResultMap>fileResultMaps=new HashMap<String,FileResultMap>();
	
	public LocationDataUploaderHandler(File filesDir,String filePrefix) {
		this.filesDir = filesDir;
		this.filePrefix=filePrefix;
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
		if ( !cleanUpExistingFiles()){
			Log.i("legalfiletracker","Unable to delete all existing buffer files");
		}
		StringBuilder sb=new StringBuilder();
		for (File file : this.filesDir.listFiles()) {
			String fileName=file.getName();
			if (file.getName().startsWith(filePrefix)) {
				Monitor.getInstance().setStatus("uploading "+fileName);
				sb.append(fileName+"\n");
				loadFile(file);
			}
		}
		Monitor.getInstance().setArchiveFiles(sb.toString());
	}

	boolean cleanUpExistingFiles() {
		boolean retValue=true;
		for (FileResultMap fileResultMap:fileResultMaps.values()){
			if (! fileResultMap.allBatchesUploaded() ){
				retValue=false;
				}else{
					File file=new File(fileResultMap.fileName);
					retValue=retValue && file.delete();
					fileResultMaps.remove(fileResultMap.fileName);
			}
		}
		return retValue;
	}

	private FileResultMap getResultMap(File file) throws FileNotFoundException, IOException, ClassNotFoundException
	{
		String fileName=file.getAbsolutePath();
		FileResultMap fileResultMap =fileResultMaps.get(fileName);
		if (fileResultMap ==null){
			fileResultMap=new FileResultMap(fileName);
			fileResultMaps.put(fileName,fileResultMap);
		}
		ObjectInputStream objectInputStream = null;
			try {
			objectInputStream = new ObjectInputStream(new FileInputStream(file));
			int bufferCounter = 0;
			int objectCounter = 0;
			int batchSize = Config.getConfig().getUploadBatchSize();
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
				fileResultMap.resultMap.put(ii, -1);
			}
			return(fileResultMap);
	}finally{
		closeInputStream(objectInputStream);

	}
	}
		
	private void loadFile(File file) {
		ObjectInputStream objectInputStream =null;
		try{
			FileResultMap fileResultMap=getResultMap(file);
			objectInputStream = new ObjectInputStream(new FileInputStream(file));
			List<LegalTrackerLocation> batchLocationDataList = new ArrayList<LegalTrackerLocation>();
			int index = 0;
			try {
				Object object = null;
				while ((object = objectInputStream.readObject()) != null) {
					LegalTrackerLocation legalTrackerLocation = (LegalTrackerLocation) object;
					batchLocationDataList.add(legalTrackerLocation);
					if (batchLocationDataList.size() > Config.getConfig()
							.getUploadBatchSize()) {
						uploadBatch(fileResultMap,index, batchLocationDataList);
						index++;
						batchLocationDataList.clear();
					}
				}
			} catch (EOFException ex) {
			}
			if (batchLocationDataList.size() > 0) {
				uploadBatch(fileResultMap,index, batchLocationDataList);
			}
		} catch (Exception ex) {
			Log.e("legaltrackerreader",
					"unable to open location data file because "
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
			Log.e("legaltracker",
					"Could not close archive input stream because "
							+ ex.getMessage());
		}
	}

	public boolean uploadBatch(FileResultMap fileResultMap,int index,
			List<LegalTrackerLocation> locationDataList) {
		if (locationDataList.size() == 0) {
			return true;
		}
		boolean retValue = true;
		LocationDataUpload locationDataUpload = new LocationDataUpload(
				new Date(), locationDataList);
		try {
			Monitor.getInstance().setLastUploadDate(new Date());
			String jsonStr = locationDataUpload.toJsonString();
			Monitor.getInstance().setStatus("Last upload status starting upload task");
			DataUploadTask dataUploadTask = new DataUploadTask(fileResultMap,index, jsonStr);
			Thread thread = new Thread(dataUploadTask);
			
			thread.start();
		} catch (Exception e) {
			Log.e("legaltracker",
					"cannot convert upload data to server because because"
							+ e.getMessage());
			Monitor.getInstance().setStatus("Cannot upload data to server");
		}
		return retValue;
	}

	class FileResultMap{
		private String fileName;
		private Map<Integer, Integer> resultMap = new HashMap<Integer, Integer>();
		
		public FileResultMap(String fileName) {
			this.fileName = fileName;
		}

		public boolean allBatchesUploaded() {
			for (Entry<Integer,Integer> entry:resultMap.entrySet()){
				if (( entry.getValue()/100) !=2){//status codes of 200,201,202,204 are all good
					return false;
				}
			}
			return true;
		}

		public Map<Integer, Integer> getResultMap() {
			return resultMap;
		}

		public void setResultMap(Map<Integer, Integer> resultMap) {
			this.resultMap = resultMap;
		}
		
	}
	
	private class DataUploadTask implements Runnable {
		private int index;
		private String jsonStr;
		private FileResultMap fileResultMap;

		public DataUploadTask(FileResultMap fileResultMap,int index, String jsonStr) {
			this.index = index;
			this.jsonStr = jsonStr;
			this.fileResultMap=fileResultMap;
		}

		@Override
		public void run() {
			try {
				HttpClient httpclient = httpClientFactory.createHttpClient();
				HttpPost httppost = new HttpPost(Config.getConfig()
						.getLocationDataEndpoint());
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
						2);
				nameValuePairs.add(new BasicNameValuePair("data", jsonStr));
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				HttpResponse response = httpclient.execute(httppost);
				fileResultMap.resultMap.put(index, response
						.getStatusLine().getStatusCode());
				Monitor.getInstance().setLastUploadStatusCode(response
						.getStatusLine().getStatusCode());
			} catch (Exception e) {
				Monitor.getInstance().setStatus("could not upload "+e.getMessage());
				Log.e("legaltracker",
						"cannot  upload data to server because because"
								+ e.getMessage());
			}
		}
	}


}
