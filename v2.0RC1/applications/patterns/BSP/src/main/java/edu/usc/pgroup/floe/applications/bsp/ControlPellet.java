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
import edu.usc.pgroup.floe.api.framework.pelletmodels.Pellet;
import edu.usc.pgroup.floe.api.framework.pelletmodels.StreamInStreamOutPellet;
import edu.usc.pgroup.floe.api.state.StateObject;
import edu.usc.pgroup.floe.api.stream.FEmitter;
import edu.usc.pgroup.floe.api.stream.FIterator;

import java.util.concurrent.Semaphore;
import java.util.logging.Logger;

public class ControlPellet implements StreamInStreamOutPellet {

    private static Logger logger = Logger.getLogger(ControlPellet.class.getName());

    private int numberOfProcessorts = 1;


    private Semaphore barrier = new Semaphore(numberOfProcessorts);

    private static int currentSuperStep;

    private MessaageSender messaageSender = null;


    private boolean isHalt = false;

    private int haltVotes = 0;

    public void invoke(FIterator fIterator, FEmitter fEmitter, StateObject stateObject) {
        if(messaageSender == null) {

            messaageSender = new MessaageSender(fEmitter);
            try {
                barrier.acquire(numberOfProcessorts);
            } catch (InterruptedException e) {
                e.printStackTrace();

            }
            Thread t = new Thread(messaageSender);
            t.start();

        }

        while (true) {
             try {
                 Object o = fIterator.next();

                 if(o instanceof BSPMessage) {
                     BSPMessage msg = (BSPMessage)o;

                     int superStep = msg.getSuperStep();
                     if(superStep == currentSuperStep) {

                            if(msg.isVoteToHalt()) {
                                System.out.println("Control get vote to halt");
                                haltVotes++;
                            }
                            if(haltVotes == numberOfProcessorts) {
                                isHalt = true;
                                barrier.release(numberOfProcessorts);
                                return;
                            }
                            barrier.release();

                     } else {
                         logger.severe("Un Expected Message , invalid super step" + superStep);
                         throw new RuntimeException("Un Expected Message , invalid super step" + superStep);
                     }


                 } else {
                     logger.severe("Un Expected Message" + o);
                     throw new RuntimeException("Unexpected Message " + o);
                 }

             } catch (LandmarkException e) {
                 e.printStackTrace();
             } catch (LandmarkPauseException e) {
                 e.printStackTrace();
             }


         }
    }

    private class MessaageSender implements Runnable {

        FEmitter e ;
        public MessaageSender(FEmitter emitter) {
            e = emitter;
        }

        public void run() {
            while (true) {
                try {
                    barrier.acquire(numberOfProcessorts);
                } catch (InterruptedException e1) {

                }
                if(isHalt) {
                    System.out.println("******************SYSTEM ON HALT*************************");
                    return;
                }
                BSPMessage msg = new BSPMessage();
                msg.setType(BSPMessage.CTRL);
                msg.setSuperStep(++currentSuperStep);
                haltVotes = 0;
                e.emit(msg);

            }

        }
    }
}
