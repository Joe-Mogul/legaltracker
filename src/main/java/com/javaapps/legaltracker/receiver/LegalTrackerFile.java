package com.javaapps.legaltracker.receiver;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import android.content.Context;
import android.util.Log;

public class LegalTrackerFile<T> {

	private final ReentrantLock lock = new ReentrantLock();

	private ObjectOutputStream objectOutputStream;

	private File filesDir;
	private String prefix;
	private String extension;
	private java.text.DateFormat dateFormat=new SimpleDateFormat("yyyyMMddHHmmss");
	private UploadHandler uploadHandler;

	public LegalTrackerFile(Context context, String prefix,String extension)
			throws FileNotFoundException, IOException {
		this.filesDir = context.getFilesDir();
		uploadHandler=new LocationDataUploaderHandler(this.filesDir );
		this.prefix =prefix;
		this.extension=extension;
		openLocationDataFileForWrite();
	}

	public List<T> writeToObjectFile(List<T> objectList) throws IOException {
		List<T> retList = new ArrayList<T>();
		boolean isNotLocked = lock.tryLock();
		// If it is locked then just return the list and try to save it another
		// time
		if (!isNotLocked) {
			return objectList;
		}
		try {
			boolean errorThrown = false;
			for (T object : objectList) {
				try {
					if (errorThrown) {
						retList.add(object);
					} else {
						objectOutputStream.writeObject(object);
					}
				} catch (Exception ex) {
					errorThrown = true;
					Log.e("legaltracker",
							"cannot save location buffer because "
									+ ex.getMessage());
					retList.add(object);
				}
			}
		} finally {
			if (objectOutputStream != null) {
				objectOutputStream.flush();
			}
			lock.unlock();
		}
		return (retList);
	}

	public void readFromObjectFile() {
		boolean isNotLocked = lock.tryLock();
		// If it is locked then just return the list and try to save it another
		// time
		if (!isNotLocked) {
			return;
		}
		Log.i("legaltrackerreader", "moving data file to buffer");
		try {
			objectOutputStream.flush();
			objectOutputStream.close();
			File file = new File(filesDir, prefix+extension);
			String newPath=file.getPath()+"/"+prefix+"_archive_"+dateFormat.format(new Date())+"."+extension;
			file.renameTo(new File(newPath));
			uploadHandler.uploadData(prefix+"_archive_");
		} catch (Exception ex) {
			Log.e("legaltrackerreader",
					"unable move data file because "
							+ ex.getMessage());
		} finally {
			openLocationDataFileForWrite();
			lock.unlock();
		}
		return;
	}

	
	private void openLocationDataFileForWrite() {
		try {
			String fileName=prefix+"."+extension;
			File file = new File(filesDir, fileName);
			objectOutputStream = new ObjectOutputStream(new FileOutputStream(
					file));
			Log.i("legaltracker", fileName + " opened");
		} catch (Exception ex) {
			Log.e("legaltracker", "unable to open location data file because "
					+ ex.getMessage());
		}
	}

	
	
}
