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

import java.util.Map;

/**
 * SenderChannel Strategy defines which channel to choose when sending Messages '
 * Underlying implementation will decide the load balancing algorithm to use.
 */
public interface SenderFiberStrategy {

    /**
     * Set the available channels.
     * @param channels channel map
     */
    public void setFibers(Map<String, Fiber> channels);

    /**
     * Return the next available channel to send the messages based on the load balancing strategy
     * @return Fiber
     */
    public Fiber getNextAvailableFiber();


    /**
     * Update the SenderChannel Strategy information which will be used by the underlying implementations
     * for the load balancing algorithm
     * @param message information message
     */
    public void update(String fiberId,Message message);
}
