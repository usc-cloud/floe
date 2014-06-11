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
package edu.usc.pgroup.floe.impl.communication;

import edu.usc.pgroup.floe.api.communication.Fiber;
import edu.usc.pgroup.floe.impl.queues.Queue;

import java.util.Map;

public class ChannelFactory {

    public static final String QUEUE = "QUEUE";
    public static final String C_QUEUE= "C_QUEUE";
    public static final String KEY = "KEY";
    public static final String SERVER_SIDE = "SERVER_SIDE";
    public static final String FIBER_STRATERGY="FIBER_STRATERGY";


    public static Fiber createChannel(String clazz, Map<String,Object> params) {

        String serverSide = (String)params.get(SERVER_SIDE);
        String key = (String) params.get(KEY);
        Queue queue = (Queue) params.get(QUEUE);
        
        boolean bServerSide = "true".equalsIgnoreCase(serverSide) ? true:false;
        
        if(!"control".equals(clazz)){
        	
            return new TCPPushFiber(queue,key,bServerSide);
        } else {
            return new TCPControlFiber(bServerSide);
        }
    }




}
