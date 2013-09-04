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
package edu.usc.pgroup.floe.applications.bsp;

import edu.usc.pgroup.floe.api.exception.LandmarkException;
import edu.usc.pgroup.floe.api.exception.LandmarkPauseException;
import edu.usc.pgroup.floe.api.framework.pelletmodels.BSPPellet;
import edu.usc.pgroup.floe.api.stream.FIterator;
import edu.usc.pgroup.floe.api.stream.FMapEmitter;
import edu.usc.pgroup.floe.util.BitConverter;

import java.util.HashMap;
import java.util.Map;

public class ProcessorPellet implements BSPPellet{


    private static int currentSuperStep = -1;

    BSPProcessor processor = new SimpleBSPProcessorImpl();

    private String controlKey ="CONTROL";

    public void invoke(FIterator fIterator, FMapEmitter fMapEmitter) {

        Map<String,Object> currentSet = new HashMap<String,Object>();

        while (true) {

            try {
                Object o  = fIterator.next();

                if(o instanceof BSPMessage) {
                    BSPMessage message = (BSPMessage)o;
                    if(message.getType() == BSPMessage.CTRL) {
                        Map<String,Object> result = processor.compute(currentSet);
                        currentSet.clear();
                        currentSuperStep++;
                        System.out.println("Control Message Received, Super step " +  currentSuperStep);
                        if (result != null) {
                            for (String key : result.keySet()) {

                                BSPMessage bspMessage = new BSPMessage();
                                bspMessage.setSuperStep(currentSuperStep);
                                bspMessage.setType(BSPMessage.DATA);
                                bspMessage.setKey(key);
                                bspMessage.setData(BitConverter.getBytes(result.get(key)));

                                fMapEmitter.emit(key, bspMessage);
                            }
                        }

                        BSPMessage bspMessage = new BSPMessage();
                        bspMessage.setSuperStep(currentSuperStep);
                        bspMessage.setType(BSPMessage.CTRL);
                        bspMessage.setKey(controlKey);
                        if(result == null) {
                            bspMessage.setVoteToHalt(true);
                        } else {
                            bspMessage.setVoteToHalt(false);
                        }
                        fMapEmitter.emit(controlKey,bspMessage);


                    } else {
                        currentSet.put(message.getKey(),message.getData());
                    }
                }


            } catch (LandmarkException e) {
                e.printStackTrace();
            } catch (LandmarkPauseException e) {
                e.printStackTrace();
            }

        }
    }
}
