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

import edu.usc.pgroup.floe.impl.FloeRuntimeEnvironment;
import edu.usc.pgroup.floe.impl.manager.resourceManager.ResourceManagerImpl;
import edu.usc.pgroup.floe.util.Constants;
import edu.usc.pgroup.floe.util.EucalyptusInstance;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Manager {

    private static Logger log = Logger.getLogger(Manager.class.getName());

    public static void main(String[] args) throws IOException {


        if (args.length == 1) {
            Properties properties = new Properties();
            properties.setProperty(Constants.CURRET_HOST,args[0]);

            FloeRuntimeEnvironment environment = FloeRuntimeEnvironment.getEnvironment();
            environment.setSystemConfig(properties);

        }


        ResourceManagerImpl newManager = ResourceManagerImpl.getManager();
    }


    private static void handleException(String message) {
        log.log(Level.SEVERE, message);
        throw new RuntimeException(message);
    }
}
