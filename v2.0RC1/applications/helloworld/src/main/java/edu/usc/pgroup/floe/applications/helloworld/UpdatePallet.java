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

public class UpdatePallet {

    public static void main(String[] args) {
//        Client c = Client.create();
//        WebResource r = c.resource("http://localhost:45002/Container/updateSingleFlake/FlakeID=1/palletType=edu.usc.pgroup.floe.applications.helloworld.pellets.MessagePellet");
//        c.getProperties().put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS, true);
//        ClientResponse response ;
//        c.getProperties().put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS, true);
//        r.put();


        Client c = Client.create();
        WebResource r = c.resource("http://localhost:45000/Coordinator/updateFlakes/currentType=edu.usc.pgroup.floe.applications.helloworld.pellets.MessagePellet/newType=edu.usc.pgroup.floe.applications.helloworld.pellets.MessagePellet");
        c.getProperties().put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS, true);
        ClientResponse response ;
        c.getProperties().put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS, true);
        r.put();
    }
}
