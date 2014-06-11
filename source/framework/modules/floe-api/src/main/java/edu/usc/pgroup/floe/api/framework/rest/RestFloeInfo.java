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


import javax.xml.bind.annotation.*;

import edu.usc.pgroup.floe.api.framework.floegraph.EdgeList;
import edu.usc.pgroup.floe.api.framework.floegraph.NodeList;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "RestFlakeCreationInfo")
@XmlType(propOrder = { "floeID","nodeList","edgeList"})
public class RestFloeInfo
{
    @XmlElement(name = "floeID")
    String floeID;
    @XmlElement(name = "nodeList")
    NodeList nodeList;
    @XmlElement(name = "edgeList")
    EdgeList edgeList;

    public RestFloeInfo()
    {
        this.nodeList = new NodeList();
        this.edgeList = new EdgeList();
    }

    public EdgeList getEdgeList()
    {
        return this.edgeList;
    }
    public NodeList getNodeList()
    {
        return this.nodeList;
    }

    public void setEdge(EdgeList edgeList)
    {
        this.edgeList = edgeList;
    }
    public void setNode(NodeList nodeList)
    {
        this.nodeList = nodeList;
    }
    public String getFloeID()
    {
        return this.floeID;
    }
    public void setFloeID(String floeID)
    {
        this.floeID = floeID;
    }

}
