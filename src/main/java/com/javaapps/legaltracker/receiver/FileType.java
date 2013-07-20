package com.javaapps.legaltracker.receiver;

public enum FileType {
	Location("locations.obj"), Alert("alerts.obj");
	private String path;

	private FileType(String path) {
		this.path = path;
	}

	public String getPath() {
		return path;
	}
	
}
