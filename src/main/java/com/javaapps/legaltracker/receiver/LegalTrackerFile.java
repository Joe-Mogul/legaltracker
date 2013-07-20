package com.javaapps.legaltracker.receiver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import com.javaapps.legaltracker.pojos.LegalTrackerLocation;

import android.content.Context;
import android.util.Log;

public class LegalTrackerFile<T> {

	private final ReentrantLock lock = new ReentrantLock();

	private ObjectOutputStream objectOutputStream;

	private File filesDir;
	private String fileName;
	private boolean dataIsAvailable=false;

	public LegalTrackerFile(Context context, String fileName)
			throws FileNotFoundException, IOException {
		this.filesDir = context.getFilesDir();
		this.fileName = fileName;
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
			dataIsAvailable=true;
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
		Log.i("legaltrackerreader", "reading location data file");
		ObjectInputStream objectInputStream = null;
		try {
			File file = new File(filesDir, fileName);
			objectOutputStream.close();
			objectInputStream = new ObjectInputStream(new FileInputStream(file));

			Object object = null;
			try {
				while ((object = objectInputStream.readObject()) != null) {
					LegalTrackerLocation legalTrackerLocation = (LegalTrackerLocation) object;
					Log.i("legaltrackerreader",
							legalTrackerLocation.getLatitude() + " "
									+ legalTrackerLocation.getLongitude());
				}
			} catch (Exception ex) {
				Log.e("legaltrackerreader",
						"unable to read location  data because "
								+ ex.getMessage());
			}
		} catch (Exception ex) {
			Log.e("legaltrackerreader",
					"unable to open location data file because "
							+ ex.getMessage());
		} finally {
			closeLocationDataFileForRead(objectInputStream);
			openLocationDataFileForWrite();
			dataIsAvailable=false;
			lock.unlock();
		}
		return;
	}

	private void closeLocationDataFileForRead(
			ObjectInputStream objectInputStream) {
		try {
			objectInputStream.close();
			Log.i("legaltracker", this.fileName + " closed");
		} catch (Exception ex) {
			Log.e("legaltracker", "unable to close location data file because "
					+ ex.getMessage());
		}

	}

	private void openLocationDataFileForWrite() {
		try {
			File file = new File(filesDir, this.fileName);
			objectOutputStream = new ObjectOutputStream(new FileOutputStream(
					file));
			Log.i("legaltracker", this.fileName + " opened");
		} catch (Exception ex) {
			Log.e("legaltracker", "unable to open location data file because "
					+ ex.getMessage());
		}
	}

	public boolean isDataIsAvailable() {
		return dataIsAvailable;
	}

	public void setDataIsAvailable(boolean dataIsAvailable) {
		this.dataIsAvailable = dataIsAvailable;
	}
	
	
}
