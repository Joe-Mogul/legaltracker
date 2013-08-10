package com.javaapps.legaltracker.upload;

import org.apache.http.client.HttpClient;

public interface HttpClientFactory {

	public abstract HttpClient getHttpClient();

}