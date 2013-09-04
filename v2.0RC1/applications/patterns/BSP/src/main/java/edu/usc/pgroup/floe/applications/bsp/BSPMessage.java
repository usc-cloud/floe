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
package edu.usc.pgroup.floe.applications.bsp;

import java.io.Serializable;

public class BSPMessage implements Serializable {

    public static final int DATA=1;

    public static final int CTRL=2;

    private int type;

    private byte[]data;

    private int superStep;

    private String key;

    private boolean voteToHalt = false;

    public boolean isVoteToHalt() {
        return voteToHalt;
    }

    public void setVoteToHalt(boolean voteToHalt) {
        this.voteToHalt = voteToHalt;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public int getSuperStep() {
        return superStep;
    }

    public void setSuperStep(int superStep) {
        this.superStep = superStep;
    }
}
