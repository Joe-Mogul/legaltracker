package com.javaapps.legaltracker.pojos;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LocationDataUpload implements Serializable {
	private static final long serialVersionUID = 1L;

	private final Date uploadDate;

	private final List<LegalTrackerLocation> locationDataList ;

	
	
	public LocationDataUpload(Date uploadDate,
			List<LegalTrackerLocation> locationDataList) {
		this.uploadDate = uploadDate;
		this.locationDataList = locationDataList;
	}

	public Date getUploadDate() {
		return uploadDate;
	}

	public List<LegalTrackerLocation> getLocationDataList() {
		return locationDataList;
	}

	public String toJsonString() throws JSONException{
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("uploadDate", getUploadDate());
		JSONArray jsonArray=new JSONArray();
		jsonObj.put("locationDataList", jsonArray);
		for (LegalTrackerLocation location:locationDataList){
			JSONObject arrayObject=new JSONObject();
			arrayObject.put("sampleDate",location.getSampleDate());
			arrayObject.put("systemDate",location.getDate());
			arrayObject.put("lastGoodUpdate",location.getLastGoodUpdate());
			arrayObject.put("lastGoodUpdate",location.getLastGoodUpdate());
			arrayObject.put("latitude",location.getLatitude());
			arrayObject.put("longitude",location.getLongitude());
			arrayObject.put("bearing",location.getBearing());
			arrayObject.put("speed",location.getSpeed());
			arrayObject.put("altitude",location.getAltitude());
			jsonArray.put(arrayObject);
		}
        return(jsonObj.toString());
	}


}
