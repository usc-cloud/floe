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
package edu.usc.pgroup.floe.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public class PalletJarUtils {

    public static final String  PALLET_CLASSES="pallet_classes";


    public static Properties getPalletInfo(File file) {
        try {

            FileInputStream fin = new FileInputStream(file.getAbsolutePath());
            try {
                ZipInputStream zin = new ZipInputStream(fin);
                try {
                    ZipEntry entry;
                    List<String> classList = new ArrayList<String>();
                    while ((entry = zin.getNextEntry()) != null) {
                        String name = entry.getName();
                        if (name.endsWith(".properties")) {
                            Properties properties = new Properties();
                            properties.load(new ZipFile(file).getInputStream(entry));
                            return properties;
                        }
                    }
                    return null;
                } finally {
                    zin.close();
                }
            } finally {
                fin.close();
            }
        } catch (IOException e) {
            throw new RuntimeException("Error while accessing jar : " + file.getName() + " :msg :  " + e.getMessage() );
        }


    }


    public static List<String> getListOfClasses(File file)  {
        try {
            FileInputStream fin = new FileInputStream(file.getAbsolutePath());
            try {
                ZipInputStream zin = new ZipInputStream(fin);
                try {
                    ZipEntry entry;
                    List<String> classList = new ArrayList<String>();
                    while ((entry = zin.getNextEntry()) != null) {
                        String name = entry.getName();
                        if (name.endsWith(".class")) {
                            classList.add(getClassNameFromResourceName(name));
                        }
                    }
                    return classList;
                } finally {
                    zin.close();
                }
            } finally {
                fin.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String getFileExtension(String fileName) {
        int index = fileName.lastIndexOf('.');
        return fileName.substring(index + 1);
    }


    public static String getClassNameFromResourceName(String resourceName) {
        if (!resourceName.endsWith(".class")) {
            throw new IllegalArgumentException("The resource name doesn't refer to a class file");
        }
        return resourceName.substring(0, resourceName.length()-6).replace('/', '.');
    }

}
