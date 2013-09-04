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
package edu.usc.pgroup.floe.api.framework.healthmanager;

public class HeathInfo {

    String flakeId;

    double cpuUsage;

    double latency;

    int sourceQueueLength;

    int sinkQueueLength;

    int coreCount;

    double inThroughPut;

    public HeathInfo(String flakeId,double cpuUsage,double latency,int sourceQueueLength,int sinkQueueLength,
                     int coreCount , double inThroughPut) {
        this.cpuUsage =cpuUsage;
        this.latency = latency;
        this.sourceQueueLength = sourceQueueLength;
        this.sinkQueueLength  = sinkQueueLength;
        this.flakeId = flakeId;
        this.coreCount = coreCount;
        this.inThroughPut = inThroughPut;
    }

    public double getCpuUsage() {
        return cpuUsage;
    }

    public double getLatency() {
        return latency;
    }

    public int getSourceQueueLength() {
        return sourceQueueLength;
    }

    public int getSinkQueueLength() {
        return sinkQueueLength;
    }

    public String getFlakeId() {
        return flakeId;
    }

    public int getCoreCount() {
        return coreCount;
    }

    public double getInThroughPut() {
        return inThroughPut;
    }
}
