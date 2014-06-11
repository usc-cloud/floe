package edu.usc.pgroup.floe.util;

import java.net.URI;

import javax.xml.bind.JAXBElement;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;

import com.sun.jersey.api.client.config.DefaultClientConfig;
import edu.usc.pgroup.floe.api.framework.rest.RestContainerResourceInfo;

public class RestClient {
	
	private static Client client = null;
	static{
        DefaultClientConfig config = new DefaultClientConfig();
        config.getProperties().put(ClientConfig.FEATURE_DISABLE_XML_SECURITY,
                true);
        config.getFeatures().put(ClientConfig.FEATURE_DISABLE_XML_SECURITY, true);
        client = Client.create(config);
        client.getProperties().put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS, true);
	}
	
	public static <T> T get(URI uri)
	{	
		WebResource webRes = client.resource(uri);	
		ClientResponse response = webRes.get(ClientResponse.class);
		GenericType<T> genericType = new GenericType<T>() {};
		T ret = response.getEntity(genericType);
		
		return ret;
	}
	
	public static <T> T post(URI uri, Object o)
	{
		WebResource webRes = client.resource(uri);	
		ClientResponse response = webRes.post(ClientResponse.class, o);
		GenericType<T> genericType = new GenericType<T>() {};
		T ret = response.getEntity(genericType);
		
		return ret;
	}
	
	public static <T> T put(URI uri, Object o)
	{
		WebResource webRes = client.resource(uri);	
		ClientResponse response = webRes.put(ClientResponse.class, o);
		GenericType<T> genericType = new GenericType<T>() {};
		T ret = response.getEntity(genericType);
		
		return ret;
	}
}
