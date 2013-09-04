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
package edu.usc.pgroup.floe.api.communication;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "ConnectionInfo")
@XmlType(propOrder = { "params","controlChannelInfo"})
public class TransportInfoBase implements Serializable
{
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 7762650307067817953L;

	
	protected HashMap<String,String> params = new HashMap<>();
	
	//NOTE: Control channel is always tcp.. (for now)
    TransportInfoBase controlChannelInfo;

    public void setParams(HashMap<String, String> params) {
		this.params = params;
	}
    
    public HashMap<String, String> getParams() {
		return params;
	}
    
    public TransportInfoBase getControlChannelInfo() {
        return controlChannelInfo;
    }

    public void setControlChannelInfo(TransportInfoBase controlChannelInfo) {
        this.controlChannelInfo = controlChannelInfo;
    }
    
    public void printConnectionInfoDetails() {

        System.out.println(" Connection Info Details");

        for(String p : params.keySet()) {

            System.out.println(p + " = " + params.get(p));
        }
    }
}