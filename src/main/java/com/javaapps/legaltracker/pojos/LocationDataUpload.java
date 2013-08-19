package com.javaapps.legaltracker.pojos;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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

	private String deviceId;
	
	public LocationDataUpload(String deviceId,Date uploadDate,
			List<LegalTrackerLocation> locationDataList) {
		this.uploadDate = uploadDate;
		this.locationDataList = locationDataList;
		this.deviceId=deviceId;
	}

	public Date getUploadDate() {
		return uploadDate;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public List<LegalTrackerLocation> getLocationDataList() {
		return locationDataList;
	}

	public String toJsonString() throws JSONException{
		JSONObject jsonObj = new JSONObject();
		DateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		jsonObj.put("deviceId", getDeviceId());
		Date uploadDate=getUploadDate();
		jsonObj.put("uploadDate", (uploadDate != null )?dateFormat.format(uploadDate):null);
		JSONArray jsonArray=new JSONArray();
		jsonObj.put("locationDataList", jsonArray);
		for (LegalTrackerLocation location:locationDataList){
			JSONObject arrayObject=new JSONObject();
			arrayObject.put("sampleDate",(location.getSampleDate() !=null )?dateFormat.format(location.getSampleDate()):null);
			arrayObject.put("systemDate",(location.getDate() !=null )?dateFormat.format(location.getDate()):null);
			arrayObject.put("lastGoodUpdate",(location.getLastGoodUpdate() !=null )?dateFormat.format(location.getLastGoodUpdate()):null);
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
