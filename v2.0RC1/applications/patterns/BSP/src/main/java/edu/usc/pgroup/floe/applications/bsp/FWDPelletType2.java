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
package edu.usc.pgroup.floe.applications.bsp;

import edu.usc.pgroup.floe.api.framework.pelletmodels.Pellet;
import edu.usc.pgroup.floe.api.state.StateObject;

public class FWDPelletType2 implements Pellet {
    public Object invoke(Object o, StateObject stateObject) {
        BSPMessage msg = (BSPMessage)o;
        System.out.println("FWDED : " + (msg.getType()== BSPMessage.CTRL?"CTRL":"DATA"));
        System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
        byte[] a= msg.getData();
        if(a==null) {
            a = new String("|####|").getBytes();
        } else {
            a = (new String(a) + "|####|").getBytes();
        }
        msg.setData(a);
        System.out.println(new String(a));
        return o;
    }
}
