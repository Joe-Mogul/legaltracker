package com.javaapps.legaltracker.io;

public enum FileType {
	Location("locations","obj"), Alert("alerts","obj"), GForce("gforce","obj");
	
	private String prefix;
	private String extension;

	private FileType(String prefix,String extension) {
		this.prefix = prefix;
		this.extension=extension;
	}

	public String getPrefix() {
		return prefix;
	}

	public String getExtension() {
		return extension;
	}

	
	
}
