package com.javaapps.legaltracker.pojos;

import java.net.URI;

public class Config {

	private static Config config;

	private int locationUploadPeriod = 6000; 
	
	private int locationUploadDelay = 6000; 

	private long minimumLoggingIntervals = 1500;

	private int locationListenerBufferSize = 10;

	private int uploadBatchSize=1000;
	
	private String locationDataEndpoint="http://lextech.com/legaltracker";
	
	public String getLocationDataEndpoint() {
		return locationDataEndpoint;
	}
	
	

	public void setLocationDataEndpoint(String locationDataEndpoint) {
		this.locationDataEndpoint = locationDataEndpoint;
	}



	public int getLocationListenerBufferSize() {
		return locationListenerBufferSize;
	}

	public void setLocationListenerBufferSize(int locationListenerBufferSize) {
		this.locationListenerBufferSize = locationListenerBufferSize;
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

	public  void setLocationUploadDelay(int locationUploadDelay) {
		this.locationUploadDelay = locationUploadDelay;
	}

	
	public int getUploadBatchSize() {
		return uploadBatchSize;
	}

	public void setUploadBatchSize(int uploadBatchSize) {
		this.uploadBatchSize = uploadBatchSize;
	}

	public synchronized static Config getConfig() {
		if (config == null) {
			config = new Config();
		}
		return (config);
	}

	private Config() {

	}


}
