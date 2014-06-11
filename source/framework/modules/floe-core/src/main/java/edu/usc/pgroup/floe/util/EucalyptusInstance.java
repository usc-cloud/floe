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

import edu.usc.pgroup.floe.api.framework.floegraph.Node.ResourceInfo;



public class EucalyptusInstance {

    String instanceType;
    ResourceInfo instanceResource;
    String instanceState;

    public EucalyptusInstance() {

    }

    public EucalyptusInstance(String insType, ResourceInfo inpRes) {
        this.instanceType = insType;
        this.instanceResource = inpRes;
    }

    public String getInstanceType() {
        return this.instanceType;
    }

    public ResourceInfo getResourceInfo() {
        return this.instanceResource;
    }

    public void setInstanceType(String instanceType) {
        this.instanceType = instanceType;
    }

    public void setInstanceType(ResourceInfo instanceResource) {
        this.instanceResource = instanceResource;
    }

    public String getInstanceState() {
        return this.instanceState;
    }

    public void setInstanceState(String instanceState) {
        this.instanceState = instanceState;
    }
}
