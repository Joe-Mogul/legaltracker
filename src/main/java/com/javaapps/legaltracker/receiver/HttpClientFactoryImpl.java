package com.javaapps.legaltracker.receiver;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

public class HttpClientFactoryImpl implements HttpClientFactory {

	/* (non-Javadoc)
	 * @see com.javaapps.legaltracker.receiver.HttpClientFactory#createHttpClient()
	 */
	@Override
	public HttpClient createHttpClient(){
		return new DefaultHttpClient();
	}
}
