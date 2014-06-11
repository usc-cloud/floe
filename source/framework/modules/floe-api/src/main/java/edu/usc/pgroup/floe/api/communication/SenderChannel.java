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

import edu.usc.pgroup.floe.api.framework.floegraph.Node.Port;

public interface SenderChannel {

    public void init();

    public void send(Message message);

    public String createFiber(Port otherEndConnectionPort, Map<String,Object> params);

    /**
     * Start a Given Fiber
     * @param channelId Fiber Id
     */
    public void startFiber(String channelId);

    /**
     * Stop a Given Fiber
     * @param channelId  Fiber Id
     */
    public void stopFiber(String channelId);


    /**
     * Remove a Fiber from Transport Receiver
     * @param channelId
     */
    public void removeFiber(String channelId);


    public void setSenderFiberStrategy(SenderFiberStrategy fiberStrategy);

    public Fiber getFiber(String channelId);

    public Fiber getControlFiber(String channelId);

    public void removeControlFiber(String channelId);

    public Map<String,Fiber> getFibers();

    public String getKey();

    public void setKey(String key);

	public void setPort(Port port);
	
	public Port getPort();

    public void updateSenderStatus(String fiberId,Message message);
}
