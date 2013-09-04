package org.soyatec.windowsazure.internal.util;

import org.apache.http.client.HttpClient;

/**
 * Listener for HttpClient, which will be notified just after the HttpClient
 * instance is created and before any request is sent.
 * 
 */
public interface IHttpClientListener {
	
	/**
	 * The method will be invoked after the HttpClient instance is created.
	 * 
	 * @param client
	 */
	abstract void onCreate(HttpClient client);
}
