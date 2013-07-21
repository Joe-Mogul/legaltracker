package com.javaapps.legaltracker.receiver;

import java.util.List;

import com.javaapps.legaltracker.pojos.LegalTrackerLocation;

public interface UploadHandler {

	public abstract boolean uploadData(
			List<LegalTrackerLocation> locationDataList);

}