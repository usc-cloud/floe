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
import edu.usc.pgroup.floe.api.communication.Message;
import edu.usc.pgroup.floe.api.communication.SenderFiberStrategy;

import java.util.Map;
import java.util.logging.Logger;

public class SimpleRoundRobinSenderFiberStrategy implements SenderFiberStrategy {

    private Logger logger  = Logger.getLogger(SimpleRoundRobinSenderFiberStrategy.class.getName());

    private Map<String, Fiber> channels;
    private int index = 0;
    @Override
    public void setFibers(Map<String, Fiber> channels) {
        this.channels = channels;
    }

    @Override
    public Fiber getNextAvailableFiber() {

        int id = index%(channels.size());
        index++;
        String channelId = ""+(id+1);
        if(channels != null) {
            if(channels.containsKey(channelId)) {
                return channels.get(channelId);
            } else {
                logger.warning("Channeld " + channelId +"does not exist");
                return null;
            }
        } else {
            throw new RuntimeException("SenderChannel Fiber not initialised : Use setFibers to set the available channels" );
        }
    }

    @Override
    public void update(String fiberId,Message message) {
        logger.warning(this.getClass().getName() + "  does not use SenderChannel Strategy update information");
    }
}
