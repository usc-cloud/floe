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
package edu.usc.pgroup.floe.applications.pipeline;

import edu.usc.pgroup.floe.api.framework.pelletmodels.Pellet;
import edu.usc.pgroup.floe.api.state.StateObject;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Random;

public class IOPellet implements Pellet {
    //private static final String file = "C:\\Users\\charith\\Desktop\\Floe-results\\random-walk-1msg\\5\\healthLogs_null.csv";
    private static final String file = "my_file.csv";

    private static final long MB_10 = 10485760*3;
    @Override
    public Object invoke(Object o, StateObject stateObject) {

        String filePostFix = "_" + System.currentTimeMillis();

        try {
            writeFile(filePostFix);
            String d = readFile(filePostFix).substring(0,100);
            deleteFile(filePostFix);
            System.out.println(d);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String data = "";
        for(int i = 0 ; i < 20;i++) {
            data +=  "|" + "This is a test message" ;
        }
        data = "|" + data;

        return data;
    }

    private static void writeFile(String pp) throws Exception {
        Random r = new Random();

        File f = new File(file+pp);
        PrintWriter writer = new PrintWriter(f);
        for( int j=0;;j++) {

            StringBuffer buffer = new StringBuffer();
            for(int i = 0; i < 400;i++) {

                char c = (char)(r.nextInt(26) + 'a');
                buffer.append(c);
            }
           // System.out.println(buffer.toString());
            if(j%3912 == 0) {
                Thread.sleep(100);
                System.out.println("j :" + j);
            }
            writer.println(buffer.toString());
            writer.flush();
            if(f.length() > MB_10) {
                break;
            }
        }

        writer.close();

    }


    private static void deleteFile(String pp) throws Exception {
        File f = new File(file+pp);
        f.delete();

    }
    private static String readFile(String pp) throws Exception {

        BufferedReader reader = new BufferedReader(new FileReader(file+pp));

        StringBuffer line = new StringBuffer();
        while (true) {
            String data = reader.readLine();
            if (data == null) {
                break;
            }

            line.append(data + "\n");

        }

        reader.close();
        return line.toString();
    }

    public static void main(String[] args) throws Exception {

       for(int i = 0 ; i < 1; i++) {
        long s = System.currentTimeMillis();
        writeFile("test");
        String d = readFile("test");
       // System.out.println("d = " + d);
        deleteFile("test");
        System.out.println("Time : " + (System.currentTimeMillis() - s));
        Thread.sleep(500);
       }
    }


}
