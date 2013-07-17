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


//        if(args.length !=2) {
//            handleException("Invalid arguments : use arg[0] = InstanceType Info properties file path arg[1] = Eucalyptus\n" +
//                    "     *              credentials properties file");
//
//        }
//
//        List<EucalyptusInstance> tempList = new ArrayList<EucalyptusInstance>();
//
//        Properties instanceTypes = new Properties();
//        try {
//            instanceTypes.load(new FileInputStream(new File(args[0])));
//        } catch (IOException e) {
//            String msg = "Error while reading Instance Type info from file : " + e;
//            handleException(msg);
//        }
//
//
//
//        for (Iterator keySet = instanceTypes.keySet().iterator();keySet.hasNext();) {
//            String key = (String) keySet.next();
//            EucalyptusInstance instance = new EucalyptusInstance();
//            instance.setInstanceType(key);
//            instance.setInstanceType(new ResourceInfo(Integer.parseInt((String) instanceTypes.get(key))));
//            tempList.add(instance);
//        }
//        InputStream credentialsAsStream;
//        try
//        {
//
//            credentialsAsStream = new FileInputStream(args[1]);
//            ResourceManagerImpl newManager = ResourceManagerImpl.getManager();
//        } catch (Exception e1) {
//            String msg = " Error while starting up Manager : " + e1.getMessage();
//            handleException(msg);
//        }*/


        if (args.length == 1) {
            Properties properties = new Properties();
            properties.load(new FileReader(args[0]));

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
