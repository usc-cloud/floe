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
package edu.usc.pgroup.floe.impl.deployers;

import edu.usc.pgroup.floe.api.framework.Container;
import edu.usc.pgroup.floe.api.framework.Deployer;
import edu.usc.pgroup.floe.util.PalletJarUtils;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;

import java.io.File;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PalletJarDeployer implements Deployer<File> {

    private Container container;
    public static String FILE_DIR_PATH = "pelletJars";

    private FileAlterationObserver observer;
    private FileAlterationMonitor monitor;
    private Map<String,String> deployedPalletMap =  new ConcurrentHashMap<String,String>();

    @Override
    public void init(Container container) {
        this.container = container;
        this.observer = new FileAlterationObserver(FILE_DIR_PATH);
        this.observer.addListener(new PalletListener());

        this.monitor = new FileAlterationMonitor(1000);
        monitor.addObserver(observer);
        try {
            monitor.start();
        } catch (Exception e) {
            throw new RuntimeException("Error while initialising the ");
        }
    }

    @Override
    public void onUpdate(File artifact) {
        System.out.println("Updating Flakes..");

        Properties properties = PalletJarUtils.getPalletInfo(artifact);
        String pallets = properties.getProperty(PalletJarUtils.PALLET_CLASSES);
        String[] palletClasses= pallets.split(",");

        for(String p : palletClasses) {
            String flakeId = deployedPalletMap.get(p);
            container.pauseFlake(flakeId);
            container.resumeFlake(flakeId);
        }


    }

    @Override
    public void onCreate(File artifact) {
        //TODO
    }

    @Override
    public void onDelete(File artifact) {
        //TODO
    }

    @Override
    public void addFake(String palletType, String flakeId) {
        this.deployedPalletMap.put(palletType,flakeId);
    }

    @Override
    public void removeFlake(String palletType) {

        if(deployedPalletMap.containsKey(palletType)) {
            deployedPalletMap.remove(palletType);
        }
    }

    private class PalletListener implements FileAlterationListener {

        Logger logger = Logger.getLogger(this.getClass().getName());
        @Override
        public void onStart(FileAlterationObserver fileAlterationObserver) {

        }

        @Override
        public void onDirectoryCreate(File file) {

        }

        @Override
        public void onDirectoryChange(File file) {

        }

        @Override
        public void onDirectoryDelete(File file) {

        }

        @Override
        public void onFileCreate(File file) {
            logger.log(Level.INFO,"Jar deployed , JAR :" + file.getName() );
            onUpdate(file);
        }

        @Override
        public void onFileChange(File file) {
            logger.log(Level.INFO,"Jar Updated , JAR : " + file.getName());
            onUpdate(file);
        }

        @Override
        public void onFileDelete(File file) {
            onDelete(file);
        }

        @Override
        public void onStop(FileAlterationObserver fileAltertionObserver) {

        }
    }
}