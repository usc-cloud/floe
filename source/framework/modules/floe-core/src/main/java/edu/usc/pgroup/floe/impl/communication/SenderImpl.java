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

import edu.usc.pgroup.floe.api.communication.*;
import edu.usc.pgroup.floe.api.framework.floegraph.Node;
import edu.usc.pgroup.floe.api.framework.floegraph.Node.Port;
import edu.usc.pgroup.floe.impl.queues.SinkQueue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SenderImpl implements Sender{

    private Map<String,SenderChannel> senderChannelMap =
            new ConcurrentHashMap<String,SenderChannel>();

    private Map<String,SenderChannel> otherEndNodeToSenderMap;
    
    private SenderChannelStrategy senderChannelStrategy;

    private Node.Port port;

    Map<String, Object> senderParams;

    private Map<String,String> flakeIdtoFiberIdMap = new HashMap<>();
    
    
    private Map<String,ControlChannelCommunicationManager> communicationManagerMap ;

    @Override
    public void init(Map<String, Object> params) {
        communicationManagerMap = new HashMap<>();
        otherEndNodeToSenderMap = new HashMap<>();
        
        SinkQueue queue = (SinkQueue)params.get(ChannelFactory.QUEUE);
        if(queue != null) {
            queue.addSender((String) params.get(ChannelFactory.KEY), this);
        }
        
        senderParams = params;
    }

    @Override
    public void send(Message message) {
        List<SenderChannel> senderListToSend = senderChannelStrategy.getNextAvailableChannel();
        for(SenderChannel channel : senderListToSend) {
            channel.send(message);
        }
    }

    @Override
	public void connect(Port otherEndConnectionPort,String flakeId) {
		
		String otherEndNodeId = otherEndConnectionPort.getNodeId();
		SenderChannel  senderChannel;
		if(! otherEndNodeToSenderMap.containsKey(otherEndNodeId))
		{
			ControlChannelCommunicationManager controlChannelCommunicationManager =
	                new ControlChannelCommunicationManager(null);
	        
	    	senderChannel = new SenderChannelImpl(controlChannelCommunicationManager, this);
	        senderChannel.init();



            SenderFiberStrategy senderFiberStrategy = null;
            if(senderParams.get(ChannelFactory.FIBER_STRATERGY) == null) {

                senderFiberStrategy = new SimpleRoundRobinSenderFiberStrategy();
            } else {
                try {
                    Class clazz = Class.forName((String) senderParams.get(ChannelFactory.FIBER_STRATERGY));
                    senderFiberStrategy = (SenderFiberStrategy) clazz.newInstance();
                } catch (Exception e) {
                    senderFiberStrategy = new SimpleRoundRobinSenderFiberStrategy();
                }
            }

            senderChannel.setSenderFiberStrategy(senderFiberStrategy);

	        int id = senderChannelMap.size() + 1;

	        senderChannelMap.put(""+id,senderChannel);
	        otherEndNodeToSenderMap.put(otherEndNodeId, senderChannel);
		}
		
		senderChannel = otherEndNodeToSenderMap.get(otherEndNodeId);
        String fid = senderChannel.createFiber(otherEndConnectionPort,senderParams);
        senderChannel.startFiber(fid);

        flakeIdtoFiberIdMap.put(flakeId,fid);

	}
	
    @Override
    public void removeSenderChannel(String channelId) {
        senderChannelMap.remove(channelId);
    }


    @Override
    public void setSenderStrategy(SenderChannelStrategy channelStrategy) {
        this.senderChannelStrategy = channelStrategy;
        senderChannelStrategy.setChannels(senderChannelMap);
    }

    @Override
    public SenderChannel getSenderChannel(String nodeId) {
        return otherEndNodeToSenderMap.get(nodeId);
    }

    @Override
    public Map<String, SenderChannel> getSenderChannels() {
        return otherEndNodeToSenderMap;
    }


    @Override
    public void setPort(Node.Port port) {
        this.port = port;
    }

    @Override
    public Node.Port getPort() {
        return port;
    }

    public void stop(){

        for(SenderChannel channel : otherEndNodeToSenderMap.values()) {
            for(Fiber f : channel.getFibers().values()) {
                channel.stopFiber(f.getId());
            }
        }
    }

    public Map<String, String> getFlakeIdtoFiberIdMap() {
        return flakeIdtoFiberIdMap;
    }
}
