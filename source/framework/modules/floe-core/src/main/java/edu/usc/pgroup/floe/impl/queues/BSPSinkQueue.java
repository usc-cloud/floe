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
package edu.usc.pgroup.floe.impl.queues;

import edu.usc.pgroup.floe.api.communication.Sender;

public class BSPSinkQueue extends MapperSinkQueue {

    @Override
    protected int getEdgeIndex(String key) {
        String[] keyParts = key.split(":");
        String processor = keyParts[0];
        for(int i=0;i< outputSinks.size();i++) {
            Sender s = outputSinks.get(i);
            if(processor.equals(s.getPort().getPortName())) {
                return i;
            }

        }

        System.out.println("[ERROR] invalid index -1 for key : " + key);
        return -1;
    }
}


