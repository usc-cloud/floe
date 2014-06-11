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
package edu.usc.pgroup.floe.impl;

import edu.usc.pgroup.floe.api.framework.Container;

import java.util.Properties;

/**
 * Singleton class that Represent the Container Environment.
 * Use this Class to get access to the Container Environment.
 */
public class FloeRuntimeEnvironment {


    private static FloeRuntimeEnvironment environment = new FloeRuntimeEnvironment();

    private Container container;

    private Properties  systemConfig ;


    private FloeRuntimeEnvironment() {

    }

    /**
     * Access the Singleton Container instance
     * @return reference to Container Environment
     */
    public static FloeRuntimeEnvironment getEnvironment() {
        return environment;
    }


    /**
     * Get access to Container
     * @return
     */
    public Container getContainer(){
        if(container == null) {
            synchronized (this) {
                if(container == null) {
                    container = new ContainerImpl();
                }
            }
        }

        return container;
    }

    public void setSystemConfig(Properties properties) {

            this.systemConfig = properties;
    }

    public String getSystemConfigParam(String params){


        if(params != null && systemConfig != null) {
            return  systemConfig.getProperty(params);
        } else {
            return null;
        }
    }
}
