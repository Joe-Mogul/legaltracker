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

public class GForceDataUpload implements Serializable {
	private static final long serialVersionUID = 1L;

	private final Date uploadDate;

	private final List<GForceData> gforceDataList ;

	private String deviceId;
	
	public GForceDataUpload(String deviceId,Date uploadDate,
			List<GForceData> gforceDataList) {
		this.uploadDate = uploadDate;
		this.gforceDataList = gforceDataList;
		this.deviceId=deviceId;
	}
	


	public Date getUploadDate() {
		return uploadDate;
	}

	public String getDeviceId() {
		return deviceId;
	}

	
	public List<GForceData> getGforceDataList() {
		return gforceDataList;
	}

	public String toJsonString() throws JSONException{
		JSONObject jsonObj = new JSONObject();
		DateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		jsonObj.put("deviceId", getDeviceId());
		Date uploadDate=getUploadDate();
		jsonObj.put("uploadDate", (uploadDate != null )?dateFormat.format(uploadDate):null);
		JSONArray jsonArray=new JSONArray();
		jsonObj.put("gforceDataList", jsonArray);
		for (GForceData gforceData:gforceDataList){
			JSONObject arrayObject=new JSONObject();
			arrayObject.put("sampleDateInMillis",gforceData.getSampleDateInMillis());
			arrayObject.put("x",gforceData.getX());
			arrayObject.put("y",gforceData.getY());
			arrayObject.put("z",gforceData.getZ());
			jsonArray.put(arrayObject);
		}
        return(jsonObj.toString());
	}


}
