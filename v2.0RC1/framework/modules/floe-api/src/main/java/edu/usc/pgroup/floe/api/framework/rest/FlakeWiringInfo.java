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
 * This is the Coordinator Implementation  
 *
 */
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import edu.usc.pgroup.floe.api.communication.TransportInfoBase;
import edu.usc.pgroup.floe.api.framework.floegraph.Node.Port;

@XmlRootElement(name = "RestFlakeWiringInfo")
@XmlType(propOrder = {"flakeID", "selfConnectionPort","otherEndConnectionPort"})
public class FlakeWiringInfo {
    String flakeID;
    Port selfConnectionPort;
    Port otherEndConnectionPort;

    public void setFlakeID(String flakeID) {
		this.flakeID = flakeID;
	}
    public String getFlakeID() {
		return flakeID;
	}
    public Port getSelfConnectionPort() {
		return selfConnectionPort;
	}
    public void setSelfConnectionPort(Port selfConnectionPort) {
		this.selfConnectionPort = selfConnectionPort;
	}
    
    public Port getOtherEndConnectionPort() {
		return otherEndConnectionPort;
	}
    public void setOtherEndConnectionPort(Port otherEndConnectionPort) {
		this.otherEndConnectionPort = otherEndConnectionPort;
	}
}
