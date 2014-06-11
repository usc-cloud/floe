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

import edu.usc.pgroup.floe.api.framework.floegraph.Node;
import edu.usc.pgroup.floe.api.framework.floegraph.Node.Port;

import java.util.Map;

public interface Sender {

    public void init(Map<String, Object> params);

    public void send(Message message);

    /**
     * Remove a Fiber from Transport Receiver
     * @param channelId
     */
    public void removeSenderChannel(String channelId);

    public void setSenderStrategy(SenderChannelStrategy channelStrategy);

    public SenderChannel getSenderChannel(String nodeId);


    public void stop();

    public Map<String,SenderChannel> getSenderChannels();


    public void setPort(Node.Port port);

    public Node.Port getPort();

	public void connect(Port otherEndConnectionPort,String flakeId);
}
