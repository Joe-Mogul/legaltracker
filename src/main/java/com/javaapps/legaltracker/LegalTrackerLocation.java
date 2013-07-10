package com.javaapps.legaltracker;

import java.io.Serializable;
import java.util.Date;

import android.location.Location;

public class LegalTrackerLocation implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private final Date systemDate;
	
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

	@Override
	public String toString() {
		return "LegalTrackerLocation [date=" + systemDate + ", latitude=" + latitude
				+ ", longitude=" + longitude + ", speed=" + speed
				+ ", bearing=" + bearing + ", altitude=" + altitude + "]";
	}
	
	

}
