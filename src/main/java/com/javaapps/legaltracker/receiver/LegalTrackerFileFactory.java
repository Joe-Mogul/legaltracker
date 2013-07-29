package com.javaapps.legaltracker.receiver;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import android.content.Context;

public class LegalTrackerFileFactory {
	private static Map<String,LegalTrackerFile> fileMap=new HashMap<String,LegalTrackerFile>();
	
	public synchronized static LegalTrackerFile getLegalTrackerFile(Context context,String filePrefix,String extension) throws FileNotFoundException, IOException{
		String filename=filePrefix+"."+extension;
		LegalTrackerFile legalTrackerFile=fileMap.get(filename);
		if (legalTrackerFile == null){
			legalTrackerFile=new LegalTrackerFile(context.getFilesDir(),filePrefix,extension);
			fileMap.put(filename, legalTrackerFile);
		}
		return legalTrackerFile;
	}

}
