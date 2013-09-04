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


import javax.xml.bind.annotation.*;
import java.io.Serializable;

/**
 * <class>PalletVersionInfo</class> Use to represent the State information related to pallet versions
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "FlakeInfo")
@XmlType(propOrder = {"flakeId", "pelletType", "version"})
public class PalletVersionInfo implements Serializable{

    @XmlElement(name = "flakeId")
    String flakeId;

    @XmlElement(name = "pelletType")
    String pelletType;

    @XmlElement(name = "version")
    String version;


    public String getFlakeId() {
        return flakeId;
    }

    public void setFlakeId(String flakeId) {
        this.flakeId = flakeId;
    }

    public String getPelletType() {
        return pelletType;
    }

    public void setPelletType(String pelletType) {
        this.pelletType = pelletType;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
