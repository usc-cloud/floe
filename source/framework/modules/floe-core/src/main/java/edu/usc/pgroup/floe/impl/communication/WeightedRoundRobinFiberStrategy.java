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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;

public class WeightedRoundRobinFiberStrategy implements SenderFiberStrategy {

    private Logger logger  = Logger.getLogger(SmartRoundRobinFiberStrategy.class.getName());

    private Map<String, Fiber> channels;

    private int index = 0;


    private Map<String,Double> weightMap = new HashMap<>();

    private Map<String,Double> timeMap = new HashMap<>();

    @Override
    public void setFibers(Map<String, Fiber> channels) {
        this.channels = channels;
        int size = channels.size();

        for(String fid : channels.keySet()) {
            weightMap.put(fid,(double)1/(double)size);
            timeMap.put(fid,1000.00);
        }

    }

    @Override
    public Fiber getNextAvailableFiber() {
        Fiber fiber = null;
        if(weightMap.size() ==0 || timeMap.size() ==0) {
            int size = channels.size();
            for(String fid : channels.keySet()) {
                weightMap.put(fid,(double)1/(double)size);
                timeMap.put(fid,1000.00);
            }
        }
        double random = Math.random();

        Double[] weights = weightMap.values().toArray(new Double[weightMap.size()]);

        Arrays.sort(weights);

        for(Double d : weights) {

            if(random <= d) {
                fiber =   findFiberWithGivenWeight(d);
            }

        }

        if(fiber == null) {
            fiber = findFiberWithGivenWeight(weights[weights.length -1]);
            if(fiber == null) {
                int i = new Random().nextInt(channels.size());

                for(String fid : channels.keySet()) {
                    i--;
                    if(i <=0) {
                        fiber = channels.get(fid);
                        break;
                    }
                }
            }
        }


        return fiber;
    }

    private Fiber findFiberWithGivenWeight(double d) {
        Fiber fiber = null;

        for(String fid : weightMap.keySet()) {
            if(weightMap.get(fid) == d) {
                fiber = channels.get(fid);
                break;
            }
        }
        return fiber;
    }

    @Override
    public void update(String fiberId,Message message) {
        byte[] payLoad = (byte[])message.getPayload();

        WeightInfo weightInfo = BitConverter.getObject(payLoad);

        double latency = weightInfo.getLatency();
        int bufferLength = weightInfo.getBufferLength();

        double t = (double)latency*bufferLength/(double)weightInfo.getPelletCount();
        if(t <= 0) {
            t = 0.0000001;
        }

        timeMap.put(weightInfo.getFlakeId(),t);
        double t_sum = 0;

        for(String fid : channels.keySet()) {

            if(timeMap.containsKey(fid)) {
                t_sum += timeMap.get(fid);
            }   else {
                t_sum += 1000.00;
                timeMap.put(fid,1000.0);
            }

        }

        double c = 1.0/t_sum;

        for(String fid : channels.keySet()) {
            weightMap.put(fid,c/(timeMap.get(fid)));
        }



    }


}
