package com.javaapps.legaltracker.pojos;

import java.io.File;

public class Config {

	private static Config config;

	private File filesDir;
	
	private String deviceId="12345";

	private int testStatusCode = 200;

	private int gforceUploadPeriod = 1100000;

	private int gforceUploadDelay = 1100000;

	private int locationUploadPeriod = 1300000;

	private int locationUploadDelay = 1300000;

	private long minimumLoggingIntervals = 400;
	
	private int locationListenerBufferSize=100;
	
	private int gforceListenerBufferSize=100;

	private long httpTimeout = 1000;

	private int uploadBatchSize = 1000;
	
	
	private String locationDataEndpoint = "http://legaltrackerserver.myjavaapps.cloudbees.net/backend/uploadLocationData";

	private String gforceDataEndpoint = "http://legaltrackerserver.myjavaapps.cloudbees.net/backend/uploadGForceData";
	
	public String getLocationDataEndpoint() {
		return locationDataEndpoint;
	}

	public void setLocationDataEndpoint(String locationDataEndpoint) {
		this.locationDataEndpoint = locationDataEndpoint;
	}
		

	public String getGforceDataEndpoint() {
		return gforceDataEndpoint;
	}

	public void setGforceDataEndpoint(String gforceDataEndpoint) {
		this.gforceDataEndpoint = gforceDataEndpoint;
	}

	public int getLocationListenerBufferSize() {
		return locationListenerBufferSize;
	}

	public void setLocationListenerBufferSize(int locationListenerBufferSize) {
		this.locationListenerBufferSize = locationListenerBufferSize;
	}

	public long getHttpTimeout() {
		return httpTimeout;
	}

	public void setHttpTimeout(long httpTimeout) {
		this.httpTimeout = httpTimeout;
	}

	
	
	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
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

	public int getGforceUploadPeriod() {
		return gforceUploadPeriod;
	}

	public void setGforceUploadPeriod(int gforceUploadPeriod) {
		this.gforceUploadPeriod = gforceUploadPeriod;
	}

	public int getGforceUploadDelay() {
		return gforceUploadDelay;
	}

	public void setGforceUploadDelay(int gforceUploadDelay) {
		this.gforceUploadDelay = gforceUploadDelay;
	}

	public int getGforceListenerBufferSize() {
		return gforceListenerBufferSize;
	}

	public void setGforceListenerBufferSize(int gforceListenerBufferSize) {
		this.gforceListenerBufferSize = gforceListenerBufferSize;
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
