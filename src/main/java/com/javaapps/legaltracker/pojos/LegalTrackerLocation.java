package com.javaapps.legaltracker.pojos;

import java.io.Serializable;
import java.util.Date;

import android.location.Location;

public class LegalTrackerLocation implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private final Date systemDate;
	
	private final Date sampleDate;
	
	private final double latitude;
	
	private final double longitude;

	private final float speed;

	private final float bearing;

	private final double altitude;
	
	public LegalTrackerLocation(Location location){
		this.latitude=location.getLatitude();
		this.longitude=location.getLongitude();
		this.systemDate=new Date();
		this.speed=location.getSpeed();
		this.bearing=location.getBearing();
		this.altitude=location.getAltitude();
		this.sampleDate=new Date(location.getTime());
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public Date getDate() {
		return systemDate;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public float getSpeed() {
		return speed;
	}

	public float getBearing() {
		return bearing;
	}

	public double getAltitude() {
		return altitude;
	}

	
	public Date getSampleDate() {
		return sampleDate;
	}

	@Override
	public String toString() {
		return "LegalTrackerLocation [systemDate=" + systemDate
				+ ", sampleDate=" + sampleDate + ", latitude=" + latitude
				+ ", longitude=" + longitude + ", speed=" + speed
				+ ", bearing=" + bearing + ", altitude=" + altitude + "]";
	}

	

}
