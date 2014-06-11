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

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Edge")
@XmlType(propOrder = { "channelBehaviourType","channelTransportType","source","sink","fiberStratergy"})
public class Edge {

    String channelBehaviourType;

    String channelTransportType;

    @XmlElement(name = "source")
    Source source;

    @XmlElement(name = "sink")
    Sink sink;

    String fiberStratergy;


    public String getChannelBehaviourType() {
        return channelBehaviourType;
    }

    public void setChannelBehaviourType(String channelBehaviourType) {
        this.channelBehaviourType = channelBehaviourType;
    }

    public String getChannelTransportType() {
        return channelTransportType;
    }

    public void setChannelTransportType(String channelTransportType) {
        this.channelTransportType = channelTransportType;
    }


    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    public Sink getSink() {
        return sink;
    }

    public void setSink(Sink sink) {
        this.sink = sink;
    }


    public String getFiberStratergy() {
        return fiberStratergy;
    }

    public void setFiberStratergy(String fiberStratergy) {
        this.fiberStratergy = fiberStratergy;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlRootElement(name = "source")
    @XmlType(propOrder = { "nodeId","port"})
    public static class Source {

        String nodeId;

        String port;

        public String getNodeId() {
            return nodeId;
        }

        public void setNodeId(String nodeId) {
            this.nodeId = nodeId;
        }

        public String getPort() {
            return port;
        }

        public void setPort(String port) {
            this.port = port;
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlRootElement(name = "sink")
    @XmlType(propOrder = { "nodeId","port"})
    public static class Sink {

        String nodeId;

        String port;

        public String getNodeId() {
            return nodeId;
        }

        public void setNodeId(String nodeId) {
            this.nodeId = nodeId;
        }

        public String getPort() {
            return port;
        }

        public void setPort(String port) {
            this.port = port;
        }

    }
}
