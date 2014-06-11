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

package edu.usc.pgroup.floe.api.framework;

/***
 * This keeps track of the Information about the Floe Graph, Node,Edges and the Wiring Map Information
 * 
 * @author Sreedhar Natarajan (sreedhan@usc.edu)
 * @author Yogesh Simmhan (simmhan@usc.edu)
 * @version v0.1, 2012-01-03
 *
 */
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import edu.usc.pgroup.floe.api.communication.TransportInfoBase;
import edu.usc.pgroup.floe.api.framework.floegraph.Edge;
import edu.usc.pgroup.floe.api.framework.floegraph.Node;


public class FloeInfo 
{
	String floeID;
	
	// Keep Track of the NodeID and FlakeID Information
	Map<String,FlakeInfo> nodeFlakeMap;
	// DAG Ordering of the Floe
	
	public FloeInfo()
	{		
	
	}
	public String getFloeID()
	{
		return this.floeID;
	}
	public void setFloeID(String inpFloe)
	{
		this.floeID = inpFloe;
	}
	
	public Map<String,FlakeInfo> getNodeFlakeMap()
	{
		return this.nodeFlakeMap;
	}
	
	public void setNodeFlakeMap(Map<String, FlakeInfo> nodeFlakeMap) {
		this.nodeFlakeMap = nodeFlakeMap;
	}
}
