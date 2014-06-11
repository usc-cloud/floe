package edu.usc.pgroup.floe.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public class Logger {
    private static Logger instance;

    private static String containerId;
    private BufferedWriter lout;
    private FileWriter writer;

    public static final String QUEUE_SIZE = "QUEUE_SIZE";
    public static final String MESSAGE_LATENCY = "MESSAGE_LATENCY";


    public static Logger getInstance() {
        if (instance == null) {
            instance = new Logger();
        }
        return instance;
    }

    public static void setContainerId(String id) {
        containerId = id;
    }

    private Logger() {
        try {
            writer = new FileWriter("healthLogs_" + containerId + ".csv", true);
            lout = new BufferedWriter(writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public synchronized void LogInfo(String cid, String fid, Date timeStamp, double latency, double cpu, int inpBufferLength, int outBufferLength, int allocatedCores, int allocatedPellets) {
        try {
            lout.write("container_status," + cid + "," +
                    fid + "," +
                    timeStamp + "," +
                    latency + "," +
                    cpu + "," +
                    inpBufferLength + "," +
                    outBufferLength + "," +
                    allocatedCores + "," +
                    allocatedPellets +
                    "\n");
            lout.flush();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public synchronized void LogInfo(String infoType, String cid, String fid, Date timeStamp, double latency, double cpu,
                                     int inpBufferLength, int outBufferLength, int allocatedCores, int allocatedPellets,
                                     double inThoughput, double outThroughput) {
        long currentTime = System.currentTimeMillis();
        try {
            lout.write(infoType + "," + cid + "," +
                    fid + "," +
                    timeStamp + "," +
                    latency + "," +
                    cpu + "," +
                    inpBufferLength + "," +
                    outBufferLength + "," +
                    allocatedCores + "," +
                    allocatedPellets + "," +
                    inThoughput + "," +
                    outThroughput + "," +
                    currentTime +
                    "\n");
            lout.flush();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    public synchronized void LogAction(String action, String fid, Date timeStamp, int resourceCount,
                                       double latency, int inBuffer, double inRate) {

        try {
            lout.write("Action" + "," +
                    action + "," +
                    fid + "," +
                    timeStamp + "," +
                    resourceCount + "," +
                    latency + "," +
                    inBuffer + "," +
                    inRate + "," +
                    System.currentTimeMillis() + "\n"
            );
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public synchronized void LogInfo(String data) {
        try {
            lout.write(data + "\n");
            lout.flush();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
