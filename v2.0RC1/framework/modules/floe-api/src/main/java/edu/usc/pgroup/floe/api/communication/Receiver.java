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
 * <class>Receiver</class> is associated with a Flake port. It create and manage Channels that accept messages to flake
 * <class>Receiver</class> is also responsible for handling the channels dynamically when Flakes expand to multiple machines.
 */
public interface Receiver {

    /**
     * Initialize receiver
     */
    public void init();

    /**
     * Start the Transport Receiver. This will start all the channels registered with the transport receiver
     */
    public void start();

    /**
     * Create a Fiber for communication purpose
     * @param params
     * @return Fiber id
     */
    public String  createChannel(Map<String,Object> params);

    /**
     * Start a Given Fiber
     * @param channelId Fiber Id
     */
    public void startChannel(String channelId);

    /**
     * Stop a Given Fiber
     * @param channelId  Fiber Id
     */
    public void stopChannel(String channelId);


    /**
     * Remove a Fiber from Transport Receiver
     * @param channelId
     */
    public void removeChannel(String channelId);


    /**
     * Stop Transport Receiver this will stop all the Channels associated with Transport Receiver
     */
    public void stop();

    /**
     * Get the Fiber given the channel id
     * @param channelId channel id which was given at channel creation
     * @return Fiber {@Link Fiber}
     */
    public Fiber getChannel(String channelId);


    /**
     * Get the Control Fiber given the channel id
     * @param channelId channel id which was given at channel creation
     * @return Control Fiber {@Link Fiber}
     */
    public Fiber getControlChannel(String channelId);

    /**
     * Publish the Control Message to all the associated Channels
     * @param message
     */
    public void publishControlMessage(Message message);


    public Map<String,Fiber> getChannels();
}
