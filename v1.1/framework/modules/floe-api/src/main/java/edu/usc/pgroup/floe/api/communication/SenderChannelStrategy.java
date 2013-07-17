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

import java.util.List;
import java.util.Map;

public interface SenderChannelStrategy {

    /**
     * Set the available channels.
     * @param channels channel map
     */
    public void setChannels(Map<String,SenderChannel> channels);

    /**
     * Return the next available channel to send the messages based on the load balancing strategy
     * @return Fiber
     */
    public List<SenderChannel> getNextAvailableChannel();
}
