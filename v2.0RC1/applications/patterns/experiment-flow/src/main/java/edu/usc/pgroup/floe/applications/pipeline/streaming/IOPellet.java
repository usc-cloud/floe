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
package edu.usc.pgroup.floe.applications.pipeline.streaming;

import edu.usc.pgroup.floe.api.exception.LandmarkException;
import edu.usc.pgroup.floe.api.exception.LandmarkPauseException;
import edu.usc.pgroup.floe.api.framework.pelletmodels.Pellet;
import edu.usc.pgroup.floe.api.framework.pelletmodels.StreamInStreamOutPellet;
import edu.usc.pgroup.floe.api.state.StateObject;
import edu.usc.pgroup.floe.api.stream.FEmitter;
import edu.usc.pgroup.floe.api.stream.FIterator;

import java.io.BufferedReader;
import java.io.FileReader;

public class IOPellet implements StreamInStreamOutPellet {
    private final String file = "in.txt";


    private String readFile() throws Exception {

        BufferedReader reader = new BufferedReader(new FileReader(file));

        String line = "";
        while (true) {
            String data = reader.readLine();
            if (data == null) {
                break;
            }

            line += data;

        }


        return line;
    }


    @Override
    public void invoke(FIterator fIterator, FEmitter emitter, StateObject stateObject) {
        while (true) {
            Object o = null;
            try {
                o = fIterator.next();
            } catch (LandmarkException e) {
                e.printStackTrace();
            } catch (LandmarkPauseException e) {
                e.printStackTrace();
            }

            if(o != null) {
                String data = "";

                for(int i = 0 ; i < 80;i++) {
                    try {
                        data +=readFile() + "|";
                    } catch (Exception e) {

                    }
                }

                data = "|" + data;

                emitter.emit(data.getBytes());
                emitter.emit(data.getBytes());
            }
        }
    }
}
