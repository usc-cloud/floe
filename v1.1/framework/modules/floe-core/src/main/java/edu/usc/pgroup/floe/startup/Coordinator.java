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

import edu.usc.pgroup.floe.impl.CoordinatorImpl;
import edu.usc.pgroup.floe.impl.FloeRuntimeEnvironment;

import java.io.FileReader;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Coordinator {

    public static Logger log = Logger.getLogger(Coordinator.class.getName());

    public static void main(String[] args) {
        try {
            if (args.length == 1) {
                Properties properties = new Properties();
                properties.load(new FileReader(args[0]));

                FloeRuntimeEnvironment environment = FloeRuntimeEnvironment.getEnvironment();
                environment.setSystemConfig(properties);

                CoordinatorImpl coordinator = CoordinatorImpl.getCoordinator();
            }
        } catch (Exception e) {
            String msg = "Error while starting coordinator : " + e.getMessage();
            handleException(msg, e);
        }

    }


    private static void handleException(String message, Exception e) {
        log.log(Level.SEVERE, message);
        throw new RuntimeException(message);
    }
}
