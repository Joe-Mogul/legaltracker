package com.javaapps.legaltracker.pojos;

import java.util.Date;

public class Monitor {

	private static Monitor monitor;
	private String status="Started Legal Tracker";
	private String gpsStatus="Not Available";
	private String wifiStatus="Not Available";
	private String lastConnectionError;
	private String lastGForceConnectionError;
	private String lastLocation="No location yet";
	private Date lastUploadDate;
	private Date lastGForceUploadDate;
	private int numberOfPointsLoggedSinceUpload;
	private int numberOfUploads;
	private int numberOfGForceUploads;
	private String archiveFiles;
	private int lastUploadStatusCode=-1;
	private int lastGForceUploadStatusCode=-1;
	private int totalPointsLogged=0;
	private int totalPointsUploaded=0;
	private int totalPointsProcessed=0;
	private int totalPointsNotProcessed=0;
	private int totalGForcePointsLogged=0;
	private int totalGForcePointsUploaded=0;
	private int totalGForcePointsProcessed=0;
	private int totalGForcePointsNotProcessed=0;
	private int pointsInBuffer=0;
	private int gforcePointsInBuffer=0;
	private long currentFileSize=0;
	private long gforceFileSize=0;
	
	
	
	public Date getLastGForceUploadDate() {
		return lastGForceUploadDate;
	}

	public void setLastGForceUploadDate(Date lastGForceUploadDate) {
		this.lastGForceUploadDate = lastGForceUploadDate;
	}

	public int getNumberOfGForceUploads() {
		return numberOfGForceUploads;
	}

	public void setNumberOfGForceUploads(int numberOfGForceUploads) {
		this.numberOfGForceUploads = numberOfGForceUploads;
	}

	public static Monitor getMonitor() {
		return monitor;
	}

	public int getTotalGForcePointsLogged() {
		return totalGForcePointsLogged;
	}

	public int getTotalGForcePointsUploaded() {
		return totalGForcePointsUploaded;
	}

	public int getTotalGForcePointsProcessed() {
		return totalGForcePointsProcessed;
	}

	public int getTotalGForcePointsNotProcessed() {
		return totalGForcePointsNotProcessed;
	}

	public int getGforcePointsInBuffer() {
		return gforcePointsInBuffer;
	}

	public void setGforcePointsInBuffer(int gforcePointsInBuffer) {
		this.gforcePointsInBuffer = gforcePointsInBuffer;
	}

	public String getLastGForceConnectionError() {
		return lastGForceConnectionError;
	}

	public void setLastGForceConnectionError(String lastGForceConnectionError) {
		this.lastGForceConnectionError = lastGForceConnectionError;
	}

	public int getLastGForceUploadStatusCode() {
		return lastGForceUploadStatusCode;
	}

	public void setLastGForceUploadStatusCode(int lastGForceUploadStatusCode) {
		this.lastGForceUploadStatusCode = lastGForceUploadStatusCode;
	}

	public long getGforceFileSize() {
		return gforceFileSize;
	}

	public void setGforceFileSize(long gforceFileSize) {
		this.gforceFileSize = gforceFileSize;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getArchiveFiles() {
		return archiveFiles;
	}

	public void setArchiveFiles(String archiveFiles) {
		this.archiveFiles = archiveFiles;
	}

	
	
	public int getLastUploadStatusCode() {
		return lastUploadStatusCode;
	}

	public void setLastUploadStatusCode(int lastUploadStatusCode) {
		this.lastUploadStatusCode = lastUploadStatusCode;
	}

	public String getLastLocation() {
		return lastLocation;
	}

	public void setLastLocation(String lastLocation) {
		this.lastLocation = lastLocation;
	}

	public Date getLastUploadDate() {
		return lastUploadDate;
	}

	public void setLastUploadDate(Date lastUploadDate) {
		this.lastUploadDate = lastUploadDate;
	}

	public int getNumberOfPointsLoggedSinceUpload() {
		return numberOfPointsLoggedSinceUpload;
	}

	public void setNumberOfPointsLoggedSinceUpload(
			int numberOfPointsLoggedSinceUpload) {
		this.numberOfPointsLoggedSinceUpload = numberOfPointsLoggedSinceUpload;
	}

	public int getNumberOfUploads() {
		return numberOfUploads;
	}

	public void setNumberOfUploads(int numberOfUploads) {
		this.numberOfUploads = numberOfUploads;
	}
	
	

	public int getTotalPointsNotProcessed() {
		return totalPointsNotProcessed;
	}

	public static Monitor getInstance() {
		if (monitor == null) {
			monitor = new Monitor();
		}
		return monitor;
	}

	private Monitor() {

	}

	public CharSequence getLastUploadDateDisplay() {
		if (this.lastUploadDate != null) {
			return this.lastUploadDate.toGMTString();
		} else {
			return "No uploads yet";
		}
	}

	public void incrementTotalPointsProcessed(int size) {
		totalPointsProcessed+=size;
	}

	public void incrementTotalPointsNotProcessed(int size) {
		totalPointsNotProcessed+=size;
	}

	public void incrementTotalPointsLogged(int size) {
		totalPointsLogged+=size;
	}

	public void incrementTotalPointsUploaded(int size) {
		totalPointsUploaded+=size;
	}

	
	public void incrementTotalGForcePointsProcessed(int size) {
		totalGForcePointsProcessed+=size;
	}

	public void incrementTotalGForcePointsNotProcessed(int size) {
		totalGForcePointsNotProcessed+=size;
	}

	public void incrementTotalGForcePointsLogged(int size) {
		totalGForcePointsLogged+=size;
	}

	public void incrementTotalGForcePointsUploaded(int size) {
		totalGForcePointsUploaded+=size;
	}

	public int getTotalPointsProcessed() {
		return totalPointsProcessed;
	}

	public int getTotalPointsLogged() {
		return totalPointsLogged;
	}

	public int getTotalPointsUploaded() {
		return totalPointsUploaded;
	}
		
	public long getCurrentFileSize() {
		return currentFileSize;
	}

	public void setCurrentFileSize(long currentFileSize) {
		this.currentFileSize = currentFileSize;
	}
	
	

	public int getPointsInBuffer() {
		return pointsInBuffer;
	}

	public void setPointsInBuffer(int pointsInBuffer) {
		this.pointsInBuffer = pointsInBuffer;
	}

	
	
	public String getGpsStatus() {
		return gpsStatus;
	}

	public void setGpsStatus(String gpsStatus) {
		this.gpsStatus = gpsStatus;
	}

	public String getLastConnectionError() {
		return lastConnectionError;
	}

	public void setLastConnectionError(String lastConnectionError) {
		this.lastConnectionError = lastConnectionError;
	}

	
	
	public String getWifiStatus() {
		return wifiStatus;
	}

	public void setWifiStatus(String wifiStatus) {
		this.wifiStatus = wifiStatus;
	}

	
	
	public void reset()
	{
	 status="Monitor reset";
	lastLocation="No location yet";
	lastConnectionError="";
	gpsStatus="Not Available";
	wifiStatus="Not Available";
	lastUploadDate=null;
	numberOfPointsLoggedSinceUpload=0;
	numberOfUploads=0;
	archiveFiles=null;
	lastUploadStatusCode=-1;
	totalPointsLogged=0;
	totalPointsUploaded=0;
	totalPointsProcessed=0;
	totalPointsProcessed=0;
	totalPointsNotProcessed=0;
	pointsInBuffer=0;
	}

	
}
