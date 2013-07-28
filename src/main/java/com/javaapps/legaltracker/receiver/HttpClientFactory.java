package com.javaapps.legaltracker.receiver;

import org.apache.http.client.HttpClient;

public interface HttpClientFactory {

	public abstract HttpClient createHttpClient();

}