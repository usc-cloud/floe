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
package edu.usc.pgroup.floe.api.framework.floegraph;


import javax.xml.bind.annotation.*;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "FloeGraph")
@XmlType(propOrder = { "nodes","edges"})
public class FloeGraph {

    @XmlElement(name = "Nodes")
    NodeList nodes;

    @XmlElement(name = "Edges")
    EdgeList edges;

    public NodeList getNodes() {
        return nodes;
    }

    public void setNodes(NodeList nodes) {
        this.nodes = nodes;
    }

    public EdgeList getEdges() {
        return edges;
    }

    public void setEdges(EdgeList edges) {
        this.edges = edges;
    }
}
