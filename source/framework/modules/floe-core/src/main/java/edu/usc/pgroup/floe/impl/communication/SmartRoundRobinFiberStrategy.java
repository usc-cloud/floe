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
import edu.usc.pgroup.floe.api.util.BitConverter;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class SmartRoundRobinFiberStrategy implements SenderFiberStrategy {

    private Logger logger  = Logger.getLogger(SmartRoundRobinFiberStrategy.class.getName());
    private Map<String, Fiber> channels;

    private int index = 0;


    private Map<String,Long> weightMap = new HashMap<>();



    @Override
    public void setFibers(Map<String, Fiber> channels) {
        this.channels = channels;

    }

    @Override
    public Fiber getNextAvailableFiber() {
        long currentTime = System.currentTimeMillis();
        double random = Math.random();
        Fiber fiber = null;
        int id = index%(channels.size());
        index++;
        String channelId = ""+(id+1);
        int iterations = 0;
        while (iterations < channels.size()) {
            if (channels != null) {
                if (channels.containsKey(channelId)) {
                    fiber = channels.get(channelId);

                    if((weightMap.containsKey(channelId))&&
                            (weightMap.get(channelId) - currentTime < 10)) {
                        return fiber;
                    } else {
                        fiber = null;
                    }

                } else {
                    logger.warning("Channeld " + channelId + "does not exist");
                    return null;
                }
                iterations++;
            } else {
                throw new RuntimeException("SenderChannel Fiber not initialised : Use setFibers to set the available channels");
            }

        }

        if(fiber == null) {
            return channels.get(channelId);
        }
        return fiber;
    }

    @Override
    public void update(String fiberId,Message message) {
        byte[] payLoad = (byte[])message.getPayload();

        WeightInfo weightInfo = BitConverter.getObject(payLoad);

        double latency = weightInfo.getLatency();
        int bufferLength = weightInfo.getBufferLength();

        int weightTime = (int) Math.ceil(latency*bufferLength);
        weightMap.put(fiberId, System.currentTimeMillis() + weightTime);

    }






}
