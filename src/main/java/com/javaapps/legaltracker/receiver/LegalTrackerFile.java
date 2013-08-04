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

import com.javaapps.legaltracker.Monitor;
import com.javaapps.legaltracker.pojos.Config;

import android.content.ContextWrapper;
import android.util.Log;

public class LegalTrackerFile<T> {

	public final static String ARCHIVE_STRING="_archive_";
	
	private final ReentrantLock lock = new ReentrantLock();
   
	private ObjectOutputStream objectOutputStream;

	private File filesDir;
	private String prefix;
	private String extension;
	private java.text.DateFormat dateFormat=new SimpleDateFormat("yyyyMMddHHmmss");
	
	public LegalTrackerFile(String prefix,String extension)
			throws FileNotFoundException, IOException {
		this.filesDir =Config.getConfig().getFilesDir();
		this.prefix =prefix;
		this.extension=extension;
		openLocationDataFileForWrite();
	}

	public void deleteFiles(){
		for (File file:filesDir.listFiles()){
			if (file.getName().startsWith(prefix+ARCHIVE_STRING)){
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
			File file = new File(filesDir,getActiveFileName());
			Monitor.getInstance().setCurrentFileSize(file.length());
			lock.unlock();
		}
		return (retList);
	}

	public void closeOutObjectFile() {
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
			File file = new File(filesDir,getActiveFileName());
			file.renameTo(new File(filesDir, getArchiveFileName()));
			Monitor.getInstance().setCurrentFileSize(0);
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

	private String getActiveFileName(){
		return prefix+"."+extension;
	}
	
	private String getArchiveFileName()
	{
		return prefix+ARCHIVE_STRING+dateFormat.format(new Date())+"."+extension;
	}
	
	private void openLocationDataFileForWrite() {
		try {
			String fileName=getActiveFileName();
			File file = new File(filesDir, fileName);
			objectOutputStream = new ObjectOutputStream(new FileOutputStream(
					file));
			Log.i("legaltracker", fileName + " opened");
		} catch (Exception ex) {
			Log.e("legaltracker", "unable to open location data file because "
					+ ex.getMessage());
		}
	}

	public boolean isEmpty() {
		String fileName=getActiveFileName();
		File file = new File(filesDir, fileName);
		//object files are not 0 length when opened
		return (file.length()<5);
	}

	
	
}
