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
package edu.usc.pgroup.floe.applications.bsp.client;

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

//        ***********************************  ********************************
    	String flakeHost = "localhost";
    	String flakeID = "Flake@192.168.1.65@3@dfa789ce-36bc-494e-9784-d141c6b2293f";

    	ResourceInfo resource = new ResourceInfo();
    	resource.setNumberOfCores(1);

    	try{
			WebResource webRes = client.resource("http://" + flakeHost + ":45002/Container/scaleOut/FlakeID="+flakeID);
	        client.getProperties().put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS, true);

	         webRes.post(ClientResponse.class, resource);
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    }
}
