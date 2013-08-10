package com.javaapps.legaltracker.pojos;

import java.io.File;

public class Config {

	private static Config config;

	private File filesDir;

	private int testStatusCode = 200;

	private int locationUploadPeriod = 600000;

	private int locationUploadDelay = 600000;

	private long minimumLoggingIntervals = 15000;

	private long httpTimeout = 1000;

	private int uploadBatchSize = 1000;

	private String locationDataEndpoint = "http://192.168.2.6:8080/demo/people/uploadLocationData";

	public String getLocationDataEndpoint() {
		return locationDataEndpoint;
	}

	public void setLocationDataEndpoint(String locationDataEndpoint) {
		this.locationDataEndpoint = locationDataEndpoint;
	}

	public int getLocationListenerBufferSize() {
		// return (600000/(int)this.minimumLoggingIntervals);
		return 40;
		// we should store 10 minutes of logged points in the buffer
	}

	public long getHttpTimeout() {
		return httpTimeout;
	}

	public void setHttpTimeout(long httpTimeout) {
		this.httpTimeout = httpTimeout;
	}

	public long getMinimumLoggingIntervals() {
		return minimumLoggingIntervals;
	}

	public void setMinimumLoggingIntervals(long minimumLoggingIntervals) {
		this.minimumLoggingIntervals = minimumLoggingIntervals;
	}

	public int getLocationUploadPeriod() {
		return locationUploadPeriod;
	}

	public void setLocationUploadPeriod(int locationUploadPeriod) {
		this.locationUploadPeriod = locationUploadPeriod;
	}

	public int getLocationUploadDelay() {
		return locationUploadDelay;
	}

	public void setLocationUploadDelay(int locationUploadDelay) {
		this.locationUploadDelay = locationUploadDelay;
	}

	public int getUploadBatchSize() {
		return uploadBatchSize;
	}

	public void setUploadBatchSize(int uploadBatchSize) {
		this.uploadBatchSize = uploadBatchSize;
	}

	public File getFilesDir() {
		return filesDir;
	}

	public void setFilesDir(File filesDir) {
		this.filesDir = filesDir;
	}

	public int getTestStatusCode() {
		return testStatusCode;
	}

	public void setTestStatusCode(int testStatusCode) {
		this.testStatusCode = testStatusCode;
	}

	public synchronized static Config getInstance() {
		if (config == null) {
			config = new Config();
		}
		return (config);
	}

	private Config() {

	}

}
