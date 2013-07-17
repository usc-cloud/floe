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
import edu.usc.pgroup.floe.api.communication.Receiver;
import edu.usc.pgroup.floe.impl.queues.SourceQueue;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class ReceiverImpl implements Receiver {

    private Map<String,Fiber> channelMap ;
    private Map<String,Fiber> controlChannelMap;

    private static Logger logger = Logger.getLogger(ReceiverImpl.class.getName());
    @Override
    public void init() {
        channelMap = new HashMap<String, Fiber>();
        controlChannelMap = new HashMap<String, Fiber>();
    }

    @Override
    public void start() {
        for(String id : channelMap.keySet()) {
            startChannel(id);
        }
    }


    @Override
    public String createChannel(Map<String, Object> params) {
        Fiber fiber = ChannelFactory.createChannel("CLASS NAME",params);
        
        int id = channelMap.size() +1;
        channelMap.put(id+"", fiber);
        
        SourceQueue queue = (SourceQueue) params.get(ChannelFactory.QUEUE);
        queue.addSourceChannel(fiber);
        
        
        Fiber controlFiber = ChannelFactory.createChannel("control",params);
        controlChannelMap.put(id+"", controlFiber);

        return ""+id;
    }


    @Override
    public void startChannel(String channelId) {
        if(channelMap.containsKey(channelId)) {
            Fiber fiber = channelMap.get(channelId);
            fiber.init();

            
            //NOTE: Control channels are only TCP
            TCPControlFiber controlChannel = (TCPControlFiber) controlChannelMap.get(channelId);
            controlChannel.init();

            fiber.getTransportInfo().setControlChannelInfo(controlChannel.getTransportInfo());
            
            //TODO: SEE THIS.. why do we need "ControlChannelInfo"?
            /*
            ControlChannelInfo controlChannelInfo = new ControlChannelInfo();

            TCPTransportInfo connectionInfo = (TCPTransportInfo) controlChannel.getConnectionInfo();

            controlChannelInfo.setDestAddress(connectionInfo.getDestinationAddress());
            controlChannelInfo.setInPortNo(connectionInfo.getTcpInputPort());
            controlChannelInfo.setOutPortNo(connectionInfo.getTcpOutputPort());
            controlChannelInfo.setSourceAddress(connectionInfo.getSourceAddress());

            
            fiber.getConnectionInfo().setControlChannelInfo(controlChannelInfo);*/

            //logger.info("Started Fiber " + fiber + " at " + fiber.getConnectionInfo().getInPortNo());
        } else {
            logger.warning("Invalid Fiber id : " + channelId + " channel id does not exist");
        }
    }

    @Override
    public void stopChannel(String channelId) {
        if(channelMap.containsKey(channelId)) {
            Fiber fiber = channelMap.get(channelId);
            fiber.close();

            fiber = controlChannelMap.get(channelId);
            fiber.close();
        } else {
            logger.warning("Invalid Fiber id : " + channelId + " channel id does not exist");
        }
    }

    @Override
    public void removeChannel(String channelId) {
        if(channelMap.containsKey(channelId)) {
            channelMap.get(channelId).close();
            channelMap.remove(channelId);
        }
        if(controlChannelMap.containsKey(channelId)) {
            controlChannelMap.get(channelId).close();
            controlChannelMap.remove(channelId);
        }
    }

    @Override
    public void stop() {
        for(String id : channelMap.keySet()) {
            stopChannel(id);
        }
    }

    @Override
    public Fiber getChannel(String channelId) {
        if(channelMap.containsKey(channelId)) {
            return channelMap.get(channelId);
        } else {
            logger.warning("Channeld " + channelId +"does not exist");
            return null;
        }
    }

    public Fiber getControlChannel(String channelId) {
        if(controlChannelMap.containsKey(channelId)) {
            return controlChannelMap.get(channelId);
        } else {
            logger.warning("Channeld " + channelId +"does not exist");
            return null;
        }
    }

    @Override
    public void publishControlMessage(Message message) {
        for(Fiber fiber : controlChannelMap.values()) {
            fiber.write(message);
        }
    }

    @Override
    public Map<String, Fiber> getChannels() {
        return channelMap;
    }
}
