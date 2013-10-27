package com.javaapps.legaltracker.upload;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

import com.javaapps.legaltracker.pojos.Config;
import com.javaapps.legaltracker.pojos.Constants;

import android.util.Log;

public class HttpClientFactoryImpl implements HttpClientFactory {

	public HttpClient getHttpClient() {
		return new DefaultHttpClient();
	}
	/*
	 * private ReentrantLock lock = new ReentrantLock();
	
	private HttpClient httpClient;
	
	private long lastLockTime=0;
	
	private long MINUTE_IN_MILLIS=60*1000;

	@Override
	public HttpClient getHttpClient() {
		/*try {
			if ((System.currentTimeMillis()-lastLockTime)>MINUTE_IN_MILLIS){
				lock.unlock();
			}
			if (lock.tryLock(Config.getInstance().getHttpTimeout(),
					TimeUnit.MILLISECONDS)) {
				lastLockTime=System.currentTimeMillis();
				if (httpClient == null) {
					HttpClient httpClient = new DefaultHttpClient();
				}
				return new DefaultHttpClient();
			/*} else {
				return null;
			}
		} catch (InterruptedException ex) {
			Log.i(Constants.LEGAL_TRACKER_TAG, "http client is locked cant upload");
			return null;
		} 
	}*/
	
	
}
