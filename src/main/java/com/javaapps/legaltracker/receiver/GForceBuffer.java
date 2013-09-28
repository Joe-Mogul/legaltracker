package com.javaapps.legaltracker.receiver;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.javaapps.legaltracker.io.FileType;
import com.javaapps.legaltracker.io.LegalTrackerFile;
import com.javaapps.legaltracker.pojos.Config;
import com.javaapps.legaltracker.pojos.Constants;
import com.javaapps.legaltracker.pojos.GForceData;
import com.javaapps.legaltracker.pojos.Monitor;

public class GForceBuffer {

	private static GForceBuffer gforceBuffer;

	private List<GForceData> gforceBufferList = new ArrayList<GForceData>();

	private LegalTrackerFile legalTrackerFile;

	private boolean gpsStatusOn = true;

	private List<GForceData> shortTermGForceDataList = new ArrayList<GForceData>();

	public static GForceBuffer getInstance() throws FileNotFoundException, IOException {
		if (gforceBuffer == null) {
			gforceBuffer = new GForceBuffer();
		}
		return gforceBuffer;
	}

	private GForceBuffer() throws FileNotFoundException, IOException {
			legalTrackerFile = new LegalTrackerFile(FileType.GForce.getPrefix(),FileType.GForce.getExtension());
	}

	public void logGForce(GForceData gforceData) {
		if (gpsStatusOn) {
			GForceData maximumGForceData = getMaximumGForceData(gforceData);
			if (maximumGForceData == null) {
				return;
			}
			gforceBufferList.add(maximumGForceData);
			Monitor.getInstance().incrementTotalGForcePointsLogged(1);
			Monitor.getInstance().setGforcePointsInBuffer(gforceBufferList.size());
			shortTermGForceDataList.clear();
			Monitor monitor = Monitor.getInstance();
			if (gforceBufferList.size() > Config.getInstance()
					.getGforceListenerBufferSize()) {
				try {
					gforceBufferList = legalTrackerFile
							.writeToObjectFile(gforceBufferList);
					this.gforceBufferList.clear();
				} catch (IOException e) {
					Log.e(Constants.LEGAL_TRACKER_TAG,
							"Unable to save gforce buffer because "
									+ e.getMessage());
				}
			}
		} else {
			Monitor.getInstance().setLastLocation(
					"No locations logged because GPS not enabled");
		}

	}

	GForceData getMaximumGForceData(GForceData gforceData) {
		GForceData retGForceData = null;
		shortTermGForceDataList.add(gforceData);
		long minimumTime = gforceData.getSampleDateInMillis()+100000;
		long maximumTime = 0;
		for (GForceData tmpGForceData : shortTermGForceDataList) {
			minimumTime = Math.min(minimumTime,
					tmpGForceData.getSampleDateInMillis());
			maximumTime = Math.max(maximumTime,
					tmpGForceData.getSampleDateInMillis());
		}
		// if the spread in data is less then 500 miliseconds then just return
		// and dont do anything
		if (Math.abs(maximumTime - minimumTime) < 500) {
			return null;
		}
		retGForceData = GForceData.maximumData(shortTermGForceDataList);
		return retGForceData;
	}

	public List<GForceData> getShortTermGForceDataList() {
		return shortTermGForceDataList;
	}

	public void setShortTermGForceDataList(List<GForceData> shortTermGForceDataList) {
		this.shortTermGForceDataList = shortTermGForceDataList;
	}

}
