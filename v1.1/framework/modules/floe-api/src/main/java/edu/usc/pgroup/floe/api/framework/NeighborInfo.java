package edu.usc.pgroup.floe.api.framework;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;



@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "NeighborInfo")
@XmlType(name = "hostname", propOrder = {"hostName"})
public class NeighborInfo {
    String hostName;

    public void sethostName(String hostName) {
        this.hostName = hostName;
    }

    public String getHostName() {
        return this.hostName;
    }
}