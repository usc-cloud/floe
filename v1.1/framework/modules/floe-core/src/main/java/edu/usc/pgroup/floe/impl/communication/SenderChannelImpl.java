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
import edu.usc.pgroup.floe.api.communication.Sender;
import edu.usc.pgroup.floe.api.communication.SenderChannel;
import edu.usc.pgroup.floe.api.communication.SenderFiberStrategy;
import edu.usc.pgroup.floe.api.framework.floegraph.Node.Port;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class SenderChannelImpl implements SenderChannel {

    private static Logger logger = Logger.getLogger(SenderChannelImpl.class.getName());


    private Map<String,Fiber> channelMap ;

    private Map<String,Fiber> controlChannelMap;
    private Map<String,ControlChannelMessageReader> channelReaders;

    private SenderFiberStrategy senderFiberStrategy;

    Executor pool;

    private ControlChannelCommunicationManager communicationManager;

    private String key;

    Sender sender;
    
	private Port selfConnectionPort;

    public SenderChannelImpl(ControlChannelCommunicationManager communicationManager, Sender sender) {
        
    	this.communicationManager = communicationManager;
        channelMap = new HashMap<String, Fiber>();
        controlChannelMap = new HashMap<String,Fiber>();
        channelReaders  = new HashMap<String,ControlChannelMessageReader>();
        pool = Executors.newCachedThreadPool();
        this.sender = sender;
    }
    
    @Override
    public void init() {

    }

    @Override
    public void send(Message message) {

        Fiber fiber = senderFiberStrategy.getNextAvailableFiber();
        fiber.write(message);
    }

    @Override
    public String  createFiber(Port otherEndConnectionPort, Map<String, Object> params) {
        Fiber fiber = ChannelFactory.createChannel("CLASS NAME",params);
        fiber.setOtherEndConnectionPort(otherEndConnectionPort);

        int id = channelMap.size() +1;
        fiber.setId(id+"");
        channelMap.put(id+"", fiber);

        Fiber controlFiber = ChannelFactory.createChannel("control",params);
        controlFiber.setOtherEndConnectionPort(otherEndConnectionPort);
        controlFiber.setId(id+"");
        
        ControlChannelMessageReader reader = new ControlChannelMessageReader(controlFiber,sender);
        channelReaders.put(""+id,reader);
        controlChannelMap.put(id+"", controlFiber);

        
        return ""+id;
    }

    @Override
    public void startFiber(String channelId) {
        if(channelMap.containsKey(channelId)) {
            Fiber fiber = channelMap.get(channelId);
            fiber.init();
            /*logger.info(fiber.getConnectionInfo().getDestAddress() + "\t"
                    + fiber.getConnectionInfo().getOutPortNo() + " Mappable " + fiber.getConnectionInfo().
                    getPortName());*/
        } else {
            logger.warning("Invalid Fiber id : " + channelId + " fiber id does not exist");
        }

        if(controlChannelMap.containsKey(channelId)) {
            Fiber fiber = controlChannelMap.get(channelId);
            fiber.init();
            /*logger.info(fiber.getConnectionInfo().getDestAddress() + "\t"
                    + fiber.getConnectionInfo().getOutPortNo() + " Mappable " + fiber.getConnectionInfo().
                    getPortName());*/
            ControlChannelMessageReader reader = channelReaders.get(channelId);
            //TODO fix this properly for the client side
            if(communicationManager != null)
                pool.execute(reader);
        } else {
            logger.warning("Invalid Fiber id : " + channelId + " fiber id does not exist");
        }
    }

    @Override
    public void stopFiber(String channelId) {
        if(channelMap.containsKey(channelId)) {
            Fiber fiber = channelMap.get(channelId);
            fiber.close();
            //logger.info("Stopped Fiber " + fiber + " at " + fiber.getConnectionInfo().getOutPortNo());
        } else {
            logger.warning("Invalid Fiber id : " + channelId + " fiber id does not exist");
        }

        if(controlChannelMap.containsKey(channelId)) {
            ControlChannelMessageReader reader = channelReaders.get(channelId);
            reader.stop();
            Fiber fiber = controlChannelMap.get(channelId);
            fiber.close();
            //logger.info("Stopped Fiber " + fiber + " at " + fiber.getConnectionInfo().getOutPortNo());
        } else {
            logger.warning("Invalid Fiber id : " + channelId + " fiber id does not exist");
        }
    }

    @Override
    public void removeFiber(String channelId) {
        if(channelMap.containsKey(channelId)) {
            channelMap.get(channelId).close();
            channelMap.remove(channelId);
        }
    }

    @Override
    public void setSenderFiberStrategy(SenderFiberStrategy fiberStrategy) {
        this.senderFiberStrategy = fiberStrategy;
        this.senderFiberStrategy.setFibers(channelMap);
    }

    @Override
    public Fiber getFiber(String channelId) {
        if(channelMap.containsKey(channelId)) {
            return channelMap.get(channelId);
        } else {
            logger.warning("Channeld " + channelId +"does not exist");
            return null;
        }
    }

    @Override
    public Fiber getControlFiber(String channelId) {
        if(controlChannelMap.containsKey(channelId)) {
            return controlChannelMap.get(channelId);
        } else {
            logger.warning("Channeld " + channelId +"does not exist");
            return null;
        }
    }

    @Override
    public void removeControlFiber(String channelId) {
        if(controlChannelMap.containsKey(channelId)) {
            controlChannelMap.remove(channelId);
        } else {
            logger.warning("Channeld " + channelId +"does not exist");
        }
    }

    @Override
    public Map<String, Fiber> getFibers() {
        return channelMap;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public void setKey(String key) {
        this.key=key;
    }

    private class ControlChannelMessageReader implements Runnable{

        private boolean running;
        private Fiber fiber;
        private Sender sender;

        ControlChannelMessageReader(Fiber fiber,Sender sender) {
            this.fiber = fiber;
            this.sender = sender;
            running = true;

        }

        @Override
        public void run() {
               while (running) {
                   Message msg = fiber.read(5000);
                   if(msg != null) {
                        communicationManager.controlSignalReceived(msg, sender);
                   }
               }
        }


        public void stop(){

            running = false;
        }
    }

	@Override
	public void setPort(Port port) {
		this.selfConnectionPort = port;
	}
	@Override
	public Port getPort() {
		return selfConnectionPort;
	}

    @Override
    public void updateSenderStatus(String fiberId,Message message) {
        senderFiberStrategy.update(fiberId,message);
    }
}
