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
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "PalletVersionInfoSet")
@XmlType(propOrder = {"palletVersionInfos"})
public class PalletVersionInfoSet {

    @XmlElement(name = "sourceConnections")
    List<PalletVersionInfo> palletVersionInfos;

    public List<PalletVersionInfo> getPalletVersionInfos() {
        return palletVersionInfos;
    }

    public void setPalletVersionInfos(List<PalletVersionInfo> palletVersionInfos) {
        this.palletVersionInfos = palletVersionInfos;
    }
}
