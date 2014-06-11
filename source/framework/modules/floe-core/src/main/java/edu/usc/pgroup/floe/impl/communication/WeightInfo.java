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
package edu.usc.pgroup.floe.impl.communication;

import java.io.Serializable;

public class WeightInfo implements Serializable {

    String nodeId;

    int bufferLength;

    double latency;

    int pelletCount;

    String flakeId;

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public int getBufferLength() {
        return bufferLength;
    }

    public void setBufferLength(int bufferLength) {
        this.bufferLength = bufferLength;
    }

    public String getFlakeId() {
        return flakeId;
    }

    public void setFlakeId(String flakeId) {
        this.flakeId = flakeId;
    }

    public double getLatency() {
        return latency;
    }

    public void setLatency(double latency) {
        this.latency = latency;
    }

    public int getPelletCount() {
        return pelletCount;
    }

    public void setPelletCount(int pelletCount) {
        this.pelletCount = pelletCount;
    }
}
