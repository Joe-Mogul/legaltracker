package com.javaapps.legaltracker.pojos;

public class Config {

	private static Config config;

	private int locationUploadPeriod = 60000; 
	
	private int locationUploadDelay = 60000; 

	private long minimumLoggingIntervals = 15000;

	private int locationListenerBufferSize = 10;

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

	public synchronized static Config getConfig() {
		if (config == null) {
			config = new Config();
		}
		return (config);
	}

	private Config() {

	}

}
