package com.javaapps.legaltracker.receiver;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.javaapps.legaltracker.io.FileType;
import com.javaapps.legaltracker.io.LegalTrackerFile;
import com.javaapps.legaltracker.io.LegalTrackerFileFactory;
import com.javaapps.legaltracker.pojos.Config;
import com.javaapps.legaltracker.pojos.Constants;
import com.javaapps.legaltracker.pojos.GForceData;
import com.javaapps.legaltracker.pojos.Monitor;

public class GForceBuffer {

	private static GForceBuffer gforceBuffer;

	private List<GForceData> gforceBufferList = new ArrayList<GForceData>();

	private LegalTrackerFile legalTrackerFile;

	private boolean gpsStatusOn = true;

	private List<GForceData> runningGForceDataAverageList = new ArrayList<GForceData>();

	public static GForceBuffer getInstance() throws FileNotFoundException, IOException {
		if (gforceBuffer == null) {
			gforceBuffer = new GForceBuffer();
		}
		return gforceBuffer;
	}

	private GForceBuffer() throws FileNotFoundException, IOException {
			legalTrackerFile = LegalTrackerFileFactory
					.getLegalTrackerFile(FileType.GForce.getPrefix(),
							FileType.GForce.getExtension());
	}

	public void logGForce(GForceData gforceData) {
		if (gpsStatusOn) {
			GForceData averagedGForceData = getAveragedGForceData(gforceData);
			if (averagedGForceData == null) {
				return;
			}
			gforceBufferList.add(averagedGForceData);
			Monitor.getInstance().incrementTotalGForcePointsLogged(1);
			Monitor.getInstance().setGforcePointsInBuffer(gforceBufferList.size());
			clearPointsInRunningAverageBuffer();
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

	GForceData getAveragedGForceData(GForceData gforceData) {
		GForceData retGForceData = null;
		runningGForceDataAverageList.add(gforceData);
		long minimumTime = gforceData.getSampleDateInMillis();
		long maximumTime = 0;
		for (GForceData tmpGForceData : runningGForceDataAverageList) {
			minimumTime = Math.min(minimumTime,
					tmpGForceData.getSampleDateInMillis());
			maximumTime = Math.max(maximumTime,
					tmpGForceData.getSampleDateInMillis());
		}
		// if the spread in data is less then 100 miliseconds then just return
		// and dont do anything
		if (Math.abs(maximumTime - minimumTime) < 100) {
			return null;
		}
		retGForceData = GForceData.averageData(runningGForceDataAverageList);
		return retGForceData;
	}

	public int getPointsInRunningAverageBuffer() {
		return runningGForceDataAverageList.size();
	}

	public void clearPointsInRunningAverageBuffer() {
		runningGForceDataAverageList.clear();
	}
}
