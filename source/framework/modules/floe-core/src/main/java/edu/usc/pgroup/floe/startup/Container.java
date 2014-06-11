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
package edu.usc.pgroup.floe.startup;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import edu.usc.pgroup.floe.impl.FloeRuntimeEnvironment;
import edu.usc.pgroup.floe.util.Constants;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <class>Container</class> Responsible for startup of a Container.
 * This will be used by the startup scripts to start up a Container instance
 */
public class Container {

    private static Logger log = Logger.getLogger(Container.class.getName());


    /**
     * To start up a Container instance use this main Class
     *
     * @param args arg[0] container.properties file path
     */
    public static void main(String[] args) {


        if (args.length == 1) {

            Properties instanceTypes = new Properties();
            try {
                instanceTypes.load(new FileInputStream(new File(args[0])));
            } catch (IOException e) {
                String msg = "Error while reading Instance Type info from file : " + e;
                handleException(msg);
            }

            String managerHost = (String) instanceTypes.get(Constants.MANAGER_HOST);
            int managerPort = Integer.parseInt((String) instanceTypes.get(Constants.MANAGER_PORT));
            String depClass = instanceTypes.getProperty(Constants.DEPLOYER_CLASS);


            String coordinatorHost = (String) instanceTypes.get(Constants.COORDINATOR_HOST);
            int coordinatorPort = Integer.parseInt((String) instanceTypes.get(Constants.COORDINATOR_PORT));


            FloeRuntimeEnvironment.getEnvironment().setSystemConfig(instanceTypes);
            edu.usc.pgroup.floe.api.framework.Container container = FloeRuntimeEnvironment.getEnvironment().getContainer();
            container.setManager(managerHost, managerPort);
            DefaultClientConfig config = new DefaultClientConfig();
            config.getProperties().put(ClientConfig.FEATURE_DISABLE_XML_SECURITY,
                    true);
            config.getFeatures().put(ClientConfig.FEATURE_DISABLE_XML_SECURITY, true);

            Client c = Client.create(config);

            if (managerHost == null || managerPort == 0) {
                handleException("Manager Host / Port have to be configured in " + args[0]);
            }

            edu.usc.pgroup.floe.util.Logger.setContainerId(container.getContainerInfo().getContainerId());
            edu.usc.pgroup.floe.util.Logger.getInstance();
            WebResource r = c.resource("http://" + managerHost + ":" + managerPort + "/Manager/addContainerInfo/Container=" +
                    container.getContainerInfo().getContainerId() + "/Host=" + container.getContainerInfo().getContainerHost());
            c.getProperties().put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS, true);
            ClientResponse response;
            response = r.get(ClientResponse.class);
            log.log(Level.INFO, "Container started : " + response.toString());
        } else if(args.length == 3) {
            Properties instanceTypes = new Properties();
            try {
                instanceTypes.load(new FileInputStream(new File(args[0])));
            } catch (IOException e) {
                String msg = "Error while reading Instance Type info from file : " + e;
                handleException(msg);
            }

            String managerHost = args[1].trim();
            int managerPort = Integer.parseInt((String) instanceTypes.get(Constants.MANAGER_PORT));
            String depClass = instanceTypes.getProperty(Constants.DEPLOYER_CLASS);


            String coordinatorHost = args[2].trim();
            int coordinatorPort = Integer.parseInt((String) instanceTypes.get(Constants.COORDINATOR_PORT));

            instanceTypes.setProperty(Constants.COORDINATOR_HOST,coordinatorHost);
            instanceTypes.setProperty(Constants.COORDINATOR_PORT,"" + coordinatorPort);

            FloeRuntimeEnvironment.getEnvironment().setSystemConfig(instanceTypes);
            edu.usc.pgroup.floe.api.framework.Container container = FloeRuntimeEnvironment.getEnvironment().getContainer();
            container.setManager(managerHost, managerPort);

            DefaultClientConfig config = new DefaultClientConfig();
            config.getProperties().put(ClientConfig.FEATURE_DISABLE_XML_SECURITY,
                    true);
            config.getFeatures().put(ClientConfig.FEATURE_DISABLE_XML_SECURITY, true);

            Client c = Client.create(config);

            if (managerHost == null || managerPort == 0) {
                handleException("Manager Host / Port have to be configured in " + args[0]);
            }

            WebResource r = c.resource("http://" + managerHost + ":" + managerPort + "/Manager/addContainerInfo/Container=" +
                    container.getContainerInfo().getContainerId() + "/Host=" + container.getContainerInfo().getContainerHost());
            c.getProperties().put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS, true);
            ClientResponse response;
            response = r.get(ClientResponse.class);
            log.log(Level.INFO, "Container started : " + response.toString());
        }

    }

    private static void handleException(String message) {
        log.log(Level.SEVERE, message);
        throw new RuntimeException(message);
    }
}



