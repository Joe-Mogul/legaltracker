package com.javaapps.legaltracker.upload;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

import com.javaapps.legaltracker.pojos.Config;
import com.javaapps.legaltracker.pojos.Constants;

import android.util.Log;

public class HttpClientFactoryImpl implements HttpClientFactory {

	private HttpClient httpClient;

	private ReentrantLock lock = new ReentrantLock();

	@Override
	public HttpClient getHttpClient() {
		try {
			if (lock.tryLock(Config.getInstance().getHttpTimeout(),
					TimeUnit.MILLISECONDS)) {
				if (httpClient == null) {
					httpClient = new DefaultHttpClient();
				}
				return httpClient;
			} else {
				return null;
			}
		} catch (InterruptedException ex) {
			Log.i(Constants.LEGAL_TRACKER_TAG, "http client is locked cant upload");
			return null;
		} finally {
			lock.unlock();
		}
	}
}
