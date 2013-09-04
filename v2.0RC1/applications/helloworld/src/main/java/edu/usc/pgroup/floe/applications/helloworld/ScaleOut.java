/*
 * Copyright 2011, University of Southern California. All Rights Reserved.
 * 
 * This software is experimental in nature and is provided on an AS-IS basis only. 
 * The University SPECIFICALLY DISCLAIMS ALL WARRANTIES, EXPRESS AND IMPLIED, INCLUDING WITHOUT 
 * LIMITATION ANY WARRANTY AS TO MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.
 * 
 * This software may be reproduced and used for non-commercial purposes only, 
 * so long as this copyright notice is reproduced with each such copy made.
 */
package edu.usc.pgroup.floe.applications.helloworld;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;

import edu.usc.pgroup.floe.api.framework.floegraph.Node.ResourceInfo;

public class ScaleOut {

    public static void main(String[] args) {
//        Client c = Client.create();
//        WebResource r = c.resource("http://localhost:45002/Container/updateFlake/FlakeID=Flake@68.181.16.49@1");
//        c.getProperties().put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS, true);
//        ClientResponse response ;
//        c.getProperties().put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS, true);
//        r.put();


    	Client client  = Client.create();
    	
    	String flakeHost = "localhost";
    	String flakeID = "Flake@128.125.124.144@2@b99dd714-6563-4bc1-a51c-27ecc060e417";
    	
    	ResourceInfo resource = new ResourceInfo();
    	resource.setNumberOfCores(1);
    	
    	try{    		
			WebResource webRes = client.resource("http://" + flakeHost + ":45002/Container/scaleOut/FlakeID="+flakeID);
	        client.getProperties().put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS, true);
	        
	        ClientResponse response = webRes.post(ClientResponse.class, resource);
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    }
}
