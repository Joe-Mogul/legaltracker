package com.javaapps.legaltracker.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import com.javaapps.legaltracker.pojos.Config;
import com.javaapps.legaltracker.pojos.Constants;
import com.javaapps.legaltracker.pojos.Monitor;

import android.content.ContextWrapper;
import android.util.Log;

public class LegalTrackerFile<T> {

	public final static String ARCHIVE_STRING = "_archive_";

	private final ReentrantLock lock = new ReentrantLock();

	private File filesDir;
	private String prefix;
	private String extension;

	public LegalTrackerFile(String prefix, String extension)
			throws FileNotFoundException, IOException {
		this.filesDir = Config.getInstance().getFilesDir();
		this.prefix = prefix;
		this.extension = extension;
	}

	public void deleteFiles() {
		for (File file : filesDir.listFiles()) {
			if (file.getName().startsWith(prefix + ARCHIVE_STRING)) {
				file.delete();
			}
		}
	}

	public List<T> writeToObjectFile(List<T> objectList) throws IOException {
		List<T> retList = new ArrayList<T>();
		boolean isNotLocked = lock.tryLock();
		// If it is locked then just return the list and try to save it another
		// time
		if (!isNotLocked) {
			return objectList;
		}
		File file = new File(filesDir, getActiveFileName());
		if (file.exists() && file.renameTo(new File(filesDir, getArchiveFileName()))) {
			Log.i(Constants.LEGAL_TRACKER_TAG, prefix
					+ " file successfully archived");
		} else {
			Log.e(Constants.LEGAL_TRACKER_TAG, prefix
					+ " file could not be archived");
		}
		ObjectOutputStream objectOutputStream = null;
		try {
			objectOutputStream = new ObjectOutputStream(new FileOutputStream(
					file, true));
			objectOutputStream.reset();
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
					Log.e(Constants.LEGAL_TRACKER_TAG, "cannot save " + prefix
							+ " buffer because " + ex.getMessage());
					retList.add(object);
				}
			}
		} finally {
			if (objectOutputStream != null) {
				objectOutputStream.flush();
				objectOutputStream.close();
			}
			if (file.getName().startsWith("location")) {
				Monitor.getInstance().setCurrentFileSize(file.length());
			} else {
				Monitor.getInstance().setGforceFileSize(file.length());
			}
			setArchiveFileNamesOnMonitor();
			lock.unlock();
		}
		return (retList);
	}

	private String getActiveFileName() {
		return prefix + "." + extension;
	}

	private String getArchiveFileName() {
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		return prefix + ARCHIVE_STRING + dateFormat.format(new Date()) + "."
				+ extension;
	}

	private void setArchiveFileNamesOnMonitor() {
		StringBuilder sb = new StringBuilder();
		for (File file : filesDir.listFiles()) {
			if (file.getName().contains(ARCHIVE_STRING)) {
				sb.append(file.getName() + " " + file.length() + "\n");
			}
		}
		Monitor.getInstance().setArchiveFiles(sb.toString());
	}

}
