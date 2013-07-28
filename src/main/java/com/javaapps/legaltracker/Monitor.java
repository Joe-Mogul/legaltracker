package com.javaapps.legaltracker;

import java.util.Date;

public class Monitor {

	private static Monitor monitor;
	private String status="Started Legal Tracker";
	private String lastLocation="No location yet";
	private Date lastUploadDate;
	private int numberOfPointsLoggedSinceUpload;
	private int numberOfUploads;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
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
}
