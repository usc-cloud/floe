package edu.usc.pgroup.floe.api.framework.rest;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import edu.usc.pgroup.floe.api.framework.NeighborInfo;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Nodes")
@XmlType(propOrder = { "nodeList" })
public class RestNeighborList {
    @XmlElement(name = "Node")
    List<NeighborInfo> nodeList;

    public List<NeighborInfo> getNodeList() {
        return this.nodeList;
    }

    public void setNodeList(List<NeighborInfo> inpList) {
        this.nodeList = inpList;
    }
}