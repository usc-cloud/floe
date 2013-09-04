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
package edu.usc.pgroup.floe.applications.iipipeline.client;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import edu.usc.pgroup.floe.api.communication.TransportInfoBase;
import edu.usc.pgroup.floe.api.framework.StartFloeInfo;
import edu.usc.pgroup.floe.api.framework.floegraph.FloeGraph;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.util.List;

public class GraphStart {
    public static void main(String[] args)
    {
        try
        {
            //Get The Node List From File
            JAXBContext ctx = JAXBContext.newInstance(FloeGraph.class);
            Unmarshaller um = ctx.createUnmarshaller();
            FloeGraph fg = (FloeGraph) um.unmarshal(new StreamSource(
                    new File("appgraph\\application2.xml")));

            DefaultClientConfig config = new DefaultClientConfig();
            config.getProperties().put(ClientConfig.FEATURE_DISABLE_XML_SECURITY,
                    true);
            config.getFeatures().put(ClientConfig.FEATURE_DISABLE_XML_SECURITY, true);


            Client c = Client.create(config);
            WebResource r = c.resource("http://localhost:45000/Coordinator/createFloe");
           // WebResource r = c.resource("http://localhost:45000/Coordinator/createFloe");
            c.getProperties().put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS, true);
            ClientResponse response ;
            c.getProperties().put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS, true);
            response = r.post(ClientResponse.class, fg);
            StartFloeInfo startFloeInfo = response.getEntity(StartFloeInfo.class);

            System.out.println("Floe id : " + startFloeInfo.getFloeID());
            for(List<TransportInfoBase> b : startFloeInfo.getSourceInfo().sourceNodeTransport.values()) {
                for(TransportInfoBase base : b) {
                    System.out.println("Channel Info");
                    base.printConnectionInfoDetails();
                    System.out.println("Control Channel info");
                    base.getControlChannelInfo().printConnectionInfoDetails();
                }
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }

    }
}
