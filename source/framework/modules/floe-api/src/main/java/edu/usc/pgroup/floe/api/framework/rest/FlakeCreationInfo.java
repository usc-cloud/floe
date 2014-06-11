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

package edu.usc.pgroup.floe.api.framework.rest;

/***
 *
 * @author Sreedhar Natarajan (sreedhan@usc.edu)
 * @author Yogesh Simmhan (simmhan@usc.edu)
 * @version v0.1, 2012-06-03
 *
 */
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import edu.usc.pgroup.floe.api.framework.floegraph.Node.Port;
import edu.usc.pgroup.floe.api.framework.floegraph.Node.ResourceInfo;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "RestFlakeCreationInfo")
@XmlType(propOrder = { "nodeID","pelletType","resource","inputPorts","outputPorts","scalingOut","singleton"})
public class FlakeCreationInfo
{
    @XmlElement(name = "nodeID")
    String nodeID;
    @XmlElement(name = "pelletType")
    String pelletType;
    @XmlElement(name = "resource")
    ResourceInfo resource;
    @XmlElement(name = "inputPorts")
    List<Port> inputPorts;
    @XmlElement(name = "outputPorts")
    List<Port> outputPorts;
    @XmlElement(name = "scalingOut")
    boolean scalingOut = false;

    @XmlElement(name = "singleton")
    boolean singleton = false;


    public void setNodeID(String nodeID) {
		this.nodeID = nodeID;
	}
    public String getNodeID() {
		return nodeID;
	}
    
    public String getPellet()
    {
        return this.pelletType;
    }
    
    public ResourceInfo getResource()
    {
        return this.resource;
    }
    
    public void setPellet(String pelletType)
    {
        this.pelletType = pelletType;
    }
    
    public void setResource(ResourceInfo inpResource)
    {
        this.resource = inpResource;
    }
    
    
    public List<Port> getInputPorts()
    {
        return this.inputPorts;
    }
    public List<Port> getOutputPorts()
    {
        return this.outputPorts;
    }
    public void setInputPorts(List<Port> inpPorts)
    {
        this.inputPorts = inpPorts;
    }
    public void setOutputPorts(List<Port> outPorts)
    {
        this.outputPorts = outPorts;
    }

    public boolean isScalingOut() {
        return scalingOut;
    }

    public void setScalingOut(boolean scalingOut) {
        this.scalingOut = scalingOut;
    }

    public boolean isSingleton() {
        return singleton;
    }

    public void setSingleton(boolean singleton) {
        this.singleton = singleton;
    }
}
