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
package edu.usc.pgroup.floe.impl.rest;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.container.httpserver.HttpServerFactory;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RestService {

    private ResourceConfig resourceConfig;
    private HttpServer httpServer;

    private static Logger logger = Logger.getLogger(RestService.class.getName());

    public RestService(String resourceName,URI contextRootUrl) {

        try {
            this.resourceConfig = new PackagesResourceConfig(resourceName);

            resourceConfig.getFeatures().put(ClientConfig.FEATURE_DISABLE_XML_SECURITY, true);

            resourceConfig.getProperties().put(ClientConfig.FEATURE_DISABLE_XML_SECURITY,"true");
            this.httpServer = HttpServerFactory.create(contextRootUrl,resourceConfig);

        } catch (IOException e) {
            String msg = "Error while starting Rest service for coordinator";
            handleException(msg,e);
        }

    }

    public void start() {
        httpServer.start();
        logger.info("Rest service for Container is started at : " + httpServer.getAddress());
    }

    public void stop() {
        httpServer.stop(0);
        logger.info("Container Rest service stopped");
    }


    private void handleException(String msg,Exception e) {
        logger.log(Level.SEVERE,msg,e);
    }
}
