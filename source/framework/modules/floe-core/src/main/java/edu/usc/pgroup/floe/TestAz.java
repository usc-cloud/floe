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
package edu.usc.pgroup.floe;

import org.soyatec.windowsazure.management.AsyncResultCallback;
import org.soyatec.windowsazure.management.ServiceManagement;
import org.soyatec.windowsazure.management.ServiceManagementRest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.Semaphore;

public class TestAz {
    public static void main(String[] args) throws Exception {
        Properties properties = new Properties();
        try {
            //properties.load(new FileInputStream("conf"+ File.separator + "azure.properties"));
            properties.load(new FileInputStream("C:\\Users\\charith\\Documents\\Projects\\USC\\floe\\trunk\\framework\\modules\\distribution\\manager\\src\\main\\conf\\azure.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        String subscriptionId = "c9d4dfa0-b0c2-4836-81ef-4ccff4e4c816";
        String keyStoreFile = "resources/floe.keystore";
        String keyStorePassword = "cwickram";
        String trustStoreFile = "resources/floe.trustcacerts";
        String trustStorePassword = "cwickram";
        String certificateAlias = "amk";
        String imageName = "floe-test1";
        String managerHost = properties.getProperty("managerHost");
        String coordinatorHost = properties.getProperty("coordinatorHost");


        if (subscriptionId == null || keyStoreFile == null || keyStorePassword == null ||
                trustStoreFile == null || trustStorePassword == null || certificateAlias == null) {
            throw new RuntimeException("Error in azure configuration " + "conf" + File.separator + "azure.properties");
        }

        try {
            final ServiceManagement management = new ServiceManagementRest(subscriptionId, keyStoreFile, keyStorePassword,
                    trustStoreFile, trustStorePassword, certificateAlias);

            management.createHostedService("floeContainer", "Floelabel11", "newFloedescription",
                    "West US", null);

            final Semaphore blocker = new Semaphore(2);
            blocker.acquire(2);
            management.createLinuxVirtualMachineDeployment("floeContainer", "floe-setup",
                    "Floelabel2", "floe-role-cont", "floecont", "cwickram", "test@1234",
                    "", imageName, "Small", new AsyncResultCallback() {
                @Override
                public void onSuccess(Object result) {
                    System.out.println("CREATED : " + result);
                    blocker.release();
                    management.startVMRole("floeContainer", "floe-setup", "floe-role-cont", new AsyncResultCallback() {
                        @Override
                        public void onSuccess(Object result) {
                            System.out.println("STARTED : " + result);
                            blocker.release(2);
                        }

                        @Override
                        public void onError(Object result) {
                            System.out.println("Error while Starting VM : " + result);
                            blocker.release(2);
                        }
                    });
                }

                @Override
                public void onError(Object result) {
                    System.out.println("Error while Creating VM : " + result);
                    blocker.release(2);
                }
            });

            blocker.acquire(2);

            System.out.println("released....");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
